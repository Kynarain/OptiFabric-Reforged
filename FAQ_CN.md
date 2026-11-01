# 常见问题

[🇬🇧 English](./FAQ.md) | [🇨🇳 中文版](./FAQ_CN.md)

这个文件回答的是本仓库**所有发布线**都成立的问题。第 1 问告诉你自己在哪条线、那条线的详细 FAQ 在哪里;第 2 到第 10 问在每条线上答案相同,而且每一条都能在仓库里找到依据。没有实测过的问题,这里会直接写"没有实测",不猜。

**目录**

1. [我在哪条线上?它的详细 FAQ 在哪?](#1-我在哪条线上它的详细-faq-在哪)
2. [我需要哪个 OptiFine jar?](#2-我需要哪个-optifine-jar)
3. [这个模组会下载或打包 OptiFine 吗?](#3-这个模组会下载或打包-optifine-吗)
4. [为什么 Sodium 会被拒载?为什么整个实例都起不来?](#4-为什么-sodium-会被拒载为什么整个实例都起不来)
5. [为什么模组 id 是 `optifabric_reforged` 而不是 `optifabric`?](#5-为什么模组-id-是-optifabric_reforged-而不是-optifabric)
6. [能装在服务器上吗?不装 Fabric API 行吗?](#6-能装在服务器上吗不装-fabric-api-行吗)
7. [升级之后行为没有任何变化。](#7-升级之后行为没有任何变化)
8. [`[OptiFabric]` 的输出在哪?我在 `latest.log` 里找不到。](#8-optifabric-的输出在哪我在-latestlog-里找不到)
9. [能在开发环境里用吗?](#9-能在开发环境里用吗)
10. [怎么报告问题才真的有人看?](#10-怎么报告问题才真的有人看)

---

## 1. 我在哪条线上?它的详细 FAQ 在哪?

每条发布线是独立分支,各有自己的 jar、自己的 OptiFine 构建和自己的文档。

| 分支 | Minecraft | 模组 id | Java | 详细 FAQ |
| --- | --- | --- | --- | --- |
| `26.x` | 26.2、26.1.2 | `optifabric_reforged` | 25 | 该分支的 `README.md` 与 `docs/OPTIFINE_VERSIONS.md` |
| `1.21.x` | 1.21 至 1.21.11(共 10 个版本) | `optifabric_reforged` | 21 或更新 | 该分支的 `docs/FAQ.md` 与 `docs/FAQ_CN.md` |
| `main` | 1.20.6 | `optifabric` | 21 或更新 | 该分支的 `docs/FAQ.md` |
| `wip/1.20.6-reforged` | 1.20.6 | `optifabric_reforged` | 21 或更新 | 该分支的 `docs/FAQ.md` |

1.20.6 的两个产物是两个独立产品,**同时只能装一个**。

## 2. 我需要哪个 OptiFine jar?

就是你那条线 README 兼容性表里对应 Minecraft 版本的那一个 —— 同一份清单也写在 `docs/OPTIFINE_VERSIONS.md` 里,模组自身则记录在 `OptifineSupport` 中。

版本必须**完全一致**:jar 里自带那一个版本的映射表,模组会从 `net/optifine/Config.class` 里读 `MC_VERSION` 来校验。OptiFine 只从 [optifine.net](https://optifine.net/downloads) 取 —— 那也是本模组唯一指向的来源。

**安装器** jar(里面带 `patch/` 的)和已经解包好的 jar 都接受,模组靠有没有 `patch/` 目录来区分。同一个版本放两个 jar 是 `DUPLICATED` 错误而不是"二选一":一个实例只跑一个 OptiFine。

## 3. 这个模组会下载或打包 OptiFine 吗?

**两样都不会,发到商店的每个产物都是如此。** 打开 jar 就能验证:没有 `net/optifine/*` 类,没有内嵌 jar,也没有下载器类。唯一与网络有关的字符串,是提示文案和文档里的 `optifine.net` 链接。

GitHub 上发布的 **`-full`** 便利构建里确实带一个下载器,它把 OptiFine 从 optifine.net 取到**你自己的机器**上 —— 仍然不打包任何东西。详见你那条线上的 `docs/DOWNLOAD_INTEGRITY.md`。

## 4. 为什么 Sodium 会被拒载?为什么整个实例都起不来?

因为 `breaks` 是**闸门**,不是警告。在 Fabric Loader 0.19.5 上实测:`conflicts` 条目不产生任何约束(只打印 `Warnings were found!`),而 `breaks` 命中已装的模组时,解析器会给出 `NEG_HARD_DEP`,加载器**直接拒绝这个实例**。

本仓库所有产物声明的是同样五个 id —— `no_fog`、`thallium`、`xradiation`、`ryoamiclights`、`sodium` —— 所以你看到的就是这一条。

Sodium 被列进去是**测出来的,不是猜的**:把所有可修的空隙补齐后,Sodium 的 mixin 全部应用、客户端也进了世界 —— 画面是**全黑**的。两个地形渲染器不能共用一个管线,所以这里选择"拒载"而不是"警告":警告只会留下一个能启动、但永远画不出东西的游戏。Iris、Indium、Chloride、Sodium Extra 不必单独列,因为它们都依赖 Sodium。

## 5. 为什么模组 id 是 `optifabric_reforged` 而不是 `optifabric`?

因为有些模组写的是 `"breaks": {"optifabric": "*"}`,而 Fabric Loader 是按**id** 匹配的 —— 改显示名不够。1.20.6 的两个产物以两个独立 id 存在,同时只能装一个。

## 6. 能装在服务器上吗?不装 Fabric API 行吗?

仅客户端(`"environment": "client"`),而且**不装 Fabric API 也能启动**:声明的依赖只有 `fabricloader` 和 `minecraft`。Fabric API 是测过、也是大多数整合包都会带的,但它缺失不算错误路径。

## 7. 升级之后行为没有任何变化。

删掉 `<游戏目录>/.optifine/`。那个目录存放上一次运行打好的字节码和重映射后的 OptiFine jar;模组会重建它,而缓存格式号一变,它自己就失效了。

## 8. `[OptiFabric]` 的输出在哪?我在 `latest.log` 里找不到。

它在**启动器控制台**里,通常不在 `logs/latest.log`。在控制台里筛 `[OptiFabric]`,能看到准备了多少个类、Loader 接手了多少个。另一个位置是崩溃报告:里面会多出一段 `OptiFabric`,写明 OptiFine 版本、jar 状态和映射后 jar 的路径。

## 9. 能在开发环境里用吗?

不能。每条线上的 `gradlew runClient` 都是**故意拒绝**的:开发环境跑在 `named` 命名空间,而本模组是给**混淆过的客户端 jar** 打补丁、并按那个命名空间交付补丁的。要支持它,需要**第二段重映射**。

## 10. 怎么报告问题才真的有人看?

牵涉到别的模组就开 **compatibility issue**,不牵涉就开 **bug report**;两个表单要的核心证据是一样的,顺序如下:

1. **OptiFine jar 名**和 **OptiFabric jar 名**(版本就在文件名里)。
2. **启动器控制台输出**,筛 `[OptiFabric]` —— 不是 `latest.log`,那里没有。
3. **崩溃报告**(如果有):它的 `OptiFabric` 段会写明 jar 状态。启动阶段的失败,栈在崩溃报告里,`latest.log` 往往在异常前就截断了。
4. **模组列表**,以及同一套配置**不装 OptiFabric** 时是否同样失败。在纯 Fabric 上也失败的模组不是本模组的问题,这个对照是最快的判断方式。
5. 渲染类问题:关掉光影、换一个光影包,是否同样出现。
