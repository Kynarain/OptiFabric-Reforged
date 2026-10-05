# 更新日志

## 2.2.9+mc1.21 … 2.2.9+mc1.21.11 - carryon 在 OptiFabric + OptiFine 下能用了：把移走的那一处粒子绘制调用瞄回游戏自己的方法

**Carry On 在 OptiFabric + OptiFine 下能用了。** 1.21.1 + `carryon-fabric-1.21.1-2.2.6.13.jar`
(sha256 `76b57c1b25a0…`)此前在标题界面之前就死;本版之后实测到**标题界面**(32 s,日志 0 条 `/ERROR`,
七项计数器全 0)并且**进了世界**(预置存档 `level.dat` 2,302 → 2,308 B、SHA-256 改变、四个 region 文件被改写;
日志里有 `Starting integrated minecraft server version 1.21.1` 与 `logged in with entity id 70`)。
**这是我们的漏洞,不是 OptiFine 与 Carry On 的冲突。** 报错出在**我们服务出去的那个类**上:OptiFine 把 `class_761`
里那条对 `class_702.method_3049(...)V` 的调用改瞄到了它自己多一个参数的 `render(..., Frustum)V` 上,而 Carry On 的
refmap 要的正是那条原版调用。既不是 OptiFine 的缺陷,也不是 Carry On 的缺陷 —— 是**我们的移植**在 OptiFine 重编译之后
没有把这条调用点还回去;任何模组只要也用这条调用点就会撞上同一堵墙,所以修在我们这边。

**失败长什么样。** Carry On 的 `LevelRendererMixin.onRenderLevel` 是一条 `@Inject`,refmap 把它的注入点解析成
`class_761.method_22710`(`renderLevel`)里**对 `class_702.method_3049(Lclass_765;Lclass_4184;F)V` 的一次调用**。
游戏自己的 `class_761` 里有两次这样的调用,**OptiFine 补丁后的 `class_761` 一次都没有** —— 它调的是自己新增的
`class_702.render(Lclass_765;Lclass_4184;FLclass_4604;)V`,**接收者与前三个参数逐字节相同**,只多了一个参数(相机视锥)。
于是 Mixin 扫到 0 个目标,而 `carryon.fabric.mixins.json` 的 `defaultRequire = 1` 让整个类失败:

```
InjectionError: Critical injection failure: Callback method onRenderLevel(...)V in
carryon.fabric.mixins.json:LevelRendererMixin from mod carryon failed injection check, (0/1) succeeded.
Scanned 0 target(s). Using refmap carryon.refmap.json
```

它在 OptiFine 自己的 `Reflector` 引导期间以 `Mixin transformation of net.minecraft.class_761 failed` 的面目出现,
客户端在标题界面之前就退出。

**修法。** 新 fixer `RestoreVanillaCallFix`,**只登记 `class_761` + `method_22710`**:把这次调用**瞄回游戏自己调的那个方法**。
OptiFine 的 `class_702.method_3049` 仍在补丁后的类上(它自己把方法体换成了六条指令的转发器 `render(..., null)`),
所以 mixin 要的调用点真的存在、而且**每帧只执行一次**,位置和原版一样。多出来的视锥参数用一条 `NOP` 顶掉
(不删指令,指令表、偏移、标签与栈映射帧都不动);视锥在 OptiFine 的 `render` 里只有一处用处(逐粒子视锥剔除),
传 null 正是 OptiFine 自己那条转发器做的事。OptiFine 的方法体、着色器阶段(`Shaders.beginParticles` /
`endParticles`)、`AFTER_PARTICLES` 分发以及 `renderLevel` 的其余部分一律未动。补丁后的 `class_761.method_22710` 里
`method_3049` 的调用点是**三个**(OptiFine 那三个分别在不同分支里:第一个被 `goto` 跳过,另两个是
`Shaders.isParticlesBeforeDeferred()` 的两支),所以每帧仍然只有一次,和原版那两次(同样互斥)一样。

**代价(必须一起说)。** 改瞄之后,**这一次调用不再走 OptiFine 的逐粒子视锥剔除**(视锥参数被顶成 `null`,
OptiFine 自己的 `method_3049` 转发器就是这么做的):视锥外的粒子仍然会被提交。这是**纯性能差异,不是正确性问题**
(屏幕外的几何体不落像素);凡是经原版 API 调进 OptiFine 的模组拿到的都是这个行为。

**为什么不用既有的 `InjectionCallPointFix`。** 那个 fixer 是在方法体前面**补一次调用**、并丢弃返回值,前提是它包的调用是
**纯取值**:这里包的是一次**绘制**,补出来的调用会让粒子 pass 跑第二遍(所有粒子渲染类型都设 `depthMask(true)`),
而且那个 fixer 把调用插在 `instructions.getFirst()`,会把 mixin 的处理器挪到整帧绘制之前。两个都不是可接受的行为变化,
所以这次是**把已有调用改瞄**,不是补一次新调用。

**验证。** 先在**私有 `.optifine` 缓存副本**上做:改服务出的 `class_761`、重算 CRC、普通启动 → 标题界面 11.5 s、
七项计数器全 0;然后才落代码、重编、用重编出来的 jar 再跑一遍:标题界面 32 s(0 `/ERROR`)、进世界(服务器 25.7 s、
`logged in with entity id` 27.8 s)、`-Dmixin.debug.export=true` 导出 JVM 实际拿到的类,里面有
`handler$zzc000$carryon$onRenderLevel` 紧贴在**改瞄后的** `class_702.method_3049` 调用之前,该处理器无条件调用
Carry On 自己的 `CarriedObjectRender.drawThirdPerson`。回归:1.21.1 + ShoulderSurfing 标题界面 17.9 s、
1.21.6 与 1.21.8 标题界面、暮色森林那条臂都过。
(仍未由机器验证的一点:把方块/实体**实际抱起来**要人在键盘前操作,见报告。)

**边界(别把这一版读大)。** 上面每一次都只启动**一次**,一个实例副本、一个新建的 `.optifine` 缓存,`-Xmx2048M`,
**没有光影包、没有压测、没有长时间游玩、没有多人**;失败计数器(`/ERROR`、`Cannot @Coerce`、
`InvalidInjectionException`、`Mixin apply … failed`、`Mixin transformation … failed`、`LVTGeneratorError`、
`SugarApplicationException`)在这几条臂里逐条为 0。**进世界那一条只量了 1.21.1**:这十个版本里只有 1.21.1 做了
「抱 Carry On 的 jar 进世界」,其余九版只做了标题界面启动与上面那几条回归,它们**没有**被单独量过。
**Carry On 自己的功能在这条通道上无法由机器验证**:把方块或实体真的抱起来是**键盘/鼠标交互**(要瞄准、要按键),
所以本版只声称「注入点回来了、处理器挂上了、客户端在世界里跑帧不报错」,**不声称**「抱起一个方块」已经验过 ——
那一步要人在键盘前做:进世界、看着一个方块、按 Carry On 的抱起键,确认方块被抱住并在第三人称里画出来,再看开它。
另外,`restorevanillacallfix` 对 **1.21.6 与 1.21.8 是 no-op**(那两版的 OptiFine 用 `class_702.renderParticles`
换掉了另一处调用),两版的标题界面启动结果与 2.2.8 相同。

---

## 2.2.8+mc1.21 … 2.2.8+mc1.21.11 — 按平台要求移除运行时下载与进程启动;改为手动安装 + 2.1.0 的提示对话框,并同时提供 GitHub-only 的 `-full` 构建

> 这一版覆盖全部 10 个产物。**没有任何修复逻辑被改动**:补丁管线、`LocalSlotLayoutFix`、`AddInterfaceFix`、
> `InjectionCallPointFix`、`LambdaMethodRefFix`、`SyntheticFieldFix`、`RestoreVanillaMethodsFix`、
> `VanillaFactoryCallFix`、`ImplicitDiscriminatorMaskFix`、mod id、支持表与提示规则全部与 2.2.7 一致;**唯一**
> 例外是下面写明的 `class_5944` 补完(2.2.7 那一处「只修到一半」)。

### 为什么改:平台的审核意见(原文与译文)

CurseForge 的审核**正是**因为这两点拒收了提交,原文:

> 该代码在运行时从外部来源下载 jar 文件,并通过 Windows 内核进程调用重新启动游戏,这可能存在安全风险。请移除运行时下载和进程启动功能。

译文:The code downloads a jar file from an external source at runtime and restarts the game through a
Windows kernel process call; this may pose a security risk. Remove the runtime download and the
process-spawning functionality.

这是**平台规则**,不是我们自己的取舍:上架到 CurseForge / Modrinth 的那一份产物不能有运行时下载,也不能启动进程。
所以本版把这两件事从**默认产物**(上架用的、没有后缀的那一个)里彻底删掉,并新增一条只放在 GitHub 上的
`-full` 构建保留这两个便利功能(见下)。

