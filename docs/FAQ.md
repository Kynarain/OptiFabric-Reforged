# Frequently asked questions

[🇬🇧 English](./FAQ.md) | [🇨🇳 中文版](./FAQ_CN.md)

Short answers, each one backed by something you can check: a table in the README, a line in
`COMPATIBILITY.md`, or a setting in `fabric.mod.json`. Where a question has no measured answer, it says so
instead of guessing.

**Contents**

1. [Which OptiFine jar do I need?](#1-which-optifine-jar-do-i-need)
2. [The game shows a dialog about OptiFine - what does it want?](#2-the-game-shows-a-dialog-about-optifine---what-does-it-want)
3. [Why is Sodium refused, and why does the whole instance fail to start?](#3-why-is-sodium-refused-and-why-does-the-whole-instance-fail-to-start)
4. [What about the rest of the Sodium family (Iris, Indium, Chloride, Sodium Extra...)?](#4-what-about-the-rest-of-the-sodium-family-iris-indium-chloride-sodium-extra)
5. [Can I use Photon? The sky and water flicker.](#5-can-i-use-photon-the-sky-and-water-flicker)
6. [My log is full of `Invalid program name` and `Error compiling vertex shader`. Is something broken?](#6-my-log-is-full-of-invalid-program-name-and-error-compiling-vertex-shader-is-something-broken)
7. [Shaders crash on 1.21.6 or 1.21.7.](#7-shaders-crash-on-1216-or-1217)
8. [Is it compatible with Sinytra Connector (信雅互联) or Kilt?](#8-is-it-compatible-with-sinytra-connector-信雅互联-or-kilt)
9. [Does this mod download or bundle OptiFine?](#9-does-this-mod-download-or-bundle-optifine)
10. [Which mods are known to be incompatible?](#10-which-mods-are-known-to-be-incompatible)
11. [Nothing changed after I upgraded.](#11-nothing-changed-after-i-upgraded)
12. [Where is the `[OptiFabric]` output? I cannot find it in `latest.log`.](#12-where-is-the-optifabric-output-i-cannot-find-it-in-latestlog)
13. [Why is the mod id `optifabric_reforged` and not `optifabric`?](#13-why-is-the-mod-id-optifabric_reforged-and-not-optifabric)
14. [Does it work on a server? Does it work without Fabric API?](#14-does-it-work-on-a-server-does-it-work-without-fabric-api)
15. [Which Minecraft versions are actually verified, and what is not?](#15-which-minecraft-versions-are-actually-verified-and-what-is-not)
16. [Can I use it in a development environment?](#16-can-i-use-it-in-a-development-environment)
17. [Was this written by AI?](#17-was-this-written-by-ai)
18. [How do I report a problem so that it can actually be looked at?](#18-how-do-i-report-a-problem-so-that-it-can-actually-be-looked-at)

---

## 1. Which OptiFine jar do I need?

The build listed for your Minecraft version in the README's compatibility table - that table, the list the
mod carries in `OptifineSupport`, and `release/notes/mc<MC>.md` are checked against each other by
`release/version.ps1 -CheckSupport`, so they cannot drift apart silently.

The version must match **exactly**: the jar contains the `official -> intermediary` mapping table for that
one release, and the mod reads `MC_VERSION` out of `net/optifine/Config.class` to check it. Get OptiFine from
[optifine.net](https://optifine.net/downloads) - that is the only source this mod ever points at.

An **installer** jar (the one with `patch/` inside) and an already-extracted jar are both accepted; the mod
tells them apart by looking for that `patch/` folder.

## 2. The game shows a dialog about OptiFine - what does it want?

It names the file, and there are only three cases:

| What it says | Why |
| --- | --- |
| No OptiFine found | Nothing in `mods/`, or in the launcher's shared `mods/`, or installed as a launcher version, declares the Minecraft release you are running. Shown on every launch. |
| An older preview is installed | The jar is a preview older than the newest build for that release. Once per build, remembered in `config/optifabric-mismatch-ack.txt`. |
| The newest build cannot load shaders | That build's own shader path throws during startup. See question 7. Once per build. |

Two jars for the same release is a **`DUPLICATED`** error and not a choice: one instance runs one OptiFine.

## 3. Why is Sodium refused, and why does the whole instance fail to start?

Because `breaks` is a **gate**, not a warning. Measured on Fabric Loader 0.19.5: a `conflicts` entry adds no
constraint at all (`ModSolver`'s `CONFLICTS` case is still a `// TODO`), so the game starts with
`Warnings were found!`; a `breaks` entry against a present mod makes the solver emit `NEG_HARD_DEP` and the
loader **refuses the instance**. The current artifact declares five `breaks` entries - `no_fog`, `thallium`,
`xradiation`, `ryoamiclights`, `sodium` - and no `conflicts`.

Sodium is there because the pairing was measured rather than assumed: on 1.21.1 with Sodium 0.8.13, with
every repairable gap closed, Sodium's mixins all apply and the client loads a world - and the frame stays
**black**. Handing the single renderer slot to Sodium instead, and turning OptiFine's Fast Render off, each
changed nothing. Two terrain renderers cannot share one pipeline, so the pairing is refused rather than
warned about: a warning leaves the user with a game that starts and never draws.

## 4. What about the rest of the Sodium family (Iris, Indium, Chloride, Sodium Extra...)?

They are not listed individually, because they do not need to be: they require Sodium
(`iris` declares `depends: sodium`, and the sweep's log has
`HARD_DEP chloride ... {depends sodium @ [>=0.8.12]}`), so `breaks: sodium` already refuses the instance
before those mods are reached. Adding them would only make the error list longer.

`moreculling` and `immediatelyfast` are **not** in that family - they do not require Sodium, and their
failures with OptiFine are our gaps, not Sodium's. They are tracked in
[`../COMPATIBILITY.md`](../COMPATIBILITY.md) like any other mod.

## 5. Can I use Photon? The sky and water flicker.

Not correctly on the OptiFine builds this line serves, and it is not something this mod can repair. What was
measured on 1.21.10 with `preview_OptiFine_1.21.10_HD_U_J7_pre11` and `photon_v1.2a.zip`:

* OptiFine logs **30 `Invalid program name`** lines and skips those programs. In one longer session the same
  names repeated to 405, so the count tracks time, not pack version.
* The skipped names are the ones Iris provides and OptiFine does not: `gbuffers_all_translucent`,
  `gbuffers_block_translucent`, `gbuffers_entities_translucent`, `gbuffers_particles_translucent`,
  `gbuffers_particles`, and Distant Horizons' `dh_water` / `dh_terrain`.
* Those programs are what writes `colortex3`, which the deferred pass and the blend pass then read. Nothing
  wrote it, so the frame is wrong - the sky and water flicker on the pack's own checkerboard cycle (16
  frames with `CLOUDS_TEMPORAL_UPSCALING 4`), and turning TAA on makes the whole image wrong because the bad
  data is accumulated into the history.

The same instance with `ComplementaryReimagined_r5.9.1.zip` has **0** `Invalid program name` lines and **0**
OpenGL errors. So: use a pack that supports OptiFine, or run Photon under Iris - which needs Sodium, and
therefore cannot be combined with this mod.

Since 2.2.12 the mod says this in the title-screen dialog once per pack and Minecraft version (remembered in
`config/optifabric-photon-ack.txt`). Upgrading the pack does not help: `photon_v1.3b` **adds**
`composite17` and `c17_copy_ao` and **removes nothing**.

## 6. My log is full of `Invalid program name` and `Error compiling vertex shader`. Is something broken?

Those two are log noise, and neither is a sign that the instance is broken:

* `Invalid program name` - the pack declares programs OptiFine does not have. OptiFine skips them and says
  so. The same 30 lines appear on 1.21, 1.21.3 and 1.21.4, which users report as working fine.
* `Error compiling vertex shader: /shaders/world0/prepare.vsh` (and `Invalid program "prepare"`) - the pack's
  own guard. `p0_clouds_prep.vsh` ends with `#ifndef CLOUD_SHADOWS` / `#error "This program should be
  disabled if Cloud Shadows are disabled"`. Iris disables the program as declared; OptiFine compiles it
  anyway, fails, and retries. Turning **Cloud Shadows** on in the pack's options removes the message - it is
  the pack's own default.

Other lines that are normal: `[OptiFine] (Reflector) Class not present: net.minecraftforge.*` and
`sun.misc.SharedSecrets` (OptiFine probing for Forge and old JDKs), `[Indigo] Different rendering plugin
detected`, `[Shaders] Unknown macro value: IRIS_VERSION`, and `Skipping bad option: lastServer`.

## 7. Shaders crash on 1.21.6 or 1.21.7.

Correct, and it is OptiFine's own code:

```
java.lang.NullPointerException: Cannot read field "norm" because "multiTex" is null
  at net.optifine.shaders.ShadersTex.initDynamicTextureNS(ShadersTex.java:322)
```

Not the pack (three unrelated packs crash at the same frame), not the patching. All seven OptiFine builds
for those two releases behave identically, so downgrading does not avoid it. The mod says so in the dialog
once per build. Play without a shader pack there, or move to a release whose newest OptiFine build is a
final one.

## 8. Is it compatible with Sinytra Connector (信雅互联) or Kilt?

No, and for Connector this is not a matter of fixing something - the two are architecturally exclusive:

* **Sinytra Connector** hosts Fabric mods inside (Neo)Forge. The game has already been transformed by
  Forge/NeoForge, while this mod needs the **untouched obfuscated vanilla client jar**: it drives OptiFine's
  own `optifine.Patcher` over that jar, then hands the result to **Fabric Loader's `GameTransformer`**. Under
  Connector the outer transform chain is NeoForge's, so bytes handed to Fabric's chain do not reach the game;
  and Connector loads game classes early, which pins them to vanilla - the one thing this mod cannot survive.
  **The correct setup on NeoForge is OptiFine's own Forge build** (optifine.net lists it per release, e.g.
  `Forge 61.0.8` for 1.21.11) plus Connector for your Fabric mods. OptiFabric has no role there.
* **Kilt** is itself a Fabric mod ("brings (Neo)Forge mods into the Fabric ecosystem"), so it would be loaded
  alongside this one. It transforms classes and loads game classes in order to run Forge mods, which
  conflicts with the same constraint. **Untested**, with a known conflict point - not a "no" from measurement.

Neither is listed in `fabric.mod.json`: `breaks` is an unconditional gate, and refusing an instance that
merely has OptiFabric installed (where it is inert without OptiFine) would be over-reach.

## 9. Does this mod download or bundle OptiFine?

**No, on both counts, in every artifact on the store.** Verified by opening the jars: no `net/optifine/*`
classes, no embedded jars, and no downloader classes. The only network-related strings are the
`optifine.net` URL in the prompt text and the docs.

The **`-full`** convenience builds published on GitHub do contain a downloader (8 classes) that fetches
OptiFine from optifine.net onto **your** machine - they still bundle nothing. Those builds are GitHub-only
because the platform asked for the runtime download to be removed from the released artifact.

## 10. Which mods are known to be incompatible?

This mod declares five ids under `breaks` (question 3). Everything else that was measured is in
[`../COMPATIBILITY.md`](../COMPATIBILITY.md) at the repository root, one row per mod, grouped by what
happened: 465 reached a world, 27 failed next to OptiFine (re-attributed to this side after checking),
8 were broken on plain Fabric too, 10 only reached the title screen, 8 had unresolved dependencies.

Those numbers come from one sweep on **1.21.1**. They have not been re-run since, so a mod listed there may
work now - the rows name the failure that was seen, not a permanent verdict.

## 11. Nothing changed after I upgraded.

Delete `<game dir>/.optifine/`. That folder holds the patched bytecode and the remapped OptiFine jar from the
previous run; the mod rebuilds it, and a change in the cache format number invalidates it by itself.

## 12. Where is the `[OptiFabric]` output? I cannot find it in `latest.log`.

It goes to the **launcher console**, which is usually not `logs/latest.log`. Filtering the console for
`[OptiFabric]` shows how many classes were prepared and how many Loader took over - a typical launch prints
about 127 such lines. The crash report is the other place: it gains an `OptiFabric` section with the OptiFine
version, the jar state and the mapped-jar path.

## 13. Why is the mod id `optifabric_reforged` and not `optifabric`?

Because some mods declare `"breaks": {"optifabric": "*"}` (Sodium does, and c2me does on the 1.20.6 line) and
Fabric Loader matches that by **id** - a display-name change would not be enough. On the 1.20.6 line the two
ids exist as two separate products, and **only one of them can be installed at a time**.

## 14. Does it work on a server? Does it work without Fabric API?

Client only (`"environment": "client"`), and it needs no Fabric API to start: its declared dependencies are
`fabricloader` and `minecraft`. Fabric API is tested with it and is what most modpacks will have, but its
absence is not an error path - the mod registers an inert placeholder where Fabric's renderer registry would
otherwise be empty.

## 15. Which Minecraft versions are actually verified, and what is not?

Ten releases, 1.21 through 1.21.11, each with its own jar and its own OptiFine build (question 1). What has
been verified per release is written down rather than summarised: the offline verification
(`VerifyPatched` with the JVM verifier, the ASM data-flow verifier, and the mixin-conflict scanners) plus a
captured launch log. What is *not* verified is stated too - for example, 1.21.6 and 1.21.7 cannot load a
shader pack at all, and the 1.20.6 line's "entered a world" step was never run, because no 1.20.6 save
existed on the machine it was tested on.

## 16. Can I use it in a development environment?

No. `gradlew runClient` is refused on purpose: a development run uses the `named` namespace, while this mod
patches the obfuscated client jar and serves patches keyed to that namespace. Making it work would need a
second remapping stage.

## 17. Was this written by AI?

Yes - the port was written and verified with AI assistance, and every README and release note says so at the
top. That is a reason to read the evidence rather than the claims: the numbers in the READMEs come from
captured logs and raw reports in this repository, and where something was not measured it is written as
"not measured".

## 18. How do I report a problem so that it can actually be looked at?

Include, in this order:

1. **The OptiFine jar name** and the **OptiFabric jar name** (the version is in the file name).
2. **The launcher console output**, filtered for `[OptiFabric]` - not `latest.log`, which does not contain it.
3. **The crash report**, if there is one - its `OptiFabric` section names the jar state.
4. **The mod list**, and whether the same setup fails without OptiFabric installed. A mod that also fails on
   plain Fabric is not this mod's problem, and that control is the fastest way to tell.
5. For a rendering problem: whether it also happens with shaders **off**, and with a different pack. That
   pair of controls is what separates a pack-side problem from a patching-side one.
