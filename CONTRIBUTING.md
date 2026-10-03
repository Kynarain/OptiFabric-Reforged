# 参与开发(26.x 线)

> 这份文件只讲**怎么把改动做出来、验出来、提上来**。使用者的安装与排查见 [`README.md`](README.md) / [`README_CN.md`](README_CN.md);
> 版本号怎么定见 [`docs/VERSIONING.md`](docs/VERSIONING.md);发布怎么走见 [`docs/PUBLISHING.md`](docs/PUBLISHING.md) 与
> [`release/MANUAL_RELEASE.md`](release/MANUAL_RELEASE.md);26.x 为什么单独一条线、官方名带来的每一类冲突见
> [`docs/PORT_26.x.md`](docs/PORT_26.x.md);逐轮排查记录与离线工具链见 [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md)。

本分支是 **26.x 线**,一份源码出两个 jar:**26.2**(当前,`2.2.2`)与 **26.1.2**(`2.2.2`)。1.21.x 线(混淆名 +
yarn/intermediary,一份源码出十个版本)在自己的 worktree 里独立开发,两条线的 jar **不能互相替代**。

## 一、环境要求

| 项 | 要求 |
|---|---|
| JDK | **25**(`build.gradle` 里 `targetJavaVersion = 25`)。这也是 **26.2 自身的硬要求**:用 Java 21 跑游戏会在窗口出现前就失败 |
| Gradle | 用仓库自带的 `gradlew` / `gradlew.bat`,版本由 `gradle/wrapper` 固定,不要另装 |
| Fabric Loader | 依赖 `0.19.5`(`gradle.properties` 的 `loader_version`) |
| 游戏本体 / OptiFine | 仓库里**不放**。离线校验需要自己准备(见第三节) |

仓库根目录**就是**那个 Gradle 项目。这一线**没有 `-Pmc`**:目标版本就是 `gradle.properties` 里的 `minecraft_version`
那**一个**值,一个项目一个版本(那个开关属于 1.21.x 线,它一份源码要出十个 MC 版本)。

## 二、怎么构建

**目标版本从 `gradle.properties` 的 `minecraft_version` 来**(当前 `26.2`),换版本就是改这一个值:

```powershell
# 1) 把 gradle.properties 的 minecraft_version 改成 26.1.2(gradle.properties 的注释里写了换法)
# 2) 构建;产物名里的 mc 版本跟着 minecraft_version 走
.\gradlew build
# -> build\libs\OptiFabric-Reforged-2.2.1+mc26.1.2.jar
```

- **一次只构建一个版本**。`minecraft_version` 是单值,没有"一次出两个 jar"的开关:26.2 与 26.1.2 要**各改一次、
  各构建一次**,两次产物分别对应两个 jar。
- 版本号默认取 `gradle.properties` 的 `mod_version_base`;26.1.2 那份的例外值记在 `release/publish.ps1` 的
  `$modVersions` 里,由 `release\version.ps1 -Mc` 维护,**别手改**。
- 构建完顺手检查 jar 里**没有** `mappings/mappings.tiny`:这一线未混淆,本来就没有映射表可打包(有的话说明构建配置
  被改错了)。
- 开发环境**不受支持**:`gradlew runClient` 会被明确拒绝 —— dev 跑在 `named` 命名空间里,而这一线运行期是 `official`,
  需要额外的映射层。

### ⚠️ 发布用的 jar 必须在**干净的临时 worktree**里构建

**不要在脏工作树里构建要发布的 jar。** 工作树里放着实验性的新类(`*Probe`、`*PerDrawState`、`*ProbeFix` 之类,还有
改了没提交的 fixer)时,Gradle 会把它们**一起编译进 jar** —— 产物里于是带着没人打算发布的类,而且体积明显偏大。
本机实测过的记录:

| 构建 | 字节 |
|---|---|
| 干净工作树里构建的 **2.2.0** jar(`OptiFabric-Reforged-2.2.0+mc26.2.jar`) | **178,300** |
| 脏工作树留下的那个 2.2.0 jar(与它同名、内容不同) | **196,235** |

同一个版本号、同名产物,差了 **17,935 字节** —— 差的就是那些实验类。所以发布前按下面这样来:

```powershell
# 在一个临时 worktree 里构建,发完就删:
git -C I:\mods\OptiFabric worktree add ..\OptiFabric-release HEAD
cd ..\OptiFabric-release
.\gradlew build --offline
# 产物核对完、复制进 dist\ 之后:
git -C I:\mods\OptiFabric worktree remove ..\OptiFabric-release
```

> 这条规则对别的分支同样适用,但这条线尤其要紧:它的工作树里**现在就有**那些实验文件(见第七节),
> 而 `-RecordDigest` 读的是 `dist\`(已发布的那份),所以脏 jar 一旦被误当成发布产物,尺寸与 SHA-256 也会一起错。

## 三、离线校验(改任何字节码相关的东西都要跑)

这一线用的是 `test-downloads\verify-26.ps1`(**不是** `verify-version.ps1` —— 那个属于 yarn/intermediary 那条线):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-26.ps1 -Version 26.2
# 同一份源码再用另一个版本跑一次,确认那一版的数字没变:
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-26.ps1 -Version 26.1.2
```