**这一版还带着一条 Twilight Forest 的最新测量**,与上面的 `class_5944` 补完一起写在这里:2.2.8 拿掉了启动崩溃、
把 TF 送到标题界面 (**1.21.1**,`twilightforest-fabric-1.21.1-4.8.734` + `fabric-api-0.116.17+1.21.1` + 本 jar +
`OptiFine_1.21.1_HD_U_J1`),而且同一套 jar 上一轮专门的调查(**11 个带 TF 的臂**)量到它**确实能进世界,但是间歇性的**:TF 那一套 **11 个臂
里进了 4 个**(入场 46.7 s / 约 50 s / 约 50 s / **122.7 s**),同一台机器上不带 TF 的对照 **3/3** 进了世界。失败的那些臂
**没有任何异常**,停在世界数据包读完、集成服务端启动之前,Render thread 闲在标题界面的帧循环里(自建的
`-javaagent` 探针:零条未捕获 throwable、没有死锁、`Server thread` 从未被创建)—— 是**一处「世界打开的交接」被丢掉,不是本模组的
代码**。**别急着判它卡死,也别忘了重试**(四次成功里有两次发生在一次失败之后,而最慢的那次是标题界面后 **122.7 s** 才进场;
`tf-fps` 那次专门测量量到的入场时间是 **20.4 s 到 152.6 s**,`Loaded 1944 advancements` 到 biome 行的空档是
**2 s 到 135 s**):给世界加载 **至少两到三分钟** 再下结论(**这是下限,不是经验上限**);真没打开就关掉客户端再启动一次,
失败时存档不会坏(**没有东西要修**:`level.dat` 逐字节未动,连修改时间都一样)。另外:**「不要压帧率」这一条本轮收回** ——
测量里它是无效的(窗口最小化时 `maxFps:5` 2/9 对 `maxFps:260` 3/10,两侧 Fisher 精确检验 **p = 1.00**),
真正相关的是**窗口可见性**:窗口最小化 **5/19** 进世界、窗口在屏幕上 **13/18**(固定 `maxFps:260`、只把窗口从最小化
改成可见并置前,3/10 → 8/9,**p = 0.0198**;固定窗口可见、只把 `maxFps` 从 5 改到 260,5/9 → 8/9,p = 0.29),
所以请**在加载世界期间让 Minecraft 窗口保持可见(不要最小化,最好放在最前面)**;**「优先从标题界面点开世界」既不成立
也没被推翻**(那次测量只跑了 `--quickPlaySingleplayer`,标题界面那条路没有量过)。那一组样本很小:
只有 19 个最小化臂与 18 个可见臂,只跑 1.21.1、只有一个预置存档,没有光影、没有世界内压测。
**边界**:那一组只有 **11 个带 TF 的臂和 3 个对照臂**,没有光影包、没有世界内压测、
没有多人、没有进 TF 维度,只有一个预置存档,而且只跑了 1.21.1 —— 所以**进世界是间歇性的,本版没有修它,也不声称
修了它**;`release/notes/mc<版本>.md` 里有同一段更细的写法。

### 唯一的修复逻辑改动:`class_5944` 的那一处补完(ShaderProgram,影响所有注册核心着色器的模组)

> 2.2.7 的「只修到一半的那一处」在这里补完。这是本版**唯一**改动补丁逻辑的地方,其余 fixer 与支持表逐字未动。

`DelegatingConstructorFix.parameterState()` 现在会把游戏自己构造函数里、从 String 参数发出的 `PUTFIELD` **重放**在
重建出来的调用**前面**,于是 Fabric API 的 `ShaderProgramMixin` 拿到的不再是 `null`:

- **症状**:Fabric API 的 `ShaderProgramMixin` 包住 `ShaderProgram` 里的 `Identifier.ofVanilla`,`modifyId` 处理器随后用
  **被 shadow 的 name 字段**去构造 Identifier(`FabricShaderProgram.rewriteAsId(id, this.field_29494)` → `Identifier.of(containedId)`)。
  游戏自己的构造函数在 `ofVanilla` 调用**之前**就把那个字段从 String 参数存好了,所以处理器总能看到值;
  OptiFine 重编译后的 `class_5944` 没有这个存储 —— 它的 String 重载委托给 `(provider, Identifier, format)` 构造函数,
  字段由 identifier 派生。`DelegatingConstructorFix` 把那具身体内联进 String 重载、并在 `super()` 之后立刻用游戏自己的
  工厂建 identifier,**恰好就是被 wrap 的那次调用**;内联后的形状里字段唯一的存储点在调用**之后**,于是处理器拿到 `null`,
  `Identifier.of(null)` 在第一次资源重载时就抛;
- **后果**:客户端**把每一个资源包都丢掉**,quick-play 请求也永远不会被受理。它影响的是**任何**通过
  `CoreShaderRegistrationCallback` 注册核心着色器的模组 —— PortingLib 的 `rendertype_entity_unlit_translucent`
  (在 Twilight Forest 4.8.734 里)只是第一个跑到那儿的;
- **修法**:把游戏自己的那几条存储语句重放到重建调用之前。处理器那次 rewrite 于是退化成恒等,而方法体自己派生字段与
  着色器位置的那一半不变(对同一个 Identifier 两者都得到 `namespace:path`);
- **实测**(1.21.1,普通启动):`stringIn` NPE **2 → 0**、`CompletionException` **1 → 0**、资源包被丢掉 **1 → 0**。

### 移除了什么

- **删掉 `OptifineDownloader`**:HTTP 客户端(`java.net.http.HttpClient`)、OptiFine 官网两步下载、把下载到的
  jar 写进 `mods/`、JNA 的 `CreateProcessW`(Windows)与非 Windows 的 `ProcessHandle`/`ProcessBuilder` 重启、
  以及 `-Doptifabric.optifineDownloadTest=...` 自测入口,全部随这个类一起消失;
- **`build.gradle` 里的 JNA 依赖删掉**:不再加载任何本地库;
- **`Util.getOperatingSystem().open(...)` 删掉**(Windows 上它就是 `ShellExecute`,属于启动进程):标题界面错误
  对话框的「打开 Mod 文件夹 / 打开帮助 / 打开 issues / 打开日志」改为**把链接或路径复制到游戏自己的剪贴板**
  (`client.keyboard.setClipboard`);
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

### 合规声明(逐 jar 机器核对)

> This build neither downloads anything nor starts any process at runtime. `java.net.URL`/`URLClassLoader` are
> used only to read a local `file:` jar that the user placed in `mods/`; there is no HTTP client, no
> `ProcessBuilder`/`ProcessHandle`/`Desktop`/`CreateProcess`, and no `com.sun.jna`.

每个**上架 jar** 的下载 / 进程 / 启动 token 都是 **0**(HTTP 客户端、`Socket`/`InetAddress`/`URLConnection`/
`openStream`、`ProcessBuilder`、`ProcessHandle`、`java/lang/Process`、`Desktop`、`CreateProcess`、`ShellExecute`、
`com.sun.jna` 全部为 0);`-full` 那些 jar 是这次扫描的**正对照** —— 它们确实带着下载器(JNA 的 `CreateProcessW`、
`ProcessBuilder`、`ProcessHandle`、`com.sun.jna`)。逐 jar 表见 `cf-resume\cp-table.md`。

`java.net.URL` / `java.net.URLClassLoader` 两个 token 在**两种产物里都有**,这正是声明里允许的那一条:它们只用于读取
用户自己放进 `mods/` 的本地 `file:` jar。

**`System.exit` 那一行要按方法看,不能按 token 看**:`System/exit` 这个 token 在 class 文件里**根本不存在** —— 一次调用
是常量池里 `java/lang/System` 加 `exit:(I)V` 的 Methodref,所以早先按这个 token 扫出来的「0」是**选错 token 的假象**。
用 `javap` 逐方法解析后,每个上架 jar 的退出原语只有这两处:`patcher/LambdaRebuilder#main` 的 `System.exit(1)`
(那个类的离线命令行入口,只有直接运行它才会走到),以及 reforged 线 `mod/C2meCompat#apply` 的 `System.exit(0)`
(垫片自己结束本次启动,**不启动任何进程**、不联网)。上架 jar **没有 `Runtime.halt`**;`Runtime.halt(0)` 只出现在
`-full` 的下载器与 `-full` 的垫片里。
### 两条产物:默认(上架)与 `-full`(仅 GitHub)

| 产物 | 内容 | 去处 |
|---|---|---|
| `OptiFabric-2.2.9+mc<版本>.jar` | **无**运行时下载、**无**任何进程启动/重启 | CurseForge / Modrinth / GitHub |
| `OptiFabric-2.2.9+mc<版本>-full.jar` | 保留自动下载(只从 optifine.net)与自动重启 | **仅** GitHub |

