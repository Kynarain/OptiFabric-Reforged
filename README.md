# OptiFabric

<p align="center">
  <img src="common/src/main/resources/assets/optifabric/icon.png" alt="OptiFabric" width="128"/>
</p>

[🇨🇳 中文版](./README_CN.md) | 🇬🇧 English

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21%20~%201.21.11%20%7C%2026.1.2-green.svg)](https://www.minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-%E2%89%A5%200.19.5-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21%20%2F%2025-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MPL--2.0-lightgrey.svg)](LICENSE.txt)

> ⚠️ This port was written and verified with AI assistance (DeepSeek). Be careful with it in production.

Run **OptiFine** and **Fabric** in the same client. Drop this mod next to your own OptiFine jar: optifabric patches the client, remaps OptiFine into Fabric's namespace, repairs the structural conflicts between OptiFine and Fabric API, and hands the result to Fabric Loader's class transformer at startup. **OptiFine is never bundled or redistributed.**

## 📖 Overview

OptiFine is not a Fabric mod — its jar holds bytecode patches against *obfuscated* Minecraft classes plus its own classes. OptiFabric drives OptiFine's own patcher at `preLaunch`, de-obfuscates it, remaps the result into the runtime namespace, applies a stack of bytecode repairs, and registers the patched classes with Loader. OptiFine's settings, shaders, connected textures and zoom then work on a Fabric client, and other mods' mixins still apply — the classes handed over are Mixin's *input*, not its output.

**Author:** kynarain (1.21.11 port) · upstream: Modmuss50, Chocohead
**Version:** `1.1.2` on the 1.21.x line · `2.0.0` (OptiFabric Reforged) on the 26.x line
**License:** MPL-2.0

**Two independent lines, their jars are not interchangeable:**

| Line | Minecraft | Mod id / artifact | Java |
|---|---|---|---|
| **1.21.x** | 1.21 ~ 1.21.11 | `optifabric` / `OptiFabric-<version>+mc<mc>.jar` | 21+ |
| **26.x** | 26.1.2 | `optifabric_reforged` / `OptiFabric-Reforged-2.0.0+mc26.1.2.jar` | **25** |

Minecraft 26.1 and newer ship **unobfuscated** — there is no yarn and no real intermediary to remap through — so that line takes a different build and a different runtime path. It also carries its own mod id, because some mods declare `"breaks": {"optifabric": "*"}` and Fabric Loader matches that by **id** ([`docs/PORT_26.x.md`](docs/PORT_26.x.md)).

## ✨ Key Features

- 🔄 **No OptiFine installer run by hand** — drop the installer jar or an already-extracted OptiFine into `mods/`; the patching happens at startup
- 🧩 **Remapping that sees the game** — the game jar goes into the remapper's classpath *and* inputs, so methods overridden in subclasses keep their mapped names (one class alone lost 35 methods otherwise)
- 🛠️ **Bytecode repairs** — dozens of fixers for what OptiFine's recompiler erases: vanilla method bodies, injection points, synthetic fields, object-creation points, renamed lambdas, region construction
- 🎨 **Anti-aliasing that works** — optifabric leaves OptiFine's own `post_effect/` chain alone and rewrites the FXAA vertex shader on the releases whose post pipeline has no vertex attributes (1.21.9 / 1.21.10)
- 📦 **One source tree, one jar per Minecraft version** — 1.21 through 1.21.11, each bound to that release's `official → intermediary` mapping table
- 🧪 **Offline verification as a first-class tool** — every patched class and every OptiFine class is loaded in a single loader and checked with the JVM verifier plus an ASM data-flow verifier, on top of five scanners
- ⚙️ **Caching** — the whole pipeline result is cached under `.optifine/<OptiFine version>/`; later launches take 1–2 seconds
- 🧯 **Honest failure** — a missing, corrupt, duplicated or mismatched OptiFine jar produces an error dialog at the title screen and an `OptiFabric` section in the crash report

## 🏗️ How It Works

```
mods/OptiFine_1.21.11_HD_U_J9.jar
        │  ① OptiFine's own optifine.Patcher patches the (obfuscated) client jar
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
        ├── non-Minecraft classes (OptiFine's own classes + resources) ──► class path
        └── patched net/minecraft/** classes ─────────────────────────► ClassCache
```

> **The 26.x line has no step ③**: Minecraft 26.1+ is unobfuscated, so the runtime namespace is `official` and no mapping table ships inside the jar.

Replacement happens through **Fabric Loader's own GameTransformer**: `KnotClassDelegate.getPreMixinClassByteArray` asks the game provider's `GameTransformer.transform(...)` for ready-made bytecode *before* Mixin runs, so the patched classes registered at `preLaunch` simply win.

| Component | Purpose |
|---|---|
| `OptifabricRuntime` | whole pipeline: find the jar → patch → remap → repair → register |
| `GameTransformerHook` | injects the patched classes into Loader's game transformer |
| `OptifineMappings` | rule-based contextual mapping (replaces upstream's hand-written table) |
| `OptifineJarFixer` | repairs OptiFine's own jar: post-effect JSON shape, the shaderpack load a 1.21.6/1.21.7 build cancels, the FXAA vertex shader of 1.21.9/1.21.10 |
| `patcher/fixes/**` | the per-release bytecode fixers (vanilla bodies, injection points, synthetic fields, …) |
| `RendererApiFallback` | registers an inert placeholder where Fabric's renderer API would otherwise be empty |

## 📦 Installation

1. Install a **Fabric** client for your Minecraft version (Fabric Loader **≥ 0.19.5**; Java 21+, or **Java 25** for 26.1.2)
2. Download the OptiFine build that matches that exact Minecraft version (table below) — do **not** run its installer
3. Put the OptiFabric jar **and** the OptiFine jar into that instance's `mods/` folder
4. Launch the Fabric profile — the first start spends a few seconds patching and remapping (watch for `[OptiFabric]` in the launcher console), later starts use the cache

**That's it.** A title screen showing OptiFine's version and OptiFine entries in video settings mean it worked.

```powershell
# OptiFine 1.21.11 from the China mirror (302 → official maven), for example
curl.exe -L -o OptiFine_1.21.11_HD_U_J9.jar "https://bmclapi2.bangbang93.com/optifine/1.21.11/HD_U/J9"
```

Do **not** install two OptiFine jars (the game reports `DUPLICATED`), do not mix versions of either jar, and do not launch through a launcher-made `1.21.x-OptiFine_xxx` profile — that injects OptiFine itself and collides with this mod. With version isolation enabled (PCL2/HMCL), the game directory is `versions/<name>/` and the `.optifine/` cache lives there too.

## 🔨 Building from Source

Prerequisites: **Git** and a JDK (21 for the 1.21.x line, 25 for the 26.x line). Gradle comes from the wrapper; the root directory is not a Gradle project, so builds need `-p`.

```bash
git clone https://github.com/Kynarain/OptiFabric-Reforged.git
cd OptiFabric-Reforged

# 1.21.x line — one jar per Minecraft version
./gradlew -p v1.21.x build "-Pmc=1.21.8"                        # 1.21 / 1.21.1 are version 1.1.0
./gradlew -p v1.21.x build "-Pmc=1.21.8" "-Pmod_version_base=1.1.2"   # 1.21.3 – 1.21.11 are 1.1.2
./gradlew -p v1.21.x build "-Pmc=1.21.11"                       # default version from gradle.properties

# 26.x line — the target version is the one in v26.x/gradle.properties (no -Pmc)
./gradlew -p v26.x build
```

The jars land in `v1.21.x/build/libs/` and `v26.x/build/libs/`. `gradlew runClient` is deliberately refused: dev runs in the `named` namespace and would need a second remapping stage.

Version numbers only ever change through one script — see [`docs/VERSIONING.md`](docs/VERSIONING.md):

```powershell
.\release\version.ps1                                   # show the current version of both lines
.\release\version.ps1 -Line 1.21.x -Mc 1.21.8 -Kind patch   # bump one jar, and only that jar
```

## 📋 Requirements

| | 1.21.x line | 26.x line |
|---|---|---|
| Minecraft | 1.21, 1.21.1, 1.21.3, 1.21.4, 1.21.6 – 1.21.11 | 26.1.2 |
| Fabric Loader | ≥ 0.19.5 | ≥ 0.19.5 |
| Java | 21+ (tested on 25) | **25** (the game's own requirement) |
| Side | client | client |
| OptiFine | your own build, exact version match | `preview_OptiFine_26.1.2_HD_U_K1_pre2` |
| Fabric API | optional (tested with the release matching your version) | optional (tested 0.155.3+26.1.2) |

## 🤝 Compatibility

**Supported versions** (one jar each — the version inside the file name is the only way to tell them apart):

| Minecraft | jar | OptiFine build | State |
|---|---|---|---|
| 1.21 | `OptiFabric-1.1.0+mc1.21.jar` | `preview_OptiFine_1.21_HD_U_J1_pre9.jar` | ✅ verified |
| 1.21.1 | `OptiFabric-1.1.0+mc1.21.1.jar` | `OptiFine_1.21.1_HD_U_J1.jar` | ✅ verified |
| 1.21.3 | `OptiFabric-1.1.2+mc1.21.3.jar` | `OptiFine_1.21.3_HD_U_J2.jar` | ✅ verified |
| 1.21.4 | `OptiFabric-1.1.2+mc1.21.4.jar` | `OptiFine_1.21.4_HD_U_J3.jar` | ✅ verified |
| 1.21.6 | `OptiFabric-1.1.2+mc1.21.6.jar` | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` | ⚠️ not recommended — that OptiFine build is broken on its own |
| 1.21.7 | `OptiFabric-1.1.2+mc1.21.7.jar` | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` | ⚠️ not recommended — same |
| 1.21.8 | `OptiFabric-1.1.2+mc1.21.8.jar` | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` | ✅ verified |
| 1.21.9 | `OptiFabric-1.1.2+mc1.21.9.jar` | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` | ✅ verified |
| 1.21.10 | `OptiFabric-1.1.2+mc1.21.10.jar` | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` | ✅ verified |
| 1.21.11 | `OptiFabric-1.1.2+mc1.21.11.jar` | `OptiFine_1.21.11_HD_U_J9.jar` | ✅ verified |
| 26.1.2 | `OptiFabric-Reforged-2.0.0+mc26.1.2.jar` | `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar` | ✅ verified |

OptiFine never shipped a build for **1.21.2 / 1.21.5**, so there is no jar for those — and none exists for 26.1, 26.1.1, 26.1.3, 26.2+ either, which is why the 26.x line stops at 26.1.2.

| | |
|---|---|
| ✅ Works | OptiFine's video settings, zoom, connected textures, dynamic lights, **shaders**, and (since 1.1.2) OptiFine's **anti-aliasing** on 1.21.3 – 1.21.11 |
| ⚠️ Neutralised | `BEFORE_BLOCK_OUTLINE` no longer fires (the outline is still drawn); the moving-block FRAPI hook is inert (vanilla still renders them); on 1.21.x Fabric's renderer API is backed by an inert placeholder |
| ❌ Incompatible | **Sodium** (declared `conflicts`), and `no_fog`, `thallium`, `xradiation`, `ryoamiclights` (declared `breaks`) |
| 📄 OptiFine-side limits | OptiFine cannot see resources inside Fabric mods (`Unknown resource pack type: …ModNioResourcePack`); shader packs print `Unknown macro value: IRIS_VERSION` and similar |

FRAPI/Indigo mods behave differently per line: on **1.21.x** Indigo steps aside (OptiFine *is* the terrain renderer, F3 shows `OptifineRendererPlaceholder`), while on **26.1.2** Indigo registers its own renderer and mods' own geometry — better grass, anything generated per block position — actually renders.

One caveat that is not the mod's: shader packs written for Iris or for an OptiFine version other than yours log their own errors (`Invalid program name`, `Unknown macro value: IRIS_VERSION`, `ParseException: Model variable not found: …`). `photon_v1.2a` is one such pack on 1.21.6+; `ComplementaryReimagined_r5.9.1` is used for the verification runs above.

## 📊 Verified State

Every release is checked by a single command, and the numbers below were produced for `1.1.2` (one run per Minecraft version):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-version.ps1 -Version 1.21.10 -ModVersion 1.1.2
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-26.ps1     # 26.x line
```

| Minecraft | Patched game classes (JVM) | OptiFine classes (JVM) | ASM verifier | `@At` / refmap / contracts / handles | Live test |
|---|---|---|---|---|---|
| 1.21.3 | 440 / 440 | 816 / 816 | 0 problems | 2 / 0 / 0 / 0 | anti-aliasing + shaders, no errors |
| 1.21.4 | 474 / 474 | 812 / 812 | 0 problems | 2 / 0 / 0 / 0 | no errors |
| 1.21.6 | 487 / 487 | 820 / 820 | 0 problems | 4 / 0 / 0 / 0 | that OptiFine build stalls on this machine |
| 1.21.7 | 500 / 500 | 823 / 823 | 0 problems | 4 / 0 / 0 / 0 | no errors |
| 1.21.8 | 516 / 516 | 831 / 831 | 0 problems | 4 / 0 / 0 / 0 | no errors, anti-aliasing checked by eye |
| 1.21.9 | 519 / 519 | 832 / 832 | 0 problems | 4 / 0 / 0 / 0 | no errors |
| 1.21.10 | 553 / 553 | 836 / 836 | 0 problems | 4 / 0 / 0 / 0 | no errors, no black screen |
| 1.21.11 | 570 / 570 | 874 / 874 | 0 problems | 4 / 0 / 0 / 0 | no errors, anti-aliasing checked by eye |
| 26.1.2 | 567 / 567 | 879 / 879 | 0 problems | 0 / 0 / 0 / 0 | single- and multiplayer, shaders, Indigo geometry |

The remaining `@At` misses are all **deliberately disabled** Indigo injections, and the patched classes count is taken in a single loader, the same way the game loads them. The full per-round record — every crash, its bytecode root cause and the fix — is in [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md), with the 26.x port in [`docs/PORT_26.x.md`](docs/PORT_26.x.md).

**Known gaps:**

1. **No development-environment support** — dev runs in the `named` namespace and would need a two-stage remapping (official → intermediary → named)
2. **The neutralised hooks are placeholders**, not working implementations (`Renderer.get()` returns an inert renderer on 1.21.x)
3. **Upstream's per-mod `compat/**` mixins are not ported** — they depend on Manningham Mills' early-riser mechanism
4. **OptiFine's individual features are not measured one by one** (connected textures, zoom, dynamic lights, FPS gain); startup, world entry, block/chunk/item/entity rendering, shaders and multiplayer are confirmed
5. **26.x covers 26.1.2 only** — another 26.1.x or 26.2 would need its own pass, and OptiFine ships no build for anything after 26.1.2 anyway

## 📝 Project Structure

```
OptiFabric/
├── common/                 # shared sources of both lines
│   └── src/main/
│       ├── java/kynarain/cn/optifabric/
│       │   ├── mod/        #   pipeline, transformer hook, jar fixer, renderer fallback
│       │   ├── patcher/    #   fixer registry + the per-release bytecode fixes
│       │   ├── mixin/      #   our own two mixins
│       │   └── util/
│       └── resources/      # fabric.mod.json (expanded per line), icon, mixin config
├── v1.21.x/                # 1.21 line: legacy fabric-loom, yarn + intermediary, Java 21
├── v26.x/                  # 26.x line: net.fabricmc.fabric-loom, no mappings, Java 25
├── docs/                   # DEVELOPMENT.md, PORT_26.x.md, VERSIONING.md, DESCRIPTION.md, PUBLISHING.md
├── release/                # version.ps1, publish.ps1, per-version release notes, checklists
├── dist/                   # built release artifacts (not in git)
└── gradlew(.bat)           # shared wrapper — the root is not a Gradle project
```

## 🔐 License

**MPL-2.0** — see [`LICENSE.txt`](LICENSE.txt). The core mechanism is a port of [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric); ported files keep their origin headers. OptiFine itself is **not** included or redistributed — get it from [optifine.net](https://optifine.net/) (or the mirror above).

## 🙋 Support

1. Read [`docs/DESCRIPTION.md`](docs/DESCRIPTION.md) — the store-page copy answers the common questions (cache, `-Doptifabric.mc-jar`, `-Doptifabric.extract=true`)
2. Check the launcher **console**, not `logs/latest.log`: `[OptiFabric]`'s own output only goes to stdout
3. Force a rebuild by deleting `<game dir>/.optifine/` (cache format is `26`)
4. When opening an issue, attach `logs/latest.log` (plus the `crash-reports/` file if it crashed — its `OptiFabric` section lists the OptiFine version, jar state and mapped-jar path), the contents of `mods/`, and your OptiFine build name

Expected noise in the log (nothing to act on): the `[OptiFabric] Resource not found: minecraft:shaders/post/fxaa_of_*.json` warnings are OptiFine probing its own pre-1.21.6 chain location (1.21.8+ builds no longer fill it in; the chain in use lives in `post_effect/`); `Failed to locate initialiser injection point in <init>(…)` is the price of applying OptiFine's `BlockEntity` patch; `[OptiFine] (Reflector) Class not present: net.minecraftforge.* / sun.misc.SharedSecrets` is OptiFine looking for Forge and old JDKs; `Unknown macro value: IRIS_VERSION` / `Invalid macro expression` / `Shaders: Block not found for name: minecraft:planks` / `ParseException: Model variable not found: …` come from the shader pack; `Skipping bad option: lastServer` is a leftover field in `options.txt`.

## 🌟 Credits

- **[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)** by Modmuss50 and Chocohead — the original project this port is built on
- **sp614x** for OptiFine itself
- The **Fabric** team for Loader, Loom and tiny-remapper

---

**Note:** this is a community port. It is not affiliated with, endorsed by or supported by the OptiFine, Fabric or Mojang teams.
