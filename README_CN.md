# OptiFabric Reforged

<p align="center">
  <img src="src/main/resources/assets/optifabric/icon.png" alt="OptiFabric Reforged" width="128"/>
</p>

🇨🇳 中文版 | [🇬🇧 English](./README.md)

[![Minecraft](https://img.shields.io/badge/Minecraft-26.2-green.svg)](https://www.minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-%E2%89%A5%200.19.5-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MPL--2.0-lightgrey.svg)](LICENSE.txt)

> ⚠️ 此模组由 deepseek 编写并验证,请小心用于生产环境。

在 **Fabric Loader** 下加载 **OptiFine**。把 OptiFine 的 jar 和本模组一起放进 `mods/`,启动时会用 OptiFine 自带的补丁器给原版客户端打补丁、重建被搬走的 lambda,并把打过补丁的 Minecraft 类交给 Fabric Loader 的类转换器接管,从而让两者共存。**不包含、也不分发 OptiFine 本体。**

本分支是 **26.x 线**,对应 **Minecraft 26.2**(当前)与 **Minecraft 26.1.2**。1.21.x 线(Minecraft 1.21 – 1.21.11)在 [`1.21.x` 分支](../../tree/1.21.x)上独立开发,两条线的 jar **不能互相替代**。

## 📖 概览

OptiFine 不是 Fabric 模组:它的 jar 里是针对原版客户端类的字节码补丁,加上 OptiFine 自己的类。OptiFabric 在 `preLaunch` 阶段驱动 OptiFine 的补丁器、修掉它重编译后留下的结构问题,并在 Mixin 之前把这些补丁类注册进 Loader。

Minecraft **26.1 起未混淆** —— 官方名就是运行期名字,既没有 yarn,也没有真正可用的 intermediary(26.1.2 只发布占位 `0.0.0`)。所以本线不取任何映射、需要 Loom 的非重映射 flavour,运行期命名空间是 `official`。本线还有**自己的 mod id**:有些模组声明 `"breaks": {"optifabric": "*"}`,而 Fabric Loader 按 **id** 匹配 —— 只改显示名没有用。

**作者:** kynarain · 上游:Modmuss50、Chocohead
**版本:** `2.1.0`(`OptiFabric-Reforged-2.1.0+mc26.2.jar`)
**许可:** MPL-2.0

## ✨ 主要特性

- 🔄 **不需要手动跑 OptiFine 安装器** —— 安装器形态(含 `patch/` 差分包)或已解包的形态丢进 `mods/` 即可,一切在启动时完成
- 🆔 **自己的 mod id** —— `optifabric_reforged`,声明 `breaks`/`conflicts` 针对 `optifabric` 的模组不再拦这一线
- 🎨 **由 Indigo 注册真正的渲染器** —— 26.1 把地形与提交节点的整合搬进了 `fabric-renderer-api-v1` 自身,所以这里的 indigo 不是地形渲染器,本线也不声明"让位键"
- 🧩 **模组自己生成的几何真的会被画出来** —— Fabric 的地形钩子注入在一个 OptiFine 区块构建器里根本不存在的循环上,于是那次方块 tessellate 调用改走桥,几何由 Fabric 产出后交给 OptiFine 自己的 `BlockQuadOutput`(顶点格式、层级、光照、光影属性都还是 OptiFine 的)
- 🛠️ **字节码修复成体系** —— OptiFine 重编译会把原版方法掏空、把注入点搬进自己的重载、把子类换掉;fixer 负责补回原版方法体、拆开同名重载(还有人调的改名并把调用者一起改过去,没人调的删掉)、补回 `NEW` 注入点
- 🧪 **离线验证是一等公民** —— 每个补丁类与每个 OptiFine 类都在与游戏一致的单一加载器里加载,用 JVM 验证器 + ASM 数据流验证器双向检查,再加 5 个扫描器
- ⚙️ **走缓存** —— 整条流水线的结果缓存在 `.optifine/<OptiFine 版本>/`,之后的启动 1–2 秒
- 🧯 **失败时说实话** —— 缺 OptiFine / jar 损坏 / 放了两份 / 版本不匹配,都会在标题界面弹错误对话框,并在崩溃报告里追加 `OptiFabric` 一节

## 🏗️ 工作原理

```
mods/<OptiFine jar>
        │  ① 用 OptiFine 自带的 optifine.Patcher 给原版客户端 jar 打补丁
        │  ② LambdaRebuilder:补丁类里的 lambda 指向已被搬走的原方法,需要重建
        ▼
   打补丁后的客户端 jar(OptiFine 的补丁 + OptiFine 的类)
        │  ④ 拆成两部分(本线没有第 ③ 步重映射)
        ├── 非 Minecraft 类(OptiFine 自己的类与资源)─────► 加进游戏 classpath
        └── net/minecraft/** 打过补丁的类 ────────────► ClassCache(替换用)
```

类替换走 **Fabric Loader 自己的 GameTransformer**:Minecraft 类被加载时,Loader 会先问游戏 provider 的 `GameTransformer.transform(类名)` 有没有现成的字节码 —— 这一步发生在 Mixin **之前**。所以 `preLaunch` 阶段注册进去的补丁类(先经过 `patcher/fixes` 的修正)直接顶替,而 Loader 自己补过的类保持 Loader 的版本。

因此不需要为每个补丁类生成 stub mixin,也不依赖 Mixin 的扩展 API;交出去的是 Mixin 的**输入**而非输出,其它模组针对这些类的 mixin 照常生效。

一条硬性约束:**在补丁类交给 Loader 之前,不能对游戏类做任何反射解析** —— 例如一次 `Class.getMethods()` 就会把方法签名里的游戏类型全部加载掉,这些类会被永久钉成原版。这段代码只接触**字节**(`getClassByteArray` / ASM),不持有 `Class` 对象。

| 组件 | 作用 |
|---|---|
| `OptifabricRuntime` | 整条流水线:找 jar → 打补丁 → 修复 → 注册 |
| `GameTransformerHook` | 把补丁类注入 Loader 的游戏 transformer |
| `OptifineMappings` / `OptifineJarFixer` | 名字对齐;修 OptiFine 自己那份 jar |
| `patcher/fixes/**` | 逐个版本的字节码 fixer(原版方法体、同名重载消歧、`NEW` 注入点……) |
| `OptifineFrapiBridge` + `FrapiTesselateBridgeFix` | 把方块 tessellate 改走 Fabric 的渲染器,顶点写进 OptiFine 的 `BlockQuadOutput` |
| `RendererApiFallback` | 只在"让位键"确实被声明时才补惰性占位渲染器 |

中间产物缓存在 `<游戏目录>/.optifine/<OptiFine 版本>/`:

| 文件 | 内容 |
|---|---|
| `cache-format.txt` | 缓存格式版本(当前 `26`),与代码不一致就整份重建 |
| `Optifine-mapped.jar` | 处理后的 OptiFine(不含 MC 类),这就是加进 classpath 的 jar |
| `Optifine.classes.gz` | 打过补丁的 MC 类缓存(ClassCache),供下次启动复用 |

## 📦 安装

1. 准备与本版本**严格一致**的 OptiFine(见下表) —— OptiFabric 会读 `optifine/Config` 里的 `MC_VERSION` 校验,不一致会直接在标题界面报错。**不需要**先运行 OptiFine 安装器。
2. 把本模组的 jar **和** OptiFine 的 jar 一起放进该 Fabric 版本自己的 `mods/` 目录。不要放两份 OptiFine(会报 `DUPLICATED`),也不要混进 1.21.x 线的 jar。
3. 用 **Fabric 版本**启动,不要用启动器自己注入 OptiFine 的那个版本。
4. 首次启动会明显变慢(要跑完整条补丁流水线),之后走缓存。标题界面出现 OptiFine 版本号、视频设置里出现 OptiFine 选项即表示成功。

PCL2 / HMCL 开启版本隔离时,游戏目录与 `mods/` 都在 `versions/<版本名>/` 下,`.optifine/` 缓存也建在那里。

```powershell
# 26.2 的 OptiFine(preview;路径是四段,补丁号带 K2 前缀)
curl.exe -L -o preview_OptiFine_26.2_HD_U_K2_pre1.jar `
  "https://bmclapi2.bangbang93.com/optifine/26.2/HD_U_K2/pre1"

# 26.1.2 的 OptiFine(同样的形状,前缀是 K1)
curl.exe -L -o preview_OptiFine_26.1.2_HD_U_K1_pre2.jar `
  "https://bmclapi2.bangbang93.com/optifine/26.1.2/HD_U_K1/pre2"
```

## 🔨 从源码构建

需要 **JDK 25**,仓库根目录就是 Gradle 项目:

```powershell
.\gradlew build          # -> build/libs/OptiFabric-Reforged-2.1.0+mc26.2.jar
```

版本号必须与 Minecraft 版本成对出现,而且只通过一个脚本改:

```powershell
.\release\version.ps1                     # 看当前版本与三类递增各会变成什么
.\release\version.ps1 -Kind minor         # 给这一线升版
```

开发环境不受支持:`gradlew runClient` 会被明确拒绝,因为开发环境的命名空间是 `named`,需要额外的映射层。

## 📋 运行要求

| | |
|---|---|
| Minecraft | **26.2**(当前目标)—— 以及 **26.1.2**,同一份源码都支持 |
| Fabric Loader | ≥ 0.19.5 |
| Java | **25**(游戏自身的硬性要求;用 Java 21 会在窗口出现之前就失败) |
| 侧 | 客户端 |
| OptiFine | 26.2 用 `preview_OptiFine_26.2_HD_U_K2_pre1.jar`,26.1.2 用 `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar`(两个都还是 preview) |
| Fabric API | 可选 —— 26.2 实测 `0.161.0+26.2`,26.1.2 实测 `0.155.3+26.1.2` |

## 🤝 兼容性

| Minecraft | 产物 | OptiFine 构建 | Java | 状态 |
|---|---|---|---|---|
| 26.2 | `OptiFabric-Reforged-2.1.0+mc26.2.jar` | `preview_OptiFine_26.2_HD_U_K2_pre1.jar` | 25 | ✅ 已实机验证 |
| 26.1.2 | `OptiFabric-Reforged-2.0.0+mc26.1.2.jar` | `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar` | 25 | ✅ 已实机验证 |

这一版是**把线拓宽**,不是把线搬走:同一份源码仍能为 26.1.2 构建,并跑完整条离线管线、数字与 2.0.0 当时记下的完全一致(见下面「验证状态」),所以 26.1.2 没有被丢开 —— 它那份 jar 只是没有变,26.1.2 上继续用 `2.0.0+mc26.1.2`。

OptiFine 只对这两个版本出过构建,别的版本一个都没有:26.1、26.1.1、26.1.3、26.2.1、26.3 的**构建列表是空的** —— 可以自己核一遍(判断依据是返回的**正文**是不是空数组):

```powershell
curl.exe -s "https://bmclapi2.bangbang93.com/optifine/26.1.2"   # 列出 pre1 / pre2
curl.exe -s "https://bmclapi2.bangbang93.com/optifine/26.2"     # 列出 HD_U_K2(pre1)
curl.exe -s "https://bmclapi2.bangbang93.com/optifine/26.3"     # -> []
```

26.2.1 与 26.3 是 26.2 之后的版本,每个都要各自重新移植一遍 —— 而且得先有 OptiFine 的构建才能移,这就是这条线停在 26.2 的原因。

| | |
|---|---|
| ✅ 可用 | OptiFine 的视频设置、缩放、连接纹理、动态光源、**光影**、**抗锯齿**,以及依赖 FRAPI 的模组自己生成的几何(在 26.1.2 上用 LambdaBetterGrass 实测:更好的草与连接纹理正常,光影开启) |
| ⚠️ 有意停用 | 两条 Fabric 渲染钩子:**移动方块提交**与**方块模型提交**(方块破坏裂纹仍然真的走 Fabric 的渲染器);这两条路径改由原版/OptiFine 绘制 |
| ❌ 不兼容 | **Sodium**(已声明 `conflicts`),以及 `no_fog`、`thallium`、`xradiation`、`ryoamiclights`(已声明 `breaks`) |
| 📄 OptiFine 侧限制 | OptiFine 看不到 Fabric 模组内部的资源(`[OptiFine] Unknown resource pack type: …ModNioResourcePack`);光影包与你的 OptiFine 版本不匹配时会打印自己的 `[Shaders]` 报错 |

## 📊 验证状态

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-26.ps1 -Version 26.2
```

| 检查项 | **26.2** | 26.1.2(同一份源码,`-Version 26.1.2`) |
|---|---|---|
| 补丁过的游戏类(JVM 验证器) | **562 / 562**,0 失败 | **567 / 567**,0 失败 |
| OptiFine 自身的类(JVM 验证器) | **879 / 879**,0 失败(2 个 NeoForge-only 类不适用) | **879 / 879**,0 失败(2.0.0 当时记下的数字) |
| ASM 数据流验证器 | **0 问题** | **0 问题** |
| `@At` 注入点 / mixin 成员引用 / 抽象契约 / 丢失的虚方法覆写 / 无法解析的引用 / invokedynamic 句柄 | **全部 0**(比 1.21.x 线还干净;那一线还剩几条属于已停用 indigo 的) | **全部 0** —— 与 2.0.0 记下的逐个相同 |
| 真机 | 流水线跑通(`[OptiFabric] Prepared 562 patched classes (0 skipped, 0 failed)`)、世界打开、`[Shaders] Loaded shaderpack: ComplementaryReimagined_r5.9.1.zip` 且**编译 27 个 program**、无崩溃、无 mixin 变换失败 | 启动、主界面、单人世界、区块重建、方块/物品/生物渲染、**抗锯齿**、**光影**、多人(2.0.0 的记录) |

26.2 那次真机只跑了 `ComplementaryReimagined_r5.9.1.zip` 这一个光影包,也没有重跑多人与抗锯齿 —— 那两项仍是 26.1.2 / 2.0.0 的记录。

那两个"不适用"的类是 `optifine.OptiFineClassProcessor` 与 `optifine.VirtualJarContents`:它们实现的是 **NeoForge** 的 SPI(`net.neoforged.*`),Fabric 启动里永远不会加载。

逐轮排查过程、每一类的根因与修法见 [`docs/PORT_26.x.md`](docs/PORT_26.x.md) 与 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md)。

**已知缺口:**

1. **不支持开发环境** —— dev 命名空间是 `named`,需要两段式重映射
2. **那两条被停用的钩子只是占位**,不是能用的实现
3. **26.2 之后的版本没有移植** —— 26.2.1 与 26.3 都要针对新的官方名逐个重新定位冲突点,而且 OptiFine 对它们至今没有构建

## 📝 项目结构

```
OptiFabric-Reforged/
├── src/main/java/kynarain/cn/optifabric/
│   ├── Optifabric.java              # preLaunch 入口
│   ├── mod/                         # 流水线、transformer 钩子、jar 修复器、FRAPI 桥、渲染器兜底
│   ├── patcher/                     # ClassCache、LambdaRebuilder 与 fixes/(字节码修复)
│   ├── mixin/                       # 本模组自己的两个 mixin
│   └── util/                        # ASM / mixin / remap / zip 工具
├── src/main/resources/              # fabric.mod.json、optifabric.mixins.json、assets/…/icon.png
├── docs/                            # PORT_26.x.md、DEVELOPMENT.md、VERSIONING.md、DESCRIPTION.md、PUBLISHING.md
├── release/                         # version.ps1、publish.ps1、notes/、MANUAL_RELEASE.md
├── build.gradle · gradle.properties · settings.gradle
└── gradlew · gradlew.bat
```

## 🔐 许可

**MPL-2.0** —— 见 [`LICENSE.txt`](LICENSE.txt)。核心逻辑移植自 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric),移植文件保留来源说明。OptiFine 本体**不包含、也不随本项目分发** —— 版权归 sp614x,请自行从 [optifine.net](https://optifine.net/)(或上面的国内镜像)获取。

## 🙋 支持与排查

- **升级本模组后行为没有变化**:先删掉 `<游戏目录>/.optifine/`,缓存里存的是打过补丁的字节码。
- `[OptiFabric]` 的输出走**启动器控制台**,通常不在 `logs/latest.log` 里;过滤 `[OptiFabric]` 能看到准备了多少补丁类、Loader 接管了多少。
- 标题界面会弹错误对话框(缺 OptiFine / jar 损坏 / 版本不匹配 / 多份 OptiFine / 内部错误),崩溃报告里会多出一节 `OptiFabric`(OptiFine 版本、jar 状态、重映射 jar 路径)。
- 显式指定原版 jar:`-Doptifabric.mc-jar=<原版 client jar 路径>`;调试解包:`-Doptifabric.extract=true`。
- **卡在加载界面**:取两次线程转储(`jstack <pid>`,间隔十几秒)对比。两次栈相同、CPU 不涨即为卡死;栈顶停在原生调用(如 `glfwSwapBuffers`)属于呈现层问题 —— 加载期间不要最小化窗口(开着垂直同步时最小化会让 Render 线程一直阻塞)。
- **模型/物品/贴图成片消失**:方向是某个 Fabric mixin 变换类失败(最外层消息常把真实原因吃掉)。OptiFine 常把原版方法改成转发给自己重载的瘦包装,注入点会随之搬走。

下列日志不影响运行:

| 日志 | 说明 |
|---|---|
| `[OptiFine] (Reflector) Class not present: net.minecraftforge.*` / `sun.misc.SharedSecrets` | OptiFine 在探测 Forge 与旧 JDK 的类,Fabric 上本就没有 |
| `[OptiFabric] Resource not found: minecraft:shaders/post/fxaa_of_{2,4}x.json` | OptiFine 仍会到 1.21.6 之前的老位置探测一次抗锯齿链;真正在用的链在 `post_effect/` |
| `[Shaders] Unknown macro value: IRIS_VERSION` / `ANGELICA_VERSION` | 光影包在探测 Iris / Angelica |
| `[Shaders] Invalid macro expression` / `ParseException: Model variable not found: …` | 光影包与本版 OptiFine 不匹配 |
| `Skipping bad option: lastServer` | 选项文件里的旧字段 |

## 🌟 致谢

- **[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)**(作者 Modmuss50、Chocohead)—— 本移植所依据的原项目
- **sp614x** —— OptiFine 本体
- **Fabric** 团队 —— Loader、Loom 与 tiny-remapper

---

**说明:** 这是一个社区移植,与 OptiFine、Fabric 或 Mojang 团队没有隶属关系,也未获其背书或支持。