两者 **mod id 相同**,所以配置与世界通用;**只能装一个**。`-full` 是「同一版修复 + 那两个便利功能」,
不是绕过审核:上架的那一份确实没有这两项能力。

### 没有改什么(可复核)

- 提示规则逐字未动:没有 OptiFine → **每次启动**都提示;装了更老的 preview → **每个构建提示一次**;
  任何 final 构建 → **从不提示**;
- 支持表(`OptifineSupport.BUILDS`)与十个 MC 版本的 OptiFine 构建名未动;
- `LocalSlotLayoutFix` / `AddInterfaceFix` / `InjectionCallPointFix` / `LambdaMethodRefFix` / `SyntheticFieldFix` /
  `RestoreVanillaMethodsFix` / `VanillaFactoryCallFix` / `ImplicitDiscriminatorMaskFix` 未动;
- 已知限制(1.21.6 / 1.21.7 的光影崩溃、sodium 冲突声明等)原样保留。

## 2.2.7+mc1.21 … 2.2.7+mc1.21.11 — 把 Twilight Forest 那条链剩下的五处 OptiFine 重编译损失一起修掉:1.21.1 上的 TF 到标题界面了

> 这一版覆盖全部 10 个产物。它修的是一条**链**上剩下的五处损失(隐式判别符掩码 + 四处调用点),
> 改动集中在三个文件:`ImplicitDiscriminatorMaskFix.java`(新文件,+268)、`InjectionCallPointFix.java`(+64/−6)、
> `OptifineFixer.java`(+106)。没有改资源、映射或元数据。**修订号递增的原因是向下兼容的问题修正**:
> 不改对外的名字、不改注册方式、不改产物名。

### 先把一句必须说清楚的话写在前面:上一版没有带上这套修复

上一版 **2.2.6 的发布产物里并没有**这个判别符掩码,也没有 `class_761` 那一处调用点。那三个提交(`1f8377b`、
`b08241d`、`faa5ad4`)此前只以 **dangling object** 的形式存在于仓库里(在另一个私有克隆里做出来的),
**从来没有并进 `1.21.x`**。所以用已发布的 2.2.6 jar 普通启动 TF,**6.1 s 就死**(`CLIENT-DIED`),
日志里是 **12 条 `invalid IMPLICIT discriminator`**(以及 `Could not initialize class net.optifine.reflect.Reflector` ×2):

```
@At("STORE" implicit Iterator) has invalid IMPLICIT discriminator ... Found 2 candidate variables but exactly 1 is required
```

**本版是第一次真的带上这套恢复的版本**,不是"延续上一版"。2.2.6 那一节里"TF 仍然不支持"的结论,
对 2.2.6 自己仍然成立;上面 2.2.6 一节末尾那句"本版没有采纳 `ImplicitDiscriminatorMaskFix` 与 `class_761`
的 `InjectionCallPointFix`"也就是这个意思 —— **本版把这两处都采纳了**,并且额外修了三处。本版没有改写 2.2.6 的正文,
更正在这里。

### 链上的五处,按客户端撞上的顺序

1. **隐式判别符掩码 + `class_761` 的调用点**。Porting Lib 的 `porting_lib_blocks` `LevelRendererMixin` 用隐式
   `@ModifyVariable` 包住方块实体的 `Iterator`,判别符会把方法里从槽位 1 开始的每一个 `Iterator` 型局部变量都数进去;
   补丁后的类在那个切片的**十二个 store 上各有两个或三个**,于是每个注入点都被丢掉,整个类失败。候选**根本不在
   类文件的 LocalVariableTable 里**:它们是 Mixin 的 `Locals` 在真实表对那个槽位没有任何在范围内的表项时、
   从代码生成出来的表项(所以名字才叫 `var26`/`var29`,跟着槽位走),这也正是"改写已有表项"够不到它们的原因。
   同一个 mixin 的另一个处理器是包在 `BlockState.getLightEmission()` 上的 `@WrapOperation`,而 OptiFine 的
   `method_23793` 只剩一条**四条指令的转发器**(转给它自己的 `getPackedLightmapCoords`),
   从来没有调用 `class_2680.method_26213`;
2. **`class_776.method_3353`**(`BlockRenderDispatcher`):OptiFine 的 `method_3353` 是一条**十条指令的转发器**,
   转给它自己那个七参数的 `renderSingleBlock`,方法体和 `ItemBlockRenderTypes.getRenderType` 调用都在那边。
   `RestoreVanillaMethodsFix(true, "method_3353")` 把原版方法体放回去;这一步是**先在一份手工改过的补丁缓存上
   证过**才注册的(把 `method_3353` 换回原版、重算 CRC 后普通启动,`class_776` 那一处消失、运行多走了几秒,
   下一个失败变成 `class_778` 的);
3. **`class_778.method_3374`**(`ModelBlockRenderer`):同一个被丢掉的调用,再往后一个类。游戏在 `tesselateBlock`
   里第 17 条指令调用 `BlockState.getLightEmission()`,OptiFine 改走自己的 `LightCacheOF`/`RenderEnv` 路径,
   那个调用在整个类里**一次都不剩**。`InjectionCallPointFix` 把调用重建出来;
4. **`class_915.method_33434`**(`ItemFrameRenderer`):这里是**新形状** —— 调用不是被挪走,而是被**内联**了。
   游戏问的是 `ItemStack.is(Items.FILLED_MAP)`,OptiFine 编译成了 `getItem() instanceof MapItem`;调用和它的常量参数
   在方法里都不存在了。MixinExtras 没有针对类型判断的 `@At`,去劫持 `getItem()` 又会改掉游戏里每一个物品,所以
   诚实的修法还是重建那个调用,只是**参数要从静态字段读**:`InjectionCallPointFix` 为此长了一个
   `withArgumentField` 工厂(既有的构造函数原样保留)。重建序列是
   `ALOAD 2 / GETSTATIC class_1802.field_8204 / INVOKEVIRTUAL method_31574 / POP`,放在 mixin 那个分支读的
   `instanceof` **前面**;两种写法问的是同一个问题,所以被包住的值就是那个分支本来会算出来的值;
5. **`class_5944`**(`ShaderProgram`):OptiFine 把被委托的 `(class_5912, class_2960, class_293)` 构造函数
   **内联**进了 String 重载,于是它自己带了一条 `Identifier.of`(`method_60654`)调用,而游戏那里是
   `Identifier.ofVanilla`(`method_60656`)—— Fabric API 的 `ShaderProgramMixin` 包的正是后者。`VanillaFactoryCallFix`
   现在也跑在 `<init>` 上。**这条只修到一半,见下面的「只修到一半的那一处」。**

### 判别符掩码做了什么,如实写

它是**写进一个 debug 属性里的一处刻意的类型谎言**:给同一类型里除"要留下的那一个"之外的每个候选槽位,
**追加**一条覆盖该方法切片的 `Ljava/lang/Object;` 表项。JVM 校验器读的是 `StackMapTable`、`max_locals`、
异常表和代码,**从不读 LocalVariableTable**,所以类照常校验、照常运行;真正会看见这条表项的,只有读局部变量表的东西
—— 首先是 Mixin 自己的 `Locals`(这正是目的),其次是 MixinExtras 的 `@Local` 糖、调试器、agent 和字节码扫描器,
而且**只在注册时给的那个切片范围内**。

为什么是"追加"而不是"改写":Mixin 取局部变量的类型走 `Locals.getLocalVariableAt`,它先读随类发布的那张
LocalVariableTable,一旦某个槽位在范围内**没有**表项,就退回到 ASM 自己按数据流生成的那张表。有歧义的候选通常正是
生成表里的那些。而 `getLocalVariableAt` 按顺序扫描、**保留最后一个范围内命中的表项**,所以追加在末尾的这条会同时
压过发布表与生成表 —— 这也是它必须**从方法开头**开始覆盖、而不是只盖住切片的原因:槽位的表项是"走过建立它的那条
指令"时填上的,值在切片之前就建立好的槽位,即使表项盖住切片也仍然是 `Iterator`。它不动基本类型、不动参数槽位、
不新建也不删除局部变量,也不改代码、槽号、范围、名字或 `maxLocals`。它是**按(类、方法、捕获类型、切片)逐条注册的
可选项**:那个形状不在了(找不到切片两条边界、同类型局部变量不足两个、或者方法根本没有真实局部变量表),它什么都不做。

### 只修到一半的那一处(`class_5944`)

