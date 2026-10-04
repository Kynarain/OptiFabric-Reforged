# 更新日志

> 本文件按时间倒序,收录本仓库**两条线**的各版本:本分支的 **26.x**(`2.2.5+mc26.2`、`2.2.4+mc26.2`、`2.2.2+mc26.2`、
> `2.2.1+mc26.2`、`2.2.0+mc26.2`、`2.1.1+mc26.2`、`2.1.0+mc26.2`、`2.0.0+mc26.1.2`、`1.2.0+mc26.1.2`)与 **1.21.x**(`1.1.0` – `1.1.2`,十个 MC
> 版本)。1.21.x 那条线在自己的分支上,它的条目按当时的样子保留,属于历史记录。26.x 现在覆盖 **26.2**(当前,`2.2.5`)
> 与 **26.1.2**(`2.2.5`),所以这一线也有了"逐 MC 版本的版本号"。

## 2.2.5+mc26.2 — 26.x 线的第十版(移植 1.21.x 线重写过的 `LocalSlotLayoutFix`:把游戏自己的局部变量放回游戏声明的槽位)

> **修订号递增的依据**(SemVer §7,规则见 [`docs/VERSIONING.md`](docs/VERSIONING.md)):改动表的这一格是
> 「修 fixer、修兼容性 → 修订号」。这一版**不移除任何支持、也不新增支持范围**,加的是 1.21.x 线已经用了一个版本的
> 那个局部变量布局修复器(它在那条线上的实测见 `tf-fix\REPORT.md`)。`2.2.4 → 2.2.5`,两个 MC 版本各出一个 jar。

### 改了什么

**症状(用户可见)**:`distanthorizons`(Distant Horizons `3.3.3-26.2`)+ Fabric API + 本模组 + OptiFine
`preview_OptiFine_26.2_HD_U_K2_pre1` 在 **26.2** 上**启动阶段就崩,连主界面都到不了**,报错落在 DH 自己那个 `@Inject` 上:

    [main/ERROR]: Critical injection failure: LVT in net/minecraft/client/renderer/GameRenderer::renderLevel(
    Lnet/minecraft/client/DeltaTracker;)V has incompatible changes at opcode 286 in callback
    DistantHorizons.fabric.mixins.json:client.MixinGameRenderer from mod distanthorizons->@Inject::renderLevel(
    Lnet/minecraft/client/DeltaTracker;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;FF
    Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/util/profiling/ProfilerFiller;Z
    Lnet/minecraft/client/renderer/state/OptionsRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;
    Lorg/joml/Matrix4fc;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/PoseStack;)V.
    Caused by: java.lang.RuntimeException: Mixin transformation of net.minecraft.client.renderer.GameRenderer failed

`2.2.3` 与 `2.2.4` 两个出厂 jar 上都是这一行(本版两个都重跑过,见下)。

**根因**:OptiFine 会**重编译它补丁的那些方法**,而它的 javac 把**它自己新增的局部变量**放在源码里出现的位置 ——
于是**游戏自己的局部变量被整体往上挤**。Mixin 读局部变量表有两种方式,这一处两种都会坏:

1. **按槽位顺序**:`CallbackInjector` 用 `getFirstNonArgLocalIndex(args)` 往上取前若干个非空局部变量来建回调的描述符,
   顺序一旦对不上,整个类变换失败并报上面那条 `LVT … has incompatible changes at opcode N`;
2. **按绝对槽位**:MixinExtras 的 `@Local(index = N)` 糖经 `Locals.getLocalsAt(...)` 取 `locals[N]`,那个槽位上是别的
   类型时糖根本建不起来(`SugarApplicationException: Unable to find matching local!`)。

**修复(本版)**:把 1.21.x 线重写过的 `LocalSlotLayoutFix` 移植过来 —— 新文件
`patcher/fixes/LocalSlotLayoutFix.java`(37,453 字节,SHA-256
`85EF437444F637EC15DC63CA810A9348998FEBE751F1F9F4509113E12E103BB1`,**逐字节**取自 1.21.x 线的侧分支
`tf-localscope` 的 `1bf3e4b`,一个字节都没改),在 `OptifineFixer.registerOfficialNameFixes()` 里按**这一线的官方名**
注册两条(`desc` 照 1.21.x 留空:两个版本的描述符并不相同,留空以后按 OptiFine 那份类自带的描述符办事):

    registerFix("net/minecraft/client/renderer/GameRenderer",
            new LocalSlotLayoutFix(null, "render", "renderLevel"));
    registerFix("net/minecraft/client/renderer/LevelRenderer",
            new LocalSlotLayoutFix(null, "renderLevel"));

它的做法是**按作用域**把两张局部变量表配对(指令下标两边不可比,局部变量名两边也不可比;可比的是「按槽位顺序排列的
描述符序列」,用最长公共子序列对齐),然后把**游戏自己的**槽位放回游戏声明的编号,OptiFine 自己的槽位搬到两个方法
各自局部区间的末尾。一个槽**整体搬、绝不拆到两个目标**(拆开会让某条 `GOTO` 越过写操作的那条路径读到未初始化的局部
变量,ASM 的数据流验证器会拒绝),同时活着的两个槽不会合并;帧由管线本来就在用的 `FrameComputingWriter` 重算。
**它不删 OptiFine 的任何代码、也不加任何指令**,只是把同一个写操作写到另一个槽号上。

因此 `@Local(index = N)` 那一类失败**整类消失**(不再取决于某个 sugar 恰好落在第几个槽位)。本线这次实测到的是第 1 种
(DH 的按顺序注入)。

**这一线上没有第二处管局部变量布局的代码,不会重复施加**:`git grep -E 'localVariables|maxLocals|getFirstNonArgLocalIndex|LocalVariableTable'`
在 2.2.4 的提交 `1891b1b` 上只命中 `FrapiTesselateBridgeFix`(它只**读** `method.localVariables` 找一个槽号,而且只注册在
`SectionCompiler` 上,不改布局);`LevelRenderer` 上原有的两条注册(`RestoreVanillaMethodsFix` /
`DropVanillaAbsentOverloadsFix`)操作的方法是 `extractBlockOutline`,与 `renderLevel` 无关;`GameRenderer` 上原本
一条注册都没有;全局的 `MissingOverrideFix` 只**新增**转发方法,不碰局部变量表;`MixinClassMetadata` 管的是 Mixin 的
类元数据缓存,与槽位无关。

### 实测

干净临时 worktree(`r225\wt`,`1891b1b` + 这一个文件 + 这一处注册)、自己的实例副本、**每次运行前清空 `.optifine`
缓存**(OptiFabric 每次启动都会重跑修复器,缓存里那份已经被补丁过的会被二次施加)、Java 25、Loader `0.19.5`、
各自的 OptiFine 预览版;`PASS` 一律指**主界面**,只有写明的那几行进了世界。`Cannot @Coerce` / `InvalidInjectionException` /
`Mixin apply … failed` / `LVTGeneratorError` **每个手臂都是 0**(下表只列非零的计数器)。

**26.2:`distanthorizons` 复现与候选**(4 个 jar:DH `3.3.3-26.2`、Fabric API `0.160.0+26.2`、本模组、OptiFine
`K2_pre1` → `Loading 51 mods`)

| 手臂 | jar(SHA-256) | 结果 | 首次致命行 |
|---|---|---|---|
| 出厂 2.2.3 | `07B91508…` | **FAIL,13 秒** | 上面那条 `LVT … opcode 286` |
| 出厂 2.2.4 | `8D67F552…` | **FAIL,21 秒** | 同一行 |
| **本版(2.2.4 + 这一处移植)** | `ED181EE9…` | **PASS,主界面**(33 秒) | 无 |

计数器(2.2.3 / 2.2.4 / 本版):`ERROR=6/6/6`、`Critical injection failure=2/2/`**`0`**、
`Mixin transformation of … failed=6/6/`**`0`**、`LVTGeneratorError=0/0/0`。

**26.2 宽集合**(18 个 jar:C2ME `0.4.2-alpha.0.55+26.2`、moreculling、particle_core、bobby、ferrite-core、
entityculling、krypton、BadOptimizations、Chunky、lithium、lomka、cloth-config、modmenu、fzzy_config、
fabric-language-kotlin、Fabric API、OptiFine、本模组 → `Loading 118 mods`)

| 手臂 | jar | 结果 | 计数器 |
|---|---|---|---|
| 出厂 2.2.4(对照) | `8D67F552…` | **PASS,到主界面并进世界**(32 s / 41 s) | `ERROR=6`,其余四项 0 |
| **本版** | `ED181EE9…` | **PASS,到主界面并进世界**(16 s / 20 s) | `ERROR=6`,其余四项 0 |

**26.1.2**(这一处移植**从未在 26.1.2 上跑过**,本版第一次):20 个 jar(C2ME `0.4.0-alpha.0.62`、moreculling、
particle_core、bobby、ferrite-core、entityculling、krypton、BadOptimizations、packetfixer、Chunky、lithium、lomka、
cloth-config、TRansition/TRender、fabric-language-kotlin、fzzy_config、Fabric API `0.155.3+26.1.2`、OptiFine
`K1_pre2`、本模组 → `Loading 118 mods`)

| 手臂 | jar | 结果 | 计数器 |
|---|---|---|---|
| 出厂 2.2.4(对照) | `F08706DC…` | **PASS,到主界面并进世界**(30 s / 37 s) | `ERROR=4`,其余四项 0 |
| **本版** | `E498EF2D…` | **PASS,到主界面并进世界**(18 s / 22 s) | `ERROR=4`,其余四项 0 |

26.1.2 **没有 `distanthorizons` 的手臂**:DH 至今只有 26.2 的构建(它的发布列表里 `3.1.0-b-26.2` 起全部只标
`26.2`),没有可用的 26.1.2 jar。

**修复器确实跑了吗?跑到了什么?**(这些行进的是**启动器 stdout**,26.x 上不进 `latest.log`)

26.2:两条都打出来了,第二条就是这一处修复:

    [OptiFabric] net/minecraft/client/renderer/GameRenderer.render needs no local slots moved: the game's own locals are
    already in the slots the game declares them in (paired 5 of 5 of OptiFine's entries with 5 of the game's; …)
    [OptiFabric] Realigned the locals of net/minecraft/client/renderer/GameRenderer.renderLevel(Lnet/minecraft/client/DeltaTracker;)V
    onto the slots the game declares them in: 18 of OptiFine's 24 table entries are the game's own (of 20), the other 6 are
    OptiFine's own or could not be paired (slots 2->22, 3->2, 4->3, 5->4, 6->5, 7->6, 8->7, 9->8, 10->9, 11->10, 12->23,
    13->11, 14->12, 15->13, 16->14, 17->15, 18->16, 19->17, 20->18, 21->24)

`GameRenderer.render` 那一条是**修复器主动不动**的检查:它的两张表本来就对齐,修复器不瞎搬。

26.1.2:同一对方法、另一套数字(这一版第一次跑):

    [OptiFabric] Realigned the locals of net/minecraft/client/renderer/GameRenderer.renderLevel(Lnet/minecraft/client/DeltaTracker;)V
    onto the slots the game declares them in: 19 of OptiFine's 25 table entries are the game's own (of 21), the other 6 are
    OptiFine's own or could not be paired (slots 2->23, 3->2, 4->3, 5->4, 6->5, 7->6, 8->7, 9->8, 10->9, 11->10, 12->24, …)

出厂 jar 上**没有**这些行(它根本没有这个修复器),这就是"哪一边跑的"的判据。

### 边界(不要读过头)

- **PASS 一律指主界面**,除了宽集合那两行(进了**一个**世界,`logged in with entity id`,约 20–40 秒就停);
  **没有光影包、没有压测、没有联机、没有 GUI 操作**;宽集合那一个存档是**复制进去的**固定存档,不是新生成的;
- 每个手臂**只跑一次**(26.2 的 DH 候选与 26.1.2 的宽集合各一次,26.2 的宽集合候选一次);
- 上面两组「出厂 2.2.4 vs 本版」就是对照:两者只差这一个文件与这一处注册,模组集合逐字节相同(每个手臂的
  `*.mods-manifest.json` 记了全部 jar 的尺寸与 SHA-256);
- **`continuity` 与 `immediatelyfast` 没有被这一版修好**,`threefix\REPORT.md` 已实测:`continuity` 是 OptiFabric
  自己的类接管/元数据状态那一路(`Scanned 0 target(s)`),`immediatelyfast` 的失败点在 OptiFine **自己重编译出来的
  方法体**里(一个 redirector 找不到调用点),两者都不是槽位问题,本版与 2.2.4 上的表现逐字相同。**不要**把这一版读成
  "三个模组都修好了";
