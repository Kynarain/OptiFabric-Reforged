# Compatibility: what the 1.21.1 sweep measured

> This file summarises an empirical compatibility sweep of **OptiFabric Reforged 2.2.1 + OptiFine** against popular
> Fabric mods for **Minecraft 1.21.1**. It reports measurements; it does not advertise. The full data — every row,
> every quoted log line, the method and the limitations — is in [`docs/compatibility/`](compatibility/) (start with
> [`INDEX.md`](compatibility/INDEX.md)). Declared incompatibilities are listed in
> [`README.md`](../README.md#declared-incompatibilities-and-what-the-loader-actually-does).
>
> **One row in this snapshot has been measured again since: The Twilight Forest.** Its FAIL verdict below is a
> 2.2.1-era **title-screen** result; the dedicated investigation run on **2.2.8** measured the mod **reaching a world
> on 1.21.1, intermittently** (4 entries in 11 arms of the TF-bearing set, against 3/3 for the same set without TF),
> with retrying working and a lost attempt leaving the save usable. See §5 and §6, and the current numbers in
> [`RELEASE_NOTES.md`](RELEASE_NOTES.md) and `release/notes/mc1.21.1.md`.
>
> **The whole 33-row failure block has been re-attributed since.** All of it was re-run with a control that can fail:
> **27 of the 33 are gaps on this side**, **6 pass both arms** and were never incompatible, **0 fail the plain-Fabric
> control**, and `sodium`'s `breaks` gate is gone in 2.2.10 while the sodium stack itself is still unsupported. See
> the box at the end of §5; the per-row verdicts are in the repository-root `COMPATIBILITY.md`.

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

Snapshot: **2026-10-03**, reports generated from the merged per-run records, which already include the 49-row
CurseForge batch. **The sweep is paused, not finished** — nothing is running, and the random-sample phase was never
launched.

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
`class_702` ParticleManager, `class_757`, `class_1921`, `class_309`, `class_332`, …), 15 name no class at all, and
`sodium` is declared by this mod's own `fabric.mod.json` under `conflicts` only (2.2.8 and 2.2.9 also carried it
in `breaks`, which refused the whole instance; 2.2.10 took it back out), so it is a declared limitation rather than a
discovered defect. That declaration is a warning, not a diagnosis: the re-attribution in §5 shows the failure behind
it is a gap in the classes this mod serves, so "declared" is not the same as "external".

**The sample-based acceptance criterion has not been measured.** A 100-row random sample was drawn (seed
`optifabric-compat-matrix/random-sample/2026-10-03T09:20Z`, 61 Modrinth + 39 CurseForge rows), 6 of the hundred were
untestable before launch and all 94 launchable rows have a resolved dependency closure — but **none of them was
launched**, so no rate from that study exists. Nothing in this document should be read as that number.

## 4. The 34 failures, by category

The same 34 FAILs sorted by what each row names and by what its no-OptiFine control did, from the per-row
`Implicated layer` and *Target class in OptiFine's rewritten set?* columns of `MATRIX.md`:

| Category | Modrinth phases | CurseForge batch | Total |
|---|---|---|---|
| **FAIL rows** | 30 | 4 | **34** |
| `mod-incompatible-with-optifine` — the control reached the title screen | 27 | 0 | 27 |
| ⤷ evidence names a `net.minecraft` class in OptiFine's rewritten set | 12 | 0 | 12 |
| ⤷ evidence names no such class | 15 | 0 | 15 |
| `unexplained` — the control run produced nothing | 3 | 0 | 3 |
| `mod-broken-on-plain-fabric` — the control failed as well | 0 | 4 | 4 |
| declared by OptiFabric's own `fabric.mod.json` (`sodium`, inside the 12) | 1 | 0 | 1 |
| control that failed to run | 3 | 0 | 3 |
| attributable to OptiFabric | 0 | 0 | **0** |

The 12 rewritten-class rows are `sodium`, `iris`, `immediatelyfast`, `sodium-extra`, `entity-model-features`,
`reeses-sodium-options`, `modernfix`, `moreculling`, `c2me-fabric`, `supplementaries`, `fallingleaves` and
`carry-on`, of which `sodium` is the only one OptiFabric declares; the 3 rows with no asserted owner are `rei`,
`freecam` and `bobby` — the same 3 whose control failed to run, so "unexplained" and "control failed to run" are one
group here (`carpet-fixes`, the fourth row whose control failed to run, is a `HARNESS-ERROR` and not one of the 34
FAILs). **A failure that shares the renderer / OptiFine-rewritten-class mechanism is a property of OptiFine plus
that mod, not of OptiFabric** — the same mod starts on plain Fabric in the control, and the class its mixin could not
be transformed against is one OptiFine had already rewritten — so the count attributable to OptiFabric is **0**, and
the rows where ownership is not asserted are `rei`, `freecam`, `bobby` and, outside the 34, `carpet-fixes`.

