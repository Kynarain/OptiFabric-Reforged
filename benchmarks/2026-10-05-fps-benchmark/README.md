# FPS Benchmark — raw results (2026-10-05)

Raw output of the third-party **FPS Benchmark** mod (`fpstest-1.0.jar`, sha256 `F11681914771E01A4677DA5EF217195FF523B01E9C3F463BF9A298D7BCCB2C56`) for the four configurations compared in this branch's README. Each group is one 3-minute scripted *Base* run (19 cinematic segments, deterministic seed `27182`).

| folder | mods |
|---|---|
| `A-vanilla-baseline/` | `fabric-api` + the benchmark |
| `C-OptiFabric-OptiFine/` | A + OptiFabric 2.2.10 + OptiFine_1.21.1_HD_U_J1 |
| `D-OptiFabric-OptiFine-LiFeC2ME/` | C + Lithium 0.15.4 + FerriteCore 7.0.3 + C2ME 0.4.0-alpha.0.29 |
| `B-Sodium-Lithium/` | A + Lithium 0.15.4 + Sodium 0.8.13 |

Files per folder:

- `report.md` — the mod's human-readable report (summary table + per-scene detail + ASCII graphs)
- `report.json` — machine-readable: every test, every metric, **every per-frame sample**
- `session.csv` — one row per session
- `system.json` — the environment the run recorded (MC, OS, CPU, Java, GPU)

`fps.csv` (≈7.5 MB per group, ~30 MB total) is **not** committed: it carries the same per-frame samples that `report.json` already contains. It stays in the local workspace (`I:\mods\OptiFabric-workspace\bench\results\`); ask if you want it added.

Environment shared by all four runs: Minecraft 1.21.1, Fabric Loader 0.19.5, Java 22.0.2, max heap 5836 MB, Intel i5-12600KF, AMD Radeon RX 7800 XT (driver 25.12.1.251128), Windows 10 10.0 amd64, render distance 8, VSync off, `maxFps:260` (= unlimited), shaders off, byte-identical `options.txt` across instances.