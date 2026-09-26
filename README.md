# OptiFabric Reforged

<p align="center">
  <img src="src/main/resources/assets/optifabric/icon.png" alt="OptiFabric Reforged" width="128"/>
</p>

[🇨🇳 中文版](./README_CN.md) | 🇬🇧 English

[![Minecraft](https://img.shields.io/badge/Minecraft-26.2-green.svg)](https://www.minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-%E2%89%A5%200.19.5-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MPL--2.0-lightgrey.svg)](LICENSE.txt)

> ⚠️ This port was written and verified with AI assistance (DeepSeek). Be careful with it in production.

Load **OptiFine** under **Fabric Loader**. Put OptiFine's jar next to this mod and it patches the vanilla client with OptiFine's own patcher, rebuilds the lambdas whose targets moved, and hands the patched Minecraft classes to Fabric Loader's class transformer — so both can live in one client. **OptiFine itself is not bundled or redistributed.**

This branch is the **26.x line** and covers **Minecraft 26.2** (current) and **Minecraft 26.1.2**. The 1.21.x line (Minecraft 1.21 – 1.21.11) lives on the [`1.21.x` branch](../../tree/1.21.x) and is developed separately; jars from the two lines are **not interchangeable**.

## 📖 Overview

OptiFine is not a Fabric mod: its jar holds bytecode patches against vanilla client classes plus its own classes. OptiFabric drives OptiFine's patcher at `preLaunch`, repairs what OptiFine's recompiler left behind, and registers the patched classes with Loader before Mixin ever sees them.

Minecraft **26.1 and newer is unobfuscated** — the official names *are* the runtime names, and there is neither yarn nor a real intermediary to remap through (26.1.2 publishes only the `0.0.0` placeholder). So this line takes no mappings, needs Loom's non-remapping flavour, and runs in the `official` namespace. The line also carries **its own mod id**, because some mods declare `"breaks": {"optifabric": "*"}` and Fabric Loader matches that by **id** — a display-name change would not be enough.

**Author:** kynarain · upstream: Modmuss50, Chocohead
**Version:** `2.1.1` (`OptiFabric-Reforged-2.1.1+mc26.2.jar`)
**License:** MPL-2.0

## ✨ Key Features

- 🔄 **No OptiFine installer run by hand** — drop the installer jar (with its `patch/` diffs) or an already-extracted OptiFine into `mods/`; everything happens at startup
- 🆔 **Its own mod id** — `optifabric_reforged`, so mods that declare `breaks`/`conflicts` against `optifabric` no longer block this line
- 🎨 **Indigo registers its own renderer** — 26.1 moved the terrain and submit-node integration into `fabric-renderer-api-v1` itself, so Indigo is not a terrain renderer here and the "step aside" key is not declared
- 🧩 **Mods' own geometry actually renders** — Fabric's terrain hook injects into a loop OptiFine's chunk builder does not have, so the block tessellation call is routed through a bridge that hands Fabric's quads to OptiFine's own `BlockQuadOutput` (vertex format, layers, lighting and shader attributes stay OptiFine's)
- 🛠️ **Bytecode repairs** — OptiFine's recompiler hollows out vanilla methods, moves injection points into overloads and replaces subclasses; the fixers restore vanilla bodies, disambiguate same-name overloads (renaming the ones still called and redirecting their callers), drop the ones nobody calls, and re-create `NEW` points
- 🧪 **Offline verification as a first-class tool** — every patched class and every OptiFine class is loaded in a single loader and checked with the JVM verifier plus an ASM data-flow verifier, on top of five scanners
- ⚙️ **Caching** — the whole pipeline result is cached under `.optifine/<OptiFine version>/`; later launches take 1–2 seconds
- 🧯 **Honest failure** — a missing, corrupt, duplicated or mismatched OptiFine jar produces an error dialog at the title screen and an `OptiFabric` section in the crash report

## 🏗️ How It Works

```
mods/<OptiFine jar>
        │  ① OptiFine's own optifine.Patcher patches the vanilla client jar
        │  ② LambdaRebuilder: lambdas in patched classes point at methods that moved
        ▼
   patched client jar   (OptiFine's patches + OptiFine's classes)
        │  ④ split in two  (this line has no ③ remapping step)
        ├── non-Minecraft classes (OptiFine's own classes + resources) ──► game class path
        └── patched net/minecraft/** classes ──────────────────────────► ClassCache
```

Replacement happens through **Fabric Loader's own GameTransformer**: when a Minecraft class is about to be loaded, Loader asks the game provider's `GameTransformer.transform(...)` for ready-made bytecode — *before* Mixin runs. The classes registered at `preLaunch` (after the `patcher/fixes` corrections) therefore win, while classes Loader patched itself keep Loader's version.

That is why no stub mixin has to be generated per patched class and no Mixin extension API is needed: what is handed over is Mixin's **input**, not its output, so other mods' mixins against those classes keep working.

There is one hard constraint: **before the patched classes are handed to Loader, nothing may reflect on game classes** — a single `Class.getMethods()` loads every type in those method signatures and pins the class to vanilla forever. This code only ever touches bytes (`getClassByteArray` / ASM), never a `Class` object.

| Component | Purpose |
|---|---|
| `OptifabricRuntime` | whole pipeline: find the jar → patch → repair → register |
| `GameTransformerHook` | injects the patched classes into Loader's game transformer |
| `OptifineMappings` / `OptifineJarFixer` | name alignment; repairs OptiFine's own jar |
| `patcher/fixes/**` | the bytecode fixers (vanilla bodies, overload disambiguation, `NEW` points, …) |
| `OptifineFrapiBridge` + `FrapiTesselateBridgeFix` | routes block tessellation through Fabric's renderer, writing into OptiFine's `BlockQuadOutput` |
| `RendererApiFallback` | registers an inert placeholder only where the "step aside" key is declared |

Intermediate files live in `<game dir>/.optifine/<OptiFine version>/`:

| File | Content |
|---|---|
| `cache-format.txt` | cache format version (currently `26`); a mismatch rebuilds everything |
| `Optifine-mapped.jar` | the processed OptiFine jar (without MC classes) that goes on the class path |
| `Optifine.classes.gz` | the patched MC classes for the next launch |

## 📦 Installation

1. Get the OptiFine build for **exactly** your Minecraft version (see the table below) — OptiFabric reads `MC_VERSION` from `optifine/Config` and refuses to start otherwise. Do **not** run OptiFine's installer.
2. Put this mod's jar **and** OptiFine's jar into that Fabric instance's `mods/` folder. Do not install two OptiFine jars (the game reports `DUPLICATED`), and do not mix in a jar from the 1.21.x line.
3. Launch the **Fabric** profile — not a launcher-made profile that injects OptiFine itself.
4. The first start is noticeably slower (it runs the whole patch pipeline); later starts use the cache. A title screen showing OptiFine's version and OptiFine entries in video settings mean it worked.

On **26.2**, do not expect shaders: selecting a shaderpack in OptiFine does nothing with this OptiFine build (the reason is under *Compatibility* below). The world itself opens and renders normally. Shaders do work on 26.1.2.

With version isolation enabled (PCL2 / HMCL), the game directory and `mods/` both live under `versions/<name>/`, and the `.optifine/` cache is created there too.

```powershell
# OptiFine 26.2 (preview; the path has four segments and the patch carries the K2 prefix)
curl.exe -L -o preview_OptiFine_26.2_HD_U_K2_pre1.jar `
  "https://bmclapi2.bangbang93.com/optifine/26.2/HD_U_K2/pre1"

# OptiFine 26.1.2 (the same shape, K1 prefix)
curl.exe -L -o preview_OptiFine_26.1.2_HD_U_K1_pre2.jar `
  "https://bmclapi2.bangbang93.com/optifine/26.1.2/HD_U_K1/pre2"
```

## 🔨 Building from Source

**JDK 25** is required, and the repository root *is* the Gradle project:

```bash
./gradlew build          # -> build/libs/OptiFabric-Reforged-2.1.1+mc26.2.jar
```

The version number always travels with the Minecraft version, and it only ever changes through one script:

```powershell
.\release\version.ps1                     # show the current version and what each bump would produce
.\release\version.ps1 -Kind minor         # bump this line
```

The development environment is not supported: `gradlew runClient` is refused outright, because dev runs in the `named` namespace and would need an extra mapping layer.

## 📋 Requirements

| | |
|---|---|
| Minecraft | **26.2** (the current target) — and **26.1.2**, from the same source |
| Fabric Loader | ≥ 0.19.5 |
| Java | **25** (the game's own hard requirement; Java 21 fails before the window appears) |
| Side | client |
| OptiFine | `preview_OptiFine_26.2_HD_U_K2_pre1.jar` for 26.2, `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar` for 26.1.2 (preview only, both of them) |
| Fabric API | optional — tested with `0.161.0+26.2` on 26.2 and `0.155.3+26.1.2` on 26.1.2 |

## 🤝 Compatibility

| Minecraft | jar | OptiFine build | Java | State |
|---|---|---|---|---|
| 26.2 | `OptiFabric-Reforged-2.1.1+mc26.2.jar` | `preview_OptiFine_26.2_HD_U_K2_pre1.jar` | 25 | ✅ verified in game (**no shaders** — see below) |
| 26.1.2 | `OptiFabric-Reforged-2.0.0+mc26.1.2.jar` | `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar` | 25 | ✅ verified in game |

`2.1.1` is a patch release, and it replaces `2.1.0+mc26.2`: that build forced OptiFine's cancelled shaderpack load back on, and with a shaderpack selected the world then drew **nothing but particles with the blocks see-through** (27 shader programs compiled, no error in any log). `2.1.1` leaves that bytecode shape exactly as OptiFine wrote it. The line is not narrowed by it: the same source still builds for 26.1.2 and goes through the whole offline pipeline there with the numbers 2.0.0 recorded (see *Verified State* below), so 26.1.2 was not dropped — its jar is simply unchanged, and `2.0.0+mc26.1.2` stays the build to use on that release.

OptiFine has a build for these two releases and for no other: 26.1, 26.1.1, 26.1.3, 26.2.1 and 26.3 have **empty build lists** — check for yourself (the answer is the *body* being an empty array):

```powershell
curl.exe -s "https://bmclapi2.bangbang93.com/optifine/26.1.2"   # lists pre1 / pre2
curl.exe -s "https://bmclapi2.bangbang93.com/optifine/26.2"     # lists HD_U_K2 (pre1)
curl.exe -s "https://bmclapi2.bangbang93.com/optifine/26.3"     # -> []
```

26.2.1 and 26.3 are the releases after 26.2, and each would need its own pass — and an OptiFine build to port against first, which is why this line stops at 26.2.

| | |
|---|---|
| ✅ Works | OptiFine's video settings, zoom, connected textures, dynamic lights, **anti-aliasing** — and FRAPI mods' own geometry (verified on 26.1.2 with LambdaBetterGrass: better grass and connected textures, with shaders on). **Shaders work on 26.1.2 only** (the 2.0.0 record); on 26.2 they are **not available** — see the row below |
| ⚠️ Shaders on 26.2 | **not available with this OptiFine build.** The 26.2 preview cancels the shaderpack load inside `Shaders.loadShaderPack` (`[Shaders] No shaderpack loaded.`), and that is left exactly as OptiFine wrote it. OptiFine's shader settings still let you pick a pack, and it then **silently does nothing**. Forcing the load back on — what 2.1.0 did — made it worse: `[Shaders] Loaded shaderpack: ComplementaryReimagined_r5.9.1.zip`, 27 programs compiled, and a world that drew **only particles with see-through blocks** |
| ⚠️ Neutralised | two Fabric render hooks are intentionally inert: the **moving-block** and **block-model** submits (block-breaking crack overlays still go through Fabric's renderer); those paths are drawn by vanilla/OptiFine instead |
| ❌ Incompatible | **Sodium** (declared `conflicts`), plus `no_fog`, `thallium`, `xradiation`, `ryoamiclights` (declared `breaks`) |
| 📄 OptiFine-side limits | OptiFine cannot see resources inside Fabric mods (`[OptiFine] Unknown resource pack type: …ModNioResourcePack`); shader packs print their own `[Shaders]` errors when they do not match your OptiFine build |

## 📊 Verified State

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-26.ps1 -Version 26.2
```

| Check | **26.2** | 26.1.2 (same source, `-Version 26.1.2`) |
|---|---|---|
| Patched game classes (JVM verifier) | **562 / 562**, 0 failures | **567 / 567**, 0 failures |
| OptiFine's own classes (JVM verifier) | **879 / 879**, 0 failures (2 NeoForge-only classes are not applicable) | **879 / 879**, 0 failures (as recorded for 2.0.0) |
| ASM data-flow verifier | **0 problems** | **0 problems** |
| `@At` points / mixin member references / abstract contracts / lost virtual overrides / unresolvable references / invokedynamic handles | **all 0** (cleaner than the 1.21.x line, which still carries a few disabled-Indigo misses) | **all 0** — the numbers 2.0.0 recorded |
| Live | pipeline runs (`[OptiFabric] Prepared 562 patched classes (0 skipped, 0 failed)`), the world opens and **renders normally**, with `[Shaders] No shaderpack loaded.` — **shaders are not available on 26.2 with this OptiFine build** (picking a pack does nothing), no crash, no mixin failure | startup, title screen, single-player, chunk rebuild, block/item/entity rendering, **anti-aliasing**, **shaders**, multiplayer (the 2.0.0 record) |

The 26.2 in-game pass measured the shader problem itself: forcing the cancelled load back on (what `2.1.0` shipped) compiles 27 programs and then draws **only particles, with the blocks see-through** and nothing in any log; leaving OptiFine's cancellation in place draws the world normally. It did not repeat the multiplayer or anti-aliasing passes, which are still the 26.1.2/2.0.0 record.

The two "not applicable" classes are `optifine.OptiFineClassProcessor` and `optifine.VirtualJarContents`: they implement **NeoForge** SPIs (`net.neoforged.*`) and are never loaded under Fabric.

The full record — every crash, the bytecode behind it and the fix — is in [`docs/PORT_26.x.md`](docs/PORT_26.x.md) and [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md).

**Known gaps:**

1. **No development-environment support** — dev runs in the `named` namespace and would need a two-stage remapping
2. **The two neutralised hooks are inert**, not reimplemented
3. **Nothing after 26.2 is ported** — 26.2.1 and 26.3 each need their own pass over the official-name conflicts, and OptiFine ships no build for either of them yet
4. **Shaders are not available on 26.2** — with this OptiFine build the shaderpack load stays cancelled, so selecting a pack in OptiFine silently does nothing; forcing it back on draws only particles. Shaders remain available on 26.1.2

## 📝 Project Structure

```
OptiFabric-Reforged/
├── src/main/java/kynarain/cn/optifabric/
│   ├── Optifabric.java              # preLaunch entry point
│   ├── mod/                         # pipeline, transformer hook, jar fixer, FRAPI bridge, renderer fallback
│   ├── patcher/                     # ClassCache, LambdaRebuilder and fixes/ (the bytecode fixes)
│   ├── mixin/                       # the two mixins this project ships
│   └── util/                        # ASM / mixin / remap / zip helpers
├── src/main/resources/              # fabric.mod.json, optifabric.mixins.json, assets/…/icon.png
├── docs/                            # PORT_26.x.md, DEVELOPMENT.md, VERSIONING.md, DESCRIPTION.md, PUBLISHING.md
├── release/                         # version.ps1, publish.ps1, notes/, MANUAL_RELEASE.md
├── build.gradle · gradle.properties · settings.gradle
└── gradlew · gradlew.bat
```

## 🔐 License

**MPL-2.0** — see [`LICENSE.txt`](LICENSE.txt). The core mechanism is a port of [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric); ported files keep their origin headers. OptiFine itself is **not** included or redistributed — it is sp614x's work, get it from [optifine.net](https://optifine.net/) (or the mirror above).

## 🙋 Support

- **Nothing changed after upgrading?** Delete `<game dir>/.optifine/` — the cache holds patched bytecode.
- `[OptiFabric]`'s output goes to the **launcher console**, not to `logs/latest.log`; filtering for `[OptiFabric]` shows how many classes were prepared and how many Loader took over.
- The title screen shows an error dialog for a missing/corrupt/duplicated/mismatched OptiFine jar, and the crash report gains an `OptiFabric` section (OptiFine version, jar state, mapped-jar path).
- Point the mod at a specific vanilla jar with `-Doptifabric.mc-jar=<path>`; unpack the processed OptiFine classes with `-Doptifabric.extract=true`.
- **Stuck on the loading screen:** take two thread dumps (`jstack <pid>`, ~15 s apart) and compare. Identical stacks with flat CPU means a real stall; a stack sitting in a native call (`glfwSwapBuffers`) is a presentation problem — and do not minimise the window while loading (with vsync on, the render thread blocks in `SwapBuffers`).
- **Models/items/textures disappearing wholesale:** a Fabric mixin failed to transform that class (the outermost message usually hides the real cause). OptiFine loves turning vanilla methods into thin wrappers that forward to its own overloads, which moves injection points with them.

Log lines that are normal and can be ignored:

| Log | Meaning |
|---|---|
| `[OptiFine] (Reflector) Class not present: net.minecraftforge.*` / `sun.misc.SharedSecrets` | OptiFine probing for Forge and old JDKs; Fabric has neither |
| `[OptiFabric] Resource not found: minecraft:shaders/post/fxaa_of_{2,4}x.json` | OptiFine still probes its pre-1.21.6 chain location once; the chain in use lives in `post_effect/` |
| `[Shaders] Unknown macro value: IRIS_VERSION` / `ANGELICA_VERSION` | the shader pack probing for Iris/Angelica |
| `[Shaders] Invalid macro expression` / `ParseException: Model variable not found: …` | shader-pack vs OptiFine version mismatch |
| `Skipping bad option: lastServer` | a leftover field in the options file |

## 🌟 Credits

- **[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)** by Modmuss50 and Chocohead — the original project this port is built on
- **sp614x** for OptiFine itself
- The **Fabric** team for Loader, Loom and tiny-remapper

---

**Note:** this is a community port. It is not affiliated with, endorsed by or supported by the OptiFine, Fabric or Mojang teams.
