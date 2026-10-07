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

Load **OptiFine** under **Fabric Loader**: put OptiFine's jar next to this mod and it patches the vanilla client with OptiFine's own patcher, rebuilds the lambdas whose targets moved, repairs what OptiFine's recompiler left behind, and hands the patched Minecraft classes to Loader's class transformer — so both mods can live in one client. **OptiFine itself is not bundled or redistributed.**

This is the **26.x line** and covers **Minecraft 26.2** (current) and **Minecraft 26.1.2**. The 1.21.x line (Minecraft 1.21 – 1.21.11) lives on the [`1.21.x` branch](../../tree/1.21.x) and is developed separately; jars from different lines are **not interchangeable**.

**Author:** kynarain · upstream: Modmuss50, Chocohead · **License:** MPL-2.0 · **Current version:** `2.2.7`

## 📦 Installation

1. Install **Fabric Loader ≥ 0.19.5** on **Java 25** (the game's own hard requirement — Java 21 fails before the window appears), then put this line's jar and the OptiFine jar for **exactly** your Minecraft version into that instance's `mods/` folder. Do **not** run OptiFine's installer.
2. Launch the **Fabric** profile — not a launcher-made `26.x-OptiFine_xxx` profile, which injects OptiFine itself and collides with this mod.
3. The first start spends a few extra seconds patching (5–7 s in practice); later starts reuse the cache (1–2 s).

This line is called **Reforged** because it carries **its own mod id** (`optifabric_reforged`): some mods declare `"breaks": {"optifabric": "*"}` and Fabric Loader matches that by **id**, so a display-name change would not be enough. Both 26.x artifacts are built from one source tree; there is no second 26.x product to keep apart from this one.

> **OptiFabric cannot assume your launcher installs OptiFine for you**, because many launchers only offer **OptiFine _or_ Fabric** as the profile, never both: install **Fabric + OptiFabric** first, download OptiFine from <https://optifine.net/downloads>, put that jar in the `mods` folder, then start the game once.
>
> **OptiFabric 不能假设启动器会替你装 OptiFine**:很多启动器只能选 **OptiFine _or_ Fabric**。先装好 **Fabric + OptiFabric**,从 <https://optifine.net/downloads> 下载 OptiFine,放进 `mods` 文件夹,然后手动启动一次游戏。

## 🤝 Compatibility

| Minecraft | jar | OptiFine build | Java | State |
|---|---|---|---|---|
| 26.2 | `OptiFabric-Reforged-2.2.8+mc26.2.jar` | `preview_OptiFine_26.2_HD_U_K2_pre1.jar` | 25 | ✅ verified in game (**no shaders** — see below) |
| 26.1.2 | `OptiFabric-Reforged-2.2.8+mc26.1.2.jar` | `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar` | 25 | ✅ verified in game |

Both OptiFine builds are **previews** — OptiFine has no final release for either Minecraft version yet.

### OptiFabric version → Minecraft version → recommended OptiFine build

This is the same list the mod itself carries and `release\notes\mc<MC>.md` states per release; the release tooling fails when the copies disagree.

| OptiFabric version | Minecraft version | Recommended OptiFine build | Build kind |
|---|---|---|---|
| `2.2.8+mc26.2` | 26.2 | `preview_OptiFine_26.2_HD_U_K2_pre1.jar` | preview (no final exists) |
| `2.2.8+mc26.1.2` | 26.1.2 | `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar` | preview (no final exists) |

### The 26.2 shader limitation

On **26.2**, selecting a shader pack in OptiFine **does nothing** with this OptiFine build: the game keeps rendering without it. It is not a crash and not this mod's doing — that build does not load shader packs on this Minecraft version. Since **2.2.7** the mod says so in the same title-screen dialog as soon as it sees that build installed. **26.1.2 is not affected.**

| | |
|---|---|
| ✅ Works | OptiFine's video settings, zoom, connected textures, dynamic lights, and the block-model geometry Fabric mods submit (routed through a bridge into OptiFine's own `BlockQuadOutput`) |
| ⚠️ Neutralised | OptiFine's `BEFORE_BLOCK_OUTLINE` event and the moving-block FRAPI hook are inert, exactly as on the 1.21.x line |
| ❌ Incompatible | **Sodium**, plus `no_fog`, `thallium`, `xradiation`, `ryoamiclights` (see the next section) |
| 🚫 Not applicable | the 1.21.x line's Indigo note: 26.1 moved the terrain and submit-node integration into `fabric-renderer-api-v1` itself, so Indigo is **not** a terrain renderer here and the "step aside" key is not declared |

### Declared incompatibilities, and what the loader actually does

Everything this mod declares against lives in `fabric.mod.json` and nowhere else — no screen shows it — so this is that list in prose:

| Declaration | Entries |
|---|---|
| `conflicts` | `sodium` (`*`) |
| `breaks` | `no_fog`, `thallium`, `xradiation`, `ryoamiclights`, `sodium` (`*`) |

