# 已知问题与常见问题(1.20.6)

> 面向使用者的排查入口。安装与功能说明见 [`../README.md`](../README.md);逐轮排查过程与离线校验工具见
> [`DEVELOPMENT.md`](DEVELOPMENT.md);参与开发见 [`../CONTRIBUTING.md`](../CONTRIBUTING.md)。

本文件只写**已经查到、能复现**的结论;没查清的会明说"未查清",不放猜测。

## 一、Litematica 投影 + 光影包会让日志刷满 OpenGL 1282

**症状**:开着光影包、同时有 Litematica 投影在渲染时,日志里成千上万条:

```
[Shaders] OpenGL error: 1282 (Invalid operation), program: gbuffers_terrain, at: pre-useProgram
```

**查到的**:

- 出错的那次调用是 **Litematica 自己的** —— `WorldRendererSchematic.renderBlockLayer` 上传了原版的
  `ShaderProgram.chunkOffset` uniform,而此时绑定的是 OptiFine 自己的 program,于是产生
  `GL_INVALID_OPERATION (1282)`;
- OptiFine 只是**报告**它:`Shaders.useProgram` 开头就是 `checkGLError("pre-useProgram")`,把之前挂着的 GL 错误
  算到了它即将绑定的那个 program 头上;
- **已排除**:`litematica-printer`、Xaero's 系列(Xaero's Minimap / World Map)、OptiLithium 都逐个查过,
  与这条日志无关。

**规避**:

1. 关掉光影包(OptiFine 视频设置 → 光影 → 关闭);或
2. 停止渲染投影(Litematica 里关掉该投影的渲染)。

**本模组这边**:改不了 —— 非法调用不是 OptiFabric 发出的,那句错误检查也是 OptiFine 自己的。这条只作为
"日志噪音/定位方向"记录在这里,免得反复当新问题查。

## 二、OptiFine 版本提示什么时候会弹

这一条常被问"为什么今天又提示了",规则按 **构建类型** 分三种:

| mods/ 里的情况 | 行为 |
|---|---|
| **完全没有** OptiFine | **每次启动都提示**,直到装上为止;点「继续返回主菜单」只作用于**这一次**会话,不写任何记录 |
| 装了 OptiFine,但是**比表里更旧的预览版**(文件名/版本号带 `_pre`,例如 `HD_U_I9_pre1`、`HD_U_J1_pre17`) | 提示**一次**,点「仍要继续」后把该构建记进 `config/optifabric-mismatch-ack.txt`,同一个构建不再重复提示 |
| 装了**正式版**(或比表里更新的构建) | **完全不提示**,哪怕之后有更新的正式版 |

1.20.6 的建议构建是 `preview_OptiFine_1.20.6_HD_U_J1_pre18.jar`(OptiFine 只发布过 `HD_U_I9_pre1`(2024-06-06)、
`HD_U_J1_pre17`(2024-09-25)、`HD_U_J1_pre18`(2024-09-27)三个构建,全是预览版,没有正式版)。

## 三、和别的模组的冲突(都在 `fabric.mod.json` 里声明了)

| 模组 | 情况 |
|---|---|
| **Sodium** | 声明为 `conflicts` 与 `breaks`:两者都是地形渲染器 |
| `no_fog`、`thallium`、`xradiation`、`ryoamiclights`、`sodium` | 声明为 `breaks`(Sodium 两处都在,见上一行) |
| **RyoamicLights** | OptiFine 把原版视频设置界面(`class_446`)**整类替换成自己的实现,连父类都换掉**,而它的 mixin 注入在原版父类上,于是变换失败(`Delegate constructor lookup failed`)。删掉不会损失功能 —— OptiFine 自带动态光源(视频设置 → 品质 → 动态光源)。更一般地,凡是往 OptiFine 整类替换的界面类里注入的模组都可能同样失败 |
| 依赖 FRAPI/indigo 的模组 | indigo 已按 Fabric 的机制让位(`fabric-renderer-api-v1:contains_renderer`),这些模组不再获得 indigo 的自定义渲染,地形由 OptiFine 渲染 |

