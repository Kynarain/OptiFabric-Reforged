# OptiFabric 兼容性清单（完整版）

MC 1.21.1 + Fabric + OptiFabric + OptiFine；全部为**普通启动**（无 debug 开关），每行一次启动。兼容与不兼容**分开列出**，每块内按首字母排序；**本文件收录全部 651 行，不做任何剔除**。
（`_baseline` 是装置自建的对照行 —— 「不装主题模组」，其余全是模组。）

> **这份清单量的是 OptiFabric 2.2.1 时代的那次扫描；其中「不兼容」那一块后来在 2.2.8 / 2.2.10 上逐行重新归因过**
> （对照臂不带 OptiFabric、也不带 OptiFine）：**6 行两臂都通过**（已移出「不兼容」块）、**27 行是我们这边的缺口**、
> **0 行在原版 Fabric 对照里失败**；**sodium 自 2.2.11 起重新写回 `breaks`（硬拒载）** —— 把能找到的缺口修完之后它仍然
> 整帧全黑，所以这里选择拒载而不是警告。除这 6 行的块归属外，逐行数据未改动、未重排。各块自己的说明见下。

| 结果 | 数量 |
|---|---:|
| 兼容（已进世界） | 465 |
| 与 OptiFine 不兼容 | 28 |
| 复测：两臂都通过，与 OptiFine 无冲突 | 6 |
| 纯 Fabric 即坏（与本模组无关） | 8 |
| 声明冲突（2.2.11 起为硬拒载 `breaks`） | 1 |
| 仅到标题界面，未进世界 | 10 |
| 依赖未解析 | 8 |
| 不可测 | 112 |
| 未运行 | 14 |
| **合计** | **651** |

逐行证据与装置缺陷记录见 `compatibility-all.csv`、`WORLD-REPORT.md`、`MATRIX-WORLD.md`（compat-matrix 目录）。

## 兼容（已进世界）（465）

普通启动下出现整合服务器启动行与登录行，且预置存档 level.dat 修改时间前移。