- 1.21.x 线那一处(`@Local(index = 24)`,即 Twilight Forest 撞上的那条)已被这个重写修好(见 `tf-fix\REPORT.md`),
  但 **Twilight Forest 本身仍然起不来**:它换成了 OptiFine 自己那份帧表里一个隐式 `Iterator` 判别符的歧义,不是槽位
  能解决的;
- 26.2 上光影照旧不可用(那个 OptiFine 构建自己取消了光影包加载),本版没有碰它。

产物:`OptiFabric-Reforged-2.2.5+mc26.2.jar` — 201124 字节
`SHA-256: 110CC020CBF5FAA7E434B4F213414C3234689A3B1FD250AC36D3ED6FBEA570B9`

产物:`OptiFabric-Reforged-2.2.5+mc26.1.2.jar` — 201126 字节
`SHA-256: 999D6EBCCE512994B88D0DC21514C8411B05942720924B8A6B4A1FA628E85A37`

两个 jar 都从**同一个干净临时 worktree**(`r225\wt`,HEAD = 本次的移植提交)构建,`fabric.mod.json` 里分别是
`2.2.5+mc26.2` 与 `2.2.5+mc26.1.2`。与上一条(2.2.4)那两个产物逐条目比对:共同条目 **81 个哈希完全相同**,不同的
只有 `fabric.mod.json`(版本号那一行)与 `patcher/fixes/OptifineFixer.class`(多了这一处注册),新增的只有
`patcher/fixes/LocalSlotLayoutFix.class` 与 `LocalSlotLayoutFix$Slot.class`。**跑真机用的那一份与正式产物只差
`fabric.mod.json` 里的一行版本号**(逐条目比对:85 个条目里 84 个哈希相同)。

## 2.2.4+mc26.2 — 26.x 线的第九版(把 Mixin 类元数据的 cache drop 收窄到顶层类:2.2.1–2.2.3 的"装了 OptiFine 就起不来"再修一次,这次去掉的是原因)

> **修订号递增的依据**(SemVer §7,规则见 [`docs/VERSIONING.md`](docs/VERSIONING.md)):改动表的这一格是
> 「修 fixer、修兼容性 → 修订号」。这一版**不移除任何支持、也不新增支持范围**,改的是 2.2.1 引进、2.2.3 只是绕过去的
> 那个 cache drop 本身。`2.2.3 → 2.2.4`,两个 MC 版本各出一个 jar。

### 改了什么

**症状(用户可见)**:Fabric API + 本模组 + 对应版 OptiFine 的组合下,**2.2.1 与 2.2.2 连主界面都到不了**;
2.2.3 补上 accessor 接口之后能起来了,但那个接口是**修症状**:导致它的那次 cache drop 还在,只要哪个模组碰到同样的
位置就还会翻车。报错落在 **Fabric API 自己那个 mixin** 上,不是本模组的名字:

    Mixin apply for mod fabric-rendering-v1 failed fabric-rendering-v1.mixins.json:GuiRendererMixin from mod
    fabric-rendering-v1 -> net.minecraft.client.gui.render.GuiRenderer: InvalidInjectionException
    @WrapOperation operation wrapper method net/minecraft/client/gui/render/GuiRenderer::fixNonQuadIndexing …
    Cannot @Coerce argument type net.minecraft.client.gui.render.GuiRenderer$Draw at index 4 to
    net.fabricmc.fabric.mixin.client.rendering.GuiRendererDrawAccessor

26.1.2 上是同一处,接口在那一版叫 `.../DrawAccessor`。

**归因(受控实验,不是推理)**:把 2.2.3 的源码放进干净的临时 worktree,只做一处改动 —— 让
`MixinClassMetadata.drop(...)` 一个条目都不删 —— 26.1.2 与 26.2 **两边都到主界面**,`Cannot @Coerce` 与
`InvalidInjectionException` 各 0 条,也没有替代性失败。反过来,在 pre-2.2.3 的代码路径上**只**把那一个 drop 调用
打开,两边都稳定复现上面的崩溃。也就是说这一处 drop 对这次崩溃**既充分又必要**。

**机制**:`Injector.checkCoerce` → `canCoerce` → `ClassInfo.forType` 读的正是这个 drop 删掉的 `ClassInfo` cache,
而 `GuiRenderer$Draw` 就是被本模组接管过去的类之一。accessor mixin 加到类上的那条**接口**也活在这份元数据里,
删掉条目就把它一起删了。

**为什么当初会删**:这个 drop 是从 **1.21.x 线抄过来的**,在那边修的是 **ShoulderSurfing 的 `LVTGeneratorError`**
(`class_757`,即 `GameRenderer.getFov` 被 OptiFine 从 private 放宽成 public,`Locals` 按旧缓存查方法元数据查不到)。
那条失败发生在**顶层类的一个方法的 access 标志**上,与嵌套类无关。而引入它的提交 `06c381a` 自己的说明里就写着
**"neither 26.2 nor 26.1.2 has been launched end to end with it"** —— 26.x 这条线当初没有重做过那次实测。

**修复**(本版):只删**顶层类**的条目,嵌套类保留自己的缓存,`MixinClassMetadata.drop(...)` 循环里的第一条判断:

```java
for (String name : classNames) {
    String internalName = name.replace('.', '/');

    //Nested classes are what an accessor mixin turns into an interface …
    if (internalName.indexOf('$') >= 0) continue;   // nested classes keep their cached metadata

    //Once per class only: …
    if (!dropped.add(internalName)) continue;
```

一个条件、一处改动。2.2.3 的 `AddInterfaceFix` **保留**:实测 `drop on + iface off = FAIL`(两个 26.x 版本都是),
所以只要 drop 还在(哪怕是收窄过的),这个接口就是让 jar 能起来的那一半;等收窄后的 drop 在更宽的模组集上验完,
它才会变成纯粹的死重量。

### 实测

`PASS` = 到主界面(日志里的 `Sound engine started`);**只有写明"进世界"的那几行进了世界**。
所有手臂用同一个 Loader `0.19.5`、同一份 OptiFine 预览版、同一个 Java 25 runtime,只换模组集与 jar。

| 手臂 | 模组 | 结果 |
|---|---|---|
| 2.2.1(26.1.2)/ 2.2.2(26.2),只 Fabric API | 49 mods | **FAIL** 22 秒,就是上面那条 `Cannot @Coerce` |
| 2.2.3 − drop 一个调用(26.1.2 / 26.2),只 Fabric API | 49 mods | **PASS**,两边都到主界面 |
| **本版 scoped drop,26.1.2,宽模组集** | **19 jars → `Loading 118 mods`** | **PASS,进世界**(12 秒到主界面 / 16 秒进世界);`Cannot @Coerce`、`InvalidInjectionException`、`Mixin apply … failed`、`LVTGeneratorError` **各 0 条** |
| 同上的**对照**:出厂 **2.2.3**(未收窄的 drop + `AddInterfaceFix`) | 同一套 20 jars,`Loading 118 mods` | **PASS,主界面**(20 秒);即**这套模组集里没有任何东西需要未收窄的 drop** |
| **本版 scoped drop,26.2,宽模组集** | **17 jars → `Loading 118 mods`** | **PASS,进世界**(12 秒到主界面 / 16 秒进世界);四个计数器同样全 0 |

模组集(每个 jar 的确切版本都在 `r224\mods-*` 里):
**26.1.2**:C2ME `0.4.0-alpha.0.62`、`moreculling 1.7.3`、`particle_core 0.3.2`(带 `fzzy_config 0.7.6`、
`fabric-language-kotlin`)、`bobby 5.2.13`、`ferrite-core 9.0.0`、`entityculling 1.11.2`、`krypton 0.3.0`、
`BadOptimizations 2.4.1`、`packetfixer 3.3.5`、`Chunky 1.5.3`、`lithium 0.24.7`、`lomka 0.6.0`、
`cloth-config 26.1.154`、TRansition/TRender、Fabric API `0.155.3+26.1.2`(+ OptiFine `preview_26.1.2_HD_U_K1_pre2`);
**26.2**:同一批里**该版本真实存在**的构建 —— C2ME `0.4.2-alpha.0.55`、`moreculling 1.8.1`、`particle_core 0.3.3`
(带 `fzzy_config 0.7.7+26.2`)、`bobby 5.2.15`、`ferrite-core 9.0.0`、`entityculling 1.11.2-mc26.2`、`krypton 0.3.1`、
`BadOptimizations 2.4.1-26.2`、`Chunky 1.5.3`、`lithium 0.25.3`、`lomka 0.6.0+26.2`、`cloth-config 26.2.155`、
Fabric API `0.160.0+26.2`(+ OptiFine `preview_26.2_HD_U_K2_pre1`)。

**26.2 的宽模组集比 26.1.2 小,而且这不是选择**:`modernfix` 与 `noisium`(1.21.1 的构建)**在 26.1.2 与 26.2 上
都直接拒载** —— `modernfix` 是 `Failed to read classTweaker file … Namespace (intermediary) does not match current
runtime namespace (official)`(26.x 不混淆,而它的 classTweaker 是 intermediary 的),`noisium` 的元数据只允许
`>=1.21 <=1.21.1`;The Twilight Forest 在 26.x 上**没有发布**。**`immediatelyfast` 在两个 26.x 版本上都在渲染类上失败**
(`Critical injection failure: Redirector disableTranslucencySorting(…) in immediatelyfast-common.mixins.json:
skip_text_translucency_sorting.MixinRenderTypes … failed injection check, (0/1) succeeded. Scanned 0 target(s)`,
目标是 `net.minecraft.client.renderer.rendertype.RenderTypes`),于是它被移出两套模组集 —— 见下面「边界」。
另外 `iris` 在两个 26.x 版本上都硬依赖 `sodium`,而本模组的 `breaks: sodium` 会**先**把整个实例拒载,所以它也移出。

**sodium 那条手臂(顺带把 2.2.2 的文档说法纠正了)**:Loader `0.19.5` 上,`breaks` 是**真的会被执行**的:

    Immediate reason: [HARD_DEP iris 1.11.4+mc26.2 {depends sodium @ [0.9.x]},
                       NEG_HARD_DEP optifabric_reforged 2.2.4+mc26.2 {breaks sodium @ [*]}, …]
    [main/ERROR]: Incompatible mods found!

也就是说 `breaks` 不是警告而是**硬拒载**(`NEG_HARD_DEP`),`conflicts` 单写在 `2.2.1` 上实测只是警告
(见 `compat-matrix\README.md` §10.10)。本文件 2.2.2 那一条写的"两个字段都不产生 `Incompatible mods found`"
**是错的**,已在本版更正;1.20.6 那条线的 README / FAQ / CHANGELOG 也在同一批更正里。

### 边界(不要读过头)

- 两个 26.x 版本的 PASS **都在 28 秒内**结束(12 秒到主界面 + 4 秒后进世界 + 约 12 秒停留),进世界那次**只在一个世界**、
  **没有加载光影包**、没有压测、没有长时间 soak;
- **26.2 的宽模组集覆盖不了 1.21.x 专属的那些模组**,因为它们在 26.2 上没有构建或直接拒载(见上);
- **`immediatelyfast` 的失败没有查清归属**:它在本版 jar 上失败,**在出厂 2.2.3 上也失败**(同一句、同一个类),
  目前只测到"这个组合起不来";没做"去掉 `AddInterfaceFix` / 去掉 drop"的对照,所以**不能**说它与这次修复有关,
  也**不能**说无关 —— 这是一条**新发现**,不属于本版修复的范畴;
- "26.x 是否真的需要那个 drop" 这个问题**本版只回答了一半**:在**这套**模组集里,未收窄的 2.2.3 与本版一样能起
  (所以**没有任何东西需要那条嵌套条目**),但这不是穷举;1.21.x 的原始失败模式(顶层类的方法 access 标志)在收窄后
  **仍然被覆盖**,而 26.x 上**没有复现出任何一个必须删顶层类条目的案例** —— 只能说"没有观察到";
- **`sodium` 那条手臂里同时有 `iris`**(它硬依赖 sodium,所以 sodium 反正也会被拉进来),不是"只有 sodium + 本模组"的
  干净对照;1.20.6 线那次(`crossline-check\logs\1.20.6-sodium-withus`)是干净的,结论相同;
- `AddInterfaceFix` 留着是**有意的**,理由在上面(它是 guard,不是死代码)。

产物:`OptiFabric-Reforged-2.2.4+mc26.2.jar` — 188912 字节
`SHA-256: 8D67F55207184A39E4778A5F1ACD9968E8360A28391184501755D6D352AB4E8E`

