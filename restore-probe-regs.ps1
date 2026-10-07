$ErrorActionPreference = 'Stop'
$p = 'I:\mods\OptiFabric\src\main\java\kynarain\cn\optifabric\patcher\fixes\OptifineFixer.java'
$text = [System.IO.File]::ReadAllText($p, [System.Text.Encoding]::UTF8)

$anchor = "`t`t`t`tnew AddInterfaceFix(`"net/minecraft/client/gui/render/GuiRenderer`$Draw`"));`r`n"

$lines = @(
	'',
	'		//The three registrations below are the read-only experiment pair (see docs\PORT_26.x.md and the fixers',
	'		//themselves). They are property-gated, so with neither property set they are inert: nothing is taken over,',
	'		//nothing is flipped, and the patched-class count and every verifier figure stay where they were.',
	'		//net/minecraft/client/renderer/rendertype/PreparedRenderType (26.2 only): the class OptiFine''s patch set',
	'		//never patches, so 26.1.2''s per-draw shader state calls inside RenderType.draw have no caller left. Only',
	'		//with -Doptifabric.experimentalPerDraw=true.',
	'		registerFix("net/minecraft/client/renderer/rendertype/PreparedRenderType", new PerDrawShaderStateFix());',
	'		//com/mojang/blaze3d/opengl/GlCommandEncoder.createRenderPass: where a draw decides which textures it',
	'		//writes into. Only with -Doptifabric.experimentalFormatProbe=true.',
	'		registerFix("com/mojang/blaze3d/opengl/GlCommandEncoder", new RenderPassProbeFix());',
	'		//net/minecraft/client/renderer/chunk/ChunkSectionsToRender.renderGroup: the chunk-section pass for one',
	'		//layer group, and where OptiFine''s own preRenderChunkLayer/postRenderChunkLayer hooks live. Only with',
	'		//-Doptifabric.experimentalFormatProbe=true.',
	'		registerFix("net/minecraft/client/renderer/chunk/ChunkSectionsToRender", new SectionDrawProbeFix());',
	''
)
$block = ($lines -join "`r`n") + "`r`n"

$count = ([regex]::Matches($text, [regex]::Escape($anchor))).Count
if ($count -ne 1) { Write-Output "anchor hits=$count - refusing to edit"; exit 1 }

$text = $text.Replace($anchor, $anchor + $block)
[System.IO.File]::WriteAllText($p, $text, (New-Object System.Text.UTF8Encoding($false)))

$b = [System.IO.File]::ReadAllBytes($p)
Write-Output ("written: {0} bytes, crlf={1}, lf={2}" -f $b.Length, ([regex]::Matches([Text.Encoding]::GetEncoding(28591).GetString($b), "`r`n")).Count, ([regex]::Matches([Text.Encoding]::GetEncoding(28591).GetString($b), "`n")).Count)
