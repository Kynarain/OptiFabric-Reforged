# 启动失败排查(尤其 OptiFabric + Lithium)

这份文档只讲**有据可查**的失败方式,以及**在那台机器上要收集什么**。它不替代日志:每条判断都给"该在日志里看到什么"。

## 零、先核对两个 jar 的 mod id(2026-10-13 在本机读 jar 核实)

不需要在出问题的机器上做任何实验就能定性:失败与否只取决于**装的那个 OptiFabric jar 的 mod id**。下面是直接读各 jar 内
`fabric.mod.json` 得到的事实(不是推测):

| jar | 它写的 mod id | 它写没写 `breaks: optifabric` |
|---|---|---|
| `lithium-fabric-0.15.3+mc1.21.4.jar`(**stock**) | `lithium` | **写了** ⇒ `"breaks": {"optifabric": "*"}` |
| `lithium-fabric-mc1.21.4-patched.jar`(**打过补丁**) | `lithium` | **没有了** —— 补丁把整段 `breaks` 删掉了 |
| `OptiLithium-1.0.0+mc1.21.4.jar` | `optilithium` | 没写(它只写 `conflicts: sodium`) |
| `OptiFabric-2.2.14+mc1.21.4.jar`(**现行产物**) | **`optifabric_reforged`** | —— |
| `OptiFabric-1.1.2+mc1.21.4.jar`(**旧**) | **`optifabric`** | —— |
| `OptiFabric-1.1.7+mc1.20.6.jar`(1.20.6 主线) | `optifabric` | —— |

**结论**:

* 装 **`OptiFabric-2.2.14+mc1.21.4.jar` + stock Lithium ⇒ 能启动**。stock Lithium 的 `breaks: optifabric` 匹配的是 id `optifabric`,
  而现行产物的 id 是 `optifabric_reforged`,**匹配不上** —— 这是本线刻意改的名,目的就是让它与这类第三方声明脱钩。
* 装 **旧的 `+mc1.21.4`(id `optifabric`)** 或**上游 OptiFabric** + stock Lithium ⇒ Loader 在**模组解析阶段硬拒载**,
  **不产生 crash-report、也没有 Java 异常** —— 这就是"起不来又看不到原因"的样子。

## 一、这一类失败在日志里长什么样:Loader 在**模组解析阶段**硬拒载

**症状**:游戏**根本起不来**,没有窗口或窗口一闪而过;**没有** `crash-reports\crash-*.txt`;**没有** Java 异常栈 —— 因为失败发生在加载模组之前/之中。

**原因**:第三方模组的元数据里写了 `breaks`,而 Fabric Loader **会执行** `breaks`。本仓库已记录的确切例子:

> 本产物的 mod id 是 `optifabric`,而 **Lithium 0.12.5 自己的元数据写着 `breaks: { "optifabric": "*" }`**,加载器**会执行 `breaks`** ⇒ **模组解析阶段就硬拒载**(`NEG_HARD_DEP lithium 0.…`)
> ——`OptiFabric-1206-main/CHANGELOG.md` 里那一段(同一结论在 `docs/REFORGED_BUILD.md` 的表里也有)

**因此先分清"装在 mods 里的 jar 的 mod id 是什么"**:

| jar | 里面的 mod id | stock Lithium 的 `breaks: optifabric` 会不会命中 |
|---|---|---|
| `OptiFabric-<版本>+mc<版本>.jar`(1.21.x 线,如 `OptiFabric-2.2.14+mc1.21.4.jar`) | `optifabric_reforged` | **不命中** ✓ |
| `OptiFabric-Reforged-<版本>-reforged+mc1.20.6.jar`(1.20.6 替代产物) | `optifabric_reforged` | **不命中** ✓ |
| `OptiFabric-<版本>+mc1.20.6.jar`(1.20.6 主线) | `optifabric` | **命中** ⇒ 硬拒载 ✗ |
| 上游 OptiFabric 的任意 jar | `optifabric` | **命中** ⇒ 硬拒载 ✗ |

**怎么确认**:在出问题那台机器的 `logs\latest.log` 里搜这几个词 ——
`NEG_HARD_DEP`、`breaks`、`Mod resolution`、`requires`。命中 `NEG_HARD_DEP` 就是这一类。

**怎么修**(按推荐顺序):

1. **让 id 不叫 `optifabric`**:用 1.21.x 线的产物(`OptiFabric-2.2.14+mc1.21.4.jar`,id 为 `optifabric_reforged`)。这是本仓库刻意的设计:改名之后就**不再匹配**任何 `breaks: optifabric` 的声明。
2. **用配套补丁而不是 stock Lithium**:OptiLithium 路线(见下)。2026-09-22 在本机跑通的 1.21.4 组合就是这条:

   ```
   Loading Minecraft 1.21.4 with Fabric Loader 0.19.5
   Loading 6 mods:
   - fabricloader 0.19.5
   - java 21
   - lithium 0.15.3+mc1.21.4
   - minecraft 1.21.4
   - optilithium 1.0.0+mc1.21.4
   - OptiFine_1.21.4_HD_U_J3.jar
   ```

   注意:这一组里**没有独立的 optifabric**,而是 `optilithium` 承担补丁角色。
   另一条已验证的路线是"**已打过补丁的 Lithium** + OptiFabric"(2026-09-23 的 `1.21.4-sweep`:`lithium-fabric-mc1.21.4-patched.jar` + `OptiFabric-1.1.2+mc1.21.4` + OptiFine J3,连续玩数小时)。
3. **临时对照**:把 Lithium 移出 `mods\` 再启动。若这样就能起,则锁定就是这条 `breaks`;若**同样起不来**,问题不在 Lithium,转而看第二节。

## 二、1.21.4 上"必须对上"的几件事

| 项 | 正确值 | 依据 |
|---|---|---|
| Minecraft | 1.21.4 | —— |
| 构建用的 jar | `OptiFabric-2.2.14+mc1.21.4.jar`(**不要**同时放 `-full` 或旧版本的另一个 `+mc1.21.4` jar,两者会同时加载) | `dist\` 里的产物名 |
| OptiFine | **`OptiFine_1.21.4_HD_U_J3.jar`**(1.21.4 的正式构建;**不是** J4_pre2 预览) | `OptifineSupport.java` 的 1.21.4 行 |
| Java | **21**(本线 `targetJavaVersion = 21`;本机跑通那次是 `Java: 21.0.9`) | `build.gradle` |
| Fabric Loader | 0.19.5(本机跑通那组) | 该次日志 |

## 三、在那台机器上要收集的四样

1. `logs\latest.log`(**要失败那一次的**,不是下一次覆盖后的);
2. 启动器自己的原始输出(如 PCL 的 `RawOutput.log`)—— 因为 **`[OptiFabric] …` 这些补丁输出不写进 `latest.log`**,只在原始 stdout 里(本机对照:同一份运行 `latest.log` 里 0 行 `[OptiFabric]`,原始输出里 121 行);
3. `crash-reports\crash-*.txt`(如果有);
4. `mods\` 的完整文件名清单 + `hs_err_pid*.log`(如果存在)。

有了 1+2 基本就能定性:第 1 节那类失败**只在原始输出里**留下一行 `NEG_HARD_DEP`。

## 四、这份文档不能替代的

* 我**没有**那台机器的访问权,上面第一节的因果是**根据本仓库已记录的实测**给出的,**不是**在那台机器上复现过的结论。
* 如果收集到的日志里**没有** `NEG_HARD_DEP`/`breaks`,那就不是这一类,请把 1–4 发我,我按日志继续。
