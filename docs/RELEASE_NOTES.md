# GitHub Release notes — tag `v2.2.8+mc1.21.11`(`OptiFabric-2.2.8+mc1.21.11.jar`)

> 复制下面 `---` 之间的内容到 GitHub Release 的说明框里(标题用第一行)。英文在前,末尾附中文摘要。
> 标签是**版本号 + 该 MC 版本**(`v2.2.8+mc1.21.11`):这个仓库同时承载 26.x 线,该线已用掉 `v2.0.0`,而 tag 是仓库级的。
> 其余版本号由 `release\version.ps1` 统一改写。

---

## OptiFabric 2.2.8+mc1.21.11 — OptiFine on Fabric 1.21.11

Run **OptiFine** and **Fabric** in the same 1.21.11 client. Drop OptiFabric and your own OptiFine jar into `mods/`; at startup OptiFabric runs OptiFine's installer, remaps its patches into Fabric's namespace, repairs the structural conflicts with Fabric API, and hands the result to Fabric Loader's class transformer.

**OptiFine is not bundled or redistributed** — bring your own `OptiFine_1.21.11_HD_U_J9.jar` (or another 1.21.11 build).

### Fixed in 2.2.3 — four of OptiFine's rewrite collisions and the FRAPI renderer registration

Seven `registerFix` entries and one registry lookup, each read off byte-level evidence rather than guessed:

- **`class_156`** → `RestoreVanillaMethodsFix(true, "method_29191")`. OptiFine's recompile downgraded one logging call inside `method_29191` from `Logger.error(String,Object)` to `Logger.debug(String,Object)` — a single opcode, the exception table and everything else identical. That call site is exactly what The Twilight Forest's `UtilMixin` redirects, so the redirect scanned nothing and `Mixin transformation of net.minecraft.class_156 failed` killed the client before the title screen, during OptiFine's `Reflector` bootstrap. Putting the vanilla body back is the only repair: the call sits inside a `catch`, so no fixer can reach it by name.
- **`class_638`** → `LambdaMethodRefFix()`. OptiFine renamed the method the game registers for its own colour resolvers to `lambda$new$3` — same descriptor, same `BootstrapMethods` slot — so porting_lib's `ClientLevelMixin` found no target and the whole class failed.
- **`class_761`** → `LambdaMethodRefFix()` for the Runnable body the game declares as `method_37365`, emitted as `lambda$updateCameraAndRender$1` with the class's own bootstrap handle re-pointed at it; that is what C2ME's `@ModifyArg` on view distance asks for by name. Deliberately *not* `RestoreVanillaMethodsFix("method_37365")`, which would leave OptiFine's lambda in use and the clamp never applied. The same class also gets `LocalSlotLayoutFix(null, "method_22710")` for the same `@Local` slot-layout reason `class_757.method_3192` has one.
- **`class_3898$class_3216`, `class_3204$class_4077`** → `SyntheticFieldFix()`. OptiFine renamed the synthetic outer-instance fields `field_17443` / `field_18255` to `this$0`, and an unresolvable `@Accessor` / `@Shadow` fails the whole target class the moment a world loads.
- **FRAPI registration** now goes through `RendererAccess.INSTANCE.registerRenderer(Renderer)` — verified with `javap` against the real `fabric-renderer-api-v1-0.116.17.jar` — instead of the removed static `Renderer.register(Renderer)`. The old lookup threw `NoSuchMethodException`, so no placeholder renderer was ever registered and every FRAPI mod saw a null `getRenderer()`; The Twilight Forest's `ForceFieldModel` died in its static initialiser on it.