`-Version` 决定工作目录、客户端 jar、OptiFine jar、Fabric API 版本与产物名。要看的数字(全部落在
`test-downloads\verify-26.log`):

| 断言 | 通过口径 |
|---|---|
| 补丁类 | `Prepared … (0 skipped, 0 failed)`、`verified OK` |
| ASM 数据流验证器 | `ASM verifier problems: 0` |
| 扫描器(`RefmapScan` / `RuntimeContractScan` / `LambdaScan` / `AtTargetScan`) | 全 0 |
| 日志 | 不得出现 `Failed to prepare` / `define failed` |

改动会改变补丁产物的形状时,记得抬 `CACHE_FORMAT`(当前 **26**),否则 harness 会复用旧缓存、看不到你的改动。

> 注意两条**只属于 1.21.x 线**的检查在这里没有对应物:那条线的 `release\version.ps1 -CheckSupport` 会离线核对
> "README 两张表 / `release\notes\` / `OptifineSupport.java`" 三处支持表是否同名,**26.x 这份 `version.ps1` 没有这个开关**;
> `verify-26.ps1` 才是这一线的离线入口(`test-downloads\` 整个目录都在 `.gitignore` 里,是本地测试工件)。
> 支持表本身仍是三处写法(两个 README + `release\notes\mc<MC>.md` + `OptifineSupport.BUILDS`),改了就**手工**逐个核对。

## 四、提交前的检查

1. **构建通过**(第二节;**发布用的 jar 必须在干净 worktree 里构建**);
2. **离线校验通过**(第三节,两个 MC 版本各一遍;只改文档时不用跑);
3. **版本号只用脚本改**(别手改):`release\version.ps1 -Line 26.x [-Mc <MC版本>] -Kind <major|minor|patch>`,
   先加 `-DryRun`;它会重写文档里的版本串,**跑完必须 `git diff` 复核**,尤其 `CHANGELOG.md` —— 见
   [`release/MANUAL_RELEASE.md`](release/MANUAL_RELEASE.md) 里那条"历史小节"警告;
4. **文件编码**(第五节);
5. **只提交你改的东西**:`release/tmp/`、`dist/`、`build/`、`test-downloads/`、`logs/`、`run/`、`config/`、`reference/`
   都已 gitignore;OptiFine 的 jar **绝不进仓库**;
6. **工作树里那 9 个未提交的实验文件**(两个改了没提交的 `OptifineJarFixer.java` / `OptifineFixer.java`,加上七个
   未跟踪的 `Optifine*Probe` / `OptiPerDrawState` / `*ProbeFix`)属于别人正在做的事:**不要** `git add -A`、
   `git add .`,更不要提交、还原或删除它们。提交时逐个点出你要提交的路径,或者 `git status` 看清楚再 `git add <路径>`。

## 五、编码与换行(踩过坑,别省)

| 文件 | 编码 | 换行 |
|---|---|---|
| `.java` / `.md` | UTF-8 **不带 BOM** | CRLF |
| `.ps1` | UTF-8 **带 BOM** | CRLF |
| `gradlew` | UTF-8 | **LF**(`.gitattributes` 里 `gradlew text eol=lf`) |
| `*.bat` | — | CRLF |

- **`.ps1` 必须带 BOM**:Windows PowerShell 在没有 BOM 时按 ANSI(本机代码页)读脚本,文件里的中文会 mis-parse,
  脚本直接加载失败(`release/publish.ps1` 顶部就写着这条注释)。所以**不要**用会去掉 BOM 的工具去"顺手统一编码"。
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
| [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) | 逐轮排查记录、离线工具链、与上游的差异 |
| [`docs/PORT_26.x.md`](docs/PORT_26.x.md) | 26.x 为什么单独一条线、官方名带来的每一类冲突(含**故意不修**的那一处) |
| [`docs/VERSIONING.md`](docs/VERSIONING.md) | 什么算不兼容修改 / 新功能 / 修订;这一线两个 MC 版本的版本号怎么记 |
| [`docs/PUBLISHING.md`](docs/PUBLISHING.md) | GitHub / CurseForge / Modrinth 上要怎么填,分支与 tag 约定 |
| [`release/MANUAL_RELEASE.md`](release/MANUAL_RELEASE.md) | 逐版数据(尺寸 / SHA-256)、发布前自查、tag 与 Latest 徽章约定 |
| [`docs/DESCRIPTION.md`](docs/DESCRIPTION.md) | 商店页文案(中英双语,可直接粘贴) |
| [`docs/RELEASE_NOTES.md`](docs/RELEASE_NOTES.md) | v2.0.0 那一版的发布正文(历史记录) |

## 七、上游与许可

核心机制移植自 [Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric)(Modmuss50、Chocohead),本项目按
**MPL-2.0** 发布(见 [`LICENSE.txt`](LICENSE.txt)),打包时会被改名成 `LICENSE.txt_OptiFabric-Reforged`。
移植文件保留来源说明。**不要**把 OptiFine 的任何类或资源打进产物,也不要打进映射表。
