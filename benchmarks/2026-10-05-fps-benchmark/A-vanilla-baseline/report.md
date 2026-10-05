# FPS Test session — 2026-10-05T16:20:29.976317+08:00

- Minecraft: `1.21.1`
- OS: `Windows 10 10.0 (amd64)`
- CPU cores: `16`
- Java: `22.0.2` (Java HotSpot(TM) 64-Bit Server VM)
- Max heap: `5836 MB`
- GPU: `AMD Radeon RX 7800 XT / 3.2.0 Core Profile Context 25.12.1.251128`

## Summary

| # | Benchmark | Cat. | Avg FPS | 1% low | 0.1% low | p99 frame ms | Tick avg ms | GC | Heap Δ MB |
|---:|---|---|---:|---:|---:|---:|---:|---:|---:|
| 1 | [Base FPS Benchmark (showcase)](#base-fps-benchmark-showcase) | Showcase | 3630.6 | 773.6 | 441.5 | 0.76 | 1.06 | 98 | 3330 |

## Details

### Base FPS Benchmark (showcase) (`base_fps_showcase`)

Category: **Showcase**  |  Duration: 194246 ms  |  Sample ticks: 3600

**FPS**  avg `3630.64`, min `58.09`, p50 `3735.52`, p95 `4541.33`, p99 `4664.18`, 1%low `773.63`, 0.1%low `441.52`, std `706.01`

**Frame time (ms)**  avg `0.29`, p50 `0.27`, p95 `0.41`, p99 `0.76`, p99.9 `1.68`, max `17.21`

**Client tick (ms)**  avg `1.06`, p95 `1.27`, max `4.62`

**Memory**  start `2210 MB`, end `4816 MB`, peak `5540 MB`, GC `98 events / 3045 ms`

**FPS over sampling window (ASCII):**

```
4343.9 |                           ██ ██                                                
4169.0 |                  █    ██████████████                                           
3994.2 |      █         █████████████████████████                                       
3819.3 |     ███      ███████████████████████████████████  █  █                         
3644.5 |     █████████████████████████████████████████████████████                      
3469.6 |    ███████████████████████████████████████████████████████        █            
3294.8 |    ████████████████████████████████████████████████████████    ████████        
3119.9 |    ████████████████████████████████████████████████████████   ████████████   █ 
2945.1 |    ████████████████████████████████████████████████████████   █████████████████
2770.2 |   ██████████████████████████████████████████████████████████ ██████████████████
2595.4 |█████████████████████████████████████████████████████████████ ██████████████████
2420.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  179785
   1 ms |   1154
   2 ms |   42
   3 ms |   10
   4 ms |   5
   5 ms |   2
  13 ms |   1
  17 ms |   1
```

**Extras:**

- `segment_plan` = `intro,plaza_orbit,forest_fly,forest_orbit,base_fly,base_orbit,village_fly,village,combat_in,combat_orbit,redstone,cave_fly,cave_inside,cave_pull,nether_fly,nether_orbit,end_fly,end_orbit,final`
- `segment_windows` = `intro=0-160,plaza_orbit=160-280,forest_fly=280-480,forest_orbit=480-680,base_fly=680-860,base_orbit=860-1080,village_fly=1080-1220,village=1220-1420,combat_in=1420-1520,combat_orbit=1520-1780,redstone=1780-1980,cave_fly=1980-2090,cave_inside=2090-2290,cave_pull=2290-2440,nether_fly=2440-2600,nether_orbit=2600-2840,end_fly=2840-3000,end_orbit=3000-3340,final=3340-3600`
- `entity_count_delta` = `19.00`
- `trees_built` = `173.00`
- `preset_full` = `0.00`
- `preset_quick` = `0.00`
- `entity_count_sample_end` = `78.00`
- `villagers_spawned` = `36.00`
- `fps_1pct_low` = `773.63`
- `segment_count` = `19.00`
- `fps_harmonic_avg` = `3393.60`
- `blocks_placed` = `314075.00`
- `other_entities_spawned` = `58.00`
- `terrain_area_blocks` = `43473.00`
- `fps_0p1pct_low` = `441.52`
- `preload_chunks` = `81.00`
- `seed` = `27182.00`
- `preset_long` = `0.00`
- `entity_count_sample_start` = `59.00`
- `preload_duration_ms` = `7.00`

