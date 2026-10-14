# Every OptiFine build this mod supports

One page, all four lines. Nothing here is a recommendation between them: each line is a separate download for a
separate set of Minecraft versions, and a jar from one line will not work on another.

## Where OptiFine comes from

Only from <https://optifine.net/downloads>. **No OptiFine file is bundled with this mod and the mod never runs the
OptiFine installer.** Put the one jar from the table below next to OptiFabric in your instance's `mods` folder.

## The table

| Minecraft | OptiFabric line | OptiFabric artifact | OptiFine build to use | Build kind | SHA-256 | Bytes |
|---|---|---|---|---|---|---|
| 1.21 | 1.21.x | ``2.2.14+mc1.21`` | `preview_OptiFine_1.21_HD_U_J1_pre9.jar` | preview | `b26b22478592ca85c3ea829784c3b7bf0d98d882d18a937af969dcc14372d37f` | 7355487 |
| 1.21.1 | 1.21.x | ``2.2.14+mc1.21.1`` | `OptiFine_1.21.1_HD_U_J1.jar` | final | `db6d2d14db0009bdea2d8f848e5cb55aac52b555e8e50506dbf7876755b79082` | 7322249 |
| 1.21.3 | 1.21.x | ``2.2.14+mc1.21.3`` | `OptiFine_1.21.3_HD_U_J2.jar` | final | `3916aadff6fd8814f93d5ca0fddf4921f67772737b1208099b1debaeb671667b` | 7304461 |
| 1.21.4 | 1.21.x | ``2.2.14+mc1.21.4`` | `OptiFine_1.21.4_HD_U_J3.jar` | final | `db8a1c508ecb3f89ae8f7b69ae25c78cbdb3d31131c4f789ffae50e2047be1d3` | 7423659 |
| 1.21.6 | 1.21.x | ``2.2.14+mc1.21.6`` | `preview_OptiFine_1.21.6_HD_U_J6_pre3.jar` | preview | `f73e5b90a89e523873b5b876cbd7601f08eface74879e8dd528bd25328ef5c7a` | 7518992 |
| 1.21.7 | 1.21.x | ``2.2.14+mc1.21.7`` | `preview_OptiFine_1.21.7_HD_U_J6_pre7.jar` | preview | `4e7340eb61b39f100da639e62490e50432ba34380f96bbea822336dbef157e6c` | 7587441 |
| 1.21.8 | 1.21.x | ``2.2.14+mc1.21.8`` | `preview_OptiFine_1.21.8_HD_U_J6_pre16.jar` | preview | `ffe496b39065fedc897dc179965f9a21f01ab1f816be973e496d94c9617d462c` | 7704702 |
| 1.21.9 | 1.21.x | ``2.2.14+mc1.21.9`` | `preview_OptiFine_1.21.9_HD_U_J7_pre2.jar` | preview | `dc7d1182360c2b23b0c96797103ef1946fae6d1bda78ec2640117df3f368f4c4` | 7664893 |
| 1.21.10 | 1.21.x | ``2.2.14+mc1.21.10`` | `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` | preview | `bf845cfc6a387b0cc879512caefa86039fd5ca9e37aa6828a1967577ae96b6d7` | 7805444 |
| 1.21.11 | 1.21.x | ``2.2.14+mc1.21.11`` | `OptiFine_1.21.11_HD_U_J9.jar` | final (two known contents) | `63a60c48b3370920e96d4c32570d7154d17b3a86654c4f1d1df418be668d3903`<br>`f97b5d06df53760e21c5a91733ac55af5a250caca2c29945c855aef7ed2d0ec8` | 8045116<br>8045105 |
| 26.2 | 26.x | ``2.2.9+mc26.2`` | `preview_OptiFine_26.2_HD_U_K2_pre1.jar` | preview | `db05b25f8aa5ac688a77354680ef939d4f3135350e64e7ccfe5996cfaf925927` | 7783636 |
| 26.1.2 | 26.x | ``2.2.9+mc26.1.2`` | `preview_OptiFine_26.1.2_HD_U_K1_pre2.jar` | preview | `f8eb9026e4da2444e18d5601d3dede2bd19cf514d02095ffcdb0e101687c2172` | 7797229 |
| 1 | 1.20.6 (reforged) | ``` | `.` | final | (not recorded) | (unknown) |
| . | 1.20.6 (reforged) | `1` | `2` | final | (not recorded) | (unknown) |
| r | 1.20.6 (reforged) | `p` | `e` | final | (not recorded) | (unknown) |
| 1 | 1.20.6 | ``` | `.` | final | (not recorded) | (unknown) |
| . | 1.20.6 | `1` | `2` | final | (not recorded) | (unknown) |
| r | 1.20.6 | `p` | `e` | final | (not recorded) | (unknown) |

## What each line needs

| Line | Minecraft versions | Mod id inside the jar | Java | Notes |
|---|---|---|---|---|
| 1.21.x | 1.21, 1.21.1, 1.21.3, 1.21.4, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, 1.21.11 | `optifabric_reforged` | Java 21 or newer | ten Minecraft releases, all of which have an OptiFine build |
| 26.x | 26.2, 26.1.2 | `optifabric_reforged` | Java 25 | preview builds only; OptiFine has never shipped a final build for 26.2 or 26.1.2 |
| 1.20.6 (reforged) | 1, ., r | `optifabric_reforged` | Java 21 | preview builds only |
| 1.20.6 | 1, ., r | `optifabric` | Java 21 | preview builds only; this line keeps the original mod id |

## How to check what you downloaded

The **SHA-256** column is the whole hash of the file, and **Bytes** is its exact size. Compare before you put it in
`mods`. Where a row lists **two** hashes, OptiFine re-uploaded that build under the same file name with different
contents - this mod accepts either of them, and refuses a file that matches neither (printing both sides so you can
tell a re-upload from a substitution).

A build name is exact: `preview_OptiFine_1.21.10_HD_U_J7_pre11.jar` for Minecraft 1.21.10 is not interchangeable with
the 1.21.9 one, and a jar built for one Minecraft version carries that version's mapping table only.

## 中文说明

**去哪下**:只有官方来源 <https://optifine.net/downloads>。本模组**不附带**任何 OptiFine 文件,也不替你安装它 —— 把表里那一个 jar 和 OptiFabric 一起放进实例的 `mods/` 即可,**不要**运行 OptiFine 安装器。

**怎么对**:表里的 `SHA-256` 是你该拿到的文件的完整哈希,`Bytes` 是精确大小。对不上就**别用** —— 重新下。

**为什么有一行写两个哈希**:OptiFine 会用**同一个文件名重传内容不同**的构建(本表里 `1.21.11` 与 `1.20.6 pre18` 都是这样)。本模组**两个都接受**;两个都对不上时会**拒绝**并把你实际拿到的哈希打出来。

**预览版**:有些 Minecraft 版本 OptiFine **只出过预览版**(名字带 `preview_`),那就只能用预览版 —— 这不是本模组的选择,是 OptiFine 的发布状态。

**版本必须严格对上**:一个 OptiFabric jar 只对应**一个** Minecraft 版本(它内部打包着该版本的 `official → intermediary` 映射表),OptiFine 也必须是与该 Minecraft 版本对应的那一个。装错版本不会"降级运行",而是打补丁时出错。

**四条线不能混用**:`1.21.x`、`26.x`、`1.20.6`(两个产物 id 不同)各自在自己的分支上,各自的 jar 只认自己那一行的 OptiFine。
