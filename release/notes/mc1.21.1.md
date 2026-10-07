# OptiFabric 2.2.14+mc1.21.1

**Minecraft 1.21.1** / Fabric Loader 0.19.5 / Java 21+ / 需求 OptiFine `OptiFine_1.21.1_HD_U_J1.jar`

状态:**已实测正常**

## 2.2.13 的改动

**一句话:** 一份坏掉的 OptiFine jar 不再把**错误提示自己**打崩(实测:修复前产生崩溃报告与 `NullPointerException … jarType is null`,修复后无崩溃报告、无 NPE、进程存活);另有一轮来自代码审查的加固,九条分支同步。

### 坏 jar 不再打崩错误路径

`mods/` 里放一个**合法 zip、`net/optifine/Config.class` 却是坏字节**的 jar 时:旧版 `parseJarType` 只接住 `ZipException | ZipError`,而 `ClassReader` 对垃圾字节抛的是**未检查异常**,它一路穿透到标题界面,`switch (OptifineVersion.jarType)` 拿到 null ⇒ NPE —— 崩溃发生在**专门用来解释失败的那条路**上。两处改法:

* `parseJarType` 把 `RuntimeException` 一并接住(读不懂的类文件与坏 zip 是同一类"不可用的 jar" ⇒ `JarType.CORRUPT_ZIP`,对话框照旧显示);
* `MixinTitleScreen` 在 `jarType == null` 时落到 `INTERNAL_ERROR` 分支(复制堆栈/日志 + issues 链接)。同树的 `CrashReportMixin` 早有 null 守卫、`OptifinePrompt.gate()` 对 null 安全,这里是唯一漏掉的一处。

**实测**:同一份坏 jar、同一个实例副本,分别用本提交与提交前构建的 jar 启动 —— 修复前有崩溃报告(`crash-2026-10-07_12.28.12-client.txt`)与两次 `NullPointerException`,进程自己退出;修复后**无崩溃报告、0 次 NPE**,客户端一直活到被测试脚手架主动停掉。那份崩溃报告同时证明错误当时**已经设置好**(进入 switch 前有 `if (!OptifabricError.hasError()) return;`),即对话框那条路确实被走到了。修复后**无法从日志证明对话框已显示**(`setError()` 只存不打印),如实写明。

### 与其它分支同步的加固

* `ChunkRendererFix`:先判参数个数再取最后一个参数(短参数表的调用以前会 `AIOOBE`);
* `MethodComparison`:不认识的 `invokedynamic` bootstrap 记为"不同"而不是抛异常;LDC 的 sort switch 原先**无 default**、基本类型会穿透到 IINC 比较并强转(`ClassCastException`),改为统一按描述符比较;
* `ClassCache`:文件声明的四个长度全部加上限,负数/超大值走"空缓存"(与其它损坏情形一致),不再抛 `NegativeArraySizeException`;
* `OptifineSetup`:`LambdaRebuilder` 放进 `finally` 关闭(以前 transform 抛异常时原版 jar 与临时文件不释放);
* `GAME_CLASSES`:改为**有界 LRU**(上限 512),而不是"只 put 不清空";
* `LambdaRebuilder`:模糊配对只按**本类**回查(以前跨类套用同名同描述符的配对);"已全部配上"从 `return 0` 改成 `continue`;
* `InjectionCallPointFix`:重建调用时按位置消费参数,并优先**照抄游戏自己那次调用的压栈指令**(以前同类型多形参会把同一个槽位用两次、静默传错值);
* `ZipUtils.extract`:路径检查改成**无条件 + 带分隔符**(审查已把它降级为纵深防御 —— 唯一调用点在 `-Doptifabric.extract` 调试开关后面;仍然修了);
* `release/publish.ps1`:`-DryRun` 打印前**脱敏令牌**,两条 curl 命令改成参数数组、不再用 `Invoke-Expression`。

### 下载器(只影响 `-full` 构建;**本产物没有下载器**)

`-full` 版本另外补了三处并做了**真实下载验证**:主机名精确比较(以前 `url.contains("optifine.net")`,`https://evil.example/?optifine.net` 会被当成官方页去抓对方页面并从里面找下载链接;`https://optifine.net@evil.example/` 这类写法同样堵住)、**一律要求 https**、以及**下载后校验下来的确实是所要的那个构建**(读它自己 `Config.class` 的 `VERSION` / `MC_VERSION`,不匹配就删掉文件并报错)。官网那个下载链接是**相对路径**,解析后仍是 https,所以强制 https 不影响正常下载。**本产物(默认产物)不联网、不启动进程。**

### 帧计算修正:公共父类型改为对称求解

1.20.6 + OptiFine `J1_pre18` 曾**启动即崩**(`VerifyError: Bad return type` 于 `class_5944.method_35785`):字节码是正确的,但我们写出的 StackMapTable 帧把 `class_284` 与 `class_278` 的公共类型写成了 `java/lang/Object`,校验器于是拒绝加载该类。原实现只展开第一个参数的祖先、再沿第二个参数的 `superName` 走,而 ASM 的传参顺序是任意的 ⇒ "接口在前"时必然回落成 `Object`。现在两侧各自求闭包(自身 + 全部超类 + 全部接口)后取最近的公共类型,`allSupertypes` 也计入自身。

**实测**:同一实例(含 Lithium 0.12.5)在修复前每次启动都产生崩溃报告;修复后 **0 份崩溃报告、0 次 VerifyError**,客户端一直存活到被测试脚手架停掉。附带确认:这个崩**与 Lithium 无关**(移出 Lithium 后同样崩)、**不是 1.1.5 的回归**(1.1.4 同样崩)、也**不是坏缓存**(删缓存重打同样崩)。



## 2.2.10 的改动

**一句话:** 2.2.8 / 2.2.9 把 sodium 写进 `breaks`,于是**整个实例被加载器拒绝**;本版把那条声明去掉,实例能起来、日志里只留一条警告,
并修掉**重新归因那一轮证明属于我们这一侧的第一处失败**——而 sodium、iris、immediatelyfast 都不是单个坑,是**连着的坑**,
本版只关掉了每一串的头几处。

### sodium 不再是闸门

`fabric.mod.json` 里从 2.2.8 起把 `sodium` **同时**写进了 `conflicts` 与 `breaks`。在 Fabric Loader 0.19.5 上这两者不是一回事:
`conflicts` 只是 `Warnings were found!`(`ModSolver` 里那条 `// TODO: soft negative dep?` 至今没有约束),而 `breaks` 点到已存在的
模组是 `NEG_HARD_DEP`,加载器**拒绝整个实例**。2.2.9 实测:

```
[main/INFO]: Immediate reason: [HARD_DEP chloride 1.8.1 {depends sodium @ [>=0.8.12]},
    NEG_HARD_DEP optifabric_reforged 2.2.9+mc1.21.1 {breaks sodium @ [*]}, …]
[main/ERROR]: Incompatible mods found!
```

代价是包里**其它所有模组**(chloride 这类 sodium 附属模组还会以 `HARD_DEP` 一起失败)。2.2.10 只保留 `conflicts`:
实例能起来,日志里留一条警告。README 与 README_CN 里那张声明表、表上的摘要行与相关段落都已按实测改写,两次运行都引在正文里。

### sodium **仍然是级联**:不要把这一版读成"sodium 能用了"

两个调用点都修好了,而且都先在**私有 `.optifine` 缓存副本**上用 fixer 类本身验过才提交:

* `class_761.method_22714` 丢了 `class_310.method_1517()Z`(OptiFine 换成了自己的 `Config.isRainFancy()Z`,一个**无参**静态取值器):
  `InjectionCallPointFix` 按"从方法自己的参数重建调用"把它补回来(无参调用可以精确重建),登记为
  `registerFix("class_761", new InjectionCallPointFix("class_310", "method_1517", "()Z", "method_22714"))`;
* `class_638.method_23777` 丢的是 `class_6491.method_24895(Lclass_243;Lclass_6491$class_4859;)Lclass_243;`,OptiFine 换成了自己的
  `class_6491.sampleM(...)Lnet/optifine/Vec3M;` —— **同样的两个参数、不同的被调方与返回类型**。第二个参数是方法自己在四条指令
  之前用 `invokedynamic` 造出来的解析器,既不是参数也不是字段,`InjectionCallPointFix` 按设计拒绝它(没有地方去取这个实参);
  但那两个值**此刻已经在操作数栈上**(就在 OptiFine 自己那次调用之前),所以新 fixer `RestoreSiblingCallFix` 把它们复制一份
  (`DUP2`,两个 category-1 引用),再调游戏自己的方法、丢弃它的 `class_243` 结果。

两个 mixin 现在都能应用,加载器也不再拒绝这个组合。**但 sodium 0.8.13 不是死在第一处,而是死在第三处、第四处**:

| 臂 | 结果 | 证明它的那行 |
|---|---|---|
| 只有这两个调用点(私有缓存) | CLIENT-DIED | `ItemRendererMixin … renderModelFastDirections(Operation)[Lnet/minecraft/class_2350; … (0/1) succeeded. Scanned 0 target(s)` → `Mixin transformation of net.minecraft.class_918 failed` |
| 再加上 `class_918`(私有缓存) | CLIENT-DIED | `Redirector redirectFancyGraphicsVignette()Z in sodium-common.mixins.json:features.options.overlays.GuiMixin … (0/1) succeeded` → `Mixin transformation of net.minecraft.class_329 failed` |
| 本版构建的 jar | 同上 | 同样两行,停在 `class_329` |

也就是说 sodium 至少还需要第四处修复(`class_329` 上那处布尔重定向),而且**没有任何证据说它是最后一处**;`indium`(sodium 0.5.11)
单独验过:同样关掉那两个命名调用点、同样停在 `class_329`。这些失败**不是"两个渲染器打架"**——逐行看,它们和前面那些是**同两个族**的
普通缺失:OptiFine 挪走或内联掉的调用点(`InjectionCallPointFix` / `RestoreSiblingCallFix`),以及被重编译改名或清空的辅助方法
(`RestoreVanillaMethodsFix`)。**渲染器并不是卡住的原因,级联才是。** 所以这一版把 sodium 以及只带 sodium 的那五行
(`sodium`、`sodium-extra`、`reeses-sodium-options`、`chloride`、`sodium-shadowy-path-blocks`、`indium`)写成**"能起、还差得远"**,
而不是"已修好";本版**不声称** sodium(或 sodium + iris 那套组合)可用,**它的定位是"不受支持,但不再拒绝加载"**。

### 六处 `RestoreVanillaMethodsFix` 登记:标题界面逐行验证

每一处都先读**服务出去的那个类**拿到字节证据(不是猜的),再在私有缓存副本上用同一个 fixer 类验一遍:

| class.method | OptiFine 换成了什么 | 行 |
|---|---|---|
| `class_757.method_18144(Lclass_1297;)Z` | `lambda$pick$57(Lclass_1297;)Z` | `cut-through` |
| `class_836.method_3580(Ljava/util/HashMap;)V` | `lambda$static$0(Ljava/util/HashMap;)V` | `deeperdarker` **和** `supplementaries` |
| `class_1043.method_22793()V` | `lambda$new$0()V` | `modernfix` |
| `class_442.method_55814(Lclass_4185;)V` | `lambda$init$1..5(Lclass_4185;)V` | `no-chat-reports` |
| `class_1921` 的六个候选方法 | `lambda$static$N` | 两条 `immediatelyfast` 行 |
| `class_702.method_3049(Lclass_765;Lclass_4184;F)V`(**replace 模式**) | 六条指令的转发器,转给 OptiFine 自己的 `render(…,class_4604)` | `particle-core` |

