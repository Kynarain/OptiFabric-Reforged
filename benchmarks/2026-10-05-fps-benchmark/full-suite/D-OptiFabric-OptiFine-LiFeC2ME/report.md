# FPS Test session — 2026-10-05T17:06:38.6629027+08:00

- Minecraft: `1.21.1`
- OS: `Windows 10 10.0 (amd64)`
- CPU cores: `16`
- Java: `22.0.2` (Java HotSpot(TM) 64-Bit Server VM)
- Max heap: `8192 MB`
- GPU: `AMD Radeon RX 7800 XT / 3.2.0 Core Profile Context 25.12.1.251128`

## Summary

| # | Benchmark | Cat. | Avg FPS | 1% low | 0.1% low | p99 frame ms | Tick avg ms | GC | Heap Δ MB |
|---:|---|---|---:|---:|---:|---:|---:|---:|---:|
| 1 | [Particle cycle (7 types → all together)](#particle-cycle-7-types--all-together) | Particles | 4901.4 | 1315.6 | 790.7 | 0.46 | 0.78 | 4 | 3894 |
| 2 | [Cows ×200 ring](#cows-200-ring) | Entities | 2318.4 | 812.6 | 638.9 | 0.92 | 0.76 | 4 | 368 |
| 3 | [Sheep ×200 ring](#sheep-200-ring) | Entities | 1363.7 | 601.1 | 463.5 | 1.52 | 0.77 | 6 | 2784 |
| 4 | [Zombies ×150 ring (AI on)](#zombies-150-ring-ai-on) | Entities | 1226.8 | 566.5 | 443.3 | 1.57 | 0.73 | 4 | 534 |
| 5 | [Pigs ×250 ring](#pigs-250-ring) | Entities | 2200.2 | 779.5 | 643.9 | 1.02 | 0.76 | 0 | 4436 |
| 6 | [Villagers ×100 ring](#villagers-100-ring) | Entities | 1815.9 | 786.9 | 645.0 | 1.16 | 0.62 | 0 | 4156 |
| 7 | [Chickens ×300 ring](#chickens-300-ring) | Entities | 1747.1 | 684.3 | 543.7 | 1.35 | 0.77 | 0 | 4234 |
| 8 | [Item entities ×500](#item-entities-500) | Entities | 937.9 | 455.2 | 375.3 | 2.04 | 0.94 | 10 | 2610 |
| 9 | [XP orbs ×500 ring](#xp-orbs-500-ring) | Entities | 1821.5 | 427.3 | 380.7 | 2.24 | 1.70 | 4 | 6428 |
| 10 | [Item merge storm (cobblestone, mergeable)](#item-merge-storm-cobblestone-mergeable) | Entities | 673.4 | 237.0 | 204.4 | 3.86 | 1.68 | 10 | 712 |
| 11 | [Zombies obstacle pathfinding (150 + pillar maze)](#zombies-obstacle-pathfinding-150--pillar-maze) | Entities | 1521.2 | 648.0 | 508.8 | 1.39 | 0.73 | 4 | 1968 |
| 12 | [Villager AI village (80, brain on)](#villager-ai-village-80-brain-on) | Entities | 1308.3 | 622.5 | 497.1 | 1.42 | 0.60 | 6 | 3900 |
| 13 | [TNT field (14×14 staggered fuses)](#tnt-field-1414-staggered-fuses) | Physics | 4434.0 | 1332.3 | 952.6 | 0.51 | 0.74 | 4 | 1222 |
| 14 | [TNT field destructive (breaks terrain)](#tnt-field-destructive-breaks-terrain) | Physics | 4026.5 | 1185.4 | 788.4 | 0.59 | 0.74 | 10 | 2072 |
| 15 | [Falling sand wall 40×40 (heavy)](#falling-sand-wall-4040-heavy) | Physics | 2841.5 | 73.1 | 63.5 | 12.08 | 4.56 | 8 | 610 |
| 16 | [Falling gravel mixed heavy (sand+gravel+concrete)](#falling-gravel-mixed-heavy-sandgravelconcrete) | Physics | 2763.8 | 68.3 | 50.9 | 12.30 | 4.74 | 10 | 3580 |
| 17 | [Falling sand wall (lite, staggered)](#falling-sand-wall-lite-staggered) | Physics | 5744.0 | 1400.2 | 779.5 | 0.44 | 1.02 | 1 | 5590 |
| 18 | [Falling gravel mixed (lite, staggered)](#falling-gravel-mixed-lite-staggered) | Physics | 5847.0 | 1478.4 | 820.3 | 0.43 | 1.02 | 10 | 1216 |
| 19 | [Projectile storm (arrows + snowballs)](#projectile-storm-arrows--snowballs) | Physics | 2193.6 | 768.5 | 584.6 | 1.04 | 0.71 | 4 | 4978 |
| 20 | [Redstone clocks (6×6)](#redstone-clocks-66) | Redstone | 5465.4 | 1551.0 | 1127.4 | 0.48 | 0.51 | 10 | 166 |
| 21 | [Redstone dust grid (16 trails ×32 + repeaters + lamps)](#redstone-dust-grid-16-trails-32--repeaters--lamps) | Redstone | 5416.9 | 1270.4 | 434.0 | 0.47 | 0.47 | 4 | 3648 |
| 22 | [Piston/slime array (8×8 toggled every 8t)](#pistonslime-array-88-toggled-every-8t) | Redstone | 5820.3 | 1622.1 | 977.7 | 0.45 | 0.49 | 10 | 4430 |
| 23 | [Static dense forest (orbit canopy, no worldgen)](#static-dense-forest-orbit-canopy-no-worldgen) | Chunks | 5358.3 | 1670.7 | 1090.3 | 0.42 | 0.47 | 4 | 228 |
| 24 | [Plains flyby (single-biome world)](#plains-flyby-single-biome-world) | Chunks | 4503.1 | 761.8 | 350.1 | 0.91 | 0.60 | 14 | 4286 |
| 25 | [Jungle flyby (single-biome world)](#jungle-flyby-single-biome-world) | Chunks | 2536.6 | 567.5 | 231.0 | 1.18 | 0.57 | 16 | 1728 |
| 26 | [Desert flyby (single-biome world)](#desert-flyby-single-biome-world) | Chunks | 4545.9 | 944.1 | 432.4 | 0.77 | 0.59 | 12 | 1306 |
| 27 | [Taiga flyby (single-biome world)](#taiga-flyby-single-biome-world) | Chunks | 4553.6 | 583.6 | 205.3 | 0.91 | 0.57 | 12 | 566 |
| 28 | [Snowy plains flyby](#snowy-plains-flyby) | Chunks | 4809.2 | 745.5 | 269.4 | 0.79 | 0.54 | 14 | 3254 |
| 29 | [Forest flyby](#forest-flyby) | Chunks | 3864.3 | 855.5 | 455.2 | 0.92 | 0.66 | 22 | 1050 |
| 30 | [Savanna flyby](#savanna-flyby) | Chunks | 4684.9 | 895.5 | 467.8 | 0.82 | 0.62 | 12 | 2496 |
| 31 | [Swamp flyby](#swamp-flyby) | Chunks | 4435.1 | 794.2 | 428.6 | 0.92 | 0.69 | 26 | 3266 |
| 32 | [Cherry grove flyby](#cherry-grove-flyby) | Chunks | 4733.1 | 673.8 | 252.7 | 0.90 | 0.69 | 11 | 954 |
| 33 | [Badlands flyby](#badlands-flyby) | Chunks | 4925.0 | 667.1 | 255.2 | 0.82 | 0.54 | 22 | 3464 |
| 34 | [Dark forest flyby (dense canopy)](#dark-forest-flyby-dense-canopy) | Chunks | 4350.2 | 799.2 | 376.4 | 0.87 | 0.56 | 12 | 3380 |
| 35 | [Windswept hills flyby](#windswept-hills-flyby) | Chunks | 4643.1 | 1033.0 | 649.8 | 0.72 | 0.55 | 18 | 1674 |
| 36 | [Idle baseline (orbit, flat world)](#idle-baseline-orbit-flat-world) | Baseline | 5982.3 | 1701.9 | 1058.5 | 0.42 | 0.46 | 4 | 4088 |
| 37 | [Particle diversity stress (16 types simultaneously)](#particle-diversity-stress-16-types-simultaneously) | Stress | 440.3 | 224.1 | 183.1 | 4.14 | 1.53 | 8 | 3806 |
| 38 | [Fluid spread (water basin, 4-step periodic reset)](#fluid-spread-water-basin-4-step-periodic-reset) | Fluids | 5653.6 | 1616.1 | 1050.6 | 0.45 | 0.46 | 10 | 528 |
| 39 | [Lighting update (16×16 glowstone reveal/hide)](#lighting-update-1616-glowstone-revealhide) | Lighting | 5593.8 | 1547.4 | 894.5 | 0.44 | 0.48 | 4 | 2280 |
| 40 | [Hopper grid 20×20 (transfer storm)](#hopper-grid-2020-transfer-storm) | Block-Entities | 4684.6 | 1519.3 | 1165.4 | 0.51 | 0.45 | 6 | 2754 |
| 41 | [Comparator storage (8×8 chests + comparators)](#comparator-storage-88-chests--comparators) | Block-Entities | 3630.7 | 1059.1 | 406.1 | 0.63 | 0.45 | 0 | 5106 |

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

Category: **Particles**  |  Duration: 23109 ms  |  Sample ticks: 400

**FPS**  avg `4901.41`, min `616.37`, p50 `5136.11`, p95 `5773.67`, p99 `5854.80`, 1%low `1315.64`, 0.1%low `790.66`, std `801.07`

**Frame time (ms)**  avg `0.21`, p50 `0.19`, p95 `0.27`, p99 `0.46`, p99.9 `1.05`, max `1.62`

**Client tick (ms)**  avg `0.78`, p95 `0.96`, max `1.78`

**Memory**  start `2850 MB`, end `2734 MB`, peak `6744 MB`, GC `4 events / 737 ms`

**FPS over sampling window (ASCII):**

```
5610.4 |                    █      █ █          █     █ █                               
5443.5 |      ███         █ ███   ██ █ ██  █ █ ███ █ ████                               
5276.7 |██ █ █████  ███ ███ ████  ████ ██  █ █████████████                              
5109.8 |██ █████████████████████ ████████ ████████████████                              
4942.9 |██████████████████████████████████████████████████                              
4776.1 |██████████████████████████████████████████████████                              
4609.2 |██████████████████████████████████████████████████          █  ███              
4442.3 |██████████████████████████████████████████████████      █ ████████              
4275.4 |█████████████████████████████████████████████████████████ ████████            █ 
4108.6 |██████████████████████████████████████████████████████████████████         █ ███
3941.7 |███████████████████████████████████████████████████████████████████   ██████████
3774.8 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20968
   1 ms |   32
```

**Per-particle stage breakdown:**

> _Approximate split: frames are bucketed evenly across stages by index, not aligned to the actual tick boundary. Use as a relative comparison, not an absolute per-stage measurement._

| Stage | Particles spawned | Frames captured | Avg FPS | p99 frame ms |
|---|---:|---:|---:|---:|
| `smoke` | 160 | 2625 | 5268.4 | 0.41 |
| `dragon_breath` | 160 | 2625 | 5239.4 | 0.43 |
| `ALL_TOGETHER` | 1680 | 2625 | 5388.3 | 0.42 |
| `dripping_water` | 240 | 2625 | 5279.0 | 0.42 |
| `portal` | 160 | 2625 | 5414.5 | 0.44 |
| `sculk_charge_pop` | 240 | 2625 | 4340.7 | 0.52 |
| `end_rod` | 240 | 2625 | 4253.8 | 0.49 |
| `flame` | 160 | 2625 | 4027.3 | 0.48 |

**Extras:**

- `preset_full` = `0.00`
- `particles_stage_smoke` = `160.00`
- `entity_count_sample_end` = `1.00`
- `fps_1pct_low` = `1315.64`
- `fps_harmonic_avg` = `4687.69`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `111.00`
- `particles_stage_dragon_breath` = `160.00`
- `preset_long` = `0.00`
- `fps_0p1pct_low` = `790.66`
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
- `particle_stage_count` = `8.00`
- `entity_count_delta` = `0.00`

### Cows ×200 ring (`entity_cows`)

Category: **Entities**  |  Duration: 23107 ms  |  Sample ticks: 400

**FPS**  avg `2318.38`, min `337.15`, p50 `2403.85`, p95 `2488.80`, p99 `2521.43`, 1%low `812.59`, 0.1%low `638.91`, std `245.02`

**Frame time (ms)**  avg `0.44`, p50 `0.42`, p95 `0.53`, p99 `0.92`, p99.9 `1.43`, max `2.97`

**Client tick (ms)**  avg `0.76`, p95 `0.89`, max `2.10`

**Memory**  start `6778 MB`, end `4686 MB`, peak `7146 MB`, GC `4 events / 716 ms`

**FPS over sampling window (ASCII):**

```
2399.0 |                                              █                                 
2382.4 |                 █     █                      █                                 
2365.8 |         █ █   █ █  █  ██                █    █            █  █ █               
2349.3 |        ██ █   █ █  █  ██      ██       ██    █    █       █  ████            ██
2332.7 |        ████  ██ █  █  ██      ██       ██    █  █ █   █ █ █  █████   █  ██   ██
2316.1 | █    █ ████ █████  █  ███ █ █████      ██ █ ███████  ██████  █████  ███████  ██
2299.5 | █  █ █ ██████████ ██  ███ ███████ █    ████████████ ███████ ██████  ███████ ███
2282.9 | █ ██████████████████ ████ ███████ ██ ██████████████ ███████████████████████ ███
2266.3 | ███████████████████████████████████████████████████ ███████████████████████████
2249.7 |████████████████████████████████████████████████████ ███████████████████████████
2233.1 |████████████████████████████████████████████████████ ███████████████████████████
2216.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20802
   1 ms |   197
   2 ms |   1
```

**Extras:**

- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `201.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `2269.25`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `638.91`
- `entities_spawned` = `200.00`
- `entity_count_sample_start` = `201.00`
- `preload_duration_ms` = `39.00`
- `preset_long` = `0.00`
- `seed` = `6121.00`
- `fps_1pct_low` = `812.59`

### Sheep ×200 ring (`entity_sheep`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1363.67`, min `385.62`, p50 `1402.13`, p95 `1472.54`, p99 `1485.88`, 1%low `601.14`, 0.1%low `463.51`, std `140.26`

**Frame time (ms)**  avg `0.75`, p50 `0.71`, p95 `0.91`, p99 `1.52`, p99.9 `1.89`, max `2.59`

**Client tick (ms)**  avg `0.77`, p95 `0.83`, max `1.44`

**Memory**  start `2496 MB`, end `2862 MB`, peak `5280 MB`, GC `6 events / 841 ms`

**FPS over sampling window (ASCII):**

```
1427.3 |                                                      █       █            █    
1411.7 |                                                    █ ██      ██        █  █    
1396.0 |           █   █                                    ████  ███ ██ █ █   ██  █   █
1380.4 |        █████ ██    █ ██                        █  █████  ███ ████ ███ █████ ███
1364.8 | ██ ███ █████████ ███ ███        █        ██ █  ████████  ███ ██████████████████
1349.2 |███ ██████████████████████ ██    █     █  ████ █████████████████████████████████
1333.5 |███ █████████████████████████    █  █  █  ██████████████████████████████████████
1317.9 |███ █████████████████████████    █  ██ █ ███████████████████████████████████████
1302.3 |█████████████████████████████   ███ ████ ███████████████████████████████████████
1286.6 |███████████████████████████████ ████████ ███████████████████████████████████████
1271.0 |████████████████████████████████████████ ███████████████████████████████████████
1255.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20304
   1 ms | █  681
   2 ms |   15
```

**Extras:**

- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `201.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1340.00`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `463.51`
- `entities_spawned` = `200.00`
- `entity_count_sample_start` = `201.00`
- `preload_duration_ms` = `52.00`
- `preset_long` = `0.00`
- `seed` = `6133.00`
- `fps_1pct_low` = `601.14`

### Zombies ×150 ring (AI on) (`entity_zombies`)

Category: **Entities**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `1226.81`, min `185.26`, p50 `1267.91`, p95 `1348.44`, p99 `1367.05`, 1%low `566.46`, 0.1%low `443.26`, std `134.73`

**Frame time (ms)**  avg `0.83`, p50 `0.79`, p95 `1.02`, p99 `1.57`, p99.9 `1.98`, max `5.40`

**Client tick (ms)**  avg `0.73`, p95 `0.79`, max `1.79`

**Memory**  start `7056 MB`, end `4654 MB`, peak `7590 MB`, GC `4 events / 338 ms`

**FPS over sampling window (ASCII):**

```
1308.1 |                                   ██           █    █                          
1280.5 |                     ██       ████████  █   ██ ███████     █                    
1252.8 |  █ █          █    ███████  █████████ ████████████████ ██ ██  ██            █  
1225.2 |  ███████ ██████ ██ █████████████████████████████████████████████           ███ 
1197.6 |██████████████████████████████████████████████████████████████████         ████ 
1169.9 |██████████████████████████████████████████████████████████████████         █████
1142.3 |██████████████████████████████████████████████████████████████████        ██████
1114.7 |██████████████████████████████████████████████████████████████████        ██████
1087.0 |██████████████████████████████████████████████████████████████████      ████████
1059.4 |██████████████████████████████████████████████████████████████████   ███████████
1031.7 |██████████████████████████████████████████████████████████████████ █████████████
1004.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  19772
   1 ms | ██  1210
   2 ms |   17
   5 ms |   1
```

**Extras:**

- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `151.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1204.83`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `443.26`
- `entities_spawned` = `150.00`
- `entity_count_sample_start` = `151.00`
- `preload_duration_ms` = `29.00`
- `preset_long` = `0.00`
- `seed` = `6151.00`
- `fps_1pct_low` = `566.46`

### Pigs ×250 ring (`entity_pigs`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `2200.17`, min `600.20`, p50 `2277.39`, p95 `2356.82`, p99 `2390.06`, 1%low `779.45`, 0.1%low `643.91`, std `233.08`

**Frame time (ms)**  avg `0.46`, p50 `0.44`, p95 `0.56`, p99 `1.02`, p99.9 `1.44`, max `1.67`

**Client tick (ms)**  avg `0.76`, p95 `0.80`, max `1.04`

**Memory**  start `2190 MB`, end `6626 MB`, peak `6626 MB`, GC `0 events / 0 ms`

**FPS over sampling window (ASCII):**

```
2272.9 |                             █                                       █          
2255.7 |█     █      ██           █  █                               █      ██          
2238.5 |███   ██     ██ █ █     █ ██ █    █   █      █               █      ██          
2221.2 |███ ████   ████ █ █   █ █ ██ █    █   █   ██ █ ████          ███ █  ███    █    
2204.0 |████████   ████ █ █   █ █ ██ █   ██ █ █ █ ████ ████   ███   ████ ██ ███ █  █   █
2186.7 |████████  ███████ █   █ ████ █   ██ █ █ ███████████   ████  ████ ████████ ██   █
2169.5 |█████████████████████ ████████   ██ █ █ ███████████   ████  ██████████████████ █
2152.2 |█████████████████████ ████████  █████ █ ████████████ ██████ ████████████████████
2135.0 |█████████████████████ ████████  ███████ ████████████████████████████████████████
2117.7 |█████████████████████ ████████ ████████ ████████████████████████████████████████
2100.5 |██████████████████████████████ █████████████████████████████████████████████████
2083.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20784
   1 ms |   216
```

**Extras:**

- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `251.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `2153.57`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `643.91`
- `entities_spawned` = `250.00`
- `entity_count_sample_start` = `251.00`
- `preload_duration_ms` = `26.00`
- `preset_long` = `0.00`
- `seed` = `6163.00`
- `fps_1pct_low` = `779.45`

### Villagers ×100 ring (`entity_villagers`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1815.95`, min `451.18`, p50 `1864.98`, p95 `1955.03`, p99 `1973.95`, 1%low `786.88`, 0.1%low `644.95`, std `180.89`

**Frame time (ms)**  avg `0.56`, p50 `0.54`, p95 `0.68`, p99 `1.16`, p99.9 `1.43`, max `2.22`

**Client tick (ms)**  avg `0.62`, p95 `0.67`, max `0.74`

**Memory**  start `1236 MB`, end `5392 MB`, peak `5392 MB`, GC `0 events / 0 ms`

**FPS over sampling window (ASCII):**

```
1887.6 |                                                                     █          
1872.9 |                                █  █             █                   █          
1858.3 |               █           █   ███ ██   █       ██ █         █   █  ██   █  █   
1843.7 |         █     ██          █   ███ ██   █ █     ██ █  ██     ███ █ ████  ██ █   
1829.0 |         ██    ██          ██  ███ ███ ██ █     ██ ██ ██     ███ █ ████  ██ ██  
1814.4 |█ █      ██    ██        █ ███████████ ████  █  █████ ██  █ ████ ███████ ███████
1799.8 |████ ████████ ███        █ ███████████ █████ █ ██████ ██ ███████ ███████ ███████
1785.2 |█████████████████ █  █ █████████████████████ ████████ ██ ███████ ███████ ███████
1770.5 |███████████████████  █ █████████████████████ ████████ ██████████████████████████
1755.9 |███████████████████  █ █████████████████████ ███████████████████████████████████
1741.3 |██████████████████████ █████████████████████ ███████████████████████████████████
1726.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20728
   1 ms | █  271
   2 ms |   1
```

**Extras:**

- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `101.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1786.55`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `644.95`
- `entities_spawned` = `100.00`
- `entity_count_sample_start` = `101.00`
- `preload_duration_ms` = `23.00`
- `preset_long` = `0.00`
- `seed` = `6173.00`
- `fps_1pct_low` = `786.88`

### Chickens ×300 ring (`entity_chickens`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1747.08`, min `425.51`, p50 `1803.10`, p95 `1894.66`, p99 `1924.19`, 1%low `684.31`, 0.1%low `543.65`, std `189.20`

**Frame time (ms)**  avg `0.58`, p50 `0.55`, p95 `0.73`, p99 `1.35`, p99.9 `1.65`, max `2.35`

**Client tick (ms)**  avg `0.77`, p95 `0.84`, max `1.07`

**Memory**  start `2878 MB`, end `7112 MB`, peak `7112 MB`, GC `0 events / 0 ms`

**FPS over sampling window (ASCII):**

```
1815.5 |  █                                                  ██                         
1801.7 |  █                                      █  █        ██              █          
1787.9 |  █ █                                    █ ██ ██     ██ █            █          
1774.1 |███████  █                █              █ ██ ██  █ ███ ██        █ ██          
1760.3 |██████████               ██              ████ ██  █████ ██      █ ████ █     █  
1746.5 |██████████     █ ███     ██    █        █████ ██ ██████ █████   █ ███████   ██  
1732.6 |█████████████  █ ███ ███ ███   █ █    █ █████████████████████   ███████████ ██  
1718.8 |█████████████ ██████ ███ ███   █ ████ ████████████████████████ ████████████████ 
1705.0 |████████████████████ ████████ ██ █████████████████████████████ ████████████████ 
1691.2 |██████████████████████████████████████████████████████████████ █████████████████
1677.4 |██████████████████████████████████████████████████████████████ █████████████████
1663.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20679
   1 ms | █  317
   2 ms |   4
```

**Extras:**

- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `301.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1711.50`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `543.65`
- `entities_spawned` = `300.00`
- `entity_count_sample_start` = `301.00`
- `preload_duration_ms` = `49.00`
- `preset_long` = `0.00`
- `seed` = `6197.00`
- `fps_1pct_low` = `684.31`

### Item entities ×500 (`entity_items`)

Category: **Entities**  |  Duration: 23101 ms  |  Sample ticks: 400

**FPS**  avg `937.86`, min `290.48`, p50 `961.26`, p95 `1010.71`, p99 `1027.68`, 1%low `455.17`, 0.1%low `375.30`, std `92.50`

**Frame time (ms)**  avg `1.08`, p50 `1.04`, p95 `1.29`, p99 `2.04`, p99.9 `2.39`, max `3.44`

**Client tick (ms)**  avg `0.94`, p95 `1.01`, max `1.83`

**Memory**  start `4986 MB`, end `2928 MB`, peak `7596 MB`, GC `10 events / 1287 ms`

**FPS over sampling window (ASCII):**

```
977.3 |                                                                         █      
969.8 |                                █                   █                █ █ █      
962.2 |                                █                   █              █ █ ███      
954.7 |                              ██████                █  █    ███    █ █ ███      
947.1 |                  █  █    ██  ██████ ██    █  █ ██ ██  ██   ███ █ ████ ███      
939.6 |          █    █  █  █    ██  ███████████████ █ █████████ ███████ ████ ████     
932.0 |█ █ █     █  █ █  █  ███  ███ █████████████████ ████████████████████████████    
924.5 |███ ██    ███████ ██ ████████ █████████████████ ████████████████████████████    
916.9 |███████  ███████████ ███████████████████████████████████████████████████████  █ 
909.4 |████████████████████████████████████████████████████████████████████████████  ██
901.8 |█████████████████████████████████████████████████████████████████████████████ ██
894.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | █████  2161
   1 ms | ████████████████████████████████████████  16016
   2 ms | █  289
   3 ms |   3
```

**Extras:**

- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `501.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `923.45`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `375.30`
- `entities_spawned` = `500.00`
- `entity_count_sample_start` = `501.00`
- `preload_duration_ms` = `37.00`
- `preset_long` = `0.00`
- `seed` = `6203.00`
- `fps_1pct_low` = `455.17`

### XP orbs ×500 ring (`entity_xp`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1821.48`, min `325.63`, p50 `1876.52`, p95 `1962.71`, p99 `1987.68`, 1%low `427.27`, 0.1%low `380.73`, std `209.19`

**Frame time (ms)**  avg `0.57`, p50 `0.53`, p95 `0.68`, p99 `2.24`, p99.9 `2.51`, max `3.07`

**Client tick (ms)**  avg `1.70`, p95 `1.77`, max `2.29`

**Memory**  start `1272 MB`, end `2276 MB`, peak `7700 MB`, GC `4 events / 393 ms`

**FPS over sampling window (ASCII):**

```
1883.6 |█             █                  █   █                                          
1867.2 |█             ███  █   █         █   █              ██                          
1850.7 |███    █  █ ██████ █   █         ██ ███ █           ██ █       █ ████      ███  
1834.3 |████   █  █ ██████ ███ ██    █   ████████           █████      █ ████ █    ███ █
1817.8 |████   ██ ████████ ██████ █ ██   ████████ ██        ██████    ███████ ██   █████
1801.4 |████   ██████████████████ █ ███ █████████ ████     ███████   ███████████  ██████
1784.9 |████  █████████████████████ ███ ███████████████    ███████  █████████████ ██████
1768.4 |███████████████████████████████████████████████    ███████ █████████████████████
1752.0 |████████████████████████████████████████████████   ███████ █████████████████████
1735.5 |████████████████████████████████████████████████ █████████ █████████████████████
1719.1 |████████████████████████████████████████████████ █████████ █████████████████████
1702.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20721
   1 ms |   38
   2 ms |   240
   3 ms |   1
```

**Extras:**

- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `501.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `1756.60`
- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `380.73`
- `entities_spawned` = `500.00`
- `entity_count_sample_start` = `501.00`
- `preload_duration_ms` = `41.00`
- `preset_long` = `0.00`
- `seed` = `6217.00`
- `fps_1pct_low` = `427.27`

### Item merge storm (cobblestone, mergeable) (`entity_items_merge_storm`)

Category: **Entities**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `673.36`, min `191.25`, p50 `665.29`, p95 `933.79`, p99 `976.86`, 1%low `237.04`, 0.1%low `204.42`, std `162.32`

**Frame time (ms)**  avg `1.59`, p50 `1.50`, p95 `2.23`, p99 `3.86`, p99.9 `4.71`, max `5.23`

**Client tick (ms)**  avg `1.68`, p95 `2.08`, max `2.66`

**Memory**  start `6920 MB`, end `1296 MB`, peak `7632 MB`, GC `10 events / 1267 ms`

**FPS over sampling window (ASCII):**

```
940.1 |  █                                                                             
897.3 |████  ███     ██                                                                
854.5 |██████████    ██                                                                
811.8 |██████████   ███████████                                                        
769.0 |██████████████████████████████                                                  
726.2 |███████████████████████████████ ███                                             
683.4 |█████████████████████████████████████ ██                                        
640.6 |██████████████████████████████████████████ ██████                               
597.8 |███████████████████████████████████████████████████████                         
555.0 |██████████████████████████████████████████████████████████████                  
512.2 |██████████████████████████████████████████████████████████████████       ███  █ 
469.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms |   6
   1 ms | ████████████████████████████████████████  10978
   2 ms | █████  1267
   3 ms | █  205
   4 ms |   90
   5 ms |   3
```

**Extras:**

- `seed` = `6287.00`
- `fps_0p1pct_low` = `204.42`
- `fps_harmonic_avg` = `627.47`
- `items_spawned` = `1560.00`
- `preset_quick` = `1.00`
- `preload_chunks` = `81.00`
- `items_merged_estimate` = `0.00`
- `items_alive_p95` = `1560.00`
- `preset_full` = `0.00`
- `fps_1pct_low` = `237.04`
- `items_alive_max` = `1560.00`
- `items_alive_p50` = `1240.00`
- `items_alive_avg` = `1230.00`
- `waves_spawned` = `12.00`
- `entity_count_sample_start` = `681.00`
- `entity_count_delta` = `880.00`
- `preset_long` = `0.00`
- `entity_count_sample_end` = `1561.00`
- `preload_duration_ms` = `41.00`

### Zombies obstacle pathfinding (150 + pillar maze) (`entity_zombies_obstacle_pathfinding`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1521.25`, min `309.40`, p50 `1571.09`, p95 `1647.99`, p99 `1665.56`, 1%low `647.95`, 0.1%low `508.82`, std `167.14`

**Frame time (ms)**  avg `0.67`, p50 `0.64`, p95 `0.85`, p99 `1.39`, p99.9 `1.75`, max `3.23`

**Client tick (ms)**  avg `0.73`, p95 `0.81`, max `1.69`

**Memory**  start `5694 MB`, end `4314 MB`, peak `7662 MB`, GC `4 events / 301 ms`

**FPS over sampling window (ASCII):**

```
1575.0 |                         █                       ██     █    █         ██       
1557.9 |█                        ██  █                █ ███  █  ██████   █    ████      
1540.8 |█              █    █   ███ ████        █     █ ████ █ ████████████   █████     
1523.7 |█       █      █ ██ ███ ████████ █ █    █     ██████████████████████ ██████     
1506.6 |██ ██   ██    ██ █████████████████ █ ████    ███████████████████████ ██████    █
1489.4 |██ ██   ███   ██ ██████████████████████████ ███████████████████████████████ ██ █
1472.3 |█████ █ ███████████████████████████████████████████████████████████████████ ██ █
1455.2 |█████ ██████████████████████████████████████████████████████████████████████████
1438.1 |█████ ██████████████████████████████████████████████████████████████████████████
1421.0 |█████ ██████████████████████████████████████████████████████████████████████████
1403.8 |█████ ██████████████████████████████████████████████████████████████████████████
1386.7 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20425
   1 ms | █  570
   2 ms |   4
   3 ms |   1
```

**Extras:**

- `zombies_spawned` = `150.00`
- `block_state_changes` = `0.00`
- `preset_long` = `0.00`
- `preload_duration_ms` = `49.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `508.82`
- `fps_1pct_low` = `647.95`
- `entity_count_sample_start` = `151.00`
- `fps_harmonic_avg` = `1491.20`
- `entity_count_sample_end` = `151.00`
- `seed` = `6271.00`
- `preset_full` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_delta` = `0.00`
- `neighbour_updates` = `0.00`
- `pillars_built` = `48.00`

### Villager AI village (80, brain on) (`villager_ai_village`)

Category: **Entities**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `1308.26`, min `335.42`, p50 `1340.48`, p95 `1448.02`, p99 `1466.71`, 1%low `622.52`, 0.1%low `497.06`, std `136.52`

**Frame time (ms)**  avg `0.78`, p50 `0.75`, p95 `0.93`, p99 `1.42`, p99.9 `1.78`, max `2.98`

**Client tick (ms)**  avg `0.60`, p95 `0.72`, max `1.02`

**Memory**  start `2106 MB`, end `1600 MB`, peak `6006 MB`, GC `6 events / 942 ms`

**FPS over sampling window (ASCII):**

```
1420.2 |                                                                         █ █    
1393.2 |                                                 █            █         ████████
1366.1 |                                    ██ █   █ ██  ████ ██████ ███        ████████
1339.0 |                                 ███████ █████████████████████████   ███████████
1311.9 |                        ██  ██ ████████████████████████████████████ ████████████
1284.8 |                    █ █ ████████████████████████████████████████████████████████
1257.7 |              █    █████████████████████████████████████████████████████████████
1230.6 |       ██     █ ████████████████████████████████████████████████████████████████
1203.5 |     ███████ ███████████████████████████████████████████████████████████████████
1176.4 |████████████ ███████████████████████████████████████████████████████████████████
1149.3 |████████████ ███████████████████████████████████████████████████████████████████
1122.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20298
   1 ms | █  696
   2 ms |   6
```

**Extras:**

- `seed` = `6299.00`
- `fps_0p1pct_low` = `497.06`
- `villagers_spawned` = `80.00`
- `fps_harmonic_avg` = `1287.86`
- `neighbour_updates` = `0.00`
- `beds_placed` = `40.00`
- `workstations_placed` = `40.00`
- `preset_quick` = `1.00`
- `preload_chunks` = `81.00`
- `preset_full` = `0.00`
- `scheduled_block_ticks` = `0.00`
- `fps_1pct_low` = `622.52`
- `block_state_changes` = `74.00`
- `doors_placed` = `16.00`
- `entity_count_sample_start` = `81.00`
- `entity_count_delta` = `0.00`
- `preset_long` = `0.00`
- `entity_count_sample_end` = `81.00`
- `preload_duration_ms` = `0.00`

### TNT field (14×14 staggered fuses) (`tnt_field`)

Category: **Physics**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `4433.97`, min `675.36`, p50 `4432.62`, p95 `5243.84`, p99 `5405.41`, 1%low `1332.32`, 0.1%low `952.61`, std `604.74`

**Frame time (ms)**  avg `0.23`, p50 `0.23`, p95 `0.28`, p99 `0.51`, p99.9 `0.96`, max `1.48`

**Client tick (ms)**  avg `0.74`, p95 `1.30`, max `1.46`

**Memory**  start `6504 MB`, end `6586 MB`, peak `7726 MB`, GC `4 events / 363 ms`

**FPS over sampling window (ASCII):**

```
5252.4 |                                                                               █
5123.3 |                                                                         █   ███
4994.2 |                                                         █           ██ ████████
4865.1 |                                                         ██  █  █    ██ ████████
4736.0 |                                                   ████ ███  █ ███   ██ ████████
4607.0 |                                                   ████████ ██████ █████████████
4477.9 |               █                          ███ █   █████████ ████████████████████
4348.8 |          ████████                 █  █ █████ █   ██████████████████████████████
4219.7 |     █ ████████████ █      ██ ██   █████████████████████████████████████████████
4090.6 |██ █ █ ████████████ █    █ █████████████████████████████████████████████████████
3961.5 |██████ ███████████████ █████████████████████████████████████████████████████████
3832.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20989
   1 ms |   11
```

**Extras:**

- `tnt_active_avg` = `36.13`
- `seed` = `3539.00`
- `entity_count_sample_end` = `1.00`
- `tnt_spawned` = `430.00`
- `tnt_active_max` = `205.00`
- `section_rebuilds` = `0.00`
- `entity_count_sample_start` = `188.00`
- `preload_chunks` = `81.00`
- `tnt_active_p50` = `25.00`
- `fps_harmonic_avg` = `4290.99`
- `tnt_active_p95` = `150.00`
- `preset_full` = `0.00`
- `fps_0p1pct_low` = `952.61`
- `preload_duration_ms` = `1.00`
- `explosions_count` = `403.00`
- `entity_count_delta` = `-187.00`
- `waves_spawned` = `13.00`
- `block_state_changes` = `0.00`
- `neighbour_updates` = `0.00`
- `preset_quick` = `1.00`
- `preset_long` = `0.00`
- `fps_1pct_low` = `1332.32`

### TNT field destructive (breaks terrain) (`tnt_field_destructive`)

Category: **Physics**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `4026.48`, min `443.32`, p50 `4101.72`, p95 `4694.84`, p99 `4899.56`, 1%low `1185.40`, 0.1%low `788.35`, std `555.71`

**Frame time (ms)**  avg `0.26`, p50 `0.24`, p95 `0.33`, p99 `0.59`, p99.9 `1.01`, max `2.26`

**Client tick (ms)**  avg `0.74`, p95 `1.29`, max `2.00`

**Memory**  start `5376 MB`, end `1736 MB`, peak `7448 MB`, GC `10 events / 1271 ms`

**FPS over sampling window (ASCII):**

```
4655.6 |                                                                               █
4561.6 |                                                                     █   █     █
4467.6 |                                                                  █  ██  █   ███
4373.6 |                                                              ██  ██ ██ ██   ███
4279.6 |█                                                        █ █  ██  ██ █████   ███
4185.6 |█████                                                    ████ ██ ███████████████
4091.6 |██████             █ █    ██                   █        █████ ██ ███████████████
3997.5 |██████    █   ██   ███  ████               ██  ██    ██ ████████████████████████
3903.5 |████████████  ███ ████ █████         █     ███ ██  █████████████████████████████
3809.5 |█████████████████████████████    ██  █   ████████  █████████████████████████████
3715.5 |██████████████████████████████   ██  ██  ████████ ██████████████████████████████
3621.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20975
   1 ms |   24
   2 ms |   1
```

**Extras:**

- `tnt_active_avg` = `36.54`
- `seed` = `3541.00`
- `entity_count_sample_end` = `78.00`
- `tnt_spawned` = `430.00`
- `tnt_active_max` = `206.00`
- `section_rebuilds` = `0.00`
- `entity_count_sample_start` = `202.00`
- `preload_chunks` = `81.00`
- `tnt_active_p50` = `26.00`
- `fps_harmonic_avg` = `3886.26`
- `tnt_active_p95` = `149.00`
- `preset_full` = `0.00`
- `fps_0p1pct_low` = `788.35`
- `preload_duration_ms` = `0.00`
- `explosions_count` = `404.00`
- `entity_count_delta` = `-124.00`
- `waves_spawned` = `13.00`
- `block_state_changes` = `7278.00`
- `neighbour_updates` = `0.00`
- `preset_quick` = `1.00`
- `preset_long` = `0.00`
- `fps_1pct_low` = `1185.40`

### Falling sand wall 40×40 (heavy) (`falling_sand`)

Category: **Physics**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `2841.45`, min `49.82`, p50 `3291.64`, p95 `4508.57`, p99 `6045.95`, 1%low `73.05`, 0.1%low `63.48`, std `1428.18`

**Frame time (ms)**  avg `1.28`, p50 `0.30`, p95 `7.76`, p99 `12.08`, p99.9 `14.86`, max `20.07`

**Client tick (ms)**  avg `4.56`, p95 `6.24`, max `7.04`

**Memory**  start `6950 MB`, end `4632 MB`, peak `7560 MB`, GC `8 events / 584 ms`

**FPS over sampling window (ASCII):**

```
5833.3 |                                                                               █
5328.6 |                                                                             ███
4823.9 |                                                                             ███
4319.2 |                                                                     █████  ████
3814.5 |██████                                                       ████  ███████  ████
3309.8 |███████████            █   ██  ███    █       █████  ████  ██████  ████████ ████
2805.0 |████████████    █  █  ███  ██  ███  ███  ███  █████  ████  ██████  ████████ ████
2300.3 |████████████   ██  ██ ███ ████ ███  ███  ████ █████ █████  ███████ ████████ ████
1795.6 |████████████ █ ██  ██ ███ ████ ████ ███  ████ █████ ██████ ███████ ████████ ████
1290.9 |████████████ █ ███ ██ ███ ████ ████ █████████ █████ ██████████████ ████████ ████
786.2 |████████████ ████████ ████████ ██████████████ █████████████████████████████ ████
281.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  12862
   1 ms | █  433
   2 ms | █  348
   3 ms | █  227
   4 ms | █  168
   5 ms | █  186
   6 ms | █  216
   7 ms | ██  584
   8 ms | █  294
   9 ms |   58
  10 ms |   34
  11 ms |   41
  12 ms |   48
  13 ms |   61
  14 ms |   38
  15 ms |   10
  16 ms |   2
  20 ms |   1
```

**Extras:**

- `variant` = `heavy`
- `waves_spawned` = `12.00`
- `falling_blocks_alive_p95` = `6400.00`
- `preset_full` = `0.00`
- `entity_count_sample_end` = `113.00`
- `block_state_changes` = `36466.00`
- `falling_blocks_alive_avg` = `4803.02`
- `fps_1pct_low` = `73.05`
- `fps_harmonic_avg` = `780.55`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `2.00`
- `falling_blocks_alive_max` = `6400.00`
- `preset_long` = `0.00`
- `falling_blocks_landed` = `20800.00`
- `fps_0p1pct_low` = `63.48`
- `topup_blocks_per_wave` = `1600.00`
- `seed` = `5077.00`
- `sand_spawned` = `20800.00`
- `entity_count_sample_start` = `3201.00`
- `neighbour_updates` = `0.00`
- `falling_blocks_alive_p50` = `4800.00`
- `preload_chunks` = `81.00`
- `wave_interval_ticks` = `30.00`
- `entity_count_delta` = `-3088.00`

### Falling gravel mixed heavy (sand+gravel+concrete) (`falling_gravel_mixed`)

Category: **Physics**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `2763.78`, min `41.83`, p50 `3227.89`, p95 `4321.52`, p99 `5552.47`, 1%low `68.31`, 0.1%low `50.92`, std `1345.67`

**Frame time (ms)**  avg `1.27`, p50 `0.31`, p95 `7.56`, p99 `12.30`, p99.9 `17.60`, max `23.91`

**Client tick (ms)**  avg `4.74`, p95 `6.38`, max `9.84`

**Memory**  start `3596 MB`, end `3534 MB`, peak `7176 MB`, GC `10 events / 1147 ms`

**FPS over sampling window (ASCII):**

```
5381.5 |                                                                               █
4921.1 |                                                                              ██
4460.8 |                                                                              ██
4000.4 | █ █                                                        █        ███████  ██
3540.1 |██████████                                                  ██ ███   ███████  ██
3079.7 |████████████    ██  ██  ██   ██  ███  ███  ███  ████  ████  ███████ ████████  ██
2619.4 |████████████    ██  ██  ███ ███  ███  ███  ███  █████ ████  ███████ ████████ ███
2159.0 |████████████  █ ██  ██  ███ ███  ███  ███  ███ ██████ ████  ███████ ████████ ███
1698.7 |████████████  █ ██ ████ ███ ████ ████ ███  ███ ██████ █████████████ ████████ ███
1238.3 |████████████  █ ██ ████ ███ █████████████████████████ █████████████ ████████ ███
778.0 |███████████████ ███████████ █████████████████████████ ██████████████████████████
317.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  13049
   1 ms | █  479
   2 ms | █  314
   3 ms | █  225
   4 ms |   152
   5 ms | █  216
   6 ms | █  222
   7 ms | ██  614
   8 ms | █  215
   9 ms |   62
  10 ms |   23
  11 ms |   38
  12 ms |   44
  13 ms |   46
  14 ms |   35
  15 ms |   23
  16 ms |   7
  17 ms |   2
  18 ms |   8
  19 ms |   1
  20 ms |   2
  21 ms |   2
  22 ms |   1
  23 ms |   1
```

**Extras:**

- `variant` = `heavy`
- `waves_spawned` = `12.00`
- `falling_blocks_alive_p95` = `6400.00`
- `preset_full` = `0.00`
- `entity_count_sample_end` = `233.00`
- `block_state_changes` = `36466.00`
- `falling_blocks_alive_avg` = `4832.56`
- `fps_1pct_low` = `68.31`
- `fps_harmonic_avg` = `789.05`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `30.00`
- `falling_blocks_alive_max` = `6400.00`
- `preset_long` = `0.00`
- `falling_blocks_landed` = `20800.00`
- `fps_0p1pct_low` = `50.92`
- `topup_blocks_per_wave` = `1600.00`
- `seed` = `5081.00`
- `sand_spawned` = `20800.00`
- `entity_count_sample_start` = `3201.00`
- `neighbour_updates` = `0.00`
- `falling_blocks_alive_p50` = `4800.00`
- `preload_chunks` = `81.00`
- `wave_interval_ticks` = `30.00`
- `entity_count_delta` = `-2968.00`

### Falling sand wall (lite, staggered) (`falling_sand_lite`)

Category: **Physics**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `5744.01`, min `667.96`, p50 `5948.84`, p95 `6345.18`, p99 `6435.01`, 1%low `1400.20`, 0.1%low `779.52`, std `723.38`

**Frame time (ms)**  avg `0.18`, p50 `0.17`, p95 `0.22`, p99 `0.44`, p99.9 `1.15`, max `1.50`

**Client tick (ms)**  avg `1.02`, p95 `1.19`, max `1.25`

**Memory**  start `2188 MB`, end `7778 MB`, peak `7778 MB`, GC `1 events / 0 ms`

**FPS over sampling window (ASCII):**

```
6206.6 |                                                         █     █                
6109.2 |                                                         ████  █   █            
6011.9 |                                               █  █ ███  █████ █   █  ███   █   
5914.5 |                            █    █    █       ██  █████  ████████  █ ████ █ █   
5817.1 |                   █    ███ █ ██ ███ ██  ██  ███  ████████████████ █ ████ █ ██  
5719.7 |                █  █    █████ ██ ███████ ███ ████ ████████████████ ██████ ████  
5622.4 | █         █   ███ ██   █████ ██ ███████ ████████ ███████████████████████ █████ 
5525.0 | █ █ █ █ █ ███ ███ ██ ██████████████████████████████████████████████████████████
5427.6 |██ █ █ █ █████ █████████████████████████████████████████████████████████████████
5330.2 |██ █ █████████ █████████████████████████████████████████████████████████████████
5232.8 |██ █ █████████ █████████████████████████████████████████████████████████████████
5135.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20964
   1 ms |   36
```

**Extras:**

- `variant` = `lite`
- `waves_spawned` = `63.00`
- `falling_blocks_alive_p95` = `833.00`
- `preset_full` = `0.00`
- `entity_count_sample_end` = `1.00`
- `block_state_changes` = `5684.00`
- `falling_blocks_alive_avg` = `619.12`
- `fps_1pct_low` = `1400.20`
- `fps_harmonic_avg` = `5528.74`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `36.00`
- `falling_blocks_alive_max` = `882.00`
- `preset_long` = `0.00`
- `falling_blocks_landed` = `3087.00`
- `fps_0p1pct_low` = `779.52`
- `topup_blocks_per_wave` = `49.00`
- `seed` = `5101.00`
- `sand_spawned` = `3087.00`
- `entity_count_sample_start` = `442.00`
- `neighbour_updates` = `0.00`
- `falling_blocks_alive_p50` = `686.00`
- `preload_chunks` = `81.00`
- `wave_interval_ticks` = `6.00`
- `entity_count_delta` = `-441.00`

### Falling gravel mixed (lite, staggered) (`falling_gravel_mixed_lite`)

Category: **Physics**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `5846.96`, min `668.72`, p50 `6038.65`, p95 `6439.15`, p99 `6514.66`, 1%low `1478.40`, 0.1%low `820.27`, std `730.14`

**Frame time (ms)**  avg `0.18`, p50 `0.17`, p95 `0.22`, p99 `0.43`, p99.9 `1.10`, max `1.50`

**Client tick (ms)**  avg `1.02`, p95 `1.21`, max `2.66`

**Memory**  start `6230 MB`, end `4450 MB`, peak `7446 MB`, GC `10 events / 1251 ms`

**FPS over sampling window (ASCII):**

```
6263.0 |                                                                  ██            
6173.4 |                                                 █ █          █ ████    █ █ ███ 
6083.7 |                                            █    █ █  ██ █ █ ██ █████   █ ██████
5994.1 |                            █          █ ██ █ █ ████████ █ █ ████████████ ██████
5904.4 |                           ██ █  ██  █ ██████ █ ████████ █ █ ███████████████████
5814.7 |                      █ █ ███ ████████████████████████████ █ ███████████████████
5725.1 |               █ █  █ ███ ██████████████████████████████████████████████████████
5635.4 |             █ █ ████████ ██████████████████████████████████████████████████████
5545.8 |█    █ █  █  █ ██████████ ██████████████████████████████████████████████████████
5456.1 |█  █ ████ ██ █ █████████████████████████████████████████████████████████████████
5366.4 |█ ██████████████████████████████████████████████████████████████████████████████
5276.8 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20966
   1 ms |   34
```

**Extras:**

- `variant` = `lite`
- `waves_spawned` = `63.00`
- `falling_blocks_alive_p95` = `833.00`
- `preset_full` = `0.00`
- `entity_count_sample_end` = `1.00`
- `block_state_changes` = `5684.00`
- `falling_blocks_alive_avg` = `619.12`
- `fps_1pct_low` = `1478.40`
- `fps_harmonic_avg` = `5635.21`
- `preset_quick` = `1.00`
- `preload_duration_ms` = `29.00`
- `falling_blocks_alive_max` = `882.00`
- `preset_long` = `0.00`
- `falling_blocks_landed` = `3087.00`
- `fps_0p1pct_low` = `820.27`
- `topup_blocks_per_wave` = `49.00`
- `seed` = `5113.00`
- `sand_spawned` = `3087.00`
- `entity_count_sample_start` = `442.00`
- `neighbour_updates` = `0.00`
- `falling_blocks_alive_p50` = `686.00`
- `preload_chunks` = `81.00`
- `wave_interval_ticks` = `6.00`
- `entity_count_delta` = `-441.00`

### Projectile storm (arrows + snowballs) (`projectile_storm`)

Category: **Physics**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `2193.57`, min `377.86`, p50 `2178.17`, p95 `2614.38`, p99 `2648.31`, 1%low `768.46`, 0.1%low `584.56`, std `286.45`

**Frame time (ms)**  avg `0.47`, p50 `0.46`, p95 `0.56`, p99 `1.04`, p99.9 `1.56`, max `2.65`

**Client tick (ms)**  avg `0.71`, p95 `0.77`, max `1.42`

**Memory**  start `2802 MB`, end `2828 MB`, peak `7780 MB`, GC `4 events / 401 ms`

**FPS over sampling window (ASCII):**

```
2568.6 |                                                                         █  █  █
2510.8 |                                                                   █ █████ █████
2453.0 |                                                            █   ████ ███████████
2395.1 |                                                           █████████████████████
2337.3 |                                                           █████████████████████
2279.4 |                                                         ███████████████████████
2221.6 |                                       █              ██████████████████████████
2163.8 |      ███ █             ██            ███    █████   ███████████████████████████
2105.9 |      █████ ██        ████      █    ████ ████████   ███████████████████████████
2048.1 |███  ██████████     ██████████████   ███████████████████████████████████████████
1990.2 |██████████████████  ████████████████████████████████████████████████████████████
1932.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20781
   1 ms |   218
   2 ms |   1
```

**Extras:**

- `entity_count_sample_end` = `251.00`
- `preset_quick` = `1.00`
- `block_state_changes` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `173.00`
- `projectiles_spawned` = `1000.00`
- `waves_spawned` = `40.00`
- `preload_duration_ms` = `46.00`
- `entity_count_sample_start` = `78.00`
- `projectiles_swept` = `270.00`
- `max_in_flight_observed` = `250.00`
- `preset_long` = `0.00`
- `seed` = `5099.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `584.56`
- `neighbour_updates` = `0.00`
- `fps_harmonic_avg` = `2136.69`
- `fps_1pct_low` = `768.46`

### Redstone clocks (6×6) (`redstone_clocks`)

Category: **Redstone**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `5465.45`, min `826.10`, p50 `5750.43`, p95 `5959.48`, p99 `6027.73`, 1%low `1550.97`, 0.1%low `1127.41`, std `705.50`

**Frame time (ms)**  avg `0.19`, p50 `0.17`, p95 `0.23`, p99 `0.48`, p99.9 `0.79`, max `1.21`

**Client tick (ms)**  avg `0.51`, p95 `0.54`, max `0.98`

**Memory**  start `7528 MB`, end `5394 MB`, peak `7694 MB`, GC `10 events / 1270 ms`

**FPS over sampling window (ASCII):**

```
5820.1 |             █                                                                  
5740.5 |             █                   █              █     █                    █    
5661.0 |     █ ██    █                   █   █  █       █  █ ██ █   █       ██     █    
5581.4 |     █ ██ ██ █ ██     █  ██    █ █   █  █       █  █ ████  ██   ██  █████ ██ █ █
5501.9 |    █████ ███████     █  ██ █  ███   ██ █       █  █ ████ █████ ██████████████ █
5422.3 |  █ █████ ███████   █ █████ █  ███  ███ █ ██ █  █  █ ████ ████████████████████ █
5342.8 |  █ █████ ███████   ███████ ██ ████ ███ █ ██ ██ ██ █ ███████████████████████████
5263.2 |  █ █████ █████████████████ ██ ████ ███ █ █████ ████ ███████████████████████████
5183.7 | ██ █████ █████████████████████████████ █ ██████████ ███████████████████████████
5104.1 | ██ █████ ██████████████████████████████████████████████████████████████████████
5024.6 | ███████████████████████████████████████████████████████████████████████████████
4945.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20995
   1 ms |   5
```

**Extras:**

- `preload_chunks` = `81.00`
- `preset_long` = `0.00`
- `fps_0p1pct_low` = `1127.41`
- `preset_quick` = `1.00`
- `clocks_built` = `36.00`
- `observers_placed` = `72.00`
- `preset_full` = `0.00`
- `fps_harmonic_avg` = `5277.14`
- `fps_1pct_low` = `1550.97`
- `preload_duration_ms` = `30.00`
- `scheduled_block_ticks` = `9612.00`
- `entity_count_delta` = `0.00`
- `neighbour_updates` = `0.00`
- `block_state_changes` = `9612.00`
- `seed` = `4001.00`
- `entity_count_sample_start` = `1.00`
- `entity_count_sample_end` = `1.00`

### Redstone dust grid (16 trails ×32 + repeaters + lamps) (`redstone_dust_grid`)

Category: **Redstone**  |  Duration: 23108 ms  |  Sample ticks: 400

**FPS**  avg `5416.94`, min `157.82`, p50 `5668.93`, p95 `5841.12`, p99 `5892.75`, 1%low `1270.43`, 0.1%low `434.03`, std `691.52`

**Frame time (ms)**  avg `0.19`, p50 `0.18`, p95 `0.24`, p99 `0.47`, p99.9 `0.96`, max `6.34`

**Client tick (ms)**  avg `0.47`, p95 `0.51`, max `0.66`

**Memory**  start `3916 MB`, end `4844 MB`, peak `7564 MB`, GC `4 events / 250 ms`

**FPS over sampling window (ASCII):**

```
5646.5 |                  █                   █                     █  █   █            
5592.0 |             █    █    █              █               ██  ███  █   █         ██ 
5537.4 |   █ █   █   ██   █  █ █     █        ██         █    ██ ████  █   █     █  ███ 
5482.8 |   ███   █ █ ██   █ ██ █     █      █ ███        █    ██ ████  █   █   █ ██ ███ 
5428.3 |   ███████ ████   █ ██ █████ █ █ ██ █ ███ █  █   █  ████ ████  █   █ █ █ ██ ███ 
5373.7 |█ █████████████   █ ██ █████ █ █ ██ ███████████  █ ██████████  █ ███ █ █ ██ ███ 
5319.1 |█ █████████████ █ ████ █████ █ ████████████████  ████████████  █ ███ █ ████████ 
5264.6 |███████████████ █ ██████████ █ ████████████████  █████████████ █ █████ █████████
5210.0 |███████████████ ██████████████ ████████████████  █████████████ █████████████████
5155.4 |██████████████████████████████ ████████████████ ████████████████████████████████
5100.9 |██████████████████████████████ █████████████████████████████████████████████████
5046.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20980
   1 ms |   16
   6 ms |   4
```

**Extras:**

- `dust_placed` = `464.00`
- `preset_full` = `0.00`
- `preset_long` = `0.00`
- `entity_count_sample_end` = `1.00`
- `entity_count_sample_start` = `1.00`
- `block_state_changes` = `70080.00`
- `fps_1pct_low` = `1270.43`
- `neighbour_updates` = `0.00`
- `pulses_issued` = `46.00`
- `fps_harmonic_avg` = `5195.59`
- `preload_duration_ms` = `0.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `434.03`
- `scheduled_block_ticks` = `2240.00`
- `preset_quick` = `1.00`
- `entity_count_delta` = `0.00`
- `lamps_placed` = `128.00`
- `repeaters_placed` = `48.00`
- `seed` = `4019.00`
- `trails_built` = `16.00`

### Piston/slime array (8×8 toggled every 8t) (`piston_slime_array`)

Category: **Redstone**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `5820.25`, min `594.71`, p50 `6053.27`, p95 `6234.41`, p99 `6293.27`, 1%low `1622.05`, 0.1%low `977.70`, std `711.77`

**Frame time (ms)**  avg `0.18`, p50 `0.17`, p95 `0.22`, p99 `0.45`, p99.9 `0.75`, max `1.68`

**Client tick (ms)**  avg `0.49`, p95 `0.56`, max `0.86`

**Memory**  start `3100 MB`, end `2776 MB`, peak `7530 MB`, GC `10 events / 1196 ms`

**FPS over sampling window (ASCII):**

```
6051.4 |                                              █                                 
5995.5 |      █                    █             █    █ █                █              
5939.7 | █   ███    █     █ █      █   █   █    ██    █ █ █    █       █ ██     █     █ 
5883.8 | █  ████    ███   █ █  ███ █  ██   █  ████  ███ ███    ████   ██ ████   █ █   █ 
5828.0 | ██ ████ █  ███████ █  ███ ██ ███  █  █████ ███ ███ ███████   ███████   █ █   █ 
5772.1 |███ ██████  ███████ ██ ███ ██ ███ ██  █████████████ ████████ ████████  ██ █ █ █ 
5716.3 |███ ██████ ████████ ██ ██████████████ ███████████████████████████████  ██ █ █ █ 
5660.4 |██████████ ███████████ ██████████████████████████████████████████████  ██ █ █ ██
5604.6 |██████████ ██████████████████████████████████████████████████████████  ██ █ ████
5548.8 |█████████████████████████████████████████████████████████████████████  ██ █ ████
5492.9 |█████████████████████████████████████████████████████████████████████ █████ ████
5437.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20990
   1 ms |   10
```

**Extras:**

- `pistons_built` = `64.00`
- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`
- `slime_blocks` = `192.00`
- `block_state_changes` = `33600.00`
- `preset_full` = `0.00`
- `power_toggles` = `57.00`
- `entity_count_delta` = `0.00`
- `preload_duration_ms` = `22.00`
- `scheduled_block_ticks` = `0.00`
- `entity_count_sample_start` = `1.00`
- `preset_long` = `0.00`
- `seed` = `4027.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `977.70`
- `neighbour_updates` = `0.00`
- `fps_harmonic_avg` = `5625.38`
- `fps_1pct_low` = `1622.05`

### Static dense forest (orbit canopy, no worldgen) (`static_dense_forest`)

Category: **Chunks**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `5358.28`, min `994.04`, p50 `5543.24`, p95 `6176.65`, p99 `6242.20`, 1%low `1670.66`, 0.1%low `1090.27`, std `812.09`

**Frame time (ms)**  avg `0.19`, p50 `0.18`, p95 `0.26`, p99 `0.42`, p99.9 `0.87`, max `1.01`

**Client tick (ms)**  avg `0.47`, p95 `0.51`, max `0.76`

**Memory**  start `7488 MB`, end `7626 MB`, peak `7716 MB`, GC `4 events / 291 ms`

**FPS over sampling window (ASCII):**

```
5610.9 |   █        █                                                                   
5564.3 |   █        ██                                                                  
5517.6 |   ██       ██   █   █                                                          
5471.0 |   ██  ██   ██   █   █     █          █    █                                    
5424.3 |  ███ ████ ███  ██   █ █  ███ █       ██ █ █ ██    █            ██ █            
5377.6 |  ████████ ███ ████  █ █  ███ █ █ █  █████ █ ███   █  █       █ ████            
5331.0 |█ ████████ ███████████ █  ███ ███ █  █████ ██████  █  █ █     █ ████  ███ █     
5284.3 |█ ████████████████████ ██ ███ █████ █████████████ ████████ ██████████ ██████ █  
5237.7 |██████████████████████ ██████ ███████████████████████████████████████ ██████████
5191.0 |██████████████████████ █████████████████████████████████████████████████████████
5144.3 |██████████████████████ █████████████████████████████████████████████████████████
5097.7 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20999
   1 ms |   1
```

**Extras:**

- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `1090.27`
- `fps_1pct_low` = `1670.66`
- `entity_count_sample_start` = `2.00`
- `leaf_blocks` = `7642.00`
- `preload_duration_ms` = `53.00`
- `seed` = `7039.00`
- `log_blocks` = `320.00`
- `entity_count_delta` = `0.00`
- `fps_harmonic_avg` = `5161.35`
- `preset_full` = `0.00`
- `preset_long` = `0.00`
- `entity_count_sample_end` = `2.00`
- `preset_quick` = `1.00`
- `trees_built` = `64.00`

### Plains flyby (single-biome world) (`chunk_plains`)

Category: **Chunks**  |  Duration: 31902 ms  |  Sample ticks: 400

**FPS**  avg `4503.11`, min `147.04`, p50 `4812.32`, p95 `5221.93`, p99 `5307.86`, 1%low `761.77`, 0.1%low `350.05`, std `866.65`

**Frame time (ms)**  avg `0.24`, p50 `0.21`, p95 `0.41`, p99 `0.91`, p99.9 `1.83`, max `6.80`

**Client tick (ms)**  avg `0.60`, p95 `0.72`, max `2.45`

**Memory**  start `2864 MB`, end `3542 MB`, peak `7150 MB`, GC `14 events / 1684 ms`

**FPS over sampling window (ASCII):**

```
4947.2 |                                       █                   █ █       █ ██ █     
4795.8 |       ████         ██                 ██ █      ████  █   █████  █ ███████ █ ██
4644.5 |██     █████       ███                 ████ █ ███████  █ ███████  █████████ ████
4493.2 |██  ████████       ████  █   ████      ████ ██████████ █████████  █████████ ████
4341.9 |██  ████████  ██ ██████  █ ██████     █████ ██████████ █████████  █████████ ████
4190.5 |███ █████████ █████████ █████████ █████████ ██████████ █████████  █████████ ████
4039.2 |███ █████████ █████████ █████████ █████████ ██████████ ██████████ █████████ ████
3887.9 |███ ███████████████████ ███████████████████ ██████████ ████████████████████ ████
3736.6 |███████████████████████████████████████████ ███████████████████████████████ ████
3585.3 |███████████████████████████████████████████ ███████████████████████████████ ████
3433.9 |███████████████████████████████████████████████████████████████████████████ ████
3282.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20860
   1 ms |   122
   2 ms |   12
   3 ms |   4
   5 ms |   1
   6 ms |   1
```

**Extras:**

- `scan_fallback` = `false`
- `biome` = `minecraft:plains`
- `stamped_fallback` = `false`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `29.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-20.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `7.00`
- `surface_water_ratio` = `0.04`
- `entity_count_sample_start` = `49.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7411.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `350.05`
- `fps_harmonic_avg` = `4091.92`
- `fps_1pct_low` = `761.77`

### Jungle flyby (single-biome world) (`chunk_jungle`)

Category: **Chunks**  |  Duration: 33252 ms  |  Sample ticks: 400

**FPS**  avg `2536.60`, min `87.42`, p50 `2555.58`, p95 `3518.71`, p99 `4454.36`, 1%low `567.49`, 0.1%low `231.01`, std `571.57`

**Frame time (ms)**  avg `0.43`, p50 `0.39`, p95 `0.67`, p99 `1.18`, p99.9 `2.37`, max `11.44`

**Client tick (ms)**  avg `0.57`, p95 `0.72`, max `1.28`

**Memory**  start `5514 MB`, end `2856 MB`, peak `7242 MB`, GC `16 events / 1837 ms`

**FPS over sampling window (ASCII):**

```
2893.7 |   ██                                                                           
2814.9 |   ███   ███     █     █     █                                                  
2736.0 |  ████   ███   ███   ███   ███    ██     █    ██                                
2657.2 | █████   ███   ███   ███   ███   ███   ███    ██     █    ██               ██   
2578.3 | █████  ████ █████   ███  ████   ███   ███   ███   ███   ███   ███   ██    ██   
2499.5 | █████ █████ █████  ████ ██████  ███  ████  ████   ███  ████  ████  ████  ███  █
2420.6 | █████ █████ █████ █████ ██████ ████ █████ █████   ███ █████  ████  ████  ███ ██
2341.8 |██████ █████ ███████████ ██████ ████ █████ ███████████ █████ █████ █████ ████ ██
2262.9 |██████ █████████████████ █████████████████ ███████████ █████ █████ █████ ████ ██
2184.1 |██████ █████████████████ ███████████████████████████████████ ███████████ ████ ██
2105.2 |████████████████████████████████████████████████████████████ ███████████████████
2026.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20595
   1 ms | █  370
   2 ms |   22
   3 ms |   5
   4 ms |   4
   5 ms |   1
   6 ms |   1
  10 ms |   1
  11 ms |   1
```

**Extras:**

- `scan_fallback` = `false`
- `biome` = `minecraft:jungle`
- `stamped_fallback` = `false`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `12.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-11.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.10`
- `entity_count_sample_start` = `23.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7417.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `231.01`
- `fps_harmonic_avg` = `2345.08`
- `fps_1pct_low` = `567.49`

### Desert flyby (single-biome world) (`chunk_desert`)

Category: **Chunks**  |  Duration: 31753 ms  |  Sample ticks: 400

**FPS**  avg `4545.92`, min `130.65`, p50 `4737.09`, p95 `5194.81`, p99 `5271.48`, 1%low `944.11`, 0.1%low `432.40`, std `737.35`

**Frame time (ms)**  avg `0.23`, p50 `0.21`, p95 `0.35`, p99 `0.77`, p99.9 `1.28`, max `7.65`

**Client tick (ms)**  avg `0.59`, p95 `0.70`, max `1.27`

**Memory**  start `6418 MB`, end `3428 MB`, peak `7724 MB`, GC `12 events / 1223 ms`

**FPS over sampling window (ASCII):**

```
5014.1 |                                                         ███          ███       
4895.6 |                                                ███      ██████  █   ████       
4777.1 |                                               ████      ██████ ███ ██████ █  ██
4658.6 |        █      ██ █                 ██         █████ ██████████ ██████████ █ ███
4540.1 |   ████ █     █████       ██ █     ██████     ██████ ██████████ ██████████ █████
4421.6 |   ██████ █  ███████    ██████  █  ██████   ████████ ██████████ ██████████ █████
4303.1 |█████████ ██ ███████ █████████  ██ ██████  █████████ ██████████ ██████████ █████
4184.6 |█████████ ██████████ ████████████████████ █████████████████████ ██████████ █████
4066.1 |█████████ ███████████████████████████████ █████████████████████ ████████████████
3947.6 |█████████ ███████████████████████████████ ██████████████████████████████████████
3829.1 |█████████ ███████████████████████████████ ██████████████████████████████████████
3710.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20945
   1 ms |   51
   3 ms |   1
   5 ms |   1
   6 ms |   1
   7 ms |   1
```

**Extras:**

- `scan_fallback` = `false`
- `biome` = `minecraft:desert`
- `stamped_fallback` = `false`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `33.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-11.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.02`
- `entity_count_sample_start` = `44.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7433.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `432.40`
- `fps_harmonic_avg` = `4261.12`
- `fps_1pct_low` = `944.11`

### Taiga flyby (single-biome world) (`chunk_taiga`)

Category: **Chunks**  |  Duration: 32053 ms  |  Sample ticks: 400

**FPS**  avg `4553.63`, min `111.61`, p50 `4863.81`, p95 `5321.98`, p99 `5411.26`, 1%low `583.61`, 0.1%low `205.27`, std `897.62`

**Frame time (ms)**  avg `0.25`, p50 `0.21`, p95 `0.39`, p99 `0.91`, p99.9 `2.87`, max `8.96`

**Client tick (ms)**  avg `0.57`, p95 `0.75`, max `1.89`

**Memory**  start `7164 MB`, end `5176 MB`, peak `7730 MB`, GC `12 events / 1257 ms`

**FPS over sampling window (ASCII):**

```
5115.6 |                                        █           █                  █ ██     
4939.7 |                    █                 █████      █ ██                ██████    █
4763.7 |██        ██     ████          █      █████  █████ ██     █ ██     █ ██████ ████
4587.8 |██    ██ ███   ███████   ███  ██  █   █████  █████████ █████████  █████████ ████
4411.8 |███ ████████   ████████ ████████  █████████ ██████████ █████████ ██████████ ████
4235.9 |███ █████████ █████████ █████████ █████████ ██████████ █████████ ██████████ ████
4059.9 |███ █████████ █████████ █████████ █████████ ██████████ █████████ ██████████ ████
3884.0 |███ █████████ █████████ █████████ █████████ ████████████████████ ██████████ ████
3708.0 |███ █████████ ███████████████████ █████████ ████████████████████ ██████████ ████
3532.1 |███ █████████ ███████████████████ █████████ ████████████████████ ██████████ ████
3356.1 |███ █████████ ██████████████████████████████████████████████████████████████████
3180.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20834
   1 ms |   123
   2 ms |   22
   3 ms |   10
   4 ms |   4
   5 ms |   2
   6 ms |   1
   7 ms |   1
   8 ms |   3
```

**Extras:**

- `scan_fallback` = `false`
- `biome` = `minecraft:taiga`
- `stamped_fallback` = `false`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `6.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-18.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.09`
- `entity_count_sample_start` = `24.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7451.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `205.27`
- `fps_harmonic_avg` = `4055.63`
- `fps_1pct_low` = `583.61`

### Snowy plains flyby (`chunk_snowy`)

Category: **Chunks**  |  Duration: 31454 ms  |  Sample ticks: 400

**FPS**  avg `4809.25`, min `81.95`, p50 `5154.64`, p95 `5527.92`, p99 `5608.52`, 1%low `745.48`, 0.1%low `269.35`, std `892.15`

**Frame time (ms)**  avg `0.23`, p50 `0.19`, p95 `0.35`, p99 `0.79`, p99.9 `2.08`, max `12.20`

**Client tick (ms)**  avg `0.54`, p95 `0.71`, max `1.15`

**Memory**  start `4382 MB`, end `3630 MB`, peak `7636 MB`, GC `14 events / 1956 ms`

**FPS over sampling window (ASCII):**

```
5224.1 |                                              █  █ █        █           ███     
5024.9 |  ██████     █████    █   █ ██      █ ███    █████ █  ███ █ █     █   █████     
4825.6 |  ██████   ████████  █████████  █   █████  ██████████ ███ █████  ██████████  ███
4626.4 |████████ ███████████ █████████  █████████  ██████████ ██████████ ██████████  ███
4427.1 |████████ ███████████ █████████ ██████████ ███████████ ██████████ ██████████ ████
4227.9 |████████ ███████████ █████████ ██████████ ██████████████████████ ██████████ ████
4028.7 |████████ █████████████████████ ██████████ ██████████████████████ ██████████ ████
3829.4 |████████ ████████████████████████████████ █████████████████████████████████ ████
3630.2 |████████ ████████████████████████████████ █████████████████████████████████ ████
3430.9 |████████ ████████████████████████████████ ██████████████████████████████████████
3231.7 |█████████████████████████████████████████ ██████████████████████████████████████
3032.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20888
   1 ms |   88
   2 ms |   15
   3 ms |   4
   4 ms |   1
   5 ms |   1
   6 ms |   2
  12 ms |   1
```

**Extras:**

- `scan_fallback` = `false`
- `biome` = `minecraft:snowy_plains`
- `stamped_fallback` = `false`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `5.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-14.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `19.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7457.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `269.35`
- `fps_harmonic_avg` = `4379.32`
- `fps_1pct_low` = `745.48`

### Forest flyby (`chunk_forest`)

Category: **Chunks**  |  Duration: 37304 ms  |  Sample ticks: 400

**FPS**  avg `3864.32`, min `149.56`, p50 `3971.41`, p95 `4972.65`, p99 `5136.11`, 1%low `855.50`, 0.1%low `455.25`, std `816.67`

**Frame time (ms)**  avg `0.28`, p50 `0.25`, p95 `0.41`, p99 `0.92`, p99.9 `1.43`, max `6.69`

**Client tick (ms)**  avg `0.66`, p95 `0.88`, max `1.33`

**Memory**  start `6694 MB`, end `5044 MB`, peak `7744 MB`, GC `22 events / 3070 ms`

**FPS over sampling window (ASCII):**

```
4474.1 |                            ██                                                  
4329.5 |                   ██    █████                ███        █                      
4184.8 |                 ████    █████     ████     █████    █████        █             
4040.2 |██       ███     ████    ██████    █████    █████    █████     ████      ███    
3895.5 |███    █████    █████    ██████   ██████    █████    █████    █████    █████    
3750.8 |███    █████ ████████  ████████ █ ██████  ███████ █████████   █████    █████    
3606.2 |███ ████████ ████████ █████████ ████████ ████████ █████████ █ █████    █████    
3461.5 |███ ████████ ████████ █████████ ████████ ████████ █████████ ████████████████ ███
3316.9 |█████████████████████ █████████ ████████ ████████ ██████████████████████████ ███
3172.2 |█████████████████████ ███████████████████████████ ██████████████████████████ ███
3027.6 |████████████████████████████████████████████████████████████████████████████ ███
2882.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20881
   1 ms |   114
   2 ms |   3
   6 ms |   2
```

**Extras:**

- `scan_fallback` = `false`
- `biome` = `minecraft:forest`
- `stamped_fallback` = `false`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `65.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `512.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `18.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `5.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `47.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7477.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `455.25`
- `fps_harmonic_avg` = `3568.71`
- `fps_1pct_low` = `855.50`

### Savanna flyby (`chunk_savanna`)

Category: **Chunks**  |  Duration: 32003 ms  |  Sample ticks: 400

**FPS**  avg `4684.91`, min `263.79`, p50 `4972.65`, p95 `5336.18`, p99 `5402.49`, 1%low `895.47`, 0.1%low `467.81`, std `817.53`

**Frame time (ms)**  avg `0.23`, p50 `0.20`, p95 `0.35`, p99 `0.82`, p99.9 `1.66`, max `3.79`

**Client tick (ms)**  avg `0.62`, p95 `0.73`, max `1.25`

**Memory**  start `4948 MB`, end `4812 MB`, peak `7444 MB`, GC `12 events / 1389 ms`

**FPS over sampling window (ASCII):**

```
5106.4 |                                   █  ██      █ █         █              █      
4939.2 |    █ █       █         █  █       █████      ██████      █   █  █ █ █   █      
4772.0 |    ████   █ ████  █    █  ██      ██████ ███████████ ███ █ ███  ███ █████  ████
4604.7 | ███████  ████████ █  ████ ██   █████████ ███████████ ███ ██████ █████████  ████
4437.5 |████████  ██████████ █████████  █████████████████████ ██████████ ██████████ ████
4270.2 |████████ ███████████ █████████  █████████████████████ ██████████ ██████████ ████
4103.0 |████████ ███████████ █████████ █████████████████████████████████████████████████
3935.7 |████████ ███████████████████████████████████████████████████████████████████████
3768.5 |████████ ███████████████████████████████████████████████████████████████████████
3601.2 |████████ ███████████████████████████████████████████████████████████████████████
3434.0 |████████ ███████████████████████████████████████████████████████████████████████
3266.7 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20910
   1 ms |   82
   2 ms |   5
   3 ms |   3
```

**Extras:**

- `scan_fallback` = `false`
- `biome` = `minecraft:savanna`
- `stamped_fallback` = `false`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `23.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-37.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `60.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7481.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `467.81`
- `fps_harmonic_avg` = `4337.30`
- `fps_1pct_low` = `895.47`

### Swamp flyby (`chunk_swamp`)

Category: **Chunks**  |  Duration: 35253 ms  |  Sample ticks: 400

**FPS**  avg `4435.10`, min `177.53`, p50 `4730.37`, p95 `5208.33`, p99 `5305.04`, 1%low `794.21`, 0.1%low `428.57`, std `858.95`

**Frame time (ms)**  avg `0.25`, p50 `0.21`, p95 `0.40`, p99 `0.92`, p99.9 `1.78`, max `5.63`

**Client tick (ms)**  avg `0.69`, p95 `0.94`, max `1.41`

**Memory**  start `3750 MB`, end `4930 MB`, peak `7016 MB`, GC `26 events / 2821 ms`

**FPS over sampling window (ASCII):**

```
4966.9 |                                           █                 █           █      
4817.2 |            █         █       ██        ██ █        █        ██        █ █      
4667.5 |██     ████ █       ███      ████      █████        █       ████   ██ ██ ██  █  
4517.8 |██    ███████     █████   ██ █████    ██████      ████    ██████   ████████  ██ 
4368.1 |███  ████████     ██████ ███ █████   ███████  █ ██████   ████████ █████████ ████
4218.4 |███ █████████ ██████████ █████████  ████████  █████████ █████████ █████████ ████
4068.7 |███ █████████ ██████████ █████████ █████████ ██████████ █████████ █████████ ████
3919.0 |███ █████████ ██████████ █████████ █████████ ██████████ ███████████████████ ████
3769.4 |███ █████████ ██████████ █████████ █████████ ██████████ ███████████████████ ████
3619.7 |█████████████ ████████████████████ ████████████████████ ███████████████████ ████
3470.0 |███████████████████████████████████████████████████████████████████████████ ████
3320.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20857
   1 ms |   131
   2 ms |   10
   3 ms |   1
   5 ms |   1
```

**Extras:**

- `scan_fallback` = `false`
- `biome` = `minecraft:swamp`
- `stamped_fallback` = `false`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `59.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `512.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `16.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `43.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7487.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `428.57`
- `fps_harmonic_avg` = `4044.89`
- `fps_1pct_low` = `794.21`

### Cherry grove flyby (`chunk_cherry`)

Category: **Chunks**  |  Duration: 32253 ms  |  Sample ticks: 400

**FPS**  avg `4733.12`, min `106.14`, p50 `5027.65`, p95 `5543.24`, p99 `5688.28`, 1%low `673.78`, 0.1%low `252.71`, std `927.72`

**Frame time (ms)**  avg `0.24`, p50 `0.20`, p95 `0.38`, p99 `0.90`, p99.9 `2.45`, max `9.42`

**Client tick (ms)**  avg `0.69`, p95 `0.87`, max `6.26`

**Memory**  start `6698 MB`, end `5294 MB`, peak `7652 MB`, GC `11 events / 1195 ms`

**FPS over sampling window (ASCII):**

```
5342.9 |                                                          █ █                   
5148.1 |                        ███          ████       ████    █ █ █    ███ ██     █   
4953.2 |               █ ██  ██ ███     █  █ ████ █ █ ██████  ████████   ██████ █   ██ █
4758.4 | ███          █████  ██████  █ ██████████ █ █████████ ██████████ ██████████ ████
4563.6 | ███ █ ██     █████  █████████ ██████████ ███████████ ██████████ ██████████ ████
4368.8 |█████████     ██████ █████████ ██████████ ███████████ ██████████ ██████████ ████
4174.0 |█████████   █ ██████ █████████ ██████████ ██████████████████████ ██████████ ████
3979.2 |█████████   ██████████████████ ██████████ ██████████████████████ ██████████ ████
3784.3 |██████████  ██████████████████ █████████████████████████████████ ██████████ ████
3589.5 |██████████ ███████████████████ █████████████████████████████████ ███████████████
3394.7 |██████████████████████████████ █████████████████████████████████████████████████
3199.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20859
   1 ms |   110
   2 ms |   18
   3 ms |   7
   4 ms |   3
   5 ms |   1
   8 ms |   1
   9 ms |   1
```

**Extras:**

- `scan_fallback` = `false`
- `biome` = `minecraft:cherry_grove`
- `stamped_fallback` = `false`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `16.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-25.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `41.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7499.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `252.71`
- `fps_harmonic_avg` = `4252.34`
- `fps_1pct_low` = `673.78`

### Badlands flyby (`chunk_badlands`)

Category: **Chunks**  |  Duration: 36652 ms  |  Sample ticks: 400

**FPS**  avg `4925.02`, min `112.88`, p50 `5288.21`, p95 `5711.02`, p99 `5807.20`, 1%low `667.12`, 0.1%low `255.22`, std `964.58`

**Frame time (ms)**  avg `0.23`, p50 `0.19`, p95 `0.37`, p99 `0.82`, p99.9 `2.73`, max `8.86`

**Client tick (ms)**  avg `0.54`, p95 `0.75`, max `1.42`

**Memory**  start `3522 MB`, end `2994 MB`, peak `6986 MB`, GC `22 events / 2791 ms`

**FPS over sampling window (ASCII):**

```
5438.4 |                                                           ██ █       ████      
5236.1 |                                    ██ █     ██ ███      ██████  █████████   █ █
5033.9 | ██ █ █    ████ ██    ███████   █  █████  █████ ███   █ ███████  █████████   ███
4831.7 |███████  █████████  █████████   ██ █████  ██████████ ██████████  ██████████ ████
4629.5 |███████  ██████████ █████████  █████████  ██████████ ██████████ ███████████ ████
4427.2 |███████  ██████████ █████████  █████████  ██████████ ██████████ ███████████ ████
4225.0 |███████  ██████████ ██████████ █████████ ███████████ ██████████ ███████████ ████
4022.8 |███████ ██████████████████████ ████████████████████████████████ ███████████ ████
3820.5 |██████████████████████████████ ████████████████████████████████ ████████████████
3618.3 |███████████████████████████████████████████████████████████████ ████████████████
3416.1 |███████████████████████████████████████████████████████████████ ████████████████
3213.8 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20868
   1 ms |   94
   2 ms |   23
   3 ms |   10
   4 ms |   1
   5 ms |   2
   7 ms |   1
   8 ms |   1
```

**Extras:**

- `scan_fallback` = `false`
- `biome` = `minecraft:badlands`
- `stamped_fallback` = `false`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `512.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-6.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `7.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `7.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7507.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `255.22`
- `fps_harmonic_avg` = `4406.63`
- `fps_1pct_low` = `667.12`

### Dark forest flyby (dense canopy) (`chunk_dark_forest`)

Category: **Chunks**  |  Duration: 32353 ms  |  Sample ticks: 400

**FPS**  avg `4350.21`, min `111.90`, p50 `4574.57`, p95 `5221.93`, p99 `5361.96`, 1%low `799.24`, 0.1%low `376.43`, std `838.68`

**Frame time (ms)**  avg `0.25`, p50 `0.22`, p95 `0.39`, p99 `0.87`, p99.9 `1.73`, max `8.94`

**Client tick (ms)**  avg `0.56`, p95 `0.74`, max `1.14`

**Memory**  start `4120 MB`, end `3892 MB`, peak `7500 MB`, GC `12 events / 1399 ms`

**FPS over sampling window (ASCII):**

```
4923.8 |                                                               █      ██ █      
4756.6 |                                                 ███        ████     ██████    █
4589.4 |        █████      █ ███             ██ █        ████    ███████     ██████ █ ██
4422.3 |        █████     ███████ █  █       ███████  █  █████  █████████ ██ ██████ █ ██
4255.1 |        ██████ ██████████ ████████  ████████  █  █████  █████████ █████████ ████
4087.9 |███   ████████ ██████████ ████████  ████████ █████████ ██████████ █████████ ████
3920.7 |████ █████████ ██████████ ████████ █████████ ████████████████████ █████████ ████
3753.5 |████ █████████ ██████████ ██████████████████ ██████████████████████████████ ████
3586.4 |██████████████ █████████████████████████████ ██████████████████████████████ ████
3419.2 |████████████████████████████████████████████ ███████████████████████████████████
3252.0 |████████████████████████████████████████████ ███████████████████████████████████
3084.8 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20879
   1 ms |   110
   2 ms |   6
   3 ms |   2
   4 ms |   2
   8 ms |   1
```

**Extras:**

- `scan_fallback` = `false`
- `biome` = `minecraft:dark_forest`
- `stamped_fallback` = `false`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `9.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `-15.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `5.00`
- `surface_water_ratio` = `0.00`
- `entity_count_sample_start` = `24.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7517.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `376.43`
- `fps_harmonic_avg` = `3984.76`
- `fps_1pct_low` = `799.24`

### Windswept hills flyby (`chunk_mountain`)

Category: **Chunks**  |  Duration: 31754 ms  |  Sample ticks: 400

**FPS**  avg `4643.10`, min `457.67`, p50 `4875.67`, p95 `5241.09`, p99 `5336.18`, 1%low `1033.01`, 0.1%low `649.82`, std `735.52`

**Frame time (ms)**  avg `0.23`, p50 `0.21`, p95 `0.34`, p99 `0.72`, p99.9 `1.22`, max `2.19`

**Client tick (ms)**  avg `0.55`, p95 `0.64`, max `0.91`

**Memory**  start `5836 MB`, end `2598 MB`, peak `7510 MB`, GC `18 events / 2101 ms`

**FPS over sampling window (ASCII):**

```
5082.4 |                                                                     █   █      
4975.5 |                                                                     ██ ██   █  
4868.5 |   █  █         █                             █ ██      █  █ █      ██████  ██  
4761.5 | █ ████       ███        █ █       ███     █  ████  █ ███  ████  █  ██████  ████
4654.5 | █ ████     ██████      █████      ████    █ ████████ ███ ██████ █████████  ████
4547.5 | ██████   ████████    ███████   ██ █████  ███████████ ██████████ ██████████ ████
4440.6 |████████ ██████████  █████████ ██████████ ███████████ ██████████████████████████
4333.6 |████████ ██████████  █████████ ██████████ ███████████ ██████████████████████████
4226.6 |████████ ███████████ █████████ ██████████ ███████████ ██████████████████████████
4119.6 |████████████████████ ████████████████████ ███████████ ██████████████████████████
4012.6 |████████████████████ ███████████████████████████████████████████████████████████
3905.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20944
   1 ms |   54
   2 ms |   2
```

**Extras:**

- `scan_fallback` = `false`
- `biome` = `minecraft:windswept_hills`
- `stamped_fallback` = `false`
- `x_offset_used` = `0.00`
- `entity_count_sample_end` = `41.00`
- `preset_quick` = `1.00`
- `z_offset_used` = `0.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `25.00`
- `stamped_blocks` = `0.00`
- `flyby_blocks_per_tick` = `1.20`
- `preload_duration_ms` = `6.00`
- `surface_water_ratio` = `0.06`
- `entity_count_sample_start` = `16.00`
- `flyby_distance_blocks` = `720.00`
- `preset_long` = `0.00`
- `seed` = `7523.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `649.82`
- `fps_harmonic_avg` = `4372.71`
- `fps_1pct_low` = `1033.01`

### Idle baseline (orbit, flat world) (`idle_baseline`)

Category: **Baseline**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `5982.32`, min `790.08`, p50 `6165.23`, p95 `6373.49`, p99 `6435.01`, 1%low `1701.86`, 0.1%low `1058.54`, std `660.02`

**Frame time (ms)**  avg `0.17`, p50 `0.16`, p95 `0.21`, p99 `0.42`, p99.9 `0.77`, max `1.27`

**Client tick (ms)**  avg `0.46`, p95 `0.49`, max `0.88`

**Memory**  start `3292 MB`, end `4692 MB`, peak `7380 MB`, GC `4 events / 354 ms`

**FPS over sampling window (ASCII):**

```
6219.4 |                                    █          █                                
6162.3 |                       ██    █      █          █                                
6105.2 |                  ██   ██   ██ ██ ████   ███  ██ ███       █   █                
6048.0 |           █    █ ███ ████  █████ ████ ██████ ████████   █ ███ █              █ 
5990.9 |           █ █  ███████████ ██████████ ██████ ████████   █████ █          █  ██ 
5933.8 |     █  █ ██ █ ███████████████████████ ███████████████   █████ ██ ██  ██  ██ ██ 
5876.6 | ███ █ ██ ██ █ ███████████████████████ ████████████████ ██████ ████████████████ 
5819.5 |████ ████ █████████████████████████████████████████████ ██████ █████████████████
5762.4 |████ ████ █████████████████████████████████████████████ ██████ █████████████████
5705.2 |████ ████ █████████████████████████████████████████████ ████████████████████████
5648.1 |████ ███████████████████████████████████████████████████████████████████████████
5591.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20997
   1 ms |   3
```

**Extras:**

- `preset_quick` = `1.00`
- `preload_duration_ms` = `50.00`
- `entity_count_sample_start` = `1.00`
- `preset_full` = `0.00`
- `preset_long` = `0.00`
- `entity_count_delta` = `0.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `1058.54`
- `entity_count_sample_end` = `1.00`
- `fps_harmonic_avg` = `5812.81`
- `fps_1pct_low` = `1701.86`
- `seed` = `1923.00`

### Particle diversity stress (16 types simultaneously) (`particle_diversity_stress`)

Category: **Stress**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `440.28`, min `146.18`, p50 `453.45`, p95 `491.11`, p99 `501.75`, 1%low `224.07`, 0.1%low `183.09`, std `53.00`

**Frame time (ms)**  avg `2.32`, p50 `2.21`, p95 `3.07`, p99 `4.14`, p99.9 `4.85`, max `6.84`

**Client tick (ms)**  avg `1.53`, p95 `1.69`, max `2.47`

**Memory**  start `3774 MB`, end `1876 MB`, peak `7580 MB`, GC `8 events / 439 ms`

**FPS over sampling window (ASCII):**

```
468.8 |                               █   █                                            
463.9 |                              ███████                                           
459.0 |                              ███████ █                                         
454.0 |                              ███████ ██    █   ██ █   █                        
449.1 |█ █                           ███████ ████ ██  ███ ███ █   ██  █                
444.1 |███                       █   ████████████ ███████ ██████████ ██                
439.2 |███                  █ █ ██ █ ████████████████████ █████████████   ███         █
434.2 |███ █             █  ████████ ████████████████████ █████████████ █████    █ ████
429.3 |█████    ██   ██████ ████████ ████████████████████ █████████████ █████ █  ██████
424.4 |█████████████ ███████████████ ██████████████████████████████████ ███████  ██████
419.4 |█████████████ ██████████████████████████████████████████████████████████████████
414.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   1 ms | █  126
   2 ms | ████████████████████████████████████████  8049
   3 ms | █  298
   4 ms | █  140
   5 ms |   4
   6 ms |   2
```

**Extras:**

- `fps_1pct_low` = `224.07`
- `preset_long` = `0.00`
- `particle_types` = `16.00`
- `particles_spawned` = `256000.00`
- `seed` = `2521.00`
- `preload_duration_ms` = `39.00`
- `fps_harmonic_avg` = `430.96`
- `preset_quick` = `1.00`
- `entity_count_delta` = `0.00`
- `entity_count_sample_end` = `1.00`
- `fps_0p1pct_low` = `183.09`
- `preset_full` = `0.00`
- `entity_count_sample_start` = `1.00`
- `preload_chunks` = `81.00`

### Fluid spread (water basin, 4-step periodic reset) (`fluid_spread`)

Category: **Fluids**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `5653.60`, min `529.66`, p50 `5910.17`, p95 `6090.13`, p99 `6153.85`, 1%low `1616.09`, 0.1%low `1050.57`, std `699.14`

**Frame time (ms)**  avg `0.18`, p50 `0.17`, p95 `0.23`, p99 `0.45`, p99.9 `0.82`, max `1.89`

**Client tick (ms)**  avg `0.46`, p95 `0.50`, max `0.86`

**Memory**  start `6932 MB`, end `6438 MB`, peak `7460 MB`, GC `10 events / 1313 ms`

**FPS over sampling window (ASCII):**

```
5908.7 |                                   █   █       █                 █              
5849.8 |    ██   █             █ █         █   █       █    █            █  █        █  
5791.0 |   ███   █   █         █ ██ ██     █   ████    ██   █            ██ █ █ █    █  
5732.1 |   ███  ██   █         █ ██ ███ █ ██  █████    ██ █ █       █    ██ █ █ █  █ █  
5673.3 |   ███  ██   █ ██     █████ █████ ███ ██████   ██ █ █ █     ██ ████ ███ ████ █  
5614.4 |  █████ ██  ██ ██ █████████ █████ ███ ██████   ██ █ █ █ ██  ██ ████ ███ ████ █ █
5555.6 |██████████████ ██ █████████ █████████████████  ████ █ █ ██  ██ ████ ███ ████ ███
5496.7 |██████████████ ██ █████████ ███████████████████████ █ █ ██  ██ ████████ ████ ███
5437.9 |█████████████████ █████████ ███████████████████████ █ █ ████████████████████████
5379.0 |█████████████████ █████████ ███████████████████████ █ █ ████████████████████████
5320.2 |█████████████████ █████████ █████████████████████████ ██████████████████████████
5261.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20997
   1 ms |   3
```

**Extras:**

- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`
- `block_state_changes` = `2305.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `0.00`
- `waves_spawned` = `6.00`
- `scheduled_block_ticks` = `0.00`
- `preload_duration_ms` = `49.00`
- `sources_placed_total` = `54.00`
- `entity_count_sample_start` = `1.00`
- `preset_long` = `0.00`
- `seed` = `9043.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `1050.57`
- `neighbour_updates` = `0.00`
- `scheduled_fluid_ticks` = `3146.00`
- `fps_harmonic_avg` = `5469.58`
- `fps_1pct_low` = `1616.09`

### Lighting update (16×16 glowstone reveal/hide) (`lighting_update`)

Category: **Lighting**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `5593.84`, min `573.39`, p50 `5934.72`, p95 `6157.64`, p99 `6211.18`, 1%low `1547.44`, 0.1%low `894.55`, std `758.49`

**Frame time (ms)**  avg `0.19`, p50 `0.17`, p95 `0.24`, p99 `0.44`, p99.9 `0.89`, max `1.74`

**Client tick (ms)**  avg `0.48`, p95 `0.52`, max `0.97`

**Memory**  start `5258 MB`, end `5984 MB`, peak `7538 MB`, GC `4 events / 267 ms`

**FPS over sampling window (ASCII):**

```
5942.6 |            █                                                                   
5862.3 |       █    █    █                   ██ █      ██               █    █  ██ █    
5782.0 |   █   █ █  █   ███   █ ██ █         ██ █      ██ ██ █  █       █    █  ██ ██   
5701.7 |   █   █ █ ██  ████   █ ██ ██        ██ █      ██ ██ █ ██      ██    █████ ██   
5621.4 |   ██  █ █ ██████████ █ █████        ██ █ █    ██ ██ ████ ██   ██ █  █████ ███  
5541.2 | █ ██ ██ █ ██████████ █ █████  █ █  ███ █ █  ████ ██ ███████ █ ██ █  █████ ███  
5460.9 | █ ██ ██ ████████████ █ █████  █ █  █████ █ ████████████████ █ ██ ██████████████
5380.6 | █ ██ ██ ████████████ ████████ ████ █████ ██████████████████ ████ ██████████████
5300.3 | █ ██████████████████ █████████████ ████████████████████████████████████████████
5220.1 | ████████████████████ █████████████ ████████████████████████████████████████████
5139.8 |███████████████████████████████████ ████████████████████████████████████████████
5059.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20989
   1 ms |   11
```

**Extras:**

- `toggles` = `22.00`
- `preload_chunks` = `81.00`
- `preset_long` = `0.00`
- `fps_0p1pct_low` = `894.55`
- `preset_quick` = `1.00`
- `preset_full` = `0.00`
- `fps_harmonic_avg` = `5389.38`
- `fps_1pct_low` = `1547.44`
- `preload_duration_ms` = `49.00`
- `scheduled_block_ticks` = `0.00`
- `entity_count_delta` = `0.00`
- `blocks_per_toggle` = `256.00`
- `neighbour_updates` = `0.00`
- `block_state_changes` = `4864.00`
- `seed` = `9007.00`
- `entity_count_sample_start` = `1.00`
- `entity_count_sample_end` = `1.00`

### Hopper grid 20×20 (transfer storm) (`hopper_grid`)

Category: **Block-Entities**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `4684.63`, min `1026.80`, p50 `4930.97`, p95 `5109.86`, p99 `5157.30`, 1%low `1519.29`, 0.1%low `1165.40`, std `570.89`

**Frame time (ms)**  avg `0.22`, p50 `0.20`, p95 `0.26`, p99 `0.51`, p99.9 `0.79`, max `0.97`

**Client tick (ms)**  avg `0.45`, p95 `0.49`, max `1.01`

**Memory**  start `4456 MB`, end `4454 MB`, peak `7210 MB`, GC `6 events / 913 ms`

**FPS over sampling window (ASCII):**

```
4951.5 |                                           █                 █       █   █  █   
4895.3 |                                     █     █                 █ ██  █ █   ██ █  █
4839.0 |                              █    █ █     ██    █          ██ ██  █ █   ██ █  █
4782.8 |         █   █                █   ██ █   ████ █  ██         ██ ██  █ █ █ ██ ██ █
4726.6 |    ██   █   ███  █      █ █ ██ █ ██ █   ████ █  ██   █     ██ ███████ █ ███████
4670.4 |█  ███   █  ████  █     ██ █ ███████ █ █ ██████  ██   █  █  ██ █████████████████
4614.2 |█ ████ █ █  ████ ██ █ █ ██ █ ███████ █ █ ███████ ██  ███ ██ ██ █████████████████
4557.9 |██████ █ █  ████ ██ ███ ████ █████████ █ ███████ ██  ███ ███████████████████████
4501.7 |██████ ███  ████ ███████████████████████ ███████ ██ ████ ███████████████████████
4445.5 |██████ █████████████████████████████████████████████████ ███████████████████████
4389.3 |████████████████████████████████████████████████████████ ███████████████████████
4333.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  21000
```

**Extras:**

- `preload_chunks` = `81.00`
- `preset_long` = `0.00`
- `fps_0p1pct_low` = `1165.40`
- `preset_quick` = `1.00`
- `restocks` = `20.00`
- `preset_full` = `0.00`
- `fps_harmonic_avg` = `4555.07`
- `fps_1pct_low` = `1519.29`
- `preload_duration_ms` = `51.00`
- `scheduled_block_ticks` = `0.00`
- `entity_count_delta` = `0.00`
- `neighbour_updates` = `0.00`
- `block_state_changes` = `0.00`
- `hoppers_built` = `400.00`
- `seed` = `8011.00`
- `entity_count_sample_start` = `1.00`
- `entity_count_sample_end` = `1.00`

### Comparator storage (8×8 chests + comparators) (`comparator_storage`)

Category: **Block-Entities**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `3630.72`, min `46.68`, p50 `3837.30`, p95 `4000.00`, p99 `4061.74`, 1%low `1059.08`, 0.1%low `406.06`, std `471.00`

**Frame time (ms)**  avg `0.28`, p50 `0.26`, p95 `0.37`, p99 `0.63`, p99.9 `1.03`, max `21.42`

**Client tick (ms)**  avg `0.45`, p95 `0.48`, max `0.77`

**Memory**  start `2544 MB`, end `7650 MB`, peak `7650 MB`, GC `0 events / 0 ms`

**FPS over sampling window (ASCII):**

```
3843.3 |█        █     ███ ████ ██   █                           █  █   █          █  █ 
3724.3 |███ ██████████ ████████████ ██ █████ █            ███ ███████   ██████ █ █ ██ █ 
3605.4 |██████████████████████████████████████            ████████████████████ ██████ █ 
3486.5 |███████████████████████████████████████          ███████████████████████████████
3367.6 |███████████████████████████████████████          ███████████████████████████████
3248.7 |███████████████████████████████████████        █ ███████████████████████████████
3129.8 |███████████████████████████████████████      ███████████████████████████████████
3010.9 |███████████████████████████████████████ ████████████████████████████████████████
2892.0 |███████████████████████████████████████ ████████████████████████████████████████
2773.1 |███████████████████████████████████████ ████████████████████████████████████████
2654.2 |███████████████████████████████████████ ████████████████████████████████████████
2535.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20972
   1 ms |   23
   2 ms |   4
  21 ms |   1
```

**Extras:**

- `oscillations` = `20.00`
- `entity_count_sample_end` = `1.00`
- `preset_quick` = `1.00`
- `block_state_changes` = `1152.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `0.00`
- `scheduled_block_ticks` = `1152.00`
- `comparators_built` = `64.00`
- `preload_duration_ms` = `31.00`
- `chests_built` = `64.00`
- `entity_count_sample_start` = `1.00`
- `preset_long` = `0.00`
- `seed` = `8053.00`
- `preload_chunks` = `81.00`
- `fps_0p1pct_low` = `406.06`
- `neighbour_updates` = `0.00`
- `fps_harmonic_avg` = `3509.20`
- `fps_1pct_low` = `1059.08`

