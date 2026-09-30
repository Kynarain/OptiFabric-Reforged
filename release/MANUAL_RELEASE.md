# 手动发布清单(1.21.x,逐版一个发布条目)

> 本文件覆盖 **1.21.x** —— 仓库里唯一的一条发布线,10 个版本各发一个条目。

版本号统一写成 `<版本>+mc<MC版本>`(jar 名与发布标题都用这一串);**GitHub 的标签是版本号本身,不带 `+mc`**
(与已发的 `v1.1.0` 一致)。正文直接用 release/notes/mc<版本>.md(已是 Markdown,含安装步骤、已知限制、该 jar 的尺寸与 SHA-256)。

> **一个版本号可以只覆盖一个 MC 版本**:1.1.0 那次把 10 个 jar 放在同一个 `v1.1.0` 条目里;
> 只改了某一个版本的行为时就单独给那个产物升版、单独发一个条目(1.1.1 就是只修 1.21.11 的抗锯齿后处理链),
> 其余版本继续停在原来的版本号。规则与命令见 [`docs/VERSIONING.md`](../docs/VERSIONING.md)。

> 1.21.6 / 1.21.7 的说明要写准确,别写成笼统的"启动即崩":**不开光影时它们能正常启动**(2026-09-13 本机复现,标题界面正常渲染);
> **一旦启用光影(选了光影包),启动阶段就会崩在 OptiFine 自己的代码里** —— `NullPointerException: Cannot read field "norm"
> because "multiTex" is null` at `net.optifine.shaders.ShadersTex.initDynamicTextureNS`。所以正文应写成"本移植不支持这两版的光影",
> 而不是"这两版起不来"。Official 列表里这两版已是最新构建。

## 逐版数据

| 版本 | 版本号 / 标签 | jar | 字节 | SHA-256 | 正文 |
|---|---|---|---|---|---|
| 1.21 | 2.0.0+mc1.21 / v1.1.0 | dist\OptiFabric-2.0.0+mc1.21.jar | 853780 | 63D8695E12B5D5C8C4F48A9C463F89BD09AAAC987D84896BBA3E01650E981648 | release/notes/mc1.21.md |
| 1.21.1 | 2.0.0+mc1.21.1 / v1.1.0 | dist\OptiFabric-2.0.0+mc1.21.1.jar | 853780 | 63D8695E12B5D5C8C4F48A9C463F89BD09AAAC987D84896BBA3E01650E981648 | release/notes/mc1.21.1.md |
| 1.21.3 | 2.0.0+mc1.21.3 / v1.1.2 | dist\OptiFabric-2.0.0+mc1.21.3.jar | 853780 | 63D8695E12B5D5C8C4F48A9C463F89BD09AAAC987D84896BBA3E01650E981648 | release/notes/mc1.21.3.md |
| 1.21.4 | 2.0.0+mc1.21.4 / v1.1.2 | dist\OptiFabric-2.0.0+mc1.21.4.jar | 853780 | 63D8695E12B5D5C8C4F48A9C463F89BD09AAAC987D84896BBA3E01650E981648 | release/notes/mc1.21.4.md |
| 1.21.6 | 2.0.0+mc1.21.6 / v1.1.2 | dist\OptiFabric-2.0.0+mc1.21.6.jar | 853780 | 63D8695E12B5D5C8C4F48A9C463F89BD09AAAC987D84896BBA3E01650E981648 | release/notes/mc1.21.6.md |
| 1.21.7 | 2.0.0+mc1.21.7 / v1.1.2 | dist\OptiFabric-2.0.0+mc1.21.7.jar | 853780 | 63D8695E12B5D5C8C4F48A9C463F89BD09AAAC987D84896BBA3E01650E981648 | release/notes/mc1.21.7.md |
| 1.21.8 | 2.0.0+mc1.21.8 / v1.1.2 | dist\OptiFabric-2.0.0+mc1.21.8.jar | 853780 | 63D8695E12B5D5C8C4F48A9C463F89BD09AAAC987D84896BBA3E01650E981648 | release/notes/mc1.21.8.md |
| 1.21.9 | 2.0.0+mc1.21.9 / v1.1.2 | dist\OptiFabric-2.0.0+mc1.21.9.jar | 853780 | 63D8695E12B5D5C8C4F48A9C463F89BD09AAAC987D84896BBA3E01650E981648 | release/notes/mc1.21.9.md |
| 1.21.10 | 2.0.0+mc1.21.10 / v1.1.2 | dist\OptiFabric-2.0.0+mc1.21.10.jar | 853780 | 63D8695E12B5D5C8C4F48A9C463F89BD09AAAC987D84896BBA3E01650E981648 | release/notes/mc1.21.10.md |
| 1.21.11 | 2.0.0+mc1.21.11 / v1.1.2 | dist\OptiFabric-2.0.0+mc1.21.11.jar | 853780 | 63D8695E12B5D5C8C4F48A9C463F89BD09AAAC987D84896BBA3E01650E981648 | release/notes/mc1.21.11.md |

## 三个平台各自要填什么

### GitHub Release(每版一个)

- Tag:`v<版本>`(只写版本号,不带 `+mc`),选 Create new tag on publish,target 选 **`1.21.x`** 分支的当前提交(1.21.x 线从自己的分支发布,见 [`docs/PUBLISHING.md`](../docs/PUBLISHING.md) 文首的分支表);
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
