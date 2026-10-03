# 更新日志

## 1.1.2+mc1.20.6 — 把 sodium 同时声明进 conflicts 与 breaks(breaks 是闸门,conflicts 只是警告)

### 改了什么

- **`fabric.mod.json`**:`sodium` 现在同时写在 `conflicts` 与 `breaks` 两处(`*`;**原有的 `conflicts` 条目一个字没动**)。上游 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)(默认分支 `llama`,v1.14.3)只在 `conflicts` 里声明它,而有些启动器与平台只读 `breaks`,所以两处都写。
- **文档**:`README.md` 与 `docs/FAQ.md` 新增「声明的不兼容」一节 —— 完整清单、继承自上游的条目、本移植**有意没有带过来**的三条 1.16/1.17 时代的版本范围(`cardinal-components-item <2.4.2`、`architectury >1.2.72 <1.3.77`、`meteor-client >=0.4.1`)、`ryoamiclights` 与本条 `sodium` 的来历,以及**加载器到底会不会拦**。

### `breaks` 是闸门,`conflicts` 只是警告

> **本节的早期版本写错了。** 它当时说 `conflicts` 与 `breaks` **两个字段都不产生** `Incompatible mods found`、都只是警告 —— 那是只测了 `conflicts`。在 **Fabric Loader 0.19.5** 上重测后:`breaks` **会被执行**,运行在解析阶段就被拒载。

在**本条线自己的 1.20.6 实例**里实测(Loader 0.19.5、Fabric API `0.100.8+1.20.6`、OptiFabric `1.1.2+mc1.20.6`、OptiFine `preview_OptiFine_1.20.6_HD_U_J1_pre18.jar`,`mods/` 里再加 `sodium-fabric-0.5.11+mc1.20.6.jar`):

```
[main/INFO]: Immediate reason: [NEG_HARD_DEP optifabric 1.1.2+mc1.20.6 {breaks sodium @ [*]}, ROOT_FORCELOAD_SINGLE optifabric 1.1.2+mc1.20.6, ROOT_FORCELOAD_SINGLE sodium 0.5.11+mc1.20.6]
[main/INFO]: Reason: [NEG_HARD_DEP optifabric 1.1.2+mc1.20.6 {breaks sodium @ [*]}, NEG_HARD_DEP sodium 0.5.11+mc1.20.6 {breaks optifabric @ [*]}]
[main/ERROR]: Incompatible mods found!
```

整个实例**连 `Loading <N> mods:` 都没到**,没有类被加载过。所以 `breaks` 是**闸门**:装上就是被加载器拦住,不是"作者温馨提示"。

`conflicts` 则是**警告**:`2.2.1` 那份产物只声明了 `conflicts: sodium`,在 1.21.x 线上实测 sodium 照常加载、应用自己的 mixin、然后在 mixin 里失败(见 `compat-matrix\README.md` §10.10),那次没有 `Incompatible mods found`。两个字段由加载器自身处理,与是哪条线无关。

于是这条声明的实际效果是:**在 id `optifabric` 这一份产物上,`sodium` 被硬拒载;`conflicts` 只为那些只读 `conflicts` 的平台保留**(上游 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric) 的默认分支 `llama` v1.14.3 只在 `conflicts` 里声明它 —— 它没带 `breaks`,也就没有这层闸门)。

本线的 mod id 仍是 **`optifabric`**,而 Sodium 自己那份元数据里针对的正是 `optifabric`(Sodium 只在 `breaks` 里声明它,没有 `conflicts`)—— 所以在 id 这一层上两条 `breaks` 会**同时**生效(都记在上面那条 `Immediate reason` 里;没有做"只留一条"的对照)。Sodium 1.20.6 构建里那条声明覆盖的确切版本范围,本仓库**没有实测记录**。

### 验证

这一版**没有改代码**:补丁管线、fixer 表与丢 `ClassInfo` 缓存那一步都与 1.1.1 逐字节相同(改动只进 `fabric.mod.json` 与文档),所以 1.0.0 记下的离线数字(**425 / 425 通过**、ASM 数据流验证器 **0 问题**)仍然对应那一份产物,1.1.1 的真机记录也继续适用。声明本身不影响启动期行为。

## 1.1.1+mc1.20.6 — 丢掉 Mixin 为被替换的类缓存的 ClassInfo(修 @ModifyVariable 的 LVTGeneratorError)

