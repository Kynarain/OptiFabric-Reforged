# OptiFabric

<p align="center">
  <img src="src/main/resources/assets/optifabric/icon.png" alt="OptiFabric" width="128"/>
</p>

🇨🇳 中文版 | [🇬🇧 English](./README.md)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21%20~%201.21.11-green.svg)](https://www.minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-%E2%89%A5%200.19.5-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21%2B-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MPL--2.0-lightgrey.svg)](LICENSE.txt)

> ⚠️ 此模组由 DeepSeek 编写并验证,请小心用于生产环境。

在 **Fabric Loader** 下加载 **OptiFine**:把 OptiFine 的 jar 和本模组一起放进 `mods/`,启动时会用 OptiFine 自带的补丁器给原版客户端打补丁、重建被搬走的 lambda、把 OptiFine 重映射进 intermediary、修好它重编译留下的缺口,并把打过补丁的 Minecraft 类交给 Loader 的类转换器接管 —— 从而让两者共存于同一个客户端。**不包含、也不分发 OptiFine 本体。**

本分支是 **1.21.x 线**,覆盖 Minecraft **1.21 – 1.21.11**(OptiFine 出过构建的全部十个版本)。26.x 线(Minecraft 26.2 / 26.1.2)与 1.20.6 线各自在自己的分支上;不同线的 jar **不能互相替代**。

**作者:** kynarain · 上游:Modmuss50、Chocohead · **许可:** MPL-2.0 · **当前版本:** `2.2.13`

## 📦 安装

1. 装好 **Fabric Loader ≥ 0.19.5** 与 **Java 21+**,然后把本线的 jar 与**版本严格一致**的 OptiFine jar 一起放进该实例的 `mods/` 文件夹。**不要**手动运行 OptiFine 安装器。
2. 启动 **Fabric** 配置 —— 不要用启动器生成的 `1.21.x-OptiFine_xxx` 配置,那种配置会自己注入 OptiFine,与本模组冲突。
3. 首次启动会多花几秒打补丁与重映射(实测 5–7 秒),之后走缓存(1–2 秒)。

**从 1.x 升级?** 先删掉旧的 `OptiFabric-<版本>+mc1.21.x.jar`:2.0.0 把 mod id 改成了 `optifabric_reforged`(显示名 *OptiFabric Reforged*),两个 id 同时在 `mods/` 里时 Fabric 会加载两份,OptiFine 会被打两遍补丁。

> **OptiFabric 不能假设启动器会替你装 OptiFine**,因为很多启动器只能选 **OptiFine _or_ Fabric**:先装好 **Fabric + OptiFabric**;到 <https://optifine.net/downloads> 下载 OptiFine;把那个 jar 放进 `mods` 文件夹;然后手动启动一次游戏。
>
> **OptiFabric cannot assume your launcher installs OptiFine for you**: many launchers only offer **OptiFine _or_ Fabric**, never both. Install **Fabric + OptiFabric** first, download OptiFine from <https://optifine.net/downloads>, put that jar in the `mods` folder, then start the game once.

开启版本隔离(PCL2 / HMCL)时,游戏目录、`mods/` 与 `.optifine/` 缓存都位于 `versions/<名称>/` 下。

## 🤝 兼容性

| Minecraft | jar | OptiFine 构建 | 状态 |
|---|---|---|---|
| 1.21 | `OptiFabric-2.2.13+mc1.21.jar` | `preview_OptiFine_1.21_HD_U_J1_pre9.jar` | ✅ 已验证 |
| 1.21.1 | `OptiFabric-2.2.13+mc1.21.1.jar` | `OptiFine_1.21.1_HD_U_J1.jar` | ✅ 已验证 |
| 1.21.3 | `OptiFabric-2.2.13+mc1.21.3.jar` | `OptiFine_1.21.3_HD_U_J2.jar` | ✅ 已验证 |
| 1.21.4 | `OptiFabric-2.2.13+mc1.21.4.jar` | `OptiFine_1.21.4_HD_U_J3.jar` | ✅ 已验证 |
| 1.21.6 | `OptiFabric-2.2.13+mc1.21.6.jar` | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` | ⚠️ 能玩,**不能用光影**(见下) |
| 1.21.7 | `OptiFabric-2.2.13+mc1.21.7.jar` | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` | ⚠️ 同上 |
| 1.21.8 | `OptiFabric-2.2.13+mc1.21.8.jar` | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` | ✅ 已验证 |
| 1.21.9 | `OptiFabric-2.2.13+mc1.21.9.jar` | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` | ✅ 已验证 |
| 1.21.10 | `OptiFabric-2.2.13+mc1.21.10.jar` | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` | ✅ 已验证 |
| 1.21.11 | `OptiFabric-2.2.13+mc1.21.11.jar` | `OptiFine_1.21.11_HD_U_J9.jar` | ✅ 已验证 |

OptiFine **从未**为 **1.21.2 / 1.21.5** 出过构建,所以这两版没有 jar。表里那个构建是该 Minecraft 版本最新的**正式版**;某版还没有正式版时,用最新的**预览版**(所以 1.21.4 列的是 J3 而不是更新的 J4_pre2)。同一 Minecraft 版本的**其它**构建也照常可用:游戏内提示只在你 `mods/` 里放的是**比表里更旧的预览版**时出现 —— 你手上已经是正式版时不会被提醒,哪怕之后出了更新的正式版。

### OptiFabric 版本 → Minecraft 版本 → 需要的 OptiFine 构建

这张表与模组自带的清单、以及 `release\notes\mc<MC>.md` 逐版写的是同一批;三者不一致时 `release\version.ps1 -CheckSupport` 会报错。

| OptiFabric version | Minecraft version | Required OptiFine build |
|---|---|---|
| `2.2.13+mc1.21` | 1.21 | `preview_OptiFine_1.21_HD_U_J1_pre9.jar` |
| `2.2.13+mc1.21.1` | 1.21.1 | `OptiFine_1.21.1_HD_U_J1.jar` |
| `2.2.13+mc1.21.3` | 1.21.3 | `OptiFine_1.21.3_HD_U_J2.jar` |
| `2.2.13+mc1.21.4` | 1.21.4 | `OptiFine_1.21.4_HD_U_J3.jar` |
| `2.2.13+mc1.21.6` | 1.21.6 | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` |
| `2.2.13+mc1.21.7` | 1.21.7 | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` |
| `2.2.13+mc1.21.8` | 1.21.8 | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` |
| `2.2.13+mc1.21.9` | 1.21.9 | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` |
| `2.2.13+mc1.21.10` | 1.21.10 | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` |
| `2.2.13+mc1.21.11` | 1.21.11 | `OptiFine_1.21.11_HD_U_J9.jar` |

那个 jar 缺失、或不是其 Minecraft 版本该有的构建时,游戏会在标题界面弹出对话框而不是默默启动:对话框写出文件名、提供「下载 OptiFine」按钮(从 OptiFine **官网** `optifine.net` 取,那是本模组唯一会用的来源),并替你打开 mods 文件夹。

### 1.21.6 / 1.21.7 的光影限制

这两版**不带光影时能正常启动与游玩**,但**一旦启用光影包,就会在启动阶段崩在 OptiFine 自己的代码里**:

```
java.lang.NullPointerException: Cannot read field "norm" because "multiTex" is null
  at net.optifine.shaders.ShadersTex.initDynamicTextureNS(ShadersTex.java:322)
```

不是光影包的问题(三个互不相关的包在同一帧崩),也不是我们打补丁的问题(崩在第一批贴图创建时)。这两版可用的**全部七个** OptiFine 构建表现一致,所以降级到更早的预览版也躲不开。自 **2.2.12** 起,模组一旦看到装的是那个构建,就会在同一个标题界面对话框里直接告诉你:请不带光影游玩,或换一个最新构建是正式版的 Minecraft 版本。

| | |
|---|---|
| ✅ 可用 | OptiFine 的视频设置、缩放、连接纹理、动态光源、**光影**(除 1.21.6 / 1.21.7 外的各版本),以及自 1.1.2 起的**抗锯齿** |
| ⚠️ 已中和 | `BEFORE_BLOCK_OUTLINE` 事件不再触发(方块轮廓照常绘制);移动方块的 FRAPI 渲染钩子为空转(移动方块走原版路径绘制) |
| ❌ 不兼容 | **Sodium**,以及 `no_fog`、`thallium`、`xradiation`、`ryoamiclights` —— 五条都在 `breaks` 里,**加载器会强制执行**(见下一节) |
| 📄 OptiFine 侧限制 | OptiFine 看不到 Fabric 模组内部的资源(`[OptiFine] Unknown resource pack type: …ModNioResourcePack`);光影包与你的 OptiFine 构建不匹配时会打印自己的 `[Shaders]` 报错 |

### 声明的不兼容,以及加载器实际会怎么做

本模组声明的不兼容**只写在 `fabric.mod.json` 里**,没有任何界面会显示它 —— 所以这里把它写成文字。当前产物声明**五条** `breaks`,没有 `conflicts`:

| 声明 | 条目 |
|---|---|
| `breaks` | `no_fog`、`thallium`、`xradiation`、`ryoamiclights`、`sodium` |
| `conflicts` | *(无)* |

**`breaks` 是闸门,`conflicts` 只是一句警告。** 在 Fabric Loader 0.19.5 上实测:一条 `conflicts` 根本不会给求解器添加约束(`ModSolver` 的 `CONFLICTS` 分支至今还是一句 `// TODO: soft negative dep?`),所以游戏以 `Warnings were found!` 照常启动;而一条点到**已存在**模组的 `breaks` 会让求解器给出 `NEG_HARD_DEP`,加载器**拒绝整个实例**(`Incompatible mods found!`)。也就是说 `breaks` 不是提醒,是拦截 —— 这里是**有意为之**(见下 Sodium)。

**这份清单从哪来。** `no_fog`、`thallium`、`xradiation` 三条**继承自上游**([Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric) 的 `llama` 分支);`ryoamiclights` 是本移植自己加的,原因是 OptiFine 会整个替换视频设置界面(连父类一起),而那个模组的 mixin 锚在原版父类上(没有损失:OptiFine 自带动态光源)。上游还带着三条 1.16/1.17 时代的版本范围条目,本移植**没有**带过来:`cardinal-components-item <2.4.2`、`architectury >1.2.72 <1.3.77`、`meteor-client >=0.4.1`。

**Sodium 自 2.2.11 起是硬拒载,而且这个结论是量出来的、不是继承来的。** 它的历史搬过两次,值得知道:2.2.8/2.2.9 两个字段都写(于是拒载整个实例),2.2.10 只留在 `conflicts`(改成警告),2.2.11 又写回 `breaks`。理由随证据变了 —— 在 1.21.1 + Sodium 0.8.13 上,那些缺失调用点背后的级联被走完(五处修复,见 [`CHANGELOG.md`](CHANGELOG.md)),而**五处全部到位之后**,Sodium 自己的 mixin 全部应用成功(`Mixin transformation of` 与 `InjectionError` 均为 0)、客户端到得了标题界面、也进得了世界 —— 但**整帧全黑**。随后两次单变量实验排除了仅剩的解释(把那个唯一的渲染槽让给 Sodium、关掉 OptiFine 的 Fast Render),两者都仍然全黑。两个地形渲染器无法共用同一条管线,所以这里选择**拒载**而不是警告:警告只会让用户拿到一个"能启动、永远不画画"的游戏。

**Sodium 自己那一侧也声明了,但用的是旧 id。** 实测的每个 Sodium 构建都写着 `"breaks": {"optifabric": "*"}`,点的是**旧** mod id;而本线自 2.0.0 起以 `optifabric_reforged` 发布,所以那条声明根本不会命中。你看到的 Sodium 冲突提示,来自本模组自己这一侧。

**Architectury 是反向的故事。** 上游曾把 architectury 判为不兼容,而 architectury 的元数据里写着 `breaks: optifabric <1.13.0`。在 1.21.1 上,本移植修好了它背后那个真实冲突:OptiFine 把自己的局部变量插进 `GameRenderer.render` 中间,顶高了 Mixin 的 `LocalCapture` 交给处理器的原版槽位,于是 `LocalSlotLayoutFix` 把多出来的槽位挪到局部变量区末尾。`architectury-api` `13.0.11` 现在能通过扫描,而改名成 `optifabric_reforged` 是让 architectury 自己那条声明不再触发的原因。测量记录见 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) 与 [`docs/RELEASE_NOTES.md`](docs/RELEASE_NOTES.md)。

