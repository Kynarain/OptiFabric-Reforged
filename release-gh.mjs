import fs from 'node:fs';
import path from 'node:path';

const root = 'I:\\mods\\OptiFabric';
const token = process.env.GH_TOKEN;
if (!token) { console.error('no GH_TOKEN'); process.exit(2); }

const api = 'https://api.github.com/repos/Kynarain/OptiFabric-Reforged';
const headers = {
  Authorization: `Bearer ${token}`,
  Accept: 'application/vnd.github+json',
  'X-GitHub-Api-Version': '2022-11-28',
  'User-Agent': 'optifabric-release',
};

const target = '80d80abcc1a9ff0cc543ae29b02a95c73c21e0ce';

const releases = [
  {
    tag: 'v2.2.3',
    name: 'OptiFabric Reforged 2.2.3+mc26.2',
    notes: path.join(root, 'release', 'notes', 'mc26.2.md'),
    make_latest: 'true',
    assets: [
      path.join(root, 'dist', 'OptiFabric-Reforged-2.2.3+mc26.2.jar'),
      path.join(root, 'dist', 'OptiFabric-Reforged-2.2.3+mc26.2-sources.jar'),
    ],
  },
  {
    tag: 'v2.2.3+mc26.1.2',
    name: 'OptiFabric Reforged 2.2.3+mc26.1.2',
    notes: path.join(root, 'release', 'notes', 'mc26.1.2.md'),
    make_latest: 'false',
    assets: [
      path.join(root, 'dist', 'OptiFabric-Reforged-2.2.3+mc26.1.2.jar'),
      path.join(root, 'dist', 'OptiFabric-Reforged-2.2.3+mc26.1.2-sources.jar'),
    ],
  },
];

for (const rel of releases) {
  const body = fs.readFileSync(rel.notes, 'utf8');
  const res = await fetch(`${api}/releases`, {
    method: 'POST',
    headers: { ...headers, 'Content-Type': 'application/json' },
    body: JSON.stringify({ tag_name: rel.tag, target_commitish: target, name: rel.name, body, draft: false, prerelease: false, make_latest: rel.make_latest }),
  });
  const text = await res.text();
  if (!res.ok) { console.log(`CREATE ${rel.tag} -> HTTP ${res.status}: ${text.slice(0, 500)}`); continue; }
  const created = JSON.parse(text);
  console.log(`CREATE ${rel.tag} -> HTTP ${res.status}  id=${created.id}  name=${created.name}  bodyChars=${created.body.length}`);

  for (const assetPath of rel.assets) {
    const fileName = path.basename(assetPath);
    const bytes = fs.readFileSync(assetPath);
    const url = `https://uploads.github.com/repos/Kynarain/OptiFabric-Reforged/releases/${created.id}/assets?name=${encodeURIComponent(fileName)}`;
    const up = await fetch(url, {
      method: 'POST',
      headers: { ...headers, 'Content-Type': 'application/java-archive', 'Content-Length': String(bytes.length) },
      body: bytes,
    });
    const upText = await up.text();
    if (!up.ok) { console.log(`  UPLOAD ${fileName} -> HTTP ${up.status}: ${upText.slice(0, 400)}`); continue; }
    const a = JSON.parse(upText);
    console.log(`  UPLOAD ${fileName} -> HTTP ${up.status}  stored=${a.name}  size=${a.size}`);
  }
}
