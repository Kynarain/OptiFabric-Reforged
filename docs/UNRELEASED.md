# Not yet in a released jar

Everything listed here is on this branch and in no released artifact of this line. The list is the
commit range itself, produced with `git log <release commit>..HEAD`, so it cannot drift from the history;
the release commit each range starts at is named below.

- line: 26.x
- last release commit on this line: `e903703`
- one of those commits, the help and issues link fix, has already been rebuilt and re-uploaded into
  every published asset of that release, so the jars do carry it: `d0b61f6`
- everything else is branch only until the next version is cut

The fuller table for this port, with the reason and the verification for each fix, is in
`docs/UNRELEASED.md` on the 1.21.x branch; the entries that apply to more than one line are the
shaderpack slot guard, the delegating constructor shape check, the interface annotation form and the
synthetic field count guard.

## Commits

- `6122e6e` 2026-10-07 - Record why this line does not repeat the RendererAccess fallback
- `87ea418` 2026-10-07 - AddInterfaceFix: read the annotation form that names its target as a class
- `0aa7b04` 2026-10-07 - Two of the smaller gaps the review left open, closed
- `d099b49` 2026-10-07 - Records: the sizes and hashes of the refreshed assets
- `d0b61f6` 2026-10-07 - Help link: the repository in the URL did not exist
- `a37fac3` 2026-10-07 - The unit tests from the 1.21.x line, and the ClassCache resource split
- `3ba9bca` 2026-10-07 - Metadata touches: contact points at this project, commons-lang3 is declared

## Checks before the next release

1. every fix above is in the built jars (compare the class files, not the commit list);
2. this file is replaced by a one line "merged into <version>" note, not left as it is;
3. a store/<line>-<version> snapshot branch is created at the release commit.

