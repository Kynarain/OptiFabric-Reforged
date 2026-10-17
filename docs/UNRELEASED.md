# Not yet in a released jar

Nothing: everything listed here before was released as 2.2.14 on 2026-10-07, and this file is kept as the
place where the next round of work is recorded.

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

## The download hang fix, reworked after review

The first version buffered the whole body inside `send()` so the three minute request timeout covered it. That fixed the
hang but made the size limit a check that runs after the memory is already committed, put a three minute ceiling on a
transfer that is merely slow, and dropped the progress lines. The body is read as it arrives again, so the limit stops an
oversized response at once and progress is reported while it downloads; a response whose `Content-Length` is already too
large is refused before a byte is read; and a watchdog closes the stream after sixty seconds without a byte, which makes
the read throw instead of hang. A slow but steady transfer is never cut off that way, only one that has stopped.

## A stalled download now says so (after the released builds)

Closing the stream from the watchdog made the blocked read throw, but the exception carried the transport's own word for
it - `Stream closed` - which tells the user nothing. The read now reports `the download stopped sending data for 60 s,
giving up; check the connection and try again`. The response stream is also closed exactly once: the guard owns it.

## Released

The stall message and the single close were published on 2026-10-17: 2.2.16 (1.21.x, ten targets), 2.2.11 (26.x, both
targets) and 1.1.9 (both 1.20.6 lines). Their -full jars carry the message; the default jars differ only in the version
string, because they do not contain the downloader.