**这些声明在哪、加载器会不会拦**

- 它们**只写在 `fabric.mod.json` 里**,游戏里没有任何界面会显示;`Warnings were found!` 是唯一能看到它们的时机。
- **加载器在两个字段上都不会拦住这个组合。** Sodium 现在同时写在 `conflicts` 与 `breaks` 里。实测(Fabric Loader 0.19.5):日志开头是 `Warnings were found!`、点名这条冲突,然后照常进入 `Loading <N> mods:` —— **没有** `Incompatible mods found`,也没有 `HARD_DEP` 之类的拒载。所以这是"已声明的已知不兼容",不是护栏。那次实测是在 1.21.x 线上做的,而 `conflicts` / `breaks` 由加载器自身处理,与是哪条线无关。
- **来源**:`sodium` 冲突与 `no_fog`、`thallium`、`xradiation` 三条继承自上游 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)(默认分支 `llama`,v1.14.3);上游另有 `cardinal-components-item <2.4.2`、`architectury >1.2.72 <1.3.77`、`meteor-client >=0.4.1` 三条带版本范围的条目,都是 1.16/1.17 时代的,本移植**没有带过来**。`ryoamiclights` 是本移植自己加的;把 `sodium` 也列进 `breaks` 同样是本移植自己加的(上游只在 `conflicts` 里声明它)。
- **id 上的差别**:本线 mod id 仍是 **`optifabric`**,而 Sodium 自己那份元数据里针对的正是 `optifabric`(Sodium 只在 `breaks` 里声明它,没有 `conflicts`)—— 所以在 id 这一层,Sodium 那条声明**不会失配**,它会照常命中,与我们新写进 `breaks` 的这一条并存,这条冲突不是只有我们单方面在声明(1.21.x / 26.x 两线从 2.0.0 起改名成 `optifabric_reforged`,那两条线上会失配)。Sodium 1.20.6 构建里那条声明覆盖的确切版本范围,本仓库**没有实测记录**。
- **Architectury**:上游声明它坏,architectury 自己的元数据也写着 `breaks: optifabric <1.13.0`。1.21.x 线在字节码层面修掉了背后的局部槽位冲突(`LocalSlotLayoutFix`),并靠改名绕开了那条声明。**本线没有那个 fixer,也没有做过实测**,按**未测**处理。

> 与本仓库另外两条线不同,**1.20.6 还没有** `-Dmixin.debug=true` 那套"点名失败的模组"的排查配方文档;
> 需要时按同样的方法做:Mixin 只在 debug 模式下打印 `Mixin apply for mod <模组> failed …`。

## 四、正常但看起来很吓人的日志

| 日志 | 说明 |
|---|---|
| `[OptiFine] (Reflector) Class not present: net.minecraftforge.*` / `sun.misc.SharedSecrets` | OptiFine 在探测 Forge 与旧 JDK 的类,Fabric 上本就没有 |
| `[OptiFine] Unknown resource pack type: ...ModNioResourcePack` | OptiFine 不认识 Fabric 的资源包类型,OptiFine 侧限制 |
| `[Indigo] Different rendering plugin detected; not applying Indigo.` | 让位键生效,属预期行为 |
| `[Shaders] Invalid program name: ...`(如 Photon 的 `dh_water`) | 光影包声明了该版本 OptiFine 不支持的程序名 |
| `Skipping bad option: lastServer` | 选项文件里的旧字段 |

## 五、升级本模组后行为没有变化

先删掉 `<游戏目录>/.optifine/`,缓存里存的是打过补丁的字节码;缓存格式号与代码不一致时会自动整份重建。
`[OptiFabric]` 的输出走**启动器控制台**,通常不在 `logs/latest.log` 里 —— "日志里没有 `[OptiFabric]`"不代表模组没运行。
