import fs from 'node:fs'
import path from 'node:path'
import type { Connect, Plugin } from 'vite'

/**
 * Mazs "backend" mācību progresam — Vite dev servera iekšā.
 *
 *   GET  /__progress  -> nolasa progress.json
 *   PUT  /__progress  -> saglabā progress.json   (POST arī, priekš navigator.sendBeacon)
 *
 * Rezerves kopijas: katru dienu pirmajā saglabāšanā vecā versija tiek nokopēta uz
 * .progress-backups/GGGG-MM-DD.json (glabā pēdējās 30 dienas).
 * Ja kaut ko sabojā — nokopē vajadzīgo backup failu pāri progress.json.
 */
export function progressPlugin(projectRoot: string): Plugin {
  const file = path.join(projectRoot, 'progress.json')
  const backupDir = path.join(projectRoot, '.progress-backups')

  const backupOncePerDay = () => {
    if (!fs.existsSync(file)) return
    const today = new Date().toLocaleDateString('sv-SE')
    const target = path.join(backupDir, `${today}.json`)
    if (fs.existsSync(target)) return
    fs.mkdirSync(backupDir, { recursive: true })
    fs.copyFileSync(file, target)
    const old = fs.readdirSync(backupDir).filter((f) => f.endsWith('.json')).sort().slice(0, -30)
    old.forEach((f) => fs.unlinkSync(path.join(backupDir, f)))
  }

  const handler: Connect.NextHandleFunction = (req, res, next) => {
    if (!req.url?.startsWith('/__progress')) return next()

    if (req.method === 'GET') {
      res.setHeader('Content-Type', 'application/json')
      res.end(fs.existsSync(file) ? fs.readFileSync(file, 'utf8') : '{}')
      return
    }

    if (req.method === 'PUT' || req.method === 'POST') {
      let body = ''
      req.on('data', (chunk) => {
        body += chunk
        if (body.length > 2_000_000) req.destroy() // aizsardzība pret milzīgu pieprasījumu
      })
      req.on('end', () => {
        try {
          const parsed = JSON.parse(body)
          if (typeof parsed !== 'object' || parsed === null || typeof parsed.values !== 'object') {
            throw new Error('Gaidīju { "values": { ... } }')
          }
          backupOncePerDay()
          const data = { savedAt: new Date().toISOString(), values: parsed.values }
          // Atomāra rakstīšana: vispirms pagaidu fails, tad pārdēvē — fails nekad nebūs pusē uzrakstīts
          const tmp = `${file}.tmp`
          fs.writeFileSync(tmp, JSON.stringify(data, null, 2) + '\n')
          fs.renameSync(tmp, file)
          res.statusCode = 204
          res.end()
        } catch (e) {
          res.statusCode = 400
          res.setHeader('Content-Type', 'application/json')
          res.end(JSON.stringify({ error: (e as Error).message }))
        }
      })
      return
    }

    res.statusCode = 405
    res.end()
  }

  return {
    name: 'be-macibas-progress',
    configureServer(server) {
      server.middlewares.use(handler)
    },
    configurePreviewServer(server) {
      server.middlewares.use(handler)
    },
  }
}