**量到标题界面的六行是** `cut-through`、`deeperdarker`、`supplementaries`、`modernfix`、`no-chat-reports`、`particle-core`
(其中五条计数器全 0;`supplementaries` 到标题界面,但它自己的 `ParrotLayerMixin` 仍然失败,见下)。`immediatelyfast` 的两行**没有**到
标题界面:它的前两处失败关掉了(`class_1921` 的六个方法 + `class_1008.method_4224`),运行撞上**第三处**(`class_327$class_5232`)。

**为什么六个候选方法全登记(而不是挑一个)。** `immediatelyfast` 的 `core.MixinRenderLayer` 是
`@ModifyArg(method = {"method_34834","method_34833","method_36437","method_36436","method_37348","method_37347"}, … index = 5)`
(用 `javap -v` 从 jar 里读出来的)。六个方法都被 OptiFine 的重编译删掉了,而游戏自己的 `class_1921` 里**六个都在、且六个都含**
`@At` 点名的 `method_24049` 调用(`Query callers` 对 `client-intermediary.jar` 查过),所以全恢复才等于"这个模组在普通 Fabric 上看到的东西"。

### 另外两处(同一轮,标题界面验证)

* **`moreculling`** —— `class_918.method_23182` 里 OptiFine 把 `class_2350.values()[Lnet/minecraft/class_2350;` **内联**成了对枚举
  自己 `$VALUES` 字段的读取(一换一:`vanilla [10] CALL class_2350.values()` / `served [10] FIELD class_2350.field_11040`)。
  sodium 0.8.13 的 `ItemRendererMixin` 与 `moreculling` 的 `ItemRenderer_faceCullingMixin` 都注入这条调用,登记
  `InjectionCallPointFix("class_2350", "values", "()[Lnet/minecraft/class_2350;", "method_23182")` 即可(无参调用,普通修法就是精确的)。
  结果:**标题界面,`/ERROR` 也是 0**。
* **`shatterbyte-lib`(OctoLib)、`iris`、`sample--mr-spectrumjei`(modonomicon)共用的 `class_761.method_22710` 调用点** —— 既有的
  `RestoreVanillaCallFix` 一处登记覆盖三行:`shatterbyte-lib` **到标题界面并进了世界**(计数器全 0);另外两行的**那一处**不再报错,
  然后各自撞上更后面的坑(iris 的 `skipLocalBlockEntities`、spectrum 的 `class_329` 上的 `InGameHudMixin`)。

### 一处写了、验过、然后**撤掉**的修复(别再盲试)

`supplementaries` 的 `ParrotLayerMixin` 需要 `class_983.method_17958` 回来,它确实不在服务出的类里,按同一族恢复也"成功"了
—— **但它会把被修的那个类弄坏**:恢复方法之后流水线要重算 `class_983` 的栈映射帧(被 fixer 改过的类都走 `FrameComputingWriter`),
重算出来的帧把实体局部变量判成 `java/lang/Object`:

```
java.lang.VerifyError: Bad type on operand stack in putfield
Location: net/minecraft/class_983.lambda$renderParrot$1(…)V @100: putfield
Reason:   Type 'java/lang/Object' (current frame, stack[0]) is not assignable to 'net/minecraft/class_1297'
```

于是资源重载直接失败(`Caught error loading resourcepacks, removing all selected resourcepacks`);把这条登记拿掉,同一条臂
**0 个 VerifyError** 并照常到标题界面。**这个修复比它要修的故障更糟**,所以没有发布,只写在 `OptifineFixer` 的注释里
(同族的 `class_983` 地雷在 1.21.8 那一侧也已经记在 `OptifineInjector#patch` 里,1.21.1 上换一个方法照样踩得到)。
下一次想修 `supplementaries` 的人请从这条注释开始,别先把方法恢复回去再来查。

### 诚实的边界:上一份报告漏掉了三处,因为每条臂都停在第一次失败

重新归因那一轮的运行**在每条行的第一处失败就结束**(一次致命的 Mixin 失败会终止整条臂),所以有三处只有在修完第一处之后才会被
变换到的类**从来没有进过报告**。本轮靠测量(而不是读报告)找到了它们:

1. `class_983.method_17958`(`supplementaries`)—— 写了、证明有害、撤回(见上);
2. `class_918.method_23182` —— 一处调用点、**两行**(`moreculling` 与 sodium),本版已修;
3. `class_1008.method_4224` —— `immediatelyfast` 的**第二处**失败(那份报告已经记了它的计数器 `Scanned 0 target(s)=2`、
   `InjectionError=2`、`Mixin apply failed=2`,但没有把它列进登记表)。`core.MixinGlDebug` 包的是 `Logger.info(String,Object)`,
   OptiFine 重编译后(原版 86 条指令 → 服务出的 303 条)那个调用已经不存在;两个实参既不是 `method_4224(IIIIIJJ)V` 的参数也不是字段,
   也没有可复制的兄弟调用,所以只有 `RestoreVanillaMethodsFix(true, "method_4224")` 把游戏自己的方法体放回去 —— 它是一个 GL 调试
   日志方法,代价只是 OptiFine 对这一个日志方法的补充。

因此**不要把本版的"已修"读成那 27 行都修好了**:真正修好并量到标题界面的是上面那六行 + `moreculling`,再加 `shatterbyte-lib` 的进世界;
`sodium` / `iris` / `immediatelyfast` / `sample--mr-spectrumjei` 是**级联**,本版只关掉了头几处。

### 只诊断、没改代码的两处

* **`ebe` 与 `sample--mr-betternether`**:Fabric 的 `client` 入口点在静态初始化里读 `net.optifine.Config.gameSettings`,而那个字段是
  OptiFine 自己在游戏构造 `GameSettings` 时才填的,晚于入口点阶段。我们**无法**在那之前给它一个正确的值:给一个默认对象会让模组按
  默认值静默注册错误的模型(它们读 `ofRandomEntities` 来决定注册什么),比现在这个清楚的 NPE 更糟。诚实的做法是把它写成文档里的
  **顺序注意事项**:入口点里不要读 `Config` 字段,改到客户端 tick 或客户端生命周期回调里读。
* **`open-parties-and-claims`**:崩溃报告一直没写出来,因为 OptiFine 自己的崩溃报告扩展先死了(`Shaders.<clinit>` 在
  `Minecraft.getInstance()` 还是 null 时读 `options`)。本轮用 javaagent 探针在报告生成**之前**抓到了真正的 throwable:是这个模组
  自己的 OptiFine 专用 mixin(`optifine.breaks.MixinFabricMob` 的 `onAiStepItemPickup`,`LocalCapture` 局部变量)在
  `class_1308.method_6007` 上因局部变量槽位不匹配而失败,连带 `class_2246`(`Blocks`)静态初始化失败。族别因此从"候选"变成**确认**:
  `LocalSlotLayoutFix`;修复没做 —— 那是所有实体都继承的类,需要自己的缓存验证和世界验证。

### 验证与边界

每条修复都是**普通启动**(没有用 `-Dmixin.debug.export=true`;要看真实服务出去的字节就直接读 `.optifine` 缓存),计数器是
`latest.log` + stdout + stderr 三者之和,以标题界面为界。回归:**Carry On 2.2.6.13**(1.21.1)、**ShoulderSurfing 5.2.0**(1.21.1)、
**1.21.6**、**1.21.8** 标题界面、**暮色森林 4.8.734** —— 全部 TITLE-SCREEN,除 TF 那条已知的空 refmap 警告(2 条 `/ERROR`)之外
计数器为 0,`field_4172` 崩溃 0 次。

**别把这一版读大。** 每一次都只启动**一次**,一个实例副本、一个新建的 `.optifine` 缓存、`-Xmx2048M`,**没有光影包、没有压测、
没有长时间游玩、没有多人**;进世界那条只量了 1.21.1(`shatterbyte-lib`),其余九版只做了标题界面与上面那几条回归。
sodium / iris / immediatelyfast 的**级联没有修完**,`class_983` 那条撤掉的修复**没有发布**,`class_1308.method_6007` 只确认了族别。

## 2.2.9 的改动

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

## 2.2.8 的改动

**本版把 Twilight Forest 那条链剩下的五处 OptiFine 重编译损失一起修掉**,1.21.1 上 TF 4.8.734 现在能到标题界面,
并且**已经量到过进世界,但只是间歇性的(11 个带 TF 的臂里进了 4 个)**(见下面「TF 进世界是间歇性的」)。改动集中在三个文件:
`ImplicitDiscriminatorMaskFix.java`(新文件,+268)、`InjectionCallPointFix.java`(+64/−6)、`OptifineFixer.java`(+106);
外加把「只修到一半」的 `class_5944` 补完(同见下)。没有改资源、映射或元数据。

**先把一句必须说清楚的话写在前面**:这套判别符掩码与 `class_761` 那一处调用点**不是 2.2.8 才有的**。那三个提交
(`1f8377b`、`b08241d`、`faa5ad4`)起初只以 **dangling object** 的形式存在于仓库里(在另一个私有克隆里做出来的),
**2.2.6 的发布产物里确实没有它们**;但**它们已经随 2.2.7 发布出去了** —— 2.2.7 的源码里 `OptifineFixer` 就注册着
`new ImplicitDiscriminatorMaskFix("method_22710", "Ljava/util/Iterator;", ...)`,已发布的
`OptiFabric-2.2.7+mc1.21.1.jar` 里也有 `ImplicitDiscriminatorMaskFix.class`,2.2.7 那次带 TF 的运行日志里打着
`[OptiFabric] Masked java.util.Iterator local(s) 53, 54, 26, 27 in net/minecraft/class_761.method_22710`
(见下面 2.2.7 那一节)。所以:用已发布的 2.2.6 jar 普通启动 TF,**6.1 s 就死**,日志里是 12 条
`invalid IMPLICIT discriminator`(11 条 `Found 2` + 1 条 `Found 3`),而 **2.2.7 已经把这个崩溃拿掉了**。
**2.2.8 不是"第一次带上这套恢复"的那个版本**:它带的是它自己那一节里写的东西 —— `class_5944`(`ShaderProgram`)
的补完,以及 `class_776$1`(烛台)那处合成字段。2.2.6 的正文里那句"TF 仍然不支持"因此仍然适用于 2.2.6 本身。


### 链上的五处,按客户端撞上的顺序