| 模组 | slug | 进世界(秒) |
|---|---|---:|
| _baseline | _baseline | 51.0 |
| [ETF] Entity Texture Features | entitytexturefeatures | 25.0 |
| [Let's Do] Beachparty | lets-do-beachparty | 31.1 |
| [Let's Do] Brewery - Farm&Charm Compat | lets-do-brewery-farmcharm-compat | 21.4 |
| [Let's Do] Candlelight - Farm&Charm compat | lets-do-candlelight-farmcharm-compat | 27.8 |
| [Let's Do] Farm & Charm | lets-do-farm-charm | 21.3 |
| [Let's Do] Meadow | lets-do-meadow | 21.8 |
| [Let's Do] Vinery | lets-do-vinery | 21.4 |
| 3D Skin Layers | 3dskinlayers | 35.7 |
| AdoraBuild: Structures | adorabuild-structures | 24.7 |
| Advanced Loot Info (ALI) | advanced-loot-info | 21.5 |
| Advanced Netherite | advanced-netherite | 24.6 |
| Advancement Plaques | advancement-plaques | 31.7 |
| AdventureZ | adventurez | 21.5 |
| Alternate Current | alternate-current | 35.4 |
| Ambient Environment | ambient-environment | 24.6 |
| AmbientSounds | ambientsounds | 33.0 |
| Animal Feeding Trough | animal_feeding_trough | 51.5 |
| Animatica | animatica | 25.8 |
| Another Furniture | another-furniture | 41.8 |
| AppleSkin | appleskin | 35.1 |
| Aquamirae | aquamirae | 37.2 |
| Architectury API | architectury-api | 43.6 |
| Artifacts | artifacts | 24.9 |
| Athena | athena-ctm | 18.6 |
| AttributeFix | attributefix | 21.5 |
| Auth Me | auth-me | 24.8 |
| AzureLib | azurelib | 25.4 |
| AzureLib Armor | azurelib-armor | 18.4 |
| bad packets | badpackets | 24.6 |
| Bad Wither No Cookie - Reloaded | bad-wither-no-cookie | 24.9 |
| BadOptimizations | badoptimizations | 25.7 |
| Balm | balm | 38.0 |
| Bartering Station | bartering-station | 30.6 |
| Beautify: Refabricated | beautify-refabricated | 36.4 |
| Better Advancements | better-advancements | 24.9 |
| Better Archeology | better-archeology | 24.5 |
| Better Combat | better-combat | 41.2 |
| Better Compatibility Checker | better-compatibility-checker | 24.6 |
| Better Mount HUD | better-mount-hud | 18.4 |
| Better Ping Display [Fabric] | better-ping-display-fabric | 34.4 |
| Better Selection | better-selection | 31.2 |
| Better Statistics Screen | better-stats | 18.6 |
| Better Third Person | better-third-person | 22.0 |
| BetterF3 | betterf3 | 40.6 |
| BetterGrassify | bettergrassify | 18.4 |
| BisectHosting Server Integration Menu | bisect-mod | 21.9 |
| Blur+ | blur-plus | 45.5 |
| Boat Item View | boat-item-view | 24.5 |
| Bookshelf | bookshelf-lib | 30.9 |
| Bountiful | bountiful | 18.4 |
| Bridging Mod | bridging-mod | 27.7 |
| Camera Utils | camera-utils | 21.6 |
| Capes | capes | 21.4 |
| Cardinal Components API | cardinal-components-api | 18.6 |
| Carpet | carpet | 28.0 |
| Carpet AMS Addition | carpet-ams-addition | 21.4 |
| cf--charm-of-undying | cf--charm-of-undying | 31.2 |
| cf--configured | cf--configured | 21.4 |
| cf--connectivity | cf--connectivity | 21.4 |
| cf--cooking-for-blockheads | cf--cooking-for-blockheads | 24.4 |
| cf--crafting-tweaks | cf--crafting-tweaks | 36.7 |
| cf--crafttweaker | cf--crafttweaker | 42.5 |
| cf--cupboard | cf--cupboard | 21.6 |
| cf--cyclops-core | cf--cyclops-core | 27.5 |
| cf--dark-utilities | cf--dark-utilities | 33.4 |
| cf--explorers-compass | cf--explorers-compass | 28.1 |
| cf--farming-for-blockheads | cf--farming-for-blockheads | 21.6 |
| cf--fix-gpu-memory-leak | cf--fix-gpu-memory-leak | 27.8 |
| cf--framework | cf--framework | 24.4 |
| cf--ftb-essentials | cf--ftb-essentials | 44.9 |
| cf--ftb-xmod-compat | cf--ftb-xmod-compat | 40.2 |
| cf--goblin-traders | cf--goblin-traders | 22.1 |
| cf--ichunutil | cf--ichunutil | 26.8 |
| cf--inventory-hud-forge | cf--inventory-hud-forge | 24.4 |
| cf--just-enough-professions-jep | cf--just-enough-professions-jep | 26.8 |
| cf--just-enough-resources-jer | cf--just-enough-resources-jer | 40.6 |
| cf--kleeslabs | cf--kleeslabs | 21.5 |
| cf--loot-integrations | cf--loot-integrations | 22.7 |
| cf--macaws-bridges | cf--macaws-bridges | 30.6 |
| cf--macaws-fences-and-walls | cf--macaws-fences-and-walls | 32.5 |
| cf--macaws-furniture | cf--macaws-furniture | 18.3 |
| cf--macaws-lights-and-lamps | cf--macaws-lights-and-lamps | 40.4 |
| cf--macaws-roofs | cf--macaws-roofs | 38.4 |
| cf--macaws-trapdoors | cf--macaws-trapdoors | 39.5 |
| cf--macaws-windows | cf--macaws-windows | 29.5 |
| cf--openblocks-elevator | cf--openblocks-elevator | 24.4 |
| cf--pehkui | cf--pehkui | 32.1 |
| cf--reborncore | cf--reborncore | 31.1 |
| cf--refined-storage | cf--refined-storage | 46.4 |
| cf--serene-seasons | cf--serene-seasons | 45.4 |
| cf--smartbrainlib | cf--smartbrainlib | 31.1 |
| cf--smooth-chunk-save | cf--smooth-chunk-save | 54.6 |
| cf--starter-kit | cf--starter-kit | 36.5 |
| cf--storage-drawers | cf--storage-drawers | 27.6 |
| cf--structure-essentials-forge-fabric | cf--structure-essentials-forge-fabric | 26.4 |
| cf--tips | cf--tips | 27.7 |
| cf--trash-cans | cf--trash-cans | 21.4 |
| cf--trashslot | cf--trashslot | 21.4 |
| Chat Animation [Smooth Chat] | chatanimation | 38.6 |
| Chat Heads | chat-heads | 45.9 |
| Chat Patches | chatpatches | 18.4 |
| Cherished Worlds | cherished-worlds | 27.3 |
| Chipped | chipped | 37.0 |
| Chunky | chunky | 28.7 |
| CICADA | cicada | 18.4 |
| CIT Resewn | cit-resewn | 21.5 |
| Client Sort | clientsort | 25.6 |
| Client Tweaks | client-tweaks | 18.3 |
| Cloth Config API | cloth-config | 21.3 |
| Clumps | clumps | 24.8 |
| Collective | collective | 27.3 |
| Combat Roll | combat-roll | 18.3 |
| Comforts | comforts | 18.4 |
| Config Manager | configmanager | 29.4 |
| Configured Defaults | configured-defaults | 18.4 |
| Continuity | continuity | 29.6 |
| Controlify (Controller support) | controlify | 25.0 |
| Controlling | controlling | 24.7 |
| Cool Rain | coolrain | 18.3 |
| CorgiLib | corgilib | 18.3 |
| CoroUtil | coroutil | 44.9 |
| Crafting Tweaks | crafting-tweaks | 18.3 |
| CraftPresence | craftpresence | 25.0 |
| Crash Assistant | crash-assistant | 29.5 |
| CreativeCore | creativecore | 59.9 |
| Cristel Lib | cristel-lib | 34.4 |
| Critters and Companions | critters-and-companions | 34.2 |
| Cubes Without Borders | cubes-without-borders | 21.5 |
| Cull Leaves | cull-leaves | 50.8 |
| CustomSkinLoader | customskinloader | 24.4 |
| Debugify | debugify | 28.1 |
| Default Options | default-options | 21.5 |
| Diagonal Fences | diagonal-fences | 29.3 |
| Difficulty Lock | difficulty-lock | 45.3 |
| Disable Custom Worlds Advice | dcwa | 21.5 |
| Distant Horizons | distanthorizons | 34.8 |
| Double Doors | double-doors | 21.7 |
| Dramatic Doors | dramatic-doors | 25.2 |
| Drip Sounds | dripsounds | 28.7 |
| Drippy Loading Screen | drippy-loading-screen | 31.1 |
| Dungeons and Taverns | dungeons-and-taverns | 28.6 |
| Durability Tooltip | durability-tooltip | 28.6 |
| Dynamic Crosshair | dynamiccrosshair | 18.3 |
| Dynamic FPS | dynamic-fps | 24.3 |
| Dynamic Lights | dynamic-lights | 25.3 |
| Dynamic Surroundings | dynamicsurroundingsfabric | 28.8 |
| Dynamic Trees | dynamictrees | 21.9 |
| e4mc | e4mc | 21.5 |
| Easy Anvils | easy-anvils | 32.4 |
| Easy Magic | easy-magic | 27.2 |
| Eating Animation | eating-animation | 22.5 |
| Elytra Slot | elytra-slot | 41.2 |
| EMI | emi | 21.8 |
| EMI Enchanting | emi-enchanting | 18.3 |
| EMI Loot | emi-loot | 28.0 |
| EMI Ores | emi-ores | 44.1 |
| EMIffect | emiffect | 18.6 |
| Emotecraft | emotecraft | 25.0 |
| Enchanting Infuser | enchanting-infuser | 41.6 |
| Enchantment Descriptions | enchantment-descriptions | 35.7 |
| End Remastered | endrem | 24.7 |
| End's Delight | ends-delight | 24.7 |
| Enhanced Attack Indicator | enhanced-attack-indicator | 18.7 |
| EnhancedVisuals | enhancedvisuals | 30.8 |
| Entity Culling | entityculling | 23.5 |
| Euphoria Patches | euphoria-patches | 26.1 |
| Exordium | exordium | 24.7 |
| Explorations | explorations | 29.0 |
| Explorer's Compass | explorers-compass | 22.5 |
| Explorify | explorify | 66.0 |
| Explosive Enhancement | explosive-enhancement | 21.5 |
| Exposure | exposure | 21.6 |
| Fabric API | fabric-api | 21.4 |
| Fabric Language Kotlin | fabric-language-kotlin | 18.3 |
| Fabric Seasons | fabric-seasons | 18.4 |
| Fabric Seasons: Extras | fabric-seasons-extras | 18.5 |
| Fabrishot | fabrishot | 29.0 |
| FallingTree | fallingtree | 24.4 |
| FancyMenu | fancymenu | 36.6 |
| Farmer's Delight Refabricated | farmers-delight-refabricated | 31.3 |
| Fast IP Ping | fast-ip-ping | 24.5 |
| Fast Noise | zfastnoise | 21.4 |
| Fast Scrolling | fast-scrolling | 25.3 |
| FastQuit | fastquit | 32.4 |
| FerriteCore | ferrite-core | 48.0 |
| First-person Model | first-person-model | 29.1 |
| Forge Config API Port | forge-config-api-port | 34.6 |
| Formations (Structure Library) | formations | 22.0 |
| Formations Nether | formations-nether | 36.9 |
| Formations Overworld | formations-overworld | 24.8 |
| FPS Reducer | fps-reducer | 18.4 |
| Friends&Foes (Fabric/Quilt) | friends-and-foes | 25.6 |
| Full Brightness Toggle | full-brightness-toggle | 26.6 |
| Fzzy Config | fzzy-config | 47.6 |
| Galosphere | galosphere | 18.6 |
| Gamma Utils (Fullbright) | gamma-utils | 34.5 |
| Geophilic | geophilic | 43.0 |
| GlitchCore | glitchcore | 25.7 |
| Global Packs | globalpacks | 24.5 |
| GPUBooster | gputape | 47.9 |
| Grass Overhaul | grass-overhaul | 18.4 |
| Handcrafted | handcrafted | 34.0 |
| Hardcore Revival | hardcore-revival | 24.5 |
| Highlight | highlight | 24.8 |
| I18nUpdateMod | i18nupdatemod | 30.6 |
| Iceberg | iceberg | 28.1 |
| IMBlocker | imblocker-original | 27.7 |
| Immersive Aircraft | immersive-aircraft | 25.1 |
| Immersive Armors | immersive-armors | 25.0 |
| Immersive Melodies | immersive-melodies | 25.1 |
| Inventory Profiles Next | inventory-profiles-next | 27.8 |
| InventoryHUD+ | inventoryhudplus | 35.7 |
| InvMove | invmove | 38.2 |
| InvMoveCompats | invmovecompats | 35.3 |
| Item Borders | item-borders | 28.1 |
| Item Highlighter | item-highlighter | 24.9 |
| Ixeris | ixeris | 22.0 |
| Jade 🔍 | jade | 18.5 |
| JamLib | jamlib | 27.8 |
| Just Enough Breeding (JEBr) | justenoughbreeding | 25.2 |
| Just Enough Effect Descriptions (JEED) | just-enough-effect-descriptions-jeed | 35.6 |
| Just Enough Items (JEI) | jei | 24.5 |
| Just Enough Resources (JER) | just-enough-resources-jer | 18.3 |
| Just Zoom | just-zoom | 24.4 |
| JustEnoughCharacters | justenoughcharacters | 18.4 |
| Kaleidoscope Cookery | kaleidoscope-cookery | 46.0 |
| kennytvs-epic-force-close-loading-screen-mod-for-fabric | forcecloseworldloadingscreen | 21.6 |
| Kiwi 🥝 | kiwi | 18.3 |
| KleeSlabs | kleeslabs | 37.2 |
| Konkrete | konkrete | 54.2 |
| Krypton | krypton | 27.4 |
| Ksyxis | ksyxis | 18.4 |
| LambDynamicLights - Dynamic Lights | lambdynamiclights | 21.5 |
| Language Reload | language-reload | 33.5 |
| Leaves Be Gone | leaves-be-gone | 18.4 |
| libIPN | libipn | 38.2 |
| LibJF | libjf | 24.4 |
| Library Ferret | library-ferret | 30.7 |
| Litematica | litematica | 22.2 |
| Lithium | lithium | 18.4 |
| Load My F***ing Tags | lmft | 21.6 |
| Log Begone | log-begone | 24.5 |
| Lootr | lootr | 21.7 |
| Macaw's Bridges | macaws-bridges | 25.2 |
| Macaw's Doors | macaws-doors | 28.6 |
| Macaw's Fences and Walls | macaws-fences-and-walls | 40.4 |
| Macaw's Furniture | macaws-furniture | 36.5 |
| Macaw's Roofs | macaws-roofs | 27.9 |
| Macaw's Stairs | macaws-stairs | 26.1 |
| Macaw's Windows | macaws-windows | 22.6 |
| Main Menu Credits | main-menu-credits | 35.7 |
| Make Bubbles Pop | make_bubbles_pop | 34.8 |
| MaLiLib | malilib | 45.6 |
| Melody | melody | 23.3 |
| MES - Moog's End Structures | mes-moogs-end-structures | 18.3 |
| MidnightLib | midnightlib | 24.7 |
| MiniHUD | minihud | 24.6 |
| MissingMons [cobblemon] | missingmons-cobblemon | 21.8 |
| MixinTrace | mixintrace | 27.6 |
| Mod Menu | modmenu | 36.3 |
| Model Gap Fix | modelfix | 18.3 |
| More Chat History | morechathistory | 41.1 |
| Mouse Tweaks | mouse-tweaks | 24.4 |
| MRU | mru | 22.3 |
| Music Delay Remover (Infinite Music) | infinite-music | 18.3 |
| MVS - Moog's Voyager Structures | moogs-voyager-structures | 18.3 |
| Naturalist | naturalist | 25.1 |
| Nature's Compass | natures-compass | 21.8 |
| Nature's Spirit | natures-spirit | 30.4 |
| Neat | neat | 40.8 |
| Necronomicon API | necronomicon | 44.1 |
| Neruina - Ticking Entity Fixer | neruina | 23.6 |
| Nether Chested | nether-chested | 21.6 |
| No Resource Pack Warnings | no-resource-pack-warnings | 24.5 |
| Not Enough Animations | not-enough-animations | 53.2 |
| Not Enough Crashes | notenoughcrashes | 18.7 |
| Nuit | nuit | 25.7 |
| Nyf's Spiders | nyfs-spiders | 24.8 |
| Ocean's Delight | oceans-delight | 28.7 |
| Oh The Trees You'll Grow | oh-the-trees-youll-grow | 41.2 |
| OptiGUI | optigui | 18.4 |
| Overflowing Bars | overflowing-bars | 32.2 |
| Packet Fixer | packet-fixer | 18.5 |
| Paginated Advancements & Custom Frames | paginatedadvancements | 37.2 |
| Particle Rain | particle-rain | 22.8 |
| Particular ✨ | particular | 25.5 |
| Paxi | paxi | 51.0 |
| Pehkui | pehkui | 35.2 |
| Philips Ruins | philips-ruins | 36.4 |
| Physics Mod | physicsmod | 25.0 |
| Pick Up Notifier | pick-up-notifier | 31.2 |
| Ping Wheel | ping-wheel | 21.6 |
| Platform | platform | 18.8 |
| playerAnimator | playeranimator | 22.0 |
| Pokeblocks | pokeblocks | 21.3 |
| Polymorph | polymorph | 18.4 |
| Presence Footsteps | presence-footsteps | 33.0 |
| Prickle | prickle | 22.2 |
| Puzzles Lib | puzzles-lib | 27.9 |
| Reactive Music | reactive-music | 27.7 |
| Rebind Narrator | rebind-narrator | 37.2 |
| Regions Unexplored | regions-unexplored | 31.2 |
| Repurposed Structures - Fabric | repurposed-structures-fabric | 25.8 |
| Resourceful Config | resourceful-config | 24.4 |
| Resourceful Lib | resourceful-lib | 18.3 |
| Resourcify | resourcify | 29.8 |
| Respackopts | respackopts | 21.4 |
| Rhino | rhino | 34.2 |
| Ribbits | ribbits | 25.0 |
| RightClickHarvest | rightclickharvest | 21.8 |
| Roughly Enough Items (REI) | rei | 31.4 |
| sample--cf-almanac-lib | sample--cf-almanac-lib | 37.8 |
| sample--cf-better-combat-by-daedelus | sample--cf-better-combat-by-daedelus | 36.4 |
| sample--cf-catalogue | sample--cf-catalogue | 29.5 |
| sample--cf-chunk-sending-forge-fabric | sample--cf-chunk-sending-forge-fabric | 24.7 |
| sample--cf-clickable-advancements | sample--cf-clickable-advancements | 21.5 |
| sample--cf-client-crafting | sample--cf-client-crafting | 38.4 |
| sample--cf-connectivity | sample--cf-connectivity | 35.8 |
| sample--cf-croptopia | sample--cf-croptopia | 24.9 |
| sample--cf-cupboard | sample--cf-cupboard | 59.9 |
| sample--cf-endremastered | sample--cf-endremastered | 24.4 |
| sample--cf-entity-texture-features-fabric | sample--cf-entity-texture-features-fabric | 25.0 |
| sample--cf-expandability | sample--cf-expandability | 33.0 |
| sample--cf-fast-async-world-save-forge-fabric | sample--cf-fast-async-world-save-forge-fabric | 27.7 |
| sample--cf-ferritecore-fabric | sample--cf-ferritecore-fabric | 18.3 |
| sample--cf-fix-gpu-memory-leak | sample--cf-fix-gpu-memory-leak | 31.6 |
| sample--cf-framework | sample--cf-framework | 25.1 |
| sample--cf-ftb-essentials | sample--cf-ftb-essentials | 21.5 |
| sample--cf-ftb-xmod-compat | sample--cf-ftb-xmod-compat | 38.4 |
| sample--cf-goblin-traders | sample--cf-goblin-traders | 21.3 |
| sample--cf-iceberg-fabric | sample--cf-iceberg-fabric | 44.1 |
| sample--cf-konkrete-fabric | sample--cf-konkrete-fabric | 26.9 |
| sample--cf-light-overlay | sample--cf-light-overlay | 18.3 |
| sample--cf-login-protection | sample--cf-login-protection | 30.2 |
| sample--cf-loot-integrations | sample--cf-loot-integrations | 21.5 |
| sample--cf-openblocks-elevator | sample--cf-openblocks-elevator | 25.0 |
| sample--cf-recipe-essentials-forge-fabric | sample--cf-recipe-essentials-forge-fabric | 24.5 |
| sample--cf-refurbished-furniture | sample--cf-refurbished-furniture | 41.3 |
| sample--cf-simple-discord-rich-presence | sample--cf-simple-discord-rich-presence | 48.9 |
| sample--cf-smooth-chunk-save | sample--cf-smooth-chunk-save | 29.7 |
| sample--cf-structure-essentials-forge-fabric | sample--cf-structure-essentials-forge-fabric | 35.1 |
| sample--cf-vein-mining | sample--cf-vein-mining | 28.1 |
| sample--mr-aligning-scepters | sample--mr-aligning-scepters | 27.6 |
| sample--mr-archive-downloader | sample--mr-archive-downloader | 18.5 |
| sample--mr-axolotl-buckets | sample--mr-axolotl-buckets | 67.6 |
| sample--mr-banner-flags | sample--mr-banner-flags | 18.3 |
| sample--mr-better-climbing | sample--mr-better-climbing | 18.4 |
| sample--mr-better-end-sky | sample--mr-better-end-sky | 31.7 |
| sample--mr-bettergrassify | sample--mr-bettergrassify | 31.2 |
| sample--mr-bookshelf-lib | sample--mr-bookshelf-lib | 30.7 |
| sample--mr-chaos-core | sample--mr-chaos-core | 24.8 |
| sample--mr-craftify | sample--mr-craftify | 54.2 |
| sample--mr-dynamiccrosshaircompat | sample--mr-dynamiccrosshaircompat | 27.7 |
| sample--mr-enchantment-calculator | sample--mr-enchantment-calculator | 26.6 |
| sample--mr-entityculling | sample--mr-entityculling | 71.6 |
| sample--mr-entitytexturefeatures | sample--mr-entitytexturefeatures | 47.0 |
| sample--mr-fabric-api | sample--mr-fabric-api | 18.3 |
| sample--mr-faster-random | sample--mr-faster-random | 28.0 |
| sample--mr-ferrite-core | sample--mr-ferrite-core | 24.8 |
| sample--mr-figura-lite | sample--mr-figura-lite | 31.7 |
| sample--mr-grass-overhaul | sample--mr-grass-overhaul | 39.3 |
| sample--mr-helpful-armorers | sample--mr-helpful-armorers | 18.6 |
| sample--mr-infinity-hoe | sample--mr-infinity-hoe | 42.8 |
| sample--mr-konkrete | sample--mr-konkrete | 18.4 |
| sample--mr-lets-do-furniture | sample--mr-lets-do-furniture | 52.9 |
| sample--mr-lgbtqdesignedtimer | sample--mr-lgbtqdesignedtimer | 25.6 |
| sample--mr-libipn | sample--mr-libipn | 38.0 |
| sample--mr-lithium | sample--mr-lithium | 20.4 |
| sample--mr-mca-selector | sample--mr-mca-selector | 18.3 |
| sample--mr-modmenu | sample--mr-modmenu | 30.0 |
| sample--mr-more-grindstone-variants | sample--mr-more-grindstone-variants | 21.3 |
| sample--mr-nexus-reds-addon | sample--mr-nexus-reds-addon | 18.3 |
| sample--mr-patchouli | sample--mr-patchouli | 18.3 |
| sample--mr-patpat | sample--mr-patpat | 28.0 |
| sample--mr-player-corpse | sample--mr-player-corpse | 48.6 |
| sample--mr-resourceful-config | sample--mr-resourceful-config | 26.1 |
| sample--mr-sandy-husks | sample--mr-sandy-husks | 24.4 |
| sample--mr-scrollabletooltips | sample--mr-scrollabletooltips | 21.9 |
| sample--mr-shield-api | sample--mr-shield-api | 34.1 |
| sample--mr-simple-voice-chat | sample--mr-simple-voice-chat | 18.3 |
| sample--mr-stoneworks | sample--mr-stoneworks | 27.8 |
| sample--mr-syncmatica | sample--mr-syncmatica | 18.3 |
| sample--mr-threadtweak | sample--mr-threadtweak | 25.4 |
| Satin Free Wakes | satin-free-wakes | 21.4 |
| ScalableLux | scalablelux | 32.4 |
| Searchables | searchables | 24.8 |
| Serene Seasons | serene-seasons | 26.5 |
| Shulker Box Tooltip | shulkerboxtooltip | 57.5 |
| Simple Voice Chat | simple-voice-chat | 39.8 |
| Simply Swords | simply-swords | 35.0 |
| Sit | bl4cks-sit | 24.4 |
| Skyboxify | skyboxify | 45.4 |
| SmartBrainLib | smartbrainlib | 31.7 |
| Smooth Scrolling | smooth-scroll | 21.4 |
| Smooth Swapping | smooth-swapping | 29.7 |
| Snow! Real Magic! ⛄ | snow-real-magic | 40.3 |
| Sound Physics Remastered | sound-physics-remastered | 35.0 |
| Sounds | sound | 26.4 |
| spark | spark | 37.0 |
| Sparse Structures | sparsestructures | 50.9 |
| Spawn Animations | spawn-animations | 35.3 |
| Spell Engine | spell-engine | 29.3 |
| Spell Power Attributes | spell-power | 21.9 |
| StackDeobfuscator | stackdeobf | 18.4 |
| Starter Kit | starter-kit | 29.0 |
| Status Effect Bars | status-effect-bars | 34.4 |
| Stoneworks | stoneworks | 48.7 |
| Structory | structory | 18.3 |
| Structory: Towers | structory-towers | 32.5 |
| Structure Layout Optimizer | structure-layout-optimizer | 18.5 |
| SuperMartijn642's Config Lib | supermartijn642s-config-lib | 39.9 |
| SuperMartijn642's Core Lib | supermartijn642s-core-lib | 25.1 |
| TCDCommons API | tcdcommons | 33.2 |
| Tectonic | tectonic | 28.6 |
| TerraBlender | terrablender | 46.0 |
| Terralith | terralith | 25.4 |
| Text Placeholder API | placeholder-api | 54.1 |
| The Lost Castle | the-lost-castle | 28.6 |
| ThreadTweak | threadtweak | 41.6 |
| Tips | tips | 22.1 |
| Tom's Simple Storage Mod | toms-storage | 26.9 |
| Towns and Towers | towns-and-towers | 25.4 |
| Trade Cycling | trade-cycling | 39.3 |
| Trading Post | trading-post | 22.4 |
| TrashSlot | trashslot | 22.6 |
| Traveler's Backpack | travelersbackpack | 28.1 |
| Traveler's Titles | travelers-titles | 18.3 |
| Tree Harvester | tree-harvester | 18.4 |
| Trek | trek | 18.4 |
| Trinkets | trinkets | 18.3 |
| True Ending - Ender Dragon Overhaul | true-ending | 25.4 |
| TslatEntityStatus | tslatentitystatus | 21.6 |
| Tweakeroo | tweakeroo | 24.4 |
| TxniLib | txnilib | 18.4 |
| ukulib | ukulib | 24.4 |
| UniLib | unilib | 18.4 |
| Vanilla Backport | vanillabackport | 42.4 |
| VeinMiner | veinminer | 59.4 |
| VeinMiner Enchantment | veinminer-enchantment | 28.7 |
| VeinMiner Hotkey | veinminer-client | 58.4 |
| Villager Names | villager-names-serilum | 23.1 |
| Villages&Pillages | villages-and-pillages | 21.3 |
| Visual Workbench | visual-workbench | 26.4 |
| Visuality | visuality | 31.3 |
| Wakes | wakes | 48.1 |
| Wavey Capes | wavey-capes | 21.5 |
| Waystones | waystones | 21.7 |
| What Are They Up To (Watut) | what-are-they-up-to | 18.4 |
| When Dungeons Arise | when-dungeons-arise | 26.4 |
| WorldEdit | worldedit | 18.6 |
| Xaero's Minimap | xaeros-minimap | 49.2 |
| Xaero's World Map | xaeros-world-map | 21.4 |
| XaeroPlus | xaeroplus | 38.3 |
| YDM's Weapon Master | weaponmaster | 24.4 |
| Yeetus Experimentus | yeetus-experimentus | 18.5 |
| Yes Steve Model | yes-steve-model | 25.5 |
| YetAnotherConfigLib (YACL) | yacl | 26.1 |
| You Shall Not Spawn! | you-shall-not-spawn | 25.1 |
| Your Options Shall Be Respected (YOSBR) | yosbr | 24.7 |
| YUNG's API | yungs-api | 23.3 |
| YUNG's Extras | yungs-extras | 28.2 |
| YUNG's Menu Tweaks | yungs-menu-tweaks | 25.2 |
| Zombie Awareness | zombie-awareness | 27.7 |
| Zume | zume | 29.0 |

## 与 OptiFine 不兼容（28）

不装本模组（纯 Fabric + OptiFine）能过标题界面，装了过不去。**但这 28 行要修的是我们这边，不是模组作者那边**：逐行复测的对照臂是「原版 Fabric + 该模组 + 扫描当时暂存的那套依赖」，**不带 OptiFabric、也不带 OptiFine**；同一批模组在对照里全部到得了标题界面，而失败发生在**本模组交给加载器的那些类**上 —— 也就是说这是「我们还没修好」，不是「这个模组有毛病」。原「33」行里另有 6 行两臂都通过、根本不属于不兼容，已移到下面的「复测：两臂都通过」一块。
机理上：25 行各对应一处具体的字节码缺口（OptiFine 重新编译时改掉或内联掉的调用点，或者删掉、改名的辅助方法），另 3 行（`ebe`、`sample--mr-betternether`、`the-shooting-star-demo`）是 OptiFine `Config` 在 Fabric 客户端入口点阶段还没初始化的生命周期问题，没有任何 fixer 覆盖它。**2.2.10 关掉了下面十行「已记录的那处失败」**：`cut-through`、`deeperdarker`、`modernfix`、`no-chat-reports`、`particle-core`、`moreculling`、`shatterbyte-lib`（这一行还进到了世界）、`supplementaries` 的 `class_836` 那处，以及两个 `immediatelyfast` 行的**第一处**失败；下表的 iris 与 immediatelyfast 两行就是反例——它们只是走到了同一个模组里的下一处，并**没有**修好。

| 模组 | slug | 进世界(秒) |
|---|---|---:|
| (Sodium) Chloride | chloride |  |
| [EMF] Entity Model Features | entity-model-features |  |
| Carry On | carry-on |  |
| Cut Through | cut-through |  |
| Deeper and Darker | deeperdarker |  |
| Enhanced Block Entities | ebe |  |
| ImmediatelyFast | immediatelyfast | 未进世界（2.2.10：前两处已修，第三个 mixin 仍失败；与下面 sample--mr-immediatelyfast 同一个 jar） |
| Indium | indium |  |
| Iris Shaders | iris | 未进世界（2.2.10：`class_761` 那处已修，仍在更后面的 iris mixin 上失败） |
| Modern UI | modern-ui |  |
| ModernFix | modernfix |  |
| More Culling | moreculling |  |
| No Chat Reports | no-chat-reports |  |
| Open Parties and Claims | open-parties-and-claims |  |
| Particle Core | particle-core |  |
| Polytone | polytone |  |
| Puzzle | puzzle |  |
| Reese's Sodium Options | reeses-sodium-options |  |
| ReplayMod | replaymod |  |
| sample--mr-betternether | sample--mr-betternether |  |
| sample--mr-immediatelyfast | sample--mr-immediatelyfast |  |
| sample--mr-spectrumjei | sample--mr-spectrumjei |  |
| ShatterLib / OctoLib | shatterbyte-lib |  |
| Sodium Extra | sodium-extra |  |
| Sodium Shadowy Path Blocks (SSPB) | sodium-shadowy-path-blocks |  |
| Supplementaries | supplementaries |  |
| The Shooting Star (Demo) | the-shooting-star-demo |  |
| ToolTipFix | tooltipfix |  |

> **这一块里有 7 行根本不是被点名模组自己的问题，只写模组 jar 名字的逐行清单会一直把它们归错**：5 行是经由暂存依赖 **sodium** 暴露出来的（`sodium-extra`、`reeses-sodium-options`、`indium`、`sodium-shadowy-path-blocks`、`chloride` —— 失败的是 sodium 自己的 mixin，不是这些模组），`sample--mr-betternether` 是暂存依赖 **bclib** 的客户端入口点，`sample--mr-spectrumjei` 是暂存依赖 **modonomicon** 的 mixin。（重新归因报告正文把这一项写成「6 行」，但它列出的正是这 7 行；这里按逐行数据记 7 行。）

## 复测：两臂都通过，与 OptiFine 无冲突（6）

这 6 行原先被列进「与 OptiFine 不兼容」，逐行复测量到**两臂都通过**：不带 OptiFabric 的原版 Fabric 对照到得了标题界面，装本模组的栈（2.2.8，2.2.10 复测同样）也到得了，两边都没有点名该模组的 mixin / 注入错误。所以它们**不属于不兼容**，原先那条结论是 2.2.1 时代那套对照方法的产物。
**C2ME 在 1.21.1 上明确不是不兼容**：它到得了标题界面，也进得了世界（在世界里连续跑 113 秒、零条 `[ERROR]`）；`docs/compatibility/MATRIX.md` 里那条 `class_761` 失败量的是 2.2.1，2.2.3 的 `LambdaMethodRefFix` / `LocalSlotLayoutFix` 登记已经把它去掉了。C2ME 自己元数据里那条 `breaks: optifabric` 点的是旧 id，本线用的 id 是 `optifabric_reforged`，所以那条声明在本线上根本不会命中。

| 模组 | slug | 进世界(秒) |
|---|---|---:|
| Bobby | bobby | 两臂均到标题界面（2.2.10 复测） |
| Concurrent Chunk Management Engine (Fabric) | c2me-fabric | 两臂均到标题界面（2.2.10 复测；2.2.3 起进过世界） |
| Falling Leaves | fallingleaves | 两臂均到标题界面（2.2.10 复测） |
| Freecam | freecam | 两臂均到标题界面（2.2.10 复测） |
| sample--mr-bedrockify | sample--mr-bedrockify | 两臂均到标题界面（2.2.10 复测） |
| sample--mr-c2me-fabric | sample--mr-c2me-fabric | 两臂均到标题界面（2.2.10 复测；与 c2me-fabric 同一个 jar） |

## 纯 Fabric 即坏（与本模组无关）（8）

不装本模组也一样坏。

| 模组 | slug | 进世界(秒) |
|---|---|---:|
| cf--aether | cf--aether |  |
| cf--better-compatibility-checker | cf--better-compatibility-checker |  |
| cf--the-twilight-forest | cf--the-twilight-forest |  |
| cf--torchmaster | cf--torchmaster |  |
| sample--cf-roughly-enough-items | sample--cf-roughly-enough-items |  |
| sample--cf-the-twilight-forest | sample--cf-the-twilight-forest |  |
| sample--mr-cobblesmartphone-pokedex | sample--mr-cobblesmartphone-pokedex |  |
| sample--mr-moonrise-compats | sample--mr-moonrise-compats |  |

> **更正:The Twilight Forest 那两行已被后来的专项调查取代。** 那两行(`cf--the-twilight-forest`、
> `sample--cf-the-twilight-forest`)量的是 **OptiFabric 2.2.1** 时代的结果,当时的失败点名 `class_156` / `class_638` 的
> mixin 变换;这两处后来都在 **2.2.3** 修好,2.2.8 又把 `class_5944`(`ShaderProgram`)的 null 补完。最新一次专项调查
> (`tf-must`,1.21.1)量到 **Twilight Forest 能进世界,但是间歇性的(11 个带 TF 的臂里 4 个;不带 TF 的同一套 3/3)**,失败的那次
> 是客户端里一处闲置的「世界打开交接」被丢掉、没有异常;**重试有效,但别急着判它卡死** —— 最慢的一次是标题界面后 122.7 s
> 才进场,给世界加载**两到三分钟**再下结论;真没打开就重启客户端,失败那次的存档不会坏。本表的逐行数据**未改动、未重排**:唯一动了位置的是下面那条重新归因说明涉及的 6 行(它们从「与 OptiFine 不兼容」移到了「复测:两臂都通过」),上面两行也保留原样,
> 只在这一条下面加了这段说明;**完整结论见 1.21.x 线上仓库的 `docs/COMPATIBILITY.md` 与 `docs/COMPATIBILITY_CN.md`
> (以及 `release/notes/mc1.21.1.md`)。**

## 声明冲突（2.2.11 起为硬拒载）（1）

模组自己在元数据里声明与本模组冲突。**2.2.11 起写进 `breaks`**：本模组 `fabric.mod.json` 的 `breaks` 里含 sodium（2.2.8 加过、2.2.10 撤掉、2.2.11 又加回来），`breaks` 点到已存在的模组是硬拒载（求解器给 `NEG_HARD_DEP`，加载器直接 `Incompatible mods found!`，整个实例都起不来），而 `conflicts` 只打印一条 `Warnings were found!`、实例照常启动。**为什么这次选择拒载**：在 1.21.1 + Sodium 0.8.13 上，那串缺口已经被走完 —— 五处修复全部到位之后，sodium 自己的 mixin **全部应用成功**（`Mixin transformation of` 与 `InjectionError` 均为 0）、客户端到得了标题界面、也进得了世界（区块构建线程已启动、无崩溃报告），但**整帧全黑**；随后两次单变量实验排除了仅剩的解释（把唯一的渲染槽让给 sodium、以及关掉 OptiFine 的 Fast Render），两者都仍然全黑。也就是说**渲染器确实不是"某一处调用点"的问题，而是两个渲染器争同一条地形管线**，这不再是逐个修缺口能解决的事；警告只会让用户拿到一个"能启动、永远不画画"的游戏。

| 模组 | slug | 进世界(秒) |
|---|---|---:|
| Sodium | sodium | 无法启动（2.2.11：`breaks` 硬拒载。实测：修完五处缺口后 mixin 全通、能进世界，但整帧全黑） |

## 仅到标题界面，未进世界（10）

到了主菜单但从未打开存档；不归因于本模组。

| 模组 | slug | 进世界(秒) |
|---|---|---:|
| Amendments | amendments |  |
| cf--creeper-overhaul | cf--creeper-overhaul |  |
| cf--valhelsia-core | cf--valhelsia-core |  |
| Cobblemon | cobblemon |  |
| Moonlight Lib | moonlight |  |
| oωo (owo-lib) | owo-lib |  |
| sample--cf-valhelsia-core | sample--cf-valhelsia-core |  |
| sample--mr-cobblemon-hug | sample--mr-cobblemon-hug |  |
| sample--mr-dynamic-fps | sample--mr-dynamic-fps |  |
| sample--mr-sophisticated-core-(unofficial-fabric-port) | sample--mr-sophisticated-core-(unofficial-fabric-port) |  |

## 依赖未解析（8）

加载器在解析阶段就拒绝（缺少必需依赖）。

| 模组 | slug | 进世界(秒) |
|---|---|---:|
| Biomes O' Plenty | biomes-o-plenty |  |
| Carpet Org Addition | carpet-org-addition |  |
| Carpet TIS Addition | carpet-tis-addition |  |
| sample--mr-indium | sample--mr-indium |  |
| sample--mr-iris | sample--mr-iris |  |
| sample--mr-sodium | sample--mr-sodium |  |
| Sodium Options API | sodium-options-api |  |
| Zoomify (Zoom) | zoomify |  |

## 不可测（112）

多为服务端专属或没有可用的 1.21.1 Fabric 文件。

| 模组 | slug | 进世界(秒) |
|---|---|---:|
| [Let's Do] Bakery - Farm&Charm Compat | lets-do-bakery-farmcharm-compat |  |
| [Let's Do] HerbalBrews | lets-do-herbalbrews |  |
| Accessories | accessories |  |
| Ad Astra | ad-astra |  |
| Almanac | almanac |  |
| AsyncParticles | asyncparticles |  |
| Axiom | axiom |  |
| BCLib | bclib |  |
| Better Beds | better-beds |  |
| Better Block Entities | better-block-entities |  |
| BetterEnd | betterend |  |
| BetterNether | betternether |  |
| Bosses of Mass Destruction | bosses-of-mass-destruction |  |
| Camera Overhaul | cameraoverhaul |  |
| Carpet Extra | carpet-extra |  |
| Carpet-Fixes | carpet-fixes |  |
| Cave Dust | cave-dust |  |
| Charm of Undying | charm-of-undying |  |
| Chef's Delight - Farmer's Delight Villagers | chefs-delight |  |
| ChoiceTheorem's Overhauled Village | ct-overhaul-village |  |
| CobbleDollars [Cobblemon Addon] | cobbledollars |  |
| CobbleFurnies | cobblefurnies |  |
| Cobblemon additions | cobblemon-additions |  |
| Cobblemon Capture XP | cobblemon-capture-xp |  |
| Cobblemon Fight or Flight Reborn | cobblemon-fight-or-flight-reborn |  |
| Cobblemon PokeNav | cobblemon-pokenav |  |
| Cobblemon Tim Core | cobblemon-tim-core |  |
| Cobblemon Trainer Battle | cobblemon-trainer-battle |  |
| Cobblemon: Legendary Monuments | legendary-monuments |  |
| Cobblemon: Mega Showdown | cobblemon-mega-showdown |  |
| CobbleThemes | cobblethemes |  |
| Cobbreeding | cobbreeding |  |
| Cobweb | cobweb |  |
| Crawl | crawl |  |
| Creeper Overhaul | creeper-overhaul |  |
| Cull Less Leaves | cull-less-leaves |  |
| Custom Splash Screen | custom-splash-screen |  |
| Do a Barrel Roll | do-a-barrel-roll |  |
| Every Compat (Wood Good) | every-compat |  |
| Fabric Seasons: Delight Compat | fabric-seasons-delight-compat |  |
| Farmer's Knives | farmers-knives |  |
| Fast Paintings | fast-paintings |  |
| Faster Random | faster-random |  |
| Flashback | flashback |  |
| Fog Overrides | fogoverrides |  |
| Fusion (Connected Textures) | fusion-connected-textures |  |
| GugleCarpetAddition | gca |  |
| Hearths | hearths |  |
| Held Item Info | held-item-info |  |
| Hold My Items | hold-my-items |  |
| Illager Invasion | illager-invasion |  |
| Immersive UI | immersive-ui |  |
| Incendium Legacy | incendium |  |
| JourneyMap | journeymap |  |
| Jump Over Fences | jump-over-fences |  |
| Kambrik | kambrik |  |
| LAN World Plug-n-Play (mcwifipnp) | mcwifipnp |  |
| Legendary Tooltips | legendary-tooltips |  |
| Let Me Despawn | lmd |  |
| Lithostitched | lithostitched |  |
| Magnum Torch | magnum-torch |  |
| MmmMmmMmmMmm | mmmmmmmmmmmm |  |
| Monsters in the Closet | monsters-in-the-closet |  |
| MoreCobblemonTweaks | more-cobblemon-tweaks |  |
| Navas ZA Megas | navas-zamega |  |
| NetherPortalFix | netherportalfix |  |
| Not Enough Recipe Book [NERB] | notenoughrecipebook |  |
| Noxesium | noxesium |  |
| Nuit Interop | nuit-interop |  |
| Nvidium | nvidium |  |
| Oh The Biomes We've Gone | oh-the-biomes-weve-gone |  |
| Open Loader | open-loader |  |
| Particle Effects | particle-effects |  |
| Radical Cobblemon Trainers | rctmod |  |
| Radical Cobblemon Trainers API | rctapi |  |
| Rechiseled | rechiseled |  |
| Resource Pack Overrides | resource-pack-overrides |  |
| Roughly Enough Professions (REP) | roughly-enough-professions-rep |  |
| Sable | sable |  |
| Sawmill | universal-sawmill |  |
| ServerCore | servercore |  |
| Servux | servux |  |
| Shoulder Surfing Reloaded | shoulder-surfing-reloaded |  |
| Show Me Your Skin! | show-me-your-skin |  |
| Simple Hats | simple-hats |  |
| Simple Rich Discord Presence | srdp |  |
| Smarter Farmers (farmers replant) | smarter-farmers-farmers-replant |  |
| Sodium Extras | sodium-extras |  |
| Sodium Leaf Culling | sodiumleafculling |  |
| Sodium Options Mod Compat | sodium-options-mod-compat |  |
| Sophisticated Backpacks (Unoffical Fabric port) | sophisticated-backpacks-(unoffical-fabric-port) |  |
| Sophisticated Core (Unofficial Fabric port) | sophisticated-core-(unofficial-fabric-port) |  |
| Sophisticated Storage (Unofficial Fabric port) | sophisticated-storage-(unofficial-fabric-port) |  |
| Stendhal | stendhal |  |
| Subtle Effects | subtle-effects |  |
| SwingThrough | swingthrough |  |
| The Aether | aether |  |
| The Bumblezone - Fabric | the-bumblezone-fabric |  |
| uku's Armor HUD | ukus-armor-hud |  |
| ViaFabricPlus | viafabricplus |  |
| VillagersPlus | villagersplus |  |
| Xaero Zoomout | xaero-zoomout |  |
| YUNG's Better Desert Temples | yungs-better-desert-temples |  |
| YUNG's Better Dungeons | yungs-better-dungeons |  |
| YUNG's Better End Island | yungs-better-end-island |  |
| YUNG's Better Jungle Temples | yungs-better-jungle-temples |  |
| YUNG's Better Mineshafts | yungs-better-mineshafts |  |
| YUNG's Better Nether Fortresses | yungs-better-nether-fortresses |  |
| YUNG's Better Ocean Monuments | yungs-better-ocean-monuments |  |
| YUNG's Better Strongholds | yungs-better-strongholds |  |
| YUNG's Better Witch Huts | yungs-better-witch-huts |  |
| YUNG's Bridges | yungs-bridges |  |

## 未运行（14）

本轮没有跑到。

| 模组 | slug | 进世界(秒) |
|---|---|---:|
| cf--macaws-doors | cf--macaws-doors |  |
| Essential Mod | essential |  |
| Geckolib | geckolib |  |
| Patchouli | patchouli |  |
| Prism | prism-lib |  |
| Remove Reloading Screen | rrls |  |
| sample--cf-dynamiclights-reforged | sample--cf-dynamiclights-reforged |  |
| sample--cf-memory-settings | sample--cf-memory-settings |  |
| sample--cf-twigs | sample--cf-twigs |  |
| sample--mr-better-arthropods | sample--mr-better-arthropods |  |
| sample--mr-dark-paintings | sample--mr-dark-paintings |  |
| sample--mr-dine | sample--mr-dine |  |
| Sodium Dynamic Lights | sodium-dynamic-lights |  |
| Very Many Players (Fabric) | vmp-fabric |  |
