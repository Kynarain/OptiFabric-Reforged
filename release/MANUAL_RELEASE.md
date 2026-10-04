��动发布清单(26.x,逐版一个发布条目)

> 本文件覆盖 **26.x** —— 仓库里唯一的一条发布线,现在有两个产物:**26.2**(当前,`2.2.5`)与
> **26.1.2**(`2.2.5`;它以前冻结在 `2.0.0`,但 2.2.1 修的那个缺陷它也有,2.2.1 起它也每版跟着重新出一份)。另一条线(1.21.x,混淆名 +
> yarn/intermediary,一份源码出十个 MC 版本)在自己的分支上,两条线的 jar 不能互相替代。

版本号统一写成 `<版本>+mc<MC版本>`(jar 名与发布标题都用这一串);**GitHub 的标签一般是版本号本身,不带 `+mc`**
(与已发的 `v1.2.0` / `v2.0.0` 一致)。**例外**:同一个版本号出两个 jar 时,26.2 那一份用裸标签、26.1.2 那一份加后缀
(`v2.2.3` 与 `v2.2.3+mc26.1.2`;`v2.2.2` / `v2.2.2+mc26.1.2`、`v2.2.1` / `v2.2.1+mc26.1.2` 与 `v2.2.0` / `v2.2.0+mc26.1.2` 就是这么发的)
—— 否则两份抢同一个标签名。
正文直接用 release/notes/mc<版本>.md(已是 Markdown,含安装步骤、已知限制、该 jar 的尺寸与 SHA-256)。

> ✅ **两个 tag 现在由脚本自己算对**(2.2.5 起):`release/publish.ps1` 里多了 `$modTagSuffixes`
> (`@{ "26.1.2" = "+mc26.1.2" }`),`$tag` 是 `"v$modVersion" + 后缀` —— 26.2 仍然是裸的 `v2.2.5`,26.1.2 是
> `v2.2.5+mc26.1.2`。**2.2.4 及更早**这里是一句警告:`$tag = "v$modVersion"` 对两个版本都算成裸标签,脚本连发两版
> 时第二个条目会因为 tag 已存在而失败,只能手工绕。现在 `.\release\publish.ps1 -DryRun` 的 `gh release create`
> 那一行就会把两个 tag 打出来,照着核对即可。
>
> 手工做同样的事(不跑脚本时),tag 名就是:
>
> ```powershell
> git tag v2.2.5                 # 26.2 那一份:裸标签(本仓库的 Latest 徽章归这一条)
> git tag "v2.2.5+mc26.1.2"      # 26.1.2 那一份:带后缀,否则和上面撞名
> git push origin v2.2.5 "v2.2.5+mc26.1.2"
> ```
>
> **Latest 徽章**:仓库的 Latest 归本线(26.2 那一份的裸标签)。1.21.x 与 1.20.6 线上的每一个条目都必须用
> **`make_latest=false`** 创建,它们那份清单里也是这么写的;26.1.2 那一条同样用 `make_latest=false`。

> **这一线现在覆盖两个 MC 版本**,所以升版分两种:整条线一起升版是
> `.\release\version.ps1 -Line 26.x -Kind <major|minor|patch>`(它改的是基数,也就是 26.2 那份);
> 只给某一个 MC 版本升版用 `-Mc <MC 版本>`,它写的是 `release\publish.ps1` 的 `$modVersions` 例外值表。
> 规则与命令见 [`docs/VERSIONING.md`](../docs/VERSIONING.md)。

> 发布说明里有两个必须写准的点:**Java 25 是 26.2 自身的硬要求**(与 1.21.x 的 Java 21 不同,写在最前面);
> **只支持 26.2 与 26.1.2** —— 26.2.1 与 26.3 至今没有 OptiFine 构建,别让用户拿这个 jar 去顶别的版本。
> 还有一条必须写在显眼处:**26.2 上光影不可用**(这个 OptiFine 构建自己取消了光影包加载,本模组不修它:强行打开
> 会让画面只剩粒子、方块透明)。`release/notes/mc26.2.md` 已经按这个口径写好,别再改成"光影正常"。