1. **隐式判别符掩码 + `class_761` 的调用点**。Porting Lib 的 `porting_lib_blocks` `LevelRendererMixin` 用隐式
   `@ModifyVariable` 包住方块实体的 `Iterator`,判别符会把方法里从槽位 1 开始的每一个 `Iterator` 型局部变量都数进去;
   补丁后的类在那个切片的**十二个 store 上各有两个或三个**,于是每个注入点都被丢掉,整个类失败:
   `@At("STORE" implicit Iterator) has invalid IMPLICIT discriminator ... Found 2 candidate variables but
   exactly 1 is required`。候选**根本不在类文件的 LocalVariableTable 里**:它们是 Mixin 的 `Locals` 在真实表
   对那个槽位没有任何在范围内的表项时、从代码生成出来的表项(所以名字才叫 `var26`/`var29`,跟着槽位走),
   这也正是"改写已有表项"够不到它们的原因。同一个 mixin 的另一个处理器是包在 `BlockState.getLightEmission()` 上的
   `@WrapOperation`,而 OptiFine 的 `method_23793` 只剩一条**四条指令的转发器**(转给它自己的
   `getPackedLightmapCoords`),从来没有调用 `class_2680.method_26213`;
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
而且**只在配置好的那个切片范围内**。

为什么是"追加"而不是"改写":Mixin 取局部变量的类型走 `Locals.getLocalVariableAt`,它先读随类发布的那张
LocalVariableTable,一旦某个槽位在范围内**没有**表项,就退回到 ASM 自己按数据流生成的那张表。有歧义的候选通常正是
生成表里的那些。而 `getLocalVariableAt` 按顺序扫描、**保留最后一个范围内命中的表项**,所以追加在末尾的这条会同时
压过发布表与生成表。它不动基本类型、不动参数槽位、不新建也不删除局部变量,也不改代码、槽号、范围、名字或
`maxLocals`。它是**按(类、方法、捕获类型、切片)逐条注册的可选项**:那个形状不在了(找不到切片两条边界、同类型
局部变量不足两个、或者方法根本没有真实局部变量表),它什么都不做。

### 补完了那一处(`class_5944`):ShaderProgram 的 null,影响所有注册核心着色器的模组

2.2.7 只把「包错了工厂」改成「包对了工厂」;2.2.8 把那一处**补完**,从 2.2.7 的「只修到一半」变成修完。抛 NPE 的那条
null **不是**被重建调用的实参,而是 Fabric API 用 `@Shadow` 读到的 `ShaderProgram.field_29494`(名字字段),四点是
逐字节看出来的:

1. `FabricShaderProgram.rewriteAsId(String, String)` 的第一条指令是 `aload_1` → `class_2960.method_60654`
   (`Identifier.of`),也就是**第二个形参**被拿去建 Identifier;
2. `ShaderProgramMixin.modifyId(String, Operation)` 交进去的第二个实参就是 `this.field_29494`;
3. 游戏自己的构造函数**先**把 String 形参写进这个字段、**再**建 location(OptiFine 重编译后的 `class_5944`
   没有那次写入,字段由 `(provider, Identifier, format)` 那个构造函数反推);
4. PortingLib 传进来的 shader id **不是 null**(`twilightforest:rendertype_entity_unlit_translucent`)。

`DelegatingConstructorFix.parameterState()` 现在把游戏自己构造函数里、从 String 参数发出的 `PUTFIELD` **重放**在
重建出来的调用**前面**,处理器那次 rewrite 于是退化成恒等。**它影响的是任何**通过
`CoreShaderRegistrationCallback` 注册核心着色器的模组 —— PortingLib 那条
`rendertype_entity_unlit_translucent`(在 Twilight Forest 4.8.734 里)只是第一个跑到那儿的。修后的补丁器输出:
`The inlined (…)V fills 1 field(s) from its String parameter before the re-created call, as the game's own
constructor does`。

**更正(2.2.7 正文里那句当默认值的一般结论)**:「不是 TF 那一臂进不去世界的原因」这句原是拿**不带 TF 的那一套**
(它一个资源包也不剩却照常进世界)推出的;本轮 8 臂的实测量到带 TF 的那一套**本身就能进世界**,而资源包那一步
修与不修都不改变进世界的成败。实测(1.21.1,普通启动):`stringIn` NPE **2 → 0**、`CompletionException` **1 → 0**、
资源包被丢掉 **1 → 0**,资源重载从两次变一次。

### 实测(普通启动,`-Xmx2048M`,没有任何 debug 开关)

TF `twilightforest-fabric-1.21.1-4.8.734.jar` + `fabric-api-0.116.17+1.21.1.jar` + 本 jar + OptiFine
`OptiFine_1.21.1_HD_U_J1.jar`,1.21.1 / Fabric Loader 0.19.5。**五个链上修复都在这份日志的标准输出里**
(`Masked java.util.Iterator local(s) …`、`Re-created the injection point … class_2680.method_26213()I in
… class_761.method_23793`、`Restored vanilla … class_776.method_3353(…)V`、`Re-created … in
… class_778.method_3374`、`Re-created … class_1799.method_31574(Lnet/minecraft/class_1792;)Z in
… class_915.method_33434`、`… class_5944: 1 factory call(s) aligned with the game`)。

计数器(逐条都是 0):

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

**这一版是"到标题界面",而且已经量到过进世界;进世界是间歇性的**:2.2.8 拿掉了启动崩溃、把 TF 送到标题界面,
并且**八次尝试里有三次进了世界**(见下)。

带 TF 的那一套(TF `twilightforest-fabric-1.21.1-4.8.734.jar` + `fabric-api-0.116.17+1.21.1.jar` + 本 jar +
`OptiFine_1.21.1_HD_U_J1.jar`)在 2.2.8 上**到标题界面**(300 s 那次 46.8 s,600 s 那次 25.8 s),致命计数器
**逐条为 0**(`Cannot @Coerce`、`InvalidInjectionException`、`Mixin apply … failed`、
`Mixin transformation of … failed`、`LVTGeneratorError`、`SugarApplicationException`、`Minecraft has crashed`);
那次复核的 `/ERROR` 只有一条,是环境的(`Failed reading REFMAP JSON … 'mapper' is null`,forgeconfigapiport)
—— 它在**不带 TF、照样进世界**的那一臂里也在。

### TF 进世界是间歇性的(本轮新量到;这一条修正了本版早先的"不能")

2.2.8 待发布笔记早先写的是「TF 没有进世界,两次都没有」;那是**另外几臂**的测量。本轮专门量了这一件事:
**11 个带 TF 的臂**(`tf-must`,同一套 jar、同一台机器、同一份按文件 SHA-256 校验过的预置存档、每个臂自己的实例副本):

| 那一套 | 进世界 |
| --- | --- |
| 不带 TF(fabric-api + 本 jar + OptiFine) | **3/3**(标题界面之后 20.3 / 20.4 / 20.6 s) |
| 带 TF(再加 `twilightforest-fabric-1.21.1-4.8.734`) | **4/11**(入场 46.7 s / 约 50 s / 约 50 s / **122.7 s**) |

进去的那几臂里,`Loaded 1944 advancements` 到 `Applied 0 biome modifications to 0 of 86 new biomes` 的间隔
是 **6 s / 25 s / 94 s** —— 交接本身没有时序控制,这也解释了为什么会输掉。

- 进去的那四臂有完整的证据:日志里 `Applied 0 biome modifications to 0 of 86 new biomes`、
  `Starting integrated minecraft server version 1.21.1`、`logged in with entity id 70`,并且预置存档的
  `level.dat` 被改写(2,303 B → 2,439 B,`01E5A1B6…` → `8C1C7514…`);
- 没进的那七臂**没有任何异常**:日志停在 `Loaded 1944 advancements` 之后,再没有一个字。用一支自建的
  `-javaagent` 探针(装上 JVM 的默认未捕获异常处理器、每秒两次采样全部线程)量到:**零条未捕获 throwable、
  没有死锁、`Server thread` 从未被创建**;Render thread 一直闲在标题界面的帧循环里(`glfwWaitEventsTimeout ←
  RenderSystem.limitDisplayFPS`),`jcmd Thread.print` 与探针上看到的是同一幅画面;`level.dat` 一个字节没动,
  下一次启动仍能正常打开同一个存档;
- **别急着判它卡死**:四次成功里有两次发生在一次失败之后,而最慢的那次是标题界面**之后 122.7 s** 才进场;
  `tf-fps` 那次专门测量量到的入场时间是 **20.4 s 到 152.6 s**,`Loaded 1944 advancements` 到
  `Applied 0 biome modifications` 的空档是 **2 s 到 135 s** —— 所以**「两到三分钟」是下限,不是经验上限**:
  给世界加载至少两到三分钟再下结论;真没打开时,关掉客户端再启动一次,失败的那次不会弄坏存档
  (**没有东西要修**:`level.dat` 逐字节未动,连修改时间都一样),下次照常打开;
- **要治的是「窗口被最小化」,不是帧率**:同一次测量里,窗口最小化的臂进世界 **5/19**(`maxFps:5` 2/9、
  `maxFps:260` 3/10,两侧 Fisher 精确检验 **p = 1.00**),窗口在屏幕上的臂进世界 **13/18**(5/9 与 8/9);
  固定 `maxFps:260`、只把窗口从最小化改成可见并置前,是 3/10 → 8/9(**p = 0.0198**)。
  **上一版写的「不要压帧率」是假设,本轮已经量过:它是无效的**(固定窗口可见、只把 `maxFps` 从 5 改到 260,
  5/9 → 8/9,p = 0.29),特此收回;请在加载世界期间**让 Minecraft 窗口保持可见(不要最小化,最好放在最前面)**;
- **「优先从标题界面点开世界」既不成立也没被推翻**:那次测量只跑了 `--quickPlaySingleplayer`(启动器的自动进场),
  从标题界面点开这条路**没有量过**,所以本版不再把它写成偏好;
- **样本很小,别读大**:这一组只有 **19 个窗口最小化的臂**与 **18 个窗口可见的臂**(只数记录了进场结果的臂),
  全部只跑 **1.21.1**、只有一个预置存档,**没有光影、没有世界内压测、没有多人、没有进 TF 维度**;
  窗口可见是这些臂里唯一活下来的变量。


**边界(别把这一条读大)**:这一组只有 **11 个带 TF 的臂**和 3 个对照臂,**样本很小**;
**没有光影包、没有世界内压测、没有多人、没有进 TF 维度**;只有一个预置存档、只跑了 **1.21.1**(1.21.6 / 1.21.8
两版带 TF 的臂**没有跑**)。挡住它的那一处**是一个「世界打开的交接」被丢掉,不是本模组的代码**:
带 TF 的臂和不带 TF 的臂打的是同一份补丁器输出,包括 `class_5944` 那三行。

**所以本版的说法是**:2.2.8 修掉了启动崩溃、把 TF 送到标题界面,并**量到过进世界(11 次里 4 次)**;
**进世界是间歇性的,本版没有修它,也不声称修了它**;失败了就再启动一次,没有东西要修。

### 与 2.2.6 / 2.2.4 的产物差别

条目级比对(每个条目都取哈希):相对已发布的 **2.2.6**,新增 1 个类
(`ImplicitDiscriminatorMaskFix.class`),2 个类的内容变了(`InjectionCallPointFix.class`、
`OptifineFixer.class`),**没有**探针、没有多余的条目;相对 **2.2.4** 的差别是上述三类再加上 2.2.5 / 2.2.6 已经发布的
那些类(逐条列在下面的核对表里)。

### 边界(别把这一版读大)

