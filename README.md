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

Load **OptiFine** under **Fabric Loader**. Put OptiFine's jar next to this mod and it patches the vanilla client with OptiFine's own patcher, rebuilds the lambdas whose targets moved, remaps OptiFine from its obfuscated names into Fabric's namespace, and hands the patched Minecraft classes to Fabric Loader's class transformer — so both can live in one client. **OptiFine itself is not bundled or redistributed.**

This branch is the **1.21.x line** and covers Minecraft **1.21 – 1.21.11** (all ten releases OptiFine ever shipped a build for). The 26.x line (Minecraft 26.2 and 26.1.2) lives on its own branch/worktree and is developed separately; jars from the two lines are **not interchangeable**.
完整兼容列表(MC 1.21.1)见 [`COMPATIBILITY.md`](COMPATIBILITY.md)。The full MC 1.21.1 compatibility list is in [`COMPATIBILITY.md`](COMPATIBILITY.md).

## 📖 Overview

OptiFine is not a Fabric mod: its jar holds bytecode patches against *obfuscated* vanilla client classes plus its own classes. OptiFabric drives OptiFine's patcher at `preLaunch`, de-obfuscates the result, remaps it into the runtime namespace, repairs what OptiFine's recompiler left behind, and registers the patched classes with Loader before Mixin ever sees them.

**One jar per Minecraft release** — every jar carries that release's `official → intermediary` mapping table (the obfuscated names differ per release, and the wrong table turns OptiFine into garbage) and pins its `minecraft` dependency to that exact version.

**Author:** kynarain · upstream: Modmuss50, Chocohead
**Version:** `2.2.1` for all ten releases (1.21 – 1.21.11)
**License:** MPL-2.0

## ✨ Key Features

- 🔄 **No OptiFine installer run by hand** — drop the installer jar (with its `patch/` diffs) or an already-extracted OptiFine into `mods/`; the patching happens at startup
- 🧩 **Remapping that sees the game** — the game jar goes into the remapper's classpath *and* inputs, so methods overridden in subclasses keep their mapped names (one class alone lost 35 methods otherwise)
- 📦 **One source tree, one jar per release** — 1.21 through 1.21.11, each bound to its own mapping table, built with `-Pmc=<version>`
- 🎨 **Anti-aliasing that works** — since 1.1.2 optifabric leaves OptiFine's own `post_effect/` chain alone and rewrites the FXAA vertex shader on the releases whose post pipeline has no vertex attributes (1.21.9 / 1.21.10)
- 🛠️ **Bytecode repairs** — dozens of fixers for what OptiFine's recompiler erases: vanilla method bodies, injection points, synthetic fields, object-creation points, renamed lambdas, region construction
- 🧪 **Offline verification as a first-class tool** — every patched class and every OptiFine class is loaded in a single loader and checked with the JVM verifier plus an ASM data-flow verifier, on top of five scanners
- ⚙️ **Caching** — the whole pipeline result is cached under `.optifine/<OptiFine version>/`; later launches take 1–2 seconds
- 🧯 **Honest failure** — a missing, corrupt, duplicated or mismatched OptiFine jar produces an error dialog at the title screen and an `OptiFabric` section in the crash report

## 🏗️ How It Works

```
mods/OptiFine_1.21.11_HD_U_J9.jar
        │  ① OptiFine's own optifine.Patcher patches the obfuscated client jar
        │     (since 1.21.6 the patches travel as xdelta diffs; the usage is unchanged)
        ▼
   patched vanilla jar   (OptiFine's patches + OptiFine's classes)
        │  ② LambdaRebuilder: lambdas in patched classes point at methods that moved
        │  ③ tiny-remapper: official (obfuscated) → intermediary
        │     **the game jar must be in the remapper's classpath**, or overrides in
        │     subclasses keep OptiFine's names
        ▼
   Optifine-mapped.jar
        │  ④ split in two
        ├── non-Minecraft classes (OptiFine's own classes + resources) ──► game class path
        └── patched net/minecraft/** classes ──────────────────────────► ClassCache
```

