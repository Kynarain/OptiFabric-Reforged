# Not yet in a released jar

Everything listed here is on this branch and in no released artifact of this line. The list is the
commit range itself, produced with `git log <release commit>..HEAD`, so it cannot drift from the history;
the release commit each range starts at is named below.

- line: main/1.20.6
- last release commit on this line: `4ba6505`
- one of those commits, the help and issues link fix, has already been rebuilt and re-uploaded into
  every published asset of that release, so the jars do carry it: `2530c2f`
- everything else is branch only until the next version is cut

The fuller table for this port, with the reason and the verification for each fix, is in
`docs/UNRELEASED.md` on the 1.21.x branch; the entries that apply to more than one line are the
shaderpack slot guard, the delegating constructor shape check, the interface annotation form and the
synthetic field count guard.

## Commits

- `2530c2f` 2026-10-07 - Help link: the repository in the URL did not exist
- `7478537` 2026-10-07 - Help link: point at this line's instructions, not at 1.21.11's
- `bfabcf6` 2026-10-07 - This line gets the silent-catch lint, and the two catches it named get their reasons
- `ac08ad5` 2026-10-07 - Manual: correct the two rows my previous edit damaged
- `73cbc3f` 2026-10-07 - Manual: the -full row for 1.1.6 records the asset that is actually published
- `9f4c428` 2026-10-07 - The unit tests from the 1.21.x line, and the ClassCache resource split
- `d4a34df` 2026-10-07 - Metadata touches: contact points at this project, commons-lang3 is declared

## Checks before the next release

1. every fix above is in the built jars (compare the class files, not the commit list);
2. this file is replaced by a one line "merged into <version>" note, not left as it is;
3. a store/<line>-<version> snapshot branch is created at the release commit.