- **每个臂只启动一次**,一个实例副本、一个预置存档,**没有光影包、没有压测、没有长时间游玩、没有多人**。
  `PASS` 的定义是**到标题界面**(日志出现 `Sound engine started`);只有明确写了「进世界」的行才声称进过世界,
  下面那张逐版本表里一行都没有(那十次都只跑到标题界面),1.21.1 那两次进世界是另外一组臂量到的;
- **所有带 TF 的世界进入测量都只跑了 1.21.1**;1.21.6 / 1.21.8 只做了**不带 TF** 的标题界面启动;
  ShoulderSurfing 那一臂(局部变量表重写当初就是拿它验证的)也只跑了 1.21.1;
  其余九个版本各做了一次**不带 TF** 的标题界面启动,结果见本版发布说明的核对表;
- **进世界那一组只有 11 个带 TF 的臂和 3 个对照臂**(`tf-must`,一个预置存档、每个臂一次启动),
  没有光影包、没有世界内压测、没有多人、没有进 TF 维度;`4/11` 只是这一组的比例,不是稳定的复现率;
- **1.21.6 / 1.21.8 上两处修复会照常触发**(`class_776` 与 `class_778` 的形状在这两版也在),另两处不触发;
  掩码在那两版是 no-op(切片指令是 1.21.1 的)。两版都到标题界面、失败计数器全 0;
- **`/ERROR` 计数只有 0 才算干净**:测试台自己的 `options.txt` JsonSyntaxException
  (`Failed to load options` / `MalformedJsonException at line 1 column 3`)与离线导致的 401
  (`Failed to fetch user properties`、`Failed to fetch Realms feature flags`)是**已知的台架噪声**,出现时会逐条点名;
  计数用的是普通的 `/ERROR` 匹配 —— 老的正则 `'\] /?ERROR'` 受 PowerShell 转义影响,**从来没有匹配到过任何东西**;
- 本版的实测是在**同一提交**构建的 jar 上做的,与随发布上传的产物**只差 `fabric.mod.json` 里的版本串**
  (已逐条目比对)。

### 逐版本实测(普通启动,每个版本一次,不带 TF)

这一节是**随本版发布的十个 jar**(上面「校验」一节逐条给了它们的尺寸与 SHA-256)各自的启动记录:每个版本
一次**普通启动**(没有 `-Dmixin.debug*`)、一个自己的实例副本、一个新建的 `.optifine` 缓存,`-Xmx2048M`,
`PASS` 指日志出现 `Sound engine started`;**这十次都不带 Twilight Forest**(TF 那一套的记录在上面
「TF 进世界是间歇性的」一节,那是另一台 rig、另一套 jar)。同一轮里还做了两次 `-full` 产物的启动(1.21.1 与 1.21.8),
用来证明两个产物都到得了标题界面,记在表尾。判定一律**到标题界面**,这十次**没有开世界**。

| MC | 产物 | 结果 | 到标题界面 | `/ERROR` | `Cannot @Coerce` | `InvalidInjectionException` | `Mixin apply … failed` | `Mixin transformation … failed` | `LVTGeneratorError` | `SugarApplicationException` | `Minecraft has crashed` |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1.21 | store | 到标题界面 | 18.7 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.1 | store | 到标题界面 | 15.8 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.3 | store | 到标题界面 | 17.2 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.4 | store | 到标题界面 | 17.2 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.6 | store | 到标题界面 | 18.1 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.7 | store | 到标题界面 | 18 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.8 | store | 到标题界面 | 17.9 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.9 | store | 到标题界面 | 18.7 s | 3 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.10 | store | 到标题界面 | 21.6 s | 3 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.11 | store | 到标题界面 | 20.2 s | 3 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.1 -full | -full | 到标题界面 | 17.3 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.8 -full | -full | 到标题界面 | 17.2 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |

表里有几条 `/ERROR` **不是 0**,**逐条都是离线环境**,与 mixin 无关,原文照录:

```
[Download-2/ERROR]: Failed to fetch user properties         <- 离线:401
[Download-1/ERROR]: Failed to fetch Realms feature flags    <- 离线:401
[IO-Worker-1/ERROR]: Couldn't connect to realms             <- 离线:连不上 Realms
```

**边界(别把这一节读大)**:每个版本只启动**一次**,只装了本 jar + 对应版本的 Fabric API + 对应版本的 OptiFine,
**没有光影包、没有压测、没有多人**;`-full` 只试了 1.21.1 与 1.21.8 两版。**`/ERROR` 计数只有 0 才算干净**:
计数用的是普通的 `/ERROR` 匹配 —— 老的正则 `'\] /?ERROR'` 受 PowerShell 转义影响,**从来没有匹配到过任何东西**。
本节与 **2.2.7** 那一节的逐版本表**不能混读**:那一张是 2.2.7 的产物、另一台 rig 跑的,两张表的数字属于各自的版本。

## 2.2.7 的改动

**本版把 Twilight Forest 那条链剩下的五处 OptiFine 重编译损失一起修掉**,1.21.1 上 TF 4.8.734 现在能到标题界面。
改动集中在三个文件:`ImplicitDiscriminatorMaskFix.java`(新文件,+268)、`InjectionCallPointFix.java`(+64/−6)、
`OptifineFixer.java`(+106)。没有改资源、映射或元数据。

**先把一句必须说清楚的话写在前面**:上一版 **2.2.6 的发布产物里并没有**这个判别符掩码,也没有 `class_761` 那一处
调用点。那三个提交(`1f8377b`、`b08241d`、`faa5ad4`)此前只以 **dangling object** 的形式存在于仓库里(在另一个私有
克隆里做出来的),**从来没有并进 `1.21.x`**。所以用已发布的 2.2.6 jar 普通启动 TF,**6.1 s 就死**,日志里是
12 条 `invalid IMPLICIT discriminator`(11 条 `Found 2` + 1 条 `Found 3`)。**本版是第一次真的带上这套恢复的版本**,
不是"延续上一版"。2.2.6 的正文里那句"TF 仍然不支持"因此仍然适用于 2.2.6 本身。

### 链上的五处,按客户端撞上的顺序

1. **隐式判别符掩码 + `class_761` 的调用点**。Porting Lib 的 `porting_lib_blocks` `LevelRendererMixin` 用隐式
   `@ModifyVariable` 包住方块实体的 `Iterator`,判别符会把方法里从槽位 1 开始的每一个 `Iterator` 型局部变量都数进去;
   补丁后的类在那个切片的**十二个 store 上各有两个或三个**,于是每个注入点都被丢掉,整个类失败:
   `@At("STORE" implicit Iterator) has invalid IMPLICIT discriminator ... Found 2 candidate variables but
   exactly 1 is required`。候选**根本不在类文件的 LocalVariableTable 里**:它们是 Mixin 的 `Locals` 在真实表
   对那个槽位没有任何在范围内的表项时、从代码生成出来的表项(所以名字才叫 `var26`/`var29`,跟着槽位走),
   这也正是"改写已有表项"够不到它们的原因。同一个 mixin 的另一个处理器是包在 `BlockState.getLightEmission()` 上的
   `@WrapOperation`,而 OptiFine 的 `method_23793` 只剩一条**四条指令的转发器**(转给它自己的
   `getPackedLightmapCoords`),从来没有调用 `class_2680.method_26213`;
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
而且**只在配置好的那个切片范围内**。

为什么是"追加"而不是"改写":Mixin 取局部变量的类型走 `Locals.getLocalVariableAt`,它先读随类发布的那张
LocalVariableTable,一旦某个槽位在范围内**没有**表项,就退回到 ASM 自己按数据流生成的那张表。有歧义的候选通常正是
生成表里的那些。而 `getLocalVariableAt` 按顺序扫描、**保留最后一个范围内命中的表项**,所以追加在末尾的这条会同时
压过发布表与生成表。它不动基本类型、不动参数槽位、不新建也不删除局部变量,也不改代码、槽号、范围、名字或
`maxLocals`。它是**按(类、方法、捕获类型、切片)逐条注册的可选项**:那个形状不在了(找不到切片两条边界、同类型
局部变量不足两个、或者方法根本没有真实局部变量表),它什么都不做。

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

计数器(逐条都是 0):

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

**这一版是"到标题界面",不是"能玩";而"挡住它的不是 TF"这句现在作废**:2.2.7 拿掉了启动崩溃、把 TF 送到标题界面。
**本节当时写的"TF 能不能进世界已经量过了,答案是不能"后来被推翻,特此更正**:那两次(300 s / 600 s)确实都没有进,
但 2.2.8 上专门做的、更大的一组测量量到 **TF 那一套能进世界,只是间歇性的** —— 带 TF 的 11 个臂里进了 4 个,
再往后按窗口归并的一组是**窗口最小化 5/19、窗口可见 13/18**;下文保留那两次看到的失败形状(它仍然准确),
**要改的只是"能不能进世界"这个结论**。


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

**所以本版只能说到这儿**:2.2.7 修掉了启动崩溃、把 TF 送到标题界面;那两次尝试都没有进世界,形状如上。
**本版没有修它,也不声称修了它**。**后来的测量把结论补完了**(见本节开头):TF 能进世界,但只是间歇性的,所以
**加载世界期间请让 Minecraft 窗口保持可见(不要最小化,最好放在最前面)** —— 窗口可见性在那组测量里是唯一活下来的
变量;并且**给世界加载至少两到三分钟**再下结论(入场时间量到 **20.4 s 到 152.6 s**),真没打开就重启客户端
(失败的那次不会弄坏存档)。它是谁的责任、为什么不动,本轮**没有量**,这里一个字都不写。


### 与 2.2.6 / 2.2.4 的产物差别

条目级比对(每个条目都取哈希):相对已发布的 **2.2.6**,新增 1 个类
(`ImplicitDiscriminatorMaskFix.class`),2 个类的内容变了(`InjectionCallPointFix.class`、
`OptifineFixer.class`),**没有**探针、没有多余的条目;相对 **2.2.4** 的差别是上述三类再加上 2.2.5 / 2.2.6 已经发布的
那些类(逐条列在下面的核对表里)。

### 边界(别把这一版读大)

- **每个臂只启动一次**,一个实例副本、一个预置存档,**没有光影包、没有压测、没有长时间游玩、没有多人**。
  `PASS` 的定义是**到标题界面**(日志出现 `Sound engine started`);只有明确写了「进世界」的行才声称进过世界,
  下面那张逐版本表里一行都没有(那十次都只跑到标题界面);
- **TF 那一臂只跑了 1.21.1**;ShoulderSurfing 那一臂(局部变量表重写当初就是拿它验证的)也只跑了 1.21.1;
  其余九个版本各做了一次**不带 TF** 的标题界面启动,结果见本版发布说明的核对表;
- **1.21.6 / 1.21.8 上两处修复会照常触发**(`class_776` 与 `class_778` 的形状在这两版也在),另两处不触发;
  掩码在那两版是 no-op(切片指令是 1.21.1 的)。两版都到标题界面、失败计数器全 0;
