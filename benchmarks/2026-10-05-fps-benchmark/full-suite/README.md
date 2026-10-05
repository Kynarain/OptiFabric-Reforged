# Full suite — 41 scripted tests, four configurations (2026-10-05)

Raw output of the third-party **FPS Benchmark** mod (`fpstest-1.0`, sha256 `F11681914771E01A4677DA5EF217195FF523B01E9C3F463BF9A298D7BCCB2C56`) running its **full 41-test suite** (particles, entities, physics, redstone, fluids, lighting, block entities, entity AI, static render, 12-biome flybys) once per configuration, on the same machine with a byte-identical `options.txt`.

| folder | mods |
|---|---|
| `A-vanilla-baseline/` | fabric-api + the benchmark |
| `C-OptiFabric-OptiFine/` | A + OptiFabric 2.2.10 + OptiFine_1.21.1_HD_U_J1 |
| `D-OptiFabric-OptiFine-LiFeC2ME/` | C + Lithium 0.15.4 + FerriteCore 7.0.3 + C2ME 0.4.0-alpha.0.29 |
| `B-Sodium-Lithium/` | A + Lithium 0.15.4 + Sodium 0.8.13 |

Each folder holds the mod's own `report.md` (per-test summary table and detail), `report.json` (every metric and every per-frame sample), `session.csv` and `system.json`. `fps.csv` (~7.5 MB per configuration) is not committed because it duplicates the per-frame samples already inside `report.json`.

`comparison-4groups.csv` joins all 41 tests across the four configurations (columns `A_avg`, `A_l1`, `B_avg`, `B_l1`, `C_avg`, `C_l1`, `D_avg`, `D_l1`).

Environment shared by all four: Minecraft 1.21.1, Fabric Loader 0.19.5, Java 22.0.2, max heap 5836 MB, Intel i5-12600KF, AMD Radeon RX 7800 XT (driver 25.12.1.251128), Windows 10, render distance 8, VSync off, maxFps 260 (= unlimited), shaders off, seed 27182.