Replacement happens through **Fabric Loader's own GameTransformer**: when a Minecraft class is about to be loaded, Loader asks the game provider's `GameTransformer.transform(...)` for ready-made bytecode — *before* Mixin runs. The classes registered at `preLaunch` (after the `patcher/fixes` corrections) therefore win, while classes Loader patched itself keep Loader's version.

That is why no stub mixin has to be generated per patched class and no Mixin extension API is needed: what is handed over is Mixin's **input**, not its output, so other mods' mixins against those classes keep working.

There is one hard constraint: **before the patched classes are handed to Loader, nothing may reflect on game classes** — a single `Class.getMethods()` loads every type in those method signatures and pins the class to vanilla forever. This code only ever touches bytes (`getClassByteArray` / ASM), never a `Class` object.

| Component | Purpose |
|---|---|
| `OptifabricRuntime` | whole pipeline: find the jar → patch → remap → repair → register |
| `GameTransformerHook` | injects the patched classes into Loader's game transformer |
| `OptifineMappings` | rule-based contextual mapping (replaces upstream's hand-written table) |
| `OptifineJarFixer` | repairs OptiFine's own jar: post-effect JSON shape, the shaderpack load a 1.21.6/1.21.7 build cancels, the FXAA vertex shader of 1.21.9/1.21.10 |
| `patcher/fixes/**` | the per-release bytecode fixers (vanilla bodies, injection points, synthetic fields, …) |
| `RendererApiFallback` | registers an inert placeholder where Fabric's renderer API would otherwise be empty |

Intermediate files live in `<game dir>/.optifine/<OptiFine version>/`:

| File | Content |
|---|---|
| `cache-format.txt` | cache format version (currently `26`); a mismatch rebuilds everything |
| `Optifine-mapped.jar` | the remapped OptiFine jar (without MC classes) that goes on the class path |
| `Optifine.classes.gz` | the patched MC classes for the next launch |

## 📦 Installation

1. Get the OptiFine build for **exactly** your Minecraft version (see the table below) — OptiFabric reads `MC_VERSION` from `optifine/Config` and refuses to start otherwise. Do **not** run OptiFine's installer.
2. Put the jar for **your** version **and** OptiFine's jar into that Fabric instance's `mods/` folder. Do not install two OptiFine jars (the game reports `DUPLICATED`), do not use the wrong version of either, and do not mix in the 26.x line's jar. **When upgrading from 1.x, delete the old `OptiFabric-<version>+mc1.21.x.jar` first** — 2.0.0 renamed the mod id to `optifabric_reforged` (display name *OptiFabric Reforged*), and with both ids in `mods/` Fabric loads both copies, so OptiFine gets patched twice.

<!-- launcher-independent install: the launcher may only offer OptiFine OR Fabric, so OptiFabric cannot
     assume the launcher installs OptiFine for the user. Kept identical on both build variants. -->
> **OptiFabric cannot assume your launcher installs OptiFine for you**, because many launchers only offer
> **OptiFine _or_ Fabric** as the profile, never both: install **Fabric + OptiFabric** first; download
> OptiFine from <https://optifine.net/downloads>; put that jar in the `mods` folder (or paste its path into
> the box on this screen and press **Install from file**); then start the game once by hand.
>
> **OptiFabric 不能假设启动器会替你装 OptiFine**,因为很多启动器只能选 **OptiFine _or_ Fabric**,不能两个都要:
> 先装好 **Fabric + OptiFabric**;到 <https://optifine.net/downloads> 下载 OptiFine;把那个 jar 放进 `mods`
> 文件夹(或把它的路径粘进屏幕上的输入框,点「从本地文件安装」);然后手动启动一次游戏。

3. Launch the **Fabric** profile — not a launcher-made `1.21.x-OptiFine_xxx` profile, which injects OptiFine itself and collides with this mod.
4. The first start spends a few extra seconds patching and remapping (5–7 s in practice); later starts use the cache (1–2 s). A title screen showing OptiFine's version and OptiFine entries in video settings mean it worked.

