# FPS Test session — 2026-10-05T16:15:35.1454139+08:00

- Minecraft: `1.21.1`
- OS: `Windows 10 10.0 (amd64)`
- CPU cores: `16`
- Java: `22.0.2` (Java HotSpot(TM) 64-Bit Server VM)
- Max heap: `5836 MB`
- GPU: `AMD Radeon RX 7800 XT / 3.2.0 Core Profile Context 25.12.1.251128`

## Summary

| # | Benchmark | Cat. | Avg FPS | 1% low | 0.1% low | p99 frame ms | Tick avg ms | GC | Heap Δ MB |
|---:|---|---|---:|---:|---:|---:|---:|---:|---:|
| 1 | [Base FPS Benchmark (showcase)](#base-fps-benchmark-showcase) | Showcase | 5040.1 | 1097.8 | 713.4 | 0.58 | 0.89 | 60 | 794 |

## Details

### Base FPS Benchmark (showcase) (`base_fps_showcase`)

Category: **Showcase**  |  Duration: 194446 ms  |  Sample ticks: 3600

**FPS**  avg `5040.14`, min `302.23`, p50 `5254.86`, p95 `6131.21`, p99 `6222.78`, 1%low `1097.82`, 0.1%low `713.42`, std `963.79`

**Frame time (ms)**  avg `0.21`, p50 `0.19`, p95 `0.28`, p99 `0.58`, p99.9 `1.25`, max `3.31`

**Client tick (ms)**  avg `0.89`, p95 `1.03`, max `3.44`

**Memory**  start `4750 MB`, end `1274 MB`, peak `5544 MB`, GC `60 events / 3733 ms`

**FPS over sampling window (ASCII):**

```
5885.0 |    █        ██████                                                             
5704.8 |█████████   █████████████ ██████                                                
5524.6 |████████████████████████████████                                                
5344.3 |████████████████████████████████████ ██ ███                                     
5164.1 |████████████████████████████████████████████                                    
4983.9 |████████████████████████████████████████████████                                
4803.7 |██████████████████████████████████████████████████                              
4623.4 |███████████████████████████████████████████████████                 █           
4443.2 |███████████████████████████████████████████████████       ████     ██           
4263.0 |████████████████████████████████████████████████████      ████   █████  █     ██
4082.7 |███████████████████████████████████████████████████████  ███████████████████████
3902.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  180259
   1 ms |   734
   2 ms |   6
   3 ms |   1
```

**Extras:**

- `segment_plan` = `intro,plaza_orbit,forest_fly,forest_orbit,base_fly,base_orbit,village_fly,village,combat_in,combat_orbit,redstone,cave_fly,cave_inside,cave_pull,nether_fly,nether_orbit,end_fly,end_orbit,final`
- `segment_windows` = `intro=0-160,plaza_orbit=160-280,forest_fly=280-480,forest_orbit=480-680,base_fly=680-860,base_orbit=860-1080,village_fly=1080-1220,village=1220-1420,combat_in=1420-1520,combat_orbit=1520-1780,redstone=1780-1980,cave_fly=1980-2090,cave_inside=2090-2290,cave_pull=2290-2440,nether_fly=2440-2600,nether_orbit=2600-2840,end_fly=2840-3000,end_orbit=3000-3340,final=3340-3600`
- `preload_duration_ms` = `9.00`
- `entity_count_sample_start` = `59.00`
- `preset_long` = `0.00`
- `seed` = `27182.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `713.42`
- `terrain_area_blocks` = `43473.00`
- `other_entities_spawned` = `58.00`
- `blocks_placed` = `314075.00`
- `fps_harmonic_avg` = `4721.69`
- `segment_count` = `19.00`
- `fps_1pct_low` = `1097.82`
- `villagers_spawned` = `36.00`
- `entity_count_sample_end` = `80.00`
- `preset_quick` = `0.00`
- `preset_full` = `0.00`
- `trees_built` = `173.00`
- `entity_count_delta` = `21.00`