`VanillaFactoryCallFix("<init>")` 把"包错了工厂"改成了"包对了工厂",但**没有消掉资源包被丢掉这件事**:Fabric
自己的处理器仍然在**这条被重建出来的调用**上抛 NPE(`Cannot invoke "String.indexOf(int)" because "stringIn" is
null`,`class_2960.method_12838` ← `method_60654` ← `FabricShaderProgram.rewriteAsId`),因为 `allow = 1`
(没有 `require`)允许 MixinExtras 在无法捕获参数的情况下照样包上去。也就是说崩溃轨迹从"mixin 没应用"变成了
"mixin 应用了、它的处理器自己抛"。**这是本链唯一一处没有修完的地方,不致命**:客户端照常走到标题界面,
只是**一个资源包也不剩**。它**不是** TF 那一臂进不去世界的原因 —— **不带 TF 的那一套一个资源包也没有,却照常进
世界**(见下),而 1.21.6 / 1.21.8(那两版 `class_915` 与 `class_5944` 都不触发)连一条资源包错误都不打。

### 实测(普通启动,`-Xmx2048M`,没有任何 debug 开关)

TF `twilightforest-fabric-1.21.1-4.8.734.jar` + `fabric-api-0.116.17+1.21.1.jar` + 本 jar + OptiFine
`OptiFine_1.21.1_HD_U_J1.jar`,1.21.1 / Fabric Loader 0.19.5。**五个链上修复都在这份日志的标准输出里**
(`Masked java.util.Iterator local(s) …`、`Re-created the injection point … class_2680.method_26213()I in
… class_761.method_23793`、`Restored vanilla … class_776.method_3353(…)V`、`Re-created … in
… class_778.method_3374`、`Re-created … class_1799.method_31574(Lnet/minecraft/class_1792;)Z in
… class_915.method_33434`、`… class_5944: 1 factory call(s) aligned with the game`)。

| 计数 | 条数 |
| --- | --- |
| `/ERROR` | 0 |
| `Cannot @Coerce` | 0 |
| `InvalidInjectionException` | 0 |
| `Mixin apply … failed` | 0 |
| `Mixin transformation of … failed` | 0 |
| `LVTGeneratorError` | 0 |
| `SugarApplicationException` | 0 |
| `expected N invocation(s)` | 0 |
| `Minecraft has crashed` | 0 |
| `invalid IMPLICIT discriminator` | 0 |
| `[Server thread]` | 0 |

**这一版是"到标题界面",不是"能玩";而"挡住它的不是 TF"这句现在作废**:2.2.7 拿掉了启动崩溃、把 TF 送到标题界面,
但 **TF 能不能进世界已经量过了,答案是不能**。

先说那句作废的观测是怎么来的:**"同一套 OptiFabric + OptiFine 不装 TF 也一样停在标题界面之后"出自一个带 Mixin
debug 开关的台架**;debug 开关在这里会把"静默没生效的注入"变成致命错误,同一原因当天已经造成过一次撤回。
**普通启动的复核与它相反**:不带 TF 的那一套(`fabric-api 0.116.17+1.21.1` + 本 jar `2.2.7+mc1.21.1` +
`OptiFine_1.21.1_HD_U_J1`)**能进世界,4/4 次**(标题界面之后 25.7 / 29.0 / 32.2 s 入场,每一次 `level.dat` 的
修改时间都前移,日志里有 `Starting integrated minecraft server version` 与 `logged in with entity id`);
更早一次同样的裸装也是 3/3 进世界。所以**本版收回"挡住它的不是 TF"这个说法**。

带 TF 的那一套(上面那一串再加 `twilightforest-fabric-1.21.1-4.8.734`)在 2.2.7 上**到标题界面**(300 s 那次
46.8 s,600 s 那次 25.8 s),致命计数器**逐条为 0**(`Cannot @Coerce`、`InvalidInjectionException`、
`Mixin apply … failed`、`Mixin transformation of … failed`、`LVTGeneratorError`、`SugarApplicationException`、
`Minecraft has crashed`);那次复核的 `/ERROR` 只有一条,是环境的(`Failed reading REFMAP JSON … 'mapper' is
null`,forgeconfigapiport)—— 它在**不带 TF、照样进世界**的那一臂里也在。

**但 TF 没有进世界**,300 s 与 600 s 两次都没有:预置存档的 `level.dat` **一个字节都没动**(两次都是 2,299 B、
同一个修改时间、同一个 SHA-256),日志停在第一次资源重载结束的那一刻,此后整个窗口一个字都不再写。这不是推断:
对冻住的 600 s 客户端做过一次 `jstack`,**进程里根本没有 Server thread,也找不到任何 `MinecraftServer` /
`IntegratedServer` 帧**,Render thread 停在原版的标题界面帧循环里(`glfwWaitEventsTimeout ←
RenderSystem.limitDisplayFPS`)。也就是说客户端一直闲在标题界面,**那个存档从来没有被打开过**。这是一处
**进世界之前的空档**,不是"世界加载到一半卡住",也和本版别处记的那段"标题界面之后停住"(世界已经开着、停在
世界/区块那一侧)不是同一个形状。

**所以本版只能说到这儿**:2.2.7 修掉了启动崩溃、把 TF 送到标题界面;TF 能不能进世界现在已经量过了,答案是不能,
形状如上;**本版没有修它,也不声称修了它** —— 它是谁的责任、为什么不动,本轮**没有量**,这里一个字都不写。

### 边界(别把这一版读大)

- **每个臂只启动一次**,一个实例副本、一个预置存档,**没有光影包、没有压测、没有长时间游玩、没有多人**。
  `PASS` 的定义是**到标题界面**(日志出现 `Sound engine started`);只有明确写了「进世界」的行才声称进过世界,
  下面那张逐版本表里一行都没有(那十次都只跑到标题界面);
- **TF 那一臂只跑了 1.21.1**;ShoulderSurfing 那一臂(局部变量表重写当初就是拿它验证的)也只跑了 1.21.1;
  其余九个版本各做了一次**不带 TF** 的标题界面启动;
- **1.21.6 / 1.21.8 上两处修复会照常触发**(`class_776` 与 `class_778` 的形状在这两版也在),另两处不触发;
  掩码在那两版是 no-op(切片指令是 1.21.1 的)。两版都到标题界面、失败计数器全 0;
- **`/ERROR` 计数只有 0 才算干净**:测试台自己的 `options.txt` JsonSyntaxException
  (`Failed to load options` / `MalformedJsonException at line 1 column 3`)与离线导致的 401
  (`Failed to fetch user properties`、`Failed to fetch Realms feature flags`)是**已知的台架噪声**,出现时会逐条点名;
  计数用的是普通的 `/ERROR` 匹配 —— 老的正则 `'\] /?ERROR'` 受 PowerShell 转义影响,**从来没有匹配到过任何东西**;
- 本版的实测是在**同一提交**构建的 jar 上做的,与随发布上传的产物**只差 `fabric.mod.json` 里的版本串**
  (已逐条目比对)。

## 2.2.6+mc1.21 … 2.2.6+mc1.21.11 — 把 OptiFine 从 `Camera` 里挪走的 `setRotation` 方法体还给模组:1.21.1 的 ShoulderSurfing 又能注入进去

> 这一版覆盖全部 10 个产物。它包含 **2.2.5 的接口修复**(`GuiRenderer$Draw` 少声明的 `DrawAccessor` 接口表项,
> 见下面那一节)**加上**一处新的修复:`class_4184`(`Camera`)的原版 `method_19325(FF)V` 方法体,
> 顺带一处 `class_702`(`ParticleEngine`)的 lambda 名还原。修订号递增的原因同前:向下兼容的问题修正,
> 不改任何对外的名字、注册方式或产物名。

### 修好了:ShoulderSurfing 的 `CameraMixin` 在 1.21.1 上第一次真的接上

```
Injection validation failed: Argument modifier method rotationYXZ(F)F in
shouldersurfing.fabric.mixins.json:CameraMixin from mod shouldersurfing expected 1 invocation(s) but
0 succeeded. Scanned 0 target(s).
```

`ShoulderSurfing` 的 `CameraMixin`(以及 Porting Lib 的 `porting_lib_client_events` 里那个同名 mixin)要在
`setRotation` 里的 `org.joml.Quaternionf.rotationYXZ` 调用上做 `@ModifyArg`,两个 refmap 指的都是**两参数**的
`setRotation` —— 在游戏里那是 `method_19325(FF)V`,方法体就在里面。OptiFine 是从带第三个 roll 参数的那一版
`Camera` 重编译过来的:它的 `method_19325(FF)V` 只剩四条指令,转发给自己那个三参数的
`public setRotation(FFF)V`,`rotationYXZ` 调用在那边。于是这个注入点在它要找的方法里不存在。

