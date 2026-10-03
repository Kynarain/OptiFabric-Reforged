# OptiFabric + OptiFine compatibility matrix — Minecraft 1.21.1 / Fabric

Generated 2026-10-03T04:40:46.188Z from pool.json, plan.json, deps\*.json and results.json by `tools/build-report.mjs`.

## Summary

Pool: the top **100** Fabric mods for Minecraft 1.21.1 by downloads (Modrinth search: 19342 projects match `project_type:mod` + `categories:fabric` + `versions:1.21.1`).

| Verdict | Count | Meaning |
| --- | --- | --- |
| OK | 187 | reached the title screen (`Sound engine started`) |
| FAIL | 34 | crashed, or never reached the title screen |
| DEP | 16 | could not be tested: a required dependency is unavailable for 1.21.1, or its metadata demands something else |
| N/A | 19 | no 1.21.1 Fabric file, or a server-only/library mod |
| HARNESS-ERROR | 1 | the run itself failed (launcher never started, no fresh log); says nothing about the mod |
| **not run** | 50 | in the pool, not yet launched — no verdict is claimed |

Mods actually launched this session: **238**.

Work queue for the parallel phase: **111 rows** (0 hand-picked Carpet, 11 from the ranked top-100 remainder, 100 extended-pool ranks past 100, 0 CurseForge). Of the 50 rows in this table with no verdict, the reason is stated per row.

Prerequisite scan: of 18 failures and harness errors examined, 0 carried a missing-prerequisite signature; 0 were rescuable by adding the named prerequisite, and 0 remain DEP for want of a 1.21.1 Fabric release for it. A row is only counted against OptiFabric when its prerequisites were satisfiable (see `prerequisites.json`).

Failures split by what the no-OptiFine control showed: 27 incompatible with OptiFine, 4 unexplained, 4 broken on plain Fabric 1.21.1.

Modpacks (separate census, no runs): **84 of the top 100** 1.21.1 Modrinth modpacks ship a Fabric file (100 have a 1.21.1 version on some loader). Those are a later phase and are not rows in this table.

> **Read the limits before using a row.** `OK` means the mod did not stop the client reaching the title screen
> under OptiFabric + OptiFine. It does **not** mean the mod's features work under OptiFine: no world was
> opened and nothing was rendered. See the Limits section of README.md.

## Failures grouped by what actually broke

A FAIL is only evidence about OptiFabric if the same mod starts without OptiFabric and OptiFine. The
no-OptiFine control run decides that, and it was run for every failure. Each group below is that split.

| What the control run showed | Count | Meaning |
| --- | --- | --- |
| incompatible with OptiFine (mod itself fine on plain Fabric) | 27 | starts on plain Fabric without OptiFabric/OptiFine, so the failure belongs to the OptiFine interaction |
| unexplained (control run itself failed) | 4 | the control run itself failed to produce a result, so ownership is not asserted |
| broken on plain Fabric 1.21.1 (OptiFabric not implicated) | 4 | fails without OptiFabric and without OptiFine too, so OptiFabric is not implicated |

### By cause, with the evidence for each

#### `mixin-transform` — 27 mods

**sodium** vmc1.21.1-0.8.13-fabric — *renderer overhaul (replaces the chunk renderer)*

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: `net.minecraft.class_638` (net.minecraft.client.world.ClientWorld) **in OptiFine's rewritten set**
- Reading: OptiFabric's own fabric.mod.json declares this mod under `conflicts`, so the combination is declared incompatible in advance and cannot be an OptiFabric defect; the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; the class(es) named in the evidence (net.minecraft.class_638) are in OptiFine's rewritten set, so the mixin is matching against bytecode OptiFine changed; this mod is a renderer overhaul (replaces the chunk renderer), for which OptiFine incompatibility is the documented expectation.

**iris** v1.8.8+1.21.1-fabric — *renderer overhaul (replaces the shader pipeline)*

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"]`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: `net.minecraft.class_761` (net.minecraft.client.render.WorldRenderer) **in OptiFine's rewritten set**
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; the class(es) named in the evidence (net.minecraft.class_761) are in OptiFine's rewritten set, so the mixin is matching against bytecode OptiFine changed; this mod is a renderer overhaul (replaces the shader pipeline), for which OptiFine incompatibility is the documented expectation; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**immediatelyfast** v1.6.14+1.21.1-fabric — *renderer overhaul (rewrites immediate-mode rendering)*

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_1921 failed [in thread "main"]`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: `net.minecraft.class_1921` (net.minecraft.client.render.RenderLayer) **in OptiFine's rewritten set**
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; the class(es) named in the evidence (net.minecraft.class_1921) are in OptiFine's rewritten set, so the mixin is matching against bytecode OptiFine changed; this mod is a renderer overhaul (rewrites immediate-mode rendering), for which OptiFine incompatibility is the documented expectation; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**sodium-extra** vmc1.21.1-0.9.4+fabric — *renderer overhaul (replaces the chunk renderer)*

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: `net.minecraft.class_638` (net.minecraft.client.world.ClientWorld) **in OptiFine's rewritten set**
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; the class(es) named in the evidence (net.minecraft.class_638) are in OptiFine's rewritten set, so the mixin is matching against bytecode OptiFine changed; this mod is a renderer overhaul (replaces the chunk renderer), for which OptiFine incompatibility is the documented expectation; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**entity-model-features** v3.3.9-fabric-1.21 — *renderer overhaul (rewrites entity models/rendering)*

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: `net.minecraft.class_756` (net.minecraft.client.render.item.BuiltinModelItemRenderer) **in OptiFine's rewritten set**
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; the class(es) named in the evidence (net.minecraft.class_756) are in OptiFine's rewritten set, so the mixin is matching against bytecode OptiFine changed; this mod is a renderer overhaul (rewrites entity models/rendering), for which OptiFine incompatibility is the documented expectation; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**reeses-sodium-options** vmc1.21.1-2.2.4+fabric — *renderer overhaul (Sodium's options screen)*

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: `net.minecraft.class_638` (net.minecraft.client.world.ClientWorld) **in OptiFine's rewritten set**
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; the class(es) named in the evidence (net.minecraft.class_638) are in OptiFine's rewritten set, so the mixin is matching against bytecode OptiFine changed; this mod is a renderer overhaul (Sodium's options screen), for which OptiFine incompatibility is the documented expectation; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**modernfix** v5.25.1+mc1.21.1

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: `net.minecraft.class_1043` (net.minecraft.client.texture.NativeImageBackedTexture) **in OptiFine's rewritten set**
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; the class(es) named in the evidence (net.minecraft.class_1043) are in OptiFine's rewritten set, so the mixin is matching against bytecode OptiFine changed; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**moreculling** v1.0.10 — *renderer overhaul (rewrites chunk/render culling)*

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_918 failed [in thread "main"]`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: `net.minecraft.class_918` (net.minecraft.client.render.item.ItemRenderer) **in OptiFine's rewritten set**
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; the class(es) named in the evidence (net.minecraft.class_918) are in OptiFine's rewritten set, so the mixin is matching against bytecode OptiFine changed; this mod is a renderer overhaul (rewrites chunk/render culling), for which OptiFine incompatibility is the documented expectation; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**no-chat-reports** vFabric-1.21.1-v2.9.1

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; no class could be extracted from the evidence line; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**indium** v1.0.35+mc1.21 — *renderer overhaul (Sodium's Fabric rendering API backend)*

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; no class could be extracted from the evidence line; this mod is a renderer overhaul (Sodium's Fabric rendering API backend), for which OptiFine incompatibility is the documented expectation; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**c2me-fabric** v0.4.0-alpha.0.29+1.21.1

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"]`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: `net.minecraft.class_761` (net.minecraft.client.render.WorldRenderer) **in OptiFine's rewritten set**
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; the class(es) named in the evidence (net.minecraft.class_761) are in OptiFine's rewritten set, so the mixin is matching against bytecode OptiFine changed; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**supplementaries** v1.21.1-3.9.9

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"]`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: `net.minecraft.class_761` (net.minecraft.client.render.WorldRenderer) **in OptiFine's rewritten set**
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; the class(es) named in the evidence (net.minecraft.class_761) are in OptiFine's rewritten set, so the mixin is matching against bytecode OptiFine changed; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**fallingleaves** v1.17.1+1.21.1 — *renderer overhaul (rewrites particle/leaf rendering)*

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_702 failed [in thread "main"]`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: `net.minecraft.class_702` (net.minecraft.client.particle.ParticleManager) **in OptiFine's rewritten set**
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; the class(es) named in the evidence (net.minecraft.class_702) are in OptiFine's rewritten set, so the mixin is matching against bytecode OptiFine changed; this mod is a renderer overhaul (rewrites particle/leaf rendering), for which OptiFine incompatibility is the documented expectation; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**carry-on** v2.2.6

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"]`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: `net.minecraft.class_761` (net.minecraft.client.render.WorldRenderer) **in OptiFine's rewritten set**
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; the class(es) named in the evidence (net.minecraft.class_761) are in OptiFine's rewritten set, so the mixin is matching against bytecode OptiFine changed; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**puzzle** v2.3.0+1.21.1-fabric

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**sodium-shadowy-path-blocks** v4.1.0-fabric — *renderer overhaul (replaces the chunk renderer)*

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; no class could be extracted from the evidence line; this mod is a renderer overhaul (replaces the chunk renderer), for which OptiFine incompatibility is the documented expectation; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**freecam** v1.3.0+mc1.21.1

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"]`
- Implicated layer: unexplained (control run itself failed)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): HARNESS-ERROR — the control run wrote no verdict.json
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**bobby** v5.2.4.1+mc1.21

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"]`
- Implicated layer: unexplained (control run itself failed)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): HARNESS-ERROR — mods\ differs from the recorded mirror: in the mirror but missing now: OptiFabric-2.0.0+mc
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**replaymod** v1.21-2.6.27

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_309 failed [in thread "main"]`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**chloride** v1.8.1-FABRIC-1.21.1

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**cut-through** vv21.1.0-1.21.1-Fabric

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_757 failed [in thread "main"]`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**polytone** v1.21-4.5.3

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**particle-core** v0.3.3+1.21

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_702 failed [in thread "main"]`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**shatterbyte-lib** v0.6.2

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"]`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**tooltipfix** v1.1.1-1.20