**实测图景**(哪些模组真的会在 OptiFabric 旁边失败、其中哪些是我们这边的缺口)在 [`docs/COMPATIBILITY_CN.md`](docs/COMPATIBILITY_CN.md);逐模组的完整清单在仓库根目录的 [`COMPATIBILITY.md`](COMPATIBILITY.md)。

### 与 indigo 的关系

Fabric API 的 `fabric-renderer-indigo` 与 OptiFine 不能同时渲染地形,所以本模组用 Fabric 自己的机制让 indigo 让位:`fabric.mod.json` 里的 `"custom": {"fabric-renderer-api-v1:contains_renderer": true}`(Sodium 用的是同一个键 —— OptiFine **就是**一个地形渲染器)。于是 indigo 会打印 `[Indigo] Different rendering plugin detected; not applying Indigo.`。但只声明这个键还不够:Fabric 的渲染器模块读的是**注册表**,空注册表会抛异常,所以本模组另外注册了一个惰性占位渲染器(`F3` 显示 `Renderer: OptifineRendererPlaceholder`)。

## 🏗️ 工作原理

```
mods/OptiFine_1.21.11_HD_U_J9.jar
        │  ① 用 OptiFine 自带的 optifine.Patcher 给原版(混淆)客户端 jar 打补丁
        ▼
   打补丁后的 vanilla jar(OptiFine 的补丁 + OptiFine 的类)
        │  ② LambdaRebuilder:补丁类里的 lambda 指向已被搬走的原方法,需要重建
        │  ③ tiny-remapper:official(混淆)→ intermediary
        │     必须把游戏 jar 放进重映射器的 classpath,否则子类里的覆写继承不到映射
        ▼
   Optifine-mapped.jar
        │  ④ 拆成两部分
        ├── OptiFine 自己的类与资源 ─► 加进游戏 classpath
        └── net/minecraft/** 打过补丁的类 ───► ClassCache(替换用)
```

