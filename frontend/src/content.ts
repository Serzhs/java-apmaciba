// Ielādē VISU saturu no projekta: docs/*.md un Java/konfigurācijas failus.
// import.meta.glob ar ?raw = faila saturs kā string. Ja maini failu, Vite pārlādē lapu automātiski.

const rawDocs = import.meta.glob('../../docs/*.md', {
  query: '?raw',
  import: 'default',
  eager: true,
}) as Record<string, string>

const rawFiles = import.meta.glob(
  ['../../src/**/*.java', '../../src/**/*.yml', '../../requests.http', '../../pom.xml'],
  { query: '?raw', import: 'default', eager: true },
) as Record<string, string>

const stripPrefix = (p: string) => p.replace(/^(\.\.\/)+/, '')

/** Projekta faili: "src/main/java/lv/macibas/oop/Encapsulation.java" -> saturs */
export const files: Record<string, string> = Object.fromEntries(
  Object.entries(rawFiles).map(([k, v]) => [stripPrefix(k), v]),
)

export const filePaths = Object.keys(files).sort()

export type Step = {
  id: string
  title: string
  intro?: string // nodarbības ievada teksts (tikai pirmajam solim)
  markdown: string
  files: string[]
}

export type Lesson = {
  id: string // "01-oop"
  num: string // "01"
  title: string
  steps: Step[]
}

/** Atrisina relatīvu ceļu: resolvePath("docs", "../src/a/B.java") -> "src/a/B.java" */
export function resolvePath(fromDir: string, href: string): string {
  const clean = href.split('#')[0].replace(/:\d+$/, '')
  const out: string[] = []
  for (const part of `${fromDir}/${clean}`.split('/')) {
    if (!part || part === '.') continue
    if (part === '..') out.pop()
    else out.push(part)
  }
  return out.join('/')
}

export type LinkTarget =
  | { kind: 'file'; path: string }
  | { kind: 'dir'; path: string; files: string[] }
  | { kind: 'lesson'; lessonId: string }
  | { kind: 'external'; href: string }

export function classifyLink(href: string, fromDir = 'docs'): LinkTarget {
  if (/^https?:\/\//.test(href)) return { kind: 'external', href }
  const path = resolvePath(fromDir, decodeURI(href))
  if (files[path]) return { kind: 'file', path }
  const docMatch = path.match(/^docs\/(.+)\.md$/)
  if (docMatch) return { kind: 'lesson', lessonId: docMatch[1] }
  const dirFiles = filePaths.filter((f) => f.startsWith(path + '/'))
  if (dirFiles.length) return { kind: 'dir', path, files: dirFiles }
  return { kind: 'external', href }
}

/** Visi koda faili, uz kuriem ir saites šajā markdown gabalā (mapes tiek izvērstas). */
function filesReferenced(markdown: string): string[] {
  const result: string[] = []
  for (const m of markdown.matchAll(/\]\(([^)\s]+)\)/g)) {
    const target = classifyLink(m[1])
    if (target.kind === 'file') result.push(target.path)
    if (target.kind === 'dir') result.push(...target.files)
  }
  return [...new Set(result)]
}

/** Noņem emoji no virsrakstiem (sānjoslā un virsrakstos tie izskatās trokšņaini). */
export const stripEmoji = (t: string) =>
  t.replace(/(?![\u2190-\u21FF])[\p{Extended_Pictographic}\u{FE0F}\u{200D}]/gu, '').replace(/\s{2,}/g, ' ').trim()

function cleanBody(md: string): string {
  return md
    .replace(/^\s*(---\s*)+/, '')
    .replace(/(\s*---\s*)+$/, '')
    .trim()
}

function parseLesson(path: string, raw: string): Lesson {
  const id = path.replace(/^.*\//, '').replace(/\.md$/, '')
  const num = id.slice(0, 2)
  const titleMatch = raw.match(/^#\s+(.+)$/m)
  const fullTitle = titleMatch ? titleMatch[1].trim() : id
  // "1. nodarbība — OOP (..)" -> "OOP (..)"
  const title = stripEmoji(fullTitle.replace(/^\d+\.\s*(nodarbība\s*—\s*)?/, ''))
  const body = titleMatch ? raw.replace(titleMatch[0], '') : raw

  const chunks = body.split(/^##\s+/m)
  const intro = cleanBody(chunks[0])
  const steps: Step[] = []
  if (chunks.length === 1) {
    // Nodarbība bez "##" sadaļām = viens solis
    steps.push({ id: `${id}/0`, title, markdown: intro, files: filesReferenced(intro) })
  }
  chunks.slice(1).forEach((chunk, i) => {
    const nl = chunk.indexOf('\n')
    const stepTitle = stripEmoji(nl === -1 ? chunk : chunk.slice(0, nl))
    const md = cleanBody(nl === -1 ? '' : chunk.slice(nl + 1))
    const stepIntro = i === 0 && intro ? intro : undefined // ievada teksts pievienojas pirmajam solim
    steps.push({ id: `${id}/${steps.length}`, title: stepTitle, intro: stepIntro, markdown: md, files: filesReferenced(`${stepIntro ?? ''}\n${md}`) })
  })
  return { id, num, title, steps }
}

export const lessons: Lesson[] = Object.entries(rawDocs)
  .map(([path, raw]) => parseLesson(stripPrefix(path), raw))
  .sort((a, b) => a.id.localeCompare(b.id))

export type StepRef = { lesson: Lesson; step: Step; lessonIndex: number; stepIndex: number; flatIndex: number }

export const allSteps: StepRef[] = lessons.flatMap((lesson, lessonIndex) =>
  lesson.steps.map((step, stepIndex) => ({ lesson, step, lessonIndex, stepIndex, flatIndex: 0 })),
).map((ref, i) => ({ ...ref, flatIndex: i }))

export const isRunnable = (content: string) => /public\s+static\s+void\s+main\s*\(/.test(content)
