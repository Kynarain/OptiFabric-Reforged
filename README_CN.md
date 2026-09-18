# OptiFabric

<p align="center">
  <img src="common/src/main/resources/assets/optifabric/icon.png" alt="OptiFabric" width="128"/>
</p>

🇨🇳 中文版 | [🇬🇧 English](./README.md)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21%20~%201.21.11%20%7C%2026.1.2-green.svg)](https://www.minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-%E2%89%A5%200.19.5-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21%20%2F%2025-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MPL--2.0-lightgrey.svg)](LICENSE.txt)

> ⚠️ 此模组由 deepseek 编写并验证,请小心用于生产环境。

让 **OptiFine** 与 **Fabric** 在同一个客户端里共存。把本模组和你自己那份 OptiFine 一起丢进 `mods/`:optifabric 在启动时给客户端打补丁、把 OptiFine 重映射进 Fabric 的命名空间、修掉它与 Fabric API 之间的结构冲突,再把结果交给 Fabric Loader 的类变换器。**不包含、也不分发 OptiFine 本体。**

## 📖 概览

OptiFine 不是 Fabric 模组 —— 它的 jar 里是**针对混淆过的 Minecraft 类**的字节码补丁加上它自己的类。OptiFabric 在 `preLaunch` 阶段驱动 OptiFine 自带的 patcher、去混淆、把结果重映射到运行期命名空间、应用一批字节码修复,最后把这些补丁类注册进 Loader。于是 OptiFine 的设置、光影、连接纹理、缩放都能在 Fabric 客户端上工作,而其它模组的 mixin 照常生效 —— 交出去的是 Mixin 的**输入**而不是输出。

**作者:** kynarain(1.21.11 移植)· 上游:Modmuss50、Chocohead
**版本:** 1.21.x 线 `1.1.2` · 26.x 线 `2.0.0`(OptiFabric Reforged)
**许可:** MPL-2.0

**两条独立发布线,jar 不能互相替代:**

| 线 | Minecraft | mod id / 产物名 | Java |
|---|---|---|---|
| **1.21.x** | 1.21 ~ 1.21.11 | `optifabric` / `OptiFabric-<版本>+mc<MC版本>.jar` | 21+ |
| **26.x** | 26.1.2 | `optifabric_reforged` / `OptiFabric-Reforged-2.0.0+mc26.1.2.jar` | **25** |

26.1 起游戏**未混淆** —— 没有 yarn,也没有真正可用的 intermediary 来重映射 —— 所以那一线走另一套构建与运行期路径。它还有自己的 mod id:有模组声明 `"breaks": {"optifabric": "*"}`,而 Fabric Loader 是按 **id** 匹配的(详见 [`docs/PORT_26.x.md`](docs/PORT_26.x.md))。

## ✨ 主要特性

- 🔄 **不需要手动跑 OptiFine 安装器** —— 安装器形态或已解包的 OptiFine 丢进 `mods/` 即可,补丁在启动时完成
- 🧩 **看得见游戏的重映射** —— 游戏 jar 同时进重映射器的 classpath 与输入,子类里覆写的方法才不会留成 OptiFine 的名字(有单个类曾漏掉 35 个方法)
- 🛠️ **字节码修复成体系** —— 一批 fixer 处理 OptiFine 重编译抹掉的东西:原版方法体、注入点、合成字段、对象创建点、被改名的 lambda、区域构造
- 🎨 **抗锯齿真的能用** —— 不再动 OptiFine 自带的 `post_effect/` 链,并给"后处理管线没有顶点属性"的两版(1.21.9 / 1.21.10)重写 FXAA 顶点着色器
- 📦 **一份源码、每个 Minecraft 版本一个 jar** —— 1.21 到 1.21.11,每个 jar 绑死它那一版的 `official → intermediary` 映射表
- 🧪 **离线验证是一等公民** —— 每个补丁类与每个 OptiFine 类都在与游戏一致的单一加载器里加载,用 JVM 验证器 + ASM 数据流验证器双向检查,外加 5 个扫描器
- ⚙️ **走缓存** —— 整条流水线的结果缓存在 `.optifine/<OptiFine 版本>/`,之后的启动 1–2 秒
- 🧯 **失败时说实话** —— 缺 OptiFine / jar 损坏 / 放了两份 / 版本不匹配,都会在标题界面弹错误对话框,并在崩溃报告里追加 `OptiFabric` 一节

## 🏗️ 工作原理

```
mods/OptiFine_1.21.11_HD_U_J9.jar
        │  ① 用 OptiFine 自带的 optifine.Patcher 给(混淆的)客户端 jar 打补丁
        │     (1.21.6 起补丁以 xdelta 差分包形式携带,用法不变)
        ▼
   打补丁后的 vanilla jar(OptiFine 的补丁 + OptiFine 的类)
        │  ② LambdaRebuilder:补丁类里的 lambda 指向已被搬走的方法
        │  ③ tiny-remapper:official(混淆)→ intermediary
        │     **必须把游戏 jar 放进重映射器的 classpath**,否则子类里的覆写留不住映射名
        ▼
   Optifine-mapped.jar
        │  ④ 拆成两部分
        ├── 非 Minecraft 类(OptiFine 自己的类与资源)─────► 加进 classpath
        └── net/minecraft/** 打过补丁的类 ────────────► ClassCache
```

> **26.x 线没有第 ③ 步**:MC 26.1 起未混淆,运行期命名空间是 `official`,jar 里也不打包映射表。

替换走 **Fabric Loader 自己的 GameTransformer**:类被加载时,`KnotClassDelegate.getPreMixinClassByteArray` 会先问游戏 provider 的 `GameTransformer.transform(...)` 有没有现成字节码 —— 这一步**在 Mixin 之前**,所以 `preLaunch` 注册进去的补丁类直接顶替。

| 组件 | 作用 |
|---|---|
| `OptifabricRuntime` | 整条流水线:找 jar → 打补丁 → 重映射 → 修复 → 注册 |
| `GameTransformerHook` | 把补丁类注入 Loader 的游戏 transformer |
| `OptifineMappings` | 规则推导的 contextual mapping(取代上游的手写表) |
| `OptifineJarFixer` | 修 OptiFine 自己那份 jar:后处理 json 的格式、1.21.6/1.21.7 被写死的 shaderpack 加载、1.21.9/1.21.10 的 FXAA 顶点着色器 |
| `patcher/fixes/**` | 逐版本的字节码 fixer(原版方法体、注入点、合成字段……) |
| `RendererApiFallback` | 在 Fabric 渲染器 API 本该为空的地方注册惰性占位器 |

## 📦 安装

1. 装一个对应版本的 **Fabric** 客户端(Fabric Loader **≥ 0.19.5**;Java 21+,26.1.2 要 **Java 25**)
2. 下载与你 MC 版本**严格一致**的 OptiFine(见下方表格),**不要**运行它的安装器
3. 把 OptiFabric 的 jar **和** OptiFine 的 jar 一起放进该版本的 `mods/` 目录
4. 用 Fabric 版本启动 —— 首次启动多花几秒做补丁+重映射(启动器控制台里能看到 `[OptiFabric]` 前缀的输出),之后走缓存

**就这样。** 标题界面出现 OptiFine 版本号、视频设置里出现 OptiFine 选项,就说明成功了。

```powershell
# 例如 1.21.11 的 OptiFine(国内直链,实测 302 → 官方 maven 分发)
curl.exe -L -o OptiFine_1.21.11_HD_U_J9.jar "https://bmclapi2.bangbang93.com/optifine/1.21.11/HD_U/J9"
```

**不要**放两份 OptiFine(会报 `DUPLICATED`),不要混用版本,也不要用启动器自己装的 `1.21.x-OptiFine_xxx` 版本启动(那是启动器在启动时注入 OptiFine,会和本模组重复)。PCL2/HMCL 若开了**版本隔离**,游戏目录是 `versions/<版本名>/`,`.optifine/` 缓存也在那里。

## 🔨 从源码构建

前置:**Git** 与 JDK(1.21.x 线用 21,26.x 线用 25)。Gradle 走 wrapper;根目录不是 Gradle 项目,所以构建要带 `-p`。

```bash
git clone https://github.com/Kynarain/OptiFabric-Reforged.git
cd OptiFabric-Reforged

# 1.21.x 线 —— 每个 Minecraft 版本一个 jar
./gradlew -p v1.21.x build "-Pmc=1.21.8"                              # 1.21 / 1.21.1 是 1.1.0
./gradlew -p v1.21.x build "-Pmc=1.21.8" "-Pmod_version_base=1.1.2"   # 1.21.3 – 1.21.11 是 1.1.2
./gradlew -p v1.21.x build "-Pmc=1.21.11"                             # 不带 -Pmc 用 gradle.properties 里的默认版本

# 26.x 线 —— 目标版本就是 v26.x/gradle.properties 里那一个(没有 -Pmc)
./gradlew -p v26.x build
```

产物在 `v1.21.x/build/libs/` 与 `v26.x/build/libs/`。`gradlew runClient` 会被明确拒绝:dev 的命名空间是 `named`,需要额外一段重映射。

版本号只通过一个脚本改,规则见 [`docs/VERSIONING.md`](docs/VERSIONING.md):

```powershell
.\release\version.ps1                                       # 看两条线当前版本
.\release\version.ps1 -Line 1.21.x -Mc 1.21.8 -Kind patch    # 只给某一个产物升版
```

## 📋 运行要求

| | 1.21.x 线 | 26.x 线 |
|---|---|---|
| Minecraft | 1.21、1.21.1、1.21.3、1.21.4、1.21.6 – 1.21.11 | 26.1.2 |
| Fabric Loader | ≥ 0.19.5 | ≥ 0.19.5 |
| Java | 21+(实测 25) | **25**(游戏本身的要求) |
| 侧 | 客户端 | 客户端 |
| OptiFine | 自备,版本必须严格一致 | `preview_OptiFine_26.1.2_HD_U_K1_pre2` |
| Fabric API | 可选(实测与你版本对应的那版) | 可选(实测 0.155.3+26.1.2) |

## 🤝 兼容性

**支持的版本**(一版一个 jar —— 文件名里的 MC 版本是唯一的区分点):

| Minecraft | jar | OptiFine 构建 | 状态 |
|---|---|---|---|
| 1.21 | `OptiFabric-1.1.0+mc1.21.jar` | `preview_OptiFine_1.21_HD_U_J1_pre9.jar` | ✅ 已实测 |
| 1.21.1 | `OptiFabric-1.1.0+mc1.21.1.jar` | `OptiFine_1.21.1_HD_U_J1.jar` | ✅ 已实测 |
| 1.21.3 | `OptiFabric-1.1.2+mc1.21.3.jar` | `OptiFine_1.21.3_HD_U_J2.jar` | ✅ 已实测 |
| 1.21.4 | `OptiFabric-1.1.2+mc1.21.4.jar` | `OptiFine_1.21.4_HD_U_J3.jar` | ✅ 已实测 |
| 1.21.6 | `OptiFabric-1.1.2+mc1.21.6.jar` | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` | ⚠️ 不推荐 —— 该 OptiFine 构建自身有缺陷 |
| 1.21.7 | `OptiFabric-1.1.2+mc1.21.7.jar` | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` | ⚠️ 不推荐 —— 同上 |
| 1.21.8 | `OptiFabric-1.1.2+mc1.21.8.jar` | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` | ✅ 已实测 |
| 1.21.9 | `OptiFabric-1.1.2+mc1.21.9.jar` | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` | ✅ 已实测 |
| 1.21.10 | `OptiFabric-1.1.2+mc1.21.10.jar` | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` | ✅ 已实测 |
| 1.21.11 | `OptiFabric-1.1.2+mc1.21.11.jar` | `OptiFine_1.21.11_HD_U_J9.jar` | ✅ 已实测 |
| 26.1.2 | `OptiFabric-Reforged-2.0.0+mc26.1.2.jar` | `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar` | ✅ 已实测 |

OptiFine 没出过 **1.21.2 / 1.21.5** 的构建,所以这两版没有对应 jar;26.1、26.1.1、26.1.3、26.2+ 同样没有构建 —— 这也是 26.x 线停在 26.1.2 的原因。

| | |
|---|---|
| ✅ 可用 | OptiFine 的视频设置、缩放、连接纹理、动态光源、**光影**,以及(1.1.2 起)1.21.3 – 1.21.11 的**抗锯齿** |
| ⚠️ 有意中和 | `BEFORE_BLOCK_OUTLINE` 事件不再触发(描边照画);移动方块的 FRAPI 钩子失效(移动方块由原版渲染);1.21.x 上 Fabric 渲染器 API 由惰性占位实现顶着 |
| ❌ 不兼容 | **Sodium**(已声明 `conflicts`),以及 `no_fog`、`thallium`、`xradiation`、`ryoamiclights`(已声明 `breaks`) |
| 📄 OptiFine 侧限制 | OptiFine 看不到 Fabric 模组内部的资源(`Unknown resource pack type: …ModNioResourcePack`);光影包会打印 `Unknown macro value: IRIS_VERSION` 之类 |

依赖 FRAPI/indigo 的模组在两条线上表现不同:**1.21.x** 上 Indigo 让位(OptiFine 本身就是地形渲染器,F3 显示 `OptifineRendererPlaceholder`);**26.1.2** 上 Indigo 会注册自己的渲染器,模组**实时生成**的几何(更好的草之类)真的会被画出来。

一条不属于本模组的注意事项:写给 Iris、或写给与你不同的 OptiFine 版本的光影包会打印自己的报错(`Invalid program name`、`Unknown macro value: IRIS_VERSION`、`ParseException: Model variable not found: …`)。`photon_v1.2a` 在 1.21.6+ 上就是这种;上面验证用的是 `ComplementaryReimagined_r5.9.1`。

## 📊 验证状态

每个版本一条命令,下面这些数字来自 `1.1.2`(逐版本各跑一次):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-version.ps1 -Version 1.21.10 -ModVersion 1.1.2
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-26.ps1     # 26.x 线
```

| Minecraft | 补丁类(JVM) | OptiFine 类(JVM) | ASM 验证器 | @At / refmap / 契约 / 句柄 | 真机 |
|---|---|---|---|---|---|
| 1.21.3 | 440 / 440 | 816 / 816 | 0 问题 | 2 / 0 / 0 / 0 | 抗锯齿 + 光影,无报错 |
| 1.21.4 | 474 / 474 | 812 / 812 | 0 问题 | 2 / 0 / 0 / 0 | 无报错 |
| 1.21.6 | 487 / 487 | 820 / 820 | 0 问题 | 4 / 0 / 0 / 0 | 该版 OptiFine 构建在本机启动即卡 |
| 1.21.7 | 500 / 500 | 823 / 823 | 0 问题 | 4 / 0 / 0 / 0 | 无报错 |
| 1.21.8 | 516 / 516 | 831 / 831 | 0 问题 | 4 / 0 / 0 / 0 | 无报错,抗锯齿目测正常 |
| 1.21.9 | 519 / 519 | 832 / 832 | 0 问题 | 4 / 0 / 0 / 0 | 无报错 |
| 1.21.10 | 553 / 553 | 836 / 836 | 0 问题 | 4 / 0 / 0 / 0 | 无报错,画面确认不是黑屏 |
| 1.21.11 | 570 / 570 | 874 / 874 | 0 问题 | 4 / 0 / 0 / 0 | 无报错,抗锯齿目测正常 |
| 26.1.2 | 567 / 567 | 879 / 879 | 0 问题 | 0 / 0 / 0 / 0 | 单机与多人、光影、Indigo 几何 |

剩下那几条 `@At` 全部属于**被有意停用**的 indigo;补丁类数量是在与游戏一致的单一加载器里点出来的。逐轮的完整记录 —— 每一次崩溃、它的字节码根因与修法 —— 在 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md),26.x 的移植过程在 [`docs/PORT_26.x.md`](docs/PORT_26.x.md)。

