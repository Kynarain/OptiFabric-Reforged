# 兼容性:1.21.1 实测扫描的结果

> 本文件汇总一次**实测**兼容性扫描:**OptiFabric Reforged 2.2.1 + OptiFine** 搭配 1.21.1 上流行的 Fabric 模组。
> 它只汇报测量结果,不做宣传。完整数据(每一行、每一句原始日志、方法与限制)在
> [`docs/compatibility/`](compatibility/)(先看 [`INDEX.md`](compatibility/INDEX.md));
> 声明式的不兼容清单见 [`README_CN.md`](../README_CN.md#声明的不兼容以及加载器实际会怎么做)。

## 一、测的是什么、怎么测的

| 组件 | 确切产物 |
|---|---|
| Minecraft | **1.21.1** |
| Fabric Loader | **0.19.5** |
| Fabric API | `fabric-api-0.116.17+1.21.1.jar` |
| OptiFabric | `OptiFabric-2.2.1+mc1.21.1.jar`(mod id `optifabric_reforged`) |
| OptiFine | `OptiFine_1.21.1_HD_U_J1.jar`,SHA-256 `DB6D2D14DB0009BDEA2D8F848E5CB55AAC52B555E8E50506DBF7876755B79082` |
| 启动方式 | 离线账号,一次只测一个模组,每次启动前删掉 `latest.log` |

每一行装载的**恰好**是:fabric-api + OptiFabric + OptiFine + **被测模组及其必需的依赖闭包**。可选依赖一律不加;
启动前会拿 `mods/` 与记录下来的清单核对,不一致就拒绝启动。被测模组是唯一的变量,其它条件全程固定。

**判定口径是"客户端进到了标题界面"** —— 本次运行自己的 `latest.log` 里出现 `Sound engine started`,且没有点名该
模组的 mixin/注入错误。它不是"这个模组能用"。

> **请先读这条限制。** 整次扫描不开世界、不玩游戏、不渲染任何一帧、不编译光影包、不改任何配置文件。一个能进标题
> 界面、但一进世界/一开界面/一放某个方块就坏的模组,在这里**记为通过**。下面的"OK"只表示*这个模组不会阻止客户端在
> OptiFabric + OptiFine 旁边启动*,仅此而已。

每一个失败行都额外跑了一次**不带 OptiFine** 的对照(OptiFabric 仍在,因为没有它 OptiFine 根本不会加载)。这条对照
用来区分"是 OptiFine 这一层的交互弄坏的"和"在原版 Fabric 上本来就坏"。

## 二、数字与口径

快照日期:**2026-10-03**,报告由合并后的逐次运行记录生成,其中**已经包含 49 行的 CurseForge 批次**。**扫描处于
暂停状态,并没有跑完** —— 当前没有任何进程在跑,随机抽样阶段从未启动。

| 分类 | 数量 | 含义 |
|---|---|---|
| OK | **187** | 进到了标题界面 |
| FAIL | **34** | 崩溃,或在预算内没能进标题界面 |
| DEP | **16** | 测不了:必需依赖在 1.21.1 上没有,或其元数据要求了这次运行给不了的东西 |
| N/A | **18** | 没有 1.21.1 的 Fabric 文件,或属于服务端/纯库 |
| HARNESS-ERROR | **1** | 运行本身失败,与模组无关 |
| **not run** | **50** | 在池子里但有意没跑 —— 不给出任何结论 |

`MATRIX.md` 描述了 **307 行**,其中 **257 行有结论**(它的汇总把 N/A 记成 19,因为它还列了 `fabric-api` —— 它是
每次运行的基础组成部分,不可能作为被测对象)。可机读的合并记录里是**实测的 256 行**:OK 187 / FAIL 34 / DEP 16 /
N/A 18 / HARNESS-ERROR 1。

覆盖面按阶段如实列出,不做暗示:

| 阶段 | 行数 | 已启动 | 结论 | 未跑 |
|---|---|---|---|---|
| 下载量前 100 的池子 | 99 | 99 | DEP 9、FAIL 15、OK 75 | — |
| 扩展池,第 101–200 名 | 100 | 100 | DEP 3、FAIL 15、N/A 15、OK 67 | — |
| 手工挑的 Carpet 家族 | 8 | 8 | DEP 2、HARNESS-ERROR 1、N/A 3、OK 2 | — |
| CurseForge 批次(50 个候选) | 50 | 49 | DEP 2、FAIL 4、OK 43 | 1(取不到 1.21.1 Fabric 文件) |
| 扩展池,第 200 名之后 | 50 | 0 | — | **50,按设计不跑** |
| 随机抽样研究(抽了 100 个) | 100 | 0 | — | 已抽样、已解析依赖、已排队,**从未启动** |
| 整合包普查 | 100 | 0 | — | 普查,不是启动行 |

第 200 名之后那 50 行大多是库、数据包工具和小模组,"配 OptiFabric 是 OK"几乎不携带信息;省下的时间给了
CurseForge 批次和抽样。它们一律标成 `not run`,从不计入通过。

## 三、是谁的锅?

只有当同一个模组**不带 OptiFine** 也能启动时,一个 FAIL 才可能说明 OptiFabric 有问题。对全部 **34 个失败**,
不带 OptiFine 的对照给出:

| 归属 | 数量 | 对照跑出来的结果 |
|---|---|---|
| `mod-incompatible-with-optifine` | **27** | 在原版 Fabric 上能启动,所以失败属于 OptiFine 这一层的交互 |
| `mod-broken-on-plain-fabric` | **4** | 不带 OptiFine 也照样失败(四条都来自 CurseForge 批次) |
| `unexplained-control-failed-to-run` | **3** | 对照本身没跑出结果,**不主张归属**(`bobby`、`freecam`、`rei`) |

**归因于 OptiFabric 自身字节码改写的:0** —— 确切表述是:**在 255 条已分类的行里为 0,另有 1 条未知
(`carpet-fixes`,那一行是运行装置本身失败);Wilson 95% 置信上界 1.5%。** 在 27 条对照通过的失败里,11 条点名的
`net.minecraft` 类**确实**在 OptiFine 改写过的集合里(`class_761` WorldRenderer、`class_702` ParticleManager、
`class_757`、`class_1921`、`class_309`、`class_332` 等),15 条没点名任何类,还有 1 条 —— `sodium` —— 是本模组自己的
`fabric.mod.json` 声明的(`conflicts` 与 `breaks` 两处都有),因此属于**事先声明的限制**,而不是新发现的缺陷。

**基于抽样的验收标准还没有被测过。** 100 行随机样本已经抽出(种子
`optifabric-compat-matrix/random-sample/2026-10-03T09:20Z`,61 行 Modrinth + 39 行 CurseForge),其中 6 行在启动前
即判定无法测试,可启动的 94 行依赖闭包全部解析完毕 —— 但**一行都没跑**,所以那项研究没有任何比率可言。本文件里
任何数字都不应被当作那个比率。

## 四、34 个失败按类别拆开

同样是这 34 个 FAIL,按每一行点名的东西和它那次不带 OptiFine 的对照做了什么来排,数据取自 `MATRIX.md` 的逐行
`Implicated layer` 与"Target class in OptiFine's rewritten set?"两列:

| 类别 | Modrinth 各阶段 | CurseForge 批次 | 合计 |
|---|---|---|---|
| **FAIL 行** | 30 | 4 | **34** |
| `mod-incompatible-with-optifine` —— 对照进到了标题界面 | 27 | 0 | 27 |
| ⤷ 证据点名的 `net.minecraft` 类在 OptiFine 改写集合里 | 12 | 0 | 12 |
| ⤷ 证据没有点名这类类 | 15 | 0 | 15 |
| `unexplained` —— 对照本身没跑出结果 | 3 | 0 | 3 |
| `mod-broken-on-plain-fabric` —— 对照同样失败 | 0 | 4 | 4 |
| 由 OptiFabric 自己的 `fabric.mod.json` 声明(`sodium`,属于那 12 条) | 1 | 0 | 1 |
| 对照没能跑起来 | 3 | 0 | 3 |
| 可归因于 OptiFabric | 0 | 0 | **0** |

那 12 条点名了被改写类的行是 `sodium`、`iris`、`immediatelyfast`、`sodium-extra`、`entity-model-features`、
`reeses-sodium-options`、`modernfix`、`moreculling`、`c2me-fabric`、`supplementaries`、`fallingleaves` 与
`carry-on`,其中只有 `sodium` 是 OptiFabric 自己声明的;没有主张归属的 3 行是 `rei`、`freecam`、`bobby` —— 与对照
没跑起来的那 3 行是同一批,所以这里的"unexplained"和"对照没跑起来"是一组(`carpet-fixes` 是第 4 条对照没跑起来的
行,但它是 `HARNESS-ERROR`,不属于这 34 个 FAIL)。**一个与渲染器 / OptiFine 改写类共享同一机理的失败,是 OptiFine
加那个模组的性质,不是 OptiFabric 的性质** —— 同一个模组在对照里能在原版 Fabric 上启动,而它的 mixin 没能完成变换的
那个类正是 OptiFine 改写过的 —— 所以可归因于 OptiFabric 的数量是 **0**,归属未被主张的行是 `rei`、`freecam`、
`bobby`,以及这 34 条之外的 `carpet-fixes`。

源数据里有两处互相矛盾,这里如实写出而不是挑一边:`MATRIX.md` 自己的汇总行与分组表说有 **4** 条失败是
unexplained,而它的逐行数据只给出 **3** 条(第 4 条是 `carpet-fixes`,那个 `HARNESS-ERROR`;上表采用逐行的值);
另外 `MATRIX.md` 把全部 49 条 CurseForge 行的类名一列都留成 `—`,所以上表 CurseForge 那一列的 0 是"没有数据",
不是"测量结果是 0" —— 扫描工作区自己的 `PAUSED.md` 记录着 `cf--the-twilight-forest` 的证据点名了 `class_156` 与
`class_638`,而这两个类都在改写集合里。`analysis.json` 是更早一次构建(126 条 Modrinth 行、17 个 FAIL:16 条
`mod-incompatible-with-optifine` 加 `rei`,同样的 12 条改写类行、同样只有那一条声明),里面**没有任何 CurseForge
行**;它的机理计数与 `MATRIX.md` 一致,而它的总数早于那轮串行重跑。

## 五、失败长什么样

失败高度集中在预期的地方:**替换渲染器**的模组,以及**注入到 OptiFine 改写过的方法/类**里的模组。最大的一类是
`mixin-transform`(34 个失败里占 27 个),其中渲染器家族明显偏多 —— `sodium`、`iris`、`immediatelyfast`、
`indium`、`sodium-extra`、`reeses-sodium-options`、`moreculling`、`entity-model-features`、`fallingleaves`、
`sodium-shadowy-path-blocks` 等在矩阵里都标着"渲染器重构"。典型证据是
`Mixin transformation of net.minecraft.class_<n> failed`,而那个类正是 OptiFine 改写过的。

- **`sodium` 是事先声明的不兼容**,不是这次新发现的:本模组 `fabric.mod.json` 在 `conflicts` 与 `breaks` 两处都写了它;
  在那一节实测的加载器版本上,两个字段都不会阻止游戏启动 —— 它们只是警告,所以这是声明,不是闸门。
  Sodium 自己的元数据只在 `breaks` 里声明这一对(没有 `conflicts`),点名的又是旧 id `optifabric`,所以那条声明在本线上根本不会命中:加载器唯一会理会的只有我们这一条。它的对照是能启动的,见
  [`README_CN.md`](../README_CN.md#声明的不兼容以及加载器实际会怎么做)。
- **The Twilight Forest**(`twilightforest-fabric-1.21.1-4.8.734.jar`,CurseForge file id 9003337)是 FAIL
  (`mixin-transform`,涉及 `class_156` / `class_638`)—— 但它是**只发在 CurseForge** 的 1.21.1 Fabric 移植,
  且不带 OptiFine 的对照**同样失败**(原因不同),因此归属是 `mod-broken-on-plain-fabric`:**不能算到 OptiFabric 头上**。
  只扫 Modrinth 的流程根本碰不到这个模组。
- 有一个第三方个案是已知的、上游有报告、我们这边已修:**ShoulderSurfing** 的 `Camera` 局部槽位冲突,上游报告在
  `Exopandora/ShoulderSurfing#476`,本线在 2.2.1 里处理(实测记录见 [`DEVELOPMENT.md`](DEVELOPMENT.md) 与
  [`RELEASE_NOTES.md`](RELEASE_NOTES.md))。它**不在本次扫描范围内**,这里只作为背景引用。
- `structory` 跑不了:它 1.21.1 Fabric 文件的记录里没有 `downloadUrl`,文件存在,但不走 CurseForge 官方 API 就取不到。
  这一条记为"无法测试",而不是悄悄丢掉。

## 六、这次扫描没有覆盖什么

1. **不开世界、不渲染、不开光影** —— 见第一节的提示框。最大的盲区是:任何只在世界加载之后才出现的失败。
2. **一次一个模组。** 不测组合、不测整合包、不测两个非基础模组之间的相互作用。
3. **只用默认配置。** 没改过配置文件,没开关过任何功能。
4. **每个模组只跑一次。** 间歇性失败的模组,记录的是第一次的结果。
5. **没开 `-Dmixin.debug`**,所以日志与默认启动一致,但对"这个 mixin 失败属于哪个模组"的指向不如开debug时精确。
6. **只有一组确切版本。** 换 OptiFabric、OptiFine 构建、加载器或模组版本,结果都可能不同。
7. **CurseForge 侧不是普查。** 那一侧的抽样框是该平台按下载量的头部,而且无密钥的镜像 API 会忽略加载器/版本过滤,
   每个项目都得单独查文件。
8. **扫描是暂停的**:还欠两个重跑队列 —— 4 行的自身运行或对照没跑出可用结果(`carpet-fixes`、`rei`、`bobby`、
   `freecam`),以及 4 行 CurseForge(两条在依赖补回之前测出的无效 DEP,两条超时);此外第 200 名之后的 50 行按设计
   丢弃,100 行随机样本从未启动。重跑会改变这些数字。

## 七、数据在哪

[`docs/compatibility/`](compatibility/) 放着从扫描工作区拷出来的快照:
[`MATRIX.md`](compatibility/MATRIX.md)(逐行表格、按原因分组的失败、CurseForge 批次)、
[`README.md`](compatibility/README.md)(方法、坑、逐阶段覆盖、装置自身被查出并修掉的缺陷)、
[`LIST.md`](compatibility/LIST.md)(同样这些行的中文清单,来自更早一次的报表构建)以及
[`upstream-optifabric-probe.md`](compatibility/upstream-optifabric-probe.md)(本文件与各 README 里那些上游说法的
证据记录:上游各分支的 `fabric.mod.json`,以及那次证明加载器在 `conflicts` 下**只警告、不拒绝**的运行)。
[`INDEX.md`](compatibility/INDEX.md) 逐个给出每个文件的行数、源字节数、**LF 归一化后的 SHA-256**、快照日期,以及
哪些扫描文件**有意没有**拷进仓库(原始日志、缓存、jar,以及说明如何继续那次运行的工作区状态文件)。
