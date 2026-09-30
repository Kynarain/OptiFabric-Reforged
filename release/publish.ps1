# NOTE: keep this file UTF-8 WITH BOM. Windows PowerShell reads .ps1 as ANSI when there is no BOM, and the
# Chinese text below then mis-parses (a trailing quote gets eaten and the whole file fails to load).
<#
    把 dist/ 里十版 jar 逐个发到三个平台。逐版一个发布条目,版本号就是 <版本>+mc<MC版本>。

    用法:
      # 先看要执行什么(不联网、不改远端)
      .\release\publish.ps1 -DryRun
      # 只发某一个版本
      .\release\publish.ps1 -Version 1.21.8
      # 真发(需要下面的凭据)
      .\release\publish.ps1

    凭据(用环境变量,不要写进文件):
      GitHub      : gh auth login 之后即可(或设 GITHUB_TOKEN)
      Modrinth    : $env:MODRINTH_TOKEN   (modrinth.com/settings/account 里创建 PAT,需 create versions 权限)
                    $env:MODRINTH_PROJECT_ID
      CurseForge  : $env:CURSEFORGE_TOKEN (curseforge.com/account/api-tokens)
                    $env:CURSEFORGE_PROJECT_ID

    说明:Modrinth 与 CurseForge 的提交都用 curl.exe 发 multipart(Windows 自带 PowerShell 5.1 没有 -Form 参数),
    元数据 JSON 会先写到 release\tmp\ 里,方便你看清楚究竟提交了什么。