**已知缺口:**

1. **不支持开发环境** —— dev 命名空间是 `named`,需要两段式重映射(official → intermediary → named)
2. **被中和的钩子是占位实现**,不是能用的实现(1.21.x 上 `Renderer.get()` 返回惰性渲染器)
3. **上游的 `compat/**` 每 mod 兼容 mixin 没有搬过来** —— 它们依赖 Manningham Mills 的 early riser 机制
4. **OptiFine 各项功能的效果没有逐项测量**(连接纹理、缩放、动态光源、FPS 提升幅度);启动、进世界、方块/区块/物品/生物渲染、光影、多人已确认
5. **26.x 只覆盖 26.1.2** —— 26.1 的其他小版本或 26.2 都要各自重走一遍,而且 OptiFine 在 26.1.2 之后根本没有构建

## 📝 项目结构

```
OptiFabric/
├── common/                 # 两条线共用的源码
│   └── src/main/
│       ├── java/kynarain/cn/optifabric/
│       │   ├── mod/        #   流水线、transformer 钩子、jar 修复器、渲染器兜底
│       │   ├── patcher/    #   fixer 注册表与逐个版本的字节码修复
│       │   ├── mixin/      #   本模组自己的两个 mixin
│       │   └── util/
│       └── resources/      # fabric.mod.json(按线展开)、图标、mixin 配置
├── v1.21.x/                # 1.21 线:legacy fabric-loom,yarn + intermediary,Java 21
├── v26.x/                  # 26.x 线:net.fabricmc.fabric-loom,无 mappings,Java 25
├── docs/                   # DEVELOPMENT.md、PORT_26.x.md、VERSIONING.md、DESCRIPTION.md、PUBLISHING.md
├── release/                # version.ps1、publish.ps1、逐版本发布说明与清单
├── dist/                   # 构建好的发布产物(不在 git 里)
└── gradlew(.bat)           # 共用 wrapper —— 根目录不是 Gradle 项目
```