- Verbatim evidence: `Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_332 failed [in thread "main"]`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**deeperdarker** v1.3.3-plus-b-fabric+1.21

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**cf--the-twilight-forest** v—

- Verbatim evidence: `Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered`
- Implicated layer: broken on plain Fabric 1.21.1 (OptiFabric not implicated)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): FAIL — [12:04:13] [Render thread/INFO]: [STDOUT]: [OptiFabric] OptiFine is not installed - showin
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

#### `linkage` — 3 mods

**open-parties-and-claims** vfabric-1.21.1-0.31.6

- Verbatim evidence: `[10:48:13] [main/WARN]: Error loading class: com/electronwill/nightconfig/core/io/IoUtils (java.lang.ClassNotFoundException: com/electronwill/nightconfig/core/io/IoUtils)`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**modern-ui** v3.13.0.1

- Verbatim evidence: `[11:16:35] [Render thread/WARN]: [OptiFine] (Reflector) java.lang.ClassNotFoundException: jdk.internal.misc.SharedSecrets`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**cf--better-compatibility-checker** v—

- Verbatim evidence: `Caused by: java.lang.ClassNotFoundException: net.neoforged.fml.config.IConfigSpec`
- Implicated layer: broken on plain Fabric 1.21.1 (OptiFabric not implicated)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): FAIL — Caused by: java.lang.ClassNotFoundException: net.neoforged.fml.config.IConfigSpec
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

#### `optifine-not-available` — 2 mods

**rei** v16.0.799+fabric

- Verbatim evidence: `C131: [08:42:11] [Render thread/INFO]: [STDOUT]: [OptiFabric] OptiFine is not installed - showing the download screen (installed nothing, recommended OptiFine_1.21.1_HD_U_J1.jar)`
- Implicated layer: unexplained (control run itself failed)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): HARNESS-ERROR — latest.log has no recognisable fatal error and no client JVM was seen: the harness launche
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.
- Reading: the control run produced no usable result, so ownership is not asserted; no class could be extracted from the evidence line; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

**ebe** v0.10.2+1.21

- Verbatim evidence: `C104: [09:03:47] [Render thread/INFO]: [STDOUT]: [OptiFabric] OptiFine is not installed - showing the download screen (installed nothing, recommended OptiFine_1.21.1_HD_U_J1.jar)`
- Implicated layer: incompatible with OptiFine (mod itself fine on plain Fabric)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): OK — Sound engine started
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.
- Reading: the mod starts on plain Fabric 1.21.1 without OptiFabric/OptiFine (control OK), so the failure belongs to the OptiFine interaction; no class could be extracted from the evidence line; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.

#### `timeout` — 2 mods

**cf--aether** v—

- Verbatim evidence: `no title screen within the budget and no recognised error line`
- Implicated layer: broken on plain Fabric 1.21.1 (OptiFabric not implicated)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): FAIL — no title screen within the budget and no recognised error line
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

**cf--torchmaster** v—

- Verbatim evidence: `no title screen within the budget and no recognised error line`
- Implicated layer: broken on plain Fabric 1.21.1 (OptiFabric not implicated)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): FAIL — no title screen within the budget and no recognised error line
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.

#### `no-verdict-file` — 1 mod

**carpet-fixes** v—

- Verbatim evidence: `run-one.ps1 wrote no verdict.json`
- Implicated layer: unexplained (control run itself failed)
- Control run (fabric-api + this mod, no OptiFabric, no OptiFine): HARNESS-ERROR — the control run wrote no verdict.json
- Class named in the evidence: none could be extracted from the evidence line, so whether OptiFine rewrote the target is **not determined** here.
- Reading: the control run produced no usable result, so ownership is not asserted; no class could be extracted from the evidence line; this failure is NOT declared anywhere in OptiFabric's metadata, so it is an empirical finding.


## The matrix

