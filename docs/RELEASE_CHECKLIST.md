# 发行核对表（下一版：1.21.x → `2.2.14`；26.x → `2.2.9`；两条 1.20.6 线 → `1.1.7`）

这份表存在的理由：发行时每一步都能**对着表走、当场核对**，不用再靠记忆。表里的每条命令都在本仓库里被实际跑过（干跑或正式跑过），
"预期"一列写的是**看什么才算过**。

## 0. 共通前置（每条线各自做）

| 步骤 | 命令 | 预期 |
|---|---|---|
| 工作区干净 | `git status --porcelain` | 无输出 |
| 读未发布清单 | 打开 `docs/UNRELEASED.md` | 知道自己这一版要带出去哪些改动 |
| 支持表一致（仅 1.21.x / 26.x 有脚本） | `.\release\version.ps1 -Line 1.21.x -CheckSupport -DryRun` | `支持表一致：三种写法都指向同一批 OptiFine 构建`，且不写任何文件 |
| 1.20.6 两条线 | 没有 `version.ps1`：手工核对 `README` 表 / CHANGELOG 节 / `OptifineSupport.java` 三处版本号一致 | 三处同名同版 |

## 1. 1.21.x 线

1. **升版本**：`.\release\version.ps1 -Line 1.21.x -Kind patch -DryRun` → 确认打印 `2.2.13 → 2.2.14`；确认无误后**去掉 `-DryRun`** 再跑一次，然后提交。
2. **逐目标构建普通 jar（十个目标，一个都不能省）**：
   ```powershell
   foreach ($mc in @('1.21','1.21.1','1.21.3','1.21.4','1.21.6','1.21.7','1.21.8','1.21.9','1.21.10','1.21.11')) {
       cd I:\mods\OptiFabric-1.21.x
       .\gradlew.bat build "-Pmc=$mc"
   }
   ```
   **为什么逐个来**：`build\libs` 里会同时堆着十个目标的产物，而一次 `-Pmc=X` 只重建 `X`；直接拿目录里的旧 jar 当新产物，曾让我把十个包里的九个当成新的（后来被校验拦下）。
   每构建一个，**立刻**核对该目标的 jar 时间戳与尺寸，再继续下一个。
3. **逐目标构建 `-full`**：在 `convenience/1.21.x-2.2.14` 分支上，同样十个 `-Pmc=<mc>`；该分支的构建产物名带 `-full` 后缀。
4. **记账**：`.\release\version.ps1 -Line 1.21.x -RecordDigest`，再 `-RecordProvenance -Head <store提交> -FullHead <conv提交>`。
   该脚本**只在该值唯一出现时改写**；出现多处时会提示，需人工判断（历史上出现过"跨行污染"，即某行的尺寸被另一行的值替换）。
5. **干跑发行**：`.\release\publish.ps1 -DryRun` → 逐版本核对 tag 名、notes 路径、`--target`；确认 1.21.6 / 1.21.7 的"不推荐版本"告警是有意为之。
6. **正式发行**：`.\release\publish.ps1`。
7. **发布后验收（关键）**：对每个 release 拉回 asset 的 size + sha256，与本地 `dist\` 逐字节比对；并在**下载下来的 jar** 上验本版修复的可判定标记（例如 `namesTarget`、`assignedBefore`）。这一步是这几轮里唯一真正抓住过错误的验收方式。
8. **快照**：`git push origin <release提交>:refs/heads/store/1.21.x-2.2.14`。
9. **清账**：把 `docs/UNRELEASED.md` 换成一行"已并入 2.2.14"。

## 2. 26.x 线

同 1.21.x，但：构建参数是 `-Pminecraft_version=<26.2|26.1.2>`（两个目标各自构建一次）；快照名 `store/26.x-2.2.9`；
`v2.2.9` 若继续承担 **Latest**，其余版本发布时必须带 `--latest=false`。

## 3. wip/1.20.6-reforged 与 main/1.20.6（手工线）

1. **升版本**：改 `gradle.properties` 的 `mod_version_base`（reforged 用 `1.1.7-reforged+mc1.20.6`，main 用 `1.1.7+mc1.20.6`）与手册表格里对应的行；**不要复用已发布的版本号**。
2. **构建**：store 侧 `.\gradlew.bat build` 出普通 jar；`convenience/…` 侧同命令出 `-full`。
3. **发布**：`gh release create <tag> <jar> --title … --notes-file …`（发布说明取本线 CHANGELOG 对应节）；**`--latest=false` 必须带**，Latest 归 26.x。
4. **发布后验收**：同第 1 节的第 7 步（size+sha256 逐字节一致；下载下来的 jar 里验证标记）。
5. **快照**：`store/1.20.6-reforged-1.1.7`、`store/1.20.6-1.1.7`。
6. **清账**：各自的 `docs/UNRELEASED.md` 换成一行。

## 4. CurseForge / Modrinth（可选，需凭据）

干跑会打印 CF 的 `projects//upload`（project id 为空）与 `Authorization:` 为空 —— **那就是缺凭据的样子**。
补齐四个环境变量后，`publish.ps1` 会同时提交；提交前用 `-DryRun` 复核一遍打印出来的 multipart 命令与脱敏后的头部。

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
