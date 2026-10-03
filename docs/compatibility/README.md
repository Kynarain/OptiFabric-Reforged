# OptiFabric + OptiFine compatibility matrix — method and limits

This directory is a self-contained test rig plus its results. It answers one narrow question:

> For each popular Fabric mod for Minecraft 1.21.1, what happens when it is loaded alongside
> **fabric-api + OptiFabric 2.2.1 + OptiFine 1.21.1 HD U J1** in a real client?

Read the limits section before quoting any verdict. A green row here is much weaker than it looks.

---

## 1. What is under test

| Component | Exact artifact |
| --- | --- |
| Loader | fabric-loader **0.19.5** (the instance) |
| Minecraft | **1.21.1** |
| Fabric API | `fabric-api-0.116.17+1.21.1.jar` |
| OptiFabric | `OptiFabric-2.2.1+mc1.21.1.jar` (Modrinth id `optifabric_reforged`), taken from `I:\mods\OptiFabric-1.21.x\dist\` and never rebuilt |
| OptiFine | `OptiFine_1.21.1_HD_U_J1.jar`, SHA-256 `DB6D2D14DB0009BDEA2D8F848E5CB55AAC52B555E8E50506DBF7876755B79082` |
| Instance | `%APPDATA%\.minecraft\versions\1.21.1-Fabric 0.19.5` |

The mod under test varies per row; everything else is held fixed for the whole sweep. Each row names the exact
file tested (version and filename) and the required dependency jars that were added with it.

### The JVM, and one discrepancy worth recording

The harness prints the java path it launched (`C:\Program Files\Java\jdk-21\bin\java.exe` on this machine), but
the runtime that actually executes the game is read back out of OptiFine's own banner in `latest.log`:

```
[OptiFine] Java: 25.0.4.1, Eclipse Adoptium
```

So the harness chose a JDK 21 path while the game reported a Java 25 runtime. That is noted, not explained; the
important point for comparability is that **every row in a sweep came from the same launch path and the same
runtime**, and the runtime is recorded per run in `logs\<mod>\verdict.json` (`javaRuntime`). The build JDK for
this line being 21 is a separate fact from the JVM the client runs on. The runtime is not changed mid-sweep.

---

## 2. How a run works

One mod per run, via `run-one.ps1` (which calls the existing harness launcher `launch-modpack.ps1` — no new
launcher was written):

1. The instance's own `mods\` is mirrored to `cache\instance-mods-mirror\`, recording every jar name and its
   SHA-256. `mods\` is then emptied and rebuilt to contain **exactly**: fabric-api + OptiFabric + OptiFine J1 +
   the mod under test + the mod's *required* dependency closure. Optional dependencies are never added.
   A pre-launch check compares `mods\` against the run manifest and refuses to launch on any mismatch, so a run
   can never silently test the wrong jar set.
2. `<instance>\.optifine` (OptiFine's patched-class cache) is cleared, and `logs\latest.log` is deleted before
   the launch. Deleting the log is what makes "did this run reach the title screen" answerable: its existence
   afterwards is proof that this run wrote it. OptiFine's own class patching is re-done from scratch each run,
   which is where most of the wall-clock time goes.
3. The client is launched offline with a fixed budget (~150 s). The wait is on the log, not the clock: the run
   stops as soon as `Sound engine started` appears, or when the log stops growing for 45 s (wedged), or at the
   budget.
4. Evidence is copied verbatim to `logs\<mod>\`: `latest.log`, the launcher stdout, the mods manifest, any crash
   reports, and `verdict.json`.
5. The instance's `mods\` is restored from the mirror and verified by hash. A `trap` handler restores it even on
   an uncaught error, and any JVM still holding the instance is killed. Nothing the instance originally had is
   ever deleted.

### Verdict criteria

| Verdict | Criterion |
| --- | --- |
| **OK** | `Sound engine started` appears in this run's `latest.log`, with no mixin/injection error naming the mod. |
| **FAIL** | A crash, or no title screen within the budget. The first causal line is recorded (`Caused by`, `InjectionError`, `LVTGeneratorError`, `Mixin transformation of`, `NoSuchMethodError`, …). |
| **DEP** | Not testable: a required dependency is missing or has no 1.21.1 Fabric build, or the mod's own metadata demands something the run cannot provide. The exact unmet requirement is stated. |
| **N/A** | The mod's 1.21.1 Fabric file does not exist, or the mod is server-only or a pure library that a client launch cannot exercise. |
| **HARNESS-ERROR** | The run itself failed: the launcher never reached its launch step, `mods\` did not match the manifest, or no fresh log was written. This says **nothing** about the mod, and any such row is retried once and then reported as a defect in this pipeline rather than as a result. |

### The no-OptiFine control

`Sound engine started` is a weak bar, and "fails with OptiFabric" and "fails on plain Fabric" are different
claims. Where a control run was done, the same mod set was launched **minus OptiFine** (OptiFabric is still
present, since it is what makes OptiFine load at all in this design). The control column records that run's
verdict and evidence. The control is a heuristic: without OptiFine, OptiFabric has nothing to patch, so a
control that still reaches the title screen is evidence the mod itself is fine on 1.21.1 Fabric and the
interaction is what broke it.

---

## 3. What these verdicts do **and do not** prove

**What an OK row proves:** the mod's jar was accepted by the loader, its mixins applied, the game finished
loading and reached the title screen with OptiFabric and OptiFine both active. That is a real and non-trivial
result: it rules out startup crashes, mixin-application failures, and hard class-linkage errors.

**What an OK row does NOT prove — this is the important part:**

- **It does not prove the mod's features work under OptiFine.** The title screen exercises a small fraction of
  any mod. A mod can reach the title screen and then break the moment you open a world, a GUI, a shader pack, or
  a specific block. Nothing in this matrix opens a world or plays the game.
- **It does not prove OptiFine's rendering still works.** No shader pack, no world, no frame was rendered. This
  matrix never checks the `post_effect` / anti-aliasing chain, shader compilation, or visual output.
- **It does not cover configuration.** Defaults only: no config files were edited, no features toggled.
- **It does not cover versions other than the exact ones in the table.** A different OptiFabric, OptiFine build,
  loader, or mod version can behave differently.
- **A FAIL row is not automatically OptiFabric's fault.** The matrix records which layer the evidence implicates
  (OptiFabric's class replacement, OptiFine's transformed classes, a genuinely missing dependency, or the mod's
  own incompatibility with 1.21.1), but the control run is what actually separates "broken with OptiFabric" from
  "broken on plain Fabric". Where no control was run, the row says so.
- **Rows marked `not run` have no verdict at all.** No guess, no inference from a similar mod, no extrapolation
  from the mod's reputation. They are listed so a later run can continue.

In short: an OK row means **"this mod does not stop the client from starting under OptiFabric + OptiFine"**.
It does not mean "this mod works with OptiFine".

---

## 4. The pool

Built from the public Modrinth API (no token). `tools/fetch-pool.mjs` pages
`/v2/search` with facets `project_type:mod`, `categories:fabric`, `versions:1.21.1`, index `downloads`, and takes
the top 100 projects. For each it records the slug/title, downloads, project id, the exact file to test
(version, filename, URL, SHA-1) and the required dependencies from both the Modrinth version metadata and the
mod's own `fabric.mod.json`.

Two metaprojects are worth naming explicitly:

- **Fabric API** is part of the base set in every run, so it cannot be tested as a subject. It is listed with
  verdict `N/A` and the reason recorded.
- **OptiFabric and OptiFine themselves are not in the pool.** They are the constants under test. What rank they
  would take does not matter to this table.

A note on scope, since it is easy to overread the pool: the top 100 by downloads is dominated by *performance*
and *library* mods (`sodium`, `lithium`, `ferrite-core`, `cloth-config`). Those are exactly the categories most
likely to collide with OptiFine's class replacements, which makes them a reasonable first target, but this is
not a random sample of Fabric mods and not the 100 most *interesting* mods.

**Download rank is a proxy for popularity, not a measure of compatibility risk.** It systematically
under-weights the *technical-mod* family — Carpet and its add-ons, Litematica's ecosystem, Tweakeroo/MiniHUD-style
client tools — because those mods are niche by design even though they are precisely the population most likely
to draw their own overlays, HUDs and render hooks into OptiFine's pipeline. Concretely, Modrinth's `carpet` has
about 11.0M downloads while this pool's rank-100 entry has about 25.9M, and the Carpet add-ons are 0.5–2.0M
each, so a pure top-100-by-downloads cut structurally cannot contain them. They are therefore tested as a
separate, explicitly non-ranked batch, marked in `MATRIX.md` as hand-selected: a table built only on download
rank would silently tell you nothing about that family.

### The extended pool (ranks beyond 100)

`tools/fetch-pool-extended.mjs` continues the same Modrinth facets past rank 100. Every row carries a `selected`
field so the report can keep the two kinds apart:

- `selected: "rank"` — reached by continuing the same download-ranked search. Ranks beyond ~100 are increasingly
  libraries, datapack/server utilities and small mods, where "OK with OptiFabric" carries much less signal than
  it does for a renderer. Those batches are therefore ordered by *expected information*, not by rank: client-side
  mods that render or inject into rendering first, then client utilities, then the rest.
- `selected: "hand"` — chosen deliberately and carrying no download rank at all (the Carpet family). These rows
  must never be presented as if rank had ordered them.

### Dependency resolution: two traps that produce false DEP rows

Both of these were hit while building the rig, and both would have put a wrong `DEP` verdict in the table. They
are recorded here because any future run that resolves Fabric dependencies from Modrinth will meet them again.

1. **Fabric API's bundled modules are only nested jars.** Fabric API ships its ~50 modules
   (`fabric-renderer-api-v1`, `fabric-resource-loader-v0`, …) as nested jars under `META-INF/jars/` inside
   `fabric-api-0.116.17+1.21.1.jar`. Its own `fabric.mod.json` `provides` list contains exactly one entry,
   `fabric`, so a resolver that trusts `provides` concludes that every one of those modules is missing. The ids
   have to be read out of the nested jars (`tools/nested-ids.mjs`). Mods do the same thing with their own
   dependencies — Xaero's minimap nests `xaerolib`, c2me nests `c2me-base`, entity-model-features nests
   `entity_texture_features` — so the same scan is also what stops the rig from adding a second jar for an id
   the mod's own jar already provides (which the loader rejects outright as a duplicate mod).
2. **A Fabric mod id is not a Modrinth slug, and search cannot always bridge the gap.** `bookshelf` is the id
   the Fabric library declares, but the project that owns the bare `bookshelf` slug is a Bukkit/Spigot plugin
   with no Fabric build at all; the Fabric library's slug is `bookshelf-lib`. Accepting the first lookup that
   answered would have recorded `enchantment-descriptions` as DEP for a dependency that resolves fine. Similarly
   `entity_texture_features` → `entitytexturefeatures`, `forgeconfigapiport` → `forge-config-api-port`, and
   `yet_another_config_lib_v3` → `yacl` (for that last one, searching the id returns zero hits, so only the slug
   works). The resolver therefore scores every candidate project on whether it actually publishes a
   **Fabric 1.21.1** file, falls back to an id index built from the jars themselves, and only then reports a
   dependency as unavailable — with the exact reason recorded in `deps\<slug>.json`.

Verified outcome: all 100 pool entries' required closures resolve, with zero unresolved dependencies. The one
row that is `DEP` without a launch is `enchantment-descriptions`' neighbour case only if this ever regresses;
as of this writing there are none.

### The instance baseline, and two jars this rig deliberately does not inherit

The baseline this rig restores the instance to after every run, and leaves in place when the sweep ends, is the
instance's **genuine original** three-jar set:

| Jar | Bytes | SHA-256 |
| --- | --- | --- |
| `fabric-api-0.116.17+1.21.1.jar` | 2452735 | `79AC44B40780ACBD884B34C50BE1E39AF682847E5F5CB3B1FDDEEAA768DCE800` |
| `OptiFabric-2.0.0+mc1.21.1.jar` | 789668 | `2F3AA5535F3556F5BAC3FFBABE4D9F9E12AAC40ACD725BD9EAB987DC9198521B` |
| `OptiFine_1.21.1_HD_U_J1.jar` | 7322249 | `DB6D2D14DB0009BDEA2D8F848E5CB55AAC52B555E8E50506DBF7876755B79082` |

That is the pre-existing state, cross-checked against an independent earlier snapshot outside this rig
(`ab-build\state\baseline\mods`, taken before any of this work) — all three hashes match exactly.

Note that the baseline carries **OptiFabric 2.0.0**, while every row of this matrix tests **2.2.1**. That is
deliberate and is the point of the exercise: the rig installs 2.2.1
(`0D9E2B0442BF50034EA45DAFDD039A8F0DA9116F281707B1476F7DFEAB5CF4F1`, 791847 B) into `mods\` for each run and
removes it again, leaving the instance's own 2.0.0 back in place. The instance is the host, not the subject.

### A discarded snapshot, and the two jars this rig does not inherit

**A cautionary note about this rig's own first mirror.** The very first snapshot this rig took of `mods\` was
recorded at 03:56:55, while a previous test run's harness cleanup was still mid-flight. It captured five jars —
`fabric-api`, `ForgeConfigAPIPort-v21.1.6-1.21.1-Fabric.jar`, `OptiFabric-2.2.1`, `OptiFine`, and
`ShoulderSurfing-Fabric-1.21.1-5.2.0.jar` — of which **three had been installed by that other run** and were
never part of the instance. That snapshot is contaminated and has been **discarded as a baseline**; it is not
restored, and no row of this matrix is built on it. It is recorded here because it is exactly the failure this
rig's guard exists to catch, and because a later run should not trust a bare directory listing as "the original
state" without comparing it to an independent reference.

`ForgeConfigAPIPort` and `ShoulderSurfing` were therefore **not** inherited. They were left behind by the earlier
ShoulderSurfing test work (a previous task's mod set), not by the instance's owner, so declining to carry them
forward discards nothing of the instance's. The instance ends the sweep at the three-jar baseline above.

### The instance-changed guard (and why it is in the rig at all)

The first version of this rig restored `mods\` from its mirror unconditionally. That is unsafe the moment the
instance is shared: if `mods\` changes while the rig is idle, restoring silently undoes someone else's work, and
it also means the run's "before" state was never actually captured.

So `run-one.ps1` now compares the instance's `mods\` against its mirror — by jar name *and* SHA-256 — and
**refuses to launch on any difference**, reporting exactly what changed ("present now but not in the mirror",
"same name, different content", "in the mirror but missing now") and writing `cause = instance-changed`. Adopting
a new baseline is possible only through an explicit `-Rebaseline` switch.

This is not hypothetical: the guard fired in practice. Mid-session the instance's `mods\` went from the mirrored
five-jar state to `fabric-api + OptiFabric-2.0.0 + OptiFine` (another agent restoring its own snapshot), then to
`fabric-api + ForgeConfigAPIPort + ShoulderSurfing` (that agent's own park/restore cycle running concurrently).
The guard caught both changes and launched nothing, which is the correct outcome and the evidence that the guard
does what it claims. It cost one aborted attempt and prevented a silent corruption of another agent's state.

### Prerequisites the mod never declares

A row is only evidence about OptiFabric if the mod's own prerequisites were satisfiable. Some mods need a library
they never declare — mentioned only in their description, or simply expected to be present — and then fail with a
loader or class-loading error. Counting that as an OptiFabric incompatibility would inflate precisely the number
this exercise is judged on, so `tools\scan-prerequisites.mjs` scans the failure region of every FAIL and
HARNESS-ERROR log for missing-prerequisite signatures: Fabric's `HARD_DEP` / `HARD_DEP_NO_CANDIDATE` /
"Incompatible mods found", "requires ... which is missing", and `NoClassDefFoundError` /
`ClassNotFoundException` / `NoSuchMethodError` in a mod's own package.

Two rules keep that scan honest, both learned by getting it wrong first:

- **Evidence must come from the failure, not from the log.** Mixin prints benign `Error loading class: <mod
  class>` lines while probing optional targets. Scanning whole files reported `immediatelyfast`, `iris`,
  `entity-model-features`, `modernfix`, `moreculling` and `c2me-fabric` as "missing a prerequisite" when all six
  are genuine OptiFine mixin conflicts. The scan now starts at the first fatal marker.
- **A named mod that was actually loaded is not missing.** OptiFine's crash reporter throws
  `NoClassDefFoundError: Could not initialize class net.optifine.reflect.Reflector` for a class that an earlier
  failed mixin already poisoned, so a class-loading error can be a *cascade* rather than a cause. The scan
  compares every named mod id against the run's own `mods-manifest.json` and ignores ids that were present.
  With that check the six false positives disappear and the scan reports zero.

Where the signature names a mod id that genuinely is not present, the resolver fetches it for 1.21.1 Fabric and
the row is re-run with it added — the point is to test the mod, not its packaging. Only when the prerequisite has
no 1.21.1 Fabric release does the row become DEP, naming the id and the reason. `prerequisites.json` records the
signature verbatim, whether the prerequisite resolved, and the resulting disposition, and the report states how
many rows were rescued this way versus how many remain DEP for want of a prerequisite. That distinction is the
difference between "OptiFabric is incompatible with this mod" and "this mod cannot be installed alone".

### Modpacks (a later phase)
`tools/verify-modpacks.mjs` produces `modpacks.json`: the top 100 Modrinth `project_type:modpack` results for
1.21.1, then — because Modrinth's `versions:` facet only proves *a* 1.21.1 version exists, on *some* loader —
every version of each project is fetched unfiltered and filtered locally on the version's own `game_versions`
and `loaders` arrays.

**84 of the top 100 ship a Fabric 1.21.1 file** (in all 84 the newest such file is Fabric-only). The other 16
are NeoForge-only, e.g. `create.ultimate`, `technical-electrical`, `lost-world-the-broken-script`,
`cobblemon-x-creating`, `pixelmon-jenny`, `create-customised`.

An earlier pass of this script reported 100/100. That was a bug, not a finding: it read a `loaders` field off
the *project* object returned by `/search`, where no such field exists (it lives on each *version*), so a
TypeError silently produced an empty array and the check degenerated to "has a 1.21.1 version on any loader".
The corrected derivation is the one in `modpacks.json`.

---

## 5. Layout

```
compat-matrix\
  run-one.ps1              one run: build mods\, launch, classify, restore
  sweep.ps1                drive a batch, restartable, retries only HARNESS-ERROR
  pool.json                the top 100 mods (Phase 1 output)
  modpacks.json            the top 100 1.21.1 modpacks, corrected loader counts
  plan.json                which mods this batch selects, and which are beyond its limit
  results.json             machine-readable per-run results (the sweep's own record)
  results-machine.json     results joined to pool metadata, one object per row
  deps\<slug>.json         per-mod resolved jar set + unresolved dependency reasons
  MATRIX.md                the table
  logs\<mod>\              verbatim evidence per run
  logs\_control-<mod>\     the no-OptiFine control run, where one was done
  cache\                   downloaded jars, API reply cache, instance mods mirror
  tools\                   the Node scripts (pool, deps, planning, report)
```

`logs\` is the raw evidence and is deliberately not paraphrased anywhere: every verdict's quoted line can be
grep-ed back to the `latest.log` it came from.

---

## 6. Reproducing

```
node tools/fetch-pool.mjs          # rebuild the pool from Modrinth
node tools/verify-modpacks.mjs     # rebuild the modpack census
node tools/resolve-deps.mjs --all  # resolve every mod's required dependency closure
node tools/plan.mjs --limit 30     # choose a batch
powershell -File sweep.ps1 -FromPlan -StopAfter 30
node tools/build-report.mjs        # write MATRIX.md and results-machine.json
```

The sweep is restartable: `sweep.ps1` folds each result into `results.json` after every mod, and re-running it
skips nothing but re-tests anything asked for. A run that fails for a harness reason is retried once
automatically; a run that produces a FAIL is never retried, because a FAIL is a result.

---

## 7. The parallel phase

Once the serial rig was validated, the remaining work (the rest of the top 100, the Carpet family, and the
extended pool to rank 300) was run across **three isolated instance copies ("lanes")**, created by
`tools\setup-lanes.ps1` under `lanes\lane1..lane3`.

**Why isolated copies are necessary, not a convenience.** A single instance cannot be shared between concurrent
clients for two independent reasons: its `mods\` folder is rebuilt per run (two lanes would test each other's
mod sets), and its `.optifine\` directory is OptiFabric's OptiFine patch cache, which is cleared at the start of
every run. So each lane gets its own complete game directory — its own `mods\`, `logs\`, `config\`, `.optifine\`,
`options*.txt`, `.fabric\`, `.mixin.out\` — while the large read-only data (`libraries\`, `assets\`) stays shared
under the real `.minecraft`. Each lane also has its own offline account name, its own `guiScale:2` /
`maxFps:60` options (restored after each run, so lanes cannot leak settings into each other), its own mirror, its
own guard, and a bounded 2 GB heap.

Lane count was decided by a smoke test, not by ambition (`tools\smoke-lanes.ps1`): all lanes launched
simultaneously with the baseline mod set. At 5 lanes the machine ran out of headroom and clients were left
orphaned when runs were killed, so the rig stepped down to the largest count that passed cleanly. Three lanes
passed: 3 of 3 reached the title screen, 27–36 s each, with peak CPU 94% of 16 logical processors, 13.3 GB of
RAM still free and no orphaned JVMs afterwards. A smaller honest number beats five lanes producing false
failures.

**Rules the parallel phase keeps** (parallelism buys throughput, not different verdicts):

- Per-run budget raised to 240 s. A starved run must not be recorded as a failure, and every failure is
  classified by evidence in the log, not by how long the client took.
- The stall detector is far looser on a lane (150 s with no log growth) than on the serial rig (45 s), and only
  applies once the client has written something at all.
- **Every row that comes out TIMEOUT, HARNESS-ERROR or otherwise ambiguous is re-run serially on one lane
  before it can be reported as a FAIL.** A row only becomes FAIL from a run whose log names a cause.
- Each lane writes its **own** `lanes\<lane>\results.json` after every mod, and the lane records are merged
  into the single `results.json` and `MATRIX.md` at the end. A crash in one lane therefore cannot corrupt the
  record of another.
- Work is split by the tier ordering described above, so lanes stay comparable.

Two real bugs were found and fixed to make concurrency safe, both worth knowing for any future run:

- The harness builds its JVM argument file at a **fixed path** (`optifabric-modpack-<instance>.args.txt` in
  `%TEMP%`). Since every lane runs the same instance *name*, all lanes wrote and read the same temp file and
  clobbered each other before the game started. The lane launcher now uses a per-launch unique name.
- Each lane's cleanup must be lane-scoped. A lane that killed "any java process" would kill a sibling lane's
  client mid-run, and a lane that matched the game version alone would too. This sandbox does not expose a
  process's command line through CIM, so the lane runner additionally snapshots the set of java PIDs before the
  launcher starts and kills only ones that appeared during its own run.

- **The instance is shared.** It is restored by hash after every run, but while a sweep is active no other
  process should use that instance. `run-one.ps1` refuses to launch if `mods\` does not match its mirror, so a
  concurrent user is detected rather than overwritten.
- **OptiFine's patching is re-done per run** (~15 s), so a run's wall clock is dominated by setup, not by the
  mod. This is deliberate: a warm `.optifine` cache would make consecutive runs test different things. A
  healthy run reaches the title screen in ~30 s, so the 120 s budget is headroom, not a measurement window.
- **This machine's wall clock jumps by hours** between processes, so all in-run timing uses a Stopwatch and the
  report never trusts `LastWriteTime` ordering alone.
- **The effective PowerShell execution policy is `Restricted`.** `run-one.ps1` therefore loads the shared
  verdict rules out of `classify.ps1` as source text into a script block instead of dot-sourcing it: a `.` of
  that file is refused with `PSSecurityException` even though the runner itself is started with
  `-ExecutionPolicy Bypass`. Any future script here that is dot-sourced will fail the same way.
- **`CommandLine` is not available from CIM in this sandbox.** `Get-CimInstance Win32_Process` returns processes
  but an empty `CommandLine`, so a run cannot be identified by its arguments. This is why the "did the client
  start" check is only ever used to produce a better error message, and why a fresh log showing the title screen
  is treated as the stronger evidence.
- **Launcher stdout is block-buffered when redirected.** The harness' own output file stays empty until it
  exits, so progress cannot be detected by reading it during a run.
- **One run per mod, no repeats.** A mod that fails intermittently would be recorded as whatever happened first.
- **`-Dmixin.debug` is off.** Mixin's own output can name the failing mod more precisely when it is on; it was
  left off to keep the run comparable to a default launch.

## 8. What was run, and what was not

This matrix was produced in phases. Coverage is stated per phase rather than implied:

| Phase | Rows | Status |
| --- | --- | --- |
| Top-100 pool by downloads | 100 | run |
| Extended pool, ranks 101-200 | 100 | run |
| Extended pool, ranks past 200 | 50 | **not run** - deliberately dropped, see below |
| Hand-picked Carpet family | 8 | run |
| CurseForge batch (50 candidates) | 50 | run: 49 launched (43 OK / 4 FAIL / 2 DEP), 1 unfetchable (its file record has no `downloadUrl`), 4 rows owed a serial re-run |
| Random-sample study (100 drawn mods) | 100 | **drawn, resolved and queued, but not launched** - the run was paused before this phase |

Every row without a verdict is marked `not run` in MATRIX.md and listed with its reason. No row is inferred from
a similar mod, from the mod's reputation, or from another platform's build of the same project. `PAUSED.md` holds
the state of the paused run - what is complete, the exact remaining queue, and the single command that resumes it.

**The rank-200 cap is a deliberate trade, not an omission.** Ranks past about 200 are overwhelmingly libraries,
datapack and server utilities and very small mods, where "OK with OptiFabric" carries almost no information,
because there is little rendering or class-transforming behaviour for OptiFine to collide with. Those hours were
reallocated to phases where a result means more.

**The CurseForge batch matters more than its size suggests.** The single clearest example is The Twilight Forest:
its official 1.21.1 Fabric port exists only on CurseForge, while Modrinth carries unofficial 1.20.1 ports. A
Modrinth-only sweep structurally cannot reach it, so its absence from this table is a statement about the
sampling frame, not about the mod. Where CurseForge and Modrinth ship different code under one name - Farmer's
Delight's Forge line versus the Refabricated port, Sophisticated Core's official Forge line versus the unofficial
Fabric port - a row must say which build was launched.

## 9. The per-row time, measured

The parallel phase's throughput was limited by the clock, not by contention, and finding that out mattered
because the first guess was wrong. Measured intervals between consecutive finished rows: **avg 312 s, min 311 s,
max 315 s**, at every lane count - i.e. each row cost the full budget plus overhead, regardless of how busy the
machine was. Nothing was serialising the lanes: each writes only its own results file and its own `.optifine`
cache, and the shared paths (jars, deps, mirror) are read-only during a run.

Where the time went for one row: ~190-210 s of client startup under ten-way load (against 16 s at one lane),
~15 s of OptiFine repatching, ~12 s of staging/restore/verification, and roughly 90 s of the client sitting at
the title screen while the launcher's own timer ran out. Both halves are now addressed: the launcher publishes
the JVM pid the moment it starts the client, and the runner stops the client and the launcher as soon as the
title marker appears instead of waiting out the budget - safe, because the runner performs its own `mods\`
restore afterwards. The residual floor is the genuine ~200 s a client needs to reach the title under ten-way
load; that is a property of the machine, not of the rig.

**The lane count is ten, and that is measured rather than assumed.** `tools\escalate-lanes.ps1` stepped the
count up one lane at a time with the baseline mod set, recording time-to-title, peak CPU and free RAM at each
step; the per-client time to the title screen was **16 s at one lane, 25 s at two, 60 s at four, 105 s at six,
152 s at eight, 191 s at ten**. Ten is the largest count that passed with every client reaching the title
screen and no orphaned JVM afterwards; the earlier five-lane smoke test that "failed" did so because orphaned
clients were left behind, not because five was too many. Each lane's heap is capped at **1536 MB**
(`-Xmx1536M`, not 2 GB: the escalation measured only ~0.5 GB free at ten lanes with a 2 GB cap).
The per-run budget in the parallel phase is **300 s** with a **300 s** stall threshold, and a stall is only
believed when the log has stopped growing *and* the client is using no CPU.

## 10. The continuation run: six defects that would each have produced a wrong table

A second pass over this rig found the following. Every one of them was live, and every one of them produces a
plausible-looking table rather than an error message, which is why they are recorded here in the same style as
section 2's traps. All six are fixed; the fix is stated with each, and `tools\check-run-staging.mjs` now checks
the fourth from the recorded evidence rather than trusting the pipeline.

### 10.1 An omitted `-Lane` meant the real instance

`lanes\run-lane.ps1` declared `[string]$Lane = "0"`, and lane `"0"` is `%APPDATA%\.minecraft\versions\...`: a
caller that forgot `-Lane` would rebuild the *user's own* `mods\` folder out of test jars. This is the one
failure that cannot be recovered from, because it destroys state the rig does not own.
**Fix:** there is no default. An empty or missing `-Lane` prints `REFUSING TO RUN: -Lane is required` and exits
2 before building any path or creating any folder; `-Lane 0` is refused by name as well, and `lanes\sweep-lane.ps1`
(the driver, whose lane `"0"` also pointed at the shared `results.json` and `logs\` at the rig root) has the same
guard. Verified by invoking both with no `-Lane` and with `-Lane 0`: nothing was written, and the real instance's
three jars are byte-identical at the end of this run (see section 11). The real instance remains reachable
through `run-one.ps1`, which is the original serial runner.

### 10.2 Required dependencies were never staged in the parallel phase

`lanes\sweep-lane.ps1` passed dependencies as `-Deps ($depJars -join ',')` through `powershell -File`. A
`[string[]]` parameter populated from a comma-joined command line collapses into **one** element, so the runner
received the single string `"A.jar,B.jar"`, every `Test-Path` failed, it printed
`MISSING SOURCE JAR: ...\A.jar,...\B.jar`, and **launched anyway**. The run set was still internally consistent,
so the pre-launch check passed and the verdict read like a real one. **44 of the 111 queued rows had two or more
extra jars**, i.e. ~40% of the parallel phase was on course to be measured on a mod set missing its required
libraries - and a missing library fails at startup in exactly the shape that gets blamed on OptiFabric.
The rig already knew this trap for `-Slugs` (see the comment above `-SlugsFile` in the same file); it had not
been applied to `-Deps`.
**Fix:** the callee splits `$Deps` on commas, which repairs every caller at once. Verified two ways: three rows
that had already run that way were confirmed defective from their own `mods-manifest.json` (`chloride`,
`drippy-loading-screen`, `capes`), and after the fix **every** staged row with dependencies contains all of them
(`tools\check-run-staging.mjs`; 15 of 15 post-fix rows correct, 4 of 4 pre-fix rows wrong). Those four rows, plus
every row whose run wrote no verdict at all, are re-run by the serial pass in `lanes\run-phases.ps1` (which needs
`-Force`, because the ordinary lane skip drops exactly the slugs that already have a verdict - the invalid one).

### 10.3 The client's identity is not on its own command line, and nine lanes deadlocked for fifteen minutes

The lane runner identifies "this run's client" by matching `java.exe` command lines against the lane's game
directory and its account name. But the harness passes the classpath and all game arguments through an
`@args` file, so the entire command line is:

```
"C:\...\java.exe" "@C:/Users/.../Temp/optifabric-modpack-1.21.1-Fabric_0.19.5-<launcherPid>-<hash>.args.txt"
```

The game directory and the username are *inside* that file. The matcher therefore matched nothing, ever - which
is why every run logged `the JVM probe did not see a client process`. That line was a statement about the probe,
not about the client, and the same broken matcher was the runner's "belt and braces" cleanup, so a client that
never reached the title screen **survived its own run**. A surviving client then did two things:

1. it kept the lane's jars locked, so the restore reported `instance mods\ restored exactly: False (5 jars)`;
2. it inherited the sweep's stdout pipe, and the sweep waits for end-of-input on that pipe - so the *parent*
   sweep blocked forever, with no error and no timeout.

That is what happened: nine of ten lanes stopped making progress at 09:12 and sat frozen, each with one live
client whose parent had exited, until the orphans were identified and killed at 09:27 (the tally was stuck at 15
rows the whole time). It was found by walking each `java.exe` parent chain and cross-checking the pid the launcher
writes into `jvm.pid` - the process tree is the only place this is visible.
**Fixes, all three needed:** the runner now stops the pid from the launcher's own `jvm.pid` **unconditionally**
(it used to read that file only on the title-screen path, so every failed run leaked its client); the command-line
matcher additionally matches the args-file name, which contains the launcher's pid and is therefore exact and
lane-scoped; and `sweep-lane.ps1` gives each child runner **files** instead of a pipeline, so a surviving
grandchild can cost a locked jar at worst, never a stopped lane.

### 10.4 A lane's own debris wedged it permanently

The instance-changed guard restores a lane's `mods\` from its verified mirror - but only when a mirrored jar was
*missing*. If the only difference was **extra** jars (precisely what a killed run leaves behind), the runner
refused to launch and exited 4 without repairing anything, and every later row in that lane hit the same refusal.
The comment above that code already says the mirror is the verified baseline and that restoring it is the safe
action; the condition did not implement it. **Fix:** extra jars that the mirror does not contain are removed and
the mirrored jars are put back, with each removal printed. A deliberate addition is still announced and still
adopted only through `-Rebaseline`.

### 10.5 A `fabric.mod.json` with raw control characters is not "no dependencies"

Six jars in this rig's own cache (`better-end`, `BetterGrassify`, `bwncr`, `Debugify`, `EuphoriaPatcher`,
`mega_showdown`) put a literal newline inside their description. Fabric's parser accepts the file; `JSON.parse`
throws; and every caller here caught the throw and concluded the jar declared **no dependencies**. That is silent
data loss in the one direction that invents compatibility failures: a mod whose required library was never added
to the run set, failing at startup for want of it. `tools\nested-ids.mjs` had the same flaw, where it also hid
every jar nested inside such a mod - and a nested dependency that looks absent gets downloaded a second time,
which the loader rejects as a duplicate mod.
**Fix:** control characters inside string literals are escaped before parsing, in all three readers
(`read-fabric-json.mjs`, `nested-ids.mjs`, `resolve-deps.mjs`). All **319** cached jars now parse (was 313), and
re-resolving the six affected rows produced **identical** `extraJars`, so no recorded row is invalidated by this
one - the affected rows' dependency *documentation* was incomplete, not their run sets.

### 10.6 The keyless CurseForge mirror silently ignores parameters it does not implement

`/v1/mods/search` accepted `searchFilter`, `gameVersion` and `modLoaderType` and returned the **identical
unfiltered page** for all three (top 50 of the class, `totalCount` 10000). Only the per-project
`/v1/mods/{id}/files` endpoint really filters by game version and loader. A "search" for `ftblibrary` therefore
answers with the 20 biggest mods of the whole class, and a resolver that trusts it will accept anything whose
*name* looks close. The first version of `tools\cf-dep-resolve.mjs` did trust it and downloaded **211 unrelated
jars** (dozens of `fabric-api` builds among them) before it was stopped and the cache was cleaned back to the 49
files the batch actually uses.
**Fix:** CurseForge-only dependencies are resolved **by id, never by search**. The mirrored file record for a row
carries `dependencies: [{modId, relationType}]` with `relationType` 3 = required, so the CF project ids of the
row's own required dependencies are known exactly; each candidate is downloaded and accepted only if the jar's
own `fabric.mod.json` declares the wanted id (and its version satisfies the declared range). `cfwidget`'s
per-project endpoint is kept only as a last resort, and its slug table is stale (`ftb-library` resolves to the
*legacy* Forge project), which is the second reason the jar's own id decides.

### 10.7 A CurseForge row can have a 1.21.1 Fabric file that cannot be fetched

`new-candidates.json` lists Structory with `Structory_26.2_v1.3.7.jar` tagged `1.21.1` + `Fabric` - and its file
record has `downloadUrl: null`, which is what a project does when it disables third-party API downloads. The file
exists; it cannot be obtained without the official API or the web UI. Rows like this are recorded `UNTESTABLE`
with that exact reason rather than dropped, and `tools\cf-fetch.mjs` reports them as
`no-1.21.1-fabric-file`/skipped: 49 of the 50 candidates were fetched.

### 10.8 Two source paths, one target file name: the manifest over-counts by one

A resolved dependency can be a jar the base set already contains. In every observed case it was `fabric-api`
(e.g. `chloride`'s closure resolves `fabric-api` as a normal dependency), so the run set copies the
Modrinth-cached `fabric-api-0.116.17+1.21.1.jar` **and** the lane mirror's own `fabric-api-0.116.17+1.21.1.jar`:
same target file name, different source paths, so `$seenSources` (which keys on the full path) does not dedupe
them. The manifest then lists one more entry than `mods\` holds, and the pre-launch check - which keys by *name* -
cannot see the collision at all. This is benign here because both sources are the same file and version; it would
stop being benign the moment a dependency resolution picked a **different** `fabric-api` version, because `mods\`
would then hold two jars providing the same mod id and the loader refuses that as a duplicate mod.
It is recorded because the symptom ("mods=5, verified 4") looks like a counting bug rather than what it is.
Not fixed: deduping by target name would need the resolver to stop proposing a jar the base set already provides,
which is a change to the resolver's dependency model rather than to the runner.

### 10.9 Two smaller things found and deliberately not fixed

* `lanes\sweep-lane.ps1`'s comment for the control run says "without OptiFabric and without OptiFine". It is
  wrong: `-Mode without` removes **OptiFine only** and keeps OptiFabric, which is correct and is what section 2
  documents (without OptiFabric there would be nothing to make OptiFine load at all). The comment, not the code,
  was in error.
* The `-FromExtended` path in `lanes\sweep-lane.ps1` references `$extendedRows` (never defined) and `$results`
  before it is created, so that path cannot work as written. It was **not exercised** by any phase here (every
  lane ran from a `-SlugsFile` queue), so it is left alone and recorded as a defect rather than patched blind.

