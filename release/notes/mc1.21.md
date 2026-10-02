# OptiFabric 2.2.1+mc1.21

**Minecraft 1.21** / Fabric Loader 0.19.5 / Java 21+ / 需求 OptiFine `preview_OptiFine_1.21_HD_U_J1_pre9.jar`

状态:**已实测正常**

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
本 jar 只适配 Minecraft 1.21,不要跨版本使用。

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

`OptiFabric-2.2.1+mc1.21.jar` — 791768 字节

`SHA-256: BE66F30C35C8E35EEF728A57C2E6C8ADA8340D5DF5A0AC51BC4FFE802A679B48`