产物:`OptiFabric-Reforged-2.2.4+mc26.1.2.jar` — 188915 字节
`SHA-256: F08706DC0357EB46396B1793F287058A384A9380D95A32DC9609F6B9197F5A18`

两个 jar 都从**同一个干净临时 worktree**(`r224\wt`,HEAD = 本次的收窄提交)构建,`fabric.mod.json` 里分别是
`2.2.4+mc26.2` 与 `2.2.4+mc26.1.2`。与上一条(2.2.3)那两个产物逐条目比对:**83 个条目里 82 个哈希完全相同**,
唯一不同的就是 `fabric.mod.json`(版本号那一行)—— 也就是说本版改的确实只有那一处代码加版本号,验证过的字节
与被发布的字节是同一份。

## 2.2.3+mc26.2 — 26.x 线的第八版(修好"装了 OptiFine 就起不来":给 OptiFine 那份 `GuiRenderer$Draw` 补上 Fabric API 的 accessor 接口)

> **修订号递增的依据**(SemVer §7,规则见 [`docs/VERSIONING.md`](docs/VERSIONING.md)):改动表的这一格是
> 「修 fixer、修兼容性 → 修订号」。这一版**不移除任何支持、也不新增支持范围**,只是修掉一个"在最常见配置下根本
> 起不来"的缺陷,所以是修订号(`2.2.2 → 2.2.3`),两个 MC 版本各出一个 jar。

### 改了什么

**症状(用户可见)**:Fabric API + 本模组 + 对应版 OptiFine、**没有别的模组**时,客户端在启动阶段直接崩掉,连主界面
都到不了。日志里失败的是 **Fabric API 自己那个 mixin**,不是本模组:

    Mixin apply for mod fabric-rendering-v1 failed fabric-rendering-v1.mixins.json:GuiRendererMixin from mod
    fabric-rendering-v1 -> net.minecraft.client.gui.render.GuiRenderer: InvalidInjectionException
    @WrapOperation operation wrapper method net/minecraft/client/gui/render/GuiRenderer::fixNonQuadIndexing …
    Cannot @Coerce argument type net.minecraft.client.gui.render.GuiRenderer$Draw at index 4 to
    net.fabricmc.fabric.mixin.client.rendering.GuiRendererDrawAccessor

26.1.2 上是同一处,接口在那一版叫 `.../DrawAccessor`。

**归因**(四条对照,每次只差一个变量):只有 Fabric API → 正常;Fabric API + 本模组、**不放 OptiFine** → 正常;
Fabric API + 本模组 + OptiFine(**不放别的模组**)→ 失败;再加任何别的模组 → 同样失败。所以只有 OptiFine 在场时
才坏,而报错落在 Fabric API 的注入器上。

**根因**:OptiFine 重编译过的类由本模组交给加载器,Mixin 从这份字节建元数据时,**Fabric API 那个 accessor mixin
加的接口还没在类上**。Fabric 的 `@WrapOperation` 处理器要把正在画的 `Draw` 当成那个接口用,Mixin 用
`ClassInfo#canCoerce` 校验 —— 问的是"这个接口现在是 `GuiRenderer$Draw` 的超类型吗" —— 答案是否,于是**整个类**
变换失败。**这不是改名、也不是方法体问题**:`GuiRenderer.executeDraw(Draw, RenderPass)` 的描述符与局部变量槽位
两边完全一样,`Draw` 的成员与 `executeDrawRange` 的指令表都与原版逐字节相同;缺的是一条**类级别的接口**
(`InnerClasses` 标志位那一处也试过,不解决问题)。

**修复**:新增 `src/main/java/kynarain/cn/optifabric/patcher/fixes/AddInterfaceFix.java` —— 一个 `ClassFixer`,
把接口**从类路径上找出来**(遍历已加载模组的 mixin 包,包名读自各自的 `*.mixins.json`,含 jar 里
`META-INF/jars/` 下的嵌套 jar;找 `@Mixin(targets=…)` 指向该类的**接口**,只接受声明了目标类当前没有的方法的
候选),再加到 OptiFine 那份 `Draw` 上;accessor 方法本身**留给 Mixin 生成**(自己写进去会让整个类因
`cannot overwrite method … because @Overwrite is required by the parent configuration` 失败,已实测)。
`OptifineFixer.registerOfficialNameFixes()` 里只有**一条注册**:

```java
registerFix("net/minecraft/client/gui/render/GuiRenderer$Draw",
        new AddInterfaceFix("net/minecraft/client/gui/render/GuiRenderer$Draw"));
```

接口**不写死名字**是因为它在两个版本里不同(26.2 `GuiRendererDrawAccessor`,26.1.2 `DrawAccessor`):写死一个会让
另一条线在 fixer 里抛 `ClassNotFoundException` —— **已实测**。

### 实测

| 手臂 | 结果 |
|---|---|
| 旧 jar(2.2.2),26.2 / 26.1.2,Fabric API + 本模组 + OptiFine | **FAIL,23 秒 / 21 秒** |
| **新 jar,26.2 / 26.1.2**,同上三件 | **主界面,两边各 20 秒**,`Loading 49 mods` |
| **新 jar,26.2 + C2ME** | **主界面,17 秒**,`Loading 79 mods` |
| **新 jar,26.2 + C2ME,进世界** | **世界加载并渲染**;`Starting integrated minecraft server version 26.2`、`Loaded 1585 recipes`、`Time elapsed: 1424 ms`;`Cannot @Coerce` / `InvalidInjectionException` / `Mixin apply … failed` **各 0 条** |

**边界(不要读过头)**:进世界那次只有约 **40 秒**,不是压测;那次与 26.2 + C2ME 的启动都是**发布前拿"2.2.2 + 这一处
修复"的构建**跑的(与正式产物只差版本号一个字符串,条目级比对见下);**26.1.2 只到主界面,没有进世界**;修复只在两个
26.x 版本与各自参考安装里那份 Fabric API 上实测过;另外**没有查明** Mixin 的 accessor 那一趟本来为什么没有走到本模组
提供的这份 `Draw` 上 —— 失败判据的方向与"补上接口就能修好"是实测结论,最初漏掉的原因不是。

### 验证(离线)

补丁管线其余部分与 2.2.2 逐字节相同,所以 2.2.1 / 2.2.2 的离线数字继续适用:26.2 **562 / 562**、26.1.2
**567 / 567**、`FAILED: 0`、`ASM verifier problems: 0`、四个扫描器全 0。两个产物都在**干净的临时 worktree**里构建
(本机的 `I:\mods\OptiFabric` 有 9 个未提交的实验文件,那里的构建会把实验类打进 jar),并与已发布的 2.2.2 逐条目比对:
共同条目 81 个里 79 个哈希相同,不同的是 `fabric.mod.json`(只差版本号一行)与 `patcher/fixes/OptifineFixer.class`,
新增的只有 `patcher/fixes/AddInterfaceFix.class` 与 `AddInterfaceFix$Candidate.class`;与发布前那份
"2.2.2 + 这一处修复"的构建相比,则**只有 `fabric.mod.json` 不同**。两个 jar 里都没有任何实验类
(`Probe` / `PerDrawShaderState` / `SectionDraw`),也没有 OptiFine 的类或 `mappings/mappings.tiny`。

产物:`OptiFabric-Reforged-2.2.3+mc26.2.jar` — 188876 字节
`SHA-256: 07B91508EE9948A5EF35FD343A057D2ACB1530B6525DF851C161390968FC45A8`

产物:`OptiFabric-Reforged-2.2.3+mc26.1.2.jar` — 188879 字节
`SHA-256: 611C717726FA066B57CB54815224904636FD385276BD9B01159CE1D6E1C811AA`

## 2.2.2+mc26.2 — 26.x 线的第七版(sodium 同时声明进 conflicts 与 breaks,并把已声明的不兼容写进两份 README)

> **修订号递增的依据**(SemVer §7,规则见 [`docs/VERSIONING.md`](docs/VERSIONING.md)):改动表的这一格是
> 「修正元数据/文档 → 修订号」。这一版**没有改任何代码、没有碰补丁管线**:`fabric.mod.json` 里 sodium 那条声明
> 从只写 `conflicts` 变成 `conflicts` 与 `breaks` 两处都写(`conflicts` 那条保留),另外把已经声明的不兼容清单
> 写进两份 README。所以是修订号(`2.2.1 → 2.2.2`),两个 MC 版本各出一个 jar。

### 改了什么

- **`fabric.mod.json`**:`sodium` 现在同时出现在 `conflicts` 与 `breaks` 里(`*`,原有的 `conflicts` 条目一个字没动)。
  上游 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric) 只在 `conflicts` 里声明它,而有些启动器与平台
  只读 `breaks`,所以两处都写;
- **两份 README**(`README.md` / `README_CN.md`)新增「声明的不兼容」一节:完整清单、继承自上游的条目、本移植有意
  没有带过来的三条 1.16/1.17 时代的版本范围、`ryoamiclights` 与本条 `sodium` 的来历,以及加载器到底会不会拦。

### 这是一条声明,不是闸门

在 **Fabric Loader 0.19.5** 上、`mods/` 里同时有 sodium 时实测:日志开头是 `Warnings were found!`、点名这条冲突,
然后照常进入 `Loading <N> mods:` —— `conflicts` 与 `breaks` **两个字段都不产生** `Incompatible mods found`,也没有
`HARD_DEP` 之类的拒载。两个字段都由加载器自身处理,都只是**警告**;请把这张表读成"作者已知这个组合会坏",而不是
"装了会被拦住"。(那次实测是在 1.21.x 线上做的。)

> 另一侧的声明:`sodium` 自己那份元数据只在 `breaks` 里声明、而且针对的是旧 id **`optifabric`**,而本线从 `2.0.0`
> 起是 `optifabric_reforged` —— 所以 Sodium 那条**在本线上不会命中**,本模组这两条是加载器唯一会读到的。

### 验证

这一版**一个字节的代码都没改**:`fabric.mod.json` 的声明与两份 README 都是数据与文档,补丁管线、fixer 表与
`ClassInfo` 缓存那一步与 2.2.1 逐字节相同。所以 2.2.1 记下的离线数字(26.2 **562 / 562**、26.1.2 **567 / 567**、
ASM 0 问题、四个扫描器全 0)与真机记录继续适用;改动只进 `fabric.mod.json`,不影响启动期行为,离线校验覆盖不到
"加载器会不会警告"这一步 —— 上面那条结论来自 Loader 0.19.5 上的实测。

产物:`OptiFabric-Reforged-2.2.2+mc26.2.jar` — 180471 字节
`SHA-256: 2654F5085EF4E5CA958BDECF627A2BCB195B608AAC1E5EA31C21445479D541C9`

## 2.2.1+mc26.2 — 26.x 线的第六版(丢掉 Mixin 为被替换的类缓存的 ClassInfo,修好 @ModifyVariable 的 LVTGeneratorError)

> **修订号递增的依据**(SemVer §7,规则见 [`docs/VERSIONING.md`](docs/VERSIONING.md)):改动表的这一格是
> 「修 fixer、修兼容性 → 修订号」。这一版**不移除任何支持、也不新增支持范围**,只是修掉一个会让 mixin 整个类变换
> 失败的缺陷,所以是修订号(`2.2.0 → 2.2.1`)。**同一个修复在本仓库的 1.21.x 线上先落地并已真机实测**(主界面、
> 进世界、光影包在世界里编译),这一版是把它补到 26.x 的两个产物上。按 §3,已发布的 `2.2.0+mc26.2` 与
> `2.2.0+mc26.1.2` 冻结(它们那一节记录原样留着);**这一版出两个 jar** —— 26.1.2 的例外值也跟着升到 2.2.1。

### 改了什么

**症状**:某些 mixin 让**整个类**的变换失败,日志里只有 Fabric 那句
`Mixin transformation of net.minecraft.class_757 failed`;真正的异常是 Mixin 抛的

    LVTGeneratorError: Could not locate method metadata for method_3196 generating LVT in net/minecraft/class_757

也就是**需要解析局部变量的注入(`@ModifyVariable` 与 locals 捕获)在 OptiFine 改过的类上必然失效**,而且它在
`require` / `expect` 之前就抛出,受影响的模组自己无法绕开。

