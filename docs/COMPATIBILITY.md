# Compatibility: what the 1.21.1 sweep measured

> This file summarises an empirical compatibility sweep of **OptiFabric Reforged 2.2.1 + OptiFine** against popular
> Fabric mods for **Minecraft 1.21.1**. It reports measurements; it does not advertise. The full data — every row,
> every quoted log line, the method and the limitations — is in [`docs/compatibility/`](compatibility/) (start with
> [`INDEX.md`](compatibility/INDEX.md)). Declared incompatibilities are listed in
> [`README.md`](../README.md#declared-incompatibilities-and-what-the-loader-actually-does).

## 1. What was tested, and how

| Component | Exact artifact |
|---|---|
| Minecraft | **1.21.1** |
| Fabric Loader | **0.19.5** |
| Fabric API | `fabric-api-0.116.17+1.21.1.jar` |
| OptiFabric | `OptiFabric-2.2.1+mc1.21.1.jar` (mod id `optifabric_reforged`) |
| OptiFine | `OptiFine_1.21.1_HD_U_J1.jar`, SHA-256 `DB6D2D14DB0009BDEA2D8F848E5CB55AAC52B555E8E50506DBF7876755B79082` |
| Launch | offline account, one mod per run, `latest.log` deleted before each run |

Each row loads exactly: fabric-api + OptiFabric + OptiFine + **the mod under test + that mod's required dependency
closure**. Optional dependencies are never added, and a pre-launch check refuses to run if `mods/` does not match the
recorded manifest. The mod under test is the only variable; everything else is held fixed.

**The verdict is "the client reached the title screen"** — `Sound engine started` appears in that run's own
`latest.log`, with no mixin/injection error naming the mod. It is not "the mod works".

> **Read this limit first.** Nothing in the sweep opens a world, plays the game, renders a frame, compiles a shader
> pack, or edits a config file. A mod that reaches the title screen and then breaks the moment a world loads, a GUI
> opens or a specific block is placed is counted here as **passing**. "OK" below means *this mod does not stop the
> client from starting next to OptiFabric + OptiFine*, and nothing more.

Each failure was additionally re-run **without OptiFine** (OptiFabric still present, since it is what makes OptiFine
load at all). That control run is what separates "broken by the OptiFine interaction" from "broken on plain Fabric".

## 2. The numbers, and their scope

Snapshot: **2026-10-03**, reports generated from the merged per-run records. **The sweep is paused, not finished** —
nothing is running, and the random-sample phase was never launched.

| Bucket | Count | Meaning |
|---|---|---|
| OK | **187** | reached the title screen |
| FAIL | **34** | crashed, or never reached the title screen within the budget |
| DEP | **16** | not testable: a required dependency is unavailable for 1.21.1, or the metadata demands something the run cannot provide |
| N/A | **18** | no 1.21.1 Fabric file, or server-only / pure library |
| HARNESS-ERROR | **1** | the run itself failed; says nothing about the mod |
| **not run** | **50** | in the pool, deliberately not launched — no verdict is claimed |

`MATRIX.md` describes **307 rows**, of which **257 carry a verdict** (its summary counts N/A as 19 because it also
lists `fabric-api`, which is part of every run's base set and therefore cannot be a subject). The merged
machine-readable record holds the **256 measured rows** — OK 187 / FAIL 34 / DEP 16 / N/A 18 / HARNESS-ERROR 1.

Coverage is stated per phase rather than implied:

| Phase | Rows | Launched | Verdicts | Not run |
|---|---|---|---|---|
| top-100 pool by downloads | 99 | 99 | DEP 9, FAIL 15, OK 75 | — |
| extended pool, ranks 101–200 | 100 | 100 | DEP 3, FAIL 15, N/A 15, OK 67 | — |
| hand-picked Carpet family | 8 | 8 | DEP 2, HARNESS-ERROR 1, N/A 3, OK 2 | — |
| CurseForge batch (50 candidates) | 50 | 49 | DEP 2, FAIL 4, OK 43 | 1 (no fetchable 1.21.1 Fabric file) |
| extended pool, ranks past 200 | 50 | 0 | — | **50, by design** |
| random-sample study (100 drawn) | 100 | 0 | — | drawn, resolved and queued, **never launched** |
| modpack census | 100 | 0 | — | a census, not launch rows |

The 50 unrun rows past rank 200 are libraries, datapack utilities and small mods, where "OK with OptiFabric" carries
very little information; those hours went to the CurseForge batch and the sample draw instead. They are labelled
`not run` and are never counted as a pass.

## 3. Whose failure is it?

A FAIL is only evidence about OptiFabric if the same mod starts without OptiFine. Over all **34 failures**, the
no-OptiFine control run gives:

| Owner | Count | What the control run showed |
|---|---|---|
| `mod-incompatible-with-optifine` | **27** | starts on plain Fabric, so the failure belongs to the OptiFine interaction |
| `mod-broken-on-plain-fabric` | **4** | fails with no OptiFine too (all four are CurseForge rows) |
| `unexplained-control-failed-to-run` | **3** | the control itself produced nothing, so ownership is **not asserted** (`bobby`, `freecam`, `rei`) |

**Attributable to OptiFabric's own transformations: 0** — stated exactly: **0 among 255 classified rows, with 1
unknown (`carpet-fixes`, the row whose rig run failed); Wilson 95 % upper bound 1.5 %.** Of the 27 control-passing
failures, 11 name a `net.minecraft` class that is in OptiFine's extracted rewritten set (`class_761` WorldRenderer,
`class_702` ParticleManager, `class_757`, `class_1921`, `class_309`, `class_332`, …), 15 name no class at all, and 1
— `sodium` — is declared by this mod's own `fabric.mod.json` (in both `conflicts` and `breaks`), so it is a
pre-declared limitation rather than a discovered defect.

**The sample-based acceptance criterion has not been measured.** A 100-row random sample was drawn (seed
`optifabric-compat-matrix/random-sample/2026-10-03T09:20Z`, 61 Modrinth + 39 CurseForge rows), 6 of the hundred were
untestable before launch and all 94 launchable rows have a resolved dependency closure — but **none of them was
launched**, so no rate from that study exists. Nothing in this document should be read as that number.

## 4. What the failures look like

The failures are overwhelmingly concentrated where you would expect: mods that **replace the renderer** or **inject
into classes OptiFine rewrites**. The largest single cause group is `mixin-transform` (27 of the 34), and among
those the renderer family is over-represented — `sodium`, `iris`, `immediatelyfast`, `indium`, `sodium-extra`,
`reeses-sodium-options`, `moreculling`, `entity-model-features`, `fallingleaves`, `sodium-shadowy-path-blocks` and
others are all tagged "renderer overhaul" in the matrix. The typical evidence is
`Mixin transformation of net.minecraft.class_<n> failed`, where that class is one OptiFine rewrote.

- **`sodium` is a declared incompatibility**, not a discovered one: this mod's `fabric.mod.json` lists it in both
  `conflicts` and `breaks`, and on the loader version measured for that section neither field prevents the game
  from starting — both are warnings, so the entry is a declaration rather than a gate. Sodium's own metadata
  declares the pairing under `breaks` only (no `conflicts`) and names the old id `optifabric`, so that entry
  cannot fire on this line: our entry is the only one a loader acts on. Its control run passes; see
  [`README.md`](../README.md#declared-incompatibilities-and-what-the-loader-actually-does).
- **The Twilight Forest** (`twilightforest-fabric-1.21.1-4.8.734.jar`, CurseForge file id 9003337) is FAIL
  (`mixin-transform` on `class_156` / `class_638`) — but it is a CurseForge-only 1.21.1 Fabric port and its
  no-OptiFine control **also fails**, for a different reason, so it is owned `mod-broken-on-plain-fabric`: not
  attributable to OptiFabric. A Modrinth-only sweep could not have reached that mod at all.
- One third-party case is known, reported upstream and patched from this side: **ShoulderSurfing**'s `Camera`
  local-slot conflict, reported at `Exopandora/ShoulderSurfing#476` and addressed in this line in 2.2.1 (its
  measurement is in [`DEVELOPMENT.md`](DEVELOPMENT.md) and [`RELEASE_NOTES.md`](RELEASE_NOTES.md)). It is **not part
  of this sweep** and is cited only as context.
- `structory` could not be run: its 1.21.1 Fabric file record carries no `downloadUrl`, so the file exists but
  cannot be fetched without CurseForge's official API. It is recorded as untestable rather than dropped.

## 5. What this does not cover

1. **No world, no rendering, no shaders** — see the box in §1. The single biggest blind spot is any failure that
   only appears after a world is loaded.
2. **One mod per run.** No combinations, no modpacks, no interaction between two non-base mods.
3. **Default configuration only.** No config file was edited and no feature was toggled.
4. **One run per mod.** A mod that fails intermittently is recorded as whatever happened first.
5. **`-Dmixin.debug` was off**, so the logs match a default launch and are less specific about *which* mod a mixin
   failure belongs to than they could be.
6. **One exact version set.** A different OptiFabric, OptiFine build, loader or mod version can behave differently.
7. **CurseForge coverage is not a census.** The CurseForge frame is the top of that platform by downloads, and each
   project needed its own file query because the keyless API mirror ignores loader/version filters.
8. **The sweep is paused**, with two re-run queues still owed — 4 rows whose own run or control produced nothing
   usable (`carpet-fixes`, `rei`, `bobby`, `freecam`) and 4 CurseForge rows (two invalid `DEP` rows measured before a
   missing dependency was restored, and two timeouts) — plus the 50 rank-past-200 rows deliberately dropped and the
   100-row sample never launched. A resumed run would change these numbers.

## 6. Where the data is

[`docs/compatibility/`](compatibility/) holds the snapshot copied out of the sweep workspace:
[`MATRIX.md`](compatibility/MATRIX.md) (every row, grouped failures, the CurseForge batch),
[`README.md`](compatibility/README.md) (method, traps, per-phase coverage, the defects found in the rig itself) and
[`LIST.md`](compatibility/LIST.md) (the same rows as a Chinese checklist, from an earlier build of the report).
[`INDEX.md`](compatibility/INDEX.md) lists what each file is, its snapshot date, and which sweep files were
deliberately **not** copied into the repository (raw logs, caches, jars, and the workspace-state file that explains
how to resume the run).
