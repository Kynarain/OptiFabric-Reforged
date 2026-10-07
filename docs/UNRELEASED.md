# Not yet in a released jar

Everything listed here is on this branch and in no released artifact of this line. The list is the
commit range itself, produced with `git log <release commit>..HEAD`, so it cannot drift from the history;
the release commit each range starts at is named below.

- line: 26.x
- last release commit on this line: `e903703`
- one of those commits, the help and issues link fix, has already been rebuilt and re-uploaded into
  every published asset of that release, so the jars do carry it: `d0b61f6`
- everything else is branch only until the next version is cut

The fuller table for this port, with the reason and the verification for each fix, is in
`docs/UNRELEASED.md` on the 1.21.x branch; the entries that apply to more than one line are the
shaderpack slot guard, the delegating constructor shape check, the interface annotation form and the
synthetic field count guard.

## Commits

- `6122e6e` 2026-10-07 - Record why this line does not repeat the RendererAccess fallback
- `87ea418` 2026-10-07 - AddInterfaceFix: read the annotation form that names its target as a class
- `0aa7b04` 2026-10-07 - Two of the smaller gaps the review left open, closed
- `d099b49` 2026-10-07 - Records: the sizes and hashes of the refreshed assets
- `d0b61f6` 2026-10-07 - Help link: the repository in the URL did not exist
- `a37fac3` 2026-10-07 - The unit tests from the 1.21.x line, and the ClassCache resource split
- `3ba9bca` 2026-10-07 - Metadata touches: contact points at this project, commons-lang3 is declared

## Checks before the next release

1. every fix above is in the built jars (compare the class files, not the commit list);
2. this file is replaced by a one line "merged into <version>" note, not left as it is;
3. a store/<line>-<version> snapshot branch is created at the release commit.

## 关于标了"未做实测"的那几行

这不是遗漏,而是**用现有装置测不到**,逐条说清原因与发行后的验证方式:

- **`AddInterfaceFix` 的 `value` 形式**:要让它生效,需要一个**用 `@Mixin(SomeClass.class)` 声明接口、且目标正好是该补丁类**的模组。本项目比对过的 FAPI 接口都用 `targets = "…"` 形式,所以修复前它是"静默漏掉",修复后**也找不到一个可观测的对照样本** —— 只能等真实模组出现时验证。
- **下载器 `verifyIdentity` 的异常分支**:要触发它,需要**真的下载出一个无法回读的 jar**(或篡改缓存里的那份),现有实例里没有这条路径。它属于"只在失败时才会走到"的代码。
- **`OptifineJarFixer` 的槽位守卫**与**`DelegatingConstructorFix` 的形状断言**:两者都只在 **OptiFine 特定构建形态**下才会走到,要触发得先构造对应的字节码。它们属于"**能验证,代价是一次专门的字节码夹具**",而不是"无法验证"。

因此这几项的后续动作是:①真实用户日志里若出现对应的 stderr 行(`[OptiFabric] … left alone …` / `… no earlier value …`),即为**已经被触发**;②否则在下一版发布说明里继续写"未实测",不以"已修好"叙述。