类替换走 **Fabric Loader 自己的 GameTransformer**:Minecraft 类被加载时,Loader 会先问游戏 provider 的 `GameTransformer.transform(类名)` 有没有现成的字节码 —— 这一步发生在 Mixin **之前**。所以 `preLaunch` 阶段注册进去的补丁类(先经过 `patcher/fixes` 的修正)直接顶替,而 Loader 自己补过的类保持 Loader 的版本。因此不需要为每个补丁类生成 stub mixin,也不依赖 Mixin 的扩展 API:交出去的是 Mixin 的**输入**而非输出,其它模组针对这些类的 mixin 照常生效。一条硬性约束:**在补丁类交给 Loader 之前,不能对游戏类做任何反射解析** —— 例如一次 `Class.getMethods()` 就会把方法签名里的游戏类型全部加载掉,这些类会被永久钉成原版。这段代码只接触**字节**。

| 组件 | 作用 |
|---|---|
| `OptifabricRuntime` | 整条流水线:找 jar → 打补丁 → 重映射 → 修复 → 注册 |
| `GameTransformerHook` | 把补丁类注入 Loader 的游戏 transformer |
| `OptifineMappings` | 规则推导的 contextual mapping(取代上游的手写表) |
| `OptifineJarFixer` | 修 OptiFine 自己那份 jar(后处理 json 的格式、FXAA 顶点着色器等) |
| `patcher/fixes/**` | 逐个版本的字节码 fixer(原版方法体、注入点、合成字段……) |
| `RendererApiFallback` | 在 Fabric 渲染器 API 本该为空的地方注册惰性占位器 |

