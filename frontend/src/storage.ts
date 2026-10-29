import { useEffect, useState } from 'react'

// PROGRESS: galvenā kopija ir failā be-macibas/progress.json (caur Vite serveri: /__progress),
// localStorage ir tikai ātrā kopija pārlūkā. Ja fails nav pieejams — strādā tikai ar localStorage.

const PREFIX = 'bem.'
const cache: Record<string, unknown> = {}
let fileAvailable = false

export type SaveStatus = 'file' | 'saving' | 'browser' | 'error'
let status: SaveStatus = 'browser'
const listeners = new Set<(s: SaveStatus) => void>()
const setStatus = (s: SaveStatus) => {
  status = s
  listeners.forEach((l) => l(s))
}

function readLocalStorage() {
  try {
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i)
      if (key?.startsWith(PREFIX)) cache[key] = JSON.parse(localStorage.getItem(key)!)
    }
  } catch {
    /* privātais režīms u.tml. */
  }
}

function writeLocalStorage(key: string, value: unknown) {
  try {
    localStorage.setItem(key, JSON.stringify(value))
  } catch {
    /* ignorējam */
  }
}

/** Izsauc VIENREIZ pirms aplikācijas renderēšanas (main.tsx). */
export async function initProgress() {
  readLocalStorage()
  try {
    const res = await fetch('/__progress', { cache: 'no-store' })
    if (!res.ok) throw new Error(String(res.status))
    const data = (await res.json()) as { values?: Record<string, unknown> }
    fileAvailable = true
    if (data.values && Object.keys(data.values).length) {
      // Fails ir galvenais — tas uzvar pār pārlūka kopiju
      for (const key of Object.keys(cache)) delete cache[key]
      for (const [key, value] of Object.entries(data.values)) {
        cache[key] = value
        writeLocalStorage(key, value)
      }
    } else if (Object.keys(cache).length) {
      await pushToFile() // pirmā reize: pārceļam esošo progresu no pārlūka uz failu
    }
    setStatus('file')
  } catch {
    fileAvailable = false
    setStatus('browser')
  }
}

async function pushToFile() {
  if (!fileAvailable) return
  setStatus('saving')
  try {
    const res = await fetch('/__progress', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ values: cache }),
      keepalive: true,
    })
    setStatus(res.ok ? 'file' : 'error')
  } catch {
    setStatus('error')
  }
}

let pushTimer: ReturnType<typeof setTimeout> | undefined
// Throttle: ne biežāk kā reizi 1,5 sekundēs (taimeris maina datus katru sekundi —
// ar debounce saglabāšana nekad nenotiktu, kamēr taimeris skaita)
function schedulePush() {
  if (pushTimer) return
  pushTimer = setTimeout(() => {
    pushTimer = undefined
    pushToFile()
  }, 1500)
}

// Aizverot cilni — pēdējās izmaiņas aizsūtām uzreiz
if (typeof window !== 'undefined') {
  window.addEventListener('pagehide', () => {
    if (!fileAvailable) return
    clearTimeout(pushTimer)
    pushTimer = undefined
    navigator.sendBeacon('/__progress', new Blob([JSON.stringify({ values: cache })], { type: 'application/json' }))
  })
}

export function load<T>(key: string, fallback: T): T {
  return key in cache ? (cache[key] as T) : fallback
}

export function save(key: string, value: unknown) {
  if (JSON.stringify(cache[key]) === JSON.stringify(value)) return
  cache[key] = value
  writeLocalStorage(key, value)
  schedulePush()
}

export function useSaveStatus() {
  const [s, setS] = useState<SaveStatus>(status)
  useEffect(() => {
    listeners.add(setS)
    return () => void listeners.delete(setS)
  }, [])
  return s
}

/** Kā useState, bet saglabājas pēc lapas pārlādes. */
export function usePersistentState<T>(key: string, fallback: T) {
  const [value, setValue] = useState<T>(() => load(key, fallback))
  useEffect(() => save(key, value), [key, value])
  return [value, setValue] as const
}

export const todayKey = () => new Date().toLocaleDateString('sv-SE') // "2026-10-29"

/** Mācību taimeris: skaita sekundes, kamēr lapa ir atvērta un redzama. Saglabā pa datumiem. */
export function useStudyTimer() {
  const [log, setLog] = usePersistentState<Record<string, number>>('bem.timeLog', {})
  const [paused, setPaused] = useState(false)

  useEffect(() => {
    if (paused) return
    const id = setInterval(() => {
      if (document.visibilityState !== 'visible') return
      const day = todayKey()
      setLog((prev) => ({ ...prev, [day]: (prev[day] ?? 0) + 1 }))
    }, 1000)
    return () => clearInterval(id)
  }, [paused, setLog])

  const todaySeconds = log[todayKey()] ?? 0

  // Cik dienas pēc kārtas mācījies vismaz 10 minūtes
  let streak = 0
  const d = new Date()
  if ((log[todayKey()] ?? 0) < 600) d.setDate(d.getDate() - 1) // šodiena vēl nav "skaitīta" — skatāmies no vakardienas
  while ((log[d.toLocaleDateString('sv-SE')] ?? 0) >= 600) {
    streak++
    d.setDate(d.getDate() - 1)
  }

  return { todaySeconds, paused, setPaused, streak }
}
