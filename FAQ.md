# Frequently asked questions

[🇬🇧 English](./FAQ.md) | [🇨🇳 中文版](./FAQ_CN.md)

This file answers what is true for **every** release line in this repository. Question 1 tells you which
line you are on and where that line's detailed FAQ lives; questions 2 to 10 have the same answer everywhere,
and each one is backed by something you can check in this repository. Where a question has no measured
answer, it says so instead of guessing.

**Contents**

1. [Which line am I on, and where is its detailed FAQ?](#1-which-line-am-i-on-and-where-is-its-detailed-faq)
2. [Which OptiFine jar do I need?](#2-which-optifine-jar-do-i-need)
3. [Does this mod download or bundle OptiFine?](#3-does-this-mod-download-or-bundle-optifine)
4. [Why is Sodium refused, and why does the whole instance fail to start?](#4-why-is-sodium-refused-and-why-does-the-whole-instance-fail-to-start)
5. [Why is the mod id `optifabric_reforged` and not `optifabric`?](#5-why-is-the-mod-id-optifabric_reforged-and-not-optifabric)
6. [Does it work on a server? Does it work without Fabric API?](#6-does-it-work-on-a-server-does-it-work-without-fabric-api)
7. [Nothing changed after I upgraded.](#7-nothing-changed-after-i-upgraded)
8. [Where is the `[OptiFabric]` output? I cannot find it in `latest.log`.](#8-where-is-the-optifabric-output-i-cannot-find-it-in-latestlog)
9. [Can I use it in a development environment?](#9-can-i-use-it-in-a-development-environment)
10. [How do I report a problem so that it can actually be looked at?](#10-how-do-i-report-a-problem-so-that-it-can-actually-be-looked-at)

---

## 1. Which line am I on, and where is its detailed FAQ?

Each release line is its own branch with its own jar, its own OptiFine build and its own documents.

| Branch | Minecraft | Mod id | Java | Detailed FAQ |
| --- | --- | --- | --- | --- |
| `26.x` | 26.2, 26.1.2 | `optifabric_reforged` | 25 | this repository's `README.md` and `docs/OPTIFINE_VERSIONS.md` |
| `1.21.x` | 1.21 through 1.21.11 (10 releases) | `optifabric_reforged` | 21 or newer | `docs/FAQ.md` and `docs/FAQ_CN.md` on that branch |
| `main` | 1.20.6 | `optifabric` | 21 or newer | `docs/FAQ.md` on that branch |
| `wip/1.20.6-reforged` | 1.20.6 | `optifabric_reforged` | 21 or newer | `docs/FAQ.md` on that branch |

On the 1.20.6 releases the two ids exist as two separate products, and **only one of them can be installed
at a time**.

## 2. Which OptiFine jar do I need?

The build listed for your Minecraft version in that line's README compatibility table - the same list lives in
`docs/OPTIFINE_VERSIONS.md`, and the mod carries it in `OptifineSupport`.

The version must match **exactly**: the jar carries the mapping table for that one release and the mod reads
`MC_VERSION` out of `net/optifine/Config.class` to check it. Get OptiFine from
[optifine.net](https://optifine.net/downloads) - that is the only source this mod ever points at.

Both the **installer** jar (the one with `patch/` inside) and an already-extracted jar are accepted; the mod
tells them apart by looking for that `patch/` folder. Two jars for the same release is a `DUPLICATED` error and
not a choice: one instance runs one OptiFine.

## 3. Does this mod download or bundle OptiFine?

**No, on both counts, in every artifact published to the stores.** Verified by opening the jars: no
`net/optifine/*` classes, no embedded jars, no downloader classes. The only network-related strings are the
`optifine.net` URL in the prompt text and in the docs.

The **`-full`** convenience builds published on GitHub do contain a downloader that fetches OptiFine from
optifine.net onto **your** machine - they still bundle nothing. See `docs/DOWNLOAD_INTEGRITY.md` on your line.

## 4. Why is Sodium refused, and why does the whole instance fail to start?

Because `breaks` is a **gate**, not a warning. Measured on Fabric Loader 0.19.5: a `conflicts` entry adds no
constraint at all, so the game starts with `Warnings were found!`; a `breaks` entry against a present mod makes
the solver emit `NEG_HARD_DEP` and the loader **refuses the instance**.

Every artifact in this repository declares the same five ids - `no_fog`, `thallium`, `xradiation`,
`ryoamiclights`, `sodium` - so that is the error you see.

Sodium is there because the pairing was measured rather than assumed: with every repairable gap closed,
Sodium's mixins all apply and the client loads a world - and the frame stays **black**. Two terrain renderers
cannot share one pipeline, so the pairing is refused rather than warned about: a warning leaves the user with a
game that starts and never draws. Iris, Indium, Chloride and Sodium Extra need not be listed individually
because they require Sodium.

## 5. Why is the mod id `optifabric_reforged` and not `optifabric`?

Because some mods declare `"breaks": {"optifabric": "*"}` and Fabric Loader matches that by **id** - a
display-name change would not be enough. On the 1.20.6 releases the two ids exist as two separate products and
only one can be installed at a time.

## 6. Does it work on a server? Does it work without Fabric API?

Client only (`"environment": "client"`), and it needs no Fabric API to start: the declared dependencies are
`fabricloader` and `minecraft`. Fabric API is tested with it and is what most modpacks will have, but its
absence is not an error path.

## 7. Nothing changed after I upgraded.

Delete `<game dir>/.optifine/`. That folder holds the patched bytecode and the remapped OptiFine jar from the
previous run; the mod rebuilds it, and a change in the cache format number invalidates it by itself.

## 8. Where is the `[OptiFabric]` output? I cannot find it in `latest.log`.

It goes to the **launcher console**, which is usually not `logs/latest.log`. Filtering the console for
`[OptiFabric]` shows how many classes were prepared and how many Loader took over. The crash report is the
other place: it gains an `OptiFabric` section with the OptiFine version, the jar state and the mapped-jar path.

## 9. Can I use it in a development environment?

No. `gradlew runClient` is refused on purpose on every line: a development run uses the `named` namespace,
while this mod patches the obfuscated client jar and serves patches keyed to that namespace. Making it work
would need a second remapping stage.

## 10. How do I report a problem so that it can actually be looked at?

Open a **compatibility issue** if another mod is involved, and a **bug report** if it is not; both forms ask for
the same core evidence, in this order:

1. **The OptiFine jar name** and the **OptiFabric jar name** (the version is in the file name).
2. **The launcher console output**, filtered for `[OptiFabric]` - not `latest.log`, which does not contain it.
3. **The crash report**, if there is one - its `OptiFabric` section names the jar state. For a startup failure
   the crash report carries the stack that `latest.log` stops short of.
4. **The mod list**, and whether the same setup fails without OptiFabric installed. A mod that also fails on
   plain Fabric is not this mod's problem, and that control is the fastest way to tell.
5. For a rendering problem: whether it also happens with shaders **off**, and with a different pack.
