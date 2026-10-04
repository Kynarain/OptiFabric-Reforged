# 手动发布清单(1.21.x,逐版一个发布条目)

> 本文件覆盖 **1.21.x** —— 仓库里唯一的一条发布线,10 个版本各发一个条目。

版本号统一写成 `<版本>+mc<MC版本>`(jar 名、release 标题与 **GitHub tag** 都用这一串)。tag 必须逐版本唯一:这个仓库同时承载 26.x 线,该线已占用 `v2.0.0`(两条线各自都把"换 mod id"当成大版本),所以 tag 带上 `+mc<MC版本>`。
正文直接用 release/notes/mc<版本>.md(已是 Markdown,含安装步骤、已知限制、该 jar 的尺寸与 SHA-256)。

> **tag 归属(两条线都别忘)**:`v<版本>+mc<MC版本>` 这一串只属于本线。仓库的 **Latest 徽章归 26.x 线** —— 它的
> 当前产物用**裸标签** `v2.2.2`(`v2.1.0` / `v2.2.0` / `v2.2.1` 也是这样,见那一线的 `release/MANUAL_RELEASE.md`)。
> 所以本线这十个条目都要用 **`make_latest=false`** 创建,否则最后发的那个 jar 会把 Latest 从 26.x 抢过来。
> 已发的 `v2.2.4+mc1.21` … `v2.2.4+mc1.21.11` 十个 tag 就是按这个口径打的(`git tag --list` 可复核)。

> **一个版本号可以只覆盖一个 MC 版本**:1.1.0 那次把 10 个 jar 放在同一个 `v1.1.0` 条目里;
> 只改了某一个版本的行为时就单独给那个产物升版、单独发一个条目(1.1.1 就是只修 1.21.11 的抗锯齿后处理链),
> 其余版本继续停在原来的版本号。规则与命令见 [`docs/VERSIONING.md`](../docs/VERSIONING.md)。

> 1.21.6 / 1.21.7 的说明要写准确,别写成笼统的"启动即崩":**不开光影时它们能正常启动**(2026-09-13 本机复现,标题界面正常渲染);
> **一旦启用光影(选了光影包),启动阶段就会崩在 OptiFine 自己的代码里** —— `NullPointerException: Cannot read field "norm"
> because "multiTex" is null` at `net.optifine.shaders.ShadersTex.initDynamicTextureNS`。所以正文应写成"本移植不支持这两版的光影",
> 而不是"这两版起不来"。Official 列表里这两版已是最新构建。

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
> 只改一个版本号(`-Mc`)**不会**踩到:那时替换串带着 MC 版本(`2.2.4+mc1.21.11`),别的 MC 版本与历史串都碰不到。
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
| 1.21 | 2.2.4+mc1.21 / v2.2.4+mc1.21 | dist\OptiFabric-2.2.4+mc1.21.jar | 792224 | CDB646DE159F3E299DB47F1DED1A3D8E016842774B33170DD3BC7FC1E6A81B9D | release/notes/mc1.21.md |
| 1.21.1 | 2.2.4+mc1.21.1 / v2.2.4+mc1.21.1 | dist\OptiFabric-2.2.4+mc1.21.1.jar | 792303 | 280196D92E74B269A98E39D45301C573B763F2BE37ACC9D2ED1D3DB39363B62A | release/notes/mc1.21.1.md |
| 1.21.3 | 2.2.4+mc1.21.3 / v2.2.4+mc1.21.3 | dist\OptiFabric-2.2.4+mc1.21.3.jar | 824086 | EB7838A5F62A9EFF1996D2921FF3528C9B75190C01F256C53338418EB5E29566 | release/notes/mc1.21.3.md |
| 1.21.4 | 2.2.4+mc1.21.4 / v2.2.4+mc1.21.4 | dist\OptiFabric-2.2.4+mc1.21.4.jar | 831871 | 01F64B2C0329E0B50E28E2FB63A50EDD042287E443C264DBB55CFC10E2874877 | release/notes/mc1.21.4.md |
| 1.21.6 | 2.2.4+mc1.21.6 / v2.2.4+mc1.21.6 | dist\OptiFabric-2.2.4+mc1.21.6.jar | 871653 | B1CA8A73AF1957BD51DC84EDD01D089C0775E077DA809DA3A0C7B105BF165211 | release/notes/mc1.21.6.md |
| 1.21.7 | 2.2.4+mc1.21.7 / v2.2.4+mc1.21.7 | dist\OptiFabric-2.2.4+mc1.21.7.jar | 871703 | B0452B5ACDD22FD13FBC975F87961983B593C7203F5B681E13E0470911A91B12 | release/notes/mc1.21.7.md |
| 1.21.8 | 2.2.4+mc1.21.8 / v2.2.4+mc1.21.8 | dist\OptiFabric-2.2.4+mc1.21.8.jar | 871775 | B099DE2C5F5806355820A3B2D6DFC5502585EFA2C8D4E868D1A834FE8FD61160 | release/notes/mc1.21.8.md |
| 1.21.9 | 2.2.4+mc1.21.9 / v2.2.4+mc1.21.9 | dist\OptiFabric-2.2.4+mc1.21.9.jar | 899806 | B8BF250AD8D4E31423AF0DA4013CB03F16840DF34D9E3ACB8E911C59BF870ED0 | release/notes/mc1.21.9.md |
| 1.21.10 | 2.2.4+mc1.21.10 / v2.2.4+mc1.21.10 | dist\OptiFabric-2.2.4+mc1.21.10.jar | 899824 | E6B208F67323212624C80403FD6078A2D38F062ADE4D810526CFAAB2686A6D06 | release/notes/mc1.21.10.md |
| 1.21.11 | 2.2.4+mc1.21.11 / v2.2.4+mc1.21.11 | dist\OptiFabric-2.2.4+mc1.21.11.jar | 917368 | 3914F1233302F0B26C062BC4FC14B819523636374595682C3C9D357A3F15A184 | release/notes/mc1.21.11.md |

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