**根因**:Mixin 为每个类建一份 `ClassInfo`(取自它的字节码提供者 —— 在 Fabric 上就是 Knot,也就是本模组的 game
transformer)并缓存。Fabric 在 `preLaunch` 入口点**之前**就准备好了 Mixin 的配置,所以对本模组替换掉的那些类,缓存里
可能已经存着**游戏自己的**那一份,而 Mixin 稍后拿到、要变换的却是 **OptiFine 的**那一份。两者对某个成员的记录一旦
不一致,查找就落空:Locals 用 `ClassInfo#findMethod(name, descriptor, access | INCLUDE_INITIALISERS)` 找正在变换的
方法,而 `ClassInfo.Member#matchesFlags` 要求"存成 private 的成员必须用 ACC_PRIVATE 查";OptiFine 重编译
`GameRenderer.getFov`(`method_3196`)时把它从 private 放宽成 public,于是对着缓存里那份**游戏**记录查不到。

**修复**:`GameTransformerHook` 记住**它实际替换掉**的类(Loader 自己打过补丁的类不在此列),安装完这些类之后按
**内部名**(斜杠形式)丢掉它们在 Mixin 缓存里的条目 —— 新文件 `mod/MixinClassMetadata.java`(113 行),每个类最多丢
一次,取不到缓存时只报告一次、不影响其它逻辑(过期元数据仍然能跑,只是那些解析类元数据的 mixin 照旧失败)。Mixin
于是按**它实际拿到**的字节重建元数据:正在变换的类通过 `ClassInfo#fromClassNode` 进入 Mixin 的目标上下文,而那只在缓存
里**没有**条目时才重建。

### 验证(2.2.1)

**离线**(两个产物各跑一次,`test-downloads\verify-26.ps1 -SkipBuild`,喂给它的就是这一版自己的 jar):26.2 →
`Prepared 562 patched classes (0 skipped, 0 failed)`、`verified OK: 562`、`FAILED: 0`、`ASM verifier problems: 0`;
26.1.2 → **567 / 567**、0 失败、ASM 0。OptiFine 自身的类两边都是 **879 / 879**(2 个 Forge-only 类不适用)、ASM 0;
`AtTargetScan PROBLEMS: 0`、`RefmapScan MISSING members: 0`、`RuntimeContractScan` broken 0 / lost overrides 0 /
unresolvable 0、`LambdaScan DANGLING handles: 0`。**两列数字与 2.2.0 记下的基线逐个相同** —— 丢缓存发生在补丁安装
**之后**,不参与字节码修复,所以补丁类数量与验证器结论都不该动。

**真机**:同一个修复在 **1.21.x 线**上真机端到端跑过(主界面、进世界、光影包在世界里编译);26.2 与 26.1.2 这两份
**没有**在真机上重跑,而丢缓存那一步只在启动期执行,离线校验覆盖不到它。

产物:`OptiFabric-Reforged-2.2.1+mc26.2.jar` — 180469 字节
`SHA-256: 5241EBBEF7C108244D90D0229C440B4F0E8B53E7292D21B401074C106079841F`

## 2.2.0+mc26.2 — 26.x 线的第五版(缺 OptiFine / 预览版过旧时给出提示,并从官方站直接下载)

> **次版本号递增的依据**(SemVer §7,规则见 [`docs/VERSIONING.md`](docs/VERSIONING.md)):改动表的这一格是
> 「向下兼容地新增功能或支持范围 → 次版本号」。这一版**新增了一个用户可见的功能**(缺 OptiFine 时的提示、
> 官方站下载、以及下载后的重启确认),**没有移除任何支持、也没有改变已有的行为**,所以是次版本号。
> 按 §3,已发布的 `2.1.1+mc26.2` 冻结(它那份 jar 与它那一节记录都原样留着),26.1.2 继续用 `2.0.0+mc26.1.2`。

**这一版是 1.21.x 那条线上同一功能的移植**,按 26.2 的客户端 API 改写(`GuiGraphicsExtractor`、`EditBox`、
`Component`、`setScreenAndShow`、`stop`、`rebuildWidgets`)。行为:

- **没有装 OptiFine**:每次启动都会给出提示;
- **装的是更旧的 preview**:每个构建只提示一次;
- **装的是 final(正式版)**:不论新旧**都不提示** —— final 是正式发布,不是"过旧的预览";
- **下载只走 OptiFine 官方站的两步流程**(`https://optifine.net/adloadx?f={file}`,页面见
  `https://optifine.net/downloads`),**保存前先校验**(校验器同时接受 `srg/net/optifine/Config.class` ——
  新版 OptiFine 的 jar 实际把 `Config` 放在那里);本包**不含、也不分发** OptiFine,也不指向任何第三方镜像
  (两个 README 里的镜像地址在这一版被删掉了);
- **Windows 上下载后自动重启**:用 JNA 的 `GetCommandLineW` + `CreateProcessW`(26.2 自带 JNA 5.17.0),
  因此 `build.gradle` 增加了两条 `compileOnly` 的 JNA 依赖 —— 只编译期使用,不进 jar。

**支持表**(`OptifineSupport`,数据来自 `https://optifine.net/downloads`,2026-10-02 读取):26.2 →
`preview_OptiFine_26.2_HD_U_K2_pre1`(26.2 **没有** final 构建,所以最新预览就是最新构建);26.1.2 →
`preview_OptiFine_26.1.2_HD_U_K1_pre2`。比较用的是 OptiFine 自己的版本串(`net.optifine.Config.VERSION`,
即文件名去掉 `.jar` 与 `preview_` 前缀),不是文件名的猜测。

新增的文件:`mod/OptifineSupport.java`(支持表与比较)、`mod/OptifineDownloader.java`(官方下载与重启)、
`mod/MissingOptifineScreen.java`(提示界面);`mixin/MixinTitleScreen.java` 与两个 README、`build.gradle` 同步更新。

### 2.2.0 这一版没有改的东西

**26.2 上光影仍不可用**:OptiFine 26.2 preview 自己取消了光影包加载,本模组照旧**原样留着**
(见下一节 2.1.1 的说明)。本版只增加提示与下载,没有碰那条判据。

**构建侧一个字没改**:26.x 这一线的目标版本只来自根目录 `gradle.properties` 的 `minecraft_version`(没有 `-Pmc`,
也没有 `-Pmod_version_base`)。这一版发布时它产出的是 **`OptiFabric-Reforged-2.2.0+mc26.2.jar`**;
`26.1.2` 那一份当时仍是冻结的 **`OptiFabric-Reforged-2.0.0+mc26.1.2.jar`**(内容不变,不重新构建、不重发,
按 §3 与 §5 记在 `release/publish.ps1` 的 `$modVersions` 例外值表里)。所以**那一版只有一个 jar**
(2.2.1 起两个 MC 版本各出一个 jar)。

### 验证(2.2.0)

**离线**(`powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-26.ps1 -Version 26.2`):
`Prepared 562 patched classes (0 skipped, 0 failed)`、`verified OK: 562`、`FAILED: 0`、`ASM verifier problems: 0`;
OptiFine 自身的类 **879 / 879**(2 个 Forge-only 类不适用)、ASM 0;`AtTargetScan PROBLEMS: 0`、
`RefmapScan MISSING members: 0`、`RuntimeContractScan` broken 0 / lost overrides 0 / unresolvable 0、
`LambdaScan DANGLING handles: 0`。**数字与 2.1.1 记下的基线逐个相同** —— 本版改的是启动期提示与下载,
不进补丁管线,所以 562 这个数不该动。

同一份源码的 26.1.2 口径(`-Version 26.1.2`):**567 / 567**、0 失败,四个扫描器同样全 0,与 2.0.0 / 2.1.1
记下的基线相同(2.2.0 没有碰那一版的产物)。这一条是拿**上面这个 jar 本体**跑出来的(2.2.0 只有一份源码,
`build\libs` 里也只有 26.2 一个产物;`verify-26.ps1` 按 `<版本>+mc<MC>` 找 jar,所以那份 jar 临时复制成
`+mc26.1.2` 的名字喂给它) —— 它证明的是**这份代码在 26.1.2 的客户端与 OptiFine 上照样全绿**,而不是"又构建了一个
26.1.2 的产物"。26.1.2 那一版的产物在当时仍是冻结的 `2.0.0+mc26.1.2`(2.2.1 起它也出自己的 jar)。

提示与下载这条路径是**运行期**行为(要在真机上点了按钮才算数),离线校验只能证明补丁管线没被改坏。

产物:`OptiFabric-Reforged-2.2.0+mc26.2.jar` — 178300 字节
`SHA-256: 3D6248F2E246B56F084BEFE279358C317E2DE32CFEB5DA2A3AADD76A39A2F7BC`

## 2.1.1+mc26.2 — 26.x 线的第四版(撤回 2.1.0 那次"修光影"的改动:26.2 上光影用不了,强行打开还会让世界画不出来)

> **修订号递增的依据**(SemVer §7,规则见 [`docs/VERSIONING.md`](docs/VERSIONING.md)):改动表的这一格是
> 「修 fixer、修兼容性、修正元数据/文档 → 修订号」。这一版**不移除任何支持、也不新增支持范围**,只是把 2.1.0 里
> 那次改坏了的修复撤回,所以是修订号。按 §3,已发布的 `2.1.0+mc26.2` 冻结(**它那份 jar 与它那一节记录都原样留着**),
> 26.1.2 继续用 `2.0.0+mc26.1.2`。

**结论先说:26.2 上光影不可用,这是这一版明确记下的限制。** OptiFine 的视频设置里仍然可以选光影包,但选了
**不会有任何效果**,也不会报错提醒你 —— OptiFine 26.2 preview(`preview_OptiFine_26.2_HD_U_K2_pre1`)在自己的
`Shaders.loadShaderPack` 里取消了这次加载,日志里是 `[Shaders] No shaderpack loaded.`。世界本身打开、渲染正常。

### 2.1.0 的缺陷:把光影包加载强行打开,结果世界只画粒子

2.1.0 认出了 OptiFine 26.2 那份构建里的形状:它把 `true` 存进 "cancelled" 标志位之后就跳过 `getShaderPack()`,
于是**任何光影包都加载不了**,连 OptiFine 自带的那份也不行(`shaderPack=(internal)` 同样)。2.1.0 的做法是
**把那一对赋值删掉、把加载强行打开**。真机测量证明这一步是错的,而且比它要修的缺陷更糟:

| 运行(同一实例、同一份 `optionsshaders.txt`、同一个光影包) | 日志 | 画面 |
|---|---|---|
| 留着 OptiFine 的取消(= 2.1.1) | `[Shaders] No shaderpack loaded.` | **世界正常渲染** |
| 强行打开(= 2.1.0) | `[Shaders] Loaded shaderpack: ComplementaryReimagined_r5.9.1.zip` | **只有粒子,方块透明** |

选了光影包(`ComplementaryReimagined_r5.9.1.zip`)之后,世界**只画粒子、方块是透视的**,同时编译了
**27 个 shader program**,而**任何日志里都没有报错** —— 不是崩溃,是安静的画错。同一个实例把 `shaderPack=OFF`
(或者把 OptiFine 的取消留着不动)就画得正常。

### 2.1.1 的改动

`src/main/java/kynarain/cn/optifabric/mod/OptifineJarFixer.java` 的 `enableShaderPackLoad` **只保留 1.21.6 / 1.21.7
那种形状原有的判据**(`ICONST_1, ISTORE, ILOAD, IFNE` 四步**紧邻**),**为 26.2 那种形状写的扩展判据整段删除**。
26.2 那种形状(赋值与判断**之间**夹了一次 `shaderPack` 配置读取)因此**原样保留 OptiFine 写的样子**,不再被改动;
这次测量的结论同时写在**类注释**与那段**行内注释**里。同一个方法里 1.21.6 / 1.21.7 那种形状的修复**不变**
(那是另一种字节码形状,照旧把加载补回来)。

### 验证(2.1.1)

**离线**(`powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-26.ps1 -Version 26.2`):
`Prepared 562 patched classes (0 skipped, 0 failed)`、`verified OK: 562`、`FAILED: 0`、`ASM verifier problems: 0`;
`AtTargetScan PROBLEMS: 0`、`RefmapScan MISSING members: 0`、`RuntimeContractScan` broken 0 / lost overrides 0 /
unresolvable 0、`LambdaScan DANGLING handles: 0`。

同一份源码的 26.1.2 口径(`-Version 26.1.2`):**567 / 567**、0 失败,四个扫描器同样全 0 —— 与 2.0.0 记下的基线
逐个数字相同,2.1.1 没有碰那一版的产物。

**真机**:留着 OptiFine 的取消不动时,日志是 `[Shaders] No shaderpack loaded.`,**世界正常渲染** —— 这就是这一版
在 26.2 上预期的状态;流水线照旧跑通(`[OptiFabric] Prepared 562 patched classes (0 skipped, 0 failed)`)。

**已知限制**:

- **26.2 上光影不可用**:OptiFine 26.2 是 **preview** 构建,它的光影管线还没做完,而那句"取消加载"正是让它不插手
  画面的开关;在 OptiFine 里选光影包不会有任何效果(也不会有报错提示你);
