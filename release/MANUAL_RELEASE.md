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
| 1.21 | 2.2.0+mc1.21 / v2.2.0+mc1.21 | dist\OptiFabric-2.2.0+mc1.21.jar | 789589 | 19EF1257CA646A0E93D99919AAE8692F181C2670C7EFB2C58D83D98396D751E9 | release/notes/mc1.21.md |
| 1.21.1 | 2.2.0+mc1.21.1 / v2.2.0+mc1.21.1 | dist\OptiFabric-2.2.0+mc1.21.1.jar | 789668 | 643338E948B4461A39BA33D5B6AF36FF0DFC1211DBB741705628CFD4659777D1 | release/notes/mc1.21.1.md |
| 1.21.3 | 2.2.0+mc1.21.3 / v2.2.0+mc1.21.3 | dist\OptiFabric-2.2.0+mc1.21.3.jar | 821452 | 288B219AE17F211BA09D6A43378ABB241AD19A4FFCFBEAD8EFA64EB39051CE74 | release/notes/mc1.21.3.md |
| 1.21.4 | 2.2.0+mc1.21.4 / v2.2.0+mc1.21.4 | dist\OptiFabric-2.2.0+mc1.21.4.jar | 829236 | 7927B5C08F6C71B8793C5E7DA8A0093C98AAB92B476BF50AE45822C4245A145E | release/notes/mc1.21.4.md |
| 1.21.6 | 2.2.0+mc1.21.6 / v2.2.0+mc1.21.6 | dist\OptiFabric-2.2.0+mc1.21.6.jar | 869018 | B72C71D3B5CC8774BB10D9BD5B03523E33558568B17E0AE3550BEF7DD59880BF | release/notes/mc1.21.6.md |
| 1.21.7 | 2.2.0+mc1.21.7 / v2.2.0+mc1.21.7 | dist\OptiFabric-2.2.0+mc1.21.7.jar | 869068 | 0D84560F8BC1F815639A8C408C06605379AD421A8E8B6C480C7EE92F2D017B48 | release/notes/mc1.21.7.md |
| 1.21.8 | 2.2.0+mc1.21.8 / v2.2.0+mc1.21.8 | dist\OptiFabric-2.2.0+mc1.21.8.jar | 869140 | B9299D13A15A1D06054CA1F5BB615BC7D561CC2B225B497D2D1BD25A307FD104 | release/notes/mc1.21.8.md |
| 1.21.9 | 2.2.0+mc1.21.9 / v2.2.0+mc1.21.9 | dist\OptiFabric-2.2.0+mc1.21.9.jar | 897172 | 358CF595261E535FA60FD3F6FC125321DFEB43A9CDB4B5EBAC90DB20E2C47F6C | release/notes/mc1.21.9.md |
| 1.21.10 | 2.2.0+mc1.21.10 / v2.2.0+mc1.21.10 | dist\OptiFabric-2.2.0+mc1.21.10.jar | 897190 | CB37A899EE7A01E2C0FD516471B6A88AF068077BA32057288E6FE1EDDE43A264 | release/notes/mc1.21.10.md |
| 1.21.11 | 2.2.0+mc1.21.11 / v2.2.0+mc1.21.11 | dist\OptiFabric-2.2.0+mc1.21.11.jar | 914733 | 7301EDD4B7634E8C8EE1B1845A0D1F73651396AF3A9E18E7E1F4988E298F2ACC | release/notes/mc1.21.11.md |

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