Two disagreements in the source data, stated rather than resolved by picking a side: `MATRIX.md`'s own summary line
and group table say **4** failures are unexplained where its rows give **3** (the fourth is `carpet-fixes`, the
`HARNESS-ERROR`; the table above uses the row-level value), and `MATRIX.md` leaves the class column as `—` for all 49
CurseForge rows, so the CurseForge column above is 0 by absence of data rather than by measurement — the sweep's own
`PAUSED.md` records that `cf--the-twilight-forest`'s evidence names `class_156` and `class_638`, both in the
rewritten set. `analysis.json` is an earlier build (126 Modrinth rows, 17 FAILs: 16 `mod-incompatible-with-optifine`
plus `rei`, with the same 12 rewritten-class rows and the same single declaration) and contains no CurseForge row at
all; its mechanism counts agree with `MATRIX.md`, while its totals predate the serial re-run pass.

## 5. What the failures look like

The failures are overwhelmingly concentrated where you would expect: mods that **replace the renderer** or **inject
into classes OptiFine rewrites**. The largest single cause group is `mixin-transform` (27 of the 34), and among
those the renderer family is over-represented — `sodium`, `iris`, `immediatelyfast`, `indium`, `sodium-extra`,
`reeses-sodium-options`, `moreculling`, `entity-model-features`, `fallingleaves`, `sodium-shadowy-path-blocks` and
others are all tagged "renderer overhaul" in the matrix. The typical evidence is
`Mixin transformation of net.minecraft.class_<n> failed`, where that class is one OptiFine rewrote.

