# 手动发布清单(26.x,逐版一个发布条目)

> 本文件覆盖 **26.x** —— 仓库里唯一的一条发布线,现在有两个产物:**26.2**(当前,`2.1.0`)与
> **26.1.2**(`2.0.0`,已发布、内容不变)。另一条线(1.21.x,混淆名 +
> yarn/intermediary,一份源码出十个 MC 版本)在自己的分支上,两条线的 jar 不能互相替代。

版本号统一写成 `<版本>+mc<MC版本>`(jar 名与发布标题都用这一串);**GitHub 的标签是版本号本身,不带 `+mc`**
(与已发的 `v1.2.0` / `v2.0.0` 一致)。正文直接用 release/notes/mc<版本>.md(已是 Markdown,含安装步骤、已知限制、该 jar 的尺寸与 SHA-256)。

> **这一线现在覆盖两个 MC 版本**,所以升版分两种:整条线一起升版是
> `.\release\version.ps1 -Line 26.x -Kind <major|minor|patch>`(它改的是基数,也就是 26.2 那份);
> 只给某一个 MC 版本升版用 `-Mc <MC 版本>`,它写的是 `release\publish.ps1` 的 `$modVersions` 例外值表。
> 规则与命令见 [`docs/VERSIONING.md`](../docs/VERSIONING.md)。

> 发布说明里有两个必须写准的点:**Java 25 是 26.2 自身的硬要求**(与 1.21.x 的 Java 21 不同,写在最前面);
> **只支持 26.2 与 26.1.2** —— 26.2.1 与 26.3 至今没有 OptiFine 构建,别让用户拿这个 jar 去顶别的版本。

## 逐版数据

| 版本 | 版本号 / 标签 | jar | 字节 | SHA-256 | 正文 |
|---|---|---|---|---|---|
| 26.2 | 2.1.0+mc26.2 / v2.1.0 | dist\OptiFabric-Reforged-2.1.0+mc26.2.jar | 139528 | ED3DD297FBE7356C9A9C69DCAAD9FC3C2AC9000C12FEB7BCF3CE9D3277028D8D | release/notes/mc26.2.md |
| 26.1.2 | 2.0.0+mc26.1.2 / v2.0.0 | dist\OptiFabric-Reforged-2.0.0+mc26.1.2.jar | 177166 | FBB432C2D9C8B0E7E06F0FDA4A0C1B6A8F302D5D09ABD7CE67F13CBE04A5CF60 | release/notes/mc26.1.2.md |

## 三处平台各自要填什么

### GitHub Release

- **Tag**:`v2.1.0`(本仓库的 tag 只写版本号,已发布的 `v1.2.0` / `v2.0.0` 就是这样;MC 版本留在产物名与标题里),
  target 选 **`26.x` 分支的当前提交**;
- **Release title**:OptiFabric Reforged 2.1.0+mc26.2;
- **Describe this release**:粘贴 `release/notes/mc26.2.md`(Markdown);
- **Attach binaries**:上传 `dist\OptiFabric-Reforged-2.1.0+mc26.2.jar`(正文里已写好尺寸与 SHA-256,方便用户校验)。

### Modrinth

| 字段 | 填什么 |
|---|---|
| Name | OptiFabric Reforged 2.1.0+mc26.2 |
| Version number | 2.1.0+mc26.2 |
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
| Display name | OptiFabric Reforged 2.1.0+mc26.2 |
| Release type | Release |
| Game version | Minecraft **26.2** 加 Fabric(只勾该版) |
| Changelog | 粘贴 `release/notes/mc26.2.md`,格式选 Markdown |
| File | 上传该版 jar |

> 若项目页面的**支持版本范围**里还没有 26.2,先在项目设置里加上,再传文件(26.1.2 那份已经发过,原样留着)。

## 发布前自查

- [ ] **版本号已按 SemVer 改好**:`.\release\version.ps1 -Line 26.x -Kind <major|minor|patch>`(整条线;
      只给一个 MC 版本升版就加 `-Mc 26.2`。规则见 [`docs/VERSIONING.md`](../docs/VERSIONING.md);
      不要手改 —— 一次要动 9 个文件);
- [ ] `.\gradlew build --offline` 通过(这一线没有 `-Pmc`,目标版本来自根目录 `gradle.properties` 的
      `minecraft_version`),且产物名是 `OptiFabric-Reforged-2.1.0+mc26.2.jar`;
- [ ] `.\release\version.ps1 -Line 26.x -RecordDigest` 跑过,正文里的尺寸与 SHA-256 与 `dist\` 里的 jar 一致;
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
- [ ] 先在 GitHub 发一版看排版,再补 Modrinth / CurseForge。
