import { useCallback, useEffect, useMemo, useState } from 'react'
import { allSteps, files, lessons } from './content'
import { buildDays, estimateMinutes, formatMinutes, ROUTINE, type Day } from './plan'
import { usePersistentState, useSaveStatus, useStudyTimer } from './storage'
import { Markdown } from './components/Markdown'
import { CodePanel } from './components/CodePanel'
import { FileTree } from './components/FileTree'
import { IconArrowLeft, IconArrowRight, IconCheck, IconFiles, IconMenu, IconPause, IconPlan, IconPlay, IconSidebar, IconStreak } from './components/Icons'
import { Splitter } from './components/Splitter'

type Route = { kind: 'step'; stepId: string } | { kind: 'day'; day: number } | { kind: 'files' }

const fileLines = (p: string) => (files[p] ?? '').split('\n').length
const DAYS: Day[] = buildDays(fileLines)
const dayOfStep = new Map(DAYS.flatMap((d) => d.steps.map((s) => [s.step.id, d.index] as const)))
const stepMinutes = new Map(allSteps.map((r) => [r.step.id, estimateMinutes(r, fileLines)]))

function parseHash(): Route | null {
  const h = decodeURIComponent(location.hash.replace(/^#\/?/, ''))
  if (h === 'files') return { kind: 'files' }
  const day = h.match(/^day\/(\d+)$/)
  if (day) return { kind: 'day', day: Math.min(Number(day[1]) - 1, DAYS.length - 1) }
  const step = h.match(/^s\/(.+)$/)
  if (step && allSteps.some((r) => r.step.id === step[1])) return { kind: 'step', stepId: step[1] }
  return null
}

function routeToHash(r: Route) {
  if (r.kind === 'files') return '#/files'
  if (r.kind === 'day') return `#/day/${r.day + 1}`
  return `#/s/${r.stepId}`
}

const formatClock = (sec: number) => {
  const h = Math.floor(sec / 3600)
  const m = Math.floor((sec % 3600) / 60)
  return h ? `${h}:${String(m).padStart(2, '0')} h` : `${m} min`
}

/** Virsrakstā `kods` -> <code>kods</code> */
const renderTitle = (title: string) =>
  title.split(/`([^`]+)`/).map((part, i) => (i % 2 ? <code key={i}>{part}</code> : part))

export default function App() {
  const [done, setDone] = usePersistentState<string[]>('bem.done', [])
  const [routine, setRoutine] = usePersistentState<Record<number, boolean[]>>('bem.routine', {})
  const [explorer, setExplorer] = usePersistentState<{ open: string[]; active: string | null }>('bem.explorer', {
    open: [],
    active: null,
  })
  const doneSet = useMemo(() => new Set(done), [done])
  const timer = useStudyTimer()
  const saveStatus = useSaveStatus()

  const currentDayIndex = useMemo(() => {
    const d = DAYS.find((day) => day.steps.some((s) => !doneSet.has(s.step.id)))
    return d ? d.index : DAYS.length - 1
  }, [doneSet])

  const [route, setRoute] = useState<Route>(() => parseHash() ?? { kind: 'day', day: currentDayIndex })
  const [menuOpen, setMenuOpen] = useState(false)
  const [sidebarHidden, setSidebarHidden] = usePersistentState('bem.sidebarHidden', false)
  const [splitRatio, setSplitRatio] = usePersistentState('bem.splitRatio', 0.47)

  // ⌘B / Ctrl+B — paslēpt/parādīt sānjoslu (kā VS Code)
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'b') {
        e.preventDefault()
        setSidebarHidden((h) => !h)
      }
    }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [setSidebarHidden])

  const go = useCallback((r: Route) => {
    setRoute(r)
    setMenuOpen(false)
    if (location.hash !== routeToHash(r)) history.pushState(null, '', routeToHash(r))
    document.querySelector('.reader')?.scrollTo({ top: 0 })
  }, [])

  useEffect(() => {
    const onPop = () => setRoute(parseHash() ?? { kind: 'day', day: currentDayIndex })
    window.addEventListener('popstate', onPop)
    window.addEventListener('hashchange', onPop)
    return () => {
      window.removeEventListener('popstate', onPop)
      window.removeEventListener('hashchange', onPop)
    }
  }, [currentDayIndex])

  const routeDay = route.kind === 'day' ? route.day : route.kind === 'step' ? dayOfStep.get(route.stepId) ?? 0 : currentDayIndex
  const [expandedDay, setExpandedDay] = useState<number | null>(routeDay)
  useEffect(() => setExpandedDay(routeDay), [routeDay])

  // ----- Soļa koda tabi -----
  const stepRef = route.kind === 'step' ? allSteps.find((r) => r.step.id === route.stepId)! : null
  const [extraTabs, setExtraTabs] = useState<string[]>([])
  const [activeTab, setActiveTab] = useState<string | null>(null)
  useEffect(() => {
    setExtraTabs([])
    setActiveTab(stepRef?.step.files[0] ?? null)
  }, [stepRef?.step.id]) // eslint-disable-line react-hooks/exhaustive-deps

  const stepTabs = stepRef ? [...stepRef.step.files, ...extraTabs.filter((t) => !stepRef.step.files.includes(t))] : []

  const openInStep = (paths: string[]) => {
    setExtraTabs((prev) => [...new Set([...prev, ...paths])])
    setActiveTab(paths[0])
  }
  const openInExplorer = (path: string) =>
    setExplorer((prev) => ({ open: prev.open.includes(path) ? prev.open : [...prev.open, path], active: path }))

  const closeExplorerTab = (path: string) =>
    setExplorer((prev) => {
      const idx = prev.open.indexOf(path)
      const open = prev.open.filter((p) => p !== path)
      const active = prev.active === path ? open[Math.min(idx, open.length - 1)] ?? null : prev.active
      return { open, active }
    })

  const openLesson = (lessonId: string) => {
    const lesson = lessons.find((l) => l.id === lessonId)
    if (lesson) go({ kind: 'step', stepId: lesson.steps[0].id })
  }

  // ----- Navigācija -----
  const markDone = (id: string) => setDone((prev) => (prev.includes(id) ? prev : [...prev, id]))
  const toggleDone = (id: string) => setDone((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]))

  const goNext = useCallback(() => {
    if (!stepRef) return
    markDone(stepRef.step.id)
    const day = DAYS[dayOfStep.get(stepRef.step.id) ?? 0]
    const isLastOfDay = day.steps[day.steps.length - 1].step.id === stepRef.step.id
    if (isLastOfDay) go({ kind: 'day', day: day.index })
    else go({ kind: 'step', stepId: allSteps[stepRef.flatIndex + 1].step.id })
  }, [stepRef, go]) // eslint-disable-line react-hooks/exhaustive-deps

  const goPrev = useCallback(() => {
    if (!stepRef) return
    const day = DAYS[dayOfStep.get(stepRef.step.id) ?? 0]
    if (day.steps[0].step.id === stepRef.step.id) go({ kind: 'day', day: day.index })
    else go({ kind: 'step', stepId: allSteps[stepRef.flatIndex - 1].step.id })
  }, [stepRef, go])

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (route.kind !== 'step' || e.metaKey || e.ctrlKey || e.altKey) return
      if ((e.target as HTMLElement).closest('input, textarea, [role="separator"]')) return
      if (e.key === 'ArrowRight') goNext()
      if (e.key === 'ArrowLeft') goPrev()
    }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [route.kind, goNext, goPrev])

  const totalSteps = allSteps.length
  const doneCount = allSteps.filter((r) => doneSet.has(r.step.id)).length
  const goalMin = 60
  const todayMin = Math.floor(timer.todaySeconds / 60)

  // ----- Sānjosla -----
  const sidebar = (
    <aside className={`sidebar ${menuOpen ? 'open' : ''}`}>
      <div className="brand">
        <span className="brand-title">Java apmācība</span>
        <button className="icon-btn hide-sidebar" onClick={() => setSidebarHidden(true)} title="Paslēpt sānjoslu (⌘B)" aria-label="Paslēpt sānjoslu">
          <IconSidebar />
        </button>
      </div>

      <div className="timer">
        <div className="timer-top">
          <div>
            <div className="timer-label">Šodien</div>
            <div className="timer-value">{formatClock(timer.todaySeconds)}</div>
          </div>
          <button className="timer-btn" onClick={() => timer.setPaused(!timer.paused)}>
            {timer.paused ? <IconPlay size={11} /> : <IconPause size={11} />}
            {timer.paused ? 'Turpināt' : 'Pauze'}
          </button>
        </div>
        <div>
          <div className="scale" title="Mērķis: 1–2 stundas dienā">
            <div className="scale-fill" style={{ width: `${Math.min(100, (todayMin / 120) * 100)}%` }} />
            <div className="scale-tick" style={{ left: '50%' }} />
          </div>
          <div className="scale-labels">
            <span style={{ left: 0 }}>0</span>
            <span style={{ left: '50%' }}>1 h</span>
            <span style={{ left: '100%' }}>2 h</span>
          </div>
        </div>
        <div className="timer-foot">
          <span>
            <IconStreak size={13} /> {timer.streak} {timer.streak === 1 ? 'diena' : 'dienas'} pēc kārtas
          </span>
          <span>
            {doneCount}/{totalSteps} soļi
          </span>
        </div>
        <div className={`save-status ${saveStatus}`} title="Progress tiek saglabāts failā be-macibas/progress.json">
          {saveStatus === 'file' && 'Saglabāts progress.json'}
          {saveStatus === 'saving' && 'Saglabā…'}
          {saveStatus === 'browser' && 'Saglabāts tikai pārlūkā'}
          {saveStatus === 'error' && 'Neizdevās saglabāt failā — vai serveris strādā?'}
        </div>
      </div>

      <div className="mode-switch" role="tablist">
        <button className={route.kind !== 'files' ? 'active' : ''} onClick={() => go({ kind: 'day', day: currentDayIndex })}>
          <IconPlan size={15} /> Plāns
        </button>
        <button className={route.kind === 'files' ? 'active' : ''} onClick={() => go({ kind: 'files' })}>
          <IconFiles size={15} /> Faili
        </button>
      </div>

      {route.kind === 'files' ? (
        <FileTree active={explorer.active} onOpen={openInExplorer} />
      ) : (
        <nav className="plan">
          {DAYS.map((day) => {
            const dayDone = day.steps.filter((s) => doneSet.has(s.step.id)).length
            const complete = dayDone === day.steps.length
            const isToday = day.index === currentDayIndex
            const expanded = expandedDay === day.index
            let lastLesson = ''
            return (
              <div key={day.index} className={`day ${complete ? 'complete' : ''} ${isToday ? 'today' : ''}`}>
                <button
                  className={`day-header ${route.kind === 'day' && route.day === day.index ? 'active' : ''}`}
                  onClick={() => {
                    setExpandedDay(expanded ? null : day.index)
                    go({ kind: 'day', day: day.index })
                  }}
                >
                  <span className="day-num">{complete ? <IconCheck size={13} /> : day.number}</span>
                  <span className="day-text">
                    <span className="day-title">
                      Diena {day.number}
                      {isToday && <span className="today-tag marker">šodien</span>}
                      {complete && <span className="status-200" title="HTTP 200 OK — diena pabeigta">200 OK</span>}
                    </span>
                    <span className="day-sub">{day.lessonTitles.join(' · ')}</span>
                  </span>
                  <span className="day-time">{formatMinutes(day.minutes)}</span>
                </button>
                {expanded && (
                  <div className="day-steps">
                    {day.steps.map((r) => {
                      const showLesson = r.lesson.id !== lastLesson
                      lastLesson = r.lesson.id
                      return (
                        <div key={r.step.id}>
                          {showLesson && day.lessonTitles.length > 1 && <div className="step-lesson">{r.lesson.title}</div>}
                          <button
                            className={`step-row ${route.kind === 'step' && route.stepId === r.step.id ? 'active' : ''} ${doneSet.has(r.step.id) ? 'done' : ''}`}
                            onClick={() => go({ kind: 'step', stepId: r.step.id })}
                          >
                            <span className="step-check">{doneSet.has(r.step.id) && <IconCheck size={9} />}</span>
                            <span className="step-title">
                              {route.kind === 'step' && route.stepId === r.step.id ? <span className="marker">{r.step.title}</span> : r.step.title}
                            </span>
                            <span className="step-time">{stepMinutes.get(r.step.id)}′</span>
                          </button>
                        </div>
                      )
                    })}
                  </div>
                )}
              </div>
            )
          })}
        </nav>
      )}
    </aside>
  )

  // ----- Galvenais saturs -----
  let main: React.ReactNode

  if (route.kind === 'files') {
    main = (
      <div className="workspace files-only">
        <CodePanel
          tabs={explorer.open}
          active={explorer.active}
          closable={() => true}
          onSelect={(p) => setExplorer((prev) => ({ ...prev, active: p }))}
          onClose={closeExplorerTab}
        />
      </div>
    )
  } else if (route.kind === 'day') {
    const day = DAYS[route.day]
    const dayDone = day.steps.filter((s) => doneSet.has(s.step.id)).length
    const complete = dayDone === day.steps.length
    const firstUndone = day.steps.find((s) => !doneSet.has(s.step.id)) ?? day.steps[0]
    const checks = routine[day.index] ?? []
    const prevDay = DAYS[day.index - 1]
    const nextDay = DAYS[day.index + 1]
    const prevQuiz = prevDay?.steps.find((s) => /pašpārbaude/i.test(s.step.title))

    main = (
      <div className="workspace">
        <section className="reader day-page">
          <div className="reader-inner">
            <div className="eyebrow">
              Diena {day.number} no {DAYS.length} · ~{formatMinutes(day.minutes)}
            </div>
            <h1>{complete ? `Diena ${day.number} pabeigta` : day.lessonTitles.join(' + ')}</h1>
            {complete && <p className="goal"><span className="label status-200">200 OK</span><span>{day.lessonTitles.join(' + ')} — izieta. Labs darbs.</span></p>}
            {!complete && <p className="goal"><span className="label">Mērķis</span><span>{day.goal}</span></p>}

            {!complete && prevDay && (
              <div className="warmup">
                <span className="label">Iesildīšanās</span>
                <span>
                5 minūtes atceries vakardienu: {prevDay.lessonTitles.join(', ')}.{' '}
                {prevQuiz && (
                  <a href="#" onClick={(e) => (e.preventDefault(), go({ kind: 'step', stepId: prevQuiz.step.id }))}>
                    Atver vakardienas pašpārbaudi
                  </a>
                )}
                </span>
              </div>
            )}

            <div className="day-progress">
              <div className="scale">
                <div className="scale-fill" style={{ width: `${(dayDone / day.steps.length) * 100}%` }} />
              </div>
              <span>
                {dayDone}/{day.steps.length} soļi
              </span>
            </div>

            <ol className="day-list">
              {day.steps.map((r) => (
                <li key={r.step.id} className={doneSet.has(r.step.id) ? 'done' : ''}>
                  <button className="day-list-check" aria-label="Atzīmēt" onClick={() => toggleDone(r.step.id)}>
                    {doneSet.has(r.step.id) && <IconCheck size={12} />}
                  </button>
                  <a href="#" onClick={(e) => (e.preventDefault(), go({ kind: 'step', stepId: r.step.id }))}>
                    {r.step.title}
                  </a>
                  <span className="muted">
                    {r.step.files.length > 0 && `${r.step.files.length} ${r.step.files.length === 1 ? 'fails' : 'faili'} · `}~
                    {stepMinutes.get(r.step.id)} min
                  </span>
                </li>
              ))}
            </ol>

            {!complete && (
              <button className="primary-btn" onClick={() => go({ kind: 'step', stepId: firstUndone.step.id })}>
                {dayDone ? 'Turpināt' : 'Sākt dienu'} <IconArrowRight />
              </button>
            )}

            <h2>Dienas čeklists</h2>
            <p className="muted">Atzīmē dienas beigās. Ja kāds punkts nav izdarīts — tas ir signāls atkārtot.</p>
            <ul className="routine">
              {ROUTINE.map((label, i) => (
                <li key={i}>
                  <label>
                    <input
                      type="checkbox"
                      checked={!!checks[i]}
                      onChange={() =>
                        setRoutine((prev) => {
                          const next = [...(prev[day.index] ?? [])]
                          next[i] = !next[i]
                          return { ...prev, [day.index]: next }
                        })
                      }
                    />
                    {label}
                  </label>
                </li>
              ))}
            </ul>

            {complete && nextDay && (
              <div className="next-day">
                <div>
                  <div className="eyebrow">Rīt · Diena {nextDay.number} · ~{formatMinutes(nextDay.minutes)}</div>
                  <strong>{nextDay.lessonTitles.join(' + ')}</strong>
                  <div className="muted">{nextDay.goal}</div>
                </div>
                <button className="secondary-btn" onClick={() => go({ kind: 'day', day: nextDay.index })}>
                  Skatīt <IconArrowRight />
                </button>
              </div>
            )}
            {complete && (
              <p className="muted tip">
                Labāk mācīties 1–2 h katru dienu nekā 6 h vienreiz nedēļā. Smadzenes nostiprina zināšanas miegā — tāpēc nākamo dienu sāc
                rīt!
              </p>
            )}
          </div>
        </section>
      </div>
    )
  } else if (stepRef) {
    const day = DAYS[dayOfStep.get(stepRef.step.id) ?? 0]
    const posInDay = day.steps.findIndex((s) => s.step.id === stepRef.step.id)
    const isLastOfDay = posInDay === day.steps.length - 1
    const hasCode = stepTabs.length > 0
    const isQuizLesson = stepRef.lesson.id.startsWith('11-')

    main = (
      <div
        className={`workspace ${hasCode ? 'with-code' : ''}`}
        style={hasCode ? { gridTemplateColumns: `minmax(0, ${splitRatio}fr) auto minmax(0, ${1 - splitRatio}fr)` } : undefined}
      >
        <section className="reader">
          <div className="reader-inner">
            <div className="eyebrow">
              <a href="#" onClick={(e) => (e.preventDefault(), go({ kind: 'day', day: day.index }))}>
                Diena {day.number}
              </a>{' '}
              · {stepRef.lesson.title} · solis {posInDay + 1}/{day.steps.length} · ~{stepMinutes.get(stepRef.step.id)} min
            </div>
            <h1>{renderTitle(stepRef.step.title)}</h1>
            {hasCode && (
              <p className="code-hint">
                Kods ir koda panelī (labajā pusē, uz telefona zemāk). Saites ar <span className="file-link">{'{ }'}</span> atver failu jaunā tabā.
              </p>
            )}
            {stepRef.step.intro && (
              <Markdown
                markdown={stepRef.step.intro}
                onOpenFile={(p) => openInStep([p])}
                onOpenFiles={openInStep}
                onOpenLesson={openLesson}
              />
            )}
            <Markdown
              markdown={stepRef.step.markdown}
              flashcards={isQuizLesson}
              onOpenFile={(p) => openInStep([p])}
              onOpenFiles={openInStep}
              onOpenLesson={openLesson}
            />
            <div className="pager">
              <button className="secondary-btn" onClick={goPrev}>
                <IconArrowLeft /> Atpakaļ
              </button>
              <span className="muted kbd-hint">
                <kbd>←</kbd> <kbd>→</kbd>
              </span>
              <button className="primary-btn" onClick={goNext}>
                {isLastOfDay ? 'Pabeigt dienu' : 'Saprotu, tālāk'} {isLastOfDay ? <IconCheck /> : <IconArrowRight />}
              </button>
            </div>
          </div>
        </section>
        {hasCode && <Splitter ratio={splitRatio} onChange={setSplitRatio} />}
        {hasCode && (
          <CodePanel
            tabs={stepTabs}
            active={activeTab}
            closable={(p) => !stepRef.step.files.includes(p)}
            onSelect={setActiveTab}
            onClose={(p) => {
              setExtraTabs((prev) => prev.filter((t) => t !== p))
              if (activeTab === p) setActiveTab(stepTabs[0])
            }}
          />
        )}
      </div>
    )
  }

  return (
    <div className={`app ${sidebarHidden ? 'sidebar-hidden' : ''}`}>
      {sidebarHidden && (
        <button className="icon-btn show-sidebar" onClick={() => setSidebarHidden(false)} title="Parādīt sānjoslu (⌘B)" aria-label="Parādīt sānjoslu">
          <IconSidebar />
        </button>
      )}
      <button className="menu-btn" onClick={() => setMenuOpen(!menuOpen)} aria-label="Izvēlne">
        <IconMenu size={18} />
      </button>
      {sidebar}
      {menuOpen && <div className="scrim" onClick={() => setMenuOpen(false)} />}
      <main className="main">{main}</main>
    </div>
  )
}
