import fs from 'node:fs';
import crypto from 'node:crypto';
import path from 'node:path';

const token = process.env.GH_TOKEN;
const headers = {
  Authorization: `Bearer ${token}`,
  Accept: 'application/vnd.github+json',
  'X-GitHub-Api-Version': '2022-11-28',
  'User-Agent': 'optifabric-verify',
};
const api = 'https://api.github.com/repos/Kynarain/OptiFabric-Reforged';
const root = 'I:\\mods\\OptiFabric';
const outDir = 'C:\\Users\\kynar\\IdeaProjects\\OptiFabric-release-223\\downloaded';
fs.mkdirSync(outDir, { recursive: true });

const expectedCommit = '80d80abcc1a9ff0cc543ae29b02a95c73c21e0ce';

function sha256(buf) { return crypto.createHash('sha256').update(buf).digest('hex').toUpperCase(); }
function localInfo(p) { const b = fs.readFileSync(p); return { size: b.length, sha: sha256(b) }; }

const local = {
  'v2.2.3': [path.join(root, 'dist', 'OptiFabric-Reforged-2.2.3+mc26.2.jar'), path.join(root, 'dist', 'OptiFabric-Reforged-2.2.3+mc26.2-sources.jar')],
  'v2.2.3+mc26.1.2': [path.join(root, 'dist', 'OptiFabric-Reforged-2.2.3+mc26.1.2.jar'), path.join(root, 'dist', 'OptiFabric-Reforged-2.2.3+mc26.1.2-sources.jar')],
};

let mismatches = 0;
let checks = 0;

// 1. tags point at the release commit
for (const tag of ['v2.2.3', 'v2.2.3+mc26.1.2']) {
  const r = await fetch(`${api}/git/ref/tags/${encodeURIComponent(tag)}`, { headers });
  const j = await r.json();
  checks++;
  if (r.status !== 200) { console.log(`TAG ${tag}: HTTP ${r.status} ${JSON.stringify(j).slice(0, 200)}`); mismatches++; continue; }
  const objType = j.object.type;
  let commitSha = j.object.sha;
  let tagObjNote = '';
  if (objType === 'tag') {
    const t = await (await fetch(`${api}/git/tags/${j.object.sha}`, { headers })).json();
    commitSha = t.object.sha;
    tagObjNote = `(annotated -> ${t.object.type})`;
  }
  const ok = commitSha === expectedCommit;
  if (!ok) mismatches++;
  console.log(`TAG ${tag}: ${objType} ${tagObjNote} -> commit ${commitSha.slice(0, 7)}  expected ${expectedCommit.slice(0, 7)}  ${ok ? 'OK' : 'MISMATCH'}`);
}

// 2/3. assets exist with intended names, API sizes equal local, downloaded sha equals local
for (const [tag, files] of Object.entries(local)) {
  const rel = await (await fetch(`${api}/releases/tags/${encodeURIComponent(tag)}`, { headers })).json();
  checks++;
  console.log(`\nRELEASE ${tag}: id=${rel.id} name="${rel.name}" assets=${rel.assets.length}`);
  const expectedNames = files.map(f => path.basename(f)).sort();
  const actualNames = rel.assets.map(a => a.name).sort();
  const namesOk = JSON.stringify(expectedNames) === JSON.stringify(actualNames);
  if (!namesOk) mismatches++;
  console.log(`  names ${namesOk ? 'OK' : `MISMATCH expected=${expectedNames} actual=${actualNames}`}`);

  for (const f of files) {
    const name = path.basename(f);
    const asset = rel.assets.find(a => a.name === name);
    checks++;
    if (!asset) { console.log(`  ${name}: MISSING on the release`); mismatches++; continue; }
    const li = localInfo(f);
    const sizeOk = asset.size === li.size;
    if (!sizeOk) mismatches++;
    const dl = Buffer.from(await (await fetch(asset.browser_download_url, { headers })).arrayBuffer());
    fs.writeFileSync(path.join(outDir, name), dl);
    const dlSha = sha256(dl);
    const shaOk = dlSha === li.sha;
    if (!shaOk) mismatches++;
    console.log(`  ${name}\n    api size=${asset.size} local size=${li.size} ${sizeOk ? 'OK' : 'MISMATCH'}\n    downloaded sha256=${dlSha}\n    local      sha256=${li.sha} ${shaOk ? 'OK' : 'MISMATCH'}`);
  }
}

// 4. /releases/latest
const latest = await (await fetch(`${api}/releases/latest`, { headers })).json();
checks++;
const latestOk = latest.tag_name === 'v2.2.3';
if (!latestOk) mismatches++;
console.log(`\n/releases/latest = ${latest.tag_name}  (expected v2.2.3)  ${latestOk ? 'OK' : 'MISMATCH'}`);

// 5. release notes content spot check
const rel2 = await (await fetch(`${api}/releases/tags/v2.2.3`, { headers })).json();
const body = rel2.body;
const needles = ['GameRenderer', 'GuiRendererDrawAccessor', 'AddInterfaceFix', '23', '21', '20', '17', 'Loading 79 mods', '1585 recipes', '1424 ms', '188876', '07B91508EE9948A5EF35FD343A057D2ACB1530B6525DF851C161390968FC45A8', '没有进世界', '40 秒'];
console.log('\nnotes spot check (v2.2.3):');
for (const n of needles) {
  const ok = body.includes(n);
  checks++;
  if (!ok) mismatches++;
  console.log(`  ${ok ? 'OK  ' : 'MISS'} ${n}`);
}
const rel12 = await (await fetch(`${api}/releases/tags/v2.2.3+mc26.1.2`, { headers })).json();
console.log('\nnotes spot check (v2.2.3+mc26.1.2):');
for (const n of ['DrawAccessor', '188879', '611C717726FA066B57CB54815224904636FD385276BD9B01159CE1D6E1C811AA', '没有进世界']) {
  const ok = rel12.body.includes(n);
  checks++;
  if (!ok) mismatches++;
  console.log(`  ${ok ? 'OK  ' : 'MISS'} ${n}`);
}

console.log(`\nCHECKS=${checks}  MISMATCHES=${mismatches}`);
