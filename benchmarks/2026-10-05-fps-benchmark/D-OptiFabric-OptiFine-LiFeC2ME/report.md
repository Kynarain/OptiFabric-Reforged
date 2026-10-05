# FPS Test session — 2026-10-05T16:04:30.3955011+08:00

- Minecraft: `1.21.1`
- OS: `Windows 10 10.0 (amd64)`
- CPU cores: `16`
- Java: `22.0.2` (Java HotSpot(TM) 64-Bit Server VM)
- Max heap: `5836 MB`
- GPU: `AMD Radeon RX 7800 XT / 3.2.0 Core Profile Context 25.12.1.251128`

## Summary

| # | Benchmark | Cat. | Avg FPS | 1% low | 0.1% low | p99 frame ms | Tick avg ms | GC | Heap Δ MB |
|---:|---|---|---:|---:|---:|---:|---:|---:|---:|
| 1 | [Base FPS Benchmark (showcase)](#base-fps-benchmark-showcase) | Showcase | 4475.1 | 1077.0 | 721.2 | 0.63 | 0.79 | 58 | 3864 |

## Details

### Base FPS Benchmark (showcase) (`base_fps_showcase`)

Category: **Showcase**  |  Duration: 194543 ms  |  Sample ticks: 3600

**FPS**  avg `4475.06`, min `318.76`, p50 `4557.89`, p95 `5336.18`, p99 `5446.62`, 1%low `1076.96`, 0.1%low `721.25`, std `746.89`

**Frame time (ms)**  avg `0.24`, p50 `0.22`, p95 `0.31`, p99 `0.63`, p99.9 `1.20`, max `3.14`

**Client tick (ms)**  avg `0.79`, p95 `0.91`, max `3.02`

**Memory**  start `1692 MB`, end `3404 MB`, peak `5556 MB`, GC `58 events / 3293 ms`

**FPS over sampling window (ASCII):**

```
5085.1 |               ██        █  ██                                                  
4933.5 | ████          █████████████████                                                
4781.8 | █████     ██ ██████████████████████       █                                    
4630.2 |████████████████████████████████████ ████████                                   
4478.6 |██████████████████████████████████████████████ █                                
4326.9 |██████████████████████████████████████████████████  █           ██           █  
4175.3 |█████████████████████████████████████████████████████      █  ████   █     ███  
4023.7 |█████████████████████████████████████████████████████     ██ █████  ███ ████████
3872.0 |█████████████████████████████████████████████████████     ██████████████████████
3720.4 |█████████████████████████████████████████████████████  █  ██████████████████████
3568.8 |██████████████████████████████████████████████████████ █  ██████████████████████
3417.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  180245
   1 ms |   748
   2 ms |   6
   3 ms |   1
```

**Extras:**

- `segment_windows` = `intro=0-160,plaza_orbit=160-280,forest_fly=280-480,forest_orbit=480-680,base_fly=680-860,base_orbit=860-1080,village_fly=1080-1220,village=1220-1420,combat_in=1420-1520,combat_orbit=1520-1780,redstone=1780-1980,cave_fly=1980-2090,cave_inside=2090-2290,cave_pull=2290-2440,nether_fly=2440-2600,nether_orbit=2600-2840,end_fly=2840-3000,end_orbit=3000-3340,final=3340-3600`
- `segment_plan` = `intro,plaza_orbit,forest_fly,forest_orbit,base_fly,base_orbit,village_fly,village,combat_in,combat_orbit,redstone,cave_fly,cave_inside,cave_pull,nether_fly,nether_orbit,end_fly,end_orbit,final`
- `segment_count` = `19.00`
- `fps_1pct_low` = `1076.96`
- `villagers_spawned` = `36.00`
- `entity_count_sample_end` = `78.00`
- `preset_quick` = `0.00`
- `preset_full` = `0.00`
- `trees_built` = `173.00`
- `entity_count_delta` = `19.00`
- `preload_duration_ms` = `6.00`
- `entity_count_sample_start` = `59.00`
- `preset_long` = `0.00`
- `seed` = `27182.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `721.25`
- `terrain_area_blocks` = `43473.00`
- `other_entities_spawned` = `58.00`
- `blocks_placed` = `314075.00`
- `fps_harmonic_avg` = `4244.76`

