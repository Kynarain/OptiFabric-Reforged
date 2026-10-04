# OptiFabric

<p align="center">
  <img src="src/main/resources/assets/optifabric/icon.png" alt="OptiFabric" width="128"/>
</p>

🇨🇳 中文版 | [🇬🇧 English](./README.md)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21%20~%201.21.11-green.svg)](https://www.minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-%E2%89%A5%200.19.5-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21%2B-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MPL--2.0-lightgrey.svg)](LICENSE.txt)

> ⚠️ 此模组由 deepseek 编写并验证,请小心用于生产环境。

在 **Fabric Loader** 下加载 **OptiFine**。把 OptiFine 的 jar 和本模组一起放进 `mods/`,启动时会用 OptiFine 自带的补丁器给原版客户端打补丁、重建被搬走的 lambda、把 OptiFine 从官方混淆名重映射到 intermediary,并把打过补丁的 Minecraft 类交给 Fabric Loader 的类转换器接管,从而让两者共存。**不包含、也不分发 OptiFine 本体。**

本分支是 **1.21.x 线**,覆盖 Minecraft **1.21 – 1.21.11**(OptiFine 出过构建的全部十个版本)。26.x 线(Minecraft 26.2 与 26.1.2)在自己的分支/worktree 上独立开发,两条线的 jar **不能互相替代**。

## 📖 概览

OptiFine 不是 Fabric 模组:它的 jar 里是针对原版**混淆**客户端类的字节码补丁,加上 OptiFine 自己的类。OptiFabric 在 `preLaunch` 阶段驱动 OptiFine 的补丁器、去混淆、重映射到运行期命名空间、修掉它重编译后留下的结构问题,并在 Mixin 之前把这些补丁类注册进 Loader。

**一个 Minecraft 版本一个 jar** —— 每个 jar 里都打包着该版本的 `official → intermediary` 映射表(官方混淆名每版不同,用错版本会把 OptiFine 重映射成乱码),`fabric.mod.json` 里的 `minecraft` 依赖也精确到该版本。

**作者:** kynarain · 上游:Modmuss50、Chocohead
**版本:** 十个产物都是 `2.2.1`(1.21 – 1.21.11)
**许可:** MPL-2.0

## ✨ 主要特性

- 🔄 **不需要手动跑 OptiFine 安装器** —— 安装器形态(含 `patch/` 差分包)或已解包的形态丢进 `mods/` 即可,补丁在启动时完成
- 🧩 **看得见游戏的重映射** —— 游戏 jar 同时进重映射器的 classpath 与输入,子类里覆写的方法才不会留成 OptiFine 的名字(有单个类曾漏掉 35 个方法)
- 📦 **一份源码、每版一个 jar** —— 1.21 到 1.21.11,每个 jar 绑死自己那一版的映射表,用 `-Pmc=<版本>` 构建
- 🎨 **抗锯齿真的能用** —— 1.1.2 起不再动 OptiFine 自带的 `post_effect/` 链,并给"后处理管线没有顶点属性"的两版(1.21.9 / 1.21.10)重写 FXAA 顶点着色器
- 🛠️ **字节码修复成体系** —— 一批 fixer 处理 OptiFine 重编译抹掉的东西:原版方法体、注入点、合成字段、对象创建点、被改名的 lambda、区域构造
- 🧪 **离线验证是一等公民** —— 每个补丁类与每个 OptiFine 类都在与游戏一致的单一加载器里加载,用 JVM 验证器 + ASM 数据流验证器双向检查,再加 5 个扫描器
- ⚙️ **走缓存** —— 整条流水线的结果缓存在 `.optifine/<OptiFine 版本>/`,之后的启动 1–2 秒
- 🧯 **失败时说实话** —— 缺 OptiFine / jar 损坏 / 放了两份 / 版本不匹配,都会在标题界面弹错误对话框,并在崩溃报告里追加 `OptiFabric` 一节

## 🏗️ 工作原理

```
mods/OptiFine_1.21.11_HD_U_J9.jar
        │  ① 用 OptiFine 自带的 optifine.Patcher 给原版(混淆)客户端 jar 打补丁
        │     (1.21.6 起的 OptiFine 用 xdelta 差分包,但 Patcher.process 的用法没变)
        ▼
   打补丁后的 vanilla jar(OptiFine 的补丁 + OptiFine 的类)
        │  ② LambdaRebuilder:补丁类里的 lambda 指向已被搬走的原方法,需要重建
        │  ③ tiny-remapper:official(混淆)→ intermediary
        │     **必须把游戏 jar 放进重映射器的 classpath**,否则子类里的覆写继承不到映射
        ▼
   Optifine-mapped.jar
        │  ④ 拆成两部分
        ├── 非 Minecraft 类(OptiFine 自己的类与资源)─────► 加进游戏 classpath
        └── net/minecraft/** 打过补丁的类 ────────────► ClassCache(替换用)
```

类替换走 **Fabric Loader 自己的 GameTransformer**:Minecraft 类被加载时,Loader 会先问游戏 provider 的 `GameTransformer.transform(类名)` 有没有现成的字节码 —— 这一步发生在 Mixin **之前**。所以 `preLaunch` 阶段注册进去的补丁类(先经过 `patcher/fixes` 的修正)直接顶替,而 Loader 自己补过的类保持 Loader 的版本。

因此不需要为每个补丁类生成 stub mixin,也不依赖 Mixin 的扩展 API;交出去的是 Mixin 的**输入**而非输出,其它模组针对这些类的 mixin 照常生效。

一条硬性约束:**在补丁类交给 Loader 之前,不能对游戏类做任何反射解析** —— 例如一次 `Class.getMethods()` 就会把方法签名里的游戏类型全部加载掉,这些类会被永久钉成原版。这段代码只接触**字节**(`getClassByteArray` / ASM),不持有 `Class` 对象。

| 组件 | 作用 |
|---|---|
| `OptifabricRuntime` | 整条流水线:找 jar → 打补丁 → 重映射 → 修复 → 注册 |
| `GameTransformerHook` | 把补丁类注入 Loader 的游戏 transformer |
| `OptifineMappings` | 规则推导的 contextual mapping(取代上游的手写表) |
| `OptifineJarFixer` | 修 OptiFine 自己那份 jar:后处理 json 的格式、1.21.6/1.21.7 被写死的 shaderpack 加载、1.21.9/1.21.10 的 FXAA 顶点着色器 |
| `patcher/fixes/**` | 逐个版本的字节码 fixer(原版方法体、注入点、合成字段……) |
| `RendererApiFallback` | 在 Fabric 渲染器 API 本该为空的地方注册惰性占位器 |

中间产物缓存在 `<游戏目录>/.optifine/<OptiFine 版本>/`:

| 文件 | 内容 |
|---|---|
| `cache-format.txt` | 缓存格式版本(当前 `26`),与代码不一致就整份重建 |
| `Optifine-mapped.jar` | 重映射后的 OptiFine(不含 MC 类),这就是加进 classpath 的 jar |
| `Optifine.classes.gz` | 打过补丁的 MC 类缓存(ClassCache),供下次启动复用 |

## 📦 安装

1. 准备与本版本**严格一致**的 OptiFine(见下表) —— OptiFabric 会读 `optifine/Config` 里的 `MC_VERSION` 校验,不一致会直接在标题界面报错。**不需要**先运行 OptiFine 安装器。
2. 把**对应版本**的 jar 与 OptiFine 的 jar 一起放进该 Fabric 版本自己的 `mods/` 目录。不要放两份 OptiFine(会报 `DUPLICATED`),不要放错版本的 OptiFabric,也不要混进 26.x 线的 jar。**从 1.x 升级时先删掉旧的 `OptiFabric-<版本>+mc1.21.x.jar`** —— 2.0.0 把 mod id 改成了 `optifabric_reforged`(显示名 OptiFabric Reforged),两个 id 同时存在时 Fabric 会**同时加载两份**,OptiFine 会被打两遍补丁。
3. 用 **Fabric 版本**启动,不要用启动器注入 OptiFine 的 `1.21.x-OptiFine_xxx` 版本(那个是启动器在启动时注入 OptiFine,会与本模组重复)。
4. 首次启动会明显变慢(实测 5–7 秒,要跑完整的补丁与重映射流程),之后走缓存(1–2 秒)。标题界面出现 OptiFine 版本号、视频设置里出现 OptiFine 选项即表示成功。

PCL2 / HMCL 开启版本隔离时,游戏目录与 `mods/` 都在 `versions/<版本名>/` 下,`.optifine/` 缓存也建在那里;没开隔离才是 `.minecraft/mods`。

```powershell
# 1.21.11 的 OptiFine(该版本最新正式版):用提示里的「下载 OptiFine」按钮即可,或打开
# https://optifine.net/downloads 自行下载 OptiFine_1.21.11_HD_U_J9.jar
```

## 🔨 从源码构建

需要 **JDK 21+**,仓库根目录就是 Gradle 项目 —— 目标版本由 `-Pmc` 指定(不带则用 `gradle.properties` 里的默认版本):

```powershell
.\gradlew build "-Pmc=1.21.11"                            # PowerShell 里必须加引号,否则 1.21.11 会被拆开
.\gradlew build "-Pmc=1.21.8" "-Pmod_version_base=2.1.0"  # 只有该产物的版本号与整条线的基数不同时才需要它
```

产物在 `build/libs/OptiFabric-<版本>+mc<MC版本>.jar`。版本号只通过一个脚本改:

```powershell
.\release\version.ps1                                     # 看当前版本与三类递增各会变成什么
.\release\version.ps1 -Mc 1.21.8 -Kind patch               # 只给某一个产物升版
```

开发环境不受支持:`gradlew runClient` 会被明确拒绝,因为开发环境的命名空间是 `named`,需要额外的 contextual mapping 层。

## 📋 运行要求

| | |
|---|---|
| Minecraft | 1.21、1.21.1、1.21.3、1.21.4、1.21.6、1.21.7、1.21.8、1.21.9、1.21.10、1.21.11 |
| Fabric Loader | ≥ 0.19.5 |
| Java | 21+(实测 25) |
| 侧 | 客户端 |
| OptiFine | 自备,**版本必须严格一致**(见下表) |
| Fabric API | 可选 —— 实测用与你 MC 版本对应的那一版 |

## 🤝 兼容性

| Minecraft | 产物 | OptiFine 构建 | 状态 |
|---|---|---|---|
| 1.21 | `OptiFabric-2.2.4+mc1.21.jar` | `preview_OptiFine_1.21_HD_U_J1_pre9.jar` | ✅ 已实测 |
| 1.21.1 | `OptiFabric-2.2.4+mc1.21.1.jar` | `OptiFine_1.21.1_HD_U_J1.jar` | ✅ 已实测 |
| 1.21.3 | `OptiFabric-2.2.4+mc1.21.3.jar` | `OptiFine_1.21.3_HD_U_J2.jar` | ✅ 已实测 |
| 1.21.4 | `OptiFabric-2.2.4+mc1.21.4.jar` | `OptiFine_1.21.4_HD_U_J3.jar` | ✅ 已实测 |
| 1.21.6 | `OptiFabric-2.2.4+mc1.21.6.jar` | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` | ⚠️ 能启动能玩,**一开光影就崩**(见下) |
| 1.21.7 | `OptiFabric-2.2.4+mc1.21.7.jar` | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` | ⚠️ 同上 |
| 1.21.8 | `OptiFabric-2.2.4+mc1.21.8.jar` | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` | ✅ 已实测 |
| 1.21.9 | `OptiFabric-2.2.4+mc1.21.9.jar` | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` | ✅ 已实测 |
| 1.21.10 | `OptiFabric-2.2.4+mc1.21.10.jar` | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` | ✅ 已实测 |
| 1.21.11 | `OptiFabric-2.2.4+mc1.21.11.jar` | `OptiFine_1.21.11_HD_U_J9.jar` | ✅ 已实测 |

表里这一列是该 MC 版本**最新的正式版** OptiFine;若该版本官方还没有正式版,则用**最新的预览版**代替
(所以 1.21.4 写的是 J3,而不是更新的预览 J4_pre2)。同一 MC 版本的其它构建同样可用 —— 游戏内提示只在你 mods/ 里的
jar 是**预览版**、且比表里这个更旧时才会出现(你已经装了正式版就不会被打扰,哪怕有更新的正式版)。

OptiFine 没出过 **1.21.2 / 1.21.5** 的构建,所以这两版没有对应 jar。

### OptiFabric 版本 → Minecraft 版本 → 需要的 OptiFine 构建

这张表与模组里自带的那张一致,也和 `release\notes\mc<MC>.md` 每版写的要求一致;三者只要有一处对不上,`release\version.ps1 -CheckSupport` 就会失败。

| OptiFabric 版本 | Minecraft 版本 | 需要的 OptiFine 构建 |
|---|---|---|
| `2.2.4+mc1.21` | 1.21 | `preview_OptiFine_1.21_HD_U_J1_pre9.jar` |
| `2.2.4+mc1.21.1` | 1.21.1 | `OptiFine_1.21.1_HD_U_J1.jar` |
| `2.2.4+mc1.21.3` | 1.21.3 | `OptiFine_1.21.3_HD_U_J2.jar` |
| `2.2.4+mc1.21.4` | 1.21.4 | `OptiFine_1.21.4_HD_U_J3.jar` |
| `2.2.4+mc1.21.6` | 1.21.6 | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` |
| `2.2.4+mc1.21.7` | 1.21.7 | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` |
| `2.2.4+mc1.21.8` | 1.21.8 | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` |
| `2.2.4+mc1.21.9` | 1.21.9 | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` |
| `2.2.4+mc1.21.10` | 1.21.10 | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` |
| `2.2.4+mc1.21.11` | 1.21.11 | `OptiFine_1.21.11_HD_U_J9.jar` |

当 OptiFabric 加载了、而上面那个 jar 不在(或者不是该 Minecraft 版本需要的那个构建)时,游戏不再默默启动,而是弹出一个界面告诉你缺哪个文件:上面有 `下载 OptiFine` 按钮,从 OptiFine **官网**(`optifine.net`,也是本模组唯一会去下载的地方)取回它,旁边就是打开 mods 文件夹;按钮上方的地址栏可以换成你自己的地址,那样就只从你填的地方取。

### 1.21.6 / 1.21.7 的光影限制

这两版**不开光影时一切正常**(标题界面正常渲染、能进世界、集成服务端、区块构建与保存都正常),但**只要启用光影包**,游戏就会在启动阶段崩:

```
java.lang.NullPointerException: Cannot read field "norm" because "multiTex" is null
  at net.optifine.shaders.ShadersTex.initDynamicTextureNS(ShadersTex.java:322)
  at net.minecraft.class_1043.method_71142 -> class_1043.<init> -> class_310.<init>
```

- **与光影包无关**:三个互不相同的包(Complementary Reimagined、Sildur's Vibrant Shaders、BSL)崩在同一个栈;把包里的自定义纹理声明与动画元数据全部删掉再重打包,同样崩。
- **也不是我们打的补丁**:崩溃点在创建第一批纹理时,早于任何与具体光影包相关的逻辑 —— 触发条件就是"光影被启用"。
- 根因在 **OptiFine 这两版的预览构建自身**:它给 `class_1043.<init>` 插入的调用缺少前置的 `setParentTexture` 关联,而被调用的 `initDynamicTextureNS` 会直接解引用 `getMultiTexID()` 的结果。这两版可用的 OptiFine 构建共七个,全部崩在同一个栈,所以降级到更早的 preview 也不能规避。

| | |
|---|---|
| ✅ 可用 | OptiFine 的视频设置、缩放、连接纹理、动态光源、**光影**(1.21.6 / 1.21.7 除外),以及 1.1.2 起的**抗锯齿** |
| ⚠️ 有意中和 | `BEFORE_BLOCK_OUTLINE` 事件不再触发(方块描边仍照画);移动方块的 FRAPI 渲染钩子失效(移动方块由原版路径正常渲染) |
| ❌ 不兼容 | **Sodium**(已声明 `conflicts` 与 `breaks`),以及 `no_fog`、`thallium`、`xradiation`、`ryoamiclights`(已声明 `breaks`)。RyoamicLights 的具体原因是 OptiFine 把原版视频设置界面**整类替换成自己的实现,连父类都换掉**,而它的 mixin 注入在原版父类上;删掉它不会损失功能,OptiFine 自带动态光源。完整清单、来源与加载器实际会怎么做:[下面这一节](#声明的不兼容以及加载器实际会怎么做) |
| 📄 OptiFine 侧限制 | OptiFine 看不到 Fabric 模组内部的资源(`[OptiFine] Unknown resource pack type: …ModNioResourcePack`);光影包与你的 OptiFine 版本不匹配时会打印自己的 `[Shaders]` 报错 |

### 声明的不兼容,以及加载器实际会怎么做

本模组声明的不兼容只写在 `fabric.mod.json` 里,**别的地方(包括任何界面)都看不到**,所以这里把它写成文字。`2.2.3` 产物声明的是:

| 声明 | 条目 |
|---|---|
| `conflicts` | `sodium`(`*`) |
| `breaks` | `no_fog`、`thallium`、`xradiation`、`ryoamiclights`、`sodium`(`*`) |

Sodium 冲突这一条与其中三条 `breaks` **继承自上游**:[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)(其默认分支 `llama` 上的 `fabric.mod.json`,v1.14.3)声明的同样是 `sodium` 冲突,以及 `no_fog`、`thallium`、`xradiation` 三条 `breaks`;它另外还有三条带版本范围的条目,**本移植有意没有带过来**:

| 上游条目 | 没有带过来的原因 |
|---|---|
| `cardinal-components-item <2.4.2` | 1.16/1.17 时代的版本范围,只有那些构建落在范围里 |
| `architectury >1.2.72 <1.3.77` | 1.16/1.17 时代的范围,而且它背后那个冲突在 1.21.1 上已经**被修好**(见下) |
| `meteor-client >=0.4.1` | 1.16/1.17 时代的条目 |

`ryoamiclights` 是本移植自己加的,原因见上面那张表。把 `sodium` 也列进 `breaks` 同样是本移植自己加的:上游只在 `conflicts` 里声明它,而有些启动器与平台只读 `breaks`。

**两个字段不是一回事:`conflicts` 只警告,`breaks` 会被执行。** Sodium 两处都写了。在 **Fabric Loader 0.19.5** 上实测:
`conflicts` 条目根本不会给加载器的依赖求解器添加任何约束 —— `ModSolver` 里 `CONFLICTS` 分支至今还是一句
`// TODO: soft negative dep?` —— 所以日志开头是 `Warnings were found!`,然后照常进入 `Loading 56 mods:`。
而 `breaks` 点到**已存在**的模组时是另一回事:求解器给出 `NEG_HARD_DEP`,加载器**拒绝这个组合**,不是放行。
本仓库记录到的一次运行里写着 `NEG_HARD_DEP optifabric_reforged 2.2.2 {breaks sodium}`。所以把 sodium 写进 `breaks`
是**闸门**,不是声明 —— 装了 sodium 就起不来;只写 `conflicts` 的条目才只是警告。请把这张表读成
"作者已知这个组合会坏",其中 `breaks` 那一半加载器会真的拦,`conflicts` 那一半不会。

**Sodium 这一对只有一侧还在声明。** Sodium 只在 `breaks` 里声明这一对 —— 我们手上的 sodium 构建都没有 `conflicts`(`sodium-fabric` `0.5.11+mc1.21`、`0.6.13+mc1.21.1`、`0.8.13+mc1.21.1` 都是 `"breaks": {"optifabric": "*"}`)—— 而它点名的是**旧 mod id `optifabric`**;本线从 2.0.0 起以 `optifabric_reforged`(显示名 *OptiFabric Reforged*)发布,那条规则根本不会命中,所以你在 sodium 上看到的那条警告来自**本模组这一侧**。把 sodium 写进我们自己的 `breaks`,正是把改名弄丢的那条声明补回来:在本线上,它是加载器唯一会理会的 sodium 声明。

**Architectury 是反过来的例子,值得留着。** 上游声明 architectury 坏,architectury 自己的元数据也声明了 `breaks: optifabric <1.13.0`。而在 1.21.1 上,本移植把它背后真正的冲突修掉了:OptiFine 往 `GameRenderer.render` **中间**插了自己的局部变量,把 Mixin `LocalCapture` 交给处理器的原版槽位整体顶高,`LocalSlotLayoutFix` 则把这些多出来的槽位挪到局部变量区末尾。现在 `architectury-api` `13.0.11` 能通过兼容性扫描(矩阵第 14 行),而改成 `optifabric_reforged` 正是让 architectury 那条声明不再命中的原因。实测记录见 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) 与 [`docs/RELEASE_NOTES.md`](docs/RELEASE_NOTES.md)。

实测层面的全貌 —— 哪些模组在 OptiFabric 旁边真的会挂、其中哪些是我们的问题 —— 见 [`docs/COMPATIBILITY_CN.md`](docs/COMPATIBILITY_CN.md)。

### 与 indigo 的关系

`fabric-renderer-indigo`(Fabric API 自带的地形渲染器)与 OptiFine 只能有一个在场,本模组用 Fabric 自己的机制让 indigo 让位:`fabric.mod.json` 里声明 `"custom": {"fabric-renderer-api-v1:contains_renderer": true}`。这个键本来就是给"另一个渲染器"用的(Sodium 用同一个键),而 OptiFine 本身就是地形渲染器。indigo 会打印 `[Indigo] Different rendering plugin detected; not applying Indigo.`。

只声明这个键还不够:Fabric 的渲染器模块查的是**注册表**,为空时会抛 `Attempted to retrieve active rendering plug-in before one was registered`,所以本模组另外注册了惰性占位渲染器(F3 调试界面显示 `Renderer: OptifineRendererPlaceholder`)。想换回 indigo 就得删掉那个 `custom` 键并重新构建 —— 但那样 `ChunkBuilder$BuiltChunk$RebuildTask` 一加载就会因为缺失注入点而崩。

## 📊 验证状态

每个版本一条命令,下面这些数字来自 `1.1.2`(逐版本各跑一次):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-version.ps1 -Version 1.21.10 -ModVersion 1.1.2
```

| Minecraft | 补丁类(JVM) | OptiFine 类(JVM) | ASM 验证器 | @At / refmap / 契约 / 句柄 | 真机 |
|---|---|---|---|---|---|
| 1.21.3 | 440 / 440 | 816 / 816 | 0 问题 | 2 / 0 / 0 / 0 | 抗锯齿 + 光影,无报错 |
| 1.21.4 | 474 / 474 | 812 / 812 | 0 问题 | 2 / 0 / 0 / 0 | 无报错 |
| 1.21.6 | 487 / 487 | 820 / 820 | 0 问题 | 4 / 0 / 0 / 0 | 能启动能玩;光影崩(OptiFine 构建自身) |
| 1.21.7 | 500 / 500 | 823 / 823 | 0 问题 | 4 / 0 / 0 / 0 | 同上 |
| 1.21.8 | 516 / 516 | 831 / 831 | 0 问题 | 4 / 0 / 0 / 0 | 无报错,抗锯齿目测正常 |
| 1.21.9 | 519 / 519 | 832 / 832 | 0 问题 | 4 / 0 / 0 / 0 | 无报错 |
| 1.21.10 | 553 / 553 | 836 / 836 | 0 问题 | 4 / 0 / 0 / 0 | 无报错,画面确认不是黑屏 |
| 1.21.11 | 570 / 570 | 874 / 874 | 0 问题 | 4 / 0 / 0 / 0 | 无报错,抗锯齿目测正常 |

剩下那几条 `@At` 全部属于**被有意停用**的 indigo;补丁类数量是在与游戏一致的单一加载器里点出来的。

**已知缺口:**

1. **不支持开发环境** —— dev 命名空间是 `named`,需要两段式重映射并补回上游的 contextual mapping 修正
2. **被中和的钩子是占位实现**,不是能用的实现(`Renderer.get()` 返回惰性渲染器)
3. **1.21.6 / 1.21.7 的光影缺陷** —— 等 OptiFine 出新构建,或把已写好但尚未接线的 `GpuTextureLinkFix` 上线

## 📝 项目结构

```
OptiFabric/
├── src/main/java/kynarain/cn/optifabric/
│   ├── Optifabric.java              # preLaunch 入口
│   ├── mod/                         # 流水线、transformer 钩子、jar 修复器、渲染器兜底
│   ├── patcher/                     # ClassCache、LambdaRebuilder 与 fixes/(字节码修复)
│   ├── mixin/                       # 本模组自己的两个 mixin
│   └── util/                        # ASM / mixin / remap / zip 工具
├── src/main/resources/              # fabric.mod.json、optifabric.mixins.json、assets/…/icon.png
├── docs/                            # DEVELOPMENT.md、COMPATIBILITY.md(+ _CN)、compatibility/(1.21.1 实测数据)……
├── release/                         # version.ps1、publish.ps1、notes/、MANUAL_RELEASE.md
├── build.gradle · gradle.properties · settings.gradle
└── gradlew · gradlew.bat
```

## 🔐 许可

**MPL-2.0** —— 见 [`LICENSE.txt`](LICENSE.txt)。核心逻辑移植自 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric),移植文件保留来源说明。OptiFine 本体**不包含、也不随本项目分发** —— 版权归 sp614x,请自行从 [optifine.net](https://optifine.net/)获取。

## 🙋 支持与排查

- **升级本模组后行为没有变化**:先删掉 `<游戏目录>/.optifine/`,缓存里存的是打过补丁的字节码(缓存格式号 `26`,不一致会自动重建)。
- `[OptiFabric]` 的输出走**启动器控制台**,通常不在 `logs/latest.log` 里;过滤 `[OptiFabric]` 能看到准备了多少补丁类、Loader 接管了多少。
- 标题界面会弹错误对话框(缺 OptiFine / jar 损坏 / 版本不匹配 / 多份 OptiFine / 内部错误),崩溃报告里会多出一节 `OptiFabric`(OptiFine 版本、jar 状态、重映射 jar 路径)。
- 显式指定原版 jar:`-Doptifabric.mc-jar=<原版 client jar 路径>`;调试解包:`-Doptifabric.extract=true`。
- **模型/物品/贴图成片消失**(日志里成片的 `Unable to bake … model`):方向是某个 Fabric mixin 变换那个类失败(最外层消息常把真实原因吃掉)。OptiFine 会把原版方法改成转发给自己重载的瘦包装,注入点会随之搬走。
- **卡在加载界面**:取两次线程转储(`jstack <pid>`,间隔十几秒)对比。两次栈相同、CPU 不涨即为卡死;栈顶停在原生调用(如 `glfwSwapBuffers`)属于呈现层问题 —— 加载期间不要最小化窗口(开着垂直同步时最小化会让 Render 线程一直阻塞)。
- **`NoClassDefFoundError: Could not initialize class net.optifine.reflect.Reflector`**(或启动阶段直接退出、连崩溃报告都没有):OptiFine 的崩溃报告器会通过 `Reflector` 读取自己的版本号,这次读取会加载某个游戏类,而该类因为**另一个模组的 mixin 没能注入到 OptiFine 改写过的类里**而无法完成加载。`logs/latest.log` 只会指出是哪个类,不会指出是哪个模组。
- **定位模组**:在 JVM 参数里加上 `-Dmixin.debug=true`(启动器设置 → Java/JVM 参数,或实例的高级设置)再启动一次 —— Mixin 只在 debug 模式下打印失败的模组名。加了这个参数后日志里会出现 `Mixin apply for mod <模组> failed … -> net.minecraft.class_<n>`,紧跟着通常就是真正的注入错误。
- **解决办法**是删掉日志里点名的那个模组,或者干脆不用 OptiFine。这属于 OptiFine 自身的类补丁与该模组注入之间的冲突,已知有三类:在 OptiFine 重写的类里捕获/修改方法局部变量或参数的模组;要求某个方法里调用点数量正好相等的模组;以及 —— **OptiFabric 这边改不了的一类** —— 注入点本身就是某个调用点、而 OptiFine 把它换成了自己的方法(例如把原版 `ParticleManager.method_3049` 调用换成 `ParticleManager.render`),注入点数量直接变成 0,只能由模组作者放宽条件或改指向。不加参数时 `latest.log` 里没有任何指向元凶的信息 —— 堆栈最后停在 OptiFine 的 `Reflector`,不要据此猜测。 1.21.1 上的已知病例:`CarryOn` 2.2.6.13(注入在 OptiFine 换掉的 `ParticleManager` 调用点上)与 `ShoulderSurfing` 5.2.0(要求 `Camera` 里调用点数量恰好相等)**我们修不了**;`SophisticatedCore` 的 `ParticleEngineMixin` 已在本构建中修好(它的目标方法被折进了 lambda)。`EntityCulling` 曾被怀疑但已排除 —— 移除它没有任何变化。

下列日志不影响运行:

| 日志 | 说明 |
|---|---|
| `[OptiFine] (Reflector) Class not present: net.minecraftforge.*` / `sun.misc.SharedSecrets` | OptiFine 在探测 Forge 与旧 JDK 的类,Fabric 上本就没有 |
| `Failed to locate initialiser injection point in <init>(class_2591,…)` | 应用 OptiFine 的 `BlockEntity` 补丁的代价(跳过它会留下 5 处悬空引用) |
| `[OptiFabric] Resource not found: minecraft:shaders/post/fxaa_of_{2,4}x.json` | OptiFine 仍会到 1.21.6 之前的老位置探测一次抗锯齿链;真正在用的链在 `post_effect/` |
| `[Shaders] Unknown macro value: IRIS_VERSION` / `ANGELICA_VERSION` | 光影包在探测 Iris / Angelica,OptiFine 不认这两个宏 |
| `[Shaders] Invalid macro expression` / `ParseException: Model variable not found: …` | 光影包与本版 OptiFine 不匹配 |
| `Skipping bad option: lastServer` | 选项文件里的旧字段 |

### 已知问题:Litematica 投影 + 光影包会让日志刷满 OpenGL 1282

开着光影包、同时有 Litematica 投影在渲染时,OptiFine 会成千上万次地打:

```
[Shaders] OpenGL error: 1282 (Invalid operation), program: gbuffers_terrain, at: pre-useProgram
```

实际查到的:

- 出错的那次调用是 **Litematica 自己的** —— `WorldRendererSchematic.renderBlockLayer` 上传了原版的
  `ShaderProgram.chunkOffset` uniform,而此时绑定的是 OptiFine 自己的 program;
- OptiFine 只是**报告**它:`Shaders.useProgram` 开头就是 `checkGLError("pre-useProgram")`,于是把之前挂着的
  GL 错误算到了它即将绑定的那个 program 头上;
- `litematica-printer`、Xaero's 系列与 OptiLithium 都逐个查过,**均已排除**。

规避办法:**关掉光影包**,或者**停止渲染投影**。OptiFabric 这边改不了 —— 非法调用不是本模组发出的,那句错误检查也是
OptiFine 自己的。

## 🌟 致谢

- **[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)**(作者 Modmuss50、Chocohead)—— 本移植所依据的原项目
- **sp614x** —— OptiFine 本体
- **Fabric** 团队 —— Loader、Loom 与 tiny-remapper

---

**说明:** 这是一个社区移植,与 OptiFine、Fabric 或 Mojang 团队没有隶属关系,也未获其背书或支持。
