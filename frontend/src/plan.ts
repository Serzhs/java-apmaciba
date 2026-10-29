import { allSteps, lessons, type StepRef } from './content'

// Mācību plāns pa dienām (1–2 stundas dienā).
// `steps: [no, līdz]` — sadaļu numuri nodarbībā (0 = pirmā "##" sadaļa). Bez `steps` = visa nodarbība.
type PlanEntry = { lesson: string; steps?: [number, number] }
type PlanDay = { goal: string; entries: PlanEntry[] }

const PLAN: PlanDay[] = [
  { goal: 'Ieraudzīt, ka daudz ko jau zini no React — īsa diena iesākumam', entries: [{ lesson: '00-FE-BE-vardnica' }] },
  { goal: 'Kas ir objekts + pirmie 3 OOP pīlāri', entries: [{ lesson: '01-oop', steps: [0, 3] }] },
  { goal: 'Abstrakcija, kompozīcija, equals/hashCode, immutability', entries: [{ lesson: '01-oop', steps: [4, 99] }] },
  { goal: 'SOLID — 5 burti, katram 1 teikums + 1 piemērs', entries: [{ lesson: '02-solid' }] },
  { goal: 'Dizaina šabloni, kas VEIDO objektus: Singleton, Builder, Factory + Strategy', entries: [{ lesson: '03-dizaina-sabloni', steps: [0, 3] }] },
  { goal: 'Observer un Proxy — un kāpēc @Transactional dažreiz nestrādā', entries: [{ lesson: '03-dizaina-sabloni', steps: [4, 99] }] },
  { goal: 'Kolekcijas, Streams (= JS masīvu metodes), Optional, izņēmumi', entries: [{ lesson: '04-java-pamati', steps: [0, 3] }] },
  { goal: 'Generics, pavedieni, atmiņa, modernā Java', entries: [{ lesson: '04-java-pamati', steps: [4, 99] }] },
  { goal: '⭐ Lielā bilde: ko backend dara, kad React izsauc fetch(). Palaid serveri!', entries: [{ lesson: '05-ka-strada-backend' }] },
  { goal: 'Spring: IoC, Dependency Injection, anotācijas', entries: [{ lesson: '06-spring' }] },
  { goal: 'SQL, indeksi, JPA un N+1 problēma', entries: [{ lesson: '07-datubazes', steps: [0, 4] }] },
  { goal: 'Transakcijas, ACID, optimistic locking', entries: [{ lesson: '07-datubazes', steps: [5, 99] }] },
  { goal: 'Testi: JUnit + Mockito (= Jest + jest.mock)', entries: [{ lesson: '08-testi' }] },
  { goal: 'Drošība: JWT, CORS, SQL injection', entries: [{ lesson: '09-drosiba' }] },
  { goal: 'Arhitektūra: mikroservisi, kešs, rindas, Docker', entries: [{ lesson: '10-arhitektura' }] },
  { goal: 'Atkārtošana ar kartītēm: OOP, SOLID, Java, Spring', entries: [{ lesson: '11-intervijas-jautajumi', steps: [0, 3] }] },
  { goal: 'Atkārtošana ar kartītēm: DB, REST, "pastāsti par sevi"', entries: [{ lesson: '11-intervijas-jautajumi', steps: [4, 99] }] },
  { goal: 'Prakse: pirmie divi uzdevumi — raksti kodu pats', entries: [{ lesson: '12-prakse', steps: [0, 2] }] },
  { goal: 'Prakse: uzraksti testus savam kodam', entries: [{ lesson: '12-prakse', steps: [3, 3] }] },
  { goal: 'Prakse: React frontends tavam API (un CORS!)', entries: [{ lesson: '12-prakse', steps: [4, 4] }] },
  { goal: 'Izmēģinājuma intervija 🎤', entries: [{ lesson: '12-prakse', steps: [5, 99] }] },
]

export type Day = {
  index: number // 0-based
  number: number // 1-based
  goal: string
  steps: StepRef[]
  minutes: number
  lessonTitles: string[]
}