- **`/ERROR` 计数只有 0 才算干净**:测试台自己的 `options.txt` JsonSyntaxException
  (`Failed to load options` / `MalformedJsonException at line 1 column 3`)与离线导致的 401
  (`Failed to fetch user properties`、`Failed to fetch Realms feature flags`)是**已知的台架噪声**,出现时会逐条点名;
  计数用的是普通的 `/ERROR` 匹配 —— 老的正则 `'\] /?ERROR'` 受 PowerShell 转义影响,**从来没有匹配到过任何东西**;
- 本版的实测是在**同一提交**构建的 jar 上做的,与随发布上传的产物**只差 `fabric.mod.json` 里的版本串**
  (已逐条目比对)。

### 逐版本实测(普通启动,每个版本一次,不带 TF)

这一节是随本版发布的十个 jar 各自的启动记录,每个版本都是一次**普通启动**(没有 `-Dmixin.debug*`)、
一个自己的实例副本、一个新建的 `.optifine` 缓存,`-Xmx2048M`,`PASS` 指日志出现 `Sound engine started`。
**只有 1.21.1 那一行带 Twilight Forest**;其余九版只装了本 jar + 对应版本的 Fabric API + 对应版本的 OptiFine。

| MC | 结果 | 到标题界面 | `/ERROR` | `Cannot @Coerce` | `InvalidInjectionException` | `Mixin apply … failed` | `Mixin transformation … failed` | `LVTGeneratorError` | `SugarApplicationException` | `expected N invocation(s)` | `Minecraft has crashed` | `invalid IMPLICIT discriminator` | `[Server thread]` |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1.21 | 到标题界面 | 19.7 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.1(TF) | 到标题界面 | 54.9 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.3 | 到标题界面 | 30.8 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.4 | 到标题界面 | 44.7 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.6 | 到标题界面 | 36.3 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.7 | 到标题界面 | 54.1 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.8 | 到标题界面 | 43.5 s | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.9 | 到标题界面 | 78.2 s | **3** | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.10 | 到标题界面 | 24.0 s | **2** | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1.21.11 | 到标题界面 | 36.8 s | **3** | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |

1.21.9 / 1.21.10 / 1.21.11 的那几条 `/ERROR` **逐条都是离线环境**,与 mixin 无关,原文照录:

```
[Download-2/ERROR]: Failed to fetch user properties        <- 离线:401
[Download-2/ERROR]: Failed to fetch Realms feature flags   <- 离线:401
[IO-Worker-1/ERROR]: Couldn't connect to realms            <- 离线:连不上 Realms
```

**同一次扫描还测到一件本版没有声明过的事,记在这里而不是删掉**:2.2.5 那条 `class_11228$class_11230` 的接口修复
(`AddInterfaceFix`)**在这一轮 1.21.9 / 1.21.10 / 1.21.11 上确实又触发了**,三版都打出
`Added net/fabricmc/fabric/mixin/client/rendering/DrawAccessor to net/minecraft/class_11228$class_11230 …`,
而且三版都到标题界面、`Cannot @Coerce` 全 0;1.21 到 1.21.8 上它安静无事(那几版没有这个类)。

还测到:`class_776.method_3353` 与 `class_778.method_3374` 两处在本表中的**每一版**都触发(它们的形状在 1.21 到
1.21.11 都在),而 `class_915` / `class_5944` 只在 1.21.1 触发 —— 与报告里"这两处在 1.21.6 / 1.21.8 不触发"的说法一致,
本表把它扩到了十个版本。掩码(判别符)仍然只在 1.21.1 触发:它的切片指令是 1.21.1 的。

## 2.2.6 的改动

**本版在 2.2.5 的接口修复之上,再修一处 OptiFine 重编译缺陷**:1.21.1 上 ShoulderSurfing 的 `CameraMixin`
一直没能注入进去 —— 它要的注入点在 **OptiFine 重编译过的 `class_4184`(`Camera`)** 里已经不存在了。

### 修好了:ShoulderSurfing 的 `CameraMixin` 在 1.21.1 上第一次真的接上

```
Injection validation failed: Argument modifier method rotationYXZ(F)F in
shouldersurfing.fabric.mixins.json:CameraMixin ... expected 1 invocation(s) but 0 succeeded.
Scanned 0 target(s).
```

`ShoulderSurfing` 的 `CameraMixin`(以及 Porting Lib 的 `porting_lib_client_events` 里那个同名 mixin)要在
`setRotation` 里的 `org.joml.Quaternionf.rotationYXZ` 调用上做 `@ModifyArg`,两个 refmap 指的都是**两参数**的
`setRotation` —— 在游戏里那是 `method_19325(FF)V`,方法体就在里面。OptiFine 是从**带第三个 roll 参数**的那一版
`Camera` 重编译过来的:它的 `method_19325(FF)V` 只剩四条指令,转发给自己那个三参数的
`public setRotation(FFF)V`,方法体(以及 `rotationYXZ` 调用)都在那边。于是这条注入在它要找的方法里
找不到目标(`Scanned 0 target(s)`),**一次都没有成功过**。

修复是把原版方法体放回去:`RestoreVanillaMethodsFix(true, "method_19325")`。转发器被替换之后 OptiFine 那个三参数
方法没有别的调用者,而 **roll = 0 时原版方法体算出的值与转发器完全相同**,所以这不是改行为,是把被 OptiFine 挪走
的调用点还给模组。同一个提交还带一处 `class_702`(`ParticleEngine`)的 lambda 名还原(`LambdaMethodRefFix()`),
它修的是 Porting Lib 的 `ParticleEngineMixin` 找不到 `method_18125` 那一条 —— 与 Twilight Forest 同一条链上的问题,
单独并不足以让 TF 工作。

### 实测:同一套模组,2.2.4 与 2.2.6 的差别在哪

**本版在一台机器上用同一套模组复跑了两侧**(Fabric API 0.116.17 + OptiFine HD U J1 + ShoulderSurfing 5.2.0 +
forgeconfigapiport 21.1.6 + cloth-config 15.0.140,**没有 Twilight Forest**)。这条注入的成败取决于 Mixin 的模式,
所以两侧都在同一模式下比较:

- **`-Dmixin.debug=true`(Mixin 自己的排查模式,注入失败在这里是致命的)**:已发布的 **2.2.4** 在启动期直接退出,
  日志里是 `Mixin apply for mod shouldersurfing failed … CameraMixin … expected 1 invocation(s) but 0 succeeded.
  Scanned 0 target(s)`;**本版 2.2.6** 到**标题界面**(18.8 s),`InvalidInjectionException` 与
  `Mixin apply for mod .* failed` 都是 0。证据:`r225-121x\logs\ss-before-2.2.4-mixindebug\`、
  `r225-121x\logs\ss-after-2.2.6-mixindebug\`;
- **普通启动(不加 debug)**:**已发布的 2.2.4 不会崩** —— 同一套模组照样到标题界面(本轮 39 s),日志里
  **连一条 `Mixin apply` 失败都没有**,`/ERROR` 也是 0。把 Mixin 导出的类(`-Dmixin.debug.export=true`)
  拆开看就清楚了:`class_4184` 的 `setRotation(FFF)` 上,ShoulderSurfing 自己的 **OptiFabric/OptiFine 兼容**
  处理器(`shouldersurfing.fabric.compat.mixins.json` → `optifabricreloaded.CameraMixin`,方法名
  `modify$zpj000$shouldersurfing$rotationYXZOptiFine`)是**接上并被调用**的;而通用 `CameraMixin` 的处理器
  (`modify$zpe000$shouldersurfing$rotationYXZ`)只被合并进类、**没有任何调用点调用它**,因为它的目标
  `method_19325(FF)V` 在 OptiFine 的拷贝里只是一个四条指令的转发器、里面没有 `rotationYXZ` 调用。
  也就是说**这条注入在 2.2.4 上从来没有生效过**,只是在普通启动里 Mixin 不把它当致命错误;
- **本版 2.2.6 的导出类**:同一个 `method_19325(FF)V` 恢复了原版方法体,通用处理器
  `modify$zpe000$…rotationYXZ` **现在真的包在 `rotationYXZ` 调用上了**;兼容处理器仍然只包着
  `setRotation(FFF)` 那一处。两处是**不同的方法**,所以不会把同一个 roll 加两遍。证据:
  `r225-121x\logs\ss-after-2.2.6-export\`、`r225-121x\export-4184-{224,226}.txt`(两份反汇编)。

所以准确的说法是:**2.2.6 让这条注入第一次真的接上**,而**不是**"2.2.4 会让装了 ShoulderSurfing 的客户端崩" ——
会在启动期退出的只有开着 Mixin debug 的那一种启动,而排查问题时大家开的正是那一种。

### 本版同时带着 2.2.5 的接口修复(1.21.9 / 1.21.10 / 1.21.11)

2.2.5 补的是被补丁的 `GuiRenderer$Draw`(`class_11228$class_11230`)少声明的那一条 `DrawAccessor` 接口:
它是一个 **record**,两份拷贝的 `interfaces` 表都是空的,而 fabric-rendering-v1 的 `GuiRendererMixin`
要用 `@Coerce` 把 record 强制成 `DrawAccessor`。游戏自己那份能过,是因为它的 mixin 配置把 `DrawAccessor`
排在 `GuiRendererMixin` 之前;本模组交给 JVM 的那份因为替换过类、丢了 Mixin 的类元数据顺序,接口没有落上去。
本版沿用同一个 `AddInterfaceFix`,细节与证据见下面的「2.2.5 的改动」一节。

### 边界(别把这一版读大)

- **The Twilight Forest 仍然不支持**,本版没有、也不能让它受支持。TF + OptiFabric + OptiFine 在 1.21.1 上
  仍然进不去:挡住它的**不是**上面那个 `Camera`(那一条本版修了),而是一条**链**——Porting Lib 各个模块的 mixin
  各自撞上一个独立的 OptiFine 重编译损失,当前停在 `porting_lib_base` 的 `client.BlockRenderDispatcherMixin`
  对 `class_776`(`expected 1 invocation(s) but 0 succeeded`)。每一个都是本线已经修过很多次的同一种形状,
  但这条链本轮没有走完;
- **证据只到标题界面这一层**:每个臂一次启动、三个(Fabric API 那套是五个)jar、一个实例副本,没有光影包、
  没有压测、没有长时间游玩、没有逐个模组跑兼容矩阵。`PASS` 的定义是**到标题界面**(日志出现
  `Sound engine started`);只有明确写了「进世界」的行才声称进过世界;
- **`ShoulderSurfing` 那一侧只在 1.21.1 上验证过**(见上),其余版本没有跑那套模组;
- **`/ERROR` 计数不是 0**,而且 2.2.4 笔记里那个 `/ERROR 0` **不是证据**:当时测试台的计数正则写作
  `'\] /?ERROR'`,PowerShell 把 `\]` 原样传了下去,`[Render thread/ERROR]` 这类行从来没有被匹配到过。
  本轮真正数到的 `/ERROR` 行逐条都是环境问题:测试台自己的 `options.txt` JsonSyntaxException
  (`Failed to load options` / `MalformedJsonException at line 1 column 3`),以及离线导致的 401
  (`Failed to fetch user properties`、`Failed to fetch Realms feature flags`)。已发布的 2.2.4 正文没有改写,
  更正写在这里;
- **1.21.11 的「进世界」这一条没有复现**:2.2.5 发布后又用**已发布的 2.2.5 jar** 在 1.21.11 上复跑过两次,
  两次都到标题界面(46 s / 18 s)、修复器照常声明接口、失败计数器全 0,但**没有进世界**(171 s / 400 s 的预算
  内日志停在 OptiFine 的资源包警告之后),也就是 1.21.11 也会遇到同一段「标题界面之后停住」。所以「进世界」
  目前只在早先那次运行里成立(27.7 s),本版没有把它当成可稳定复现的结论。

## 2.2.5 的改动

**本版与 1.21.1 无关**:这一版里没有任何已加载的模组声明 `@Mixin(targets = "net/minecraft/class_11228$class_11230")`,
修复器无事可做。下面写的是这条修复本身、它对三个受影响版本的实测,以及为什么它对这一版是构造上的 no-op。

### 失败是什么

```
Mixin apply for mod fabric-rendering-v1 failed fabric-rendering-v1.mixins.json:GuiRendererMixin from mod fabric-rendering-v1 -> net.minecraft.class_11228: org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException @WrapOperation operation wrapper method net/minecraft/class_11228::fixNonQuadIndexing from fabric-rendering-v1.mixins.json:GuiRendererMixin from mod fabric-rendering-v1 has an invalid signature. Cannot @Coerce argument type net.minecraft.class_11228$class_11230 at index 4 to net.fabricmc.fabric.mixin.client.rendering.DrawAccessor.
```

`class_11228` = `GuiRenderer`,`class_11228$class_11230` = `GuiRenderer$Draw`(按各版自己的 yarn 映射解析出来的,
不是猜的)。`fixNonQuadIndexing` 是 `@WrapOperation`,包装的是那个 record 作为接收者的 `RenderPass.setIndexBuffer`
调用,它第 5 个 handler 参数声明为 `DrawAccessor` 并标了 `@Coerce`;Mixin 的 `Injector.checkCoerce` 于是要问:
`DrawAccessor` 是不是 `class_11228$class_11230` 的父类型之一。

### 为什么答案是「不是」,而游戏自己的那份是「是」

`GuiRenderer$Draw` 是一个 **record**:原版与 OptiFine 重编译后的两份拷贝都**没有声明任何接口**
(`interfaces` 表为空,super 是 `java/lang/Record`,两边都是 11 个声明方法)。缺的正是 `DrawAccessor`
那一条接口表项。游戏自己那份能过,是因为 `fabric-rendering-v1.mixins.json` 把 `DrawAccessor` 排在
`GuiRendererMixin` **之前**:Mixin 的 accessor 先跑,接口先落到类上,后面那句 `@Coerce` 检查就过了。本模组
交给 JVM 的那份没有这条接口 —— 被替换过的类会丢掉 Mixin 先前按游戏字节建立的类元数据
(`MixinClassMetadata.drop`,2.2.1 引入),那套顺序信息随之丢失,accessor 的接口就不再落上去。

### 怎么修的

新增 `AddInterfaceFix`(本线新文件)+ 一条注册,共两个文件、+419 行:

```java
registerFix("class_11228$class_11230", new AddInterfaceFix("net/minecraft/class_11228$class_11230"));
```

它只**声明接口**,不实现 accessor 方法 —— `fabric$pipeline()` / `fabric$indexCount()` 由 Mixin 自己的
ACCESSOR pass 补上;本模组若也写一遍,Mixin 会在 `mergeMethod` 处报 `cannot overwrite method … because
@Overwrite is required`。接口名是**解析**出来的(扫描已加载模组的 mixin 包,找
`@Mixin(targets = "…class_11228$class_11230")` 的接口,且只接受声明了该类所缺方法的候选),不是写死的 ——
它是 Fabric API 的名字,会随版本变。`class_11228$class_11230` 这一个中介号三版通用,所以一条注册覆盖
1.21.9 / 1.21.10 / 1.21.11。**这是 26.x 线上已经解决过的同一个形状**,那一条线用的就是同一个修复器,
本版只是把它移植过来。

### 机制是证明过的,不是推断的

写这个修复器之前先证明了一遍:把一份私有缓存里的 `class_11228$class_11230` **手工**加上 `DrawAccessor`
(并重算 CRC 让本模组接受这份缓存,用 `-KeepCache` 启动)—— 1.21.9 **9.5 s 到标题界面,`Cannot @Coerce` /
`InvalidInjectionException` / `Mixin apply … failed` 全部 0,且没有任何东西取代它们**。修好之后 Mixin 自己
导出的类(`-Dmixin.debug.export=true` 下的 `.mixin.out\class\net\minecraft\class_11228$class_11230.class`)
声明的接口是 `DrawAccessor`、方法 **13** 个(11 个是 record 自己的,另外 2 个正是 `fabric$pipeline()` /
`fabric$indexCount()`)—— 接口来自本模组、方法来自 Mixin,正是这个修复器要的分工。

### 本版在 1.21.1 上的冒烟测试

**PASS + 进世界**:标题界面 **18.3 s**,进世界 **21.6 s**。`Cannot @Coerce` / `InvalidInjectionException` /
`Mixin apply … failed` / `LVTGeneratorError` / `SugarApplicationException` 全部 **0**;修复器打印的是
「没有模组声明指向该类」,类原样交给 JVM。

### 停住:1.21.10 与 1.21.6 卡在标题界面之后,这不是本版造成的

1.21.10 到标题界面之后**没有进世界**:日志不再增长,窗口标题始终没有出现 ` - Singleplayer` 后缀,
`level.dat` 也没有在运行期间被重写,也就是 `--quickPlaySingleplayer` 从未被处理;所有失败计数器同样是 0。
这就是 `r214` 报告 §10 已经为 1.21(2.2.3 / 2.2.4)记下的同一类「标题界面之后停住」:本轮 1.21.6 也一样
(给足 240 s 余量,实际跑到 261 s 仍未过去),而 1.21.9 在同一处等了约 196 s 后过去了 —— 所以它更像一段很长、
很不稳定的等待,而不是一个确定的死锁。**它不是本版造成的**:修复器在 1.21.6 和 1.21 上是彻底的 no-op
(见下),1.21.10 只是本版第一次让它走到标题界面,才把这段等待暴露出来。**它的原因本轮没有查清,这里也不做解释。**

### 关于 `/ERROR` 行:它们不是 0,但每一条都是环境问题

本轮 `/ERROR` 计数不是 0:1.21.1 是 0,1.21.6 / 1.21.8 是 1,1.21.9 / 1.21.10 / 1.21.11 是 3。逐条看过,
全是测试台自己的环境问题,与 mixin、`@Coerce` 都无关:

```
[Render thread/ERROR]: Failed to load options            <- 测试台删掉了 options.txt 的处理,JsonSyntaxException
                                                            (MalformedJsonException at line 1 column 3)
[Download-2/ERROR]: Failed to fetch user properties      <- 离线:InvalidCredentialsException Status: 401
[Download-1/ERROR]: Failed to fetch Realms feature flags <- 离线:401
```

**更正(2.2.4 的计数缺陷)**:2.2.4 那两份笔记(1.21 与 1.21.1)里写过 `/ERROR` 0,那个数字**不是证据**:
当时测试台的计数正则写作 `'\] /?ERROR'`,而 PowerShell 把 `\]` 原样传了下去,于是 `[Render thread/ERROR]`
这类行**从来没有被匹配到过** —— `/ERROR 0` 是「没找到」,不是「没有」。本版把正则改成普通的 `/ERROR`
之后计数才第一次真的动起来(就是上面那 0 / 1 / 3)。**已发布的 2.2.4 正文本轮没有改写**,更正写在这里。

### 边界(别把这一版读大)

- **PASS 的定义是「到标题界面」**(日志出现 `Sound engine started`);只有明确写了「进世界」的那些行才声称进过世界,
  判据是集成服务端自己写的标记(`Starting integrated minecraft server version`、
  `Preparing start region for dimension`)加上运行期间被重写的 `level.dat` 与 `session.lock`;
- **每个臂只有一次启动、三个 jar(本模组 + OptiFine + Fabric API)、一个预置存档**:没有光影包、没有压测、
  没有长时间游玩、没有逐个模组跑兼容矩阵;
- 实机只跑了三个受影响版本(1.21.9 / 1.21.10 / 1.21.11)与三个冒烟臂(**1.21.1** / 1.21.6 / 1.21.8);
  **1.21 / 1.21.3 / 1.21.4 / 1.21.7 本轮没有启动游戏**,它们的 no-op 结论只来自上面那次离线字节扫描;
- 实机结论来自**本版同一提交**构建的候选 jar(与本版产物的差别只有 `fabric.mod.json` 里的版本串,
  已逐条目比对);本版 jar 没有再把这几版各跑一遍;
- **离线惰性核对**(本版新做的检查):逐版打开各版 Fabric API 里嵌的 `fabric-rendering-v1`,按字节搜
  `net/minecraft/class_11228$class_11230` —— 只有 1.21.9 / 1.21.10 / 1.21.11 有 `DrawAccessor` 指向它;
  1.21 / 1.21.1 / 1.21.3 / 1.21.4 整个 jar 里连 `class_11228` 都不出现,1.21.6 / 1.21.7 / 1.21.8 只有
  `GuiRendererMixin` 引用 `class_11228`、没有任何类引用 `$class_11230`。所以修复器在其余七版上要么无事可做,
  要么打印「没有模组声明指向该类」后原样离开。

## 2.2.4 的改动

**本版只改一个文件**:LocalSlotLayoutFix 重写(+488 / −90)。注册方式没动(仍是 desc = null),没有按类打补丁,
也没有新增修复器;这是把它原先那套「按槽位」排布换成了「按作用域」配对。

**为什么值得换**:它去掉的是一整类失败 —— 模组里 @Local(index = N) / Unable to find matching local 的 MixinExtras
糖失败。原先这个修复器按槽位逐个判断,只能把 OptiFine 自己多出来的局部变量往后推,**推不回**游戏被挪走的那些;
而 class_761.method_22710 曾因此被整条跳过(原话:would need 19 local slots moved, which is more than this fixer
understands (8); leaving the method alone),Porting Lib 的 client.LevelRendererMixin 随即在
@Local(index = 24) 上抛 SugarApplicationException: Unable to find matching local!。同一原因,
class_757.method_3192 的游戏表项原先也只回去一部分。

**怎么改的**:按**作用域(局部变量表项)**而不是按槽位配对 —— 两个方法的指令下标与局部变量名都不可比,
唯一可比的是两张表按槽位排下来的描述符序列,于是用最长公共子序列对齐,再**整槽位**搬运:某个槽位的表项都是游戏的,
就搬回游戏声明它的槽位(method_22710 里 27->24 就是把 PoseStack 放回去,@Local(index = 24) 因此能解析);
配不上的槽位搬到两个方法范围之外的尾部。**一个槽位绝不拆给两个目标**(实测:OptiFine 的 method_22710 里槽位 36
在 1079..1098 与 1130..1167 两段都装着 class_4597,而 1097 处的 GOTO 1130 跳过了 1129 的写入,拆开就留下一个
未初始化读,ASM 的数据流校验会报 Expected an object reference, but found .);只有生命周期不重叠的槽位才共用一个号。
**任何一步放弃都不改字节**,旧的按槽位方案保留给「某一侧完全没有局部变量表」的情况。

**离线零回归**(1.21.1,重写前后各跑一遍 verify-version.ps1 -Version 1.21.1,数字逐项相同):425 个补丁类 prepared、
425 verified、0 failed、0 个 ASM 校验问题;783 个 OptiFine 类 verified、0 failed;Refmap 缺失 0、LambdaScan DANGLING 0。
只有修复器自己的判断变了:class_761.method_22710 从「放弃」变成「对齐」,class_757.method_3192 把游戏 18 个表项全部放回。

**本版实测(冒烟测试:本版 jar + 与该版本匹配的 OptiFine + 与该版本匹配的 Fabric API,每次都用全新的 .optifine 缓存)**:

- `IN-WORLD`(标题界面 15 s、进世界 19 s,`/ERROR` 0)。同上两个方法都对齐:`class_761.method_22710` 41 / 64(含 `27->24`),`class_757.method_3192` 18 / 21;`SugarApplicationException` 与 `Unable to find matching local` 各 0。证据:`r214\logs\smoke-1.21.1\`

**边界(别把这一版读大)**:

- **The Twilight Forest + OptiFabric + OptiFine 在 1.21.1 上仍然起不来**,本版**没有**、也**不能**让它受支持。
  让 TF 卡住的是**另一个**阻塞点:porting_lib_blocks 的 client.LevelRendererMixin 里那个带隐式 Iterator
  判别符的 @ModifyVariable,@At(`"STORE`") 在 **OptiFine 自己的**方法体里有**两个同时活着的** Iterator 槽位
  (OptiFine 自己的帧表里本来就写着两条),而游戏原版方法里只有一个。**改槽位号无法改变这个歧义** ——
  重排前是 32:Iterator, 34:Iterator,重排后是 26:Iterator, 54:Iterator,条数一样。所以本版**不声称** TF 可用;