#>
[CmdletBinding()]
param(
	[string]$Version = "all",
	[switch]$DryRun
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
# 本仓库只有 1.21.x 一条发布线(见 release\MANUAL_RELEASE.md)。
# 版本基数:$defaultModVersion 是 1.21.x 里没有例外值的那些版本用的。
$versions = @("1.21", "1.21.1", "1.21.3", "1.21.4", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11")
$defaultModVersion = "2.0.0"
# 逐 MC 版本的例外值:某个版本单独升过版就写在这里(1.21.11 的 1.1.1 = 只修它一个版本的抗锯齿后处理链)。
# 这张表由 release\version.ps1 -Mc 维护,别手改(见 docs\VERSIONING.md 第五节)。
$modVersions = @{ "1.21.11" = "2.0.0"; "1.21.3" = "2.0.0"; "1.21.4" = "2.0.0"; "1.21.6" = "2.0.0"; "1.21.7" = "2.0.0"; "1.21.8" = "2.0.0"; "1.21.9" = "2.0.0"; "1.21.10" = "2.0.0" }
# 产物名默认就是 OptiFabric(mod id 也是 optifabric)。下面几张逐版本覆盖表现在都是空的 ——
# 只有某个 MC 版本要用别的产物名 / 显示名 / tag 分支时才往里加一条。
$defaultArtifact = "OptiFabric"
$modArtifacts = @{}
# The display name follows the artifact; the per-version override table is empty while every jar is the same mod.
$defaultModName = "OptiFabric"
$modNames = @{}
# 本仓库的开发与发布分支:1.21.x 的修复开发与 tag 都在 1.21.x 分支上(见 docs\PUBLISHING.md)。
$defaultTagTarget = "1.21.x"
$modTagTargets = @{}

if ($Version -ne "all") {
	if ($versions -notcontains $Version) { throw "未知版本: $Version(可选:" + ($versions -join ", ") + ")" }
	$versions = @($Version)
}

$tmp = Join-Path $root "release\tmp"
New-Item -ItemType Directory -Force $tmp | Out-Null

#1.21.6 / 1.21.7 的 OptiFine 构建自身有缺陷,默认不推荐发布(要发就加 -Version 单独发)
$unsupported = @("1.21.6", "1.21.7")

# 同一版本号可能覆盖多个 jar(例如 2.0.0 覆盖全部十版)。GitHub 的 tag 就是版本号本身,所以这种情况下只能建
# **一个** release、把共用的 jar 全部挂上去 —— 否则第二个 gh release create 会撞同一个 tag。Modrinth 与
# CurseForge 不受影响:它们本来就要求每个 MC 版本一个 version 条目。
$mcsByVersion = @{}
foreach ($eachMc in $versions) {
	$eachVersion = if ($modVersions.ContainsKey($eachMc)) { $modVersions[$eachMc] } else { $defaultModVersion }
	if (-not $mcsByVersion.ContainsKey($eachVersion)) { $mcsByVersion[$eachVersion] = @() }
	$mcsByVersion[$eachVersion] += $eachMc
}
$releasedTags = @{}

foreach ($mc in $versions) {
	$modVersion = if ($modVersions.ContainsKey($mc)) { $modVersions[$mc] } else { $defaultModVersion }

	$artifact = if ($modArtifacts.ContainsKey($mc)) { $modArtifacts[$mc] } else { $defaultArtifact }
	$modName = if ($modNames.ContainsKey($mc)) { $modNames[$mc] } else { $defaultModName }
$jar = Join-Path $root "dist\$artifact-$modVersion+mc$mc.jar"
	$notes = Join-Path $root "release\notes\mc$mc.md"
	# Tag shape follows the releases this repo already has (v1.1.0, v1.1.2): the version number alone. The MC
	# version stays in the artifact name and in the release title, not in the tag.
	$tag = "v$modVersion"
	$title = "$modName $modVersion+mc$mc"
	# Which branch the tag is made on: gh would otherwise tag the default branch (main), which is not where this
	# release line lives. This line is released from 1.21.x (see the branch section of docs\PUBLISHING.md);
	# mc1.21.x is the single-project layout the ten 1.1.0 jars were built from and is kept as history only.
	$tagTarget = if ($modTagTargets.ContainsKey($mc)) { $modTagTargets[$mc] } else { $defaultTagTarget }

	if (-not (Test-Path $jar)) { Write-Warning "跳过 $mc :没有 $jar"; continue }
	if (-not (Test-Path $notes)) { Write-Warning "跳过 $mc :没有 $notes"; continue }
	if ($unsupported -contains $mc) { Write-Warning "$mc 属于不推荐的版本(OptiFine 构建自身缺陷),仍会按你指定的版本号发布" }

	Write-Host "=== $title ==="

	#GitHub —— 每个**版本号**一个 release,不是每个 jar 一个:共用这个版本号的 jar 一起挂上去
	#(见 $mcsByVersion)。tag 就是版本号本身,与已有发布一致(v1.1.0、v1.1.2)。
	if ($releasedTags.ContainsKey($tag)) {
		Write-Host "  [github]     ($tag 已经建好,这个 jar 挂在那一个 release 上)"
	}
	else {
		$releasedTags[$tag] = $true
		$shared = @($mcsByVersion[$modVersion] | Where-Object { Test-Path (Join-Path $root "dist\$artifact-$modVersion+mc$_.jar") })
		if ($shared.Count -eq 0) { $shared = @($mc) }
		# 真引号,不是反引号转义:这一段拼出来的字符串会被 Invoke-Expression 再解析一次。
		$attach = ($shared | ForEach-Object { '"' + (Join-Path $root "dist\$artifact-$modVersion+mc$_.jar") + '"' }) -join " "
		$ghTitle = $title
		$notesFile = $notes

		if ($shared.Count -gt 1) {
			# 多个 jar 共用一个版本号:正文取这条线最新的那一版说明,再附上所有附件的尺寸与 SHA-256。
			$ghTitle = "$modName $modVersion"
			$bodyMc = $shared[$shared.Count - 1]
			$body = [System.IO.File]::ReadAllText((Join-Path $root "release\notes\mc$bodyMc.md"), [System.Text.Encoding]::UTF8)
			$rows = foreach ($eachMc in $shared) {
				$eachJar = Join-Path $root "dist\$artifact-$modVersion+mc$eachMc.jar"
				'| {0} | {1} | {2} | {3} |' -f $eachMc, (Split-Path -Leaf $eachJar), (Get-Item $eachJar).Length, (Get-FileHash $eachJar -Algorithm SHA256).Hash
			}
			$body += "`r`n---`r`n`r`n## 本条目附带的 $($shared.Count) 个 jar`r`n`r`n| Minecraft | 文件 | 字节 | SHA-256 |`r`n|---|---|---|---|`r`n" + ($rows -join "`r`n") + "`r`n"
			$notesFile = Join-Path $tmp "github-$tag.md"
			[System.IO.File]::WriteAllText($notesFile, $body, (New-Object System.Text.UTF8Encoding($false)))
		}

		$gh = "gh release create `"$tag`" $attach --title `"$ghTitle`" --notes-file `"$notesFile`" --target `"$tagTarget`""
		if ($DryRun) { Write-Host "  [github]     $gh" }
		else {
			git push origin $tagTarget
			Invoke-Expression $gh
		}
	}

	#Modrinth
	$mrMeta = Join-Path $tmp "modrinth-$mc.json"
	$payload = [ordered]@{
		name           = $title
		version_number = "$modVersion+mc$mc"
		changelog      = [System.IO.File]::ReadAllText($notes, [System.Text.Encoding]::UTF8)
		dependencies   = @()
		game_versions  = @($mc)
		version_type   = "release"
		loaders        = @("fabric")
		client_side    = "required"       # OptiFabric 是客户端模组:OptiFine 本身只在客户端
		server_side    = "unsupported"
		featured       = $false
		project_id     = $env:MODRINTH_PROJECT_ID
		file_parts     = @("file")
		primary_file   = "file"
	}
	[System.IO.File]::WriteAllText($mrMeta, ($payload | ConvertTo-Json -Depth 5), (New-Object System.Text.UTF8Encoding($false)))
	$mr = "curl.exe -sS -X POST https://api.modrinth.com/v2/version -H `"Authorization: $env:MODRINTH_TOKEN`" -F `"data=@$mrMeta;type=application/json`" -F `"file=@$jar`""
	if ($DryRun) { Write-Host "  [modrinth]   metadata -> $mrMeta"; Write-Host "               $mr" }
	else {
		if (-not $env:MODRINTH_TOKEN -or -not $env:MODRINTH_PROJECT_ID) { Write-Warning "  缺 MODRINTH_TOKEN / MODRINTH_PROJECT_ID,跳过 Modrinth" }
		else { Invoke-Expression $mr }
	}

	#CurseForge
	$cfMeta = Join-Path $tmp "curseforge-$mc.json"
	$meta = [ordered]@{
		changelog     = [System.IO.File]::ReadAllText($notes, [System.Text.Encoding]::UTF8)
		changelogType = "markdown"
		displayName   = $title
		releaseType   = "release"
		gameVersions  = @($mc, "Fabric")
	}
	[System.IO.File]::WriteAllText($cfMeta, ($meta | ConvertTo-Json -Depth 5), (New-Object System.Text.UTF8Encoding($false)))
	$cf = "curl.exe -sS -X POST `"https://minecraft.curseforge.com/api/projects/$env:CURSEFORGE_PROJECT_ID/upload`" -H `"X-Api-Token: $env:CURSEFORGE_TOKEN`" -F `"metadata=@$cfMeta;type=application/json`" -F `"file=@$jar`""
	if ($DryRun) { Write-Host "  [curseforge] metadata -> $cfMeta"; Write-Host "               $cf" }
	else {
		if (-not $env:CURSEFORGE_TOKEN -or -not $env:CURSEFORGE_PROJECT_ID) { Write-Warning "  缺 CURSEFORGE_TOKEN / CURSEFORGE_PROJECT_ID,跳过 CurseForge" }
		else { Invoke-Expression $cf }
	}
}

Write-Host ""
Write-Host "完成。逐版备注在 release\notes\,元数据留档在 release\tmp\。"
if ($DryRun) { Write-Host "这是 -DryRun:刚才什么都没发。" }