修复是把原版方法体放回去:`RestoreVanillaMethodsFix(true, "method_19325")`。转发器被替换之后 OptiFine 那个
三参数方法没有别的调用者,而 **roll = 0 时原版方法体算出的值与转发器完全相同**,所以这不是改行为,是把被
OptiFine 挪走的调用点还给模组。同一个提交还带一处 `class_702`(`ParticleEngine`)的 lambda 名还原
(`LambdaMethodRefFix()`),它修的是 Porting Lib 的 `ParticleEngineMixin` 找不到 `method_18125` ——
与 Twilight Forest 同一条链上的问题,**单独并不足以让 TF 工作**。

### 实测:同一套模组,2.2.4 与 2.2.6 的差别在哪

1.21.1,模组:Fabric API 0.116.17 + OptiFine HD U J1 + ShoulderSurfing 5.2.0 + forgeconfigapiport 21.1.6 +
cloth-config 15.0.140(**没有 Twilight Forest**)。两侧都在同一模式下比较,因为这条注入的成败取决于
Mixin 的模式:

| 启动方式 | OptiFabric | 结果 |
| --- | --- | --- |
| `-Dmixin.debug=true`(Mixin 的排查模式,注入失败在这里是致命的) | 已发布的 **2.2.4** | 启动期退出:`Mixin apply for mod shouldersurfing failed … CameraMixin … Scanned 0 target(s)` |
| 同上 | **2.2.6** | **到标题界面**(18.8 s);`InvalidInjectionException` 0、`Mixin apply for mod .* failed` 0 |
| 普通启动(不加 debug) | 已发布的 **2.2.4** | **不崩**:到标题界面(39 s),日志里连一条 `Mixin apply` 失败都没有,`/ERROR` 0 |
| 同上 | **2.2.6** | 到标题界面(18.5 s) |

**普通启动下 2.2.4 为什么没事**:Mixin 在非 debug 模式下不把这条 0 命中的注入当致命错误。把 Mixin 导出的类
(`-Dmixin.debug.export=true`)拆开看,`class_4184` 的 `setRotation(FFF)` 上 ShoulderSurfing 自己的
**OptiFabric/OptiFine 兼容**处理器(`shouldersurfing.fabric.compat.mixins.json` →
`optifabricreloaded.CameraMixin`,`modify$zpj000$shouldersurfing$rotationYXZOptiFine`)**是接上并被调用**的,
镜头旋转本来就工作;失败的是通用 `CameraMixin` 的处理器(`modify$zpe000$shouldersurfing$rotationYXZ`),
它只被合并进类、没有任何调用点调用它 —— **这条注入在 2.2.4 上从来没有生效过**。

本版 2.2.6 的导出类里,`method_19325(FF)V` 恢复了原版方法体,通用处理器**现在真的包在它的 `rotationYXZ`
调用上**;兼容处理器仍然只包着 `setRotation(FFF)` 那一处。两处是**不同的方法**,所以不会把同一个 roll 加两遍。