中间产物缓存在 `<游戏目录>/.optifine/<OptiFine 版本>/`:`cache-format.txt`(缓存格式版本,与代码不一致就整份重建)、`Optifine-mapped.jar`(重映射后的 OptiFine,不含 MC 类)、`Optifine.classes.gz`(打过补丁的 MC 类缓存,供下次启动复用)。

## ✨ 主要特性

- 🔄 **不需要手动跑安装器** —— 安装器形态(含 `patch/` 差分包)或已解包的形态丢进 `mods/` 即可,补丁在启动时完成
- 🧩 **看得见游戏的重映射** —— 游戏 jar 同时进重映射器的 classpath 与输入,子类里覆写的方法才不会留成 OptiFine 的名字(有单个类曾漏掉 35 个方法)
- 📦 **一份源码、每版一个 jar** —— 每个 jar 绑死自己那一版的映射表,用 `-Pmc=<版本>` 构建
- 🎨 **抗锯齿真的能用** —— 自 1.1.2 起不再动 OptiFine 自带的 `post_effect/` 链,只在"后处理管线没有顶点属性"的两版(1.21.9 / 1.21.10)重写 FXAA 顶点着色器
- 🛠️ **字节码修复成体系** —— 一批 fixer 处理 OptiFine 重编译抹掉的东西:原版方法体、注入点、合成字段、对象创建点、被改名的 lambda、区域构造
- 🧪 **离线验证是一等公民** —— 每个补丁类与每个 OptiFine 类都在与游戏一致的单一加载器里加载,用 JVM 验证器 + ASM 数据流验证器双向检查
- ⚙️ **走缓存** —— 整条流水线的结果缓存在 `.optifine/<OptiFine 版本>/`,之后的启动 1–2 秒
- 🧯 **失败时说实话** —— 缺 OptiFine / jar 损坏 / 放了两份 / 版本不匹配,都会在标题界面弹错误对话框,并在崩溃报告里追加 `OptiFabric` 一节

