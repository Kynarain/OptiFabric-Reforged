# 下载校验：现状、真实数据，以及为什么"钉死单一哈希"是错的

## 现状（报告点名的 P0 后半段）

`-full` 构建的下载器**算了** SHA-256 并把结果打印出来（`Outcome.sha256`、日志里的 `SHA-256 …`），但**从不与任何期望值比对** ——
也就是说，报告说的"哈希钉死仍缺"属实；`convenience/1.20.6-1.1.6`（main 线 `-full` 的来源）连 `verifyIdentity` 都没有。

## 支持表点名的十个构建，与磁盘上真实文件的 SHA-256

下表全部由**磁盘上真实存在的 jar** 算出（不是估计值）。同名文件在本机有多份副本，凡多份都核对过是否同内容。

| MC | 文件 | 字节 | SHA-256 | 本机同名不同内容的份数 |
|---|---|---:|---|---:|
| 1.21 | `preview_OptiFine_1.21_HD_U_J1_pre9.jar` | 7,355,487 | `b26b22478592ca85c3ea829784c3b7bf…` | 1 |
| 1.21.1 | `OptiFine_1.21.1_HD_U_J1.jar` | 7,322,249 | `db6d2d14db0009bdea2d8f848e5cb55a…` | 1 |
| 1.21.3 | `OptiFine_1.21.3_HD_U_J2.jar` | 7,304,461 | `3916aadff6fd8814f93d5ca0fddf4921…` | 1 |
| 1.21.4 | `OptiFine_1.21.4_HD_U_J3.jar` | 7,423,659 | `db8a1c508ecb3f89ae8f7b69ae25c78c…` | 1 |
| 1.21.6 | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` | 7,518,992 | `f73e5b90a89e523873b5b876cbd7601f…` | 1 |
| 1.21.7 | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` | 7,587,441 | `4e7340eb61b39f100da639e62490e504…` | 1 |
| 1.21.8 | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` | 7,704,702 | `ffe496b39065fedc897dc179965f9a21…` | 1 |
| 1.21.9 | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` | 7,664,893 | `dc7d1182360c2b23b0c96797103ef194…` | 1 |
| 1.21.10 | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` | 7,805,444 | `bf845cfc6a387b0cc879512caefa8603…` | 1 |
| 1.21.11 | `OptiFine_1.21.11_HD_U_J9.jar` | 8,045,116 / 8,045,105 | `63a60c48b3370920e96d4c32570d7154…` / `f97b5d06df53760e21c5a91733ac55af…` | **2** |

（上表只写前 32 位十六进制，完整值可从本机同名文件重算；写文档时以完整 64 位写入代码表。）

## 为什么不能"钉死单一哈希"

在本机 173 个 OptiFine jar 名里，**24 个存在多份不同内容**，包括：

- `OptiFine_1.21.11_HD_U_J9.jar` —— 支持表点名的构建之一，网上拿到的两份内容不同（字节数也不同）；
- `preview_OptiFine_1.20.6_HD_U_J1_pre18.jar`、`optifine-OptiFine_1.21.8_HD_U_J6_pre16.jar`（4 份）等预览版。

也就是说：**OptiFine 会重传同一个构建名的文件**（预览版尤其如此）。若把校验写成"必须等于唯一哈希"，用户会在他完全没做错的情况下被挡住。

## 因此定下的实现口径（待实现，属报告 P0 的后半段）

1. 支持表里每个构建记录一组**可接受哈希**（现在已知的都列进去；`1.21.11` 就是两个）；
2. 下载后比对：命中**任一** ⇒ 通过，并把实际哈希打出来；
3. **全不命中** ⇒ **拒绝**，并在日志里同时打印"期望集合"与"实际收到"，让用户能自己判断是重传还是被换包；
4. 不做"静默接受"，也不做"仅警告后放行" —— 报告担心的正是**静默**换包；
5. `convenience/1.20.6-1.1.6`（main 线 `-full`）除哈希外还要补回 `verifyIdentity`（同 reforged 分支那份）。

## Status (updated after the implementation)

Implemented on all eight convenience branches, which are the only builds that carry the downloader: the host is
compared exactly, every request must be https, the downloaded jar is read back to confirm it declares the build
that was asked for, and its SHA-256 is compared with the recorded contents of that build.

| branch | identity check | hash check | commits |
|---|---|---|---|
| convenience/1.21.x-2.2.13 | was there | added | `e9d5e18`, `ebf8027` |
| convenience/1.21.x-2.2.12 | was there | added | `3ea1c5b`, `2cfa0ab` |
| convenience/1.21.x-2.2.11 | was there | added | `4d507a2`, `2131291` |
| convenience/26.x-2.2.8 | was there | added | `e19001c`, `1bbc839` |
| convenience/26.x-2.2.7 | was there | added | `e1f123f`, `190364f` |
| convenience/1.20.6-reforged-1.1.5 | was there | added | `8a561d9` |
| convenience/1.20.6-reforged-1.1.6 | was there | added | `80c7e9e` |
| convenience/1.20.6-1.1.6 | **added in the same pass** | added | `47e8554` |

The 1.20.6 branch needed the identity check as well: its downloader comes from the older lineage and never read
the downloaded jar back, which is what the review meant by the -full 1.1.6 having no identity at all. Two of the
commits above (`e9d5e18` and its siblings) added only the table, because the step that was supposed to insert the
call passed three arguments to a two argument method, and the message on them said otherwise; the follow-up
commits wire the call and say so. Everything this document describes is now in published jars: the -full variants of 2.2.14, 2.2.9, 1.1.7 and 1.1.7-reforged were rebuilt and re-uploaded for exactly these reasons, and the older ones that promised the same things (2.2.11, 2.2.12, 2.2.8, 1.1.6-reforged) were rebuilt afterwards, and 2.2.11 and 2.2.12 were rebuilt a second time once the missed branches were fixed. The two lines whose older releases never promised this (26.x-2.2.7, 1.20.6-1.1.6) have the corrected code on their branches but their published jars predate it and are not going to be replaced.

## Correction (after the seven-round review)

Two defects in the first implementation of this check, both found by the reviewer on the published jars:

1. **The table was per repository, not per line.** The 1.21.x table was copied to every branch carrying the
   downloader, so on the 26.x and 1.20.6 lines it named no build those lines can download - every download was
   refused. Each line now has its own table, taken from the build names that line's own support table declares, and the
   entries were computed from the real files - by hand, there is no build step that produces this file. Verified in the rebuilt jars: each carries its line's own names. Because the check fails closed, a build that OptiFine ships later and that nobody adds here is refused: adding the new build's hashes belongs to the release steps.
2. **The check ran after the jar had been written.** A refused download therefore stayed on disk, and the next
   launch returned it as "already present" without any check passing - the hash never actually gated. The check
   now runs before the write, and a jar already on disk has to match the recorded contents or it is removed and
   fetched again.

Both corrections are in every branch that carries the downloader - after the first attempt they were in eight of the twelve, which the reviewer caught by reading the bytecode of the 2.2.11 and 2.2.12 jars: those twenty had been rebuilt from branches that still wrote before checking. They were rebuilt again. The fourteen -full jars that had been
published with the first implementation were rebuilt and re-uploaded; each was then pulled back from the release
and compared byte for byte with the local build.
