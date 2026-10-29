import { useMemo, useState } from 'react'
import { filePaths } from '../content'
import { FileBadge } from './CodePanel'

type Node = { name: string; path: string; children: Node[]; isFile: boolean }

function buildTree(paths: string[]): Node {
  const root: Node = { name: '', path: '', children: [], isFile: false }
  for (const p of paths) {
    let cur = root
    p.split('/').forEach((part, i, arr) => {
      const path = arr.slice(0, i + 1).join('/')
      let child = cur.children.find((c) => c.name === part)
      if (!child) {
        child = { name: part, path, children: [], isFile: i === arr.length - 1 }
        cur.children.push(child)
      }
      cur = child
    })
  }
  // "Compact folders" kā VS Code: src/main/java/lv/macibas -> vienā rindā
  const compact = (n: Node): Node => {
    let node = n
    while (!node.isFile && node.children.length === 1 && !node.children[0].isFile && node.name) {
      const only = node.children[0]
      node = { ...only, name: `${node.name}/${only.name}` }
    }
    const children = node.children.map(compact).sort((a, b) => Number(a.isFile) - Number(b.isFile) || a.name.localeCompare(b.name))
    return { ...node, children }
  }
  return compact(root)
}

export function FileTree({ active, onOpen }: { active: string | null; onOpen: (path: string) => void }) {
  const tree = useMemo(() => buildTree(filePaths), [])
  const [collapsed, setCollapsed] = useState<Set<string>>(new Set())

  const toggle = (path: string) =>
    setCollapsed((prev) => {
      const next = new Set(prev)
      if (next.has(path)) next.delete(path)
      else next.add(path)
      return next
    })

  const render = (node: Node, depth: number) =>
    node.children.map((child) =>
      child.isFile ? (
        <div
          key={child.path}
          className={`tree-row file ${active === child.path ? 'active' : ''}`}
          style={{ paddingLeft: 12 + depth * 14 }}
          onClick={() => onOpen(child.path)}
        >
          <FileBadge path={child.path} />
          <span>{child.name}</span>
        </div>
      ) : (
        <div key={child.path}>
          <div className="tree-row folder" style={{ paddingLeft: 12 + depth * 14 }} onClick={() => toggle(child.path)}>
            <span className="chevron">{collapsed.has(child.path) ? '▸' : '▾'}</span>
            <span>{child.name}</span>
          </div>
          {!collapsed.has(child.path) && render(child, depth + 1)}
        </div>
      ),
    )

  return <div className="file-tree">{render(tree, 0)}</div>
}
