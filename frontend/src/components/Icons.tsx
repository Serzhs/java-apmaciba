// Mazas līniju ikonas UI vajadzībām (emoji vietā). Krāsu ņem no teksta (currentColor).
type P = { size?: number }
const base = (size = 16) => ({
  width: size,
  height: size,
  viewBox: '0 0 16 16',
  fill: 'none',
  stroke: 'currentColor',
  strokeWidth: 1.6,
  strokeLinecap: 'round' as const,
  strokeLinejoin: 'round' as const,
  'aria-hidden': true,
})

export const IconPlan = ({ size }: P) => (
  <svg {...base(size)}>
    <rect x="2.5" y="3" width="11" height="10.5" rx="1.5" />
    <path d="M2.5 6.5h11M5.5 1.5v3M10.5 1.5v3" />
  </svg>
)

export const IconFiles = ({ size }: P) => (
  <svg {...base(size)}>
    <path d="M2 4.5V12a1 1 0 0 0 1 1h10a1 1 0 0 0 1-1V6a1 1 0 0 0-1-1H8L6.5 3H3a1 1 0 0 0-1 1.5Z" />
  </svg>
)

export const IconCheck = ({ size }: P) => (
  <svg {...base(size)} strokeWidth={2.2}>
    <path d="m3.5 8.5 3 3 6-7" />
  </svg>
)

export const IconPause = ({ size }: P) => (
  <svg {...base(size)}>
    <path d="M5.5 3.5v9M10.5 3.5v9" />
  </svg>
)

export const IconPlay = ({ size }: P) => (
  <svg {...base(size)} fill="currentColor" stroke="none">
    <path d="M5 3.2v9.6a.5.5 0 0 0 .77.42l7.2-4.8a.5.5 0 0 0 0-.84l-7.2-4.8A.5.5 0 0 0 5 3.2Z" />
  </svg>
)

export const IconArrowRight = ({ size }: P) => (
  <svg {...base(size)}>
    <path d="M3 8h10M9 4l4 4-4 4" />
  </svg>
)

export const IconArrowLeft = ({ size }: P) => (
  <svg {...base(size)}>
    <path d="M13 8H3M7 4 3 8l4 4" />
  </svg>
)

export const IconStreak = ({ size }: P) => (
  <svg {...base(size)}>
    <path d="M8 14c2.8 0 4.5-1.9 4.5-4.4C12.5 6.4 9.5 5 9 2c-1.6 1.2-2.6 3-2.6 4.6-.8-.4-1.4-1.2-1.6-2-1 1.2-1.3 2.5-1.3 3.6C3.5 11.8 5.2 14 8 14Z" />
  </svg>
)

export const IconMenu = ({ size }: P) => (
  <svg {...base(size)}>
    <path d="M2.5 4.5h11M2.5 8h11M2.5 11.5h11" />
  </svg>
)

export const IconSidebar = ({ size }: P) => (
  <svg {...base(size)}>
    <rect x="2" y="3" width="12" height="10" rx="1.5" />
    <path d="M6.5 3v10" />
  </svg>
)
