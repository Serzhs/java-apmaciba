import path from 'node:path'
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { progressPlugin } from './progress-plugin'

const projectRoot = path.resolve(__dirname, '..')

export default defineConfig({
  plugins: [react(), progressPlugin(projectRoot)],
  server: {
    port: 5173,
    // Atļaujam lasīt failus no projekta saknes (docs/ un src/ ir mapi augstāk)
    fs: { allow: ['..'] },
    // progress.json maiņa nedrīkst pārlādēt lapu
    watch: { ignored: ['**/progress.json', '**/progress.json.tmp', '**/.progress-backups/**'] },
  },
})