- **26.2.1 与 26.3 支持不了** —— 这两版游戏确实存在,但 OptiFine 至今没有为它们发布任何构建,没有可移植的对象;
- 26.1.2 不受影响:那一版的 `2.0.0+mc26.1.2` 原样不变,光影在那一版上是可用的。

产物:`OptiFabric-Reforged-2.1.1+mc26.2.jar` — 138982 字节
`SHA-256: D9286B81851F0473C4DAEA2DFE06D216CF2315681F6116145086E4C3F1B6043E`

## 2.1.0+mc26.2 — 26.x 线的第三版(新增 Minecraft 26.2 支持)

> ⚠️ **这一版有一个已被取代的缺陷(见上一节)**:它把 OptiFine 26.2 preview 对光影包加载的取消删掉了、
> 把加载强行打开;真机测量下来,选了光影包之后世界**只画粒子、方块透明**。请改用 **`2.2.3+mc26.2`** ——
> 本节其余内容(26.2 的移植记录、离线数字、产物尺寸与摘要)按 §3 原样保留,属于历史记录。

> **次版本号递增的依据**(SemVer §7,规则见 [`docs/VERSIONING.md`](docs/VERSIONING.md)):改动表的这一格是
> 「向下兼容地新增功能或支持范围 → 次版本号(支持新 OptiFine 构建)」。这一版把 26.x 这条线从 26.1.2 **扩到 26.2**,
> 没有丢开任何版本:同一份源码用 `-Version 26.1.2` 跑完整条离线管线,数字与 2.0.0 当时的基线**逐个相同**
> (567/567、0 失败、扫描器全 0),所以这是**支持范围的扩大**,不是主版本号级别的取舍。
> 已发布的 `2.0.0+mc26.1.2` 内容不变(§3),26.1.2 的用户继续用它。

**Minecraft 26.2**(2026-09-15 发布)/ Fabric Loader 0.19.5 / **Java 25** / 需要 OptiFine
`preview_OptiFine_26.2_HD_U_K2_pre1.jar`(2026-09-22 发布的 **preview** 构建)。

26.2 与 26.1.2 一样**未混淆**(官方名即运行名,没有 yarn、也没有真正可用的 intermediary),所以构建侧一个字都没改:
仍然是 Loom 的非重映射 flavour,目标版本只来自根目录 `gradle.properties` 的 `minecraft_version=26.2`。

```powershell
.\gradlew build                  →  OptiFabric-Reforged-2.1.0+mc26.2.jar
```

### 26.2 上移动或改名的四处(逐处都定位到了字节码)

1. **`Minecraft.setScreen` 没了**:26.2 把它换成了 **`setScreenAndShow`**,本模组 `MixinTitleScreen` 里那次调用直接
   编译失败(报 `找不到符号: 方法 setScreen(ConfirmScreen)`)。26.1.2 上**两个名字都在**,所以改用
   `setScreenAndShow` 之后两边都能编译 —— 这是本版唯一一处改写的 mixin 目标;
2. **`extractBlockOutline` 搬到了新类**:26.2 把整趟关卡渲染状态的处理抽进
   `net/minecraft/client/renderer/extract/LevelExtractor`,Fabric API 跟着搬(它的处理器现在是
   `LevelExtractorMixin.hasMaterialFlagProxy`,不再是 `LevelRendererMixin`),于是按 `LevelRenderer` 注册的旧判据
   不再命中 —— `AtTargetScan` 报 `[NO INSTRUCTION] …LevelExtractor.extractBlockOutline(…Camera;…LevelRenderState;)V
   has no INVOKE of …BlockStateModel.hasMaterialFlag(I)Z`。`OptifineFixer` 为 `LevelExtractor` 补了同样的
   `RestoreVanillaMethodsFix(true, "extractBlockOutline")` + `DropVanillaAbsentOverloadsFix("extractBlockOutline")`
   (OptiFine 又一次把原版方法体削成转发给自己三参重载的薄壳)。两条判据**同时保留**:26.1.2 上没有
   `LevelExtractor` 这个类,那条新判据在那一边是空操作;
3. **区块重建任务类改名**:26.2 把 `SectionRenderDispatcher$RenderSection$RebuildTask` 改名为 **`…$CompileTask`**。
   OptiFine 重命名的那个 `SectionCompiler.compile(…ChunkCacheOF…, III)` → `optifabric$compile` 靠
   `CallSiteRedirectFix` 把调用者一起改过去,而这条 fixer 按类名注册:不跟着改名,被改名的重载就只剩一个还在喊
   旧名字的调用者 —— 真机上表现为**开存档后的第一帧**(第一次区块重建)抛 `NoSuchMethodError`。
   `RuntimeContractScan` 报的正是它:`[patched caller] …$CompileTask.doTask(…) -> SectionCompiler.compile(SectionPos,
   ChunkCacheOF, VertexSorting, SectionBufferBuilderPack, III)`,且 `unpatched callers 0, optifine callers 0`。
   现在这条 `CallSiteRedirectFix` 也注册在 `CompileTask` 上(同样与 `RebuildTask` 那条并存,缺失的目标是空操作);
4. **光影包一个都加载不了(OptiFine 26.2 preview 自身的缺陷)**:这一版的 `Shaders.loadShaderPack` 把 `true` 存进
   "cancelled" 标志位之后就跳过了 `getShaderPack()`,于是**任何光影包都加载不了**,连 OptiFine 自带的那份也不行。
   与 1.21.6 / 1.21.7 那次同源,差别在 26.2 会在赋值与判断**之间**读一次 `shaderPack` 设置项,所以老判据要求的
   `ICONST_1, ISTORE, ILOAD, IFNE` 四步紧邻不再成立;2.1.0 因此**另外锚定那次被守护的 `getShaderPack(String)` 调用**,
   再往前回溯到赋值处删掉那一对指令,把加载强行打开。实测(同一实例布局、同一份 `optionsshaders.txt`、同一个光影包):
   26.2 未修时日志是 `[Shaders] No shaderpack loaded.`(`shaderPack=ComplementaryReimagined_r5.9.1.zip` 与
   `shaderPack=(internal)` 都一样),而 26.1.2 是 `[Shaders] Loaded shaderpack: ComplementaryReimagined_r5.9.1.zip`;
   强行打开之后 26.2 打出同一行 `Loaded shaderpack` 并编译 27 个 program。**但这一步是错的**,而且比它要修的缺陷更糟:
   选了光影包的世界**只画粒子、方块透明**,27 个 program 编译完成、任何日志里都没有报错(同一个实例把 `shaderPack=OFF`
   就画得正常)。**所以 2.1.1 把这条扩展判据整段删掉,26.2 那种形状原样留给 OptiFine,光影在 26.2 上明确记为不可用**
   (见本文件顶部 2.1.1 那一节)。1.21.6 / 1.21.7 那种形状的修复不变。

### 要求与实测

| 项 | 值 |
|---|---|
| Minecraft | 26.2(**本版的目标**;26.1.2 仍由同一份源码支持) |
| Fabric Loader | >= 0.19.5 |
| Java | **25**(与 1.21.x 的 Java 21 不同,26.2 本身要求 25) |
| OptiFine | `preview_OptiFine_26.2_HD_U_K2_pre1.jar`(目前只有 preview) |
| Fabric API | 0.161.0+26.2 |

**离线校验**(`powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-26.ps1 -Version 26.2`):
被补丁的游戏类 **562 / 562**(`Prepared 562 patched classes (0 skipped, 0 failed)`)、`FAILED: 0`、
`ASM verifier problems: 0`;OptiFine 自身的类 **879 / 879**、0 失败(2 个 NeoForge-only 类不适用);
`AtTargetScan PROBLEMS: 0`、`RefmapScan MISSING members: 0`、`RuntimeContractScan` 0/0/0、`LambdaScan DANGLING: 0`。

同一份源码的 26.1.2 口径(`-Version 26.1.2`):`Prepared 567 patched classes (0 skipped, 0 failed)`、
`verified OK: 567`、0 失败,四个扫描器同样全 0 —— 与 2.0.0 记下的基线**逐个数字相同**。

**真机**(`test-downloads\launch-26.ps1 -Version 26.2 -World OptiTest`):流水线跑通
(`[OptiFabric] Prepared 562 patched classes (0 skipped, 0 failed)`)、世界打开(`Starting integrated minecraft server`)、
无崩溃、无 mixin 变换失败,窗口标题 `Minecraft* 26.2 - 单人游戏`。当时的日志里是
`[Shaders] Loaded shaderpack: ComplementaryReimagined_r5.9.1.zip`、`Program loaded` 27 个 —— **这一行正是那个缺陷**:
被强行打开的加载确实把光影程序编译起来了,但画面只画粒子、方块透明(见本节第 4 条)。把这句取消留着不动时是
`[Shaders] No shaderpack loaded.`,世界正常渲染。

**已知限制**:26.2 的 OptiFine 是 **preview** 构建(`HD_U_K2_pre1`,与 26.1.2 当时一样);光影**在 26.2 上用不了**
(选了包也不会有任何效果,详见本节第 4 条与顶部 2.1.1 那一节),只在 26.1.2 上可用;**26.2.1 与 26.3 支持不了** ——
这两版游戏确实存在,但 OptiFine 至今没有为它们发布任何构建,这也是这条线停在 26.2 的原因。

产物:`OptiFabric-Reforged-2.1.0+mc26.2.jar` — 139528 字节
`SHA-256: ED3DD297FBE7356C9A9C69DCAAD9FC3C2AC9000C12FEB7BCF3CE9D3277028D8D`

## 1.1.2+mc1.21.3 … 1.1.2+mc1.21.11 — 抗锯齿全线修复,并纠正 1.1.1 里的错误结论

> **这一版覆盖 8 个产物**:1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 / 1.21.9 / 1.21.10 / 1.21.11(都叫 1.1.2)。
> **1.21 与 1.21.1 不受影响,继续停在 1.1.0** —— 那两版的 OptiFine 只带老位置的后处理链,当时的修复根本没碰过它。
> 每个 jar 的版本号只描述它自己那份产物(SemVer §3),所以这是"8 个产物一起升到 1.1.2",不是把整条线重发;
> 1.21.11 那一份在**行为上**与 1.1.1 相同(它那时已经修好),但字节不同(管线不再做那次后处理链修复),
> 因此同样按新版本发布。

1.1.1 修的是 1.21.11 换光影包时的"重载资源失败"。当时判断问题只出在这一版,并写下"1.21.6–1.21.10 上 OptiFine
读老位置、实测可用"。**逐版本真机实测证明那句话是错的。**

### 实测:缺陷覆盖 1.21.3 起的几乎所有版本

每个版本都用**生产 jar**跑一遍(抗锯齿 2x + `ComplementaryReimagined_r5.9.1.zip` + 进世界,75–140 秒后读
`logs/latest.log`):

| MC | `Resource not found: minecraft:post_effect/fxaa_of_*` | 同会话里的 `Failed to load post chain` |
|---|---|---|
| 1.21 / 1.21.1 | —(修复没碰过这两版) | — |
| 1.21.3 | **2 条** | 未出现(那次没切抗锯齿) |
| 1.21.4 | **2 条** | 同上 |
| 1.21.6 | 未测到(无光影时不出现此警告) | 无光影:0 条;**启用光影则启动阶段崩溃**(见下方注) |
| 1.21.7 | **2 条** | 未出现 |
| 1.21.8 | **2 条** | **2 条**(用户 09-12 的会话) |
| 1.21.9 | **2 条** | 未出现 |
| 1.21.10 | **2 条** | **2 条**(用户 09-12 的会话) |
| 1.21.11 | 0(1.1.1 已修) | 0 |

> **1.21.6 那一行的更正(2026-09-13 本机复现)**:当时"量不到"是因为那台机器上这两版**启用光影即崩**,
> 而不是它们根本起不来 —— 用 `1.1.2` 生产 jar 复测:**不开光影时可以正常启动**(标题界面正常渲染、无崩溃报告),
> 只有**选了光影包**才会在启动阶段崩于 OptiFine 自身的 `NullPointerException ... "multiTex" is null` at
> `net.optifine.shaders.ShadersTex.initDynamicTextureNS`(把 `shaderPack=` 清空即恢复;只开 `ofAaLevel:2` 不崩)。
> 详见 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) 的"仍待办的两项 A"。

