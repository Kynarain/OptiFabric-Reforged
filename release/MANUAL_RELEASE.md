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
| 1.21 | 2.1.0+mc1.21 / v2.1.0+mc1.21 | dist\OptiFabric-2.1.0+mc1.21.jar | 749929 | 97226B7409EB12EF5D5C9B2F35BFD4BE08A7D70C70E8820A0689F2F8D6AC7D46 | release/notes/mc1.21.md |
| 1.21.1 | 2.1.0+mc1.21.1 / v2.1.0+mc1.21.1 | dist\OptiFabric-2.1.0+mc1.21.1.jar | 750008 | FA1E0D78E044F826531B0D6803C42FE209E9C78CAF491614B613E2D03748FCD8 | release/notes/mc1.21.1.md |
| 1.21.3 | 2.1.0+mc1.21.3 / v2.1.0+mc1.21.3 | dist\OptiFabric-2.1.0+mc1.21.3.jar | 781791 | 3DA62226347362C81CE050DBDB60906A92BFD64EEB606E471B1AB542B73D3E8A | release/notes/mc1.21.3.md |
| 1.21.4 | 2.1.0+mc1.21.4 / v2.1.0+mc1.21.4 | dist\OptiFabric-2.1.0+mc1.21.4.jar | 789575 | BBAC480DECCEFEEB245E701DA7CFF00C6082BBF561AE39ABAB76EF28DD120E06 | release/notes/mc1.21.4.md |
| 1.21.6 | 2.1.0+mc1.21.6 / v2.1.0+mc1.21.6 | dist\OptiFabric-2.1.0+mc1.21.6.jar | 829344 | 992D601C714487DCB9E086D8C97056FACFA3FACAAE0975601D886A086FF512BF | release/notes/mc1.21.6.md |
| 1.21.7 | 2.1.0+mc1.21.7 / v2.1.0+mc1.21.7 | dist\OptiFabric-2.1.0+mc1.21.7.jar | 829394 | FB0DF39A57B24FD6E23897C7000AEF1DF15C863ECB2CA50C5352D1A2856546AB | release/notes/mc1.21.7.md |
| 1.21.8 | 2.1.0+mc1.21.8 / v2.1.0+mc1.21.8 | dist\OptiFabric-2.1.0+mc1.21.8.jar | 829466 | E6D1D5549DBCC63E4A3565632CE1DACC050D6270B95F4398E7255C1DD83AF5E6 | release/notes/mc1.21.8.md |
| 1.21.9 | 2.1.0+mc1.21.9 / v2.1.0+mc1.21.9 | dist\OptiFabric-2.1.0+mc1.21.9.jar | 857498 | 8FC6CD3F616C6262FCDA181350FAF8BD53BF566DAC2438E4646E6CCEB9B26354 | release/notes/mc1.21.9.md |
| 1.21.10 | 2.1.0+mc1.21.10 / v2.1.0+mc1.21.10 | dist\OptiFabric-2.1.0+mc1.21.10.jar | 857515 | 49E82A2AFBB211F56228663338141673EE29A2F9DB38F5A6D97D46B2775EB6DD | release/notes/mc1.21.10.md |
| 1.21.11 | 2.1.0+mc1.21.11 / v2.1.0+mc1.21.11 | dist\OptiFabric-2.1.0+mc1.21.11.jar | 875059 | 86F1EFB252EF246E29468BD309AD8B4E95117446C1D185587A075005A7AE4AF0 | release/notes/mc1.21.11.md |

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
