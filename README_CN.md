# OptiFabric Reforged

<p align="center">
  <img src="src/main/resources/assets/optifabric/icon.png" alt="OptiFabric Reforged" width="128"/>
</p>

🇨🇳 中文版 | [🇬🇧 English](./README.md)

[![Minecraft](https://img.shields.io/badge/Minecraft-26.2-green.svg)](https://www.minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-%E2%89%A5%200.19.5-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MPL--2.0-lightgrey.svg)](LICENSE.txt)

> ⚠️ 此模组由 DeepSeek 编写并验证,请小心用于生产环境。

在 **Fabric Loader** 下加载 **OptiFine**:把 OptiFine 的 jar 和本模组一起放进 `mods/`,启动时会用 OptiFine 自带的补丁器给原版客户端打补丁、重建被搬走的 lambda、修好它重编译留下的缺口,并把打过补丁的 Minecraft 类交给 Loader 的类转换器接管 —— 从而让两者共存于同一个客户端。**不包含、也不分发 OptiFine 本体。**

本分支是 **26.x 线**,对应 **Minecraft 26.2**(当前)与 **Minecraft 26.1.2**。1.21.x 线(Minecraft 1.21 – 1.21.11)在 [`1.21.x` 分支](../../tree/1.21.x)上独立开发,不同线的 jar **不能互相替代**。

**作者:** kynarain · 上游:Modmuss50、Chocohead · **许可:** MPL-2.0 · **当前版本:** `2.2.7`

## 📦 安装

1. 装好 **Fabric Loader ≥ 0.19.5** 与 **Java 25**(这是游戏自身的硬要求 —— Java 21 连窗口都出不来),然后把本线的 jar 与**版本严格一致**的 OptiFine jar 一起放进该实例的 `mods/` 文件夹。**不要**手动运行 OptiFine 安装器。
2. 启动 **Fabric** 配置 —— 不要用启动器生成的 `26.x-OptiFine_xxx` 配置,那种配置会自己注入 OptiFine,与本模组冲突。
3. 首次启动会多花几秒打补丁(实测 5–7 秒),之后走缓存(1–2 秒)。

这一线叫 **Reforged**,是因为它有**自己的 mod id**(`optifabric_reforged`):有些模组声明 `"breaks": {"optifabric": "*"}`,而 Fabric Loader 按 **id** 匹配,只改显示名没有用。26.x 的两个产物出自同一份源码,不存在需要彼此回避的第二个 26.x 产物。

> **OptiFabric 不能假设启动器会替你装 OptiFine**,因为很多启动器只能选 **OptiFine _or_ Fabric**:先装好 **Fabric + OptiFabric**;到 <https://optifine.net/downloads> 下载 OptiFine;把那个 jar 放进 `mods` 文件夹;然后手动启动一次游戏。
>
> **OptiFabric cannot assume your launcher installs OptiFine for you**: many launchers only offer **OptiFine _or_ Fabric**, never both. Install **Fabric + OptiFabric** first, download OptiFine from <https://optifine.net/downloads>, put that jar in the `mods` folder, then start the game once.

## 🤝 兼容性

| Minecraft | jar | OptiFine 构建 | Java | 状态 |
|---|---|---|---|---|
| 26.2 | `OptiFabric-Reforged-2.2.8+mc26.2.jar` | `preview_OptiFine_26.2_HD_U_K2_pre1.jar` | 25 | ✅ 已实机验证(**不能用光影**,见下) |
| 26.1.2 | `OptiFabric-Reforged-2.2.8+mc26.1.2.jar` | `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar` | 25 | ✅ 已实机验证 |

两个 OptiFine 构建**都是预览版** —— OptiFine 对这两个 Minecraft 版本都还没有正式构建。

### OptiFabric 版本 → Minecraft 版本 → 建议的 OptiFine 构建

这张表与模组自带的清单、以及 `release\notes\mc<MC>.md` 逐版写的是同一批;发布工具会在几处不一致时报错。

| OptiFabric version | Minecraft version | Recommended OptiFine build | Build kind |
|---|---|---|---|
| `2.2.8+mc26.2` | 26.2 | `preview_OptiFine_26.2_HD_U_K2_pre1.jar` | preview (no final exists) |
| `2.2.8+mc26.1.2` | 26.1.2 | `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar` | preview (no final exists) |

### 26.2 的光影限制

在 **26.2** 上,在 OptiFine 里选择光影包**没有任何效果**:游戏照常不带光影渲染。它**不是崩溃**,也不是本模组造成的 —— 是那个 OptiFine 构建在这个 Minecraft 版本上不加载光影包。自 **2.2.7** 起,模组一旦看到装的是那个构建,就会在同一个标题界面对话框里直接说明。**26.1.2 不受影响。**

| | |
|---|---|
| ✅ 可用 | OptiFine 的视频设置、缩放、连接纹理、动态光源,以及 Fabric 模组提交的方块模型几何(经桥接交给 OptiFine 自己的 `BlockQuadOutput`) |
| ⚠️ 已中和 | OptiFine 的 `BEFORE_BLOCK_OUTLINE` 事件与移动方块的 FRAPI 钩子为空转,与 1.21.x 线一致 |
| ❌ 不兼容 | **Sodium**,以及 `no_fog`、`thallium`、`xradiation`、`ryoamiclights`(见下一节) |
| 🚫 不适用 | 1.21.x 线那条 indigo 说明:26.1 把地形与提交节点的整合搬进了 `fabric-renderer-api-v1` 自身,所以这里的 indigo **不是**地形渲染器,本线也**不声明**"让位键" |

### 声明的不兼容,以及加载器实际会怎么做

本模组声明的不兼容**只写在 `fabric.mod.json` 里**,没有任何界面会显示它 —— 所以这里把它写成文字:

| 声明 | 条目 |
|---|---|
| `conflicts` | `sodium`(`*`) |
| `breaks` | `no_fog`、`thallium`、`xradiation`、`ryoamiclights`、`sodium`(`*`) |

**`breaks` 是闸门,`conflicts` 只是一句警告。** 在 Fabric Loader 0.19.5 上,一条 `conflicts` 根本不会给求解器添加约束(`ModSolver` 的 `CONFLICTS` 分支至今还是一句 `// TODO: soft negative dep?`),所以游戏以 `Warnings were found!` 照常启动;而一条点到**已存在**模组的 `breaks` 会让求解器给出 `NEG_HARD_DEP`,加载器**拒绝整个实例**(`Incompatible mods found!`)。因为这里 `sodium` 同时出现在两个字段里,实际生效的是那道闸门 —— 这是有意的。

**为什么 Sodium 是硬拒载。** 两个地形渲染器无法共用同一条管线。在 1.21.x 线的 1.21.1 目标上配合 Sodium 0.8.13 实测:把所有可修的缺口都补上之后,Sodium 的 mixin 全部应用成功、客户端也进得了世界,但**整帧全黑**;把那个唯一的渲染槽让给 Sodium、以及关掉 OptiFine 的 Fast Render,两次单变量实验都没有改变结果。Sodium 自己那一侧也声明了这组不兼容,但用的是**旧** mod id,所以那条声明在这里根本不会命中。

`no_fog`、`thallium`、`xradiation` 三条继承自上游([Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric));`ryoamiclights` 是本移植自己加的,原因是 OptiFine 会整个替换视频设置界面(连父类一起),而那个模组的 mixin 锚在原版父类上(没有损失:OptiFine 自带动态光源)。

## 🏗️ 工作原理

```
mods/<OptiFine jar>
        │  ① 用 OptiFine 自带的 optifine.Patcher 给原版客户端 jar 打补丁
        │  ② LambdaRebuilder:补丁类里的 lambda 指向已被搬走的原方法,需要重建
        ▼
   打补丁后的客户端 jar(OptiFine 的补丁 + OptiFine 的类)
        │  ③ 拆成两部分(本线没有重映射这一步 —— 见下)
        ├── OptiFine 自己的类与资源 ─► 加进游戏 classpath
        └── net/minecraft/** 打过补丁的类 ───► ClassCache(替换用)
```

**本线没有重映射这一步,而这正是它的要点。** Minecraft **26.1 起未混淆** —— 官方名就是运行期名字,既没有 yarn,也没有真正可用的 intermediary。所以本线不取任何映射、需要 Loom 的非重映射 flavour,运行期命名空间是 `official`。

类替换走 **Loader 自己的 GameTransformer**:Minecraft 类被加载时,Loader 会先问游戏 provider 的 `GameTransformer.transform(类名)` 有没有现成的字节码 —— 这一步发生在 Mixin **之前**。交出去的是 Mixin 的**输入**而非输出,所以别的模组针对这些类的 mixin 照常生效,也不必为每个补丁类生成 stub mixin。一条硬性约束:**在补丁类交给 Loader 之前,不能对游戏类做任何反射解析** —— 一次 `Class.getMethods()` 就会把类永久钉在原版上 —— 所以这段代码只接触字节。

26.1+ 上,来自 Fabric 模组的几何是个特例:Fabric 的地形钩子注入在一个 OptiFine 区块构建器里**根本不存在**的循环上,于是那次方块 tessellate 调用改走**桥**,把 Fabric 产出的四边形交给 OptiFine 自己的 `BlockQuadOutput` —— 顶点格式、层级、光照、光影属性都仍然是 OptiFine 的。

| 组件 | 作用 |
|---|---|
| `OptifabricRuntime` | 整条流水线:找 jar → 打补丁 → 修复 → 注册 |
| `GameTransformerHook` | 把补丁类注入 Loader 的游戏 transformer |
| `OptifineFrapiBridge` | 把 Fabric 的四边形交给 OptiFine 的 `BlockQuadOutput` |
| `OptifineJarFixer` | 修 OptiFine 自己那份 jar |
| `patcher/fixes/**` | 字节码 fixer(原版方法体、同名重载消歧、`NEW` 注入点……) |
| `RendererApiFallback` | 在 Fabric 渲染器 API 本该为空的地方注册惰性占位器 |

中间产物缓存在 `<游戏目录>/.optifine/<OptiFine 版本>/`:`cache-format.txt`(缓存格式版本,不一致就整份重建)、`Optifine-mapped.jar`、`Optifine.classes.gz`(供下次启动复用的补丁类)。

## ✨ 主要特性

- 🔄 **不需要手动跑安装器** —— 安装器形态(含 `patch/` 差分包)或已解包的形态丢进 `mods/` 即可,一切在启动时完成
- 🆔 **自己的 mod id** —— `optifabric_reforged`,声明 `breaks`/`conflicts` 针对 `optifabric` 的模组不再拦这一线
- 🧩 **模组自己生成的几何真的会被画出来** —— 见上面的桥接
- 🛠️ **字节码修复成体系** —— OptiFine 重编译会把原版方法掏空、把注入点搬进自己的重载、把子类换掉;fixer 负责补回原版方法体、拆开同名重载、删掉没人调的那份、补回 `NEW` 注入点
- 🧪 **离线验证是一等公民** —— 每个补丁类与每个 OptiFine 类都在与游戏一致的单一加载器里加载,用 JVM 验证器 + ASM 数据流验证器双向检查
- ⚙️ **走缓存** —— 整条流水线的结果缓存在 `.optifine/<OptiFine 版本>/`,之后的启动 1–2 秒
- 🧯 **失败时说实话** —— 缺 OptiFine / jar 损坏 / 放了两份 / 版本不匹配,都会在标题界面弹错误对话框,并在崩溃报告里追加 `OptiFabric` 一节

## 📊 验证状态

每次发布用与 1.21.x 线相同的办法核对:每个补丁类与每个 OptiFine 类都在单一加载器里加载,过 JVM 验证器 + ASM 数据流验证器,再用扫描器找 `@At` / refmap / 契约 / 句柄问题。本线的两个目标都做过**实机启动**;26.2 那次的结果就是上面写的光影限制。

**已知缺口:**

1. **不支持开发环境** —— `gradlew runClient` 被拒绝,原因与 1.21.x 线相同
2. **26.2 的光影** —— 要等一个能在该 Minecraft 版本上加载光影包的 OptiFine 构建
3. **两处被中和的钩子是占位实现**,不是可用实现

## 🔨 从源码构建

需要 **JDK 25**(游戏自身的硬要求),而且**仓库根目录就是 Gradle 工程** —— 目标版本取自 `minecraft_version`,所以切换版本用的是属性而不是 `-Pmc`:

```bash
./gradlew build                                      # 默认目标 26.2
./gradlew build "-Pminecraft_version=26.1.2"          # 本线的另一个目标
```

产物落在 `build/libs/OptiFabric-Reforged-<版本>+mc<MC>.jar`。

## 📋 运行要求

| | |
|---|---|
| Minecraft | **26.2**(当前目标)—— 以及同一份源码出的 **26.1.2** |
| Fabric Loader | ≥ 0.19.5 |
| Java | **25**(游戏自身的硬要求;Java 21 连窗口都出不来) |
| 侧 | 客户端 |
| OptiFine | 26.2 用 `preview_OptiFine_26.2_HD_U_K2_pre1.jar`,26.1.2 用 `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar`(两个都只有预览版) |
| Fabric API | 可选 —— 26.2 上用 `0.161.0+26.2` 测过,26.1.2 上用 `0.155.3+26.1.2` 测过 |

## 📝 项目结构

```
OptiFabric-Reforged/
├── src/main/java/kynarain/cn/optifabric/
│   ├── Optifabric.java              # preLaunch 入口
│   ├── mod/                         # 流水线、变换器钩子、jar 修复器、FRAPI 桥
│   ├── patcher/                     # ClassCache、LambdaRebuilder 与 fixes/(字节码修复)
│   ├── mixin/                       # 本项目自带的 mixin
│   └── util/                        # ASM / mixin / zip 辅助
├── src/main/resources/              # fabric.mod.json、optifabric.mixins.json、assets/…/icon.png
├── docs/                            # DEVELOPMENT.md、PUBLISHING.md、REFORGED_BUILD.md,……
└── release/                         # notes/、MANUAL_RELEASE.md、make-metadata.ps1
```

## 🙋 支持与排查

- **升级后没有任何变化?** 删掉 `<游戏目录>/.optifine/` —— 缓存里是打过补丁的字节码,会自动重建。
- `[OptiFabric]` 的输出走**启动器控制台**,不在 `logs/latest.log` 里;过滤 `[OptiFabric]` 能看到准备了多少个类、Loader 接管了多少个。
- OptiFine 缺失 / 损坏 / 重复 / 版本不匹配时,标题界面会弹对话框,崩溃报告里会多出一节 `OptiFabric`(OptiFine 版本、jar 状态、mapped jar 路径)。
- **`NoClassDefFoundError: Could not initialize class net.optifine.reflect.Reflector`**(或启动阶段直接死掉、连崩溃报告都没有):另一个模组的 mixin 没能注入到 OptiFine 改写过的类里。给 JVM 参数加上 `-Dmixin.debug=true` 再跑一次 —— Mixin 只在调试模式下会打出出问题的模组名。**处理办法**是删掉那个模组,或者不用 OptiFine;这边修不了。
- **卡在加载界面:** 隔约 15 秒抓两份线程转储(`jstack <pid>`)对比;栈完全相同且 CPU 平坦是真卡住。

属于正常、可以忽略的日志行:

| 日志 | 含义 |
|---|---|
| `[OptiFine] (Reflector) Class not present: net.minecraftforge.*` / `sun.misc.SharedSecrets` | OptiFine 在探测 Forge 与老 JDK;Fabric 两样都没有 |
| `Failed to locate initialiser injection point in <init>(…)` | 应用 OptiFine 那个 `BlockEntity` 补丁的代价 |
| `[Shaders] Unknown macro value: IRIS_VERSION` / `ANGELICA_VERSION` | 光影包在探测 Iris/Angelica |
| `Skipping bad option: lastServer` | 选项文件里留下的旧字段 |

### 已知问题:Litematica 投影 + 光影包会让日志刷满 OpenGL 1282

开着光影包并渲染 Litematica 投影时,OptiFine 会刷出成千上万条 `[Shaders] OpenGL error: 1282 (Invalid operation), program: gbuffers_terrain, at: pre-useProgram`。那个出错的调用**是 Litematica 自己的**(`WorldRendererSchematic.renderBlockLayer` 上传原版的 `ShaderProgram.chunkOffset` uniform,而在 OptiFine 自己的程序绑定时这个操作非法);OptiFine 只是**报告**它,因为 `Shaders.useProgram` 开头就有一句 `checkGLError("pre-useProgram")`。绕开办法:关掉光影包,或停止渲染投影。

## 🔐 许可

**MPL-2.0** —— 见 [`LICENSE.txt`](LICENSE.txt)。核心机制移植自 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric);移植来的文件保留原始头部声明。OptiFine 本身**不**被包含或再分发 —— 那是 sp614x 的作品,请到 [optifine.net](https://optifine.net/) 获取。

## 🌟 致谢

- **[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)**(Modmuss50 与 Chocohead)—— 本移植所基于的原始项目
- **sp614x** —— OptiFine 本身
- **Fabric** 团队 —— Loader、Loom 与 tiny-remapper

---

**说明:** 这是一个社区移植,与 OptiFine、Fabric、Mojang 团队均无关联,也未获其背书或支持。
