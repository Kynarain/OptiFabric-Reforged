# OptiFabric 2.0.0+mc1.21

**Minecraft 1.21** / Fabric Loader 0.19.5 / Java 21+ / 需求 OptiFine `preview_OptiFine_1.21_HD_U_J1_pre9.jar`

状态:**已实测正常**

## 这个版本是什么

把 OptiFine 完整接入 Fabric:OptiFine 的补丁在构建期离线应用到 Minecraft jar(官方名 -> intermediary 重映射、
补丁类修复、逐类双向校验),运行时把补丁类交给 Fabric Loader,光影、连接纹理、缩放等 OptiFine 功能照常工作。
本 jar 只适配 Minecraft 1.21,不要跨版本使用。

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

`OptiFabric-2.0.0+mc1.21.jar` — 746192 字节

`SHA-256: 0E10612B7A1D148E08BEF2187FDD474D8582FBAFBC3DB19B64A46436D22EC4E0`