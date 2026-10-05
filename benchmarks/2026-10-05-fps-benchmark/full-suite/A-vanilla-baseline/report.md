# FPS Test session — 2026-10-05T17:31:01.1973274+08:00

- Minecraft: `1.21.1`
- OS: `Windows 10 10.0 (amd64)`
- CPU cores: `16`
- Java: `22.0.2` (Java HotSpot(TM) 64-Bit Server VM)
- Max heap: `8192 MB`
- GPU: `AMD Radeon RX 7800 XT / 3.2.0 Core Profile Context 25.12.1.251128`

## Summary

| # | Benchmark | Cat. | Avg FPS | 1% low | 0.1% low | p99 frame ms | Tick avg ms | GC | Heap Δ MB |
|---:|---|---|---:|---:|---:|---:|---:|---:|---:|
| 1 | [Particle cycle (7 types → all together)](#particle-cycle-7-types--all-together) | Particles | 3507.9 | 890.1 | 603.6 | 0.66 | 1.00 | 8 | 5464 |
| 2 | [Cows ×200 ring](#cows-200-ring) | Entities | 2238.3 | 688.6 | 573.0 | 1.02 | 0.97 | 15 | 852 |
| 3 | [Sheep ×200 ring](#sheep-200-ring) | Entities | 1242.4 | 532.8 | 443.6 | 1.76 | 0.95 | 12 | 898 |
| 4 | [Zombies ×150 ring (AI on)](#zombies-150-ring-ai-on) | Entities | 1226.8 | 485.2 | 403.8 | 1.86 | 0.99 | 8 | 698 |
| 5 | [Pigs ×250 ring](#pigs-250-ring) | Entities | 2171.3 | 698.8 | 597.0 | 1.06 | 0.94 | 14 | 462 |
| 6 | [Villagers ×100 ring](#villagers-100-ring) | Entities | 1638.9 | 656.1 | 569.1 | 1.43 | 0.83 | 8 | 2534 |
| 7 | [Chickens ×300 ring](#chickens-300-ring) | Entities | 1651.4 | 618.1 | 514.8 | 1.51 | 0.94 | 14 | 1316 |
| 8 | [Item entities ×500](#item-entities-500) | Entities | 779.5 | 352.9 | 301.7 | 2.65 | 1.29 | 8 | 4932 |
| 9 | [XP orbs ×500 ring](#xp-orbs-500-ring) | Entities | 1410.3 | 248.4 | 229.9 | 3.89 | 3.16 | 8 | 4398 |
| 10 | [Item merge storm (cobblestone, mergeable)](#item-merge-storm-cobblestone-mergeable) | Entities | 565.8 | 114.7 | 90.4 | 8.08 | 4.46 | 12 | 3138 |
| 11 | [Zombies obstacle pathfinding (150 + pillar maze)](#zombies-obstacle-pathfinding-150--pillar-maze) | Entities | 1455.9 | 556.7 | 411.1 | 1.62 | 0.93 | 12 | 1096 |
| 12 | [Villager AI village (80, brain on)](#villager-ai-village-80-brain-on) | Entities | 1147.8 | 530.1 | 445.0 | 1.72 | 0.79 | 14 | 296 |
| 13 | [TNT field (14×14 staggered fuses)](#tnt-field-1414-staggered-fuses) | Physics | 3638.4 | 1001.4 | 714.6 | 0.60 | 1.12 | 4 | 5256 |
| 14 | [TNT field destructive (breaks terrain)](#tnt-field-destructive-breaks-terrain) | Physics | 3047.0 | 769.6 | 459.9 | 0.79 | 1.16 | 10 | 1418 |
| 15 | [Falling sand wall 40×40 (heavy)](#falling-sand-wall-4040-heavy) | Physics | 2113.5 | 32.5 | 26.6 | 27.84 | 15.36 | 8 | 1328 |
| 16 | [Falling gravel mixed heavy (sand+gravel+concrete)](#falling-gravel-mixed-heavy-sandgravelconcrete) | Physics | 2115.8 | 33.8 | 30.4 | 27.52 | 15.22 | 8 | 4588 |
| 17 | [Falling sand wall (lite, staggered)](#falling-sand-wall-lite-staggered) | Physics | 4298.3 | 887.7 | 450.7 | 0.57 | 1.77 | 10 | 240 |
| 18 | [Falling gravel mixed (lite, staggered)](#falling-gravel-mixed-lite-staggered) | Physics | 4311.5 | 873.0 | 426.5 | 0.56 | 1.79 | 8 | 1412 |
| 19 | [Projectile storm (arrows + snowballs)](#projectile-storm-arrows--snowballs) | Physics | 1935.0 | 615.3 | 496.5 | 1.36 | 0.95 | 8 | 5276 |
| 20 | [Redstone clocks (6×6)](#redstone-clocks-66) | Redstone | 4080.0 | 1162.2 | 867.3 | 0.59 | 0.71 | 8 | 1272 |
| 21 | [Redstone dust grid (16 trails ×32 + repeaters + lamps)](#redstone-dust-grid-16-trails-32--repeaters--lamps) | Redstone | 4303.3 | 882.0 | 308.0 | 0.61 | 0.65 | 8 | 1740 |
| 22 | [Piston/slime array (8×8 toggled every 8t)](#pistonslime-array-88-toggled-every-8t) | Redstone | 4442.9 | 1156.6 | 731.9 | 0.58 | 0.68 | 10 | 2544 |
| 23 | [Static dense forest (orbit canopy, no worldgen)](#static-dense-forest-orbit-canopy-no-worldgen) | Chunks | 4673.1 | 1295.4 | 896.1 | 0.53 | 0.66 | 8 | 924 |
| 24 | [Plains flyby (single-biome world)](#plains-flyby-single-biome-world) | Chunks | 2468.1 | 284.7 | 74.9 | 1.45 | 0.93 | 19 | 5876 |
| 25 | [Jungle flyby (single-biome world)](#jungle-flyby-single-biome-world) | Chunks | 1487.9 | 162.0 | 56.0 | 2.77 | 0.91 | 20 | 4864 |
| 26 | [Desert flyby (single-biome world)](#desert-flyby-single-biome-world) | Chunks | 2794.3 | 336.2 | 74.2 | 1.24 | 0.87 | 20 | 2732 |
| 27 | [Taiga flyby (single-biome world)](#taiga-flyby-single-biome-world) | Chunks | 3121.2 | 319.6 | 80.8 | 1.21 | 0.84 | 20 | 5448 |
| 28 | [Snowy plains flyby](#snowy-plains-flyby) | Chunks | 3338.0 | 388.0 | 93.9 | 1.12 | 0.80 | 20 | 3170 |
| 29 | [Forest flyby](#forest-flyby) | Chunks | 2156.3 | 249.8 | 62.9 | 1.56 | 0.91 | 22 | 4850 |
| 30 | [Savanna flyby](#savanna-flyby) | Chunks | 2712.0 | 302.1 | 73.2 | 1.34 | 0.92 | 20 | 2592 |
| 31 | [Swamp flyby](#swamp-flyby) | Chunks | 1946.4 | 212.2 | 62.2 | 2.00 | 0.83 | 16 | 5454 |
| 32 | [Cherry grove flyby](#cherry-grove-flyby) | Chunks | 3146.8 | 325.6 | 78.0 | 1.21 | 0.91 | 26 | 3998 |
| 33 | [Badlands flyby](#badlands-flyby) | Chunks | 3572.4 | 354.8 | 81.0 | 1.06 | 0.77 | 23 | 1922 |
| 34 | [Dark forest flyby (dense canopy)](#dark-forest-flyby-dense-canopy) | Chunks | 2075.5 | 219.6 | 72.0 | 1.77 | 0.88 | 17 | 5274 |
| 35 | [Windswept hills flyby](#windswept-hills-flyby) | Chunks | 2455.7 | 296.4 | 67.9 | 1.32 | 0.80 | 22 | 2966 |
| 36 | [Idle baseline (orbit, flat world)](#idle-baseline-orbit-flat-world) | Baseline | 4485.0 | 1259.8 | 817.2 | 0.53 | 0.66 | 8 | 3278 |
| 37 | [Particle diversity stress (16 types simultaneously)](#particle-diversity-stress-16-types-simultaneously) | Stress | 436.8 | 155.6 | 143.2 | 6.18 | 3.25 | 4 | 6156 |
| 38 | [Fluid spread (water basin, 4-step periodic reset)](#fluid-spread-water-basin-4-step-periodic-reset) | Fluids | 4324.3 | 1219.5 | 813.1 | 0.56 | 0.66 | 8 | 3502 |
| 39 | [Lighting update (16×16 glowstone reveal/hide)](#lighting-update-1616-glowstone-revealhide) | Lighting | 4302.5 | 1168.1 | 729.5 | 0.56 | 0.70 | 8 | 3100 |
| 40 | [Hopper grid 20×20 (transfer storm)](#hopper-grid-2020-transfer-storm) | Block-Entities | 3719.4 | 1154.9 | 863.2 | 0.60 | 0.66 | 10 | 4118 |
| 41 | [Comparator storage (8×8 chests + comparators)](#comparator-storage-88-chests--comparators) | Block-Entities | 3128.7 | 1032.1 | 687.4 | 0.67 | 0.65 | 8 | 1172 |

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

Category: **Particles**  |  Duration: 23111 ms  |  Sample ticks: 400

**FPS**  avg `3507.92`, min `452.69`, p50 `3644.31`, p95 `4215.85`, p99 `4295.53`, 1%low `890.06`, 0.1%low `603.58`, std `592.98`

**Frame time (ms)**  avg `0.30`, p50 `0.27`, p95 `0.42`, p99 `0.66`, p99.9 `1.53`, max `2.21`

**Client tick (ms)**  avg `1.00`, p95 `1.22`, max `1.76`

**Memory**  start `1610 MB`, end `878 MB`, peak `7074 MB`, GC `8 events / 582 ms`

**FPS over sampling window (ASCII):**

```
4112.3 |                                                      █                         
3977.4 |                                     █ █ █ █   █ █  ████                        
3842.5 |                              █      ███ █████ █ ███████                        
3707.7 |  █           █ █   █ █ █████ ████ ██████████████████████                       
3572.8 |███████████████ ██ ██ ███████████████████████████████████                       
3437.9 |█████████████████████████████████████████████████████████                       
3303.0 |███████████████████████████████████████████████████████████                    █
3168.1 |███████████████████████████████████████████████████████████               ██████
3033.2 |███████████████████████████████████████████████████████████               ██████
2898.3 |███████████████████████████████████████████████████████████  ██████ ██   ███████
2763.5 |██████████████████████████████████████████████████████████████████████  ████████
2628.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20871
   1 ms |   128
   2 ms |   1
```

**Per-particle stage breakdown:**

> _Approximate split: frames are bucketed evenly across stages by index, not aligned to the actual tick boundary. Use as a relative comparison, not an absolute per-stage measurement._

| Stage | Particles spawned | Frames captured | Avg FPS | p99 frame ms |
|---|---:|---:|---:|---:|
| `end_rod` | 240 | 2625 | 3598.7 | 0.61 |
| `flame` | 160 | 2625 | 3568.1 | 0.64 |
| `smoke` | 160 | 2625 | 3682.8 | 0.65 |
| `dragon_breath` | 160 | 2625 | 3783.7 | 0.57 |
| `ALL_TOGETHER` | 1680 | 2625 | 3857.8 | 0.54 |
| `dripping_water` | 240 | 2625 | 3682.6 | 0.64 |
| `portal` | 160 | 2625 | 2871.8 | 0.77 |
| `sculk_charge_pop` | 240 | 2625 | 3017.8 | 0.68 |

**Extras:**

- `particles_total` = `3040.00`
- `particles_stage_end_rod` = `240.00`
- `preload_chunks` = `81.00`
- `particles_stage_flame` = `160.00`
- `particle_stage_count` = `8.00`
- `entity_count_delta` = `0.00`
- `preset_full` = `0.00`
- `particles_stage_smoke` = `160.00`
- `entity_count_sample_end` = `1.00`
- `fps_1pct_low` = `890.06`
- `fps_harmonic_avg` = `3336.78`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `120.00`
- `particles_stage_dragon_breath` = `160.00`
- `preset_long` = `0.00`
- `fps_0p1pct_low` = `603.58`
- `seed` = `2503.00`
- `particle_stage_ticks` = `50.00`
- `particles_stage_ALL_TOGETHER` = `1680.00`
- `particles_stage_dripping_water` = `240.00`
- `particles_stage_portal` = `160.00`
- `entity_count_sample_start` = `1.00`
- `particles_stage_sculk_charge_pop` = `240.00`

### Cows ×200 ring (`entity_cows`)

Category: **Entities**  |  Duration: 23106 ms  |  Sample ticks: 400

**FPS**  avg `2238.31`, min `436.19`, p50 `2306.81`, p95 `2527.81`, p99 `2585.32`, 1%low `688.64`, 0.1%low `572.98`, std `274.12`

**Frame time (ms)**  avg `0.46`, p50 `0.43`, p95 `0.58`, p99 `1.02`, p99.9 `1.62`, max `2.29`

**Client tick (ms)**  avg `0.97`, p95 `1.05`, max `1.93`

**Memory**  start `6476 MB`, end `832 MB`, peak `7328 MB`, GC `15 events / 517 ms`

**FPS over sampling window (ASCII):**

```
2363.7 |                                                        █               ████    
2319.4 |                                             █    ███ ███             █ ████    
2275.2 |█ █          ██     █  █       █████  █ ██   █ ██ ████████ █         █████████ █
2230.9 |███   ██ █ █ ██ ██  ██████ █  ███████████████████████████████       ██████████ █
2186.7 |███ ████████████████████████████████████████████████████████████   ███████████ █
2142.4 |████████████████████████████████████████████████████████████████   ███████████ █
2098.1 |████████████████████████████████████████████████████████████████   █████████████
2053.9 |████████████████████████████████████████████████████████████████   █████████████
2009.6 |████████████████████████████████████████████████████████████████   █████████████
1965.3 |█████████████████████████████████████████████████████████████████  █████████████
1921.1 |██████████████████████████████████████████████████████████████████ █████████████
1876.8 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20785
   1 ms |   212
   2 ms |   3
```

**Extras:**

- `seed` = `6121.00`
- `fps_1pct_low` = `688.64`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `201.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `2175.94`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `572.98`
- `entities_spawned` = `200.00`
- `entity_count_sample_start` = `201.00`
- `preload_duration_ms` = `47.00`
- `preset_long` = `0.00`

### Sheep ×200 ring (`entity_sheep`)

Category: **Entities**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `1242.41`, min `373.66`, p50 `1276.32`, p95 `1347.53`, p99 `1363.14`, 1%low `532.81`, 0.1%low `443.56`, std `128.87`

**Frame time (ms)**  avg `0.82`, p50 `0.78`, p95 `0.99`, p99 `1.76`, p99.9 `2.04`, max `2.68`

**Client tick (ms)**  avg `0.95`, p95 `1.03`, max `1.94`

**Memory**  start `6842 MB`, end `800 MB`, peak `7740 MB`, GC `12 events / 501 ms`

**FPS over sampling window (ASCII):**

```
1290.3 |                  █                  █                                ██        
1278.9 |                 ██                  █            █      █            ██        
1267.4 |                 ██         ██       █           ██ █   ██    █ █     ██        
1256.0 |   █        █   ████    █ █ ██ █     ██   ██   ██████ ████    █ ██  ████      █ 
1244.5 | ███ █     ██  ██████   █ ████ ██    ██  ███   ██████ ████ █ ██ ██  █████   █ █ 
1233.1 | █████  █ ███  ██████  ██ ████ ██  █ ██  ███   ██████████████████████████   ████
1221.6 |███████ █ ███ ███████  ██ ████ ██ ██████ ███ ████████████████████████████ █ ████
1210.2 |█████████████████████ ███████████ ██████████ ████████████████████████████ █ ████
1198.7 |█████████████████████ █████████████████████████████████████████████████████ ████
1187.3 |███████████████████████████████████████████████████████████████████████████ ████
1175.8 |███████████████████████████████████████████████████████████████████████████ ████
1164.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20032
   1 ms | ██  935
   2 ms |   33
```

**Extras:**

- `seed` = `6133.00`
- `fps_1pct_low` = `532.81`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `201.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1219.99`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `443.56`
- `entities_spawned` = `200.00`
- `entity_count_sample_start` = `201.00`
- `preload_duration_ms` = `45.00`
- `preset_long` = `0.00`

### Zombies ×150 ring (AI on) (`entity_zombies`)

Category: **Entities**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `1226.79`, min `327.20`, p50 `1259.60`, p95 `1344.81`, p99 `1367.24`, 1%low `485.24`, 0.1%low `403.81`, std `133.84`

**Frame time (ms)**  avg `0.83`, p50 `0.79`, p95 `1.01`, p99 `1.86`, p99.9 `2.30`, max `3.06`

**Client tick (ms)**  avg `0.99`, p95 `1.07`, max `1.98`

**Memory**  start `6750 MB`, end `7448 MB`, peak `7448 MB`, GC `8 events / 476 ms`

**FPS over sampling window (ASCII):**

```
1304.8 |                                             █                                  
1289.0 |                                           ███  █                               
1273.3 |                        █      █        ██ ████████                █            
1257.5 |                      █ ██    ██  █     ███████████  █           ███        █   
1241.8 |█                █ █  █████   ██ ██   ██████████████ █       █   ████       ██  
1226.0 |███          ██ ████  ██████  █████  █████████████████       ██ ███████   ████  
1210.2 |███  █       ███████ ██████████████  ███████████████████     ██████████ █ ████ █
1194.5 |███ ███     ████████ ███████████████ █████████████████████  ████████████████████
1178.7 |████████    ██████████████████████████████████████████████ █████████████████████
1163.0 |████████ █  ████████████████████████████████████████████████████████████████████
1147.2 |████████ ██ ████████████████████████████████████████████████████████████████████
1131.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  19909
   1 ms | ██  970
   2 ms |   119
   3 ms |   2
```

**Extras:**

- `seed` = `6151.00`
- `fps_1pct_low` = `485.24`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `152.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1201.40`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `403.81`
- `entities_spawned` = `150.00`
- `entity_count_sample_start` = `152.00`
- `preload_duration_ms` = `56.00`
- `preset_long` = `0.00`

### Pigs ×250 ring (`entity_pigs`)

Category: **Entities**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `2171.27`, min `448.57`, p50 `2237.14`, p95 `2447.98`, p99 `2501.88`, 1%low `698.80`, 0.1%low `597.05`, std `257.02`

**Frame time (ms)**  avg `0.47`, p50 `0.45`, p95 `0.58`, p99 `1.06`, p99.9 `1.56`, max `2.23`

**Client tick (ms)**  avg `0.94`, p95 `1.00`, max `2.12`

**Memory**  start `7044 MB`, end `5596 MB`, peak `7506 MB`, GC `14 events / 1097 ms`

**FPS over sampling window (ASCII):**

```
2276.8 | █ █                                                               █            
2252.2 | ████ █                 █       █                            █     █            
2227.5 |███████    █       █    ██     ██    █                       █  █  █            
2202.9 |███████    ██   █ ██  █ ██ █  ████  ██  ██      ███          █  ██████ █     █  
2178.3 |████████   ███ ███████████ ███████ ████ ██      ███          ████████████    █  
2153.7 |████████  ████████████████████████ ███████      ███         █████████████    █  
2129.1 |████████  ███████████████████████████████████  █████   ██   █████████████   ████
2104.5 |████████  ███████████████████████████████████  █████ ████   █████████████ █ ████
2079.9 |█████████████████████████████████████████████  █████ ██████ █████████████ █ ████
2055.2 |█████████████████████████████████████████████  ████████████ █████████████ █ ████
2030.6 |█████████████████████████████████████████████████████████████████████████ █ ████
2006.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20777
   1 ms |   221
   2 ms |   2
```

**Extras:**

- `seed` = `6163.00`
- `fps_1pct_low` = `698.80`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `251.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `2115.75`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `597.05`
- `entities_spawned` = `250.00`
- `entity_count_sample_start` = `251.00`
- `preload_duration_ms` = `49.00`
- `preset_long` = `0.00`

### Villagers ×100 ring (`entity_villagers`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1638.88`, min `421.23`, p50 `1681.52`, p95 `1779.99`, p99 `1798.88`, 1%low `656.15`, 0.1%low `569.09`, std `172.45`

**Frame time (ms)**  avg `0.62`, p50 `0.59`, p95 `0.76`, p99 `1.43`, p99.9 `1.67`, max `2.37`

**Client tick (ms)**  avg `0.83`, p95 `0.90`, max `1.60`

**Memory**  start `5236 MB`, end `6336 MB`, peak `7770 MB`, GC `8 events / 510 ms`

**FPS over sampling window (ASCII):**

```
1710.9 |                                                      █   █ ██                  
1690.9 |██                                                  ███   ████     █ █    █    █
1670.9 |██       █           ██                █ █     █    ████ ███████  ██ █    █  █ █
1651.0 |██ ██  ███           ██ ██             █████  ██ ███████ ███████  ████  █ ██████
1631.0 |█████  ████████    █ ██ ███  █         █████████████████████████  ████ █████████
1611.1 |█████  ████████  █ ████████  █         █████████████████████████ █████ █████████
1591.1 |████████████████ █ ██████████████     ██████████████████████████ ███████████████
1571.1 |██████████████████████████████████  ████████████████████████████████████████████
1551.2 |██████████████████████████████████ █████████████████████████████████████████████
1531.2 |██████████████████████████████████ █████████████████████████████████████████████
1511.3 |██████████████████████████████████ █████████████████████████████████████████████
1491.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20658
   1 ms | █  341
   2 ms |   1
```

**Extras:**

- `seed` = `6173.00`
- `fps_1pct_low` = `656.15`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `101.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1608.02`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `569.09`
- `entities_spawned` = `100.00`
- `entity_count_sample_start` = `101.00`
- `preload_duration_ms` = `29.00`
- `preset_long` = `0.00`

### Chickens ×300 ring (`entity_chickens`)

Category: **Entities**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `1651.42`, min `422.74`, p50 `1697.22`, p95 `1829.16`, p99 `1861.85`, 1%low `618.15`, 0.1%low `514.84`, std `183.59`

**Frame time (ms)**  avg `0.62`, p50 `0.59`, p95 `0.76`, p99 `1.51`, p99.9 `1.74`, max `2.37`

**Client tick (ms)**  avg `0.94`, p95 `1.03`, max `2.03`

**Memory**  start `6068 MB`, end `2462 MB`, peak `7384 MB`, GC `14 events / 1047 ms`

**FPS over sampling window (ASCII):**

```
1720.5 |  █    █                                                                        
1706.3 |█ █    █                                                                   █    
1692.1 |█ ██████      █      █                              ██         █    ███   ██    
1677.9 |████████  █ █ █      █                              ███   █    █   ████   ████  
1663.7 |███████████ █ ████   █ █   █          █           ██████ ██ █ ██  █████   ████  
1649.4 |███████████ █ █████  ███   █          █   ██     ███████████████  ██████  ████  
1635.2 |███████████████████  ████  █          █ ████    ████████████████ ██████████████ 
1621.0 |██████████████████████████ █     █  █ ███████   ████████████████████████████████
1606.8 |████████████████████████████ █ ███  █████████   ████████████████████████████████
1592.6 |██████████████████████████████ ███  █████████  █████████████████████████████████
1578.3 |███████████████████████████████████ ████████████████████████████████████████████
1564.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20627
   1 ms | █  366
   2 ms |   7
```

**Extras:**

- `seed` = `6197.00`
- `fps_1pct_low` = `618.15`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `301.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1615.57`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `514.84`
- `entities_spawned` = `300.00`
- `entity_count_sample_start` = `301.00`
- `preload_duration_ms` = `42.00`
- `preset_long` = `0.00`

### Item entities ×500 (`entity_items`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `779.51`, min `234.16`, p50 `803.92`, p95 `843.88`, p99 `857.35`, 1%low `352.93`, 0.1%low `301.73`, std `84.50`

**Frame time (ms)**  avg `1.31`, p50 `1.24`, p95 `1.55`, p99 `2.65`, p99.9 `3.05`, max `4.27`

**Client tick (ms)**  avg `1.29`, p95 `1.37`, max `1.69`

**Memory**  start `2484 MB`, end `4846 MB`, peak `7416 MB`, GC `8 events / 297 ms`

**FPS over sampling window (ASCII):**

```
809.0 |                                 █                                              
800.8 | █                   █           ██                                    █  █     
792.5 | █████       █       █    ██████████                            █   █  ██ ███   
784.3 | █████ █ █  ██    █  ██  ████████████     █ ██     █  ██ █ ██ █ █   █ ██████████
776.1 |██████████ ████  ██  ███ ████████████ ██ ██ ██ ███ █ ███ █ ██ █ █   ████████████
767.8 |██████████ ████  ██  █████████████████████████████████████ ██ █ █   ████████████
759.6 |████████████████ ██  ████████████████████████████████████████ █████ ████████████
751.4 |███████████████████  ████████████████████████████████████████ █████ ████████████
743.1 |███████████████████  ██████████████████████████████████████████████ ████████████
734.9 |███████████████████  ██████████████████████████████████████████████ ████████████
726.7 |████████████████████ ███████████████████████████████████████████████████████████
718.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   1 ms | ████████████████████████████████████████  14854
   2 ms | █  409
   3 ms |   19
   4 ms |   1
```

**Extras:**

- `seed` = `6203.00`
- `fps_1pct_low` = `352.93`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `501.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `764.13`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `301.73`
- `entities_spawned` = `500.00`
- `entity_count_sample_start` = `501.00`
- `preload_duration_ms` = `29.00`
- `preset_long` = `0.00`

### XP orbs ×500 ring (`entity_xp`)

Category: **Entities**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `1410.28`, min `187.15`, p50 `1456.13`, p95 `1534.68`, p99 `1553.28`, 1%low `248.45`, 0.1%low `229.86`, std `179.22`

**Frame time (ms)**  avg `0.75`, p50 `0.69`, p95 `0.88`, p99 `3.89`, p99.9 `4.17`, max `5.34`

**Client tick (ms)**  avg `3.16`, p95 `3.33`, max `3.80`

**Memory**  start `3020 MB`, end `4098 MB`, peak `7418 MB`, GC `8 events / 305 ms`

**FPS over sampling window (ASCII):**

```
1466.1 |                    █                                      █        █           
1454.8 |                  █ █                                    █ ██       █           
1443.6 |                ███ █           █            █           ████  ██   ██          
1432.4 |              ████████      █ ███            █           ████  ██  ███       █  
1421.2 |           █ █████████  █  ██████ ██         █ █       █ ████ ███  ███      ██ █
1410.0 | █         ███████████ ██  ██████ ██        ██ █████ █ █ ████ ███ ████  █   ██ █
1398.7 | ██ ██  █ ████████████ ███ █████████    ███ ████████ █ █ ████████ █████ ██  ██ █
1387.5 | ██ ██  █ ███████████████████████████ █ ███ ████████ █ █ ████████ █████ ███ ████
1376.3 |██████  █ █████████████████████████████ ███ ████████ ████████████ ██████████████
1365.1 |██████  █ █████████████████████████████████ ████████████████████████████████████
1353.9 |███████ █ ██████████████████████████████████████████████████████████████████████
1342.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20472
   1 ms |   211
   3 ms |   219
   4 ms |   97
   5 ms |   1
```

**Extras:**

- `seed` = `6217.00`
- `fps_1pct_low` = `248.45`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `501.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1325.39`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `229.86`
- `entities_spawned` = `500.00`
- `entity_count_sample_start` = `501.00`
- `preload_duration_ms` = `50.00`
- `preset_long` = `0.00`

### Item merge storm (cobblestone, mergeable) (`entity_items_merge_storm`)

Category: **Entities**  |  Duration: 23109 ms  |  Sample ticks: 400

**FPS**  avg `565.83`, min `37.47`, p50 `558.28`, p95 `798.30`, p99 `844.98`, 1%low `114.74`, 0.1%low `90.36`, std `156.45`

**Frame time (ms)**  avg `2.01`, p50 `1.79`, p95 `2.76`, p99 `8.08`, p99.9 `9.06`, max `26.69`

**Client tick (ms)**  avg `4.46`, p95 `6.28`, max `6.82`

**Memory**  start `4238 MB`, end `2062 MB`, peak `7376 MB`, GC `12 events / 328 ms`

**FPS over sampling window (ASCII):**

```
789.6 |████                                                                            
753.5 |█████████                                                                       
717.4 |███████████ ███ █   ████                                                        
681.3 |█████████████████   ███████                                                     
645.2 |███████████████████ ████████████                                                
609.1 |████████████████████████████████ █ █                                            
573.0 |█████████████████████████████████████████                                       
536.9 |█████████████████████████████████████████████                                   
500.8 |███████████████████████████████████████████████████ ████                        
464.7 |█████████████████████████████████████████████████████████████                   
428.6 |█████████████████████████████████████████████████████████████████            ██ 
392.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   1 ms | ████████████████████████████████████████  6217
   2 ms | █████████████████████  3322
   3 ms |   49
   4 ms | █  78
   5 ms |   67
   6 ms |   63
   7 ms |   68
   8 ms | █  96
   9 ms |   11
  26 ms |   1
```

**Extras:**

- `waves_spawned` = `12.00`
- `entity_count_sample_start` = `681.00`
- `entity_count_delta` = `880.00`
- `preset_long` = `0.00`
- `entity_count_sample_end` = `1561.00`
- `preload_duration_ms` = `49.00`
- `seed` = `6287.00`
- `fps_0p1pct_low` = `90.36`
- `fps_harmonic_avg` = `498.55`
- `items_spawned` = `1560.00`
- `preset_quick` = `1.00`
- `preload_chunks` = `81.00`
- `items_merged_estimate` = `0.00`
- `items_alive_p95` = `1560.00`
- `preset_full` = `0.00`
- `fps_1pct_low` = `114.74`
- `items_alive_max` = `1560.00`
- `items_alive_p50` = `1240.00`
- `items_alive_avg` = `1230.00`

### Zombies obstacle pathfinding (150 + pillar maze) (`entity_zombies_obstacle_pathfinding`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1455.86`, min `144.43`, p50 `1507.39`, p95 `1605.65`, p99 `1627.87`, 1%low `556.75`, 0.1%low `411.10`, std `175.93`

**Frame time (ms)**  avg `0.70`, p50 `0.66`, p95 `0.90`, p99 `1.62`, p99.9 `1.93`, max `6.92`

**Client tick (ms)**  avg `0.93`, p95 `1.02`, max `2.01`

**Memory**  start `6740 MB`, end `1284 MB`, peak `7836 MB`, GC `12 events / 531 ms`

**FPS over sampling window (ASCII):**

```
1516.1 |                                                            ███   ██   █        
1487.6 |       █   ██              █   █  ████      █   ██       ███████████ █████      
1459.0 |  █    ██ ██████  ██    ████ ██████████████████████████  ███████████████████ ██ 
1430.5 | ██    ██████████ ████ █████████████████████████████████████████████████████████
1402.0 |███    █████████████████████████████████████████████████████████████████████████
1373.5 |███   ██████████████████████████████████████████████████████████████████████████
1345.0 |███   ██████████████████████████████████████████████████████████████████████████
1316.5 |███   ██████████████████████████████████████████████████████████████████████████
1288.0 |███  ███████████████████████████████████████████████████████████████████████████
1259.5 |███  ███████████████████████████████████████████████████████████████████████████
1230.9 |████ ███████████████████████████████████████████████████████████████████████████
1202.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20257
   1 ms | █  732
   2 ms |   8
   3 ms |   2
   6 ms |   1
```

**Extras:**

- `preset_quick` = `1.00`
- `entity_count_delta` = `0.00`
- `neighbour_updates` = `0.00`
- `pillars_built` = `48.00`
- `zombies_spawned` = `150.00`
- `block_state_changes` = `0.00`
- `preset_long` = `0.00`
- `preload_duration_ms` = `28.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `411.10`
- `fps_1pct_low` = `556.75`
- `entity_count_sample_start` = `151.00`
- `fps_harmonic_avg` = `1418.85`
- `entity_count_sample_end` = `151.00`
- `seed` = `6271.00`
- `preset_full` = `0.00`

### Villager AI village (80, brain on) (`villager_ai_village`)

Category: **Entities**  |  Duration: 23101 ms  |  Sample ticks: 400

**FPS**  avg `1147.76`, min `319.28`, p50 `1182.31`, p95 `1285.02`, p99 `1300.39`, 1%low `530.08`, 0.1%low `445.01`, std `131.51`

**Frame time (ms)**  avg `0.89`, p50 `0.85`, p95 `1.09`, p99 `1.72`, p99.9 `2.06`, max `3.13`

**Client tick (ms)**  avg `0.79`, p95 `0.93`, max `1.56`

**Memory**  start `7092 MB`, end `2862 MB`, peak `7388 MB`, GC `14 events / 1143 ms`

**FPS over sampling window (ASCII):**

```
1248.6 |                                                           █  █      ███ █      
1219.2 |                                                       █████████    ███████████ 
1189.8 |                              ██      ██  █ █ ██  █    ███████████  ████████████
1160.4 |               █             ██████ ██████████████████ ████████████ ████████████
1131.0 |            █████ ██      █████████ ████████████████████████████████████████████
1101.6 |           █████████████ ███████████████████████████████████████████████████████
1072.3 |         ███████████████████████████████████████████████████████████████████████
1042.9 |        ████████████████████████████████████████████████████████████████████████
1013.5 |       █████████████████████████████████████████████████████████████████████████
984.1 |     ███████████████████████████████████████████████████████████████████████████
954.7 |  ██████████████████████████████████████████████████████████████████████████████
925.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  18580
   1 ms | █████  2382
   2 ms |   36
   3 ms |   2
```

**Extras:**

- `doors_placed` = `16.00`
- `entity_count_sample_start` = `81.00`
- `entity_count_delta` = `0.00`
- `preset_long` = `0.00`
- `entity_count_sample_end` = `81.00`
- `preload_duration_ms` = `26.00`
- `seed` = `6299.00`
- `fps_0p1pct_low` = `445.01`
- `villagers_spawned` = `80.00`
- `fps_harmonic_avg` = `1126.05`
- `neighbour_updates` = `0.00`
- `beds_placed` = `40.00`
- `workstations_placed` = `40.00`
- `preset_quick` = `1.00`
- `preload_chunks` = `81.00`
- `preset_full` = `0.00`
- `scheduled_block_ticks` = `0.00`
- `fps_1pct_low` = `530.08`
- `block_state_changes` = `106.00`

### TNT field (14×14 staggered fuses) (`tnt_field`)

Category: **Physics**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `3638.41`, min `554.35`, p50 `3697.54`, p95 `4258.94`, p99 `4370.63`, 1%low `1001.36`, 0.1%low `714.55`, std `471.10`

**Frame time (ms)**  avg `0.28`, p50 `0.27`, p95 `0.34`, p99 `0.60`, p99.9 `1.29`, max `1.80`

**Client tick (ms)**  avg `1.12`, p95 `2.58`, max `3.27`

**Memory**  start `2252 MB`, end `7102 MB`, peak `7508 MB`, GC `4 events / 273 ms`

**FPS over sampling window (ASCII):**

```
4186.9 |                                                                               █
4101.2 |                                                                        ████████
4015.5 |                                                                       █████████
3929.8 |                                                                   █████████████
3844.1 |                                                             █ ██ ██████████████
3758.4 |                                                        ██ █████████████████████
3672.7 |        █ █                  █ █     █  █  █         ██ ████████████████████████
3587.0 |        █ █               █  ███    ██ ███ ██    ██ ███ ████████████████████████
3501.3 |    ████████      ██   █ ████████  █████████████████████████████████████████████
3415.6 | ████████████  ███████████████████ █████████████████████████████████████████████
3329.9 | ███████████████████████████████████████████████████████████████████████████████
3244.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20882
   1 ms |   118
```

**Extras:**

- `block_state_changes` = `0.00`
- `neighbour_updates` = `0.00`
- `preset_quick` = `1.00`
- `preset_long` = `0.00`
- `fps_1pct_low` = `1001.36`
- `tnt_active_avg` = `36.14`
- `seed` = `3539.00`
- `entity_count_sample_end` = `1.00`
- `tnt_spawned` = `430.00`
- `tnt_active_max` = `205.00`
- `section_rebuilds` = `161.00`
- `entity_count_sample_start` = `188.00`
- `preload_chunks` = `81.00`
- `tnt_active_p50` = `25.00`
- `fps_harmonic_avg` = `3518.26`
- `tnt_active_p95` = `150.00`
- `preset_full` = `0.00`
- `fps_0p1pct_low` = `714.55`
- `preload_duration_ms` = `1.00`
- `explosions_count` = `403.00`
- `entity_count_delta` = `-187.00`
- `waves_spawned` = `13.00`

### TNT field destructive (breaks terrain) (`tnt_field_destructive`)

Category: **Physics**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `3047.04`, min `84.41`, p50 `3088.33`, p95 `3763.64`, p99 `3913.91`, 1%low `769.64`, 0.1%low `459.87`, std `560.11`

**Frame time (ms)**  avg `0.35`, p50 `0.32`, p95 `0.47`, p99 `0.79`, p99.9 `1.50`, max `11.85`

**Client tick (ms)**  avg `1.16`, p95 `2.51`, max `2.82`

**Memory**  start `6218 MB`, end `7220 MB`, peak `7636 MB`, GC `10 events / 989 ms`

**FPS over sampling window (ASCII):**

```
3689.4 |                     █ █                                                        
3550.6 |                    ████████████  █                                             
3411.8 |██      █        ██████████████████ ███ ██                                      
3273.0 |███ █ █████    ████████████████████████████                                     
3134.2 |███████████████████████████████████████████                                     
2995.4 |███████████████████████████████████████████                                   ██
2856.5 |███████████████████████████████████████████                       ██████████████
2717.7 |███████████████████████████████████████████                █████ ███████████████
2578.9 |███████████████████████████████████████████             ████████████████████████
2440.1 |███████████████████████████████████████████ ██  ████████████████████████████████
2301.3 |███████████████████████████████████████████ ████████████████████████████████████
2162.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20838
   1 ms |   159
   2 ms |   2
  11 ms |   1
```

**Extras:**

- `block_state_changes` = `7600.00`
- `neighbour_updates` = `0.00`
- `preset_quick` = `1.00`
- `preset_long` = `0.00`
- `fps_1pct_low` = `769.64`
- `tnt_active_avg` = `36.52`
- `seed` = `3541.00`
- `entity_count_sample_end` = `81.00`
- `tnt_spawned` = `430.00`
- `tnt_active_max` = `206.00`
- `section_rebuilds` = `605.00`
- `entity_count_sample_start` = `206.00`
- `preload_chunks` = `81.00`
- `tnt_active_p50` = `26.00`
- `fps_harmonic_avg` = `2885.57`
- `tnt_active_p95` = `149.00`
- `preset_full` = `0.00`
- `fps_0p1pct_low` = `459.87`
- `preload_duration_ms` = `1.00`
- `explosions_count` = `404.00`
- `entity_count_delta` = `-125.00`
- `waves_spawned` = `13.00`

### Falling sand wall 40×40 (heavy) (`falling_sand`)

Category: **Physics**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `2113.47`, min `20.29`, p50 `2442.30`, p95 `3403.79`, p99 `4921.28`, 1%low `32.52`, 0.1%low `26.61`, std `1134.65`

**Frame time (ms)**  avg `2.04`, p50 `0.41`, p95 `9.48`, p99 `27.84`, p99.9 `33.46`, max `49.28`

**Client tick (ms)**  avg `15.36`, p95 `21.75`, max `36.75`

**Memory**  start `6288 MB`, end `5410 MB`, peak `7616 MB`, GC `8 events / 470 ms`

**FPS over sampling window (ASCII):**

```
4776.8 |                                                                             █ █
4359.5 |                                                                             ███
3942.3 |                                                                            ████
3525.1 |                                                                            ████
3107.8 |                                                                  █████ ██  ████
2690.6 |                  █    █                                 ██████   ████████  ████
2273.3 |██████████  █  █  ██  ██  ██   ██  ███  ███  ████  ████  ███████ █████████  ████
1856.1 |██████████  █  █  ██  ███ ███ ███  ███  ███  █████ ████  ███████ █████████  ████
1438.8 |██████████  █ ███ ██  ███ ███ ████ ███  ████ █████ █████ ███████ ██████████ ████
1021.6 |██████████  █ ███ ███ ███ ███ ████ █████████ █████ █████ ███████ ██████████ ████
604.3 |█████████████████ ███████ ███ ████ █████████████████████████████ ███████████████
187.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  7828
   1 ms | ██  363
   2 ms | █  219
   3 ms | █  149
   4 ms |   95
   5 ms | █  103
   6 ms | █  101
   7 ms | █  125
   8 ms | █  246
   9 ms | █  143
  10 ms |   36
  11 ms |   18
  12 ms |   1
  13 ms |   3
  14 ms |   8
  15 ms |   51
  16 ms |   32
  17 ms |   21
  18 ms |   19
  19 ms |   17
  20 ms |   17
  21 ms |   19
  22 ms |   26
  23 ms |   17
  24 ms |   9
  25 ms |   5
  26 ms |   6
  27 ms |   31
  28 ms |   25
  29 ms |   19
  30 ms |   19
  31 ms |   9
  32 ms |   7
  33 ms |   4
  34 ms |   2
  35 ms |   2
  37 ms |   2
  38 ms |   1
  40 ms |   1
  49 ms |   1
```

**Extras:**

- `variant` = `heavy`
- `falling_blocks_alive_p50` = `4800.00`
- `preload_chunks` = `81.00`
- `wave_interval_ticks` = `30.00`
- `entity_count_delta` = `-3093.00`
- `waves_spawned` = `12.00`
- `falling_blocks_alive_p95` = `6400.00`
- `preset_full` = `0.00`
- `entity_count_sample_end` = `108.00`
- `block_state_changes` = `36466.00`
- `falling_blocks_alive_avg` = `4804.90`
- `fps_1pct_low` = `32.52`
- `fps_harmonic_avg` = `490.00`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `28.00`
- `falling_blocks_alive_max` = `6400.00`
- `preset_long` = `0.00`
- `falling_blocks_landed` = `20800.00`
- `fps_0p1pct_low` = `26.61`
- `topup_blocks_per_wave` = `1600.00`
- `seed` = `5077.00`
- `sand_spawned` = `20800.00`
- `entity_count_sample_start` = `3201.00`
- `neighbour_updates` = `0.00`

### Falling gravel mixed heavy (sand+gravel+concrete) (`falling_gravel_mixed`)

Category: **Physics**  |  Duration: 23101 ms  |  Sample ticks: 400

**FPS**  avg `2115.80`, min `26.82`, p50 `2457.00`, p95 `3913.93`, p99 `4828.31`, 1%low `33.76`, 0.1%low `30.37`, std `1124.76`

**Frame time (ms)**  avg `2.02`, p50 `0.41`, p95 `9.59`, p99 `27.52`, p99.9 `31.28`, max `37.29`

**Client tick (ms)**  avg `15.22`, p95 `20.79`, max `25.53`

**Memory**  start `3230 MB`, end `1694 MB`, peak `7818 MB`, GC `8 events / 447 ms`

**FPS over sampling window (ASCII):**

```
4716.0 |                                                                               █
4309.0 |                                                                             ███
3902.1 |                                                                            ████
3495.1 |                                                                            ████
3088.1 |                                                                  ██ ██  █  ████
2681.2 |██  ██                                                    ██████  ████████  ████
2274.2 |██████████     █   █  ██  ███  ███  ██  ████  ████  ███  ███████  ████████  ████
1867.2 |███████████    ██ ██  ███ ███  ███ ████ ████ █████ █████ ███████  ████████  ████
1460.3 |███████████  █ ██ ███ ███ ███ ████ ████ ████ █████ █████ ███████ ██████████ ████
1053.3 |███████████ ██ ██ ███ ███ ███ ████ ████ ████ █████ █████ ███████ ██████████ ████
646.3 |███████████ ██ ██ ███████ ████████ ████ ████████████████ ███████████████████████
239.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  7968
   1 ms | ██  355
   2 ms | █  191
   3 ms | █  161
   4 ms |   98
   5 ms |   94
   6 ms |   93
   7 ms | █  127
   8 ms | █  248
   9 ms | █  125
  10 ms |   43
  11 ms |   23
  12 ms |   9
  13 ms |   3
  14 ms |   8
  15 ms |   51
  16 ms |   37
  17 ms |   12
  18 ms |   21
  19 ms |   19
  20 ms |   22
  21 ms |   21
  22 ms |   25
  23 ms |   16
  24 ms |   11
  25 ms |   10
  26 ms |   8
  27 ms |   30
  28 ms |   24
  29 ms |   23
  30 ms |   22
  31 ms |   10
  32 ms |   2
  33 ms |   1
  35 ms |   1
  37 ms |   1
```

**Extras:**

- `variant` = `heavy`
- `falling_blocks_alive_p50` = `4800.00`
- `preload_chunks` = `81.00`
- `wave_interval_ticks` = `30.00`
- `entity_count_delta` = `-2988.00`
- `waves_spawned` = `12.00`
- `falling_blocks_alive_p95` = `6400.00`
- `preset_full` = `0.00`
- `entity_count_sample_end` = `213.00`
- `block_state_changes` = `36466.00`
- `falling_blocks_alive_avg` = `4803.02`
- `fps_1pct_low` = `33.76`
- `fps_harmonic_avg` = `495.65`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `74.00`
- `falling_blocks_alive_max` = `6400.00`
- `preset_long` = `0.00`
- `falling_blocks_landed` = `20800.00`
- `fps_0p1pct_low` = `30.37`
- `topup_blocks_per_wave` = `1600.00`
- `seed` = `5081.00`
- `sand_spawned` = `20800.00`
- `entity_count_sample_start` = `3201.00`
- `neighbour_updates` = `0.00`

### Falling sand wall (lite, staggered) (`falling_sand_lite`)

Category: **Physics**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `4298.34`, min `358.96`, p50 `4393.67`, p95 `4992.51`, p99 `5065.86`, 1%low `887.74`, 0.1%low `450.69`, std `624.29`

**Frame time (ms)**  avg `0.24`, p50 `0.23`, p95 `0.32`, p99 `0.57`, p99.9 `2.07`, max `2.79`

**Client tick (ms)**  avg `1.77`, p95 `2.17`, max `2.47`

**Memory**  start `7600 MB`, end `7268 MB`, peak `7840 MB`, GC `10 events / 1014 ms`

**FPS over sampling window (ASCII):**

```
4896.1 |                                                                        █       
4799.1 |                                                                    ██  █ █     
4702.1 |                                                                 ████████ ███   
4605.1 |                                                                 ████████ ███  █
4508.1 |                                           █                    █████████████  █
4411.2 |                                  █ █   █ ██    ███  ██   █     ████████████████
4314.2 |                           █      █ █ ██████    █████████ █   ██████████████████
4217.2 | █  █ █  ███    █          █ █ █  ██████████████████████████  ██████████████████
4120.2 |█████████████ █ ██     █  ██ ███ ███████████████████████████████████████████████
4023.2 |██████████████████ █  ██ ███ ███████████████████████████████████████████████████
3926.2 |█████████████████████ ██ ███████████████████████████████████████████████████████
3829.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20906
   1 ms |   69
   2 ms |   25
```

**Extras:**

- `variant` = `lite`
- `falling_blocks_alive_p50` = `686.00`
- `preload_chunks` = `81.00`
- `wave_interval_ticks` = `6.00`
- `entity_count_delta` = `-441.00`
- `waves_spawned` = `63.00`
- `falling_blocks_alive_p95` = `833.00`
- `preset_full` = `0.00`
- `entity_count_sample_end` = `1.00`
- `block_state_changes` = `5684.00`
- `falling_blocks_alive_avg` = `619.12`
- `fps_1pct_low` = `887.74`
- `fps_harmonic_avg` = `4085.24`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `60.00`
- `falling_blocks_alive_max` = `882.00`
- `preset_long` = `0.00`
- `falling_blocks_landed` = `3087.00`
- `fps_0p1pct_low` = `450.69`
- `topup_blocks_per_wave` = `49.00`
- `seed` = `5101.00`
- `sand_spawned` = `3087.00`
- `entity_count_sample_start` = `442.00`
- `neighbour_updates` = `0.00`

### Falling gravel mixed (lite, staggered) (`falling_gravel_mixed_lite`)

Category: **Physics**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `4311.50`, min `334.84`, p50 `4385.96`, p95 `4899.56`, p99 `4972.65`, 1%low `873.04`, 0.1%low `426.47`, std `589.34`

**Frame time (ms)**  avg `0.24`, p50 `0.23`, p95 `0.31`, p99 `0.56`, p99.9 `2.11`, max `2.99`

**Client tick (ms)**  avg `1.79`, p95 `2.14`, max `2.89`

**Memory**  start `6266 MB`, end `3650 MB`, peak `7678 MB`, GC `8 events / 489 ms`

**FPS over sampling window (ASCII):**

```
4653.7 |                                                            █    ██  ████   ██  
4560.4 |                                         █           ██    ██   █████████  ███ █
4467.1 |                                         █  █     ██ ██  █ ██ █ ██████████████ █
4373.9 |                                 ██ ██  ██████  █ ██ ██  ██████ ████████████████
4280.6 |                ██  █            █████████████ █████████████████████████████████
4187.3 | ██  █   █      ███ ███  █ ██ ██████████████████████████████████████████████████
4094.0 | ██ ███ ███ █  █████████ ████ ██████████████████████████████████████████████████
4000.8 | ██████ ███ ██ ██████████████ ██████████████████████████████████████████████████
3907.5 |███████ ███ ██ █████████████████████████████████████████████████████████████████
3814.2 |██████████████ █████████████████████████████████████████████████████████████████
3720.9 |██████████████ █████████████████████████████████████████████████████████████████
3627.7 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20905
   1 ms |   66
   2 ms |   29
```

**Extras:**

- `variant` = `lite`
- `falling_blocks_alive_p50` = `686.00`
- `preload_chunks` = `81.00`
- `wave_interval_ticks` = `6.00`
- `entity_count_delta` = `-441.00`
- `waves_spawned` = `63.00`
- `falling_blocks_alive_p95` = `833.00`
- `preset_full` = `0.00`
- `entity_count_sample_end` = `1.00`
- `block_state_changes` = `5684.00`
- `falling_blocks_alive_avg` = `619.12`
- `fps_1pct_low` = `873.04`
- `fps_harmonic_avg` = `4104.30`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `55.00`
- `falling_blocks_alive_max` = `882.00`
- `preset_long` = `0.00`
- `falling_blocks_landed` = `3087.00`
- `fps_0p1pct_low` = `426.47`
- `topup_blocks_per_wave` = `49.00`
- `seed` = `5113.00`
- `sand_spawned` = `3087.00`
- `entity_count_sample_start` = `442.00`
- `neighbour_updates` = `0.00`

### Projectile storm (arrows + snowballs) (`projectile_storm`)

Category: **Physics**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `1935.03`, min `387.16`, p50 `1940.99`, p95 `2324.50`, p99 `2401.54`, 1%low `615.30`, 0.1%low `496.45`, std `257.12`

**Frame time (ms)**  avg `0.53`, p50 `0.52`, p95 `0.65`, p99 `1.36`, p99.9 `1.84`, max `2.58`

**Client tick (ms)**  avg `0.95`, p95 `1.03`, max `1.58`

**Memory**  start `1948 MB`, end `7224 MB`, peak `7224 MB`, GC `8 events / 47 ms`

**FPS over sampling window (ASCII):**

```
2244.2 |                                                                      ███  ██ █ 
2197.0 |                                                                  ██████████████
2149.7 |                                                                 ███████████████
2102.5 |                                                               █████████████████
2055.3 |                                                               █████████████████
2008.0 |                                             █             █   █████████████████
1960.8 |                 █                   █      ██   █        ██  ██████████████████
1913.5 | █ ██           ██            ████   ██   █████ ██  █     ██████████████████████
1866.3 | ████  ██ █    ███   █        █████  ████████████████   ████████████████████████
1819.1 |███████████ █  █████ █  █  ████████  ███████████████████████████████████████████
1771.8 |█████████████ ██████ █  █  █████████████████████████████████████████████████████
1724.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20733
   1 ms |   258
   2 ms |   9
```

**Extras:**

- `fps_harmonic_avg` = `1878.81`
- `fps_1pct_low` = `615.30`
- `entity_count_sample_end` = `251.00`
- `preset_quick` = `1.00`
- `block_state_changes` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `173.00`
- `projectiles_spawned` = `1000.00`
- `waves_spawned` = `40.00`
- `preload_duration_ms` = `23.00`
- `entity_count_sample_start` = `78.00`
- `projectiles_swept` = `270.00`
- `max_in_flight_observed` = `250.00`
- `preset_long` = `0.00`
- `seed` = `5099.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `496.45`
- `neighbour_updates` = `0.00`

### Redstone clocks (6×6) (`redstone_clocks`)

Category: **Redstone**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `4080.00`, min `689.51`, p50 `4269.85`, p95 `4504.50`, p99 `4564.13`, 1%low `1162.24`, 0.1%low `867.29`, std `543.92`

**Frame time (ms)**  avg `0.25`, p50 `0.23`, p95 `0.33`, p99 `0.59`, p99.9 `1.07`, max `1.45`

**Client tick (ms)**  avg `0.71`, p95 `0.77`, max `1.16`

**Memory**  start `6556 MB`, end `4652 MB`, peak `7828 MB`, GC `8 events / 476 ms`

**FPS over sampling window (ASCII):**

```
4324.5 |                                                      █  █                      
4272.3 |                        █                  █       █  █  ██                     
4220.2 |                       ██  █ █    █  █     █  █   █████ ███   █                 
4168.1 |      █            █   ███ █ ██   █ ███  ███  ██ ██████████████                 
4115.9 |      █   █        ███ ███ █ ███  ██████ ███  ██ ███████████████  █ █  █        
4063.8 |      █   █       ████ ███ █ ████ ██████████  ███████████████████ █ █ ██ █  █ ██
4011.7 |   █  █   ████    ██████████ ████ ███████████ ██████████████████████████ █  █ ██
3959.6 |█  █  █ █ ████    ██████████████████████████████████████████████████████████████
3907.4 |██ ██ █ █ ████    ██████████████████████████████████████████████████████████████
3855.3 |█████████ ████    ██████████████████████████████████████████████████████████████
3803.2 |████████████████  ██████████████████████████████████████████████████████████████
3751.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20937
   1 ms |   63
```

**Extras:**

- `entity_count_sample_start` = `1.00`
- `entity_count_sample_end` = `1.00`
- `preload_chunks` = `81.00`
- `preset_long` = `0.00`
- `fps_0p1pct_low` = `867.29`
- `preset_quick` = `1.00`
- `clocks_built` = `36.00`
- `observers_placed` = `72.00`
- `preset_full` = `0.00`
- `fps_harmonic_avg` = `3933.49`
- `fps_1pct_low` = `1162.24`
- `preload_duration_ms` = `50.00`
- `scheduled_block_ticks` = `9612.00`
- `entity_count_delta` = `0.00`
- `neighbour_updates` = `0.00`
- `block_state_changes` = `9612.00`
- `seed` = `4001.00`

### Redstone dust grid (16 trails ×32 + repeaters + lamps) (`redstone_dust_grid`)

Category: **Redstone**  |  Duration: 23109 ms  |  Sample ticks: 400

**FPS**  avg `4303.31`, min `130.61`, p50 `4466.28`, p95 `4833.25`, p99 `4904.36`, 1%low `881.98`, 0.1%low `307.99`, std `612.98`

**Frame time (ms)**  avg `0.24`, p50 `0.22`, p95 `0.31`, p99 `0.61`, p99.9 `1.40`, max `7.66`

**Client tick (ms)**  avg `0.65`, p95 `0.71`, max `1.02`

**Memory**  start `5900 MB`, end `6218 MB`, peak `7640 MB`, GC `8 events / 498 ms`

**FPS over sampling window (ASCII):**

```
4625.2 |██                       █                                                      
4570.5 |███                      █    █                                       █        █
4515.8 |███                      █ █████                                      █        █
4461.1 |███                  █   █ █████                  █                   █        █
4406.4 |███      █ █         █   █ ██████          █  █  ██   █       █   █ █ █ █      █
4351.8 |███     ████        ██ ███ ██████ █   █ █  █  █  ██   █  ██ █ █   █ █ ███      █
4297.1 |███    █████   █   ███ ███ ██████ █   █ ██ █  ██ ██   █ ███ █ ██  █ █ ███    █ █
4242.4 |███ █  █████   ██  ███████ ██████ █  ██ ██ █  ██ ██   █ ███ █ ███ █ █ ███  █ █ █
4187.7 |██████████████ █████████████████████ ██ ██ █████ ███  █████ █████ ███ ███ ██ █ █
4133.0 |████████████████████████████████████ ██ ████████████ ███████████████████████ ███
4078.3 |████████████████████████████████████ ███████████████████████████████████████ ███
4023.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20940
   1 ms |   47
   2 ms |   8
   7 ms |   5
```

**Extras:**

- `seed` = `4019.00`
- `trails_built` = `16.00`
- `dust_placed` = `464.00`
- `preset_full` = `0.00`
- `preset_long` = `0.00`
- `entity_count_sample_end` = `1.00`
- `entity_count_sample_start` = `1.00`
- `block_state_changes` = `70080.00`
- `fps_1pct_low` = `881.98`
- `neighbour_updates` = `0.00`
- `pulses_issued` = `46.00`
- `fps_harmonic_avg` = `4087.61`
- `preload_duration_ms` = `0.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `307.99`
- `scheduled_block_ticks` = `2240.00`
- `preset_quick` = `1.00`
- `entity_count_delta` = `0.00`
- `lamps_placed` = `128.00`
- `repeaters_placed` = `48.00`

### Piston/slime array (8×8 toggled every 8t) (`piston_slime_array`)

Category: **Redstone**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `4442.93`, min `378.19`, p50 `4690.43`, p95 `4904.49`, p99 `4955.40`, 1%low `1156.56`, 0.1%low `731.93`, std `606.21`

**Frame time (ms)**  avg `0.23`, p50 `0.21`, p95 `0.30`, p99 `0.58`, p99.9 `1.16`, max `2.64`

**Client tick (ms)**  avg `0.68`, p95 `0.76`, max `1.40`

**Memory**  start `5234 MB`, end `7468 MB`, peak `7778 MB`, GC `10 events / 886 ms`

**FPS over sampling window (ASCII):**

```
4737.8 |               █                                                                
4679.0 |    █    █ █   █ █           ██             █                                   
4620.3 |█ █ ██   ███   ████     █   ████   █      █ █                                   
4561.5 |███ ██  █████  ████ █   █   ████ █ █ █    ██████                                
4502.7 |███ ██  ██████ ████ █  ████ ████ █ ███   ███████                           █    
4443.9 |███ ███████████████ █ ████████████████  ████████       ██                 ██  █ 
4385.1 |███████████████████ ██████████████████ █████████   █ █ ██              █  ██ ███
4326.4 |████████████████████████████████████████████████   █ █ ██              █  ██ ███
4267.6 |██████████████████████████████████████████████████ █ ████  ██  █  █ █ ██ ███ ███
4208.8 |██████████████████████████████████████████████████ ██████ ███ ██  ██████████████
4150.0 |█████████████████████████████████████████████████████████ ██████████████████████
4091.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20960
   1 ms |   39
   2 ms |   1
```

**Extras:**

- `fps_harmonic_avg` = `4267.37`
- `fps_1pct_low` = `1156.56`
- `pistons_built` = `64.00`
- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`
- `slime_blocks` = `192.00`
- `block_state_changes` = `33600.00`
- `preset_full` = `0.00`
- `power_toggles` = `57.00`
- `entity_count_delta` = `0.00`
- `preload_duration_ms` = `41.00`
- `scheduled_block_ticks` = `0.00`
- `entity_count_sample_start` = `1.00`
- `preset_long` = `0.00`
- `seed` = `4027.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `731.93`
- `neighbour_updates` = `0.00`

### Static dense forest (orbit canopy, no worldgen) (`static_dense_forest`)

Category: **Chunks**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `4673.11`, min `691.42`, p50 `4859.09`, p95 `5063.29`, p99 `5104.65`, 1%low `1295.40`, 0.1%low `896.11`, std `562.46`

**Frame time (ms)**  avg `0.22`, p50 `0.21`, p95 `0.27`, p99 `0.53`, p99.9 `1.05`, max `1.45`

**Client tick (ms)**  avg `0.66`, p95 `0.72`, max `1.20`

**Memory**  start `6556 MB`, end `6810 MB`, peak `7480 MB`, GC `8 events / 479 ms`

**FPS over sampling window (ASCII):**

```
4913.4 |                                                                         █ █    
4859.9 |                                                       █     ██ ██  ███  █ ██ █ 
4806.5 |     █        █               █            █          ██   ████ ██  ███  █ ██ █ 
4753.1 |    ██     █  █  █          █ ████     ███ █         ███ █ ████ ██  ███  █ ██ ██
4699.6 |    ██     █  █  █ █  █    ███████   █████ █        ████ █ ████████ ████ ████ ██
4646.2 |█   ██ █   ██ █ ██ █ ██    ███████  █████████       ████ ██████████ ████████████
4592.7 |█   ██ █   ██ █ ██ █ ██ █ █████████ ██████████   █  ████████████████████████████
4539.3 |█ █ ████  ██████████ ████ █████████ ██████████ ████ ████████████████████████████
4485.8 |█ █ ████  ███████████████ ████████████████████ █████████████████████████████████
4432.4 |█ ██████████████████████████████████████████████████████████████████████████████
4378.9 |█ ██████████████████████████████████████████████████████████████████████████████
4325.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20972
   1 ms |   28
```

**Extras:**

- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`
- `trees_built` = `64.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `896.11`
- `fps_1pct_low` = `1295.40`
- `entity_count_sample_start` = `1.00`
- `leaf_blocks` = `7642.00`
- `preload_duration_ms` = `46.00`
- `seed` = `7039.00`
- `log_blocks` = `320.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `4522.31`
- `preset_full` = `0.00`
- `preset_long` = `0.00`

### Plains flyby (single-biome world) (`chunk_plains`)

Category: **Chunks**  |  Duration: 23107 ms  |  Sample ticks: 400

**FPS**  avg `2468.06`, min `44.75`, p50 `2560.16`, p95 `3279.76`, p99 `3484.32`, 1%low `284.69`, 0.1%low `74.92`, std `597.80`

**Frame time (ms)**  avg `0.46`, p50 `0.39`, p95 `0.77`, p99 `1.45`, p99.9 `7.72`, max `22.35`

**Client tick (ms)**  avg `0.93`, p95 `1.38`, max `3.67`

**Memory**  start `1706 MB`, end `4836 MB`, peak `7582 MB`, GC `19 events / 2054 ms`

**FPS over sampling window (ASCII):**

```
3062.5 |                                                                              █ 
2948.4 |                                                                           ██ ██
2834.3 |                                            █ █                █  █ ██   ████ ██
2720.2 |                                      ███  ████   ██    ██ █  ██ ██████ █████ ██
2606.2 |                              █ ███ █████  ████ █████ █ ██ █████ ████████████ ██
2492.1 |                            █ █ ███ █████████████████ ████ █████████████████████
2378.0 |                         █ ██ ███████████████████████ ████ █████████████████████
2263.9 | █       █           ███ ███████████████████████████████████████████████████████
2149.8 |█████ █ ██    █     ████ ███████████████████████████████████████████████████████
2035.7 |█████ ████ ████   ██████████████████████████████████████████████████████████████
1921.7 |████████████████ ███████████████████████████████████████████████████████████████
1807.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20460
   1 ms | █  421
   2 ms |   55
   3 ms |   28
   4 ms |   6
   5 ms |   6
   6 ms |   2
   7 ms |   3
   8 ms |   3
   9 ms |   2
  10 ms |   1
  11 ms |   2
  12 ms |   1
  13 ms |   1
  14 ms |   1
  15 ms |   2
  16 ms |   2
  19 ms |   1
  20 ms |   2
  22 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:plains`
- `fps_harmonic_avg` = `2157.41`
- `fps_1pct_low` = `284.69`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `30.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-18.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `48.00`
- `surface_water_ratio` = `0.02`
- `entity_count_sample_start` = `48.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7411.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `74.92`

### Jungle flyby (single-biome world) (`chunk_jungle`)

Category: **Chunks**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1487.86`, min `37.95`, p50 `1553.16`, p95 `2043.32`, p99 `2361.83`, 1%low `161.99`, 0.1%low `55.99`, std `381.12`

**Frame time (ms)**  avg `0.78`, p50 `0.64`, p95 `1.45`, p99 `2.77`, p99.9 `13.15`, max `26.35`

**Client tick (ms)**  avg `0.91`, p95 `1.30`, max `11.67`

**Memory**  start `2474 MB`, end `2078 MB`, peak `7338 MB`, GC `20 events / 1527 ms`

**FPS over sampling window (ASCII):**

```
1750.7 | █                                                                              
1708.4 | █                                                                              
1666.0 | █ ███                               █ █                                        
1623.6 | █ ████                         █    ███                                        
1581.3 | █ ████               █ █  █    █  █ ███  █ ██   █                  █           
1538.9 |██ ████ █   █       █ █ ██ █    █  █ ███  █ ██ ███                  █    █      
1496.5 |███████ █ █ █      ██ ████ █   ██  ████████ ██ ███     █  █ ███ ██  █ █  █     █
1454.2 |█████████ █ █ █  ████ ███████ ███ ████████████████ █ █ █  █ ██████  █ ██ ██ █  █
1411.8 |█████████ █ █ █  ████████████ ████████████████████████ █  █ ███████ █ ██ ████  █
1369.5 |█████████ ███ ██████████████████████████████████████████  █████████ █ ██████████
1327.1 |████████████████████████████████████████████████████████ ██████████ ████████████
1284.7 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  18599
   1 ms | ████  1984
   2 ms |   229
   3 ms |   75
   4 ms |   33
   5 ms |   15
   6 ms |   13
   7 ms |   11
   8 ms |   5
   9 ms |   5
  10 ms |   5
  11 ms |   2
  12 ms |   1
  13 ms |   5
  14 ms |   2
  15 ms |   3
  16 ms |   2
  17 ms |   2
  18 ms |   1
  19 ms |   3
  20 ms |   1
  21 ms |   1
  22 ms |   1
  23 ms |   1
  26 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:jungle`
- `fps_harmonic_avg` = `1278.23`
- `fps_1pct_low` = `161.99`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `13.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-15.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `24.00`
- `surface_water_ratio` = `0.06`
- `entity_count_sample_start` = `28.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7417.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `55.99`

### Desert flyby (single-biome world) (`chunk_desert`)

Category: **Chunks**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `2794.32`, min `48.25`, p50 `2927.40`, p95 `3388.68`, p99 `3487.97`, 1%low `336.25`, 0.1%low `74.16`, std `546.53`

**Frame time (ms)**  avg `0.40`, p50 `0.34`, p95 `0.64`, p99 `1.24`, p99.9 `5.96`, max `20.73`

**Client tick (ms)**  avg `0.87`, p95 `1.08`, max `14.66`

**Memory**  start `4946 MB`, end `2808 MB`, peak `7678 MB`, GC `20 events / 1464 ms`

**FPS over sampling window (ASCII):**

```
3039.0 |    █                                                                           
2986.8 | █ ████    █     █                                                        ███   
2934.5 | █ ████   ████   ███                                                      ███   
2882.3 | █ ████ █ ████  ████ █  █     █           █     █ █     ██           █   ████ ██
2830.1 | ██████ █ ████  ████ █  ██   ██      █    ███   ███    ███ █  ██    ██   ████ ██
2777.8 | ██████ ██████  ████ █ ███ █ ███   ███   ████ █████    ███ █  ██ █████  █████ ██
2725.6 |███████ ██████  ████ █ ███ █ ████  ███ █ ████ █████   ████ █████ ████████████ ██
2673.4 |███████ ██████ █████ █ ███████████ █████ ████ ██████ █████ █████ ████████████ ██
2621.1 |██████████████ █████ ███████████████████ ████ ██████ █████ █████ ████████████ ██
2568.9 |████████████████████ ████████████████████████ ████████████ █████ ████████████ ██
2516.7 |█████████████████████████████████████████████ ███████████████████████████████ ██
2464.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20651
   1 ms | █  286
   2 ms |   27
   3 ms |   8
   4 ms |   4
   5 ms |   3
   6 ms |   3
   7 ms |   2
   8 ms |   1
  10 ms |   2
  11 ms |   1
  12 ms |   1
  13 ms |   2
  14 ms |   1
  15 ms |   1
  17 ms |   1
  18 ms |   1
  19 ms |   1
  20 ms |   4
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:desert`
- `fps_harmonic_avg` = `2500.48`
- `fps_1pct_low` = `336.25`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `33.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-10.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `49.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `43.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7433.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `74.16`

### Taiga flyby (single-biome world) (`chunk_taiga`)

Category: **Chunks**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `3121.23`, min `47.03`, p50 `3332.22`, p95 `4084.97`, p99 `4212.30`, 1%low `319.63`, 0.1%low `80.84`, std `780.07`

**Frame time (ms)**  avg `0.37`, p50 `0.30`, p95 `0.65`, p99 `1.21`, p99.9 `5.83`, max `21.26`

**Client tick (ms)**  avg `0.84`, p95 `1.11`, max `1.58`

**Memory**  start `1876 MB`, end `2710 MB`, peak `7324 MB`, GC `20 events / 1469 ms`

**FPS over sampling window (ASCII):**

```
3873.6 |                                                                  █          ██ 
3715.3 |                                                          █ █    ███  █ ████ ███
3556.9 |                                          █   ██ █  █ █ █████ ██ ███ ██ ████ ███
3398.5 |                                   ███ ██ ███ ██ ████ ███████ ██████ ███████ ███
3240.1 |                            ███  █████ ██ ███ ██ ████ ███████ ██████ ███████ ███
3081.7 |                     █ █    ███  █████ ██████ ███████ ███████ ██████████████ ███
2923.4 |               ███ █ ███ █ ████ ██████ ██████ ███████ ███████ ██████████████████
2765.0 |          █    ███ █████ █████████████ ██████████████ ██████████████████████████
2606.6 |         ███ █████ █████████████████████████████████████████████████████████████
2448.2 |█    █   ███ ███████████████████████████████████████████████████████████████████
2289.9 |█ █ ██ █████ ███████████████████████████████████████████████████████████████████
2131.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20634
   1 ms | █  278
   2 ms |   32
   3 ms |   21
   4 ms |   7
   5 ms |   8
   6 ms |   1
   7 ms |   2
   8 ms |   1
   9 ms |   1
  10 ms |   5
  11 ms |   1
  13 ms |   1
  14 ms |   2
  16 ms |   3
  17 ms |   1
  19 ms |   1
  21 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:taiga`
- `fps_harmonic_avg` = `2676.58`
- `fps_1pct_low` = `319.63`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `5.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-16.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `49.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `21.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7451.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `80.84`

### Snowy plains flyby (`chunk_snowy`)

Category: **Chunks**  |  Duration: 23092 ms  |  Sample ticks: 400

**FPS**  avg `3338.04`, min `44.09`, p50 `3552.40`, p95 `4133.94`, p99 `4257.13`, 1%low `388.02`, 0.1%low `93.87`, std `766.16`

**Frame time (ms)**  avg `0.34`, p50 `0.28`, p95 `0.61`, p99 `1.12`, p99.9 `3.99`, max `22.68`

**Client tick (ms)**  avg `0.80`, p95 `1.11`, max `1.91`

**Memory**  start `4240 MB`, end `3496 MB`, peak `7410 MB`, GC `20 events / 1408 ms`

**FPS over sampling window (ASCII):**

```
3872.7 |                                                   █ ██   █ █                   
3741.4 |                                      █       █ █ ██ ███ ████    ████           
3610.1 |                                 ██ █ █  ███ ███████ ███ ████ ██ ████    ███   █
3478.9 |                           █  █  ████ ██ ███ ███████ ███ ████ ███████ ██████ ███
3347.6 |                   ███    ██  █ █████ ██████ ███████ ████████ ███████ ██████ ███
3216.3 |            █ █ █  ███   ████ ███████ ██████ ███████ ████████ ███████ ██████ ███
3085.0 |      █  █ ████ █  ███ ██████████████ ██████ ███████ ████████ ██████████████ ███
2953.7 |   █ ███ ██████ ██ ███ ██████████████ ██████ ███████ ████████ ██████████████ ███
2822.4 |██ █ ███ ██████ ████████████████████████████████████ ███████████████████████████
2691.2 |██ █ ███ ███████████████████████████████████████████████████████████████████████
2559.9 |████ ███ ███████████████████████████████████████████████████████████████████████
2428.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20695
   1 ms |   244
   2 ms |   26
   3 ms |   14
   4 ms |   5
   5 ms |   3
   6 ms |   1
   8 ms |   1
   9 ms |   1
  12 ms |   2
  13 ms |   3
  14 ms |   1
  16 ms |   1
  19 ms |   1
  22 ms |   2
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:snowy_plains`
- `fps_harmonic_avg` = `2916.09`
- `fps_1pct_low` = `388.02`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `5.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-19.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `1.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `24.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7457.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `93.87`

### Forest flyby (`chunk_forest`)

Category: **Chunks**  |  Duration: 23106 ms  |  Sample ticks: 400

**FPS**  avg `2156.32`, min `35.97`, p50 `2230.15`, p95 `2836.88`, p99 `3109.45`, 1%low `249.81`, 0.1%low `62.88`, std `480.40`

**Frame time (ms)**  avg `0.53`, p50 `0.45`, p95 `0.86`, p99 `1.56`, p99.9 `8.74`, max `27.80`

**Client tick (ms)**  avg `0.91`, p95 `1.20`, max `18.01`

**Memory**  start `2238 MB`, end `4160 MB`, peak `7088 MB`, GC `22 events / 1868 ms`

**FPS over sampling window (ASCII):**

```
2413.5 | █                                                █                             
2355.0 | █                                          ███   █  ██    █    █               
2296.5 | ██ █                                   ██  ███  ███ ██ █ █████ █ ██ █  █       
2238.0 |███ █ █                             █ █ ██  ███  ███ ███████████████ █  █       
2179.5 |█████ █                       ██   ██ ████ ████  ███████████████████ ████ ███   
2121.0 |█████ ████            █   █   ██ █ ██ ██████████████████████████████ ████ ███  █
2062.5 |██████████       █    █  ██ █ ██ ████ ██████████████████████████████████████████
2004.0 |██████████   █ █ █   ██  ██ ████ ███████████████████████████████████████████████
1945.5 |████████████ █ ███ █ ███████████████████████████████████████████████████████████
1887.0 |██████████████ ███ █████████████████████████████████████████████████████████████
1828.5 |██████████████ █████████████████████████████████████████████████████████████████
1770.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20292
   1 ms | █  589
   2 ms |   49
   3 ms |   20
   4 ms |   11
   5 ms |   9
   6 ms |   5
   7 ms |   3
   8 ms |   2
   9 ms |   2
  10 ms |   2
  11 ms |   1
  12 ms |   1
  13 ms |   2
  15 ms |   2
  16 ms |   3
  17 ms |   1
  20 ms |   2
  21 ms |   1
  22 ms |   1
  25 ms |   1
  27 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:forest`
- `fps_harmonic_avg` = `1903.08`
- `fps_1pct_low` = `249.81`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `34.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `11.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `49.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `23.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7477.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `62.88`

### Savanna flyby (`chunk_savanna`)

Category: **Chunks**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `2711.99`, min `39.17`, p50 `2833.66`, p95 `3427.00`, p99 `3589.38`, 1%low `302.08`, 0.1%low `73.22`, std `567.62`

**Frame time (ms)**  avg `0.42`, p50 `0.35`, p95 `0.69`, p99 `1.34`, p99.9 `6.39`, max `25.53`

**Client tick (ms)**  avg `0.92`, p95 `1.31`, max `2.63`

**Memory**  start `4906 MB`, end `4778 MB`, peak `7498 MB`, GC `20 events / 1486 ms`

**FPS over sampling window (ASCII):**

```
2941.5 |           █     █                                                              
2892.0 |           █   █ █   █                          █               █               
2842.5 |           █   █ █ ███                          █ █      █  █  ██   █ █ █   █   
2793.0 |       ███ ██ ██ █ ████     █    ██    █        █ █  █   █  █████ █ █ █ █  ██   
2743.6 |  █  █████ ████████████ █ ███    ██    █   ██   █ █  █████ ██████ ███ █ █  ██   
2694.1 | ██  █████ ████████████ █████   ███    █ █ ███ █████ █████ ██████ █████ █████ █ 
2644.6 | ███ █████ ████████████ █████ █ ███  █ █ █ ███ █████ █████ ██████ █████ █████ ██
2595.1 | ███ █████ ████████████ █████ █ ███  ███ █ █████████ █████ ██████ █████ █████ ██
2545.6 | ███ █████ ██████████████████ █ ███ ████ █ ███████████████ ██████████████████ ██
2496.2 |████ ████████████████████████ █████ ████ █ ███████████████ ██████████████████ ██
2446.7 |█████████████████████████████ ██████████ █ █████████████████████████████████████
2397.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20601
   1 ms | █  310
   2 ms |   38
   3 ms |   17
   4 ms |   7
   5 ms |   3
   6 ms |   4
   7 ms |   4
   9 ms |   3
  10 ms |   2
  12 ms |   1
  13 ms |   1
  15 ms |   3
  16 ms |   1
  18 ms |   1
  20 ms |   1
  22 ms |   1
  24 ms |   1
  25 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:savanna`
- `fps_harmonic_avg` = `2396.12`
- `fps_1pct_low` = `302.08`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `26.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-33.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `23.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `59.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7481.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `73.22`

### Swamp flyby (`chunk_swamp`)

Category: **Chunks**  |  Duration: 23108 ms  |  Sample ticks: 400

**FPS**  avg `1946.38`, min `38.70`, p50 `1938.55`, p95 `2713.70`, p99 `3095.05`, 1%low `212.22`, 0.1%low `62.16`, std `507.67`

**Frame time (ms)**  avg `0.59`, p50 `0.52`, p95 `1.00`, p99 `2.00`, p99.9 `10.27`, max `25.84`

**Client tick (ms)**  avg `0.83`, p95 `1.17`, max `4.82`

**Memory**  start `1890 MB`, end `5480 MB`, peak `7344 MB`, GC `16 events / 1101 ms`

**FPS over sampling window (ASCII):**

```
2439.5 |   ███      █        █                                                          
2344.0 |█  ███    █ ████ █  ██                                                          
2248.5 |██████ ████ ████ █████   █                                                      
2153.0 |███████████ ████ ██████████   █                                                 
2057.5 |████████████████████████████ ██    █                                            
1962.0 |██████████████████████████████████ █                                         █ █
1866.5 |████████████████████████████████████████  █    █                         █ ███ █
1771.0 |████████████████████████████████████████████ ███      █   █      █  ██ ███████ █
1675.5 |███████████████████████████████████████████████████ ███  ██  █ ███  ████████████
1580.0 |███████████████████████████████████████████████████████  ███████████████████████
1484.5 |████████████████████████████████████████████████████████ ███████████████████████
1388.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  19964
   1 ms | ██  826
   2 ms |   97
   3 ms |   42
   4 ms |   22
   5 ms |   19
   6 ms |   3
   7 ms |   2
   8 ms |   2
   9 ms |   1
  10 ms |   3
  11 ms |   3
  12 ms |   3
  13 ms |   1
  15 ms |   1
  16 ms |   3
  17 ms |   1
  19 ms |   2
  20 ms |   1
  21 ms |   3
  25 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:swamp`
- `fps_harmonic_avg` = `1682.11`
- `fps_1pct_low` = `212.22`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `37.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `30.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `50.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `7.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7487.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `62.16`

### Cherry grove flyby (`chunk_cherry`)

Category: **Chunks**  |  Duration: 23106 ms  |  Sample ticks: 400

**FPS**  avg `3146.75`, min `35.27`, p50 `3286.23`, p95 `4242.68`, p99 `4342.16`, 1%low `325.63`, 0.1%low `77.95`, std `847.13`

**Frame time (ms)**  avg `0.37`, p50 `0.30`, p95 `0.64`, p99 `1.21`, p99.9 `6.26`, max `28.35`

**Client tick (ms)**  avg `0.91`, p95 `1.22`, max `1.89`

**Memory**  start `3354 MB`, end `2166 MB`, peak `7352 MB`, GC `26 events / 2126 ms`

**FPS over sampling window (ASCII):**

```
4119.2 |                                                                             ██ 
3934.0 |                                                                     ██ █ ██ ███
3748.8 |                                             █       █  ████ ███ ███ ██ ████ ███
3563.5 |                                           █ ██ ███  ███████ ███████ ██ ████ ███
3378.3 |                                   ██ ██ ███ ██ ███ ████████ ███████ ██ ████ ███
3193.1 |                                 ████ ██████ ███████████████ ███████ ███████ ███
3007.8 |                           ███ ██████ ██████ ███████████████ ███████████████████
2822.6 |                     ███  ████ ██████ ██████████████████████████████████████████
2637.4 |         ██  █ ███ █████ █████ █████████████████████████████████████████████████
2452.2 |    ██   ███████████████████████████████████████████████████████████████████████
2266.9 |█ ██████████████████████████████████████████████████████████████████████████████
2081.7 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20676
   1 ms |   243
   2 ms |   33
   3 ms |   13
   4 ms |   9
   5 ms |   3
   6 ms |   2
   8 ms |   3
   9 ms |   5
  10 ms |   4
  11 ms |   1
  12 ms |   1
  13 ms |   1
  15 ms |   1
  16 ms |   1
  17 ms |   1
  18 ms |   1
  20 ms |   1
  28 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:cherry_grove`
- `fps_harmonic_avg` = `2682.44`
- `fps_1pct_low` = `325.63`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `13.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-62.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `49.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `75.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7499.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `77.95`

### Badlands flyby (`chunk_badlands`)

Category: **Chunks**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `3572.36`, min `42.61`, p50 `3800.84`, p95 `4323.39`, p99 `4401.41`, 1%low `354.81`, 0.1%low `81.04`, std `753.65`

**Frame time (ms)**  avg `0.32`, p50 `0.26`, p95 `0.55`, p99 `1.06`, p99.9 `5.37`, max `23.47`

**Client tick (ms)**  avg `0.77`, p95 `1.07`, max `5.02`

**Memory**  start `5194 MB`, end `3906 MB`, peak `7116 MB`, GC `23 events / 1630 ms`

**FPS over sampling window (ASCII):**

```
4118.2 |                                                                           █ ██ 
3997.0 |    █                                                           █    █  ████ ███
3875.8 |   ██ ██  ██                                                 ██ ███  ██ ████ ███
3754.6 | ████ ██  ████ █                                    █ █ ███  ██ ███  ███████ ███
3633.4 |█████ ██ █████ █    █                 █      █   ██ ███████  ██████ ████████ ███
3512.2 |█████ ████████ ██ ███ █    █ █    ██ ██  █   ██  ██ ███████  ███████████████ ███
3391.0 |█████ ████████ ██ ███ █ ████ █  ████ ██ ███  ██ ███ ████████ ███████████████ ███
3269.8 |█████ ████████ ██████ ██████ █ █████ ██████████████ ████████ ███████████████ ███
3148.6 |█████ ████████ ██████ ██████ ███████ ██████████████ ████████████████████████ ███
3027.3 |█████ ████████ █████████████ ███████ ██████████████ ████████████████████████ ███
2906.1 |███████████████████████████████████████████████████ ████████████████████████████
2784.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20739
   1 ms |   193
   2 ms |   25
   3 ms |   9
   4 ms |   11
   5 ms |   4
   6 ms |   3
   7 ms |   1
   8 ms |   1
   9 ms |   2
  10 ms |   1
  13 ms |   2
  14 ms |   2
  15 ms |   2
  16 ms |   1
  17 ms |   1
  18 ms |   1
  20 ms |   1
  23 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:badlands`
- `fps_harmonic_avg` = `3113.33`
- `fps_1pct_low` = `354.81`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-1.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `48.00`
- `surface_water_ratio` = `0.07`
- `entity_count_sample_start` = `2.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7507.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `81.04`

### Dark forest flyby (dense canopy) (`chunk_dark_forest`)

Category: **Chunks**  |  Duration: 23106 ms  |  Sample ticks: 400

**FPS**  avg `2075.45`, min `50.13`, p50 `2107.93`, p95 `2783.96`, p99 `3075.99`, 1%low `219.61`, 0.1%low `71.97`, std `494.64`

**Frame time (ms)**  avg `0.55`, p50 `0.47`, p95 `0.91`, p99 `1.77`, p99.9 `10.98`, max `19.95`

**Client tick (ms)**  avg `0.88`, p95 `1.23`, max `4.44`

**Memory**  start `1938 MB`, end `6786 MB`, peak `7212 MB`, GC `17 events / 1217 ms`

**FPS over sampling window (ASCII):**

```
2513.2 |                                                                      █  █ ██   
2435.6 |                                                            █     █   ██ █ ██ ██
2358.0 |                                                       █    █    ██ ████ ████ ██
2280.4 |                                                     ████ ███  █ ██ ████ ████ ██
2202.9 |                                             █   ███ █████████ █ ██ ████ ████ ██
2125.3 | █                                   █       █ █ ███████████████████████████████
2047.7 | █ █                           ██ █ ██   █ █████████████████████████████████████
1970.2 | ███   █   █       █   ██   █  ██ ████ ███ █████████████████████████████████████
1892.6 | ███ ███  ██  ██ █ █ ████  ██ ███ ████████ █████████████████████████████████████
1815.0 |████████ ███ ███ ███ ████ ██████████████████████████████████████████████████████
1737.5 |████████ ███████ ███████████████████████████████████████████████████████████████
1659.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20175
   1 ms | █  647
   2 ms |   78
   3 ms |   31
   4 ms |   11
   5 ms |   9
   6 ms |   13
   7 ms |   5
   8 ms |   3
   9 ms |   5
  10 ms |   2
  11 ms |   7
  12 ms |   2
  13 ms |   3
  14 ms |   3
  15 ms |   2
  17 ms |   1
  18 ms |   2
  19 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:dark_forest`
- `fps_harmonic_avg` = `1805.36`
- `fps_1pct_low` = `219.61`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `13.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-11.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `49.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `24.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7517.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `71.97`

### Windswept hills flyby (`chunk_mountain`)

Category: **Chunks**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `2455.73`, min `33.41`, p50 `2536.78`, p95 `3135.83`, p99 `3333.33`, 1%low `296.41`, 0.1%low `67.89`, std `507.47`

**Frame time (ms)**  avg `0.46`, p50 `0.39`, p95 `0.73`, p99 `1.32`, p99.9 `5.89`, max `29.93`

**Client tick (ms)**  avg `0.80`, p95 `1.00`, max `1.44`

**Memory**  start `4476 MB`, end `3874 MB`, peak `7442 MB`, GC `22 events / 2024 ms`

**FPS over sampling window (ASCII):**

```
2748.7 |                                                                   █            
2691.3 |                                                                █  █    █   █ ██
2633.9 |                                                     █    █   █████████ █ ███ ██
2576.6 |█                                                █ ███  ███ █ █████████ █████ ██
2519.2 |█  █  █ ██                     █              ██ █████ ████ ███████████ █████ ██
2461.8 |█████ █ ██  █                 ██   █   █    ████ █████ ████ ███████████ █████ ██
2404.5 |█████ ████ ███ █        ██   ███   ██ ████  ████ █████ ████ ███████████ ████████
2347.1 |█████ ████ █████   █   ███ █ ███  ████████ ████████████████ ███████████ ████████
2289.7 |█████ ██████████ ███  ████ █████ █████████ ████████████████ ████████████████████
2232.4 |████████████████ ████ ████ █████████████████████████████████████████████████████
2175.0 |█████████████████████ ████ █████████████████████████████████████████████████████
2117.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20547
   1 ms | █  361
   2 ms |   43
   3 ms |   17
   4 ms |   8
   5 ms |   4
   7 ms |   1
   8 ms |   2
   9 ms |   1
  11 ms |   2
  12 ms |   1
  13 ms |   1
  14 ms |   3
  15 ms |   1
  16 ms |   1
  17 ms |   2
  18 ms |   1
  19 ms |   1
  20 ms |   1
  21 ms |   1
  29 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `scan_fallback` = `false`
- `biome` = `minecraft:windswept_hills`
- `fps_harmonic_avg` = `2192.76`
- `fps_1pct_low` = `296.41`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `78.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `62.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `48.00`
- `surface_water_ratio` = `0.02`
- `entity_count_sample_start` = `16.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7523.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `67.89`

### Idle baseline (orbit, flat world) (`idle_baseline`)

Category: **Baseline**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `4485.01`, min `535.68`, p50 `4618.94`, p95 `5005.01`, p99 `5058.17`, 1%low `1259.83`, 0.1%low `817.22`, std `599.28`

**Frame time (ms)**  avg `0.23`, p50 `0.22`, p95 `0.29`, p99 `0.53`, p99.9 `1.08`, max `1.87`

**Client tick (ms)**  avg `0.66`, p95 `0.71`, max `1.13`

**Memory**  start `4534 MB`, end `4036 MB`, peak `7812 MB`, GC `8 events / 520 ms`

**FPS over sampling window (ASCII):**

```
4762.3 |       ███  █                                        █                          
4684.2 |█     ████ ███              █    █ ███ █     █       █    █                     
4606.1 |█     █████████             ██ ███ ██████    █ ████ ███ ███      █              
4528.0 |█    ███████████            ██████ █████████ █ ████████ █████    █             █
4449.9 |█    ████████████   █    ██ ███████████████████████████ ███████ ██       ███   █
4371.8 |█    █████████████████ ████ ███████████████████████████████████ ██   ██  ████  █
4293.7 |█   ███████████████████████ ███████████████████████████████████████  ███ ████  █
4215.6 |█   ████████████████████████████████████████████████████████████████ █████████ █
4137.5 |█   ████████████████████████████████████████████████████████████████ ███████████
4059.4 |█   ████████████████████████████████████████████████████████████████████████████
3981.3 |█  █████████████████████████████████████████████████████████████████████████████
3903.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20967
   1 ms |   33
```

**Extras:**

- `seed` = `1923.00`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `49.00`
- `entity_count_sample_start` = `1.00`
- `preset_full` = `0.00`
- `preset_long` = `0.00`
- `entity_count_delta` = `0.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `817.22`
- `entity_count_sample_end` = `1.00`
- `fps_harmonic_avg` = `4322.71`
- `fps_1pct_low` = `1259.83`

### Particle diversity stress (16 types simultaneously) (`particle_diversity_stress`)

Category: **Stress**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `436.84`, min `130.02`, p50 `454.63`, p95 `490.29`, p99 `502.44`, 1%low `155.57`, 0.1%low `143.21`, std `67.19`

**Frame time (ms)**  avg `2.41`, p50 `2.20`, p95 `3.09`, p99 `6.18`, p99.9 `6.70`, max `7.69`

**Client tick (ms)**  avg `3.25`, p95 `3.54`, max `3.84`

**Memory**  start `1408 MB`, end `5242 MB`, peak `7564 MB`, GC `4 events / 38 ms`

**FPS over sampling window (ASCII):**

```
470.5 |                                                 █                              
464.5 |█                                               ██                              
458.4 |█                                              ███                              
452.3 |█ █                                            ███████                          
446.3 |███                    █  ████  █  █   ██   █  █████████      █                 
440.2 |████                   ███████ ███ ██ ███  ███ ██████████ ██████                
434.1 |█████ █ █ █            ██████████████ ███████████████████████████ █        ██   
428.1 |█████████████     ██  ██████████████████████████████████████████████  █ ████████
422.0 |█████████████ █ ████ ████████████████████████████████████████████████ ██████████
415.9 |█████████████ ██████ ███████████████████████████████████████████████████████████
409.9 |████████████████████ ███████████████████████████████████████████████████████████
403.8 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   1 ms | █  116
   2 ms | ████████████████████████████████████████  7764
   3 ms |   25
   4 ms |   1
   5 ms | █  259
   6 ms | █  139
   7 ms |   2
```

**Extras:**

- `fps_0p1pct_low` = `143.21`
- `preset_full` = `0.00`
- `entity_count_sample_start` = `1.00`
- `preload_chunks` = `81.00`
- `fps_1pct_low` = `155.57`
- `preset_long` = `0.00`
- `particle_types` = `16.00`
- `particles_spawned` = `256000.00`
- `seed` = `2521.00`
- `preload_duration_ms` = `49.00`
- `fps_harmonic_avg` = `415.29`
- `preset_quick` = `1.00`
- `entity_count_delta` = `0.00`
- `entity_count_sample_end` = `1.00`

### Fluid spread (water basin, 4-step periodic reset) (`fluid_spread`)

Category: **Fluids**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `4324.26`, min `479.50`, p50 `4535.15`, p95 `4692.63`, p99 `4734.85`, 1%low `1219.52`, 0.1%low `813.06`, std `541.68`

**Frame time (ms)**  avg `0.24`, p50 `0.22`, p95 `0.29`, p99 `0.56`, p99.9 `1.06`, max `2.09`

**Client tick (ms)**  avg `0.66`, p95 `0.72`, max `1.15`

**Memory**  start `4312 MB`, end `4036 MB`, peak `7814 MB`, GC `8 events / 459 ms`

**FPS over sampling window (ASCII):**

```
4504.3 |         ██                               █                                     
4456.2 |         ██       █                       █    █   █ █       █  █ █   █   █  █  
4408.1 |      █  ██       ████   ██     █ █       ██   █████ ███ ██  █  ████████ ███ ██ 
4360.0 |      █  ██       ████   ███  █ █ █ █   █ ██ █ █████ ███ █████  ████████████████
4311.8 |      █  ██       █████  ███  █ █ █ ███ ████████████ █████████ █████████████████
4263.7 |   ██ █  ███      █████ █████ █ █ █ ███ ████████████████████████████████████████
4215.6 | ████ █  ███      ███████████ █ █ █ ███ ████████████████████████████████████████
4167.5 | ████ █  ███   █  █████████████ ███████ ████████████████████████████████████████
4119.3 | ████ █  █████ ██ █████████████ ████████████████████████████████████████████████
4071.2 |█████ ██ ████████ ██████████████████████████████████████████████████████████████
4023.1 |████████ ████████ ██████████████████████████████████████████████████████████████
3975.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20963
   1 ms |   36
   2 ms |   1
```

**Extras:**

- `scheduled_fluid_ticks` = `3146.00`
- `fps_harmonic_avg` = `4180.50`
- `fps_1pct_low` = `1219.52`
- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`
- `block_state_changes` = `2305.00`
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
- `fps_0p1pct_low` = `813.06`
- `neighbour_updates` = `0.00`

### Lighting update (16×16 glowstone reveal/hide) (`lighting_update`)

Category: **Lighting**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `4302.54`, min `540.92`, p50 `4260.76`, p95 `4878.05`, p99 `4921.26`, 1%low `1168.11`, 0.1%low `729.53`, std `578.47`

**Frame time (ms)**  avg `0.24`, p50 `0.23`, p95 `0.30`, p99 `0.56`, p99.9 `1.12`, max `1.85`

**Client tick (ms)**  avg `0.70`, p95 `0.76`, max `1.08`

**Memory**  start `4594 MB`, end `4014 MB`, peak `7694 MB`, GC `8 events / 485 ms`

**FPS over sampling window (ASCII):**

```
4639.7 |                                 █                                              
4578.8 |                                 █                  █                           
4517.9 |                                 █    █  █          █         █           █     
4456.9 |█    █ █                         █    █  █         ██         █           █    █
4396.0 |█  █ ███        █ █ █   █ █ █    ██  ██  ██  ██  █ ██         █ █         █    █
4335.1 |█  █ ███ █      ███ ██  █ ███   ███  ██ ████ ██  ████        ██ █   █ █   █    █
4274.2 |█ ██████ █      ███████ ██████ ████████ ████ ██ █████  ███   ██ ██  █ █████    █
4213.2 |██████████ ██   ██████████████ ████████ ██████████████ ████ ███ ██ ██ ███████ ██
4152.3 |██████████ ██ █ ██████████████████████████████████████████████████ ██ ███████ ██
4091.4 |██████████ ██ █ ████████████████████████████████████████████████████████████████
4030.5 |█████████████ ██████████████████████████████████████████████████████████████████
3969.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20949
   1 ms |   51
```

**Extras:**

- `entity_count_sample_start` = `1.00`
- `entity_count_sample_end` = `1.00`
- `toggles` = `22.00`
- `preload_chunks` = `81.00`
- `preset_long` = `0.00`
- `fps_0p1pct_low` = `729.53`
- `preset_quick` = `1.00`
- `preset_full` = `0.00`
- `fps_harmonic_avg` = `4145.97`
- `fps_1pct_low` = `1168.11`
- `preload_duration_ms` = `50.00`
- `scheduled_block_ticks` = `0.00`
- `entity_count_delta` = `0.00`
- `blocks_per_toggle` = `256.00`
- `neighbour_updates` = `0.00`
- `block_state_changes` = `4864.00`
- `seed` = `9007.00`

### Hopper grid 20×20 (transfer storm) (`hopper_grid`)

Category: **Block-Entities**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `3719.37`, min `615.16`, p50 `3871.47`, p95 `4083.30`, p99 `4130.52`, 1%low `1154.91`, 0.1%low `863.18`, std `440.12`

**Frame time (ms)**  avg `0.28`, p50 `0.26`, p95 `0.33`, p99 `0.60`, p99.9 `1.05`, max `1.63`

**Client tick (ms)**  avg `0.66`, p95 `0.71`, max `1.08`

**Memory**  start `3564 MB`, end `7682 MB`, peak `7682 MB`, GC `10 events / 932 ms`

**FPS over sampling window (ASCII):**

```
3954.5 | █ █                                                                            
3896.8 | █ ██ █   █       █                                                             
3839.1 |███████ ████ ███  █  █                     █                                    
3781.4 |████████████ ███ ███ ██ ██  █ █ █    █   █ █ ██   ██                           █
3723.7 |████████████ ███████ ██ ███ █ ███ ████ █████████ ████████                      █
3666.1 |███████████████████████ █████████ ███████████████████████    █ █  ██ ██  █  █  █
3608.4 |█████████████████████████████████████████████████████████    ███  ██ ██  █  ████
3550.7 |███████████████████████████████████████████████████████████  ███ ██████ ██ █████
3493.0 |████████████████████████████████████████████████████████████████ ███████████████
3435.3 |████████████████████████████████████████████████████████████████ ███████████████
3377.7 |████████████████████████████████████████████████████████████████ ███████████████
3320.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20957
   1 ms |   43
```

**Extras:**

- `entity_count_sample_start` = `1.00`
- `entity_count_sample_end` = `1.00`
- `preload_chunks` = `81.00`
- `preset_long` = `0.00`
- `fps_0p1pct_low` = `863.18`
- `preset_quick` = `1.00`
- `restocks` = `20.00`
- `preset_full` = `0.00`
- `fps_harmonic_avg` = `3616.44`
- `fps_1pct_low` = `1154.91`
- `preload_duration_ms` = `23.00`
- `scheduled_block_ticks` = `0.00`
- `entity_count_delta` = `0.00`
- `neighbour_updates` = `0.00`
- `block_state_changes` = `0.00`
- `hoppers_built` = `400.00`
- `seed` = `8011.00`

### Comparator storage (8×8 chests + comparators) (`comparator_storage`)

Category: **Block-Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `3128.70`, min `398.41`, p50 `3245.70`, p95 `3352.33`, p99 `3386.39`, 1%low `1032.12`, 0.1%low `687.37`, std `336.09`

**Frame time (ms)**  avg `0.33`, p50 `0.31`, p95 `0.39`, p99 `0.67`, p99.9 `1.15`, max `2.51`

**Client tick (ms)**  avg `0.65`, p95 `0.70`, max `0.98`

**Memory**  start `6464 MB`, end `6198 MB`, peak `7636 MB`, GC `8 events / 469 ms`

**FPS over sampling window (ASCII):**

```
3238.9 |                                   █ █                                          
3210.0 |                █                  █ █ █  █  █     █         █   █              
3181.2 |     █       █  ██             █  ██ █ ██ █ █████ ██     █████   ██   █     █ █ 
3152.3 |██   █ █     █  ██ █    ██    ██  ██ █ ████ █████ ██     █████ ████  ██    ██ █ 
3123.5 |██   ███     █  ██ █    ████ █████████ █████████████ █ █ ███████████████   ██ █ 
3094.7 |██   ███ █   ██ ████ █  ████ █████████ ███████████████ █ ████████████████  ██ █ 
3065.8 |███  ██████  ██ ████ ██ ████ █████████████████████████ ██████████████████ ███ ██
3037.0 |███  ███████████████ █████████████████████████████████ ██████████████████ ███ ██
3008.1 |███  █████████████████████████████████████████████████ █████████████████████████
2979.3 |████ ███████████████████████████████████████████████████████████████████████████
2950.5 |████ ███████████████████████████████████████████████████████████████████████████
2921.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20921
   1 ms |   75
   2 ms |   4
```

**Extras:**

- `fps_harmonic_avg` = `3056.46`
- `fps_1pct_low` = `1032.12`
- `oscillations` = `20.00`
- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`
- `block_state_changes` = `1152.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `0.00`
- `scheduled_block_ticks` = `1152.00`
- `comparators_built` = `64.00`
- `preload_duration_ms` = `21.00`
- `chests_built` = `64.00`
- `entity_count_sample_start` = `1.00`
- `preset_long` = `0.00`
- `seed` = `8053.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `687.37`
- `neighbour_updates` = `0.00`