## 📊 跑分实测

用第三方基准 **FPS Benchmark**(`fpstest-1.0.jar`,sha256 `F11681914771E01A4677DA5EF217195FF523B01E9C3F463BF9A298D7BCCB2C56`)测得:每组跑一次 3 分钟脚本化 *Base*(19 段运镜;固定种子 `27182`)。**逐帧原始报告**见 **[`benchmarks/2026-10-05-fps-benchmark/`](benchmarks/2026-10-05-fps-benchmark/)**。

**运行环境 —— 四组完全相同:** Minecraft 1.21.1 · Fabric Loader 0.19.5 · Java 22.0.2 · max heap 5836 MB · Intel i5-12600KF · AMD RX 7800 XT(驱动 25.12.1.251128)· Windows 10 amd64 · 视距 8 · VSync 关 · `maxFps:260`(等于"无限")· 光影关 · `options.txt` 四份逐字节相同(sha256 `AC506701…`)· 种子 27182

| 组 | 角色 | 模组(sha256 前 8) |
|---|---|---|
| **A** | 纯净基线 | `fabric-api-0.116.17+1.21.1.jar` `79AC44B4` + `fpstest-1.0.jar` `F1168191` |
| **C** | 仅 OptiFabric + OptiFine | A + `OptiFabric-2.2.10+mc1.21.1.jar` `A897DA34` + `OptiFine_1.21.1_HD_U_J1.jar` `DB6D2D14` |
| **D** | 推荐组合 | C + `lithium-fabric-0.15.4+mc1.21.1.jar` `92329D98` + `ferritecore-7.0.3-fabric.jar` `98C3AB1D` + `c2me-fabric-mc1.21.1-0.4.0-alpha.0.29.jar` `9C4C1C4C` |
| **B** | Sodium 路线(与 OptiFine 不兼容) | A + `lithium-fabric-0.15.4+mc1.21.1.jar` `92329D98` + `sodium-fabric-0.8.13+mc1.21.1.jar` `3D43C149` |

| 组 | 平均 FPS | 1% low | 0.1% low | p99 帧 | 最大帧 | 最低帧 | 标准差 | 原始结果 |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| **A** | 3631 | 774 | 442 | 0.76 ms | 17.21 ms | 58.1 | 706 | [report.md](benchmarks/2026-10-05-fps-benchmark/A-vanilla-baseline/report.md) |
| **C** | **5040** | **1098** | **713** | 0.58 ms | **3.31 ms** | 302 | 964 | [report.md](benchmarks/2026-10-05-fps-benchmark/C-OptiFabric-OptiFine/report.md) |
| **D** | 4475 | 1077 | 721 | 0.63 ms | 3.14 ms | **319** | **747** | [report.md](benchmarks/2026-10-05-fps-benchmark/D-OptiFabric-OptiFine-LiFeC2ME/report.md) |
| **B** | **5680** | 1017 | 349 | **0.50 ms** | 12.47 ms | 80.2 | 1177 | [report.md](benchmarks/2026-10-05-fps-benchmark/B-Sodium-Lithium/report.md) |

