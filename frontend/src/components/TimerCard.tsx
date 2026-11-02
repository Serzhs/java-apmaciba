import { useSaveStatus, useStudyTimer } from '../storage'
import { IconPause, IconPlay, IconStreak } from './Icons'

const formatClock = (sec: number) => {
  const h = Math.floor(sec / 3600)
  const m = Math.floor((sec % 3600) / 60)
  return h ? `${h}:${String(m).padStart(2, '0')} h` : `${m} min`
}

/**
 * Taimeris ir ATSEVIŠĶS komponents: tas atjaunojas katru sekundi, bet pārējā lapa netiek pārzīmēta.
 * (Ja taimeris būtu App iekšā, katru sekundi pārzīmētos viss — arī teksts un kods.)
 */
export function TimerCard({ doneCount, totalSteps }: { doneCount: number; totalSteps: number }) {
  const timer = useStudyTimer()
  const saveStatus = useSaveStatus()
  const todayMin = Math.floor(timer.todaySeconds / 60)

  return (
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
  )
}
