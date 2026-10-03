# 更新日志

## 2.2.2+mc1.21 … 2.2.2+mc1.21.11 — 声明 sodium 不兼容（conflicts 与 breaks 同时列出）

> 这一版覆盖全部 10 个产物（1.21 / 1.21.1 / 1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 / 1.21.9 / 1.21.10 / 1.21.11）,
> 行为一致。**修订号递增的原因是元数据修正(声明不兼容而不是门槛)**：
> sodium 现在 `conflicts` 和 `breaks` 两处都列出（Fabric Loader 0.19.5 上两个字段都只产生警告、不阻止游戏启动）。

- **`sodium` is now declared in both `conflicts` and `breaks`** in `fabric.mod.json`: on Fabric Loader 0.19.5 measured here, **neither field prevents the game from starting** -- both are warnings, so this is a declaration, not a gate.

## 2.2.1+mc1.21 … 2.2.1+mc1.21.11 — 换过类之后丢掉 Mixin 的旧类元数据,局部变量捕获不再让整个类变换失败

> **这一版覆盖全部 10 个产物**(1.21 / 1.21.1 / 1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 / 1.21.9 / 1.21.10 / 1.21.11),
> 全部是 2.2.1,行为一致。**修订号递增的原因是向下兼容的问题修正**:替换完游戏类之后丢掉 Mixin 为这些类缓存的旧元数据。
> 没有新能力、也没有不兼容修改(mod id 仍是 `optifabric_reforged`),升级直接换 jar 即可。

### 修复:在 OptiFine 改写过的类里解析局部变量的 mixin 不再在加载期失败

OptiFabric 读走游戏类之后会把它替换成 OptiFine 打补丁后的字节码,而 Fabric 在 preLaunch 入口点跑起来之前就已经把 Mixin 的
配置准备好了 —— 所以对**被替换的这些类**,Mixin 的 `ClassInfo` 缓存里存的可能是**游戏自己**的那份。之后 Mixin 拿到的是
OptiFine 的那份:两份字节只要在某个成员上不一致,查表就会落空,而 `Locals`(`@ModifyVariable` 与局部变量捕获用的那套机制)
对此没有退路 —— 它用

    ClassInfo#findMethod(name, descriptor, method.access | INCLUDE_INITIALISERS)

解析正在被变换的方法,而 `ClassInfo.Member#matchesFlags` 要求**缓存里记为 private 的成员必须以 `ACC_PRIVATE` 查询**。
OptiFine 重编译 `GameRenderer.getFov` 时把它从 private 放宽成 public,于是对着缓存里那份"游戏自己的"元数据查不到,
整个类变换失败:

    LVTGeneratorError: Could not locate method metadata for method_3196 generating LVT in net/minecraft/class_757

这个异常抛在 `ModifyVariableInjector.preInject` 里,此时 `require` / `expect` 都还没被看到,**受影响的模组无法从自己这一侧绕过**;
Fabric 侧只留下一句笼统的 `Mixin transformation of net.minecraft.class_757 failed`。

`GameTransformerHook` 现在记住它**真正替换过**的那些类,装好之后把这些类的缓存条目丢掉(内部名形式、反射删除、每个类至多
一次、失败只记日志不致命),Mixin 于是按它真正拿到的那份字节重建元数据 —— 正在被变换的类是通过 `ClassInfo#fromClassNode`
进入 Mixin 目标上下文的,而那个方法只要缓存里有就直接返回缓存实例。字节仍是 Loader 自己那份的类(没有被替换)不动它的缓存条目。

实测(1.21.1,OptiFine HD U J1,ShoulderSurfing 5.2.0、ForgeConfigAPIPort 21.1.6 与 Fabric API):**已发布的 2.2.0 jar 6 秒就死在
`Mixin transformation of net.minecraft.class_757 failed`**;本版日志里出现 `Dropped 47 of 425 Mixin class metadata entries`,
进标题界面、进存档,光影包正常编译 27 个世界内程序,无注入错误、无崩溃报告。配 Architectury 13.0.11 时 `LocalSlotLayoutFix`
报告的数字与 2.1.0 相同(槽位 7→15、10→16;OptiFine 那份 21 个局部变量,游戏那份 18 个),运行以 FIXED 结束。

### 校验

本版只改了这一处(替换类之后处理 Mixin 缓存的旧元数据),补丁管线与其余 fixer 未动,2.2.0 那套离线/真机校验结论继续适用;
十个产物的尺寸与 SHA-256 见 `release\MANUAL_RELEASE.md`。

## 2.2.0+mc1.21 … 2.2.0+mc1.21.11 — 缺 OptiFine 时先说清楚,并可从官网下载

> **这一版覆盖全部 10 个产物**(1.21 / 1.21.1 / 1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 / 1.21.9 / 1.21.10 / 1.21.11),
> 全部是 2.2.0,行为一致。**次版本号递增的原因是新能力:缺 OptiFine(或装的是过旧预览版)时出现提示界面、可从官网下载、下载后询问是否重启**;
> 没有任何东西被移除(按 `docs/VERSIONING.md` 的映射表,新能力走 minor,不兼容修改才走 major)。
> mod id 与 2.0.0 相同(仍是 `optifabric_reforged`),升级直接换 jar 即可。

### 新增:启动时发现 OptiFine 缺失/过旧就提示,而不是默默照常启动

在这之前,`mods/` 里没有 OptiFine 时游戏会照常启动,只是 OptiFine 的功能全都不在 —— 用户得自己想到"是不是少放了什么"。
本版在标题界面打开前先看模组自带的 OptiFine 支持表,分两种情形处理:

- **`mods/` 里完全没有 OptiFine**(`OptiFine 未安装`):**每次启动都会出现**这个界面,直到装上为止;界面写明该 MC 版本
  需要哪个构建、哪个文件名,`继续返回主菜单` 只把**这一次**会话放过去(不写任何"已确认"记录);
- **装了 OptiFine,但比本版已知的最新构建更旧**(`OptiFine 版本不匹配`):游戏**照常继续运行**,界面只是告诉你有更新的一版;
  点 `仍要继续` 后会把这个构建记进 `config/optifabric-mismatch-ack.txt`,同一个构建不再重复打扰。
  **只有预览版(文件名或版本号里带 `_pre`)比表里的旧时才会提示**;你已经装了正式版就不会被打扰,哪怕之后有更新的正式版 ——
  一个正式版是正常发行版,不值得为它中断你。

### 新增:从 OptiFine 官网下载,只从你指定的那一处取