- **证据只到主界面/进世界这一层**:一次启动、一个实例、一个存档,没有光影包、没有压测、没有长时间游玩,
  也没有逐个模组跑兼容矩阵。1.21.6 / 1.21.7 的 OptiFine 预览构建本身「开光影就崩」,那是 OptiFine 自己的缺陷,
  与本版无关,本次也没有去开光影;
- **1.21.9 / 1.21.10 / 1.21.11 的失败不在本版**:这三版本轮没到标题界面,死在 fabric-rendering-v1 的
  GuiRendererMixin(Cannot @Coerce … class_11228 → …DrawAccessor),而**已发布的 2.2.3** jar
  在同一套模组里死在同一处。这三版的修复器都跑到了并给出了正确判断,但「到主界面」这一条**本轮未成立**;
- **离线回归只对 1.21.1 跑过**:425 / 425 / 0 与 783 / 0 是 1.21.1 的数字,其余九版本轮没有跑扫描器套件。

**更正**:2.2.3 的发布说明把这一处写反了 —— 说过「LocalSlotLayoutFix 是**按作用域**而不是**按槽位**重映射,
class_761.method_22710 的 19 个候选槽位超过它的 MAX_MOVES(8),所以放弃该方法」。**前两处说反了**:
那一版那个修复器是按槽位判断的,放弃的原因也不是 MAX_MOVES 这个常量。本版把这段历史说明改回准确。

## 2.2.3 的改动

**七处修复**:OptiFine 重编译改掉了四个类里的调用点、lambda 名与合成字段名,以及 FRAPI 的渲染器注册方式。
每一条都是对着补丁前后两份字节逐处比对定下来的,不是猜的。

- **`class_156`**(`net.minecraft.Util`)→ `RestoreVanillaMethodsFix(true, "method_29191")`。OptiFine 重编译时把同一个
  catch 块里的日志调用从 `Logger.error(String,Object)` 降成了 `Logger.debug(String,Object)` —— 整个方法只有一个操作码
  不同(异常表与其余部分逐字节相同)。而这个调用点正是 The Twilight Forest 的 `UtilMixin` 要重定向的那一个:重定向扫不到东西,
  注入以 `Scanned 0 target(s)` 失败,`Mixin transformation of net.minecraft.class_156 failed` —— 连标题界面都到不了,
  而且死在 OptiFine 的 `Reflector` 引导阶段。把原版方法体放回去才能修;
- **`class_638`**(`net.minecraft.client.world.ClientLevel`)→ `LambdaMethodRefFix()`。OptiFine 把游戏**自己注册**的那个
  colour resolver 方法记成了 `lambda$new$3`:描述符与 `BootstrapMethods` 槽位都没变,只有名字从 `method_23778` 换了。
  porting_lib 的 `ClientLevelMixin` 按名字找 `method_23778`,找不到目标,整个类变换失败。把 lambda 改回游戏的名字就行
  —— 因为它本来就**是**游戏注册的那个方法;
- **`class_761`**(`net.minecraft.client.render.LevelRenderer`)→ 两条。`LambdaMethodRefFix()`:游戏声明为
  `method_37365` 的 Runnable 体被重编译成了 `lambda$updateCameraAndRender$1`(描述符、访问标志、注册位置,
  以及混入用 `@ModifyArg` 包住的那个 `class_758.method_3211` 调用,全都没变,只有名字变了),C2ME 的视距修改因此失效。
  这里**故意不用** `RestoreVanillaMethodsFix("method_37365")`:那会把原版方法体放在 OptiFine 的 lambda 旁边,
  实际跑的仍是 OptiFine 那份,混入也就永远不会生效。另一个是 `LocalSlotLayoutFix(null, "method_22710")`,
  与 `class_757.method_3192` 同一个局部变量槽位错位的原因;
- **`class_3898$class_3216` 与 `class_3204$class_4077`** → `SyntheticFieldFix()`。OptiFine 把合成外层实例字段
  `field_17443` / `field_18255` 改名成了 `this$0`,而 C2ME 的 `@Accessor` 与 `@Shadow` 是按游戏的名字找的:
  找不到就会在**进世界**时让整个目标类变换失败(`…class_3898$class_3216 failed` / `…class_3204$class_4077 failed`),
  不是少画一笔那么轻;
- **FRAPI 的渲染器注册**:`RendererApiFallback` 原先用静态的 `Renderer.register(Renderer)` 注册占位渲染器,
  而本线的 Fabric API 里已经没有这个方法(用 `javap` 对着 `fabric-api-0.116.17+1.21.1` 里嵌的
  `fabric-renderer-api-v1-0.116.17.jar` 核过:`RendererAccess` 只剩 `registerRenderer` / `getRenderer` / `hasRenderer`)。
  查找抛 `NoSuchMethodException`,占位渲染器**从未注册**,于是每个用 FRAPI 的模组拿到的 `getRenderer()` 都是 null,
  The Twilight Forest 的 `ForceFieldModel` 在静态初始化时就死在上面。现在走
  `RendererAccess.INSTANCE.registerRenderer(Renderer)`(旧写法保留为第一次尝试,失败路径仍不致命并照旧打日志)。

### 实测结果

- **C2ME**:能到标题界面,进世界连续跑 **113 秒,零条 `[ERROR]`**;
- **The Twilight Forest**:不带 OptiFine 时,它现在能加载了(此前连标题界面都到不了)。**但它仍未被完整支持** ——
  它在本版剩下的那个 OptiFine 侧阻塞点是 `LocalSlotLayoutFix` 的一个已知局限:这个修复器是**按作用域**而不是
  **按槽位**重映射的,`class_761.method_22710` 的 19 个候选槽位超过它的 `MAX_MOVES`(8),于是它直接放弃该方法,
  porting_lib_base 的 `LevelRendererMixin` 仍然失败。**本版没有修这一条**,别把它读成"The Twilight Forest 已受支持"。

### 已知限制(本版没有修的部分)

- `LocalSlotLayoutFix` 对上面那个方法放弃处理,原因与后果见上一条;
- 约 **36 个**被补丁的类里仍带着**未注册**的 `vtN` / `this$N` 合成字段。它们现在拦不到本模组自己,但对**别的模组**
  是敞开的:任何按名字找这些字段的 `@Accessor` / `@Shadow` 都会在进世界时让目标类变换失败 —— 上面那两条合成字段的问题
  就是同一种,只是它们已经被注册进修复器了。

### 更正:`conflicts` 与 `breaks` 的实际行为(2.2.2 那条说明写反了)

2.2.2 的小节里曾断言"两个字段都只产生警告、不阻止游戏启动"。**实测不是这样**:
**`conflicts` 条目只警告**(Fabric Loader 0.19.5 的 `ModSolver` 里,`CONFLICTS` 分支连约束都不加,只有一句
`// TODO: soft negative dep?`),而 **`breaks` 条目是被执行的**:当它点到**已存在**的模组时,依赖求解器给出
`NEG_HARD_DEP`,加载器**拒绝**这个组合,而不是放行。一次记录到的运行里就写着
`NEG_HARD_DEP optifabric_reforged 2.2.2 {breaks sodium}`。所以把 sodium 写进 `breaks` 是**闸门**,不是声明;
只写在 `conflicts` 里才是"只警告"。

## 2.2.2 的改动

- **`sodium` is now declared in both `conflicts` and `breaks`** in `fabric.mod.json`.
  **更正**:本行原来说"两个字段都只产生警告、不阻止游戏启动" —— 那句话是错的,按加载器实际行为与源码复核后的结论见上面 2.2.3 一节。

## 2.2.1 的改动

- **换过类之后不再让 Mixin 用替换之前的类元数据**:OptiFabric 把游戏类替换成 OptiFine 打补丁后的字节码时,Mixin 已经为这些类
  缓存了一份 `ClassInfo`(按 Fabric 的启动顺序,缓存里存的可能是**游戏自己**那份)。两份字节只要在某个成员上不一致,`Locals`
  (`@ModifyVariable` 与局部变量捕获)就查不到正在变换的方法,整个类变换失败并抛
  `LVTGeneratorError: Could not locate method metadata for method_3196 generating LVT in net/minecraft/class_757`,
  Fabric 侧只报一句笼统的 `Mixin transformation of net.minecraft.class_757 failed`。这个异常抛在 `preInject` 阶段,
  **`require` / `expect` 都拦不住**,模组侧无法自行绕过 —— 已发布的 2.2.0 就是这样在加载期丢掉 ShoulderSurfing 的;
- **做法**:`GameTransformerHook` 记住真正被替换过的类,装好后丢掉这些类的 Mixin 缓存条目(内部名形式、反射、每类至多一次、
  失败不致命),让 Mixin 按它真正拿到的那份字节重建元数据;没有被替换的类不动;
- **实测**(1.21.1,OptiFine HD U J1,ShoulderSurfing 5.2.0、ForgeConfigAPIPort 21.1.6 与 Fabric API):2.2.0 的 jar 6 秒
  死在 `Mixin transformation of net.minecraft.class_757 failed`;本版日志出现 `Dropped 47 of 425 Mixin class metadata entries`,
  进标题界面、进存档,光影包正常编译 27 个世界内程序,无注入错误、无崩溃报告。

本版只改了这一处,2.2.0 的其余结论继续适用。

## 这个版本是什么