所以准确的说法是:**2.2.6 让这条注入第一次真的接上**,而**不是**"2.2.4 会让装了 ShoulderSurfing 的客户端崩" ——
会在启动期退出的只有开着 Mixin debug 的那一种启动,而排查问题时大家开的正是那一种。证据:
`logs\ss-before-2.2.4-mixindebug\`、`logs\ss-after-2.2.6-mixindebug\`、`logs\ss-before-2.2.4-export\`、
`logs\ss-after-2.2.6-export\` 与两份 `class_4184` 反汇编。

### 本版还带着 2.2.5 的接口修复(1.21.9 / 1.21.10 / 1.21.11)

2.2.5 补的是被补丁的 `GuiRenderer$Draw`(`class_11228$class_11230`)少声明的那一条 `DrawAccessor` 接口:
它是一个 **record**,原版与 OptiFine 重编译后的两份拷贝的 `interfaces` 表都是空的,而 fabric-rendering-v1 的
`GuiRendererMixin` 要用 `@Coerce` 把那个 record 强制成 `DrawAccessor`。游戏自己那份能过,是因为它的 mixin
配置把 `DrawAccessor` 排在 `GuiRendererMixin` 之前;本模组交给 JVM 的那份因为替换过类、丢了 Mixin 的类元数据
顺序,接口没有落上去,于是 `Cannot @Coerce argument type net.minecraft.class_11228$class_11230 at index 4 to
…DrawAccessor` 在客户端画第一帧之前就把它打死。本版沿用同一个 `AddInterfaceFix`;细节与逐版本结果见下面
「2.2.5 的改动」一节。本版在 1.21.9 上用**已发布的 2.2.6 jar**复跑了一次冒烟(三个 jar、一次启动):
到标题界面,修复器照常打出 `Added net/fabricmc/…DrawAccessor to …`,三个失败计数器都是 0。

### Twilight Forest 仍然不支持

本版**没有**、也**不能**让 TF + OptiFabric + OptiFine 在 1.21.1 上工作。挡住它的**不是**上面那个 `Camera`
(那一条本版修了),而是一条**链**:Porting Lib 各个模块的 mixin 各自撞上一个独立的 OptiFine 重编译损失,
当前停在 `porting_lib_base` 的 `client.BlockRenderDispatcherMixin` 对 `class_776`
(`expected 1 invocation(s) but 0 succeeded`)。每一个都是本线已经修过很多次的同一种形状,但这条链本轮没有走完。

### 关于 `/ERROR`:它不是 0,而 2.2.4 记下的 `/ERROR 0` 不是证据

2.2.4 的两份笔记(1.21 与 1.21.1)里写过 `/ERROR` 0。那个数字**不是证据**:当时测试台的计数正则写作
`'\] /?ERROR'`,而 PowerShell 把 `\]` 原样传了下去,于是 `[Render thread/ERROR]` 这类行**从来没有被匹配到过**
—— `/ERROR 0` 是"没找到",不是"没有"。本轮真正数到的 `/ERROR` 行逐条都是环境问题:测试台自己的
`options.txt` JsonSyntaxException(`Failed to load options` / `MalformedJsonException at line 1 column 3`),
以及离线导致的 401(`Failed to fetch user properties`、`Failed to fetch Realms feature flags`)。
**已发布的 2.2.4 正文没有改写**,更正写在这里。

### 边界(别把这一版读大)

- **`ShoulderSurfing` 那一侧只在 1.21.1 上验证过**;1.21.9 只做了一次三个 jar 的冒烟,**其余八版本轮没有启动游戏**
  (1.21 / 1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 / 1.21.10 / 1.21.11)。`class_4184` 与 `class_702` 的注册用的是
  与版本无关的中介名,但没有逐版实测;
- **证据只到标题界面这一层**:每次启动一个实例副本、三个(ShoulderSurfing 那套是六个)jar,没有光影包、没有压测、
  没有长时间游玩、没有逐个模组跑兼容矩阵。`PASS` 的定义是**到标题界面**(日志出现 `Sound engine started`);
- **1.21.11 的「进世界」这一条没有复现**:2.2.5 发布后又用**已发布的 2.2.5 jar** 在 1.21.11 上复跑过两次,
  两次都到标题界面(46 s / 18 s)、修复器照常声明接口、失败计数器全 0,但**没有进世界**(171 s / 400 s 的预算内
  日志停在 OptiFine 的资源包警告之后),也就是 1.21.11 也会遇到 2.2.5 那节里说的同一段「标题界面之后停住」。
  所以「进世界」目前只在早先那次运行里成立(27.7 s),本版没有把它当成可稳定复现的结论;
- 本版**没有**采纳与它同源的另外两处改动:`ImplicitDiscriminatorMaskFix`(那是往一个 debug 属性里写一个刻意的
  类型谎言,取舍微妙,而且**并不能**让 Twilight Forest 工作)与 `class_761`/`method_23793` 的
  `InjectionCallPointFix`(本轮没有在本分支上独立验证)。两处都不影响上面这些结论。

## 2.2.5+mc1.21 … 2.2.5+mc1.21.11 — 补上被补丁的 record 缺的那一条接口:1.21.9 / 1.21.10 / 1.21.11 的 `GuiRendererMixin` 又能应用了

> 这一版覆盖全部 10 个产物(1.21 / 1.21.1 / 1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 / 1.21.9 / 1.21.10 / 1.21.11),
> 但**只对 1.21.9 / 1.21.10 / 1.21.11 有行为上的影响**:那三版此前连标题界面都到不了,死在 fabric-rendering-v1 的
> `GuiRendererMixin` 上。改动是**两处**:新增 `src/main/java/kynarain/cn/optifabric/patcher/fixes/AddInterfaceFix.java`
> (移植自 26.x 线的同名修复器,那一条线已经用同一个修复器解决过同一个形状),以及
> `OptifineFixer` 里的一条注册,共 +419 行、两个文件。
> **修订号递增的原因是向下兼容的问题修正**:不改对外的名字、不改注册方式、不改产物名。

### 修好了:1.21.9 / 1.21.10 / 1.21.11 死在 `Cannot @Coerce` 的那一处

```
Mixin apply for mod fabric-rendering-v1 failed fabric-rendering-v1.mixins.json:GuiRendererMixin from mod fabric-rendering-v1 -> net.minecraft.class_11228: org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException @WrapOperation operation wrapper method net/minecraft/class_11228::fixNonQuadIndexing from fabric-rendering-v1.mixins.json:GuiRendererMixin from mod fabric-rendering-v1 has an invalid signature. Cannot @Coerce argument type net.minecraft.class_11228$class_11230 at index 4 to net.fabricmc.fabric.mixin.client.rendering.DrawAccessor.
```

三个版本打的是**逐字相同**的失败行(只有时间戳不同),`class_11228` = `GuiRenderer`、
`class_11228$class_11230` = `GuiRenderer$Draw`、`DrawAccessor` =
`net.fabricmc.fabric.mixin.client.rendering.DrawAccessor` —— 都是按各版自己的 yarn 映射解析出来的,
不是按别的版本的印象写的。

### 根因:那个 record 少声明了一个**接口**,不是少了方法,也不是局部变量错位

`GuiRenderer$Draw` 是一个 **record**。原版与 OptiFine 重编译后的两份拷贝都**没有声明任何接口**
(`interfaces` 表为空,super 是 `java/lang/Record`,两边都是 11 个声明方法)。缺的只有一样东西:
`DrawAccessor` 那一条接口表项 —— `fabric-rendering-v1` 的 `DrawAccessor` 正是
`@Mixin(targets = "net/minecraft/class_11228$class_11230")`,声明 `fabric$pipeline()` 与 `fabric$indexCount()`。

`GuiRendererMixin.fixNonQuadIndexing` 是 `@WrapOperation`,包装的是那个 record 作为接收者的
`RenderPass.setIndexBuffer` 调用;它第 5 个 handler 参数声明为 `DrawAccessor` 并标了 `@Coerce`,
于是 Mixin 的 `Injector.checkCoerce` 会问:**`DrawAccessor` 是不是 `class_11228$class_11230` 的父类型之一**。

- 游戏自己的那份能过:因为 `fabric-rendering-v1.mixins.json` 把 `DrawAccessor` 排在 `GuiRendererMixin`
  **之前**,Mixin 的 accessor pass 先把接口放到类上,后面那句 `@Coerce` 检查自然成立;
- 本模组交给 JVM 的那份过不了:被替换过的类会丢掉 Mixin 先前按**游戏**字节建立的类元数据
  (`MixinClassMetadata.drop`,2.2.1 引入),这套"谁先谁后"的信息随之丢失,accessor 的接口不再落上去。

### 怎么改的(以及为什么不换一种改法)

`AddInterfaceFix` 只**声明接口**,不实现那两个 accessor 方法:方法由 Mixin 自己的 ACCESSOR pass 补上,
本模组若也写一遍,Mixin 会在 `mergeMethod` 处报 `cannot overwrite method … because @Overwrite is required`。
接口名是**解析**出来的 —— 扫描已加载模组的 mixin 包,找 `@Mixin(targets = "…class_11228$class_11230")` 的接口,
并且只接受一个声明了该类所缺方法的候选 —— 因为它属于 Fabric API,名字会随版本变,写死会随下游漂移。
`class_11228$class_11230` 是这三个版本共用的同一个中介号,所以一条注册覆盖三版:

```java
registerFix("class_11228$class_11230", new AddInterfaceFix("net/minecraft/class_11228$class_11230"));
```

**不会重复施加**:`AddInterfaceFix` 是这一线唯一会写类的 `interfaces` 列表的修复器,`class_11228$class_11230`
只出现在一条注册里;修复器内部对"接口已经在了"的类直接跳过。

### 机制是证明过的,不是推断的

写修复器之前先把机制证明了一遍:把一份私有缓存里的 `class_11228$class_11230` **手工**加上 `DrawAccessor`
(并重算 CRC 让本模组接受这份缓存,用 `-KeepCache` 启动)—— **1.21.9 在 9.5 s 内到标题界面,
`Cannot @Coerce` / `InvalidInjectionException` / `Mixin apply … failed` 全部为 0,而且没有任何东西取代它们。**
修好之后 Mixin 自己导出的类(`-Dmixin.debug.export=true` 下的
`.mixin.out\class\net\minecraft\class_11228$class_11230.class`)声明接口 `DrawAccessor`、方法 **13** 个
(11 个是 record 自己的,另外 2 个正是 `fabric$pipeline()` / `fabric$indexCount()`)—— 接口来自本模组,
方法来自 Mixin,正是这个修复器要的分工。

### 实测(每个臂:自己的实例副本、`-CleanCache`、本模组 + 该版 OptiFine + 该版 Fabric API、一个预置存档)

| MC | 结果 | 标题界面 | 进世界 | `Cannot @Coerce` | `InvalidInjectionException` | `Mixin apply … failed` | `LVTGeneratorError` | `SugarApplicationException` | 修复器 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1.21.9 | **PASS + 进世界** | 24.4 s | 是,220.6 s | 0 | 0 | 0 | 0 | 0 | 声明了接口 |
| 1.21.10 | **PASS(只到标题界面)** | 42.8 s | 否,见「停住」 | 0 | 0 | 0 | 0 | 0 | 声明了接口 |
| 1.21.11 | **PASS + 进世界** | 21.6 s | 是,27.7 s | 0 | 0 | 0 | 0 | 0 | 声明了接口 |
| 1.21.1(冒烟) | **PASS + 进世界** | 18.3 s | 是,21.6 s | 0 | 0 | 0 | 0 | 0 | no-op |
| 1.21.6(冒烟) | **PASS(只到标题界面)** | 18.5 s | 否,见「停住」 | 0 | 0 | 0 | 0 | 0 | no-op |
| 1.21.8(冒烟) | **PASS + 进世界** | 18.4 s | 是,24.7 s | 0 | 0 | 0 | 0 | 0 | no-op |

`PASS` 的定义是**到标题界面**(日志出现 `Sound engine started`);只有写了"进世界"的行才声称进过世界,
判据是集成服务端自己写的标记(`Starting integrated minecraft server version`、
`Preparing start region for dimension`)加上运行期间被重写的 `level.dat` 与 `session.lock`。

修复器自己打的那一行:

```
[OptiFabric] Added net/fabricmc/fabric/mixin/client/rendering/DrawAccessor to net/minecraft/class_11228$class_11230 so the mixin that coerces to it can apply (its fabric$pipeline()Lcom/mojang/blaze3d/pipeline/RenderPipeline;, fabric$indexCount()I will be contributed by Mixin)
```

### 停住:1.21.10 与 1.21.6 到标题界面之后不走,这不是本版造成的

1.21.10 到标题界面之后**没有进世界**:日志不再增长,窗口标题始终没有出现 ` - Singleplayer` 后缀,
`level.dat` 也没有在运行期间被重写,也就是 `--quickPlaySingleplayer` 从未被处理;**所有失败计数器同样是 0**。
这就是 `r214` 报告 §10 已经为 1.21(2.2.3 / 2.2.4)记下的同一类"标题界面之后停住":本轮 1.21.6 也一样
(给足 240 s 余量,实际跑到 261 s 仍未过去),而 1.21.9 在同一处等了约 196 s 后过去了 —— 所以它更像一段很长、
很不稳定的等待,而不是一个确定的死锁。

**它不是本版造成的**:修复器在 1.21.6 上是彻底的 no-op(那一版整个 Fabric API 里没有任何类引用
`class_11228$class_11230`),在 1.21 上连 `class_11228` 都不存在;1.21.10 只是本版第一次让它走到标题界面,
才把这段等待暴露出来。**它的原因本轮没有查清,这里也不做解释。**

### 关于 `/ERROR`:这一版里它不是 0,而 2.2.4 记下的 `/ERROR 0` 不是证据

本轮 `/ERROR` 计数是 0(1.21.1)、1(1.21.6 / 1.21.8)、3(1.21.9 / 1.21.10 / 1.21.11)。逐条看过,
全是测试台自己的环境问题,与 mixin、`@Coerce` 都无关:

```
[Render thread/ERROR]: Failed to load options            <- 测试台删掉了 options.txt 的处理,JsonSyntaxException
                                                            (MalformedJsonException at line 1 column 3)
