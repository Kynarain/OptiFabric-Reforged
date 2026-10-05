# FPS Test session — 2026-10-05T18:19:29.8092057+08:00

- Minecraft: `1.21.1`
- OS: `Windows 10 10.0 (amd64)`
- CPU cores: `16`
- Java: `22.0.2` (Java HotSpot(TM) 64-Bit Server VM)
- Max heap: `8192 MB`
- GPU: `AMD Radeon RX 7800 XT / 3.2.0 Core Profile Context 25.12.1.251128`

## Summary

| # | Benchmark | Cat. | Avg FPS | 1% low | 0.1% low | p99 frame ms | Tick avg ms | GC | Heap Δ MB |
|---:|---|---|---:|---:|---:|---:|---:|---:|---:|
| 1 | [Particle cycle (7 types → all together)](#particle-cycle-7-types--all-together) | Particles | 4222.3 | 1116.7 | 677.1 | 0.55 | 0.81 | 6 | 3940 |
| 2 | [Cows ×200 ring](#cows-200-ring) | Entities | 2233.2 | 728.6 | 602.1 | 0.99 | 0.86 | 0 | 4006 |
| 3 | [Sheep ×200 ring](#sheep-200-ring) | Entities | 1395.0 | 597.7 | 464.6 | 1.53 | 0.80 | 6 | 1756 |
| 4 | [Zombies ×150 ring (AI on)](#zombies-150-ring-ai-on) | Entities | 1240.7 | 550.2 | 478.9 | 1.67 | 0.83 | 4 | 346 |
| 5 | [Pigs ×250 ring](#pigs-250-ring) | Entities | 2207.8 | 750.4 | 606.0 | 1.08 | 0.81 | 0 | 4358 |
| 6 | [Villagers ×100 ring](#villagers-100-ring) | Entities | 1784.9 | 745.1 | 608.1 | 1.25 | 0.68 | 0 | 4178 |
| 7 | [Chickens ×300 ring](#chickens-300-ring) | Entities | 1720.6 | 660.1 | 535.6 | 1.40 | 0.82 | 1 | 4098 |
| 8 | [Item entities ×500](#item-entities-500) | Entities | 909.8 | 405.3 | 328.9 | 2.30 | 1.16 | 10 | 2380 |
| 9 | [XP orbs ×500 ring](#xp-orbs-500-ring) | Entities | 1778.8 | 258.8 | 234.1 | 3.72 | 3.17 | 8 | 1750 |
| 10 | [Item merge storm (cobblestone, mergeable)](#item-merge-storm-cobblestone-mergeable) | Entities | 617.8 | 119.9 | 111.2 | 7.95 | 4.42 | 10 | 1818 |
| 11 | [Zombies obstacle pathfinding (150 + pillar maze)](#zombies-obstacle-pathfinding-150--pillar-maze) | Entities | 1455.1 | 574.0 | 420.7 | 1.54 | 0.83 | 4 | 3742 |
| 12 | [Villager AI village (80, brain on)](#villager-ai-village-80-brain-on) | Entities | 1224.8 | 603.9 | 441.3 | 1.45 | 0.60 | 6 | 1276 |
| 13 | [TNT field (14×14 staggered fuses)](#tnt-field-1414-staggered-fuses) | Physics | 4379.5 | 1267.0 | 893.6 | 0.52 | 0.82 | 1 | 6048 |
| 14 | [TNT field destructive (breaks terrain)](#tnt-field-destructive-breaks-terrain) | Physics | 4275.2 | 1127.6 | 765.8 | 0.57 | 0.88 | 10 | 2068 |
| 15 | [Falling sand wall 40×40 (heavy)](#falling-sand-wall-4040-heavy) | Physics | 2792.5 | 33.6 | 29.7 | 27.44 | 15.95 | 8 | 1536 |
| 16 | [Falling gravel mixed heavy (sand+gravel+concrete)](#falling-gravel-mixed-heavy-sandgravelconcrete) | Physics | 2724.9 | 34.2 | 30.6 | 26.34 | 15.95 | 14 | 1020 |
| 17 | [Falling sand wall (lite, staggered)](#falling-sand-wall-lite-staggered) | Physics | 5561.6 | 1153.6 | 514.6 | 0.47 | 1.81 | 4 | 1922 |
| 18 | [Falling gravel mixed (lite, staggered)](#falling-gravel-mixed-lite-staggered) | Physics | 5517.0 | 1146.4 | 514.6 | 0.46 | 1.76 | 1 | 5360 |
| 19 | [Projectile storm (arrows + snowballs)](#projectile-storm-arrows--snowballs) | Physics | 2151.4 | 730.6 | 567.6 | 1.08 | 0.77 | 6 | 1960 |
| 20 | [Redstone clocks (6×6)](#redstone-clocks-66) | Redstone | 5461.1 | 1561.4 | 1176.4 | 0.49 | 0.54 | 4 | 2938 |
| 21 | [Redstone dust grid (16 trails ×32 + repeaters + lamps)](#redstone-dust-grid-16-trails-32--repeaters--lamps) | Redstone | 5429.4 | 1218.6 | 372.3 | 0.47 | 0.48 | 6 | 4194 |
| 22 | [Piston/slime array (8×8 toggled every 8t)](#pistonslime-array-88-toggled-every-8t) | Redstone | 5679.8 | 1535.0 | 999.5 | 0.48 | 0.49 | 4 | 3618 |
| 23 | [Static dense forest (orbit canopy, no worldgen)](#static-dense-forest-orbit-canopy-no-worldgen) | Chunks | 5182.3 | 1577.7 | 1044.8 | 0.44 | 0.50 | 6 | 3042 |
| 24 | [Plains flyby (single-biome world)](#plains-flyby-single-biome-world) | Chunks | 4378.5 | 483.0 | 147.5 | 1.07 | 0.81 | 16 | 5384 |
| 25 | [Jungle flyby (single-biome world)](#jungle-flyby-single-biome-world) | Chunks | 2454.1 | 330.7 | 96.4 | 1.44 | 0.76 | 21 | 2660 |
| 26 | [Desert flyby (single-biome world)](#desert-flyby-single-biome-world) | Chunks | 4358.8 | 525.7 | 132.7 | 0.85 | 0.71 | 16 | 3960 |
| 27 | [Taiga flyby (single-biome world)](#taiga-flyby-single-biome-world) | Chunks | 4412.2 | 405.3 | 118.5 | 1.07 | 0.72 | 16 | 4690 |
| 28 | [Snowy plains flyby](#snowy-plains-flyby) | Chunks | 4531.3 | 443.3 | 119.2 | 1.00 | 0.69 | 16 | 4792 |
| 29 | [Forest flyby](#forest-flyby) | Chunks | 4173.8 | 447.2 | 114.6 | 1.10 | 0.75 | 17 | 5288 |
| 30 | [Savanna flyby](#savanna-flyby) | Chunks | 4568.1 | 501.0 | 134.6 | 0.99 | 0.73 | 21 | 2048 |
| 31 | [Swamp flyby](#swamp-flyby) | Chunks | 4139.9 | 470.5 | 133.3 | 1.08 | 0.70 | 20 | 5280 |
| 32 | [Cherry grove flyby](#cherry-grove-flyby) | Chunks | 4575.5 | 500.2 | 138.8 | 1.01 | 0.81 | 20 | 4238 |
| 33 | [Badlands flyby](#badlands-flyby) | Chunks | 4601.9 | 545.7 | 147.3 | 0.93 | 0.64 | 22 | 3792 |
| 34 | [Dark forest flyby (dense canopy)](#dark-forest-flyby-dense-canopy) | Chunks | 4090.1 | 352.8 | 107.1 | 1.15 | 0.68 | 21 | 3844 |
| 35 | [Windswept hills flyby](#windswept-hills-flyby) | Chunks | 4358.1 | 523.8 | 149.2 | 1.02 | 0.69 | 20 | 4266 |
| 36 | [Idle baseline (orbit, flat world)](#idle-baseline-orbit-flat-world) | Baseline | 5592.7 | 1479.9 | 888.6 | 0.47 | 0.53 | 10 | 3956 |
| 37 | [Particle diversity stress (16 types simultaneously)](#particle-diversity-stress-16-types-simultaneously) | Stress | 439.5 | 220.7 | 193.1 | 4.31 | 1.71 | 8 | 1504 |
| 38 | [Fluid spread (water basin, 4-step periodic reset)](#fluid-spread-water-basin-4-step-periodic-reset) | Fluids | 5208.1 | 1235.1 | 808.8 | 0.62 | 0.54 | 4 | 5846 |
| 39 | [Lighting update (16×16 glowstone reveal/hide)](#lighting-update-1616-glowstone-revealhide) | Lighting | 5695.5 | 1383.0 | 750.7 | 0.48 | 0.57 | 4 | 6418 |
| 40 | [Hopper grid 20×20 (transfer storm)](#hopper-grid-2020-transfer-storm) | Block-Entities | 4578.5 | 1467.7 | 1064.8 | 0.50 | 0.51 | 4 | 1678 |
| 41 | [Comparator storage (8×8 chests + comparators)](#comparator-storage-88-chests--comparators) | Block-Entities | 3343.1 | 1061.7 | 595.2 | 0.66 | 0.53 | 3 | 4634 |

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

Category: **Particles**  |  Duration: 23110 ms  |  Sample ticks: 400

**FPS**  avg `4222.34`, min `280.63`, p50 `4275.33`, p95 `5170.63`, p99 `5282.62`, 1%low `1116.73`, 0.1%low `677.06`, std `745.44`

**Frame time (ms)**  avg `0.25`, p50 `0.23`, p95 `0.36`, p99 `0.55`, p99.9 `1.24`, max `3.56`

**Client tick (ms)**  avg `0.81`, p95 `0.98`, max `2.85`

**Memory**  start `1684 MB`, end `5624 MB`, peak `5624 MB`, GC `6 events / 556 ms`

**FPS over sampling window (ASCII):**

```
4991.8 |                                              ██    █                           
4840.9 |                                         █    █████ █                           
4690.1 |                   █      █  ███  █ ██   █  █ ███████                           
4539.2 |                   █  █   █  ████ █ ███ ██  █████████                           
4388.3 |█ ██     █  ██ █████ ██ ██████████████████████████████                          
4237.4 |██████████ ███████████████████████████████████████████                          
4086.5 |██████████████████████████████████████████████████████                          
3935.6 |██████████████████████████████████████████████████████ ██        ███            
3784.7 |█████████████████████████████████████████████████████████████ ██████         █ █
3633.8 |████████████████████████████████████████████████████████████████████         ███
3483.0 |████████████████████████████████████████████████████████████████████   █ ██ ████
3332.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20918
   1 ms |   81
   3 ms |   1
```

**Per-particle stage breakdown:**

> _Approximate split: frames are bucketed evenly across stages by index, not aligned to the actual tick boundary. Use as a relative comparison, not an absolute per-stage measurement._

| Stage | Particles spawned | Frames captured | Avg FPS | p99 frame ms |
|---|---:|---:|---:|---:|
| `smoke` | 160 | 2625 | 4290.9 | 0.53 |
| `flame` | 160 | 2625 | 4368.8 | 0.53 |
| `end_rod` | 240 | 2625 | 4424.9 | 0.51 |
| `sculk_charge_pop` | 240 | 2625 | 4573.0 | 0.50 |
| `portal` | 160 | 2625 | 4695.5 | 0.49 |
| `dripping_water` | 240 | 2625 | 4181.9 | 0.55 |
| `ALL_TOGETHER` | 1680 | 2625 | 3713.6 | 0.58 |
| `dragon_breath` | 160 | 2625 | 3530.1 | 0.65 |

**Extras:**

- `preload_duration_ms` = `0.00`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `4010.18`
- `fps_1pct_low` = `1116.73`
- `entity_count_sample_end` = `1.00`
- `particles_stage_smoke` = `160.00`
- `preset_full` = `0.00`
- `entity_count_delta` = `0.00`
- `particle_stage_count` = `8.00`
- `particles_stage_flame` = `160.00`
- `preload_chunks` = `81.00`
- `particles_stage_end_rod` = `240.00`
- `particles_total` = `3040.00`
- `particles_stage_sculk_charge_pop` = `240.00`
- `entity_count_sample_start` = `1.00`
- `particles_stage_portal` = `160.00`
- `particles_stage_dripping_water` = `240.00`
- `particles_stage_ALL_TOGETHER` = `1680.00`
- `particle_stage_ticks` = `50.00`
- `seed` = `2503.00`
- `fps_0p1pct_low` = `677.06`
- `preset_long` = `0.00`
- `particles_stage_dragon_breath` = `160.00`

### Cows ×200 ring (`entity_cows`)

Category: **Entities**  |  Duration: 23106 ms  |  Sample ticks: 400

**FPS**  avg `2233.17`, min `531.91`, p50 `2293.58`, p95 `2437.24`, p99 `2469.14`, 1%low `728.59`, 0.1%low `602.05`, std `263.58`

**Frame time (ms)**  avg `0.46`, p50 `0.44`, p95 `0.59`, p99 `0.99`, p99.9 `1.58`, max `1.88`

**Client tick (ms)**  avg `0.86`, p95 `0.92`, max `1.12`

**Memory**  start `3164 MB`, end `7170 MB`, peak `7170 MB`, GC `0 events / 0 ms`

**FPS over sampling window (ASCII):**

```
2319.0 |                █                 █                                █            
2296.3 | █    █ █ █     █         █   █   █ █      █      ██              ██            
2273.6 | ██   █ █ █  ████   █ █   █   █   █ █     ██   █ ███         ██ █ ██            
2250.8 |████ ████ █████████ █████ ██ ██   █ ███ █ ██   █ ████      █ ████ ████          
2228.1 |███████████████████ ████████ ██  ██████ █ ██ ████████   █████████ █████         
2205.4 |███████████████████ ███████████  ██████ █ ████████████ ████████████████         
2182.6 |███████████████████ ███████████████████████████████████████████████████         
2159.9 |███████████████████████████████████████████████████████████████████████         
2137.2 |███████████████████████████████████████████████████████████████████████ █      █
2114.4 |███████████████████████████████████████████████████████████████████████ █      █
2091.7 |███████████████████████████████████████████████████████████████████████ █████  █
2069.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20792
   1 ms |   208
```

**Extras:**

- `preload_chunks` = `81.00`
- `preset_full` = `0.00`
- `fps_1pct_low` = `728.59`
- `seed` = `6121.00`
- `preset_long` = `0.00`
- `preload_duration_ms` = `42.00`
- `entity_count_sample_start` = `201.00`
- `entities_spawned` = `200.00`
- `fps_0p1pct_low` = `602.05`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `2174.83`
- `entity_count_delta` = `0.00`
- `entity_count_sample_end` = `201.00`

### Sheep ×200 ring (`entity_sheep`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1394.96`, min `343.18`, p50 `1433.49`, p95 `1505.57`, p99 `1519.53`, 1%low `597.69`, 0.1%low `464.56`, std `144.26`

**Frame time (ms)**  avg `0.73`, p50 `0.70`, p95 `0.89`, p99 `1.53`, p99.9 `1.88`, max `2.91`

**Client tick (ms)**  avg `0.80`, p95 `0.87`, max `2.02`

**Memory**  start `4390 MB`, end `3796 MB`, peak `6146 MB`, GC `6 events / 1010 ms`

**FPS over sampling window (ASCII):**

```
1446.7 |                                                        █                       
1433.9 |                                           █            █    █     █    █       
1421.2 |    ██               █     ██   █    █     █         ████    █   █ ████ █    █  
1408.4 | █  ██     █         █     ██ █ ██ █ █    █████ █    █████   █   ████████    ██ 
1395.7 |█████████ ██         █    ██████████ ██ █████████  ████████ ██   ███████████████
1382.9 |█████████ ██        ███ ███████████████ █████████  ████████ ██  ████████████████
1370.1 |███████████████  █ ████████████████████ █████████ ████████████  ████████████████
1357.4 |███████████████  █ ██████████████████████████████ ████████████  ████████████████
1344.6 |███████████████  █ ██████████████████████████████ ████████████  ████████████████
1331.9 |███████████████ ██ ██████████████████████████████ █████████████ ████████████████
1319.1 |█████████████████████████████████████████████████ ██████████████████████████████
1306.4 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20391
   1 ms | █  599
   2 ms |   10
```

**Extras:**

- `preload_chunks` = `81.00`
- `preset_full` = `0.00`
- `fps_1pct_low` = `597.69`
- `seed` = `6133.00`
- `preset_long` = `0.00`
- `preload_duration_ms` = `45.00`
- `entity_count_sample_start` = `201.00`
- `entities_spawned` = `200.00`
- `fps_0p1pct_low` = `464.56`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `1370.12`
- `entity_count_delta` = `0.00`
- `entity_count_sample_end` = `201.00`

### Zombies ×150 ring (AI on) (`entity_zombies`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1240.72`, min `435.16`, p50 `1275.84`, p95 `1352.27`, p99 `1372.31`, 1%low `550.20`, 0.1%low `478.89`, std `130.74`

**Frame time (ms)**  avg `0.82`, p50 `0.78`, p95 `1.02`, p99 `1.67`, p99.9 `2.01`, max `2.30`

**Client tick (ms)**  avg `0.83`, p95 `0.89`, max `1.30`

**Memory**  start `7472 MB`, end `5240 MB`, peak `7818 MB`, GC `4 events / 410 ms`

**FPS over sampling window (ASCII):**

```
1310.9 |                                                          █                     
1297.4 |                                                       ████                     
1283.9 |                         █           █  ██          █  ████                     
1270.4 |                        ███  ██     ██  ███         ███████      ██             
1257.0 |                       ████ ███     ██  ███  ███   ████████      ██             
1243.5 |           █  █ ██  ██ ████ ███  █████  ███ █████████████████   ███       █ █  █
1230.0 |      █   ████████  ██ ████████ ███████████████████████████████ ████      ███  █
1216.5 |█    ███ ████████████████████████████████████████████████████████████   ████████
1203.0 |█ █  ████████████████████████████████████████████████████████████████   ████████
1189.5 |█ ██ ████████████████████████████████████████████████████████████████  █████████
1176.0 |████ ███████████████████████████████████████████████████████████████████████████
1162.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  19857
   1 ms | ██  1121
   2 ms |   22
```

**Extras:**

- `preload_chunks` = `81.00`
- `preset_full` = `0.00`
- `fps_1pct_low` = `550.20`
- `seed` = `6151.00`
- `preset_long` = `0.00`
- `preload_duration_ms` = `51.00`
- `entity_count_sample_start` = `151.00`
- `entities_spawned` = `150.00`
- `fps_0p1pct_low` = `478.89`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `1218.54`
- `entity_count_delta` = `0.00`
- `entity_count_sample_end` = `151.00`

### Pigs ×250 ring (`entity_pigs`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `2207.79`, min `438.90`, p50 `2286.76`, p95 `2386.63`, p99 `2419.55`, 1%low `750.41`, 0.1%low `606.01`, std `246.53`

**Frame time (ms)**  avg `0.46`, p50 `0.44`, p95 `0.57`, p99 `1.08`, p99.9 `1.55`, max `2.28`

**Client tick (ms)**  avg `0.81`, p95 `0.85`, max `0.93`

**Memory**  start `2308 MB`, end `6666 MB`, peak `6666 MB`, GC `0 events / 0 ms`

**FPS over sampling window (ASCII):**

```
2299.9 |                                                              █                 
2279.6 |   █ █  █     █                                               █                 
2259.4 |█ ████  █  █  █                          █  ██         █    █ ███ █        █    
2239.1 |█ ████  █ ██  █     ███    ██ █          █  ██  █  █   █    █ ██████  █    █    
2218.8 |██████ ██ ██  █     ████ ████ █          █  ██ ███ ██  █ █  ████████ ████  █   █
2198.5 |█████████ ███████   ███████████ █        █  ██████ ██  ████ █████████████  █ █ █
2178.3 |█████████ ███████ █ █████████████        ██ ██████ ███ ███████████████████ █ █ █
2158.0 |██████████████████████████████████       ██ ██████ ███████████████████████████ █
2137.7 |██████████████████████████████████ █    ████████████████████████████████████████
2117.5 |██████████████████████████████████ █ █  ████████████████████████████████████████
2097.2 |██████████████████████████████████ ███  ████████████████████████████████████████
2076.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20782
   1 ms |   217
   2 ms |   1
```

**Extras:**

- `preload_chunks` = `81.00`
- `preset_full` = `0.00`
- `fps_1pct_low` = `750.41`
- `seed` = `6163.00`
- `preset_long` = `0.00`
- `preload_duration_ms` = `26.00`
- `entity_count_sample_start` = `251.00`
- `entities_spawned` = `250.00`
- `fps_0p1pct_low` = `606.01`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `2155.69`
- `entity_count_delta` = `0.00`
- `entity_count_sample_end` = `251.00`

### Villagers ×100 ring (`entity_villagers`)

Category: **Entities**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `1784.90`, min `428.12`, p50 `1836.21`, p95 `1933.11`, p99 `1953.13`, 1%low `745.08`, 0.1%low `608.08`, std `189.80`

**Frame time (ms)**  avg `0.57`, p50 `0.54`, p95 `0.71`, p99 `1.25`, p99.9 `1.53`, max `2.34`

**Client tick (ms)**  avg `0.68`, p95 `0.72`, max `1.11`

**Memory**  start `2594 MB`, end `6772 MB`, peak `6772 MB`, GC `0 events / 0 ms`

**FPS over sampling window (ASCII):**

```
1845.0 |      █      █     ██      █     █                                              
1826.1 |      █    █ █ ██  ██    █ █     █    █              █           █     █        
1807.3 |      █  █ ██████████   ██ ██ █  █    █    ██  █     ██     █   ██     █        
1788.5 | █ █ ██ ██ ██████████   ███████████   █    ██ ██     ███ █  ███ ██ ██ ██ ██ ███ 
1769.7 |█████████████████████ █████████████ █ █ ██ ██████    ███ █ ██████████ ██ ███████
1750.9 |█████████████████████ █████████████ ███ █████████    ███ ███████████████████████
1732.1 |█████████████████████ █████████████████ █████████   ████ ███████████████████████
1713.3 |█████████████████████████████████████████████████  █████████████████████████████
1694.5 |█████████████████████████████████████████████████  █████████████████████████████
1675.7 |█████████████████████████████████████████████████ ██████████████████████████████
1656.9 |█████████████████████████████████████████████████ ██████████████████████████████
1638.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20703
   1 ms | █  296
   2 ms |   1
```

**Extras:**

- `preload_chunks` = `81.00`
- `preset_full` = `0.00`
- `fps_1pct_low` = `745.08`
- `seed` = `6173.00`
- `preset_long` = `0.00`
- `preload_duration_ms` = `20.00`
- `entity_count_sample_start` = `101.00`
- `entities_spawned` = `100.00`
- `fps_0p1pct_low` = `608.08`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `1751.64`
- `entity_count_delta` = `0.00`
- `entity_count_sample_end` = `101.00`

### Chickens ×300 ring (`entity_chickens`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1720.63`, min `380.08`, p50 `1768.66`, p95 `1893.60`, p99 `1924.56`, 1%low `660.09`, 0.1%low `535.57`, std `195.95`

**Frame time (ms)**  avg `0.59`, p50 `0.57`, p95 `0.75`, p99 `1.40`, p99.9 `1.72`, max `2.63`

**Client tick (ms)**  avg `0.82`, p95 `0.88`, max `1.08`

**Memory**  start `3680 MB`, end `7778 MB`, peak `7778 MB`, GC `1 events / 0 ms`

**FPS over sampling window (ASCII):**

```
1783.1 |                            █                          █                        
1766.2 |                      █     █               █      █   █          ██  █     █   
1749.3 |                    █ █     █            █ ██     ██   ██ █     █ ██████    ██  
1732.3 |█     █    █      █ █ ████  ██   █    █  ██████ ████ ██████  █ ██ ██████    ██ █
1715.4 |█  █ ████ ███  █  ███ ████  ██ █ █    █ ████████████████████ ████ ██████  █ ██ █
1698.5 |█  ██████████████ █████████ ██████    ██████████████████████████████████  █ ██ █
1681.6 |█ █████████████████████████████████  ████████████████████████████████████ ████ █
1664.7 |██████████████████████████████████████████████████████████████████████████████ █
1647.8 |██████████████████████████████████████████████████████████████████████████████ █
1630.9 |██████████████████████████████████████████████████████████████████████████████ █
1614.0 |██████████████████████████████████████████████████████████████████████████████ █
1597.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20658
   1 ms | █  338
   2 ms |   4
```

**Extras:**

- `preload_chunks` = `81.00`
- `preset_full` = `0.00`
- `fps_1pct_low` = `660.09`
- `seed` = `6197.00`
- `preset_long` = `0.00`
- `preload_duration_ms` = `36.00`
- `entity_count_sample_start` = `301.00`
- `entities_spawned` = `300.00`
- `fps_0p1pct_low` = `535.57`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `1682.31`
- `entity_count_delta` = `0.00`
- `entity_count_sample_end` = `301.00`

### Item entities ×500 (`entity_items`)

Category: **Entities**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `909.82`, min `225.93`, p50 `935.72`, p95 `980.20`, p99 `997.01`, 1%low `405.26`, 0.1%low `328.91`, std `95.20`

**Frame time (ms)**  avg `1.12`, p50 `1.07`, p95 `1.33`, p99 `2.30`, p99.9 `2.65`, max `4.43`

**Client tick (ms)**  avg `1.16`, p95 `1.24`, max `2.17`

**Memory**  start `5402 MB`, end `1302 MB`, peak `7782 MB`, GC `10 events / 1469 ms`

**FPS over sampling window (ASCII):**

```
939.2 |                      █                  █                   ██                 
932.2 |                   █ ██                  █    █              ██         █       
925.1 |             █     ████ █      █         ██   █        █     ██  ██ █   ██      
918.1 |             █ █ █ ███████  █  █   █ █  ███  ███   █  ██ █   ██ ███ ███ ██      
911.1 |       █    ████ ███████████████ ███ █  ███ ████   █████████ ██ ███ ███ ████    
904.0 |       █ █  ██████████████████████████  ███ ████   █████████████████████████  █ 
897.0 |       ███ ███████████████████████████  ███ █████ ██████████████████████████  ██
890.0 |     █ ███ ██████████████████████████████████████ ███████████████████████████ ██
882.9 |█  █████████████████████████████████████████████████████████████████████████████
875.9 |█ ██████████████████████████████████████████████████████████████████████████████
868.9 |█ ██████████████████████████████████████████████████████████████████████████████
861.8 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms |   139
   1 ms | ████████████████████████████████████████  17308
   2 ms | █  403
   3 ms |   6
   4 ms |   1
```

**Extras:**

- `preload_chunks` = `81.00`
- `preset_full` = `0.00`
- `fps_1pct_low` = `405.26`
- `seed` = `6203.00`
- `preset_long` = `0.00`
- `preload_duration_ms` = `49.00`
- `entity_count_sample_start` = `501.00`
- `entities_spawned` = `500.00`
- `fps_0p1pct_low` = `328.91`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `892.86`
- `entity_count_delta` = `0.00`
- `entity_count_sample_end` = `501.00`

### XP orbs ×500 ring (`entity_xp`)

Category: **Entities**  |  Duration: 23106 ms  |  Sample ticks: 400

**FPS**  avg `1778.78`, min `183.13`, p50 `1836.21`, p95 `1922.71`, p99 `1940.99`, 1%low `258.84`, 0.1%low `234.05`, std `222.34`

**Frame time (ms)**  avg `0.60`, p50 `0.54`, p95 `0.70`, p99 `3.72`, p99.9 `4.03`, max `5.46`

**Client tick (ms)**  avg `3.17`, p95 `3.36`, max `4.01`

**Memory**  start `5812 MB`, end `1484 MB`, peak `7562 MB`, GC `8 events / 568 ms`

**FPS over sampling window (ASCII):**

```
1846.0 |   █                                                                  █         
1828.1 |  ███ ██         █ █                                  █   █      █  ███  █    █ 
1810.2 |█ ███████ █      ███  ██                              █  ███     ██████  █    █ 
1792.3 |████████████    ████  ██              █ █ █     █     ██ ██████  ██████ ██    ██
1774.4 |█████████████ ███████ ██            █ █ █ ██   ███    █████████ ███████ ███   ██
1756.5 |█████████████████████ ██       █  █ ████████   ████████████████████████ ███   ██
1738.6 |█████████████████████ ███   ███████ ████████  ██████████████████████████████ ███
1720.7 |█████████████████████ ███   █████████████████ ██████████████████████████████ ███
1702.8 |██████████████████████████  ████████████████████████████████████████████████ ███
1684.9 |██████████████████████████  ████████████████████████████████████████████████ ███
1667.0 |███████████████████████████ ████████████████████████████████████████████████████
1649.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20694
   1 ms |   53
   3 ms |   225
   4 ms |   27
   5 ms |   1
```

**Extras:**

- `preload_chunks` = `81.00`
- `preset_full` = `0.00`
- `fps_1pct_low` = `258.84`
- `seed` = `6217.00`
- `preset_long` = `0.00`
- `preload_duration_ms` = `49.00`
- `entity_count_sample_start` = `501.00`
- `entities_spawned` = `500.00`
- `fps_0p1pct_low` = `234.05`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `1664.27`
- `entity_count_delta` = `0.00`
- `entity_count_sample_end` = `501.00`

### Item merge storm (cobblestone, mergeable) (`entity_items_merge_storm`)

Category: **Entities**  |  Duration: 23109 ms  |  Sample ticks: 400

**FPS**  avg `617.84`, min `104.34`, p50 `621.76`, p95 `872.37`, p99 `920.78`, 1%low `119.87`, 0.1%low `111.20`, std `162.83`

**Frame time (ms)**  avg `1.83`, p50 `1.61`, p95 `2.44`, p99 `7.95`, p99.9 `8.82`, max `9.58`

**Client tick (ms)**  avg `4.42`, p95 `6.26`, max `7.15`

**Memory**  start `5894 MB`, end `6730 MB`, peak `7712 MB`, GC `10 events / 1203 ms`

**FPS over sampling window (ASCII):**

```
868.9 |  █                                                                             
829.2 |█████████                                                                       
789.5 |█████████ ██  ███                                                               
749.9 |█████████████████                                                               
710.2 |████████████████████████ ██  ██                                                 
670.5 |███████████████████████████████ █  ███                                          
630.8 |█████████████████████████████████████████ ██                                    
591.2 |█████████████████████████████████████████████ ████                              
551.5 |████████████████████████████████████████████████████                            
511.8 |█████████████████████████████████████████████████████████████                   
472.1 |██████████████████████████████████████████████████████████████████       █   █ █
432.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   1 ms | ████████████████████████████████████████  7995
   2 ms | █████████████  2536
   3 ms |   44
   4 ms |   85
   5 ms |   69
   6 ms |   58
   7 ms |   62
   8 ms |   94
   9 ms |   3
```

**Extras:**

- `items_spawned` = `1560.00`
- `fps_harmonic_avg` = `547.28`
- `fps_0p1pct_low` = `111.20`
- `seed` = `6287.00`
- `preload_duration_ms` = `43.00`
- `entity_count_sample_end` = `1561.00`
- `preset_long` = `0.00`
- `entity_count_delta` = `880.00`
- `entity_count_sample_start` = `681.00`
- `waves_spawned` = `12.00`
- `items_alive_avg` = `1230.00`
- `items_alive_p50` = `1240.00`
- `items_alive_max` = `1560.00`
- `fps_1pct_low` = `119.87`
- `preset_full` = `0.00`
- `items_alive_p95` = `1560.00`
- `items_merged_estimate` = `0.00`
- `preload_chunks` = `81.00`
- `preset_quick` = `1.00`

### Zombies obstacle pathfinding (150 + pillar maze) (`entity_zombies_obstacle_pathfinding`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1455.07`, min `300.79`, p50 `1503.31`, p95 `1612.90`, p99 `1633.46`, 1%low `574.00`, 0.1%low `420.66`, std `178.29`

**Frame time (ms)**  avg `0.70`, p50 `0.67`, p95 `0.92`, p99 `1.54`, p99.9 `2.02`, max `3.32`

**Client tick (ms)**  avg `0.83`, p95 `0.93`, max `1.64`

**Memory**  start `3914 MB`, end `2332 MB`, peak `7656 MB`, GC `4 events / 255 ms`

**FPS over sampling window (ASCII):**

```
1538.6 |                                █                                               
1510.6 |                           █  █ █    ██   ██   ███     █       █ █   █  █       
1482.5 | █         ███            ██████████████ ████ █████  █ █  █    ████  █  █ █ █   
1454.4 |██         ███████████   ██████████████████████████ ████████ ████████████████   
1426.3 |██      ██████████████████████████████████████████████████████████████████████  
1398.2 |███     ████████████████████████████████████████████████████████████████████████
1370.2 |███     ████████████████████████████████████████████████████████████████████████
1342.1 |███     ████████████████████████████████████████████████████████████████████████
1314.0 |███     ████████████████████████████████████████████████████████████████████████
1285.9 |███     ████████████████████████████████████████████████████████████████████████
1257.8 |█████  █████████████████████████████████████████████████████████████████████████
1229.8 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20224
   1 ms | █  753
   2 ms |   20
   3 ms |   3
```

**Extras:**

- `block_state_changes` = `0.00`
- `zombies_spawned` = `150.00`
- `pillars_built` = `48.00`
- `neighbour_updates` = `0.00`
- `entity_count_delta` = `0.00`
- `preset_quick` = `1.00`
- `preset_full` = `0.00`
- `seed` = `6271.00`
- `entity_count_sample_end` = `153.00`
- `fps_harmonic_avg` = `1419.13`
- `entity_count_sample_start` = `153.00`
- `fps_1pct_low` = `574.00`
- `fps_0p1pct_low` = `420.66`
- `preload_chunks` = `81.00`
- `preload_duration_ms` = `60.00`
- `preset_long` = `0.00`

### Villager AI village (80, brain on) (`villager_ai_village`)

Category: **Entities**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `1224.82`, min `288.04`, p50 `1260.88`, p95 `1347.71`, p99 `1363.70`, 1%low `603.95`, 0.1%low `441.31`, std `128.21`

**Frame time (ms)**  avg `0.83`, p50 `0.79`, p95 `1.01`, p99 `1.45`, p99.9 `1.83`, max `3.47`

**Client tick (ms)**  avg `0.60`, p95 `0.66`, max `1.41`

**Memory**  start `5988 MB`, end `4206 MB`, peak `7264 MB`, GC `6 events / 1027 ms`

**FPS over sampling window (ASCII):**

```
1299.0 |                                 █                                         █    
1274.2 |                            ██████ █ █    █ ███████ █  █ █ ██ ████  ██ ██ ████  
1249.5 |                          █████████████████ ██████████████████████  ████████████
1224.7 |                      █  █████████████████████████████████████████  ████████████
1199.9 |                    ██████████████████████████████████████████████ █████████████
1175.2 |                 ███████████████████████████████████████████████████████████████
1150.4 |    █   ████     ███████████████████████████████████████████████████████████████
1125.6 |   ██████████  █████████████████████████████████████████████████████████████████
1100.9 |  ████████████ █████████████████████████████████████████████████████████████████
1076.1 |  ██████████████████████████████████████████████████████████████████████████████
1051.3 | ███████████████████████████████████████████████████████████████████████████████
1026.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  19914
   1 ms | ██  1073
   2 ms |   9
   3 ms |   4
```

**Extras:**

- `beds_placed` = `40.00`
- `neighbour_updates` = `0.00`
- `fps_harmonic_avg` = `1205.77`
- `villagers_spawned` = `80.00`
- `fps_0p1pct_low` = `441.31`
- `seed` = `6299.00`
- `preload_duration_ms` = `23.00`
- `entity_count_sample_end` = `81.00`
- `preset_long` = `0.00`
- `entity_count_delta` = `0.00`
- `entity_count_sample_start` = `81.00`
- `doors_placed` = `16.00`
- `block_state_changes` = `94.00`
- `fps_1pct_low` = `603.95`
- `scheduled_block_ticks` = `0.00`
- `preset_full` = `0.00`
- `preload_chunks` = `81.00`
- `preset_quick` = `1.00`
- `workstations_placed` = `40.00`

### TNT field (14×14 staggered fuses) (`tnt_field`)

Category: **Physics**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `4379.53`, min `662.30`, p50 `4428.70`, p95 `5112.47`, p99 `5246.59`, 1%low `1266.95`, 0.1%low `893.64`, std `566.02`

**Frame time (ms)**  avg `0.24`, p50 `0.23`, p95 `0.29`, p99 `0.52`, p99.9 `1.02`, max `1.51`

**Client tick (ms)**  avg `0.82`, p95 `1.66`, max `1.86`

**Memory**  start `1632 MB`, end `7680 MB`, peak `7680 MB`, GC `1 events / 0 ms`

**FPS over sampling window (ASCII):**

```
5077.3 |                                                                          █    █
4970.9 |                                                                         ███ ███
4864.5 |                                                                      █  ███ ███
4758.2 |                                                            █      █ ██  ███████
4651.8 |                                                        █ ████    ██████████████
4545.4 |                                                    █  █████████████████████████
4439.0 |                                        █  █ ██ █ ██████████████████████████████
4332.6 |                ██           █         ██ █████ ████████████████████████████████
4226.2 |           █ ██ █████      ███ ██ ████ █████████████████████████████████████████
4119.8 | █ █ █  █ ████████████     █████████████████████████████████████████████████████
4013.4 |██ ██████ ████████████ █ ███████████████████████████████████████████████████████
3907.0 |████████████████████████████████████████████████████████████████████████████████
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
- `seed` = `3539.00`
- `tnt_active_avg` = `36.13`
- `fps_1pct_low` = `1266.95`
- `preset_long` = `0.00`
- `preset_quick` = `1.00`
- `neighbour_updates` = `0.00`
- `block_state_changes` = `0.00`
- `waves_spawned` = `13.00`
- `entity_count_delta` = `-187.00`
- `explosions_count` = `403.00`
- `preload_duration_ms` = `1.00`
- `fps_0p1pct_low` = `893.64`
- `preset_full` = `0.00`
- `tnt_active_p95` = `150.00`
- `fps_harmonic_avg` = `4240.95`
- `tnt_active_p50` = `25.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_start` = `188.00`
- `section_rebuilds` = `0.00`
- `tnt_active_max` = `205.00`
- `tnt_spawned` = `430.00`

### TNT field destructive (breaks terrain) (`tnt_field_destructive`)

Category: **Physics**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `4275.24`, min `466.37`, p50 `4359.20`, p95 `5032.71`, p99 `5144.03`, 1%low `1127.65`, 0.1%low `765.80`, std `628.72`

**Frame time (ms)**  avg `0.24`, p50 `0.23`, p95 `0.31`, p99 `0.57`, p99.9 `1.10`, max `2.14`

**Client tick (ms)**  avg `0.88`, p95 `1.67`, max `1.92`

**Memory**  start `5490 MB`, end `1760 MB`, peak `7558 MB`, GC `10 events / 1294 ms`

**FPS over sampling window (ASCII):**

```
4954.4 |                                                                   █  █        █
4840.8 |                                                                 ███ ███       █
4727.3 |                                                          █ ███  ████████     ██
4613.7 |██                                                    ███ █ █████████████ ██████
4500.2 |██                                                    ██████████████████████████
4386.6 |██                                                 ██ ██████████████████████████
4273.1 |███       █         █                              █████████████████████████████
4159.5 |███     █ ███  ██ ███ █           ██       █ █  █  █████████████████████████████
4046.0 |███ ███ █████  ████████         █████  ██  █ █████ █████████████████████████████
3932.4 |█████████████████████████    █  ████████████████████████████████████████████████
3818.9 |█████████████████████████ █  █ █████████████████████████████████████████████████
3705.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20915
   1 ms |   84
   2 ms |   1
```

**Extras:**

- `entity_count_sample_end` = `84.00`
- `seed` = `3541.00`
- `tnt_active_avg` = `36.59`
- `fps_1pct_low` = `1127.65`
- `preset_long` = `0.00`
- `preset_quick` = `1.00`
- `neighbour_updates` = `0.00`
- `block_state_changes` = `7396.00`
- `waves_spawned` = `13.00`
- `entity_count_delta` = `-117.00`
- `explosions_count` = `404.00`
- `preload_duration_ms` = `1.00`
- `fps_0p1pct_low` = `765.80`
- `preset_full` = `0.00`
- `tnt_active_p95` = `149.00`
- `fps_harmonic_avg` = `4104.09`
- `tnt_active_p50` = `26.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_start` = `201.00`
- `section_rebuilds` = `0.00`
- `tnt_active_max` = `206.00`
- `tnt_spawned` = `430.00`

### Falling sand wall 40×40 (heavy) (`falling_sand`)

Category: **Physics**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `2792.51`, min `27.47`, p50 `3233.11`, p95 `4458.31`, p99 `6049.61`, 1%low `33.59`, 0.1%low `29.71`, std `1409.99`

**Frame time (ms)**  avg `1.69`, p50 `0.31`, p95 `8.21`, p99 `27.44`, p99.9 `31.85`, max `36.41`

**Client tick (ms)**  avg `15.95`, p95 `22.24`, max `27.24`

**Memory**  start `6166 MB`, end `2330 MB`, peak `7702 MB`, GC `8 events / 534 ms`

**FPS over sampling window (ASCII):**

```
5883.8 |                                                                              ██
5371.5 |                                                                              ██
4859.2 |                                                                             ███
4346.8 |                                                                    █   █    ███
3834.5 |   █                                                              ████████  ████
3322.2 |██████████   █  █     █   █         █   █ █   █    ██    ██████  █████████  ████
2809.9 |███████████  █  █  █  ██  ███ ███  ███ ████  ████  ████  ███████ █████████  ████
2297.5 |███████████  █ ██  █  ███ ███ ███  ███ ████  ████  ████  ███████ ██████████ ████
1785.2 |███████████  █ ██ ███ ███ ███ ████ ███ ████ ██████ █████ ███████ ██████████ ████
1272.9 |███████████ ██ ██ ███ ███ ███ ████ ███ █████████████████████████ ██████████ ████
760.6 |█████████████████████████ ███ ██████████████████████████████████ ██████████ ████
248.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  9827
   1 ms | █  340
   2 ms | █  243
   3 ms | █  154
   4 ms |   106
   5 ms |   109
   6 ms |   114
   7 ms | █  296
   8 ms | █  236
   9 ms |   33
  10 ms |   14
  11 ms |   9
  12 ms |   5
  13 ms |   7
  14 ms |   7
  15 ms |   17
  16 ms |   46
  17 ms |   22
  18 ms |   25
  19 ms |   20
  20 ms |   18
  21 ms |   14
  22 ms |   27
  23 ms |   25
  24 ms |   11
  25 ms |   5
  26 ms |   7
  27 ms |   9
  28 ms |   42
  29 ms |   25
  30 ms |   24
  31 ms |   9
  32 ms |   7
  34 ms |   3
  35 ms |   1
  36 ms |   1
```

**Extras:**

- `variant` = `heavy`
- `preload_duration_ms` = `23.00`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `592.90`
- `fps_1pct_low` = `33.59`
- `falling_blocks_alive_avg` = `4806.17`
- `block_state_changes` = `36466.00`
- `entity_count_sample_end` = `104.00`
- `preset_full` = `0.00`
- `falling_blocks_alive_p95` = `6400.00`
- `waves_spawned` = `12.00`
- `entity_count_delta` = `-3097.00`
- `wave_interval_ticks` = `30.00`
- `preload_chunks` = `81.00`
- `falling_blocks_alive_p50` = `4800.00`
- `neighbour_updates` = `0.00`
- `entity_count_sample_start` = `3201.00`
- `sand_spawned` = `20800.00`
- `seed` = `5077.00`
- `topup_blocks_per_wave` = `1600.00`
- `fps_0p1pct_low` = `29.71`
- `falling_blocks_landed` = `20800.00`
- `preset_long` = `0.00`
- `falling_blocks_alive_max` = `6400.00`

### Falling gravel mixed heavy (sand+gravel+concrete) (`falling_gravel_mixed`)

Category: **Physics**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `2724.94`, min `28.68`, p50 `3183.70`, p95 `4264.39`, p99 `5184.03`, 1%low `34.16`, 0.1%low `30.57`, std `1317.17`

**Frame time (ms)**  avg `1.67`, p50 `0.31`, p95 `8.21`, p99 `26.34`, p99.9 `31.38`, max `34.87`

**Client tick (ms)**  avg `15.95`, p95 `21.75`, max `24.84`

**Memory**  start `6840 MB`, end `1242 MB`, peak `7860 MB`, GC `14 events / 1533 ms`

**FPS over sampling window (ASCII):**

```
5022.5 |                                                                             ███
4589.7 |                                                                             ███
4156.8 |                                                                       ██    ███
3724.0 | █                                                        █ █ █    ███████   ███
3291.1 |███████████         █  ██        █        █         ██    ███████  ███████  ████
2858.3 |███████████     █  ██  ██  ██  ███  ███  ███  ████  ████  ███████  ███████  ████
2425.4 |████████████    ██ ██  ███ ███ ███  ███  ███  ████  ████  ███████ █████████ ████
1992.6 |████████████ █  ██ ██  ███ ███ ████ ███  ████ ████ █████ ████████ █████████ ████
1559.7 |████████████ █ ███ ███ ███ ███ ████ ███ █████ ████ █████ ████████ █████████ ████
1126.9 |████████████ █████ ███████ ███ ████ ██████████████ ██████████████ █████████ ████
694.0 |████████████ █████ ███████ ███ ████████████████████████████████████████████ ████
261.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  9995
   1 ms | ██  384
   2 ms | █  195
   3 ms | █  155
   4 ms |   101
   5 ms |   108
   6 ms |   84
   7 ms | █  297
   8 ms | █  229
   9 ms |   43
  10 ms |   19
  11 ms |   16
  12 ms |   3
  13 ms |   5
  14 ms |   6
  15 ms |   14
  16 ms |   60
  17 ms |   19
  18 ms |   19
  19 ms |   23
  20 ms |   18
  21 ms |   23
  22 ms |   25
  23 ms |   24
  24 ms |   14
  25 ms |   4
  26 ms |   5
  27 ms |   22
  28 ms |   34
  29 ms |   31
  30 ms |   13
  31 ms |   10
  32 ms |   4
  33 ms |   1
  34 ms |   2
```

**Extras:**

- `variant` = `heavy`
- `preload_duration_ms` = `5.00`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `600.25`
- `fps_1pct_low` = `34.16`
- `falling_blocks_alive_avg` = `4803.02`
- `block_state_changes` = `36466.00`
- `entity_count_sample_end` = `210.00`
- `preset_full` = `0.00`
- `falling_blocks_alive_p95` = `6400.00`
- `waves_spawned` = `12.00`
- `entity_count_delta` = `-2991.00`
- `wave_interval_ticks` = `30.00`
- `preload_chunks` = `81.00`
- `falling_blocks_alive_p50` = `4800.00`
- `neighbour_updates` = `0.00`
- `entity_count_sample_start` = `3201.00`
- `sand_spawned` = `20800.00`
- `seed` = `5081.00`
- `topup_blocks_per_wave` = `1600.00`
- `fps_0p1pct_low` = `30.57`
- `falling_blocks_landed` = `20800.00`
- `preset_long` = `0.00`
- `falling_blocks_alive_max` = `6400.00`

### Falling sand wall (lite, staggered) (`falling_sand_lite`)

Category: **Physics**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `5561.58`, min `444.01`, p50 `5711.02`, p95 `6497.73`, p99 `6640.11`, 1%low `1153.62`, 0.1%low `514.59`, std `806.60`

**Frame time (ms)**  avg `0.19`, p50 `0.18`, p95 `0.24`, p99 `0.47`, p99.9 `1.73`, max `2.25`

**Client tick (ms)**  avg `1.81`, p95 `2.31`, max `2.88`

**Memory**  start `5588 MB`, end `4580 MB`, peak `7510 MB`, GC `4 events / 244 ms`

**FPS over sampling window (ASCII):**

```
6316.7 |                                                                  █             
6183.9 |                                                                  █             
6051.0 |                                                          ██   █  █        ██   
5918.2 |                                     █   █                ███  █  █   █  █ ████ 
5785.4 |                              ██    ███  ██    █  █   █   ███  █ ██   ██ ███████
5652.5 |                     █    █   ██████████ ███   █████  █ █ ███ ██ ███ ███████████
5519.7 |         █        ██ █    █   ██████████████   ████████ █████ ██ ███ ███████████
5386.8 |  █  █   ██ █     ██ ██   ███████████████████ ██████████████████████████████████
5254.0 | ██  █ ███████  █ ███████ ██████████████████████████████████████████████████████
5121.2 | █████ ██████████████████ ██████████████████████████████████████████████████████
4988.3 |██████ █████████████████████████████████████████████████████████████████████████
4855.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20940
   1 ms |   53
   2 ms |   7
```

**Extras:**

- `variant` = `lite`
- `preload_duration_ms` = `34.00`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `5285.03`
- `fps_1pct_low` = `1153.62`
- `falling_blocks_alive_avg` = `619.12`
- `block_state_changes` = `5684.00`
- `entity_count_sample_end` = `1.00`
- `preset_full` = `0.00`
- `falling_blocks_alive_p95` = `833.00`
- `waves_spawned` = `63.00`
- `entity_count_delta` = `-441.00`
- `wave_interval_ticks` = `6.00`
- `preload_chunks` = `81.00`
- `falling_blocks_alive_p50` = `686.00`
- `neighbour_updates` = `0.00`
- `entity_count_sample_start` = `442.00`
- `sand_spawned` = `3087.00`
- `seed` = `5101.00`
- `topup_blocks_per_wave` = `49.00`
- `fps_0p1pct_low` = `514.59`
- `falling_blocks_landed` = `3087.00`
- `preset_long` = `0.00`
- `falling_blocks_alive_max` = `882.00`

### Falling gravel mixed (lite, staggered) (`falling_gravel_mixed_lite`)

Category: **Physics**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `5517.03`, min `388.23`, p50 `5717.55`, p95 `6305.17`, p99 `6381.62`, 1%low `1146.38`, 0.1%low `514.60`, std `808.68`

**Frame time (ms)**  avg `0.19`, p50 `0.17`, p95 `0.24`, p99 `0.46`, p99.9 `1.70`, max `2.58`

**Client tick (ms)**  avg `1.76`, p95 `2.18`, max `2.47`

**Memory**  start `2358 MB`, end `7718 MB`, peak `7718 MB`, GC `1 events / 0 ms`

**FPS over sampling window (ASCII):**

```
6201.0 |                                                           █                    
6087.8 |                                                █     █   ██           █ █    █ 
5974.5 |                                            █   ██   ██  ███ █  █      █ █    █ 
5861.2 |                                          ████  ██ ████  ███ █  █   █  █ █    ██
5747.9 |                                █        ██████████████  ██████ ██ ███████    ██
5634.7 |                                █        ██████████████ ███████ ██ ███████ █  ██
5521.4 |                          ██    █  █    ███████████████████████ ██ ██████████ ██
5408.1 |          █           █   ██  ███  █ █  ████████████████████████████████████████
5294.8 |   ██     █ █   █     █ ████████████ █  ████████████████████████████████████████
5181.5 |   ██  ██ █████ █ ██  █ ████████████ ███████████████████████████████████████████
5068.3 |██████ ██████████ █████ ████████████████████████████████████████████████████████
4955.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20939
   1 ms |   54
   2 ms |   7
```

**Extras:**

- `variant` = `lite`
- `preload_duration_ms` = `5.00`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `5243.33`
- `fps_1pct_low` = `1146.38`
- `falling_blocks_alive_avg` = `619.12`
- `block_state_changes` = `5684.00`
- `entity_count_sample_end` = `1.00`
- `preset_full` = `0.00`
- `falling_blocks_alive_p95` = `833.00`
- `waves_spawned` = `63.00`
- `entity_count_delta` = `-441.00`
- `wave_interval_ticks` = `6.00`
- `preload_chunks` = `81.00`
- `falling_blocks_alive_p50` = `686.00`
- `neighbour_updates` = `0.00`
- `entity_count_sample_start` = `442.00`
- `sand_spawned` = `3087.00`
- `seed` = `5113.00`
- `topup_blocks_per_wave` = `49.00`
- `fps_0p1pct_low` = `514.60`
- `falling_blocks_landed` = `3087.00`
- `preset_long` = `0.00`
- `falling_blocks_alive_max` = `882.00`

### Projectile storm (arrows + snowballs) (`projectile_storm`)

Category: **Physics**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `2151.42`, min `516.45`, p50 `2163.57`, p95 `2533.57`, p99 `2574.67`, 1%low `730.58`, 0.1%low `567.63`, std `266.23`

**Frame time (ms)**  avg `0.48`, p50 `0.46`, p95 `0.58`, p99 `1.08`, p99.9 `1.61`, max `1.94`

**Client tick (ms)**  avg `0.77`, p95 `0.86`, max `1.37`

**Memory**  start `5390 MB`, end `7350 MB`, peak `7350 MB`, GC `6 events / 903 ms`

**FPS over sampling window (ASCII):**

```
2451.5 |                                                                   █ █    █     
2405.2 |                                                                 █ █ █ █ ██  ██ 
2358.9 |                                                               ███████ █████████
2312.6 |                                                             ███████████████████
2266.3 |          █                                                  ███████████████████
2219.9 |        █ █                                               ██████████████████████
2173.6 |        █ ██            █ █              ██              ███████████████████████
2127.3 |        ██████    █    ████   █          ███ █        ██████████████████████████
2081.0 |      █ ███████   █    ████████       ██████████      ██████████████████████████
2034.6 |█     ██████████████ ███████████████ ██████████████   ██████████████████████████
1988.3 |██ █ ███████████████ ███████████████████████████████ ███████████████████████████
1942.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20776
   1 ms |   224
```

**Extras:**

- `preset_quick` = `1.00`
- `entity_count_sample_end` = `251.00`
- `fps_1pct_low` = `730.58`
- `fps_harmonic_avg` = `2097.31`
- `neighbour_updates` = `0.00`
- `fps_0p1pct_low` = `567.63`
- `preload_chunks` = `81.00`
- `seed` = `5099.00`
- `preset_long` = `0.00`
- `max_in_flight_observed` = `250.00`
- `projectiles_swept` = `270.00`
- `entity_count_sample_start` = `78.00`
- `preload_duration_ms` = `23.00`
- `waves_spawned` = `40.00`
- `projectiles_spawned` = `1000.00`
- `entity_count_delta` = `173.00`
- `preset_full` = `0.00`
- `block_state_changes` = `0.00`

### Redstone clocks (6×6) (`redstone_clocks`)

Category: **Redstone**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `5461.11`, min `932.84`, p50 `5701.25`, p95 `5913.66`, p99 `5988.02`, 1%low `1561.43`, 0.1%low `1176.37`, std `705.33`

**Frame time (ms)**  avg `0.19`, p50 `0.18`, p95 `0.24`, p99 `0.49`, p99.9 `0.78`, max `1.07`

**Client tick (ms)**  avg `0.54`, p95 `0.57`, max `0.99`

**Memory**  start `4832 MB`, end `4774 MB`, peak `7770 MB`, GC `4 events / 240 ms`

**FPS over sampling window (ASCII):**

```
5729.4 |                          █ █               █                                   
5659.3 |█   █               █ █ █ █ █         █   █ ██ █   █                            
5589.2 |█   ███       █ █   █ █ ███ █ █████████ ████████ █ █                            
5519.1 |█   ████      █ █ ███ █ ████████████████████████████   █ █          █           
5449.0 |███ ████ ████ ███████ ███████████████████████████████  ███   █    █ ██    █     
5378.9 |████████ ████ ███████████████████████████████████████  ███ █ █    █ ███   █ █   
5308.8 |██████████████████████████████████████████████████████ █████ █    █ ███  ████  █
5238.7 |██████████████████████████████████████████████████████ ███████    █ ███  █████ █
5168.6 |███████████████████████████████████████████████████████████████  ██ ███  █████ █
5098.5 |███████████████████████████████████████████████████████████████ ███ ███ ████████
5028.4 |███████████████████████████████████████████████████████████████████ ████████████
4958.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20996
   1 ms |   4
```

**Extras:**

- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `1176.37`
- `preset_long` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `1.00`
- `entity_count_sample_start` = `1.00`
- `seed` = `4001.00`
- `block_state_changes` = `9612.00`
- `neighbour_updates` = `0.00`
- `entity_count_delta` = `0.00`
- `scheduled_block_ticks` = `9612.00`
- `preload_duration_ms` = `49.00`
- `fps_1pct_low` = `1561.43`
- `fps_harmonic_avg` = `5267.04`
- `preset_full` = `0.00`
- `observers_placed` = `72.00`
- `clocks_built` = `36.00`

### Redstone dust grid (16 trails ×32 + repeaters + lamps) (`redstone_dust_grid`)

Category: **Redstone**  |  Duration: 23109 ms  |  Sample ticks: 400

**FPS**  avg `5429.40`, min `121.73`, p50 `5665.72`, p95 `5827.51`, p99 `5889.28`, 1%low `1218.65`, 0.1%low `372.27`, std `682.74`

**Frame time (ms)**  avg `0.19`, p50 `0.18`, p95 `0.24`, p99 `0.47`, p99.9 `0.90`, max `8.21`

**Client tick (ms)**  avg `0.48`, p95 `0.52`, max `0.73`

**Memory**  start `2564 MB`, end `6758 MB`, peak `6758 MB`, GC `6 events / 969 ms`

**FPS over sampling window (ASCII):**

```
5613.7 |                       █    ██         █       ██              █     █   █      
5551.0 |             █    ██   █    ██         ███     ███       ███   █ █████   █ █ █  
5488.4 | █      █  █ █ █ ████  ██  ███    █  █████  ██ ███ █  █  ███   ███████   █ █ █  
5425.8 | █   █  ██ █ █ █ █████████ ██████ █  █████  ██████ █  ██ █████████████████ ███  
5363.2 | █   █  ██ █ █ █ ██████████████████  █████  ████████  █████████████████████████ 
5300.6 |██   █  ████ █ █ ███████████████████ ██████████████████████████████████████████ 
5238.0 |██   █ █████████ ███████████████████ ██████████████████████████████████████████ 
5175.4 |██  ██ ████████████████████████████████████████████████████████████████████████ 
5112.7 |██  ██ ████████████████████████████████████████████████████████████████████████ 
5050.1 |██ ███ ████████████████████████████████████████████████████████████████████████ 
4987.5 |██ █████████████████████████████████████████████████████████████████████████████
4924.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20982
   1 ms |   10
   2 ms |   4
   7 ms |   2
   8 ms |   2
```

**Extras:**

- `entity_count_sample_start` = `1.00`
- `entity_count_sample_end` = `1.00`
- `preset_long` = `0.00`
- `preset_full` = `0.00`
- `dust_placed` = `464.00`
- `trails_built` = `16.00`
- `seed` = `4019.00`
- `repeaters_placed` = `48.00`
- `lamps_placed` = `128.00`
- `entity_count_delta` = `0.00`
- `preset_quick` = `1.00`
- `scheduled_block_ticks` = `2240.00`
- `fps_0p1pct_low` = `372.27`
- `preload_chunks` = `81.00`
- `preload_duration_ms` = `55.00`
- `fps_harmonic_avg` = `5198.33`
- `pulses_issued` = `46.00`
- `neighbour_updates` = `0.00`
- `fps_1pct_low` = `1218.65`
- `block_state_changes` = `70080.00`

### Piston/slime array (8×8 toggled every 8t) (`piston_slime_array`)

Category: **Redstone**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `5679.75`, min `841.33`, p50 `5984.44`, p95 `6191.95`, p99 `6265.66`, 1%low `1534.96`, 0.1%low `999.50`, std `766.09`

**Frame time (ms)**  avg `0.18`, p50 `0.17`, p95 `0.24`, p99 `0.48`, p99.9 `0.84`, max `1.19`

**Client tick (ms)**  avg `0.49`, p95 `0.53`, max `0.74`

**Memory**  start `4110 MB`, end `4846 MB`, peak `7728 MB`, GC `4 events / 234 ms`

**FPS over sampling window (ASCII):**

```
6004.3 |█                                                                               
5935.1 |█                     █              █                           ██             
5865.8 |█                    █████    █ █    █       █          █ ██  █ ████    ██      
5796.6 |█               █  █ █████   ██ █  █ █   █   ██    █ █  █ ██  █ █████   ██      
5727.3 |█      ██       █  █ ██████  ████ ██ █   ██  ██ █ ██ █ ██ ██ ██ ██████  ██      
5658.1 |██   █ ███     ██  █ ██████ █████ ██ █   ██ ███ █ ████ ██ ██ ██ ██████  ██  █  █
5588.8 |██  ██ ███     ██ ██ ██████ █████ ██ ███ ██ ████████████████ ██ ██████  ██ ███ █
5519.6 |██████████   █ ██ ███████████████ ██ █████████████████████████████████  ██████ █
5450.4 |███████████ ██ ██ ███████████████ ██████████████████████████████████████████████
5381.1 |██████████████ ██████████████████ ██████████████████████████████████████████████
5311.9 |██████████████ ██████████████████ ██████████████████████████████████████████████
5242.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20989
   1 ms |   11
```

**Extras:**

- `slime_blocks` = `192.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `1.00`
- `pistons_built` = `64.00`
- `fps_1pct_low` = `1534.96`
- `fps_harmonic_avg` = `5461.68`
- `neighbour_updates` = `0.00`
- `fps_0p1pct_low` = `999.50`
- `preload_chunks` = `81.00`
- `seed` = `4027.00`
- `preset_long` = `0.00`
- `entity_count_sample_start` = `1.00`
- `scheduled_block_ticks` = `0.00`
- `preload_duration_ms` = `13.00`
- `entity_count_delta` = `0.00`
- `power_toggles` = `57.00`
- `preset_full` = `0.00`
- `block_state_changes` = `33600.00`

### Static dense forest (orbit canopy, no worldgen) (`static_dense_forest`)

Category: **Chunks**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `5182.30`, min `893.18`, p50 `5221.93`, p95 `6146.28`, p99 `6234.41`, 1%low `1577.68`, 0.1%low `1044.76`, std `760.10`

**Frame time (ms)**  avg `0.20`, p50 `0.19`, p95 `0.27`, p99 `0.44`, p99.9 `0.88`, max `1.12`

**Client tick (ms)**  avg `0.50`, p95 `0.54`, max `0.71`

**Memory**  start `2798 MB`, end `5840 MB`, peak `5840 MB`, GC `6 events / 972 ms`

**FPS over sampling window (ASCII):**

```
5597.3 |█                                                                               
5515.2 |█  █  █                                                                         
5433.0 |████  ██ █  ██        █ █                                                       
5350.9 |████████ █████ ████ █████ ██                                                    
5268.7 |██████████████ ████ ████████                                                    
5186.5 |███████████████████ ████████    █                              █   ███ ████ ████
5104.4 |███████████████████████████████ ███  █    █      █  ███  █  ████ ███████████████
5022.2 |████████████████████████████████████████ ██████████████████ ████████████████████
4940.1 |███████████████████████████████████████████████████████████ ████████████████████
4857.9 |███████████████████████████████████████████████████████████ ████████████████████
4775.8 |███████████████████████████████████████████████████████████ ████████████████████
4693.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20998
   1 ms |   2
```

**Extras:**

- `entity_count_sample_start` = `1.00`
- `fps_1pct_low` = `1577.68`
- `fps_0p1pct_low` = `1044.76`
- `preload_chunks` = `81.00`
- `trees_built` = `64.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `1.00`
- `preset_long` = `0.00`
- `preset_full` = `0.00`
- `fps_harmonic_avg` = `4995.83`
- `entity_count_delta` = `0.00`
- `log_blocks` = `320.00`
- `seed` = `7039.00`
- `preload_duration_ms` = `55.00`
- `leaf_blocks` = `7642.00`

### Plains flyby (single-biome world) (`chunk_plains`)

Category: **Chunks**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `4378.49`, min `66.23`, p50 `4766.44`, p95 `5192.11`, p99 `5285.41`, 1%low `483.00`, 0.1%low `147.54`, std `964.42`

**Frame time (ms)**  avg `0.26`, p50 `0.21`, p95 `0.50`, p99 `1.07`, p99.9 `3.81`, max `15.10`

**Client tick (ms)**  avg `0.81`, p95 `1.17`, max `2.28`

**Memory**  start `2004 MB`, end `4292 MB`, peak `7388 MB`, GC `16 events / 1399 ms`

**FPS over sampling window (ASCII):**

```
5097.1 |                                                                         ██     
4909.5 |                                           █                  █    █    ███     
4721.9 |    █       █ ██        ██                 ██       ████ █    ██   █   ████  █  
4534.3 |    ████  █ █████     █████     ███ █ █   ████   ███████ █   ████  █  ██████ █  
4346.7 | █ █████ ██ █████    ███████ █  █████ █ ██████   ███████ ███ ████  ██ ██████ █ █
4159.1 |████████ ██ █████  █ ███████ ████████ █ ██████   ███████ █████████ █████████ ███
3971.5 |████████ █████████ █████████ ████████ █ ██████  ████████ ███████████████████ ███
3783.9 |████████ ███████████████████ ████████ █ ██████ █████████████████████████████ ███
3596.3 |████████ ███████████████████ ███████████████████████████████████████████████████
3408.7 |████████ ███████████████████ ███████████████████████████████████████████████████
3221.1 |████████ ███████████████████████████████████████████████████████████████████████
3033.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20740
   1 ms |   206
   2 ms |   26
   3 ms |   10
   4 ms |   5
   5 ms |   3
   6 ms |   2
   7 ms |   3
   9 ms |   2
  10 ms |   1
  11 ms |   1
  15 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `biome` = `minecraft:plains`
- `scan_fallback` = `false`
- `z_offset_used` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `30.00`
- `x_offset_used` = `0.00`
- `fps_1pct_low` = `483.00`
- `fps_harmonic_avg` = `3780.12`
- `fps_0p1pct_low` = `147.54`
- `preload_chunks` = `81.00`
- `seed` = `7411.00`
- `preset_long` = `0.00`
- `flyby_distance_blocks` = `720.00`
- `entity_count_sample_start` = `51.00`
- `surface_water_ratio` = `0.02`
- `preload_duration_ms` = `44.00`
- `flyby_blocks_per_tick` = `1.20`
- `stamped_blocks` = `0.00`
- `entity_count_delta` = `-21.00`
- `preset_full` = `0.00`

### Jungle flyby (single-biome world) (`chunk_jungle`)

Category: **Chunks**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `2454.09`, min `48.26`, p50 `2497.50`, p95 `3549.94`, p99 `4428.70`, 1%low `330.74`, 0.1%low `96.43`, std `638.71`

**Frame time (ms)**  avg `0.46`, p50 `0.40`, p95 `0.82`, p99 `1.44`, p99.9 `6.52`, max `20.72`

**Client tick (ms)**  avg `0.76`, p95 `1.09`, max `2.23`

**Memory**  start `4502 MB`, end `5616 MB`, peak `7162 MB`, GC `21 events / 2774 ms`

**FPS over sampling window (ASCII):**

```
2818.3 |    ██     █                                                                    
2739.9 |█   ██    ██   ███    █    ██                                                   
2661.4 |█   ██   ███   ███  ███    ██   ███    █                                        
2582.9 |█  ███  ████  ████  ███   ███   ███  █ █    █    █     █    █         ██        
2504.5 |█ ████  ████  ████  ████  ███ █████  ███   ██    ██  ███   ██    █    ██    █   
2426.0 |█ ██████████  ████  ████ ████ █████  ███ █ ██   ███  ███   ██  ████   ██   ██   
2347.5 |█ ██████████  ████ ██████████ █████ ████ ████   ███ ████  ███  ████ ████ ████  █
2269.0 |████████████ █████ ██████████ █████ ████ ████  ████ ████ ████  ████ ████ █████ █
2190.6 |████████████ ████████████████ ██████████ ████ █████ ████ ██████████ ██████████ █
2112.1 |████████████ ████████████████ ██████████ ███████████████████████████████████████
2033.6 |████████████████████████████████████████ ███████████████████████████████████████
1955.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20249
   1 ms | █  651
   2 ms |   56
   3 ms |   10
   4 ms |   6
   5 ms |   5
   6 ms |   5
   7 ms |   3
   8 ms |   4
   9 ms |   2
  10 ms |   3
  12 ms |   2
  14 ms |   1
  15 ms |   1
  16 ms |   1
  20 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `biome` = `minecraft:jungle`
- `scan_fallback` = `false`
- `z_offset_used` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `34.00`
- `x_offset_used` = `0.00`
- `fps_1pct_low` = `330.74`
- `fps_harmonic_avg` = `2154.17`
- `fps_0p1pct_low` = `96.43`
- `preload_chunks` = `81.00`
- `seed` = `7417.00`
- `preset_long` = `0.00`
- `flyby_distance_blocks` = `720.00`
- `entity_count_sample_start` = `34.00`
- `surface_water_ratio` = `0.06`
- `preload_duration_ms` = `48.00`
- `flyby_blocks_per_tick` = `1.20`
- `stamped_blocks` = `0.00`
- `entity_count_delta` = `0.00`
- `preset_full` = `0.00`

### Desert flyby (single-biome world) (`chunk_desert`)

Category: **Chunks**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `4358.82`, min `58.99`, p50 `4690.43`, p95 `4970.18`, p99 `5042.86`, 1%low `525.74`, 0.1%low `132.74`, std `834.49`

**Frame time (ms)**  avg `0.26`, p50 `0.21`, p95 `0.44`, p99 `0.85`, p99.9 `2.78`, max `16.95`

**Client tick (ms)**  avg `0.71`, p95 `0.88`, max `1.65`

**Memory**  start `3238 MB`, end `6174 MB`, peak `7198 MB`, GC `16 events / 1328 ms`

**FPS over sampling window (ASCII):**

```
4823.6 |                      █                  █                             █ █      
4681.7 |            █ █      ████         █     █████        █       █ █       █ ██     
4539.7 |  ████      █ █      █████  █ █ ███     █████     ████    █ ████   █ ██████ █   
4397.8 |  ████ █   ████   ████████ ██ █████    ██████ █ ███████   ███████  █ ██████ █   
4255.9 |  ████ █  █████   ████████ ████████  █ ██████ █ ████████  ███████ ██ ██████ ██ █
4114.0 |██████ ██ █████ █ ████████ ████████ █████████ █ ████████ ████████ █████████ ████
3972.1 |██████ ████████ ██████████ ████████ █████████ ██████████ ████████ █████████ ████
3830.2 |██████ ████████ ██████████ ████████ █████████ ██████████ ██████████████████ ████
3688.3 |██████ ███████████████████ █████████████████████████████████████████████████████
3546.4 |██████ █████████████████████████████████████████████████████████████████████████
3404.5 |██████ █████████████████████████████████████████████████████████████████████████
3262.6 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20843
   1 ms |   121
   2 ms |   16
   3 ms |   6
   4 ms |   2
   5 ms |   1
   6 ms |   3
   7 ms |   1
   8 ms |   1
  10 ms |   1
  13 ms |   2
  15 ms |   2
  16 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `biome` = `minecraft:desert`
- `scan_fallback` = `false`
- `z_offset_used` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `33.00`
- `x_offset_used` = `0.00`
- `fps_1pct_low` = `525.74`
- `fps_harmonic_avg` = `3886.84`
- `fps_0p1pct_low` = `132.74`
- `preload_chunks` = `81.00`
- `seed` = `7433.00`
- `preset_long` = `0.00`
- `flyby_distance_blocks` = `720.00`
- `entity_count_sample_start` = `44.00`
- `surface_water_ratio` = `0.00`
- `preload_duration_ms` = `49.00`
- `flyby_blocks_per_tick` = `1.20`
- `stamped_blocks` = `0.00`
- `entity_count_delta` = `-11.00`
- `preset_full` = `0.00`

### Taiga flyby (single-biome world) (`chunk_taiga`)

Category: **Chunks**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `4412.18`, min `40.40`, p50 `4854.37`, p95 `5271.48`, p99 `5350.48`, 1%low `405.35`, 0.1%low `118.52`, std `1043.01`

**Frame time (ms)**  avg `0.27`, p50 `0.21`, p95 `0.55`, p99 `1.07`, p99.9 `4.87`, max `24.76`

**Client tick (ms)**  avg `0.72`, p95 `1.01`, max `2.23`

**Memory**  start `2608 MB`, end `5598 MB`, peak `7298 MB`, GC `16 events / 1705 ms`

**FPS over sampling window (ASCII):**

```
5180.6 |                                                               ██        █      
4956.8 |                                 ██      █  ██     ████     █ ████      ████    
4733.0 |     ███     █  █      █        ████     █████   █ ████     ██████ █  ██████    
4509.2 |     ███  ██ █ ███   █ ████    █████ █   █████   █ █████ █  ██████ █  ██████    
4285.3 |   ██████ ██ █████   ██████  ███████ █  ██████ █ ███████ █ ███████ █████████ ██ 
4061.5 |   ██████ ██ █████ ████████  ███████ ██ ██████ █████████ █████████ █████████ ███
3837.7 | ████████ ████████ ████████  ███████ █████████ █████████ █████████ █████████ ███
3613.9 | ████████ ████████ ████████ ████████ █████████████████████████████ █████████ ███
3390.1 |█████████ ████████ ███████████████████████████████████████████████ █████████ ███
3166.3 |█████████ ██████████████████████████████████████████████████████████████████ ███
2942.5 |█████████ ██████████████████████████████████████████████████████████████████████
2718.7 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20748
   1 ms |   181
   2 ms |   28
   3 ms |   15
   4 ms |   8
   5 ms |   8
   6 ms |   2
   8 ms |   2
   9 ms |   5
  10 ms |   1
  13 ms |   1
  24 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `biome` = `minecraft:taiga`
- `scan_fallback` = `false`
- `z_offset_used` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `6.00`
- `x_offset_used` = `0.00`
- `fps_1pct_low` = `405.35`
- `fps_harmonic_avg` = `3698.63`
- `fps_0p1pct_low` = `118.52`
- `preload_chunks` = `81.00`
- `seed` = `7451.00`
- `preset_long` = `0.00`
- `flyby_distance_blocks` = `720.00`
- `entity_count_sample_start` = `21.00`
- `surface_water_ratio` = `0.00`
- `preload_duration_ms` = `49.00`
- `flyby_blocks_per_tick` = `1.20`
- `stamped_blocks` = `0.00`
- `entity_count_delta` = `-15.00`
- `preset_full` = `0.00`

### Snowy plains flyby (`chunk_snowy`)

Category: **Chunks**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `4531.33`, min `53.58`, p50 `4962.78`, p95 `5458.66`, p99 `5534.06`, 1%low `443.34`, 0.1%low `119.20`, std `1063.09`

**Frame time (ms)**  avg `0.26`, p50 `0.20`, p95 `0.50`, p99 `1.00`, p99.9 `5.16`, max `18.66`

**Client tick (ms)**  avg `0.69`, p95 `0.98`, max `1.30`

**Memory**  start `2610 MB`, end `4502 MB`, peak `7402 MB`, GC `16 events / 1480 ms`

**FPS over sampling window (ASCII):**

```
5237.6 |                                                  ███                   ██      
5011.3 |             █        ███                ██      ██████      ███     █ ████     
4785.1 |   ███       ██     █ ███     █ █       ████     ██████     █████    ███████  █ 
4558.8 | █ ████    █ ██     █████    ██████ █ ██████    ███████ █ ███████ ██████████  █ 
4332.5 |███████  █ ████    ███████ █ ██████ █ ███████ █████████ █████████ ██████████ ███
4106.3 |███████  █ █████  ████████ ████████ █ ███████ █████████ █████████ ██████████ ███
3880.0 |███████  ███████ █████████ ████████ █ ██████████████████████████████████████ ███
3653.8 |███████ ██████████████████ ████████ ████████████████████████████████████████████
3427.5 |███████ ██████████████████ █████████████████████████████████████████████████████
3201.2 |███████ ████████████████████████████████████████████████████████████████████████
2975.0 |███████ ████████████████████████████████████████████████████████████████████████
2748.7 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20789
   1 ms |   156
   2 ms |   24
   3 ms |   6
   4 ms |   3
   5 ms |   7
   6 ms |   5
   7 ms |   2
   8 ms |   3
  10 ms |   2
  11 ms |   1
  17 ms |   1
  18 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `biome` = `minecraft:snowy_plains`
- `scan_fallback` = `false`
- `z_offset_used` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `7.00`
- `x_offset_used` = `0.00`
- `fps_1pct_low` = `443.34`
- `fps_harmonic_avg` = `3845.51`
- `fps_0p1pct_low` = `119.20`
- `preload_chunks` = `81.00`
- `seed` = `7457.00`
- `preset_long` = `0.00`
- `flyby_distance_blocks` = `720.00`
- `entity_count_sample_start` = `24.00`
- `surface_water_ratio` = `0.00`
- `preload_duration_ms` = `50.00`
- `flyby_blocks_per_tick` = `1.20`
- `stamped_blocks` = `0.00`
- `entity_count_delta` = `-17.00`
- `preset_full` = `0.00`

### Forest flyby (`chunk_forest`)

Category: **Chunks**  |  Duration: 23104 ms  |  Sample ticks: 400

**FPS**  avg `4173.77`, min `43.16`, p50 `4436.56`, p95 `5037.78`, p99 `5157.32`, 1%low `447.24`, 0.1%low `114.64`, std `927.11`

**Frame time (ms)**  avg `0.28`, p50 `0.23`, p95 `0.48`, p99 `1.10`, p99.9 `3.65`, max `23.17`

**Client tick (ms)**  avg `0.75`, p95 `1.09`, max `2.14`

**Memory**  start `1760 MB`, end `4770 MB`, peak `7048 MB`, GC `17 events / 1651 ms`

**FPS over sampling window (ASCII):**

```
4801.3 |█        ███                         █                                          
4630.7 |██     █████     ████              ███          █                               
4460.0 |███    █████  █  ████    █ ████    ████       ███                 █             
4289.3 |███  ███████  ██ ████   ███████   █████     █████    ████     █████    █ ███    
4118.6 |███  ███████ ████████   ███████   ██████ █ ██████  ███████    █████    █████    
3947.9 |███ ████████ ████████ █ ███████ █ ██████ ████████  ███████ ████████   ██████    
3777.3 |███ ████████ ████████ █████████ ████████ ████████ ████████ ████████ █ ██████ ███
3606.6 |███ ████████ ████████ █████████ █████████████████ ████████ ████████ ████████ ███
3435.9 |████████████ ██████████████████ █████████████████ ██████████████████████████ ███
3265.2 |████████████ ██████████████████ █████████████████ ██████████████████████████ ███
3094.6 |████████████ ███████████████████████████████████████████████████████████████ ███
2923.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20700
   1 ms |   252
   2 ms |   20
   3 ms |   7
   4 ms |   7
   5 ms |   2
   6 ms |   1
   7 ms |   1
   8 ms |   2
   9 ms |   3
  10 ms |   1
  14 ms |   2
  17 ms |   1
  23 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `biome` = `minecraft:forest`
- `scan_fallback` = `false`
- `z_offset_used` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `33.00`
- `x_offset_used` = `0.00`
- `fps_1pct_low` = `447.24`
- `fps_harmonic_avg` = `3607.17`
- `fps_0p1pct_low` = `114.64`
- `preload_chunks` = `81.00`
- `seed` = `7477.00`
- `preset_long` = `0.00`
- `flyby_distance_blocks` = `720.00`
- `entity_count_sample_start` = `14.00`
- `surface_water_ratio` = `0.00`
- `preload_duration_ms` = `39.00`
- `flyby_blocks_per_tick` = `1.20`
- `stamped_blocks` = `0.00`
- `entity_count_delta` = `19.00`
- `preset_full` = `0.00`

### Savanna flyby (`chunk_savanna`)

Category: **Chunks**  |  Duration: 23101 ms  |  Sample ticks: 400

**FPS**  avg `4568.10`, min `40.16`, p50 `4972.65`, p95 `5268.70`, p99 `5333.33`, 1%low `500.99`, 0.1%low `134.59`, std `973.15`

**Frame time (ms)**  avg `0.25`, p50 `0.20`, p95 `0.47`, p99 `0.99`, p99.9 `3.12`, max `24.90`

**Client tick (ms)**  avg `0.73`, p95 `1.05`, max `1.93`

**Memory**  start `5182 MB`, end `4870 MB`, peak `7230 MB`, GC `21 events / 2692 ms`

**FPS over sampling window (ASCII):**

```
5092.7 |                                                   ███       █ █       ██ █     
4911.6 | ███       █                 ███       ████     ██████     █ ████      ████     
4730.6 | ████    █████     ████      ████     ██████ █  ███████ █  ██████   ██ ████ █   
4549.6 | ████   ██████ █  ██████   █ █████ █ ███████ █ ████████ █  ██████ █ ███████ █  █
4368.6 |█████   ██████ █ ███████  ████████ █████████ █ ████████ █████████ █ ███████ █ ██
4187.6 |█████ ████████ █ ███████ █████████ █████████ ██████████ █████████ █ ███████ ████
4006.6 |█████ ████████ █ ███████ █████████ █████████ ██████████ █████████ █████████ ████
3825.6 |█████ ████████ █ ███████ █████████ ██████████████████████████████ █████████ ████
3644.6 |█████ ████████ █ ███████████████████████████████████████████████████████████████
3463.6 |█████ ████████ █████████████████████████████████████████████████████████████████
3282.6 |█████ ██████████████████████████████████████████████████████████████████████████
3101.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20793
   1 ms |   167
   2 ms |   17
   3 ms |   8
   4 ms |   1
   5 ms |   4
   6 ms |   2
   7 ms |   3
   8 ms |   1
  11 ms |   1
  12 ms |   1
  16 ms |   1
  24 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `biome` = `minecraft:savanna`
- `scan_fallback` = `false`
- `z_offset_used` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `28.00`
- `x_offset_used` = `0.00`
- `fps_1pct_low` = `500.99`
- `fps_harmonic_avg` = `3961.73`
- `fps_0p1pct_low` = `134.59`
- `preload_chunks` = `81.00`
- `seed` = `7481.00`
- `preset_long` = `0.00`
- `flyby_distance_blocks` = `720.00`
- `entity_count_sample_start` = `61.00`
- `surface_water_ratio` = `0.00`
- `preload_duration_ms` = `50.00`
- `flyby_blocks_per_tick` = `1.20`
- `stamped_blocks` = `0.00`
- `entity_count_delta` = `-33.00`
- `preset_full` = `0.00`

### Swamp flyby (`chunk_swamp`)

Category: **Chunks**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `4139.88`, min `48.77`, p50 `4531.04`, p95 `5015.05`, p99 `5117.71`, 1%low `470.54`, 0.1%low `133.28`, std `961.87`

**Frame time (ms)**  avg `0.28`, p50 `0.22`, p95 `0.54`, p99 `1.08`, p99.9 `3.34`, max `20.50`

**Client tick (ms)**  avg `0.70`, p95 `0.94`, max `2.88`

**Memory**  start `2340 MB`, end `7620 MB`, peak `7620 MB`, GC `20 events / 2509 ms`

**FPS over sampling window (ASCII):**

```
4776.4 |                                                                      █ ██ █    
4618.3 |███        █                                         █         █ █    █ ████    
4460.1 |████     █ █      █ █      ██      ██        ███     ███      █████   █ ████    
4302.0 |████   █ █ █   █ ████    █ ████    ██       ████     ███     ██████   ██████    
4143.9 |████ █ ██████ ██ ████   ███████    ██ █   █ ████   █ ███   █ ██████ █ ██████  ██
3985.7 |████ █ ██████ ██ ████   ███████  █ ████  ██ ████ █ ██████  █ ██████ ████████ ███
3827.6 |████ █ ██████ ██ ████   ███████ ███████ ████████ █████████ ████████ ████████ ███
3669.4 |████ ████████ ███████ █ ███████ ███████ ████████ █████████ ████████ ████████ ███
3511.3 |█████████████ █████████████████ ████████████████ █████████ ████████ ████████ ███
3353.1 |█████████████ █████████████████ ████████████████████████████████████████████ ███
3195.0 |█████████████ █████████████████ ████████████████████████████████████████████ ███
3036.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20702
   1 ms |   250
   2 ms |   23
   3 ms |   12
   5 ms |   4
   6 ms |   1
   7 ms |   1
   9 ms |   2
  12 ms |   3
  15 ms |   1
  20 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `biome` = `minecraft:swamp`
- `scan_fallback` = `false`
- `z_offset_used` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `37.00`
- `x_offset_used` = `0.00`
- `fps_1pct_low` = `470.54`
- `fps_harmonic_avg` = `3550.11`
- `fps_0p1pct_low` = `133.28`
- `preload_chunks` = `81.00`
- `seed` = `7487.00`
- `preset_long` = `0.00`
- `flyby_distance_blocks` = `720.00`
- `entity_count_sample_start` = `7.00`
- `surface_water_ratio` = `0.00`
- `preload_duration_ms` = `50.00`
- `flyby_blocks_per_tick` = `1.20`
- `stamped_blocks` = `0.00`
- `entity_count_delta` = `30.00`
- `preset_full` = `0.00`

### Cherry grove flyby (`chunk_cherry`)

Category: **Chunks**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `4575.54`, min `65.57`, p50 `4967.71`, p95 `5503.58`, p99 `5580.36`, 1%low `500.17`, 0.1%low `138.83`, std `1063.95`

**Frame time (ms)**  avg `0.26`, p50 `0.20`, p95 `0.51`, p99 `1.01`, p99.9 `3.48`, max `15.25`

**Client tick (ms)**  avg `0.81`, p95 `1.17`, max `2.01`

**Memory**  start `2896 MB`, end `5088 MB`, peak `7134 MB`, GC `20 events / 1740 ms`

**FPS over sampling window (ASCII):**

```
5301.3 |                                                   █                    ██      
5080.8 |                                        ███       ████  █    ███  █    ████  █  
4860.3 |  █ █       █       ████  █   █ █      █████ █ █ █████  █  █████  ██ █ ████ ██  
4639.8 |█ ███     ████    █ ████  ██ █████ █   █████ █ ████████ █  █████  ██ ██████ ██  
4419.3 |█ ████ ██ ████    ███████ ██ █████ ██ ██████ ██████████ █ ███████ █████████ ████
4198.9 |██████ ███████   ████████ ██ █████ ██ ██████ ██████████ █████████ █████████ ████
3978.4 |██████ ███████  █████████ ████████ ██ ██████ ██████████ ███████████████████ ████
3757.9 |██████ ██████████████████ ████████ █████████ ██████████████████████████████ ████
3537.4 |██████ ███████████████████████████ █████████ ███████████████████████████████████
3316.9 |██████ ███████████████████████████ █████████ ███████████████████████████████████
3096.4 |██████ █████████████████████████████████████████████████████████████████████████
2875.9 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20782
   1 ms |   177
   2 ms |   15
   3 ms |   7
   4 ms |   5
   5 ms |   2
   6 ms |   4
   7 ms |   2
   8 ms |   1
   9 ms |   1
  10 ms |   1
  11 ms |   1
  14 ms |   1
  15 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `biome` = `minecraft:cherry_grove`
- `scan_fallback` = `false`
- `z_offset_used` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `18.00`
- `x_offset_used` = `0.00`
- `fps_1pct_low` = `500.17`
- `fps_harmonic_avg` = `3913.01`
- `fps_0p1pct_low` = `138.83`
- `preload_chunks` = `81.00`
- `seed` = `7499.00`
- `preset_long` = `0.00`
- `flyby_distance_blocks` = `720.00`
- `entity_count_sample_start` = `72.00`
- `surface_water_ratio` = `0.00`
- `preload_duration_ms` = `50.00`
- `flyby_blocks_per_tick` = `1.20`
- `stamped_blocks` = `0.00`
- `entity_count_delta` = `-54.00`
- `preset_full` = `0.00`

### Badlands flyby (`chunk_badlands`)

Category: **Chunks**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `4601.87`, min `59.79`, p50 `5037.78`, p95 `5540.32`, p99 `5643.34`, 1%low `545.68`, 0.1%low `147.29`, std `1077.35`

**Frame time (ms)**  avg `0.25`, p50 `0.20`, p95 `0.53`, p99 `0.93`, p99.9 `3.32`, max `16.72`

**Client tick (ms)**  avg `0.64`, p95 `0.94`, max `1.14`

**Memory**  start `3556 MB`, end `4698 MB`, peak `7348 MB`, GC `22 events / 2396 ms`

**FPS over sampling window (ASCII):**

```
5424.3 |                                                                        █       
5210.3 |                                          ██        ██      ████     █ ████     
4996.3 |   █        ██        █       █         █ ██      █████ █   █████ █  █ ████     
4782.2 | ████   █   ██    █  ███     █████      ████ █ █ ██████ ██  █████ ██ █ █████ █  
4568.2 | ████ ███ ████    ███████ █  █████     █████ ███ ██████ █████████ ██ ███████ █  
4354.2 | ████ ███ ████ █ ████████ ██ █████    ██████ ██████████ █████████ ██ ███████ █ █
4140.1 |█████ ███ ████ ██████████ ██ █████    ██████ ██████████ █████████ ██████████ ███
3926.1 |█████ ███ ████ ██████████ █████████ ████████ ██████████ █████████ ██████████ ███
3712.1 |█████ ███████████████████ ██████████████████ ████████████████████ ██████████ ███
3498.1 |█████ ███████████████████ ██████████████████ ████████████████████ ██████████ ███
3284.0 |█████ ███████████████████████████████████████████████████████████ ██████████████
3070.0 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20843
   1 ms |   121
   2 ms |   11
   3 ms |   9
   4 ms |   3
   5 ms |   3
   7 ms |   3
   8 ms |   4
  10 ms |   1
  11 ms |   1
  16 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `biome` = `minecraft:badlands`
- `scan_fallback` = `false`
- `z_offset_used` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `1.00`
- `x_offset_used` = `0.00`
- `fps_1pct_low` = `545.68`
- `fps_harmonic_avg` = `3954.07`
- `fps_0p1pct_low` = `147.29`
- `preload_chunks` = `81.00`
- `seed` = `7507.00`
- `preset_long` = `0.00`
- `flyby_distance_blocks` = `720.00`
- `entity_count_sample_start` = `1.00`
- `surface_water_ratio` = `0.10`
- `preload_duration_ms` = `41.00`
- `flyby_blocks_per_tick` = `1.20`
- `stamped_blocks` = `0.00`
- `entity_count_delta` = `0.00`
- `preset_full` = `0.00`

### Dark forest flyby (dense canopy) (`chunk_dark_forest`)

Category: **Chunks**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `4090.11`, min `37.82`, p50 `4403.35`, p95 `5107.25`, p99 `5208.33`, 1%low `352.76`, 0.1%low `107.12`, std `993.75`

**Frame time (ms)**  avg `0.29`, p50 `0.23`, p95 `0.55`, p99 `1.15`, p99.9 `6.05`, max `26.44`

**Client tick (ms)**  avg `0.68`, p95 `0.97`, max `1.46`

**Memory**  start `3508 MB`, end `4272 MB`, peak `7352 MB`, GC `21 events / 2514 ms`

**FPS over sampling window (ASCII):**

```
4900.3 |                                              ██                                
4678.0 |                             █      ██     █████       ██       █        █      
4455.7 |                   ██     ████    █████    █████     ████  █   ███  █ ██ ██  █  
4233.5 |                  ███     █████ █ █████   ██████ █  ██████ █ █████  █ ██████ ██ 
4011.2 |   ███     ██    ████    ██████ ███████ █ ██████ █ ███████ ████████ ████████ ███
3788.9 |  ████   █████   ████ █████████ ███████ █ ██████ █████████ ████████ ████████ ███
3566.7 | █████ █ █████ ██████ █████████ ███████ █ ██████ █████████ █████████████████ ███
3344.4 |██████ ███████ ████████████████ ███████ ████████ █████████ █████████████████████
3122.1 |██████████████ ████████████████ ████████████████████████████████████████████████
2899.8 |██████████████ ████████████████ ████████████████████████████████████████████████
2677.6 |██████████████ █████████████████████████████████████████████████████████████████
2455.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20719
   1 ms |   201
   2 ms |   26
   3 ms |   15
   4 ms |   5
   5 ms |   10
   6 ms |   8
   7 ms |   7
   8 ms |   2
   9 ms |   3
  10 ms |   2
  17 ms |   1
  26 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `biome` = `minecraft:dark_forest`
- `scan_fallback` = `false`
- `z_offset_used` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `11.00`
- `x_offset_used` = `0.00`
- `fps_1pct_low` = `352.76`
- `fps_harmonic_avg` = `3422.77`
- `fps_0p1pct_low` = `107.12`
- `preload_chunks` = `81.00`
- `seed` = `7517.00`
- `preset_long` = `0.00`
- `flyby_distance_blocks` = `720.00`
- `entity_count_sample_start` = `28.00`
- `surface_water_ratio` = `0.00`
- `preload_duration_ms` = `50.00`
- `flyby_blocks_per_tick` = `1.20`
- `stamped_blocks` = `0.00`
- `entity_count_delta` = `-17.00`
- `preset_full` = `0.00`

### Windswept hills flyby (`chunk_mountain`)

Category: **Chunks**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `4358.13`, min `61.70`, p50 `4697.04`, p95 `5120.33`, p99 `5202.91`, 1%low `523.81`, 0.1%low `149.20`, std `893.54`

**Frame time (ms)**  avg `0.26`, p50 `0.21`, p95 `0.45`, p99 `1.02`, p99.9 `3.06`, max `16.21`

**Client tick (ms)**  avg `0.69`, p95 `0.94`, max `1.42`

**Memory**  start `3250 MB`, end `3770 MB`, peak `7516 MB`, GC `20 events / 1680 ms`

**FPS over sampling window (ASCII):**

```
4911.1 |                                                                      █ █ █     
4766.3 |                                          ██       █ █        █      ██ ███     
4621.4 |█ ██        █        ███ █         █    ██████    ██ █   █   ███  █  ██████ █   
4476.5 |████     █ ██ █      █████      ████ █  ██████  █ █████  █   ███  █  ██████ █ ██
4331.6 |█████ █ ██ ████  ██ ██████  ██  ████ █  ██████  █ ██████ █   ████ █ ███████ ████
4186.8 |█████ █ ████████ ██ ███████ ███ ████ █  ██████  █ ██████ █  █████ █████████ ████
4041.9 |█████ █ ████████ ██ ███████ ████████ █████████  ████████ █ ██████ █████████ ████
3897.0 |█████ █ ████████ ██████████ ████████ █████████ █████████ ████████ █████████ ████
3752.1 |█████ █ ████████ ███████████████████ █████████ ██████████████████ █████████ ████
3607.2 |█████ ██████████████████████████████ █████████ █████████████████████████████████
3462.4 |██████████████████████████████████████████████ █████████████████████████████████
3317.5 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20763
   1 ms |   195
   2 ms |   20
   3 ms |   9
   4 ms |   3
   5 ms |   1
   6 ms |   3
   8 ms |   1
  11 ms |   2
  12 ms |   1
  14 ms |   1
  16 ms |   1
```

**Extras:**

- `stamped_fallback` = `false`
- `biome` = `minecraft:windswept_hills`
- `scan_fallback` = `false`
- `z_offset_used` = `0.00`
- `preset_quick` = `1.00`
- `entity_count_sample_end` = `84.00`
- `x_offset_used` = `0.00`
- `fps_1pct_low` = `523.81`
- `fps_harmonic_avg` = `3836.26`
- `fps_0p1pct_low` = `149.20`
- `preload_chunks` = `81.00`
- `seed` = `7523.00`
- `preset_long` = `0.00`
- `flyby_distance_blocks` = `720.00`
- `entity_count_sample_start` = `16.00`
- `surface_water_ratio` = `0.02`
- `preload_duration_ms` = `49.00`
- `flyby_blocks_per_tick` = `1.20`
- `stamped_blocks` = `0.00`
- `entity_count_delta` = `68.00`
- `preset_full` = `0.00`

### Idle baseline (orbit, flat world) (`idle_baseline`)

Category: **Baseline**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `5592.68`, min `420.36`, p50 `5896.23`, p95 `6230.53`, p99 `6301.20`, 1%low `1479.91`, 0.1%low `888.64`, std `769.79`

**Frame time (ms)**  avg `0.19`, p50 `0.17`, p95 `0.24`, p99 `0.47`, p99.9 `0.98`, max `2.38`

**Client tick (ms)**  avg `0.53`, p95 `0.57`, max `0.83`

**Memory**  start `3560 MB`, end `1642 MB`, peak `7516 MB`, GC `10 events / 1653 ms`

**FPS over sampling window (ASCII):**

```
6059.8 |                                        █                                       
5969.6 |                 █  █                   █                                       
5879.3 | █               ████   ██              █                               █ █     
5789.1 |██          █  ████████ ██          █  ██                               ████    
5698.9 |███ ██   █ ██ █████████████         █  ██  █ █  █    ██ █          █    █████   
5608.6 |███ ██   ██████████████████       █ █  ██  █ █  █    ██ █  ██     ██   ██████  █
5518.4 |██████  ████████████████████  █  ██ █  ██ ██ █  █   ██████ ███  █ ████ ██████ ██
5428.1 |██████ █████████████████████  █  ██ █ ███ ████ ███  ██████ ███  ████████████████
5337.9 |██████ ██████████████████████ █  ████ ███ ████ ████ ██████████ █████████████████
5247.6 |█████████████████████████████ █████████████████████ ████████████████████████████
5157.4 |█████████████████████████████ ██████████████████████████████████████████████████
5067.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20983
   1 ms |   16
   2 ms |   1
```

**Extras:**

- `entity_count_sample_start` = `1.00`
- `preload_duration_ms` = `50.00`
- `preset_quick` = `1.00`
- `seed` = `1923.00`
- `fps_1pct_low` = `1479.91`
- `fps_harmonic_avg` = `5375.40`
- `entity_count_sample_end` = `1.00`
- `fps_0p1pct_low` = `888.64`
- `preload_chunks` = `81.00`
- `entity_count_delta` = `0.00`
- `preset_long` = `0.00`
- `preset_full` = `0.00`

### Particle diversity stress (16 types simultaneously) (`particle_diversity_stress`)

Category: **Stress**  |  Duration: 23105 ms  |  Sample ticks: 400

**FPS**  avg `439.51`, min `166.52`, p50 `454.07`, p95 `487.54`, p99 `499.28`, 1%low `220.68`, 0.1%low `193.14`, std `53.15`

**Frame time (ms)**  avg `2.33`, p50 `2.20`, p95 `2.96`, p99 `4.31`, p99.9 `4.82`, max `6.01`

**Client tick (ms)**  avg `1.71`, p95 `1.86`, max `2.77`

**Memory**  start `6134 MB`, end `3600 MB`, peak `7638 MB`, GC `8 events / 569 ms`

**FPS over sampling window (ASCII):**

```
468.0 |                                                                █               
462.8 |                                                                ██              
457.7 |                                                                ██              
452.5 |██            █                █    █                          ███              
447.4 |███          ████ █           ████████ █ █  ████    ███  █  █  ███              
442.2 |███          ██████       █   ██████████ ████████   ██████  ██ ███ █            
437.0 |████         ███████ ███ ███ █████████████████████████████  ███████████ ██   █  
431.9 |█████        ███████████████ █████████████████████████████ ███████████████ ███ █
426.7 |██████  █ █  █████████████████████████████████████████████ ███████████████ ███ █
421.6 |███████ ███  █████████████████████████████████████████████████████████████ █████
416.4 |████████████ ███████████████████████████████████████████████████████████████████
411.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   1 ms |   75
   2 ms | ████████████████████████████████████████  8096
   3 ms | █  132
   4 ms | █  286
   5 ms |   4
   6 ms |   1
```

**Extras:**

- `preset_long` = `0.00`
- `fps_1pct_low` = `220.68`
- `preload_chunks` = `81.00`
- `entity_count_sample_start` = `1.00`
- `preset_full` = `0.00`
- `fps_0p1pct_low` = `193.14`
- `entity_count_sample_end` = `1.00`
- `entity_count_delta` = `0.00`
- `preset_quick` = `1.00`
- `fps_harmonic_avg` = `429.69`
- `preload_duration_ms` = `49.00`
- `seed` = `2521.00`
- `particles_spawned` = `256000.00`
- `particle_types` = `16.00`

### Fluid spread (water basin, 4-step periodic reset) (`fluid_spread`)

Category: **Fluids**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `5208.06`, min `395.40`, p50 `5558.64`, p95 `5896.23`, p99 `5955.96`, 1%low `1235.14`, 0.1%low `808.78`, std `906.26`

**Frame time (ms)**  avg `0.20`, p50 `0.18`, p95 `0.33`, p99 `0.62`, p99.9 `1.02`, max `2.53`

**Client tick (ms)**  avg `0.54`, p95 `0.58`, max `0.73`

**Memory**  start `1984 MB`, end `2578 MB`, peak `7830 MB`, GC `4 events / 359 ms`

**FPS over sampling window (ASCII):**

```
5590.4 |                                         █                        █             
5482.8 |    █                        █           █     ██          █      █             
5375.2 |   ████ █  █   ███           █    █   █ ██ ███ ██   █ █    █ █    ███   ██     █
5267.5 |   ████ █ ██   ███ ██        █ ████  █████ ███ ███ ██ ██ █ ███ ██ ████ ████   ██
5159.9 |█ █████ █████ ███████        █████████████ ███ ██████ ██ █ ███████████ ████ █ ██
5052.3 |█████████████████████        █████████████ ███████████████ ██████████████████ ██
4944.7 |█████████████████████       ██████████████ █████████████████████████████████████
4837.1 |███████████████████████     ████████████████████████████████████████████████████
4729.5 |███████████████████████   █ ████████████████████████████████████████████████████
4621.9 |███████████████████████ ████████████████████████████████████████████████████████
4514.3 |███████████████████████ ████████████████████████████████████████████████████████
4406.7 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20974
   1 ms |   25
   2 ms |   1
```

**Extras:**

- `preset_quick` = `1.00`
- `entity_count_sample_end` = `1.00`
- `fps_1pct_low` = `1235.14`
- `fps_harmonic_avg` = `4880.42`
- `scheduled_fluid_ticks` = `3146.00`
- `neighbour_updates` = `0.00`
- `fps_0p1pct_low` = `808.78`
- `preload_chunks` = `81.00`
- `seed` = `9043.00`
- `preset_long` = `0.00`
- `entity_count_sample_start` = `1.00`
- `sources_placed_total` = `54.00`
- `preload_duration_ms` = `49.00`
- `scheduled_block_ticks` = `0.00`
- `waves_spawned` = `6.00`
- `entity_count_delta` = `0.00`
- `preset_full` = `0.00`
- `block_state_changes` = `2305.00`

### Lighting update (16×16 glowstone reveal/hide) (`lighting_update`)

Category: **Lighting**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `5695.48`, min `509.45`, p50 `5938.24`, p95 `6127.45`, p99 `6195.79`, 1%low `1383.02`, 0.1%low `750.70`, std `698.87`

**Frame time (ms)**  avg `0.18`, p50 `0.17`, p95 `0.22`, p99 `0.48`, p99.9 `1.00`, max `1.96`

**Client tick (ms)**  avg `0.57`, p95 `0.60`, max `0.99`

**Memory**  start `1408 MB`, end `1912 MB`, peak `7826 MB`, GC `4 events / 372 ms`

**FPS over sampling window (ASCII):**

```
5958.5 |                         ██                                                     
5847.2 | █ ██ ████ █    █      █ ███         █ █   █ ███ ███            █ ██ █    █     
5735.9 | █ ██ ██████ █ ██     ██████ █     ███████ █ ███ ████ ██ ███ █ ███████ █ ██  ███
5624.6 |█████ ██████ █ ██  ███████████    ██████████ █████████████████ █████████████████
5513.3 |████████████ █ ███████████████    ████████████████████████████ █████████████████
5402.0 |███████████████████████████████   ██████████████████████████████████████████████
5290.6 |███████████████████████████████   ██████████████████████████████████████████████
5179.3 |████████████████████████████████  ██████████████████████████████████████████████
5068.0 |████████████████████████████████ ███████████████████████████████████████████████
4956.7 |████████████████████████████████ ███████████████████████████████████████████████
4845.4 |████████████████████████████████ ███████████████████████████████████████████████
4734.1 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20980
   1 ms |   20
```

**Extras:**

- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `750.70`
- `preset_long` = `0.00`
- `preload_chunks` = `81.00`
- `toggles` = `22.00`
- `entity_count_sample_end` = `1.00`
- `entity_count_sample_start` = `1.00`
- `seed` = `9007.00`
- `block_state_changes` = `4864.00`
- `neighbour_updates` = `0.00`
- `blocks_per_toggle` = `256.00`
- `entity_count_delta` = `0.00`
- `scheduled_block_ticks` = `0.00`
- `preload_duration_ms` = `50.00`
- `fps_1pct_low` = `1383.02`
- `fps_harmonic_avg` = `5482.87`
- `preset_full` = `0.00`

### Hopper grid 20×20 (transfer storm) (`hopper_grid`)

Category: **Block-Entities**  |  Duration: 23102 ms  |  Sample ticks: 400

**FPS**  avg `4578.54`, min `916.00`, p50 `4803.07`, p95 `4957.86`, p99 `5005.03`, 1%low `1467.67`, 0.1%low `1064.80`, std `547.56`

**Frame time (ms)**  avg `0.22`, p50 `0.21`, p95 `0.27`, p99 `0.50`, p99.9 `0.82`, max `1.09`

**Client tick (ms)**  avg `0.51`, p95 `0.56`, max `1.06`

**Memory**  start `6066 MB`, end `5554 MB`, peak `7744 MB`, GC `4 events / 254 ms`

**FPS over sampling window (ASCII):**

```
4762.6 |                                    █   █     █ █          █        █    █     █
4710.1 |    █                █     █        █   █    ██ █  █      ███     █ ██   █ █ █ █
4657.6 |    █     █ █     █  █  █  ██       █   █  █ ██ ██ █      ███     █████  █ █ █ █
4605.1 |    ██    █ █ █  ██ ██  ██ ██   █  ██   ██ ████ ██ ██   █ ██████  █████  █ █ ███
4552.7 |█  ████   █ █ █  ██ ██  ██ ██   █  ██ █ ██ ████ ██ ███  █ ██████  █████  █ █ ███
4500.2 |█ ██████  █ ███████ ██  █████   ██ ████ ██ ███████ █████████████ █████████ █ ███
4447.7 |█ █████████ ███████████ ███████ ██████████ ███████ █████████████ █████████ █████
4395.2 |██████████████████████████████████████████ █████████████████████ ███████████████
4342.7 |██████████████████████████████████████████ █████████████████████████████████████
4290.2 |██████████████████████████████████████████ █████████████████████████████████████
4237.7 |██████████████████████████████████████████ █████████████████████████████████████
4185.2 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20994
   1 ms |   6
```

**Extras:**

- `preset_quick` = `1.00`
- `fps_0p1pct_low` = `1064.80`
- `preset_long` = `0.00`
- `preload_chunks` = `81.00`
- `entity_count_sample_end` = `1.00`
- `entity_count_sample_start` = `1.00`
- `seed` = `8011.00`
- `hoppers_built` = `400.00`
- `block_state_changes` = `0.00`
- `neighbour_updates` = `0.00`
- `entity_count_delta` = `0.00`
- `scheduled_block_ticks` = `0.00`
- `preload_duration_ms` = `67.00`
- `fps_1pct_low` = `1467.67`
- `fps_harmonic_avg` = `4452.17`
- `preset_full` = `0.00`
- `restocks` = `20.00`

### Comparator storage (8×8 chests + comparators) (`comparator_storage`)

Category: **Block-Entities**  |  Duration: 23103 ms  |  Sample ticks: 400

**FPS**  avg `3343.12`, min `176.88`, p50 `3513.70`, p95 `3710.58`, p99 `3837.30`, 1%low `1061.69`, 0.1%low `595.22`, std `427.87`

**Frame time (ms)**  avg `0.31`, p50 `0.28`, p95 `0.38`, p99 `0.66`, p99.9 `1.08`, max `5.65`

**Client tick (ms)**  avg `0.53`, p95 `0.56`, max `1.01`

**Memory**  start `1550 MB`, end `6184 MB`, peak `6184 MB`, GC `3 events / 1019 ms`

**FPS over sampling window (ASCII):**

```
3742.2 |  █                                                                             
3662.9 |  █                                                                             
3583.6 |  █                                                                        █    
3504.4 |████   █                    ██   █  ██ ██   ██ ███       █   ██  ██   █    █ █ █
3425.1 |█████████                   █████████████  ███████ ███ ███ ████████   ██████ ███
3345.9 |█████████                  ███████████████████████████ █████████████████████ ███
3266.6 |██████████                 █████████████████████████████████████████████████████
3187.4 |██████████                 █████████████████████████████████████████████████████
3108.1 |██████████                 █████████████████████████████████████████████████████
3028.8 |████████████  █ █  █ █  ██ █████████████████████████████████████████████████████
2949.6 |████████████  ████████████ █████████████████████████████████████████████████████
2870.3 |████████████████████████████████████████████████████████████████████████████████
       --------------------------------------------------------------------------------
       start                                                                      end
```

**Frame-time histogram (ms bucket → count):**

```
   0 ms | ████████████████████████████████████████  20948
   1 ms |   47
   2 ms |   4
   5 ms |   1
```

**Extras:**

- `preset_quick` = `1.00`
- `entity_count_sample_end` = `1.00`
- `oscillations` = `20.00`
- `fps_1pct_low` = `1061.69`
- `fps_harmonic_avg` = `3244.95`
- `neighbour_updates` = `0.00`
- `fps_0p1pct_low` = `595.22`
- `preload_chunks` = `81.00`
- `seed` = `8053.00`
- `preset_long` = `0.00`
- `entity_count_sample_start` = `1.00`
- `chests_built` = `64.00`
- `preload_duration_ms` = `50.00`
- `comparators_built` = `64.00`
- `scheduled_block_ticks` = `1152.00`
- `entity_count_delta` = `0.00`
- `preset_full` = `0.00`
- `block_state_changes` = `1152.00`

