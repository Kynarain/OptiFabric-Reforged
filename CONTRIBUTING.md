# 参与开发(1.21.x 线)

> 这份文件只讲**怎么把改动做出来、验出来、提上来**。使用者的安装与排查见 [`README.md`](README.md) / [`README_CN.md`](README_CN.md);
> 版本号怎么定见 [`docs/VERSIONING.md`](docs/VERSIONING.md);发布怎么走见 [`docs/PUBLISHING.md`](docs/PUBLISHING.md) 与
> [`release/MANUAL_RELEASE.md`](release/MANUAL_RELEASE.md);逐轮排查记录与离线工具链见 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md)。

本分支是 **1.21.x 线**,一份源码出 **10 个 jar**(1.21 / 1.21.1 / 1.21.3 / 1.21.4 / 1.21.6 / 1.21.7 / 1.21.8 /
1.21.9 / 1.21.10 / 1.21.11)。26.x 线(未混淆,26.2 / 26.1.2)在自己的 worktree 里独立开发,两条线的 jar 不能互相替代。

## 一、环境要求

| 项 | 要求 |
|---|---|
| JDK | **21**(`build.gradle` 里 `targetJavaVersion = 21`;本机实测跑在 25 上也可以) |
| Gradle | 用仓库自带的 `gradlew` / `gradlew.bat`,版本由 `gradle/wrapper` 固定,不要另装 |
| Fabric Loader | 依赖 `0.19.5`(`gradle.properties` 的 `loader_version`),产出的 jar 只能跑在 ≥ 0.19.5 上 |
| 游戏本体 / OptiFine | 仓库里**不放**。离线校验需要自己准备(见第四节) |

仓库根目录**就是**那个 Gradle 项目 —— 没有 `v1.21.x/` 之类的子项目。

## 二、怎么构建

**每个 jar 构建时都要显式指定它对应的 MC 版本**(混淆名与 yarn 构建号逐版本不同,`build.gradle` 的 `yarnBuilds`
表里那十个就是全部受支持的版本):

```powershell
.\gradlew build "-Pmc=1.21.8"          # 产物:build\libs\OptiFabric-2.2.1+mc1.21.8.jar
```

- `-Pmc=` 的参数在 PowerShell 里**必须加引号**,否则 `1.21.8` 会被拆成 `1`;不写 `-Pmc` 时用 `gradle.properties`
  里的 `minecraft_version`(当前 `1.21.11`)。
- 版本号默认取 `gradle.properties` 的 `mod_version_base`;只有某个 jar 的版本号与整条线不同时才需要
  `"-Pmod_version_base=<该版本>"`([`docs/VERSIONING.md`](docs/VERSIONING.md) 第五节)。
- 构建某个 MC 版本**第一次**需要联网取它的 MC/yarn/intermediary;之后可以加 `--offline`。
- 十个版本一起构建:

  ```powershell
  foreach ($v in @("1.21","1.21.1","1.21.3","1.21.4","1.21.6","1.21.7","1.21.8","1.21.9","1.21.10","1.21.11")) {
      .\gradlew build "-Pmc=$v" --offline
  }
  ```

开发环境**不受支持**:`gradlew runClient` 会被明确拒绝 —— dev 跑在 `named` 命名空间里,需要额外的 contextual
mapping 层。改代码只能靠离线校验(下一节)与真机测试。

## 三、离线校验(改任何字节码相关的东西都要跑)

`test-downloads/` 里的工具只读、可重复运行,不需要启动游戏,而且**整个目录都在 `.gitignore` 里**
(它是本地测试工件,不进仓库)。全部在下一次提交前跑一遍:

```powershell
# 跑真实补丁管线,把补丁类与 OptiFine 自己的类都在单一加载器里过 JVM 验证器 + ASM 数据流验证器,再跑各扫描器
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-version.ps1 -Version 1.21.10 -ModVersion 1.1.2
```

要看的数字(`docs/DEVELOPMENT.md` 的"离线校验工具链"一节有逐项解释):

| 断言 | 通过口径 |
|---|---|
| 补丁类 | `Prepared … (0 skipped, 0 failed)`、`verified OK` |
| ASM 数据流验证器 | `ASM verifier problems: 0` |
| 各扫描器(`RefmapScan` / `AtTargetScan` / `LocalsScan` / `ShadowScan` / `LambdaScan` …) | 除**有意中和**的 indigo 那几条外全 0 |
| 日志 | 不得出现 `Failed to prepare` / `define failed` |

