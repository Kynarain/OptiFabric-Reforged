# FPS Test session — 2026-10-05T16:09:25.8203995+08:00

- Minecraft: `1.21.1`
- OS: `Windows 10 10.0 (amd64)`
- CPU cores: `16`
- Java: `22.0.2` (Java HotSpot(TM) 64-Bit Server VM)
- Max heap: `5836 MB`
- GPU: `AMD Radeon RX 7800 XT / 3.2.0 Core Profile Context 25.12.1.251128`

## Summary

| # | Benchmark | Cat. | Avg FPS | 1% low | 0.1% low | p99 frame ms | Tick avg ms | GC | Heap Δ MB |
|---:|---|---|---:|---:|---:|---:|---:|---:|---:|
| 1 | [Base FPS Benchmark (showcase)](#base-fps-benchmark-showcase) | Showcase | 5680.0 | 1016.7 | 349.2 | 0.50 | 0.84 | 70 | 1800 |

## Details

### Base FPS Benchmark (showcase) (`base_fps_showcase`)

Category: **Showcase**  |  Duration: 194145 ms  |  Sample ticks: 3600

**FPS**  avg `5679.97`, min `80.20`, p50 `5717.55`, p95 `7541.76`, p99 `7836.99`, 1%low `1016.67`, 0.1%low `349.24`, std `1176.90`

**Frame time (ms)**  avg `0.19`, p50 `0.17`, p95 `0.28`, p99 `0.50`, p99.9 `1.44`, max `12.47`

**Client tick (ms)**  avg `0.84`, p95 `0.99`, max `3.40`

**Memory**  start `3744 MB`, end `738 MB`, peak `5544 MB`, GC `70 events / 3487 ms`

**FPS over sampling window (ASCII):**

```
6345.1 |█ ██████                                                                        
6101.4 |██████████████████████ █ ███                                                    
5857.7 |██████████████████████████████████  ██████████              ██ █                
5614.0 |███████████████████████████████████████████████       █     █████              █
5370.3 |███████████████████████████████████████████████      ████ █████████ ███ █ ██ ███
5126.6 |████████████████████████████████████████████████    █████ ██████████████████████
4883.0 |████████████████████████████████████████████████    ████████████████████████████
4639.3 |████████████████████████████████████████████████    ████████████████████████████
4395.6 |████████████████████████████████████████████████   █████████████████████████████
4151.9 |█████████████████████████████████████████████████  █████████████████████████████
3908.2 |█████████████████████████████████████████████████ ██████████████████████████████
3664.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  180374
   1 ms |   505
   2 ms |   64
   3 ms |   24
   4 ms |   22
   5 ms |   5
   6 ms |   3
   7 ms |   1
  12 ms |   2
```

**Extras:**

- `segment_plan` = `intro,plaza_orbit,forest_fly,forest_orbit,base_fly,base_orbit,village_fly,village,combat_in,combat_orbit,redstone,cave_fly,cave_inside,cave_pull,nether_fly,nether_orbit,end_fly,end_orbit,final`
- `segment_windows` = `intro=0-160,plaza_orbit=160-280,forest_fly=280-480,forest_orbit=480-680,base_fly=680-860,base_orbit=860-1080,village_fly=1080-1220,village=1220-1420,combat_in=1420-1520,combat_orbit=1520-1780,redstone=1780-1980,cave_fly=1980-2090,cave_inside=2090-2290,cave_pull=2290-2440,nether_fly=2440-2600,nether_orbit=2600-2840,end_fly=2840-3000,end_orbit=3000-3340,final=3340-3600`
- `preload_duration_ms` = `8.00`
- `entity_count_sample_start` = `59.00`
- `preset_long` = `0.00`
- `seed` = `27182.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `349.24`
- `terrain_area_blocks` = `43473.00`
- `other_entities_spawned` = `58.00`
- `blocks_placed` = `314075.00`
- `fps_harmonic_avg` = `5244.81`
- `segment_count` = `19.00`
- `fps_1pct_low` = `1016.67`
- `villagers_spawned` = `36.00`
- `entity_count_sample_end` = `77.00`
- `preset_quick` = `0.00`
- `preset_full` = `0.00`
- `trees_built` = `173.00`
- `entity_count_delta` = `18.00`

