import { useEffect, useMemo, useRef, useState } from 'react'
import { files, isRunnable } from '../content'
import { highlight, languageForPath } from '../highlight'
import { computeFoldRanges, splitHighlightedLines } from '../folding'

type Props = {
  tabs: string[]
  active: string | null
  closable?: (path: string) => boolean
  onSelect: (path: string) => void
  onClose?: (path: string) => void
}

const fileName = (p: string) => p.split('/').pop() ?? p

export function FileBadge({ path }: { path: string }) {
  const ext = path.split('.').pop() ?? ''
  const label = { java: 'J', yml: 'Y', xml: 'M', http: 'H' }[ext] ?? '•'
  return <span className={`file-badge ext-${ext}`}>{label}</span>
}

export function CodePanel({ tabs, active, closable, onSelect, onClose }: Props) {
  const current = active && tabs.includes(active) ? active : tabs[0]
  const content = current ? files[current] ?? '' : ''
  const htmlLines = useMemo(
    () => (current ? splitHighlightedLines(highlight(content, languageForPath(current))) : []),
    [current, content],
  )
  const rawLines = useMemo(() => content.split('\n'), [content])
  const foldRanges = useMemo(() => computeFoldRanges(content), [content])
  // Sakļautās rindas katram failam atsevišķi (pārslēdzot tabus, atceras)
  const [foldedByFile, setFoldedByFile] = useState<Record<string, number[]>>({})
  const folded = new Set(current ? foldedByFile[current] ?? [] : [])
  const [copied, setCopied] = useState(false)
  const scrollRef = useRef<HTMLDivElement>(null)
  const tabsRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: 0, left: 0 })
    // Ritinām TIKAI tabu joslu (scrollIntoView ritinātu arī visu lapu)
    const bar = tabsRef.current
    const tab = bar?.querySelector<HTMLElement>('.tab.active')
    if (bar && tab) {
      if (tab.offsetLeft < bar.scrollLeft) bar.scrollLeft = tab.offsetLeft
      else if (tab.offsetLeft + tab.offsetWidth > bar.scrollLeft + bar.clientWidth)
        bar.scrollLeft = tab.offsetLeft + tab.offsetWidth - bar.clientWidth
    }
  }, [current])

  if (!current) {
    return (
      <div className="code-panel empty">
        <p>Atver failu kokā kreisajā pusē</p>
      </div>
    )
  }

  const setFolded = (next: Set<number>) => setFoldedByFile((prev) => ({ ...prev, [current]: [...next] }))
  const toggleFold = (line: number) => {
    const next = new Set(folded)
    if (next.has(line)) next.delete(line)
    else next.add(line)
    setFolded(next)
  }
  // "Sakļaut visu": visas iekšējās daļas (atstāj redzamu tikai faila augšējo līmeni)
  const foldAll = () => setFolded(new Set([...foldRanges.keys()].filter((l) => /^\s/.test(rawLines[l]))))
  const unfoldAll = () => setFolded(new Set())

  // Kuras rindas rādīt: ja rinda ir sakļauta, izlaižam tās ķermeni
  const visible: { index: number; foldedTo?: number }[] = []
  for (let i = 0; i < htmlLines.length; i++) {
    const end = foldRanges.get(i)
    if (end !== undefined && folded.has(i)) {
      visible.push({ index: i, foldedTo: end })
      i = end
    } else {
      visible.push({ index: i })
    }
  }

  const copy = async () => {
    try {
      await navigator.clipboard.writeText(content)
    } catch {
      return
    }
    setCopied(true)
    setTimeout(() => setCopied(false), 1500)
  }

  return (
    <div className="code-panel">
      <div className="tabs" ref={tabsRef} role="tablist">
        {tabs.map((t) => (
          <div
            key={t}
            role="tab"
            aria-selected={t === current}
            className={`tab ${t === current ? 'active' : ''}`}
            onClick={() => onSelect(t)}
            onAuxClick={(e) => e.button === 1 && closable?.(t) && onClose?.(t)}
            title={t}
          >
            <FileBadge path={t} />
            <span className="tab-name">{fileName(t)}</span>
            {closable?.(t) && (
              <button
                className="tab-close"
                aria-label="Aizvērt"
                onClick={(e) => {
                  e.stopPropagation()
                  onClose?.(t)
                }}
              >
                ×
              </button>
            )}
          </div>
        ))}
      </div>

      <div className="code-toolbar">
        <span className="breadcrumb">
          {current.split('/').map((part, i, arr) => (
            <span key={i} className={i === arr.length - 1 ? 'crumb last' : 'crumb'}>
              {part}
            </span>
          ))}
        </span>
        <span className="toolbar-actions">
          {isRunnable(content) && (
            <span className="run-badge" title="VS Code: atver šo failu un spied 'Run' virs main() metodes">
              <svg width="9" height="9" viewBox="0 0 16 16" aria-hidden><path fill="currentColor" d="M4 2.5v11l9.5-5.5z" /></svg>
              var palaist
            </span>
          )}
          {foldRanges.size > 0 && (
            <>
              <button className="toolbar-btn" onClick={foldAll} title="Sakļaut visas metodes un blokus">
                Sakļaut visu
              </button>
              {folded.size > 0 && (
                <button className="toolbar-btn" onClick={unfoldAll} title="Izvērst visu">
                  Izvērst visu
                </button>
              )}
            </>
          )}
          <button className="toolbar-btn" onClick={copy}>
            {copied ? 'Nokopēts' : 'Kopēt'}
          </button>
        </span>
      </div>

      <div className="code-scroll" ref={scrollRef}>
        <div className="code-lines hljs" role="presentation">
          {visible.map(({ index, foldedTo }) => {
            const canFold = foldRanges.has(index)
            const closer = foldedTo !== undefined ? rawLines[foldedTo].trim() : ''
            const showCloser = foldedTo !== undefined && /^[}\])]|^\*\/|^<\//.test(closer)
            return (
              <div key={index} className={`code-line ${foldedTo !== undefined ? 'is-folded' : ''}`}>
                <span className="ln" aria-hidden>
                  {index + 1}
                </span>
                <span className="fold-slot">
                  {canFold && (
                    <button
                      className="fold-btn"
                      onClick={() => toggleFold(index)}
                      aria-label={foldedTo !== undefined ? 'Izvērst' : 'Sakļaut'}
                      title={foldedTo !== undefined ? 'Izvērst' : 'Sakļaut'}
                    >
                      <svg width="10" height="10" viewBox="0 0 10 10" aria-hidden>
                        <path d="M2.5 3.5 5 6l2.5-2.5" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                      </svg>
                    </button>
                  )}
                </span>
                <span className="lc">
                  <span dangerouslySetInnerHTML={{ __html: htmlLines[index] || ' ' }} />
                  {foldedTo !== undefined && (
                    <>
                      <button className="fold-pill" onClick={() => toggleFold(index)} title={`Izvērst (${foldedTo - index} rindas)`}>
                        ⋯ {foldedTo - index} rindas
                      </button>
                      {showCloser && <span className="fold-closer">{closer}</span>}
                    </>
                  )}
                </span>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}
