# 运行时证据

## 1.21.x 线 · 1.21.1 · 一次成功的启动与进世界

**来源**:作者 2026-10-09 粘贴的启动日志(我据此转写,**没有**自己去读日志文件,所以下面每一条都只陈述日志里写了什么)。产物为
本线 1.21.x 的普通 jar(id `optifabric_reforged`),OptiFine 为 `OptiFine_1.21.1_HD_U_J1` —— 支持表点名的构建,且它的
SHA-256 就在本线 `OptifineHashes` 的记录里。装载组合含 `c2me`、`lithium`、`entityculling`,即这条线存在的理由那一组。

**日志里能直接读到的结论**

| 观察 | 日志原文 |
|---|---|
| 补丁数量与失败数 | `Prepared 425 patched classes (0 skipped, 0 failed)` |
| 交给 Fabric Loader 的数量与耗时 | `Ready: 424 patched classes taken over by Fabric Loader in 0.7 seconds` |
| 帧同步与槽位对齐在工作 | `Realigned the locals of net/minecraft/class_761.method_22710…: 41 of OptiFine's 64 table entries are the game's own (of 47)` |
| `@ModifyVariable` 判别子在按预期收窄 | `Masked java.util.Iterator local(s) 53, 54, 26, 27 … so its implicit @ModifyVariable discriminator counts only slot 29 (of 5 same-typed locals)` |
| 注入点重建在工作 | `Re-created the injection point net/minecraft/class_2680.method_26213()I in …`(多处) |
| 缺调用点会补齐 | `Put the call net/minecraft/class_6491.method_24895(…) back in …`(多处) |
| 渲染器占位在工作 | `Registered OptifineRendererPlaceholder$Renderer as Fabric's rendering plug-in` |
| 跑到哪一步 | `Starting integrated minecraft server version 1.21.1` → `Kynarain logged in with entity id 41` → 反复 `Saving and pausing game...`;**全程无 `ERROR`,无崩溃** |

**这次运行同时说明**:1.21.1 的补丁路径走到"进世界并持续运行",不是只到标题界面。这是本线**第一次**有这类端到端记录。

## 这次运行里两处需要跟进的行

1. **四条** `No common supertype for java/lang/Object and <X> … the recomputed frame will say java/lang/Object`
   (`java/util/Iterator`、`class_4597$class_4598`、`java/util/concurrent/ForkJoinPool`、`class_5250`)。
   把合并结果写成 `Object` 对"Object 与某类型"而言是**安全的上界**,所以它本身不致命;但它落在
   `FrameComputingWriter.getCommonSuperClass` 这条路径上,而**该路径至今没有任何测试**(审查报告 §十八.3.2 点名)。
   待办:为它构造一个"两个无关引用类型合并"的用例,固定住"回落到 Object"这一行为。
2. **一行** `Cannot resolve the synthetic field net/minecraft/class_776$1.$SwitchMap$net$minecraft$world$level$block$RenderShape[I:
   no unique field of that type is left in the game's class`。紧邻的上一行说明这个名字与混淆名**都被保留**、并且由同一个
   类初始化器写入,所以这行很可能是**另一条解析路径**的失败。它是整份日志里唯一读起来像"没做成"的一句。
   待办:确认它对应哪一次查询、失败后调用方拿到什么(若只是"没有唯一候选就跳过",要把它写成注释而不是留在日志里让人误解)。

## 与本记录无关、但日志里很吵的几类

`Unknown resource pack type: …`, `InaccessibleObjectException: … jdk.internal.misc.VM.maxDirectMemory()`,
`Shader rendertype_entity_translucent_emissive could not find sampler named Sampler2` —— 这三类都是 **OptiFine 自己**
面对 Fabric 的资源包类型与 Java 22 模块限制发出的抱怨,与本补丁无关,不要读成回归。

## 2. 同一天两次"无异常退出"(2026-10-10 19:57 / 19:58,均在 1.21.1)"无异常退出"(2026-10-10 19:57 / 19:58,均在 1.21.1)

**来源**:作者导出的 PCL 错误报告 zip(`错误报告-2026-10-10_19.59.33.zip`,我复制到 `I:\mods\OptiFabric-workspace\crash-2026-10-10\` 后解包)。包里是
`latest.log`、`游戏崩溃前的输出.txt`、`PCL 启动器日志.txt`(5870 行)、`启动脚本.bat`、版本 json —— **没有** `crash-*.txt`,**没有** `hs_err_pid*.log`。

**日志能直接读到的**

| 观察 | 原文 |
|---|---|
| 两次都是异常终止 | 启动器:`[31484] Minecraft 已退出,返回值:-1`、`[58432] Minecraft 已退出,返回值:-1` |
| 分析器找不到原因 | `开始进行 Minecraft 日志堆栈分析,发现 0 个报错项` → `未找到可能的原因` |
| 第二次的死前状态 | `19:58:03 Kynarain logged in with entity id 41` → 反复存档 → `19:58:46` 退出(中间约 43 秒,日志无异常) |
| 崩溃报告的系统信息里出现图形驱动 | 13:01 与 13:07 那两次的 dump 里有 `atio6axx.dll:AMD OpenGL Driver:25.11.250605_36cd845:Advanced Micro Devices, Inc` |

**据此能说与不能说**

* 能说:这两次退出**不是**补丁抛出的 Java 异常 —— 日志零异常、退出码 `-1`(异常终止),而包里可见的崩溃报告系统信息指向图形驱动层。
* 不能因此定论驱动是唯一原因:要区分"原生崩"与"Java 层崩",必须看 `hs_err_pid*.log`(有则是 JVM 级)或 `crash-*.txt`(有 Java 栈则是 Java 级)。**这两份都不在这个 zip 里**。
* 时间上值得注意:两次都发生在**进世界约一分钟后**、且当时**没有加载 shaderpack**(`No shaderpack loaded`)。

**要继续查,需要补的两样**

1. `C:\Users\<用户>\AppData\Roaming\.minecraft\versions\1.21.1-Fabric 0.19.5\crash-reports\crash-*.txt` —— 启动器日志确认这些文件存在(13:01/13:07/14:36 等次都写了报告)。
2. 实例目录或游戏目录下的 `hs_err_pid*.log`(如果存在)—— 有它就是 JVM 级崩溃,里面会直接写明 `SIGSEGV`/`EXCEPTION_ACCESS_VIOLATION` 与出错的**本地库**。

若确实是驱动层,可做的对照:**关掉 c2me/lithium/entityculling 只留 OptiFabric + OptiFine** 跑同一个世界;以及查 Windows 事件查看器里有没有 `Display driver amdkmdap stopped responding`(TDR)。
