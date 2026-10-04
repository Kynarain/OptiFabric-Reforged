# 更新日志

## 1.1.4-reforged+mc1.20.6 — 按平台要求移除运行时下载与进程启动;C2ME 垫片改为「打印指引并结束本次启动」;同时提供 GitHub-only 的 `-full` 构建

### 为什么改:平台的审核意见(原文与译文)

CurseForge 的审核**正是**因为这两点拒收了提交,原文:

> 该代码在运行时从外部来源下载 jar 文件,并通过 Windows 内核进程调用重新启动游戏,这可能存在安全风险。请移除运行时下载和进程启动功能。

译文:The code downloads a jar file from an external source at runtime and restarts the game through a
Windows kernel process call; this may pose a security risk. Remove the runtime download and the
process-spawning functionality.

这是**平台规则**,所以本版把这个产物的两处能力删掉:OptiFine 的运行时下载(同 `main` 线的移除),以及
**C2ME 垫片的自动重启**。

### C2ME 垫片的行为变化(唯一的功能性改动)

`config/c2me.toml` 的写入逻辑一字未改(只动 `[threadedWorldGen] enabled`,先备份,显式 `true` 不覆盖,
`-Doptifabric.noC2meCompat=true` 可关)。改的是写完之后的动作:

- 旧:用 `CreateProcessW` / `ProcessHandle` 重启游戏一次(一次性标记文件防循环);
- 新:**打印双语指引后结束本次启动**(`System.exit(0)`,退出码 0,不启动任何进程),一次性标记文件保留。
  日志里写「OptiFabric 改动了一项需要重启才生效的设置:请手动重新启动游戏。」,中文同句;第二次启动读到
  `enabled = false`,正常进世界(实测 0 错误)。

### 移除了什么

- **删掉 `OptifineDownloader`**(HTTP 客户端、官网下载、写 jar、JNA `CreateProcessW` / `ProcessHandle` /
  `ProcessBuilder` 重启、自测入口)与 `build.gradle` 里的 JNA 依赖;
- **`C2meCompat` 不再重启**(见上);
- **`Util.getOperatingSystem().open(...)` 删掉**:错误对话框按钮改为**复制链接/路径到剪贴板**;
- **`MissingOptifineScreen` 不再下载**:这个类**整个删掉**(2.1.0 没有它);找不到 OptiFine 时由 2.1.0 的确认对话框出面,屏幕上的文案里没有官网链接,官网地址只在日志与文档里。

### 提示界面:回到 2.1.0 的那一个对话框

初版把「本地文件安装」做成了一张带输入框和按钮的屏幕,后来又把提示做成标题界面上直接画的四行字 —— 两种都不是 2.1.0 的
样子,也都不是本版的样子。现在**完全回到 2.1.0 的机制**:找不到 OptiFine 时 `OptifineVersion.findOptifineJar()` 设一条
`OptifabricError` 文案并抛出一个**不致命**的失败,`mixin/MixinTitleScreen` 把它显示成这个 mod 唯一的那一个确认对话框
(标题 `There was an error loading OptiFabric!`),**没有任何独立的 OptiFine 屏幕类**。

正文就是 2.1.0 的原话(只把写死的 1.20.6 换成正在运行的版本,mods 路径仍插在 2.1.0 插的那个位置):

- 找不到:`OptiFabric could not find the OptiFine jar in the mods folder:` + mods 文件夹**绝对路径** + 空行 +
  `Download OptiFine for Minecraft <MC 版本> and place it in that folder next to this mod.`;
- 重复:`Please ensure you only have 1 copy of OptiFine in the mods folder!` + `Found:` 两行路径;
- 损坏:`The jar at <文件> is corrupt`;
- 认不出构建:`Unable to find OptiFine version from OptiFine jar at <文件>`;
- 版本不符:`This version of OptiFine from <文件> is not compatible with the current minecraft version` + 空行 +
  `Optifine requires <需要> you are running <实际>`。

**平台那段话只在日志和文档里**:找不到 OptiFine 时,日志逐行写出 mods 文件夹路径、搜索过的每一个位置、官网地址
(`https://optifine.net/downloads`),并写明本产物不带下载器、不启动任何进程、只读用户自己放进 `mods/` 的本地 `file:` jar。
屏幕上的文案里既没有官网链接,也没有「为什么不再下载」的说明。

对话框的两个按钮**只做复制**(`client.keyboard.setClipboard`):mods 文件夹路径 / 帮助链接;内部错误时是堆栈 / issues
链接或 logs 路径。**不打开文件夹、不打开网页、不启动任何进程**(2.1.0 的「打开 Mod 文件夹 / 打开帮助」在 Windows 上就是
`ShellExecute`,属于平台要求删掉的那一类)。

**提示规则**(`OptifinePrompt` 是一个**不画任何界面**的闸门,在标题界面那一步生效):

- 没有 OptiFine → **每次启动**都弹(错误在 finder 里就设好了,和 2.1.0 一样);
- 装了更老的 **preview** → 同一个对话框,但**每个构建只弹一次**(已提示的构建写进
  `config/optifabric-mismatch-ack.txt`);