- **C 平均比纯净基线高 38.8%**,最大帧时间还好 5 倍(3.31 ms vs 17.21 ms)—— 兼容层在这里是净收益而不是代价。
- **B(Sodium)平均最快(比 C 高约 13%),但尾部最差**:0.1% low 349 vs 713、最大帧 12.47 ms vs 3.31 ms。

### 完整套件 —— 41 项测试、四套配置

同一台机器、同一套设置;下表是分类平均,格式为 **平均 FPS / 1% low**(原始报告见 [`benchmarks/`](benchmarks/2026-10-05-fps-benchmark/)):

| 类别(测试数) | A 纯原版 | B Sodium + Lithium | C OptiFabric + OptiFine | D = C + Lithium + FerriteCore + C2ME |
|---|---|---|---|---|
| 空载 / 静态(2) | 4579 / 1278 | **5961** / 1712 | 5388 / 1529 | 5670 / 1686 |
| 飞行,12 个生态(12) | 2606 / 288 | **5254** / 649 | 4220 / 461 | 4382 / **777** |
| 实体(8) | 1496 / 537 | **1861** / 681 | 1595 / 587 | 1626 / 638 |
| 物理(6) | 3066 / 602 | **4040** / 924 | 3915 / 785 | 3979 / 901 |
| 红石 / 方块实体(5) | 3935 / 1078 | **5537** / 1523 | 4898 / 1369 | 5004 / 1404 |
| 粒子(2) | 1972 / 523 | 2634 / 510 | 2331 / 669 | **2670** / **770** |

- **纯原版基线在六个类别里全部垫底**,而 **D 几乎每一项都不低于 C** 且尾部明显更好(飞行 1% low 777 对 461、粒子 770 对 669)—— 也就是说 Lithium / FerriteCore / C2ME 在真实场景里**确实有收益**,上面那张静止的 *Base* 表看不出来。
- **粒子多样性是唯一谁都治不好的场景**:四组都落在 437 到 495 之间。这个瓶颈不在我们这边。

*单机、每组一轮、单一基准,分辨率未强制。这些数字是参考值,不是规格书 —— 你自己的结果会不同。*

## 📊 验证状态

