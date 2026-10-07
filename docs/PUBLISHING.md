# 发布指引(GitHub / CurseForge)

本文档记录"把本项目发出去"需要做的步骤。仓库里已经准备好的东西、以及**你还需要自己做的部分**都写在下面。

> 本仓库只有**一条发布线**:**26.x** = `2.2.8+mc26.2`(26.1.2 那一个是 `2.2.8+mc26.1.2`,这一版两个 MC 版本各出一个 jar)。
> `2.2.3` 是修订版(当前):给 OptiFine 那份 `GuiRenderer$Draw` 补上 Fabric API 的 accessor 接口,修好"装了 OptiFine
> 就起不来"那一处(修 fixer / 修兼容性 → 修订号);
> `2.2.2` 也是修订版:`fabric.mod.json` 里把 `sodium` 同时声明进 `conflicts` 与 `breaks`(修正元数据/文档 → 修订号),
> 并把已声明的不兼容清单写进两份 README;
> `2.2.1` 也是修订版:丢掉 Mixin 为被本模组替换掉的类缓存的 `ClassInfo`,修好 `@ModifyVariable` 在 OptiFine 改过的类上
> 抛 `LVTGeneratorError`、整个类变换失败的问题(26.2 与 26.1.2 都受它影响,所以两份一起发);
> `2.2.0` 新增了缺 OptiFine 时的提示与官方站下载(功能新增 → 次版本号);`2.1.1` 是修订版,它取代 `2.1.0+mc26.2`
> —— 那一版把 OptiFine 取消掉的光影包加载强行打开,选了光影包的世界只画粒子;
> **26.2 上光影不可用**这一点必须在发布说明里写明(release/notes/mc26.2.md 已经写好)。
> 26.1 起 Minecraft **未混淆**,官方名即运行名,
> 没有 yarn、也没有真正的 intermediary 可重映射(26.1.2 只发布占位 `intermediary:0.0.0`),所以这一线是
> 另一套构建与运行期路径 —— 仓库根目录就是那个 Gradle 项目,下面各节都以它为例。
>
> **分支:**
>
> | 分支 | 用途 |
> |---|---|
> | **`26.x`** | **这一线的开发与发布分支**。26.x 的修复提交在这里,26.x 的 tag 也打在这里 |
> | `wip/26.2` | 本机的 26.x 开发 worktree(`I:\mods\OptiFabric` 就停在这个分支上)。它的提交与 `26.x` 同步(`origin/26.x` 与 `origin/wip/26.2` 当前指向同一个提交);发布时 tag 的 target 用 **`26.x`** |
> | `1.21.x` | 另一条线(混淆名 + yarn/intermediary,一份源码出十个版本)的开发与发布分支,与本线无关。**它有自己的 worktree** `I:\mods\OptiFabric-1.21.x`,父仓库里不要 checkout 它 |
> | `main` | 1.20.6 线(worktree `I:\mods\OptiFabric-1.20.6`)的开发与发布分支;本仓库 1.0.0 那次也是从这里发的 |
> | `mc1.21.x`、`mc1.21.11` | 历史:1.1.0 发布时的**旧布局**(仓库根目录单项目),只作保留、不再更新 |
>
> ⚠️ **`mc1.21.x` 不是 1.21.x 的开发分支** —— 名字像,内容是 1.1.0 那一刻的快照。
>
> **发布标签是版本号本身**(已发的:`v1.2.0`、`v2.0.0`、`v2.1.0`、`v2.1.1`、`v2.2.0`、`v2.2.1`、`v2.2.2`),不带 `+mc` —— MC 版本留在
> 产物名与标题里;每个版本一个条目,旧条目原样保留。
> **例外**:同一个版本号要出两个 jar 时,26.2 那一份用**裸标签**(它是这一线的"当前版本"),26.1.2 那一份**加后缀**,
> 否则两份抢同一个标签名 —— `v2.2.0` / `v2.2.0+mc26.1.2` 与 `v2.2.1` / `v2.2.1+mc26.1.2` 就是这么发的,`v2.2.2` 与 `v2.2.2+mc26.1.2` 照此办理(2.2.3 是 `v2.2.3` 与 `v2.2.3+mc26.1.2`)。
> ✅ `release/publish.ps1` **自己算对两个 tag**(2.2.6 起):它带一张逐 MC 版本的后缀表 `$modTagSuffixes`
> (`@{ "26.1.2" = "+mc26.1.2" }`),`$tag` 是 `"v$modVersion" + 后缀` —— 26.2 得到裸的 `v2.2.6`,26.1.2 得到
> `v2.2.8+mc26.1.2`,`.\release\publish.ps1 -DryRun` 会把两个 `gh release create` 命令连 tag 一起打出来。
> **2.2.4 及更早**这里是一句警告:脚本对两个版本都算成裸标签,26.1.2 那个带后缀的 tag 只能手工打
> (`v2.2.0+mc26.1.2` / `v2.2.1+mc26.1.2` / `v2.2.2+mc26.1.2` 就是这么发的),否则两个条目抢同一个 tag。
> 本仓库 2.2.3 的两个 tag(`v2.2.3` 与 `v2.2.3+mc26.1.2`)**都是手工打的**。
> `release/publish.ps1` 取 tag 的目标分支用脚本顶部的 `$defaultTagTarget`(当前为 `26.x`)。
>
> **Latest 徽章归本线**(26.2 那一份的裸标签)。**1.21.x 与 1.20.6 的每一个发布条目都必须用 `make_latest=false`
> 创建** —— 那两条线的 tag 都带 `+mc`,而 Latest 只能有一个;它们的 `release/MANUAL_RELEASE.md` 也是这么写的。