代码层面它对得上:OptiFine 补丁过的 `ShaderManager`(`class_10151`)里注册 FXAA 链的方法,在
1.21.8 / 1.21.9 / 1.21.10 / 1.21.11 上**逐条指令、逐条常量完全一致**,字符串配方就是 `post_effect/\u0001.json`;
这套代码**从 1.21.3 起就有**。而当时的修复**从 1.21.3 起一直在删那个文件** —— 所以它从一开始就是错的,
只是 1.21.11 先被真机撞到。

> 1.21.9 / 1.21.10 那条"抗锯齿黑屏已修好"的结论同样是错的:删掉链之后链**加载失败**,抗锯齿不再被应用,
> 于是不黑屏了 —— 看起来像修好,其实是**静默失效**。真正的原因是顶点着色器,见下。

### 本版做了什么

1. **不再碰 OptiFine 的后处理文件**:`OptifinePostChainFixer` 整个删除。链本来就该由游戏按
   `minecraft:fxaa_of_2x` 从 `post_effect/` 解析(OptiFine 把 `ShaderManager` 改成在那儿注册),OptiFine 自带的
   那份就是它要的:补写老位置文件多余,删掉新位置文件是错的。这一条同时消掉"每次资源重载刷警告"与
   "一动抗锯齿就弹重载资源失败"。
2. **`OptifineJarFixer` 增加顶点着色器修复**:游戏从 1.21.9 起改用 `gl_VertexID` 生成全屏三角形、**不再提供
   `Position` 顶点属性**,而 OptiFine 1.21.9 / 1.21.10 那份 `fxaa_of_*.vsh` 还在读 `Position`(顶点塌成一点 ——
   这才是当初"一开抗锯齿整屏黑"的真正原因)。管线把这两个 `.vsh` 改写成同一套全屏三角形写法,
   `SamplerInfo` / `FxaaConfig` 两个 uniform 块与 FXAA 的 `posPos` 计算**原样保留**。判据是**文件内容**
   (游戏那份 `core/screenquad.vsh` 是否用 `gl_VertexID` + OptiFine 那份是否还在读 `Position`),所以
   1.21.3–1.21.8 的 `.vsh` 与 1.21.11 那份(sp614x 自己已经修好)**都不动**。

缓存格式 25 -> **26**。

> 日志里会重新出现一对 `Resource not found: minecraft:shaders/post/fxaa_of_{2,4}x.json` —— 那是 OptiFine 每次
> 还去 1.21.6 之前的**老位置**探一次它的链(1.21.8 起的构建早就不带那个文件),纯探测警告:链走的是
> `post_effect/` 那条,不补写老位置文件反而少一个没人读的资源。用户已确认抗锯齿正常的 1.21.11(1.1.1)会话里
> 同样有这两条。

### 离线校验(每个受影响版本一条命令)

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-version.ps1 -Version 1.21.10 -ModVersion 1.1.2
```

| MC | 补丁类(JVM+ASM) | OptiFine 类(JVM+ASM) | ASM 问题 | @At 注入点 | 其余扫描器 |
|---|---|---|---|---|---|
| 1.21.3 | 440 / 440 | 816 / 816 | 0 | 2(已停用 indigo) | 全 0 |
| 1.21.4 | 474 / 474 | 812 / 812 | 0 | 2 | 全 0 |
| 1.21.6 | 487 / 487 | 820 / 820 | 0 | 4 | 全 0 |
| 1.21.7 | 500 / 500 | 823 / 823 | 0 | 4 | 全 0 |
| 1.21.8 | 516 / 516 | 831 / 831 | 0 | 4 | 全 0 |
| 1.21.9 | 519 / 519 | 832 / 832 | 0 | 4 | 全 0 |
| 1.21.10 | 553 / 553 | 836 / 836 | 0 | 4 | 全 0 |
| 1.21.11 | 570 / 570 | 874 / 874 | 0 | 4 | 全 0 |

真机:八个版本各用修好的 jar 重跑探针(`test-downloads\probe-1.21-aa.ps1`),
`Resource not found: minecraft:post_effect/fxaa_of_*` **全部为 0 条**,全部进到标题界面。

八个产物各自的字节数与 SHA-256 见 `dist/README.txt` 与对应版本的 `release/notes/mc<版本>.md`。

## 1.1.1+mc1.21.11 — 只覆盖 1.21.11 的一版(抗锯齿后处理链)

> **为什么只跳一格、而且只覆盖一个 MC 版本**:1.21.x 这条线是"一份源码、每个 MC 版本一个 jar",每个 jar 的版本号
> 描述的是**它自己那份产物的内容**(SemVer §3:已发布的版本号不能改内容,只能发新版本)。这次的行为改动只发生在
> 1.21.11 上(判据按版本取值),其余九个 1.1.0 的 jar 内容没变、不需要重建,所以只有 1.21.11 从 1.1.0 升到 1.1.1,
> `v1.1.0` 那个发布条目原样不动。逐 MC 版本升版只用一条命令,见 [`docs/VERSIONING.md`](docs/VERSIONING.md)。

**修复:切换光影包时提示"重载资源失败"**(`Resource not found: minecraft:post_effect/fxaa_of_2x.json`,
可能还带 `Could not find post chain with id: minecraft:fxaa_of_2x`)。

根因在**我们自己那条抗锯齿修复**上:1.21.8 起的 OptiFine 构建不再带老位置的
`assets/minecraft/shaders/post/fxaa_of_2x.json`,于是管线按老办法**补写**它、并把游戏新位置的
`assets/minecraft/post_effect/fxaa_of_2x.json` **删掉**(1.21.6–1.21.10 上 OptiFine 确实读老位置的那条链,
实测可用)。但 1.21.11 的后处理链已改由**游戏自己的加载器**解析 —— 它按 `minecraft:fxaa_of_2x` 去
`post_effect/` 取文件,而那个文件被我们删了:每次资源重载日志里都留一条 `Resource not found`,
需要这条链时(开抗锯齿 / 选光影包)直接失败并弹窗。

修法:1.21.11 上**原样保留** OptiFine 自带的新位置文件 —— 不补写老位置的那个,也不删它
(`OptifinePostChainFixer.fix(jar, keepGameChains)` 的新分支);1.21.6–1.21.10 保持老做法不变。
这个版本列表是**逐版本实测**得出的(静态特征不可用:每个 1.21.x 原版 jar 里都只有 `post_effect/` 一种位置,
OptiFine 的类里也没有路径字面量),依据写在 `OptifineSetup.POST_EFFECT_RELEASES` 旁边。
缓存格式号同时升到 `25`(管线产物变了),升级后首次启动会重建 `.optifine/`。

**离线校验**(`powershell -File test-downloads\verify-version.ps1 -Version 1.21.11 -ModVersion 1.1.1`):
被补丁的游戏类 **570 / 570**、OptiFine 自身的类 **874 / 874** 通过 JVM 校验,ASM 数据流验证器 **0 问题**;
扫描器:注入点 4 条(全部属于**已停用**的 indigo)、mixin 成员引用缺失 0、契约/覆写/引用 0/0/0、invokedynamic 句柄 0 悬空。

**真机确认**(2026-09-13,把 1.1.0 换成 1.1.1 并清空 `.optifine/`):启动、资源重载、切光影包、开关抗锯齿都正常;
1.1.0 上每次资源重载都出现的 `Resource not found: minecraft:post_effect/fxaa_of_2x.json` 与
`Failed to load post chain: minecraft:fxaa_of_2x` **都不再出现**,整轮 `[ERROR]` 0 条。

产物:`OptiFabric-1.1.1+mc1.21.11.jar` — 876446 字节
`SHA-256: 9E78C98FC0FC568C453ACA880FE167545192021D16C4A2DA0E4127AC5F3A9143`

> 这一版的范围判断("问题只在 1.21.11")与上面"1.21.6–1.21.10 读老位置、实测可用"那句是**错的**,1.1.2 已经纠正;
> 这一节保留原样,作为当时的记录。已发布的 `1.1.1+mc1.21.11` 内容不变(§3)。

## 2.0.0+mc26.1.2 — 26.x 线的第二版(mod id 改名 + 实时几何)

> **主版本号递增的依据**(SemVer §8,规则见 [`docs/VERSIONING.md`](docs/VERSIONING.md)):这一版把 mod id 从
> `optifabric` 改成 `optifabric_reforged`,对任何 `depends`/`breaks` 那个 id 的东西都是**不兼容修改** ——
> 升级时请**删掉旧的 `OptiFabric-1.2.0+mc26.1.2.jar`**,换成 `OptiFabric-Reforged-2.0.0+mc26.1.2.jar`。
> 同一版里那些"向下兼容的新功能"被主版本号一并吸收(§8:主版本号递增时次版本号与修订号归零)。

**Minecraft 26.1.2** —— 26.1 起游戏**未混淆**,这是一条与 1.21.x 完全独立的线,两边的 jar **不能互相替代**。

**这一线的 mod id 与显示名也是它自己的**:`optifabric_reforged` / **OptiFabric Reforged**(1.21.x 仍为 `optifabric`,已发布的 1.1.0 不动)。理由是实际的:有模组声明 `"breaks": {"optifabric": "*"}`(LambdaBetterGrass 就是),而 Fabric Loader 按 **id** 匹配 —— 只改显示名无效;独立 id 之后这类声明不再拦 26.x。实测上游**未修改**的 LBG jar 可与本模组共存,更好的草与连接纹理正常。

官方名就是运行名,既没有 yarn 也没有真正的 intermediary 可重映射(26.1.2 只发布占位 `intermediary:0.0.0`)。
因此 26.x 用 Loom 的**非重映射** flavour(`net.fabricmc.fabric-loom`)、不写 `mappings`、运行期命名空间是
`official` 而不是 `intermediary`;1.21.x 那套按 `class_XXXX` 注册的 fixer 判据在这一线指向的类**根本不存在**,
所以这条线只有一张"官方名"注册表(`registerOfficialNameFixes`),intermediary 那一张在 1.21.x 自己的分支上。

```powershell
.\gradlew build                  →  OptiFabric-Reforged-2.0.0+mc26.1.2.jar
```

### 本版修复

- **注入点(最主要的一类)**:OptiFine 重编译时把原版方法**削成薄壳**、真正的实现搬进它自己加的重载,
  而 Fabric API 用**不带描述符**的 `method = "..."` 指名目标 —— 恢复原版方法体之后类里出现两个同名方法,
  MixinExtras 建不出局部变量上下文,整个类变换失败。逐处消歧:`LevelRenderer`、`SectionCompiler`、
  `CuboidItemModelWrapper`、`ScreenEffectRenderer`、`ModelManager`;
- **渲染器(FRAPI)**:26.1 把 Fabric 渲染器 API 挪进了 `api.client.renderer.v1`,按旧名字查找失败曾让占位
  **从未注册**,第一个 `Renderer.get()` 就把游戏带走;更根本的是,从 1.21.x 继承来的
  `fabric-renderer-api-v1:contains_renderer` 让位键在这一线**本来就是多余的** —— 26.1.2 的 indigo 已经不是
  地形渲染器(地形与提交节点的整合搬进了 `fabric-renderer-api-v1` 自己,它只剩 1 条物品 mixin + 2 个 accessor),
  键一声明就把 Fabric API 唯一能问到的那个渲染器关掉,模组生成的网格进了惰性占位 —— **不报错,也不显示**。
  本版**不再声明该键**,`RendererApiFallback` 把它读回来、只在确实被声明时才补占位器,否则由 Indigo 注册自己的
  `IndigoRenderer`(F3 的 `Renderer:` 一行因此显示 `IndigoRenderer`,而不再是 `OptifineRendererPlaceholder`);
- **实时几何(FRAPI)**:Fabric 的地形渲染钩子注入在原版 `compile` 的 `BlockPos.betweenClosed` 循环上,而 OptiFine
  的区块构建方法里那个循环**一次都不出现** —— 钩子只落在没人调用的补丁方法里,注入成功、永不执行,于是需要按位置
  实时生成几何的模型(例如 LambdaBetterGrass 的"更好的草")`emitQuads` 一次都没被问过、几何**静默消失**。
  新增 `FrapiTesselateBridgeFix` + `OptifineFrapiBridge`:把 OptiFine 循环里那次方块 tessellate 调用改到桥上,
  几何由 Fabric 的渲染器产出(AO / 染色 / 光照齐),**顶点交给 OptiFine 传进来的 `BlockQuadOutput` 写** ——
  顶点格式、层级缓冲、光照与光影属性全归 OptiFine,桥上不碰任何顶点缓冲(第一版直接写区块缓冲,会把整层数据写成垃圾,
  表现为"一切方块透明",原因见 `docs/PORT_26.x.md` 第 5 节)。只路由 `emitQuads` 声明在游戏之外的模型,
  原版方块一律留在 OptiFine 的路由上(否则光影下会把 OptiFine 的额外顶点属性弄丢:实测发黑/光照怪);
- **抗锯齿**:26.x 从 `post_effect/` 读后处理链,而 1.21.x 那套修复的做法是**删掉该文件**、补写老位置的链 ——
  在这一线正好是反的。当时按版本线分开处理;
- **渲染路径**:移动方块与普通方块模型这两处 Fabric API 钩子仍改为惰性(由原版/OptiFine 路径绘制);方块破坏
  裂纹那条没有动,它现在真的走 Indigo 的渲染器。

### 要求与实测

| 项 | 值 |
|---|---|
| Minecraft | 26.1.2(**只支持这一个版本**) |
| Fabric Loader | >= 0.19.5 |
| Java | **25**(与 1.21.x 的 Java 21 不同,26.1.2 本身要求 25) |
| OptiFine | `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar`(目前只有 preview) |
| Fabric API | 0.155.3+26.1.2 |

实测:启动、进世界、方块/物品/生物渲染、抗锯齿、光影、多人全部正常;进世界时 Indigo 注册真正的渲染器
(`[Indigo] Registering Indigo renderer!`),Fabric API 自己的渲染钩子从它上面绘制;**装 LambdaBetterGrass 实测:
"更好的草"正常、连接纹理正确(光影开启)**。

产物:`OptiFabric-Reforged-2.0.0+mc26.1.2.jar` — 177166 字节
`SHA-256: FBB432C2D9C8B0E7E06F0FDA4A0C1B6A8F302D5D09ABD7CE67F13CBE04A5CF60`

## 1.2.0+mc26.1.2 — 26.x 线的第一版(已发布 2026-09-12)

[GitHub release](https://github.com/Kynarain/OptiFabric-Reforged/releases/tag/v1.2.0) ——
`OptiFabric-1.2.0+mc26.1.2.jar`,163164 字节,
`SHA-256: 672F3895AB656FACDA42C93218F885BA21487D929A92C9E05D542A4D0A20B64A`

当时的状态:26.x 走通了"未混淆 + 官方名"的独立构建与运行期路径,能构建出一个可玩的 jar;但 mod id 仍是
`optifabric`,Fabric 的渲染器由一个**惰性占位**顶着(Indigo 让位),**需要按方块位置实时生成的几何画不出来**
—— 设置与取舍见 [`docs/PORT_26.x.md`](docs/PORT_26.x.md) 第 2、3 节。`2.0.0` 修掉的正是后者。

## 1.0.0+mc1.21 … 1.0.0+mc1.21.11 — 1.21.x 全系列

**一份源码,覆盖 OptiFine 出过 1.21.x 构建的全部 10 个版本**,每个版本一个 jar:

```
.\gradlew build "-Pmc=1.21.8"     →  OptiFabric-1.0.0+mc1.21.8.jar
```

（PowerShell 里必须给参数加引号,否则 `1.21.8` 会被拆成 `1`。不带参数则构建 `gradle.properties` 里的默认版本。）

每个 jar 都绑定了**自己那个版本**的 `official→intermediary` 映射表(官方混淆名每版不同,用错版本会把 OptiFine 重映射成乱码),`fabric.mod.json` 里的 `minecraft` 依赖也精确到该版本。

### 各版本与实测搭配

| MC | 产出的 jar | OptiFine 构建 | 建议 Fabric API |
|---|---|---|---|
| 1.21 | `OptiFabric-1.0.0+mc1.21.jar` | `preview_OptiFine_1.21_HD_U_J1_pre9.jar`(只有 preview) | 0.99.5+1.21 |
| 1.21.1 | `OptiFabric-1.0.0+mc1.21.1.jar` | **`OptiFine_1.21.1_HD_U_J1.jar`** | 0.116.9+1.21.1 |
| 1.21.3 | `OptiFabric-1.0.0+mc1.21.3.jar` | **`OptiFine_1.21.3_HD_U_J2.jar`** | 0.114.1+1.21.3 |
| 1.21.4 | `OptiFabric-1.0.0+mc1.21.4.jar` | **`OptiFine_1.21.4_HD_U_J3.jar`** | 0.119.4+1.21.4 |
| 1.21.6 | `OptiFabric-1.0.0+mc1.21.6.jar` | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` | 0.128.2+1.21.6 |
| 1.21.7 | `OptiFabric-1.0.0+mc1.21.7.jar` | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` | 0.129.0+1.21.7 |
| 1.21.8 | `OptiFabric-1.0.0+mc1.21.8.jar` | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` | 0.136.1+1.21.8 |
| 1.21.9 | `OptiFabric-1.0.0+mc1.21.9.jar` | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` | 0.134.1+1.21.9 |
| 1.21.10 | `OptiFabric-1.0.0+mc1.21.10.jar` | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` | 0.138.4+1.21.10 |
| 1.21.11 | `OptiFabric-1.0.0+mc1.21.11.jar` | **`OptiFine_1.21.11_HD_U_J9.jar`** | 0.141.6+1.21.11 |

