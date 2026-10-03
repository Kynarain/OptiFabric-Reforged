# Upstream OptiFabric — probe record (evidence for the declared-incompatibility docs)

Written to back the upstream facts cited in `docs/COMPATIBILITY.md` / `COMPATIBILITY_CN.md` and in the new
"Declared incompatibilities" sections of the three lines' READMEs. The documentation pass could not verify them
because it had no network; they were read live from the GitHub contents API on 2026-10-03 and are recorded here
verbatim so the claims are auditable.

## 1. Repository and branches

`GET https://api.github.com/repos/Chocohead/OptiFabric` → `full_name: Chocohead/OptiFabric`,
`default_branch: llama`, 380 stars, license MPL-2.0.

`GET https://api.github.com/repos/Chocohead/OptiFabric/branches?per_page=100` → `alpaca`, `llama`, `master`,
`optisine`, `testing`, `vicuña`. (The names are camelids, not typos.)

## 2. Declared dependencies / conflicts / breaks, per branch

Read with `GET /repos/Chocohead/OptiFabric/contents/src/main/resources/fabric.mod.json?ref=<branch>`
(base64 content decoded; `raw.githubusercontent.com` is IPv4-reset on this machine, so the contents API is the
only working route here).

- `alpaca`, `master`, `optisine`, `vicuña` → `minecraft: 1.15.2`; **no `conflicts`, no `breaks`**.
- `llama` (= `testing`) → the multi-version line (1.16.1 … 1.20.4), `version: ${version}`, and:

```json
"depends": { "fabricloader": ">=0.8.0", "mm": ">=2.0" },
"conflicts": { "sodium": "*" },
"breaks": {
  "no_fog": "*",
  "thallium": "*",
  "xradiation": "*",
  "cardinal-components-item": "<2.4.2",
  "architectury": ">1.2.72 <1.3.77",
  "meteor-client": ">=0.4.1"
}
```

`gradle.properties` on that branch: `minecraft_version=1.16.5`, `mod_version = 1.14.3`.

So: the `sodium` conflict and the `no_fog` / `thallium` / `xradiation` breaks are **inherited** by this fork;
`ryoamiclights` is this fork's own addition; and `cardinal-components-item`, `architectury`, `meteor-client`
are the three range-limited entries this fork dropped (they only apply to 1.16/1.17-era builds).

## 3. What the loader actually does with `conflicts` / `breaks`

**The loader warns; it does not refuse.** Evidence from the sweep's own run:

`compat-matrix\logs\sodium\latest.log` — line 2 `Warnings were found!`, line 5 `Loading 56 mods:`,
with **zero** occurrences of `Incompatible mods found`, `Incompatible mod set` or `HARD_DEP`.
Corroborated by the comment in `compat-matrix\tools\analyze-failures.mjs` (lines 24-34).

Consequence for wording: `conflicts: {sodium: "*"}` is a **declared/known incompatibility**, not a refusal —
the game starts, and sodium then fails during Mixin application. This corrects the following claim:

> `compat-matrix\declared-incompat.json` → its `note` field says "conflicts is a hard refusal by the loader;
> breaks is a warning". **That is wrong** as measured above, and it was not propagated into the repository docs.

That file's `upstream` block is also stale: it was probed against `ref: master`, which (see §2) declares
neither `conflicts` nor `breaks`, so it records empty lists and contradicts §2. The `llama` branch is the one
that carries the list.

## 4. One-sided declaration after the fork rename

- This fork's id is `optifabric_reforged` in the 1.21.x and 26.x lines (`build.gradle`), and still `optifabric`
  in the 1.20.6 line.
- Sodium's own metadata declares `breaks` against the **old** id `optifabric`. On 1.21.x and 26.x that means the
  pairing is declared on **one side only** (ours), because the ids no longer match. On 1.20.6 the id-level
  relationship is intact; whether sodium's version range covers that line's builds was **not measured**.

## 5. Still not backed by a workspace file

`Exopandora/ShoulderSurfing#476` (the upstream pull request for the Camera/OptiFine mixin fix) is cited in
`docs/COMPATIBILITY.md` as context only. It is a real, open PR raised from the human's fork; it is not part of
the sweep and no sweep artifact references it.