## 逐版数据

| 版本 | 版本号 / 标签 | jar | 字节 | SHA-256 | 正文 |
|---|---|---|---|---|---|
| 26.2 | 2.2.6+mc26.2 / v2.2.6 | dist\OptiFabric-Reforged-2.2.6+mc26.2.jar | 206575 | 73D3090B8A454605BA187F8A470C5124E7128D25E8D2401F78060F5573F035AC | release/notes/mc26.2.md |
| 26.1.2 | 2.2.6+mc26.1.2 / v2.2.6+mc26.1.2 | dist\OptiFabric-Reforged-2.2.6+mc26.1.2.jar | 206577 | 37F13ED392444F31203565A9B0E81743AC3E2CAC9D71D1577D0FF18883ADFB89 | release/notes/mc26.1.2.md |

> ⚠️ **`-RecordDigest` 会把这张表里每一行都改一遍,跑完必须 `git diff` 逐行看**
>
> Markdown 表格对脚本来说是**一个段落**(行与行之间没有空行),而 `-RecordDigest` 是按段落做替换的。所以
> `-Line 26.x -RecordDigest` 记录 26.2 那份时,表里 26.1.2 那一行的字节数与 SHA-256 也会被写成 **26.2 的**;
> 26.x 这一版的 `version.ps1` **没有** 1.21.x 那份脚本里的"一段里出现多个 MC 版本就逐行处理"的保护
> (`$versionsHere` 那一段)。
>
> 本机实测(`-Line 26.x -RecordDigest -DryRun`,真实产物 180469 字节):
>
> | 行 | 被写成 |
> |---|---|
> | 26.2 | `180469` / `5241EBBE…9841F` ← 正确 |
> | 26.1.2 | `180469` / `5241EBBE…9841F` ← **错的**(它自己那份是 180472 / `836BC0C5…730A8F`) |
>
> **每次跑完手工把不是这一版的行改回去**;要根治就把 1.21.x 那份脚本里 `$versionsHere` 的逐行分支
> **一字不差地**搬过来(那段代码本身已在 1.21.x 线用了几个版本,搬运零风险)。26.x 的 2.2.1 发布时就踩了这条,
> 26.2 那一行是手工改回去的。

> ⚠️ **`version.ps1` 升级时也会改写历史小节的版本串,同样必须 `git diff` 复核**
>
> 升版做的是**全文件**字符串替换:每一个 `<旧版本>+mc` 换成 `<新版本>+mc`。而 `CHANGELOG.md` 的历史小节正文里
> 本来就带版本号("…2.1.0 那套校验结论继续适用"、`OptiFabric-Reforged-2.1.1+mc26.2.jar` 这样的产物名),
> 所以只要 `<旧版本>` 也出现在更早的小节里,那一段就会被一起改掉 —— 已冻结的发布记录于是被改写成当前版本。
> **这个坑在本仓库已经手工修过三次**(2.1.0 一次,2.2.1 期间两条线各一次)。
>
> - 只改某**一个 MC 版本**(`-Mc`)不会踩到:替换串带着 MC 版本(`2.2.5+mc26.2`),别的 MC 版本与历史串都碰不到;
> - **每次升版都先 `-DryRun`,真跑完再 `git diff -- CHANGELOG.md`**,只允许出现"当前版本自己那一节"的改动;
> - 历史小节被改了就先手工改回去再提交发布。

## 三处平台各自要填什么

### GitHub Release

- **Tag**:`v2.2.5`(26.2 那一份;本仓库的 tag 一般只写版本号,已发布的 `v1.2.0` / `v2.0.0` / `v2.1.0` / `v2.1.1` / `v2.2.0` / `v2.2.1` / `v2.2.2` / `v2.2.3` / `v2.2.4` 就是这样;MC 版本留在产物名与标题里)。
  **26.1.2 那一份用带后缀的 `v2.2.5+mc26.1.2`**(两份同版本号,裸标签会撞名,见文首);
  target 选 **26.x 线的发布提交(当前在 `wip/26.2` 上,即这次发版的提交)**;
