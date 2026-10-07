�动发布清单(1.20.6 线,每个版本一个发布条目)

> 本文件只讲 **1.20.6 线**(`main`):整条线**只出一个 jar**,mod id 是 `optifabric`。
> 同仓库还有 1.21.x 与 26.x 两条线,各线的 jar **不能互相替代**;此外 `wip/1.20.6-reforged` 分支上还有
> **第二个 1.20.6 产物**(mod id `optifabric_reforged`),它**不是**已发布线,而且两个 1.20.6 产物**只能装一个**。
>
> **发布标签是 `v<版本>+mc<MC版本>`**(已发:`v1.1.0+mc1.20.6`、`v1.1.1+mc1.20.6`、`v1.1.2+mc1.20.6`)。
> ⚠️ GitHub 的 Latest 徽章归 26.x 线 —— 本线每建一个 Release 都要用 **`make_latest=false`** 创建。
>
> **发布正文就是 [`CHANGELOG.md`](../CHANGELOG.md) 里对应那一节。** 商店页文案在
> [`docs/DESCRIPTION.md`](../docs/DESCRIPTION.md);构建与上传步骤见 [`docs/PUBLISHING.md`](../docs/PUBLISHING.md)。

## 一、逐版数据

> **顺序**:先 `.\gradlew build --offline`,把新 jar(与 `-sources.jar`)复制进 `dist\`,给下面这张表**加一行**,
> 然后逐行核对「表里的字节数与 SHA-256」是否等于「它点名的那一个 `dist\` 文件」——命令见第三节。
> `dist\` 在 `.gitignore` 里,不进仓库;`1.1.0`~`1.1.2` 三行是从 `release-upload\1.20.6-<版本>\` 里
> **逐字节复制**回来留档的已发布件(哈希与发布时记录的一致,见下 `1.1.2` 的交叉核对)。

| 版本 | 版本号 / 标签 | jar(`dist\`) | 字节 | SHA-256 | 正文 |
|---|---|---|---|---|---|
| 1.1.0 | `1.1.0+mc1.20.6` / `v1.1.0+mc1.20.6` | `dist\OptiFabric-1.1.0+mc1.20.6.jar` | 726924 | `6ABABCAF25DFDB592CE2299DC3F19F717FD4399B089C6C5C559EBE4AC5BDC93D` | `CHANGELOG.md` 1.1.0 节 |
| 1.1.1 | `1.1.1+mc1.20.6` / `v1.1.1+mc1.20.6` | `dist\OptiFabric-1.1.1+mc1.20.6.jar` | 729103 | `F9BBEE732E0C1FFB0FA84CA65DB8E566A555138DBFEC602CB71F7442C1F3DFFD` | `CHANGELOG.md` 1.1.1 节 |
| 1.1.2 | `1.1.2+mc1.20.6` / `v1.1.2+mc1.20.6` | `dist\OptiFabric-1.1.2+mc1.20.6.jar` | 729105 | `0267DF7B6DDA25424A6FF11060EFC528429B15AE3A844992AE0ED1F94DC85C0C` | `CHANGELOG.md` 1.1.2 节 |
| **1.1.3** | `1.1.3+mc1.20.6` / `v1.1.3+mc1.20.6` | `dist\OptiFabric-1.1.3+mc1.20.6.jar` | 733927 | `7A65777272624D8A33DC1C6EEF28986FCBDCC40998A175B7119A43101245010C` | `CHANGELOG.md` 1.1.3 节 |
| **1.1.4** | `1.1.4+mc1.20.6` / `v1.1.4+mc1.20.6` | `dist\OptiFabric-1.1.4+mc1.20.6.jar` | 716511 | `85BAB371B95EF1B83FFC1F7776505770B9DBEC5FE61E1EDE0FAC40251B6EA2FD` | `CHANGELOG.md` 1.1.4 节 |
| 1.1.4(仅 GitHub:`-full`) | `1.1.4+mc1.20.6` | `dist\OptiFabric-1.1.4+mc1.20.6-full.jar` | 734716 | `3424CB44B534448566BD2B8E015FAE6AD1724FFAAC7279F8346351DF20986104` | `CHANGELOG.md` 1.1.4 节 |
| **1.1.6** | `1.1.6+mc1.20.6` / `v1.1.6+mc1.20.6` | `dist\OptiFabric-1.1.6+mc1.20.6.jar` | 718217 | `55A51F016718AEF1479348C0FA6D0AC375E739A6C69E95A10C05D293689815C9` | `CHANGELOG.md` 1.1.6 鑺?|
| 1.1.6(仅 GitHub:`-full`) | `1.1.6+mc1.20.6` | `dist\OptiFabric-1.1.6+mc1.20.6-full.jar` | 736347 | `75F8FDD75BD86981CD02225863871853BDD968A643AA3FE67943C940AC41A0B6` | `CHANGELOG.md` 1.1.6 鑺?|
| **1.1.7** | `1.1.7+mc1.20.6` / `v1.1.7+mc1.20.6` | `dist\OptiFabric-1.1.7+mc1.20.6.jar` | 718213 | `4C82EDF7FA633BE8FAE67B31C38004205F5FF407245D1EC726D0C8B2F639F84A` | CHANGELOG 1.1.7 |
| 1.1.7(GitHub only: `-full`) | `1.1.7+mc1.20.6` | `dist\OptiFabric-1.1.7+mc1.20.6-full.jar` | 740383 | `478CD5BF0D2067CA7D615825C9C455A361B54F902583BEDB477908853BAC1399` | CHANGELOG 1.1.7 |

`-sources.jar`(一般不发,留档用):

| 版本 | 文件(`dist\`) | 字节 | SHA-256 |
|---|---|---|---|
| 1.1.0 | `dist\OptiFabric-1.1.0+mc1.20.6-sources.jar` | 89498 | `B4417B52417C0C9FC2F15BE640D41812805231F64451A00C219060C343D26722` |
| 1.1.1 | `dist\OptiFabric-1.1.1+mc1.20.6-sources.jar` | 92481 | `A12AEE0753935446C8F0F53788F595FF74F80CD4D044B61DF441E08C941EB3E8` |
| 1.1.2 | `dist\OptiFabric-1.1.2+mc1.20.6-sources.jar` | 92485 | `B8D715A845BF94931FCC61DA96D682BC88158CD0D3E96ED11C2271B4A88A44B6` |
| **1.1.6** | `dist\OptiFabric-1.1.6+mc1.20.6-sources.jar` | 89727 | `8B7E49738CAEDBFCEB9BD2ECA5C24A6C7ABCDAE82647A2A7F82A39C5E712812C` |
| **1.1.3** | `dist\OptiFabric-1.1.3+mc1.20.6-sources.jar` | 98407 | `AF1589E3EC5EEB5BCAC320C5F53E3622CB9D82A04E55DF3C4A35FB82CC6B9D7C` |
| **1.1.4** | `dist\OptiFabric-1.1.4+mc1.20.6-sources.jar` | 87369 | `C5413F5C70CB9E61107A7E5F54E8143E8EDC73A1155A215CBC7ACEEAC0CAF1F3` |

**交叉核对(1.1.2)**:上表 1.1.2 的 SHA-256 与 `collision-1206\REPORT.md` 里记下的"已发布 1.1.2"
(`0267DF7B…C85C0C`,729,105 B)逐字符相同,也与 `release-upload\1.20.6-1.1.2\` 里那一份相同 —— 三处一致。

### 1.1.3 (historical)

| | |
|---|---|
| 上传的 jar | `dist\OptiFabric-1.1.3+mc1.20.6.jar`(733,927 B,`7A657772…245010C`) |
| 取件目录 | `C:\Users\kynar\IdeaProjects\OptiFabric\release-upload\1.20.6-1.1.3\`(jar + `-sources.jar` + `metadata\`) |

## 二、1.1.7 这一次要发的东西

| | |
|---|---|
| 上传的 jar | `dist\OptiFabric-1.1.7+mc1.20.6.jar`(718,213 B,`4C82EDF7…`) |
| `-full` 变体 | 由 `convenience/1.20.6-1.1.7` 构建(740,383 B,`478CD5BF…`),与默认产物共用同一个 Release(仅 GitHub) |
| 取件目录 | `I:\mods\release-upload\1.20.6-1.1.7\`(jar + `-full` + `-sources.jar` + `metadata\`) |
| 标签 | `v1.1.7+mc1.20.6` |
| Release 正文 | `CHANGELOG.md` 的 **1.1.7 节**(整节粘贴),另存于 `release\notes\1.1.7+mc1.20.6.md` |
| Latest | **不勾** `make_latest`(Latest 归 26.x 线) |
| 快照分支 | `store/1.20.6-1.1.7`(发布提交) |

> 本页下方"### 1.1.3 那一次"是更早一次发行的记录,保留原样。

| 元数据草稿 | [`release\tmp\modrinth-1.20.6.json`](tmp/modrinth-1.20.6.json)、[`release\tmp\curseforge-1.20.6.json`](tmp/curseforge-1.20.6.json) |
| 标签 | `v1.1.3+mc1.20.6` |
| Release 正文 | `CHANGELOG.md` 的 **1.1.3 节**(整节粘贴) |
| Latest | **不勾** `make_latest`(Latest 归 26.x 线) |

## 三、核对命令(每次升版都跑一遍)

```powershell
# 1) 表里的每一行都必须和它点名的 dist\ 文件对得上
foreach ($f in Get-ChildItem dist\OptiFabric-*+mc1.20.6.jar) {
  "{0}`t{1}`t{2}" -f $f.Name, $f.Length, (Get-FileHash $f.FullName -Algorithm SHA256).Hash
}
# 2) 把输出逐行与第一节的表比对(尺寸 + 大写 SHA-256)
# 3) release\tmp\ 里的版本号与 changelog 必须是这一版的:
Get-Content release\tmp\modrinth-1.20.6.json   | Select-String 'version_number|version_type'
Get-Content release\tmp\curseforge-1.20.6.json | Select-String 'displayName|releaseType'
```

## 四、发布前请再确认这几点

- [ ] `gradle.properties` 的 `mod_version`、`README.md` 的产物名与「支持的版本」表、`CONTRIBUTING.md` 第二节、
      `docs/PUBLISHING.md` 第二节、本文件第一节 —— **一处都不能漏**。
- [ ] `CHANGELOG.md` 顶部那一节是**未发布**的新版本,而且里面写的现象都真的实测过。
- [ ] **不要承诺 c2me 可用。** 本产物在 1.20.6 上会被 c2me 硬拒载(`NEG_HARD_DEP … {breaks optifabric @ [*]}`,
      见 `docs/FAQ.md` 第四节):Release 正文里那一段说明**必须保留**;要用 c2me 的是另一个产物
      (`wip/1.20.6-reforged` 分支)。最新一次的拒载实测记录在 `CHANGELOG.md` 的 1.1.3 节。
- [ ] 用**干净的实例**实测一次(只放 Fabric API + OptiFabric + OptiFine):进主界面、进存档 0 错误。
- [ ] jar 里没有 OptiFine 的任何类或资源;`LICENSE.txt` 还在;`fabric.mod.json` 的 `license` 仍是 `MPL-2.0`。
- [ ] 编码:`.md` / `.java` / `.json` = UTF-8 **无 BOM** + CRLF;`.ps1` = UTF-8 **带 BOM** + CRLF。
- [ ] Branch topology BEFORE anything else: `git rev-list --left-right --count main...store/1.20.6-<version>` (and `wip/1.20.6-reforged...store/1.20.6-reforged-<version>`) must start with `0`. A non-zero first column means the line branch is not an ancestor of its store branch, so `git merge --ff-only` is refused and the branch cannot be pushed without a force. 1.1.4 hit exactly this: `main...store/1.20.6-1.1.4` was 2 (main-only) / 9 (store-only), because the two `COMPATIBILITY.md` docs commits `60fe0e9` and `bf92e81` landed on `main` after the store branch had merged it (`e2b91de`); the release went out on a lossless `git merge --no-ff store/1.20.6-1.1.4` instead. Never reset the line branch to the store head: those commits are already on `origin`, so the push would be rejected.