/** Iesildīšanās (5 min) + dienas beigu atkārtošana un čeklists (10 min). */
export const DAILY_OVERHEAD = 15

/** Aptuvenais laiks solim mierīgā tempā: lasīšana ar pārlasīšanu + koda apskate + palaišana un eksperimenti. */
export function estimateMinutes(ref: StepRef, fileLines: (path: string) => number): number {
  const md = `${ref.step.intro ?? ''}\n${ref.step.markdown}`
  if (/^Uzdevums/i.test(ref.step.title)) {
    // Prakses uzdevums: raksti kodu pats
    return /React/.test(ref.step.title) ? 90 : /intervija/i.test(ref.step.title) ? 60 : 45
  }
  const flashcards = (md.match(/^\*\*\d+\./gm) ?? []).length
  if (flashcards) return Math.round(flashcards * 2.5) // katrs jautājums: atbildi skaļi + salīdzini
  const words = md.split(/\s+/).length
  const codeLines = ref.step.files.reduce((n, f) => n + fileLines(f), 0)
  const practice = ref.step.files.length ? 8 : 0 // palaist + pamainīt kodu
  const isQuiz = /pašpārbaude/i.test(ref.step.title)
  return Math.max(5, Math.round(words / 35 + Math.min(codeLines, 300) / 12 + practice + (isQuiz ? 10 : 0)))
}

export function buildDays(fileLines: (path: string) => number): Day[] {
  const dayOfStep = new Map<string, number>()
  PLAN.forEach((day, dayIndex) => {
    for (const entry of day.entries) {
      const lesson = lessons.find((l) => l.id === entry.lesson)
      if (!lesson) continue
      const [from, to] = entry.steps ?? [0, 999]
      lesson.steps.forEach((s, i) => {
        if (i >= from && i <= to) dayOfStep.set(s.id, dayIndex)
      })
    }
  })

  // Soļi, kas nav plānā (piem. pievienota jauna nodarbība), pievienojas iepriekšējā soļa dienai
  let lastDay = 0
  const days: Day[] = PLAN.map((d, i) => ({ index: i, number: i + 1, goal: d.goal, steps: [], minutes: 0, lessonTitles: [] }))
  for (const ref of allSteps) {
    const d = dayOfStep.get(ref.step.id) ?? lastDay
    lastDay = d
    days[d].steps.push(ref)
  }
  const result = days.filter((d) => d.steps.length)
  // Ja tēma sadalīta vairākās dienās -> "OOP (1/2)", "OOP (2/2)"
  const daysPerLesson = new Map<string, number[]>()
  for (const day of result) {
    for (const id of new Set(day.steps.map((r) => r.lesson.id))) {
      daysPerLesson.set(id, [...(daysPerLesson.get(id) ?? []), day.index])
    }
  }
  for (const day of result) {
    day.minutes = DAILY_OVERHEAD + day.steps.reduce((n, r) => n + estimateMinutes(r, fileLines), 0)
    day.lessonTitles = [...new Map(day.steps.map((r) => [r.lesson.id, r.lesson.title])).entries()].map(([id, title]) => {
      const parts = daysPerLesson.get(id)!
      return parts.length > 1 ? `${title} (${parts.indexOf(day.index) + 1}/${parts.length})` : title
    })
  }
  result.forEach((d, i) => {
    d.index = i
    d.number = i + 1
  })
  return result
}

export const ROUTINE = [
  'Izlasīju visus šodienas soļus',
  'Palaidu kodu un redzēju rezultātu',
  'Pamainīju kodu un paskatījos, kas notiek',
  'Skaļi atbildēju uz pašpārbaudes jautājumiem',
  '5 min: atkārtoju vakardienas tēmu',
]

export function formatMinutes(min: number): string {
  if (min < 60) return `${min} min`
  const h = Math.floor(min / 60)
  const m = min % 60
  return m ? `${h} h ${m} min` : `${h} h`
}
