# 手动发布清单(1.21.x,逐版一个发布条目)

> 本文件覆盖 **1.21.x** —— 仓库里唯一的一条发布线,10 个版本各发一个条目。

版本号统一写成 `<版本>+mc<MC版本>`(jar 名、release 标题与 **GitHub tag** 都用这一串)。tag 必须逐版本唯一:这个仓库同时承载 26.x 线,该线已占用 `v2.0.0`(两条线各自都把"换 mod id"当成大版本),所以 tag 带上 `+mc<MC版本>`。
正文直接用 release/notes/mc<版本>.md(已是 Markdown,含安装步骤、已知限制、该 jar 的尺寸与 SHA-256)。

> **tag 归属(两条线都别忘)**:`v<版本>+mc<MC版本>` 这一串只属于本线。仓库的 **Latest 徽章归 26.x 线** —— 它的
> 当前产物用**裸标签** `v2.2.2`(`v2.1.0` / `v2.2.0` / `v2.2.1` 也是这样,见那一线的 `release/MANUAL_RELEASE.md`)。
> 所以本线这十个条目都要用 **`make_latest=false`** 创建,否则最后发的那个 jar 会把 Latest 从 26.x 抢过来。
> 已发的 `v2.2.7+mc1.21` … `v2.2.7+mc1.21.11` 十个 tag 就是按这个口径打的(`git tag --list` 可复核)。

> **一个版本号可以只覆盖一个 MC 版本**:1.1.0 那次把 10 个 jar 放在同一个 `v1.1.0` 条目里;
> 只改了某一个版本的行为时就单独给那个产物升版、单独发一个条目(1.1.1 就是只修 1.21.11 的抗锯齿后处理链),
> 其余版本继续停在原来的版本号。规则与命令见 [`docs/VERSIONING.md`](../docs/VERSIONING.md)。

> 1.21.6 / 1.21.7 的说明要写准确,别写成笼统的"启动即崩":**不开光影时它们能正常启动**(2026-09-13 本机复现,标题界面正常渲染);
> **一旦启用光影(选了光影包),启动阶段就会崩在 OptiFine 自己的代码里** —— `NullPointerException: Cannot read field "norm"
> because "multiTex" is null` at `net.optifine.shaders.ShadersTex.initDynamicTextureNS`。所以正文应写成"本移植不支持这两版的光影",
> 而不是"这两版起不来"。Official 列表里这两版已是最新构建。


