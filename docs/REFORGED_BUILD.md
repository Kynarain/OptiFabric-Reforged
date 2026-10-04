# The alternative 1.20.6 build (`optifabric_reforged`)

> 本文件只讲 **`wip/1.20.6-reforged` 分支上的第二个 1.20.6 产物**:同一份源码,另一个 mod id,外加一个
> **C2ME 兼容垫片**。已发布的 1.20.6 线(`main`)没有改动,mod id 仍是 `optifabric`。中文在[下半部分](#中文)。
>
> This file documents the **second 1.20.6 product**, built on the branch `wip/1.20.6-reforged`: the same
> sources as the published line, published under its own mod id, plus a **C2ME compatibility shim**. The
> published line (`main`) is unchanged and still publishes `id: optifabric`. The Chinese half is [below](#中文).

## English

### What this build is

| | published line (`main`) | this branch (`wip/1.20.6-reforged`) |
|---|---|---|
| jar | `OptiFabric-1.1.3+mc1.20.6.jar` | `OptiFabric-Reforged-1.1.3-reforged+mc1.20.6.jar` |
| mod id | `optifabric` | `optifabric_reforged` |
| display name | `OptiFabric` | `OptiFabric Reforged` |
| `provides` | — | **— (deliberately, see below)** |
| our own `depends` / `conflicts` / `breaks` | unchanged | **identical** |
| C2ME compatibility shim (`C2meCompat`) | — | **this branch only** (see [The C2ME shim](#the-c2me-shim)) |
| everything else (classes, `mappings/mappings.tiny`, mixin config, icon) | — | **identical except that one new class** |

The two jars differ in the metadata entries (`fabric.mod.json`: id, name, description; and the renamed
`LICENSE.txt_<archives_base_name>`) plus the one added class for the shim. Measured on this branch against the
published 1.1.2 jar: 69 entries each, 67 byte-identical, 1 differing (`fabric.mod.json`), 1 renamed
(`LICENSE.txt_OptiFabric` → `LICENSE.txt_OptiFabric-Reforged`). Against the published **1.1.3** jar (which
already carries the `class_3898` rename fix) this build adds `C2meCompat.class` + `C2meCompat$Inspection.class`,
changes `Optifabric.class` (it calls the shim first) and `fabric.mod.json`, and differs in nothing else.

Build it with `.\gradlew build` on this branch; on `main` the same command still produces the published jar.

### Why an id change

Fabric Loader matches `depends`, `breaks` and `conflicts` **by mod id**. c2me's 1.20.6 build ships

```json
"breaks": { "tic_tacs": "*", "optifabric": "*" }
```

so with the published jar Loader stops during mod resolution, before Minecraft starts:

```
[main/INFO]: Immediate reason: [NEG_HARD_DEP c2me 0.2.0+alpha.11.100+1.20.6 {breaks optifabric @ [*]}, ...]
[main/ERROR]: Incompatible mods found!
```

Another mod's metadata is not ours to edit, so the only lever is our own id. This build gives it up.

### Who should use it

Use it only if **all** of these hold:

* you need c2me — or another 1.20.6 mod whose metadata refuses the id `optifabric` — **and** OptiFine in the same instance;
* that combination is worth more to you than the guards listed under "What stops being refused";
* you accept that this is a **separate product**: our own declarations are unchanged, but no third party's declaration against `optifabric` fires any more.

If you do not run such a mod, use the published build. The published line is the supported one and gets fixes
first; this branch exists to unblock one combination.

> **Read the table at the end of this file.** With the shim in place the combination now **reaches a world**:
> the 1.1.3 build of this branch turns C2ME's `c2me-threading-worldgen` module off by itself (one key in
> `config/c2me.toml`, backed up first), and entering a world is then clean — 0 errors in the measured runs. What
> that costs, and the two things it does *not* fix, is in [The C2ME shim](#the-c2me-shim); in short: you give up
> C2ME's threaded world generation, and the **first launch after installing changes the config and restarts the
> game once by itself** (C2ME reads its config before any mod's `preLaunch` code runs — see that section).
> The id change alone was never enough: it removes the *refusal*, not the *incompatibility*.

### The C2ME shim

Only this branch ships it, and only when the mod `c2me` is present; without C2ME this build behaves exactly
like the published one. `kynarain.cn.optifabric.mod.C2meCompat` runs as the first thing in the `preLaunch`
entrypoint and touches **one key of one file**: `[threadedWorldGen] enabled` in `config/c2me.toml`.

| | |
|---|---|
| file | `<gameDir>/config/c2me.toml` (read-modify-write, every other byte kept) |
| key | `[threadedWorldGen]` → `enabled` |
| value written | `false` — unless it is **already `false`** (then nothing is written at all) or an **explicit `true`** (then it is left alone and a warning is logged: that combination crashes on world load) |
| absent file / key | the file is created (`version = 3` + the section + the key) or the key is inserted in that section; next to `[threadedWorldGen]` nothing else is added |
| backup | `config/c2me.toml.optifabric-backup`, written **once**, never overwritten, before the first change |
| restart marker | `config/c2me.toml.optifabric-relaunched` — allows exactly one automatic restart, and is deleted again as soon as the config already reads `false` |
| opt-out | `-Doptifabric.noC2meCompat=true` leaves C2ME's config completely alone (and says so in the log) |
| logging | `[OptiFabric]` lines on the launcher console: what was found, what was written, where the backup is, what is given up, and how to opt out |

**Why a `preLaunch` write is not enough on its own.** Loader prepares every mod's mixin configurations in
`FabricMixinBootstrap.init` and invokes the `preLaunch` entrypoints *afterwards* (that order is visible in
`Knot.init` of Fabric Loader 0.19.5). C2ME reads `config/c2me.toml` inside its mixin plugin's `onLoad`, i.e. in
that earlier step — so by the time this shim runs, C2ME has already resolved the module for the launch in
progress. The log order shows it plainly:

```
[main/INFO]: Initializing com.ishland.c2me.threading.worldgen.mixin
[main/INFO]: Config threadedWorldGen.enabled changed from true to true
[OptiFabric] Preparing OptiFine OptiFine_1.20.6_HD_U_J1_pre18, this may take a few seconds
```

Without a restart the user would therefore see the config fixed and the world still failing once (the crash the
shim exists to prevent). So when the shim has to change the value **and** C2ME's own resolved value for this
launch is on (read back from `com.ishland.c2me.threading.worldgen.ModuleEntryPoint.enabled`, with the
`(Default: …)` comment C2ME writes above the key as a fallback), it restarts the game once with the same command
line and ends the current process before it can reach a world. The restarted process finds `enabled = false`,
logs `nothing to do (file left untouched)`, and enters worlds. Three guards keep that from looping: the value
must not already be resolved to off, the write must be re-read from disk and really say `false`, and the marker
file allows only one automatic restart.

**What is lost, and what is not fixed.**

* Given up: C2ME's `c2me-threading-worldgen` — parallel/async chunk-generation scheduling, plus its
  status-cancellation and progress-logger paths. C2ME's other **19** modules stay active.
* Not fixed, and not fixable from this side: the member that module needs. OptiFine's own bytes for
  `class_3898.method_17224` disagree with themselves (descriptor vs. the values the registration site pushes vs.
  the slots the body reads), so no reordering can present the game's signature *and* stay self-consistent. This
  is why the module is turned off instead of repaired; the analysis is in `collision-1206\REPORT.md` and
  `c2me-modules\REPORT.md`.
* Worth knowing: that module's default is **computed, not fixed** — `enabled` defaults to
  `globalExecutorParallelism >= 3`, which depends on CPU count and `-Xmx`. On a small heap C2ME turns the module
  off by itself and you would never see the crash. That is luck, not compatibility; the shim's write makes the
  outcome deterministic. A user who forces `enabled = true` **will** crash on world load — the shim respects that
  choice and only warns.
* C2ME rewrites `config/c2me.toml` at startup (resolved defaults, comments, key order). It keeps an explicit
  `false`; the shim therefore does not have to rewrite anything on later launches.

### Only one of the two may be installed

The two jars are two different mods to Loader (different ids), so Loader's dependency solver accepts both. Mixin
does not: both jars ship a mixin config with the same name, and the launch dies immediately with

```
[main/ERROR]: Uncaught exception in thread "main"
java.lang.RuntimeException: Non-unique Mixin config name optifabric.mixins.json used by the mods optifabric and optifabric_reforged
```

(measured on this branch, with no c2me present). That is at least a clear error rather than a silent double
setup — but do not rely on it: delete the other jar when you switch. And if some other mod still declares
`breaks: optifabric` (c2me does), Loader refuses the pair even earlier, with `NEG_HARD_DEP`.

### Why there is no `provides: ["optifabric"]`

Declaring `provides` would be the obvious way to keep satisfying a hypothetical `depends: optifabric` — and it
does not work. Loader 0.19.5 indexes a mod in `ModPrioSorter.sort` under **its own id and every id in
`provides`**, and `ModSolver` builds the `breaks` constraint from that same map (`case BREAKS:` → `impliesNot`
→ `NEG_HARD_DEP`). A renamed jar that provides `optifabric` therefore re-arms c2me's refusal. Measured on
Loader 0.19.5, scratch metadata surgery on this line:

| # | jar metadata | result |
|---|---|---|
| a | `id: optifabric_reforged`, no `provides` | `Loading 80 mods:` → title screen |
| b | `id: optifabric_reforged` + `"provides": ["optifabric"]` | `NEG_HARD_DEP c2me 0.2.0+alpha.11.100+1.20.6 {breaks optifabric @ [*]}` → refused |

So the id had to be *given up*, not aliased. The consequence is that this jar satisfies no
`depends: {"optifabric": ...}`. On 1.20.6 that costs nothing today: of **6,423** jar files scanned across the
analysis caches, **0 declare `depends`, `recommends` or `suggests` and 0 declare `provides`** on the id — the 86
that do name it use `breaks` (84) or `conflicts` (2). It would stop being free the day a mod adds such a
`depends`; that mod would then need the published jar.

### What stops being refused

Our own declarations are untouched — they are about *other* mods and they still fire. What changes is the other
direction: every third-party declaration that names `optifabric` stops matching this jar. On 1.20.6 that is
**10 mods / 13 distinct mod+version builds** — 41 cached jar-builds if every copy in the analysis caches is
counted separately:

| mod | 1.20.6-era build(s) | declaration | cached copies |
|---|---|---|---|
| c2me | `0.2.0+alpha.11.93`, `.98`, `.100` (`+1.20.6`) | `breaks: {"optifabric": "*"}` | 10 |
| Sodium | `0.5.11+mc1.20.6` | `breaks: {"optifabric": "*"}` | 7 |
| Iris | `1.7.0+mc1.20.6`, `1.7.2+mc1.20.6` | `breaks: {"optifabric": "*"}` | 5 |
| ImmediatelyFast | `1.3.0+1.20.6` | `breaks: {"optifabric": "*"}` | 5 |
| Lithium | `0.12.5` | `breaks: {"optifabric": "*"}` | 4 |
| Enhanced Block Entities | `0.10.1+1.20.6` | `breaks: {"optifabric": "*"}` | 3 |
| Entity Model Features | `3.0.17` | `breaks: {"optifabric": "*"}` | 2 |
| Entity Texture Features | `7.0.13` | `breaks: {"optifabric": "*"}` | 2 |
| Architectury | `12.1.4` | `breaks: {"optifabric": "<1.13.0"}` | 2 |
| Embeddium | `0.3.20+mc1.20.6` | `breaks: {"optifabric": "*"}` | 1 |

Three more builds declare a `minecraft` range that also covers 1.20.6 although they target a newer release
(`entity_model_features 3.3.9` and `entity_texture_features 7.2.4`, both `"*"`; `vmp 0.2.0+beta.7.172+1.21.1`,
`">=1.20.2-beta.2"`), so their entries stop firing too if a user installs one of them on this release.

For c2me that is the point of the build. For the rest it is the price: each of those entries was written
because the combination was seen to go wrong, so with this jar the combination is no longer refused and
whatever motivated the entry becomes reachable again. `sodium` is the clearest case, because both sides declare:
Sodium declares `breaks` on us, and we declare `conflicts` **and** `breaks` on Sodium — this build keeps our
half and loses theirs.

### Mods that detect OptiFabric at runtime

Some mods never declare anything about us; they ask Loader `isModLoaded("optifabric")` at runtime, or key a
Mixin config plugin on that flag. Those stop seeing this jar as well. A byte-level scan of every `.class` in
every cached jar (14,006 jars, 2,535,896 class entries, 9.8 GB decompressed) found 8 third-party mods with the
literal inside a class file; three of them are relevant to 1.20.6:

| mod | build | class | how it names us |
|---|---|---|---|
| FpsReducer | `1.20.5-2.8` | `bre2el/fpsreducer/MixinConfigPlugin` | `hasOptiFabric` flag |
| Pehkui | `3.8.3+1.14.4-1.21` | `virtuoel/pehkui/mixin/PehkuiMixinConfigPlugin` | `OPTIFABRIC_LOADED` flag |
| Essential | `1.5.0.1` | `gg/essential/mixins/Plugin` | checks the **class** `me.modmuss50.optifabric.mod.OptifineInjector` |

The other five are newer-release builds (Bedrockify `1.21`, Figura `1.21.1`, Inventory Profiles Next `1.21.1`,
Physics Mod `1.21.1`, ReplayMod `1.21`). Note that Essential and ReplayMod look for upstream's
`me.modmuss50.optifabric.*` class names, which this 1.20.6 port has never shipped (it uses
`kynarain.cn.optifabric`), so those two cannot detect it with either id.

### Trade-offs, in one place

* **+** c2me (and any other mod that refuses `optifabric`) is no longer refused, and with the 1.1.3 shim the
  combination **reaches a world** with 0 errors: C2ME's `c2me-threading-worldgen` is switched off for you (one
  key in `config/c2me.toml`, backed up first).
* **−** that module's function is genuinely lost: parallel/async chunk-generation scheduling plus its
  cancellation and progress-logger paths. C2ME's other 19 modules stay active. The module cannot be repaired
  from this side — OptiFine's bytes for `class_3898.method_17224` are self-inconsistent (see
  `collision-1206\REPORT.md`), and a shim that only changed the descriptor would hand C2ME a member that lies.
* **−** the first launch after installing the shim changes C2ME's config and **restarts the game once by
  itself**, because C2ME reads that config before any `preLaunch` code runs. Later launches are ordinary.
* **−** 13 third-party refusals (10 mods) stop firing; keeping those mods apart is now the user's job.
* **−** a third party's `depends: optifabric` is not satisfied (none exist on 1.20.6 today).
* **−** `provides: optifabric` cannot be added later without re-arming c2me (see above).
* **±** the jar carries its own id, version and name, so mod lists, crash reports and support threads say
  `OptiFabric Reforged 1.1.3-reforged+mc1.20.6`. That is deliberate: it has to be obvious which of the two is
  installed.

## 中文

### 这是什么

| | 已发布线(`main`) | 本分支(`wip/1.20.6-reforged`) |
|---|---|---|
| jar | `OptiFabric-1.1.3+mc1.20.6.jar` | `OptiFabric-Reforged-1.1.3-reforged+mc1.20.6.jar` |
| mod id | `optifabric` | `optifabric_reforged` |
| 显示名 | `OptiFabric` | `OptiFabric Reforged` |
| `provides` | — | **—(有意为之,见下)** |
| 我们自己的 `depends` / `conflicts` / `breaks` | 不变 | **完全相同** |
| C2ME 兼容垫片(`C2meCompat`) | — | **只有这一支有**(见[下面那一节](#c2me-兼容垫片)) |
| 其它一切(类、`mappings/mappings.tiny`、mixin 配置、图标) | — | **相同,只多这一个新类** |

两个 jar 的差别在元数据(`fabric.mod.json` 的 id、名称、描述,以及改名的 `LICENSE.txt_<archives_base_name>`)
加上垫片这一个新类。本分支实测:对已发布的 **1.1.2** 逐项比对,两边各 69 个条目,**67 个逐字节相同**,
1 个不同(`fabric.mod.json`),1 个改名(`LICENSE.txt_OptiFabric` → `LICENSE.txt_OptiFabric-Reforged`);
对已发布的 **1.1.3**(它已经带了 `class_3898` 改名修复)比对,这个产物多出 `C2meCompat.class` 与
`C2meCompat$Inspection.class`,改了 `Optifabric.class`(它先调垫片)与 `fabric.mod.json`,其余全部相同。

在本分支上 `.\gradlew build` 出的是这个产物;在 `main` 上同一条命令出的仍是已发布的那一个。

### 为什么要改 id

Fabric Loader 的 `depends`、`breaks`、`conflicts` **都按 mod id 匹配**。c2me 的 1.20.6 构建里写着

```json
"breaks": { "tic_tacs": "*", "optifabric": "*" }
```

所以装了已发布 jar 时,Loader 在**模组解析阶段**就停下,游戏还没起来:

```
[main/INFO]: Immediate reason: [NEG_HARD_DEP c2me 0.2.0+alpha.11.100+1.20.6 {breaks optifabric @ [*]}, ...]
[main/ERROR]: Incompatible mods found!
```

别人的元数据不是我们能改的,唯一的杠杆就是我们自己的 id。这个产物就是把 id 让出去。

### 谁该用它

只有下面**全部**成立时才用:

* 你需要在同一个实例里同时用 **c2me**(或别的元数据里拒绝 `optifabric` 的 1.20.6 模组)**和** OptiFine;
* 这个组合对你的价值,高于「哪些声明不再生效」那一节列出的代价;
* 你接受它是**另一个产物**:我们自己的声明一个没动,但第三方针对 `optifabric` 的声明**一条都不再命中**。

不跑这类模组就用已发布的那一个。**已发布线才是主线**,修复先给它;这个分支只为解开某一个组合。

> **请看文末那张表。** 装上垫片之后,这个组合**能进世界**了:1.1.3 的垫片会自己把 C2ME 的
> `c2me-threading-worldgen` 关掉(`config/c2me.toml` 里的一个键,先备份原文件),进世界 0 错误。
> 代价、以及它**没有**修好的两件事,写在[「C2ME 兼容垫片」](#c2me-兼容垫片)那一节;一句话:
> 你放弃的是 C2ME 的线程化世界生成,而且**装好后的第一次启动会改配置并自己重启一次游戏**
> (C2ME 读配置早于任何模组的 `preLaunch`,那节里有证据)。**只改 id 从来不够** —— 改 id 去掉的是**拒载**,
> 不是**不兼容**。

### C2ME 兼容垫片

只有这一支有,而且只在装了 `c2me` 时动手;没有 c2me 时它的行为与已发布产物完全一样。
`kynarain.cn.optifabric.mod.C2meCompat` 在 `preLaunch` 入口点的**第一件事**里运行,只碰**一个文件的一个键**:
`config/c2me.toml` 的 `[threadedWorldGen] enabled`。

| | |
|---|---|
| 文件 | `<游戏目录>/config/c2me.toml`(读—改—写,其余每一个字节保持原样) |
| 键 | `[threadedWorldGen]` 下的 `enabled` |
| 写入的值 | `false`;但如果**本来就是 `false`**,就**一个字节都不写**;如果是**显式 `true`**,则**不动**并打印警告(那个组合进世界必崩) |
| 文件/键不存在 | 文件不存在就新建(`version = 3` + 该节 + 该键);有节没键就把键插进那一节;不往别处加东西 |
| 备份 | `config/c2me.toml.optifabric-backup`,**只写一次**、不覆盖,发生在第一次修改之前 |
| 重启标记 | `config/c2me.toml.optifabric-relaunched`,允许**一次**自动重启;配置已经是 `false` 时它会自动删掉 |
| 关闭开关 | `-Doptifabric.noC2meCompat=true` 完全不碰 C2ME 的配置(并在日志里说明) |
| 日志 | 启动器控制台的 `[OptiFabric]` 行:读到什么、写了什么、备份在哪、放弃了什么、怎么关掉 |

**为什么只靠 `preLaunch` 写一次不够。** Loader 在 `FabricMixinBootstrap.init` 里准备所有模组的 mixin 配置,
**之后**才调用 `preLaunch` 入口点(Fabric Loader 0.19.5 的 `Knot.init` 里就是这个顺序)。而 C2ME 是在它自己的
mixin 插件 `onLoad` 里读 `config/c2me.toml` 的 —— 那是更早的一步。所以垫片运行时,C2ME 已经为**本次**启动
解析好了那一项。日志顺序就是证据:

```
[main/INFO]: Initializing com.ishland.c2me.threading.worldgen.mixin
[main/INFO]: Config threadedWorldGen.enabled changed from true to true
[OptiFabric] Preparing OptiFine OptiFine_1.20.6_HD_U_J1_pre18, this may take a few seconds
```

如果只写文件不重启,使用者就会看到"配置已经改好了、但这一次进世界仍然崩"——正是垫片要避免的那一幕。所以:
当垫片**必须改值**、而 C2ME 本次解析出来的值**是开**的时候(从
`com.ishland.c2me.threading.worldgen.ModuleEntryPoint.enabled` 读回,读不到就退回 C2ME 写在键上方那行
`(Default: …)` 注释),它就用**同一条命令行**重启一次游戏,并在进入世界之前结束当前进程。重启后的进程看到
`enabled = false`,打印 `nothing to do (file left untouched)`,然后正常进世界。三重防循环:值本来就已经解析为关
就不重启;写完之后必须**重新读盘**确认真的是 `false`;标记文件只允许一次自动重启。

**放弃了什么、什么没修好。**

* 放弃:`c2me-threading-worldgen` —— 并行/异步的世界生成调度,以及它的状态取消与进度日志两条路径。
  C2ME 另外 **19** 个模块照常工作。
* 没修好,而且不是这一侧能修的:那一个成员。OptiFine 交给我们的 `class_3898.method_17224`
  **自己的字节就不自洽**(描述符、注册点压入的实参、方法体读的槽位三者对不上),没有任何重排能同时做到
  "交出游戏签名"与"三者自洽"。所以这里选择**关掉那个模块**而不是"修"它;分析见
  `collision-1206\REPORT.md` 与 `c2me-modules\REPORT.md`。
* 需要知道:那一项的默认值是**算出来的,不是写死的** —— `enabled` 的默认 = `globalExecutorParallelism >= 3`,
  而后者取决于 CPU 数与 `-Xmx`。**小堆**上 C2ME 会自己把模块关掉,你可能永远看不到那个崩溃 ——
  那是运气,不是兼容;垫片把它写成 `false` 是为了让结果确定。反过来,自己把它设成 `enabled = true` 的人
  **一定会**在进世界时崩:垫片尊重这个选择,只警告、不覆盖。
* C2ME 每次启动都会重写 `config/c2me.toml`(写回解析出来的默认值、注释与键顺序),但会保留显式的 `false`;
  所以之后每次启动垫片都不需要再写任何东西。

### 两个产物只能装一个

两个 jar 对加载器来说是两个不同的模组(id 不同),所以依赖求解器两个都收。**Mixin 不收**:两个 jar 里各有一份
同名的 mixin 配置,启动会立刻死在这里:

```
[main/ERROR]: Uncaught exception in thread "main"
java.lang.RuntimeException: Non-unique Mixin config name optifabric.mixins.json used by the mods optifabric and optifabric_reforged
```

(本分支实测,当时 `mods/` 里没有 c2me。)至少这是一个明确的报错,而不是两套 OptiFabric 悄悄互相踩 —— 但别指望它:
切换时请删掉另一个 jar。若还有别的模组声明 `breaks: optifabric`(c2me 就是),加载器会更早一步用 `NEG_HARD_DEP`
拒掉这一对。

### 为什么不写 `provides: ["optifabric"]`

用 `provides` 保住「别人 `depends` 我们」看起来是最自然的做法,但它**不成立**。Fabric Loader 0.19.5 的
`ModPrioSorter.sort` 会把一个模组**同时登记在它自己的 id 和它 `provides` 的每个 id 下**,而 `ModSolver` 正是
用这张表构造 `breaks` 约束(`case BREAKS:` → `impliesNot` → `NEG_HARD_DEP`)。所以改名的 jar 一旦
`provides: ["optifabric"]`,c2me 那条拒载立刻就回来了。Loader 0.19.5 上实测(本线元数据手术):

| # | jar 元数据 | 结果 |
|---|---|---|
| a | `id: optifabric_reforged`,无 `provides` | `Loading 80 mods:` → 主界面 |
| b | `id: optifabric_reforged` + `"provides": ["optifabric"]` | `NEG_HARD_DEP c2me 0.2.0+alpha.11.100+1.20.6 {breaks optifabric @ [*]}` → 拒载 |

也就是说 id 只能**让出去**,不能**别名化**。代价是:这个 jar **不满足**任何 `depends: {"optifabric": ...}`。
1.20.6 上今天不花钱 —— 扫过的 **6,423** 个 jar 里,**`depends` / `recommends` / `suggests` 0 条、
`provides` 0 条**;点名这个 id 的 86 个 jar 里,84 个用 `breaks`、2 个用 `conflicts`。哪天真有模组写
`depends`,它就只能配已发布的那一个。

### 哪些声明不再生效

我们自己的声明一条没动 —— 那些是针对**别的**模组的,照常生效。变的是反方向:第三方凡是点名 `optifabric`
的声明,都不再命中这个 jar。1.20.6 上共 **10 个模组 / 13 个"模组+版本"构建**(把分析缓存里每一份都算上是 41 个 jar 构建):

| 模组 | 1.20.6 时代构建 | 声明 | 缓存份数 |
|---|---|---|---|
| c2me | `0.2.0+alpha.11.93`、`.98`、`.100`(`+1.20.6`) | `breaks: {"optifabric": "*"}` | 10 |
| Sodium | `0.5.11+mc1.20.6` | `breaks: {"optifabric": "*"}` | 7 |
| Iris | `1.7.0+mc1.20.6`、`1.7.2+mc1.20.6` | `breaks: {"optifabric": "*"}` | 5 |
| ImmediatelyFast | `1.3.0+1.20.6` | `breaks: {"optifabric": "*"}` | 5 |
| Lithium | `0.12.5` | `breaks: {"optifabric": "*"}` | 4 |
| Enhanced Block Entities | `0.10.1+1.20.6` | `breaks: {"optifabric": "*"}` | 3 |
| Entity Model Features | `3.0.17` | `breaks: {"optifabric": "*"}` | 2 |
| Entity Texture Features | `7.0.13` | `breaks: {"optifabric": "*"}` | 2 |
| Architectury | `12.1.4` | `breaks: {"optifabric": "<1.13.0"}` | 2 |
| Embeddium | `0.3.20+mc1.20.6` | `breaks: {"optifabric": "*"}` | 1 |

另有三个构建声明的 `minecraft` 范围也覆盖 1.20.6,虽然它们瞄的是更新的版本(`entity_model_features 3.3.9`
与 `entity_texture_features 7.2.4`,都是 `"*"`;`vmp 0.2.0+beta.7.172+1.21.1`,范围 `">=1.20.2-beta.2"`)——
使用者若在这个版本上装它们,那些声明同样不再生效。

对 c2me 来说这正是本产物的目的;对其余几个来说这是代价:那些条目都是因为**真的出过问题**才写下的,
用了这个 jar 之后组合不再被拒,当初促成那条声明的问题就重新变得可达。`sodium` 是最清楚的例子 ——
两边都有声明:Sodium 在 `breaks` 里点了我们,我们同时在 `conflicts` 和 `breaks` 里点了它;
本产物保住的是我们这一半,丢掉的是它那一半。

### 运行期「认出我们」的模组

还有些模组什么都不声明,而是在运行期问加载器 `isModLoaded("optifabric")`,或者拿这个标志决定 Mixin 配置插件
加载哪些 mixin。这类模组同样会看不见这个 jar。把所有缓存 jar 的每一个 `.class` 逐字节扫过一遍
(14,006 个 jar、2,535,896 个类条目、解压后 9.8 GB),第三方共有 8 个模组的类文件里出现该字面量,
其中与 1.20.6 有关的是三个:

| 模组 | 构建 | 类 | 认我们的方式 |
|---|---|---|---|
| FpsReducer | `1.20.5-2.8` | `bre2el/fpsreducer/MixinConfigPlugin` | `hasOptiFabric` 标志 |
| Pehkui | `3.8.3+1.14.4-1.21` | `virtuoel/pehkui/mixin/PehkuiMixinConfigPlugin` | `OPTIFABRIC_LOADED` 标志 |
| Essential | `1.5.0.1` | `gg/essential/mixins/Plugin` | 查的是**类** `me.modmuss50.optifabric.mod.OptifineInjector`,不是 id |

另外五个都是更新版本的构建(Bedrockify `1.21`、Figura `1.21.1`、Inventory Profiles Next `1.21.1`、
Physics Mod `1.21.1`、ReplayMod `1.21`)。注意 Essential 与 ReplayMod 找的是上游的
`me.modmuss50.optifabric.*` 类名,而本 1.20.6 移植用的是 `kynarain.cn.optifabric` —— 这两个无论 id 是哪个都认不出它。

### 代价一览

* **+** c2me(以及其它拒绝 `optifabric` 的模组)不再被拒载;装上 1.1.3 的垫片后,这个组合**能进世界**,
  0 错误 —— 垫片替你关掉了 C2ME 的 `c2me-threading-worldgen`(`config/c2me.toml` 一个键,先备份)。
* **−** 那个模块的功能是真的没了:并行/异步的世界生成调度,以及它的状态取消与进度日志两条路径。
  C2ME 另外 19 个模块照常工作。这一项**不是这一侧能修的** —— OptiFine 给 `class_3898.method_17224` 的字节
  自己就不自洽(见 `collision-1206\REPORT.md`),只改描述符等于交给 c2me 一个"说谎"的成员。
* **−** 装好垫片后的**第一次启动**会改 C2ME 的配置并**自己重启一次游戏**,因为 C2ME 读配置早于任何
  `preLaunch` 代码;之后的启动都是普通启动。
* **−** 13 条第三方拒载(10 个模组)不再生效;把这些模组分开的责任回到使用者身上。
* **−** 第三方的 `depends: optifabric` 不再被满足(1.20.6 上目前没有这样的模组)。
* **−** 以后也不能补 `provides: optifabric`,那会把 c2me 的拒载重新武装起来(见上)。
* **±** jar 带自己的 id、版本与名称,所以模组列表、崩溃报告、求助帖里显示的是
  `OptiFabric Reforged 1.1.3-reforged+mc1.20.6`。这是有意的:必须一眼看出装的是哪一个。

## Measured on this branch / 本分支实测

Every arm below ran on my own copies of a 1.20.6 instance (never on the launcher's own `versions/`), Fabric
Loader 0.19.5, fabric-api `0.100.8+1.20.6`, OptiFine `preview_OptiFine_1.20.6_HD_U_J1_pre18.jar`, c2me
`0.2.0+alpha.11.100+1.20.6` where listed, and **a pristine `.optifine` cache per arm** (OptiFabric re-runs its
fixers on every launch, so a used cache would double-apply them - see `collision-1206\REPORT.md` section 2).
`-Xmx6G` everywhere: with this machine's 16 logical CPUs C2ME then computes `globalExecutorParallelism = 6`, so
`[threadedWorldGen] enabled` **defaults to true** and the failure is really exercised instead of being hidden by
a small heap. `PASS` = title screen **and** a world (`logged in with entity id` / `Saving chunks for level`),
not just the title screen.

Measured jars: `OptiFabric-1.1.3+mc1.20.6.jar` 733,927 B sha256 `7A657772…245010C`, and
`OptiFabric-Reforged-1.1.3-reforged+mc1.20.6.jar` 742,496 B sha256 `F2762EFC…73B5D1`.

| # | `mods/` | result |
|---|---|---|
| 1 | **published 1.1.3 build** + c2me + fabric-api + OptiFine | **REFUSED at mod resolution, 3 s**: `Immediate reason: [NEG_HARD_DEP c2me 0.2.0+alpha.11.100+1.20.6 {breaks optifabric @ [*]}, ROOT_FORCELOAD_SINGLE c2me …, ROOT_FORCELOAD_SINGLE optifabric 1.1.3+mc1.20.6]` → `Incompatible mods found!`; 18 log lines, never reaches `Loading 80 mods:`, no class is loaded |
| 2 | published 1.1.3 build, **no c2me** | **PASS — in world** (title 13 s, world 17 s): 966 log lines, **0** `/ERROR`, 581 `/WARN`, `Prepared 425 patched classes (1 skipped, 0 failed)` |
| 3 | **Reforged 1.1.3-reforged** + c2me + fabric-api + OptiFine, fresh instance with **no `config/c2me.toml`** | **PASS — in world** (title 15 s, world 18 s): 1,236 log lines, **0** `/ERROR`, 595 `/WARN`. Launch 1: the shim logs `config/c2me.toml [threadedWorldGen] enabled: "default" -> false`, backs the file up to `c2me.toml.optifabric-backup` (7,188 B), then `Restarting the game (Windows, CreateProcessW true)`. Launch 2 (same command line, C2ME's own log lines included): `Config threadedWorldGen.enabled changed from true to false` → `Disabling com.ishland.c2me.threading.worldgen.mixin`, the shim logs `already says [threadedWorldGen] enabled = false, nothing to do (file left untouched)`, then OptiFine setup and the world |
| 4 | arm 3 again, **same instance, nothing changed between the runs** (idempotence) | **PASS — in world**: `config/c2me.toml` is the **same file** before and after (7,184 B, sha256 `076F6D5D…`), the backup is still the original (sha256 `D70BE820…`), the relaunch marker is gone, the shim logs `nothing to do (file left untouched)`, **0** `/ERROR` |
| 5 | Reforged + c2me with **the shim opted out** (`-Doptifabric.noC2meCompat=true`), fresh instance | **TITLE ONLY**: the shim logs `switched off by -Doptifabric.noC2meCompat=true … left exactly as it is`, C2ME keeps the module on, and world load dies with `Mixin apply for mod c2me-threading-worldgen failed` → `InvalidInjectionException … method_17224` → `Mixin transformation of net.minecraft.class_3898 failed`; **4** `/ERROR`, 1 `Mixin apply … failed`, 3 `InvalidInjectionException`, 1 crash report. Config untouched (no backup, no marker) |
| 6 | Reforged + c2me with **an explicit `enabled = true`** in `config/c2me.toml` | **TITLE ONLY** — the choice is respected: the shim logs `says [threadedWorldGen] enabled = true, which is an explicit choice - leaving it alone` plus the crash warning, writes nothing (no backup is created), and the file still reads `true` afterwards; the world-load crash is the same as arm 5 (4 `/ERROR`, 1 mixin failure, 3 `InvalidInjectionException`, 1 crash report) |
| 7 | Reforged **without c2me** | **PASS — in world** (title 15 s, world 18 s): 967 log lines, **0** `/ERROR`, 582 `/WARN` — the shim costs nothing when C2ME is absent |
| 8 | Reforged + **sodium** `0.5.11+mc1.20.6` | **REFUSED by our own declaration, 3 s**: `[NEG_HARD_DEP optifabric_reforged 1.1.3-reforged+mc1.20.6 {breaks sodium @ [*]}, …]` → `Incompatible mods found!` — the rename did not weaken our own guards |

Two things worth stating plainly:

* **Arms 5 and 3 differ only in one system property** (`-Doptifabric.noC2meCompat=true`): same jar, same c2me,
  same OptiFine, same heap, same save. One does not reach a world, the other does. That is the causal proof that
  the shim is the lever, and that what still blocks the combination is C2ME's worldgen module — not the
  `class_3898` rename (which arm 3 shows working: 59 references restored, the three `c2me-opts-scheduling`
  `@Overwrite`s apply, no error line names `method_17252`/`19487`/`20579`).
* **A small heap hides the problem.** `enabled` defaults to `globalExecutorParallelism >= 3`; with `-Xmx2G` C2ME
  computes 1, turns the module off by itself, and the same jars "work" without the shim. Every arm above used
  6 GB precisely so that the default really is *on*.

Older measurements that this table replaces (published 1.1.2 jar, before the rename fix and before the shim) are
still in the git history and in `collision-1206\REPORT.md`; the two-jar arms (`Non-unique Mixin config name
optifabric.mixins.json`, and c2me refusing the pair) are in the section above.

Not measured / not claimed: no soak (each in-world arm is ~20-30 s of one pre-existing save), no shaderpack, no
c2me build other than `0.2.0+alpha.11.100+1.20.6`, no multiplayer, and the other 19 c2me modules are only "on by
default and error-free in these runs" rather than individually verified (per-module isolation for one of them,
`c2me-notickvd`, is in `c2me-modules\REPORT.md`).