把 OptiFine 完整接入 Fabric:OptiFine 的补丁在构建期离线应用到 Minecraft jar(官方名 -> intermediary 重映射、
补丁类修复、逐类双向校验),运行时把补丁类交给 Fabric Loader,光影、连接纹理、缩放等 OptiFine 功能照常工作。
本 jar 只适配 Minecraft 1.21.1,不要跨版本使用。

## 2.2.0 的改动

- **`mods/` 里没有 OptiFine 时不再默默启动**:以前游戏照常起来,只是 OptiFine 的功能全不在,得你自己想到是少了什么。
  现在标题界面打开前会看模组自带的 OptiFine 支持表,并弹出一个界面写明本 MC 版本需要哪个构建、哪个文件名;
  **完全没装时每次启动都会出现**(`继续返回主菜单` 只放行这一次会话,不写任何"已确认"记录),直到装上为止;
- **装了但比支持表更旧的预览版时**:游戏**照常继续运行**,界面只是告诉你有更新的一版;点 `仍要继续` 会把这个构建记进
  `config/optifabric-mismatch-ack.txt`,同一个构建不再重复打扰。**只有预览版(带 `_pre`)比表里旧才会提示** ——
  你已经装了正式版就不会被打扰,哪怕之后有更新的正式版;
- **可以从 OptiFine 官网直接下载**:界面上的 `下载 OptiFine` 把该 MC 版本需要的那个 jar 取回并放进 `mods\`。地址栏默认是官网
  的两步流程(`optifine.net/adloadx?f=<文件名>` 页面带一次性令牌,jar 由同一台主机的 `downloadx?f=<文件名>&x=<令牌>` 给出);
  **本模组不带任何第三方/镜像地址,也不会自动回退** —— 官网这条路失败就如实写出原因,按钮换成 `打开官网下载页` + `重新检查`,
  由你自己下好放进去再点重新检查。地址栏也可以填你自己的地址(支持 `{mc}` / `{type}` / `{patch}` / `{file}` 占位符);
- **下载完成后先问,再重启**:不再直接退出游戏,而是问一句 `是否重启游戏使 OptiFine 生效?`;`立即重启` 用**启动本进程时的
  那条命令行**重新拉起游戏再结束自己(Windows 下 JVM 读不回自己的命令行 —— 游戏目录里带个空格就读不出来 —— 所以用 JNA 的
  `GetCommandLineW` 取原始命令行、`CreateProcessW` 原样重跑;非 Windows 走 `ProcessHandle`);启动器把命令行藏起来时会如实说明,
  让你自己重启。JNA 5.14.0 只是 `compileOnly` 依赖,不打包进 jar;
- README / `README_CN.md` 新增「OptiFabric 版本 → Minecraft 版本 → 需要的 OptiFine 构建」表(与 `release\notes\`、
  模组内置支持表三处由 `release\version.ps1 -CheckSupport` 核对);README 与 `docs/DEVELOPMENT.md` 里的第三方镜像链接已全部移除。

本版没有改动补丁管线与任何 fixer(改的是"缺 OptiFine 时怎么办"这条路径),2.1.0 那套离线/真机校验结论继续适用。

## 2.1.0 的改动

- **崩溃报告现在自己说明原因**:最常见的那类崩溃 `NoClassDefFoundError: Could not initialize class
  net.optifine.reflect.Reflector` 源于**别的模组的 mixin 没能注入到 OptiFine 改写过的类里**,OptiFine 的崩溃报告器读自己
  版本号时正好撞上它,于是报告停在 `Reflector`、不说是哪个模组。现在报告里会多出一节 `OptiFabric: OptiFine / mixin conflict`,
  说明发生了什么、真正的模组名要加 `-Dmixin.debug=true` 才会被 Mixin 打出来、已知的三类,以及怎么办 —— 删掉那个模组,
  或者不用 OptiFine。README 的"支持与排查"里有同一套做法;
- **SophisticatedCore / Sophisticated Backpacks 装上不再启动崩**(1.21.1 实测,配该整合包):它的 `ParticleEngineMixin`
  要求的 `class_702.method_34020` 被 OptiFine 重编译折进了 lambda,补丁后的类里没有这个方法;本版把原版方法体补回去当
  注入目标(死代码),注入不再失败;
- **两个故意不修**:CarryOn(注入在 OptiFine 换掉的调用点上)与 ShoulderSurfing(要求 `Camera` 里调用点数量正好相等),
  它们的注入在 OptiFine 改写后的字节码里对不上,只能在模组侧改。清单见 `docs/DEVELOPMENT.md` 的冲突表;
- 另修:`LocalSlotLayoutFix` 的类型判定与布局比较不再猜错(`long`/`double` 不再被当成一个槽位)。

细节见仓库根目录 `CHANGELOG.md` 的 2.1.0 一节。

## 2.0.0 的改动

- **mod id 改为 `optifabric_reforged`(显示名 OptiFabric Reforged)**:先删掉 `mods/` 里旧的
  `OptiFabric-<版本>+mc1.21.x.jar` 再放新的 —— 两个 id 同时存在时 Fabric 会同时加载两份,OptiFine 会被打两遍补丁;
- **修掉 Architectury 崩溃**:OptiFine 在 `GameRenderer.render` 中间插入了它自己的两个局部变量,把原版
  `Matrix4f`/`Matrix4fStack`/`GuiGraphics` 顶高 1–2 个槽位,而 Mixin 的局部变量捕获是**按槽位顺序**比对的,
  于是整个类变换失败(`InjectionError: LVT ... has incompatible changes at opcode 601`)。新增
  `LocalSlotLayoutFix` 把 OptiFine 自己的局部变量挪到局部变量区末尾(1.21.1 实测 7→15、10→16),只改槽号不改语义;
  1.21.1 + architectury 13.0.11 已实测可进世界,且不需要任何配置文件;
- 顺带修:`processResources` 未把 mod id / 显示名声明为 task inputs,改名曾静默不生效。

细节见仓库根目录 `CHANGELOG.md` 的 2.0.0 一节。
## 本版修复(1.0.0)

- 补丁管线:保留 OptiFine 自带栈帧、必要时只对改动过的类重算;注入点(调用/句柄)被 OptiFine 改写时逐一对齐;
- 工厂调用错位、HUD 图层 lambda 名被改写、区块 section 位置缺失、HUD 与区块路径的类型不匹配等逐一修复;
- 抗锯齿(1.21.9 / 1.21.10):OptiFine 的 FXAA 走它自己的 post chain,而 1.21.8 起的构建不再带那个老位置的文件,
  且游戏那条同名 post effect 会画出黑屏 -> 管线补写该文件并让 OptiFine 自己的链独占;
- 多人(1.21.8):Fabric 渲染器 API 的占位实现不再抛异常终结会话。

## 安装

1. 安装 Fabric Loader 0.19.5(Java 21 或更高);
2. 把本 jar 和对应版本的 OptiFine 一起放进 `mods\`;
3. 启动一次即可(首次会重建 `.optifine` 缓存)。

## 已知限制

- **光影包请用 Complementary 等主流包**:`photon_v1.2a.zip` 在 1.21.6 起的 OptiFine 上不工作(包与 OptiFine 的版本差异);
- 1.21.8 起 Indigo 让位于 OptiFine,走 Fabric 渲染器 API 的方块实体由原版路径绘制,个别情况下可能画不出来;
- 1.21.6 / 1.21.7 的 OptiFine 预览构建自身有缺陷,本移植不提供支持(见上)。

## 校验

`OptiFabric-2.2.14+mc1.21.1.jar` — 812349 字节

`SHA-256: 6DB14257ADA346FD74221F3B066C05CF9833BE39EB5D983931346CC9B73C92B2`
---

## 这一版有两条产物(装之前请看这一段)

- `OptiFabric-2.2.14+mc1.21.1.jar` —— **上架到 CurseForge / Modrinth 的那一份(默认产物,没有后缀)**:
  运行时**不下载任何东西**,也**不启动任何进程**(平台的规则不允许模组在游戏运行时下载文件或启动进程)。
  OptiFine 要你自己从官网 <https://optifine.net/downloads> 下载,把 jar 放进这个 mod 旁边的 `mods` 文件夹;
  装好之后**手动重新启动游戏一次**(本产物不会自动重启)。找不到 OptiFine 时游戏仍会走到标题界面,并按 **2.1.0 的老办法**
  弹出一个确认对话框说明原因(标题 `There was an error loading OptiFabric!`,正文就是 2.1.0 那三行原话,只把写死的 Minecraft
  版本换成正在运行的版本);对话框的两个按钮**只做复制**(mods 文件夹路径 / 帮助链接,内部错误时是堆栈或 `logs` 路径),
  **不打开文件夹、不打开网页、不启动任何进程**。装了**比本产物认识的最新构建更旧的预览版**时,同一个对话框**每个构建只弹
  一次**(已提示的构建记在 `config/optifabric-mismatch-ack.txt`);同版、更新版与任何正式版**从不提示**。
- `OptiFabric-2.2.14+mc1.21.1-full.jar` —— **只放在 GitHub 上的便利版**:保留「自动从 optifine.net 下载」与
  「自动重启」这两项。除了这两项,它与默认产物是同一版修复。

两条产物的 **mod id 相同**,所以配置与世界通用,但**只能装其中一个**。2.2.14 的默认产物构建自 `8bffabb`,
`-full` 产物构建自 `bac218b`(2.2.13 分别是 `946e302` 与 `0e38d1a`;`-full` 没有单独的 tag,与默认产物共用同一个 release)。


> **上架 jar 的合规声明**(逐 jar 机器核对,扫描表见 `cf-resume\cp-table.md`):
>
> This build neither downloads anything nor starts any process at runtime. `java.net.URL`/`URLClassLoader` are
> used only to read a local `file:` jar that the user placed in `mods/`; there is no HTTP client, no
> `ProcessBuilder`/`ProcessHandle`/`Desktop`/`CreateProcess`, and no `com.sun.jna`.
>
> 也就是:运行时**不下载任何东西**、**不启动任何进程**;`java.net.URL`/`URLClassLoader` 只用于读取用户自己放进 `mods/`
> 的本地 `file:` jar。每个上架 jar 的下载 / 进程 / 启动 token 都是 **0**;`-full` 那些 jar 是这次扫描的**正对照**
> (它们确实带着下载器与自动重启,所以只放在 GitHub 上)。

> **`System.exit` 那一行要按方法看,不能按 token 看**:`System/exit` 这个 token 在 class 文件里**根本不存在** —— 一次调用
> 是常量池里 `java/lang/System` 加 `exit:(I)V` 的 Methodref,所以早先按这个 token 扫出来的「0」是**选错 token 的假象**。
> 用 `javap` 逐方法解析后,每个上架 jar 的退出原语只有这两处:`patcher/LambdaRebuilder#main` 的 `System.exit(1)`
> (那个类的离线命令行入口,只有直接运行它才会走到),以及 reforged 线 `mod/C2meCompat#apply` 的 `System.exit(0)`
> (垫片自己结束本次启动,**不启动任何进程**、不联网)。上架 jar **没有 `Runtime.halt`**;`Runtime.halt(0)` 只出现在
> `-full` 的下载器与 `-full` 的垫片里。逐 jar 的解析表见 `cf-resume\cp-table.md`。