- **`sodium` is a declared conflict, and the declaration is a warning — the failure behind it is ours.**
  `fabric.mod.json` lists sodium under `conflicts` only (2.2.10). The two fields do different things on the loader
  measured for that section: a `conflicts` entry only warns — `ModSolver`'s `CONFLICTS` case adds no constraint at
  all — so the instance starts with a `Warnings were found!` line and then a normal `Loading N mods:`. A `breaks`
  entry against a mod that is present **is enforced**: the solver reports `NEG_HARD_DEP` and the loader **refuses the
  combination** (recorded runs logged `NEG_HARD_DEP optifabric_reforged 2.2.2 {breaks sodium}` and, on the published
  2.2.9 jar, `NEG_HARD_DEP optifabric_reforged 2.2.9+mc1.21.1 {breaks sodium @ [*]}` followed by
  `Incompatible mods found!`), which costs the user every other mod in the pack. **2.2.8 and 2.2.9 carried both fields
  and therefore refused to load in any instance containing sodium; 2.2.10 removed the `breaks` entry**, so a sodium
  instance now loads with a warning. That is not "sodium works": a sodium stack **still fails**, on an unbounded
  cascade of ordinary call-site gaps — `class_6491.method_24895` in `class_638.method_23777` and
  `class_310.method_1517` in `class_761.method_22714` (both repaired in 2.2.10), `class_2350.values()` in
  `class_918.method_23182`, then sodium's `class_329` `GuiMixin` redirect, with no evidence that it is the last — so
  **sodium remains unsupported: documented, not fixed**. What the measurements rule out is the usual explanation:
  **the renderers are not the blocker.** These are a call site that is gone and a helper method OptiFine's recompiler
  renamed — the same ordinary families the rest of this list needs — and each repair simply moves the run to the next
  one. Sodium's own metadata declares the pairing under `breaks` only (no `conflicts`) and names the old id
  `optifabric`, so that entry cannot fire on this line: the warning you see is ours. Its control run passes; see
  [`README.md`](../README.md#declared-incompatibilities-and-what-the-loader-actually-does) and the box below.
- **The Twilight Forest** (`twilightforest-fabric-1.21.1-4.8.734.jar`, CurseForge file id 9003337) is FAIL
  (`mixin-transform` on `class_156` / `class_638`) — but it is a CurseForge-only 1.21.1 Fabric port and its
  no-OptiFine control **also fails**, for a different reason, so it is owned `mod-broken-on-plain-fabric`: not
  attributable to OptiFabric. A Modrinth-only sweep could not have reached that mod at all.
  **This verdict is superseded, and the two named classes are not the blocker any more.** It was measured with
  **OptiFabric 2.2.1**; `class_156` and `class_638` were each given a fix in **2.2.3**
  (`RestoreVanillaMethodsFix(true, "method_29191")` and `LambdaMethodRefFix()`, both above), so today
  **The Twilight Forest reaches the title screen** next to OptiFabric + OptiFine on 1.21.1, and the dedicated
  investigation run on **2.2.8 measures world entry as intermittent — 4 entries in 11 arms of the TF-bearing set,
  against 3/3 for the same set without TF — and retrying works**; the attempt that is lost leaves the save usable.
  The stop is an idle world-open hand-off in the client, not a crash and not attributable to this mod; the shader fix
  that was needed along the way (`class_5944`, `field_29494` null) is in **2.2.8**. The current numbers are in
  [`RELEASE_NOTES.md`](RELEASE_NOTES.md) and `release/notes/mc1.21.1.md`.
- One third-party case is known, reported upstream and patched from this side: **ShoulderSurfing**'s `Camera`
  local-slot conflict, reported at `Exopandora/ShoulderSurfing#476` and addressed in this line in 2.2.1 (its
  measurement is in [`DEVELOPMENT.md`](DEVELOPMENT.md) and [`RELEASE_NOTES.md`](RELEASE_NOTES.md)). It is **not part
  of this sweep** and is cited only as context.
- `structory` could not be run: its 1.21.1 Fabric file record carries no `downloadUrl`, so the file exists but
  cannot be fetched without CurseForge's official API. It is recorded as untestable rather than dropped.

> **The 33-row `mod-incompatible-with-optifine` block has been re-tested since, and the heading it is filed under is
> wrong in both directions.** A second pass re-ran all 33 rows (plus `sodium` as the 34th) on 1.21.1 with a control
> that can fail — plain Fabric + the mod + exactly the dependency jars the sweep staged, **no OptiFabric and no
> OptiFine** — and then with our stack (2.2.8, then the 2.2.10 fixes). **27 of the 33 are gaps on this side** (25
> traced to a named fixer family, 2 to OptiFine's `Config` lifecycle), **6 pass both arms** and were never
> incompatible at all (`c2me-fabric`, `sample--mr-c2me-fabric`, `freecam`, `bobby`, `sample--mr-bedrockify`,
> `fallingleaves`), and **0 fail the plain-Fabric control**. **C2ME is not incompatible on 1.21.1**: it reaches the
> title screen *and* a world with this stack (113 s in a world, zero `[ERROR]` lines), and the matrix row that says
> otherwise is a 2.2.1-era `class_761` mixin failure that the 2.2.3 `LambdaMethodRefFix` / `LocalSlotLayoutFix`
> registrations removed. The sweep's control run cannot separate "conflicts with OptiFine's code" from "conflicts
> with the classes OptiFabric serves" — without OptiFabric the OptiFine jar is inert — so §3's owner counts and this
> section's "a property of OptiFine plus that mod, not of OptiFabric" reading are superseded for this block: the fix
> belongs on this side, and §5's "renderer family" is a description of the symptom, not of the cause. The 2.2.10
> re-test closed the recorded failure of ten rows — `cut-through`, `deeperdarker`, `modernfix`, `no-chat-reports`,
> `particle-core`, `moreculling`, `shatterbyte-lib` (which now also reaches a world), `supplementaries`' `class_836`
> gap and both `immediatelyfast` rows' first failure — and it proved that `sodium`, `iris`, `immediatelyfast` and
> `sample--mr-spectrumjei` are **cascades** rather than single gaps: each fix moves the run to the next failure
> inside the same mod, which is why a per-row list built from one run of each recorded only the first one. **Seven of
> the 33 rows are not about the named mod at all** — five are sodium's failure reached through a staged dependency
> (`sodium-extra`, `reeses-sodium-options`, `indium`, `sodium-shadowy-path-blocks`, `chloride`),
> `sample--mr-betternether` is bclib's client entrypoint and `sample--mr-spectrumjei` is modonomicon's mixin (the
> source report says "six" of the 33 while listing these seven rows; the enumeration is what was measured) — so a
> per-row list that names only the mod jar keeps mis-attributing them. The per-row verdicts are in this line's
> `COMPATIBILITY.md` (repository root) and the 2.2.10 section of `CHANGELOG.md`.

## 6. What this does not cover

> **One row here has been measured again since.** The Twilight Forest (the bullet in §5) does enter a world on
> 1.21.1 with OptiFabric + OptiFine once the 2.2.3–2.2.8 fixes are in, but only intermittently (4 of 11 arms), and
> this sweep could not have seen that: its verdict is the title screen, it ran one launch per mod, and a lost
> world-open hand-off looks exactly like a mod that never got a verdict. The current numbers are in
> `release/notes/mc1.21.1.md` and [`RELEASE_NOTES.md`](RELEASE_NOTES.md).

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

## 7. Where the data is

[`docs/compatibility/`](compatibility/) holds the snapshot copied out of the sweep workspace:
[`MATRIX.md`](compatibility/MATRIX.md) (every row, grouped failures, the CurseForge batch),
[`README.md`](compatibility/README.md) (method, traps, per-phase coverage, the defects found in the rig itself),
[`LIST.md`](compatibility/LIST.md) (the same rows as a Chinese checklist, from an earlier build of the report) and
[`upstream-optifabric-probe.md`](compatibility/upstream-optifabric-probe.md) (the probe record behind this file's and
the READMEs' upstream claims: each upstream branch's `fabric.mod.json`, and the measurement that a `conflicts`
entry only **warns** while a `breaks` entry is **enforced**). [`INDEX.md`](compatibility/INDEX.md) gives each file's
line count, source byte size and LF-canonical SHA-256, its snapshot date, and which sweep files were deliberately
**not** copied into the repository (raw logs, caches, jars, and the workspace-state file that explains how to resume
the run).