- **Release title**:OptiFabric Reforged 2.2.5+mc26.2;
- **Describe this release**:粘贴 `release/notes/mc26.2.md`(Markdown);
- **Attach binaries**:上传 `dist\OptiFabric-Reforged-2.2.6+mc26.2.jar`(正文里已写好尺寸与 SHA-256,方便用户校验)。
- **26.1.2 那一份要单独发一个条目**(同一个版本号、另一个 jar):tag `v2.2.5+mc26.1.2`、title
  `OptiFabric Reforged 2.2.6+mc26.1.2`、正文用 `release/notes/mc26.1.2.md`、附件 `dist\OptiFabric-Reforged-2.2.6+mc26.1.2.jar`
  (以及它的 `-sources.jar`);**`make_latest` 保持 false** —— 仓库的 Latest 徽章归 26.2 那一份(`v2.2.5`)。

### Modrinth

| 字段 | 填什么 |
|---|---|
| Name | OptiFabric Reforged 2.2.5+mc26.2 |
| Version number | 2.2.5+mc26.2 |
| Release channel | Release |
| Game versions | 只勾 **26.2** |
| Loaders | Fabric |
| Environment | 客户端 Required、服务端 Unsupported(OptiFine 只在客户端存在) |
| Dependencies | Fabric API 可标 required;OptiFine 无法上架,正文已写明需用户自备 |
| Changelog | 粘贴 `release/notes/mc26.2.md` |
| Files | 上传该版 jar |

### CurseForge

| 字段 | 填什么 |
|---|---|
| Display name | OptiFabric Reforged 2.2.5+mc26.2 |
| Release type | Release |
| Game version | Minecraft **26.2** 加 Fabric(只勾该版) |
| Changelog | 粘贴 `release/notes/mc26.2.md`,格式选 Markdown |
| File | 上传该版 jar |

> 若项目页面的**支持版本范围**里还没有 26.2,先在项目设置里加上,再传文件(26.1.2 那份已经发过,原样留着)。

## `release\tmp\` 是每次升版都会过期的草稿

`release\tmp\` 整个目录都在 `.gitignore` 里,里面是 `publish.ps1` 上一次跑出来的提交体(`publish.ps1` 每次都会按
`dist\` 里的 jar 与 `release\notes\mc<MC>.md` **重写**它们):

- `release\tmp\modrinth-<MC版本>.json` —— Modrinth 的 version 元数据(`name` / `version_number` / `game_versions` /
  `changelog`,changelog 就是当时那份 `release\notes\mc<MC>.md` 的全文);
- `release\tmp\curseforge-<MC版本>.json` —— CurseForge 的 upload 元数据(`displayName` / `gameVersions` / `changelog`)。

它们的版本号、尺寸与正文**都是上一版那一刻的快照**:升版之后随便点开一个,里面写的还是旧版本号与旧正文。
每个 MC 版本各一份(`modrinth-26.2.json` 与 `modrinth-26.1.2.json`),两份要**分别**核对。
**每次发布前重新生成一遍,不要手改、也不要直接复用**:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File release\publish.ps1 -DryRun      # 重写全部 tmp 草稿,-DryRun 不联网
type release\tmp\modrinth-26.2.json                                                   # 核对版本号与 changelog 是这一版的
```

