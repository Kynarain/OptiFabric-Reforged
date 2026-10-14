# Which mods work next to OptiFabric

Every mod this project has run with OptiFabric, with the result for each of the four lines. Read the row of the mod
you want and the column of the line you use.

**This is one sweep.** The lists the lines carry come from the same run (Minecraft **1.21.1**, the **2.2.x** line): the
`26.x`, `1.20.6 (reforged)` and `1.20.6` lists are the same one byte for byte. `1.21.x` differs in exactly seven rows:
`the-shooting-star-demo`, which only its list carries, and six mods that were re-tested and are now marked as reaching
the world. That is why its incompatible count is 28 where the other three say 33. It says
the combination was tested - not that each line was tested on its own Minecraft version.

## What the marks mean

| Mark | Meaning |
|---|---|
| OK | reached the world; the number in brackets is how many seconds it took, and "retested" marks a re-tested mod |
| NO | incompatible with OptiFine: it does not get to the world with OptiFine installed |
| BAD | broken on plain Fabric as well, without this mod - not this mod's doing |
| BLOCKED | the mod declares itself incompatible with this mod (Sodium; a hard refusal since 2.2.11) |
| TITLE | reached the title screen only |
| DEP | a dependency was not resolved |
| N/A | not testable here (server only, or the wrong platform) |
| NOT RUN | not reached in this sweep |
| - | this line's list has no entry for the mod |

## The table