> **写正文时的 PowerShell 转义坑(2.2.4 修过一次,必须记住)**:正文就是 `release\notes\mc<MC>.md` 本身(`publish.ps1` 用
> `--notes-file`,没有中间变量),所以**正文坏了就是笔记文件坏了**。2.2.4 的十份笔记是在 **Windows PowerShell 5.1 的双引号
> 字符串**里写的:字符串里的 `` `f `` 与 `` `v `` 落进文件后不再是 `f`、`v`,而是 U+000C(form feed)与 U+000B(vertical tab),
> 于是 `fabric-rendering-v1` 成了 ``abric-rendering-v1``、`verify-version.ps1` 成了 ``erify-version.ps1``。这两个字符随后
> **逐字节**进了十个已发布的 Release 正文与二十个 Modrinth / CurseForge changelog;修复提交是 `f987e0f`(只改这两个字节,
> `1.21.x` 与 `release/1.21.x-2.2.4` 都已收录)。
>
> **怎么写**:把整段正文直接写进文件(编辑器写盘即可),再用 `--notes-file` / 读文件的方式发出去;非要在 PowerShell 里
> 拼字符串,就用**单引号字符串**或 here-string(单引号版本 `@` 与 `@` 之间的内容是字面量;双引号版本**照样**转义)。
> 反引号转义(`` `f `` `` `v `` `` `n `` `` `t `` `` `a `` `` `b `` `` `0 `` `` `e `` `` `r ``)在双引号里一律变成控制字符 / 换行;要写 Markdown 的行内代码,
> 用单引号或双写 `` `` ``。
>
> **写盘后立刻验字节**,别等发出去再查:
>
> ```powershell
> # 逐字节扫一遍:C0 控制字符里只允许 TAB(0x09)与 LF(0x0A);CR(0x0D)按行尾一并允许
> $b = [IO.File]::ReadAllBytes("release\notes\mc1.21.11.md")
> ($b | Where-Object { $_ -lt 32 -and $_ -ne 9 -and $_ -ne 10 -and $_ -ne 13 }).Count   # 必须是 0
>
> # 正文与笔记文件逐字节相等(正文按 LF 规范化;本线笔记的工作区是 CRLF、blob 是 LF)
> $note = ([IO.File]::ReadAllText("release\notes\mc1.21.11.md", [Text.Encoding]::UTF8)) -replace "`r`n", "`n"
> gh release view "v2.2.7+mc1.21.11" --json body --jq .body > "$env:TEMP\body.txt"
> $body = ([IO.File]::ReadAllText("$env:TEMP\body.txt", [Text.Encoding]::UTF8)) -replace "`r`n", "`n"
> $body -ceq $note   # 必须是 True
> ```
>
> 2.2.4 的修复就是这么复核的:十个正文重新拉回来与笔记逐字符相等,二十个 changelog 字段里 U+000B / U+000C / U+0000 各 0 个。

## 逐版数据

> **顺序**:先 `.\gradlew build`,把新 jar 复制进 `dist\`(并更新本表的版本号列),再跑 `.\release\version.ps1 -Line 1.21.x -Mc <MC版本> -RecordDigest`;
> 这条命令记录的是**已发布**的那份 —— `dist\` 与 `build\libs\` 同名却不同字节时会直接停下,所以表里/正文里的尺寸与 SHA-256 永远描述真正上传的那个文件。

> ⚠️ **`version.ps1` 会改写历史小节的版本串,跑完必须 `git diff` 复核(尤其 `CHANGELOG.md`)**
>
> 升版时脚本做的是**全文件**的字符串替换:把每一个 `<旧版本>+mc` 换成 `<新版本>+mc`。而 `CHANGELOG.md` 里
> **历史小节的正文本来就带版本号**(例如"本版没有改动补丁管线…2.1.0 那套校验结论继续适用"、`OptiFabric-1.1.2+mc…jar`
> 这样的产物名),所以当 `<旧版本>` 恰好也出现在某个更早的小节里时,那一段会被一起改掉 —— 已冻结的发布记录于是
> 被改写成当前版本。**这个坑已经手工修过五次**(2.1.0 一次,2.2.1 期间两条线各一次,2.2.2 一次,2.2.3 一次)。
>
> 只改一个版本号(`-Mc`)**不会**踩到:那时替换串带着 MC 版本(`2.2.7+mc1.21.11`),别的 MC 版本与历史串都碰不到。
>
> **每次升版都按这个顺序做**:
>
> ```powershell
> powershell -NoProfile -ExecutionPolicy Bypass -File release\version.ps1 -Line 1.21.x -Mc 1.21.11 -Kind patch -DryRun
> # 看逐文件改动数;然后真跑一次(去掉 -DryRun),再:
> git diff -- CHANGELOG.md      # ← 只允许出现"当前版本自己那一节"的改动
> ```
>
> 若历史小节被改了,**先手工改回去**再提交发布。当前脚本里**没有**做"只改当前小节"的保护,这是有意的取舍:
> 一个只认 CHANGELOG 小节的补丁会漏掉 `release/notes/mc<MC>.md` 与 `docs/RELEASE_NOTES.md` 里同样带版本号的历史段,
> 而一个通用的"只改当前版本段落"的改法在发布清单那种混合文件上更危险 —— 所以这里只把坑写清楚,不动脚本。

| 版本 | 版本号 / 标签 | jar | 字节 | SHA-256 | 正文 |
|---|---|---|---|---|---|
| 1.21 | 2.2.9+mc1.21 / v2.2.9+mc1.21 | dist\OptiFabric-2.2.9+mc1.21.jar | 799568 | 46A498D1211C0B94DE5929A21821DBFF5F151FBFBBF2ECC44156F1B518298CE3 | release/notes/mc1.21.md |
| 1.21.1 | 2.2.9+mc1.21.1 / v2.2.9+mc1.21.1 | dist\OptiFabric-2.2.9+mc1.21.1.jar | 799647 | E9118D09C610E5F1492E3B9BA2B6C9AC4E992DAB98750C258CD34C6572E37BF7 | release/notes/mc1.21.1.md |
| 1.21.3 | 2.2.9+mc1.21.3 / v2.2.9+mc1.21.3 | dist\OptiFabric-2.2.9+mc1.21.3.jar | 831430 | 089488CC21009130FDA65613684A18681A88F12AE1041363558D83C64B011ACB | release/notes/mc1.21.3.md |
| 1.21.4 | 2.2.9+mc1.21.4 / v2.2.9+mc1.21.4 | dist\OptiFabric-2.2.9+mc1.21.4.jar | 839214 | AF455C9A16475A75EC7A5219F8D789277057C9AA19279FB02142B2376190F782 | release/notes/mc1.21.4.md |
| 1.21.6 | 2.2.9+mc1.21.6 / v2.2.9+mc1.21.6 | dist\OptiFabric-2.2.9+mc1.21.6.jar | 878984 | 1414AD79749B269CA614F680D4BF93F62E5D96A27FBCCA1574DBABC17CF7DC23 | release/notes/mc1.21.6.md |
| 1.21.7 | 2.2.9+mc1.21.7 / v2.2.9+mc1.21.7 | dist\OptiFabric-2.2.9+mc1.21.7.jar | 879034 | C19490732B76601468D3AA2055013612266D446B796A5D3D17617C92A4F1C6A2 | release/notes/mc1.21.7.md |
| 1.21.8 | 2.2.9+mc1.21.8 / v2.2.9+mc1.21.8 | dist\OptiFabric-2.2.9+mc1.21.8.jar | 879106 | 33661D572BF92E1DA3F19FE0B1C2B6FD0DE4C0805DE7D8C30AE2D200448AE3A0 | release/notes/mc1.21.8.md |
| 1.21.9 | 2.2.9+mc1.21.9 / v2.2.9+mc1.21.9 | dist\OptiFabric-2.2.9+mc1.21.9.jar | 907137 | 9A1D813753796849D5FFE56F3AFFB1FC0AE0093FBDD56FB3539B0988949553B1 | release/notes/mc1.21.9.md |
| 1.21.10 | 2.2.9+mc1.21.10 / v2.2.9+mc1.21.10 | dist\OptiFabric-2.2.9+mc1.21.10.jar | 907155 | FDB5B345E28847F409C569FD13862167547D844D1D0BF14059807B7958D396B4 | release/notes/mc1.21.10.md |
| 1.21.11 | 2.2.9+mc1.21.11 / v2.2.9+mc1.21.11 | dist\OptiFabric-2.2.9+mc1.21.11.jar | 924698 | 7AB0BF69E75CFEC993DF65F436CACDE893F2B96B706747ADB1521CB7D5B72229 | release/notes/mc1.21.11.md |

## 三个平台各自要填什么

### GitHub Release(每版一个)

- Tag:`v<版本>+mc<MC版本>`,选 Create new tag on publish,target 选 **`1.21.x`** 分支的当前提交(1.21.x 线从自己的分支发布,见 [`docs/PUBLISHING.md`](../docs/PUBLISHING.md) 文首的分支表);
- Release title:OptiFabric `<版本>+mc<MC版本>`;
- Describe this release:粘贴 release/notes/mc<版本>.md 的内容(Markdown);
- Attach binaries:上传该版的 `dist\OptiFabric-<版本>+mc<MC版本>.jar`(正文里已写好它的尺寸与 SHA-256,方便用户校验)。

### Modrinth(每版一个 version)

| 字段 | 填什么 |
|---|---|
| Name | OptiFabric `<版本>+mc<MC版本>` |
| Version number | `<版本>+mc<MC版本>` |
| Release channel | Release |
| Game versions | 只勾该版对应的那一个(如 1.21.8) |
| Loaders | Fabric |
| Environment | 客户端 Required、服务端 Unsupported(OptiFabric 加载的 OptiFine 只在客户端存在) |
| Dependencies | 建议把 Fabric API 标为 required;OptiFine 无法在 Modrinth 上架,正文里已写明需用户自备对应版本的 OptiFine |
| Changelog | 粘贴 release/notes/mc<版本>.md 的内容 |
| Files | 上传该版 jar |

### CurseForge(每版一个 file)

| 字段 | 填什么 |
|---|---|
| Display name | OptiFabric `<版本>+mc<MC版本>` |
| Release type | Release |
| Game version | Minecraft <版本> 加 Fabric(只勾该版) |
| Changelog | 粘贴 release/notes/mc<版本>.md 的内容,格式选 Markdown |
| File | 上传该版 jar |

## `release\tmp\` 是每次升版都会过期的草稿

`release\tmp\` 整个目录都在 `.gitignore` 里,里面是 `publish.ps1` 上一次跑出来的提交体:

- `release\tmp\modrinth-<MC版本>.json` —— Modrinth 的 version 元数据(`name` / `version_number` / `game_versions` /
  `changelog`,changelog 就是当时那份 `release\notes\mc<MC>.md` 的全文);
- `release\tmp\curseforge-<MC版本>.json` —— CurseForge 的 upload 元数据(`displayName` / `gameVersions` / `changelog`);
- `release\tmp\github-*.md` —— 早期手工留档的 GitHub Release 正文(仅历史)。

它们的版本号、尺寸与正文**都是上一版那一刻的快照**:升版之后随便点开一个,里面写的还是旧版本号与旧正文。
**每次发布前重新生成一遍,不要手改、也不要直接复用**:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File release\publish.ps1 -DryRun      # 重写全部 tmp 草稿,-DryRun 不联网
type release\tmp\modrinth-1.21.11.json                                                # 核对版本号与 changelog 是这一版的
```

`-DryRun` 就会把草稿按 `dist\` 里的 jar 与 `release\notes\mc<MC>.md` 重写一遍(只有真发那一步需要凭据)。

**人的上传文件夹也要顺手刷新**:`C:\Users\kynar\IdeaProjects\OptiFabric\release-upload\<线>-<版本>\`,例如
`release-upload\1.21.x-2.2.1\`。里面放该版全部 jar(含 `-sources.jar`)与 `metadata\`(从 `release\tmp\` 拷
`modrinth-*.json` / `curseforge-*.json`),命名规则与已有那些目录一致:
`1.21.x-<版本>`、`26.x-<版本>`、`26.1.2-<版本>`、`1.20.6-<版本>`。它是**上传时的取件处**,不是仓库内容,
`.gitignore` 之外由人自己维护。

## 发布前自查

- 每版正文里写的 OptiFine 构建名,必须与用户实际要装的 jar 名一致(正文已写明);
- 三处都要挂上 jar 本体,不要只贴正文;
- 1.21.9 / 1.21.10 的正文里那条「光影包请用 Complementary 等主流包」别删(Photon 在 1.21.6 起的 OptiFine 上不工作);
- 先在 GitHub 发一版看排版,再批量做其余版本;
- **文档一致性**(改了支持表 / OptiFine 构建之后必须跑,离线):

  ```powershell
  powershell -NoProfile -ExecutionPolicy Bypass -File release\version.ps1 -CheckSupport
  ```

  通过的输出以 `支持表一致:三种写法都指向同一批 OptiFine 构建。` 结尾;

- **文档编码**(改过 `.md` / `.ps1` 之后抽查):`.md` / `.java` = UTF-8 **无 BOM** + CRLF;
  `.ps1` = UTF-8 **带 BOM** + CRLF。`.ps1` 丢了 BOM 会被 Windows PowerShell 当 ANSI 读、脚本直接加载失败。