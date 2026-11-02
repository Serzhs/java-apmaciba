// Koda sakļaušana (folding) pēc atkāpēm — tāpat kā VS Code pēc noklusējuma.
// Rinda ir "sakļaujama", ja tai seko rindas ar lielāku atkāpi (metodes ķermenis, klase, Javadoc komentārs).

const indentOf = (line: string) => (line.match(/^[ \t]*/)?.[0] ?? '').replace(/\t/g, '    ').length
const isBlank = (line: string) => line.trim() === ''
const isCloser = (line: string) => /^\s*[}\])]/.test(line) || /^\s*\*\//.test(line) || /^\s*<\//.test(line)

/** Atgriež Map: sākuma rinda -> pēdējā sakļautā rinda (abas 0-indeksētas). */
export function computeFoldRanges(code: string): Map<number, number> {
  const lines = code.split('\n')
  const ranges = new Map<number, number>()

  for (let i = 0; i < lines.length; i++) {
    if (isBlank(lines[i])) continue
    const base = indentOf(lines[i])
    let end = i
    for (let j = i + 1; j < lines.length; j++) {
      if (isBlank(lines[j])) continue
      if (indentOf(lines[j]) > base) end = j
      else break
    }
    if (end === i) continue
    // Noslēdzošo "}" (vai "*/", "</tag>") arī ieliekam sakļautajā daļā, lai paliek "{ ⋯ }"
    let next = end + 1
    while (next < lines.length && isBlank(lines[next])) next++
    if (next < lines.length && indentOf(lines[next]) === base && isCloser(lines[next])) end = next
    ranges.set(i, end)
  }
  return ranges
}

/**
 * highlight.js atgriež vienu HTML gabalu. Lai varētu slēpt atsevišķas rindas, sadalām to pa rindām,
 * korekti aizverot un atkal atverot <span> tagus, kas turpinās pāri rindām (piem. daudzrindu komentāri).
 */
export function splitHighlightedLines(html: string): string[] {
  const lines: string[] = []
  const open: string[] = []
  let current = ''
  const re = /(<span[^>]*>)|(<\/span>)|(\n)|([^<\n]+)/g
  let m: RegExpExecArray | null
  while ((m = re.exec(html))) {
    if (m[1]) {
      open.push(m[1])
      current += m[1]
    } else if (m[2]) {
      open.pop()
      current += m[2]
    } else if (m[3]) {
      lines.push(current + '</span>'.repeat(open.length))
      current = open.join('')
    } else {
      current += m[4]
    }
  }
  lines.push(current)
  return lines
}
