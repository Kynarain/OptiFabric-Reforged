# 尚未发布到产物的修复（待并入下一版本）

本文件列出**只在分支上、还没进任何已发布 jar** 的改动。等下一版（1.21.x/26.x 线为 `2.2.14`，两条 1.20.6 线为 `1.1.7`）发行时，
这些改动会随构建一起出去；在那之前，**已发布的 `v2.2.13*` / `v2.2.8*` / `v1.1.6*` 里没有它们**。

清单只写"改了什么、为什么、怎么验证"，不写结论性的"已修好"——因为**产物还没带着它出去**。

| # | 文件 | 改动 | 为什么 | 验证方式 |
|---|---|---|---|---|
| 1 | `patcher/fixes/` 之外的 `OptifineDownloader.java`（仅 conv 分支） | `verifyIdentity` 的 catch 从 `IOException` 扩到 `IOException \| RuntimeException` | `OptifineVersion.parseJarType` **自己就会抛 `RuntimeException`**（`OptifineVersion.java:84`），jar 类型判不出来时还会走到对 null 的 switch；这两种都会**绕过删除**，把不可用的 jar 留在 `mods/` —— 而那正是该方法存在的意义 | 编译 ✓；行为只能在真实下载回读失败时触发，**未做端到端实测** |
| 2 | `mod/OptifineJarFixer.java` | 删 `ICONST_1 + ISTORE n` 前新增 `assignedBefore(...)`：槽位此前没有值就**不动**并把原因打到 stderr | 被删的 store 若为该槽首次赋值，紧随的 `ILOAD n` 就读未初始化局部变量，而那里用 `ClassWriter(0)` 不会重算帧 ⇒ 整个 `Shaders` 类加载失败 | 编译 ✓；1.21.x 与 26.x 的 11 个单元测试 ✓（该路径不在测试覆盖内，**实测未做**） |
| 3 | `patcher/fixes/DelegatingConstructorFix.java` | `inline()` 产物顶替 `delegating` 前，要求 `replacement.desc.equals(delegating.desc)` | 产物形状取自 `target.desc`，形状不等时原来的 delegating 构造器**凭空消失**，调用方 `NoSuchMethodError`；这条同时挡下 `parameterSlot` 取"首个同类型参数"的错值 | 编译 ✓；单元测试 ✓；**未在世界内实测** |
| 4 | `mod/RendererApiFallback.java`（26.x） | 只加注释，不改逻辑 | 记录为何该线不重复 1.21.x 的 `RendererAccess.INSTANCE` 回退：`install()` 先判 `indigoWillRegister()`，而该线 Indigo 就是地形渲染器，直接 return，注册尝试到不了 | 编译 ✓ |
| 5 | `patcher/fixes/AddInterfaceFix.java` | `targets()` 同时读注解的 `value`（`Type`/`Type[]`），不再只读 `targets = "…"` | `@Mixin(SomeClass.class)` 形式的接口原先**静默漏掉**，其抽象方法也就不会补进目标类 | 编译 ✓；单元测试 ✓；**未实测该形式的具体接口** |
| 6 | `patcher/fixes/SyntheticFieldFix.java` | 位置配对新增前置条件："两边字段数相同"才信任位置；注释写明"同类字段互换"是剩余盲区 | 原先只判 `index < minecraft.fields.size()`：重编译后字段数不同会**整体错位**却照样配对成功 | 编译 ✓；单元测试 ✓ |
| 7 | `patcher/fixes/DelegatingConstructorFix.java` | 只加注释 | 写明 `findSuperCall` 的边界（`super(new X())` 需数据流才能区分）及"若真遇到会是链接错误而非静默错值" | 编译 ✓ |

## 已发布物与仓库的当前差异（重要）

- 上面 6 处**代码**改动都在**分支**上；`v2.2.13*`、`v2.2.8*`、`v1.1.6*` 的 jar 是**改前**构建的 ⇒ 几处**只在异常输入下才触发**的鲁棒性缺口仍在那些 jar 里。
- **已同步**：`MixinTitleScreen` 的 help/issues 链接（仓库名 404 + 分支写死）**已经重传进所有 14 个 release 的两个 asset**，手册 / `release/notes/*` / 取件目录也已随之更新（`7134620`、`d099b49`）。

## 下一版发行时的检查项

1. 本表 1–7 全部随构建出去（`dist/` 里逐线核对）；
2. `docs/UNRELEASED.md` 在发行后**清空为一条"已并入 X"**，不要留旧条目；
3. 同步本文件的同名副本到 26.x、wip/1.20.6、main（现在只有 1.21.x 这一份）；
4. 快照分支：`store/1.21.x-2.2.14`、`store/26.x-2.2.9`、`store/1.20.6-reforged-1.1.7`、`store/1.20.6-1.1.7`。

## 关于标了"未做实测"的那几行

这不是遗漏,而是**用现有装置测不到**,逐条说清原因与发行后的验证方式:

- **`AddInterfaceFix` 的 `value` 形式**:要让它生效,需要一个**用 `@Mixin(SomeClass.class)` 声明接口、且目标正好是该补丁类**的模组。本项目比对过的 FAPI 接口都用 `targets = "…"` 形式,所以修复前它是"静默漏掉",修复后**也找不到一个可观测的对照样本** —— 只能等真实模组出现时验证。
- **下载器 `verifyIdentity` 的异常分支**:要触发它,需要**真的下载出一个无法回读的 jar**(或篡改缓存里的那份),现有实例里没有这条路径。它属于"只在失败时才会走到"的代码。
- **`OptifineJarFixer` 的槽位守卫**与**`DelegatingConstructorFix` 的形状断言**:两者都只在 **OptiFine 特定构建形态**下才会走到,要触发得先构造对应的字节码。它们属于"**能验证,代价是一次专门的字节码夹具**",而不是"无法验证"。

因此这几项的后续动作是:①真实用户日志里若出现对应的 stderr 行(`[OptiFabric] … left alone …` / `… no earlier value …`),即为**已经被触发**;②否则在下一版发布说明里继续写"未实测",不以"已修好"叙述。
