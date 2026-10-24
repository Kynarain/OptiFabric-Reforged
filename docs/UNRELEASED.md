# Not yet in a released jar

Nothing: everything listed here before was released as 2.2.9 on 2026-10-07.

The timeout fix below was released on this line as **2.2.10** on 2026-10-17 (the -full jar of 2.2.10 carries it). It is
still pending for the 1.21.x line, which has not shipped it yet.

## The OptiFine download can no longer hang forever (all twelve branches with a downloader)

Found on a machine whose network throttles optifine.net: the -full build fetched the download page, got its
token, read 3076 KiB of the 7424 KiB jar and then blocked in the read of the response body for 24 minutes, with
the game sitting in preLaunch and no window ever appearing. The cause is that HttpRequest.timeout ends when the
response is received, not when its body is finished, and the body was being read from the stream handed back by
BodyHandlers.ofInputStream. The body is now buffered inside send(), so the three minute request timeout covers
the whole exchange; a timeout explains itself and says that putting OptiFine into mods is the way around it.

Verified by reading the built jar: OptifineDownloader.class reads the body as it arrives (BodyHandlers.ofInputStream) behind a stall watchdog, no longer
references ofInputStream, and carries both HttpTimeoutException and the message.

Commits: 1.21.x-2.2.11 eb8031c, -2.2.12 bf32a5f, -2.2.13 7653105, -2.2.14 0627135, 26.x-2.2.7 d17b6a8,
-2.2.8 7debcdc, -2.2.9 f2a4b69, 1.20.6-reforged-1.1.5 ba9e996, -1.1.6 dc30672, -1.1.7 7938abd,
1.20.6-1.1.6 6e08de3, -1.1.7 6b17abf. Not yet in any published jar.

## A stalled download now says so (after the released builds)

Closing the stream from the watchdog made the blocked read throw, but the exception carried the transport's own word for
it - `Stream closed` - which tells the user nothing. The read now reports `the download stopped sending data for 60 s,
giving up; check the connection and try again`. The response stream is also closed exactly once: the guard owns it.

## Released

The stall message and the single close were published on 2026-10-17: 2.2.16 (1.21.x, ten targets), 2.2.11 (26.x, both
targets) and 1.1.9 (both 1.20.6 lines). Their -full jars carry the message; the default jars differ only in the version
string, because they do not contain the downloader.

## OptiFine 26.2 `K2_pre5` is recorded, not yet recommended

OptiFine published `preview_OptiFine_26.2_HD_U_K2_pre5.jar` on 2026-10-20 (7,849,368 bytes, sha256
`db8ddfdc045742c53fcda95fee21ca5a4ca9fcbc370238a1f78c5e92a1cee5d2`). It is in the `-full` hash table on the 26.x
build branches, so the downloader accepts it.

Offline, with pre5 as the installed build: `Prepared 571 patched classes (0 skipped, 0 failed)`, `verified OK: 571`,
`FAILED: 0`, `ASM verifier problems: 0`; OptiFine's own classes `879 OK, 0 failed, 0 ASM`; `AtTargetScan PROBLEMS: 0`;
`RefmapScan MISSING members: 0`; `RuntimeContractScan` broken contracts 0, lost overrides 0; `LambdaScan DANGLING
handles: 0` - the same shape as pre4.

It is also worth knowing what pre5 actually is. Compared entry by entry, its 7,382 entries have the same content as
pre4's - every uncompressed entry hashes the same, and the two files differ only in the zip local header's timestamp
field. The publisher's changelog lists one more line for pre5 (`fixed crash in Options and BlockUtils #2`), but that
change is not in the uploaded jar. So installing pre5 gains nothing over pre4 beyond accepting the newer file name,
and there is no reason to prefer it as the recommended build.

Not done, on purpose: the support table still names pre4 for 26.2, and no released jar carries this entry. Making pre5
the recommended build needs one thing the offline run cannot give - a hand-started game that reaches a world on it.