（OptiFine 没出过 1.21.2 / 1.21.5 的构建,所以这两版没有对应 jar。）

### 为什么一套源码够用,以及它需要什么

- **intermediary id 在 1.21.x 各版本之间是稳定的** —— 对比 1.21.10 与 1.21.11 的映射,本移植用到的类/方法 id 两边都在。fixer 全部以 intermediary 名义注册,于是能跨版本复用。
- fixer 的行为是"找到就修、找不到就跳过",而且 `RestoreVanillaMethodsFix` 这类读的是**当前版本游戏 jar 里的原版字节码**,不存在"把 1.21.11 的方法体塞进 1.21.4"的问题。版本差异退化为"某些 fixer 在某版本不触发"。
- 但**写死描述符的 fixer 会在别的版本上静默失效**:`class_761.method_62210` 在 1.21.8 收一个 `Camera`、在 1.21.11 收一个 `Vec3d`。`StubInjectionTargetFix` 与 `CallSiteRedirectFix` 因此改成**按方法名匹配、描述符可选**,并用"实际找到的那个方法"的描述符去改调用点 —— 否则 1.21.8 上的方块描边钩子会落回活代码,而它要的注入点在 OptiFine 重编译后的方法体里并不存在(真机必崩)。
- 同为"让不兼容的钩子注入进死代码"那招,**死代码副本现在用原版方法体**(Mixin 本来就是照着原版写的,OptiFine 会把方法体内的调用改写掉;副本是死代码,身体只需要"长得像原版")。
- 版本特有的成员缺失(仅 ≤1.21.5)由 `RestoreVanillaMethodsFix` 补:`class_1088.method_65737`(1.21.4)/`method_61072`(1.21.1)、`class_329.method_55806/55807/55808`、`class_309.method_1454`。
- `KeyboardFix`(1.20.6 线上用过)重新启用并**改成容错**:1.21/1.21.1 还有 `method_1454`(OptiFine 的构建把它丢了,而 fabric-screen-api-v1 往它里面注入),1.21.6 以后根本没有这个方法。上游找不到方法时**直接抛异常**,改成"只 revert 该版本确实存在的那些"之后,同一个 fixer 能同时服务 1.21 到 1.21.11。

### 验证(每个版本都跑完整离线链路)