界面上的 `下载 OptiFine`(`OptiFine 版本不匹配` 时是 `下载正确的版本`)会取回该 MC 版本需要的那个 jar,放进 `mods\`:

- 地址栏默认是 OptiFine 官网的两步流程:`https://optifine.net/adloadx?f=<文件名>` 页面带一次性令牌,jar 在同一台主机上由
  `downloadx?f=<文件名>&x=<令牌>` 给出。页面是 HTML,所以用浏览器 User-Agent 取;
- **本模组不带任何第三方/镜像地址,也不会自动回退**:官网这条路一旦失败(非 200、页面上没有令牌、拿回来的不是 zip、
  zip 不是 OptiFine、域名不通),界面如实写出原因,并把按钮换成 `打开官网下载页` + `重新检查`,由你自己下好、放进 mods 再点重新检查;
- 地址栏是**可以改的**:填一个带 `{mc}` / `{type}` / `{patch}` / `{file}` 占位符的地址会按支持表填好直接取 —— 但那必须是你自己填的,
  模组不会替你做这个选择。

### 新增:下载完成后先问,再重启

下载成功(或重新检查找到了 jar)不再直接退出游戏,而是先问一句 **`是否重启游戏使 OptiFine 生效?`**:
`立即重启` 用**启动本进程时的那条命令行**重新拉起游戏再结束自己(Windows 下 JVM 读不回自己的命令行 —— 游戏目录里带个空格
就读不出来了 —— 所以用 JNA 的 `GetCommandLineW` 取原始命令行、`CreateProcessW` 原样重跑;非 Windows 走 `ProcessHandle`;
启动器把命令行藏起来时如实说明,让你自己重启)。JNA 5.14.0 只是编译期依赖(`compileOnly`),不打包进 jar。

### 文档与支持表

