# The alternative 1.20.6 build (`optifabric_reforged`)

> 本文件只讲 **`wip/1.20.6-reforged` 分支上的第二个 1.20.6 产物**:同一份源码,另一个 mod id。
> 已发布的 1.20.6 线(`main`)没有改动,mod id 仍是 `optifabric`。中文在[下半部分](#中文)。
>
> This file documents the **second 1.20.6 product**, built on the branch `wip/1.20.6-reforged`: the same
> sources as the published line, published under its own mod id. The published line (`main`) is unchanged and
> still publishes `id: optifabric`. The Chinese half is [below](#中文).

## English

### What this build is

| | published line (`main`) | this branch (`wip/1.20.6-reforged`) |
|---|---|---|
| jar | `OptiFabric-1.1.2+mc1.20.6.jar` | `OptiFabric-Reforged-1.1.2-reforged+mc1.20.6.jar` |
| mod id | `optifabric` | `optifabric_reforged` |
| display name | `OptiFabric` | `OptiFabric Reforged` |
| `provides` | — | **— (deliberately, see below)** |
| our own `depends` / `conflicts` / `breaks` | unchanged | **identical** |
| everything else (classes, `mappings/mappings.tiny`, mixin config, icon) | — | **identical** |

The two jars differ in exactly two entries: `fabric.mod.json` (id, name, description) and
`LICENSE.txt_<archives_base_name>`. Measured on this branch against the published 1.1.2 jar: 69 entries each,
67 byte-identical, 1 differing (`fabric.mod.json`), 1 renamed (`LICENSE.txt_OptiFabric` →
`LICENSE.txt_OptiFabric-Reforged`).

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

> **Measured caveat — read the table at the end of this file.** This build reaches the *title screen* with
> c2me + OptiFine, but a **world does not load**: `c2me-opts-scheduling` `@Overwrite`s
> `method_17252(Lnet/minecraft/class_3193;Ljava/lang/Runnable;)V` in `net.minecraft.class_3898`, and OptiFine's
> patched copy of that class — the one OptiFabric serves — no longer carries that method, so the mixin fails and
> the integrated server dies with `Mixin transformation of net.minecraft.class_3898 failed`. The same jars
> **without** OptiFabric load the same world with 0 errors. The id change removes the *refusal*, not the
> *incompatibility*: c2me's `breaks: optifabric` is telling the truth. Treat this build as a way to reach and
> debug that crash — not as a usable c2me + OptiFine configuration.

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

* **+** c2me (and any other mod that refuses `optifabric`) is no longer refused, and the game reaches the title screen.
* **−** it does **not** make c2me + OptiFine work: entering a world still dies in c2me's `@Overwrite`
  (see the measured table at the end). The refusal was the symptom; the incompatibility is real.
* **−** 14 third-party refusals stop firing; keeping those mods apart is now the user's job.
* **−** a third party's `depends: optifabric` is not satisfied (none exist on 1.20.6 today).
* **−** `provides: optifabric` cannot be added later without re-arming c2me (see above).
* **±** the jar carries its own id, version and name, so mod lists, crash reports and support threads say
  `OptiFabric Reforged 1.1.2-reforged+mc1.20.6`. That is deliberate: it has to be obvious which of the two is
  installed.

## 中文

### 这是什么

| | 已发布线(`main`) | 本分支(`wip/1.20.6-reforged`) |
|---|---|---|
| jar | `OptiFabric-1.1.2+mc1.20.6.jar` | `OptiFabric-Reforged-1.1.2-reforged+mc1.20.6.jar` |
| mod id | `optifabric` | `optifabric_reforged` |
| 显示名 | `OptiFabric` | `OptiFabric Reforged` |
| `provides` | — | **—(有意为之,见下)** |
| 我们自己的 `depends` / `conflicts` / `breaks` | 不变 | **完全相同** |
| 其它一切(类、`mappings/mappings.tiny`、mixin 配置、图标) | — | **完全相同** |

两个 jar 只有两项不同:`fabric.mod.json`(id、名称、描述)与 `LICENSE.txt_<archives_base_name>`。本分支实测:
对已发布的 1.1.2 逐项比对,两边各 69 个条目,**67 个逐字节相同**,1 个不同(`fabric.mod.json`),
1 个改名(`LICENSE.txt_OptiFabric` → `LICENSE.txt_OptiFabric-Reforged`)。

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

> **实测警告 —— 请看文末那张表。** 这个产物配 c2me + OptiFine 能到**主界面**,但**进不了世界**:
> `c2me-opts-scheduling` 用 `@Overwrite` 覆盖 `net.minecraft.class_3898` 里的
> `method_17252(Lnet/minecraft/class_3193;Ljava/lang/Runnable;)V`,而 OptiFabric 交出去的、OptiFine 打过补丁的那份类
> 里已经没有这个方法,于是 mixin 失败、集成服务器直接崩:`Mixin transformation of net.minecraft.class_3898 failed`。
> 同样这几个 jar **去掉 OptiFabric** 时,同一个世界能正常加载、0 错误。也就是说:改 id 去掉的是**拒载**,不是**不兼容** ——
> c2me 那条 `breaks: optifabric` 说的是实话。请把这个产物当成"用来复现和排查那个崩溃"的工具,
> 而不是一个可用的 c2me + OptiFine 配置。

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

* **+** c2me(以及其它拒绝 `optifabric` 的模组)不再被拒载,游戏能进到主界面。
* **−** 但它**并没有**让 c2me + OptiFine 变得可用:进世界仍然死在 c2me 的 `@Overwrite` 上(见文末实测表)。
  拒载只是症状,不兼容是真的。
* **−** 14 条第三方拒载不再生效;把这些模组分开的责任回到使用者身上。
* **−** 第三方的 `depends: optifabric` 不再被满足(1.20.6 上目前没有这样的模组)。
* **−** 以后也不能补 `provides: optifabric`,那会把 c2me 的拒载重新武装起来(见上)。
* **±** jar 带自己的 id、版本与名称,所以模组列表、崩溃报告、求助帖里显示的是
  `OptiFabric Reforged 1.1.2-reforged+mc1.20.6`。这是有意的:必须一眼看出装的是哪一个。

## Measured on this branch / 本分支实测

Every arm below ran on my own copies of a 1.20.6 instance (never on the launcher's own `versions/`), Fabric
Loader 0.19.5, fabric-api `0.100.8+1.20.6` and OptiFine `preview_OptiFine_1.20.6_HD_U_J1_pre18.jar`.
`PASS` = title screen (`Sound engine started`).

| # | `mods/` | result |
|---|---|---|
| 1 | **OptiFabric-Reforged 1.1.2-reforged** + c2me `0.2.0+alpha.11.100+1.20.6` + fabric-api + OptiFine | **PASS — title screen 20 s**; `Loading 80 mods:`, `Prepared 425 patched classes (1 skipped, 0 failed)`, `Ready: 424 patched classes taken over by Fabric Loader in 0.4 seconds`, `0` `/ERROR` in 1018 lines |
| 2 | **published OptiFabric 1.1.2** + the same three | **REFUSED at mod resolution** (18 s): `Immediate reason: [NEG_HARD_DEP c2me 0.2.0+alpha.11.100+1.20.6 {breaks optifabric @ [*]}, ROOT_FORCELOAD_SINGLE c2me …, ROOT_FORCELOAD_SINGLE optifabric 1.1.2+mc1.20.6]` → `Incompatible mods found!` |
| 3 | **OptiFabric-Reforged** + sodium `0.5.11+mc1.20.6` + fabric-api + OptiFine | **REFUSED by our own declaration**: `[NEG_HARD_DEP optifabric_reforged 1.1.2-reforged+mc1.20.6 {breaks sodium @ [*]}, …]` → `Incompatible mods found!` |
| 4 | arm 1, with `--quickPlaySingleplayer <copied save>` | title screen 20 s, then **the world does not load**: `InvalidMixinException: @Overwrite method method_17252(Lnet/minecraft/class_3193;Ljava/lang/Runnable;)V in c2me-opts-scheduling.mixins.json:task_scheduling.MixinThreadedAnvilChunkStorage … was not located in the target class net.minecraft.class_3898` → `RuntimeException: Mixin transformation of net.minecraft.class_3898 failed` → server crash report |
| 5 | both jars + fabric-api + OptiFine (no c2me) | **does not start**: `java.lang.RuntimeException: Non-unique Mixin config name optifabric.mixins.json used by the mods optifabric and optifabric_reforged` |
| 6 | both jars + c2me + fabric-api + OptiFine | **REFUSED** — c2me's `breaks` matches the published jar |
| 7 | c2me + fabric-api + OptiFine, **no OptiFabric** (attribution for 4), same save | **PASS — title screen 23 s, and the world loads**: `Starting integrated minecraft server version 1.20.6`, `Time elapsed: 246 ms`, `Dev[…] logged in with entity id 17`, `Saving chunks for level 'ServerLevel[新的世界]'`, **0** `/ERROR` in 165 lines |

Arms 2, 3, 5 and 6 are reported by the harness as `INCONCLUSIVE` because its verdict only distinguishes
"title screen" from "Minecraft has crashed"; their logs are quoted verbatim above.

**What this means for the plan:** the rename does exactly what it was expected to do mechanically — c2me's
declaration stops matching (arm 1 vs 2) and our own declarations keep working (arm 3). But the combination it
unblocks is still broken one step later: arm 4 vs arm 7 shows the world-load crash comes from OptiFine's patched
`net.minecraft.class_3898` meeting c2me's `@Overwrite`, i.e. from the incompatibility that c2me's declaration was
there to prevent. So this build is a debugging tool for that crash, not a shippable "c2me + OptiFine" answer.

