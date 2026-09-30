# 手动发布清单(1.21.x,逐版一个发布条目)

> 本文件覆盖 **1.21.x** —— 仓库里唯一的一条发布线,10 个版本各发一个条目。

版本号统一写成 `<版本>+mc<MC版本>`(jar 名、release 标题与 **GitHub tag** 都用这一串)。tag 必须逐版本唯一:这个仓库同时承载 26.x 线,该线已占用 `v2.0.0`(两条线各自都把"换 mod id"当成大版本),所以 tag 带上 `+mc<MC版本>`。
正文直接用 release/notes/mc<版本>.md(已是 Markdown,含安装步骤、已知限制、该 jar 的尺寸与 SHA-256)。

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

| 版本 | 版本号 / 标签 | jar | 字节 | SHA-256 | 正文 |
|---|---|---|---|---|---|
| 1.21 | 2.0.0+mc1.21 / v2.0.0+mc1.21 | dist\OptiFabric-2.0.0+mc1.21.jar | 746861 | B54E35023B2E49166DDA2E0E9728050216B3A1CCE63D5A861865B7CB5E354EAA | release/notes/mc1.21.md |
| 1.21.1 | 2.0.0+mc1.21.1 / v2.0.0+mc1.21.1 | dist\OptiFabric-2.0.0+mc1.21.1.jar | 746940 | 20DD6B609D526217DF87F5D6B51E41710A5458E9A641D864B74982CDC7AC1FCE | release/notes/mc1.21.1.md |
| 1.21.3 | 2.0.0+mc1.21.3 / v2.0.0+mc1.21.3 | dist\OptiFabric-2.0.0+mc1.21.3.jar | 778724 | 0AA6276F09C402D861D85E0D8835284270884756F2034A31FB2AD20683CEBA7C | release/notes/mc1.21.3.md |
| 1.21.4 | 2.0.0+mc1.21.4 / v2.0.0+mc1.21.4 | dist\OptiFabric-2.0.0+mc1.21.4.jar | 786508 | 2E466E29C95CF3A21EC60756AA412FFF4911329DC271352BC6581441B64A5498 | release/notes/mc1.21.4.md |
| 1.21.6 | 2.0.0+mc1.21.6 / v2.0.0+mc1.21.6 | dist\OptiFabric-2.0.0+mc1.21.6.jar | 826277 | 6E8C5EC896F4859095045A0EA02E4ED0283E64009D9CE942C15E9C1612B255DD | release/notes/mc1.21.6.md |
| 1.21.7 | 2.0.0+mc1.21.7 / v2.0.0+mc1.21.7 | dist\OptiFabric-2.0.0+mc1.21.7.jar | 826327 | 0F8C635C0A7E3F0A1D9129D0F1FE68129BFB8CDD0C0FDAB3DF3282B064373A9B | release/notes/mc1.21.7.md |
| 1.21.8 | 2.0.0+mc1.21.8 / v2.0.0+mc1.21.8 | dist\OptiFabric-2.0.0+mc1.21.8.jar | 826399 | 6061BFAF7782D4A85C5ABCA6C7151D56B5B1CEB7B6D4B18DDC1FCDFC823BB58B | release/notes/mc1.21.8.md |
| 1.21.9 | 2.0.0+mc1.21.9 / v2.0.0+mc1.21.9 | dist\OptiFabric-2.0.0+mc1.21.9.jar | 854430 | 3ABE4CF0BB447748E20198913968EEA1B096F8467CA242365E4779C0E32EB40A | release/notes/mc1.21.9.md |
| 1.21.10 | 2.0.0+mc1.21.10 / v2.0.0+mc1.21.10 | dist\OptiFabric-2.0.0+mc1.21.10.jar | 854449 | 0697D758AC60D820E326D385B54211F7E8B4BBB3D4708E33C6B9928EB35227DC | release/notes/mc1.21.10.md |
| 1.21.11 | 2.0.0+mc1.21.11 / v2.0.0+mc1.21.11 | dist\OptiFabric-2.0.0+mc1.21.11.jar | 871992 | 7AB4B03405C1809AC3E4F9D06905013DAB6A514030C714FF4E65723BC7C63877 | release/notes/mc1.21.11.md |

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

## 发布前自查

- 每版正文里写的 OptiFine 构建名,必须与用户实际要装的 jar 名一致(正文已写明);
- 三处都要挂上 jar 本体,不要只贴正文;
- 1.21.9 / 1.21.10 的正文里那条「光影包请用 Complementary 等主流包」别删(Photon 在 1.21.6 起的 OptiFine 上不工作);
- 先在 GitHub 发一版看排版,再批量做其余版本。
