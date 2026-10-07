# 常见问题

[🇬🇧 English](./FAQ.md) | 🇨🇳 中文版

每个答案都短,而且**都能自己核对**:README 里的表、`COMPATIBILITY.md` 里的一行、或 `fabric.mod.json` 里的一个字段。凡是没有实测答案的问题,就直接写"没有测过",不猜。

**目录**

1. [我需要哪个 OptiFine jar?](#1-我需要哪个-optifine-jar)
2. [游戏弹出关于 OptiFine 的对话框,它到底要什么?](#2-游戏弹出关于-optifine-的对话框它到底要什么)
3. [为什么拒载 Sodium?为什么整个实例直接起不来?](#3-为什么拒载-sodium为什么整个实例直接起不来)
4. [Sodium 生态的其它模组呢(Iris、Indium、Chloride、Sodium Extra……)?](#4-sodium-生态的其它模组呢irisindiumchloridesodium-extra)
5. [我能用 Photon 吗?天空和水面在闪。](#5-我能用-photon-吗天空和水面在闪)
6. [日志刷满 `Invalid program name` 和 `Error compiling vertex shader`,是坏了吗?](#6-日志刷满-invalid-program-name-和-error-compiling-vertex-shader是坏了吗)
7. [1.21.6 / 1.21.7 上光影会崩。](#7-1216--1217-上光影会崩)
8. [兼容信雅互联(Sinytra Connector)和 Kilt 吗?](#8-兼容信雅互联sinytra-connector和-kilt-吗)
9. [这个模组会下载或打包 OptiFine 吗?](#9-这个模组会下载或打包-optifine-吗)
10. [有哪些已知不兼容的模组?](#10-有哪些已知不兼容的模组)
11. [升级之后什么都没变。](#11-升级之后什么都没变)
12. [`[OptiFabric]` 的输出在哪?我在 `latest.log` 里找不到。](#12-optifabric-的输出在哪我在-latestlog-里找不到)
13. [为什么 mod id 是 `optifabric_reforged` 而不是 `optifabric`?](#13-为什么-mod-id-是-optifabric_reforged-而不是-optifabric)
14. [能用在服务器上吗?不带 Fabric API 能用吗?](#14-能用在服务器上吗不带-fabric-api-能用吗)
15. [哪些 Minecraft 版本是真正验证过的?哪些还没有?](#15-哪些-minecraft-版本是真正验证过的哪些还没有)
16. [能用于开发环境吗?](#16-能用于开发环境吗)
17. [这是 AI 写的吗?](#17-这是-ai-写的吗)
18. [报告问题时,要附什么才能真正被看?](#18-报告问题时要附什么才能真正被看)
19. [启动时崩在 `Config.gameSettings is null`,是谁的问题?](#19-启动时崩在-configgamesettings-is-null是谁的问题)

---

## 1. 我需要哪个 OptiFine jar?

用 README 兼容性表里对你那个 Minecraft 版本列出的那个构建 —— **那张表、模组内自带的 `OptifineSupport`、以及 `release/notes/mc<MC>.md`,三者由 `release/version.ps1 -CheckSupport` 互相比对**,所以不会悄悄漂移。

版本必须**精确匹配**:jar 里装着**该版本自己**的 `official → intermediary` 映射表,而模组会从 `net/optifine/Config.class` 里读 `MC_VERSION` 来校验。OptiFine 请从 [optifine.net](https://optifine.net/downloads) 获取 —— 这是本模组唯一会指向的来源。

**安装器形态**(内含 `patch/` 差分包)与**已解包形态**都接受;模组就是靠有没有那个 `patch/` 目录来区分二者的。

## 2. 游戏弹出关于 OptiFine 的对话框,它到底要什么?

它会写出文件名,而且只有三种情形:

| 它显示什么 | 为什么 |
| --- | --- |
| 没找到 OptiFine | `mods/` 里、启动器的共享 `mods/` 里、以及"作为启动器版本安装"的三处,都没有声明你正在运行的 Minecraft 版本的 OptiFine。**每次启动都会提示。** |
| 装的是更旧的预览版 | 那个 jar 是比该版本最新构建更旧的预览版。**每个构建提示一次**,记在 `config/optifabric-mismatch-ack.txt`。 |
| 最新构建无法加载光影 | 那个构建自己的光影路径在启动阶段抛异常。见第 7 问。每个构建提示一次。 |

同一个版本放了**两个** OptiFine jar 是 **`DUPLICATED` 错误**,不是选择题:一个实例只跑一个 OptiFine。

## 3. 为什么拒载 Sodium?为什么整个实例直接起不来?

因为 `breaks` 是**闸门**,不是警告。在 Fabric Loader 0.19.5 上实测:一条 `conflicts` **完全不添加约束**(`ModSolver` 的 `CONFLICTS` 分支至今还是 `// TODO`),游戏照常以 `Warnings were found!` 启动;而一条点到**已存在**模组的 `breaks` 会让求解器给出 `NEG_HARD_DEP`,加载器**拒绝整个实例**。当前产物声明 **5 条 `breaks`** —— `no_fog`、`thallium`、`xradiation`、`ryoamiclights`、`sodium` —— 没有 `conflicts`。

Sodium 在里面是**量出来的、不是假定的**:在 1.21.1 + Sodium 0.8.13 上,把所有可修的缺口都补上之后,Sodium 的 mixin 全部应用成功、客户端也进得了世界 —— 但**整帧全黑**。把那个唯一的渲染槽让给 Sodium、以及关掉 OptiFine 的 Fast Render,两次单变量实验都没有改变结果。两个地形渲染器无法共用同一条管线,所以这里选择**拒载**而不是警告:警告只会让用户拿到一个"能启动、永远不画画"的游戏。

## 4. Sodium 生态的其它模组呢(Iris、Indium、Chloride、Sodium Extra……)?

它们**不需要逐条列**,因为它们都要求 Sodium(`iris` 的元数据里写着 `depends: sodium`;扫描日志里也有 `HARD_DEP chloride ... {depends sodium @ [>=0.8.12]}`)⇒ `breaks: sodium` 在到达它们之前就已经拒载整个实例。逐条加上去只会让错误列表更长。

`moreculling` 与 `immediatelyfast` **不属于**这一族 —— 它们**不**要求 Sodium,它们与 OptiFine 的冲突是**我们这边的缺口**,和别的模组一样登记在仓库根的 [`../COMPATIBILITY.md`](../COMPATIBILITY.md) 里。

## 5. 我能用 Photon 吗?天空和水面在闪。

在本线支持的那些 OptiFine 构建上**不能正常用**,而且这不是本模组能修的东西。以下是 **1.21.10 + `preview_OptiFine_1.21.10_HD_U_J7_pre11` + `photon_v1.2a.zip`** 上的实测:

* OptiFine 打出 **30 条 `Invalid program name`** 并跳过这些程序。在一次更长的会话里,同一批名字重复到 **405** 条 —— 所以这个数字跟的是**时间**,不是包版本。
* 被跳过的正是 **Iris 有、OptiFine 没有**的那些名字:`gbuffers_all_translucent`、`gbuffers_block_translucent`、`gbuffers_entities_translucent`、`gbuffers_particles_translucent`、`gbuffers_particles`,以及 Distant Horizons 的 `dh_water` / `dh_terrain`。
* 这些程序正是**写 `colortex3` 的人**,而延迟光照与混合(b|lend)那两个 pass 要读 `colortex3`。没人写它,画面就是错的:天空与水面按包自己的棋盘格周期闪(`CLOUDS_TEMPORAL_UPSCALING 4` ⇒ **16 帧**),而**开 TAA 会让整幅画面不对**,因为错数据被累积进了历史缓冲。

同一个实例换成 `ComplementaryReimagined_r5.9.1.zip`,**`Invalid program name` 0 条、OpenGL 错误 0 条**。所以:要么换一个支持 OptiFine 的光影包,要么在 Iris 下用 Photon —— 后者需要 Sodium,因此**无法与本模组共存**。

自 **2.2.12** 起,模组会在标题界面对话框里说明这一点,**每个"包名 + 版本"提示一次**(记在 `config/optifabric-photon-ack.txt`)。**升级光影包没有用**:`photon_v1.3b` **新增**了 `composite17` 与 `c17_copy_ao`,**一个名字都没删**。

## 6. 日志刷满 `Invalid program name` 和 `Error compiling vertex shader`,是坏了吗?

这两类都是**日志噪音**,都不代表实例坏了:

* `Invalid program name` —— 光影包声明了 OptiFine 没有的程序名,OptiFine 跳过并报出来。**同样这 30 条也出现在 1.21 / 1.21.3 / 1.21.4 上**,而用户报告那三版是正常的。
* `Error compiling vertex shader: /shaders/world0/prepare.vsh`(以及 `Invalid program "prepare"`)—— 这是**包自己写的守卫**。`p0_clouds_prep.vsh` 结尾是 `#ifndef CLOUD_SHADOWS` / `#error "This program should be disabled if Cloud Shadows are disabled"`。Iris 会按声明**禁用**这个程序;OptiFine 照样去编译、失败、然后重试。**在包的光影设置里把 `Cloud Shadows` 打开**,这条就会消失 —— 那本来就是包的默认值。

其它正常可以忽略的行:`[OptiFine] (Reflector) Class not present: net.minecraftforge.*` 与 `sun.misc.SharedSecrets`(OptiFine 在探测 Forge 与老 JDK)、`[Indigo] Different rendering plugin detected`、`[Shaders] Unknown macro value: IRIS_VERSION`、`Skipping bad option: lastServer`。

## 7. 1.21.6 / 1.21.7 上光影会崩。

确实会,而且崩在 **OptiFine 自己的代码**里:

```
java.lang.NullPointerException: Cannot read field "norm" because "multiTex" is null
  at net.optifine.shaders.ShadersTex.initDynamicTextureNS(ShadersTex.java:322)
```

不是光影包的问题(三个互不相关的包在同一帧崩),也不是打补丁的问题。这两个版本可用的**全部七个** OptiFine 构建表现一致,所以**降级也躲不开**。模组会在对话框里说明,**每个构建一次**。在这两版上**不带光影游玩**,或者换一个最新 OptiFine 构建是正式版的版本。

## 8. 兼容信雅互联(Sinytra Connector)和 Kilt 吗?

**不兼容。** 而且对 Connector 来说,这不是"修一修就好",而是**架构上互斥**:

* **信雅互联**把 Fabric 模组搬进 (Neo)Forge。此时游戏**已经被 Forge/NeoForge 转换过**,而本模组需要的是**未被改动的原版混淆客户端 jar**:它驱动 OptiFine 自己的 `optifine.Patcher` 去打那个 jar,再把结果交给 **Fabric Loader 的 `GameTransformer`**。在 Connector 下,**外层转换链是 NeoForge 的**,交给 Fabric 那条链的字节码**到不了游戏里**;而且 Connector 会**很早就加载游戏类**,把类钉在原版 —— 这恰恰是本模组无法承受的一件事。**NeoForge 上的正确做法是 OptiFine 官方 Forge 版**(官网每个条目都列了它,例如 1.21.11 对应 `Forge 61.0.8`),Fabric 模组交给 Connector。**这条路上 OptiFabric 没有角色。**
* **Kilt** 本身就是一个 Fabric 模组(「brings (Neo)Forge mods into the Fabric ecosystem」),所以它会与本模组**一起被加载**。为了跑 Forge 模组,它要**转换类并加载游戏类**,这与上面同一条约束冲突。**未实测**,但**已知存在冲突点** —— 这不是实测得出的"不行"。

两者都**没有**写进 `fabric.mod.json`:`breaks` 是**无条件闸门**,而"只是装了 OptiFabric(没装 OptiFine 时它处于惰性)"的实例被拒载属于**越权**。

## 9. 这个模组会下载或打包 OptiFine 吗?

**两样都不会,而且上架的每一个产物都是如此。** 打开 jar 逐个核对过:**没有** `net/optifine/*` 类、**没有**内嵌 jar、**没有**下载器类。唯一与网络有关的字符串,是提示文案与文档里那个 `optifine.net` 网址。

GitHub 上另外发布的 **`-full`** 便利版**确实带下载器**(8 个类),它从 optifine.net 把 OptiFine 取到**你自己的机器上** —— 它依然**不打包**任何东西。这个变体只发 GitHub,是因为平台要求把"运行时下载"从已发布产物里移除。

## 10. 有哪些已知不兼容的模组?

本模组只声明 **5 个 `breaks` id**(见第 3 问)。其余实测过的都在仓库根的 [`../COMPATIBILITY.md`](../COMPATIBILITY.md),**逐模组一行**,按结果分块:**465** 个进了世界、**27** 个在 OptiFine 旁失败(复查后归因到我们这边)、**8** 个在不装本模组时也坏、**10** 个只到标题界面、**8** 个依赖未解析。

那些数字来自 **1.21.1 上的一次扫描**。此后**没有重跑**,所以清单里的某个模组现在可能已经能用 —— 那些行记录的是**当时看到的失败**,不是永久判决。

## 11. 升级之后什么都没变。

删掉 `<游戏目录>/.optifine/`。那个目录里是上一次运行留下的**打过补丁的字节码**与重映射后的 OptiFine jar,模组会重建它;缓存格式号变化也会自动让它失效。

## 12. `[OptiFabric]` 的输出在哪?我在 `latest.log` 里找不到。

它走**启动器控制台**,通常**不在** `logs/latest.log` 里。把控制台过滤 `[OptiFabric]`,就能看到准备了多少个类、Loader 接管了多少个 —— 一次典型启动大约 **127 行**。另一个地方是**崩溃报告**:里面会多出一节 `OptiFabric`,写明 OptiFine 版本、jar 状态与 mapped jar 路径。

## 13. 为什么 mod id 是 `optifabric_reforged` 而不是 `optifabric`?

因为有些模组声明了 `"breaks": {"optifabric": "*"}`(Sodium 就是;1.20.6 那条线上 c2me 也是),而 Fabric Loader 按 **id** 匹配 —— **只改显示名没用**。在 1.20.6 那条线上,这两个 id 是两个**不同的产物**,而且**只能装一个**。

## 14. 能用在服务器上吗?不带 Fabric API 能用吗?

**仅客户端**(`"environment": "client"`),而且**不需要 Fabric API 就能启动**:它声明的依赖只有 `fabricloader` 与 `minecraft`。Fabric API 是**与它一起测过**的,大多数整合包也会有;但缺少它不是错误路径 —— 在 Fabric 渲染器注册表本该为空的地方,模组会注册一个**惰性占位渲染器**。

## 15. 哪些 Minecraft 版本是真正验证过的?哪些还没有?

**十个版本,1.21 到 1.21.11**,每个都有自己的 jar 和自己的 OptiFine 构建(见第 1 问)。每个版本验证了什么,**是写下来的而不是概括的**:离线验证(`VerifyPatched` 走 JVM 验证器、ASM 数据流验证器与 mixin 冲突扫描器)加上一份**抓取的启动日志**。**没有**验证的也照写 —— 例如 1.21.6 与 1.21.7 根本无法加载光影包;而 1.20.6 那条线的"进世界"这一步**从未跑过**,因为当时那台机器上**没有 1.20.6 存档**。

## 16. 能用于开发环境吗?

不能。`gradlew runClient` 是**故意拒绝**的:开发环境跑在 `named` 命名空间,而本模组给**混淆过的客户端 jar** 打补丁,并按那个命名空间交付补丁类。要支持它,需要**第二段重映射**。

## 17. 这是 AI 写的吗?

是 —— 这个移植在 **AI 协助**下编写与验证,每一份 README 与发布说明的开头都写着这一点。这恰恰是**应该看证据而不是看主张**的理由:README 里的数字来自本仓库里**抓取的日志与原始报告**;凡是没测的,就写成"**未测**"。

## 18. 报告问题时,要附什么才能真正被看?

按这个顺序给:

1. **OptiFine 的 jar 名**与 **OptiFabric 的 jar 名**(版本就在文件名里)。
2. **启动器控制台输出**,过滤 `[OptiFabric]` —— 不是 `latest.log`,那里没有它。
3. **崩溃报告**(如果有)—— 里面的 `OptiFabric` 一节会写明 jar 状态。
4. **模组列表**,以及"**不装 OptiFabric 时同一套是否也失败**"。在纯 Fabric 下也失败的模组不是本模组的问题,而这个对照是判断它最快的方法。
5. 渲染类问题:请说明**关掉光影**时是否仍然出现,以及**换另一个光影包**时是否仍然出现。这两个对照正是把"包侧问题"与"补丁侧问题"分开的东西。

## 19. 启动时崩在 `Config.gameSettings is null`,是谁的问题?

**症状**:客户端在**初始化阶段**就崩(还没到标题界面),栈形如:

```
java.lang.NullPointerException: Cannot read field "ofRandomEntities" because "net.optifine.Config.gameSettings" is null
	at net.optifine.Config.isRandomEntities(Config.java:…)
	at net.minecraft.class_1921.getCustomTexture(class_1921.java:…)
	at …<某个模组>…<clinit>
Caused by: Could not execute entrypoint stage 'client' … provided by '<那个模组>'
```

读 `ofTelemetry`、`ofFastRender` 这类字段时也是同一个栈。

**原因**:Fabric 的 `client` 入口点跑在**游戏构造 `GameSettings` 之前**,而 `net.optifine.Config.gameSettings` 正是 OptiFine 在那一刻才填进去的。所以**任何在入口点里(尤其是静态初始化里)读 OptiFine `Config` 字段的模组**,拿到的都是 `null`。

**为什么我们不"顺手给个默认值"**:给一个默认对象会让这些模组按**默认值**继续跑,而它们正是读 `ofRandomEntities` 之类来决定**注册哪些模型/材质**的 —— 那就会静默注册**错误的东西**,比现在这个清清楚楚的 NPE 更糟。所以这是**有意的取舍**,不是待修的缺陷。

**怎么办**:

* 让那个模组把读取挪到**客户端 tick 或客户端生命周期回调**里(通常一行改动,位置见它的 `onInitializeClient` 或触发它的静态初始化);
* 在那之前先把它移出 `mods/`,客户端就能起来;
* 已经见过的实例:`ebe`、`sample--mr-betternether`(bclib 的客户端入口点)、`shooting_star_demo`(The Shooting Star Demo 的 `RemoteRenderer` 静态初始化)。