[Download-2/ERROR]: Failed to fetch user properties      <- 离线:InvalidCredentialsException Status: 401
[Download-1/ERROR]: Failed to fetch Realms feature flags <- 离线:401
```

**更正(2.2.4 的计数缺陷)**:2.2.4 的两份笔记(1.21 与 1.21.1)里写过 `/ERROR` 0。那个数字**不是证据**:
当时测试台的计数正则写作 `'\] /?ERROR'`,而 PowerShell 把 `\]` 原样传了下去,于是 `[Render thread/ERROR]`
这类行**从来没有被匹配到过** —— `/ERROR 0` 是"没找到",不是"没有"。把正则改成普通的 `/ERROR` 之后,
计数才第一次真的动起来(就是上面那 0 / 1 / 3)。**已发布的 2.2.4 正文没有改写**,更正写在这里。

### 离线惰性核对(本版新做的检查)

修复器只在"某个已加载的模组声明了一个 `@Mixin` 指向 `class_11228$class_11230` 的接口"时才动作。
逐版打开各版 Fabric API 里嵌的 `fabric-rendering-v1`,按字节搜 `net/minecraft/class_11228$class_11230`:

```
1.21     fabric-rendering-v1-0.102.0.jar              没有 class_11228
1.21.1   fabric-rendering-v1-0.116.17.jar             没有 class_11228
1.21.3   fabric-rendering-v1-0.114.1.jar              没有 class_11228
1.21.4   fabric-rendering-v1-10.2.1+0d31b09f04.jar    没有 class_11228
1.21.6   fabric-rendering-v1-12.4.0+e8d43c7696.jar    只有 GuiRendererMixin 引用 class_11228
1.21.7   fabric-rendering-v1-12.4.0+e8d43c766c.jar    只有 GuiRendererMixin 引用 class_11228
1.21.8   fabric-rendering-v1-12.6.0+486b7ff72c.jar    只有 GuiRendererMixin 引用 class_11228
1.21.9   fabric-rendering-v1-16.0.1+328a75ba7d.jar    DrawAccessor -> class_11228$class_11230
1.21.10  fabric-rendering-v1-16.2.0+bee81f016f.jar    DrawAccessor -> class_11228$class_11230
1.21.11  fabric-rendering-v1-16.2.10+0290ad933e.jar   DrawAccessor -> class_11228$class_11230
```

所以它在其余七版上要么无事可做,要么打印"没有模组声明指向该类"后原样离开。

### 边界(别把这一版读大)

- **每个臂只有一次启动、三个 jar、一个预置存档**:没有光影包、没有压测、没有长时间游玩、没有逐个模组跑兼容矩阵;
- **1.21 / 1.21.3 / 1.21.4 / 1.21.7 本轮没有启动游戏**;1.21.1 / 1.21.6 / 1.21.8 是冒烟臂;
- **1.21.10 只声称到标题界面**,不声称能进世界;
- 实机结论来自**本版同一提交**构建的候选 jar(与本版产物的差别只有 `fabric.mod.json` 里的版本串,已逐条目比对);
  本版 jar 没有再把这几版各跑一遍。1.21.6 / 1.21.7 的 OptiFine 预览构建"开光影就崩"是 OptiFine 自己的缺陷,
  与本版无关,本轮也没有去开光影。

## 2.2.4+mc1.21 … 2.2.4+mc1.21.11 — 重写 `LocalSlotLayoutFix`:补丁方法的局部变量按**作用域**配对回游戏声明的槽位

> 这一版覆盖全部 10 个产物(1.21 / 1.21.1 / 1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 / 1.21.9 / 1.21.10 / 1.21.11),
> 行为一致,而且**只改一个文件**:`src/main/java/kynarain/cn/optifabric/patcher/fixes/LocalSlotLayoutFix.java`
> (+488 / −90)。注册方式没动(仍是 `registerFix("class_757", new LocalSlotLayoutFix(null, "method_3192"))` 与
> `registerFix("class_761", new LocalSlotLayoutFix(null, "method_22710"))`,`desc = null`),没有按类打补丁、没有新增修复器。
> **修订号递增的原因是向下兼容的问题修正**:它去掉的是一整类失败,不新增能力,也不改任何对外的名字或注册。

### 修复:一个模组里的 `@Local(index = N)` / `Unable to find matching local` 这一整类失败

`LocalSlotLayoutFix` 原先**按槽位**判断:把 OptiFine 自己多出来的局部变量往后推,推不回去游戏被挪走的那些。
于是补丁后的字节里,游戏声明在槽位 N 的局部变量可能落在别的槽位上,MixinExtras 的 `@Local(index = N)` 就找不到它:

```
SugarApplicationException: Failed to validate sugar @Local(index = 24) class_4587 on method port_lib$renderEntityOutline(…)
Caused by: SugarApplicationException: Unable to find matching local!
    at LocalSugarApplicator.validate(LocalSugarApplicator.java:44)