每个版本用一条命令核对:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-version.ps1 -Version 1.21.10 -ModVersion 1.1.2
```

它把每个补丁类与每个 OptiFine 类都在单一加载器里加载,过 JVM 验证器 + ASM 数据流验证器,并在任何 `@At` / refmap / 契约 / 句柄问题上失败。逐版本数字记录在 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) 与 [`docs/RELEASE_NOTES.md`](docs/RELEASE_NOTES.md);如今剩下的 `@At` 未命中全部是**故意停用**的 indigo 注入。

**已知缺口:**

1. **不支持开发环境** —— dev 运行在 `named` 命名空间,需要两段重映射加上游那套上下文映射修复(`gradlew runClient` 直接被拒绝)
2. **两处被中和的钩子是占位实现**,不是可用实现(`Renderer.get()` 返回一个惰性渲染器)
3. **1.21.6 / 1.21.7 的光影** —— 要么等新的 OptiFine 构建,要么把已经写好的 `GpuTextureLinkFix` 接上

## 🔨 从源码构建

需要 **JDK 21+**,而且**仓库根目录就是 Gradle 工程** —— 目标版本由 `-Pmc` 指定(省略时取 `gradle.properties`):

```bash
./gradlew build "-Pmc=1.21.11"                            # PowerShell 里引号必不可少,否则 1.21.11 会被拆开
./gradlew build "-Pmc=1.21.8" "-Pmod_version_base=2.1.0"  # 只有那个 jar 的版本号与基数不同时才需要
```

产物落在 `build/libs/`。版本号只通过一个脚本改动:

```powershell
.\release\version.ps1                                      # 查看本线当前版本
.\release\version.ps1 -Mc 1.21.8 -Kind patch                # 只升某一个 jar,其余不动
```

## 📋 运行要求

| | |
|---|---|
| Minecraft | 1.21、1.21.1、1.21.3、1.21.4、1.21.6、1.21.7、1.21.8、1.21.9、1.21.10、1.21.11 |
| Fabric Loader | ≥ 0.19.5 |
| Java | 21+(在 25 上测过) |
| 侧 | 客户端 |
| OptiFine | 你自备,**版本必须严格一致**(见上面的表) |
| Fabric API | 可选 —— 与该 Minecraft 版本对应的发行版测过 |

## 📝 项目结构

```
OptiFabric/
├── src/main/java/kynarain/cn/optifabric/
│   ├── Optifabric.java              # preLaunch 入口
│   ├── mod/                         # 流水线、变换器钩子、jar 修复器、渲染器占位
│   ├── patcher/                     # ClassCache、LambdaRebuilder 与 fixes/(字节码修复)
│   ├── mixin/                       # 本项目自带的那两个 mixin
│   └── util/                        # ASM / mixin / 重映射 / zip 辅助
├── src/main/resources/              # fabric.mod.json、optifabric.mixins.json、assets/…/icon.png
├── COMPATIBILITY.md                 # 逐模组完整兼容清单(MC 1.21.1)
├── docs/                            # DEVELOPMENT.md、COMPATIBILITY_CN.md、compatibility/,……
├── release/                         # version.ps1、publish.ps1、notes/、MANUAL_RELEASE.md
└── build.gradle · gradle.properties · settings.gradle · gradlew(.bat)
```

## 🙋 支持与排查

**[`docs/FAQ_CN.md`](docs/FAQ_CN.md) 先回答了 18 个问题** —— OptiFine 对话框、为什么拒载 Sodium、Sodium 生态、Photon 与闪烁、`Invalid program name` 与 `prepare` 的日志噪音、1.21.6/1.21.7 的光影崩溃、信雅互联与 Kilt、是否打包或下载 OptiFine,以及报告问题时该附什么。

- **升级后没有任何变化?** 删掉 `<游戏目录>/.optifine/` —— 缓存里是打过补丁的字节码,会自动重建(缓存格式号也会让它失效)。
- `[OptiFabric]` 的输出走**启动器控制台**,不在 `logs/latest.log` 里;过滤 `[OptiFabric]` 能看到准备了多少个类、Loader 接管了多少个。
- OptiFine 缺失 / 损坏 / 重复 / 版本不匹配时,标题界面会弹对话框,崩溃报告里会多出一节 `OptiFabric`(OptiFine 版本、jar 状态、mapped jar 路径)。
- 用 `-Doptifabric.mc-jar=<路径>` 指定原版 jar;用 `-Doptifabric.extract=true` 把重映射后的 OptiFine 类解出来。
- **模型 / 物品 / 贴图成片消失**(日志刷满 `Unable to bake … model`):某个 Fabric mixin 没能变换那个类 —— 最外层的报错通常掩盖了真正的原因。
- **`NoClassDefFoundError: Could not initialize class net.optifine.reflect.Reflector`**(或启动阶段直接死掉、连崩溃报告都没有):另一个模组的 mixin 没能注入到 OptiFine 改写过的类里,而 OptiFine 的崩溃报告器正好撞上它。给 JVM 参数加上 `-Dmixin.debug=true` 再跑一次 —— Mixin 只在调试模式下会打出出问题的模组名(`Mixin apply for mod <模组> failed … -> net.minecraft.class_<n>`)。**处理办法**是删掉那个模组,或者不用 OptiFine;这是 OptiFine 的类补丁与那个模组的注入之间的冲突,OptiFabric 这边修不了。1.21.1 上的已知例子:`CarryOn` 2.2.6.13 与 `ShoulderSurfing` 5.2.0 无法从这里修好,而 `SophisticatedCore` 的 `ParticleEngineMixin` 在本构建里已修好;`EntityCulling` 曾被怀疑,后来证明无关。
- **卡在加载界面:** 隔约 15 秒抓两份线程转储(`jstack <pid>`)对比。栈完全相同且 CPU 平坦是真卡住;栈停在 `glfwSwapBuffers` 里则是呈现问题 —— 而且**加载时不要把窗口最小化**(开着垂直同步时渲染线程会阻塞在那里)。

属于正常、可以忽略的日志行:

| 日志 | 含义 |
|---|---|
| `[OptiFine] (Reflector) Class not present: net.minecraftforge.*` / `sun.misc.SharedSecrets` | OptiFine 在探测 Forge 与老 JDK;Fabric 两样都没有 |
| `Failed to locate initialiser injection point in <init>(class_2591,…)` | 应用 OptiFine 那个 `BlockEntity` 补丁的代价(跳过它会留下五处悬空引用) |
| `[OptiFabric] Resource not found: minecraft:shaders/post/fxaa_of_{2,4}x.json` | OptiFine 仍会探一次它 1.21.6 之前那条链的位置;实际在用的链在 `post_effect/` |
| `[Shaders] Unknown macro value: IRIS_VERSION` / `ANGELICA_VERSION` | 光影包在探测 Iris/Angelica |
| `[Shaders] Invalid macro expression` / `ParseException: Model variable not found: …` | 光影包与 OptiFine 版本不匹配 |
| `Skipping bad option: lastServer` | 选项文件里留下的旧字段 |

### 已知问题:Litematica 投影 + 光影包会让日志刷满 OpenGL 1282

开着光影包并渲染 Litematica 投影时,OptiFine 会刷出成千上万条 `[Shaders] OpenGL error: 1282 (Invalid operation), program: gbuffers_terrain, at: pre-useProgram`。那个出错的调用**是 Litematica 自己的**(`WorldRendererSchematic.renderBlockLayer` 上传原版的 `ShaderProgram.chunkOffset` uniform,而在 OptiFine 自己的程序绑定时这个操作非法);OptiFine 只是**报告**它,因为 `Shaders.useProgram` 开头就有一句 `checkGLError("pre-useProgram")`。绕开办法:关掉光影包,或停止渲染投影。OptiFabric 这边无法修。

## 🔐 许可

**MPL-2.0** —— 见 [`LICENSE.txt`](LICENSE.txt)。核心机制移植自 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric);移植来的文件保留原始头部声明。OptiFine 本身**不**被包含或再分发 —— 那是 sp614x 的作品,请到 [optifine.net](https://optifine.net/) 获取。

## 🌟 致谢

- **[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)**(Modmuss50 与 Chocohead)—— 本移植所基于的原始项目
- **sp614x** —— OptiFine 本身
- **Fabric** 团队 —— Loader、Loom 与 tiny-remapper

---

**说明:** 这是一个社区移植,与 OptiFine、Fabric、Mojang 团队均无关联,也未获其背书或支持。

## 出处、许可与 AI 参与

这个项目是 Modmuss50 与 Chocohead 的 [OptiFabric](https://github.com/Chocohead/OptiFabric) 的移植,并沿用其许可:
**MPL-2.0**(见 `LICENSE.txt`)。MPL-2.0 要求修改过的文件保持源码可得,这正是本仓库公开、且带着完整历史的原因 ——
这次移植改动过的每一处都在里面。

移植工作 —— 读 OptiFine 打补丁后的字节码、写各个 fixer、以及发布流程 —— 是**在 AI 协助下编写与验证**的,
每一份 README 与发布说明的开头都写着这一点(另见 FAQ 第 17 问)。这在实践中的含义与"谁写的"无关,而是一条
关于**证据**的规矩:这些文档里的每一个数字都来自本仓库里抓取到的日志或原始报告,凡是没测的,就写成
**未测**,而不是估一个数。`CONTRIBUTING.md` 里的复查清单也是同一条规矩。

任何构建都**不打包、也不重新分发** OptiFine:模组只读你放进 `mods/` 的那个 OptiFine jar;`-full` 版本才会按你的
请求去 optifine.net 下载它。本仓库涉及到的第三方项目(Fabric API、Sodium、Lithium、C2ME……)同样从不随 jar 分发 ——
与它们的兼容情况写在 `COMPATIBILITY.md` 里,而不是塞进产物里。
