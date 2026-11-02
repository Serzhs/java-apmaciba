import { useRef, useState, type ReactNode } from 'react'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import { classifyLink } from '../content'
import { highlight } from '../highlight'

type Props = {
  markdown: string
  flashcards?: boolean
  onOpenFile: (path: string) => void
  onOpenFiles: (paths: string[]) => void
  onOpenLesson: (lessonId: string) => void
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function hastText(node: any): string {
  if (!node) return ''
  if (node.type === 'text') return node.value
  return (node.children ?? []).map(hastText).join('')
}

function CodeBlock({ code, lang }: { code: string; lang?: string }) {
  const [copied, setCopied] = useState(false)
  const preRef = useRef<HTMLPreElement>(null)
  const text = code.replace(/\n$/, '')

  const copy = async () => {
    try {
      await navigator.clipboard.writeText(text)
    } catch {
      // Ja pārlūks neļauj — iezīmējam tekstu, lai var nokopēt ar ⌘C
      const codeEl = preRef.current?.querySelector('code')
      if (codeEl) window.getSelection()?.selectAllChildren(codeEl)
      return
    }
    setCopied(true)
    setTimeout(() => setCopied(false), 1500)
  }

  return (
    <div className="md-code-wrap">
      <div className="md-code-bar">
        <span className="md-code-lang">{lang ?? 'kods'}</span>
        <button className="md-copy" onClick={copy}>
          {copied ? 'Nokopēts' : 'Kopēt'}
        </button>
      </div>
      <pre className="md-code hljs" ref={preRef}>
        <code dangerouslySetInnerHTML={{ __html: highlight(text, lang) }} />
      </pre>
    </div>
  )
}

function Flashcard({ children }: { children: ReactNode }) {
  const [open, setOpen] = useState(false)
  return (
    <blockquote
      className={`flashcard ${open ? 'open' : ''}`}
      onClick={() => setOpen(true)}
      title={open ? undefined : 'Vispirms atbildi pats, tad klikšķini'}
    >
      {!open && <span className="flashcard-hint">Atbildi skaļi, tad klikšķini, lai redzētu atbildi</span>}
      <div className="flashcard-body">{children}</div>
    </blockquote>
  )
}

/** Emoji tekstā -> tīrāks izskats. ✅/❌ kļūst par ✓/✗, pārējie tiek izņemti. Koda blokus neaiztiekam. */
function tidy(md: string): string {
  return md
    .split(/(```[\s\S]*?```)/g)
    .map((part) =>
      part.startsWith('```')
        ? part
        : part
            .replace(/✅/g, '✓')
            .replace(/❌/g, '✗')
            .replace(/(?:(?![\u2190-\u21FF])\p{Extended_Pictographic}[\u{FE0F}\u{200D}]*)+ ?/gu, '')
            // "**Kods:** ..." rindas, kas seko viena otrai, paliek katra savā rindā
            .replace(/([^\n])\n(?=\*\*[^*\n]+:\*\*)/g, '$1  \n'),
    )
    .join('')
}

export function Markdown({ markdown, flashcards, onOpenFile, onOpenFiles, onOpenLesson }: Props) {
  return (
    <div className="markdown">
      <ReactMarkdown
        remarkPlugins={[remarkGfm]}
        components={{
          pre: ({ node }) => {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            const codeEl = node?.children?.[0] as any
            const cls: string[] = codeEl?.properties?.className ?? []
            const lang = cls.find((c) => String(c).startsWith('language-'))?.slice(9)
            return <CodeBlock code={hastText(codeEl)} lang={lang} />
          },
          a: ({ href = '', children }) => {
            const target = classifyLink(href)
            if (target.kind === 'external') {
              return (
                <a href={href} target="_blank" rel="noreferrer">
                  {children}
                </a>
              )
            }
            const onClick = (e: React.MouseEvent) => {
              e.preventDefault()
              if (target.kind === 'file') onOpenFile(target.path)
              if (target.kind === 'dir') onOpenFiles(target.files)
              if (target.kind === 'lesson') onOpenLesson(target.lessonId)
            }
            const cls = target.kind === 'lesson' ? 'lesson-link' : 'file-link'
            return (
              <a href="#" className={cls} onClick={onClick}>
                {target.kind !== 'lesson' && <span className="file-link-icon">{target.kind === 'dir' ? '/' : '{ }'}</span>}
                {children}
              </a>
            )
          },
          blockquote: ({ children }) =>
            flashcards ? <Flashcard>{children}</Flashcard> : <blockquote>{children}</blockquote>,
          table: ({ children }) => (
            <div className="table-wrap">
              <table>{children}</table>
            </div>
          ),
        }}
      >
        {tidy(markdown)}
      </ReactMarkdown>
    </div>
  )
}
