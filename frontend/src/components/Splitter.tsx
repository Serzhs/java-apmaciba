import { useRef } from 'react'

type Props = {
  ratio: number // teksta daļa no platuma (0..1)
  onChange: (ratio: number) => void
  min?: number
  max?: number
  defaultRatio?: number
}

/** Vilkšanas josla starp tekstu un kodu. Velc ar peli; dubultklikšķis atjauno; bultiņas arī strādā. */
export function Splitter({ ratio, onChange, min = 0.22, max = 0.78, defaultRatio = 0.47 }: Props) {
  const ref = useRef<HTMLDivElement>(null)
  const clamp = (r: number) => Math.min(max, Math.max(min, r))

  const onPointerDown = (e: React.PointerEvent) => {
    const container = ref.current?.parentElement
    if (!container) return
    e.preventDefault()
    const rect = container.getBoundingClientRect()
    ref.current!.setPointerCapture(e.pointerId)
    document.body.classList.add('resizing')

    const move = (ev: PointerEvent) => onChange(clamp((ev.clientX - rect.left) / rect.width))
    const up = () => {
      document.body.classList.remove('resizing')
      window.removeEventListener('pointermove', move)
      window.removeEventListener('pointerup', up)
    }
    window.addEventListener('pointermove', move)
    window.addEventListener('pointerup', up)
  }

  return (
    <div
      ref={ref}
      className="splitter"
      role="separator"
      aria-orientation="vertical"
      aria-label="Mainīt teksta un koda platumu"
      aria-valuemin={Math.round(min * 100)}
      aria-valuemax={Math.round(max * 100)}
      aria-valuenow={Math.round(ratio * 100)}
      tabIndex={0}
      title="Velc, lai mainītu platumu. Dubultklikšķis — atpakaļ uz sākumu."
      onPointerDown={onPointerDown}
      onDoubleClick={() => onChange(defaultRatio)}
      onKeyDown={(e) => {
        if (e.key === 'ArrowLeft') (e.preventDefault(), e.stopPropagation(), onChange(clamp(ratio - 0.05)))
        if (e.key === 'ArrowRight') (e.preventDefault(), e.stopPropagation(), onChange(clamp(ratio + 0.05)))
      }}
    >
      <span className="splitter-grip" />
    </div>
  )
}