| MC | 补丁类(JVM+ASM) | OptiFine 类(JVM+ASM) | @At 注入点 | mixin 成员引用 | 契约/覆写/引用 | invokedynamic 句柄 |
|---|---|---|---|---|---|---|
| 1.21 | 440/440 | 773/773 | 3(全是已停用 indigo) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.1 | 425/425 | 783/783 | 2(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.3 | 440/440 | 816/816 | 2(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.4 | 474/474 | 812/812 | 2(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.6 | 487/487 | 820/820 | 4(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.7 | 500/500 | 823/823 | 4(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.8 | 516/516 | 831/831 | 4(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.9 | 519/519 | 832/832 | 4(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.10 | 553/553 | 836/836 | 4(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.11 | 570/570 | 874/874 | 4(同上) | 0 缺失 | 0/0/0 | 0 悬空 |

新增扫描器 **`LambdaScan`**:逐个检查补丁类里 `invokedynamic` 的 bootstrap 方法/字段句柄是否指向"游戏真正会加载的那份类"里还存在的方法。这是之前唯一没人查的一环 —— JVM 与 ASM 验证器都**不解析** invokedynamic 目标(它们只在首次执行时才链接,也就是在游戏里)。逐版本结果 **0 悬空**。

每个版本的验证都能一条命令重跑:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-version.ps1 -Version 1.21.8
```

真机验证只有 **1.21.11 完成**(启动、主界面、单人、多人、方块/区块/物品渲染、光影、F3,`[ERROR]` 0 条)。其余 9 个版本已通过全部离线校验并装机实测过,结果是:

- **1.21.3 / 1.21.8** 崩在 `VerifyError: Bad type on operand stack in putfield`(`EntityRenderDispatcher.renderHitbox`、`ShoulderParrotFeatureRenderer.render`)。根因不在 fixer 上,而在管线自己:**未被任何 fixer 改动**的类也被重算栈帧(`MissingOverrideFix` 是全局的,于是全部 ~500 个补丁类都走了 `COMPUTE_FRAMES`),合并分支类型时退化成 `java/lang/Object`,游戏拒绝加载该类。现在这类类原样保留 OptiFine(经 tiny-remapper)的栈帧,只有真被改动的类才重算;`getCommonSuperClass` 退化时会打日志,验证器也不再依赖类加载顺序。**已修,待复测。**
- **1.21 / 1.21.4** 崩在 `RuntimeException: Mixin transformation of net.minecraft.class_X failed`。Mixin 不把原因写进 `latest.log`,从注入点倒推出来是**注入点本身被改掉了**:
  - 1.21 的 `class_5944`(ShaderProgram):`DelegatingConstructorFix` 内联 OptiFine 的委托构造函数时,照抄了 OptiFine 的 `new Identifier(name)`,而原版构造函数用的是 `Identifier.ofVanilla(name)` —— Fabric API 的 `@WrapOperation` 包的正是后者。现在内联时先问原版类怎么转换,有静态工厂就照抄。**已修,待复测。**
  - 1.21.4 的 `class_329`(InGameHud):OptiFine 把构造函数里三条 layer 方法引用(`this::method_55806/55807/55808`)改写成了 `lambda$new$0/1/2`;那一版 Fabric API 的 `LayerInjectionPoint` 是**按引导方法句柄**匹配注入点的,句柄对不上就等于注入点不存在。新增 `LambdaMethodRefFix`,把这些 lambda 改名回游戏使用的方法名(保留 OptiFine 的身体,它在准星那一层比原版多了 QuickInfo)。**已修,待复测。**

- **1.21.6 / 1.21.7 / 1.21.9** 的"光影没有任何效果",三个原因都定位到了(见下),其中两个已在本项目里修好;
- **1.21.8 / 1.21.10** 光影加载成功但渲染不对:光影包 `photon_v1.2a.zip` 使用了 OptiFine 不认识的程序名(`gbuffers_entities/particles/block_translucent`、`gbuffers_all_translucent` 与 Distant Horizons 的 `dh_terrain`/`dh_water`),OptiFine 只报 `Invalid program name` 并跳过这些 pass。**这属于光影包与 OptiFine 不匹配,不是补丁能修的**(换 OptiFine 专用包即可验证)。

**第二轮复测(同日 12:03–12:11)后又修掉的三处**:

- **1.21 / 1.21.3 / 1.21.4 进世界十几秒后崩**(`NullPointerException: Cannot invoke "net.optifine.override.ChunkCacheOF.renderStart()" because "regionIn" is null`):`RegionSectionPosFix` 在 1.21–1.21.4 上没生效 —— 那些版本的 region 构建器收的是 `ChunkSectionPos` 对象,1.21.6 起才是打包 long,fixer 只处理后者就整段跳过,于是 region 用原版构造器建出来、内部的 `ChunkCacheOF` 为 null。现在两种形状都支持。**已修,待复测。**
- **1.21.1 崩在 `Mixin transformation of net.minecraft.class_5944 failed`**:与 1.21 同一处注入点,但 OptiFine 在那里用的是**静态工厂**委托(`this(provider, Identifier.ofVanilla(id), type)`),内联 fixer 原来只认 `new Identifier(...)`,于是注入点留在 `this()` 之前(Mixin 拒绝实例 handler 落在 `super()` 前)。两种形状现在都识别。**已修,待复测。**
- **1.21.6 / 1.21.7 光影完全不加载**:OptiFine 那两个预览构建(`J6_pre3`、`J6_pre7`)的 `Shaders.loadShaderPack()` 在检查之前写死了 `cancelled = true`(1.21.8 起是读取检查结果),于是 `getShaderPack()` 永远不会被调用,选任何光影包都是 `[Shaders] No shaderpack loaded.`。新增的 `OptifineJarFixer` 在映射后的 jar 上删掉那两条指令;**同一组件还修好了 1.21.9**:OptiFine 自带的 `post_effect/fxaa_of_{2,4}x.json` 把 `minecraft:post/blit` 当顶点着色器,而 1.21.9 起游戏只有 `post/blit.fsh`(顶点阶段是 `core/screenquad`),后处理管线编译失败会连带光影初始化失败;顺带把 1.21.6 / 1.21.7 那份用了 `"program"` 键(那两版的解析器只认 `vertex_shader`/`fragment_shader`)的 JSON 一并改写。**已修,待复测。**

逐条推导、日志证据与复跑口径见 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) 的"第二轮真机反馈"。

---

## 1.0.0+mc1.21.11 — 移植到 Minecraft 1.21.11

把 OptiFine 接进 Minecraft 1.21.11 的 Fabric,并处理掉 OptiFine 与 Fabric API 之间的全部已知结构冲突。
实测搭配:**OptiFine 1.21.11 HD_U J9**(build `20260205-175838`)+ **Fabric Loader 0.19.5** + **Fabric API 0.141.6+1.21.11**,运行于 Java 25。

### 功能

- 在 `preLaunch` 阶段对 OptiFine jar 自动执行:跑 `optifine.Patcher` 解包(1.21.11 的 OptiFine 换成 xdelta 差分包,用法不变)→ 去 volde 化 → official→intermediary 重映射 → 应用字节码修复 → 注入 Fabric Loader 的类变换器。
- 重映射时把**游戏 jar 一起放进重映射器的 classpath 与输入**。这是 1.21.11 移植最关键的一处修正:TinyRemapper 的成员映射按**声明类**记录,看不见游戏类层次时,子类里覆写的方法会留成 OptiFine 的名字(`class_1308` 一个类就漏了 35 个方法 → 281 处破坏的抽象契约、254 处丢失的虚方法覆写)。
- 结果缓存到 `<游戏目录>/.optifine/<OptiFine 版本>/`(含缓存格式版本号,当前 `13`),二次启动直接复用(1–2 秒)。
- 缺 OptiFine / jar 损坏 / 版本不匹配 / 放了多份 OptiFine / 内部错误时,标题界面弹错误对话框,并在崩溃报告里追加 `OptiFabric` 一节。
- `-Doptifabric.mc-jar=<原版 client jar>` 可显式指定原版 jar;`-Doptifabric.extract=true` 会把重映射后的类解包出来便于排查。

### 与 Fabric API 的兼容修复(全部来自真机崩溃,逐个定位到字节码)

| 症状 | 根因 | 处理 |
|---|---|---|
| 启动崩 `Mixin transformation of net.minecraft.class_761 failed` | OptiFine 把 lambda 体重编译成 `lambda$addMainPass$1` 且**多一个参数**,Mixin 按名字+描述符找不到 `method_62214` | 恢复原版方法体(同类另有 `class_3898.method_60440`、`class_1092.method_65750`) |
| `class_1088` 的 `@WrapOperation` 找不到 `method_68018/68019` | 重编译后方法消失(同上) | 恢复原版方法体;另给 `class_775.method_3347` 补回被干掉的注入点(`InjectionCallPointFix`) |
| 启动崩 `@Local class_2338$class_2339` 校验失败 | 原版在 `ARETURN` 处作用域内有 `MutableBlockPos`,补丁后没有(MixinExtras 在**注入点**上判别局部变量) | `RestoreVanillaMethodsFix(true, "method_24225")` |
| 进世界 4 秒后崩 `ChunkCacheOF.renderStart() ... regionIn is null` | OptiFine 的区域构造器只有**六参数**版本会写 section 位置,恢复出的原版构建方法调用的是五参数那个 | `RegionSectionPosFix`:改调六参数构造器,用该方法本就收到的打包 long 生成第六个参数 |
| 进世界 4 秒后崩 `WorldRenderContextImpl.worldState()` 为 null | Fabric 的方块描边钩子读的上下文由 `LevelRenderer.method_22710` 头部准备,而 OptiFine 用 `RenderPass` + 自己的 lambda 替换了那条流程 | `StubInjectionTargetFix`:钩子目标改名并留同名副本,让注入落在**没人调用**的代码上(代价:`BEFORE_BLOCK_OUTLINE` 事件不再触发,描边照画) |
| **所有物品贴图丢失** | `class_10430.method_65584` 上的 `@Inject(at = RETURN)` 需要重编译后不再存在的局部变量 → **每个**物品模型都烘焙失败 | `RestoreVanillaMethodsFix(true, "method_65584")` |
| 进多人服务器约 30 秒崩 `Attempted to retrieve active rendering plug-in before one was registered` | 同上那招对**移动方块**钩子失效:它的调用者在**另一个类**(`class_11684.method_73002`)里,按名字调到的是被注入的副本 | `CallSiteRedirectFix`:调用方一并接管,调用点改到改名后的真实方法体 |
| 一按 **F3** 就崩(同一异常) | 声明 `contains_renderer` 只是让 Indigo 退场;Fabric API 自己的 F3 调试条目仍要 `Renderer.get()`,而没有任何渲染器被注册 | `RendererApiFallback`:注册惰性占位渲染器(F3 显示 `Renderer: OptifineRendererPlaceholder`) |
| 启动即崩 `NoSuchMethodError: class_2680.getBlockStateBaseCacheClass()` | 上一项第一版在 preLaunch 调了 `Class.getMethods()` 读 Fabric 接口,而接口方法签名里全是游戏类型 → 这些类被**提前加载成原版**,整局都停在原版上(`class_2680`=BlockState 少了 OptiFine 加的方法) | 生成器改为 ASM 只读接口 class 文件(不解析任何类型);注册改用 `MethodHandles.findStatic`;注册时机挪到补丁类注入之后 |

此外还有一批**离线推导**出来的结构问题:合成字段 `this$0`/`val$…` 同名同类型时按声明顺序配对并改成模组可 shadow 的名字(`SyntheticFieldFix` 扩展);被 OptiFine 重编译掉的原版方法桥接(`MissingOverrideFix`);被换成 OptiFine 子类的对象创建(`ChunkOF`)补回惰性 `NEW` 标记;被读取但从未被赋值的字段(`UnsetFieldScan` 报 0)。

### 兼容性

- 需要 **Fabric Loader ≥ 0.19.5**、Java 21+(实测运行在 Java 25)、客户端。
- 与 `sodium` 冲突(已声明);`no_fog`、`thallium`、`xradiation`、`ryoamiclights` 声明为不兼容。
- **不包含、也不分发 OptiFine 本体**;OptiFine 需自行获取后放进 `mods/`(1.21.11 有正式发布版,如 HD_U J9)。
- 依赖 FRAPI/indigo 的模组不再获得 indigo 的自定义渲染(地形交给 OptiFine);Fabric 的渲染器 API 由占位实现顶着。
- 两个 Fabric API 钩子被有意中和:`BEFORE_BLOCK_OUTLINE` 事件不再触发;移动方块的 FRAPI 渲染钩子失效(移动方块由原版路径渲染)。
- OptiFine 看不到 Fabric 模组内部的资源(它不认 Fabric 的资源包类型)。

### 验证

- 离线字节码校验(与游戏一致的单一加载器):被补丁的 **570 / 570** 个游戏类通过 JVM 校验,OptiFine 自身的 **874 / 874** 个类通过,ASM 数据流验证器 **0 问题**。
- 扫描器:mixin 成员引用缺失 **0**、破坏的抽象契约/丢失的虚方法覆写/无法解析的成员引用 **0/0/0**;`@At` 注入点仅剩 4 条,全部属于**已被停用**的 indigo。
- 真机(final session):启动、主界面、单人世界、**多人服务器**、方块/区块/物品渲染、光影(`ComplementaryReimagined` 加载成功)、F3 调试屏;`[ERROR]` 0 条、无崩溃报告。
- 过程、每一类的根因与修法、可复现工具见 [`docs/DEVELOPMENT.md`](../docs/DEVELOPMENT.md)。

---

## 1.0.0+mc1.20.6 — 首个发布版

把 OptiFine 接进 Minecraft 1.20.6 的 Fabric,并处理掉 OptiFine 与 Fabric API 之间的全部已知结构冲突。

### 功能

- 在 `preLaunch` 阶段对 OptiFine jar 自动执行:跑 `optifine.Patcher` 解包 → 去 volde 化 → official→intermediary 重映射 → 应用字节码修复 → 注入 Fabric Loader 的类变换器。
- 结果缓存到 `<游戏目录>/.optifine/<OptiFine 版本>/`(含缓存格式版本号),二次启动直接复用。
- 缺 OptiFine / jar 损坏 / 版本不匹配 / 放了多份 OptiFine / 内部错误时,标题界面弹错误对话框,并在崩溃报告里追加 `OptiFabric` 一节。
- `-Doptifabric.mc-jar=<原版 client jar>` 可显式指定原版 jar;`-Doptifabric.extract=true` 会把重映射后的类解包出来便于排查。

### 与 Fabric API 的兼容修复(全部来自真机崩溃,逐个定位到字节码)

| 症状 | 根因 | 处理 |
|---|---|---|
| 启动崩 `@ModifyArg handler before this() invocation must be static` | OptiFine 的 `ShaderProgram` 委托构造器在 `this()` 之前创建 `Identifier`,Fabric 的注入点落在 `super()` 前 | 内联委托构造器,把标识符创建挪到 `super()` 之后(保留原参数布局) |
| `@Shadow field field_3835 was not located` | OptiFine 把字段留成混淆名且描述符不符 | 按映射表对齐字段名、类型与构造器里存入的值 |
| 校验错 `Bad type on operand stack` / `Bad local variable type` | 构造器重写的槽位搬移破坏了描述符与调用点的一致性 | 改用末尾空闲槽位,并用反汇编逐条核对 |
| 缺注入目标 `EntityRenderers.method_32174/32175`、`ThreadedAnvilChunkStorage.method_17227/18843` | OptiFine 重编译时把私有助手内联掉了 | 恢复原版方法体 |
| 缺 `@Shadow` 字段 `field_27735` / `field_40571` / `field_20839` | 合成外部实例引用被 javac 命名为 `this$0` / `this$1`,映射表里没有这种名字 | 按描述符匹配并重命名合成字段 |
| 区块构建崩 | `ChunkRendererRegionBuilder.build` 被改成转发给自己重载的瘦包装,局部变量搬走 | 恢复原版方法体 |
| 56,042 次模型烘焙失败(方块全部消失) | `ModelLoader$BakerImpl.bake` 同样被改成转发,三条注入点搬进了重载 | 恢复原版方法体 |
| 开存档报"网络协议错误" | OptiFine 用 `net.optifine.ChunkOF` 取代 `WorldChunk`,Fabric 的 `@At(value="NEW", target="WorldChunk")` 匹配不到 | 在 OptiFine 创建子类处前面插入惰性 `NEW; POP` 标记 |
| 进多人服务器两秒后掉线(协议错误) | 移植的 `ParticleManagerFix` 把构造器换成原版,连带丢掉了 OptiFine 对 `renderEnv` 字段的唯一初始化 → 破坏方块粒子时 NPE 抛在网络包处理里 | 不再替换构造器(类型对齐由映射层完成) |
| 区块构建时崩(提前拦住的隐患) | indigo 注入 `BlockPos.iterate`,而 OptiFine 重写了那段循环 | 声明 `fabric-renderer-api-v1:contains_renderer`,让 indigo 按 Fabric 的机制让位 |

### 兼容性

- 需要 **Fabric Loader ≥ 0.19.3**、Java 21+(实测运行在 Java 25)、客户端。
- 与 `sodium` 冲突(已声明);`no_fog`、`thallium`、`xradiation`、`ryoamiclights` 声明为不兼容。
- **不包含、也不分发 OptiFine 本体**;OptiFine 需自行获取后放进 `mods/`。
- 依赖 FRAPI/indigo 的模组不再获得 indigo 的自定义渲染(地形交给 OptiFine)。
- OptiFine 看不到 Fabric 模组内部的资源(它不认 Fabric 的资源包类型)。

### 验证

- 离线字节码校验:与游戏一致的单一加载器下 **425 / 425 通过**,ASM 数据流验证器 **0 问题**。
- 真机:带 Fabric API 进主界面、开单人存档、进多人服务器、模型与区块渲染、光影生效。
- 过程与可复现工具见 [`docs/DEVELOPMENT.md`](../docs/DEVELOPMENT.md)。
