$ErrorActionPreference = 'Stop'
$p = 'I:\mods\OptiFabric\dist\README.txt'
$text = [System.IO.File]::ReadAllText($p, [System.Text.Encoding]::UTF8)

$pairs = @(
	@('OptiFabric-Reforged-2.2.3+mc26.2.jar        562 个补丁类   180471 字节', 'OptiFabric-Reforged-2.2.3+mc26.2.jar        562 个补丁类   188876 字节'),
	@('2654F5085EF4E5CA958BDECF627A2BCB195B608AAC1E5EA31C21445479D541C9', '07B91508EE9948A5EF35FD343A057D2ACB1530B6525DF851C161390968FC45A8'),
	@('OptiFabric-Reforged-2.2.3+mc26.1.2.jar      567 个补丁类   180474 字节', 'OptiFabric-Reforged-2.2.3+mc26.1.2.jar      567 个补丁类   188879 字节'),
	@('4EC5357B58238851005EA7D78FA85199F204F63A96F13FBFB6853F074402EFC9', '611C717726FA066B57CB54815224904636FD385276BD9B01159CE1D6E1C811AA'),
	@("    (2.2.2 只改声明与文档:`fabric.mod.json` 里 sodium 同时写进 conflicts 与 breaks,`r`n     两份 README 补上「声明的不兼容」。加载器只警告、不拦启动)", "    (2.2.3 修的是'起不来':给 OptiFine 那份 GuiRenderer`$Draw 补上 Fabric API 的 accessor 接口;`r`n     接口按类路径查找、不写死名字 —— 26.2 是 GuiRendererDrawAccessor、26.1.2 是 DrawAccessor)`r`n    (2.2.2 只改声明与文档:`fabric.mod.json` 里 sodium 同时写进 conflicts 与 breaks,`r`n     两份 README 补上「声明的不兼容」。加载器只警告、不拦启动)")
)

$report = @()
foreach ($pair in $pairs) {
	$old = $pair[0]
	$new = $pair[1]
	$count = ([regex]::Matches($text, [regex]::Escape($old))).Count
	$report += "hits=$count  $($old.Substring(0, [Math]::Min(60, $old.Length)))"
	if ($count -ne 1) { continue }
	$text = $text.Replace($old, $new)
}

[System.IO.File]::WriteAllText($p, $text, (New-Object System.Text.UTF8Encoding($false)))
$report
