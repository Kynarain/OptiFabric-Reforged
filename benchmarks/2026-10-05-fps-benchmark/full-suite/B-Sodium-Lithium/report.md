# FPS Test session — 2026-10-05T17:56:06.6500569+08:00

- Minecraft: `1.21.1`
- OS: `Windows 10 10.0 (amd64)`
- CPU cores: `16`
- Java: `22.0.2` (Java HotSpot(TM) 64-Bit Server VM)
- Max heap: `8192 MB`
- GPU: `AMD Radeon RX 7800 XT / 3.2.0 Core Profile Context 25.12.1.251128`

## Summary

| # | Benchmark | Cat. | Avg FPS | 1% low | 0.1% low | p99 frame ms | Tick avg ms | GC | Heap Δ MB |
|---:|---|---|---:|---:|---:|---:|---:|---:|---:|
| 1 | [Particle cycle (7 types → all together)](#particle-cycle-7-types--all-together) | Particles | 4772.9 | 818.9 | 216.9 | 0.58 | 0.80 | 4 | 6462 |
| 2 | [Cows ×200 ring](#cows-200-ring) | Entities | 2887.8 | 895.4 | 730.8 | 0.75 | 0.81 | 4 | 2540 |
| 3 | [Sheep ×200 ring](#sheep-200-ring) | Entities | 1514.2 | 646.6 | 546.7 | 1.47 | 0.78 | 10 | 5904 |
| 4 | [Zombies ×150 ring (AI on)](#zombies-150-ring-ai-on) | Entities | 1406.9 | 610.1 | 493.1 | 1.54 | 0.81 | 4 | 1664 |
| 5 | [Pigs ×250 ring](#pigs-250-ring) | Entities | 2597.4 | 822.3 | 618.1 | 0.83 | 0.77 | 6 | 5248 |
| 6 | [Villagers ×100 ring](#villagers-100-ring) | Entities | 2008.3 | 816.6 | 713.3 | 1.16 | 0.66 | 4 | 4370 |
| 7 | [Chickens ×300 ring](#chickens-300-ring) | Entities | 2184.8 | 771.2 | 670.1 | 1.10 | 0.76 | 6 | 4594 |
| 8 | [Item entities ×500](#item-entities-500) | Entities | 1229.2 | 525.5 | 481.7 | 1.83 | 0.99 | 4 | 5150 |
| 9 | [XP orbs ×500 ring](#xp-orbs-500-ring) | Entities | 1614.3 | 385.4 | 313.4 | 2.43 | 1.83 | 10 | 2494 |
| 10 | [Item merge storm (cobblestone, mergeable)](#item-merge-storm-cobblestone-mergeable) | Entities | 705.4 | 228.0 | 202.2 | 4.11 | 1.83 | 0 | 5186 |
| 11 | [Zombies obstacle pathfinding (150 + pillar maze)](#zombies-obstacle-pathfinding-150--pillar-maze) | Entities | 1700.2 | 630.7 | 512.6 | 1.44 | 0.82 | 4 | 5378 |
| 12 | [Villager AI village (80, brain on)](#villager-ai-village-80-brain-on) | Entities | 1468.4 | 707.2 | 549.3 | 1.29 | 0.59 | 10 | 2172 |
| 13 | [TNT field (14×14 staggered fuses)](#tnt-field-1414-staggered-fuses) | Physics | 5027.3 | 1348.9 | 832.5 | 0.48 | 0.89 | 8 | 612 |
| 14 | [TNT field destructive (breaks terrain)](#tnt-field-destructive-breaks-terrain) | Physics | 4708.0 | 1298.8 | 908.7 | 0.52 | 0.87 | 4 | 6108 |
| 15 | [Falling sand wall 40×40 (heavy)](#falling-sand-wall-4040-heavy) | Physics | 2165.4 | 66.5 | 48.4 | 12.73 | 4.53 | 10 | 720 |
| 16 | [Falling gravel mixed heavy (sand+gravel+concrete)](#falling-gravel-mixed-heavy-sandgravelconcrete) | Physics | 2151.7 | 73.9 | 63.0 | 12.10 | 4.46 | 9 | 3824 |
| 17 | [Falling sand wall (lite, staggered)](#falling-sand-wall-lite-staggered) | Physics | 5856.8 | 1437.9 | 821.8 | 0.41 | 1.02 | 4 | 2720 |
| 18 | [Falling gravel mixed (lite, staggered)](#falling-gravel-mixed-lite-staggered) | Physics | 6011.2 | 1495.4 | 828.7 | 0.39 | 1.02 | 4 | 5970 |
| 19 | [Projectile storm (arrows + snowballs)](#projectile-storm-arrows--snowballs) | Physics | 2361.5 | 745.4 | 528.7 | 0.89 | 0.81 | 10 | 894 |
| 20 | [Redstone clocks (6×6)](#redstone-clocks-66) | Redstone | 6191.6 | 1704.1 | 1195.6 | 0.39 | 0.55 | 8 | 720 |
| 21 | [Redstone dust grid (16 trails ×32 + repeaters + lamps)](#redstone-dust-grid-16-trails-32--repeaters--lamps) | Redstone | 5983.4 | 1428.9 | 586.0 | 0.42 | 0.49 | 4 | 4050 |
| 22 | [Piston/slime array (8×8 toggled every 8t)](#pistonslime-array-88-toggled-every-8t) | Redstone | 6167.0 | 1669.5 | 1113.8 | 0.41 | 0.51 | 10 | 3390 |
| 23 | [Static dense forest (orbit canopy, no worldgen)](#static-dense-forest-orbit-canopy-no-worldgen) | Chunks | 5541.6 | 1609.5 | 1131.5 | 0.43 | 0.50 | 7 | 3848 |
| 24 | [Plains flyby (single-biome world)](#plains-flyby-single-biome-world) | Chunks | 5147.3 | 687.4 | 199.4 | 0.74 | 0.71 | 14 | 6050 |
| 25 | [Jungle flyby (single-biome world)](#jungle-flyby-single-biome-world) | Chunks | 3683.4 | 477.9 | 95.1 | 0.85 | 0.68 | 16 | 1326 |
| 26 | [Desert flyby (single-biome world)](#desert-flyby-single-biome-world) | Chunks | 5212.7 | 689.4 | 180.4 | 0.64 | 0.64 | 12 | 2756 |
| 27 | [Taiga flyby (single-biome world)](#taiga-flyby-single-biome-world) | Chunks | 5472.5 | 621.4 | 120.8 | 0.57 | 0.64 | 23 | 2358 |
| 28 | [Snowy plains flyby](#snowy-plains-flyby) | Chunks | 5717.8 | 685.1 | 167.7 | 0.57 | 0.59 | 18 | 2352 |
| 29 | [Forest flyby](#forest-flyby) | Chunks | 4939.8 | 627.8 | 155.0 | 0.70 | 0.63 | 22 | 5900 |
| 30 | [Savanna flyby](#savanna-flyby) | Chunks | 5766.3 | 766.6 | 186.2 | 0.56 | 0.65 | 12 | 4628 |
| 31 | [Swamp flyby](#swamp-flyby) | Chunks | 4969.2 | 336.2 | 111.9 | 0.72 | 0.67 | 35 | 4958 |
| 32 | [Cherry grove flyby](#cherry-grove-flyby) | Chunks | 5751.7 | 649.6 | 135.3 | 0.56 | 0.72 | 16 | 4060 |
| 33 | [Badlands flyby](#badlands-flyby) | Chunks | 6010.5 | 851.1 | 188.8 | 0.53 | 0.57 | 37 | 5196 |
| 34 | [Dark forest flyby (dense canopy)](#dark-forest-flyby-dense-canopy) | Chunks | 4876.7 | 616.4 | 149.5 | 0.72 | 0.63 | 36 | 5868 |
| 35 | [Windswept hills flyby](#windswept-hills-flyby) | Chunks | 5497.6 | 775.1 | 193.2 | 0.57 | 0.58 | 12 | 2996 |
| 36 | [Idle baseline (orbit, flat world)](#idle-baseline-orbit-flat-world) | Baseline | 6379.5 | 1814.2 | 1348.9 | 0.37 | 0.48 | 7 | 5848 |
| 37 | [Particle diversity stress (16 types simultaneously)](#particle-diversity-stress-16-types-simultaneously) | Stress | 495.3 | 200.7 | 178.0 | 4.77 | 2.45 | 4 | 5954 |
| 38 | [Fluid spread (water basin, 4-step periodic reset)](#fluid-spread-water-basin-4-step-periodic-reset) | Fluids | 5401.5 | 1540.7 | 1104.4 | 0.46 | 0.49 | 4 | 5646 |
| 39 | [Lighting update (16×16 glowstone reveal/hide)](#lighting-update-1616-glowstone-revealhide) | Lighting | 5903.9 | 1519.4 | 828.6 | 0.41 | 0.54 | 10 | 4948 |
| 40 | [Hopper grid 20×20 (transfer storm)](#hopper-grid-2020-transfer-storm) | Block-Entities | 5079.2 | 1452.5 | 956.6 | 0.49 | 0.47 | 8 | 1798 |
| 41 | [Comparator storage (8×8 chests + comparators)](#comparator-storage-88-chests--comparators) | Block-Entities | 4265.5 | 1359.7 | 952.5 | 0.56 | 0.48 | 10 | 2072 |

## Table of contents

- [Particle cycle (7 types → all together)](#particle-cycle-7-types--all-together)
- [Cows ×200 ring](#cows-200-ring)
- [Sheep ×200 ring](#sheep-200-ring)
- [Zombies ×150 ring (AI on)](#zombies-150-ring-ai-on)
- [Pigs ×250 ring](#pigs-250-ring)
- [Villagers ×100 ring](#villagers-100-ring)
- [Chickens ×300 ring](#chickens-300-ring)
- [Item entities ×500](#item-entities-500)
- [XP orbs ×500 ring](#xp-orbs-500-ring)
- [Item merge storm (cobblestone, mergeable)](#item-merge-storm-cobblestone-mergeable)
- [Zombies obstacle pathfinding (150 + pillar maze)](#zombies-obstacle-pathfinding-150--pillar-maze)
- [Villager AI village (80, brain on)](#villager-ai-village-80-brain-on)
- [TNT field (14×14 staggered fuses)](#tnt-field-1414-staggered-fuses)
- [TNT field destructive (breaks terrain)](#tnt-field-destructive-breaks-terrain)
- [Falling sand wall 40×40 (heavy)](#falling-sand-wall-4040-heavy)
- [Falling gravel mixed heavy (sand+gravel+concrete)](#falling-gravel-mixed-heavy-sandgravelconcrete)
- [Falling sand wall (lite, staggered)](#falling-sand-wall-lite-staggered)
- [Falling gravel mixed (lite, staggered)](#falling-gravel-mixed-lite-staggered)
- [Projectile storm (arrows + snowballs)](#projectile-storm-arrows--snowballs)
- [Redstone clocks (6×6)](#redstone-clocks-66)
- [Redstone dust grid (16 trails ×32 + repeaters + lamps)](#redstone-dust-grid-16-trails-32--repeaters--lamps)
- [Piston/slime array (8×8 toggled every 8t)](#pistonslime-array-88-toggled-every-8t)
- [Static dense forest (orbit canopy, no worldgen)](#static-dense-forest-orbit-canopy-no-worldgen)
- [Plains flyby (single-biome world)](#plains-flyby-single-biome-world)
- [Jungle flyby (single-biome world)](#jungle-flyby-single-biome-world)
- [Desert flyby (single-biome world)](#desert-flyby-single-biome-world)
- [Taiga flyby (single-biome world)](#taiga-flyby-single-biome-world)
- [Snowy plains flyby](#snowy-plains-flyby)
- [Forest flyby](#forest-flyby)
- [Savanna flyby](#savanna-flyby)
- [Swamp flyby](#swamp-flyby)
- [Cherry grove flyby](#cherry-grove-flyby)
- [Badlands flyby](#badlands-flyby)
- [Dark forest flyby (dense canopy)](#dark-forest-flyby-dense-canopy)
- [Windswept hills flyby](#windswept-hills-flyby)
- [Idle baseline (orbit, flat world)](#idle-baseline-orbit-flat-world)
- [Particle diversity stress (16 types simultaneously)](#particle-diversity-stress-16-types-simultaneously)
- [Fluid spread (water basin, 4-step periodic reset)](#fluid-spread-water-basin-4-step-periodic-reset)
- [Lighting update (16×16 glowstone reveal/hide)](#lighting-update-1616-glowstone-revealhide)
- [Hopper grid 20×20 (transfer storm)](#hopper-grid-2020-transfer-storm)
- [Comparator storage (8×8 chests + comparators)](#comparator-storage-88-chests--comparators)

## Details

### Particle cycle (7 types → all together) (`particle_cycle`)

Category: **Particles**  |  Duration: 23112 ms  |  Sample ticks: 400

**FPS**  avg `4772.87`, min `52.56`, p50 `4901.96`, p95 `6613.76`, p99 `6958.94`, 1%low `818.91`, 0.1%low `216.94`, std `1106.14`

**Frame time (ms)**  avg `0.23`, p50 `0.20`, p95 `0.37`, p99 `0.58`, p99.9 `1.16`, max `19.03`

**Client tick (ms)**  avg `0.80`, p95 `0.99`, max `1.57`

**Memory**  start `968 MB`, end `1632 MB`, peak `7430 MB`, GC `4 events / 688 ms`

**FPS over sampling window (ASCII):**

```
6411.8 |                                             ██                                 
6120.0 |                                             ████                               
5828.2 |                                         █  █████                               
5536.5 |                            ██           ██ ██████                              
5244.7 |       █ ██  █ █  ███   ██  ██ █         ██ ██████            █                 
4953.0 |   █  ███████████ █████ ██ ███████      ██████████      █ ███████               
4661.2 | █████████████████████████████████      ██████████ ███████████████              
4369.5 |███████████████████████████████████    ███████████ ███████████████          █  █
4077.7 |███████████████████████████████████    ███████████ ████████████████   ██ █ █████
3785.9 |███████████████████████████████████   ██████████████████████████████████████████
3494.2 |███████████████████████████████████  ███████████████████████████████████████████
3202.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20918
   1 ms |   76
   2 ms |   1
   3 ms |   1
  16 ms |   1
  17 ms |   2
  19 ms |   1
```

**Per-particle stage breakdown:**

> _Approximate split: frames are bucketed evenly across stages by index, not aligned to the actual tick boundary. Use as a relative comparison, not an absolute per-stage measurement._

| Stage | Particles spawned | Frames captured | Avg FPS | p99 frame ms |
|---|---:|---:|---:|---:|
| `smoke` | 160 | 2625 | 4815.0 | 0.56 |
| `dragon_breath` | 160 | 2625 | 5033.8 | 0.49 |
| `ALL_TOGETHER` | 1680 | 2625 | 5075.6 | 0.53 |
| `dripping_water` | 240 | 2625 | 4257.6 | 0.68 |
| `portal` | 160 | 2625 | 5750.8 | 0.65 |
| `sculk_charge_pop` | 240 | 2625 | 4650.3 | 0.62 |
| `end_rod` | 240 | 2625 | 4501.1 | 0.53 |
| `flame` | 160 | 2625 | 4098.6 | 0.53 |

**Extras:**

- `particle_stage_count` = `8.00`
- `entity_count_delta` = `0.00`
- `preset_full` = `0.00`
- `particles_stage_smoke` = `160.00`
- `entity_count_sample_end` = `1.00`
- `fps_1pct_low` = `818.91`
- `fps_harmonic_avg` = `4328.93`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `108.00`
- `particles_stage_dragon_breath` = `160.00`
- `preset_long` = `0.00`
- `fps_0p1pct_low` = `216.94`
- `seed` = `2503.00`
- `particle_stage_ticks` = `50.00`
- `particles_stage_ALL_TOGETHER` = `1680.00`
- `particles_stage_dripping_water` = `240.00`
- `particles_stage_portal` = `160.00`
- `entity_count_sample_start` = `1.00`
- `particles_stage_sculk_charge_pop` = `240.00`
- `particles_total` = `3040.00`
- `particles_stage_end_rod` = `240.00`
- `preload_chunks` = `81.00`
- `particles_stage_flame` = `160.00`

### Cows ×200 ring (`entity_cows`)

Category: **Entities**  |  Duration: 23107 ms  |  Sample ticks: 400

**FPS**  avg `2887.82`, min `616.33`, p50 `2989.54`, p95 `3207.18`, p99 `3258.40`, 1%low `895.36`, 0.1%low `730.81`, std `366.61`

**Frame time (ms)**  avg `0.36`, p50 `0.33`, p95 `0.44`, p99 `0.75`, p99.9 `1.28`, max `1.62`

**Client tick (ms)**  avg `0.81`, p95 `0.87`, max `1.80`

**Memory**  start `4888 MB`, end `4500 MB`, peak `7428 MB`, GC `4 events / 176 ms`

**FPS over sampling window (ASCII):**

```
3027.9 |                                █                                    █          
3001.0 |           █                    ██                                   ██         
2974.1 |        █  █               █    ██                █   █              ██         
2947.2 |        █ ███             ██   ███              ███   █ █   █      ██████ █     
2920.3 |     █  █ ███   █         ███ ████  ███    █    ███   █ █   █  █   ██████ ██    
2893.4 |█   ██  █████   ██     █ █████████ ████ █  █  █████████ █  ██ ██  ███████ ███   
2866.5 |█ █ ██  █████ █ ██  ██ █ ██████████████ █ ███ █████████ █  ██ ██  ███████████   
2839.6 |█ █ ███ █████ ████  ██ █ ██████████████ █████ █████████ █ ███ ████████████████  
2812.7 |█ █ ███ ██████████  ██ █ ████████████████████████████████ ███ █████████████████ 
2785.8 |█ █ ███ ██████████ ████████████████████████████████████████████████████████████ 
2758.9 |███ ███ ██████████ █████████████████████████████████████████████████████████████
2732.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20845
   1 ms |   155
```

**Extras:**

- `seed` = `6121.00`
- `fps_1pct_low` = `895.36`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `201.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `2802.48`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `730.81`
- `entities_spawned` = `200.00`
- `entity_count_sample_start` = `201.00`
- `preload_duration_ms` = `4.00`
- `preset_long` = `0.00`

### Sheep ×200 ring (`entity_sheep`)

Category: **Entities**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `1514.23`, min `396.76`, p50 `1555.21`, p95 `1666.39`, p99 `1688.05`, 1%low `646.64`, 0.1%low `546.68`, std `159.88`

**Frame time (ms)**  avg `0.67`, p50 `0.64`, p95 `0.80`, p99 `1.47`, p99.9 `1.63`, max `2.52`

**Client tick (ms)**  avg `0.78`, p95 `0.83`, max `1.58`

**Memory**  start `1540 MB`, end `2548 MB`, peak `7444 MB`, GC `10 events / 890 ms`

**FPS over sampling window (ASCII):**

```
1584.9 |             █ █                                                                
1566.6 |            ████          █  █     █                                            
1548.2 |       █   ██████████  █ ███████ █ █  █  █           █  █          █            
1529.9 |█  ██  █   ██████████ ██████████████ ██ ███          █  █  █      ██ ██  █      
1511.6 |█ ███  █   ████████████████████████████ ███     █    ██ █ ██   █ ███ ███ █    █ 
1493.2 |█████  ████████████████████████████████████     █    ██ █ ████ █ ███ ███ █  ████
1474.9 |█████ █████████████████████████████████████   ███  ████ █ ██████████████ █ █████
1456.6 |████████████████████████████████████████████  ███  ████ ████████████████████████
1438.2 |████████████████████████████████████████████  █████████ ████████████████████████
1419.9 |████████████████████████████████████████████  ██████████████████████████████████
1401.6 |█████████████████████████████████████████████ ██████████████████████████████████
1383.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20589
   1 ms | █  407
   2 ms |   4
```

**Extras:**

- `seed` = `6133.00`
- `fps_1pct_low` = `646.64`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `201.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1487.09`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `546.68`
- `entities_spawned` = `200.00`
- `entity_count_sample_start` = `201.00`
- `preload_duration_ms` = `34.00`
- `preset_long` = `0.00`

### Zombies ×150 ring (AI on) (`entity_zombies`)

Category: **Entities**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `1406.88`, min `210.24`, p50 `1442.17`, p95 `1585.79`, p99 `1625.75`, 1%low `610.11`, 0.1%low `493.15`, std `164.62`

**Frame time (ms)**  avg `0.73`, p50 `0.69`, p95 `0.90`, p99 `1.54`, p99.9 `1.73`, max `4.76`

**Client tick (ms)**  avg `0.81`, p95 `0.87`, max `1.77`

**Memory**  start `5964 MB`, end `5126 MB`, peak `7628 MB`, GC `4 events / 162 ms`

**FPS over sampling window (ASCII):**

```
1529.7 |                               █                                                
1501.9 |                       █      ██ █ █                                            
1474.1 |            █        █ █      █████████ ███                                     
1446.4 |          ████   ███████ █   ███████████████  █ ██                              
1418.6 |         █████ ███████████  ███████████████████ ██         ███         █        
1390.9 |    ██ ███████████████████████████████████████████   █     █████ █ ██ ███ █     
1363.1 |    ██████████████████████████████████████████████   ████████████████████████ ██
1335.3 |█   ██████████████████████████████████████████████   ███████████████████████████
1307.6 |█   ███████████████████████████████████████████████  ███████████████████████████
1279.8 |██  ███████████████████████████████████████████████ ████████████████████████████
1252.1 |███████████████████████████████████████████████████ ████████████████████████████
1224.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20371
   1 ms | █  623
   2 ms |   5
   4 ms |   1
```

**Extras:**

- `seed` = `6151.00`
- `fps_1pct_low` = `610.11`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `151.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1377.58`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `493.15`
- `entities_spawned` = `150.00`
- `entity_count_sample_start` = `151.00`
- `preload_duration_ms` = `25.00`
- `preset_long` = `0.00`

### Pigs ×250 ring (`entity_pigs`)

Category: **Entities**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `2597.43`, min `216.73`, p50 `2693.97`, p95 `2890.17`, p99 `2927.40`, 1%low `822.34`, 0.1%low `618.14`, std `318.41`

**Frame time (ms)**  avg `0.40`, p50 `0.37`, p95 `0.49`, p99 `0.83`, p99.9 `1.38`, max `4.61`

**Client tick (ms)**  avg `0.77`, p95 `0.87`, max `1.59`

**Memory**  start `1492 MB`, end `6740 MB`, peak `6740 MB`, GC `6 events / 889 ms`

**FPS over sampling window (ASCII):**

```
2740.8 |                   █  █           █       █                                     
2704.0 |                   █  █ █ █       ███ █   █  █          █      █                
2667.1 |                █  ██████ █ █    ██████████  █         ███     █                
2630.3 |            █████ ███████ █ ██  ███████████  █ █   ████████ █  █   █      █  █  
2593.5 |██         ████████████████ ██████████████████ ██  ██████████ ███  █      █  ██ 
2556.6 |██    █   ████████████████████████████████████ ██ ███████████████ ███  █ ██  ███
2519.8 |██    █   ████████████████████████████████████ ██████████████████ ████ ████ ████
2482.9 |██  █ █   ████████████████████████████████████████████████████████████ █████████
2446.1 |██  █ █   ████████████████████████████████████████████████████████████ █████████
2409.2 |██  █ █ ████████████████████████████████████████████████████████████████████████
2372.4 |██ ████ ████████████████████████████████████████████████████████████████████████
2335.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20826
   1 ms |   173
   4 ms |   1
```

**Extras:**

- `seed` = `6163.00`
- `fps_1pct_low` = `822.34`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `251.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `2526.16`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `618.14`
- `entities_spawned` = `250.00`
- `entity_count_sample_start` = `251.00`
- `preload_duration_ms` = `50.00`
- `preset_long` = `0.00`

### Villagers ×100 ring (`entity_villagers`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `2008.27`, min `461.17`, p50 `2056.77`, p95 `2221.73`, p99 `2249.21`, 1%low `816.60`, 0.1%low `713.32`, std `215.29`

**Frame time (ms)**  avg `0.51`, p50 `0.49`, p95 `0.61`, p99 `1.16`, p99.9 `1.29`, max `2.17`

**Client tick (ms)**  avg `0.66`, p95 `0.71`, max `1.42`

**Memory**  start `3344 MB`, end `4918 MB`, peak `7714 MB`, GC `4 events / 166 ms`

**FPS over sampling window (ASCII):**

```
2092.2 |                █                           █     █            █              █ 
2069.2 |             █  █ █                         █   █ ██  ███  █ █ █         █ ████ 
2046.1 |             ██ █ █            █        █   █  ██ ██ █████████ ██      █ █ ████ 
2023.0 | █   █ █    ███ █ █       █  █ ███   ████   ██ ██ ██ ████████████ █    █ █ ████ 
2000.0 | █   ███  █ ███ ███ ███   ██ █ ████  ████   █████ █████████████████    █ ███████
1976.9 | ██  ███  █████ ███████   ████ ████ █████   █████ ███████████████████ ██████████
1953.9 | ███ ███  █████ ███████   ███████████████  █████████████████████████████████████
1930.8 | ███ ███  ██████████████████████████████████████████████████████████████████████
1907.7 |████ ████ ██████████████████████████████████████████████████████████████████████
1884.7 |████ ████ ██████████████████████████████████████████████████████████████████████
1861.6 |████ ███████████████████████████████████████████████████████████████████████████
1838.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20763
   1 ms |   236
   2 ms |   1
```

**Extras:**

- `seed` = `6173.00`
- `fps_1pct_low` = `816.60`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `101.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1971.42`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `713.32`
- `entities_spawned` = `100.00`
- `entity_count_sample_start` = `101.00`
- `preload_duration_ms` = `23.00`
- `preset_long` = `0.00`

### Chickens ×300 ring (`entity_chickens`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `2184.81`, min `543.66`, p50 `2253.78`, p95 `2445.59`, p99 `2483.24`, 1%low `771.17`, 0.1%low `670.10`, std `262.24`

**Frame time (ms)**  avg `0.47`, p50 `0.44`, p95 `0.58`, p99 `1.10`, p99.9 `1.36`, max `1.84`

**Client tick (ms)**  avg `0.76`, p95 `0.81`, max `1.55`

**Memory**  start `1686 MB`, end `6280 MB`, peak `6280 MB`, GC `6 events / 903 ms`

**FPS over sampling window (ASCII):**

```
2322.0 |   █               █                                                            
2294.0 |   █ █          █  █  █               █                               ██ ███    
2266.0 | ███ █        █ █  █  █               █         █                     ██ ███████
2238.0 | █████ █      ███  █ ██   █           █         █              █      ██████████
2210.0 | █████ █      ███ ██ ██   █  █        █ █       █             ██      ██████████
2182.0 | █████ █    █████ █████   █ ██  █ █   █ █ █ █  ██   █         ██   █ ███████████
2154.1 |████████    ███████████  ██ ██ ██ █ █ █ █████████  ██   █ █   ██ ███ ███████████
2126.1 |██████████  ████████████ ██ ██ ██████ █ █████████  ██  ████  ███████████████████
2098.1 |███████████████████████████ ██ ████████ █████████  ████████ ████████████████████
2070.1 |██████████████████████████████ ████████ ██████████ █████████████████████████████
2042.1 |██████████████████████████████ █████████████████████████████████████████████████
2014.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20777
   1 ms |   223
```

**Extras:**

- `seed` = `6197.00`
- `fps_1pct_low` = `771.17`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `301.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `2131.92`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `670.10`
- `entities_spawned` = `300.00`
- `entity_count_sample_start` = `301.00`
- `preload_duration_ms` = `49.00`
- `preset_long` = `0.00`

### Item entities ×500 (`entity_items`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1229.17`, min `358.92`, p50 `1262.15`, p95 `1345.54`, p99 `1408.25`, 1%low `525.52`, 0.1%low `481.70`, std `130.54`

**Frame time (ms)**  avg `0.83`, p50 `0.79`, p95 `0.99`, p99 `1.83`, p99.9 `1.97`, max `2.79`

**Client tick (ms)**  avg `0.99`, p95 `1.05`, max `1.85`

**Memory**  start `2618 MB`, end `996 MB`, peak `7768 MB`, GC `4 events / 148 ms`

**FPS over sampling window (ASCII):**

```
1350.0 |                                                                               █
1333.5 |                                                                              ██
1316.9 |                                                                              ██
1300.3 |                                                                              ██
1283.7 |                                                                              ██
1267.1 |                                                  █         █                 ██
1250.6 |   █                               █ █         █ ██      █  █ █               ██
1234.0 |██████   ██  ███         ██      █ █ ████ █ ████████████ █ ████ ████ █  █    ███
1217.4 |████████████ ████       ███ █ █████████████████████████████████ ███████████  ███
1200.8 |█████████████████ █  ██████████████████████████████████████████████████████ ████
1184.2 |█████████████████ █ ████████████████████████████████████████████████████████████
1167.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20019
   1 ms | ██  966
   2 ms |   15
```

**Extras:**

- `seed` = `6203.00`
- `fps_1pct_low` = `525.52`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `501.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1205.95`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `481.70`
- `entities_spawned` = `500.00`
- `entity_count_sample_start` = `501.00`
- `preload_duration_ms` = `46.00`
- `preset_long` = `0.00`

### XP orbs ×500 ring (`entity_xp`)

Category: **Entities**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `1614.30`, min `235.47`, p50 `1668.61`, p95 `1816.20`, p99 `1850.14`, 1%low `385.44`, 0.1%low `313.39`, std `226.10`

**Frame time (ms)**  avg `0.65`, p50 `0.60`, p95 `0.83`, p99 `2.43`, p99.9 `2.75`, max `4.25`

**Client tick (ms)**  avg `1.83`, p95 `1.93`, max `2.90`

**Memory**  start `5000 MB`, end `5030 MB`, peak `7494 MB`, GC `10 events / 1052 ms`

**FPS over sampling window (ASCII):**

```
1726.2 |██      ██ █  █           █               █                                     
1661.5 |███████████████████████   █████ ██     ████             █         ███  █  ███ █ 
1596.7 |██████████████████████████████████  █  █████           █████████████████████████
1531.9 |████████████████████████████████████████████   ██ ██████████████████████████████
1467.1 |████████████████████████████████████████████  ██████████████████████████████████
1402.4 |████████████████████████████████████████████  ██████████████████████████████████
1337.6 |████████████████████████████████████████████  ██████████████████████████████████
1272.8 |████████████████████████████████████████████  ██████████████████████████████████
1208.0 |████████████████████████████████████████████  ██████████████████████████████████
1143.3 |████████████████████████████████████████████ ███████████████████████████████████
1078.5 |████████████████████████████████████████████ ███████████████████████████████████
1013.7 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20375
   1 ms | █  352
   2 ms | █  263
   3 ms |   8
   4 ms |   2
```

**Extras:**

- `seed` = `6217.00`
- `fps_1pct_low` = `385.44`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `501.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1539.72`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `313.39`
- `entities_spawned` = `500.00`
- `entity_count_sample_start` = `501.00`
- `preload_duration_ms` = `45.00`
- `preset_long` = `0.00`

### Item merge storm (cobblestone, mergeable) (`entity_items_merge_storm`)

Category: **Entities**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `705.37`, min `157.42`, p50 `688.44`, p95 `1041.02`, p99 `1091.25`, 1%low `227.95`, 0.1%low `202.17`, std `195.17`

**Frame time (ms)**  avg `1.55`, p50 `1.45`, p95 `2.28`, p99 `4.11`, p99.9 `4.68`, max `6.35`

**Client tick (ms)**  avg `1.83`, p95 `2.25`, max `2.78`

**Memory**  start `1910 MB`, end `7096 MB`, peak `7096 MB`, GC `0 events / 0 ms`

**FPS over sampling window (ASCII):**

```
1031.6 |███    ██                                                                       
981.2 |████ ████                                                                       
930.7 |████████████ █                                                                  
880.3 |█████████████████ █ ██                                                          
829.8 |█████████████████████████                                                       
779.4 |███████████████████████████████                                                 
728.9 |████████████████████████████████ █████                                          
678.5 |████████████████████████████████████████████                                    
628.0 |██████████████████████████████████████████████████                              
577.6 |█████████████████████████████████████████████████████████ ██                    
527.1 |██████████████████████████████████████████████████████████████████              
476.7 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████  1060
   1 ms | ████████████████████████████████████████  10177
   2 ms | █████  1318
   3 ms | █  171
   4 ms | █  154
   5 ms |   1
   6 ms |   1
```

**Extras:**

- `entity_count_sample_end` = `1561.00`
- `preload_duration_ms` = `49.00`
- `seed` = `6287.00`
- `fps_0p1pct_low` = `202.17`
- `fps_harmonic_avg` = `644.10`
- `items_spawned` = `1560.00`
- `preset_quick` = `1.00`
- `preload_chunks` = `81.00`
- `items_merged_estimate` = `0.00`
- `items_alive_p95` = `1560.00`
- `preset_full` = `0.00`
- `fps_1pct_low` = `227.95`
- `items_alive_max` = `1560.00`
- `items_alive_p50` = `1240.00`
- `items_alive_avg` = `1230.00`
- `waves_spawned` = `12.00`
- `entity_count_sample_start` = `681.00`
- `entity_count_delta` = `880.00`
- `preset_long` = `0.00`

### Zombies obstacle pathfinding (150 + pillar maze) (`entity_zombies_obstacle_pathfinding`)

Category: **Entities**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `1700.18`, min `397.38`, p50 `1751.01`, p95 `1910.22`, p99 `1964.25`, 1%low `630.71`, 0.1%low `512.55`, std `214.00`

**Frame time (ms)**  avg `0.60`, p50 `0.57`, p95 `0.78`, p99 `1.44`, p99.9 `1.76`, max `2.52`

**Client tick (ms)**  avg `0.82`, p95 `0.98`, max `1.35`

**Memory**  start `2406 MB`, end `2156 MB`, peak `7784 MB`, GC `4 events / 167 ms`

**FPS over sampling window (ASCII):**

```
1777.6 |               █                               █                                
1761.3 |               █                █ █ █          █   █                            
1745.1 |               █              █████ ███        █  ██     █                   █  
1728.8 |             █ █     █        █████ ████ █  █ ██ ████    █                   █  
1712.6 |  █ █        █ █     █ █  █   █████ ██████  █ ███████  ███ █   ███           █  
1696.3 |█ █ █ █     ████ █   █ █  █   ████████████████████████ ███████ ████     ███  ███
1680.1 |█ ███ ███ ██████ ██ ████  ██ █████████████████████████ ███████ █████  █ ████ ███
1663.9 |█ ███ ███ ██████ ████████ ████████████████████████████ █████████████  ██████ ███
1647.6 |█ ███████ ████████████████████████████████████████████ █████████████████████████
1631.4 |█████████ ████████████████████████████████████████████ █████████████████████████
1615.1 |█████████ ██████████████████████████████████████████████████████████████████████
1598.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20569
   1 ms | █  424
   2 ms |   7
```

**Extras:**

- `neighbour_updates` = `0.00`
- `pillars_built` = `48.00`
- `zombies_spawned` = `150.00`
- `block_state_changes` = `0.00`
- `preset_long` = `0.00`
- `preload_duration_ms` = `50.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `512.55`
- `fps_1pct_low` = `630.71`
- `entity_count_sample_start` = `151.00`
- `fps_harmonic_avg` = `1655.16`
- `entity_count_sample_end` = `151.00`
- `seed` = `6271.00`
- `preset_full` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_delta` = `0.00`

### Villager AI village (80, brain on) (`villager_ai_village`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1468.36`, min `467.31`, p50 `1497.68`, p95 `1664.17`, p99 `1707.36`, 1%low `707.24`, 0.1%low `549.30`, std `168.05`

**Frame time (ms)**  avg `0.69`, p50 `0.67`, p95 `0.85`, p99 `1.29`, p99.9 `1.59`, max `2.14`

**Client tick (ms)**  avg `0.59`, p95 `0.66`, max `1.21`

**Memory**  start `5618 MB`, end `3520 MB`, peak `7790 MB`, GC `10 events / 995 ms`

**FPS over sampling window (ASCII):**

```
1602.5 |                                                                             █  
1574.1 |                                            █        █             █   ███ █ ██ 
1545.6 |                              █ █   █       █    █  ██ █       █ ███   ████████ 
1517.2 |                   █         ██ ██ ██       █  █ █ █████       █████   ████████ 
1488.7 |                █  ███       █████ ██      █████████████████████████ ██████████ 
1460.2 |             █ ██ ███████  ███████████     █████████████████████████ ██████████ 
1431.8 |         █   █ ██████████  ███████████    ██████████████████████████ ███████████
1403.3 |         █ ███████████████████████████  ████████████████████████████████████████
1374.9 |         █████████████████████████████ █████████████████████████████████████████
1346.4 |█       ████████████████████████████████████████████████████████████████████████
1317.9 |██ ██  █████████████████████████████████████████████████████████████████████████
1289.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20497
   1 ms | █  500
   2 ms |   3
```

**Extras:**

- `entity_count_sample_end` = `81.00`
- `preload_duration_ms` = `49.00`
- `seed` = `6299.00`
- `fps_0p1pct_low` = `549.30`
- `villagers_spawned` = `80.00`
- `fps_harmonic_avg` = `1442.01`
- `neighbour_updates` = `0.00`
- `beds_placed` = `40.00`
- `workstations_placed` = `40.00`
- `preset_quick` = `1.00`
- `preload_chunks` = `81.00`
- `preset_full` = `0.00`
- `scheduled_block_ticks` = `0.00`
- `fps_1pct_low` = `707.24`
- `block_state_changes` = `116.00`
- `doors_placed` = `16.00`
- `entity_count_sample_start` = `81.00`
- `entity_count_delta` = `0.00`
- `preset_long` = `0.00`

### TNT field (14×14 staggered fuses) (`tnt_field`)

Category: **Physics**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `5027.35`, min `436.47`, p50 `5070.99`, p95 `6317.12`, p99 `6510.42`, 1%low `1348.89`, 0.1%low `832.49`, std `815.78`

**Frame time (ms)**  avg `0.21`, p50 `0.20`, p95 `0.27`, p99 `0.48`, p99.9 `1.03`, max `2.29`

**Client tick (ms)**  avg `0.89`, p95 `1.92`, max `2.60`

**Memory**  start `7184 MB`, end `1266 MB`, peak `7796 MB`, GC `8 events / 390 ms`

**FPS over sampling window (ASCII):**

```
6238.7 |                                                                              ██
6039.1 |                                                                        ████████
5839.6 |                                                                    █ ██████████
5640.1 |                                                                   ██ ██████████
5440.5 |                                                    ███  █        ██████████████
5241.0 |                                         █      ████████ ████ ███ ██████████████
5041.5 |                                  █   █████  █ ██████████████████ ██████████████
4841.9 |   █                 █     █████ ████████████████████████████████ ██████████████
4642.4 |████    █  ███      ██   ███████████████████████████████████████████████████████
4442.9 |███████ ███████    █████████████████████████████████████████████████████████████
4243.3 |███████████████ █  █████████████████████████████████████████████████████████████
4043.8 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20971
   1 ms |   28
   2 ms |   1
```

**Extras:**

- `tnt_active_avg` = `36.91`
- `seed` = `3539.00`
- `entity_count_sample_end` = `1.00`
- `tnt_spawned` = `430.00`
- `tnt_active_max` = `205.00`
- `section_rebuilds` = `0.00`
- `entity_count_sample_start` = `188.00`
- `preload_chunks` = `81.00`
- `tnt_active_p50` = `25.00`
- `fps_harmonic_avg` = `4806.57`
- `tnt_active_p95` = `150.00`
- `preset_full` = `0.00`
- `fps_0p1pct_low` = `832.49`
- `preload_duration_ms` = `96.00`
- `explosions_count` = `403.00`
- `entity_count_delta` = `-187.00`
- `waves_spawned` = `13.00`
- `block_state_changes` = `0.00`
- `neighbour_updates` = `0.00`
- `preset_quick` = `1.00`
- `preset_long` = `0.00`
- `fps_1pct_low` = `1348.89`

### TNT field destructive (breaks terrain) (`tnt_field_destructive`)

Category: **Physics**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `4708.03`, min `581.63`, p50 `4777.83`, p95 `5783.69`, p99 `6027.73`, 1%low `1298.81`, 0.1%low `908.65`, std `737.02`

**Frame time (ms)**  avg `0.22`, p50 `0.21`, p95 `0.29`, p99 `0.52`, p99.9 `1.01`, max `1.72`

**Client tick (ms)**  avg `0.87`, p95 `1.78`, max `1.94`

**Memory**  start `1698 MB`, end `2934 MB`, peak `7806 MB`, GC `4 events / 228 ms`

**FPS over sampling window (ASCII):**

```
5825.9 |                                                                               █
5659.8 |                                                                               █
5493.6 |                                                                        ██  █  █
5327.5 |                                                                █ █  ██ █████ ██
5161.3 |                                                    █           █ █  ███████████
4995.2 |                                                    █ ██ █  ████████████████████
4829.0 |                                                    ████ █ █████████████████████
4662.9 |   █████     ███ █           ██   █ ████  █        █████ ███████████████████████
4496.7 |  ████████ █ ██████      █  ███████████████   █ █  █████████████████████████████
4330.6 |█ ██████████████████    ████████████████████  ██████████████████████████████████
4164.4 |██████████████████████  █████████████████████ ██████████████████████████████████
3998.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20976
   1 ms |   24
```

**Extras:**

- `tnt_active_avg` = `36.52`
- `seed` = `3541.00`
- `entity_count_sample_end` = `86.00`
- `tnt_spawned` = `430.00`
- `tnt_active_max` = `206.00`
- `section_rebuilds` = `0.00`
- `entity_count_sample_start` = `206.00`
- `preload_chunks` = `81.00`
- `tnt_active_p50` = `26.00`
- `fps_harmonic_avg` = `4513.20`
- `tnt_active_p95` = `149.00`
- `preset_full` = `0.00`
- `fps_0p1pct_low` = `908.65`
- `preload_duration_ms` = `0.00`
- `explosions_count` = `404.00`
- `entity_count_delta` = `-120.00`
- `waves_spawned` = `13.00`
- `block_state_changes` = `7620.00`
- `neighbour_updates` = `0.00`
- `preset_quick` = `1.00`
- `preset_long` = `0.00`
- `fps_1pct_low` = `1298.81`

### Falling sand wall 40×40 (heavy) (`falling_sand`)

Category: **Physics**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `2165.36`, min `41.00`, p50 `2469.14`, p95 `3750.94`, p99 `6513.17`, 1%low `66.46`, 0.1%low `48.37`, std `1259.27`

**Frame time (ms)**  avg `1.49`, p50 `0.41`, p95 `7.56`, p99 `12.73`, p99.9 `18.18`, max `24.39`

**Client tick (ms)**  avg `4.53`, p95 `6.33`, max `11.87`

**Memory**  start `7102 MB`, end `6662 MB`, peak `7822 MB`, GC `10 events / 915 ms`

**FPS over sampling window (ASCII):**

```
5755.9 |                                                                               █
5252.2 |                                                                              ██
4748.6 |                                                                              ██
4244.9 |                                                                             ███
3741.2 |                                                                             ███
3237.6 |                                                                    ███████  ███
2733.9 |█████████                                ██                 ██████  ███████  ███
2230.2 |██████████     █   ██  ██  ███  ███  ██  █████ ████  █████ ███████  ███████  ███
1726.6 |██████████  █ ███ ███  ██  ███ ████ ████ █████ ████  █████ ███████ █████████ ███
1222.9 |██████████  █ ███ ███ ████ ███ ████ ████ █████ ████ ██████ ███████ █████████ ███
719.2 |███████████ █ ███ ████████ ███ ████ ████ █████ █████████████████████████████████
215.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  10508
   1 ms | ██  635
   2 ms | █  372
   3 ms | █  175
   4 ms | █  234
   5 ms | █  232
   6 ms | █  316
   7 ms | ██  480
   8 ms | █  204
   9 ms |   57
  10 ms |   36
  11 ms |   38
  12 ms |   31
  13 ms |   44
  14 ms |   27
  15 ms |   17
  16 ms |   7
  17 ms |   9
  18 ms |   2
  19 ms |   6
  20 ms |   1
  21 ms |   3
  22 ms |   1
  24 ms |   1
```

**Extras:**

- `variant` = `heavy`
- `entity_count_delta` = `-3084.00`
- `waves_spawned` = `12.00`
- `falling_blocks_alive_p95` = `6400.00`
- `preset_full` = `0.00`
- `entity_count_sample_end` = `117.00`
- `block_state_changes` = `36466.00`
- `falling_blocks_alive_avg` = `4819.56`
- `fps_1pct_low` = `66.46`
- `fps_harmonic_avg` = `671.80`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `32.00`
- `falling_blocks_alive_max` = `6400.00`
- `preset_long` = `0.00`
- `falling_blocks_landed` = `20800.00`
- `fps_0p1pct_low` = `48.37`
- `topup_blocks_per_wave` = `1600.00`
- `seed` = `5077.00`
- `sand_spawned` = `20800.00`
- `entity_count_sample_start` = `3201.00`
- `neighbour_updates` = `0.00`
- `falling_blocks_alive_p50` = `4800.00`
- `preload_chunks` = `81.00`
- `wave_interval_ticks` = `30.00`

### Falling gravel mixed heavy (sand+gravel+concrete) (`falling_gravel_mixed`)

Category: **Physics**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `2151.74`, min `47.37`, p50 `2446.78`, p95 `4065.79`, p99 `6333.12`, 1%low `73.88`, 0.1%low `62.95`, std `1331.19`

**Frame time (ms)**  avg `1.55`, p50 `0.41`, p95 `7.44`, p99 `12.10`, p99.9 `14.92`, max `21.11`

**Client tick (ms)**  avg `4.46`, p95 `6.14`, max `11.96`

**Memory**  start `4000 MB`, end `2102 MB`, peak `7824 MB`, GC `9 events / 371 ms`

**FPS over sampling window (ASCII):**

```
5942.3 |                                                                              ██
5416.7 |                                                                             ███
4891.0 |                                                                            ████
4365.3 |                                                                            ████
3839.7 |                                                                            ████
3314.0 |                                                                   ██████   ████
2788.3 |█████                           █                          ██████  ███████  ████
2262.7 |██████████     ██  █   ██  ██   ██   ██  ████  ████  ████  ██████  ███████  ████
1737.0 |███████████    ██  ██ ███  ███ ████ ████ ████  ████  ████  ██████  ███████ █████
1211.4 |███████████  █ ██ ███ ███  ███ ████ ████ █████ █████ █████ ██████ ████████ █████
685.7 |███████████ ██ ██ ███ ████████ ████ ████ █████████████████ █████████████████████
160.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  9937
   1 ms | ██  574
   2 ms | ██  402
   3 ms | █  187
   4 ms | █  226
   5 ms | █  232
   6 ms | ██  417
   7 ms | ██  513
   8 ms | █  161
   9 ms |   44
  10 ms |   48
  11 ms |   36
  12 ms |   67
  13 ms |   35
  14 ms |   28
  15 ms |   9
  16 ms |   1
  21 ms |   1
```

**Extras:**

- `variant` = `heavy`
- `entity_count_delta` = `-2992.00`
- `waves_spawned` = `12.00`
- `falling_blocks_alive_p95` = `6400.00`
- `preset_full` = `0.00`
- `entity_count_sample_end` = `209.00`
- `block_state_changes` = `36466.00`
- `falling_blocks_alive_avg` = `4803.02`
- `fps_1pct_low` = `73.88`
- `fps_harmonic_avg` = `645.90`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `12.00`
- `falling_blocks_alive_max` = `6400.00`
- `preset_long` = `0.00`
- `falling_blocks_landed` = `20800.00`
- `fps_0p1pct_low` = `62.95`
- `topup_blocks_per_wave` = `1600.00`
- `seed` = `5081.00`
- `sand_spawned` = `20800.00`
- `entity_count_sample_start` = `3201.00`
- `neighbour_updates` = `0.00`
- `falling_blocks_alive_p50` = `4800.00`
- `preload_chunks` = `81.00`
- `wave_interval_ticks` = `30.00`

### Falling sand wall (lite, staggered) (`falling_sand_lite`)

Category: **Physics**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `5856.80`, min `768.58`, p50 `6031.36`, p95 `7007.71`, p99 `7127.58`, 1%low `1437.90`, 0.1%low `821.75`, std `908.51`

**Frame time (ms)**  avg `0.18`, p50 `0.17`, p95 `0.24`, p99 `0.41`, p99.9 `1.13`, max `1.30`

**Client tick (ms)**  avg `1.02`, p95 `1.19`, max `1.63`

**Memory**  start `5056 MB`, end `6086 MB`, peak `7776 MB`, GC `4 events / 161 ms`

**FPS over sampling window (ASCII):**

```
6625.0 |                                                                            █   
6482.5 |                                                              ██   █ ████  ██   
6339.9 |                                                            ████  ██ ████████  █
6197.3 |                                                            █████ ███████████  █
6054.7 |                                 █     █    █    █          ██████████████████ █
5912.1 |                      █    █ █  ███    ███  ████ █████ ███████████████████████ █
5769.5 |                      ███  ████ ███ █  ███ ███████████ █████████████████████████
5627.0 |           █      ████████ ████ ██████████████████████ █████████████████████████
5484.4 |█ █ █ ███  ███ ███████████ █████████████████████████████████████████████████████
5341.8 |█████ ███ ████████████████ █████████████████████████████████████████████████████
5199.2 |██████████████████████████ █████████████████████████████████████████████████████
5056.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20961
   1 ms |   39
```

**Extras:**

- `variant` = `lite`
- `entity_count_delta` = `-441.00`
- `waves_spawned` = `63.00`
- `falling_blocks_alive_p95` = `833.00`
- `preset_full` = `0.00`
- `entity_count_sample_end` = `1.00`
- `block_state_changes` = `5684.00`
- `falling_blocks_alive_avg` = `619.12`
- `fps_1pct_low` = `1437.90`
- `fps_harmonic_avg` = `5590.60`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `16.00`
- `falling_blocks_alive_max` = `882.00`
- `preset_long` = `0.00`
- `falling_blocks_landed` = `3087.00`
- `fps_0p1pct_low` = `821.75`
- `topup_blocks_per_wave` = `49.00`
- `seed` = `5101.00`
- `sand_spawned` = `3087.00`
- `entity_count_sample_start` = `442.00`
- `neighbour_updates` = `0.00`
- `falling_blocks_alive_p50` = `686.00`
- `preload_chunks` = `81.00`
- `wave_interval_ticks` = `6.00`

### Falling gravel mixed (lite, staggered) (`falling_gravel_mixed_lite`)

Category: **Physics**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `6011.20`, min `690.70`, p50 `6191.95`, p95 `7037.30`, p99 `7122.51`, 1%low `1495.39`, 0.1%low `828.68`, std `889.37`

**Frame time (ms)**  avg `0.17`, p50 `0.16`, p95 `0.23`, p99 `0.39`, p99.9 `1.11`, max `1.45`

**Client tick (ms)**  avg `1.02`, p95 `1.20`, max `1.45`

**Memory**  start `1874 MB`, end `2976 MB`, peak `7844 MB`, GC `4 events / 283 ms`

**FPS over sampling window (ASCII):**

```
6651.3 |                                                                  █ █ █     █  █
6495.8 |                                                              █████████████ █ ██
6340.3 |                                                  █    █████ ███████████████████
6184.8 |                                          █   █ ████ ███████████████████████████
6029.3 |                     █     █   ██  █ ███  ██████████████████████████████████████
5873.8 |                    ██  █ ███ ███ ██████████████████████████████████████████████
5718.3 |    █      █ ██  ██ █████████ ██████████████████████████████████████████████████
5562.8 |   ██ █ ██ █ ███████████████████████████████████████████████████████████████████
5407.3 |██ ████ ██ █████████████████████████████████████████████████████████████████████
5251.8 |██ ████ ████████████████████████████████████████████████████████████████████████
5096.3 |███████ ████████████████████████████████████████████████████████████████████████
4940.8 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20967
   1 ms |   33
```

**Extras:**

- `variant` = `lite`
- `entity_count_delta` = `-441.00`
- `waves_spawned` = `63.00`
- `falling_blocks_alive_p95` = `833.00`
- `preset_full` = `0.00`
- `entity_count_sample_end` = `1.00`
- `block_state_changes` = `5684.00`
- `falling_blocks_alive_avg` = `619.12`
- `fps_1pct_low` = `1495.39`
- `fps_harmonic_avg` = `5756.16`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `26.00`
- `falling_blocks_alive_max` = `882.00`
- `preset_long` = `0.00`
- `falling_blocks_landed` = `3087.00`
- `fps_0p1pct_low` = `828.68`
- `topup_blocks_per_wave` = `49.00`
- `seed` = `5113.00`
- `sand_spawned` = `3087.00`
- `entity_count_sample_start` = `442.00`
- `neighbour_updates` = `0.00`
- `falling_blocks_alive_p50` = `686.00`
- `preload_chunks` = `81.00`
- `wave_interval_ticks` = `6.00`

### Projectile storm (arrows + snowballs) (`projectile_storm`)

Category: **Physics**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `2361.45`, min `203.47`, p50 `2368.55`, p95 `2850.63`, p99 `2915.45`, 1%low `745.44`, 0.1%low `528.73`, std `317.79`

**Frame time (ms)**  avg `0.44`, p50 `0.42`, p95 `0.53`, p99 `0.89`, p99.9 `1.53`, max `4.91`

**Client tick (ms)**  avg `0.81`, p95 `0.97`, max `1.53`

**Memory**  start `6888 MB`, end `3060 MB`, peak `7782 MB`, GC `10 events / 913 ms`

**FPS over sampling window (ASCII):**

```
2757.8 |                                                                          █    █
2699.8 |                                                                          ██ █ █
2641.8 |                                                                █  █ █  ████████
2583.8 |                                                           ████ ████ ███████████
2525.8 |                                                           █████████████████████
2467.7 |                                                           █████████████████████
2409.7 |    █                                                 █  ███████████████████████
2351.7 |    ██  ██         █ █   █                █          ███ ███████████████████████
2293.7 | █████████        █████████   █    █ ██   ██         ███████████████████████████
2235.7 | ██████████████   ██████████  █    █████████  ██    ████████████████████████████
2177.6 |█████████████████████████████ ███ ███████████████ █ ████████████████████████████
2119.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20807
   1 ms |   190
   2 ms |   2
   4 ms |   1
```

**Extras:**

- `fps_1pct_low` = `745.44`
- `entity_count_sample_end` = `251.00`
- `preset_quick` = `1.00`
- `block_state_changes` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `173.00`
- `projectiles_spawned` = `1000.00`
- `waves_spawned` = `40.00`
- `preload_duration_ms` = `20.00`
- `entity_count_sample_start` = `78.00`
- `projectiles_swept` = `270.00`
- `max_in_flight_observed` = `250.00`
- `preset_long` = `0.00`
- `seed` = `5099.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `528.73`
- `neighbour_updates` = `0.00`
- `fps_harmonic_avg` = `2292.11`

### Redstone clocks (6×6) (`redstone_clocks`)

Category: **Redstone**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `6191.62`, min `1116.20`, p50 `6640.11`, p95 `7017.54`, p99 `7077.14`, 1%low `1704.12`, 0.1%low `1195.61`, std `965.13`

**Frame time (ms)**  avg `0.17`, p50 `0.15`, p95 `0.23`, p99 `0.39`, p99.9 `0.78`, max `0.90`

**Client tick (ms)**  avg `0.55`, p95 `0.58`, max `0.91`

**Memory**  start `7086 MB`, end `3258 MB`, peak `7806 MB`, GC `8 events / 370 ms`

**FPS over sampling window (ASCII):**

```
6563.1 |            █              █       █                                            
6480.1 |     █  █ █ █      █ ██    █       █             █              █    █          
6397.0 |████ █  █ ████     █ ██    ██   █  █ █     ██    █       █ █    █   ███         
6314.0 |██████  █ ████     █ ██   ███   ██ █ █     ██   ██   █   █ ███ ██   ███      ██ 
6230.9 |██████ ██ ████  █  █ ██   ███   ██ █ █ ██ ███   ██ █ █ █ █ ███ ███  ███  █   ██ 
6147.9 |██████████████ ██  ████ █ ███   ██ █ █ ██ ████  ██ █ █ █ █████ ███  ███ ██   ███
6064.8 |██████████████ ██  ████ █ ███  ███ █ █ ██ ████  ██ █ █ ███████ ███  ███ ██  ████
5981.8 |██████████████████ ████ █ ███  ███ ███ ██ ████ ███ █ █ ███████ ███ ███████  ████
5898.7 |███████████████████████ █████  ███ ███ ██ ████████ ███ ███████ ███████████  ████
5815.7 |███████████████████████ █████████████████ ████████ ███████████ ███████████  ████
5732.6 |███████████████████████ █████████████████ ████████████████████████████████ █████
5649.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  21000
```

**Extras:**

- `entity_count_sample_end` = `1.00`
- `preload_chunks` = `81.00`
- `preset_long` = `0.00`
- `fps_0p1pct_low` = `1195.61`
- `preset_quick` = `1.00`
- `clocks_built` = `36.00`
- `observers_placed` = `72.00`
- `preset_full` = `0.00`
- `fps_harmonic_avg` = `5924.67`
- `fps_1pct_low` = `1704.12`
- `preload_duration_ms` = `50.00`
- `scheduled_block_ticks` = `9612.00`
- `entity_count_delta` = `0.00`
- `neighbour_updates` = `0.00`
- `block_state_changes` = `9612.00`
- `seed` = `4001.00`
- `entity_count_sample_start` = `1.00`

### Redstone dust grid (16 trails ×32 + repeaters + lamps) (`redstone_dust_grid`)

Category: **Redstone**  |  Duration: 23106 ms  |  Sample ticks: 400

**FPS**  avg `5983.45`, min `215.34`, p50 `6329.11`, p95 `6635.70`, p99 `6688.96`, 1%low `1428.87`, 0.1%low `586.00`, std `852.65`

**Frame time (ms)**  avg `0.17`, p50 `0.16`, p95 `0.23`, p99 `0.42`, p99.9 `0.78`, max `4.64`

**Client tick (ms)**  avg `0.49`, p95 `0.53`, max `0.71`

**Memory**  start `3730 MB`, end `7430 MB`, peak `7780 MB`, GC `4 events / 156 ms`

**FPS over sampling window (ASCII):**

```
6181.7 | █          █          █                   █  █                █             █  
6121.7 | █         ███         █           █  █    █ ██         ███    █ █           █  
6061.7 |██         ███ █     ███   ██     ██  ██  ██ ██   █    ████ ██ █ █      █    █  
6001.7 |███ █    █ █████ █  ████   ███ ██ ██████  ██████ ██    ███████ █ █     ███  ██  
5941.6 |█████    █ █████ ██ ████ █ ███ ██████████ ██████████  ████████ ████ █  ████████ 
5881.6 |█████ ██ █ █████ ███████ █ ███ ██████████ ███████████ ████████ ████ ██ ████████ 
5821.6 |████████ █ █████ ████████████████████████████████████ ████████ ███████ ████████ 
5761.5 |██████████ █████ ███████████████████████████████████████████████████████████████
5701.5 |██████████ █████████████████████████████████████████████████████████████████████
5641.5 |██████████ █████████████████████████████████████████████████████████████████████
5581.5 |██████████ █████████████████████████████████████████████████████████████████████
5521.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20985
   1 ms |   12
   4 ms |   3
```

**Extras:**

- `trails_built` = `16.00`
- `dust_placed` = `464.00`
- `preset_full` = `0.00`
- `preset_long` = `0.00`
- `entity_count_sample_end` = `1.00`
- `entity_count_sample_start` = `1.00`
- `block_state_changes` = `70080.00`
- `fps_1pct_low` = `1428.87`
- `neighbour_updates` = `0.00`
- `pulses_issued` = `46.00`
- `fps_harmonic_avg` = `5719.40`
- `preload_duration_ms` = `0.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `586.00`
- `scheduled_block_ticks` = `2240.00`
- `preset_quick` = `1.00`
- `entity_count_delta` = `0.00`
- `lamps_placed` = `128.00`
- `repeaters_placed` = `48.00`
- `seed` = `4019.00`

### Piston/slime array (8×8 toggled every 8t) (`piston_slime_array`)

Category: **Redstone**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `6167.00`, min `922.85`, p50 `6548.79`, p95 `6887.05`, p99 `6944.44`, 1%low `1669.47`, 0.1%low `1113.81`, std `901.77`

**Frame time (ms)**  avg `0.17`, p50 `0.15`, p95 `0.23`, p99 `0.41`, p99.9 `0.78`, max `1.08`

**Client tick (ms)**  avg `0.51`, p95 `0.58`, max `1.02`

**Memory**  start `4372 MB`, end `6792 MB`, peak `7762 MB`, GC `10 events / 896 ms`

**FPS over sampling window (ASCII):**

```
6469.1 |                                    █         █                                 
6395.3 | █                               █  █         █           █          █          
6321.6 | █ █        █  █   █    █        █  █       █ █      ███ ██   █   █  █ █      █ 
6247.8 | ███  █     ██ ██  █  ███  █     █ █████ █  █ ███    ███ ██  ██ █ █  ███  ██ ██ 
6174.0 | ███  █    ███ ██ ██  ███  █     █████████  █████  ████████  ██████  ███  ██ ███
6100.3 | ███  █ █ ████ ██ ██ ████  ███  ██████████  █████  ████████████████ █████ ██ ███
6026.5 |████  █ █ ███████████████  ███ ███████████████████ ████████████████ ████████████
5952.7 |█████ █ █ ████████████████ ███ ███████████████████ ████████████████ ████████████
5878.9 |█████ █ ██████████████████ ███ ████████████████████████████████████ ████████████
5805.2 |█████ ████████████████████ ███ █████████████████████████████████████████████████
5731.4 |█████ ████████████████████████ █████████████████████████████████████████████████
5657.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20999
   1 ms |   1
```

**Extras:**

- `fps_1pct_low` = `1669.47`
- `pistons_built` = `64.00`
- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`
- `slime_blocks` = `192.00`
- `block_state_changes` = `33600.00`
- `preset_full` = `0.00`
- `power_toggles` = `57.00`
- `entity_count_delta` = `0.00`
- `preload_duration_ms` = `15.00`
- `scheduled_block_ticks` = `0.00`
- `entity_count_sample_start` = `1.00`
- `preset_long` = `0.00`
- `seed` = `4027.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `1113.81`
- `neighbour_updates` = `0.00`
- `fps_harmonic_avg` = `5914.54`

### Static dense forest (orbit canopy, no worldgen) (`static_dense_forest`)

Category: **Chunks**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `5541.60`, min `871.84`, p50 `5558.64`, p95 `6854.01`, p99 `6973.50`, 1%low `1609.55`, 0.1%low `1131.50`, std `1010.73`

**Frame time (ms)**  avg `0.19`, p50 `0.18`, p95 `0.26`, p99 `0.43`, p99.9 `0.78`, max `1.15`

**Client tick (ms)**  avg `0.50`, p95 `0.57`, max `0.70`

**Memory**  start `3930 MB`, end `1108 MB`, peak `7778 MB`, GC `7 events / 160 ms`

**FPS over sampling window (ASCII):**

```
5833.9 |    █                                                                           
5756.6 |  █ █ █ █ ██    █                                                               
5679.3 |  ███ █ █ ██   ██ █      █        █  █         █    █                           
5602.1 |█████ ████████ ████  ██ ██    █   ██ ██   ██ ███   ██ █       ██            █   
5524.8 |█████ ██████████████ █████ ██ ██  ██ ███ ████████ ███ ██████  ██ █      █   █  █
5447.6 |████████████████████████████████  ██ ███████████████████████  ██ █████  █  ██  █
5370.3 |████████████████████████████████████████████████████████████ ████████████ ███ ██
5293.1 |█████████████████████████████████████████████████████████████████████████ ██████
5215.8 |█████████████████████████████████████████████████████████████████████████ ██████
5138.5 |█████████████████████████████████████████████████████████████████████████ ██████
5061.3 |█████████████████████████████████████████████████████████████████████████ ██████
4984.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20996
   1 ms |   4
```

**Extras:**

- `trees_built` = `64.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `1131.50`
- `fps_1pct_low` = `1609.55`
- `entity_count_sample_start` = `1.00`
- `leaf_blocks` = `7642.00`
- `preload_duration_ms` = `32.00`
- `seed` = `7039.00`
- `log_blocks` = `320.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `5272.23`
- `preset_full` = `0.00`
- `preset_long` = `0.00`
- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`

### Plains flyby (single-biome world) (`chunk_plains`)

Category: **Chunks**  |  Duration: 29353 ms  |  Sample ticks: 400

**FPS**  avg `5147.29`, min `67.52`, p50 `5549.39`, p95 `6211.18`, p99 `6329.11`, 1%low `687.35`, 0.1%low `199.44`, std `1129.28`

**Frame time (ms)**  avg `0.22`, p50 `0.18`, p95 `0.36`, p99 `0.74`, p99.9 `2.23`, max `14.81`

**Client tick (ms)**  avg `0.71`, p95 `0.91`, max `1.37`

**Memory**  start `1622 MB`, end `4942 MB`, peak `7672 MB`, GC `14 events / 1338 ms`

**FPS over sampling window (ASCII):**

```
5993.6 |                           ██                                                   
5753.8 |███      █ █ █          █  ██        █                                          
5514.0 |████     █ ███ █        █████ █  ███ ██      █    █       █  █       █ █ █      
5274.1 |█████   █████████     █ █████ █  ███████  █  █ █████      █████ █  ███████      
5034.3 |█████   █████████ ██ ████████ █ ████████ ██ ██ █████     ██████ █  ████████ █  █
4794.5 |██████ █████████████ ████████ ██████████ █████ █████ ██  ██████ ██ ████████ ██ █
4554.7 |████████████████████ ████████ ██████████ ███████████ ██ ███████████████████ ████
4314.8 |████████████████████ ███████████████████ ██████████████████████████████████ ████
4075.0 |████████████████████ ███████████████████ ██████████████████████████████████ ████
3835.2 |████████████████████████████████████████ ██████████████████████████████████ ████
3595.3 |████████████████████████████████████████ ███████████████████████████████████████
3355.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20897
   1 ms |   77
   2 ms |   15
   3 ms |   3
   5 ms |   2
   6 ms |   2
   7 ms |   1
   9 ms |   1
  14 ms |   2
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:plains`
- `fps_1pct_low` = `687.35`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `31.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-17.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `7.00`
- `surface_water_ratio` = `0.02`
- `entity_count_sample_start` = `48.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7411.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `199.44`
- `fps_harmonic_avg` = `4577.01`

### Jungle flyby (single-biome world) (`chunk_jungle`)

Category: **Chunks**  |  Duration: 30703 ms  |  Sample ticks: 400

**FPS**  avg `3683.39`, min `33.53`, p50 `3652.30`, p95 `5136.11`, p99 `5500.55`, 1%low `477.95`, 0.1%low `95.07`, std `858.15`

**Frame time (ms)**  avg `0.30`, p50 `0.27`, p95 `0.43`, p99 `0.85`, p99.9 `2.15`, max `29.82`

**Client tick (ms)**  avg `0.68`, p95 `0.84`, max `1.58`

**Memory**  start `6008 MB`, end `2830 MB`, peak `7334 MB`, GC `16 events / 1100 ms`

**FPS over sampling window (ASCII):**

```
4032.9 |       ██                                                                       
3934.2 |      ███ █             ██       ██       █       █                             
3835.4 | ██ █ ███ █   ███     ████ █    ███ ██   ██      ██       ██       █            
3736.7 | ████████ █ █████   ██████ █ ██████ ███████    ████  █   ███  █   ██      ██    
3638.0 | ████████ █████████ ██████ █ ██████ ███████  ███████ ███████  ██  ██ █  ████  █ 
3539.3 | ████████ █████████ ████████ ██████ ███████ ████████ ███████ ███████ █ █████ ███
3440.5 | ██████████████████ ███████████████ ████████████████ ███████ ███████ ███████ ███
3341.8 | ██████████████████████████████████ ████████████████ ███████ ███████ ███████ ███
3243.1 | ███████████████████████████████████████████████████ ███████████████ ███████ ███
3144.3 |████████████████████████████████████████████████████ ███████████████████████ ███
3045.6 |████████████████████████████████████████████████████████████████████████████ ███
2946.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20844
   1 ms |   131
   2 ms |   11
   3 ms |   2
   4 ms |   2
   5 ms |   1
   7 ms |   1
  12 ms |   1
  14 ms |   1
  20 ms |   1
  21 ms |   1
  23 ms |   1
  24 ms |   1
  28 ms |   1
  29 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:jungle`
- `fps_1pct_low` = `477.95`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `14.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-16.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `8.00`
- `surface_water_ratio` = `0.07`
- `entity_count_sample_start` = `30.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7417.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `95.07`
- `fps_harmonic_avg` = `3303.12`

### Desert flyby (single-biome world) (`chunk_desert`)

Category: **Chunks**  |  Duration: 29054 ms  |  Sample ticks: 400

**FPS**  avg `5212.73`, min `71.33`, p50 `5611.67`, p95 `6075.33`, p99 `6165.23`, 1%low `689.39`, 0.1%low `180.35`, std `1002.76`

**Frame time (ms)**  avg `0.21`, p50 `0.18`, p95 `0.34`, p99 `0.64`, p99.9 `2.29`, max `14.02`

**Client tick (ms)**  avg `0.64`, p95 `0.78`, max `1.44`

**Memory**  start `4702 MB`, end `6912 MB`, peak `7458 MB`, GC `12 events / 724 ms`

**FPS over sampling window (ASCII):**

```
5675.6 |           █              █         █        █                                  
5544.0 |     █  ████         ██ ███     █  ██     █  █                         █        
5412.4 |█    █ ██████ █      ██████ █   ██████    █ ██ ██           ███      █ █        
5280.7 |██   ██████████ █  ████████ █  ███████  ███████████   ██   ████ █  █ █████  █  █
5149.1 |██  ███████████ █  ████████ █ █████████ ███████████   █████████ █  ███████  █ ██
5017.4 |██  ███████████ ███████████ █ █████████ ███████████  ██████████ █  ███████  ████
4885.8 |██ ████████████ ███████████ █ █████████ ███████████ ███████████ █ ████████  ████
4754.1 |██ ████████████ ███████████ █ █████████ ███████████ ███████████ █ ████████  ████
4622.5 |██ ████████████ ███████████ ███████████████████████ ███████████ █ ████████  ████
4490.9 |██ ████████████ ███████████ ███████████████████████████████████ █ ██████████████
4359.2 |███████████████ ████████████████████████████████████████████████████████████████
4227.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20915
   1 ms |   58
   2 ms |   16
   3 ms |   2
   5 ms |   2
   7 ms |   1
   8 ms |   1
   9 ms |   2
  10 ms |   1
  11 ms |   1
  14 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:desert`
- `fps_1pct_low` = `689.39`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `33.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-11.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `36.00`
- `surface_water_ratio` = `0.02`
- `entity_count_sample_start` = `44.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7433.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `180.35`
- `fps_harmonic_avg` = `4699.10`

### Taiga flyby (single-biome world) (`chunk_taiga`)

Category: **Chunks**  |  Duration: 29603 ms  |  Sample ticks: 400

**FPS**  avg `5472.51`, min `41.53`, p50 `5889.28`, p95 `6397.95`, p99 `6497.73`, 1%low `621.43`, 0.1%low `120.82`, std `1033.66`

**Frame time (ms)**  avg `0.20`, p50 `0.17`, p95 `0.31`, p99 `0.57`, p99.9 `2.03`, max `24.08`

**Client tick (ms)**  avg `0.64`, p95 `0.77`, max `1.31`

**Memory**  start `5380 MB`, end `3778 MB`, peak `7738 MB`, GC `23 events / 936 ms`

**FPS over sampling window (ASCII):**

```
6054.0 |                                   ██           █                               
5901.6 |                             █     ██   █      ███  █     █                     
5749.3 |    █  ██ ██     ██████      ████████   █    ██████ █ ███████                   
5597.0 |    █████ ██  █ ███████      ████████  ███   ██████ █ ███████   █     █         
5444.6 |  █ ████████ ██ █████████ █ █████████  ███  ███████ █ ███████   █    █████  █   
5292.3 |█ ██████████ ██ █████████ █ █████████  ███ ████████ ██████████  █   ██████  ██  
5140.0 |█ ██████████ ██ ███████████████████████████████████ ███████████ █   ██████  ██  
4987.6 |█ ██████████ ██████████████████████████████████████ ███████████ █ ████████  ███ 
4835.3 |█ █████████████████████████████████████████████████ ███████████ █ ██████████████
4683.0 |███████████████████████████████████████████████████████████████ █ ██████████████
4530.6 |███████████████████████████████████████████████████████████████ ████████████████
4378.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20942
   1 ms |   36
   2 ms |   9
   3 ms |   2
   4 ms |   2
   7 ms |   1
   9 ms |   1
  11 ms |   2
  15 ms |   1
  18 ms |   1
  20 ms |   2
  24 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:taiga`
- `fps_1pct_low` = `621.43`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `7.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-14.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `7.00`
- `surface_water_ratio` = `0.04`
- `entity_count_sample_start` = `21.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7451.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `120.82`
- `fps_harmonic_avg` = `4906.62`

### Snowy plains flyby (`chunk_snowy`)

Category: **Chunks**  |  Duration: 28906 ms  |  Sample ticks: 400

**FPS**  avg `5717.83`, min `70.29`, p50 `6188.12`, p95 `6743.09`, p99 `6849.32`, 1%low `685.06`, 0.1%low `167.65`, std `1140.94`

**Frame time (ms)**  avg `0.20`, p50 `0.16`, p95 `0.31`, p99 `0.57`, p99.9 `2.87`, max `14.23`

**Client tick (ms)**  avg `0.59`, p95 `0.71`, max `1.70`

**Memory**  start `5166 MB`, end `3054 MB`, peak `7518 MB`, GC `18 events / 1449 ms`

**FPS over sampling window (ASCII):**

```
6193.2 |                                █              █          █                     
5988.2 |█████ ███       ██████    ██ █ ███      █ ██ ████       ███ █ █    ███  ██  ██  
5783.3 |█████ ███    █ ███████   █████████  █ ██████ ████      ██████ █ █ ████████ ████ 
5578.3 |█████████    █████████   ██████████ █████████████   █████████ █ ██████████ ████ 
5373.3 |██████████ █ █████████ █ ██████████ █████████████  ██████████ ████████████ █████
5168.4 |██████████ █ █████████ ████████████ █████████████ ███████████ ████████████ █████
4963.4 |████████████ ██████████████████████ █████████████ ███████████ ████████████ █████
4758.5 |████████████ ██████████████████████ █████████████ ████████████████████████ █████
4553.5 |███████████████████████████████████ █████████████ ████████████████████████ █████
4348.6 |█████████████████████████████████████████████████ ████████████████████████ █████
4143.6 |██████████████████████████████████████████████████████████████████████████ █████
3938.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20931
   1 ms |   35
   2 ms |   13
   3 ms |   9
   4 ms |   4
   5 ms |   2
   7 ms |   1
  10 ms |   2
  11 ms |   1
  12 ms |   1
  14 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:snowy_plains`
- `fps_1pct_low` = `685.06`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `6.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-12.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `18.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7457.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `167.65`
- `fps_harmonic_avg` = `5116.36`

### Forest flyby (`chunk_forest`)

Category: **Chunks**  |  Duration: 30154 ms  |  Sample ticks: 400

**FPS**  avg `4939.75`, min `43.52`, p50 `5060.73`, p95 `6373.49`, p99 `6540.22`, 1%low `627.81`, 0.1%low `154.96`, std `1089.79`

**Frame time (ms)**  avg `0.23`, p50 `0.20`, p95 `0.34`, p99 `0.70`, p99.9 `2.49`, max `22.98`

**Client tick (ms)**  avg `0.63`, p95 `0.78`, max `1.74`

**Memory**  start `1760 MB`, end `4894 MB`, peak `7660 MB`, GC `22 events / 1411 ms`

**FPS over sampling window (ASCII):**

```
5350.9 | █ █        █     █          ██                                                 
5195.7 | █ ████ █ ████  ███  █ █ ██████        ███                                      
5040.5 |███████ █ █████████  ██████████ █ ████ ███      ██ ███ █     ███                
4885.3 |███████ █ █████████  ██████████ ██████████  ██████████ ██ █ ████       ████  ██ 
4730.2 |███████ █ █████████████████████ ██████████ ███████████ █████████ █ ████████ ████
4575.0 |█████████ █████████████████████ ██████████ ███████████ █████████ █ ████████ ████
4419.8 |███████████████████████████████ ██████████ ███████████ ████████████████████ ████
4264.6 |██████████████████████████████████████████████████████ ████████████████████ ████
4109.4 |██████████████████████████████████████████████████████ ████████████████████ ████
3954.3 |███████████████████████████████████████████████████████████████████████████ ████
3799.1 |███████████████████████████████████████████████████████████████████████████ ████
3643.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20909
   1 ms |   65
   2 ms |   9
   3 ms |   11
   9 ms |   1
  10 ms |   1
  11 ms |   1
  15 ms |   1
  16 ms |   1
  22 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:forest`
- `fps_1pct_low` = `627.81`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `30.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `20.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `7.00`
- `surface_water_ratio` = `0.09`
- `entity_count_sample_start` = `10.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7477.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `154.96`
- `fps_harmonic_avg` = `4420.13`

### Savanna flyby (`chunk_savanna`)

Category: **Chunks**  |  Duration: 29653 ms  |  Sample ticks: 400

**FPS**  avg `5766.29`, min `63.56`, p50 `6253.91`, p95 `6825.94`, p99 `6915.63`, 1%low `766.56`, 0.1%low `186.17`, std `1157.03`

**Frame time (ms)**  avg `0.19`, p50 `0.16`, p95 `0.31`, p99 `0.56`, p99.9 `2.10`, max `15.73`

**Client tick (ms)**  avg `0.65`, p95 `0.76`, max `1.56`

**Memory**  start `2800 MB`, end `7192 MB`, peak `7428 MB`, GC `12 events / 748 ms`

**FPS over sampling window (ASCII):**

```
6313.0 |             █    █ █       █  █             █                                  
6164.8 |             ███  █ █    █  ██ █    █    █ ████ █          ██                   
6016.6 |   █ ███    ████  ███    █  ████ █  ██  ███████ █         ████        █ ██      
5868.4 |  ██████   ██████████    ███████ █  ███ █████████   ██  ██████ █  ██  █ ██      
5720.2 |  ██████   ██████████   ████████ █  █████████████   ██████████ █ ███ ██ ██      
5572.0 |████████   ██████████ ██████████ █ ██████████████ ████████████ █ ███ █████  █   
5423.9 |████████   ██████████ ██████████ █ ██████████████ ████████████ █ █████████  █   
5275.7 |████████ █ ██████████ ██████████ █ ██████████████ ████████████ ███████████  █  █
5127.5 |████████ █ ██████████████████████████████████████ ████████████ ███████████  █  █
4979.3 |██████████ ██████████████████████████████████████ ████████████ ███████████  █  █
4831.1 |██████████████████████████████████████████████████████████████████████████  ████
4682.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20953
   1 ms |   24
   2 ms |   9
   3 ms |   4
   4 ms |   1
   5 ms |   2
   6 ms |   2
   7 ms |   2
   9 ms |   1
  11 ms |   1
  15 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:savanna`
- `fps_1pct_low` = `766.56`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `24.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-37.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `61.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7481.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `186.17`
- `fps_harmonic_avg` = `5192.00`

### Swamp flyby (`chunk_swamp`)

Category: **Chunks**  |  Duration: 32909 ms  |  Sample ticks: 400

**FPS**  avg `4969.21`, min `53.98`, p50 `5165.29`, p95 `6381.62`, p99 `6535.95`, 1%low `336.16`, 0.1%low `111.93`, std `1166.60`

**Frame time (ms)**  avg `0.24`, p50 `0.19`, p95 `0.36`, p99 `0.72`, p99.9 `6.67`, max `18.52`

**Client tick (ms)**  avg `0.67`, p95 `0.89`, max `3.69`

**Memory**  start `2400 MB`, end `6608 MB`, peak `7358 MB`, GC `35 events / 1795 ms`

**FPS over sampling window (ASCII):**

```
5433.7 |                               █         █          █         ██        ██      
5260.7 |         █         ██       ████     █████  █  █    ██    █  ███       ████    █
5087.7 |    █ ████   █ ██████    █ █████   ████████ █ ████████ █████████    ███████    █
4914.7 |█   █ █████ ██ ███████  ████████   ████████ █ ████████ █████████    ███████  █ █
4741.7 |█   ███████ ██ ███████  ████████ █ ████████ ██████████ ██████████ █████████  ███
4568.7 |█ █ ███████ ██████████ █████████ ██████████ ██████████ ████████████████████  ███
4395.7 |███████████ ██████████ ████████████████████ ██████████ ████████████████████ ████
4222.6 |███████████ ██████████ ████████████████████ ██████████ ████████████████████ ████
4049.6 |███████████ ██████████ ███████████████████████████████ ████████████████████ ████
3876.6 |██████████████████████ █████████████████████████████████████████████████████████
3703.6 |██████████████████████ █████████████████████████████████████████████████████████
3530.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20843
   1 ms |   39
   2 ms |   27
   3 ms |   50
   4 ms |   13
   5 ms |   3
   6 ms |   7
   7 ms |   10
   8 ms |   2
   9 ms |   2
  11 ms |   2
  17 ms |   1
  18 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:swamp`
- `fps_1pct_low` = `336.16`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `77.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `512.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `72.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `7.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `5.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7487.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `111.93`
- `fps_harmonic_avg` = `4149.92`

### Cherry grove flyby (`chunk_cherry`)

Category: **Chunks**  |  Duration: 29803 ms  |  Sample ticks: 400

**FPS**  avg `5751.68`, min `56.16`, p50 `6222.78`, p95 `6743.09`, p99 `6844.63`, 1%low `649.58`, 0.1%low `135.30`, std `1111.36`

**Frame time (ms)**  avg `0.19`, p50 `0.16`, p95 `0.30`, p99 `0.56`, p99.9 `1.88`, max `17.81`

**Client tick (ms)**  avg `0.72`, p95 `0.93`, max `1.45`

**Memory**  start `3214 MB`, end `3848 MB`, peak `7274 MB`, GC `16 events / 918 ms`

**FPS over sampling window (ASCII):**

```
6384.0 |                                  █           █                                 
6207.5 |       █         ██ ██         ████      █ █  ██        █  ██                   
6030.9 |█ ██████      █████ ██  █  ██ █████      █ █████       ██████           █       
5854.4 |█ ██████   █  ████████  ██ █████████    ██ ██████      ███████       ██ ██      
5677.8 |█████████ ██  ████████ ███ █████████    █████████     ████████  █   ██████      
5501.3 |█████████ ██ █████████ █████████████    █████████     ████████ ██ ████████   █  
5324.8 |█████████ ████████████ █████████████ █████████████ █ █████████ ██ ████████ █████
5148.2 |█████████ ██████████████████████████ █████████████ ███████████ ██ ████████ █████
4971.7 |████████████████████████████████████ █████████████████████████ ███████████ █████
4795.1 |██████████████████████████████████████████████████████████████████████████ █████
4618.6 |██████████████████████████████████████████████████████████████████████████ █████
4442.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20938
   1 ms |   41
   2 ms |   7
   3 ms |   2
   4 ms |   2
   6 ms |   1
   7 ms |   1
   9 ms |   1
  10 ms |   1
  12 ms |   2
  15 ms |   2
  16 ms |   1
  17 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:cherry_grove`
- `fps_1pct_low` = `649.58`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `14.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-30.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `44.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7499.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `135.30`
- `fps_harmonic_avg` = `5144.85`

### Badlands flyby (`chunk_badlands`)

Category: **Chunks**  |  Duration: 33753 ms  |  Sample ticks: 400

**FPS**  avg `6010.52`, min `50.37`, p50 `6531.68`, p95 `7042.25`, p99 `7132.67`, 1%low `851.10`, 0.1%low `188.80`, std `1177.19`

**Frame time (ms)**  avg `0.18`, p50 `0.15`, p95 `0.29`, p99 `0.53`, p99.9 `1.29`, max `19.85`

**Client tick (ms)**  avg `0.57`, p95 `0.76`, max `1.13`

**Memory**  start `2376 MB`, end `6844 MB`, peak `7572 MB`, GC `37 events / 954 ms`

**FPS over sampling window (ASCII):**

```
6722.3 |                          █    █                                                
6512.8 |            ███           █ █ ██        █ ██        █ █ ██             █ █      
6303.4 |██ █      █ █████       ███ █ ██  █  █ █████ ██     █ █ ██          ██████      
6093.9 |████     ██ ██████   █  ███ ████  ██ █ █████ ██ █ █ ███ ████ █     ███████   █  
5884.5 |████  ██ █████████ █ █ █████████ ███ ██████████ ███ ████████ █   █ ███████ ███  
5675.0 |█████ ████████████ █ █ █████████ ███ ██████████ ████████████ ██  █████████ ███  
5465.6 |██████████████████ █ █ █████████ ███ ██████████ ████████████ █████████████ ███  
5256.1 |████████████████████████████████ ███ ██████████ ██████████████████████████ █████
5046.6 |████████████████████████████████ █████████████████████████████████████████ █████
4837.2 |██████████████████████████████████████████████████████████████████████████ █████
4627.7 |██████████████████████████████████████████████████████████████████████████ █████
4418.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20970
   1 ms |   16
   2 ms |   4
   3 ms |   5
   5 ms |   1
  15 ms |   1
  16 ms |   1
  17 ms |   1
  19 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:badlands`
- `fps_1pct_low` = `851.10`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `18.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `512.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `17.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `1.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7507.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `188.80`
- `fps_harmonic_avg` = `5449.51`

### Dark forest flyby (dense canopy) (`chunk_dark_forest`)

Category: **Chunks**  |  Duration: 29604 ms  |  Sample ticks: 400

**FPS**  avg `4876.67`, min `32.41`, p50 `5162.62`, p95 `6123.70`, p99 `6246.10`, 1%low `616.39`, 0.1%low `149.49`, std `1165.03`

**Frame time (ms)**  avg `0.23`, p50 `0.19`, p95 `0.39`, p99 `0.72`, p99.9 `1.86`, max `30.85`

**Client tick (ms)**  avg `0.63`, p95 `1.00`, max `1.18`

**Memory**  start `1732 MB`, end `5258 MB`, peak `7600 MB`, GC `36 events / 945 ms`

**FPS over sampling window (ASCII):**

```
5381.2 |             █                      █      █      █                    █ █      
5211.9 |  █          █      █               █ █   ██ ██ ████  ██   ████      ██████     
5042.5 | ██       █  █      █     █         █ █   ██████████  ██  ██████ █  ███████     
4873.1 | ███ ██   ██ █  █   █ ███ ███   █ █ ████  ██████████  ██████████ █ ████████  ███
4703.8 | ███████  ████  █  ██ ███ ███ █ █ █ ████ ███████████ ███████████ ██████████  ███
4534.4 | ████████ ████  ██ ██ █████████ ███ ████ ██████████████████████████████████ ████
4365.0 | ██████████████ ██ ██ █████████ ████████ ██████████████████████████████████ ████
4195.7 |██████████████████ █████████████████████ ██████████████████████████████████ ████
4026.3 |███████████████████████████████████████████████████████████████████████████ ████
3857.0 |███████████████████████████████████████████████████████████████████████████ ████
3687.6 |███████████████████████████████████████████████████████████████████████████ ████
3518.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20900
   1 ms |   80
   2 ms |   9
   3 ms |   1
   4 ms |   3
   5 ms |   3
   9 ms |   1
  16 ms |   1
  25 ms |   1
  30 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:dark_forest`
- `fps_1pct_low` = `616.39`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `14.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-12.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `8.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `26.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7517.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `149.49`
- `fps_harmonic_avg` = `4289.03`

### Windswept hills flyby (`chunk_mountain`)

Category: **Chunks**  |  Duration: 29303 ms  |  Sample ticks: 400

**FPS**  avg `5497.60`, min `63.09`, p50 `5941.77`, p95 `6377.55`, p99 `6464.12`, 1%low `775.08`, 0.1%low `193.18`, std `1020.82`

**Frame time (ms)**  avg `0.20`, p50 `0.17`, p95 `0.31`, p99 `0.57`, p99.9 `2.12`, max `15.85`

**Client tick (ms)**  avg `0.58`, p95 `0.66`, max `1.27`

**Memory**  start `4794 MB`, end `7012 MB`, peak `7790 MB`, GC `12 events / 651 ms`

**FPS over sampling window (ASCII):**

```
5980.3 |                      █            █            █  █                            
5812.0 | █   ██ ██         ██ █  █   █ ███ █        ██ ██  █   █  █                     
5643.7 | █ ███████      ███████  █ █ ███████  ███   ██ ██  ███ █ ██  █    ██ █ █ █  █  █
5475.4 |██ ████████ █  ████████ ██ █████████  ████████ ██  ███ ████ ██ █  ████████ ██  █
5307.1 |███████████ █  ████████ ██ █████████  ████████████ ███████████ █ █████████ ██  █
5138.8 |███████████ █  ████████ ████████████ █████████████ ███████████ █ █████████ █████
4970.5 |███████████ ████████████████████████ █████████████ ███████████ █ █████████ █████
4802.2 |███████████ ████████████████████████ █████████████████████████ ███████████ █████
4633.9 |███████████ ██████████████████████████████████████████████████ ███████████ █████
4465.5 |██████████████████████████████████████████████████████████████████████████ █████
4297.2 |██████████████████████████████████████████████████████████████████████████ █████
4128.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20955
   1 ms |   23
   2 ms |   8
   3 ms |   8
   5 ms |   1
   7 ms |   1
   9 ms |   1
  11 ms |   1
  12 ms |   1
  15 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:windswept_hills`
- `fps_1pct_low` = `775.08`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `78.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `62.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.06`
- `entity_count_sample_start` = `16.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7523.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `193.18`
- `fps_harmonic_avg` = `5010.02`

### Idle baseline (orbit, flat world) (`idle_baseline`)

Category: **Baseline**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `6379.55`, min `1269.84`, p50 `6761.33`, p95 `7097.23`, p99 `7153.08`, 1%low `1814.15`, 0.1%low `1348.87`, std `907.96`

**Frame time (ms)**  avg `0.16`, p50 `0.15`, p95 `0.22`, p99 `0.37`, p99.9 `0.72`, max `0.79`

**Client tick (ms)**  avg `0.48`, p95 `0.51`, max `0.91`

**Memory**  start `1828 MB`, end `4718 MB`, peak `7676 MB`, GC `7 events / 1083 ms`

**FPS over sampling window (ASCII):**

```
6715.9 |                      █                                                         
6639.8 |                      █      █                            █            █        
6563.7 |█ █               █ █ █   █  █        █         █ █ █ █   █            █ ██  █  
6487.6 |███ █             ███ ██ ██  █   █  █ █   █  ██ █ █ █ ██  █  █       ███ ██  ██ 
6411.4 |█████        █    █████████  █  ██ ██ █ █████████████ █████  █      ████ ██████ 
6335.3 |█████        █  █ █████████  █████ ██ █████████████████████  ███ █ ████████████ 
6259.2 |█████ █      ██ █ █████████  █████ ████████████████████████  ███ █ ████████████ 
6183.1 |███████ ██  ███ █ █████████ ██████ █████████████████████████ ███ █ ████████████ 
6107.0 |███████ ██  ███████████████ ██████ █████████████████████████ ███████████████████
6030.8 |███████ ██████████████████████████ █████████████████████████████████████████████
5954.7 |██████████████████████████████████ █████████████████████████████████████████████
5878.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  21000
```

**Extras:**

- `preset_quick` = `1.00`
- `preload_duration_ms` = `49.00`
- `entity_count_sample_start` = `1.00`
- `preset_full` = `0.00`
- `preset_long` = `0.00`
- `entity_count_delta` = `0.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `1348.87`
- `entity_count_sample_end` = `1.00`
- `fps_harmonic_avg` = `6139.88`
- `fps_1pct_low` = `1814.15`
- `seed` = `1923.00`

### Particle diversity stress (16 types simultaneously) (`particle_diversity_stress`)

Category: **Stress**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `495.26`, min `149.51`, p50 `512.98`, p95 `554.66`, p99 `568.39`, 1%low `200.67`, 0.1%low `178.00`, std `68.73`

**Frame time (ms)**  avg `2.09`, p50 `1.95`, p95 `2.56`, p99 `4.77`, p99.9 `5.20`, max `6.69`

**Client tick (ms)**  avg `2.45`, p95 `2.71`, max `3.99`

**Memory**  start `1842 MB`, end `1280 MB`, peak `7796 MB`, GC `4 events / 226 ms`

**FPS over sampling window (ASCII):**

```
522.1 |██                                                                              
513.8 |██                    █                ███          ██         █                
505.4 |███ █                 █     █████ ██  ████ ██     ████ █ █ █ █ ███              
497.0 |██████ █      █       ███ ███████ █████████████  █████████ ████████ █           
488.6 |█████████ █ █████ ██████████████████████████████ █████████████████████ █ █      
480.2 |██████████████████████████████████████████████████████████████████████ ███      
471.8 |██████████████████████████████████████████████████████████████████████████     █
463.4 |████████████████████████████████████████████████████████████████████████████   █
455.0 |██████████████████████████████████████████████████████████████████████████████ █
446.6 |██████████████████████████████████████████████████████████████████████████████ █
438.2 |██████████████████████████████████████████████████████████████████████████████ █
429.8 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   1 ms | ████████████████████████████████████████  6100
   2 ms | ████████████████████  3047
   3 ms |   8
   4 ms | ██  371
   5 ms |   27
   6 ms |   2
```

**Extras:**

- `preload_chunks` = `81.00`
- `fps_1pct_low` = `200.67`
- `preset_long` = `0.00`
- `particle_types` = `16.00`
- `particles_spawned` = `256000.00`
- `seed` = `2521.00`
- `preload_duration_ms` = `49.00`
- `fps_harmonic_avg` = `477.78`
- `preset_quick` = `1.00`
- `entity_count_delta` = `0.00`
- `entity_count_sample_end` = `1.00`
- `fps_0p1pct_low` = `178.00`
- `preset_full` = `0.00`
- `entity_count_sample_start` = `1.00`

### Fluid spread (water basin, 4-step periodic reset) (`fluid_spread`)

Category: **Fluids**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `5401.48`, min `468.27`, p50 `5780.35`, p95 `6257.82`, p99 `6313.13`, 1%low `1540.67`, 0.1%low `1104.39`, std `900.89`

**Frame time (ms)**  avg `0.19`, p50 `0.17`, p95 `0.27`, p99 `0.46`, p99.9 `0.79`, max `2.14`

**Client tick (ms)**  avg `0.49`, p95 `0.53`, max `0.84`

**Memory**  start `2132 MB`, end `5198 MB`, peak `7778 MB`, GC `4 events / 224 ms`

**FPS over sampling window (ASCII):**

```
5790.2 |                                                        █                       
5714.5 |    ██   █                                              █     █     ██   ██     
5638.8 |    ██   █       █                                  █   ██    ██  █████ ███    █
5563.1 | █  ██   █       █        █          █              █   ██ █  ██  █████████  █ █
5487.4 | ██ ██  ███      █ █      █       █  ██        ██   █   ██ █  ██ ██████████ ██ █
5411.7 | ██ ██  ███      █ █   █  █       █  ██      ████   █   ██ █ ███ ██████████ ██ █
5336.0 |███ ███ ███ █ █ ██ ██ ██  █ ███ ███ ███ █ █  ████  ██   ██ █ █████████████████ █
5260.3 |███████ ███ ██████ ██ ███ █ ███ ███ ███ ███ █████  ██   ██████████████████████ █
5184.6 |███████ ███ ██████ ██████ █████████████ ███ █████  ██   ████████████████████████
5108.9 |███████ ██████████ ████████████████████████████████████ ████████████████████████
5033.2 |███████████████████████████████████████████████████████ ████████████████████████
4957.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20998
   1 ms |   1
   2 ms |   1
```

**Extras:**

- `fps_1pct_low` = `1540.67`
- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`
- `block_state_changes` = `2317.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `0.00`
- `waves_spawned` = `6.00`
- `scheduled_block_ticks` = `0.00`
- `preload_duration_ms` = `50.00`
- `sources_placed_total` = `54.00`
- `entity_count_sample_start` = `1.00`
- `preset_long` = `0.00`
- `seed` = `9043.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `1104.39`
- `neighbour_updates` = `0.00`
- `scheduled_fluid_ticks` = `3170.00`
- `fps_harmonic_avg` = `5153.17`

### Lighting update (16×16 glowstone reveal/hide) (`lighting_update`)

Category: **Lighting**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `5903.89`, min `520.86`, p50 `6277.46`, p95 `6666.67`, p99 `6761.33`, 1%low `1519.36`, 0.1%low `828.64`, std `878.60`

**Frame time (ms)**  avg `0.18`, p50 `0.16`, p95 `0.24`, p99 `0.41`, p99.9 `0.84`, max `1.92`

**Client tick (ms)**  avg `0.54`, p95 `0.59`, max `0.96`

**Memory**  start `2864 MB`, end `1754 MB`, peak `7812 MB`, GC `10 events / 814 ms`

**FPS over sampling window (ASCII):**

```
6288.6 |                                                                         █  █ █ 
6195.1 |                                                        ██     █ █  █    █  █ █ 
6101.7 |  █ █    █  █     █        █                            ██    ██ ████ ████  █ █ 
6008.3 | ███████ █  █     ██ ██ ██ █      █ █    █     █  █     ██  █ ██ ████ ████  █ ██
5914.9 |██████████  █ █ ████ ██ ████      █ ██ █ ██  █ █  █   █ ██  ██████████████  █ ██
5821.5 |████████████████████ ██ ████      █ ██ ████  ███ ██   █ ███ ██████████████  █ ██
5728.1 |████████████████████ ███████ ████ █ ██ ████  ███ ██ █ █ ███████████████████ ████
5634.7 |███████████████████████████████████ ██ ██████████████ █ ███████████████████ ████
5541.3 |███████████████████████████████████ █████████████████ █ ████████████████████████
5447.9 |███████████████████████████████████ ████████████████████████████████████████████
5354.5 |███████████████████████████████████ ████████████████████████████████████████████
5261.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20986
   1 ms |   14
```

**Extras:**

- `entity_count_sample_end` = `1.00`
- `toggles` = `22.00`
- `preload_chunks` = `81.00`
- `preset_long` = `0.00`
- `fps_0p1pct_low` = `828.64`
- `preset_quick` = `1.00`
- `preset_full` = `0.00`
- `fps_harmonic_avg` = `5652.50`
- `fps_1pct_low` = `1519.36`
- `preload_duration_ms` = `50.00`
- `scheduled_block_ticks` = `0.00`
- `entity_count_delta` = `0.00`
- `blocks_per_toggle` = `256.00`
- `neighbour_updates` = `0.00`
- `block_state_changes` = `4864.00`
- `seed` = `9007.00`
- `entity_count_sample_start` = `1.00`

### Hopper grid 20×20 (transfer storm) (`hopper_grid`)

Category: **Block-Entities**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `5079.25`, min `480.01`, p50 `5379.24`, p95 `5694.76`, p99 `5797.10`, 1%low `1452.51`, 0.1%low `956.58`, std `711.15`

**Frame time (ms)**  avg `0.20`, p50 `0.19`, p95 `0.26`, p99 `0.49`, p99.9 `0.81`, max `2.08`

**Client tick (ms)**  avg `0.47`, p95 `0.51`, max `0.93`

**Memory**  start `5980 MB`, end `1686 MB`, peak `7778 MB`, GC `8 events / 399 ms`

**FPS over sampling window (ASCII):**

```
5382.9 |                                                           █ █           █      
5296.1 |                                                        ██████           █   █  
5209.4 |     █ █ █   ██  █   ██            ██ █ █ █            ███████      █  █ █   █  
5122.7 |█   ████ ██ ████ ██ █████     ██ █ ████ █ ███          ████████ █   █ ██ ██  █ █
5036.0 |███ ████ ███████ ████████  █  █████████ █ ███ █ █ █   ████████████ ██ █████ ██ █
4949.3 |███ ████ ███████████████████████████████████████████  ████████████ ██ ████████ █
4862.5 |███ ████████████████████████████████████████████████  ████████████ ██ ██████████
4775.8 |███ ████████████████████████████████████████████████  ███████████████ ██████████
4689.1 |███ ████████████████████████████████████████████████ ████████████████ ██████████
4602.4 |███ ████████████████████████████████████████████████ ███████████████████████████
4515.6 |████████████████████████████████████████████████████ ███████████████████████████
4428.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20989
   1 ms |   10
   2 ms |   1
```

**Extras:**

- `entity_count_sample_end` = `1.00`
- `preload_chunks` = `81.00`
- `preset_long` = `0.00`
- `fps_0p1pct_low` = `956.58`
- `preset_quick` = `1.00`
- `restocks` = `20.00`
- `preset_full` = `0.00`
- `fps_harmonic_avg` = `4895.35`
- `fps_1pct_low` = `1452.51`
- `preload_duration_ms` = `52.00`
- `scheduled_block_ticks` = `0.00`
- `entity_count_delta` = `0.00`
- `neighbour_updates` = `0.00`
- `block_state_changes` = `0.00`
- `hoppers_built` = `400.00`
- `seed` = `8011.00`
- `entity_count_sample_start` = `1.00`

### Comparator storage (8×8 chests + comparators) (`comparator_storage`)

Category: **Block-Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `4265.54`, min `482.79`, p50 `4498.43`, p95 `4810.00`, p99 `4928.54`, 1%low `1359.67`, 0.1%low `952.52`, std `600.50`

**Frame time (ms)**  avg `0.24`, p50 `0.22`, p95 `0.32`, p99 `0.56`, p99.9 `0.83`, max `2.07`

**Client tick (ms)**  avg `0.48`, p95 `0.52`, max `0.91`

**Memory**  start `5718 MB`, end `6616 MB`, peak `7790 MB`, GC `10 events / 911 ms`

**FPS over sampling window (ASCII):**

```
4522.9 |         █                                        █  █                          
4464.8 |         █                                     █  █  █                          
4406.6 |        ██            █                   █  █ █ ██  █ █          █   ██        
4348.5 |  █     ██ ██   █  ██ █    █         █    █  █ █ ███ █ █       █  █   ██ █ █    
4290.3 |████   ███ ██   █  ██ ██ █ █  ██  █  ██   ██ ███ ██████████ █  █  █   ██ █ █  █ 
4232.1 |█████  ███ ██   █ ███ ██ █ █ ████ ██████ ███ ███ ████████████  ████ █ ██ ██████ 
4174.0 |█████  ███████  ████████████ ████ ██████ ███ ██████████████████████ █ ██ ██████ 
4115.8 |█████  ███████ █████████████████████████ ███ ████████████████████████ ██ ██████ 
4057.7 |██████████████ █████████████████████████████ ███████████████████████████████████
3999.5 |██████████████ █████████████████████████████████████████████████████████████████
3941.4 |██████████████ █████████████████████████████████████████████████████████████████
3883.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20996
   1 ms |   1
   2 ms |   3
```

**Extras:**

- `fps_1pct_low` = `1359.67`
- `oscillations` = `20.00`
- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`
- `block_state_changes` = `1152.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `0.00`
- `scheduled_block_ticks` = `1152.00`
- `comparators_built` = `64.00`
- `preload_duration_ms` = `37.00`
- `chests_built` = `64.00`
- `entity_count_sample_start` = `1.00`
- `preset_long` = `0.00`
- `seed` = `8053.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `952.52`
- `neighbour_updates` = `0.00`
- `fps_harmonic_avg` = `4122.29`