| # | Mod | Downloads | Version tested | Verdict | Evidence / reason | Implicated layer | Target class in OptiFine's rewritten set? | Also tested without OptiFabric+OptiFine |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | [fabric-api](https://modrinth.com/mod/fabric-api) | 265.2M | 0.116.17+1.21.1 | N/A | base set: fabric-api is present in every run of the matrix, so it cannot be tested as a subject | — | — | not run |
| 2 | [sodium](https://modrinth.com/mod/sodium) *(renderer overhaul: replaces the chunk renderer)* | 234.3M | mc1.21.1-0.8.13-fabric | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | incompatible with OptiFine (mod itself fine on plain Fabric) | **yes** | OK — Sound engine started |
| 3 | [iris](https://modrinth.com/mod/iris) *(renderer overhaul: replaces the shader pipeline)* | 181.9M | 1.8.8+1.21.1-fabric | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"] | incompatible with OptiFine (mod itself fine on plain Fabric) | **yes** | OK — Sound engine started |
| 4 | [entityculling](https://modrinth.com/mod/entityculling) *(renderer overhaul: rewrites entity rendering/culling)* | 172.8M | 1.11.2 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 5 | [cloth-config](https://modrinth.com/mod/cloth-config) | 172.8M | 15.0.140+fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 6 | [ferrite-core](https://modrinth.com/mod/ferrite-core) | 154.8M | 7.0.3-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 7 | [modmenu](https://modrinth.com/mod/modmenu) | 148.6M | 11.0.5 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 8 | [lithium](https://modrinth.com/mod/lithium) | 131.4M | mc1.21.1-0.15.4-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 9 | [immediatelyfast](https://modrinth.com/mod/immediatelyfast) *(renderer overhaul: rewrites immediate-mode rendering)* | 127.0M | 1.6.14+1.21.1-fabric | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_1921 failed [in thread "main"] | incompatible with OptiFine (mod itself fine on plain Fabric) | **yes** | OK — Sound engine started |
| 10 | [yacl](https://modrinth.com/mod/yacl) | 126.0M | 3.8.2+1.21.1-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 11 | [fabric-language-kotlin](https://modrinth.com/mod/fabric-language-kotlin) | 122.2M | 1.14.1+kotlin.2.4.20 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 12 | [xaeros-minimap](https://modrinth.com/mod/xaeros-minimap) | 113.1M | fabric-1.21.1-26.5.0 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 13 | [entitytexturefeatures](https://modrinth.com/mod/entitytexturefeatures) *(renderer overhaul: rewrites entity textures/rendering)* | 103.5M | 7.2.4-fabric-1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 14 | [architectury-api](https://modrinth.com/mod/architectury-api) | 101.9M | 13.0.11+fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 15 | [sodium-extra](https://modrinth.com/mod/sodium-extra) *(renderer overhaul: replaces the chunk renderer)* | 99.0M | mc1.21.1-0.9.4+fabric | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | incompatible with OptiFine (mod itself fine on plain Fabric) | **yes** | OK — Sound engine started |
| 16 | [xaeros-world-map](https://modrinth.com/mod/xaeros-world-map) | 98.9M | fabric-1.21.1-1.46.0 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 17 | [entity-model-features](https://modrinth.com/mod/entity-model-features) *(renderer overhaul: rewrites entity models/rendering)* | 98.2M | 3.3.9-fabric-1.21 | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | incompatible with OptiFine (mod itself fine on plain Fabric) | **yes** | OK — Sound engine started |
| 18 | [appleskin](https://modrinth.com/mod/appleskin) | 91.9M | 3.0.6+mc1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 19 | [not-enough-animations](https://modrinth.com/mod/not-enough-animations) | 88.6M | 1.12.6 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 20 | [veinminer](https://modrinth.com/mod/veinminer) | 88.4M | 2.11.2 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 21 | [reeses-sodium-options](https://modrinth.com/mod/reeses-sodium-options) *(renderer overhaul: Sodium's options screen)* | 82.7M | mc1.21.1-2.2.4+fabric | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | incompatible with OptiFine (mod itself fine on plain Fabric) | **yes** | OK — Sound engine started |
| 22 | [jei](https://modrinth.com/mod/jei) | 79.7M | 19.51.0.418 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 23 | [modernfix](https://modrinth.com/mod/modernfix) | 79.1M | 5.25.1+mc1.21.1 | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | incompatible with OptiFine (mod itself fine on plain Fabric) | **yes** | OK — Sound engine started |
| 24 | [3dskinlayers](https://modrinth.com/mod/3dskinlayers) *(renderer overhaul: rewrites player model rendering)* | 78.8M | 1.11.3 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 25 | [continuity](https://modrinth.com/mod/continuity) *(renderer overhaul: replaces the block/connected-texture renderer)* | 74.9M | 3.0.0+1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 26 | [geckolib](https://modrinth.com/mod/geckolib) | 71.6M | 4.9.3 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 27 | [simple-voice-chat](https://modrinth.com/mod/simple-voice-chat) | 70.9M | fabric-1.21.1-2.6.22 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 28 | [jade](https://modrinth.com/mod/jade) | 69.3M | 15.10.6+fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 29 | [moreculling](https://modrinth.com/mod/moreculling) *(renderer overhaul: rewrites chunk/render culling)* | 67.5M | 1.0.10 | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_918 failed [in thread "main"] | incompatible with OptiFine (mod itself fine on plain Fabric) | **yes** | OK — Sound engine started |
| 30 | [zoomify](https://modrinth.com/mod/zoomify) | 67.5M | 2.15.2+1.21.1 | DEP | [07:44:21] [main/ERROR]: Incompatible mods found! | — | — | not run |
| 31 | [placeholder-api](https://modrinth.com/mod/placeholder-api) | 67.4M | 2.4.2+1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 32 | [fancymenu](https://modrinth.com/mod/fancymenu) | 67.2M | 3.9.14-1.21.1-fabric | DEP | [07:39:13] [main/ERROR]: Incompatible mods found! | — | — | not run |
| 33 | [dynamic-fps](https://modrinth.com/mod/dynamic-fps) | 67.1M | 3.11.4 | DEP | [07:39:06] [main/ERROR]: Incompatible mods found! | — | — | not run |
| 34 | [forge-config-api-port](https://modrinth.com/mod/forge-config-api-port) | 66.7M | v21.1.6-1.21.1-Fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 35 | [collective](https://modrinth.com/mod/collective) | 65.9M | 1.21.1-8.41-fabric+forge+neo | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 36 | [puzzles-lib](https://modrinth.com/mod/puzzles-lib) | 62.8M | 21.1.62 | DEP | [07:49:23] [main/ERROR]: Incompatible mods found! | — | — | not run |
| 37 | [konkrete](https://modrinth.com/mod/konkrete) | 62.2M | 1.9.9-1.21-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 38 | [veinminer-client](https://modrinth.com/mod/veinminer-client) | 62.2M | 2.11.2 | DEP | [07:49:32] [main/ERROR]: Incompatible mods found! | — | — | not run |
| 39 | [balm](https://modrinth.com/mod/balm) | 60.4M | 21.0.66+fabric-1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 40 | [mouse-tweaks](https://modrinth.com/mod/mouse-tweaks) | 59.8M | 1.21-2.26-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 41 | [lambdynamiclights](https://modrinth.com/mod/lambdynamiclights) | 59.3M | 4.8.11+1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 42 | [no-chat-reports](https://modrinth.com/mod/no-chat-reports) | 58.6M | Fabric-1.21.1-v2.9.1 | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 43 | [sound-physics-remastered](https://modrinth.com/mod/sound-physics-remastered) | 53.7M | fabric-1.21.1-1.4.10 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 44 | [creativecore](https://modrinth.com/mod/creativecore) | 52.4M | 2.13.48 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 45 | [melody](https://modrinth.com/mod/melody) | 50.3M | 1.0.10-1.21-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 46 | [chat-heads](https://modrinth.com/mod/chat-heads) | 49.7M | 0.15.7 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 47 | [owo-lib](https://modrinth.com/mod/owo-lib) | 48.6M | 0.12.15.4+1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 48 | [bookshelf-lib](https://modrinth.com/mod/bookshelf-lib) | 46.5M | 21.1.81 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 49 | [essential](https://modrinth.com/mod/essential) | 45.2M | 1.5.0.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 50 | [indium](https://modrinth.com/mod/indium) *(renderer overhaul: Sodium's Fabric rendering API backend)* | 44.6M | 1.0.35+mc1.21 | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 51 | [badoptimizations](https://modrinth.com/mod/badoptimizations) | 43.9M | 2.4.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 52 | [krypton](https://modrinth.com/mod/krypton) | 42.4M | 0.2.8 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 53 | [language-reload](https://modrinth.com/mod/language-reload) | 41.2M | 1.7.6+1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 54 | [moonlight](https://modrinth.com/mod/moonlight) | 41.1M | 1.21.1-3.7.0 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 55 | [fzzy-config](https://modrinth.com/mod/fzzy-config) | 40.6M | 0.7.7+1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 56 | [inventory-profiles-next](https://modrinth.com/mod/inventory-profiles-next) | 40.4M | fabric-1.21.1-2.2.6 | DEP | [07:59:50] [main/ERROR]: Incompatible mods found! | — | — | not run |
| 57 | [terrablender](https://modrinth.com/mod/terrablender) | 40.2M | 4.1.0.8 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 58 | [presence-footsteps](https://modrinth.com/mod/presence-footsteps) | 39.8M | 1.11.2+1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 59 | [searchables](https://modrinth.com/mod/searchables) | 39.7M | 1.0.2 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 60 | [controlling](https://modrinth.com/mod/controlling) | 39.6M | 19.0.5 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 61 | [ambientsounds](https://modrinth.com/mod/ambientsounds) | 39.5M | 6.3.9 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 62 | [clumps](https://modrinth.com/mod/clumps) | 39.4M | 19.0.0.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 63 | [shulkerboxtooltip](https://modrinth.com/mod/shulkerboxtooltip) | 38.2M | 5.1.9+1.21.1-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 64 | [c2me-fabric](https://modrinth.com/mod/c2me-fabric) | 38.2M | 0.4.0-alpha.0.29+1.21.1 | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"] | incompatible with OptiFine (mod itself fine on plain Fabric) | **yes** | OK — Sound engine started |
| 65 | [enchantment-descriptions](https://modrinth.com/mod/enchantment-descriptions) | 37.8M | 21.1.11 | DEP | [07:59:55] [main/ERROR]: Incompatible mods found! | — | — | not run |
| 66 | [biomes-o-plenty](https://modrinth.com/mod/biomes-o-plenty) | 37.1M | 21.1.0.14 | DEP | [08:05:06] [main/ERROR]: Incompatible mods found! | — | — | not run |
| 67 | [debugify](https://modrinth.com/mod/debugify) | 36.6M | 1.21.1+1.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 68 | [malilib](https://modrinth.com/mod/malilib) | 36.3M | 0.21.10 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 69 | [iceberg](https://modrinth.com/mod/iceberg) | 36.1M | 1.3.2 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 70 | [distanthorizons](https://modrinth.com/mod/distanthorizons) *(renderer overhaul: replaces distant terrain rendering)* | 35.6M | 3.3.3-1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 71 | [cobblemon](https://modrinth.com/mod/cobblemon) | 35.6M | 1.8.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 72 | [resourceful-lib](https://modrinth.com/mod/resourceful-lib) | 35.5M | 3.0.12 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 73 | [patchouli](https://modrinth.com/mod/patchouli) | 35.2M | 1.21.1-93-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 74 | [yungs-api](https://modrinth.com/mod/yungs-api) | 34.9M | 1.21.1-Fabric-5.1.9 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 75 | [libipn](https://modrinth.com/mod/libipn) | 34.6M | fabric-1.21.1-6.6.3 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 76 | [betterf3](https://modrinth.com/mod/betterf3) | 34.5M | 11.0.3 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 77 | [modelfix](https://modrinth.com/mod/modelfix) *(renderer overhaul: patches model rendering)* | 32.6M | 1.21-1.6 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 78 | [supermartijn642s-config-lib](https://modrinth.com/mod/supermartijn642s-config-lib) | 32.3M | 1.1.8-fabric-mc1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 79 | [cherished-worlds](https://modrinth.com/mod/cherished-worlds) | 32.2M | 10.1.1+1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 80 | [coroutil](https://modrinth.com/mod/coroutil) | 30.7M | 1.21.1-1.3.8 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 81 | [visuality](https://modrinth.com/mod/visuality) *(renderer overhaul: adds render-layer effects)* | 29.5M | 0.7.7+1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 82 | [packet-fixer](https://modrinth.com/mod/packet-fixer) | 29.3M | 3.3.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 83 | [emi](https://modrinth.com/mod/emi) | 29.2M | 1.1.24+1.21.1+fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 84 | [midnightlib](https://modrinth.com/mod/midnightlib) | 28.6M | 1.9.3+1.21.1-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 85 | [morechathistory](https://modrinth.com/mod/morechathistory) | 28.2M | 1.3.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 86 | [euphoria-patches](https://modrinth.com/mod/euphoria-patches) *(renderer overhaul: shader-pack add-on)* | 28.0M | 1.10.5-r5.9.3-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 87 | [cubes-without-borders](https://modrinth.com/mod/cubes-without-borders) *(renderer overhaul: changes the render/window pipeline)* | 28.0M | 3.0.0+1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 88 | [supplementaries](https://modrinth.com/mod/supplementaries) | 27.9M | 1.21.1-3.9.9 | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"] | incompatible with OptiFine (mod itself fine on plain Fabric) | **yes** | OK — Sound engine started |
| 89 | [playeranimator](https://modrinth.com/mod/playeranimator) | 27.1M | 2.0.4+1.21.1-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 90 | [fallingleaves](https://modrinth.com/mod/fallingleaves) *(renderer overhaul: rewrites particle/leaf rendering)* | 27.0M | 1.17.1+1.21.1 | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_702 failed [in thread "main"] | incompatible with OptiFine (mod itself fine on plain Fabric) | **yes** | OK — Sound engine started |
| 91 | [rrls](https://modrinth.com/mod/rrls) *(renderer overhaul: rewrites the render layer/state pipeline)* | 26.5M | 5.0.11+mc1.21.1-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 92 | [handcrafted](https://modrinth.com/mod/handcrafted) | 26.5M | 4.0.3 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 93 | [rei](https://modrinth.com/mod/rei) | 26.4M | 16.0.799+fabric | FAIL | C131: [08:42:11] [Render thread/INFO]: [STDOUT]: [OptiFabric] OptiFine is not installed - showing the download screen (installed nothing, recommended OptiFine_1.21.1_HD_U_J1.jar) | unexplained (control run itself failed) | — | HARNESS-ERROR — latest.log has no recognisable fatal error and no client JVM was seen: the harness launche |
| 94 | [carry-on](https://modrinth.com/mod/carry-on) | 26.1M | 2.2.6 | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"] | incompatible with OptiFine (mod itself fine on plain Fabric) | **yes** | OK — Sound engine started |
| 95 | [better-advancements](https://modrinth.com/mod/better-advancements) | 26.1M | 0.4.3.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 96 | [natures-compass](https://modrinth.com/mod/natures-compass) | 26.0M | 1.21.1-2.6.0-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 97 | [optigui](https://modrinth.com/mod/optigui) | 25.9M | 2.3.0-beta.9+1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 98 | [visual-workbench](https://modrinth.com/mod/visual-workbench) | 25.9M | 21.1.2 | DEP | [08:38:53] [main/ERROR]: Incompatible mods found! | — | — | not run |
| 99 | [waystones](https://modrinth.com/mod/waystones) | 25.9M | 21.1.46+fabric-1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 100 | [litematica](https://modrinth.com/mod/litematica) | 25.9M | 0.19.61 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [carpet](https://modrinth.com/mod/carpet) | 11.1M | 1.4.147 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [gca](https://modrinth.com/mod/gca) | 1.7M | v2.12.1+build.83 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| null | [carpet-tis-addition](https://modrinth.com/mod/carpet-tis-addition) | 1.6M | v1.82.4-mc1.21.1 | DEP | [07:39:04] [main/ERROR]: Incompatible mods found! | — | — | not run |
| null | [carpet-extra](https://modrinth.com/mod/carpet-extra) | 787.5K | 1.4.148 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| null | [carpet-org-addition](https://modrinth.com/mod/carpet-org-addition) | 677.6K | 1.41.7 | DEP | [07:39:06] [main/ERROR]: Incompatible mods found! | — | — | not run |
| null | [carpet-ams-addition](https://modrinth.com/mod/carpet-ams-addition) | 510.7K | mc1.21.1-v26.3 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [carpet-fixes](https://modrinth.com/mod/carpet-fixes) | 2.0M | — | HARNESS-ERROR | run-one.ps1 wrote no verdict.json | unexplained (control run itself failed) | — | HARNESS-ERROR — the control run wrote no verdict.json |
| null | [servux](https://modrinth.com/mod/servux) | 973.7K | 0.3.17 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 101 | [puzzle](https://modrinth.com/mod/puzzle) | 25.7M | 2.3.0+1.21.1-fabric | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 102 | [polymorph](https://modrinth.com/mod/polymorph) | 25.2M | 1.1.0+1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 103 | [resourceful-config](https://modrinth.com/mod/resourceful-config) | 25.0M | 3.0.11 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 104 | [ebe](https://modrinth.com/mod/ebe) | 25.0M | 0.10.2+1.21 | FAIL | C104: [09:03:47] [Render thread/INFO]: [STDOUT]: [OptiFabric] OptiFine is not installed - showing the download screen (installed nothing, recommended OptiFine_1.21.1_HD_U_J1.jar) | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 105 | [cit-resewn](https://modrinth.com/mod/cit-resewn) | 25.0M | 1.2.2+1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 106 | [e4mc](https://modrinth.com/mod/e4mc) | 24.4M | 6.2.3-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 107 | [trinkets](https://modrinth.com/mod/trinkets) | 24.3M | 3.10.0 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 108 | [netherportalfix](https://modrinth.com/mod/netherportalfix) | 24.3M | 21.1.3+fabric-1.21.1 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 109 | [capes](https://modrinth.com/mod/capes) | 24.0M | 1.5.4+1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 110 | [physicsmod](https://modrinth.com/mod/physicsmod) | 24.0M | 3.0.33 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 111 | [lmd](https://modrinth.com/mod/lmd) | 23.9M | 1.5.0 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 112 | [yosbr](https://modrinth.com/mod/yosbr) | 23.8M | 0.1.2 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 113 | [controlify](https://modrinth.com/mod/controlify) | 23.7M | 3.0.1+lts | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 114 | [better-mount-hud](https://modrinth.com/mod/better-mount-hud) | 23.7M | 1.2.4 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 115 | [customskinloader](https://modrinth.com/mod/customskinloader) | 23.7M | 15.0.1-Universal | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 116 | [fast-ip-ping](https://modrinth.com/mod/fast-ip-ping) | 23.6M | v1.0.12-mc1.21.1-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 117 | [athena-ctm](https://modrinth.com/mod/athena-ctm) | 23.5M | 4.0.6 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 118 | [lithostitched](https://modrinth.com/mod/lithostitched) | 23.4M | 1.8.0-fabric-21.1 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 119 | [terralith](https://modrinth.com/mod/terralith) | 23.4M | 2.6.2 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 120 | [sodium-shadowy-path-blocks](https://modrinth.com/mod/sodium-shadowy-path-blocks) *(renderer overhaul: replaces the chunk renderer)* | 23.4M | 4.1.0-fabric | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 121 | [fastquit](https://modrinth.com/mod/fastquit) | 23.0M | 3.0.0+1.20.6 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 122 | [chipped](https://modrinth.com/mod/chipped) | 23.0M | 4.0.2 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 123 | [comforts](https://modrinth.com/mod/comforts) | 23.0M | 9.0.5+1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 124 | [glitchcore](https://modrinth.com/mod/glitchcore) | 22.9M | 2.1.0.2 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 125 | [yungs-better-nether-fortresses](https://modrinth.com/mod/yungs-better-nether-fortresses) | 22.9M | 1.21.1-Fabric-3.1.5 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 126 | [chatanimation](https://modrinth.com/mod/chatanimation) | 22.8M | 1.3.3+fabric-1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 127 | [spark](https://modrinth.com/mod/spark) | 22.7M | 1.10.109-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 128 | [lootr](https://modrinth.com/mod/lootr) | 22.7M | 1.21.1-1.11.38.127 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 129 | [open-parties-and-claims](https://modrinth.com/mod/open-parties-and-claims) | 22.7M | fabric-1.21.1-0.31.6 | FAIL | [10:48:13] [main/WARN]: Error loading class: com/electronwill/nightconfig/core/io/IoUtils (java.lang.ClassNotFoundException: com/electronwill/nightconfig/core/io/IoUtils) | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 130 | [modern-ui](https://modrinth.com/mod/modern-ui) | 22.6M | 3.13.0.1 | FAIL | [11:16:35] [Render thread/WARN]: [OptiFine] (Reflector) java.lang.ClassNotFoundException: jdk.internal.misc.SharedSecrets | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 131 | [rhino](https://modrinth.com/mod/rhino) | 22.4M | 2101.2.7-build.85+Rhino-1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 132 | [drippy-loading-screen](https://modrinth.com/mod/drippy-loading-screen) | 22.3M | 3.1.5-1.21.1-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 133 | [kiwi](https://modrinth.com/mod/kiwi) | 22.0M | 15.8.8+fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 134 | [yungs-better-dungeons](https://modrinth.com/mod/yungs-better-dungeons) | 22.0M | 1.21.1-Fabric-5.1.4 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 135 | [yungs-better-ocean-monuments](https://modrinth.com/mod/yungs-better-ocean-monuments) | 22.0M | 1.21.1-Fabric-4.1.2 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 136 | [attributefix](https://modrinth.com/mod/attributefix) | 22.0M | 21.1.3 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 137 | [better-third-person](https://modrinth.com/mod/better-third-person) | 21.5M | 1.9.0 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 138 | [dungeons-and-taverns](https://modrinth.com/mod/dungeons-and-taverns) | 21.4M | v4.4.4+mod | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 139 | [freecam](https://modrinth.com/mod/freecam) | 21.2M | 1.3.0+mc1.21.1 | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"] | unexplained (control run itself failed) | — | HARNESS-ERROR — the control run wrote no verdict.json |
| 140 | [dynamiccrosshair](https://modrinth.com/mod/dynamiccrosshair) | 21.1M | 9.11+1.21.1-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 141 | [bobby](https://modrinth.com/mod/bobby) | 21.0M | 5.2.4.1+mc1.21 | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"] | unexplained (control run itself failed) | — | HARNESS-ERROR — mods\ differs from the recorded mirror: in the mirror but missing now: OptiFabric-2.0.0+mc |
| 142 | [particle-rain](https://modrinth.com/mod/particle-rain) | 21.0M | 3.0.5 | DEP | [08:44:02] [main/ERROR]: Incompatible mods found! | — | — | not run |
| 143 | [animatica](https://modrinth.com/mod/animatica) | 20.9M | 0.6.1+1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 144 | [almanac](https://modrinth.com/mod/almanac) | 20.9M | 1.5.2 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 145 | [travelersbackpack](https://modrinth.com/mod/travelersbackpack) | 20.7M | 1.21.1-10.1.39 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 146 | [sodium-options-api](https://modrinth.com/mod/sodium-options-api) *(renderer overhaul: replaces the chunk renderer)* | 20.5M | fabric-1.21.1-1.0.10 | DEP | [08:44:04] [main/ERROR]: Incompatible mods found! | — | — | not run |
| 147 | [yungs-better-mineshafts](https://modrinth.com/mod/yungs-better-mineshafts) | 20.3M | 1.21.1-Fabric-5.1.1 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 148 | [yungs-better-jungle-temples](https://modrinth.com/mod/yungs-better-jungle-temples) | 20.2M | 1.21.1-Fabric-3.1.2 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 149 | [toms-storage](https://modrinth.com/mod/toms-storage) | 20.1M | 1.21-2.4.2-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 150 | [mru](https://modrinth.com/mod/mru) | 20.0M | 1.0.41+1.21.1-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 151 | [yungs-better-end-island](https://modrinth.com/mod/yungs-better-end-island) | 19.8M | 1.21.1-Fabric-3.1.2 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 152 | [mixintrace](https://modrinth.com/mod/mixintrace) | 19.7M | 1.1.1+1.17 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 153 | [better-stats](https://modrinth.com/mod/better-stats) | 19.5M | 3.13.9+fabric-1.21 | DEP | [08:44:07] [main/ERROR]: Incompatible mods found! | — | — | not run |
| 154 | [enhancedvisuals](https://modrinth.com/mod/enhancedvisuals) | 19.4M | 1.8.30 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 155 | [fabrishot](https://modrinth.com/mod/fabrishot) | 19.3M | 1.14.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 156 | [crash-assistant](https://modrinth.com/mod/crash-assistant) | 19.3M | 1.11.12 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 157 | [amendments](https://modrinth.com/mod/amendments) | 19.1M | 1.21-2.1.10 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 158 | [yeetus-experimentus](https://modrinth.com/mod/yeetus-experimentus) | 18.9M | 87.0.0 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 159 | [sodium-dynamic-lights](https://modrinth.com/mod/sodium-dynamic-lights) *(renderer overhaul: replaces the chunk renderer)* | 18.9M | fabric-1.21.1-1.0.10 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 160 | [replaymod](https://modrinth.com/mod/replaymod) | 18.9M | 1.21-2.6.27 | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_309 failed [in thread "main"] | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 161 | [sound](https://modrinth.com/mod/sound) | 18.7M | 2.4.22+lts+1.21.1-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 162 | [chunky](https://modrinth.com/mod/chunky) | 18.5M | 1.4.23 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 163 | [yungs-better-strongholds](https://modrinth.com/mod/yungs-better-strongholds) | 18.3M | 1.21.1-Fabric-5.1.3 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 164 | [default-options](https://modrinth.com/mod/default-options) | 18.2M | 21.1.8+fabric-1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 165 | [cristel-lib](https://modrinth.com/mod/cristel-lib) | 18.1M | fabric-1.21.1-3.1.7 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 166 | [chloride](https://modrinth.com/mod/chloride) | 18.1M | 1.8.1-FABRIC-1.21.1 | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 167 | [supermartijn642s-core-lib](https://modrinth.com/mod/supermartijn642s-core-lib) | 17.9M | 1.1.24a-fabric-mc1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 168 | [paginatedadvancements](https://modrinth.com/mod/paginatedadvancements) | 17.9M | 2.5.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 169 | [prism-lib](https://modrinth.com/mod/prism-lib) | 17.9M | 1.0.11 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 170 | [towns-and-towers](https://modrinth.com/mod/towns-and-towers) | 17.8M | 1.13.11 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 171 | [cut-through](https://modrinth.com/mod/cut-through) | 17.8M | v21.1.0-1.21.1-Fabric | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_757 failed [in thread "main"] | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 172 | [wavey-capes](https://modrinth.com/mod/wavey-capes) | 17.7M | 1.11.2 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 173 | [yungs-better-witch-huts](https://modrinth.com/mod/yungs-better-witch-huts) | 17.7M | 1.21.1-Fabric-4.1.1 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 174 | [polytone](https://modrinth.com/mod/polytone) | 17.7M | 1.21-4.5.3 | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 175 | [advancement-plaques](https://modrinth.com/mod/advancement-plaques) | 17.4M | 1.6.8 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 176 | [veinminer-enchantment](https://modrinth.com/mod/veinminer-enchantment) | 17.3M | 2.11.2 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 177 | [servercore](https://modrinth.com/mod/servercore) | 17.3M | 1.5.19+1.21.1 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 178 | [i18nupdatemod](https://modrinth.com/mod/i18nupdatemod) | 17.2M | 3.7.0 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 179 | [item-highlighter](https://modrinth.com/mod/item-highlighter) | 17.0M | 1.1.11 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 180 | [main-menu-credits](https://modrinth.com/mod/main-menu-credits) | 17.0M | 1.2.0 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 181 | [vmp-fabric](https://modrinth.com/mod/vmp-fabric) | 16.9M | 0.2.0+beta.7.172+1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 182 | [invmove](https://modrinth.com/mod/invmove) | 16.8M | 0.9.3+1.21.1-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 183 | [particle-core](https://modrinth.com/mod/particle-core) | 16.7M | 0.3.3+1.21 | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_702 failed [in thread "main"] | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 184 | [bettergrassify](https://modrinth.com/mod/bettergrassify) | 16.7M | 1.8.7+fabric.1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 185 | [tcdcommons](https://modrinth.com/mod/tcdcommons) | 16.7M | 3.12.7+fabric-1.21 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 186 | [better-combat](https://modrinth.com/mod/better-combat) | 16.7M | 2.4.0+1.21.1-fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 187 | [tectonic](https://modrinth.com/mod/tectonic) | 16.6M | 3.0.28-fabric-21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 188 | [yungs-better-desert-temples](https://modrinth.com/mod/yungs-better-desert-temples) | 16.6M | 1.21.1-Fabric-4.1.5 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 189 | [easy-anvils](https://modrinth.com/mod/easy-anvils) | 16.6M | v21.1.0-1.21.1-Fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 190 | [shatterbyte-lib](https://modrinth.com/mod/shatterbyte-lib) | 16.4M | 0.6.2 | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_761 failed [in thread "main"] | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 191 | [tooltipfix](https://modrinth.com/mod/tooltipfix) | 16.4M | 1.1.1-1.20 | FAIL | Caused by: java.lang.ExceptionInInitializerError: Exception java.lang.RuntimeException: Mixin transformation of net.minecraft.class_332 failed [in thread "main"] | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 192 | [farmers-delight-refabricated](https://modrinth.com/mod/farmers-delight-refabricated) | 16.1M | 1.21.1-3.3.6 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 193 | [another-furniture](https://modrinth.com/mod/another-furniture) | 15.8M | 4.0.2 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 194 | [deeperdarker](https://modrinth.com/mod/deeperdarker) | 15.8M | 1.3.3-plus-b-fabric+1.21 | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | incompatible with OptiFine (mod itself fine on plain Fabric) | — | OK — Sound engine started |
| 195 | [yes-steve-model](https://modrinth.com/mod/yes-steve-model) | 15.7M | 2.6.5-fabric+mc1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 196 | [yungs-bridges](https://modrinth.com/mod/yungs-bridges) | 15.7M | 1.21.1-Fabric-5.1.1 | N/A | Modrinth marks this project client_side=unsupported, so a client launch cannot exercise it; no launch was performed | — | — | not needed (no failure to attribute) |
| 197 | [cicada](https://modrinth.com/mod/cicada) | 15.7M | 0.14.3+1.21-1.21.1 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 198 | [pick-up-notifier](https://modrinth.com/mod/pick-up-notifier) | 15.7M | v21.1.1-1.21.1-Fabric | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 199 | [globalpacks](https://modrinth.com/mod/globalpacks) | 15.6M | 21.0.6 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 200 | [libjf](https://modrinth.com/mod/libjf) | 15.5M | 3.17.5 | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| 201 | [betterend](https://modrinth.com/mod/betterend) | 15.5M | 21.0.11 | **not run** | — | — | — | not run |
| 202 | [cardinal-components-api](https://modrinth.com/mod/cardinal-components-api) | 15.3M | 6.1.3 | **not run** | — | — | — | not run |
| 203 | [notenoughcrashes](https://modrinth.com/mod/notenoughcrashes) | 15.3M | 4.4.9+1.21.1-fabric | **not run** | — | — | — | not run |
| 204 | [eating-animation](https://modrinth.com/mod/eating-animation) | 15.3M | 1.9.72 | **not run** | — | — | — | not run |
| 205 | [what-are-they-up-to](https://modrinth.com/mod/what-are-they-up-to) | 15.2M | 1.21.0-1.2.7 | **not run** | — | — | — | not run |
| 206 | [fusion-connected-textures](https://modrinth.com/mod/fusion-connected-textures) | 15.1M | 1.3.15b-fabric-mc1.21 | **not run** | — | — | — | not run |
| 207 | [immersive-aircraft](https://modrinth.com/mod/immersive-aircraft) | 15.0M | 1.5.2+1.21.1 | **not run** | — | — | — | not run |
| 208 | [corgilib](https://modrinth.com/mod/corgilib) | 14.9M | 1.21.1-5.0.0.9-Fabric | **not run** | — | — | — | not run |
| 209 | [justenoughcharacters](https://modrinth.com/mod/justenoughcharacters) | 14.9M | 4.5.28 | **not run** | — | — | — | not run |
| 210 | [bisect-mod](https://modrinth.com/mod/bisect-mod) | 14.8M | 2.5.3 | **not run** | — | — | — | not run |
| 211 | [journeymap](https://modrinth.com/mod/journeymap) | 14.8M | 1.21.1-6.0.9+fabric | **not run** | — | — | — | not run |
| 212 | [jamlib](https://modrinth.com/mod/jamlib) | 14.6M | 1.3.6+1.21.1 | **not run** | — | — | — | not run |
| 213 | [scalablelux](https://modrinth.com/mod/scalablelux) | 14.4M | 0.1.0.1+fabric.d0d58ab | **not run** | — | — | — | not run |
| 214 | [durability-tooltip](https://modrinth.com/mod/durability-tooltip) | 14.3M | 1.2.0-fabric-mc1.21 | **not run** | — | — | — | not run |
| 215 | [exposure](https://modrinth.com/mod/exposure) | 14.3M | 1.9.19 | **not run** | — | — | — | not run |
| 216 | [first-person-model](https://modrinth.com/mod/first-person-model) | 14.3M | 2.7.3 | **not run** | — | — | — | not run |
| 217 | [txnilib](https://modrinth.com/mod/txnilib) | 14.2M | fabric-1.21.1-1.0.24 | **not run** | — | — | — | not run |
| 218 | [repurposed-structures-fabric](https://modrinth.com/mod/repurposed-structures-fabric) | 14.2M | 7.5.22+1.21.1-fabric | **not run** | — | — | — | not run |
| 219 | [nuit](https://modrinth.com/mod/nuit) | 14.1M | mc1.21-0.7.4 | **not run** | — | — | — | not run |
| 220 | [particular](https://modrinth.com/mod/particular) | 14.0M | 1.1.2+1.21 | **not run** | — | — | — | not run |
| 221 | [skyboxify](https://modrinth.com/mod/skyboxify) | 14.0M | 1.4 | **not run** | — | — | — | not run |
| 222 | [accessories](https://modrinth.com/mod/accessories) | 13.9M | 1.1.0-beta.53+1.21.1 | **not run** | — | — | — | not run |
| 223 | [elytra-slot](https://modrinth.com/mod/elytra-slot) | 13.9M | 9.0.1+1.21.1 | **not run** | — | — | — | not run |
| 224 | [imblocker-original](https://modrinth.com/mod/imblocker-original) | 13.8M | 5.6.2.1 | **not run** | — | — | — | not run |
| 225 | [bad-wither-no-cookie](https://modrinth.com/mod/bad-wither-no-cookie) | 13.8M | 3.20.4 | **not run** | — | — | — | not run |
| 226 | [easy-magic](https://modrinth.com/mod/easy-magic) | 13.7M | v21.1.4-1.21.1-Fabric | **not run** | — | — | — | not run |
| 227 | [ping-wheel](https://modrinth.com/mod/ping-wheel) | 13.7M | 1.12.2 | **not run** | — | — | — | not run |
| 228 | [better-ping-display-fabric](https://modrinth.com/mod/better-ping-display-fabric) | 13.7M | 1.21.1-1.1.1 | **not run** | — | — | — | not run |
| 229 | [boat-item-view](https://modrinth.com/mod/boat-item-view) | 13.6M | 1.21-1.21.1-0.0.6-fabric | **not run** | — | — | — | not run |
| 230 | [necronomicon](https://modrinth.com/mod/necronomicon) | 13.6M | 1.6.0+1.21 | **not run** | — | — | — | not run |
| 231 | [artifacts](https://modrinth.com/mod/artifacts) | 13.5M | 13.2.4 | **not run** | — | — | — | not run |
| 232 | [ukulib](https://modrinth.com/mod/ukulib) | 13.4M | 1.4.1+1.21 | **not run** | — | — | — | not run |
| 233 | [craftpresence](https://modrinth.com/mod/craftpresence) | 13.4M | 2.7.1+1.21.1-fabric | **not run** | — | — | — | not run |
| 234 | [dynamictrees](https://modrinth.com/mod/dynamictrees) | 13.4M | 1.7.2-BETA | **not run** | — | — | — | not run |
| 235 | [axiom](https://modrinth.com/mod/axiom) | 13.4M | 6.1.3 | **not run** | — | — | — | not run |
| 236 | [make_bubbles_pop](https://modrinth.com/mod/make_bubbles_pop) | 13.3M | 0.3.0-fabric | **not run** | — | — | — | not run |
| 237 | [betternether](https://modrinth.com/mod/betternether) | 13.2M | 21.0.11 | **not run** | — | — | — | not run |
| 238 | [gamma-utils](https://modrinth.com/mod/gamma-utils) | 13.2M | 2.1.3 | **not run** | — | — | — | not run |
| 239 | [prickle](https://modrinth.com/mod/prickle) | 13.1M | 21.1.11 | **not run** | — | — | — | not run |
| 240 | [sophisticated-core-(unofficial-fabric-port)](https://modrinth.com/mod/sophisticated-core-(unofficial-fabric-port)) | 13.0M | 1.21.1-1.2.9.21.168 | **not run** | — | — | — | not run |
| 241 | [leaves-be-gone](https://modrinth.com/mod/leaves-be-gone) | 12.8M | v21.1.1-1.21.1-Fabric | **not run** | — | — | — | not run |
| 242 | [resourcify](https://modrinth.com/mod/resourcify) | 12.8M | 1.8.7 | **not run** | — | — | — | not run |
| 243 | [yungs-extras](https://modrinth.com/mod/yungs-extras) | 12.7M | 1.21.1-Fabric-5.1.1 | **not run** | — | — | — | not run |
| 244 | [mmmmmmmmmmmm](https://modrinth.com/mod/mmmmmmmmmmmm) | 12.5M | 1.21-2.1.2 | **not run** | — | — | — | not run |
| 245 | [bclib](https://modrinth.com/mod/bclib) | 12.5M | 21.0.13 | **not run** | — | — | — | not run |
| 246 | [fallingtree](https://modrinth.com/mod/fallingtree) | 12.5M | 1.21.1-1.21.1.11 | **not run** | — | — | — | not run |
| 247 | [cobblemon-mega-showdown](https://modrinth.com/mod/cobblemon-mega-showdown) | 12.5M | 1.2.0+1.8.1+1.21.1-release | **not run** | — | — | — | not run |
| 248 | [sodium-extras](https://modrinth.com/mod/sodium-extras) | 12.4M | fabric-1.21.1-1.0.8 | **not run** | — | — | — | not run |
| 249 | [rightclickharvest](https://modrinth.com/mod/rightclickharvest) | 12.4M | 4.6.1+1.21.1 | **not run** | — | — | — | not run |
| 250 | [smarter-farmers-farmers-replant](https://modrinth.com/mod/smarter-farmers-farmers-replant) | 12.4M | 1.21-2.2.4 | **not run** | — | — | — | not run |
| null | [cf--cyclops-core](https://modrinth.com/mod/cf--cyclops-core) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--the-twilight-forest](https://modrinth.com/mod/cf--the-twilight-forest) | ? | — | FAIL | Caused by: org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError: An unexpected critical error was encountered | broken on plain Fabric 1.21.1 (OptiFabric not implicated) | — | FAIL — [12:04:13] [Render thread/INFO]: [STDOUT]: [OptiFabric] OptiFine is not installed - showin |
| null | [cf--valhelsia-core](https://modrinth.com/mod/cf--valhelsia-core) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--smooth-chunk-save](https://modrinth.com/mod/cf--smooth-chunk-save) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--trash-cans](https://modrinth.com/mod/cf--trash-cans) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--refined-storage](https://modrinth.com/mod/cf--refined-storage) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--creeper-overhaul](https://modrinth.com/mod/cf--creeper-overhaul) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--fix-gpu-memory-leak](https://modrinth.com/mod/cf--fix-gpu-memory-leak) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--just-enough-resources-jer](https://modrinth.com/mod/cf--just-enough-resources-jer) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--kleeslabs](https://modrinth.com/mod/cf--kleeslabs) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--storage-drawers](https://modrinth.com/mod/cf--storage-drawers) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--better-compatibility-checker](https://modrinth.com/mod/cf--better-compatibility-checker) | ? | — | FAIL | Caused by: java.lang.ClassNotFoundException: net.neoforged.fml.config.IConfigSpec | broken on plain Fabric 1.21.1 (OptiFabric not implicated) | — | FAIL — Caused by: java.lang.ClassNotFoundException: net.neoforged.fml.config.IConfigSpec |
| null | [cf--ftb-essentials](https://modrinth.com/mod/cf--ftb-essentials) | ? | — | DEP | [12:21:14] [main/ERROR]: Incompatible mods found! | — | — | not run |
| null | [cf--macaws-bridges](https://modrinth.com/mod/cf--macaws-bridges) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--tips](https://modrinth.com/mod/cf--tips) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--macaws-lights-and-lamps](https://modrinth.com/mod/cf--macaws-lights-and-lamps) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--dark-utilities](https://modrinth.com/mod/cf--dark-utilities) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--crafttweaker](https://modrinth.com/mod/cf--crafttweaker) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--macaws-windows](https://modrinth.com/mod/cf--macaws-windows) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--macaws-roofs](https://modrinth.com/mod/cf--macaws-roofs) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--cupboard](https://modrinth.com/mod/cf--cupboard) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--loot-integrations](https://modrinth.com/mod/cf--loot-integrations) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--framework](https://modrinth.com/mod/cf--framework) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--farming-for-blockheads](https://modrinth.com/mod/cf--farming-for-blockheads) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--structure-essentials-forge-fabric](https://modrinth.com/mod/cf--structure-essentials-forge-fabric) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--ftb-xmod-compat](https://modrinth.com/mod/cf--ftb-xmod-compat) | ? | — | DEP | [12:05:56] [main/ERROR]: Incompatible mods found! | — | — | not run |
| null | [cf--pehkui](https://modrinth.com/mod/cf--pehkui) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--serene-seasons](https://modrinth.com/mod/cf--serene-seasons) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--cooking-for-blockheads](https://modrinth.com/mod/cf--cooking-for-blockheads) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--explorers-compass](https://modrinth.com/mod/cf--explorers-compass) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--openblocks-elevator](https://modrinth.com/mod/cf--openblocks-elevator) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--charm-of-undying](https://modrinth.com/mod/cf--charm-of-undying) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--configured](https://modrinth.com/mod/cf--configured) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--aether](https://modrinth.com/mod/cf--aether) | ? | — | FAIL | no title screen within the budget and no recognised error line | broken on plain Fabric 1.21.1 (OptiFabric not implicated) | — | FAIL — no title screen within the budget and no recognised error line |
| null | [cf--macaws-fences-and-walls](https://modrinth.com/mod/cf--macaws-fences-and-walls) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--ichunutil](https://modrinth.com/mod/cf--ichunutil) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--crafting-tweaks](https://modrinth.com/mod/cf--crafting-tweaks) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--macaws-furniture](https://modrinth.com/mod/cf--macaws-furniture) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--just-enough-professions-jep](https://modrinth.com/mod/cf--just-enough-professions-jep) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--smartbrainlib](https://modrinth.com/mod/cf--smartbrainlib) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--reborncore](https://modrinth.com/mod/cf--reborncore) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--macaws-doors](https://modrinth.com/mod/cf--macaws-doors) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--torchmaster](https://modrinth.com/mod/cf--torchmaster) | ? | — | FAIL | no title screen within the budget and no recognised error line | broken on plain Fabric 1.21.1 (OptiFabric not implicated) | — | FAIL — no title screen within the budget and no recognised error line |
| null | [cf--goblin-traders](https://modrinth.com/mod/cf--goblin-traders) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--starter-kit](https://modrinth.com/mod/cf--starter-kit) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--connectivity](https://modrinth.com/mod/cf--connectivity) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--macaws-trapdoors](https://modrinth.com/mod/cf--macaws-trapdoors) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--inventory-hud-forge](https://modrinth.com/mod/cf--inventory-hud-forge) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |
| null | [cf--trashslot](https://modrinth.com/mod/cf--trashslot) | ? | — | OK | Sound engine started | — | — | not needed (no failure to attribute) |

## Remaining (in the pool, not run)

50 mods have no verdict yet. Nothing is claimed about them.

- 201. betterend (v21.0.11)
- 202. cardinal-components-api (v6.1.3)
- 203. notenoughcrashes (v4.4.9+1.21.1-fabric)
- 204. eating-animation (v1.9.72)
- 205. what-are-they-up-to (v1.21.0-1.2.7)
- 206. fusion-connected-textures (v1.3.15b-fabric-mc1.21)
- 207. immersive-aircraft (v1.5.2+1.21.1)
- 208. corgilib (v1.21.1-5.0.0.9-Fabric)
- 209. justenoughcharacters (v4.5.28)
- 210. bisect-mod (v2.5.3)
- 211. journeymap (v1.21.1-6.0.9+fabric)
- 212. jamlib (v1.3.6+1.21.1)
- 213. scalablelux (v0.1.0.1+fabric.d0d58ab)
- 214. durability-tooltip (v1.2.0-fabric-mc1.21)
- 215. exposure (v1.9.19)
- 216. first-person-model (v2.7.3)
- 217. txnilib (vfabric-1.21.1-1.0.24)
- 218. repurposed-structures-fabric (v7.5.22+1.21.1-fabric)
- 219. nuit (vmc1.21-0.7.4)
- 220. particular (v1.1.2+1.21)
- 221. skyboxify (v1.4)
- 222. accessories (v1.1.0-beta.53+1.21.1)
- 223. elytra-slot (v9.0.1+1.21.1)
- 224. imblocker-original (v5.6.2.1)
- 225. bad-wither-no-cookie (v3.20.4)
- 226. easy-magic (vv21.1.4-1.21.1-Fabric)
- 227. ping-wheel (v1.12.2)
- 228. better-ping-display-fabric (v1.21.1-1.1.1)
- 229. boat-item-view (v1.21-1.21.1-0.0.6-fabric)
- 230. necronomicon (v1.6.0+1.21)
- 231. artifacts (v13.2.4)
- 232. ukulib (v1.4.1+1.21)
- 233. craftpresence (v2.7.1+1.21.1-fabric)
- 234. dynamictrees (v1.7.2-BETA)
- 235. axiom (v6.1.3)
- 236. make_bubbles_pop (v0.3.0-fabric)
- 237. betternether (v21.0.11)
- 238. gamma-utils (v2.1.3)
- 239. prickle (v21.1.11)
- 240. sophisticated-core-(unofficial-fabric-port) (v1.21.1-1.2.9.21.168)
- 241. leaves-be-gone (vv21.1.1-1.21.1-Fabric)
- 242. resourcify (v1.8.7)
- 243. yungs-extras (v1.21.1-Fabric-5.1.1)
- 244. mmmmmmmmmmmm (v1.21-2.1.2)
- 245. bclib (v21.0.13)
- 246. fallingtree (v1.21.1-1.21.1.11)
- 247. cobblemon-mega-showdown (v1.2.0+1.8.1+1.21.1-release)
- 248. sodium-extras (vfabric-1.21.1-1.0.8)
- 249. rightclickharvest (v4.6.1+1.21.1)
- 250. smarter-farmers-farmers-replant (v1.21-2.2.4)

<!-- PHASE-SECTIONS: generated by tools/build-phase-report.mjs; everything after this line is replaced on each run -->

---

## Coverage by phase: what was run, and what was not

Coverage is stated per phase rather than implied. A row with no verdict is never reported as a pass and no
rate here is computed over a subset that would flatter it.

| Phase | Rows | Launched | Verdicts | Not run |
| --- | --- | --- | --- | --- |
| top-100 pool by downloads | 99 | 99 | DEP 9, FAIL 15, OK 75 | — |
| extended pool, ranks 101-200 | 100 | 100 | DEP 3, FAIL 15, N/A 15, OK 67 | — |
| hand-picked Carpet family | 8 | 8 | DEP 2, HARNESS-ERROR 1, N/A 3, OK 2 | — |
| CurseForge batch (50 candidates) | 50 | 49 | DEP 2, FAIL 4, OK 43 | 1 (no fetchable 1.21.1 Fabric file) |
| Random-sample study (100 drawn) | 100 | not run yet | — | — |
| Modpack census | 100 | 0 | — | by design: a separate census, not launch rows |

### Rows deliberately dropped, and rows that could not be run

- **Extended pool, ranks past 200: 50 rows, not run.** Ranks past ~200 are overwhelmingly libraries, datapack
  and server utilities where "OK with OptiFabric" carries almost no information. Those hours were moved to
  the CurseForge batch and the random-sample study, which measure something. Labelled `not run`, never a pass.
- **CurseForge candidates that cannot be fetched.** `structory` — its 1.21.1 Fabric file record carries no `downloadUrl` (the project disables third-party API downloads), so the file exists but cannot be obtained without the official API
- **Random-sample rows that are untestable.** 6 of the hundred: `sample--mr-yungs-better-desert-temples` (server-only: the project's own metadata says client_side=unsupported, so a client launch cannot exercise it); `sample--mr-audaki-cart-engine` (server-only: the project's own metadata says client_side=unsupported, so a client launch cannot exercise it); `sample--mr-configurable-extra-mob-drops` (server-only: the project's own metadata says client_side=unsupported, so a client launch cannot exercise it); `sample--mr-mod-list-was-taken` (server-only: the project's own metadata says client_side=unsupported, so a client launch cannot exercise it); `sample--cf-skin-layers-3d` (the CurseForge file record for the newest 1.21.1 Fabric file (skinlayers3d-fabric-1.11.3-mc1.21.1.jar) carries no downlo); `sample--cf-more-overlays-updated` (the CurseForge file record for the newest 1.21.1 Fabric file (moreoverlays-1.24.1-mc1.21.1-fabric.jar) carries no downlo)

## The CurseForge batch

The point of this batch is what a Modrinth-only sweep structurally cannot reach. It was launched in
CurseForge download order, with **The Twilight Forest first** (lane1, started alone before the other nine lanes).
Every row launches the **CurseForge build**, named exactly below; where a project is also on Modrinth the two
builds can be different code (Farmer's Delight's Forge line versus the Refabricated port, Sophisticated Core's
official line versus the unofficial Fabric port), which is why the file name, id and SHA-1 are recorded per row.
Neither Farmer's Delight nor Sophisticated Core is in this batch: the CurseForge pages for both publish no
**1.21.1 Fabric** file at all (their CurseForge lines are Forge/NeoForge for 1.21.1), so there is no
CurseForge build of them to launch - the Modrinth Fabric ports are the only 1.21.1 Fabric builds that exist.

| # | CurseForge project | CF downloads | CF 1.21.1 Fabric file launched | file id | SHA-1 of the bytes | Verdict |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | [The Twilight Forest](https://www.curseforge.com/minecraft/mc-mods/the-twilight-forest) | 208,419,251 | `twilightforest-fabric-1.21.1-4.8.734.jar` (mirror's published SHA-1 matches) | 9003337 | `274f3e3bab137a8ce5ff02564a2e4085454040cb` | FAIL (control FAIL) |
| 2 | [Just Enough Resources (JER)](https://www.curseforge.com/minecraft/mc-mods/just-enough-resources-jer) | 242,629,221 | `JustEnoughResources-Fabric-1.21.1-1.6.0.17.jar` (mirror's published SHA-1 matches) | 6506299 | `3ec90637c4eba5664649e3e2c514e8b43ba107d1` | OK |
| 3 | [Storage Drawers](https://www.curseforge.com/minecraft/mc-mods/storage-drawers) | 240,069,111 | `StorageDrawers-fabric-1.21.1-13.11.4.jar` (mirror's published SHA-1 matches) | 6995424 | `4e0956acf40a68bf0c4acac290c0cf169445dee4` | OK |
| 4 | [CraftTweaker](https://www.curseforge.com/minecraft/mc-mods/crafttweaker) | 227,970,125 | `CraftTweaker-fabric-1.21.1-21.0.38.jar` (mirror's published SHA-1 matches) | 7772818 | `5ae830c68919a115d2aba97c4ee2bea6600234b3` | OK |
| 5 | [Cupboard](https://www.curseforge.com/minecraft/mc-mods/cupboard) | 207,287,463 | `cupboard-fabric-1.21.1-4.2.jar` (mirror's published SHA-1 matches) | 8888955 | `c790bd1c15a216f0291a99ac1e4dc1f4829270fc` | OK |
| 6 | [Cooking for Blockheads](https://www.curseforge.com/minecraft/mc-mods/cooking-for-blockheads) | 197,893,306 | `cookingforblockheads-fabric-1.21.1-21.1.24.jar` (mirror's published SHA-1 matches) | 8404449 | `6d334327d501e7429fd0e7f2d10544955dbdc94f` | OK |
| 7 | [OpenBlocks Elevator](https://www.curseforge.com/minecraft/mc-mods/openblocks-elevator) | 179,073,751 | `elevatorid-fabric-1.21-1.11.1.jar` (mirror's published SHA-1 matches) | 5552108 | `3f34470781f4b5cbeb1e83a259cceae616077b67` | OK |
| 8 | [Crafting Tweaks](https://www.curseforge.com/minecraft/mc-mods/crafting-tweaks) | 176,795,096 | `craftingtweaks-fabric-1.21.1-21.1.11.jar` (mirror's published SHA-1 matches) | 8697048 | `b428e5e64e16a671b7f620accffdf47b64375b92` | OK |
| 9 | [Torchmaster](https://www.curseforge.com/minecraft/mc-mods/torchmaster) | 165,891,165 | `torchmaster-fabric-1.21.1-21.1.13.jar` (mirror's published SHA-1 matches) | 8949980 | `ba45b64e8ae9f914898201f3fa7e684004b52d4c` | FAIL (control FAIL) |
| 10 | [Connectivity](https://www.curseforge.com/minecraft/mc-mods/connectivity) | 163,682,283 | `connectivity-fabric-1.21-7.7.jar` (mirror's published SHA-1 matches) | 9011114 | `cde4b589ba88bc8b32a5b0b9f0dc102d3d6eac47` | OK |
| 11 | [Cyclops Core](https://www.curseforge.com/minecraft/mc-mods/cyclops-core) | 158,183,866 | `cyclopscore-1.21.1-fabric-1.30.0.jar` (mirror's published SHA-1 matches) | 8859264 | `153b13004ed970d8b70a04eccf77b7859f88f1d6` | OK |
| 12 | [Refined Storage](https://www.curseforge.com/minecraft/mc-mods/refined-storage) | 156,198,197 | `refinedstorage-fabric-2.0.9.jar` (mirror's published SHA-1 matches) | 8211700 | `0be595b8c0d96010172df81a91fbb46e4f1a71db` | OK |
| 13 | [Macaw's Bridges](https://www.curseforge.com/minecraft/mc-mods/macaws-bridges) | 155,463,554 | `mcw-bridges-3.1.2-mc1.21.1fabric.jar` (mirror's published SHA-1 matches) | 7628029 | `c496bc2bc330e23c5100fd0a2421d12d4da7cd11` | OK |
| 14 | [Dark Utilities](https://www.curseforge.com/minecraft/mc-mods/dark-utilities) | 143,027,092 | `darkutils-fabric-1.21.1-21.1.1.jar` (mirror's published SHA-1 matches) | 7336065 | `8772d9cf6ac3b6f218ac4e0061af501b1a12a3b2` | OK |
| 15 | [Framework](https://www.curseforge.com/minecraft/mc-mods/framework) | 140,072,721 | `framework-fabric-1.21.1-0.13.11.jar` (mirror's published SHA-1 matches) | 7530359 | `87ce6c5c312da043e823e9407a04d307b5974d06` | OK |
| 16 | [FTB XMod Compat](https://www.curseforge.com/minecraft/mc-mods/ftb-xmod-compat) | 131,661,984 | `ftb-xmod-compat-fabric-21.1.12.jar` (mirror's published SHA-1 matches) | 8909888 | `ac881a3b567058f97313d308bf46649a40042493` | DEP |
| 17 | [Configured](https://www.curseforge.com/minecraft/mc-mods/configured) | 128,976,656 | `configured-fabric-1.21.1-2.6.3.jar` (mirror's published SHA-1 matches) | 7276575 | `7d1f43eb143c528e2431d8dcaf2e3007837ddf08` | OK |
| 18 | [iChunUtil](https://www.curseforge.com/minecraft/mc-mods/ichunutil) | 128,238,090 | `iChunUtil-1.21-Fabric-1.0.3.jar` (mirror's published SHA-1 matches) | 6000437 | `a47cac07d5606482b93d94b8a484ddeb24fbc669` | OK |
| 19 | [Macaw's Doors](https://www.curseforge.com/minecraft/mc-mods/macaws-doors) | 126,915,990 | `mcw-doors-1.1.5-mc1.21.1fabric.jar` (mirror's published SHA-1 matches) | 7618800 | `7b31f8db94fff60eee188a5118438def65adba40` | OK |
| 20 | [TrashSlot](https://www.curseforge.com/minecraft/mc-mods/trashslot) | 126,581,519 | `trashslot-fabric-1.21.1-21.1.11.jar` (mirror's published SHA-1 matches) | 8163132 | `1e58a95ebfd9c2078fcba4100f1afa80512c9f94` | OK |
| 21 | [Server Performance - Smooth Chunk Save](https://www.curseforge.com/minecraft/mc-mods/smooth-chunk-save) | 122,785,803 | `smoothchunk-fabric-1.21-4.1.jar` (mirror's published SHA-1 matches) | 6296632 | `b367bd9aa8709c09bb26662aeefd0d8bc871d3f5` | OK |
| 22 | [KleeSlabs](https://www.curseforge.com/minecraft/mc-mods/kleeslabs) | 121,503,734 | `kleeslabs-fabric-1.21.1-21.1.11.jar` (mirror's published SHA-1 matches) | 8163140 | `035615a9eba7d521353e7895774ed1c4128d3ac0` | OK |
| 23 | [Better Compatibility Checker](https://www.curseforge.com/minecraft/mc-mods/better-compatibility-checker) | 121,026,932 | `better-compatability-checker-fabric-21.1.8.jar` (mirror's published SHA-1 matches) | 7404404 | `0b71c071b83c613ba83a7f452db008385131a0c7` | FAIL (control FAIL) |
| 24 | [Macaw's Windows](https://www.curseforge.com/minecraft/mc-mods/macaws-windows) | 119,724,701 | `mcw-mcwwindows-2.4.2-mc1.21.1fabric.jar` (mirror's published SHA-1 matches) | 7317646 | `0d47db265d99f75f29c93b37489b5602f37ec4af` | OK |
| 25 | [Farming for Blockheads](https://www.curseforge.com/minecraft/mc-mods/farming-for-blockheads) | 117,380,805 | `farmingforblockheads-fabric-1.21.1-21.1.14.jar` (mirror's published SHA-1 matches) | 8969750 | `182422351251582f00852468160ff79285b11e21` | OK |
| 26 | [Serene Seasons](https://www.curseforge.com/minecraft/mc-mods/serene-seasons) | 115,660,227 | `SereneSeasons-fabric-1.21.1-10.1.0.3.jar` (mirror's published SHA-1 matches) | 6182595 | `b2c666d1b48974d2a34658612062b7c71cebcaeb` | OK |
| 27 | [Macaw's Fences and Walls](https://www.curseforge.com/minecraft/mc-mods/macaws-fences-and-walls) | 115,423,464 | `mcw-mcwfences-1.2.1-mc1.21.1fabric.jar` (mirror's published SHA-1 matches) | 7308375 | `f9be270761e17ad131f186b2537c2ce2f305815a` | OK |
| 28 | [Just Enough Professions (JEP)](https://www.curseforge.com/minecraft/mc-mods/just-enough-professions-jep) | 110,054,982 | `JustEnoughProfessions-fabric-1.21.1-4.0.5.jar` (mirror's published SHA-1 matches) | 7966682 | `e1f77d90829632cf7e890c6f482b4795f58a71d3` | OK |
| 29 | [Reborn Core](https://www.curseforge.com/minecraft/mc-mods/reborncore) | 107,703,550 | `RebornCore-5.11.19.jar` (mirror's published SHA-1 matches) | 6664366 | `dcdd32735c415acd82e7955440ce2b2d552fd23c` | OK |
| 30 | [Macaw's Trapdoors](https://www.curseforge.com/minecraft/mc-mods/macaws-trapdoors) | 107,295,018 | `mcw-trapdoors-1.1.5-mc1.21.1fabric.jar` (mirror's published SHA-1 matches) | 7256233 | `ecf7e153e9ae9fa4707ad6f200177ae87d0bbfff` | OK |
| 31 | [Trash Cans](https://www.curseforge.com/minecraft/mc-mods/trash-cans) | 107,068,202 | `trashcans-1.1.1-fabric-mc1.21.jar` (mirror's published SHA-1 matches) | 8972268 | `d094f59effcd2a904b3fca799ce62f54c946c25f` | OK |
| 32 | [fix GPU memory leak](https://www.curseforge.com/minecraft/mc-mods/fix-gpu-memory-leak) | 106,132,885 | `gpumemleakfix-fabric-1.21-1.8.jar` (mirror's published SHA-1 matches) | 5513550 | `416bca48280bbbb824e595d12fc694cc7488cfc4` | OK |
| 33 | [FTB Essentials (Forge & Fabric)](https://www.curseforge.com/minecraft/mc-mods/ftb-essentials) | 105,132,034 | `ftb-essentials-fabric-2101.1.10.jar` (mirror's published SHA-1 matches) | 8442865 | `92cc7924d80cac7e941e7fbb739f8fe12148fe68` | DEP |
| 34 | [Macaw's Roofs](https://www.curseforge.com/minecraft/mc-mods/macaws-roofs) | 104,182,195 | `mcw-roofs-2.3.2-mc1.21.1fabric.jar` (mirror's published SHA-1 matches) | 6494433 | `2367e5972a8ab30cc4f764e6ec98841e3eaa341a` | OK |
| 35 | [Structure Essentials](https://www.curseforge.com/minecraft/mc-mods/structure-essentials-forge-fabric) | 100,700,343 | `structureessentials-fabric-1.21-5.0.jar` (mirror's published SHA-1 matches) | 7962589 | `288325cab1dd28bd43ab44a212f25114f04bc4c1` | OK |
| 36 | [Explorer's Compass](https://www.curseforge.com/minecraft/mc-mods/explorers-compass) | 99,195,655 | `ExplorersCompass-1.21.1-2.6.0-fabric.jar` (mirror's published SHA-1 matches) | 7892942 | `cc3cf49a0ddb726e28af58d186145b027ea0adb5` | OK |
| 37 | [Charm of Undying (Fabric/Forge/Quilt)](https://www.curseforge.com/minecraft/mc-mods/charm-of-undying) | 91,881,772 | `charmofundying-fabric-9.1.0+1.21.1.jar` (mirror's published SHA-1 matches) | 5846601 | `cf1478a22e4aa06c3ce1728ad2713a8128ceeea1` | OK |
| 38 | [Macaw's Furniture](https://www.curseforge.com/minecraft/mc-mods/macaws-furniture) | 90,956,582 | `mcw-furniture-3.4.1-mc1.21.1fabric.jar` (mirror's published SHA-1 matches) | 7255687 | `4c914a4d3bb84103b34346e60c6aa981be8dfa23` | OK |
| 39 | [Goblin Traders](https://www.curseforge.com/minecraft/mc-mods/goblin-traders) | 89,439,959 | `goblintraders-fabric-1.21.1-1.11.2.jar` (mirror's published SHA-1 matches) | 6427511 | `b8b2e0df693cb7c4aa2338dd916a7066f988109a` | OK |
| 40 | [Inventory HUD+](https://www.curseforge.com/minecraft/mc-mods/inventory-hud-forge) | 87,909,558 | `inventoryhud.fabric.1.21.1-3.4.26.jar` (mirror's published SHA-1 matches) | 5639986 | `eacbf082d653e9be288298a009609aa2e57b480f` | OK |
| 41 | [Valhelsia Core](https://www.curseforge.com/minecraft/mc-mods/valhelsia-core) | 87,634,571 | `valhelsia_core-fabric-1.21.1-1.1.5.jar` (mirror's published SHA-1 matches) | 6296784 | `18ac1c3d8278bab079160eeb9baf8dd1b7507658` | OK |
| 42 | [Creeper Overhaul](https://www.curseforge.com/minecraft/mc-mods/creeper-overhaul) | 86,299,078 | `CreeperOverhaul-fabric-1.21.1-4.0.6.jar` (mirror's published SHA-1 matches) | 6051282 | `1e12b0d7c310ad60bd648dd97cbc459ab57eaa38` | OK |
| 43 | [Tips](https://www.curseforge.com/minecraft/mc-mods/tips) | 83,556,507 | `tipsmod-fabric-1.21.1-21.1.3.jar` (mirror's published SHA-1 matches) | 7126356 | `400a51caa8b4d41a5daf4ca066d9b04d21de1c3d` | OK |
| 44 | [Macaw's Lights and Lamps](https://www.curseforge.com/minecraft/mc-mods/macaws-lights-and-lamps) | 82,745,938 | `mcw-lights-1.1.5-mc1.21.1fabric.jar` (mirror's published SHA-1 matches) | 7304042 | `0dacc96b850d57f9d51c232b3c123698f4bc73b3` | OK |
| 45 | [Loot Integrations](https://www.curseforge.com/minecraft/mc-mods/loot-integrations) | 81,222,176 | `lootintegrations-fabric-1.21-4.7.jar` (mirror's published SHA-1 matches) | 6641045 | `c49967152f302b18a1e0e2e556a729dee8b477b4` | OK |
| 46 | [Pehkui](https://www.curseforge.com/minecraft/mc-mods/pehkui) | 79,989,273 | `Pehkui-3.8.3+1.14.4-1.21.jar` (mirror's published SHA-1 matches) | 5419266 | `3ce548910f0aa2cef79dc333982493ccf8d73a3e` | OK |
| 47 | [The Aether](https://www.curseforge.com/minecraft/mc-mods/aether) | 78,964,612 | `aether-1.21.1-1.5.11-fabric.jar` (mirror's published SHA-1 matches) | 7062820 | `1d9db7437ecc7b5ebab2dd918abca63908152f3a` | FAIL (control FAIL) |
| 48 | [SmartBrainLib (Forge/Fabric/Quilt)](https://www.curseforge.com/minecraft/mc-mods/smartbrainlib) | 78,891,576 | `SmartBrainLib-fabric-1.21.1-1.16.11.jar` (mirror's published SHA-1 matches) | 7055146 | `73d46a6e22efb34b787d702c2ef5ad15818181e0` | OK |
| 49 | [Starter Kit](https://www.curseforge.com/minecraft/mc-mods/starter-kit) | 78,313,001 | `starterkit-1.21.1-8.3.jar` (mirror's published SHA-1 matches) | 9032655 | `c3ccf12374bb9eaad8e6046c6eb419f5f26b997e` | OK |
| 50 | [structory](https://www.curseforge.com/minecraft/mc-mods/structory) | — | `—` — **no downloadUrl, cannot be fetched** | — | — | **not run** |

Failures by what the no-OptiFine control showed: mod-broken-on-plain-fabric 4, no control 2, — 43.

