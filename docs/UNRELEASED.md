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

## 更正:这份名单是"**上界**",不是"缺失清单"

上面那份提交区间只能说明"发布提交之后有哪些提交",**不能**说明它们都不在产物里 —— 其中一部分在重传那一轮被**重新构建并重传**过,
因此**已经**进了同版本 jar。以本仓库的实际比对为准:

| 线 | 已**在**产物里的 | 仍**不在**产物里的 |
|---|---|---|
| 1.21.x | help/issues 链接修复(`3035c87`);重传轮的其余内容 | 本表前面列的 6 处代码修复(已用字节码标记证实:新构建有 `namesTarget`/`assignedBefore`,已发布 jar 两者皆无) |
| 26.x | help/issues 链接修复(`d0b61f6`) | `assignedBefore`(槽位守卫)与 `namesTarget`(`targets()` 形式) |
| wip/1.20.6 | help 链接修复(`6edbde8`)+ **`ClassCache` 资源拆分**:该线**当前分支构建出的 jar 与该 release 的 asset 逐字节相同**(732,367 B / `935c7281…`),即产物**就是**现在的分支构建 | 无(以 jar 与源码比对为准) |
| main/1.20.6 | 同 wip:help 链接(`2530c2f`)+ `ClassCache` 资源拆分,分支构建与 asset 逐字节相同(718,215 B / `89748ef4…`) | 无 |

**判定规则(下次照此办理)**:先比 jar 与源码,再决定某条改动算"已发布"还是"未发布" —— 提交名单只是**候选集**。

## 便利分支（convenience/*）侧的补充：两处只在便利分支上缺失的修复

下面两处**不在这条 store 线上**，而在为它构建 `-full` 的便利分支上 —— 发行时两个产物来自两边，所以两边都要有。

| 便利分支 | 缺的是什么 | 现状 |
|---|---|---|
| `convenience/26.x-2.2.8` | `AddInterfaceFix` 只读 `@Mixin(targets = "…")`，没读 `value`（`Type`/`Type[]`）形式 —— store 线早已修，这条分支没跟上 | **已补**（提交 `4449eaf`）：源码 `namesTarget=3`，编译通过 |
| `convenience/1.20.6-1.1.6` | 下载器的**主机精确比较与 https 要求**整块不在源码里：早前在这里改过一次，随后一步"回退从 store 抄来的文件"的循环把它一起退回去了（下载器不在该步的保留名单里），而我当时把它**报告成了"已应用"** —— 这是本项目里一次真实的误报 | **已补**（提交 `cc8bb3b` 补回主体，`118d4fb` 补上 `Locale`/`Set` 两个 import 使它能编译）：源码 `requireHttps=3 / isOfficial=2 / url.contains=0`，构建出的 jar 里三个标记齐全；`-full` 现为 736,694 B / `74dc0514…`（已发布那份是 736,349 / `d4fa3ddf…`） |

**由此固定下来的两条规矩**：

1. **同一处修复要在 store 线与便利分支上分别确认**（`git log` 在该分支上能查到那次提交才算确认），发行前用"下载回来的 jar 里有没有对应标记"做最终验收；
2. **提交前必须先构建通过** —— `convenience/1.20.6-1.1.6` 上曾经推过一个编译不过的提交，因为当时的脚本没有把"编译成功"当成提交前提。

### convenience/26.x-2.2.7

This branch, which builds the -full jars for 2.2.7, still had the older AddInterfaceFix reader: it only
looked at the targets member of the Mixin annotation. It reads the value form as well now (commit 770dcfa),
verified by compiling and by the namesTarget helper appearing in the class.

## Fix coverage sweep (checked 2026-10-07, and to be re-run before each release)

Every tree that carries one of the fixes below has it; where a column reads n/a the file itself is not on
that line, which is by design and not a gap. The sweep is three greps over src/main/java:

| tree | targets() in AddInterfaceFix | assignedBefore in OptifineJarFixer | requireHttps in OptifineDownloader |
|---|---|---|---|
| 1.21.x | ok | ok | n/a |
| 26.x | ok | ok | n/a |
| wip/1.20.6, main/1.20.6 | n/a | n/a | n/a |
| conv 1.21.x-2.2.13, -2.2.12, -2.2.11 | ok | ok | ok |
| conv 26.x-2.2.8, -2.2.7 | ok | ok | ok |
| conv rf-1.1.5, rf-1.1.6, 1.20.6-1.1.6 | n/a | n/a | ok |

Two of those entries were added late, both after a check that compared the branch sources with the published
jars: convenience/26.x-2.2.8 and convenience/26.x-2.2.7 were missing the annotation value form, and
convenience/1.20.6-1.1.6 had lost the downloader checks to a step that reverted files copied from the store
branch. All three are in now, each verified in the jar that branch builds.
