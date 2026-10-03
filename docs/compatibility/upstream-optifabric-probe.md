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

**The two fields do different things: `conflicts` warns, `breaks` is enforced. This section was wrong before
and is corrected here.**

What the earlier sweep run actually proves: `compat-matrix\logs\sodium\latest.log` — line 2 `Warnings were
found!`, line 5 `Loading 56 mods:`, with **zero** occurrences of `Incompatible mods found`, `Incompatible mod
set` or `HARD_DEP`. That run predates the `breaks` entry, so it measures **`conflicts` only**:

- **`conflicts` → warning, no refusal.** In Fabric Loader 0.19.5's `ModSolver`, the `CONFLICTS` case in both
  `generatePreselectConstraints` and `generateMainConstraints` still carries only a
  `// TODO: soft negative dep?` comment and adds **no constraint at all**. The combination is therefore never
  refused; `conflicts` is a **declared/known incompatibility**, the game starts, and sodium then fails during
  Mixin application.
- **`breaks` → enforced, hard refusal.** The `BREAKS` case in the same methods is a strong negative dep
  (`dependencyHelper.implication(mod).impliesNot(match)`, explanation kind `NEG_HARD_DEP`), and
  `hasAllDepsSatisfied` rejects a mod whose `breaks` matches a present one. A recorded run logged
  `NEG_HARD_DEP optifabric_reforged 2.2.2 {breaks sodium}` — when `breaks` names a mod that is present, the
  loader **refuses** the combination.

This corrects the following claim, and it was the wrong correction in the opposite direction:

> `compat-matrix\declared-incompat.json` → its `note` field says "conflicts is a hard refusal by the loader;
> breaks is a warning". **That is backwards.** The fields are the other way round, as measured above and read
> off Loader 0.19.5's `ModSolver`. (That claim was, however, correctly *not* propagated into the repository
> docs when it was first identified; what those docs then asserted instead — that both fields merely warn — is
> what this section now fixes.)

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
