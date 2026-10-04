# OptiFabric 2.2.3+mc1.21.9

**Minecraft 1.21.9** / Fabric Loader 0.19.5 / Java 21+ / 需求 OptiFine `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar`

状态:**已实测正常(含抗锯齿)**

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

**离线零回归**(1.21.1,重写前后各跑一遍 erify-version.ps1 -Version 1.21.1,数字逐项相同):425 个补丁类 prepared、
425 verified、0 failed、0 个 ASM 校验问题;783 个 OptiFine 类 verified、0 failed;Refmap 缺失 0、LambdaScan DANGLING 0。
只有修复器自己的判断变了:class_761.method_22710 从「放弃」变成「对齐」,class_757.method_3192 把游戏 18 个表项全部放回。

**本版实测(冒烟测试:本版 jar + 与该版本匹配的 OptiFine + 与该版本匹配的 Fabric API,每次都用全新的 .optifine 缓存)**:

- **没到标题界面**:死在 `fabric-rendering-v1` 的 `GuiRendererMixin` —— `Cannot @Coerce argument type net.minecraft.class_11228$class_11230 at index 4 to …DrawAccessor`。**这不是本版引入的**:同一套模组、同一个 Fabric API,换成**已发布的 2.2.3** jar 也在同一处死(`r214\logs\ctl-1.21.9-released-2.2.3\`)。本版的修复器**跑到了**:`class_761.method_22710` `Realigned … 16 of OptiFine's 19 table entries … (slots 12->30, 13->12 … 25->23, 26->24 …)`,`class_757.method_3192` 报 `needs no local slots moved`(配对 17 / 18)。所以「能到主界面/进世界」这一条对 1.21.9 **本轮未成立**,原因在本版之外。证据:`r214\logs\smoke-1.21.9\`

**边界(别把这一版读大)**:

- **The Twilight Forest + OptiFabric + OptiFine 在 1.21.1 上仍然起不来**,本版**没有**、也**不能**让它受支持。
  让 TF 卡住的是**另一个**阻塞点:porting_lib_blocks 的 client.LevelRendererMixin 里那个带隐式 Iterator
  判别符的 @ModifyVariable,@At(`"STORE`") 在 **OptiFine 自己的**方法体里有**两个同时活着的** Iterator 槽位
  (OptiFine 自己的帧表里本来就写着两条),而游戏原版方法里只有一个。**改槽位号无法改变这个歧义** ——
  重排前是 32:Iterator, 34:Iterator,重排后是 26:Iterator, 54:Iterator,条数一样。所以本版**不声称** TF 可用;
- **证据只到主界面/进世界这一层**:一次启动、一个实例、一个存档,没有光影包、没有压测、没有长时间游玩,
  也没有逐个模组跑兼容矩阵。1.21.6 / 1.21.7 的 OptiFine 预览构建本身「开光影就崩」,那是 OptiFine 自己的缺陷,
  与本版无关,本次也没有去开光影;
- **1.21.9 / 1.21.10 / 1.21.11 的失败不在本版**:这三版本轮没到标题界面,死在 abric-rendering-v1 的
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
## 1.1.2 修了什么

**抗锯齿之前是坏的(或干脆没生效),这一版把它修好了。**

- **病根**:管线会删掉 OptiFine 自带的 `assets/minecraft/post_effect/fxaa_of_{2,4}x.json`(以为这条链该走
  1.21.6 之前的老位置),而事实上链是由**游戏自己的后处理链注册表**按 `minecraft:fxaa_of_2x` 从 `post_effect/`
  解析的(OptiFine 把 `ShaderManager` 改成在那儿注册)。文件被删了,于是每次资源重载都刷一条
  `Resource not found: minecraft:post_effect/fxaa_of_2x.json`,一旦动抗锯齿(或切光影包 —— 两者都会重载光影)
  就 `Failed to load post chain: minecraft:fxaa_of_2x`,界面上就是"重载资源失败"。
- **现在**:不再碰 OptiFine 的后处理文件,链由游戏按原样解析。
- **顶点着色器(本版本特有)**:游戏从 1.21.9 起改用 `gl_VertexID` 生成全屏三角形、**不再提供 `Position`
  顶点属性**,而 OptiFine 那两个预览构建的 `fxaa_of_*.vsh` 还在读 `Position` → 顶点塌成一点 →
  当初"一开抗锯齿就整屏黑"就是这么来的。管线会把这两个 `.vsh` 改写成同一套全屏三角形写法
  (`SamplerInfo`/`FxaaConfig` 两个 uniform 块与 FXAA 的 `posPos` 计算**原样保留**,片段阶段还在读它们)。
- 判据都是文件内容(游戏那份 `core/screenquad.vsh` 是否用 `gl_VertexID`、OptiFine 那份 `.vsh` 是否还在读
  `Position`),所以不需要按版本列表维护:**本 jar** 属于哪一档已由上面的命令行验证过。

## 这个版本是什么

把 OptiFine 完整接入 Fabric:OptiFine 的补丁在构建期离线应用到 Minecraft jar(官方名 -> intermediary 重映射、
补丁类修复、逐类双向校验),运行时把补丁类交给 Fabric Loader,光影、连接纹理、缩放等 OptiFine 功能照常工作。
本 jar 只适配 Minecraft 1.21.9,不要跨版本使用。

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

`OptiFabric-2.2.3+mc1.21.9.jar` — 899806 字节

`SHA-256: B8BF250AD8D4E31423AF0DA4013CB03F16840DF34D9E3ACB8E911C59BF870ED0`