> **版本号规则**:本项目按 [语义化版本 2.0.0](https://semver.org/lang/zh-CN/) 定版本,`+mc<版本>` 是编译信息。
> 什么算不兼容修改、什么算新功能、一次改动要同步哪些文件,全部写在 [`docs/VERSIONING.md`](VERSIONING.md);
> **不要手改版本号**(一次要动 9 个文件,漏一处就文档与产物对不上)。

- **没有 `-Pmc=`**:这一线的目标版本就是根目录 `gradle.properties` 里的 `minecraft_version` 那一个值,一个项目一个版本
  (那个开关属于 1.21.x 线 —— 它一份源码要出十个 MC 版本,构建时要按版本传参);
- **Java 25**(26.2 自身的硬要求,26.1.2 也一样),发布说明里要提醒用户;
- 离线校验用 `test-downloads\verify-26.ps1`(不是 `verify-version.ps1`,后者属于 yarn/intermediary 那条线);
- 正文、逐版数据、三个平台要填的字段都在 `release/MANUAL_RELEASE.md` 里;
- 别把 26.x 的 jar 传成别的版本的 —— 文件名里的 `mc` 版本是唯一的区分点。

## 一、已经准备好的东西

| 项目 | 位置 | 说明 |
|---|---|---|
| 源码仓库 | 仓库根目录 | 已配好 `.gitignore`(不含 OptiFine、测试工件、构建产物) |
| 构建配置 | `build.gradle` / `gradle.properties`(仓库根目录) | 版本号 `2.2.8+mc26.2`,产物名 `OptiFabric-Reforged-2.2.8+mc26.2.jar` |
| 许可 | `LICENSE.txt` | MPL-2.0(上游 OptiFabric 的许可,移植必须保留) |
| 使用者文档 | `README.md` | 原理、安装、已知问题、排查(已按 26.2 更新) |
| 开发记录 | `docs/DEVELOPMENT.md` | 逐轮排查与可复现的离线校验工具 |
| 移植记录 | `docs/PORT_26.x.md` | 26.x 为什么单独一条线、官方名带来的每一类冲突、26.2 那四处(含**故意不修**的那一处) |
| 更新日志 | `CHANGELOG.md` | 26.x 的 2.2.3 / 2.2.2 / 2.2.1 / 2.2.0 / 2.1.1 / 2.1.0 / 2.0.0 / 1.2.0 八节,后面是 1.21.x / 1.20.6 的历史 |
| 页面文案 | `docs/DESCRIPTION.md` | 简要描述 + 详细描述,中英双语,可直接粘贴(已按 26.2 更新) |
| 模组图标 | `src/main/resources/assets/optifabric/icon.png` | 128×128,已接进 `fabric.mod.json`;想换风格直接替换这个文件 |
| 发布包副本 | `dist/` | 已构建好的 jar + `README.txt`(含 SHA-256),`.gitignore` 排除,本地上传用 |

## 二、构建发布包

26.x 当前的目标是 **26.2**(26.1.2 是同一份源码的另一个产物,见文首),从**仓库根目录**构建:

```powershell
cd I:\mods\OptiFabric
git checkout wip/26.2      # 本机这台 worktree 就停在这一支(见文首的分支表)
.\gradlew build --offline
Copy-Item "build\libs\OptiFabric-Reforged-2.2.8+mc26.2.jar" dist -Force
```

> ⚠️ **要发布的 jar 必须在干净的临时 worktree 里构建**:这个 worktree 里有 9 个未提交的实验文件(见
> [`CONTRIBUTING.md`](../CONTRIBUTING.md) 第二节),脏构建会把那些实验类一起打进 jar —— 已记录的对照是
> **178,300**(干净 2.2.0)对 **196,235**(脏 2.2.0)字节,同名却不同内容。
>
> 这一线**没有 `-Pmc`**:`minecraft_version` 就是 `26.2`、`mod_version_base` 就是 `2.2.3`(都在根目录
> `gradle.properties` 里);26.1.2 那份用的是它自己的版本号,记在 `release\publish.ps1` 的 `$modVersions` 里。
> 要升版别手改,走 `.\release\version.ps1 -Line 26.x -Kind <major|minor|patch>`(只给某一个 MC 版本升版就加
> `-Mc <MC 版本>`,先加 `-DryRun`)—— 它一次把 `gradle.properties`、`release/publish.ps1` 与各文档里那 9 处一起改掉
> (规则见 [`VERSIONING.md`](VERSIONING.md) 第四、五节)。**跑完必须 `git diff` 复核 `CHANGELOG.md`**:
> 历史小节的版本串会被一起改写,见 [`release/MANUAL_RELEASE.md`](../release/MANUAL_RELEASE.md) 里那条警告。

产物在 `build\libs\`(顺手复制到 `dist\`,里面已有一份现成的):

- `OptiFabric-Reforged-2.2.8+mc26.2.jar` ← **上传这个**
- `OptiFabric-Reforged-2.2.8+mc26.2-sources.jar`(可选,一般不用发)

发布之前建议先跑一遍离线验证(一条命令,几分钟):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File test-downloads\verify-26.ps1 -Version 26.2
```

## 三、发到 GitHub

```powershell
cd I:\mods\OptiFabric
git add <你改的文件>       # 别用 -A:这个 worktree 里有 9 个不该提交的实验文件(见 CONTRIBUTING.md 第四节)
git commit -m "OptiFabric Reforged 2.2.8+mc26.2: OptiFine on Fabric for 26.2"
git remote add origin https://github.com/<你的用户名>/<仓库名>.git
git push -u origin wip/26.2    # 推当前分支;发布用的分支是 26.x
```

发 Release —— 26.2 那一份的标签就是版本号本身,旧条目原样保留:

```powershell
git tag v2.2.6                  # 26.2,裸标签
git tag "v2.2.8+mc26.1.2"       # 26.1.2,加后缀(裸标签已被 26.2 占用)
git push origin v2.2.6 "v2.2.8+mc26.1.2"
```

然后在 GitHub 网页上基于该 tag 建 Release(`Target` 选 **`26.x`** 分支 —— 见文首分支表),
把 `OptiFabric-Reforged-2.2.8+mc26.2.jar`
(以及 `-sources.jar`,可选)作为附件上传,并且**只让 26.2 那一条成为 Latest** —— 26.1.2 那一条用
`make_latest=false`(网页上就是"Set as the latest release"不勾)。仓库根目录的发布脚本也能做同样的事:

```powershell
.\release\publish.ps1 -DryRun   # 先看它会创建什么(不联网、不改远端)
```

> Release 文案已经写好了:[`docs/RELEASE_NOTES.md`](RELEASE_NOTES.md) 是 **v2.0.0** 那一版的正文(历史记录);
> 发布脚本正文用的是 [`release/notes/mc26.2.md`](../release/notes/mc26.2.md)。
> 里面的 SHA-256 与 `dist/` 里那个 jar 是核对过的。

**仓库里不该出现的东西**(`.gitignore` 已经排除,提交前可再确认一次):

- OptiFine 的 jar(许可不允许再分发)
- `test-downloads/`(里面有你下载的 OptiFine 安装器、日志与扫描输出)
- `build/`、`dist/`、`reference/`、`build-log.txt`

## 四、发到 CurseForge

1. 用 CurseForge 账号创建一个 **Minecraft → Mods** 项目。
   - **项目名**:上游 OptiFabric 已经在 CurseForge 上有项目了,重名会被审核挡下。用能区分开的名字,例如
     **`OptiFabric Reforged`**(与本模组自己的显示名一致);项目正文里说明它是 Chocohead 版 OptiFabric 的移植(署名必须保留)。
   - 游戏版本:**26.2**(项目级支持范围;26.1.2 之前在范围内,继续留着);上传文件时再按文件指定具体版本。
   - 模组加载器:**Fabric**
   - 许可:**MPL-2.0**(与上游一致,必须一致)
   - 分类建议:Optimization / Miscellaneous
2. **上传文件**:一个 jar **一个文件**,版本名用 `2.2.8+mc26.2`,并在文件设置里把 **26.2** 勾上。
   changelog 用 `release/notes/mc26.2.md`。
3. **项目描述**:`docs/DESCRIPTION.md` 里给了成套文案 —— "简介"栏粘贴**简要描述**(英文在前、中文在后,CF 要求英文排最前),
   项目正文粘贴**详细描述**(有中文和英文两版,CF 支持 Markdown;里面已经带了"支持的版本"表)。
   GitHub 仓库的 About 也可以直接用那句简要描述。
4. **项目图标**:CurseForge 的图标要在网页上单独上传(要求 ≥400×400,且**不能是纯色图**;
   `src/main/resources/assets/optifabric/icon.png` 是给游戏内模组列表用的 128×128,两者可以同图)。
5. **依赖关系设置**:把 **Fabric API** 标为可选依赖(Optional dependency);**不要**把 OptiFine 列为依赖项 ——
   CurseForge 不允许分发 OptiFine,依赖项里也不要指向它的下载。
6. 提交后等审核。

## 五、发布前请再确认这几点

- [ ] jar 里**没有**包含 OptiFine 的任何类或资源(本项目的构建脚本不会打包它,但换过构建配置的话要复查)。
- [ ] jar 里**没有** `mappings/mappings.tiny`:这一线未混淆,本来就没有映射表可打包(有的话说明构建配置被改错了)。
- [ ] `LICENSE.txt` 还在(打包成 `LICENSE.txt_OptiFabric-Reforged`),`fabric.mod.json` 里的 `license` 仍是 `MPL-2.0`,
      README 里保留了对上游项目的署名。
- [ ] 项目描述里写明"需要自行获取 OptiFine 26.2"(目前只有 preview 构建 `HD_U_K2_pre1`)。
- [ ] 项目描述与发布说明里写明 **26.2 上光影不可用**(选了光影包不会有任何效果),不要留下"光影正常"的说法。
- [ ] 用**干净的实例**实测一次:只放 Fabric API + OptiFabric + OptiFine,能进主界面、能进存档;
      Java 必须是 25(26.2 的硬要求,Java 21 会在游戏窗口出现前就失败)。
      - 提示:`<游戏目录>/.optifine/` 是缓存目录,删掉它可强制重新生成,适合用来测"首次安装"的路径。
- [ ] 不要把开发时产生的日志、映射文件、OptiFine 安装器提交进仓库。

## 六、后续版本怎么发

1. 升版别手改,走脚本(先加 `-DryRun` 看结果;整条线一起升版去掉 `-Mc`):
   `.\release\version.ps1 -Line 26.x -Kind patch` 或 `-Line 26.x -Mc 26.2 -Kind patch`
   (见 [`VERSIONING.md`](VERSIONING.md) 第四、五节)。⚠️ 它会全文件替换 `<旧版本>+mc`,**跑完必须
   `git diff` 复核 `CHANGELOG.md`** —— 历史小节被改写是已知坑,见
   [`release/MANUAL_RELEASE.md`](../release/MANUAL_RELEASE.md) 里那条警告;
2. 在 `CHANGELOG.md` 顶部加一节(写清新的版本号,例如 `2.2.8+mc26.2`;`release/notes/mc26.2.md` 同步);
3. 构建(这一线没有 `-Pmc`,目标版本来自 `gradle.properties`;**发布用的 jar 在干净的临时 worktree 里构建**):
   ```powershell
   .\gradlew build --offline
   ```
4. 跑离线校验(`test-downloads\verify-26.ps1 -Version <MC版本>`,两个 MC 版本各一遍);
5. 复制到 `dist\`,用 `.\release\version.ps1 -Line 26.x -RecordDigest`(26.1.2 那份加 `-Mc 26.1.2`)把字节数与
   SHA-256 同步进 `release/notes/mc<MC版本>.md` 与 `release/MANUAL_RELEASE.md`。⚠️ 发布清单里那张表整张是
   **一个段落**,所以 `-RecordDigest` 会把表里**每一行**都改一遍(甚至 26.1.2 那一行也会被写成 26.2 的数字)——
   **跑完必须 `git diff release\MANUAL_RELEASE.md` 把不是这一版的行手工改回去**;
6. `.\release\publish.ps1 -DryRun` 重新生成 `release\tmp\` 草稿(**那里面是上一版的快照,升版后必须重跑**),
   再打 tag(26.2 裸标签 / 26.1.2 加后缀)、发 Release、上传三个平台,并刷新
   `release-upload\<线>-<版本>\`(见 [`release/MANUAL_RELEASE.md`](../release/MANUAL_RELEASE.md))。