| Mod | slug | 1.21.x | 26.x | 1.20.6 (reforged) | 1.20.6 |
|---|---|---|---|---|---|
| _baseline | `_baseline` | ✅ | ✅ | ✅ | ✅ |
| [ETF] Entity Texture Features | `entitytexturefeatures` | ✅ | ✅ | ✅ | ✅ |
| [Let's Do] Beachparty | `lets-do-beachparty` | ✅ | ✅ | ✅ | ✅ |
| [Let's Do] Brewery - Farm&Charm Compat | `lets-do-brewery-farmcharm-compat` | ✅ | ✅ | ✅ | ✅ |
| [Let's Do] Candlelight - Farm&Charm compat | `lets-do-candlelight-farmcharm-compat` | ✅ | ✅ | ✅ | ✅ |
| [Let's Do] Farm & Charm | `lets-do-farm-charm` | ✅ | ✅ | ✅ | ✅ |
| [Let's Do] Meadow | `lets-do-meadow` | ✅ | ✅ | ✅ | ✅ |
| [Let's Do] Vinery | `lets-do-vinery` | ✅ | ✅ | ✅ | ✅ |
| 3D Skin Layers | `3dskinlayers` | ✅ | ✅ | ✅ | ✅ |
| AdoraBuild: Structures | `adorabuild-structures` | ✅ | ✅ | ✅ | ✅ |
| Advanced Loot Info (ALI) | `advanced-loot-info` | ✅ | ✅ | ✅ | ✅ |
| Advanced Netherite | `advanced-netherite` | ✅ | ✅ | ✅ | ✅ |
| Advancement Plaques | `advancement-plaques` | ✅ | ✅ | ✅ | ✅ |
| AdventureZ | `adventurez` | ✅ | ✅ | ✅ | ✅ |
| Alternate Current | `alternate-current` | ✅ | ✅ | ✅ | ✅ |
| Ambient Environment | `ambient-environment` | ✅ | ✅ | ✅ | ✅ |
| AmbientSounds | `ambientsounds` | ✅ | ✅ | ✅ | ✅ |
| Animal Feeding Trough | `animal_feeding_trough` | ✅ | ✅ | ✅ | ✅ |
| Animatica | `animatica` | ✅ | ✅ | ✅ | ✅ |
| Another Furniture | `another-furniture` | ✅ | ✅ | ✅ | ✅ |
| AppleSkin | `appleskin` | ✅ | ✅ | ✅ | ✅ |
| Aquamirae | `aquamirae` | ✅ | ✅ | ✅ | ✅ |
| Architectury API | `architectury-api` | ✅ | ✅ | ✅ | ✅ |
| Artifacts | `artifacts` | ✅ | ✅ | ✅ | ✅ |
| Athena | `athena-ctm` | ✅ | ✅ | ✅ | ✅ |
| AttributeFix | `attributefix` | ✅ | ✅ | ✅ | ✅ |
| Auth Me | `auth-me` | ✅ | ✅ | ✅ | ✅ |
| AzureLib | `azurelib` | ✅ | ✅ | ✅ | ✅ |
| AzureLib Armor | `azurelib-armor` | ✅ | ✅ | ✅ | ✅ |
| bad packets | `badpackets` | ✅ | ✅ | ✅ | ✅ |
| Bad Wither No Cookie - Reloaded | `bad-wither-no-cookie` | ✅ | ✅ | ✅ | ✅ |
| BadOptimizations | `badoptimizations` | ✅ | ✅ | ✅ | ✅ |
| Balm | `balm` | ✅ | ✅ | ✅ | ✅ |
| Bartering Station | `bartering-station` | ✅ | ✅ | ✅ | ✅ |
| Beautify: Refabricated | `beautify-refabricated` | ✅ | ✅ | ✅ | ✅ |
| Better Advancements | `better-advancements` | ✅ | ✅ | ✅ | ✅ |
| Better Archeology | `better-archeology` | ✅ | ✅ | ✅ | ✅ |
| Better Combat | `better-combat` | ✅ | ✅ | ✅ | ✅ |
| Better Compatibility Checker | `better-compatibility-checker` | ✅ | ✅ | ✅ | ✅ |
| Better Mount HUD | `better-mount-hud` | ✅ | ✅ | ✅ | ✅ |
| Better Ping Display [Fabric] | `better-ping-display-fabric` | ✅ | ✅ | ✅ | ✅ |
| Better Selection | `better-selection` | ✅ | ✅ | ✅ | ✅ |
| Better Statistics Screen | `better-stats` | ✅ | ✅ | ✅ | ✅ |
| Better Third Person | `better-third-person` | ✅ | ✅ | ✅ | ✅ |
| BetterF3 | `betterf3` | ✅ | ✅ | ✅ | ✅ |
| BetterGrassify | `bettergrassify` | ✅ | ✅ | ✅ | ✅ |
| BisectHosting Server Integration Menu | `bisect-mod` | ✅ | ✅ | ✅ | ✅ |
| Blur+ | `blur-plus` | ✅ | ✅ | ✅ | ✅ |
| Boat Item View | `boat-item-view` | ✅ | ✅ | ✅ | ✅ |
| Bookshelf | `bookshelf-lib` | ✅ | ✅ | ✅ | ✅ |
| Bountiful | `bountiful` | ✅ | ✅ | ✅ | ✅ |
| Bridging Mod | `bridging-mod` | ✅ | ✅ | ✅ | ✅ |
| Camera Utils | `camera-utils` | ✅ | ✅ | ✅ | ✅ |
| Capes | `capes` | ✅ | ✅ | ✅ | ✅ |
| Cardinal Components API | `cardinal-components-api` | ✅ | ✅ | ✅ | ✅ |
| Carpet | `carpet` | ✅ | ✅ | ✅ | ✅ |
| Carpet AMS Addition | `carpet-ams-addition` | ✅ | ✅ | ✅ | ✅ |
| cf--charm-of-undying | `cf--charm-of-undying` | ✅ | ✅ | ✅ | ✅ |
| cf--configured | `cf--configured` | ✅ | ✅ | ✅ | ✅ |
| cf--connectivity | `cf--connectivity` | ✅ | ✅ | ✅ | ✅ |
| cf--cooking-for-blockheads | `cf--cooking-for-blockheads` | ✅ | ✅ | ✅ | ✅ |
| cf--crafting-tweaks | `cf--crafting-tweaks` | ✅ | ✅ | ✅ | ✅ |
| cf--crafttweaker | `cf--crafttweaker` | ✅ | ✅ | ✅ | ✅ |
| cf--cupboard | `cf--cupboard` | ✅ | ✅ | ✅ | ✅ |
| cf--cyclops-core | `cf--cyclops-core` | ✅ | ✅ | ✅ | ✅ |
| cf--dark-utilities | `cf--dark-utilities` | ✅ | ✅ | ✅ | ✅ |
| cf--explorers-compass | `cf--explorers-compass` | ✅ | ✅ | ✅ | ✅ |
| cf--farming-for-blockheads | `cf--farming-for-blockheads` | ✅ | ✅ | ✅ | ✅ |
| cf--fix-gpu-memory-leak | `cf--fix-gpu-memory-leak` | ✅ | ✅ | ✅ | ✅ |
| cf--framework | `cf--framework` | ✅ | ✅ | ✅ | ✅ |
| cf--ftb-essentials | `cf--ftb-essentials` | ✅ | ✅ | ✅ | ✅ |
| cf--ftb-xmod-compat | `cf--ftb-xmod-compat` | ✅ | ✅ | ✅ | ✅ |
| cf--goblin-traders | `cf--goblin-traders` | ✅ | ✅ | ✅ | ✅ |
| cf--ichunutil | `cf--ichunutil` | ✅ | ✅ | ✅ | ✅ |
| cf--inventory-hud-forge | `cf--inventory-hud-forge` | ✅ | ✅ | ✅ | ✅ |
| cf--just-enough-professions-jep | `cf--just-enough-professions-jep` | ✅ | ✅ | ✅ | ✅ |
| cf--just-enough-resources-jer | `cf--just-enough-resources-jer` | ✅ | ✅ | ✅ | ✅ |
| cf--kleeslabs | `cf--kleeslabs` | ✅ | ✅ | ✅ | ✅ |
| cf--loot-integrations | `cf--loot-integrations` | ✅ | ✅ | ✅ | ✅ |
| cf--macaws-bridges | `cf--macaws-bridges` | ✅ | ✅ | ✅ | ✅ |
| cf--macaws-fences-and-walls | `cf--macaws-fences-and-walls` | ✅ | ✅ | ✅ | ✅ |
| cf--macaws-furniture | `cf--macaws-furniture` | ✅ | ✅ | ✅ | ✅ |
| cf--macaws-lights-and-lamps | `cf--macaws-lights-and-lamps` | ✅ | ✅ | ✅ | ✅ |
| cf--macaws-roofs | `cf--macaws-roofs` | ✅ | ✅ | ✅ | ✅ |
| cf--macaws-trapdoors | `cf--macaws-trapdoors` | ✅ | ✅ | ✅ | ✅ |
| cf--macaws-windows | `cf--macaws-windows` | ✅ | ✅ | ✅ | ✅ |
| cf--openblocks-elevator | `cf--openblocks-elevator` | ✅ | ✅ | ✅ | ✅ |
| cf--pehkui | `cf--pehkui` | ✅ | ✅ | ✅ | ✅ |
| cf--reborncore | `cf--reborncore` | ✅ | ✅ | ✅ | ✅ |
| cf--refined-storage | `cf--refined-storage` | ✅ | ✅ | ✅ | ✅ |
| cf--serene-seasons | `cf--serene-seasons` | ✅ | ✅ | ✅ | ✅ |
| cf--smartbrainlib | `cf--smartbrainlib` | ✅ | ✅ | ✅ | ✅ |
| cf--smooth-chunk-save | `cf--smooth-chunk-save` | ✅ | ✅ | ✅ | ✅ |
| cf--starter-kit | `cf--starter-kit` | ✅ | ✅ | ✅ | ✅ |
| cf--storage-drawers | `cf--storage-drawers` | ✅ | ✅ | ✅ | ✅ |
| cf--structure-essentials-forge-fabric | `cf--structure-essentials-forge-fabric` | ✅ | ✅ | ✅ | ✅ |
| cf--tips | `cf--tips` | ✅ | ✅ | ✅ | ✅ |
| cf--trash-cans | `cf--trash-cans` | ✅ | ✅ | ✅ | ✅ |
| cf--trashslot | `cf--trashslot` | ✅ | ✅ | ✅ | ✅ |
| Chat Animation [Smooth Chat] | `chatanimation` | ✅ | ✅ | ✅ | ✅ |
| Chat Heads | `chat-heads` | ✅ | ✅ | ✅ | ✅ |
| Chat Patches | `chatpatches` | ✅ | ✅ | ✅ | ✅ |
| Cherished Worlds | `cherished-worlds` | ✅ | ✅ | ✅ | ✅ |
| Chipped | `chipped` | ✅ | ✅ | ✅ | ✅ |
| Chunky | `chunky` | ✅ | ✅ | ✅ | ✅ |
| CICADA | `cicada` | ✅ | ✅ | ✅ | ✅ |
| CIT Resewn | `cit-resewn` | ✅ | ✅ | ✅ | ✅ |
| Client Sort | `clientsort` | ✅ | ✅ | ✅ | ✅ |
| Client Tweaks | `client-tweaks` | ✅ | ✅ | ✅ | ✅ |
| Cloth Config API | `cloth-config` | ✅ | ✅ | ✅ | ✅ |
| Clumps | `clumps` | ✅ | ✅ | ✅ | ✅ |
| Collective | `collective` | ✅ | ✅ | ✅ | ✅ |
| Combat Roll | `combat-roll` | ✅ | ✅ | ✅ | ✅ |
| Comforts | `comforts` | ✅ | ✅ | ✅ | ✅ |
| Config Manager | `configmanager` | ✅ | ✅ | ✅ | ✅ |
| Configured Defaults | `configured-defaults` | ✅ | ✅ | ✅ | ✅ |
| Continuity | `continuity` | ✅ | ✅ | ✅ | ✅ |
| Controlify (Controller support) | `controlify` | ✅ | ✅ | ✅ | ✅ |
| Controlling | `controlling` | ✅ | ✅ | ✅ | ✅ |
| Cool Rain | `coolrain` | ✅ | ✅ | ✅ | ✅ |
| CorgiLib | `corgilib` | ✅ | ✅ | ✅ | ✅ |
| CoroUtil | `coroutil` | ✅ | ✅ | ✅ | ✅ |
| Crafting Tweaks | `crafting-tweaks` | ✅ | ✅ | ✅ | ✅ |
| CraftPresence | `craftpresence` | ✅ | ✅ | ✅ | ✅ |
| Crash Assistant | `crash-assistant` | ✅ | ✅ | ✅ | ✅ |
| CreativeCore | `creativecore` | ✅ | ✅ | ✅ | ✅ |
| Cristel Lib | `cristel-lib` | ✅ | ✅ | ✅ | ✅ |
| Critters and Companions | `critters-and-companions` | ✅ | ✅ | ✅ | ✅ |
| Cubes Without Borders | `cubes-without-borders` | ✅ | ✅ | ✅ | ✅ |
| Cull Leaves | `cull-leaves` | ✅ | ✅ | ✅ | ✅ |
| CustomSkinLoader | `customskinloader` | ✅ | ✅ | ✅ | ✅ |
| Debugify | `debugify` | ✅ | ✅ | ✅ | ✅ |
| Default Options | `default-options` | ✅ | ✅ | ✅ | ✅ |
| Diagonal Fences | `diagonal-fences` | ✅ | ✅ | ✅ | ✅ |
| Difficulty Lock | `difficulty-lock` | ✅ | ✅ | ✅ | ✅ |
| Disable Custom Worlds Advice | `dcwa` | ✅ | ✅ | ✅ | ✅ |
| Distant Horizons | `distanthorizons` | ✅ | ✅ | ✅ | ✅ |
| Double Doors | `double-doors` | ✅ | ✅ | ✅ | ✅ |
| Dramatic Doors | `dramatic-doors` | ✅ | ✅ | ✅ | ✅ |
| Drip Sounds | `dripsounds` | ✅ | ✅ | ✅ | ✅ |
| Drippy Loading Screen | `drippy-loading-screen` | ✅ | ✅ | ✅ | ✅ |
| Dungeons and Taverns | `dungeons-and-taverns` | ✅ | ✅ | ✅ | ✅ |
| Durability Tooltip | `durability-tooltip` | ✅ | ✅ | ✅ | ✅ |
| Dynamic Crosshair | `dynamiccrosshair` | ✅ | ✅ | ✅ | ✅ |
| Dynamic FPS | `dynamic-fps` | ✅ | ✅ | ✅ | ✅ |
| Dynamic Lights | `dynamic-lights` | ✅ | ✅ | ✅ | ✅ |
| Dynamic Surroundings | `dynamicsurroundingsfabric` | ✅ | ✅ | ✅ | ✅ |
| Dynamic Trees | `dynamictrees` | ✅ | ✅ | ✅ | ✅ |
| e4mc | `e4mc` | ✅ | ✅ | ✅ | ✅ |
| Easy Anvils | `easy-anvils` | ✅ | ✅ | ✅ | ✅ |
| Easy Magic | `easy-magic` | ✅ | ✅ | ✅ | ✅ |
| Eating Animation | `eating-animation` | ✅ | ✅ | ✅ | ✅ |
| Elytra Slot | `elytra-slot` | ✅ | ✅ | ✅ | ✅ |
| EMI | `emi` | ✅ | ✅ | ✅ | ✅ |
| EMI Enchanting | `emi-enchanting` | ✅ | ✅ | ✅ | ✅ |
| EMI Loot | `emi-loot` | ✅ | ✅ | ✅ | ✅ |
| EMI Ores | `emi-ores` | ✅ | ✅ | ✅ | ✅ |
| EMIffect | `emiffect` | ✅ | ✅ | ✅ | ✅ |
| Emotecraft | `emotecraft` | ✅ | ✅ | ✅ | ✅ |
| Enchanting Infuser | `enchanting-infuser` | ✅ | ✅ | ✅ | ✅ |
| Enchantment Descriptions | `enchantment-descriptions` | ✅ | ✅ | ✅ | ✅ |
| End Remastered | `endrem` | ✅ | ✅ | ✅ | ✅ |
| End's Delight | `ends-delight` | ✅ | ✅ | ✅ | ✅ |
| Enhanced Attack Indicator | `enhanced-attack-indicator` | ✅ | ✅ | ✅ | ✅ |
| EnhancedVisuals | `enhancedvisuals` | ✅ | ✅ | ✅ | ✅ |
| Entity Culling | `entityculling` | ✅ | ✅ | ✅ | ✅ |
| Euphoria Patches | `euphoria-patches` | ✅ | ✅ | ✅ | ✅ |
| Exordium | `exordium` | ✅ | ✅ | ✅ | ✅ |
| Explorations | `explorations` | ✅ | ✅ | ✅ | ✅ |
| Explorer's Compass | `explorers-compass` | ✅ | ✅ | ✅ | ✅ |
| Explorify | `explorify` | ✅ | ✅ | ✅ | ✅ |
| Explosive Enhancement | `explosive-enhancement` | ✅ | ✅ | ✅ | ✅ |
| Exposure | `exposure` | ✅ | ✅ | ✅ | ✅ |
| Fabric API | `fabric-api` | ✅ | ✅ | ✅ | ✅ |
| Fabric Language Kotlin | `fabric-language-kotlin` | ✅ | ✅ | ✅ | ✅ |
| Fabric Seasons | `fabric-seasons` | ✅ | ✅ | ✅ | ✅ |
| Fabric Seasons: Extras | `fabric-seasons-extras` | ✅ | ✅ | ✅ | ✅ |
| Fabrishot | `fabrishot` | ✅ | ✅ | ✅ | ✅ |
| FallingTree | `fallingtree` | ✅ | ✅ | ✅ | ✅ |
| FancyMenu | `fancymenu` | ✅ | ✅ | ✅ | ✅ |
| Farmer's Delight Refabricated | `farmers-delight-refabricated` | ✅ | ✅ | ✅ | ✅ |
| Fast IP Ping | `fast-ip-ping` | ✅ | ✅ | ✅ | ✅ |
| Fast Noise | `zfastnoise` | ✅ | ✅ | ✅ | ✅ |
| Fast Scrolling | `fast-scrolling` | ✅ | ✅ | ✅ | ✅ |
| FastQuit | `fastquit` | ✅ | ✅ | ✅ | ✅ |
| FerriteCore | `ferrite-core` | ✅ | ✅ | ✅ | ✅ |
| First-person Model | `first-person-model` | ✅ | ✅ | ✅ | ✅ |
| Forge Config API Port | `forge-config-api-port` | ✅ | ✅ | ✅ | ✅ |
| Formations (Structure Library) | `formations` | ✅ | ✅ | ✅ | ✅ |
| Formations Nether | `formations-nether` | ✅ | ✅ | ✅ | ✅ |
| Formations Overworld | `formations-overworld` | ✅ | ✅ | ✅ | ✅ |
| FPS Reducer | `fps-reducer` | ✅ | ✅ | ✅ | ✅ |
| Friends&Foes (Fabric/Quilt) | `friends-and-foes` | ✅ | ✅ | ✅ | ✅ |
| Full Brightness Toggle | `full-brightness-toggle` | ✅ | ✅ | ✅ | ✅ |
| Fzzy Config | `fzzy-config` | ✅ | ✅ | ✅ | ✅ |
| Galosphere | `galosphere` | ✅ | ✅ | ✅ | ✅ |
| Gamma Utils (Fullbright) | `gamma-utils` | ✅ | ✅ | ✅ | ✅ |
| Geophilic | `geophilic` | ✅ | ✅ | ✅ | ✅ |
| GlitchCore | `glitchcore` | ✅ | ✅ | ✅ | ✅ |
| Global Packs | `globalpacks` | ✅ | ✅ | ✅ | ✅ |
| GPUBooster | `gputape` | ✅ | ✅ | ✅ | ✅ |
| Grass Overhaul | `grass-overhaul` | ✅ | ✅ | ✅ | ✅ |
| Handcrafted | `handcrafted` | ✅ | ✅ | ✅ | ✅ |
| Hardcore Revival | `hardcore-revival` | ✅ | ✅ | ✅ | ✅ |
| Highlight | `highlight` | ✅ | ✅ | ✅ | ✅ |
| I18nUpdateMod | `i18nupdatemod` | ✅ | ✅ | ✅ | ✅ |
| Iceberg | `iceberg` | ✅ | ✅ | ✅ | ✅ |
| IMBlocker | `imblocker-original` | ✅ | ✅ | ✅ | ✅ |
| Immersive Aircraft | `immersive-aircraft` | ✅ | ✅ | ✅ | ✅ |
| Immersive Armors | `immersive-armors` | ✅ | ✅ | ✅ | ✅ |
| Immersive Melodies | `immersive-melodies` | ✅ | ✅ | ✅ | ✅ |
| Inventory Profiles Next | `inventory-profiles-next` | ✅ | ✅ | ✅ | ✅ |
| InventoryHUD+ | `inventoryhudplus` | ✅ | ✅ | ✅ | ✅ |
| InvMove | `invmove` | ✅ | ✅ | ✅ | ✅ |
| InvMoveCompats | `invmovecompats` | ✅ | ✅ | ✅ | ✅ |
| Item Borders | `item-borders` | ✅ | ✅ | ✅ | ✅ |
| Item Highlighter | `item-highlighter` | ✅ | ✅ | ✅ | ✅ |
| Ixeris | `ixeris` | ✅ | ✅ | ✅ | ✅ |
| Jade 🔍 | `jade` | ✅ | ✅ | ✅ | ✅ |
| JamLib | `jamlib` | ✅ | ✅ | ✅ | ✅ |
| Just Enough Breeding (JEBr) | `justenoughbreeding` | ✅ | ✅ | ✅ | ✅ |
| Just Enough Effect Descriptions (JEED) | `just-enough-effect-descriptions-jeed` | ✅ | ✅ | ✅ | ✅ |
| Just Enough Items (JEI) | `jei` | ✅ | ✅ | ✅ | ✅ |
| Just Enough Resources (JER) | `just-enough-resources-jer` | ✅ | ✅ | ✅ | ✅ |
| Just Zoom | `just-zoom` | ✅ | ✅ | ✅ | ✅ |
| JustEnoughCharacters | `justenoughcharacters` | ✅ | ✅ | ✅ | ✅ |
| Kaleidoscope Cookery | `kaleidoscope-cookery` | ✅ | ✅ | ✅ | ✅ |
| kennytvs-epic-force-close-loading-screen-mod-for-fabric | `forcecloseworldloadingscreen` | ✅ | ✅ | ✅ | ✅ |
| Kiwi 🥝 | `kiwi` | ✅ | ✅ | ✅ | ✅ |
| KleeSlabs | `kleeslabs` | ✅ | ✅ | ✅ | ✅ |
| Konkrete | `konkrete` | ✅ | ✅ | ✅ | ✅ |
| Krypton | `krypton` | ✅ | ✅ | ✅ | ✅ |
| Ksyxis | `ksyxis` | ✅ | ✅ | ✅ | ✅ |
| LambDynamicLights - Dynamic Lights | `lambdynamiclights` | ✅ | ✅ | ✅ | ✅ |
| Language Reload | `language-reload` | ✅ | ✅ | ✅ | ✅ |
| Leaves Be Gone | `leaves-be-gone` | ✅ | ✅ | ✅ | ✅ |
| libIPN | `libipn` | ✅ | ✅ | ✅ | ✅ |
| LibJF | `libjf` | ✅ | ✅ | ✅ | ✅ |
| Library Ferret | `library-ferret` | ✅ | ✅ | ✅ | ✅ |
| Litematica | `litematica` | ✅ | ✅ | ✅ | ✅ |
| Lithium | `lithium` | ✅ | ✅ | ✅ | ✅ |
| Load My F***ing Tags | `lmft` | ✅ | ✅ | ✅ | ✅ |
| Log Begone | `log-begone` | ✅ | ✅ | ✅ | ✅ |
| Lootr | `lootr` | ✅ | ✅ | ✅ | ✅ |
| Macaw's Bridges | `macaws-bridges` | ✅ | ✅ | ✅ | ✅ |
| Macaw's Doors | `macaws-doors` | ✅ | ✅ | ✅ | ✅ |
| Macaw's Fences and Walls | `macaws-fences-and-walls` | ✅ | ✅ | ✅ | ✅ |
| Macaw's Furniture | `macaws-furniture` | ✅ | ✅ | ✅ | ✅ |
| Macaw's Roofs | `macaws-roofs` | ✅ | ✅ | ✅ | ✅ |
| Macaw's Stairs | `macaws-stairs` | ✅ | ✅ | ✅ | ✅ |
| Macaw's Windows | `macaws-windows` | ✅ | ✅ | ✅ | ✅ |
| Main Menu Credits | `main-menu-credits` | ✅ | ✅ | ✅ | ✅ |
| Make Bubbles Pop | `make_bubbles_pop` | ✅ | ✅ | ✅ | ✅ |
| MaLiLib | `malilib` | ✅ | ✅ | ✅ | ✅ |
| Melody | `melody` | ✅ | ✅ | ✅ | ✅ |
| MES - Moog's End Structures | `mes-moogs-end-structures` | ✅ | ✅ | ✅ | ✅ |
| MidnightLib | `midnightlib` | ✅ | ✅ | ✅ | ✅ |
| MiniHUD | `minihud` | ✅ | ✅ | ✅ | ✅ |
| MissingMons [cobblemon] | `missingmons-cobblemon` | ✅ | ✅ | ✅ | ✅ |
| MixinTrace | `mixintrace` | ✅ | ✅ | ✅ | ✅ |
| Mod Menu | `modmenu` | ✅ | ✅ | ✅ | ✅ |
| Model Gap Fix | `modelfix` | ✅ | ✅ | ✅ | ✅ |
| More Chat History | `morechathistory` | ✅ | ✅ | ✅ | ✅ |
| Mouse Tweaks | `mouse-tweaks` | ✅ | ✅ | ✅ | ✅ |
| MRU | `mru` | ✅ | ✅ | ✅ | ✅ |
| Music Delay Remover (Infinite Music) | `infinite-music` | ✅ | ✅ | ✅ | ✅ |
| MVS - Moog's Voyager Structures | `moogs-voyager-structures` | ✅ | ✅ | ✅ | ✅ |
| Naturalist | `naturalist` | ✅ | ✅ | ✅ | ✅ |
| Nature's Compass | `natures-compass` | ✅ | ✅ | ✅ | ✅ |
| Nature's Spirit | `natures-spirit` | ✅ | ✅ | ✅ | ✅ |
| Neat | `neat` | ✅ | ✅ | ✅ | ✅ |
| Necronomicon API | `necronomicon` | ✅ | ✅ | ✅ | ✅ |
| Neruina - Ticking Entity Fixer | `neruina` | ✅ | ✅ | ✅ | ✅ |
| Nether Chested | `nether-chested` | ✅ | ✅ | ✅ | ✅ |
| No Resource Pack Warnings | `no-resource-pack-warnings` | ✅ | ✅ | ✅ | ✅ |
| Not Enough Animations | `not-enough-animations` | ✅ | ✅ | ✅ | ✅ |
| Not Enough Crashes | `notenoughcrashes` | ✅ | ✅ | ✅ | ✅ |
| Nuit | `nuit` | ✅ | ✅ | ✅ | ✅ |
| Nyf's Spiders | `nyfs-spiders` | ✅ | ✅ | ✅ | ✅ |
| Ocean's Delight | `oceans-delight` | ✅ | ✅ | ✅ | ✅ |
| Oh The Trees You'll Grow | `oh-the-trees-youll-grow` | ✅ | ✅ | ✅ | ✅ |
| OptiGUI | `optigui` | ✅ | ✅ | ✅ | ✅ |
| Overflowing Bars | `overflowing-bars` | ✅ | ✅ | ✅ | ✅ |
| Packet Fixer | `packet-fixer` | ✅ | ✅ | ✅ | ✅ |
| Paginated Advancements & Custom Frames | `paginatedadvancements` | ✅ | ✅ | ✅ | ✅ |
| Particle Rain | `particle-rain` | ✅ | ✅ | ✅ | ✅ |
| Particular ✨ | `particular` | ✅ | ✅ | ✅ | ✅ |
| Paxi | `paxi` | ✅ | ✅ | ✅ | ✅ |
| Pehkui | `pehkui` | ✅ | ✅ | ✅ | ✅ |
| Philips Ruins | `philips-ruins` | ✅ | ✅ | ✅ | ✅ |
| Physics Mod | `physicsmod` | ✅ | ✅ | ✅ | ✅ |
| Pick Up Notifier | `pick-up-notifier` | ✅ | ✅ | ✅ | ✅ |
| Ping Wheel | `ping-wheel` | ✅ | ✅ | ✅ | ✅ |
| Platform | `platform` | ✅ | ✅ | ✅ | ✅ |
| playerAnimator | `playeranimator` | ✅ | ✅ | ✅ | ✅ |
| Pokeblocks | `pokeblocks` | ✅ | ✅ | ✅ | ✅ |
| Polymorph | `polymorph` | ✅ | ✅ | ✅ | ✅ |
| Presence Footsteps | `presence-footsteps` | ✅ | ✅ | ✅ | ✅ |
| Prickle | `prickle` | ✅ | ✅ | ✅ | ✅ |
| Puzzles Lib | `puzzles-lib` | ✅ | ✅ | ✅ | ✅ |
| Reactive Music | `reactive-music` | ✅ | ✅ | ✅ | ✅ |
| Rebind Narrator | `rebind-narrator` | ✅ | ✅ | ✅ | ✅ |
| Regions Unexplored | `regions-unexplored` | ✅ | ✅ | ✅ | ✅ |
| Repurposed Structures - Fabric | `repurposed-structures-fabric` | ✅ | ✅ | ✅ | ✅ |
| Resourceful Config | `resourceful-config` | ✅ | ✅ | ✅ | ✅ |
| Resourceful Lib | `resourceful-lib` | ✅ | ✅ | ✅ | ✅ |
| Resourcify | `resourcify` | ✅ | ✅ | ✅ | ✅ |
| Respackopts | `respackopts` | ✅ | ✅ | ✅ | ✅ |
| Rhino | `rhino` | ✅ | ✅ | ✅ | ✅ |
| Ribbits | `ribbits` | ✅ | ✅ | ✅ | ✅ |
| RightClickHarvest | `rightclickharvest` | ✅ | ✅ | ✅ | ✅ |
| Roughly Enough Items (REI) | `rei` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-almanac-lib | `sample--cf-almanac-lib` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-better-combat-by-daedelus | `sample--cf-better-combat-by-daedelus` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-catalogue | `sample--cf-catalogue` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-chunk-sending-forge-fabric | `sample--cf-chunk-sending-forge-fabric` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-clickable-advancements | `sample--cf-clickable-advancements` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-client-crafting | `sample--cf-client-crafting` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-connectivity | `sample--cf-connectivity` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-croptopia | `sample--cf-croptopia` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-cupboard | `sample--cf-cupboard` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-endremastered | `sample--cf-endremastered` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-entity-texture-features-fabric | `sample--cf-entity-texture-features-fabric` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-expandability | `sample--cf-expandability` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-fast-async-world-save-forge-fabric | `sample--cf-fast-async-world-save-forge-fabric` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-ferritecore-fabric | `sample--cf-ferritecore-fabric` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-fix-gpu-memory-leak | `sample--cf-fix-gpu-memory-leak` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-framework | `sample--cf-framework` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-ftb-essentials | `sample--cf-ftb-essentials` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-ftb-xmod-compat | `sample--cf-ftb-xmod-compat` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-goblin-traders | `sample--cf-goblin-traders` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-iceberg-fabric | `sample--cf-iceberg-fabric` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-konkrete-fabric | `sample--cf-konkrete-fabric` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-light-overlay | `sample--cf-light-overlay` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-login-protection | `sample--cf-login-protection` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-loot-integrations | `sample--cf-loot-integrations` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-openblocks-elevator | `sample--cf-openblocks-elevator` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-recipe-essentials-forge-fabric | `sample--cf-recipe-essentials-forge-fabric` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-refurbished-furniture | `sample--cf-refurbished-furniture` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-simple-discord-rich-presence | `sample--cf-simple-discord-rich-presence` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-smooth-chunk-save | `sample--cf-smooth-chunk-save` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-structure-essentials-forge-fabric | `sample--cf-structure-essentials-forge-fabric` | ✅ | ✅ | ✅ | ✅ |
| sample--cf-vein-mining | `sample--cf-vein-mining` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-aligning-scepters | `sample--mr-aligning-scepters` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-archive-downloader | `sample--mr-archive-downloader` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-axolotl-buckets | `sample--mr-axolotl-buckets` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-banner-flags | `sample--mr-banner-flags` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-better-climbing | `sample--mr-better-climbing` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-better-end-sky | `sample--mr-better-end-sky` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-bettergrassify | `sample--mr-bettergrassify` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-bookshelf-lib | `sample--mr-bookshelf-lib` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-chaos-core | `sample--mr-chaos-core` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-craftify | `sample--mr-craftify` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-dynamiccrosshaircompat | `sample--mr-dynamiccrosshaircompat` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-enchantment-calculator | `sample--mr-enchantment-calculator` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-entityculling | `sample--mr-entityculling` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-entitytexturefeatures | `sample--mr-entitytexturefeatures` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-fabric-api | `sample--mr-fabric-api` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-faster-random | `sample--mr-faster-random` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-ferrite-core | `sample--mr-ferrite-core` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-figura-lite | `sample--mr-figura-lite` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-grass-overhaul | `sample--mr-grass-overhaul` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-helpful-armorers | `sample--mr-helpful-armorers` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-infinity-hoe | `sample--mr-infinity-hoe` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-konkrete | `sample--mr-konkrete` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-lets-do-furniture | `sample--mr-lets-do-furniture` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-lgbtqdesignedtimer | `sample--mr-lgbtqdesignedtimer` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-libipn | `sample--mr-libipn` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-lithium | `sample--mr-lithium` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-mca-selector | `sample--mr-mca-selector` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-modmenu | `sample--mr-modmenu` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-more-grindstone-variants | `sample--mr-more-grindstone-variants` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-nexus-reds-addon | `sample--mr-nexus-reds-addon` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-patchouli | `sample--mr-patchouli` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-patpat | `sample--mr-patpat` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-player-corpse | `sample--mr-player-corpse` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-resourceful-config | `sample--mr-resourceful-config` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-sandy-husks | `sample--mr-sandy-husks` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-scrollabletooltips | `sample--mr-scrollabletooltips` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-shield-api | `sample--mr-shield-api` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-simple-voice-chat | `sample--mr-simple-voice-chat` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-stoneworks | `sample--mr-stoneworks` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-syncmatica | `sample--mr-syncmatica` | ✅ | ✅ | ✅ | ✅ |
| sample--mr-threadtweak | `sample--mr-threadtweak` | ✅ | ✅ | ✅ | ✅ |
| Satin Free Wakes | `satin-free-wakes` | ✅ | ✅ | ✅ | ✅ |
| ScalableLux | `scalablelux` | ✅ | ✅ | ✅ | ✅ |
| Searchables | `searchables` | ✅ | ✅ | ✅ | ✅ |
| Serene Seasons | `serene-seasons` | ✅ | ✅ | ✅ | ✅ |
| Shulker Box Tooltip | `shulkerboxtooltip` | ✅ | ✅ | ✅ | ✅ |
| Simple Voice Chat | `simple-voice-chat` | ✅ | ✅ | ✅ | ✅ |
| Simply Swords | `simply-swords` | ✅ | ✅ | ✅ | ✅ |
| Sit | `bl4cks-sit` | ✅ | ✅ | ✅ | ✅ |
| Skyboxify | `skyboxify` | ✅ | ✅ | ✅ | ✅ |
| SmartBrainLib | `smartbrainlib` | ✅ | ✅ | ✅ | ✅ |
| Smooth Scrolling | `smooth-scroll` | ✅ | ✅ | ✅ | ✅ |
| Smooth Swapping | `smooth-swapping` | ✅ | ✅ | ✅ | ✅ |
| Snow! Real Magic! ⛄ | `snow-real-magic` | ✅ | ✅ | ✅ | ✅ |
| Sound Physics Remastered | `sound-physics-remastered` | ✅ | ✅ | ✅ | ✅ |
| Sounds | `sound` | ✅ | ✅ | ✅ | ✅ |
| spark | `spark` | ✅ | ✅ | ✅ | ✅ |
| Sparse Structures | `sparsestructures` | ✅ | ✅ | ✅ | ✅ |
| Spawn Animations | `spawn-animations` | ✅ | ✅ | ✅ | ✅ |
| Spell Engine | `spell-engine` | ✅ | ✅ | ✅ | ✅ |
| Spell Power Attributes | `spell-power` | ✅ | ✅ | ✅ | ✅ |
| StackDeobfuscator | `stackdeobf` | ✅ | ✅ | ✅ | ✅ |
| Starter Kit | `starter-kit` | ✅ | ✅ | ✅ | ✅ |
| Status Effect Bars | `status-effect-bars` | ✅ | ✅ | ✅ | ✅ |
| Stoneworks | `stoneworks` | ✅ | ✅ | ✅ | ✅ |
| Structory | `structory` | ✅ | ✅ | ✅ | ✅ |
| Structory: Towers | `structory-towers` | ✅ | ✅ | ✅ | ✅ |
| Structure Layout Optimizer | `structure-layout-optimizer` | ✅ | ✅ | ✅ | ✅ |
| SuperMartijn642's Config Lib | `supermartijn642s-config-lib` | ✅ | ✅ | ✅ | ✅ |
| SuperMartijn642's Core Lib | `supermartijn642s-core-lib` | ✅ | ✅ | ✅ | ✅ |
| TCDCommons API | `tcdcommons` | ✅ | ✅ | ✅ | ✅ |
| Tectonic | `tectonic` | ✅ | ✅ | ✅ | ✅ |
| TerraBlender | `terrablender` | ✅ | ✅ | ✅ | ✅ |
| Terralith | `terralith` | ✅ | ✅ | ✅ | ✅ |
| Text Placeholder API | `placeholder-api` | ✅ | ✅ | ✅ | ✅ |
| The Lost Castle | `the-lost-castle` | ✅ | ✅ | ✅ | ✅ |
| ThreadTweak | `threadtweak` | ✅ | ✅ | ✅ | ✅ |
| Tips | `tips` | ✅ | ✅ | ✅ | ✅ |
| Tom's Simple Storage Mod | `toms-storage` | ✅ | ✅ | ✅ | ✅ |
| Towns and Towers | `towns-and-towers` | ✅ | ✅ | ✅ | ✅ |
| Trade Cycling | `trade-cycling` | ✅ | ✅ | ✅ | ✅ |
| Trading Post | `trading-post` | ✅ | ✅ | ✅ | ✅ |
| TrashSlot | `trashslot` | ✅ | ✅ | ✅ | ✅ |
| Traveler's Backpack | `travelersbackpack` | ✅ | ✅ | ✅ | ✅ |
| Traveler's Titles | `travelers-titles` | ✅ | ✅ | ✅ | ✅ |
| Tree Harvester | `tree-harvester` | ✅ | ✅ | ✅ | ✅ |
| Trek | `trek` | ✅ | ✅ | ✅ | ✅ |
| Trinkets | `trinkets` | ✅ | ✅ | ✅ | ✅ |
| True Ending - Ender Dragon Overhaul | `true-ending` | ✅ | ✅ | ✅ | ✅ |
| TslatEntityStatus | `tslatentitystatus` | ✅ | ✅ | ✅ | ✅ |
| Tweakeroo | `tweakeroo` | ✅ | ✅ | ✅ | ✅ |
| TxniLib | `txnilib` | ✅ | ✅ | ✅ | ✅ |
| ukulib | `ukulib` | ✅ | ✅ | ✅ | ✅ |
| UniLib | `unilib` | ✅ | ✅ | ✅ | ✅ |
| Vanilla Backport | `vanillabackport` | ✅ | ✅ | ✅ | ✅ |
| VeinMiner | `veinminer` | ✅ | ✅ | ✅ | ✅ |
| VeinMiner Enchantment | `veinminer-enchantment` | ✅ | ✅ | ✅ | ✅ |
| VeinMiner Hotkey | `veinminer-client` | ✅ | ✅ | ✅ | ✅ |
| Villager Names | `villager-names-serilum` | ✅ | ✅ | ✅ | ✅ |
| Villages&Pillages | `villages-and-pillages` | ✅ | ✅ | ✅ | ✅ |
| Visual Workbench | `visual-workbench` | ✅ | ✅ | ✅ | ✅ |
| Visuality | `visuality` | ✅ | ✅ | ✅ | ✅ |
| Wakes | `wakes` | ✅ | ✅ | ✅ | ✅ |
| Wavey Capes | `wavey-capes` | ✅ | ✅ | ✅ | ✅ |
| Waystones | `waystones` | ✅ | ✅ | ✅ | ✅ |
| What Are They Up To (Watut) | `what-are-they-up-to` | ✅ | ✅ | ✅ | ✅ |
| When Dungeons Arise | `when-dungeons-arise` | ✅ | ✅ | ✅ | ✅ |
| WorldEdit | `worldedit` | ✅ | ✅ | ✅ | ✅ |
| Xaero's Minimap | `xaeros-minimap` | ✅ | ✅ | ✅ | ✅ |
| Xaero's World Map | `xaeros-world-map` | ✅ | ✅ | ✅ | ✅ |
| XaeroPlus | `xaeroplus` | ✅ | ✅ | ✅ | ✅ |
| YDM's Weapon Master | `weaponmaster` | ✅ | ✅ | ✅ | ✅ |
| Yeetus Experimentus | `yeetus-experimentus` | ✅ | ✅ | ✅ | ✅ |
| Yes Steve Model | `yes-steve-model` | ✅ | ✅ | ✅ | ✅ |
| YetAnotherConfigLib (YACL) | `yacl` | ✅ | ✅ | ✅ | ✅ |
| You Shall Not Spawn! | `you-shall-not-spawn` | ✅ | ✅ | ✅ | ✅ |
| Your Options Shall Be Respected (YOSBR) | `yosbr` | ✅ | ✅ | ✅ | ✅ |
| YUNG's API | `yungs-api` | ✅ | ✅ | ✅ | ✅ |
| YUNG's Extras | `yungs-extras` | ✅ | ✅ | ✅ | ✅ |
| YUNG's Menu Tweaks | `yungs-menu-tweaks` | ✅ | ✅ | ✅ | ✅ |
| Zombie Awareness | `zombie-awareness` | ✅ | ✅ | ✅ | ✅ |
| Zume | `zume` | ✅ | ✅ | ✅ | ✅ |
| (Sodium) Chloride | `chloride` | ❌ | ❌ | ❌ | ❌ |
| [EMF] Entity Model Features | `entity-model-features` | ❌ | ❌ | ❌ | ❌ |
| Carry On | `carry-on` | ❌ | ❌ | ❌ | ❌ |
| Cut Through | `cut-through` | ❌ | ❌ | ❌ | ❌ |
| Deeper and Darker | `deeperdarker` | ❌ | ❌ | ❌ | ❌ |
| Enhanced Block Entities | `ebe` | ❌ | ❌ | ❌ | ❌ |
| ImmediatelyFast | `immediatelyfast` | ❌ | ❌ | ❌ | ❌ |
| Indium | `indium` | ❌ | ❌ | ❌ | ❌ |
| Iris Shaders | `iris` | ❌ | ❌ | ❌ | ❌ |
| Modern UI | `modern-ui` | ❌ | ❌ | ❌ | ❌ |
| ModernFix | `modernfix` | ❌ | ❌ | ❌ | ❌ |
| More Culling | `moreculling` | ❌ | ❌ | ❌ | ❌ |
| No Chat Reports | `no-chat-reports` | ❌ | ❌ | ❌ | ❌ |
| Open Parties and Claims | `open-parties-and-claims` | ❌ | ❌ | ❌ | ❌ |
| Particle Core | `particle-core` | ❌ | ❌ | ❌ | ❌ |
| Polytone | `polytone` | ❌ | ❌ | ❌ | ❌ |
| Puzzle | `puzzle` | ❌ | ❌ | ❌ | ❌ |
| Reese's Sodium Options | `reeses-sodium-options` | ❌ | ❌ | ❌ | ❌ |
| ReplayMod | `replaymod` | ❌ | ❌ | ❌ | ❌ |
| sample--mr-betternether | `sample--mr-betternether` | ❌ | ❌ | ❌ | ❌ |
| sample--mr-immediatelyfast | `sample--mr-immediatelyfast` | ❌ | ❌ | ❌ | ❌ |
| sample--mr-spectrumjei | `sample--mr-spectrumjei` | ❌ | ❌ | ❌ | ❌ |
| ShatterLib / OctoLib | `shatterbyte-lib` | ❌ | ❌ | ❌ | ❌ |
| Sodium Extra | `sodium-extra` | ❌ | ❌ | ❌ | ❌ |
| Sodium Shadowy Path Blocks (SSPB) | `sodium-shadowy-path-blocks` | ❌ | ❌ | ❌ | ❌ |
| Supplementaries | `supplementaries` | ❌ | ❌ | ❌ | ❌ |
| The Shooting Star (Demo) | `the-shooting-star-demo` | ❌ | - | - | - |
| ToolTipFix | `tooltipfix` | ❌ | ❌ | ❌ | ❌ |
| Bobby | `bobby` | ✅ | ❌ | ❌ | ❌ |
| Concurrent Chunk Management Engine (Fabric) | `c2me-fabric` | ✅ | ❌ | ❌ | ❌ |
| Falling Leaves | `fallingleaves` | ✅ | ❌ | ❌ | ❌ |
| Freecam | `freecam` | ✅ | ❌ | ❌ | ❌ |
| sample--mr-bedrockify | `sample--mr-bedrockify` | ✅ | ❌ | ❌ | ❌ |
| sample--mr-c2me-fabric | `sample--mr-c2me-fabric` | ✅ | ❌ | ❌ | ❌ |
| cf--aether | `cf--aether` | 🚫 | 🚫 | 🚫 | 🚫 |
| cf--better-compatibility-checker | `cf--better-compatibility-checker` | 🚫 | 🚫 | 🚫 | 🚫 |
| cf--the-twilight-forest | `cf--the-twilight-forest` | 🚫 | 🚫 | 🚫 | 🚫 |
| cf--torchmaster | `cf--torchmaster` | 🚫 | 🚫 | 🚫 | 🚫 |
| sample--cf-roughly-enough-items | `sample--cf-roughly-enough-items` | 🚫 | 🚫 | 🚫 | 🚫 |
| sample--cf-the-twilight-forest | `sample--cf-the-twilight-forest` | 🚫 | 🚫 | 🚫 | 🚫 |
| sample--mr-cobblesmartphone-pokedex | `sample--mr-cobblesmartphone-pokedex` | 🚫 | 🚫 | 🚫 | 🚫 |
| sample--mr-moonrise-compats | `sample--mr-moonrise-compats` | 🚫 | 🚫 | 🚫 | 🚫 |
| Sodium | `sodium` | ⛔ | ⛔ | ⛔ | ⛔ |
| Amendments | `amendments` | ⚠️ | ⚠️ | ⚠️ | ⚠️ |
| cf--creeper-overhaul | `cf--creeper-overhaul` | ⚠️ | ⚠️ | ⚠️ | ⚠️ |
| cf--valhelsia-core | `cf--valhelsia-core` | ⚠️ | ⚠️ | ⚠️ | ⚠️ |
| Cobblemon | `cobblemon` | ⚠️ | ⚠️ | ⚠️ | ⚠️ |
| Moonlight Lib | `moonlight` | ⚠️ | ⚠️ | ⚠️ | ⚠️ |
| oωo (owo-lib) | `owo-lib` | ⚠️ | ⚠️ | ⚠️ | ⚠️ |
| sample--cf-valhelsia-core | `sample--cf-valhelsia-core` | ⚠️ | ⚠️ | ⚠️ | ⚠️ |
| sample--mr-cobblemon-hug | `sample--mr-cobblemon-hug` | ⚠️ | ⚠️ | ⚠️ | ⚠️ |
| sample--mr-dynamic-fps | `sample--mr-dynamic-fps` | ⚠️ | ⚠️ | ⚠️ | ⚠️ |
| sample--mr-sophisticated-core-(unofficial-fabric-port) | `sample--mr-sophisticated-core-(unofficial-fabric-port)` | ⚠️ | ⚠️ | ⚠️ | ⚠️ |
| Biomes O' Plenty | `biomes-o-plenty` | 🧩 | 🧩 | 🧩 | 🧩 |
| Carpet Org Addition | `carpet-org-addition` | 🧩 | 🧩 | 🧩 | 🧩 |
| Carpet TIS Addition | `carpet-tis-addition` | 🧩 | 🧩 | 🧩 | 🧩 |
| sample--mr-indium | `sample--mr-indium` | 🧩 | 🧩 | 🧩 | 🧩 |
| sample--mr-iris | `sample--mr-iris` | 🧩 | 🧩 | 🧩 | 🧩 |
| sample--mr-sodium | `sample--mr-sodium` | 🧩 | 🧩 | 🧩 | 🧩 |
| Sodium Options API | `sodium-options-api` | 🧩 | 🧩 | 🧩 | 🧩 |
| Zoomify (Zoom) | `zoomify` | 🧩 | 🧩 | 🧩 | 🧩 |
| [Let's Do] Bakery - Farm&Charm Compat | `lets-do-bakery-farmcharm-compat` | ➖ | ➖ | ➖ | ➖ |
| [Let's Do] HerbalBrews | `lets-do-herbalbrews` | ➖ | ➖ | ➖ | ➖ |
| Accessories | `accessories` | ➖ | ➖ | ➖ | ➖ |
| Ad Astra | `ad-astra` | ➖ | ➖ | ➖ | ➖ |
| Almanac | `almanac` | ➖ | ➖ | ➖ | ➖ |
| AsyncParticles | `asyncparticles` | ➖ | ➖ | ➖ | ➖ |
| Axiom | `axiom` | ➖ | ➖ | ➖ | ➖ |
| BCLib | `bclib` | ➖ | ➖ | ➖ | ➖ |
| Better Beds | `better-beds` | ➖ | ➖ | ➖ | ➖ |
| Better Block Entities | `better-block-entities` | ➖ | ➖ | ➖ | ➖ |
| BetterEnd | `betterend` | ➖ | ➖ | ➖ | ➖ |
| BetterNether | `betternether` | ➖ | ➖ | ➖ | ➖ |
| Bosses of Mass Destruction | `bosses-of-mass-destruction` | ➖ | ➖ | ➖ | ➖ |
| Camera Overhaul | `cameraoverhaul` | ➖ | ➖ | ➖ | ➖ |
| Carpet Extra | `carpet-extra` | ➖ | ➖ | ➖ | ➖ |
| Carpet-Fixes | `carpet-fixes` | ➖ | ➖ | ➖ | ➖ |
| Cave Dust | `cave-dust` | ➖ | ➖ | ➖ | ➖ |
| Charm of Undying | `charm-of-undying` | ➖ | ➖ | ➖ | ➖ |
| Chef's Delight - Farmer's Delight Villagers | `chefs-delight` | ➖ | ➖ | ➖ | ➖ |
| ChoiceTheorem's Overhauled Village | `ct-overhaul-village` | ➖ | ➖ | ➖ | ➖ |
| CobbleDollars [Cobblemon Addon] | `cobbledollars` | ➖ | ➖ | ➖ | ➖ |
| CobbleFurnies | `cobblefurnies` | ➖ | ➖ | ➖ | ➖ |
| Cobblemon additions | `cobblemon-additions` | ➖ | ➖ | ➖ | ➖ |
| Cobblemon Capture XP | `cobblemon-capture-xp` | ➖ | ➖ | ➖ | ➖ |
| Cobblemon Fight or Flight Reborn | `cobblemon-fight-or-flight-reborn` | ➖ | ➖ | ➖ | ➖ |
| Cobblemon PokeNav | `cobblemon-pokenav` | ➖ | ➖ | ➖ | ➖ |
| Cobblemon Tim Core | `cobblemon-tim-core` | ➖ | ➖ | ➖ | ➖ |
| Cobblemon Trainer Battle | `cobblemon-trainer-battle` | ➖ | ➖ | ➖ | ➖ |
| Cobblemon: Legendary Monuments | `legendary-monuments` | ➖ | ➖ | ➖ | ➖ |
| Cobblemon: Mega Showdown | `cobblemon-mega-showdown` | ➖ | ➖ | ➖ | ➖ |
| CobbleThemes | `cobblethemes` | ➖ | ➖ | ➖ | ➖ |
| Cobbreeding | `cobbreeding` | ➖ | ➖ | ➖ | ➖ |
| Cobweb | `cobweb` | ➖ | ➖ | ➖ | ➖ |
| Crawl | `crawl` | ➖ | ➖ | ➖ | ➖ |
| Creeper Overhaul | `creeper-overhaul` | ➖ | ➖ | ➖ | ➖ |
| Cull Less Leaves | `cull-less-leaves` | ➖ | ➖ | ➖ | ➖ |
| Custom Splash Screen | `custom-splash-screen` | ➖ | ➖ | ➖ | ➖ |
| Do a Barrel Roll | `do-a-barrel-roll` | ➖ | ➖ | ➖ | ➖ |
| Every Compat (Wood Good) | `every-compat` | ➖ | ➖ | ➖ | ➖ |
| Fabric Seasons: Delight Compat | `fabric-seasons-delight-compat` | ➖ | ➖ | ➖ | ➖ |
| Farmer's Knives | `farmers-knives` | ➖ | ➖ | ➖ | ➖ |
| Fast Paintings | `fast-paintings` | ➖ | ➖ | ➖ | ➖ |
| Faster Random | `faster-random` | ➖ | ➖ | ➖ | ➖ |
| Flashback | `flashback` | ➖ | ➖ | ➖ | ➖ |
| Fog Overrides | `fogoverrides` | ➖ | ➖ | ➖ | ➖ |
| Fusion (Connected Textures) | `fusion-connected-textures` | ➖ | ➖ | ➖ | ➖ |
| GugleCarpetAddition | `gca` | ➖ | ➖ | ➖ | ➖ |
| Hearths | `hearths` | ➖ | ➖ | ➖ | ➖ |
| Held Item Info | `held-item-info` | ➖ | ➖ | ➖ | ➖ |
| Hold My Items | `hold-my-items` | ➖ | ➖ | ➖ | ➖ |
| Illager Invasion | `illager-invasion` | ➖ | ➖ | ➖ | ➖ |
| Immersive UI | `immersive-ui` | ➖ | ➖ | ➖ | ➖ |
| Incendium Legacy | `incendium` | ➖ | ➖ | ➖ | ➖ |
| JourneyMap | `journeymap` | ➖ | ➖ | ➖ | ➖ |
| Jump Over Fences | `jump-over-fences` | ➖ | ➖ | ➖ | ➖ |
| Kambrik | `kambrik` | ➖ | ➖ | ➖ | ➖ |
| LAN World Plug-n-Play (mcwifipnp) | `mcwifipnp` | ➖ | ➖ | ➖ | ➖ |
| Legendary Tooltips | `legendary-tooltips` | ➖ | ➖ | ➖ | ➖ |
| Let Me Despawn | `lmd` | ➖ | ➖ | ➖ | ➖ |
| Lithostitched | `lithostitched` | ➖ | ➖ | ➖ | ➖ |
| Magnum Torch | `magnum-torch` | ➖ | ➖ | ➖ | ➖ |
| MmmMmmMmmMmm | `mmmmmmmmmmmm` | ➖ | ➖ | ➖ | ➖ |
| Monsters in the Closet | `monsters-in-the-closet` | ➖ | ➖ | ➖ | ➖ |
| MoreCobblemonTweaks | `more-cobblemon-tweaks` | ➖ | ➖ | ➖ | ➖ |
| Navas ZA Megas | `navas-zamega` | ➖ | ➖ | ➖ | ➖ |
| NetherPortalFix | `netherportalfix` | ➖ | ➖ | ➖ | ➖ |
| Not Enough Recipe Book [NERB] | `notenoughrecipebook` | ➖ | ➖ | ➖ | ➖ |
| Noxesium | `noxesium` | ➖ | ➖ | ➖ | ➖ |
| Nuit Interop | `nuit-interop` | ➖ | ➖ | ➖ | ➖ |
| Nvidium | `nvidium` | ➖ | ➖ | ➖ | ➖ |
| Oh The Biomes We've Gone | `oh-the-biomes-weve-gone` | ➖ | ➖ | ➖ | ➖ |
| Open Loader | `open-loader` | ➖ | ➖ | ➖ | ➖ |
| Particle Effects | `particle-effects` | ➖ | ➖ | ➖ | ➖ |
| Radical Cobblemon Trainers | `rctmod` | ➖ | ➖ | ➖ | ➖ |
| Radical Cobblemon Trainers API | `rctapi` | ➖ | ➖ | ➖ | ➖ |
| Rechiseled | `rechiseled` | ➖ | ➖ | ➖ | ➖ |
| Resource Pack Overrides | `resource-pack-overrides` | ➖ | ➖ | ➖ | ➖ |
| Roughly Enough Professions (REP) | `roughly-enough-professions-rep` | ➖ | ➖ | ➖ | ➖ |
| Sable | `sable` | ➖ | ➖ | ➖ | ➖ |
| Sawmill | `universal-sawmill` | ➖ | ➖ | ➖ | ➖ |
| ServerCore | `servercore` | ➖ | ➖ | ➖ | ➖ |
| Servux | `servux` | ➖ | ➖ | ➖ | ➖ |
| Shoulder Surfing Reloaded | `shoulder-surfing-reloaded` | ➖ | ➖ | ➖ | ➖ |
| Show Me Your Skin! | `show-me-your-skin` | ➖ | ➖ | ➖ | ➖ |
| Simple Hats | `simple-hats` | ➖ | ➖ | ➖ | ➖ |
| Simple Rich Discord Presence | `srdp` | ➖ | ➖ | ➖ | ➖ |
| Smarter Farmers (farmers replant) | `smarter-farmers-farmers-replant` | ➖ | ➖ | ➖ | ➖ |
| Sodium Extras | `sodium-extras` | ➖ | ➖ | ➖ | ➖ |
| Sodium Leaf Culling | `sodiumleafculling` | ➖ | ➖ | ➖ | ➖ |
| Sodium Options Mod Compat | `sodium-options-mod-compat` | ➖ | ➖ | ➖ | ➖ |
| Sophisticated Backpacks (Unoffical Fabric port) | `sophisticated-backpacks-(unoffical-fabric-port)` | ➖ | ➖ | ➖ | ➖ |
| Sophisticated Core (Unofficial Fabric port) | `sophisticated-core-(unofficial-fabric-port)` | ➖ | ➖ | ➖ | ➖ |
| Sophisticated Storage (Unofficial Fabric port) | `sophisticated-storage-(unofficial-fabric-port)` | ➖ | ➖ | ➖ | ➖ |
| Stendhal | `stendhal` | ➖ | ➖ | ➖ | ➖ |
| Subtle Effects | `subtle-effects` | ➖ | ➖ | ➖ | ➖ |
| SwingThrough | `swingthrough` | ➖ | ➖ | ➖ | ➖ |
| The Aether | `aether` | ➖ | ➖ | ➖ | ➖ |
| The Bumblezone - Fabric | `the-bumblezone-fabric` | ➖ | ➖ | ➖ | ➖ |
| uku's Armor HUD | `ukus-armor-hud` | ➖ | ➖ | ➖ | ➖ |
| ViaFabricPlus | `viafabricplus` | ➖ | ➖ | ➖ | ➖ |
| VillagersPlus | `villagersplus` | ➖ | ➖ | ➖ | ➖ |
| Xaero Zoomout | `xaero-zoomout` | ➖ | ➖ | ➖ | ➖ |
| YUNG's Better Desert Temples | `yungs-better-desert-temples` | ➖ | ➖ | ➖ | ➖ |
| YUNG's Better Dungeons | `yungs-better-dungeons` | ➖ | ➖ | ➖ | ➖ |
| YUNG's Better End Island | `yungs-better-end-island` | ➖ | ➖ | ➖ | ➖ |
| YUNG's Better Jungle Temples | `yungs-better-jungle-temples` | ➖ | ➖ | ➖ | ➖ |
| YUNG's Better Mineshafts | `yungs-better-mineshafts` | ➖ | ➖ | ➖ | ➖ |
| YUNG's Better Nether Fortresses | `yungs-better-nether-fortresses` | ➖ | ➖ | ➖ | ➖ |
| YUNG's Better Ocean Monuments | `yungs-better-ocean-monuments` | ➖ | ➖ | ➖ | ➖ |
| YUNG's Better Strongholds | `yungs-better-strongholds` | ➖ | ➖ | ➖ | ➖ |
| YUNG's Better Witch Huts | `yungs-better-witch-huts` | ➖ | ➖ | ➖ | ➖ |
| YUNG's Bridges | `yungs-bridges` | ➖ | ➖ | ➖ | ➖ |
| cf--macaws-doors | `cf--macaws-doors` | ▫️ | ▫️ | ▫️ | ▫️ |
| Essential Mod | `essential` | ▫️ | ▫️ | ▫️ | ▫️ |
| Geckolib | `geckolib` | ▫️ | ▫️ | ▫️ | ▫️ |
| Patchouli | `patchouli` | ▫️ | ▫️ | ▫️ | ▫️ |
| Prism | `prism-lib` | ▫️ | ▫️ | ▫️ | ▫️ |
| Remove Reloading Screen | `rrls` | ▫️ | ▫️ | ▫️ | ▫️ |
| sample--cf-dynamiclights-reforged | `sample--cf-dynamiclights-reforged` | ▫️ | ▫️ | ▫️ | ▫️ |
| sample--cf-memory-settings | `sample--cf-memory-settings` | ▫️ | ▫️ | ▫️ | ▫️ |
| sample--cf-twigs | `sample--cf-twigs` | ▫️ | ▫️ | ▫️ | ▫️ |
| sample--mr-better-arthropods | `sample--mr-better-arthropods` | ▫️ | ▫️ | ▫️ | ▫️ |
| sample--mr-dark-paintings | `sample--mr-dark-paintings` | ▫️ | ▫️ | ▫️ | ▫️ |
| sample--mr-dine | `sample--mr-dine` | ▫️ | ▫️ | ▫️ | ▫️ |
| Sodium Dynamic Lights | `sodium-dynamic-lights` | ▫️ | ▫️ | ▫️ | ▫️ |
| Very Many Players (Fabric) | `vmp-fabric` | ▫️ | ▫️ | ▫️ | ▫️ |

## How many of each, per line

| Mark | 1.21.x | 26.x | 1.20.6 (reforged) | 1.20.6 |
|---|---|---|---|---|
| OK | 0 | 0 | 0 | 0 |
| NO | 0 | 0 | 0 | 0 |
| BAD | 0 | 0 | 0 | 0 |
| BLOCKED | 0 | 0 | 0 | 0 |
| TITLE | 0 | 0 | 0 | 0 |
| DEP | 0 | 0 | 0 | 0 |
| N/A | 0 | 0 | 0 | 0 |
| NOT RUN | 0 | 0 | 0 | 0 |

## How to read it, and what it does not say

Check the **Mod** column for what you want to install, then your line's column. **OK** means it got to the world with
OptiFabric; **NO** and **BLOCKED** mean do not install it (**BLOCKED** is a refusal at load time - no crash report,
nothing in `logs/latest.log`); **TITLE** means take a backup before trying it; **BAD** is the mod's own fault and has
nothing to do with this mod; **N/A** and **NOT RUN** mean unknown, which is not the same as working.

The measurement is "does it reach the world", not whether every feature works. The mod versions are the ones current
when the sweep ran, and a verdict does not carry across Minecraft versions: a 26.2 build of the same mod is not the
build that was tested here. The per-mod data and a section on what the sweep did not cover are in `COMPATIBILITY.md`
in each line, and in `docs/COMPATIBILITY.md` and `docs/COMPATIBILITY_CN.md` on the 1.21.x line.

## 中文说明

**这张表是什么**:**一次**模组扫描的结果(跑在 **Minecraft 1.21.1**、OptiFabric **2.2.x** 线上),逐条记录每个模组与 OptiFine 一起启动时**走到哪一步**。四条线的清单来自同一批数据 —— 其中 `26.x`、`1.20.6 (reforged)`、`1.20.6` 三份**逐字节相同**,`1.21.x` 那份额外多了一次 6 个模组的复测、并把 5 个模组的结论从「与 OptiFine 不兼容」改成「已进世界」。**请把它读成"这套组合被测过",而不是"每条线各测了一次"。**

**表的读法**:每一行是一个模组,四列分别是四条线里该模组的结果;某条线的清单里**没有这个模组**时写 `—`。

| 标记 | 含义 |
|---|---|
| ✅ | 进了世界(括号里的秒数是进入世界所用时间;`retested` 表示那是复测确认的) |
| ❌ | 与 OptiFine 不兼容:装上就起不来或进不去世界 |
| 🚫 | 在原版 Fabric(不装本模组)上**同样**坏 ⇒ **与本模组无关** |
| ⛔ | 声明性拒载:模组自己在元数据里声明与本模组冲突(Sodium;自 **2.2.11** 起为硬拒载) |
| ⚠️ | 只到标题界面,没进世界 |
| 🧩 | 依赖没解析出来(缺前置) |
| ➖ | 不可测(例如只服务端、或平台不匹配) |
| ▫️ | 未运行(这一轮没排到) |
| — | 该线的清单里没有这一条 |

**怎么用**:想装某个模组,先在 **Mod** 列找到它,再看你要用的那条线的列。**✅ 可以直接装**;**❌ / ⛔ 别装**(⛔ 是加载器层直接拒绝,不会给崩溃报告);**⚠️ 先备份世界再试**;`🚫` 说明那是那个模组自己的问题,**跟本模组无关**;**➖ / ▫️ 表示未知,不代表能用**。

**口径与边界**:测的是"能不能进世界"这一条,不是逐项功能;模组版本是扫描当时的最新版;结论**不跨 Minecraft 版本平移**(同名的 26.x 构建不等于 1.21.1 上测过的那个构建)。原始逐条数据与"这次扫描没覆盖什么"见各线根目录的 `COMPATIBILITY.md`,以及 `docs/COMPATIBILITY.md` / `docs/COMPATIBILITY_CN.md`(1.21.x 线)。