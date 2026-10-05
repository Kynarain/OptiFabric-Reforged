# OptiFabric

#!!!此模组由deepseek编写并验证请小心用于生产环境!!!#

在 Fabric Loader 下加载 **OptiFine** 的客户端模组。把 OptiFine 的 jar 和本模组一起放进 `mods/`,启动时 OptiFabric 会用 OptiFine 自带的补丁器给原版客户端打补丁、重建被搬走的 lambda、把 OptiFine 从官方混淆名重映射到 intermediary,并把打过补丁的 Minecraft 类交给 Fabric Loader 的类转换器接管,从而让两者共存。

本分支是 **1.20.6 线**。更新的版本(1.21.x、26.x)在各自的分支上独立开发,各线的 jar 不能互相替代。
完整兼容列表(MC 1.21.1)见 [`COMPATIBILITY.md`](COMPATIBILITY.md)。The full MC 1.21.1 compatibility list is in [`COMPATIBILITY.md`](COMPATIBILITY.md).


## 📊 跑分实测 —— 所有环境与结果都在这一张表里(1.21.x 线)

用第三方基准 **FPS Benchmark**(`fpstest-1.0.jar`,sha256 `F11681914771E01A4677DA5EF217195FF523B01E9C3F463BF9A298D7BCCB2C56`)测得:每组跑一次 3 分钟脚本化 *Base*(19 段运镜 —— 森林、村庄、战斗、红石、洞穴、下界、末地;固定种子 `27182`)。**原始报告(含逐帧样本)**见 [`benchmarks/2026-10-05-fps-benchmark/`](https://github.com/Kynarain/OptiFabric-Reforged/tree/1.21.x/benchmarks/2026-10-05-fps-benchmark)。

| 组 | 角色 | 模组(sha256 前 8) | 运行环境 —— 四组完全相同 | 平均 FPS | 1% low | 0.1% low | p99 帧 | 最大帧 | 最低帧 | 标准差 | 原始结果 |
|---|---|---|---:|---:|---:|---:|---:|---:|---:|---|
| **A** | 纯净基线 | `fabric-api-0.116.17+1.21.1.jar` `79AC44B4` + `fpstest-1.0.jar` `F1168191` | Minecraft 1.21.1 · Fabric Loader 0.19.5 · Java 22.0.2 · max heap 5836 MB · Intel i5-12600KF · AMD RX 7800 XT(驱动 25.12.1.251128)· Windows 10 amd64 · 视距 8 · VSync 关 · `maxFps:260`(等于"无限")· 光影关 · `options.txt` 四份逐字节相同(sha256 `AC506701…`)· 种子 27182 | 3631 | 774 | 442 | 0.76 ms | 17.21 ms | 58.1 | 706 | [report.md](https://github.com/Kynarain/OptiFabric-Reforged/blob/1.21.x/benchmarks/2026-10-05-fps-benchmark/A-vanilla-baseline/report.md) |
| **C** | 仅 OptiFabric + OptiFine | A + `OptiFabric-2.2.10+mc1.21.1.jar` `A897DA34` + `OptiFine_1.21.1_HD_U_J1.jar` `DB6D2D14` | *(同上)* | **5040** | **1098** | **713** | 0.58 ms | **3.31 ms** | 302 | 964 | [report.md](https://github.com/Kynarain/OptiFabric-Reforged/blob/1.21.x/benchmarks/2026-10-05-fps-benchmark/C-OptiFabric-OptiFine/report.md) |
| **D** | 推荐组合 | C + `lithium-fabric-0.15.4+mc1.21.1.jar` `92329D98` + `ferritecore-7.0.3-fabric.jar` `98C3AB1D` + `c2me-fabric-mc1.21.1-0.4.0-alpha.0.29.jar` `9C4C1C4C` | *(同上)* | 4475 | 1077 | 721 | 0.63 ms | 3.14 ms | **319** | **747** | [report.md](https://github.com/Kynarain/OptiFabric-Reforged/blob/1.21.x/benchmarks/2026-10-05-fps-benchmark/D-OptiFabric-OptiFine-LiFeC2ME/report.md) |
| **B** | Sodium 路线(与 OptiFine 不兼容) | A + `lithium-fabric-0.15.4+mc1.21.1.jar` `92329D98` + `sodium-fabric-0.8.13+mc1.21.1.jar` `3D43C149` | *(同上)* | **5680** | 1017 | 349 | **0.50 ms** | 12.47 ms | 80.2 | 1177 | [report.md](https://github.com/Kynarain/OptiFabric-Reforged/blob/1.21.x/benchmarks/2026-10-05-fps-benchmark/B-Sodium-Lithium/report.md) |

- **C 平均比纯净基线高 38.8%**,最大帧时间还好 5 倍(3.31 ms vs 17.21 ms)—— OptiFine 自带的优化确实在干活,兼容层在这里是净收益而不是代价。
- **B(Sodium)平均最快(比 C 高约 13%),但尾部最差**:0.1% low 349 vs 713、最大帧 12.47 ms vs 3.31 ms、最低帧 80 vs 302。
- **那三个"性能模组"在这个静止场景里几乎看不出来**(C 5040 → D 4475)—— *Base* 那一轮刻意让世界生成与 tick 保持轻量。下面是 41 项完整套件的结果,在它们真正擅长的场景里结论正好相反。

### 完整套件 —— 41 项测试、四套配置

同一台机器、同一套设置、同一批确定性场景。下表是分类平均,格式为 平均 FPS / 1% low:

| 类别(测试数) | A 纯原版 | B Sodium + Lithium | C OptiFabric + OptiFine | D = C + Lithium + FerriteCore + C2ME |
|---|---|---|---|---|
| 空载 / 静态(2) | 4579 / 1278 | 5961 / 1712 | 5388 / 1529 | 5670 / 1686 |
| 飞行,12 个生态(12) | 2606 / 288 | 5254 / 649 | 4220 / 461 | 4382 / 777 |
| 实体(8) | 1496 / 537 | 1861 / 681 | 1595 / 587 | 1626 / 638 |
| 物理(6) | 3066 / 602 | 4040 / 924 | 3915 / 785 | 3979 / 901 |
| 红石 / 方块实体(5) | 3935 / 1078 | 5537 / 1523 | 4898 / 1369 | 5004 / 1404 |
| 粒子(2) | 1972 / 523 | 2634 / 510 | 2331 / 669 | 2670 / 770 |

- 纯原版基线在六个类别里全部垫底。加上 OptiFabric + OptiFine(C)之后大约得到:飞行 +62%、物理 +28%、红石 +25%、粒子 +18%、空载 +17%。
- D 在几乎所有类别都不低于 C,而且尾部明显更好(飞行 1% low 777 对 461、粒子 770 对 669)。所以 Lithium / FerriteCore / C2ME 在真实场景里确实有用。
- 最清楚的一例是落沙墙:1% low 分别是 32(A)、66(B)、34(C)、73(D) —— 没有那三个性能模组时 C 的尾部和纯原版一样糟,是 D 救回来的。
- 粒子多样性是唯一谁都治不好的场景:四组都在 437 到 495 之间,瓶颈不在我们这边。

*完整套件的英文说明与逐项数据见 [1.21.x 分支 README](https://github.com/Kynarain/OptiFabric-Reforged/blob/1.21.x/README.md#full-suite--41-tests-all-four-configurations);原始报告在 [benchmarks/](https://github.com/Kynarain/OptiFabric-Reforged/tree/1.21.x/benchmarks/2026-10-05-fps-benchmark)。*

*单机、每组一轮、单一基准,分辨率未强制。数字是参考值,不是规格书。完整英文版见 [`1.21.x` 分支 README](https://github.com/Kynarain/OptiFabric-Reforged/blob/1.21.x/README.md#-benchmarks--every-environment-and-every-result-in-one-table)。*

## 支持的版本

| Minecraft | 产物 | OptiFine 构建 | Java |
|---|---|---|---|
| 1.20.6 | `OptiFabric-1.1.3+mc1.20.6.jar` | `preview_OptiFine_1.20.6_HD_U_J1_pre18.jar` | 21 |

- mod id `optifabric`,仅客户端,要求 **Fabric Loader ≥ 0.19.3**。
- 一个 jar 只对应一个版本:jar 里打包着该版本的 `official → intermediary` 映射表(混淆名每版不同,用错版本会把 OptiFine 重映射坏),`fabric.mod.json` 里的 `minecraft` 依赖也精确到该版本。
- 1.20.6 的 OptiFine **只有 preview 构建**,下载后直接丢进 `mods/` 即可(它是安装器形态,OptiFabric 会自己运行 `optifine.Patcher`)。请到 OptiFine 官网 <https://optifine.net/downloads> 的 **Minecraft 1.20.6** 一节里自己取:`preview_OptiFine_1.20.6_HD_U_J1_pre18.jar`(2024-09-27,J1_pre18)它是该版本最新的构建。本模组**不携带、也不指向任何第三方镜像**:它只会从 OptiFine 官网下载,失败时也只告诉你原因并让你去官网手动下载。
- 离线字节码校验 **425 / 425 通过**、ASM 数据流验证器 0 问题;真机验证:进入主界面、单人存档、多人服务器、模型与区块渲染、光影生效。

表里这一列是该 MC 版本**最新的正式版** OptiFine;若该版本官方还没有正式版,则用**最新的预览版**代替。
1.20.6 就是后者:OptiFine 为它发布过 `HD_U_I9_pre1`(2024-06-06)、`HD_U_J1_pre17`(2024-09-25)、`HD_U_J1_pre18`(2024-09-27)
三个构建,全是预览版,没有正式版,所以最新那个 `preview_OptiFine_1.20.6_HD_U_J1_pre18.jar` 就是建议项(也是下载按钮会取的那个)。
同一 MC 版本的其它构建同样可用 —— 游戏内提示只在你 mods/ 里的 jar 是**预览版**、且比表里这个更旧时才会出现(你已经装了正式版就不会被打扰,哪怕有更新的正式版)。

### OptiFabric 版本 → Minecraft 版本 → 需要的 OptiFine 构建

这张表与模组里自带的那张(`OptifineSupport`)一致;本分支只构建 1.20.6 一个版本,所以只有一行。

| OptiFabric 版本 | Minecraft 版本 | 需要的 OptiFine 构建 |
|---|---|---|
| `1.1.3+mc1.20.6` | 1.20.6 | `preview_OptiFine_1.20.6_HD_U_J1_pre18.jar` |

当 OptiFabric 加载了、而上面那个 jar 不在(或者不是该 Minecraft 版本需要的那个构建)时,游戏不再默默启动,而是弹出一个界面告诉你缺哪个文件:
上面有「下载 OptiFine」按钮,从 OptiFine **官网**(`optifine.net`,也是本模组唯一会去下载的地方)取回它,旁边就是打开 mods 文件夹;
按钮上方的地址栏可以换成你自己的地址,那样就只从你填的地方取。

两种提示的行为:

- **mods/ 里完全没有 OptiFine**:每次启动都会提示;点「继续返回主菜单」只是这次会话不再出现,它不会被记住,下次启动还会提示。
- **装的是更旧的预览版**(比如 `HD_U_I9_pre1`、`HD_U_J1_pre17`):提示会建议换成最新的那个,点「仍要继续」后把该构建记到 `config/optifabric-mismatch-ack.txt`,同一个构建不再重复提示。
- **装的是正式版**(或比表里更新的构建):不提示 —— 只有预览版才值得为它打断你。

下载下来的 jar 会先校验(`PK` 归档 + OptiFine 自己的 `Config.class`),再按官方文件名原子写入 `mods/`,然后弹出确认框问是否立即重启;
Windows 上「立即重启」走 JNA 的 `GetCommandLineW` + `CreateProcessW`(路径里有空格时 `ProcessHandle` 读不回自己的命令行),其它系统走 `ProcessHandle`,两条路都会在启动新进程后结束当前进程。
下载失败时界面只显示具体原因 + 「打开官网下载页」+「重新检查」,**不会**换一个源重试。

## 安装

1. 准备与本版本**严格一致**的 OptiFine(1.20.6)。OptiFabric 会读取 jar 内 `optifine/Config` 的 `MC_VERSION` 做校验,不一致会直接在标题界面报错。安装器形态(含 `patch/` 差分包)与解包形态(含 `notch/<混淆名>.class`)都支持,直接丢进 `mods/` 即可,**不需要**先运行 OptiFine 安装器。
2. 把本模组的 jar 与 OptiFine 的 jar 一起放进该 Fabric 版本自己的 `mods/` 目录。不要放两份 OptiFine(会报 `DUPLICATED`)。

<!-- launcher-independent install: the launcher may only offer OptiFine OR Fabric, so OptiFabric cannot
     assume the launcher installs OptiFine for the user. Kept identical on both build variants. -->
> **OptiFabric cannot assume your launcher installs OptiFine for you**, because many launchers only offer
> **OptiFine _or_ Fabric** as the profile, never both: install **Fabric + OptiFabric** first; download
> OptiFine from <https://optifine.net/downloads>; put that jar in the `mods` folder (or paste its path into
> start the game once by hand.
>
> **OptiFabric 不能假设启动器会替你装 OptiFine**,因为很多启动器只能选 **OptiFine _or_ Fabric**,不能两个都要:
> 先装好 **Fabric + OptiFabric**;到 <https://optifine.net/downloads> 下载 OptiFine;把那个 jar 放进 `mods`
> 文件夹;然后手动启动一次游戏。

<!-- launcher-independent install: the launcher may only offer OptiFine OR Fabric, so OptiFabric cannot
     assume the launcher installs OptiFine for the user. Kept identical on both build variants. -->
> **OptiFabric cannot assume your launcher installs OptiFine for you**, because many launchers only offer
> **OptiFine _or_ Fabric** as the profile, never both: install **Fabric + OptiFabric** first; download
> OptiFine from <https://optifine.net/downloads>; put that jar in the `mods` folder (or paste its path into
> start the game once by hand.
>
> **OptiFabric 不能假设启动器会替你装 OptiFine**,因为很多启动器只能选 **OptiFine _或_ Fabric**,不能两个都要:
> 先装好 **Fabric + OptiFabric**;到 <https://optifine.net/downloads> 下载 OptiFine;把那个 jar 放进 `mods`
> 文件夹;然后手动启动一次游戏。
<!-- launcher-independent install: the launcher may only offer OptiFine OR Fabric, so OptiFabric cannot
     assume the launcher installs OptiFine for the user. Kept identical on both build variants. -->
> **OptiFabric cannot assume your launcher installs OptiFine for you**, because many launchers only offer
> **OptiFine _or_ Fabric** as the profile, never both: install **Fabric + OptiFabric** first; download
> OptiFine from <https://optifine.net/downloads>; put that jar in the `mods` folder (or paste its path into
> start the game once by hand.
>
> **OptiFabric 不能假设启动器会替你装 OptiFine**,因为很多启动器只能选 **OptiFine _或_ Fabric**,不能两个都要:
> 先装好 **Fabric + OptiFabric**;到 <https://optifine.net/downloads> 下载 OptiFine;把那个 jar 放进 `mods`
> 文件夹;然后手动启动一次游戏。
3. 用 **Fabric 版本**启动,不要用启动器注入 OptiFine 的 `1.20.6-OptiFine_xxx` 版本(那个是启动器在启动时注入 OptiFine,会与本模组重复)。
4. 首次启动会明显变慢(要跑完整的补丁与重映射流程),之后走缓存。标题界面出现 OptiFine 版本号、视频设置里出现 OptiFine 选项即表示成功。

## 构建

需要 **JDK 21**。仓库根目录就是 Gradle 项目:

```powershell
.\gradlew build
```

产物为 `build/libs/OptiFabric-1.1.3+mc1.20.6.jar`。

开发环境不受支持:`gradlew runClient` 会被明确拒绝,因为开发环境的命名空间是 `named`,需要额外的 contextual mapping 层。

## 工作原理

OptiFine 不是 Fabric 模组:它的 jar 里是针对原版客户端类的字节码补丁,加上 OptiFine 自己的类。本模组在 `preLaunch` 阶段完成四件事:

```
mods/<OptiFine>.jar
        │  ① 用 OptiFine 自带的 optifine.Patcher 给原版(混淆)客户端 jar 打补丁
        │  ② LambdaRebuilder:补丁类里的 lambda(invokedynamic)指向已被搬走的原方法,需要重建
        │  ③ tiny-remapper:official(混淆) → intermediary 重映射
        │     必须把游戏 jar 一起放进重映射器的 classpath,否则子类里覆写的方法继承不到映射
        ▼
  Optifine-mapped.jar
        │  ④ 拆成两部分
        ├── 非 Minecraft 类(OptiFine 自己的类与资源)──► 加进游戏 classpath
        └── net/minecraft/** 打过补丁的类 ──────────► ClassCache(替换用)
```

类替换走 **Fabric Loader 自己的 GameTransformer**:Minecraft 类被加载时,Loader 会先问游戏 provider 的 `GameTransformer.transform(类名)` 有没有现成的字节码,而这一步发生在 Mixin **之前**。OptiFabric 在 preLaunch 阶段把打过补丁的 MC 类(先经过 `patcher/fixes` 的版本修正)放进该 transformer 的 `patchedClasses`,类加载时即被顶替;Loader 自己补过的类保持 Loader 的版本。

因此不需要为每个补丁类生成 stub mixin,也不依赖 Mixin 的扩展 API;交出去的是 Mixin 的**输入**而非输出,其它模组针对这些类的 mixin 照常生效。

一条硬性约束:**在补丁类交给 Loader 之前,不能对游戏类做任何反射解析**(例如 `Class.getMethods()` 会把方法签名里的游戏类型全部加载掉),否则这些类会被永久钉成原版。这段代码只允许接触字节(`getClassByteArray` / ASM),不允许持有 `Class` 对象。

中间产物缓存在 `<游戏目录>/.optifine/<OptiFine 版本>/`:

| 文件 | 内容 |
|---|---|
| `cache-format.txt` | 缓存格式版本,与代码不一致就整份重建 |
| `Optifine-mapped.jar` | 重映射后的 OptiFine(不含 MC 类),这就是加进 classpath 的 jar |
| `Optifine.classes.gz` | 打过补丁的 MC 类缓存(ClassCache),供下次启动复用 |

## 已知限制

- **与 Sodium 不兼容**:两者都是渲染器,`fabric.mod.json` 已声明 `conflicts` 与 `breaks`。`no_fog`、`thallium`、`xradiation`、`ryoamiclights` 同样声明为不兼容。完整清单、这些条目的来源,以及加载器到底会不会拦,见下面「[声明的不兼容](#声明的不兼容以及加载器会不会拦)」。
- **c2me 在 1.20.6 上装不进这个产物**:c2me 自己的元数据写着 `breaks: { "optifabric": "*" }`,而加载器**会执行 `breaks`**,于是游戏在**模组解析阶段**就被硬拒载(`NEG_HARD_DEP c2me … {breaks optifabric @ [*]}`),连一个类都不会加载 —— **本模组这边没有任何代码或配置能绕开它**。要用 c2me 只能装替代产物(`wip/1.20.6-reforged` 分支的 `optifabric_reforged`);即便那样,c2me 的线程化世界生成(`c2me-threading-worldgen`)也与 OptiFine 改过的 `class_3898` 不兼容,替代产物自带一个兼容处理在启动时把那一项关掉。实测、代价与两个必须知道的坑见 [`docs/FAQ.md`](docs/FAQ.md) 第四节。
- **RyoamicLights 的具体冲突**:OptiFine 把原版视频设置界面(`class_446`)**整类替换成自己的实现,连父类都换掉**,而 RyoamicLights 的 mixin 注入在原版父类上,于是变换失败(`Delegate constructor lookup failed`)。这是 OptiFine 自身的行为,不是补丁造成的。删掉它不会损失功能 —— OptiFine 自带动态光源(视频设置 → 品质 → 动态光源)。更一般地,凡是往 OptiFine 整类替换的界面类里注入的模组都可能同样失败。
- **OptiFine 看不到 Fabric 模组内部的资源**:日志里会出现成片的 `[OptiFine] Unknown resource pack type: ...ModNioResourcePack`,属于 OptiFine 侧的限制,不影响启动与运行。
- **光影包与 OptiFine 版本不匹配时会报 `[Shaders] Invalid program name: ...`**(例如 Photon 的 `dh_water`、`gbuffers_particles*`),属于光影包自身问题。
- OptiFine 各项功能的具体效果(连接纹理、缩放、动态光源、FPS 优化幅度)尚未逐项验证;启动、进世界、模型与区块渲染、光影子系统已确认工作。

### 声明的不兼容,以及加载器会不会拦

本模组声明的不兼容只写在 `fabric.mod.json` 里,**别的地方(包括任何界面)都看不到**,所以这里把它写成文字。`1.1.3` 产物声明的是:

| 声明 | 条目 |
|---|---|
| `conflicts` | `sodium`(`*`) |
| `breaks` | `no_fog`、`thallium`、`xradiation`、`ryoamiclights`、`sodium`(`*`) |

Sodium 冲突这一条与其中三条 `breaks` **继承自上游**:[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)(其默认分支 `llama` 上的 `fabric.mod.json`,v1.14.3)声明的同样是 `sodium` 冲突,以及 `no_fog`、`thallium`、`xradiation` 三条 `breaks`;它另外还有三条带版本范围的条目,**本移植有意没有带过来**:

| 上游条目 | 没有带过来的原因 |
|---|---|
| `cardinal-components-item <2.4.2` | 1.16/1.17 时代的版本范围,只有那些构建落在范围里 |
| `architectury >1.2.72 <1.3.77` | 1.16/1.17 时代的范围;它背后那个真实冲突是 1.21.x 线修的(见下),这条线没有对应实现 |
| `meteor-client >=0.4.1` | 1.16/1.17 时代的条目 |

`ryoamiclights` 是本移植自己加的,原因见上面那条已知限制。把 `sodium` 也列进 `breaks` 同样是本移植自己加的:上游只在 `conflicts` 里声明它,而有些启动器与平台只读 `breaks`。

**两个字段不等价:加载器会执行 `breaks`,而 `conflicts` 只是警告。** 在 **Fabric Loader 0.19.5** 上实测:

- **`breaks` = 硬拒载。** 在**本条线自己的 1.20.6 实例**里(Loader 0.19.5、Fabric API `0.100.8+1.20.6`、OptiFabric `1.1.2+mc1.20.6`、OptiFine `preview_OptiFine_1.20.6_HD_U_J1_pre18.jar`,`mods/` 里再加 `sodium-fabric-0.5.11+mc1.20.6.jar`)实测:运行在**解析阶段**就结束,第 4 行是

  ```
  Immediate reason: [NEG_HARD_DEP optifabric 1.1.2+mc1.20.6 {breaks sodium @ [*]}, ROOT_FORCELOAD_SINGLE optifabric 1.1.2+mc1.20.6, ROOT_FORCELOAD_SINGLE sodium 0.5.11+mc1.20.6]
  ```

  整个实例连 `Loading <N> mods:` 都没到,没有类被加载过。**所以"装了会被拦住"对 `breaks` 是准确的** —— 拦住你的是加载器,不是本模组。
- **`conflicts` 只是警告。** `2.2.1` 那份产物只声明了 `conflicts: sodium`,在 1.21.x 线上实测时 sodium 照常加载:它应用自己的 mixin、然后**在 mixin 里**失败(见 `compat-matrix\README.md` §10.10);同一份产物在 `sodium` 在场的那次运行里**没有** `Incompatible mods found`。

所以这张表要分两半读:`breaks` 里的条目是**闸门**(装了会被加载器拒载),`conflicts` 里的条目是**声明**(加载器只警告)。本线的 mod id 仍是 `optifabric`,而 Sodium 自己那份元数据里针对的也正是 `optifabric`(Sodium 只在 `breaks` 里声明它,没有 `conflicts`)—— 所以在 id 这一层上,两个 `breaks` 声明会**同时**生效(都记在日志的 `Immediate reason` 里;没有做"只留一条"的对照)。Sodium 1.20.6 构建里那条声明覆盖的确切版本范围,本仓库没有实测记录。

**Architectury**:上游声明它坏,architectury 自己的元数据也写着 `breaks: optifabric <1.13.0`。1.21.x 线在字节码层面修掉了它背后那个冲突(OptiFine 往 `GameRenderer.render` 中间插自己的局部变量,把 Mixin `LocalCapture` 交给处理器的槽位整体顶高,那边用 `LocalSlotLayoutFix` 把多出来的槽位挪到局部变量区末尾),并改名绕开了那条声明。**本线既没有那个 fixer,也没有做过对应的实测**,所以在这条线上 architectury 属于**未测**。

完整清单与复现结论见 [`docs/FAQ.md`](docs/FAQ.md);下面这条是最近一次查清的:

**Litematica 投影 + 光影包会让日志刷满 `[Shaders] OpenGL error: 1282 (Invalid operation), program: gbuffers_terrain, at: pre-useProgram`**
—— 出错的那次调用是 **Litematica 自己的**(`WorldRendererSchematic.renderBlockLayer` 上传原版的
`ShaderProgram.chunkOffset` uniform,而当时绑定的是 OptiFine 的 program),OptiFine 只是**报告**它
(`Shaders.useProgram` 开头就是 `checkGLError("pre-useProgram")`)。`litematica-printer`、Xaero's 系列与 OptiLithium
都逐个查过、**均已排除**。规避:关掉光影包,或停止渲染投影。

### 与 indigo 的关系

`fabric-renderer-indigo`(Fabric API 自带的地形渲染器)与 OptiFine 只能有一个在场,本模组用 Fabric 自己的机制让 indigo 让位:`fabric.mod.json` 里声明 `"custom": {"fabric-renderer-api-v1:contains_renderer": true}`。这个键本来就是给"另一个渲染器"用的(Sodium 用同一个键),而 OptiFine 本身就是地形渲染器。indigo 会打印 `[Indigo] Different rendering plugin detected; not applying Indigo.`,F3 调试界面显示 `[Fabric] Active renderer: none (vanilla)`。

- **代价**:依赖 FRAPI/indigo 的模组不再有 indigo 提供的自定义渲染(地形由 OptiFine 渲染)。
## 常见日志信息

下列输出不影响运行:

| 日志 | 说明 |
|---|---|
| `[OptiFine] (Reflector) Class not present: net.minecraftforge.*` / `sun.misc.SharedSecrets` | OptiFine 在探测 Forge 与旧 JDK 的类,Fabric 上本就没有 |
| `[OptiFine] Unknown resource pack type: ...ModNioResourcePack` | OptiFine 不认识 Fabric 的资源包类型 |
| `[Indigo] Different rendering plugin detected; not applying Indigo.` | 让位键生效,属预期行为 |
| `[Shaders] Invalid program name: ...` | 光影包声明了该版本 OptiFine 不支持的程序名 |
| `Skipping bad option: lastServer` | 选项文件里的旧字段 |

## 排查

- **升级本模组后行为没有变化**:先删掉 `<游戏目录>/.optifine/`,缓存里存的是打过补丁的字节码。
- `[OptiFabric]` 的输出走**启动器控制台**,通常不在 `logs/latest.log` 里;"日志里没有 `[OptiFabric]`"不代表模组没运行。过滤 `[OptiFabric]` 可以看到准备了多少补丁类、Loader 接管了多少。
- 标题界面会弹错误对话框(缺 OptiFine / jar 损坏 / 版本不匹配 / 多份 OptiFine / 内部错误),并提供打开 mods 目录、复制堆栈等按钮;崩溃报告里会多出一节 `OptiFabric`。
- Loader 没有暴露 `fabric-loader:inputGameJar` 时,可显式指定原版 jar:`-Doptifabric.mc-jar=<原版 1.20.6 client jar 路径>`。
- 调试:`-Doptifabric.extract=true` 会把重映射后的 OptiFine 类解包到 `.optifine/<版本>/optifine-classes/`。
- **方块/物品模型成片消失**(日志里成片的 `Unable to bake model: ...: Mixin transformation of <类> failed`):方向是某个 Fabric mixin 变换那个类失败(最外层消息常把真实原因吃掉)。OptiFine 会把原版方法改成转发给自己重载的瘦包装,注入点会随之搬走。
- **卡在加载界面**:取两次线程转储(`jstack <pid>`,间隔十几秒)对比。两次栈相同、CPU 不涨即为卡死;栈顶停在原生调用(如 `glfwSwapBuffers`)属于呈现层问题,注意加载期间不要最小化窗口(开着垂直同步时最小化会让 Render 线程一直阻塞)。

## 后续计划

1. 支持开发环境(dev 命名空间是 `named`,需要两段式重映射并补回上游的 contextual mapping 修正)。
2. 把上游 `compat/**` 的按模组兼容搬回来(需要重写 early riser 机制)。
3. 对着真实的 OptiFine 1.20.6 反编译产物,逐个核对 `patcher/fixes` 里硬编码的 intermediary id 与描述符。

## 许可与致谢

- 本项目遵循 **MPL-2.0**(`LICENSE.txt`),核心逻辑移植自 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)(作者 Modmuss50、Chocohead),移植文件保留来源说明。
- **不包含、也不分发 OptiFine 本体**,OptiFine 版权归 sp614x 所有,请自行获取。
- 逐轮排查过程与离线校验工具见 `docs/DEVELOPMENT.md`,`reference/upstream/` 保存了移植所依据的上游源码快照。

- 希望它能工作