**`breaks` is a gate, `conflicts` is only a warning.** On Fabric Loader 0.19.5 a `conflicts` entry adds no constraint to the loader's solver at all (`ModSolver`'s `CONFLICTS` case is still a `// TODO: soft negative dep?`), so the game starts with `Warnings were found!`; a `breaks` entry against a present mod makes the solver emit `NEG_HARD_DEP` and the loader **refuses the whole instance** (`Incompatible mods found!`). Since `sodium` appears in both fields here, the effect is the gate — deliberately.

**Why Sodium is a hard break.** Two terrain renderers cannot share one pipeline. Measured on the 1.21.x line's 1.21.1 target with Sodium 0.8.13: with every repairable gap closed, Sodium's mixins all apply and the client loads a world, but the frame stays **black**; handing the single renderer slot to Sodium, and turning OptiFine's Fast Render off, each changed nothing. Sodium's own metadata declares the pairing on its side too, but under the **old** mod id, so that entry cannot fire here.

`no_fog`, `thallium` and `xradiation` are inherited from upstream ([Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)); `ryoamiclights` is this fork's own addition, because OptiFine replaces the whole video-settings screen — parent class included — and that mod's mixin targets the vanilla parent (nothing is lost: OptiFine has its own dynamic lights).

## 🏗️ How It Works

```
mods/<OptiFine jar>
        │  ① OptiFine's own optifine.Patcher patches the vanilla client jar
        │  ② LambdaRebuilder: lambdas in patched classes point at methods that moved
        ▼
   patched client jar   (OptiFine's patches + OptiFine's classes)
        │  ③ split in two   (this line has no remapping step — see below)
        ├── OptiFine's own classes + resources ─► game class path
        └── patched net/minecraft/** classes ───► ClassCache
```

**There is no remapping step on this line, and that is the point.** Minecraft **26.1 and newer is unobfuscated** — the official names *are* the runtime names, and there is neither yarn nor a real intermediary to remap through. So this line takes no mappings, needs Loom's non-remapping flavour, and runs in the `official` namespace.

Replacement goes through **Loader's own GameTransformer**: when a Minecraft class is about to load, Loader asks the game provider's `GameTransformer.transform(...)` for ready-made bytecode — *before* Mixin runs. What is handed over is Mixin's **input**, not its output, so other mods' mixins against those classes keep working, and no stub mixin has to be generated per patched class. One hard constraint: **before the patched classes are handed to Loader, nothing may reflect on game classes** — a single `Class.getMethods()` would pin a class to vanilla forever — so this code only ever touches bytes.

Geometry from Fabric mods is a special case on 26.1+: Fabric's terrain hook injects into a loop OptiFine's chunk builder does not have, so the block tessellation call is routed through a **bridge** that hands Fabric's quads to OptiFine's own `BlockQuadOutput` — vertex format, layers, lighting and shader attributes all stay OptiFine's.

| Component | Purpose |
|---|---|
| `OptifabricRuntime` | whole pipeline: find the jar → patch → repair → register |
| `GameTransformerHook` | injects the patched classes into Loader's game transformer |
| `OptifineFrapiBridge` | hands Fabric's quads to OptiFine's `BlockQuadOutput` |
| `OptifineJarFixer` | repairs OptiFine's own jar |
| `patcher/fixes/**` | the bytecode fixers (vanilla bodies, overload disambiguation, `NEW` points, …) |
| `RendererApiFallback` | registers an inert placeholder where Fabric's renderer API would otherwise be empty |

Intermediate files live in `<game dir>/.optifine/<OptiFine version>/`: `cache-format.txt` (format version — a mismatch rebuilds everything), `Optifine-mapped.jar` and `Optifine.classes.gz` (the patched MC classes for the next launch).

## ✨ Key Features

- 🔄 **No installer run by hand** — drop the installer jar or an already-extracted OptiFine into `mods/`; everything happens at startup
- 🆔 **Its own mod id** — `optifabric_reforged`, so mods that declare `breaks`/`conflicts` against `optifabric` no longer block this line
- 🧩 **Mods' own geometry actually renders** — see the bridge above
- 🛠️ **Bytecode repairs** — OptiFine's recompiler hollows out vanilla methods, moves injection points into overloads and replaces subclasses; the fixers restore vanilla bodies, disambiguate same-name overloads, drop the ones nobody calls, and re-create `NEW` points
- 🧪 **Offline verification as a first-class tool** — every patched class and every OptiFine class is loaded in a single loader and checked with the JVM verifier plus an ASM data-flow verifier
- ⚙️ **Caching** — the pipeline result is cached under `.optifine/<OptiFine version>/`; later launches take 1–2 seconds
- 🧯 **Honest failure** — a missing, corrupt, duplicated or mismatched OptiFine jar produces a dialog at the title screen and an `OptiFabric` section in the crash report

## 📊 Verified State

Each release is checked the same way the 1.21.x line does it: every patched game class and every OptiFine class is loaded in a single loader, run through the JVM verifier plus an ASM data-flow verifier, and the scanners look for `@At` / refmap / contract / handle problems. Both targets of this line have been started in game by hand; the 26.2 result is the one with the shader limitation above.

