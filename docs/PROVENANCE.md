# Where this code comes from

What was ported from upstream OptiFabric, what was written for this fork, and where the boundary with
OptiFine lies. Every number here was measured against the files in this repository and against the
upstream source snapshot - none of it is estimated.

## The upstream

[Chocohead/OptiFabric](https://github.com/Chocohead/OptiFabric) by Modmuss50 and Chocohead, default branch
`llama`. The snapshot this port was written against declares `mod_version = 1.14.3` and
`minecraft_version = 1.16.5`, targets Java 8, and covers Minecraft 1.16.1 through 1.17.1 (including the
`1.17-alpha` / `1.17-beta` / `1.18-beta` builds) with a single jar.

The snapshot is kept locally at `reference/upstream/` and is **not** in this repository - `reference/` is in
`.gitignore` on purpose, since it is a working reference rather than a source of truth. It exists only in
the 26.x worktree at the time of writing, which is worth knowing before looking for it here.

## The file-level accounting

Measured by comparing the Java file names of the two trees (this line has 54, upstream has 54):

| | Count | Meaning |
| --- | --- | --- |
| Same name in both | **24** | Ported, then rewritten: `LambdaRebuilder`, `MethodComparison`, `ClassCache`, `ClassFixer`/`OptifineFixer`, the seven shared fixers, the four `util/` classes, `MixinTitleScreen`/`CrashReportMixin`, `OptifabricError`/`OptifabricSetup`/`OptifineInjector`/`OptifineSetup`/`OptifineVersion` |
| Only upstream | **30** | Not carried over - see below |
| Only here | **30** | Written for this line - see below |

### The 30 upstream files that were not carried over

* **The whole `compat/` layer (6)** - `InterceptingMixin`, `InterceptingMixinPlugin`, `PlacatingSurrogate`,
  `Shim`, `EmptyMixinPlugin`, `DevOnly`. Upstream's 1.16-era per-mod compatibility and early-riser
  machinery. Left out deliberately; bringing it back would mean rewriting the early-riser mechanism, and it
  is listed as a known gap rather than as something that was forgotten.
* **The contextual-mapping layer (4)** - `ContextualMapping`, `ContextualMappingContext`,
  `ContextualMappingProvider`, `IntermediaryContextTransformer`. Not dropped but **replaced**:
  `OptifineMappings` does the same job from rules instead of a hand-written table.
* **1.16-era screens, resources and registries (20)** - `Config`, `SMCLog`, `Text`, `TextSetup`,
  `Registries`, `RegistriesSetup`, `DrawContext`, `DrawContextSetup`, `Preloader`, `FeatureFinder`,
  `OptifineResources`, `OptifabricLoadGuard`, and the seven mixins aimed at screens and resource packs
  (`ShadersMixin`, `VideoOptionsScreenMixin`, `CustomColoursMixin`, `DefaultResourcePackMixin`,
  `MixinOptifineConfig`, `MixinReflectorClass`, `ParticleManagerMixin`).

The shape difference is the point: upstream carries **12 mixins** and **9 fixers**; this line carries
**2 mixins** and **27 fixers**. Upstream patched screens by mixing into them; this line hands patched
bytecode to Loader's game transformer before Mixin runs and repairs what OptiFine's recompile damaged.

### The 30 files written for this line

The pipeline and its plumbing - `Optifabric`, `OptifabricRuntime`, `OptifabricSetup`, `OptifineRuntime`,
`GameTransformerHook`, `MixinClassMetadata`, `OptifabricError` - the OptiFine-facing pieces -
`OptifineSearch`, `OptifineVersion`, `OptifineSupport`, `OptifinePrompt`, `OptifineJarFixer`,
`OptifineMappings` - the renderer wiring - `RendererApiFallback`, `RendererApiStubGenerator` - and the
fixers that have no upstream counterpart: `AddInterfaceFix`, `InjectionCallPointFix`,
`InjectionFieldPointFix`, `ObjectCreationPointFix`, `StubInjectionTargetFix`, `RestoreVanillaMethodsFix`,
`RestoreVanillaCallFix`, `RestoreSiblingCallFix`, `MissingOverrideFix`, `LambdaMethodRefFix`,
`CallSiteRedirectFix`, `VanillaFactoryCallFix`, `DelegatingConstructorFix`, `LocalSlotLayoutFix`, and the
rest of `patcher/fixes/` that the name comparison lists as new.

### How a file says which it is

Two headers, applied by hand:

```java
/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * ...
 */
```

```java
/*
 * New in this release of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 */
```

The second form sometimes names the release it arrived in, and sometimes says which line it was ported
from - `AddInterfaceFix`, for instance, records that it came from this repository's 26.x line. **Not every
file carries one of these headers yet**; the ones that do are the ones to trust for provenance, and adding
the rest is a known gap rather than a claim that the untagged files have no origin.

## The OptiFine boundary

This is the part a licence review asks about, so it is stated precisely and with the evidence behind it.

| | |
| --- | --- |
| **Bundled** | Nothing. Opening every released jar entry by entry found **no** `net/optifine/*` classes, **no** embedded jars, and **no** downloader classes. |
| **Redistributed** | Nothing. OptiFine is never copied into this repository, into a build, or into a release asset. |
| **Third-party mirrors** | None. `optifine.net` is the only source this project ever names; the download page is linked, and no mirror is offered. |
| **Read at runtime** | The user's own jar, from `mods/` or a launcher version, opened as a local `file:` and patched in memory. Nothing is fetched. |
| **Downloaded by the mod** | Not in any store artifact. The GitHub-only `-full` builds contain an 8-class downloader that fetches OptiFine from optifine.net onto the user's machine; they still bundle nothing. The released artifact had that removed at the platform's request. |

The upstream project is **MPL-2.0** and so is this one (`LICENSE.txt`); ported files keep their origin
headers, and the upstream `LICENSE` is the same licence under a different file name. OptiFine itself is
sp614x's work and is neither included nor redistributed here.

## What this project adds beyond porting

* **One jar per Minecraft release.** Where upstream served 1.16.1-1.17.1 from one artifact through a
  runtime contextual-mapping layer, this line builds ten artifacts, each with its own mapping table inside.
* **The bytecode fixers.** 27 of them, against upstream's 9. Each one exists because a specific OptiFine
  recompile damaged a specific injection point on a specific release; the class-level comments record which.
* **A verification toolchain.** `VerifyPatched` (the JVM verifier plus an ASM data-flow verifier over every
  patched class and every OptiFine class), the mixin-conflict scanners (`AtTargetScan`, `RefmapScan`,
  `RuntimeContractScan`, `InjectionScan`, `LambdaScan`, `LocalsScan`, `ShadowScan`, and the rest),
  `CheckFixerIds`, and one captured launch log per release. **This toolchain is not in this repository yet** -
  it lives in the 26.x worktree - which is a known gap: a fresh clone cannot reproduce a verification run.
* **The notice and error path.** `OptifinePrompt` with its three OptiFine cases plus the Photon notice, the
  single title-screen dialog, and the `OptiFabric` section in the crash report.
* **The offline lint.** `tools/check-silent-catches.ps1` with `.github/workflows/checks.yml`: a catch block
  must either speak or say why it is silent.