- SAME / NEWER / 任何正式版 → **从不提示**。

### 两条产物

| 产物 | 内容 | 去处 |
|---|---|---|
| `OptiFabric-Reforged-1.1.4-reforged+mc1.20.6.jar` | **无**运行时下载、**无**进程启动 | CurseForge / Modrinth / GitHub |
| `OptiFabric-Reforged-1.1.4-reforged+mc1.20.6-full.jar` | 保留自动下载与自动重启(含 C2ME 垫片的自动重启) | **仅** GitHub |

两者 mod id 相同;**只能装一个**。

## 1.1.3-reforged+mc1.20.6 — 替代产物:自动关掉 c2me 的线程化世界生成(装好就能进世界)

### 新功能

- **`C2meCompat`**:装了 `c2me` 时,启动的 `preLaunch` 第一步把 `config/c2me.toml` 里的
  `[threadedWorldGen] enabled` 写成 `false` —— **只动这一个键**,其余字节原样保留;第一次修改前把原文件备份到
  `config/c2me.toml.optifabric-backup`(只备份一次,不覆盖)。
  - c2me 的元数据里写着 `breaks: { optifabric: "*" }`,所以**只有这一支产物**(mod id `optifabric_reforged`)
    能让 c2me 加载;而加载之后,`c2me-threading-worldgen` 会注入 OptiFine 重编译过的 `class_3898.method_17224` ——
    那个成员的字节**自相矛盾**(lambda 描述符 / 注册点压入的实参 / 方法体读的槽位三者对不上),**不可能**在保持
    游戏签名的同时自洽,所以只能把那个模块关掉。分析见 `docs/REFORGED_BUILD.md` 与 `collision-1206\REPORT.md`。
  - **尊重显式 `true`**:使用者自己把它写成 `true` 时,垫片**只打印警告、不覆盖**(那个组合进世界必崩)。
  - **只写一次**:第二次启动看到已经是 `false`,就一个字节都不写。
  - **开关**:`-Doptifabric.noC2meCompat=true` 完全不碰配置,并在日志里说明。
  - 只在装了 c2me 时做任何事;没有 c2me 时这一支与已发布产物行为一致。
- **第一次启动会自己重启一次游戏。** Fabric Loader 先准备所有模组的 mixin 配置、**之后**才调用 `preLaunch`
  (Loader 0.19.5 的 `Knot.init` 顺序),而 c2me 是在它自己的 mixin 插件 `onLoad` 里读 `config/c2me.toml` ——
  所以 `preLaunch` 里写的值只对**下一次**启动生效。垫片因此会读回 c2me 本次解析出来的值,只在「必须改 + 本次是开」
  时用**同一条命令行**重启一次,并在进入世界之前结束当前进程;三重防循环(值本来就已经是关 / 写完之后重新读盘确认 /
  标记文件只允许一次自动重启)。之后每次启动都是普通启动。

### 验证(本条线自己的实例,pristine `.optifine` 缓存,`-Xmx6G` 让 c2me 的默认值真的是"开")

| `mods/` | 结果 |
|---|---|
| 替代产物 1.1.3-reforged + c2me,全新实例(连 `config/c2me.toml` 都没有) | **进世界**,0 个 `/ERROR`(1236 行)。第一次启动:垫片写 `"default" -> false`、备份、自己重启;第二个进程里 `Config threadedWorldGen.enabled changed from true to false` → `Disabling com.ishland.c2me.threading.worldgen.mixin`,垫片打印 `already says … false, nothing to do` |
| 同一个实例紧接着再跑一次(幂等) | **进世界**,0 个 `/ERROR`;`config/c2me.toml` 前后**是同一个 sha256**,备份仍是原来那一份,垫片打印 `nothing to do (file left untouched)` |
| 同上,但加 `-Doptifabric.noC2meCompat=true` | **只到主界面**:世界加载崩在 `method_17224`(4 个 `/ERROR`、1 个 `Mixin apply … failed`、3 个 `InvalidInjectionException`、1 个崩溃报告),配置**一个字节没动** |
| 同上,但配置里显式 `enabled = true` | **只到主界面**:垫片打印警告且不覆盖,文件仍然是 `true`,崩溃与上一行相同 |
| 替代产物,不带 c2me | **进世界**,0 个 `/ERROR`(967 行) |
| 替代产物 + sodium | **被我们自己的声明拒载**:`NEG_HARD_DEP optifabric_reforged 1.1.3-reforged+mc1.20.6 {breaks sodium @ [*]}` |

### 说明

- 这一支仍然是**替代产物**:mod id 变了,所以 13 条第三方针对 `optifabric` 的声明(10 个模组)不再生效,
  而且两个 1.20.6 产物**只能装一个**(实测:同时装会死于 `Non-unique Mixin config name optifabric.mixins.json`)。
  代价、完整表格与实测见 `docs/REFORGED_BUILD.md`。
- 它同样带着 `class_3898` 的 lambda 改名修复(59 个成员):没有它,`c2me-opts-scheduling` 的三个 `@Overwrite`
  与 `c2me-notickvd` 的几个注入都找不到目标。

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
