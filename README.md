# OptiFabric

<p align="center">
  <img src="src/main/resources/assets/optifabric/icon.png" alt="OptiFabric" width="128"/>
</p>

[🇨🇳 中文版](./README_CN.md) | 🇬🇧 English

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21%20~%201.21.11-green.svg)](https://www.minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-%E2%89%A5%200.19.5-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21%2B-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MPL--2.0-lightgrey.svg)](LICENSE.txt)

> ⚠️ This port was written and verified with AI assistance (DeepSeek). Be careful with it in production.

Load **OptiFine** under **Fabric Loader**: put OptiFine's jar next to this mod and it patches the vanilla client with OptiFine's own patcher, rebuilds the lambdas whose targets moved, remaps OptiFine into Fabric's namespace, repairs what OptiFine's recompiler left behind, and hands the patched Minecraft classes to Loader's class transformer — so both mods can live in one client. **OptiFine itself is not bundled or redistributed.**

This is the **1.21.x line** and covers Minecraft **1.21 – 1.21.11** (all ten releases OptiFine ever shipped a build for). The 26.x line (Minecraft 26.2 / 26.1.2) and the 1.20.6 line live on their own branches; jars from different lines are **not interchangeable**.

**Author:** kynarain · upstream: Modmuss50, Chocohead · **License:** MPL-2.0 · **Current version:** `2.2.14`

## 📦 Installation

1. Install **Fabric Loader ≥ 0.19.5** on **Java 21+**, then put this line's jar and the OptiFine jar for **exactly** your Minecraft version into that instance's `mods/` folder. Do **not** run OptiFine's installer.
2. Launch the **Fabric** profile — not a launcher-made `1.21.x-OptiFine_xxx` profile, which injects OptiFine itself and collides with this mod.
3. The first start spends a few extra seconds patching and remapping (5–7 s in practice); later starts reuse the cache (1–2 s).

**Upgrading from 1.x?** Delete the old `OptiFabric-<version>+mc1.21.x.jar` first: 2.0.0 renamed the mod id to `optifabric_reforged` (display name *OptiFabric Reforged*), and with both ids in `mods/` Fabric loads both copies, so OptiFine gets patched twice.

> **OptiFabric cannot assume your launcher installs OptiFine for you**, because many launchers only offer **OptiFine _or_ Fabric** as the profile, never both: install **Fabric + OptiFabric** first, download OptiFine from <https://optifine.net/downloads>, put that jar in the `mods` folder, then start the game once.
>
> **OptiFabric 不能假设启动器会替你装 OptiFine**:很多启动器只能选 **OptiFine _or_ Fabric**。先装好 **Fabric + OptiFabric**,从 <https://optifine.net/downloads> 下载 OptiFine,放进 `mods` 文件夹,然后手动启动一次游戏。

With version isolation (PCL2 / HMCL) the game directory, `mods/` and the `.optifine/` cache all live under `versions/<name>/`.

## 🤝 Compatibility

| Minecraft | jar | OptiFine build | State |
|---|---|---|---|
| 1.21 | `OptiFabric-2.2.14+mc1.21.jar` | `preview_OptiFine_1.21_HD_U_J1_pre9.jar` | ✅ verified |
| 1.21.1 | `OptiFabric-2.2.14+mc1.21.1.jar` | `OptiFine_1.21.1_HD_U_J1.jar` | ✅ verified |
| 1.21.3 | `OptiFabric-2.2.14+mc1.21.3.jar` | `OptiFine_1.21.3_HD_U_J2.jar` | ✅ verified |
| 1.21.4 | `OptiFabric-2.2.14+mc1.21.4.jar` | `OptiFine_1.21.4_HD_U_J3.jar` | ✅ verified |
| 1.21.6 | `OptiFabric-2.2.14+mc1.21.6.jar` | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` | ⚠️ plays, **no shaders** (below) |
| 1.21.7 | `OptiFabric-2.2.14+mc1.21.7.jar` | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` | ⚠️ same |
| 1.21.8 | `OptiFabric-2.2.14+mc1.21.8.jar` | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` | ✅ verified |
| 1.21.9 | `OptiFabric-2.2.14+mc1.21.9.jar` | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` | ✅ verified |
| 1.21.10 | `OptiFabric-2.2.14+mc1.21.10.jar` | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` | ✅ verified |
| 1.21.11 | `OptiFabric-2.2.14+mc1.21.11.jar` | `OptiFine_1.21.11_HD_U_J9.jar` | ✅ verified |

OptiFine never shipped a build for **1.21.2 / 1.21.5**, so there is no jar for those. The build listed is the newest **final** OptiFine release for that Minecraft version, or the newest **preview** when no final exists yet (that is why 1.21.4 lists J3 rather than the newer J4_pre2). Any other build of the same Minecraft version still works: the in-game prompt only appears when the jar in `mods/` is a **preview older** than the one listed — a final build you already have is left alone even when a newer final exists.

### OptiFabric version → Minecraft version → required OptiFine build

This is the same list the mod itself carries and `release\notes\mc<MC>.md` states per release; `release\version.ps1 -CheckSupport` fails when the three disagree.

| OptiFabric version | Minecraft version | Required OptiFine build |
|---|---|---|
| `2.2.14+mc1.21` | 1.21 | `preview_OptiFine_1.21_HD_U_J1_pre9.jar` |
| `2.2.14+mc1.21.1` | 1.21.1 | `OptiFine_1.21.1_HD_U_J1.jar` |
| `2.2.14+mc1.21.3` | 1.21.3 | `OptiFine_1.21.3_HD_U_J2.jar` |
| `2.2.14+mc1.21.4` | 1.21.4 | `OptiFine_1.21.4_HD_U_J3.jar` |
| `2.2.14+mc1.21.6` | 1.21.6 | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` |
| `2.2.14+mc1.21.7` | 1.21.7 | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` |
| `2.2.14+mc1.21.8` | 1.21.8 | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` |
| `2.2.14+mc1.21.9` | 1.21.9 | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` |
| `2.2.14+mc1.21.10` | 1.21.10 | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` |
| `2.2.14+mc1.21.11` | 1.21.11 | `OptiFine_1.21.11_HD_U_J9.jar` |

When that jar is missing, or is not the build its Minecraft version expects, the game says so in a dialog at the title screen instead of starting silently: the dialog names the file, offers a `Download OptiFine` button that fetches it from OptiFine's **official** site (`optifine.net`, the only source this mod ever uses), and opens the mods folder for you.

### The 1.21.6 / 1.21.7 shader limitation

Both releases **start and play normally** without shaders, but **enabling a shader pack crashes during startup**, inside OptiFine's own code:

```
java.lang.NullPointerException: Cannot read field "norm" because "multiTex" is null
  at net.optifine.shaders.ShadersTex.initDynamicTextureNS(ShadersTex.java:322)
```

It is not the shader pack (three unrelated packs crash at the same frame) and not our patching (the crash happens while the first textures are created). All seven OptiFine builds available for those two releases crash identically, so downgrading does not avoid it. Since **2.2.12** the mod tells you this in the same title-screen dialog as soon as it sees that build installed — play without shaders, or move to a Minecraft version whose newest OptiFine build is a final release.

| | |
|---|---|
| ✅ Works | OptiFine's video settings, zoom, connected textures, dynamic lights, **shaders** (every release except 1.21.6 / 1.21.7), and since 1.1.2 **anti-aliasing** |
| ⚠️ Neutralised | the `BEFORE_BLOCK_OUTLINE` event no longer fires (the outline is still drawn); the moving-block FRAPI render hook is inert (moving blocks are drawn by the vanilla path) |
| ❌ Incompatible | **Sodium**, plus `no_fog`, `thallium`, `xradiation`, `ryoamiclights` — all five under `breaks`, and the loader enforces it (see the section below) |
| 📄 OptiFine-side limits | OptiFine cannot see resources inside Fabric mods (`[OptiFine] Unknown resource pack type: …ModNioResourcePack`); shader packs print their own `[Shaders]` errors when they do not match your OptiFine build |

### Declared incompatibilities, and what the loader actually does

Everything this mod declares against lives in `fabric.mod.json` and nowhere else — no screen shows it — so this is that list in prose. The current artifact declares **five** `breaks` entries and no `conflicts`:

| Declaration | Entries |
|---|---|
| `breaks` | `no_fog`, `thallium`, `xradiation`, `ryoamiclights`, `sodium` |
| `conflicts` | *(none)* |

**`breaks` is a gate, `conflicts` is only a warning.** Measured on Fabric Loader 0.19.5: a `conflicts` entry adds no constraint to the loader's solver at all — `ModSolver`'s `CONFLICTS` case is still a `// TODO: soft negative dep?` — so the game starts with `Warnings were found!`, while a `breaks` entry against a present mod makes the solver emit `NEG_HARD_DEP` and the loader **refuses the whole instance** (`Incompatible mods found!`). So a `breaks` entry does not warn, it blocks — and that is deliberate here (see Sodium below).

**Where the list comes from.** `no_fog`, `thallium` and `xradiation` are **inherited from upstream** ([Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric), `fabric.mod.json` on `llama`); `ryoamiclights` is this fork's own addition, because OptiFine replaces the whole video-settings screen — parent class included — and that mod's mixin targets the vanilla parent (nothing is lost: OptiFine has its own dynamic lights). Upstream also carries three range-limited entries this fork dropped as 1.16/1.17-era: `cardinal-components-item <2.4.2`, `architectury >1.2.72 <1.3.77` and `meteor-client >=0.4.1`.

**Sodium is a hard break since 2.2.11, and it is measured rather than inherited.** The history is worth knowing because it moved twice: 2.2.8/2.2.9 declared it in both fields (refusing the instance), 2.2.10 moved it to `conflicts` only (a warning), and 2.2.11 put it back under `breaks`. The reason changed with the evidence — on 1.21.1 with Sodium 0.8.13 the cascade behind Sodium's missing call sites was worked through (five repairs, listed in [`CHANGELOG.md`](CHANGELOG.md)), and with every one of them in place Sodium's own mixins all apply (`Mixin transformation of` and `InjectionError` both zero), the client reaches the title screen and loads a world — and the frame is **black**. Two single-variable runs ruled out what was left (handing the single renderer slot to Sodium instead of this mod's placeholder; OptiFine's Fast Render off): both still black. Two terrain renderers cannot share one pipeline, so the pairing is refused rather than warned about — a warning would leave the user with a game that starts and never draws.

**Sodium declares it on its side too, but under the old id.** Every Sodium build measured carries `"breaks": {"optifabric": "*"}` and names the **old** mod id; since 2.0.0 this line ships as `optifabric_reforged`, so that entry cannot fire. What you see for Sodium is this mod's own declaration.

**Architectury is the reverse story.** Upstream declared architectury broken and architectury's metadata declares `breaks: optifabric <1.13.0`. On 1.21.1 this fork fixed the real conflict behind it: OptiFine inserts its own locals into the middle of `GameRenderer.render`, shifting the vanilla slots Mixin's `LocalCapture` hands to a handler, so `LocalSlotLayoutFix` moves the extra slots to the end of the range. `architectury-api` `13.0.11` now passes, and the rename to `optifabric_reforged` is what stops architectury's own declaration from firing. Measurements: [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md), [`docs/RELEASE_NOTES.md`](docs/RELEASE_NOTES.md).

The empirical picture — which mods actually fail next to OptiFabric, and which of those failures are ours — is in [`docs/COMPATIBILITY.md`](docs/COMPATIBILITY.md); the full per-mod list is [COMPATIBILITY.md](COMPATIBILITY.md) at the repository root.

### How indigo is handled

Fabric API's `fabric-renderer-indigo` and OptiFine cannot both render terrain, so this mod makes indigo step aside with Fabric's own mechanism: `"custom": {"fabric-renderer-api-v1:contains_renderer": true}` in `fabric.mod.json` (the key Sodium uses — OptiFine *is* a terrain renderer). Indigo then logs `[Indigo] Different rendering plugin detected; not applying Indigo.` The key alone is not enough: Fabric's renderer modules read a registry, and an empty one throws, so the mod also registers an inert placeholder (`F3` shows `Renderer: OptifineRendererPlaceholder`).

## 🏗️ How It Works

```
mods/OptiFine_1.21.11_HD_U_J9.jar
        │  ① OptiFine's own optifine.Patcher patches the obfuscated client jar
        ▼
   patched vanilla jar   (OptiFine's patches + OptiFine's classes)
        │  ② LambdaRebuilder: lambdas in patched classes point at methods that moved
        │  ③ tiny-remapper: official (obfuscated) → intermediary
        │     the game jar must be in the remapper's classpath, or overrides in
        │     subclasses keep OptiFine's names
        ▼
   Optifine-mapped.jar
        │  ④ split in two
        ├── OptiFine's own classes + resources ─► game class path
        └── patched net/minecraft/** classes ───► ClassCache
```

Replacement goes through **Loader's own GameTransformer**: when a Minecraft class is about to load, Loader asks the game provider's `GameTransformer.transform(...)` for ready-made bytecode — *before* Mixin runs. What is handed over is Mixin's **input**, not its output, so other mods' mixins against those classes keep working, and no stub mixin has to be generated per patched class. One hard constraint: **before the patched classes are handed to Loader, nothing may reflect on game classes** — a single `Class.getMethods()` would pin a class to vanilla forever — so this code only ever touches bytes.

| Component | Purpose |
|---|---|
| `OptifabricRuntime` | whole pipeline: find the jar → patch → remap → repair → register |
| `GameTransformerHook` | injects the patched classes into Loader's game transformer |
| `OptifineMappings` | rule-based contextual mapping (replaces upstream's hand-written table) |
| `OptifineJarFixer` | repairs OptiFine's own jar (post-effect JSON shape, the FXAA vertex shader, …) |
| `patcher/fixes/**` | the per-release bytecode fixers (vanilla bodies, injection points, synthetic fields, …) |
| `RendererApiFallback` | registers an inert placeholder where Fabric's renderer API would otherwise be empty |

Intermediate files live in `<game dir>/.optifine/<OptiFine version>/`: `cache-format.txt` (format version — a mismatch rebuilds everything), `Optifine-mapped.jar` and `Optifine.classes.gz` (the patched MC classes for the next launch).

## ✨ Key Features

- 🔄 **No installer run by hand** — drop the installer jar or an already-extracted OptiFine into `mods/`; patching happens at startup
- 🧩 **Remapping that sees the game** — the game jar goes into the remapper's classpath *and* inputs, so methods overridden in subclasses keep their mapped names (one class alone lost 35 methods otherwise)
- 📦 **One source tree, one jar per release** — each bound to its own mapping table, built with `-Pmc=<version>`
- 🎨 **Anti-aliasing that works** — since 1.1.2 this mod leaves OptiFine's own `post_effect/` chain alone and rewrites the FXAA vertex shader only on the releases whose post pipeline has no vertex attributes (1.21.9 / 1.21.10)
- 🛠️ **Bytecode repairs** — dozens of fixers for what OptiFine's recompiler erases: vanilla method bodies, injection points, synthetic fields, object-creation points, renamed lambdas, region construction
- 🧪 **Offline verification as a first-class tool** — every patched class and every OptiFine class is loaded in a single loader and checked with the JVM verifier plus an ASM data-flow verifier
- ⚙️ **Caching** — the pipeline result is cached under `.optifine/<OptiFine version>/`; later launches take 1–2 seconds
- 🧯 **Honest failure** — a missing, corrupt, duplicated or mismatched OptiFine jar produces a dialog at the title screen and an `OptiFabric` section in the crash report

## 📊 Benchmarks

Measured with the third-party **FPS Benchmark** mod (`fpstest-1.0.jar`, sha256 `F11681914771E01A4677DA5EF217195FF523B01E9C3F463BF9A298D7BCCB2C56`): one 3-minute scripted *Base* run per group (19 cinematic segments; deterministic seed `27182`). Raw per-frame reports: **[`benchmarks/2026-10-05-fps-benchmark/`](benchmarks/2026-10-05-fps-benchmark/)**.

**Environment — identical in all four runs:** Minecraft 1.21.1 · Fabric Loader 0.19.5 · Java 22.0.2 · max heap 5836 MB · Intel i5-12600KF · AMD RX 7800 XT (driver 25.12.1.251128) · Windows 10 amd64 · render distance 8 · VSync off · `maxFps:260` (= uncapped) · shaders off · identical `options.txt` (sha256 `AC506701…`) · seed 27182

| group | role | mods (sha256 prefix) |
|---|---|---|
| **A** | vanilla baseline | `fabric-api-0.116.17+1.21.1.jar` `79AC44B4` + `fpstest-1.0.jar` `F1168191` |
| **C** | OptiFabric + OptiFine only | A + `OptiFabric-2.2.10+mc1.21.1.jar` `A897DA34` + `OptiFine_1.21.1_HD_U_J1.jar` `DB6D2D14` |
| **D** | the recommended set | C + `lithium-fabric-0.15.4+mc1.21.1.jar` `92329D98` + `ferritecore-7.0.3-fabric.jar` `98C3AB1D` + `c2me-fabric-mc1.21.1-0.4.0-alpha.0.29.jar` `9C4C1C4C` |
| **B** | Sodium route (not compatible with OptiFine) | A + `lithium-fabric-0.15.4+mc1.21.1.jar` `92329D98` + `sodium-fabric-0.8.13+mc1.21.1.jar` `3D43C149` |

| group | avg FPS | 1% low | 0.1% low | p99 frame | worst frame | lowest FPS | std dev | raw results |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| **A** | 3631 | 774 | 442 | 0.76 ms | 17.21 ms | 58.1 | 706 | [report.md](benchmarks/2026-10-05-fps-benchmark/A-vanilla-baseline/report.md) |
| **C** | **5040** | **1098** | **713** | 0.58 ms | **3.31 ms** | 302 | 964 | [report.md](benchmarks/2026-10-05-fps-benchmark/C-OptiFabric-OptiFine/report.md) |
| **D** | 4475 | 1077 | 721 | 0.63 ms | 3.14 ms | **319** | **747** | [report.md](benchmarks/2026-10-05-fps-benchmark/D-OptiFabric-OptiFine-LiFeC2ME/report.md) |
| **B** | **5680** | 1017 | 349 | **0.50 ms** | 12.47 ms | 80.2 | 1177 | [report.md](benchmarks/2026-10-05-fps-benchmark/B-Sodium-Lithium/report.md) |

- **C averages 38.8% above the vanilla baseline** and its worst frame is 5× better (3.31 ms vs 17.21 ms) — the compatibility layer is a net gain, not a tax.
- **B (Sodium) is fastest on average (~13% over C) but has the worst tail**: 0.1% low 349 vs 713, worst frame 12.47 ms vs 3.31 ms.

### Full suite — 41 tests, all four configurations

Same machine and settings; category averages as **average FPS / 1% low** (raw reports under [`benchmarks/`](benchmarks/2026-10-05-fps-benchmark/)):

| category (tests) | A vanilla | B Sodium + Lithium | C OptiFabric + OptiFine | D = C + Lithium + FerriteCore + C2ME |
|---|---|---|---|---|
| idle / static (2) | 4579 / 1278 | **5961** / 1712 | 5388 / 1529 | 5670 / 1686 |
| flyby, 12 biomes (12) | 2606 / 288 | **5254** / 649 | 4220 / 461 | 4382 / **777** |
| entities (8) | 1496 / 537 | **1861** / 681 | 1595 / 587 | 1626 / 638 |
| physics (6) | 3066 / 602 | **4040** / 924 | 3915 / 785 | 3979 / 901 |
| redstone / block entities (5) | 3935 / 1078 | **5537** / 1523 | 4898 / 1369 | 5004 / 1404 |
| particles (2) | 1972 / 523 | 2634 / 510 | 2331 / 669 | **2670** / **770** |

- **The vanilla baseline is last in all six categories**, and **D is at or above C in almost every one** with clearly better tails (flyby 1% low 777 vs 461, particles 770 vs 669) — so Lithium / FerriteCore / C2ME *do* earn their keep in real scenarios, while the still *Base* scene above cannot show it.
- **Particle diversity is the one scenario no mod set fixes**: all four land between 437 and 495 fps. That bottleneck is not ours.

*One machine, one run per group, one benchmark, resolution not forced. Treat these numbers as an indication, not a specification — yours will differ.*

## 📊 Verified State

Every release is checked by one command per Minecraft version:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-version.ps1 -Version 1.21.10 -ModVersion 1.1.2
```

It loads every patched game class and every OptiFine class in a single loader, runs the JVM verifier plus an ASM data-flow verifier, and fails on any `@At` / refmap / contract / handle problem. The per-version numbers are recorded in [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) and [`docs/RELEASE_NOTES.md`](docs/RELEASE_NOTES.md); the only remaining `@At` misses are **deliberately disabled** Indigo injections.

**Known gaps:**

1. **No development-environment support** — dev runs in the `named` namespace and would need a two-stage remapping plus upstream's contextual-mapping fixes (`gradlew runClient` is refused outright)
2. **The two neutralised hooks are placeholders**, not working implementations (`Renderer.get()` returns an inert renderer)
3. **1.21.6 / 1.21.7 shaders** — waiting either for a new OptiFine build or for wiring up the already-written `GpuTextureLinkFix`

## 🔨 Building from Source

**JDK 21+** is required, and the repository root *is* the Gradle project — the target release comes from `-Pmc` (or from `gradle.properties` when omitted):

```bash
./gradlew build "-Pmc=1.21.11"                            # quotes matter in PowerShell: 1.21.11 is split otherwise
./gradlew build "-Pmc=1.21.8" "-Pmod_version_base=2.1.0"  # only when that jar's version differs from the base
```

The jars land in `build/libs/`. Version numbers only ever change through one script:

```powershell
.\release\version.ps1                                      # show the current version of the line
.\release\version.ps1 -Mc 1.21.8 -Kind patch                # bump one jar, and only that jar
```

## 📋 Requirements

| | |
|---|---|
| Minecraft | 1.21, 1.21.1, 1.21.3, 1.21.4, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, 1.21.11 |
| Fabric Loader | ≥ 0.19.5 |
| Java | 21+ (tested on 25) |
| Side | client |
| OptiFine | your own build, **exact version match** (table above) |
| Fabric API | optional — tested with the release matching your Minecraft version |

## 📝 Project Structure

```
OptiFabric/
├── src/main/java/kynarain/cn/optifabric/
│   ├── Optifabric.java              # preLaunch entry point
│   ├── mod/                         # pipeline, transformer hook, jar fixer, renderer fallback
│   ├── patcher/                     # ClassCache, LambdaRebuilder and fixes/ (the bytecode fixes)
│   ├── mixin/                       # the two mixins this project ships
│   └── util/                        # ASM / mixin / remap / zip helpers
├── src/main/resources/              # fabric.mod.json, optifabric.mixins.json, assets/…/icon.png
├── COMPATIBILITY.md                 # the full per-mod compatibility list (MC 1.21.1)
├── docs/                            # DEVELOPMENT.md, COMPATIBILITY.md (+ _CN), compatibility/, …
├── release/                         # version.ps1, publish.ps1, notes/, MANUAL_RELEASE.md
└── build.gradle · gradle.properties · settings.gradle · gradlew(.bat)
```

## 🙋 Support

**[`docs/FAQ.md`](docs/FAQ.md) answers 18 questions first** - the OptiFine dialog, why Sodium is refused, the Sodium family, Photon and the flicker, the `Invalid program name` and `prepare` log noise, the 1.21.6/1.21.7 shader crash, Sinytra Connector and Kilt, whether OptiFine is bundled or downloaded, and what to include in a report.

- **Nothing changed after upgrading?** Delete `<game dir>/.optifine/` — the cache holds patched bytecode and is rebuilt automatically (the format version also invalidates it).
- `[OptiFabric]`'s output goes to the **launcher console**, not `logs/latest.log`; filtering for `[OptiFabric]` shows how many classes were prepared and how many Loader took over.
- The title screen shows a dialog for a missing/corrupt/duplicated/mismatched OptiFine jar, and the crash report gains an `OptiFabric` section (OptiFine version, jar state, mapped-jar path).
- Point the mod at a specific vanilla jar with `-Doptifabric.mc-jar=<path>`; unpack the remapped OptiFine classes with `-Doptifabric.extract=true`.
- **Models/items/textures disappearing wholesale** (log full of `Unable to bake … model`): a Fabric mixin failed to transform that class — the outermost message usually hides the real cause.
- **`NoClassDefFoundError: Could not initialize class net.optifine.reflect.Reflector`** (or the game dying during startup with no crash report): another mod's mixin failed to apply to a class OptiFine patches, and OptiFine's crash reporter trips over the result. Add `-Dmixin.debug=true` to the JVM arguments and run again — Mixin only names the failing mod in debug mode (`Mixin apply for mod <mod> failed … -> net.minecraft.class_<n>`). **The fix** is to remove that mod or run without OptiFine; it is a conflict between OptiFine's class patches and that mod's injection, and it cannot be fixed from OptiFabric's side. Known cases on 1.21.1: `CarryOn` 2.2.6.13 and `ShoulderSurfing` 5.2.0 cannot be fixed from here, while `SophisticatedCore`'s `ParticleEngineMixin` is fixed in this build. `EntityCulling` was suspected but cleared.
- **Stuck on the loading screen:** take two thread dumps (`jstack <pid>`, ~15 s apart) and compare. Identical stacks with flat CPU means a real stall; a stack sitting in `glfwSwapBuffers` is a presentation problem — and do not minimise the window while loading (with vsync on, the render thread blocks there).

Log lines that are normal and can be ignored:

| Log | Meaning |
|---|---|
| `[OptiFine] (Reflector) Class not present: net.minecraftforge.*` / `sun.misc.SharedSecrets` | OptiFine probing for Forge and old JDKs; Fabric has neither |
| `Failed to locate initialiser injection point in <init>(class_2591,…)` | the price of applying OptiFine's `BlockEntity` patch (skipping it would leave five dangling references) |
| `[OptiFabric] Resource not found: minecraft:shaders/post/fxaa_of_{2,4}x.json` | OptiFine still probes its pre-1.21.6 chain location once; the chain in use lives in `post_effect/` |
| `[Shaders] Unknown macro value: IRIS_VERSION` / `ANGELICA_VERSION` | the shader pack probing for Iris/Angelica |
| `[Shaders] Invalid macro expression` / `ParseException: Model variable not found: …` | shader-pack vs OptiFine version mismatch |
| `Skipping bad option: lastServer` | a leftover field in the options file |

### Known issue: Litematica schematics + a shader pack floods the log with OpenGL 1282

With a shader pack enabled and a Litematica schematic being rendered, OptiFine logs thousands of `[Shaders] OpenGL error: 1282 (Invalid operation), program: gbuffers_terrain, at: pre-useProgram`. The failing call is **Litematica's own** (`WorldRendererSchematic.renderBlockLayer` uploads the vanilla `ShaderProgram.chunkOffset` uniform, which is invalid while OptiFine's program is bound); OptiFine only **reports** it, because `Shaders.useProgram` starts with `checkGLError("pre-useProgram")`. Workarounds: disable the shader pack, or stop rendering the schematic. Nothing in OptiFabric can fix it.

## 🔐 License

**MPL-2.0** — see [`LICENSE.txt`](LICENSE.txt). The core mechanism is a port of [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric); ported files keep their origin headers. OptiFine itself is **not** included or redistributed — it is sp614x's work, get it from [optifine.net](https://optifine.net/).

## 🌟 Credits

- **[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)** by Modmuss50 and Chocohead — the original project this port is built on
- **sp614x** for OptiFine itself
- The **Fabric** team for Loader, Loom and tiny-remapper

---

**Note:** this is a community port. It is not affiliated with, endorsed by or supported by the OptiFine, Fabric or Mojang teams.

## Attribution, licence and AI involvement

This is a port of [OptiFabric](https://github.com/Chocohead/OptiFabric) by Modmuss50 and Chocohead, and it keeps
their licence: **MPL-2.0** (`LICENSE.txt`). MPL-2.0 asks that the source of modified files stay available, which
is why this repository is public and carries the full history: everything this port changed is in it.

The porting work - reading OptiFine's patched bytecode, writing the fixers, and the release plumbing - is
**written and verified with AI assistance**, and every README and release note says so at the top (see also
FAQ 17). What that means in practice is a rule about evidence rather than about authorship: every number in
these documents comes from a log or a report captured in this repository, and anything not measured is written
as **not measured** instead of being estimated. The reviewer's checklist in `CONTRIBUTING.md` is built the same
way.

OptiFine itself is **not bundled, patched into a download, or redistributed** by any build here: the mod reads
the OptiFine jar you put in `mods/`, or `-full` variants download it from optifine.net at your request. The
third-party projects this repository talks to (Fabric API, Sodium, Lithium, C2ME, ...) are never included
either - compatibility with them is described in `COMPATIBILITY.md`, not shipped inside the jar.