**Verified:** C2ME now reaches the title screen and runs **113 s in a world with zero `[ERROR]` lines**. **The Twilight Forest now loads without OptiFine** (it previously could not reach the title screen at all). Its remaining OptiFine-side blocker is a known `LocalSlotLayoutFix` limitation — that fixer remaps per scope rather than per slot, and `class_761.method_22710` has 19 candidate slots against its `MAX_MOVES` of 8, so it declines the method — and that is **not fixed in this release**: The Twilight Forest is **not** fully supported. Still open as exposure for other mods: about **36** patched classes carry unregistered `vtN` / `this$N` synthetic fields.

**Corrected:** an earlier note here and in the READMEs said `conflicts` and `breaks` both merely warn. Measured behaviour is the opposite way round: a **`conflicts`** entry only warns (Fabric Loader 0.19.5's `ModSolver` adds no constraint for it at all), while a **`breaks`** entry is **enforced** — a run recorded `NEG_HARD_DEP optifabric_reforged 2.2.2 {breaks sodium}`, i.e. the loader refuses the combination when `breaks` names a present mod.

### Fixed in 2.2.1 — a mixin that resolves locals in a class OptiFine rewrites no longer dies at class load

OptiFabric replaces game classes with the bytes OptiFine's patches produce, but Mixin has already built and cached one `ClassInfo` per class — from the **game's own** copies, because Fabric prepares Mixin's configs before a `preLaunch` entrypoint runs. Where the cached entry and the bytes Mixin is actually handed disagree about a member, the lookup misses, and `Locals` — what `@ModifyVariable` and local-variable capture use — cannot recover from it: it resolves the method being transformed with `ClassInfo#findMethod(name, descriptor, method.access | INCLUDE_INITIALISERS)`, whose `ClassInfo.Member#matchesFlags` requires a member stored as private to be queried with `ACC_PRIVATE`. OptiFine recompiles `GameRenderer.getFov` and widens it from private to public, so against the cached game entry the lookup fails and the whole class is lost:

```
LVTGeneratorError: Could not locate method metadata for method_3196 generating LVT in net/minecraft/class_757
```

That is thrown from `ModifyVariableInjector.preInject`, **before `require` or `expect` are consulted** — the affected mod cannot work around it from its own side — and Fabric reports only the generic `Mixin transformation of net.minecraft.class_757 failed`.

`GameTransformerHook` now remembers the classes it actually replaced and drops their cache entries right after they are installed (internal-name form, reflectively, at most once per class, non-fatal on failure), so Mixin rebuilds that metadata from the bytes it is really handed: the class being transformed enters Mixin's target context through `ClassInfo#fromClassNode`, which returns the cached instance whenever there is one. A class whose bytes are still Fabric Loader's own was never replaced and keeps its entry.

Measured on 1.21.1 with OptiFine HD U J1, ShoulderSurfing 5.2.0, ForgeConfigAPIPort 21.1.6 and Fabric API: **the shipped 2.2.0 jar dies 6 s in with `Mixin transformation of net.minecraft.class_757 failed`**, while this build logs `Dropped 47 of 425 Mixin class metadata entries`, reaches the title screen, opens a world and compiles 27 in-world shader programs, with no injection error and no crash report. With Architectury 13.0.11 the slot fixer still reports the same moves (7→15, 10→16; 21 locals in OptiFine's copy against 18 in the game's) and the run ends FIXED.

### New in 2.2.0 — the game says what is missing, and fetches it from OptiFine's own site

**A missing OptiFine is no longer silent.** Until 2.2.0 the game simply started without OptiFine and none of its features; the user had to guess what was wrong. The title screen now consults the support table this mod carries and, in two cases, says so on a screen instead:

- **Nothing in `mods/` at all** — "OptiFine is not installed": shown on **every** launch until a jar is there, naming the build and the file name this Minecraft release expects. `Continue to main menu` lets that one session through and writes nothing.
- **A jar is there but its build is older** than the newest one this release knows — "OptiFine version mismatch": the game **keeps running with the OptiFine you have**, and the screen is only a recommendation. `Continue anyway` records that build in `config/optifabric-mismatch-ack.txt` so it is not mentioned again. Only a **preview** older than the table is ever worth interrupting for; a final build you already have is left alone even when a newer final exists.

**The download comes from OptiFine's own site, and only from where you point it.** The `Download OptiFine` button (called `Download the right build` in the mismatch case) puts the jar the table asks for into `mods\`. The URL field is pre-filled with the official two-step flow — the `optifine.net/adloadx?f=<file>` page carries a one-use token, and the jar is behind `downloadx?f=<file>&x=<token>` on the same host. The mod ships **no** third-party or mirror URL and never falls back to one: if that path fails (a non-200 status, no token on the page, a body that is not a zip, a zip that is not OptiFine, an unreachable host), the screen states the reason and swaps the button for `Open the official download page` plus `Re-check` — you fetch the named jar yourself, drop it in `mods/`, and re-check picks it up. The field is editable: a URL of your own carrying `{mc}` / `{type}` / `{patch}` / `{file}` is expanded from the table and fetched directly, but that choice is yours alone.

**A finished download asks before it restarts.** A success (or a re-check that finds the jar) no longer quits the game on its own: it asks **"Restart the game to make OptiFine take effect?"**, and `Restart now` re-runs the game with **the command line this JVM was started with**. On Windows a JVM cannot read its own command line back (a space in the game directory breaks `ProcessHandle`), so the raw line is read with JNA's `GetCommandLineW` and re-executed verbatim through `CreateProcessW`; elsewhere `ProcessHandle` is used. When a launcher hides the command line the game says it cannot restart automatically and leaves the restart to you. JNA 5.14.0 is a `compileOnly` dependency — it is not bundled in the jar.

**Both READMEs carry the support table** (OptiFabric version → Minecraft version → required OptiFine build) and state that it lists the newest **final** build, using the newest preview only where a release has no final build yet; `release\version.ps1 -CheckSupport` fails when a README table, `release\notes\mc<MC>.md` and the mod's own table disagree. Every third-party mirror link has been removed from the README and `docs/DEVELOPMENT.md`: downloads only ever go through the official site.

### New in 2.1.0 — the crash report explains itself, and one more mod gets its injection target back

**The crash this project is asked about most now diagnoses itself.** `NoClassDefFoundError: Could not initialize class net.optifine.reflect.Reflector` happens because *another* mod's mixin failed to apply to a class OptiFine patches: OptiFine's crash reporter reads its own version through `Reflector`, that read loads the game class, and the class can no longer finish loading — so the report ends at `Reflector` and never names the mod. When the cause chain carries that signature (or `Mixin transformation of` / `Mixin apply for mod`), the report now gets a short `OptiFabric: OptiFine / mixin conflict` section: what happened, that Mixin only names the mod with **`-Dmixin.debug=true`**, the three families that fail this way, and what to do (remove that mod, or play without OptiFine). It adds nothing when the signature is absent, and the whole addition is wrapped so a diagnostic can never replace the report it was meant to explain. The same recipe is in both READMEs.

**SophisticatedCore / Sophisticated Backpacks no longer crash on startup** (measured on 1.21.1 with that pack): its `client.ParticleEngineMixin` asks for `class_702.method_34020`, which OptiFine's recompile folded into `lambda$addBlockDestroyEffects$13`, so the patched class has no such method and `@ModifyArgs` with `require = 1` fails the whole class. The vanilla body is restored as the injection target — nothing in the patched classes refers to it, so it stays dead code.

**Two cases stay broken by design**, both on 1.21.1, neither fixable from here (the table is in [`docs/DEVELOPMENT.md`](DEVELOPMENT.md)): **CarryOn** 2.2.6.13 injects at a call site inside `WorldRenderer` that OptiFine replaced with its own `ParticleManager.render` overload — zero injection points, and faking one would draw the particles twice; **ShoulderSurfing** 5.2.0 expects an exact call-site count in `Camera`, which OptiFine's rewrite of that class changes. Both need the mod author to retarget or relax the matcher.

**`LocalSlotLayoutFix` no longer guesses.** A wide (`long`/`double`) candidate used to be given one slot — two of them could even share a destination, which is invalid bytecode — and when only one side carried a local variable table the two layouts were compared in different representations (`Z` against `I`). The slot type now comes from the method's own table, comparison is by category whenever either side came from the opcode fallback, and the two "gave up" paths say which cause fired. Move sets are unchanged from 2.0.0 on every release.

**All ten jars of this line are 2.1.0**: 1.21, 1.21.1, 1.21.3, 1.21.4, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10 and 1.21.11. Nothing was removed, and the mod id is unchanged (`optifabric_reforged`), so upgrading from 2.0.0 is a jar swap.

### New in 2.0.0 — the fork's own mod id, and Architectury support

**The mod id is now `optifabric_reforged`** (display name *OptiFabric Reforged* — the identity the 26.x line already ships). Upgrade note: **delete any older `OptiFabric-<version>+mc1.21.x.jar` before dropping this one in**. With both ids present Fabric loads both copies and OptiFine gets patched twice.

The rename is what makes **Architectury** work. Its metadata declares `breaks: optifabric <1.13.0`, which Fabric Loader enforces before anything runs — and that rule is not ours to change, so this line changed its own id instead. With `optifabric_reforged` the rule simply no longer matches and the two mods load together, with no `fabric_loader_dependencies.json` involved.

Behind that declaration was a genuine bytecode conflict, and it is fixed too. With the declaration neutralised the game still died on its first frame:

```
InjectionError: LVT in net/minecraft/class_757::method_3192 has incompatible changes at opcode 601
```

Mixin hands a method's locals to an `@Inject` handler **strictly by slot order** (`CallbackInjector` takes the first `extraArgs` non-null locals from `getFirstNonArgLocalIndex` upwards), and OptiFine's build inserts two floats of its own — `guiFarPlane` and `guiOffsetZ` — in the middle of `GameRenderer.render`, pushing the game's `Matrix4f`, `Matrix4fStack` and `GuiGraphics` one or two slots up. The order the handler was compiled against no longer lines up. The new `LocalSlotLayoutFix` moves the locals that have no counterpart in the game's layout to the end of the slot range (7→15 and 10→16 on 1.21.1) and rewrites the local variable table — that table is what Mixin reads the locals from, and no writer in the pipeline regenerates it — while the stack map frames are recomputed by the frame-computing writer that every changed class goes through.

**The post-effect modernisation is now gated by what the release's parser understands.** OptiFine's FXAA post-chain files are rewritten into the 1.21.6+ shape (`vertex_shader` / `fragment_shader`, plus `BlitConfig`) only when the game's own `post_effect/*.json` already uses those keys, and the texture repair only runs where the `GpuTexture` API exists (1.21.6 and up) — triggering on file content alone rewrote 1.21.3 / 1.21.4 into a shape their parser cannot read, which silently disabled anti-aliasing on both.

**All ten jars of the 2.0.0 release were 2.0.0**: 1.21, 1.21.1, 1.21.3, 1.21.4, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10 and 1.21.11.

Measured on 1.21.1 with `OptiFine_1.21.1_HD_U_J1`, fabric-api 0.116.17 and architectury 13.0.11, no config file: 56 mods load, the fixer reports the slot move, a world opens, and the client stays up with no injection error, no `VerifyError` and no crash report.

### Previously in 1.1.2 — anti-aliasing, on every release from 1.21.3 up

**1.1.2 covers eight jars** — 1.21.3, 1.21.4, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10 and 1.21.11 (1.21 and 1.21.1 stay at 1.1.0: OptiFine only ships the old chain location for those, which the repair never touched). On 1.21.11 this jar behaves exactly like 1.1.1, which already had this fix.

Anti-aliasing was broken on all of them. The pipeline deleted OptiFine's own `post_effect/fxaa_of_{2,4}x.json`, while that chain id (`minecraft:fxaa_of_2x`) is resolved by the **game's post-chain registry** from `post_effect/` — OptiFine patches `ShaderManager` to register it there. So every resource reload logged `Resource not found: minecraft:post_effect/fxaa_of_2x.json`, and touching anti-aliasing (or picking a shader pack — both reload the shaders) ended in `Failed to load post chain: minecraft:fxaa_of_2x` and a "failed to reload resources" prompt. 1.1.2 leaves OptiFine's files alone. On 1.21.9 and 1.21.10 there was a second, older bug behind the black screen: from 1.21.9 the game draws post-effect passes as an attribute-less fullscreen triangle (`gl_VertexID`), while OptiFine's `fxaa_of_*.vsh` still read the `Position` vertex attribute — the pipeline rewrites those two files (1.21.11's own build already ships the fixed shape, and 1.21.3–1.21.8's pipeline still has the attribute, so both are skipped).

Measured, not assumed: all eight releases were run with the shipped jar, anti-aliasing at 2x and a shader pack loaded, and the log line `Resource not found: minecraft:post_effect/fxaa_of_*` is **0** on every one of them. The offline verification below was re-run for each of the eight.

This is versioned per artifact: 1.21.3 – 1.21.11 are 1.1.2, 1.21 and 1.21.1 keep their published 1.1.0 jars, and `v1.1.0` / `v1.1.1` stay as published.

### Requirements

| | |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | 0.19.5 or newer |
| Java | 21+ (tested on Java 25) |
| Side | client |
| OptiFine | your own 1.21.11 build (tested: HD_U J9, build `20260205-175838`) |
| Fabric API | optional — supported, tested with 0.141.6+1.21.11 |

### Install

1. Install a 1.21.11 Fabric client (Loader 0.19.5+).
2. Put `OptiFabric-2.2.8+mc1.21.11.jar` **and** your OptiFine 1.21.11 jar into that version's `mods/` folder. Do **not** run OptiFine's installer — dropping the file in is enough. **When upgrading: delete any older `OptiFabric-<version>+mc1.21.11.jar` first** — the mod id changed in 2.0.0, and two ids in `mods/` load both copies.
3. Start the game with the **Fabric** profile. The first launch spends a few seconds patching and remapping (cached afterwards under `<game dir>/.optifine/<version>/`).

### What it took for 1.21.11

OptiFine's 1.21.11 build ships its class patches as xdelta diffs, and its recompiled classes no longer line up with what Fabric API injects into. Every conflict below was found through a real crash and traced down to the bytecode; the fixers are in `patcher/fixes` and the whole story is in [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md):

- **Remapping needs the game on the classpath** — member mappings are recorded per declaring class, so overrides in subclasses silently kept OptiFine's names (35 unmapped methods in `class_1308` alone → 281 broken abstract contracts, 254 lost virtual overrides).
- Injection targets OptiFine's recompiler erased (inlined helpers, renamed lambdas with a different signature) — the vanilla method bodies are restored so Fabric's injections have a target again.
- A Fabric hook that reads a render context OptiFine's pass structure never fills in — the hook is moved onto code that is never called, so the game stops crashing and the block outline is still drawn.
- The same for the **moving-blocks** renderer hook, whose caller lives in *another* class — those call sites are redirected too, otherwise the hook fires from the injected copy (this one crashed multiplayer after ~30 seconds).
- Fabric's renderer registry being empty while `contains_renderer` keeps Indigo away — an inert placeholder renderer is registered, which also stops the **F3 debug screen** from crashing.
- Item models failing to bake (every item texture missing), chunk rendering NPEs from OptiFine's region constructor, obfuscated fields with mismatching descriptors, same-typed synthetic `this$0`/`val$…` fields, and `ChunkOF` object creation replacing Fabric's injection point.
- Stack map frames were being recomputed for **every** patched class (the global override fixer always reports a change, so the patcher could not tell), and the merge degraded a local to `java/lang/Object` in classes as unrelated to Fabric as `ShoulderParrotFeatureRenderer` and `EntityRenderDispatcher` — the game rejects those with `VerifyError: Bad type on operand stack in putfield`. Classes no fixer modifies now keep OptiFine's own frames, and recomputation only happens where a fixer really changed the class.

### Verified

- Offline, every class is loaded and linked in a single loader (the same way the game does it) and checked with the JVM verifier plus an ASM data-flow verifier: **570/570 patched game classes** and **874/874 OptiFine classes**, 0 failures, 0 verifier problems.
- Mixin member references, `@At` injection points, `@Shadow` members and uninitialised-field scans: clean (the only 4 `@At` misses belong to the **disabled** Indigo).
- In game: startup, title screen, singleplayer, **multiplayer server**, block/chunk/item rendering, **shaders** (`ComplementaryReimagined` loaded), F3 debug screen — 0 `[ERROR]` lines and no crash report in the final session.

### Known limits (deliberate)

- **Conflicts with Sodium** (declared); `no_fog`, `thallium`, `xradiation`, `ryoamiclights` are declared incompatible.
- **Mods that rely on FRAPI/Indigo** do not get Indigo's custom rendering — OptiFine renders the terrain. Fabric's renderer API exists but is backed by a placeholder (F3 shows `OptifineRendererPlaceholder`), and it explains itself if something really tries to draw through it.
- Two Fabric API hooks are intentionally inert: the `BEFORE_BLOCK_OUTLINE` event does not fire (the outline is still drawn), and the Fabric renderer's moving-block hook is bypassed (moving blocks are drawn by the vanilla path).
- OptiFine cannot see resources inside Fabric mods (`Unknown resource pack type: ...ModNioResourcePack`) — a limitation on OptiFine's side.
- Shader packs log warnings such as `Unknown macro value: IRIS_VERSION` or `ParseException: Model variable not found: …`; those come from the shader pack.

### Files

| File | SHA-256 |
|---|---|
| `OptiFabric-2.2.8+mc1.21.11.jar` (941087 bytes) | `1EAA1A7E3B8A033D7D7147B15C3F81F77AB986D464880FCD08EBACCC821BE600` |

Other Minecraft releases OptiFine ships a build for — 1.21, 1.21.1, 1.21.3, 1.21.4, 1.21.6, 1.21.7, 1.21.8, 1.21.9 and 1.21.10 — come out of this same repository root (one Gradle project, no `v1.21.x` subproject) with `.\gradlew build "-Pmc=<version>"`, and each of them passes the same offline verification (see [`docs/DEVELOPMENT.md`](DEVELOPMENT.md)).

Full changelog: [`CHANGELOG.md`](CHANGELOG.md) · Usage, troubleshooting and known issues: [`README.md`](README.md) · Verification tooling: [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md)

### Credits and license

A port of [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric) by Modmuss50 and Chocohead, licensed under **MPL-2.0**. OptiFine itself is neither included nor redistributed; get it from the official site.

---

## 中文摘要

把 OptiFine 接进 Minecraft **1.21.11** 的 Fabric。把本 jar 与自备的 `OptiFine_1.21.11_HD_U_J9.jar` 一起放进 `mods/`,用 Fabric 版本启动即可(**不需要**先运行 OptiFine 安装器);首次启动多花几秒做补丁+重映射,之后走缓存。

- **2.2.1 的修复**:OptiFabric 换掉游戏类之后,不再让 Mixin 继续用**替换之前**缓存的类元数据 —— 缓存与 OptiFine 改写后的字节
  不一致时,`@ModifyVariable` / 局部变量捕获会在加载期抛 `LVTGeneratorError` 让整个类变换失败(Fabric 只报
  `Mixin transformation of net.minecraft.class_757 failed`),而这个异常抛在 `require` / `expect` 之前,模组侧无法绕过;
  现在替换完类就丢掉这些类的缓存条目,让 Mixin 按真正拿到的字节重建(1.21.1 实测日志 `Dropped 47 of 425 Mixin class
  metadata entries`;2.2.0 的 jar 6 秒崩溃,本版进标题界面、进存档,光影包编译 27 个世界内程序,无注入错误、无崩溃报告);
- **2.2.0 的改动**:`mods/` 里没有 OptiFine(或装的是比支持表更旧的**预览版**)时不再默默启动 —— 标题界面打开前会弹出说明界面,
  写明该 MC 版本需要的构建与文件名,`下载 OptiFine` 从 OptiFine **官网**的两步流程取回它(不带、也不回退任何第三方镜像;
  失败就如实报原因并换成 `打开官网下载页` + `重新检查`,你放好 jar 再点重新检查);地址栏可以换成你自己的地址;
  下载成功后**先问一句**`是否重启游戏使 OptiFine 生效?`,`立即重启` 用启动本进程时的那条命令行重开游戏再结束自己
  (Windows 用 JNA 读原始命令行 —— 游戏目录带空格时 JVM 读不回自己的命令行);完全没装时每次启动都会提示,
  装了但较旧则只在**预览版**较旧时提示一次,点`仍要继续`后记进 `config/optifabric-mismatch-ack.txt` 不再重复;
- **2.1.0 的改动**:崩溃报告多出一节 `OptiFabric: OptiFine / mixin conflict` —— 最常见的
  `NoClassDefFoundError: … net.optifine.reflect.Reflector` 是**别的模组的 mixin 没能注入到 OptiFine 改写过的类里**,
  报告现在说明原因,并给出用 `-Dmixin.debug=true` 让 Mixin **点名**模组的做法(README 的"支持与排查"同);
  同时**修好 SophisticatedCore / Sophisticated Backpacks 装上就启动崩**(1.21.1 实测,补回 `class_702.method_34020`
  作为注入目标);**CarryOn 与 ShoulderSurfing 仍故意不修**(注入点在 OptiFine 改写后的字节码里对不上,只能模组侧改);
- **2.0.0 的改动**:**mod id 改成 `optifabric_reforged`(显示名 OptiFabric Reforged)** —— 升级前请先删掉旧的
  `OptiFabric-<版本>+mc1.21.x.jar`,两个 id 同时存在会**同时加载两份**、OptiFine 被打两遍补丁;同时**修掉
  Architectury 崩在第一帧的局部变量冲突**(`GameRenderer.render` 里 OptiFine 插入的两个局部变量顶高了原版槽位,
  Mixin 按槽位顺序捕获即失败),1.21.1 + architectury 13.0.11 实测可进世界且不需要任何配置文件;
  2.0.0 那次**十个产物统一为 2.0.0**(1.21 – 1.21.11)
- 历史:1.1.2 修好了抗锯齿(覆盖 1.21.3 – 1.21.11 八个产物),1.21 与 1.21.1 当时停在 1.1.0
- 需要:Fabric Loader ≥ 0.19.5、Java 21+、客户端;Fabric API 可选(实测 0.141.6+1.21.11)
- 离线校验:被补丁的 **570** 个游戏类与 OptiFine 自身的 **874** 个类全部通过 JVM + ASM 双向校验
- 真机已验证:启动、主界面、单人、**多人服务器**、方块/区块/物品渲染、**光影**、F3 调试屏,`[ERROR]` 0 条
- 已知限制:与 Sodium 冲突;依赖 FRAPI/indigo 的模组不再有 indigo 渲染;`BEFORE_BLOCK_OUTLINE` 事件与移动方块的 FRAPI 钩子被有意停用(渲染本身正常)
- **不包含、也不分发 OptiFine 本体**