**Known gaps:**

1. **No development-environment support** — `gradlew runClient` is refused, for the same reason as on the 1.21.x line
2. **26.2 shaders** — waiting for an OptiFine build that loads shader packs on that Minecraft version
3. **The two neutralised hooks are placeholders**, not working implementations

## 🔨 Building from Source

**JDK 25** is required (the game's own requirement), and the repository root *is* the Gradle project — the target comes from `minecraft_version`, so a build switches versions with a property rather than `-Pmc`:

```bash
./gradlew build                                      # 26.2, the default target
./gradlew build "-Pminecraft_version=26.1.2"          # the other target of this line
```

The jars land in `build/libs/OptiFabric-Reforged-<version>+mc<mc>.jar`.

## 📋 Requirements

| | |
|---|---|
| Minecraft | **26.2** (the current target) — and **26.1.2**, from the same source |
| Fabric Loader | ≥ 0.19.5 |
| Java | **25** (the game's own hard requirement; Java 21 fails before the window appears) |
| Side | client |
| OptiFine | `preview_OptiFine_26.2_HD_U_K2_pre1.jar` for 26.2, `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar` for 26.1.2 (preview only, both of them) |
| Fabric API | optional — tested with `0.161.0+26.2` on 26.2 and `0.155.3+26.1.2` on 26.1.2 |

## 📝 Project Structure

```
OptiFabric-Reforged/
├── src/main/java/kynarain/cn/optifabric/
│   ├── Optifabric.java              # preLaunch entry point
│   ├── mod/                         # pipeline, transformer hook, jar fixer, FRAPI bridge
│   ├── patcher/                     # ClassCache, LambdaRebuilder and fixes/ (the bytecode fixes)
│   ├── mixin/                       # the mixins this project ships
│   └── util/                        # ASM / mixin / zip helpers
├── src/main/resources/              # fabric.mod.json, optifabric.mixins.json, assets/…/icon.png
├── docs/                            # DEVELOPMENT.md, PUBLISHING.md, REFORGED_BUILD.md, …
└── release/                         # notes/, MANUAL_RELEASE.md, make-metadata.ps1
```

## 🙋 Support

- **Nothing changed after upgrading?** Delete `<game dir>/.optifine/` — the cache holds patched bytecode and is rebuilt automatically.
- `[OptiFabric]`'s output goes to the **launcher console**, not `logs/latest.log`; filtering for `[OptiFabric]` shows how many classes were prepared and how many Loader took over.
- The title screen shows a dialog for a missing/corrupt/duplicated/mismatched OptiFine jar, and the crash report gains an `OptiFabric` section (OptiFine version, jar state, mapped-jar path).
- **`NoClassDefFoundError: Could not initialize class net.optifine.reflect.Reflector`** (or the game dying during startup with no crash report): another mod's mixin failed to apply to a class OptiFine patches. Add `-Dmixin.debug=true` to the JVM arguments and run again — Mixin only names the failing mod in debug mode. **The fix** is to remove that mod or run without OptiFine; it cannot be fixed from this side.
- **Stuck on the loading screen:** take two thread dumps (`jstack <pid>`, ~15 s apart) and compare; identical stacks with flat CPU means a real stall.

Log lines that are normal and can be ignored:

| Log | Meaning |
|---|---|
| `[OptiFine] (Reflector) Class not present: net.minecraftforge.*` / `sun.misc.SharedSecrets` | OptiFine probing for Forge and old JDKs; Fabric has neither |
| `Failed to locate initialiser injection point in <init>(…)` | the price of applying OptiFine's `BlockEntity` patch |
| `[Shaders] Unknown macro value: IRIS_VERSION` / `ANGELICA_VERSION` | the shader pack probing for Iris/Angelica |
| `Skipping bad option: lastServer` | a leftover field in the options file |

### Known issue: Litematica schematics + a shader pack floods the log with OpenGL 1282

With a shader pack enabled and a Litematica schematic being rendered, OptiFine logs thousands of `[Shaders] OpenGL error: 1282 (Invalid operation), program: gbuffers_terrain, at: pre-useProgram`. The failing call is **Litematica's own** (`WorldRendererSchematic.renderBlockLayer` uploads the vanilla `ShaderProgram.chunkOffset` uniform, which is invalid while OptiFine's program is bound); OptiFine only **reports** it, because `Shaders.useProgram` starts with `checkGLError("pre-useProgram")`. Workarounds: disable the shader pack, or stop rendering the schematic.

## 🔐 License

**MPL-2.0** — see [`LICENSE.txt`](LICENSE.txt). The core mechanism is a port of [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric); ported files keep their origin headers. OptiFine itself is **not** included or redistributed — it is sp614x's work, get it from [optifine.net](https://optifine.net/).

## 🌟 Credits

- **[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)** by Modmuss50 and Chocohead — the original project this port is built on
- **sp614x** for OptiFine itself
- The **Fabric** team for Loader, Loom and tiny-remapper

---

**Note:** this is a community port. It is not affiliated with, endorsed by or supported by the OptiFine, Fabric or Mojang teams.
