import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './styles.css'
import App from './App'
import { initProgress } from './storage'

// Vispirms ielādējam progresu no progress.json, tad zīmējam aplikāciju
initProgress().finally(() => {
  createRoot(document.getElementById('root')!).render(
    <StrictMode>
      <App />
    </StrictMode>,
  )
})