## 🔐 许可

**MPL-2.0** —— 见 [`LICENSE.txt`](LICENSE.txt)。核心机制是 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric) 的移植,移植文件保留来源说明头。OptiFine 本体**不包含、也不随本项目分发** —— 请自行从 [optifine.net](https://optifine.net/)(或上面的国内镜像)获取。

## 🙋 支持与排查

1. 先看 [`docs/DESCRIPTION.md`](docs/DESCRIPTION.md) —— 发布用文案里已经回答了常见问题(缓存在哪、`-Doptifabric.mc-jar`、`-Doptifabric.extract=true`)
2. 看**启动器控制台**而不是 `logs/latest.log`:`[OptiFabric]` 自己的输出只走 stdout
3. 删掉 `<游戏目录>/.optifine/` 可强制重建(缓存格式号是 `26`)
4. 提 issue 时请附 `logs/latest.log`(若崩溃再加 `crash-reports/` 里那份,末尾的 `OptiFabric` 一节写着 OptiFine 版本、jar 状态与重映射 jar 路径)、`mods/` 的文件列表,以及你的 OptiFine 构建名

日志里有几条正常噪音,不用管:`[OptiFabric] Resource not found: minecraft:shaders/post/fxaa_of_*.json` 是 OptiFine 每次还去它 1.21.6 之前的老位置探一次链(1.21.8 起的构建早就不带那个文件,真正在用的链在 `post_effect/`);`Failed to locate initialiser injection point in <init>(…)` 是应用 OptiFine 的 `BlockEntity` 补丁的代价;`[OptiFine] (Reflector) Class not present: net.minecraftforge.* / sun.misc.SharedSecrets` 是 OptiFine 在探测 Forge 与旧 JDK;`Unknown macro value: IRIS_VERSION`、`Invalid macro expression`、`Shaders: Block not found for name: minecraft:planks`、`ParseException: Model variable not found: …` 来自光影包本身;`Skipping bad option: lastServer` 是 `options.txt` 里的旧字段。

## 🌟 致谢

- **[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)**(作者 Modmuss50、Chocohead)—— 本移植所依据的原项目
- **sp614x** —— OptiFine 本体
- **Fabric** 团队 —— Loader、Loom 与 tiny-remapper

---

**说明:** 这是一个社区移植,与 OptiFine、Fabric 或 Mojang 团队没有隶属关系,也未获其背书或支持。