```

更糟的是它不是报错而是**放弃整个方法**:`class_761.method_22710` 曾因此被整条跳过,原话是
`would need 19 local slots moved, which is more than this fixer understands (8); leaving the method alone`。

### 修复:`class_757.method_3192` 的局部变量现在全部对齐回游戏声明的位置

同一原因,这个方法的 18 个游戏自己的表项原先只回去了一部分;现在 **18/18** 都回到游戏声明的槽位。凡是靠
`@Local(index = N)` 或按顺序读局部变量的混入,拿到的布局从此与游戏自己编译出来的一致。

### 怎么改的

- **配对粒度从"槽位"改成"作用域(局部变量表项)"**:两个方法的指令下标不可比(1636 ↔ 2673 条指令)、局部变量名也不可比
  (游戏侧的 jar 带的是 Mojang 混淆名,OptiFine 侧带的是它自己的编译树上的名字),唯一可比的是**两张表按槽位排下来的
  描述符序列** —— 用最长公共子序列把两张表对齐,每对被配上的表项就是"OptiFine 这一项对应的游戏局部变量",
  配不上的就是 OptiFine 自己的;
- **整个槽位一起搬**:某个槽位里的表项都是游戏的,就搬回游戏声明它的槽位(`method_22710` 里 `27->24` 就是把
  `PoseStack` 放回去,`@Local(index = 24)` 因此能解析);配不上的槽位搬到两个方法各自范围之外的尾部,于是它永远不会是
  处理器看到的前几个局部变量;
- **一个槽位绝不拆给两个目标**,这不是保守而是实测出来的缺陷:OptiFine 的 `class_761.method_22710` 里槽位 36 在
  指令 1079..1098 与 1130..1167 两段都装着 `class_4597`,而 1097 处的 `GOTO 1130` **跳过**了 1129 的写入:
  后一段读到的是一个从未被写过的值,只是因为前一段的值还在同一个槽位里才成立。早先一版拆过槽位的实现就留下了这个
  未初始化读,**JVM 自己的校验器放行**,ASM 的数据流校验在指令 1165 处报 `Expected an object reference, but found .`;
- **只有生命周期不重叠的槽位才共用一个号**:一个槽位若带着任何表项没描述的读或写(javac 并不描述它分配的每个临时量,
  OptiFine 的 `method_22710` 有十五处在两个表项都没覆盖的偏移上写槽位 34),就按"整个方法都活着"算,单独占一个号;
  `maxLocals` 相应变大,帧仍由管线里的 `FrameComputingWriter` 重算;
- **任何一步放弃都不改字节**:整套方案先算完再动手,接收者与参数永不移动。旧的"按槽位"方案作为 `moveBySlot` 保留,
  只在某一侧**完全没有局部变量表**(拿不到作用域)时才走。

### 离线校验(1.21.1,重写前后各跑一遍,数字逐项相同)

`test-downloads\verify-version.ps1 -Version 1.21.1 -ModVersion 2.2.3`,修复器 stash 前后各一次:

| 指标 | 重写前 | 重写后 |
| --- | --- | --- |
| 补丁类 prepared | 425(0 skipped,0 failed) | 425(0 skipped,0 failed) |
| 补丁类 verified / failed | 425 / 0 | 425 / 0 |
| ASM 校验问题(补丁类) | 0 | 0 |
| OptiFine 类 verified / failed | 783 / 0 | 783 / 0 |
| RefmapScan MISSING | 0 | 0 |
| LambdaScan DANGLING | 0 | 0 |
| 修复器自己的判断 | `class_761.method_22710` **放弃**(19 槽) | `class_761` **对齐**;`class_757.method_3192` 把游戏 18 个表项**全部**放回去 |

### 真机结果(1.21.1)

`OptiFabric-2.2.4+mc1.21.1.jar` + `OptiFine_1.21.1_HD_U_J1.jar` + `fabric-api-0.116.17+1.21.1.jar`:
**到了主界面(15 s)并进入了世界**;修复器自己的 stdout 对该版本的两个方法都打了 `Realigned the locals of`
(`class_761.method_22710`:41 / 64 个表项被配对回游戏自己的 47 个,含 `27->24`;`class_757.method_3192`:18 / 21),
`SugarApplicationException` 与 `Unable to find matching local` 在整份日志里 **0 次**。其余九个版本见本次发布说明里的
冒烟测试表:**只到主界面/进世界这一层**,没有光影包、没有压测。

### 已知限制 / 边界(别把这一版读大)

- **The Twilight Forest + OptiFabric + OptiFine 在 1.21.1 上仍然起不来**,本版**没有**、也**不能**让它受支持:
  让 TF 卡住的是另一个阻塞点 —— `porting_lib_blocks` 的 `client.LevelRendererMixin` 里那个带隐式 `Iterator`
  判别符的 `@ModifyVariable`,`@At("STORE")` 在 **OptiFine 自己的**方法体里有**两个同时活着的** `Iterator` 槽位
  (OptiFine 自己的帧表里本来就写着两条),而游戏原版方法里只有一个。**改槽位号无法改变这个歧义**
  (重排前是 `32:Iterator, 34:Iterator`,重排后是 `26:Iterator, 54:Iterator`,条数一样);
- **证据只到主界面/进世界这一层**:一次启动、一个实例、一个存档,没有光影包、没有压测、没有长时间游玩,
  也没有逐个模组跑兼容矩阵;
- **没有重新验证本版对 2.2.3 那些修复的影响**:那些修复器与本次改动的目标方法不重叠(见各版本的发布说明)。

### 更正:2.2.3 的发布说明把这一处写反了

2.2.3 的说明里写过"`LocalSlotLayoutFix` 是**按作用域**而不是**按槽位**重映射,`class_761.method_22710` 的 19 个候选槽位
超过它的 `MAX_MOVES`(8),所以放弃该方法"。**前两处说反了**:那一版那个修复器**是按槽位**判断的,放弃的原因也不是
`MAX_MOVES` 这个常量,而是"要搬的槽位超过它理解的上限(8)"。这句只是历史说明写错,不影响 2.2.3 产物本身;本版把它改回准确。
## 2.2.3+mc1.21 … 2.2.3+mc1.21.11 — 七处修复:OptiFine 重编译改掉的调用点、lambda 名与合成字段,以及 FRAPI 的渲染器注册

> 这一版覆盖全部 10 个产物（1.21 / 1.21.1 / 1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 / 1.21.9 / 1.21.10 / 1.21.11）,
> 行为一致。**修订号递增的原因是向下兼容的问题修正**:七条 `registerFix` 与一处注册方式改写,没有新能力、也没有不兼容修改。

### 修复:四个被 OptiFine 重编译改写的类,现在在补丁期修回去

不是猜的:每一条都对着补丁前后两份字节逐个比对(`compat-recheck\RECHECK.md` 与 `c2me-check\REPORT.md`)。

- **`class_156`**(`net.minecraft.Util`)→ `RestoreVanillaMethodsFix(true, "method_29191")`。OptiFine 重编译时把同一个
  catch 块里的日志调用从 `Logger.error(String,Object)` 降成了 `Logger.debug(String,Object)`(只有一个操作码不同,
  异常表与其余部分逐字节相同),而这个调用点正是 The Twilight Forest 的 `UtilMixin` 要重定向的那一个:重定向扫不到东西,
  注入失败把整个类带下去,`Mixin transformation of net.minecraft.class_156 failed`,标题界面都到不了。把原版方法体放回去
  才能修 —— 不能靠"在方法前面补一个调用",那个参数是被捕获的异常,而且补在最前面就会变成每次查表都打日志;
- **`class_638`**(`net.minecraft.client.world.ClientLevel`)→ `LambdaMethodRefFix()`。OptiFine 把游戏**自己注册**的那个
  colour resolver 方法记成了 `lambda$new$3`,描述符与 `BootstrapMethods` 槽位都没变,只是名字从 `method_23778` 换了。
  porting_lib 的 `ClientLevelMixin` 按名字找 `method_23778`,找不到目标,整个类变换失败。把 lambda 改回游戏的名字即可
  —— 因为它本来就**是**游戏注册的那个方法;
- **`class_761`**(`net.minecraft.client.render.LevelRenderer`)→ 两条。`LambdaMethodRefFix()`:游戏声明为
  `method_37365` 的 Runnable 体被重编译成了 `lambda$updateCameraAndRender$1`(描述符、访问标志、注册位置、
  混入用 `@ModifyArg` 包住的那个 `class_758.method_3211` 调用全都没变,只有名字变了),C2ME 的视距修改因此失效；
  这里**故意不用** `RestoreVanillaMethodsFix("method_37365")` —— 那会把原版方法体放在 OptiFine 的 lambda 旁边,
  实际跑的仍是 OptiFine 那份,混入也就永远不生效。另一个是 `LocalSlotLayoutFix(null, "method_22710")`,
  与 `class_757.method_3192` 同一个局部变量槽位错位的原因;
- **`class_3898$class_3216` 与 `class_3204$class_4077`** → `SyntheticFieldFix()`。OptiFine 把合成外层实例字段
  `field_17443` / `field_18255` 改名成 `this$0`**,而 C2ME 的 `@Accessor` 与 `@Shadow` 是按游戏的名字找的:
  找不到就会在**进世界**时让整个目标类变换失败(`…class_3898$class_3216 failed` / `…class_3204$class_4077 failed`)。

### 修复:FRAPI 的渲染器注册

`RendererApiFallback` 原先用静态的 `Renderer.register(Renderer)` 注册占位渲染器 —— 本线的 Fabric API 里已经没有这个方法了
(用 `javap` 对着 `fabric-api-0.116.17+1.21.1` 里嵌的 `fabric-renderer-api-v1-0.116.17.jar` 核过:`RendererAccess` 只剩
`registerRenderer` / `getRenderer` / `hasRenderer`)。查找抛 `NoSuchMethodException`,占位渲染器**从未注册**,于是每个用 FRAPI
的模组拿到的 `getRenderer()` 都是 null,The Twilight Forest 的 `ForceFieldModel` 在静态初始化时就死在上面。现在走
`RendererAccess.INSTANCE.registerRenderer(Renderer)`(旧写法保留为第一次尝试,失败路径仍不致命并照旧打日志)。

### 实测结果

- **C2ME**:能到标题界面,进世界连续跑 **113 秒,零条 `[ERROR]`**;
- **The Twilight Forest**:**不带 OptiFine 时已经能加载**(此前连标题界面都到不了);但它在本版**仍未被完整支持** ——
  关掉 OptiFine 才会遇到的那个 OptiFine 侧阻塞点还在:`LocalSlotLayoutFix` 是**按作用域**而不是**按槽位**重映射,
  `class_761.method_22710` 的 19 个候选槽位超过它的 `MAX_MOVES`(8),这个修复器**直接放弃该方法**,
  porting_lib_base 的 `LevelRendererMixin` 因此仍然失败。**本版没有修这一条**,别把它读成"The Twilight Forest 已受支持"。

### 已知限制(本版没有修的部分)

- `LocalSlotLayoutFix` 对上面那个方法放弃处理,原因与后果见上一条;
- 约 **36 个**被补丁的类里仍带着**未注册**的 `vtN` / `this$N` 合成字段(见 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md))。
  它们现在不会拦到本模组自己,但对**别的模组**是敞开的:任何按名字找这些字段的 `@Accessor` / `@Shadow`
  都会在进世界时让目标类变换失败,和上面那两条合成字段的问题同一种。

### 更正:`conflicts` 与 `breaks` 在加载器上的实际行为(2.2.2 的说明写反了)

2.2.2 的小节里曾写过"两个字段都只产生警告、不阻止游戏启动"。**实测不是这样**:
**`conflicts` 条目只警告**(Fabric Loader 0.19.5 的 `ModSolver` 里对 `CONFLICTS` 连约束都不加,只有一条
`// TODO: soft negative dep?`),而 **`breaks` 条目是被执行的** —— 加载器拒绝这个组合,而不是放行。
一次记录到的运行里写着 `NEG_HARD_DEP optifabric_reforged 2.2.2 {breaks sodium}`:当 `breaks` 点到**已存在**的模组时,
加载器的依赖求解器给出 `NEG_HARD_DEP` 并拒绝加载。所以把 sodium 同时写进 `breaks`,是**闸门**,不是声明;
只写在 `conflicts` 里才是"只警告"。

## 2.2.2+mc1.21 … 2.2.2+mc1.21.11 — 声明 sodium 不兼容（conflicts 与 breaks 同时列出）

> 这一版覆盖全部 10 个产物（1.21 / 1.21.1 / 1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 / 1.21.9 / 1.21.10 / 1.21.11）,
> 行为一致。**修订号递增的原因是元数据修正(声明不兼容而不是门槛)**：
> sodium 现在 `conflicts` 和 `breaks` 两处都列出。

- **`sodium` is now declared in both `conflicts` and `breaks`** in `fabric.mod.json`.
  **更正**:本行原先断言"两个字段都只产生警告、不阻止游戏启动" —— 那句话是错的,
  按加载器与源码复核后的结论见上面 2.2.3 一节。

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