With version isolation enabled (PCL2 / HMCL), the game directory and `mods/` both live under `versions/<name>/`, and the `.optifine/` cache is created there too.

```powershell
# OptiFine 1.21.11, the newest final build for it: use the "Download OptiFine" button in the prompt,
# or open https://optifine.net/downloads and download OptiFine_1.21.11_HD_U_J9.jar from there.
```

## 🔨 Building from Source

**JDK 21+** is required, and the repository root *is* the Gradle project — the target release comes from `-Pmc` (or from `gradle.properties` when omitted):

```bash
./gradlew build "-Pmc=1.21.11"                            # quotes matter in PowerShell: 1.21.11 is split otherwise
./gradlew build "-Pmc=1.21.8" "-Pmod_version_base=2.1.0"  # only needed when that jar's version differs from the base
```

The jars land in `build/libs/OptiFabric-<version>+mc<mc>.jar`. Version numbers only ever change through one script:

```powershell
.\release\version.ps1                                     # show the current version of the line
.\release\version.ps1 -Mc 1.21.8 -Kind patch               # bump one jar, and only that jar
```

The development environment is not supported: `gradlew runClient` is refused outright, because dev runs in the `named` namespace and would need an extra contextual-mapping layer.

## 📋 Requirements

| | |
|---|---|
| Minecraft | 1.21, 1.21.1, 1.21.3, 1.21.4, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, 1.21.11 |
| Fabric Loader | ≥ 0.19.5 |
| Java | 21+ (tested on 25) |
| Side | client |
| OptiFine | your own build, **exact version match** (table below) |
| Fabric API | optional — tested with the release matching your Minecraft version |

## 🤝 Compatibility

| Minecraft | jar | OptiFine build | State |
|---|---|---|---|
| 1.21 | `OptiFabric-2.2.8+mc1.21.jar` | `preview_OptiFine_1.21_HD_U_J1_pre9.jar` | ✅ verified |
| 1.21.1 | `OptiFabric-2.2.8+mc1.21.1.jar` | `OptiFine_1.21.1_HD_U_J1.jar` | ✅ verified |
| 1.21.3 | `OptiFabric-2.2.8+mc1.21.3.jar` | `OptiFine_1.21.3_HD_U_J2.jar` | ✅ verified |
| 1.21.4 | `OptiFabric-2.2.8+mc1.21.4.jar` | `OptiFine_1.21.4_HD_U_J3.jar` | ✅ verified |
| 1.21.6 | `OptiFabric-2.2.8+mc1.21.6.jar` | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` | ⚠️ starts and plays, **crashes as soon as shaders are enabled** (below) |
| 1.21.7 | `OptiFabric-2.2.8+mc1.21.7.jar` | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` | ⚠️ same |
| 1.21.8 | `OptiFabric-2.2.8+mc1.21.8.jar` | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` | ✅ verified |
| 1.21.9 | `OptiFabric-2.2.8+mc1.21.9.jar` | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` | ✅ verified |
| 1.21.10 | `OptiFabric-2.2.8+mc1.21.10.jar` | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` | ✅ verified |
| 1.21.11 | `OptiFabric-2.2.8+mc1.21.11.jar` | `OptiFine_1.21.11_HD_U_J9.jar` | ✅ verified |

OptiFine never shipped a build for **1.21.2 / 1.21.5**, so there is no jar for those.

The build in this table is the newest **final** OptiFine release for that Minecraft version; when a version has no
final release yet, the newest **preview** is used instead (that is why 1.21.4 lists J3 rather than the newer
J4_pre2 preview). Any other build of the same Minecraft version still works - the in-game prompt only appears
when the jar in mods/ is a **preview** older than the one listed here - a final build you already have is left alone, even when a newer final exists.

### OptiFabric version → Minecraft version → required OptiFine build

This is the same list the mod itself carries (and `release\notes\mc<MC>.md` states per release); `release\version.ps1 -CheckSupport` fails when the three disagree.

| OptiFabric version | Minecraft version | Required OptiFine build |
|---|---|---|
| `2.2.8+mc1.21` | 1.21 | `preview_OptiFine_1.21_HD_U_J1_pre9.jar` |
| `2.2.8+mc1.21.1` | 1.21.1 | `OptiFine_1.21.1_HD_U_J1.jar` |
| `2.2.8+mc1.21.3` | 1.21.3 | `OptiFine_1.21.3_HD_U_J2.jar` |
| `2.2.8+mc1.21.4` | 1.21.4 | `OptiFine_1.21.4_HD_U_J3.jar` |
| `2.2.8+mc1.21.6` | 1.21.6 | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` |
| `2.2.8+mc1.21.7` | 1.21.7 | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` |
| `2.2.8+mc1.21.8` | 1.21.8 | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` |
| `2.2.8+mc1.21.9` | 1.21.9 | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` |
| `2.2.8+mc1.21.10` | 1.21.10 | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` |
| `2.2.8+mc1.21.11` | 1.21.11 | `OptiFine_1.21.11_HD_U_J9.jar` |