**人的上传文件夹也要顺手刷新**:`C:\Users\kynar\IdeaProjects\OptiFabric\release-upload\<线>-<版本>\`。这一线是
**两个**目录:`release-upload\26.x-<版本>\`(26.2 那份)与 `release-upload\26.1.2-<版本>\`(26.1.2 那份)——
`release-upload\26.x-2.2.2\` 与 `release-upload\26.1.2-2.2.2\` 就是 2.2.2 发布时的两个取件处。
里面放该版 jar(含 `-sources.jar`)与 `metadata\`(从 `release\tmp\` 拷 `modrinth-*.json` / `curseforge-*.json`),
命名规则与已有那些目录一致(`1.21.x-<版本>`、`26.x-<版本>`、`26.1.2-<版本>`、`1.20.6-<版本>`)。它是**上传时的
取件处**,不是仓库内容,由人自己维护。

## 发布前自查

- [ ] **版本号已按 SemVer 改好**:`.\release\version.ps1 -Line 26.x -Kind <major|minor|patch>`(整条线;
      只给一个 MC 版本升版就加 `-Mc 26.2`。规则见 [`docs/VERSIONING.md`](../docs/VERSIONING.md);
      不要手改 —— 一次要动 9 个文件);跑完 `git diff -- CHANGELOG.md` 确认历史小节没被动(见上面的警告);
- [ ] `.\gradlew build --offline` 通过(这一线没有 `-Pmc`,目标版本来自根目录 `gradle.properties` 的
      `minecraft_version`),且产物名是 `OptiFabric-Reforged-2.2.5+mc26.2.jar`;
      **发布用的 jar 是在干净的临时 worktree 里构建的** —— 这个 worktree 里有 9 个未提交的实验文件,
      脏构建会多出约 18 KB 的实验类(178,300 对 196,235,见 [`CONTRIBUTING.md`](../CONTRIBUTING.md) 第二节);
- [ ] `.\release\version.ps1 -Line 26.x -RecordDigest` 跑过,正文里的尺寸与 SHA-256 与 `dist\` 里的 jar 一致;
      **并已 `git diff release\MANUAL_RELEASE.md` 确认 26.1.2 那一行没有被写成 26.2 的数字**;
- [ ] jar 里**没有** OptiFine 的类或资源、没有 `mappings/mappings.tiny`(26.x 本就不该有映射表);
- [ ] `LICENSE.txt_OptiFabric-Reforged` 在(`jar` 任务按 `archives_base_name` 给 `LICENSE.txt` 改名),
      `fabric.mod.json` 的 `license` 仍是 `MPL-2.0`;
- [ ] `fabric.mod.json` 的 `id` 是 `optifabric_reforged`、`minecraft` 是 `26.2`、`fabricloader` 是 `>=0.19.5`;
- [ ] 离线校验跑过:`powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-26.ps1 -Version 26.2`,
      口径为 `Prepared … (0 skipped, 0 failed)`、`verified OK`、`ASM verifier problems: 0`、扫描器全 0;
      同一份源码再用 `-Version 26.1.2` 跑一次,确认那一版的数字没变(26.1.2 是 `567 / 567`);
- [ ] 真机在**干净实例**上实测一次(只要 Fabric API + OptiFabric + OptiFine):能进主界面、能进存档;
      提示:`<游戏目录>/.optifine/` 是缓存,删掉可强制重建,适合测"首次安装";
- [ ] 正文里写的 OptiFine 构建名与用户实际要装的 jar 名一致;
- [ ] 三处都挂上了 jar 本体,不要只贴正文;
- [ ] 发布说明里提醒用户需要 **Java 25**(26.2 的硬要求,与 1.21.x 的 Java 21 不同);
- [ ] `release\tmp\` 草稿已重新生成、`release-upload\26.x-<版本>\` 与 `release-upload\26.1.2-<版本>\` 已刷新;
- [ ] 两个 tag 都对(`v<版本>` 给 26.2、`v<版本>+mc26.1.2` 给 26.1.2),且只有 26.2 那条成为 Latest;
- [ ] **文档编码**:改过 `.md` / `.ps1` 之后抽查 —— `.md` / `.java` = UTF-8 **无 BOM** + CRLF;
      `.ps1` = UTF-8 **带 BOM** + CRLF(丢了 BOM 会被 Windows PowerShell 当 ANSI 读、脚本直接加载失败);
- [ ] 先在 GitHub 发一版看排版,再补 Modrinth / CurseForge。