改动会改变补丁产物的形状时,记得抬 `CACHE_FORMAT`(当前 **26**),否则 harness 会复用旧缓存、看不到你的改动。

## 四、提交前的检查

1. **构建通过**(第 2 节),产物名与你要发的版本一致;
2. **离线校验通过**(第 3 节;只改文档时不用跑);
3. **支持表一致** —— 改了支持的 MC / OptiFine 构建之后必须跑(`README.md` 两张表、`release/notes/mc<MC>.md`
   与模组内置的 `OptifineSupport.java` 三处必须同名,离线,不联网):

   ```powershell
   powershell -NoProfile -ExecutionPolicy Bypass -File release\version.ps1 -CheckSupport
   ```

4. **版本号只用脚本改**(别手改):`.\release\version.ps1 -Line 1.21.x -Mc <MC版本> -Kind <major|minor|patch> -DryRun`
   先看结果。它会重写文档里的版本串,**跑完必须 `git diff` 复核**,尤其 `CHANGELOG.md` —— 见
   [`release/MANUAL_RELEASE.md`](release/MANUAL_RELEASE.md) 里那条"历史小节"警告;
5. **文件编码**(下一节);
6. **只提交你改的东西**:`release/tmp/`、`dist/`、`build/`、`test-downloads/` 都已 gitignore;OptiFine 的 jar
   **绝不进仓库**。

## 五、编码与换行(踩过坑,别省)

| 文件 | 编码 | 换行 |
|---|---|---|
| `.java` / `.md` | UTF-8 **不带 BOM** | CRLF |
| `.ps1` | UTF-8 **带 BOM** | CRLF |
| `gradlew` | UTF-8 | **LF**(`.gitattributes` 里 `gradlew text eol=lf`) |
| `*.bat` | — | CRLF |

- **`.ps1` 必须带 BOM**:Windows PowerShell 在没有 BOM 时按 ANSI(本机代码页)读脚本,文件里的中文会mis-parse,
  脚本直接加载失败(`release/publish.ps1` 与 `release/version.ps1` 顶部都写了这条注释)。所以**不要**用会去掉 BOM
  的编辑器/工具去"顺手统一编码"。
- **`.md` / `.java` 不要带 BOM**:这些文件是给 Git、GitHub 与 javac 看的,加了 BOM 会在 diff 与工具输出里留下垃圾字符。
- 仓库的 `.gitattributes` 是 `* text=auto`,加上本机 `core.autocrlf=true`,检出的文本文件就是 CRLF;新增文件请
  按上表写,**不要**提交 LF 的 `.ps1` / `.md`。

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
| [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) | 逐轮排查记录、每一类冲突的根因与修法、离线工具链、与上游的差异 |
| [`docs/VERSIONING.md`](docs/VERSIONING.md) | 什么算不兼容修改 / 新功能 / 修订,一次升版要动哪些文件,`-Mc` 逐版本升版 |
| [`docs/PUBLISHING.md`](docs/PUBLISHING.md) | GitHub / CurseForge / Modrinth 上要怎么填,分支与 tag 约定 |
| [`release/MANUAL_RELEASE.md`](release/MANUAL_RELEASE.md) | 逐版数据(尺寸 / SHA-256)、发布前自查、tag 与标签约定 |
| [`docs/DESCRIPTION.md`](docs/DESCRIPTION.md) | 商店页文案(中英双语,可直接粘贴) |
| [`docs/RELEASE_NOTES.md`](docs/RELEASE_NOTES.md) / [`docs/RELEASE_NOTES_1.21.x.md`](docs/RELEASE_NOTES_1.21.x.md) | 发布正文 |

## 七、上游与许可

核心机制移植自 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)(Modmuss50、Chocohead),本项目按
**MPL-2.0** 发布(见 [`LICENSE.txt`](LICENSE.txt))。移植文件保留来源说明;新增文件在文件头注明
`New in the 1.20.6 port …` / `New in the 1.21.11 port …` 之类。**不要**把 OptiFine 的任何类或资源打进产物。
