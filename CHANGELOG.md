# 更新日志

## 1.1.3+mc1.20.6 — `class_3898` 的 lambda 名字还回去(并说清 c2me 在这一份产物上到底行不行)

### 修了什么

- **症状**:装上 c2me(`0.2.0+alpha.11.100+1.20.6`)之后能到主界面,**一进世界就崩**:`InvalidMixinException: @Overwrite method method_17252(Lnet/minecraft/class_3193;Ljava/lang/Runnable;)V … was not located in the target class net.minecraft.class_3898` → `Mixin transformation of net.minecraft.class_3898 failed` → 集成服务器崩溃报告。去掉 OptiFabric、同样那几个 jar 时,同一个世界 0 错误加载。
- **根因**(逐字节确认):OptiFine 重编译 `net/minecraft/class_3898` 时,javac 给**游戏自己的 lambda 体**取了 javac 的名字,而游戏本体是用混淆名声明它们的。`method_17252(Lclass_3193;Ljava/lang/Runnable;)V` 就是 `lambda$protoChunkToFullChunk$36`,**描述符、访问标志、注册它的 bootstrap 方法句柄的 owner 与 tag 全都一样**,只是名字变了 —— 是**改名**,不是删除。c2me 按名字+描述符找它,自然找不到。这个类里这样的成员一共 **59 个**,`method_19487` 与 `method_20579`(c2me-opts-scheduling 另外两个 `@Overwrite`)也在其中。
- **修法**:新增 `LambdaMethodRefFix`,把 lambda 改回游戏给它的名字,并把注册它的方法句柄一起改指过去。它排在 `RestoreVanillaMethodsFix("method_17227", "method_18843")` **之前**注册,于是后者发现名字已被占,不再往旁边塞一份 vanilla 拷贝 —— 这样类自己的 bootstrap 句柄仍然指向一个存在的成员,而且跑的是 OptiFine 那份代码(模组 `@Overwrite` 覆盖的才是真正执行的那份)。
- **两种对齐方式**:先按「同一个宿主方法内,两边 LambdaMetafactory 的 invokedynamic 逐位对齐」;够不到的再按「两张 BootstrapMethods 表逐位对齐,且调用点形状必须一致」。两者都只从两个 class 文件自己的字节里取证据。

### 没修的那一个,以及为什么

`method_17224` **没有**被放回游戏的签名,c2me-threading-worldgen 的 `@ModifyReturnValue` 仍然找不到它,所以 `1.20.6 + c2me` 依然**进不了世界**(崩溃点从 `method_17252` 变成 `method_17224`)。原因是 OptiFine 这份字节自己就不自洽,三样东西互相对不上:

| | |
|---|---|
| 注册点压栈的值 | `class_9259`, `class_3898`(接收者), `class_1923`, `class_2806`, `class_3193`, `Executor` |
| lambda 自己的描述符 | `(class_1923, class_2806, class_3193, Executor, class_9259)` |
| lambda 体的读法 | 槽 2 当状态(status)、槽 3 当 chunk holder |
| 游戏声明 | `(class_1923, class_3193, class_2806, Executor, class_9259)` |

按描述符「槽 2 = status、槽 3 = chunk holder」,注册点压进去的却是「槽 2 = chunk holder、槽 3 = status」。**体、描述符、实参表三者本来就互相矛盾**,因此不存在任何重排能同时做到「三者自洽」与「交出游戏签名」:改体的槽位会改变它实际操作的参数;只改描述符不改体,就是一个**存在但说谎**的成员 —— 对 c2me 来说,`@ModifyReturnValue` 会读到错的实参,比找不到更糟。所以这个 fixer 只改名、不重排,并在日志里说明。

要把这一格也做掉,只能换一层动手:让 c2me 的 `@ModifyReturnValue` 去匹配 OptiFine 实际交出来的签名,或者对这一个注册点做一次知道真实实参顺序的专门重建(需要把体、描述符、实参表三者一起重写并按真实顺序验证,而不是只把描述符改个顺序)。这一条留给下一步,不在本次改动里。

### `1.20.6 + c2me`:这一份产物上它连加载都过不去(而且不是我们能修的)

- c2me 的 `fabric.mod.json` 里写着 `"breaks": { "tic_tacs": "*", "optifabric": "*" }`,而 **Fabric Loader 0.19.5 会执行 `breaks`**:不是警告,是硬拒载(`NEG_HARD_DEP`)—— 机制与那次实测见 1.1.2 那一节。
- 这条判断发生在**模组解析阶段**,早于 `preLaunch`、早于任何模组代码。拒载发生时**本模组一个类都没被加载**,所以**本模组这边没有任何代码、配置或运行时开关能绕开它**。
- 实测(本条线自己的 1.20.6 实例:Loader 0.19.5、Fabric API `0.100.8+1.20.6`、OptiFabric `1.1.3+mc1.20.6`、OptiFine `preview_OptiFine_1.20.6_HD_U_J1_pre18.jar`,再加 `c2me-fabric-mc1.20.6-0.2.0+alpha.11.100.jar`):

  ```
  [main/INFO]: Immediate reason: [NEG_HARD_DEP c2me 0.2.0+alpha.11.100+1.20.6 {breaks optifabric @ [*]}, …]
  [main/ERROR]: Incompatible mods found!
  ```

  整个实例**连 `Loading <N> mods:` 都没到**。
- 结论:**要用 c2me 只能装替代产物**(mod id `optifabric_reforged`,`wip/1.20.6-reforged` 分支),本产物与它**只能装一个**。替代产物 + c2me 的实测、它自带的兼容处理,以及那一个仍然修不掉的 `method_17224`,都写在**那条分支的** `docs/REFORGED_BUILD.md` 里。

### 这一版修的东西仍然有意义

`LambdaMethodRefFix` 是**真修复**,只是它的价值不在于"让这一份产物配上 c2me"(配不上,见上):

- 它把 `class_3898` 里 **59 个**被 javac 改名的成员改回游戏给它们的名字(c2me 三个 `@Overwrite` 要找的 `method_17252`/`method_19487`/`method_20579` 都在里面),并把注册它们的方法句柄一起改指过去;在**替代产物**上实测这三个注入全部应用(见 `collision-1206\REPORT.md`)。
- 它顺带消掉了整类"改名型"冲突:名字被占住之后,`RestoreVanillaMethodsFix` 不再往旁边塞一份 vanilla 拷贝,类自己的 bootstrap 句柄始终指向一个存在的成员。
- 对不带 c2me 的用法它同样生效(见下面的验证)。

### 验证

- **本产物 + c2me**:解析阶段就结束,`NEG_HARD_DEP c2me 0.2.0+alpha.11.100+1.20.6 {breaks optifabric @ [*]}` → `Incompatible mods found!`,**记录为预期且已文档化**(不是本次引入的回归)。
- **本产物不带 c2me**:pristine `.optifine` 缓存 + 这一份新 jar → 主界面 → 进世界,`/ERROR` 0 条。
- 离线校验与 1.1.2 相同:补丁管线、fixer 表与丢 `ClassInfo` 缓存那一步逐字节未变,1.0.0 记下的 **425 / 425 通过**、ASM 数据流验证器 **0 问题** 仍然对应这一份产物。
- `1.21.x` 线上早有同类 fixer(`LambdaMethodRefFix`),这里是它在 1.20.6 线上的对应物。

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