### 修复

- **症状**:某些 mixin 让**整个类**的变换失败,日志里只有 Fabric 那句 `Mixin transformation of <类名> failed`;真正的异常是 Mixin 抛的 `LVTGeneratorError: Could not locate method metadata for ...`,也就是**需要解析局部变量的注入(`@ModifyVariable` 与 locals 捕获)在 OptiFine 改过的类上必然失效**。它在 `require` / `expect` 之前抛出,受影响的模组自己无法绕开。
- **根因**:Mixin 为每个类建一份元数据(`ClassInfo`)并缓存,内容取自**它的字节码提供者** —— 在 Fabric 上就是 Knot,也就是本模组替换游戏类的那一层。Fabric 在 `preLaunch` 入口点**之前**就准备好了 Mixin 的配置,所以对本模组替换掉的那些类,缓存里可能已经存着**游戏自己的**那一份,而稍后交给 Mixin 变换的是 **OptiFine 的**那一份。两者对某个成员的记录一旦不一致,查找就落空:Locals(局部变量机制)用 `ClassInfo#findMethod(name, descriptor, access | INCLUDE_INITIALISERS)` 找正在变换的方法,而 `ClassInfo.Member#matchesFlags` 要求**存成 private 的成员必须用 ACC_PRIVATE 查**;OptiFine 重编译方法时把它从 private 放宽成 public,对着缓存里那份**游戏**记录就查不到。
- **修复**:`GameTransformerHook` 记住**它实际替换掉**的类(Loader 自己打过补丁的类不在此列),安装完这些类之后按**内部名**(斜杠形式)丢掉它们在 Mixin 缓存里的条目 —— 新文件 `MixinClassMetadata`,每个类最多丢一次,取不到缓存时只报告一次、不影响其它逻辑(过期元数据仍然能跑,只是那些解析类元数据的 mixin 照旧失败)。Mixin 于是按**它实际拿到**的字节重建元数据;字节仍是 Loader 自己那份的类没有被替换,条目照旧保留。
- **验证**:同一个修复在 **1.21.x 线**上真机端到端实测过(主界面、进世界,以及**光影包在世界里编译**)。1.20.6 这一份**没有**在真机上重跑,也没有重跑离线校验 —— 它的字节码修复(fixer 表与补丁管线)一个字节没动,这个改动发生在**补丁安装之后**;1.0.0 记下的离线数字(**425 / 425**、ASM 数据流验证器 0 问题)仍然对应那一份产物。

细节见 `README.md`;1.1.0 的提示与官方下载见下一节。

## 1.1.0+mc1.20.6 — 缺 OptiFine、或装的预览版过旧时的提示与官方下载

### 新功能

- **mods/ 里没有 OptiFine 时,每次启动都提示**:标题界面写明缺哪个文件,并给出「下载 OptiFine」按钮(只从 OptiFine 官网取,这也是本模组唯一会去下载的地方)与打开 mods 文件夹的入口。点「继续返回主菜单」只作用于本次会话、不会被记住,下次启动照旧提示。
- **装的是更旧的预览版时,按构建提示一次**:1.20.6 的建议构建是 `preview_OptiFine_1.20.6_HD_U_J1_pre18.jar`(OptiFine 为 1.20.6 只发布过 `HD_U_I9_pre1`、`HD_U_J1_pre17`、`HD_U_J1_pre18` 三个构建,全是预览版)。点「仍要继续」后把该构建记进 `config/optifabric-mismatch-ack.txt`,同一个构建不再重复提示。
- **正式版从不被提及**:装了正式版、或装了比表里更新的构建,都不会提示 —— 只有预览版才值得为它打断玩家。1.20.6 目前没有正式版,所以这一条不改变 1.20.6 的行为。
- **下载只走 OptiFine 官网**:地址栏默认填官方地址,也可以换成玩家自己的地址(那样就只从玩家填的地方取);下载失败时只说明原因并引导去官网手动下载,**不会**换第三方镜像重试。
- **下载完成先问再重启**:确认后 Windows 上走 JNA 的 `GetCommandLineW` + `CreateProcessW`,其它系统走 `ProcessHandle`;两条路都会在启动新进程后结束当前进程。

细节见 `README.md` 的「支持的版本」一节;1.0.0 的移植与兼容修复记录见下一节。

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