When OptiFabric loads and that jar is missing, or is not the build its Minecraft version expects, the game says so on a screen instead of starting silently: it names the file, offers a `Download OptiFine` button that fetches it from OptiFine's **official** site (`optifine.net`, the only source this mod ever uses), and opens the mods folder for you — the URL field above the buttons can be replaced with a source of your own, which is then the only place the jar is fetched from.

### The 1.21.6 / 1.21.7 shader limitation

Both releases **start and play normally** without shaders (the title screen renders, worlds load, the integrated server, chunk building and saving all work), but **enabling a shader pack crashes the game** during startup:

```
java.lang.NullPointerException: Cannot read field "norm" because "multiTex" is null
  at net.optifine.shaders.ShadersTex.initDynamicTextureNS(ShadersTex.java:322)
  at net.minecraft.class_1043.method_71142 -> class_1043.<init> -> class_310.<init>
```

- It is **not** the shader pack: three unrelated packs (Complementary Reimagined, Sildur's Vibrant Shaders, BSL) crash at the same frame, and stripping a pack's custom textures and animation metadata does not help.
- It is not our patching either: the crash happens while the first textures are created, earlier than any pack-specific logic — the trigger is simply "shaders enabled".
- The cause is **OptiFine's own preview builds for those two releases**: the call they inject into `class_1043.<init>` lacks the `setParentTexture` association that the 1.21.8 build performs first, while the `initDynamicTextureNS` it calls dereferences `getMultiTexID()` right away. All seven OptiFine builds available for the two releases crash identically, so downgrading to an earlier preview does not avoid it.

| | |
|---|---|
| ✅ Works | OptiFine's video settings, zoom, connected textures, dynamic lights, **shaders** (on every release except 1.21.6 / 1.21.7), and since 1.1.2 **anti-aliasing** |
| ⚠️ Neutralised | the `BEFORE_BLOCK_OUTLINE` event no longer fires (the block outline is still drawn); the moving-block FRAPI render hook is inert (moving blocks are drawn by the vanilla path) |
| ❌ Incompatible | **Sodium** (declared `conflicts` and `breaks`), plus `no_fog`, `thallium`, `xradiation`, `ryoamiclights` (declared `breaks`). RyoamicLights fails because OptiFine replaces the whole video-settings screen — parent class included — and its mixin targets the vanilla parent; OptiFine has its own dynamic lights, so nothing is lost. The full list, where it comes from and what the loader does with it: [Declared incompatibilities](#declared-incompatibilities-and-what-the-loader-actually-does) |
| 📄 OptiFine-side limits | OptiFine cannot see resources inside Fabric mods (`[OptiFine] Unknown resource pack type: …ModNioResourcePack`); shader packs print their own `[Shaders]` errors when they do not match your OptiFine build |

### Declared incompatibilities, and what the loader actually does

Everything this mod declares against lives in `fabric.mod.json` and nowhere else — no screen shows it — so this is that list in prose. The `2.2.3` artifact declares:

| Declaration | Entries |
|---|---|
| `conflicts` | `sodium` (`*`) |
| `breaks` | `no_fog`, `thallium`, `xradiation`, `ryoamiclights`, `sodium` (`*`) |

The `sodium` conflict and three of those five `breaks` are **inherited from upstream**: [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric) (`fabric.mod.json` on its default branch `llama`, v1.14.3) declares the same `sodium` conflict and `breaks` on `no_fog`, `thallium` and `xradiation`, and adds three range-limited entries this fork deliberately dropped:

| Upstream entry | Why it is not carried here |
|---|---|
| `cardinal-components-item <2.4.2` | a 1.16/1.17-era version range; those mod builds are the only ones inside it |
| `architectury >1.2.72 <1.3.77` | a 1.16/1.17-era range, and the underlying conflict is *fixed* on 1.21.1 (below) |
| `meteor-client >=0.4.1` | a 1.16/1.17-era entry |

`ryoamiclights` is this fork's own addition, for the reason in the table above. Putting `sodium` in `breaks` as well is this fork's own addition: upstream declares it only under `conflicts`, and `breaks` is the field launchers and platforms that do not read `conflicts` look at.

**The two fields are not the same thing: `conflicts` only warns, `breaks` is enforced.** Sodium is declared in **both**. Measured on **Fabric Loader 0.19.5**: a `conflicts` entry adds no constraint to the loader's dependency solver at all — in `ModSolver` the `CONFLICTS` case is still a `// TODO: soft negative dep?` — so the game starts with `Warnings were found!` and then a normal `Loading 56 mods:`. A `breaks` entry against a mod that **is** present is a different animal: the solver emits `NEG_HARD_DEP` and the loader **refuses the combination** rather than starting. A run recorded here logged `NEG_HARD_DEP optifabric_reforged 2.2.2 {breaks sodium}`. So putting `sodium` in `breaks` is a gate, not a declaration — the loader will not start with sodium present. Read the list as "the author already knows this pairing breaks"; the `breaks` half of it is actively enforced, the `conflicts` half is not.

**The sodium pairing is declared on one side only.** Sodium declares it under `breaks` only — `conflicts` is absent from every sodium build measured (`sodium-fabric` `0.5.11+mc1.21`, `0.6.13+mc1.21.1` and `0.8.13+mc1.21.1` all carry `"breaks": {"optifabric": "*"}`) — and it names the **old** mod id `optifabric`. Since 2.0.0 this line ships as `optifabric_reforged` (display name *OptiFabric Reforged*), so that entry cannot fire at all, and the warning you see for sodium is the one from this mod's side. Adding sodium to our own `breaks` restores a declaration the rename broke: on this line it is the only sodium declaration a loader will act on.

**Architectury is the reverse story, and worth keeping.** Upstream declared architectury broken, and architectury's own metadata declares `breaks: optifabric <1.13.0`. On 1.21.1 this fork fixed the real conflict behind that declaration: OptiFine inserts its own locals into the middle of `GameRenderer.render`, which shifts the vanilla slots that Mixin's `LocalCapture` hands to a handler, so `LocalSlotLayoutFix` moves the extra slots to the end of the local variable range. `architectury-api` `13.0.11` now passes the sweep (row #14 of the matrix), and the rename to `optifabric_reforged` is what stops architectury's own declaration from firing. The measurements are in [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) and [`docs/RELEASE_NOTES.md`](docs/RELEASE_NOTES.md).

The empirical picture — which mods actually fail next to OptiFabric, and which of those failures are ours — is in [`docs/COMPATIBILITY.md`](docs/COMPATIBILITY.md); the full per-mod list is in [`COMPATIBILITY.md`](COMPATIBILITY.md) at the repository root.

### How indigo is handled

Fabric API's `fabric-renderer-indigo` and OptiFine cannot both render terrain, so this mod makes indigo step aside with Fabric's own mechanism: `"custom": {"fabric-renderer-api-v1:contains_renderer": true}` in `fabric.mod.json` (the same key Sodium uses — OptiFine *is* a terrain renderer). Indigo then logs `[Indigo] Different rendering plugin detected; not applying Indigo.`

Declaring the key is not enough on its own: Fabric's renderer modules look at a *registry*, and an empty one throws `Attempted to retrieve active rendering plug-in before one was registered`, so the mod also registers an inert placeholder renderer (F3 shows `Renderer: OptifineRendererPlaceholder`). Wanting indigo back means deleting that `custom` key and rebuilding — which then crashes `ChunkBuilder$BuiltChunk$RebuildTask` on load, because the injection point it needs is gone.

## 📊 Verified State

Every release is checked by a single command, and the numbers below were produced for `1.1.2` (one run per Minecraft version):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-version.ps1 -Version 1.21.10 -ModVersion 1.1.2
```

| Minecraft | Patched game classes (JVM) | OptiFine classes (JVM) | ASM verifier | `@At` / refmap / contracts / handles | Live test |
|---|---|---|---|---|---|
| 1.21.3 | 440 / 440 | 816 / 816 | 0 problems | 2 / 0 / 0 / 0 | anti-aliasing + shaders, no errors |
| 1.21.4 | 474 / 474 | 812 / 812 | 0 problems | 2 / 0 / 0 / 0 | no errors |
| 1.21.6 | 487 / 487 | 820 / 820 | 0 problems | 4 / 0 / 0 / 0 | starts and plays; shaders crash (OptiFine's own build) |
| 1.21.7 | 500 / 500 | 823 / 823 | 0 problems | 4 / 0 / 0 / 0 | same |
| 1.21.8 | 516 / 516 | 831 / 831 | 0 problems | 4 / 0 / 0 / 0 | no errors, anti-aliasing checked by eye |
| 1.21.9 | 519 / 519 | 832 / 832 | 0 problems | 4 / 0 / 0 / 0 | no errors |
| 1.21.10 | 553 / 553 | 836 / 836 | 0 problems | 4 / 0 / 0 / 0 | no errors, no black screen |
| 1.21.11 | 570 / 570 | 874 / 874 | 0 problems | 4 / 0 / 0 / 0 | no errors, anti-aliasing checked by eye |

The remaining `@At` misses are all **deliberately disabled** Indigo injections, and the class counts are taken in a single loader, the same way the game loads them.

**Known gaps:**

1. **No development-environment support** — dev runs in the `named` namespace and would need a two-stage remapping plus upstream's contextual-mapping fixes
2. **The two neutralised hooks are placeholders**, not working implementations (`Renderer.get()` returns an inert renderer)
3. **1.21.6 / 1.21.7 shaders** — waiting either for a new OptiFine build or for wiring up the already-written `GpuTextureLinkFix`

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
├── docs/                            # DEVELOPMENT.md, COMPATIBILITY.md (+ _CN), compatibility/ (the 1.21.1 sweep), …
├── release/                         # version.ps1, publish.ps1, notes/, MANUAL_RELEASE.md
├── build.gradle · gradle.properties · settings.gradle
└── gradlew · gradlew.bat
```

## 🔐 License

**MPL-2.0** — see [`LICENSE.txt`](LICENSE.txt). The core mechanism is a port of [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric); ported files keep their origin headers. OptiFine itself is **not** included or redistributed — it is sp614x's work, get it from [optifine.net](https://optifine.net/).

## 🙋 Support

- **Nothing changed after upgrading?** Delete `<game dir>/.optifine/` — the cache holds patched bytecode, and the cache format (`26`) rebuilds it automatically otherwise.
- `[OptiFabric]`'s output goes to the **launcher console**, not to `logs/latest.log`; filtering for `[OptiFabric]` shows how many classes were prepared and how many Loader took over.
- The title screen shows an error dialog for a missing/corrupt/duplicated/mismatched OptiFine jar, and the crash report gains an `OptiFabric` section (OptiFine version, jar state, mapped-jar path).
- Point the mod at a specific vanilla jar with `-Doptifabric.mc-jar=<path>`; unpack the remapped OptiFine classes with `-Doptifabric.extract=true`.
- **Models/items/textures disappearing wholesale** (log full of `Unable to bake … model`): a Fabric mixin failed to transform that class (the outermost message usually hides the real cause). OptiFine loves turning vanilla methods into thin wrappers that forward to its own overloads, which moves injection points with them.
- **Stuck on the loading screen:** take two thread dumps (`jstack <pid>`, ~15 s apart) and compare. Identical stacks with flat CPU means a real stall; a stack sitting in a native call (`glfwSwapBuffers`) is a presentation problem — and do not minimise the window while loading (with vsync on, the render thread blocks in `SwapBuffers`).
- **`NoClassDefFoundError: Could not initialize class net.optifine.reflect.Reflector`** (or the game dying during startup with no crash report at all): OptiFine's crash reporter reads its own version through `Reflector`, that read loads a game class, and the class can no longer finish loading because another mod's mixin failed to apply to a class OptiFine patches. `logs/latest.log` names the class but never the mod.
- **To find the mod:** add `-Dmixin.debug=true` to the JVM arguments (launcher settings → Java/JVM arguments, or the instance's advanced settings) and run again — Mixin only prints the failing mod in debug mode. The log then shows `Mixin apply for mod <mod> failed … -> net.minecraft.class_<n>`, usually followed by the exact injection error.
- **The fix** is to remove the mod Mixin names, or to run without OptiFine. This is a conflict between OptiFine's own class patches and that mod's injection, and three families are known: mods that capture or modify a method's local variables or arguments inside a class OptiFine rewrites, mods that expect an exact number of call sites in such a method, and — not fixable from OptiFabric's side — mods that inject *at a call site* OptiFine replaced with one of its own (it swaps vanilla `ParticleManager.method_3049` calls for `ParticleManager.render`, for example), which leaves the injection with zero points and needs the mod author to relax or retarget it. Nothing in `latest.log` without the flag points at the culprit, so do not guess from the stack trace — it ends at OptiFine's `Reflector`. Known cases on 1.21.1: `CarryOn` 2.2.6.13 (injects at a `ParticleManager` call site OptiFine replaced) and `ShoulderSurfing` 5.2.0 (expects an exact call-site count in `Camera`) cannot be fixed from here, while `SophisticatedCore`'s `ParticleEngineMixin` is fixed in this build (its target method was folded into a lambda). `EntityCulling` was suspected but cleared — removing it changed nothing.

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

With a shader pack enabled and a Litematica schematic being rendered, OptiFine logs thousands of:

```
[Shaders] OpenGL error: 1282 (Invalid operation), program: gbuffers_terrain, at: pre-useProgram
```

What was actually shown:

- The failing call is **Litematica's own** — `WorldRendererSchematic.renderBlockLayer` uploads the vanilla
  `ShaderProgram.chunkOffset` uniform, which is invalid while OptiFine's own program is bound.
- OptiFine only **reports** it: `Shaders.useProgram` starts with `checkGLError("pre-useProgram")`, so it attributes the
  pending GL error to the program it is about to bind.
- `litematica-printer`, Xaero's mods and OptiLithium were each checked and shown **not** to be involved.

Workarounds: disable the shader pack, or stop rendering the schematic. Nothing in OptiFabric can fix it — the invalid
call is not made by this mod, and OptiFine's error check is its own.

## 🌟 Credits

- **[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)** by Modmuss50 and Chocohead — the original project this port is built on
- **sp614x** for OptiFine itself
- The **Fabric** team for Loader, Loom and tiny-remapper

---

**Note:** this is a community port. It is not affiliated with, endorsed by or supported by the OptiFine, Fabric or Mojang teams.