`README.md` / `README_CN.md` 新增“OptiFabric 版本 → Minecraft 版本 → 需要的 OptiFine 构建”表(与 `release\notes\`、
本模组内置的支持表三者由 `release\version.ps1 -CheckSupport` 核对),并说明该表列的是该版本**最新正式版**(没有正式版才用最新预览版)。
README 与 `docs/DEVELOPMENT.md` 里的第三方镜像链接全部移除:下载只走官网。

### 已知限度

- 提示只覆盖"完全没有"与"比表里的旧"两种情形。比本版已知构建**更新**的正式版 OptiFine 一律放行、不提示(它通常就是能用的);
- 自动重启依赖启动器把命令行交出来。拿不到时(`Cannot restart automatically`)只提示你手动重启,不会假装重启成功;
- 下载的 jar 会校验:必须是 zip、必须含 `net/optifine/Config.class`(本线十份构建在 `notch/` 下也有),单次响应上限 64 MiB,
  连接超时 15 s、整次请求 3 min,拒绝 https→http 跳转 —— 畸形或恶意地址只会以可读的原因失败,不会在 `mods\` 里留下半个 jar。

### 校验

本版没有改动补丁管线与任何 fixer(改的是"缺 OptiFine 时怎么办"这条路径),因此 2.1.0 那套离线/真机校验结论继续适用;
十个产物的尺寸与 SHA-256 见 `release\MANUAL_RELEASE.md`。

## 2.1.0+mc1.21 … 2.1.0+mc1.21.11 — 崩溃报告自己说明原因,并让 SophisticatedCore 这类模组重新有注入目标

> **这一版覆盖全部 10 个产物**(1.21 / 1.21.1 / 1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 / 1.21.9 / 1.21.10 / 1.21.11),
> 全部是 2.1.0,行为一致。**次版本号递增的原因是新能力:崩溃报告里新增一节诊断,固定的几个模组冲突不再需要用户自己猜**;
> 没有任何东西被移除(按 `docs/VERSIONING.md` 的映射表,新能力走 minor,不兼容修改才走 major)。
> mod id 与 2.0.0 相同(仍是 `optifabric_reforged`),从 2.0.0 升上来直接换 jar 即可。

### 新增:崩溃报告里的 `OptiFabric` 诊断节

本项目被问得最多的一类崩溃是 `NoClassDefFoundError: Could not initialize class
net.optifine.reflect.Reflector`(有些实例连崩溃报告都不留,直接退出)。它的链条是:OptiFine 的崩溃报告器通过
`Reflector` 读自己的版本号 → 这次读取要加载某个游戏类 → 那个类因为**另一个模组的 mixin 没能注入到 OptiFine
改写过的类里**而无法完成加载 —— 于是报告停在 `Reflector`,**完全没提是哪个模组**。

现在只要原因链里出现那个签名(`net.optifine.reflect.Reflector`,或 `Mixin transformation of` / `Mixin apply for mod`),
崩溃报告就会多出一节 `OptiFabric: OptiFine / mixin conflict`,写明发生了什么、真正的模组名只在
`-Dmixin.debug=true` 下才会被 Mixin 打出来、已知的三类冲突,以及该怎么办(删掉那个模组,或者不用 OptiFine)。
它是 `@Unique` 方法,签名不匹配时一个字都不加,整段还包在 `try/catch` 里 —— **诊断永远不会顶掉它要解释的那份报告**。

### 新增:README 的排查配方

`README.md` / `README_CN.md` 的"支持与排查"里补了同一套做法:如何用 `-Dmixin.debug=true` 让 Mixin **点名**失败的
模组(`Mixin apply for mod <模组> failed … -> net.minecraft.class_<n>`),以及已知的三类:
捕获/修改方法局部变量或参数的模组、要求某个方法里调用点数量正好相等的模组、注入点本身就是某个调用点而 OptiFine
把它换成了自己的方法的模组。不加这个参数时 `latest.log` 里没有任何指向元凶的信息。

### 修好了:SophisticatedCore / Sophisticated Backpacks 装上就启动崩

它的 `client.ParticleEngineMixin` 用 `@ModifyArgs` 要求 `class_702.method_34020`(ParticleEngine)。OptiFine 重编译时
把那段循环折进了 `lambda$addBlockDestroyEffects$13`(描述符一样),补丁后的类里**根本没有** `method_34020`,
`@ModifyArgs` 按名字 + 描述符找不到,`require = 1` 于是让整个类变换失败。现在用
`RestoreVanillaMethodsFix("method_34020")` 把原版方法体补回去当注入目标 —— 补丁类里没有任何地方引用它,是死代码。

**实测(1.21.1 + SophisticatedCore / Sophisticated Backpacks 那个整合包)**:`Restored vanilla
net/minecraft/class_702.method_34020(...)V so injections into it have a target`、
`Prepared 425 patched classes (0 skipped, 0 failed)`、`Mixing client.ParticleEngineMixin … into
net.minecraft.class_702` 不再失败,客户端走到声音引擎、无崩溃报告。

### 还有两个:**故意不修**

它们不是槽位问题,修不了,只能由模组作者改,清单在 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) 的冲突表里:

- **CarryOn 2.2.6.13**(1.21.1):它的 `LevelRendererMixin` 注入在 `class_761` 里对 `class_702.method_3049` 的
  **调用点**上,而 OptiFine 把那两处调用换成了自己的 `class_702.render(…)` → 注入点数量为 0。槽位修复器在这里是
  空操作(该方法原版局部变量本来就不按槽号排列,19 个候选 > 上限 8);伪造一个调用点会让粒子画两遍、回调时机也变;
- **ShoulderSurfing 5.2.0**(1.21.1):它的 `CameraMixin` 要求 `class_4184`(Camera)里调用点**数量正好相等**,
  而 OptiFine 重写 Camera 后数量不符。

### `LocalSlotLayoutFix` 的稳健性:不是搬流程,是搬之前别把类型和布局搞错

2.0.0 那次独立复核在 `LocalSlotLayoutFix` 里查出四个缺陷(都不改变已发布版本上的搬移结果,本版一并修掉):

- 槽位类型原来是"从第一条提到它的指令猜",于是 `long`/`double` 这类**占两个槽位的候选**只被分配一个槽,两个宽候选
  还会被分到同一个目标槽 —— 那是**非法字节码**。现在先读方法自己的局部变量表,读不到才退回 opcode 探测(只认
  `*LOAD`/`*STORE`);
- 布局回退原来**逐方法**选表示法:只有一侧带局部变量表时,两边会用不同表示法比较(`Z` 对 `I`),遍历错位后撞上
  ">8"上限 —— 于是这个 fixer 存在的意义(阻止那次注入失败)反而没生效。现在只要有一侧来自 opcode 回退,就**按
  类别**比较,只有两侧都来自各自的表时才用精确比较;日志会写明每侧用的是哪种表示法;
- ">8"上限的消息与"无需搬移"两条路径原来会把两种原因混为一谈(或干脆不说话),现在分开说;
- `ClassFixer.fix` 注明:传进来的 minecraft `ClassNode` 是共享缓存,**必须当成只读**。

### 已知限制:`LocalSlotLayoutFix` 的"保留"判定只看槽号,不看作用域

搬移前会保留"原版也有"的槽位,而这个判定是**按槽号**的:槽位只要在遍历里匹配过一次就整体不搬,但槽号会被复用 ——
1.21.1 的 `class_757.method_3192` 有 21 个局部变量(原版 18 个),槽位 12 同时住着 OptiFine 自己的 `class_425 rlpg`
和原版 catch 块的局部变量,于是这个多出来的局部变量被误判为保留、留在原版局部变量中间(1.21.4 是 22 对 19,
1.21.11 是 18 对 17,**所以 1.21.11 报的"无需搬移"并不是两边本来就一致**)。实测没有破坏本 fixer 针对的捕获
(Architectury 仍为 FIXED、无注入错误、无 `VerifyError`),它没覆盖到的注入点没有逐个枚举;现在 fixer 会把被丢掉的
候选槽位连同两侧条目与指令区间打进日志,修法(按作用域而不是按槽号重映射)见类里的注释。

> 提醒:`.optifine` 缓存按 **OptiFabric 版本号**判定新旧,不按 jar 内容。用同一版本号的新构建做验证前,要先删掉实例
> 里的 `.optifine` 目录,否则会继续使用旧构建写进去的补丁产物(这次就因此先被误导了一次)。

### 校验

十个产物的字节数与 SHA-256 见各自的发布页(`release/notes/mc<MC>.md`),构建产物在 `build/libs/`。

---

> **下面这一节记录的是已经发出去的 2.0.0**(jar 内容与发布页都已冻结)。当时发布的十个 jar 里只有本节写的东西;
> 上面 2.1.0 的那几条**不在** 2.0.0 里 —— 它们的 jar 已经发布了,而那之后的改动属于新版本(§3)。

## 2.0.0+mc1.21 … 2.0.0+mc1.21.11 — 换用本分叉自己的 mod id,并修掉让 Architectury 崩在第一帧的局部变量冲突

> **这一版覆盖全部 10 个产物**(1.21 / 1.21.1 / 1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 / 1.21.9 / 1.21.10 / 1.21.11),
> 全部是 2.0.0。**主版本号递增的原因是换了 mod id** —— 按 `docs/VERSIONING.md` 的映射表,改 id 属于不兼容修改。

### 升级前必须做:删掉旧 jar

mod id 从 `optifabric` 改成 **`optifabric_reforged`**,显示名改成 **OptiFabric Reforged**(与 26.x 线一致)。
身份变了,所以**先把 `mods/` 里旧的 `OptiFabric-<版本>+mc1.21.x.jar` 删掉,再放新的**:两个 id 同时存在时 Fabric 会
**同时加载两份**,OptiFine 会被打两遍补丁。

### 修好了:装上 Architectury 不再崩在第一帧

用户的报告("和 Architectury 不兼容")其实是两层,分开看才清楚:

1. **声明层**:Architectury 自己的元数据写着 `breaks: optifabric <1.13.0`,Loader 直接拒载,它的三个 1.21.1 构建
   都一样。这条规则不是我们的元数据,能改的只有我们自己的 id,于是有了上面的改名;改名后规则不再匹配,
   按常规方式装即可(不需要任何 `fabric_loader_dependencies.json`)。
2. **字节码层**:把声明中和掉之后,游戏跑到第一帧就崩:
   `InjectionError: LVT in net/minecraft/class_757::method_3192 has incompatible changes at opcode 601`。
   Mixin 把方法的局部变量**按槽位顺序**交给 `@Inject` 处理器(`CallbackInjector` 从 `getFirstNonArgLocalIndex`
   起取前 N 个非空局部变量),而 OptiFine 的构建在 `GameRenderer.render` **中间**插入了它自己的两个 float
   (`guiFarPlane`、`guiOffsetZ`),把原版的 `Matrix4f`/`Matrix4fStack`/`GuiGraphics` 顶高了 1–2 个槽位,
   处理器按原版顺序声明的参数因此对不上。

新增 `LocalSlotLayoutFix`:按槽位顺序对齐两边的局部布局,把"原版没有、OptiFine 自己加的"槽位整体挪到方法
局部变量区末尾(1.21.1 实测 7→15、10→16),只改槽号不改语义;局部变量表随之改写 —— **那张表才是 Mixin 读取
局部变量的来源,而管线里没有任何 writer 会重建它**;栈帧交给改动类必经的 `FrameComputingWriter` 重算。

实测(1.21.1 + `OptiFine_1.21.1_HD_U_J1` + fabric-api 0.116.17 + architectury 13.0.11,不放任何配置文件):
56 个模组加载、fixer 报告槽位搬移、进世界正常、无注入错误、无 `VerifyError`、无崩溃报告。

### 另修

`build.gradle` 的 `processResources` 从来没把 mod id / 显示名声明成 task inputs,于是改名后 Gradle 认为任务
up-to-date、继续展开旧 id(jar 里仍是 `optifabric`)。现已补上 `inputs.property`,改名才会真的生效。

### 顺带修好的工具缺陷

`release\version.ps1 -RecordDigest` 的版本串匹配没有锚定:`2.0.0+mc1.21.1` 是 `2.0.0+mc1.21.11` 的前缀,
于是 1.21.1 那一次会把 1.21.11 的段落一起改写(实测把 `release/notes/mc1.21.11.md` 的尺寸与 SHA-256 写成了
1.21.1 的值)。现改为 `(?!\.?\d)`:后面不能再跟"可选点 + 数字" —— 既能匹配 `...1.21.11.jar`,又不会把
`...1.21` 后面的 `.11` 吃进去。

另有一处:发布清单里的"逐版数据"表在摘要脚本眼里是**一个段落**,而它按段落改写,于是**最后运行的那个版本会把
十行的尺寸与 SHA-256 全写成自己的值**(实测十行全变成 1.21.10 那份的值)。现在只要段落里出现多个版本,就退化为
**按行**改写、且只改提到本版本的那些行;用扰动测试验证过:只修被改坏的那一行,相邻行不动。

### 修正:后处理文件的现代化改写现在按版本设门

`OptifineJarFixer` 会把 OptiFine 自带的 FXAA 后处理文件从旧写法(`"program"` 键)改写成 1.21.6 起的新写法
(`"vertex_shader"` / `"fragment_shader"`),并给 blit pass 补 `BlitConfig`。这段改写原本只按**文件内容**触发,
于是在 **1.21.3 / 1.21.4** 上把文件改成了这两版的解析器读不了的形状:

```
Failed to parse post chain at minecraft:post_effect/fxaa_of_2x.json
JsonSyntaxException: Not a json array: {"BlitConfig":...}; No key program
```

后果是这两版的**抗锯齿链解析失败、静默失效**(实测:不设门时 2 条;已发布的 1.1.2 是 0 条 —— 1.1.2 根本不改这些文件)。

现改为**内容判据**,不再依赖版本号列表:只有游戏自带的 `post_effect/*.json` 使用新键时才做这段改写;只有游戏带
`GpuTexture` API(1.21.6 起)时才做那处纹理修复。实测:1.21.3 与 1.21.4 由 2 条降到 **0 条**,1.21.11 仍走改写路径
(未被误跳过),1.21.1 + Architectury 用同一个新 jar 重验仍为 FIXED。

### 校验

十个产物的字节数与 SHA-256 见各自的发布页(`release/notes/mc<MC>.md`),构建产物在 `build/libs/`。

## 1.1.2+mc1.21.3 … 1.1.2+mc1.21.11 — 抗锯齿全线修复,并纠正 1.1.1 里的错误结论

> **这一版覆盖 8 个产物**:1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 / 1.21.9 / 1.21.10 / 1.21.11(都叫 1.1.2)。
> **1.21 与 1.21.1 不受影响,继续停在 1.1.0** —— 那两版的 OptiFine 只带老位置的后处理链,当时的修复根本没碰过它。
> 每个 jar 的版本号只描述它自己那份产物(SemVer §3),所以这是"8 个产物一起升到 1.1.2",不是把整条线重发;
> 1.21.11 那一份在**行为上**与 1.1.1 相同(它那时已经修好),但字节不同(管线不再做那次后处理链修复),
> 因此同样按新版本发布。

1.1.1 修的是 1.21.11 换光影包时的"重载资源失败"。当时判断问题只出在这一版,并写下"1.21.6–1.21.10 上 OptiFine
读老位置、实测可用"。**逐版本真机实测证明那句话是错的。**

### 实测:缺陷覆盖 1.21.3 起的几乎所有版本

每个版本都用**生产 jar**跑一遍(抗锯齿 2x + `ComplementaryReimagined_r5.9.1.zip` + 进世界,75–140 秒后读
`logs/latest.log`):

| MC | `Resource not found: minecraft:post_effect/fxaa_of_*` | 同会话里的 `Failed to load post chain` |
|---|---|---|
| 1.21 / 1.21.1 | —(修复没碰过这两版) | — |
| 1.21.3 | **2 条** | 未出现(那次没切抗锯齿) |
| 1.21.4 | **2 条** | 同上 |
| 1.21.6 | 未测到(无光影时不出现此警告) | 无光影:0 条;**启用光影则启动阶段崩溃**(见下方注) |
| 1.21.7 | **2 条** | 未出现 |
| 1.21.8 | **2 条** | **2 条**(用户 09-12 的会话) |
| 1.21.9 | **2 条** | 未出现 |
| 1.21.10 | **2 条** | **2 条**(用户 09-12 的会话) |
| 1.21.11 | 0(1.1.1 已修) | 0 |

> **1.21.6 那一行的更正(2026-09-13 本机复现)**:当时"量不到"是因为那台机器上这两版**启用光影即崩**,
> 而不是它们根本起不来 —— 用 `1.1.2` 生产 jar 复测:**不开光影时可以正常启动**(标题界面正常渲染、无崩溃报告),
> 只有**选了光影包**才会在启动阶段崩于 OptiFine 自身的 `NullPointerException ... "multiTex" is null` at
> `net.optifine.shaders.ShadersTex.initDynamicTextureNS`(把 `shaderPack=` 清空即恢复;只开 `ofAaLevel:2` 不崩)。
> 详见 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) 的"仍待办的两项 A"。

代码层面它对得上:OptiFine 补丁过的 `ShaderManager`(`class_10151`)里注册 FXAA 链的方法,在
1.21.8 / 1.21.9 / 1.21.10 / 1.21.11 上**逐条指令、逐条常量完全一致**,字符串配方就是 `post_effect/\u0001.json`;
这套代码**从 1.21.3 起就有**。而当时的修复**从 1.21.3 起一直在删那个文件** —— 所以它从一开始就是错的,
只是 1.21.11 先被真机撞到。

> 1.21.9 / 1.21.10 那条"抗锯齿黑屏已修好"的结论同样是错的:删掉链之后链**加载失败**,抗锯齿不再被应用,
> 于是不黑屏了 —— 看起来像修好,其实是**静默失效**。真正的原因是顶点着色器,见下。

### 本版做了什么

1. **不再碰 OptiFine 的后处理文件**:`OptifinePostChainFixer` 整个删除。链本来就该由游戏按
   `minecraft:fxaa_of_2x` 从 `post_effect/` 解析(OptiFine 把 `ShaderManager` 改成在那儿注册),OptiFine 自带的
   那份就是它要的:补写老位置文件多余,删掉新位置文件是错的。这一条同时消掉"每次资源重载刷警告"与
   "一动抗锯齿就弹重载资源失败"。
2. **`OptifineJarFixer` 增加顶点着色器修复**:游戏从 1.21.9 起改用 `gl_VertexID` 生成全屏三角形、**不再提供
   `Position` 顶点属性**,而 OptiFine 1.21.9 / 1.21.10 那份 `fxaa_of_*.vsh` 还在读 `Position`(顶点塌成一点 ——
   这才是当初"一开抗锯齿整屏黑"的真正原因)。管线把这两个 `.vsh` 改写成同一套全屏三角形写法,
   `SamplerInfo` / `FxaaConfig` 两个 uniform 块与 FXAA 的 `posPos` 计算**原样保留**。判据是**文件内容**
   (游戏那份 `core/screenquad.vsh` 是否用 `gl_VertexID` + OptiFine 那份是否还在读 `Position`),所以
   1.21.3–1.21.8 的 `.vsh` 与 1.21.11 那份(sp614x 自己已经修好)**都不动**。

缓存格式 25 -> **26**。

> 日志里会重新出现一对 `Resource not found: minecraft:shaders/post/fxaa_of_{2,4}x.json` —— 那是 OptiFine 每次
> 还去 1.21.6 之前的**老位置**探一次它的链(1.21.8 起的构建早就不带那个文件),纯探测警告:链走的是
> `post_effect/` 那条,不补写老位置文件反而少一个没人读的资源。用户已确认抗锯齿正常的 1.21.11(1.1.1)会话里
> 同样有这两条。

### 离线校验(每个受影响版本一条命令)

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-version.ps1 -Version 1.21.10 -ModVersion 1.1.2
```

| MC | 补丁类(JVM+ASM) | OptiFine 类(JVM+ASM) | ASM 问题 | @At 注入点 | 其余扫描器 |
|---|---|---|---|---|---|
| 1.21.3 | 440 / 440 | 816 / 816 | 0 | 2(已停用 indigo) | 全 0 |
| 1.21.4 | 474 / 474 | 812 / 812 | 0 | 2 | 全 0 |
| 1.21.6 | 487 / 487 | 820 / 820 | 0 | 4 | 全 0 |
| 1.21.7 | 500 / 500 | 823 / 823 | 0 | 4 | 全 0 |
| 1.21.8 | 516 / 516 | 831 / 831 | 0 | 4 | 全 0 |
| 1.21.9 | 519 / 519 | 832 / 832 | 0 | 4 | 全 0 |
| 1.21.10 | 553 / 553 | 836 / 836 | 0 | 4 | 全 0 |
| 1.21.11 | 570 / 570 | 874 / 874 | 0 | 4 | 全 0 |

真机:八个版本各用修好的 jar 重跑探针(`test-downloads\probe-1.21-aa.ps1`),
`Resource not found: minecraft:post_effect/fxaa_of_*` **全部为 0 条**,全部进到标题界面。

八个产物各自的字节数与 SHA-256 见对应版本的 `release/notes/mc<版本>.md`。

## 1.1.1+mc1.21.11 — 只覆盖 1.21.11 的一版(抗锯齿后处理链)

> **为什么只跳一格、而且只覆盖一个 MC 版本**:1.21.x 这条线是"一份源码、每个 MC 版本一个 jar",每个 jar 的版本号
> 描述的是**它自己那份产物的内容**(SemVer §3:已发布的版本号不能改内容,只能发新版本)。这次的行为改动只发生在
> 1.21.11 上(判据按版本取值),其余九个 1.1.0 的 jar 内容没变、不需要重建,所以只有 1.21.11 从 1.1.0 升到 1.1.1,
> `v1.1.0` 那个发布条目原样不动。逐 MC 版本升版只用一条命令,见 [`docs/VERSIONING.md`](docs/VERSIONING.md)。

**修复:切换光影包时提示"重载资源失败"**(`Resource not found: minecraft:post_effect/fxaa_of_2x.json`,
可能还带 `Could not find post chain with id: minecraft:fxaa_of_2x`)。

根因在**我们自己那条抗锯齿修复**上:1.21.8 起的 OptiFine 构建不再带老位置的
`assets/minecraft/shaders/post/fxaa_of_2x.json`,于是管线按老办法**补写**它、并把游戏新位置的
`assets/minecraft/post_effect/fxaa_of_2x.json` **删掉**(1.21.6–1.21.10 上 OptiFine 确实读老位置的那条链,
实测可用)。但 1.21.11 的后处理链已改由**游戏自己的加载器**解析 —— 它按 `minecraft:fxaa_of_2x` 去
`post_effect/` 取文件,而那个文件被我们删了:每次资源重载日志里都留一条 `Resource not found`,
需要这条链时(开抗锯齿 / 选光影包)直接失败并弹窗。

修法:1.21.11 上**原样保留** OptiFine 自带的新位置文件 —— 不补写老位置的那个,也不删它
(`OptifinePostChainFixer.fix(jar, keepGameChains)` 的新分支);1.21.6–1.21.10 保持老做法不变。
这个版本列表是**逐版本实测**得出的(静态特征不可用:每个 1.21.x 原版 jar 里都只有 `post_effect/` 一种位置,
OptiFine 的类里也没有路径字面量),依据写在 `OptifineSetup.POST_EFFECT_RELEASES` 旁边。
缓存格式号同时升到 `25`(管线产物变了),升级后首次启动会重建 `.optifine/`。

**离线校验**(`powershell -File test-downloads\verify-version.ps1 -Version 1.21.11 -ModVersion 1.1.1`):
被补丁的游戏类 **570 / 570**、OptiFine 自身的类 **874 / 874** 通过 JVM 校验,ASM 数据流验证器 **0 问题**;
扫描器:注入点 4 条(全部属于**已停用**的 indigo)、mixin 成员引用缺失 0、契约/覆写/引用 0/0/0、invokedynamic 句柄 0 悬空。

**真机确认**(2026-09-13,把 1.1.0 换成 1.1.1 并清空 `.optifine/`):启动、资源重载、切光影包、开关抗锯齿都正常;
1.1.0 上每次资源重载都出现的 `Resource not found: minecraft:post_effect/fxaa_of_2x.json` 与
`Failed to load post chain: minecraft:fxaa_of_2x` **都不再出现**,整轮 `[ERROR]` 0 条。

产物:`OptiFabric-1.1.1+mc1.21.11.jar` — 876446 字节
`SHA-256: 9E78C98FC0FC568C453ACA880FE167545192021D16C4A2DA0E4127AC5F3A9143`

> 这一版的范围判断("问题只在 1.21.11")与上面"1.21.6–1.21.10 读老位置、实测可用"那句是**错的**,1.1.2 已经纠正;
> 这一节保留原样,作为当时的记录。已发布的 `1.1.1+mc1.21.11` 内容不变(§3)。

## 1.0.0+mc1.21 … 1.0.0+mc1.21.11 — 1.21.x 全系列

**一份源码,覆盖 OptiFine 出过 1.21.x 构建的全部 10 个版本**,每个版本一个 jar:

```
.\gradlew build "-Pmc=1.21.8"     →  OptiFabric-1.0.0+mc1.21.8.jar
```

（PowerShell 里必须给参数加引号,否则 `1.21.8` 会被拆成 `1`。不带参数则构建 `gradle.properties` 里的默认版本。）

每个 jar 都绑定了**自己那个版本**的 `official→intermediary` 映射表(官方混淆名每版不同,用错版本会把 OptiFine 重映射成乱码),`fabric.mod.json` 里的 `minecraft` 依赖也精确到该版本。

### 各版本与实测搭配

| MC | 产出的 jar | OptiFine 构建 | 建议 Fabric API |
|---|---|---|---|
| 1.21 | `OptiFabric-1.0.0+mc1.21.jar` | `preview_OptiFine_1.21_HD_U_J1_pre9.jar`(只有 preview) | 0.99.5+1.21 |
| 1.21.1 | `OptiFabric-1.0.0+mc1.21.1.jar` | **`OptiFine_1.21.1_HD_U_J1.jar`** | 0.116.9+1.21.1 |
| 1.21.3 | `OptiFabric-1.0.0+mc1.21.3.jar` | **`OptiFine_1.21.3_HD_U_J2.jar`** | 0.114.1+1.21.3 |
| 1.21.4 | `OptiFabric-1.0.0+mc1.21.4.jar` | **`OptiFine_1.21.4_HD_U_J3.jar`** | 0.119.4+1.21.4 |
| 1.21.6 | `OptiFabric-1.0.0+mc1.21.6.jar` | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` | 0.128.2+1.21.6 |
| 1.21.7 | `OptiFabric-1.0.0+mc1.21.7.jar` | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` | 0.129.0+1.21.7 |
| 1.21.8 | `OptiFabric-1.0.0+mc1.21.8.jar` | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` | 0.136.1+1.21.8 |
| 1.21.9 | `OptiFabric-1.0.0+mc1.21.9.jar` | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` | 0.134.1+1.21.9 |
| 1.21.10 | `OptiFabric-1.0.0+mc1.21.10.jar` | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` | 0.138.4+1.21.10 |
| 1.21.11 | `OptiFabric-1.0.0+mc1.21.11.jar` | **`OptiFine_1.21.11_HD_U_J9.jar`** | 0.141.6+1.21.11 |

（OptiFine 没出过 1.21.2 / 1.21.5 的构建,所以这两版没有对应 jar。）

### 为什么一套源码够用,以及它需要什么

- **intermediary id 在 1.21.x 各版本之间是稳定的** —— 对比 1.21.10 与 1.21.11 的映射,本移植用到的类/方法 id 两边都在。fixer 全部以 intermediary 名义注册,于是能跨版本复用。
- fixer 的行为是"找到就修、找不到就跳过",而且 `RestoreVanillaMethodsFix` 这类读的是**当前版本游戏 jar 里的原版字节码**,不存在"把 1.21.11 的方法体塞进 1.21.4"的问题。版本差异退化为"某些 fixer 在某版本不触发"。
- 但**写死描述符的 fixer 会在别的版本上静默失效**:`class_761.method_62210` 在 1.21.8 收一个 `Camera`、在 1.21.11 收一个 `Vec3d`。`StubInjectionTargetFix` 与 `CallSiteRedirectFix` 因此改成**按方法名匹配、描述符可选**,并用"实际找到的那个方法"的描述符去改调用点 —— 否则 1.21.8 上的方块描边钩子会落回活代码,而它要的注入点在 OptiFine 重编译后的方法体里并不存在(真机必崩)。
- 同为"让不兼容的钩子注入进死代码"那招,**死代码副本现在用原版方法体**(Mixin 本来就是照着原版写的,OptiFine 会把方法体内的调用改写掉;副本是死代码,身体只需要"长得像原版")。
- 版本特有的成员缺失(仅 ≤1.21.5)由 `RestoreVanillaMethodsFix` 补:`class_1088.method_65737`(1.21.4)/`method_61072`(1.21.1)、`class_329.method_55806/55807/55808`、`class_309.method_1454`。
- `KeyboardFix`(1.20.6 线上用过)重新启用并**改成容错**:1.21/1.21.1 还有 `method_1454`(OptiFine 的构建把它丢了,而 fabric-screen-api-v1 往它里面注入),1.21.6 以后根本没有这个方法。上游找不到方法时**直接抛异常**,改成"只 revert 该版本确实存在的那些"之后,同一个 fixer 能同时服务 1.21 到 1.21.11。

### 验证(每个版本都跑完整离线链路)

| MC | 补丁类(JVM+ASM) | OptiFine 类(JVM+ASM) | @At 注入点 | mixin 成员引用 | 契约/覆写/引用 | invokedynamic 句柄 |
|---|---|---|---|---|---|---|
| 1.21 | 440/440 | 773/773 | 3(全是已停用 indigo) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.1 | 425/425 | 783/783 | 2(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.3 | 440/440 | 816/816 | 2(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.4 | 474/474 | 812/812 | 2(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.6 | 487/487 | 820/820 | 4(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.7 | 500/500 | 823/823 | 4(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.8 | 516/516 | 831/831 | 4(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.9 | 519/519 | 832/832 | 4(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.10 | 553/553 | 836/836 | 4(同上) | 0 缺失 | 0/0/0 | 0 悬空 |
| 1.21.11 | 570/570 | 874/874 | 4(同上) | 0 缺失 | 0/0/0 | 0 悬空 |

新增扫描器 **`LambdaScan`**:逐个检查补丁类里 `invokedynamic` 的 bootstrap 方法/字段句柄是否指向"游戏真正会加载的那份类"里还存在的方法。这是之前唯一没人查的一环 —— JVM 与 ASM 验证器都**不解析** invokedynamic 目标(它们只在首次执行时才链接,也就是在游戏里)。逐版本结果 **0 悬空**。

每个版本的验证都能一条命令重跑:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-version.ps1 -Version 1.21.8
```

真机验证只有 **1.21.11 完成**(启动、主界面、单人、多人、方块/区块/物品渲染、光影、F3,`[ERROR]` 0 条)。其余 9 个版本已通过全部离线校验并装机实测过,结果是:

- **1.21.3 / 1.21.8** 崩在 `VerifyError: Bad type on operand stack in putfield`(`EntityRenderDispatcher.renderHitbox`、`ShoulderParrotFeatureRenderer.render`)。根因不在 fixer 上,而在管线自己:**未被任何 fixer 改动**的类也被重算栈帧(`MissingOverrideFix` 是全局的,于是全部 ~500 个补丁类都走了 `COMPUTE_FRAMES`),合并分支类型时退化成 `java/lang/Object`,游戏拒绝加载该类。现在这类类原样保留 OptiFine(经 tiny-remapper)的栈帧,只有真被改动的类才重算;`getCommonSuperClass` 退化时会打日志,验证器也不再依赖类加载顺序。**已修,待复测。**
- **1.21 / 1.21.4** 崩在 `RuntimeException: Mixin transformation of net.minecraft.class_X failed`。Mixin 不把原因写进 `latest.log`,从注入点倒推出来是**注入点本身被改掉了**:
  - 1.21 的 `class_5944`(ShaderProgram):`DelegatingConstructorFix` 内联 OptiFine 的委托构造函数时,照抄了 OptiFine 的 `new Identifier(name)`,而原版构造函数用的是 `Identifier.ofVanilla(name)` —— Fabric API 的 `@WrapOperation` 包的正是后者。现在内联时先问原版类怎么转换,有静态工厂就照抄。**已修,待复测。**
  - 1.21.4 的 `class_329`(InGameHud):OptiFine 把构造函数里三条 layer 方法引用(`this::method_55806/55807/55808`)改写成了 `lambda$new$0/1/2`;那一版 Fabric API 的 `LayerInjectionPoint` 是**按引导方法句柄**匹配注入点的,句柄对不上就等于注入点不存在。新增 `LambdaMethodRefFix`,把这些 lambda 改名回游戏使用的方法名(保留 OptiFine 的身体,它在准星那一层比原版多了 QuickInfo)。**已修,待复测。**

- **1.21.6 / 1.21.7 / 1.21.9** 的"光影没有任何效果",三个原因都定位到了(见下),其中两个已在本项目里修好;
- **1.21.8 / 1.21.10** 光影加载成功但渲染不对:光影包 `photon_v1.2a.zip` 使用了 OptiFine 不认识的程序名(`gbuffers_entities/particles/block_translucent`、`gbuffers_all_translucent` 与 Distant Horizons 的 `dh_terrain`/`dh_water`),OptiFine 只报 `Invalid program name` 并跳过这些 pass。**这属于光影包与 OptiFine 不匹配,不是补丁能修的**(换 OptiFine 专用包即可验证)。

**第二轮复测(同日 12:03–12:11)后又修掉的三处**:

- **1.21 / 1.21.3 / 1.21.4 进世界十几秒后崩**(`NullPointerException: Cannot invoke "net.optifine.override.ChunkCacheOF.renderStart()" because "regionIn" is null`):`RegionSectionPosFix` 在 1.21–1.21.4 上没生效 —— 那些版本的 region 构建器收的是 `ChunkSectionPos` 对象,1.21.6 起才是打包 long,fixer 只处理后者就整段跳过,于是 region 用原版构造器建出来、内部的 `ChunkCacheOF` 为 null。现在两种形状都支持。**已修,待复测。**
- **1.21.1 崩在 `Mixin transformation of net.minecraft.class_5944 failed`**:与 1.21 同一处注入点,但 OptiFine 在那里用的是**静态工厂**委托(`this(provider, Identifier.ofVanilla(id), type)`),内联 fixer 原来只认 `new Identifier(...)`,于是注入点留在 `this()` 之前(Mixin 拒绝实例 handler 落在 `super()` 前)。两种形状现在都识别。**已修,待复测。**
- **1.21.6 / 1.21.7 光影完全不加载**:OptiFine 那两个预览构建(`J6_pre3`、`J6_pre7`)的 `Shaders.loadShaderPack()` 在检查之前写死了 `cancelled = true`(1.21.8 起是读取检查结果),于是 `getShaderPack()` 永远不会被调用,选任何光影包都是 `[Shaders] No shaderpack loaded.`。新增的 `OptifineJarFixer` 在映射后的 jar 上删掉那两条指令;**同一组件还修好了 1.21.9**:OptiFine 自带的 `post_effect/fxaa_of_{2,4}x.json` 把 `minecraft:post/blit` 当顶点着色器,而 1.21.9 起游戏只有 `post/blit.fsh`(顶点阶段是 `core/screenquad`),后处理管线编译失败会连带光影初始化失败;顺带把 1.21.6 / 1.21.7 那份用了 `"program"` 键(那两版的解析器只认 `vertex_shader`/`fragment_shader`)的 JSON 一并改写。**已修,待复测。**

逐条推导、日志证据与复跑口径见 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) 的"第二轮真机反馈"。

---

## 1.0.0+mc1.21.11 — 移植到 Minecraft 1.21.11

把 OptiFine 接进 Minecraft 1.21.11 的 Fabric,并处理掉 OptiFine 与 Fabric API 之间的全部已知结构冲突。
实测搭配:**OptiFine 1.21.11 HD_U J9**(build `20260205-175838`)+ **Fabric Loader 0.19.5** + **Fabric API 0.141.6+1.21.11**,运行于 Java 25。

### 功能

- 在 `preLaunch` 阶段对 OptiFine jar 自动执行:跑 `optifine.Patcher` 解包(1.21.11 的 OptiFine 换成 xdelta 差分包,用法不变)→ 去 volde 化 → official→intermediary 重映射 → 应用字节码修复 → 注入 Fabric Loader 的类变换器。
- 重映射时把**游戏 jar 一起放进重映射器的 classpath 与输入**。这是 1.21.11 移植最关键的一处修正:TinyRemapper 的成员映射按**声明类**记录,看不见游戏类层次时,子类里覆写的方法会留成 OptiFine 的名字(`class_1308` 一个类就漏了 35 个方法 → 281 处破坏的抽象契约、254 处丢失的虚方法覆写)。
- 结果缓存到 `<游戏目录>/.optifine/<OptiFine 版本>/`(含缓存格式版本号,当前 `13`),二次启动直接复用(1–2 秒)。
- 缺 OptiFine / jar 损坏 / 版本不匹配 / 放了多份 OptiFine / 内部错误时,标题界面弹错误对话框,并在崩溃报告里追加 `OptiFabric` 一节。
- `-Doptifabric.mc-jar=<原版 client jar>` 可显式指定原版 jar;`-Doptifabric.extract=true` 会把重映射后的类解包出来便于排查。

### 与 Fabric API 的兼容修复(全部来自真机崩溃,逐个定位到字节码)

| 症状 | 根因 | 处理 |
|---|---|---|
| 启动崩 `Mixin transformation of net.minecraft.class_761 failed` | OptiFine 把 lambda 体重编译成 `lambda$addMainPass$1` 且**多一个参数**,Mixin 按名字+描述符找不到 `method_62214` | 恢复原版方法体(同类另有 `class_3898.method_60440`、`class_1092.method_65750`) |
| `class_1088` 的 `@WrapOperation` 找不到 `method_68018/68019` | 重编译后方法消失(同上) | 恢复原版方法体;另给 `class_775.method_3347` 补回被干掉的注入点(`InjectionCallPointFix`) |
| 启动崩 `@Local class_2338$class_2339` 校验失败 | 原版在 `ARETURN` 处作用域内有 `MutableBlockPos`,补丁后没有(MixinExtras 在**注入点**上判别局部变量) | `RestoreVanillaMethodsFix(true, "method_24225")` |
| 进世界 4 秒后崩 `ChunkCacheOF.renderStart() ... regionIn is null` | OptiFine 的区域构造器只有**六参数**版本会写 section 位置,恢复出的原版构建方法调用的是五参数那个 | `RegionSectionPosFix`:改调六参数构造器,用该方法本就收到的打包 long 生成第六个参数 |
| 进世界 4 秒后崩 `WorldRenderContextImpl.worldState()` 为 null | Fabric 的方块描边钩子读的上下文由 `LevelRenderer.method_22710` 头部准备,而 OptiFine 用 `RenderPass` + 自己的 lambda 替换了那条流程 | `StubInjectionTargetFix`:钩子目标改名并留同名副本,让注入落在**没人调用**的代码上(代价:`BEFORE_BLOCK_OUTLINE` 事件不再触发,描边照画) |
| **所有物品贴图丢失** | `class_10430.method_65584` 上的 `@Inject(at = RETURN)` 需要重编译后不再存在的局部变量 → **每个**物品模型都烘焙失败 | `RestoreVanillaMethodsFix(true, "method_65584")` |
| 进多人服务器约 30 秒崩 `Attempted to retrieve active rendering plug-in before one was registered` | 同上那招对**移动方块**钩子失效:它的调用者在**另一个类**(`class_11684.method_73002`)里,按名字调到的是被注入的副本 | `CallSiteRedirectFix`:调用方一并接管,调用点改到改名后的真实方法体 |
| 一按 **F3** 就崩(同一异常) | 声明 `contains_renderer` 只是让 Indigo 退场;Fabric API 自己的 F3 调试条目仍要 `Renderer.get()`,而没有任何渲染器被注册 | `RendererApiFallback`:注册惰性占位渲染器(F3 显示 `Renderer: OptifineRendererPlaceholder`) |
| 启动即崩 `NoSuchMethodError: class_2680.getBlockStateBaseCacheClass()` | 上一项第一版在 preLaunch 调了 `Class.getMethods()` 读 Fabric 接口,而接口方法签名里全是游戏类型 → 这些类被**提前加载成原版**,整局都停在原版上(`class_2680`=BlockState 少了 OptiFine 加的方法) | 生成器改为 ASM 只读接口 class 文件(不解析任何类型);注册改用 `MethodHandles.findStatic`;注册时机挪到补丁类注入之后 |

此外还有一批**离线推导**出来的结构问题:合成字段 `this$0`/`val$…` 同名同类型时按声明顺序配对并改成模组可 shadow 的名字(`SyntheticFieldFix` 扩展);被 OptiFine 重编译掉的原版方法桥接(`MissingOverrideFix`);被换成 OptiFine 子类的对象创建(`ChunkOF`)补回惰性 `NEW` 标记;被读取但从未被赋值的字段(`UnsetFieldScan` 报 0)。

### 兼容性

- 需要 **Fabric Loader ≥ 0.19.5**、Java 21+(实测运行在 Java 25)、客户端。
- 与 `sodium` 冲突(已声明);`no_fog`、`thallium`、`xradiation`、`ryoamiclights` 声明为不兼容。
- **不包含、也不分发 OptiFine 本体**;OptiFine 需自行获取后放进 `mods/`(1.21.11 有正式发布版,如 HD_U J9)。
- 依赖 FRAPI/indigo 的模组不再获得 indigo 的自定义渲染(地形交给 OptiFine);Fabric 的渲染器 API 由占位实现顶着。
- 两个 Fabric API 钩子被有意中和:`BEFORE_BLOCK_OUTLINE` 事件不再触发;移动方块的 FRAPI 渲染钩子失效(移动方块由原版路径渲染)。
- OptiFine 看不到 Fabric 模组内部的资源(它不认 Fabric 的资源包类型)。

### 验证

- 离线字节码校验(与游戏一致的单一加载器):被补丁的 **570 / 570** 个游戏类通过 JVM 校验,OptiFine 自身的 **874 / 874** 个类通过,ASM 数据流验证器 **0 问题**。
- 扫描器:mixin 成员引用缺失 **0**、破坏的抽象契约/丢失的虚方法覆写/无法解析的成员引用 **0/0/0**;`@At` 注入点仅剩 4 条,全部属于**已被停用**的 indigo。
- 真机(final session):启动、主界面、单人世界、**多人服务器**、方块/区块/物品渲染、光影(`ComplementaryReimagined` 加载成功)、F3 调试屏;`[ERROR]` 0 条、无崩溃报告。
- 过程、每一类的根因与修法、可复现工具见 [`docs/DEVELOPMENT.md`](../docs/DEVELOPMENT.md)。

---

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
