# 参与开发(1.20.6 线)

> 这份文件只讲**怎么把改动做出来、验出来、提上来**。使用者的安装与排查见 [`README.md`](README.md);
> 发布怎么走见 [`docs/PUBLISHING.md`](docs/PUBLISHING.md);已知问题见 [`docs/FAQ.md`](docs/FAQ.md);
> 逐轮排查记录与离线校验工具见 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md)。

本分支是 **1.20.6 线**:`main` 出已发布的那一个 jar(mod id 仍是 `optifabric`),`wip/1.20.6-reforged` 出替代产物
`OptiFabric-Reforged-1.1.3-reforged+mc1.20.6.jar`(mod id `optifabric_reforged`;两者**只能装一个**,见
[`docs/REFORGED_BUILD.md`](docs/REFORGED_BUILD.md))。更新的两条线
(1.21.x、26.x)在各自的 worktree/分支上独立开发,各线的 jar **不能互相替代**。

## 一、环境要求

| 项 | 要求 |
|---|---|
| JDK | **21**(`build.gradle` 里 `targetJavaVersion = 21`) |
| Gradle | 用仓库自带的 `gradlew` / `gradlew.bat`,版本由 `gradle/wrapper` 固定,不要另装 |
| Fabric Loader | 依赖 `0.19.3`(`gradle.properties` 的 `loader_version`) |
| 游戏本体 / OptiFine | 仓库里**不放**。离线校验需要自己准备(见第三节) |

仓库根目录**就是**那个 Gradle 项目。这一线**没有 `-Pmc`**:`gradle.properties` 里的
`minecraft_version=1.20.6` 就是唯一目标。

## 二、怎么构建

```powershell
.\gradlew build --offline
# 产物:build\libs\OptiFabric-1.1.3+mc1.20.6.jar
```

- 版本号来自 `gradle.properties` 的 **`mod_version`**(注意:这一线不叫 `mod_version_base`,也不按 MC 版本分叉)。
- 首次构建若 `--offline` 失败,去掉它联网补齐依赖;之后就可以离线。
- 开发环境**不受支持**:`gradlew runClient` 会被明确拒绝 —— dev 跑在 `named` 命名空间里,需要额外的
  contextual mapping 层。

## 三、离线校验

这一线的离线口径是**字节码校验**:在与游戏一致的单一加载器里加载每个补丁类,过 JVM 验证器 + ASM 数据流验证器。
当前记下的结果是 **425 / 425 通过、ASM 数据流验证器 0 问题**(`CHANGELOG.md` 的 1.0.0 一节),对应那一份产物。

> 本机这个 worktree 里**没有** `test-downloads/`(整个目录在 `.gitignore` 里,是本地测试工件):需要重跑就按
> [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md)「离线校验工具链」一节里的工具名与用法自己搭起来,结论也写回那里。
> 1.1.1 那次改动**没有**重跑离线校验 —— 它发生在补丁安装之后,不动任何 fixer 与补丁管线,425 / 425 仍对应 1.0.0
> 那份产物(`CHANGELOG.md` 的 1.1.1 一节写明了这一点)。

## 四、提交前的检查

1. **构建通过**(第二节),产物名与要发的版本一致;
2. **离线校验**:改了字节码相关的东西就跑(第三节),把数字写进 `CHANGELOG.md`;
3. **版本号**:这一线**没有** `release/version.ps1` —— 版本号手改 `gradle.properties` 的 `mod_version`,
   并同步 `fabric.mod.json` 里展开的 `${version}` 会跟着走;**改完把下面这几处一起核对**:
   - `gradle.properties` 的 `mod_version`
   - `README.md` 里的产物名与「支持的版本」表
   - `CHANGELOG.md` 顶部新增一节(它同时是发布正文,见 [`docs/PUBLISHING.md`](docs/PUBLISHING.md))
   - `docs/PUBLISHING.md` 里的产物名
   这一线没有脚本兜底,漏一处就是"文档写 A、jar 是 B"。
4. **文件编码**(第五节);
5. **只提交你改的东西**:`build/`、`dist/`、`test-downloads/`、`reference/`、`*.log` 都已 gitignore;
   OptiFine 的 jar **绝不进仓库**。

## 五、编码与换行(踩过坑,别省)

| 文件 | 编码 | 换行 |
|---|---|---|
| `.java` / `.md` | UTF-8 **不带 BOM** | CRLF |
| `.ps1` | UTF-8 **带 BOM** | CRLF |
| `gradlew` | UTF-8 | **LF**(`.gitattributes` 里 `gradlew text eol=lf`) |
| `*.bat` | — | CRLF |

- **`.ps1` 必须带 BOM**:Windows PowerShell 在没有 BOM 时按 ANSI(本机代码页)读脚本,文件里的中文会 mis-parse,
  脚本直接加载失败(另外两条线的 `release/publish.ps1` 顶部就写着这条注释)。
- **`.md` / `.java` 不要带 BOM**:这些文件是给 Git、GitHub 与 javac 看的。
- 仓库的 `.gitattributes` 是 `* text=auto`,加上本机 `core.autocrlf=true`,检出的文本文件就是 CRLF;新增文件请按上表写。

写完可以逐个文件自查(把路径换成你改的文件):

```powershell
$f = "docs\DEVELOPMENT.md"
$b = [System.IO.File]::ReadAllBytes($f)
"BOM: " + ($b[0] -eq 0xEF -and $b[1] -eq 0xBB -and $b[2] -eq 0xBF)
"LF-only lines: " + ([regex]::Matches([System.Text.Encoding]::UTF8.GetString($b), "(?<!`r)`n").Count)
```

## 六、还有哪些文档

| 文档 | 讲什么 |
|---|---|
| [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) | 逐轮排查记录、离线校验工具链 |
| [`docs/FAQ.md`](docs/FAQ.md) | 已知问题(启动项、日志、以及和别的模组的冲突) |
| [`docs/PUBLISHING.md`](docs/PUBLISHING.md) | GitHub / CurseForge 上要怎么填,分支与 tag 约定 |
| [`docs/DESCRIPTION.md`](docs/DESCRIPTION.md) | 商店页文案(中英双语,可直接粘贴) |
| [`CHANGELOG.md`](CHANGELOG.md) | 逐版本记录;**它同时是发布正文** |

## 七、上游与许可

核心逻辑移植自 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)(Modmuss50、Chocohead),本项目按
**MPL-2.0** 发布(见 [`LICENSE.txt`](LICENSE.txt)),移植文件保留来源说明。**不要**把 OptiFine 的任何类或资源打进
产物。`reference/upstream/` 是本机留的上游源码快照(已 gitignore)。
