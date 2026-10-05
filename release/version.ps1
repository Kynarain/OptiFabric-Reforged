# 版本号自动化(SemVer 2.0.0,见 docs/VERSIONING.md)
#
# 一次版本号改动要同时落在三个地方:项目的 gradle.properties、release\publish.ps1 的映射,以及
# README / CHANGELOG / docs\ / release\notes\ / release\MANUAL_RELEASE*.md 里所有
# "<版本>+mc" 的写法。手改漏一处就会出现文档与产物对不上,所以只走这个脚本。
#
#   .\release\version.ps1                                  # 看:当前版本,以及三类递增各会变成什么
#   .\release\version.ps1 -Line 1.21.x -Kind minor         # 1.1.0 -> 1.2.0(真正写入)
#   .\release\version.ps1 -Line 1.21.x -Kind patch -DryRun # 1.1.0 -> 1.1.1,只看结果,不写文件
#   .\release\version.ps1 -Line 1.21.x -Set 1.2.0-beta.1   # 直接指定(校验格式与优先级)
#   .\release\version.ps1 -Line 1.21.x -Part               # 只打印当前版本号(给别的脚本用)
#   .\release\version.ps1 -Line 1.21.x -RecordDigest       # 构建之后:把产物的字节数与 SHA-256 写回文档
#
# 发布页上"这一版的两条产物各由哪次提交构建"那一句也由这个脚本生成(那一句里没有 "<版本>+mc" 串,所以递增与
# -RecordDigest 都不会碰到它,手写已经错过两次)。两条产物都构建完、提交都定下来之后跑:
#
#   .\release\version.ps1 -RecordProvenance -Head <默认产物的提交> -FullHead <-full 产物的提交>
#
# 版本号默认取 gradle.properties 的当前值;给更早的版本补记时加 -ProvenanceVersion <版本>。
#
# 发布前离线核对"哪一版 MC 要哪个 OptiFine 构建":README 表、release\notes\ 与模组里的 OptifineSupport
# 三者必须同名,不看网络。改动支持表或换 OptiFine 构建之后跑一遍:
#
#   .\release\version.ps1 -CheckSupport
#
# 一个 jar 对应一个 MC 版本,所以"只改了某一个 MC 版本的行为"时,只给那一个产物升版,不要连累其余九个:
#
#   .\release\version.ps1 -Line 1.21.x -Mc 1.21.11 -Kind patch   # 1.1.0+mc1.21.11 -> 1.1.1+mc1.21.11,其余不动
#   .\release\version.ps1 -Line 1.21.x -Mc 1.21.11               # 看那个版本的当前值
#   .\release\version.ps1 -Line 1.21.x -Mc 1.21.11 -RecordDigest # 那个产物的尺寸与 SHA-256
#
# 逐 MC 版本的例外值记在 release\publish.ps1 的 $modVersions 里(发布脚本本来就要靠它取文件名),
# 没有例外的版本仍用项目的 gradle.properties 基数。文档里只有 "<版本>+mc<MC>" 这一串被改写,
# 所以别的 MC 版本与历史版本号都不会被动到。
#
# 递增依据 SemVer:patch = 向下兼容的修正(minor 与 patch 归零规则见规范 §7/§8),
# minor = 向下兼容的新功能,patch 号归零;major = 不兼容修改,次版本号与修订号都归零。
# 新版本必须比当前版本**优先级更高**(§11),否则拒绝写入(要硬来加 -Force)。
[CmdletBinding()]
param(
	# Which release line to version. This repository carries the 1.21.x line only.
	[ValidateSet("1.21.x")]
	[string]$Line,
	# Which part of the version to increment, per SemVer: major | minor | patch.
	[ValidateSet("major", "minor", "patch")]
	[string]$Kind,
	# Set an explicit version instead of incrementing one (validated like any other).
	[string]$Set,
	# Version one Minecraft version's jar instead of the whole line: the override map in release\publish.ps1
	# gets the new value and only "<version>+mc<Mc>" references are rewritten. For a fix that only changes how
	# one release behaves - rebuilding the other nine at a new version would freeze nothing and prove nothing.
	[string]$Mc,
	# Print the line's current version and nothing else.
	[switch]$Part,
	# Show what would change without writing anything.
	[switch]$DryRun,
	# Write the built jar's size and SHA-256 into the documents that record them.
	[switch]$RecordDigest,
	# The commit the default artifact (no "-full" suffix - the one uploaded to CurseForge / Modrinth) was built
	# from. -RecordProvenance writes it, and the other, into the sentence every release\notes\mc*.md carries.
	[string]$Head,
	# The commit the "-full" artifact was built from.
	[string]$FullHead,
	# Which version's provenance is being recorded; defaults to the current gradle.properties base. Only needed
	# when back-filling an older version.
	[string]$ProvenanceVersion,
	# Write the "which commit built which artifact" sentence back into all ten release\notes\mc*.md.
	[switch]$RecordProvenance,
	# Offline cross-check: the README tables, release\notes\mc<MC>.md and the mod's own support table must name
	# the same OptiFine build for every Minecraft version. Touches no network and writes no file.
	[switch]$CheckSupport,
	# Allow a new version that is not greater than the current one.
	[switch]$Force
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot

# SemVer 2.0.0, second official regex (the one without named groups): major, minor, patch, prerelease, build.
$semverRegex = '^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?$'

# Per line: the project directory (the repository root is the Gradle project now), the artifact prefix that
# names its jars, and the document files that carry "<version>+mc" strings.
$lines = @{
	"1.21.x" = @{
		project  = "."
		artifact = "OptiFabric"
		mc       = "1.21.11"
	}
}

$documentFiles = @(
	"README.md",
	"README_CN.md",
	"CHANGELOG.md",
	"docs/DESCRIPTION.md",
	"docs/PUBLISHING.md",
	"release/MANUAL_RELEASE.md",
	"docs/RELEASE_NOTES.md"
)
# Every per-release note file belongs here too: each one carries its own "<version>+mc<mc>" strings, and a bump
# that misses them leaves the release page for that Minecraft version describing an older jar. Globbed rather than
# listed, so adding a release note cannot forget this.
$documentFiles += @(Get-ChildItem (Join-Path $root "release/notes") -Filter "mc*.md" | ForEach-Object { "release/notes/" + $_.Name })
$documentFiles = @($documentFiles | Select-Object -Unique)
# docs/VERSIONING.md is deliberately absent: its version numbers are examples of the rules, not statements about
# the current release, so a bump must not rewrite them.

# Reads a file the way these scripts must write it back: UTF-8, with the BOM it already had (a BOM-less .ps1 with
# Chinese text is read as ANSI by Windows PowerShell and then fails to parse).
function Read-ReleaseFile([string]$path) {
	$bytes = [System.IO.File]::ReadAllBytes((Join-Path $root $path))
	$text = [System.Text.Encoding]::UTF8.GetString($bytes)
	if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
		$text = $text.Substring(1)
	}

	return $text
}

function Write-ReleaseFile([string]$path, [string]$text) {
	$full = Join-Path $root $path
	$bytes = [System.IO.File]::ReadAllBytes($full)
	$bom = ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF)

	if (-not $DryRun) {
		[System.IO.File]::WriteAllText($full, $text, (New-Object System.Text.UTF8Encoding($bom)))
	}
}

# The shape a document has to keep: its BOM, its newline style, and no stray C0 control bytes. -RecordProvenance
# rewrites text inside files this script must not reformat, so it compares the shape before and after the write.
function Get-FileShape([string]$fullPath) {
	$bytes = [System.IO.File]::ReadAllBytes($fullPath)
	$crlf = 0
	$bareLf = 0
	$control = 0
	for ($i = 0; $i -lt $bytes.Length; $i++) {
		$b = $bytes[$i]
		if ($b -eq 0x0D) {
			if ($i + 1 -lt $bytes.Length -and $bytes[$i + 1] -eq 0x0A) { $crlf++ } else { $control++ }
		} elseif ($b -eq 0x0A) { $bareLf++ } elseif ($b -lt 0x20 -and $b -ne 0x09) { $control++ }
	}

	return @{
		bom     = ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF)
		crlf    = $crlf
		bareLf  = $bareLf
		control = $control
	}
}

# The Minecraft versions release\publish.ps1 iterates; that list is what decides whether a version has a jar at
# all, so it is also the list a -Mc value has to come from (no second copy to keep in sync).
function Get-PublishVersions {
	$text = Read-ReleaseFile "release/publish.ps1"
	$match = [regex]::Match($text, '(?s)\$versions\s*=\s*@\((.*?)\)')
	if (-not $match.Success) { throw "release\publish.ps1 里找不到 `$versions 列表" }

	return @([regex]::Matches($match.Groups[1].Value, '"([^"]+)"') | ForEach-Object { $_.Groups[1].Value })
}

function Get-LineVersions([string]$line) {
	$prefix = '^1\.21'

	return @(Get-PublishVersions | Where-Object { $_ -match $prefix })
}

# The per-Minecraft-version exceptions in release\publish.ps1: @{ "1.21.11" = "1.1.1" } means that jar is 1.1.1
# while the rest of the line stays on the gradle.properties base.
function Get-McVersionMap {
	$text = Read-ReleaseFile "release/publish.ps1"
	$match = [regex]::Match($text, '(?s)\$modVersions\s*=\s*@\{(.*?)\}')
	if (-not $match.Success) { throw "release\publish.ps1 里找不到 `$modVersions 映射" }

	$map = @{}
	foreach ($pair in [regex]::Matches($match.Groups[1].Value, '"([^"]+)"\s*=\s*"([^"]+)"')) {
		$map[$pair.Groups[1].Value] = $pair.Groups[2].Value
	}

	return $map
}

# One jar's version: its own exception if it has one, otherwise the line's base.
function Resolve-Version([string]$line, [string]$mc) {
	$map = Get-McVersionMap
	if ($map.ContainsKey($mc)) { return $map[$mc] }

	return Get-CurrentVersion $line
}

# Writes one jar's new version into the publish script's map, adding the key when it is not there yet.
function Set-McVersion([string]$mc, [string]$version) {
	$map = Get-McVersionMap
	if ($map.ContainsKey($mc)) {
		return Update-File "release/publish.ps1" @{ ('"' + $mc + '" = "' + $map[$mc] + '"') = ('"' + $mc + '" = "' + $version + '"') }
	}

	$text = Read-ReleaseFile "release/publish.ps1"
	$entry = '"' + $mc + '" = "' + $version + '"'
	$updated = [regex]::Replace($text, '(?s)(\$modVersions\s*=\s*@\{)(.*?)(\})', {
			param($m)
			$body = $m.Groups[2].Value.TrimEnd()
			$body = if ($body -eq "") { ' ' + $entry + ' ' } else { $body + '; ' + $entry + ' ' }

			return $m.Groups[1].Value + $body + $m.Groups[3].Value
		}, 1)
	if ($updated -eq $text) { throw "没能把 $mc 写进 release\publish.ps1 的 `$modVersions 映射" }

	Write-ReleaseFile "release/publish.ps1" $updated

	return 1
}

function Get-CurrentVersion([string]$line) {
	$properties = Join-Path $root (Join-Path $lines[$line].project "gradle.properties")
	$match = Select-String -Path $properties -Pattern '^mod_version_base=(.+)$' | Select-Object -First 1
	if (-not $match) { throw "找不到 $properties 里的 mod_version_base" }

	return $match.Matches[0].Groups[1].Value.Trim()
}

function Parse-Version([string]$text) {
	if ($text -notmatch $semverRegex) { throw "不是合法的语义化版本号: '$text'(见 https://semver.org/lang/zh-CN/)" }

	return @{
		text   = $text
		major  = [int]$Matches[1]
		minor  = [int]$Matches[2]
		patch  = [int]$Matches[3]
		pre    = $Matches[4]
		build  = $Matches[5]
	}
}

# SemVer §11: compare major, minor and patch numerically, then prerelease presence (a prerelease is lower),
# then the dot-separated prerelease identifiers. Build metadata is ignored, as the spec requires.
function Compare-Version($a, $b) {
	foreach ($part in @("major", "minor", "patch")) {
		if ($a[$part] -ne $b[$part]) { return [Math]::Sign($a[$part] - $b[$part]) }
	}

	$preA = [string]$a.pre
	$preB = [string]$b.pre
	if ($preA -eq $preB) { return 0 }
	if ($preA -eq "") { return 1 }    # a release outranks a prerelease
	if ($preB -eq "") { return -1 }

	$idsA = $preA.Split(".")
	$idsB = $preB.Split(".")
	for ($i = 0; $i -lt [Math]::Min($idsA.Count, $idsB.Count); $i++) {
		$numA = $idsA[$i] -match '^\d+$'
		$numB = $idsB[$i] -match '^\d+$'
		if ($numA -and $numB) {
			if ([int]$idsA[$i] -ne [int]$idsB[$i]) { return [Math]::Sign([int]$idsA[$i] - [int]$idsB[$i]) }
		} elseif ($numA -ne $numB) {
			return $(if ($numA) { -1 } else { 1 })   # numeric identifiers rank lower
		} elseif ($idsA[$i] -cne $idsB[$i]) {
			return [Math]::Sign([string]::CompareOrdinal($idsA[$i], $idsB[$i]))
		}
	}

	return [Math]::Sign($idsA.Count - $idsB.Count)
}

function Next-Version([string]$current, [string]$kind) {
	$v = Parse-Version $current

	switch ($kind) {
		"major" { return "$($v.major + 1).0.0" }
		"minor" { return "$($v.major).$($v.minor + 1).0" }
		"patch" { return "$($v.major).$($v.minor).$($v.patch + 1)" }
	}

	throw "未知的递增类型: $kind"
}

# Rewrites one file in place, keeping its encoding (and its BOM, where it has one - a BOM-less .ps1 with Chinese
# text fails to parse under Windows PowerShell).
function Update-File([string]$path, [hashtable]$pairs) {
	$full = Join-Path $root $path
	if (-not (Test-Path $full)) { return 0 }

	$bytes = [System.IO.File]::ReadAllBytes($full)
	$bom = ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF)
	$text = [System.Text.Encoding]::UTF8.GetString($bytes)
	if ($bom) { $text = $text.Substring(1) }

	$count = 0
	foreach ($key in $pairs.Keys) {
		$hits = ([regex]::Matches($text, [regex]::Escape($key))).Count
		if ($hits -gt 0) {
			$text = $text.Replace($key, $pairs[$key])
			$count += $hits
		}
	}

	if ($count -gt 0 -and -not $DryRun) {
		[System.IO.File]::WriteAllText($full, $text, (New-Object System.Text.UTF8Encoding($bom)))
	}

	return $count
}

# Same, but the search is a regular expression, so a bump can leave alone the names where this version sits inside
# a *different*, frozen artifact: an earlier release's file keeps "archive-OptiFabric-1.1.0+mc…-live-verified…" as
# its name, and that name belongs to that older build, not to this one.
function Update-FilePattern([string]$path, [string]$pattern, [string]$replacement) {
	$full = Join-Path $root $path
	if (-not (Test-Path $full)) { return 0 }

	$bytes = [System.IO.File]::ReadAllBytes($full)
	$bom = ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF)
	$text = [System.Text.Encoding]::UTF8.GetString($bytes)
	if ($bom) { $text = $text.Substring(1) }

	$count = ([regex]::Matches($text, $pattern)).Count
	if ($count -gt 0) {
		$text = [regex]::Replace($text, $pattern, { param($m) $replacement })
		if (-not $DryRun) {
			[System.IO.File]::WriteAllText($full, $text, (New-Object System.Text.UTF8Encoding($bom)))
		}
	}

	return $count
}

function Get-BuiltJar([string]$line, [string]$version, [string]$mc) {
	$name = "$($lines[$line].artifact)-$version+mc$mc.jar"

	return @{ name = $name; path = (Join-Path $root (Join-Path $lines[$line].project "build/libs/$name")) }
}

# -RecordDigest has to describe what was *published*, so the values come from dist\ whenever that jar exists:
# build\libs\ is only the working tree's most recent build, and after any rebuild it no longer holds what the
# release page says. If both directories hold this name with different bytes they are not the same artifact, so
# the tool never guesses: a real run stops (guessing would rewrite a published size/SHA-256 with a jar nobody can
# download), while -DryRun writes nothing and therefore reports the difference and computes from the published
# dist\ copy.
function Get-DigestJar([string]$line, [string]$version, [string]$mc) {
	$name = "$($lines[$line].artifact)-$version+mc$mc.jar"
	$dist = Join-Path $root (Join-Path "dist" $name)
	$project = if ($lines[$line].project -eq ".") { $root } else { Join-Path $root $lines[$line].project }
	$built = Join-Path $project "build/libs/$name"

	if (-not (Test-Path $built)) {
		if (-not (Test-Path $dist)) {
			throw "还没有构建产物:$built(dist\ 里也没有 $name)`n先跑 .\gradlew build;要记录已发布的那份,请把 jar 放进 dist\"
		}

		return @{ path = $dist; source = "dist" }
	}

	if (-not (Test-Path $dist)) { return @{ path = $built; source = "build\libs" } }

	$distSize = (Get-Item $dist).Length
	$builtSize = (Get-Item $built).Length
	$distHash = (Get-FileHash $dist -Algorithm SHA256).Hash
	$builtHash = (Get-FileHash $built -Algorithm SHA256).Hash
	if ($distSize -ne $builtSize -or $distHash -ne $builtHash) {
		$conflict = "dist\ 与 build\libs 里的 $name 不是同一份产物,不能猜哪一份:`n" +
			"  $dist`n    $distSize 字节  $distHash`n" +
			"  $built`n    $builtSize 字节  $builtHash"
		if (-not $DryRun) {
			throw ($conflict + "`n发布页要写的是已经发出去的那份(dist\);确实要改用新构建,请先把新 jar 放进 dist\ 并同步发布清单。")
		}

		Write-Host "  警告:$conflict"
		Write-Host "  真实运行(去掉 -DryRun)会就此停下、不写任何文档;本次 DryRun 按已发布的 dist\ 那份计算。"
	}

	return @{ path = $dist; source = "dist" }
}

# ---------------------------------------------------------------- support table mode

# -CheckSupport: which OptiFine build a Minecraft version needs is written down in three places - the table in
# README.md / README_CN.md, the "需求 OptiFine `<文件名>`" line of release\notes\mc<MC>.md, and the table the mod
# itself carries (src\main\java\kynarain\cn\optifabric\mod\OptifineSupport.java, which is what the prompt screen
# names and what the downloader saves as). A row that disagrees with a note sends users to the wrong jar, and
# neither README is allowed to drift from the other. All of it is local text, so this check never needs the
# network - it is a document consistency check, not a download check.
if ($CheckSupport) {
	$readmes = @("README.md", "README_CN.md")
	$supportPath = "src/main/java/kynarain/cn/optifabric/mod/OptifineSupport.java"
	$maps = [ordered]@{}

	foreach ($file in $readmes) {
		$map = @{}
		foreach ($rowText in ((Read-ReleaseFile $file) -split "`r?`n")) {
			# Only the "OptiFabric version | Minecraft version | OptiFine build" table matches: in the
			# compatibility table above it the second column holds a jar name, not a bare version, and no other
			# table in these files has a bare version followed by a .jar in the next column.
			$row = [regex]::Match($rowText, '^\|\s*[^|]*\|\s*(\d[\d.]*)\s*\|\s*`?([^`|\s]+\.jar)`?\s*\|')
			if ($row.Success) { $map[$row.Groups[1].Value] = $row.Groups[2].Value }
		}
		if ($map.Count -eq 0) { throw "$file 里找不到「OptiFabric 版本 | Minecraft 版本 | OptiFine 构建」这张表" }
		$maps[$file] = $map
	}

	# The mod's own table: new Build("1.21.1", "HD_U_J1", "", "OptiFine_1.21.1_HD_U_J1.jar", null)
	$support = @{}
	foreach ($entry in [regex]::Matches((Read-ReleaseFile $supportPath),
			'new Build\(\s*"([\d.]+)"\s*,\s*"([^"]*)"\s*,\s*"([^"]*)"\s*,\s*"([^"]+\.jar)"')) {
		$support[$entry.Groups[1].Value] = $entry.Groups[4].Value
	}
	if ($support.Count -eq 0) { throw "$supportPath 里读不到支持表(OptifineSupport 的 new Build(...) 写法变了?)" }
	$maps[$supportPath] = $support

	$notes = @{}
	foreach ($note in (Get-ChildItem (Join-Path $root "release/notes") -Filter "mc*.md" | Sort-Object Name)) {
		$mcVersion = $note.BaseName -replace '^mc', ''
		$match = [regex]::Match((Read-ReleaseFile ("release/notes/" + $note.Name)),
			'需求[^\r\n]*OptiFine[^\r\n]*`([^`]+\.jar)`')
		if (-not $match.Success) { throw "release\notes\$($note.Name) 里找不到「需求 OptiFine ``<文件名>``」这一行" }
		$notes[$mcVersion] = $match.Groups[1].Value
	}

	# 1.21 sorts before 1.21.1 by padding each part, which [version] would do too - but not for "1.21" alone.
	# The loop variable must not be $mc either: that is the -Mc parameter (names are case-insensitive).
	$sortKey = { param($part) (($part -split '\.') | ForEach-Object { $_.PadLeft(4, '0') }) -join '.' }
	$problems = @()

	foreach ($mcVersion in ($notes.Keys | Sort-Object -Property $sortKey)) {
		foreach ($name in $maps.Keys) {
			if (-not $maps[$name].ContainsKey($mcVersion)) {
				$problems += "$name 里缺少 Minecraft $mcVersion 一行"
			} elseif ($maps[$name][$mcVersion] -ne $notes[$mcVersion]) {
				$problems += "$name 里 $mcVersion 写的是 $($maps[$name][$mcVersion]),release\notes\mc$mcVersion.md 要求 $($notes[$mcVersion])"
			}
		}
	}

	foreach ($name in $maps.Keys) {
		foreach ($mcVersion in $maps[$name].Keys) {
			if (-not $notes.ContainsKey($mcVersion)) { $problems += "$name 里有 Minecraft $mcVersion,但 release\notes\mc$mcVersion.md 不存在" }
		}
	}

	Write-Host "支持表核对:$($notes.Count) 个 Minecraft 版本 x $($maps.Count) 处写法(离线)"
	foreach ($name in $maps.Keys) { Write-Host ("  {0,-64} {1} 行" -f $name, $maps[$name].Count) }

	if ($problems.Count -gt 0) {
		Write-Host ""
		foreach ($problem in $problems) { Write-Host "  $problem" }
		throw "支持表不一致:$($problems.Count) 处(README 两张表、release\notes\、OptifineSupport.java 必须同名)"
	}

	Write-Host ""
	Write-Host "支持表一致:三种写法都指向同一批 OptiFine 构建。"
	return
}

# ---------------------------------------------------------------- provenance mode

# -RecordProvenance: "这一版的两条产物各由哪次提交构建"这句话既读不出 jar、也不由构建过程产生,所以脚本别的地方都带不上它:
# -Kind / -Set 的文档替换只认 "<版本>+mc" 这一串,而这句话里没有,于是永远走不到它。结果就是手写错了两次(2.2.9 与 2.2.10
# 发布时都还写着 2.2.7 的提交)。这个模式拿两条产物真正的构建提交,把十份 release\notes\mc*.md 里的这句话重写一遍。
if ($RecordProvenance) {
	if (-not $Head -or -not $FullHead) {
		throw "-RecordProvenance 需要 -Head(默认产物那次构建的提交)与 -FullHead(-full 产物那次构建的提交)"
	}

	$version = if ($ProvenanceVersion) { (Parse-Version $ProvenanceVersion).text } else { Get-CurrentVersion "1.21.x" }

	# 写进发布页之前先证一遍:这一版的两条产物必须构建自 mod_version_base=<版本> 的那棵树,所以逐个提交读它自己的
	# gradle.properties。发布页发出去就改不回来,写错一次就是把"这一版构建自哪里"永久写错。
	function Resolve-ProvenanceCommit([string]$sha, [string]$label) {
		$shaLines = @(& git -C $root rev-parse --verify --quiet "$sha^{commit}")
		if ($LASTEXITCODE -ne 0 -or $shaLines.Count -eq 0 -or -not $shaLines[0]) {
			throw "${label}的提交 '$sha' 在仓库里不是一次提交(git rev-parse <sha>^{commit} 失败)"
		}
		$commit = ([string]$shaLines[0]).Trim()

		$propertyLines = @(& git -C $root show "${commit}:gradle.properties" 2>$null)
		if ($LASTEXITCODE -ne 0 -or $propertyLines.Count -eq 0) { throw "${label}的提交 $commit 里读不到 gradle.properties" }

		$base = [regex]::Match(($propertyLines -join "`n"), '(?m)^mod_version_base=(.+)$')
		if (-not $base.Success) { throw "${label}的提交 $commit 的 gradle.properties 里没有 mod_version_base" }

		$declared = $base.Groups[1].Value.Trim()
		if ($declared -ne $version) {
			throw ("${label}的提交 $commit 的 gradle.properties 写的是 mod_version_base=$declared,不是 $version;" +
				"这一次构建不可能是它(提交给错了?要记的是别的版本,那就加 -ProvenanceVersion)")
		}

		return $commit
	}

	function Get-ShortSha([string]$commit) {
		$shaLines = @(& git -C $root rev-parse --short=7 $commit)
		if ($shaLines.Count -eq 0 -or -not $shaLines[0]) { throw "算不出提交 $commit 的短 SHA" }

		return ([string]$shaLines[0]).Trim()
	}

	$headCommit = Resolve-ProvenanceCommit $Head "默认产物"
	$fullCommit = Resolve-ProvenanceCommit $FullHead "-full 产物"
	$headShort = Get-ShortSha $headCommit
	$fullShort = Get-ShortSha $fullCommit

	# 这句话的形状是唯一的:主槽("<版本> 的默认产物构建自 `<sha>`," + 换行 + "`-full` 产物构建自 `<sha>`"),紧接着副槽
	# ("(<更早的版本> 分别是 `<sha>` 与 `<sha>`;…")。提交按十六进制串收,长短由 git 自己决定。整句都必须落在模式里
	# (包括结尾那句"没有单独的 tag…release)。"):模式只吃掉半句、替换却给整句,就会把尾巴一遍遍重复写进文件。
	$provenancePattern = '(?<pver>\d+\.\d+\.\d+) 的默认产物构建自 `(?<pdef>[0-9a-f]{7,40})`,' + "`r`n" +
		'`-full` 产物构建自 `(?<pfull>[0-9a-f]{7,40})`\((?<sver>\d+\.\d+\.\d+) 分别是 `(?<sdef>[0-9a-f]{7,40})` 与 `(?<sfull>[0-9a-f]{7,40})`' +
		[regex]::Escape(';`-full` 没有单独的 tag,与默认产物共用同一个 release)。')

	# 反引号在双引号串里要转义,拼句子时用 [char]96 更不容易看错。
	$tick = [char]96
	function Format-ProvenanceSentence([string]$pv, [string]$pd, [string]$pf, [string]$sv, [string]$sd, [string]$sf) {
		return ("$pv 的默认产物构建自 ${tick}$pd${tick}," + "`r`n" +
			"${tick}-full${tick} 产物构建自 ${tick}$pf${tick}($sv 分别是 ${tick}$sd${tick} 与 ${tick}$sf${tick};" +
			"${tick}-full${tick} 没有单独的 tag,与默认产物共用同一个 release)。")
	}

	# 句子与模式必须严丝合缝:这里用一组假值自证一次。改了一处忘了另一处时,当场就在这里停下,而不是把半句
	# 重复写进十份发布说明(那正是手写这条路已经错过两次的地方)。
	$sample = Format-ProvenanceSentence "1.2.3" "aaaaaaa" "bbbbbbb" "1.2.2" "ccccccc" "ddddddd"
	if (-not [regex]::IsMatch($sample, '^' + $provenancePattern + '$')) {
		throw "构建来源那句与匹配模式对不上(Format-ProvenanceSentence 与 `$provenancePattern 没有同步);停下,不写任何文件"
	}

	$notes = @(Get-ChildItem (Join-Path $root "release/notes") -Filter "mc*.md" | Sort-Object Name)
	if ($notes.Count -eq 0) { throw "release\notes 里没有 mc*.md" }

	Write-Host "构建来源:$version  默认产物 $headShort  -full $fullShort"
	$files = 0
	$already = 0

	# 先只读、只校验:十份文件全都合格才动第一份。"改了一半"的发布说明比根本没改更难收拾。
	$pending = @()
	foreach ($note in $notes) {
		$relative = "release/notes/" + $note.Name
		$text = Read-ReleaseFile $relative
		$found = [regex]::Matches($text, $provenancePattern)

		if ($found.Count -eq 0) {
			throw ("$relative 里找不到那句构建来源;这一句由 -RecordProvenance 生成、不允许手写," +
				"先按原样放回一句(形状见 release\MANUAL_RELEASE.md 的发布期陷阱清单)")
		}
		if ($found.Count -gt 1) {
			throw "$relative 里那句构建来源出现了 $($found.Count) 次:同一次运行里同一处替换不能做两遍,先把它合并成一句"
		}

		$hit = $found[0]
		$currentVersion = $hit.Groups['pver'].Value
		$previousVersion = $hit.Groups['sver'].Value

		if ($currentVersion -eq $version) {
			# 主槽就是这一版:只换两个提交,副槽原样保留(所以重复跑同一对提交是幂等的)。
			$newCurrentVersion = $currentVersion
			$newCurrentDefault = $headShort
			$newCurrentFull = $fullShort
			$newPreviousVersion = $previousVersion
			$newPreviousDefault = $hit.Groups['sdef'].Value
			$newPreviousFull = $hit.Groups['sfull'].Value
		}
		elseif ($previousVersion -eq $version) {
			# 副槽是这一版(给上一版补记):同样只换两个提交。
			$newCurrentVersion = $currentVersion
			$newCurrentDefault = $hit.Groups['pdef'].Value
			$newCurrentFull = $hit.Groups['pfull'].Value
			$newPreviousVersion = $previousVersion
			$newPreviousDefault = $headShort
			$newPreviousFull = $fullShort
		}
		else {
			# 两个槽都不是这一版:整句往前挪一格(旧的主槽变成副槽)。挪格会挤掉原来的副槽,所以先证自己站在对的版本上:
			# 标题里的版本必须是这一版,而且必须比句子里记的那个版本高。
			$title = [regex]::Match($text, '(?m)^#\s+OptiFabric\s+(\d+\.\d+\.\d+)\+mc')
			if (-not $title.Success) { throw "$relative 的标题不是「# OptiFabric <版本>+mc<MC>」的形状,认不出这份文件是哪个版本" }
			if ($title.Groups[1].Value -ne $version) {
				throw ("$relative 的标题写的是 $($title.Groups[1].Value),而这次要记的是 $version:句子里既没有 $version 这个槽," +
					"标题也不是它 —— 先核对版本号(要补记更早的版本才用 -ProvenanceVersion)")
			}
			if ((Compare-Version (Parse-Version $version) (Parse-Version $currentVersion)) -le 0) {
				throw "$relative 里记的已经是 $currentVersion,不比 $version 低:不能把它挤到副槽去"
			}

			$newCurrentVersion = $version
			$newCurrentDefault = $headShort
			$newCurrentFull = $fullShort
			$newPreviousVersion = $currentVersion
			$newPreviousDefault = $hit.Groups['pdef'].Value
			$newPreviousFull = $hit.Groups['pfull'].Value
		}

		$sentence = Format-ProvenanceSentence $newCurrentVersion $newCurrentDefault $newCurrentFull $newPreviousVersion $newPreviousDefault $newPreviousFull
		$updated = [regex]::Replace($text, $provenancePattern, { param($x) $sentence })

		$pending += @{
			relative           = $relative
			updated            = $updated
			changed            = ($updated -ne $text)
			currentVersion     = $currentVersion
			previousVersion    = $previousVersion
			newCurrentVersion  = $newCurrentVersion
			newCurrentDefault  = $newCurrentDefault
			newCurrentFull     = $newCurrentFull
			newPreviousVersion = $newPreviousVersion
			newPreviousDefault = $newPreviousDefault
			newPreviousFull    = $newPreviousFull
		}
	}

	foreach ($item in $pending) {
		if (-not $item.changed) {
			$already++
			Write-Host ("  {0,-30} 已是该值({1} / {2}),未改动" -f $item.relative, $item.newCurrentVersion, $item.newPreviousVersion)
			continue
		}

		if ($DryRun) {
			$files++
			Write-Host ("  {0,-30} [DryRun] 会改成 {1}={2}/{3}、{4}={5}/{6}" -f $item.relative, `
				$item.newCurrentVersion, $item.newCurrentDefault, $item.newCurrentFull, $item.newPreviousVersion, $item.newPreviousDefault, $item.newPreviousFull)
			continue
		}

		# 这一句以外的字节都不该动,所以写回前后比一次形状:BOM、换行风格、C0 控制字节数。
		$before = Get-FileShape (Join-Path $root $item.relative)
		Write-ReleaseFile $item.relative $item.updated
		$after = Get-FileShape (Join-Path $root $item.relative)
		if ($before.bom -ne $after.bom -or $before.crlf -ne $after.crlf -or $before.bareLf -ne $after.bareLf) {
			throw ("$($item.relative) 写回后形状变了:BOM $($before.bom)->$($after.bom)、CRLF $($before.crlf)->$($after.crlf)、" +
				"裸 LF $($before.bareLf)->$($after.bareLf)")
		}
		if ($after.control -ne 0) { throw "$($item.relative) 写回后有 $($after.control) 个 C0 控制字节(CR/LF/TAB 之外)" }

		$files++
		Write-Host ("  {0,-30} 主槽 {1} -> {2}、副槽 {3} -> {4}" -f $item.relative, $item.currentVersion, $item.newCurrentVersion, $item.previousVersion, $item.newPreviousVersion)
	}

	Write-Host ""
	if ($DryRun) { Write-Host "[DryRun] 会改写 $files 份 release\notes\mc*.md;$already 份已经是该值" }
	else { Write-Host "已改写 $files 份 release\notes\mc*.md;$already 份已经是该值;请 git diff release\notes\ 复核" }
	return
}

# ---------------------------------------------------------------- listing mode

# -Part is meant to be consumed by other scripts: with a single line in this repository it defaults to it,
# so plain `.\release\version.ps1 -Part` prints that line's version and nothing else.
if (-not $Line -and $Part) { $Line = "1.21.x" }

if (-not $Line -and -not $RecordDigest) {
	foreach ($name in @("1.21.x")) {
		$current = Get-CurrentVersion $name
		$parsed = Parse-Version $current
		Write-Host ("{0,-8} 当前 {1,-10} 产物 {2}-{1}+mc{3}.jar" -f $name, $current, $lines[$name].artifact, $lines[$name].mc)
		Write-Host ("          按 SemVer 递增:patch -> {0}   minor -> {1}   major -> {2}" -f `
			(Next-Version $current "patch"), (Next-Version $current "minor"), (Next-Version $current "major"))

		$overrides = Get-McVersionMap
		foreach ($mc in Get-LineVersions $name) {
			if ($overrides.ContainsKey($mc) -and $overrides[$mc] -ne $current) {
				Write-Host ("          {0,-9} 单独用 {1,-8} 产物 {2}-{1}+mc{0}.jar" -f $mc, $overrides[$mc], $lines[$name].artifact)
			}
		}
	}
	Write-Host ""
	Write-Host "用法见 docs\VERSIONING.md;要真改就加 -Line 与 -Kind(或 -Set);只改一个 MC 版本加 -Mc。"
	return
}

if (-not $Line) { throw "-RecordDigest 需要同时给 -Line" }

# -Mc picks one jar of the line; everything below then reads and writes that jar's version alone.
if ($Mc) {
	$allowed = Get-LineVersions $Line
	if ($allowed -notcontains $Mc) {
		throw "$Line 线里没有 $Mc 这个版本(可选:" + ($allowed -join ", ") + ")"
	}
	if ($allowed.Count -eq 1) {
		throw "$Line 线只有一个 MC 版本($Mc):直接用 -Kind / -Set 给整条线升版"
	}
}

# ---------------------------------------------------------------- digest mode

if ($RecordDigest) {
	$current = if ($Mc) { Resolve-Version $Line $Mc } else { Get-CurrentVersion $Line }
	$mc = if ($Mc) { $Mc } else { $lines[$Line].mc }
	$jarName = "$($lines[$Line].artifact)-$current+mc$mc.jar"
	$jar = Get-DigestJar $Line $current $mc

	$size = (Get-Item $jar.path).Length
	$hash = (Get-FileHash $jar.path -Algorithm SHA256).Hash
	$sizeText = "$size 字节"
	Write-Host "产物 $jarName"
	Write-Host "  $sizeText"
	Write-Host "  SHA-256 $hash"
	Write-Host "  取自 $($jar.source)\$jarName"

	# Only the paragraphs that mention *this* artifact+version are rewritten. A blanket search for
	# "<n> 字节" would also hit the 1.1.0-era figures kept in the changelog and in release/notes/, which belong
	# to earlier releases - one jar per Minecraft version means those numbers are all different. With -Mc
	# the pattern carries the Minecraft version as well, so the other nine jars stay untouched too.
	# Anchored, because one jar's version string can be a prefix of another's: 2.0.0+mc1.21.1 sits inside
	# 2.0.0+mc1.21.11, and without the lookahead the 1.21.1 run rewrote 1.21.11's paragraphs in the same file.
	$versionPattern = [regex]::Escape("$current+mc$mc") + '(?!\.?\d)'
	# The unit is kept as written: the Chinese documents say "字节", docs\RELEASE_NOTES.md says "bytes".
	$sizePattern = '([\d,]{4,})(\s*(?:字节|bytes))'
	$hashPattern = '([0-9A-Fa-f]{64})'

	$touched = 0
	$files = 0

	foreach ($file in $documentFiles) {
		$full = Join-Path $root $file
		if (-not (Test-Path $full)) { continue }

		$bytes = [System.IO.File]::ReadAllBytes($full)
		$bom = ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF)
		$text = [System.Text.Encoding]::UTF8.GetString($bytes)
		if ($bom) { $text = $text.Substring(1) }

		$paragraphs = [regex]::Split($text, '(\r?\n[ \t]*\r?\n)')
		$changes = 0
		$previousMatched = $false

		for ($i = 0; $i -lt $paragraphs.Count; $i++) {
			$paragraph = $paragraphs[$i]

			# The split keeps the blank-line separators as elements of their own; they must not clear the
			# "previous paragraph named the artifact" flag, or a digest written under a blank line gets missed.
			if ($paragraph -match '^(\r?\n[ \t]*\r?\n)$') { continue }

			# A paragraph about a *different* version of this artifact is history (an earlier release's figures),
			# not the current state: leave it alone. The changelog keeps exactly such a section. The lookahead
			# matters: without it this also matches this release's own name, and then nothing is ever updated.
			$otherVersion = [regex]::IsMatch($paragraph,
				[regex]::Escape($lines[$Line].artifact) + '-(?!' + [regex]::Escape($current) + ')\d+\.\d+\.\d+[^\s`]*\+mc')
			$mentionsCurrent = [regex]::IsMatch($paragraph, [regex]::Escape("$($lines[$Line].artifact)-$current+mc$mc") + '(?!\.?\d)') -or
				$paragraph -match $versionPattern
			$matched = $mentionsCurrent -and -not $otherVersion

			# The digest is often written on its own line under the file name, separated by a blank line; treat such
			# a paragraph as the continuation of the one that named the artifact.
			if (-not $matched -and $paragraph -match '^[`\s]*SHA-256[:`\s]*[0-9A-Fa-f]{64}') { $matched = $previousMatched }

			if (-not $matched) { $previousMatched = $false; continue }
			$previousMatched = $true

			$before = $paragraph
			# A single paragraph can name several jars at once - the release checklist's table has one row per
			# Minecraft version - and rewriting every figure in it gives all ten rows the same size and hash
			# (whichever version was processed last). When more than one release is named, only the lines that
			# name this one are rewritten.
			$versionsHere = @([regex]::Matches($before, '\+mc(1\.21(?:\.\d+)?)') | ForEach-Object { $_.Groups[1].Value } | Sort-Object -Unique)
			if ($versionsHere.Count -gt 1) {
				# The loop variable must not be called $line: PowerShell variable names are case-insensitive, so
				# that is the same variable as this script's -Line parameter and assigning to it throws.
				$perLine = foreach ($row in ($before -split "`r`n")) {
					if ($row -notmatch [regex]::Escape("$current+mc$mc") + '(?!\.?\d)') { $row; continue }

					$fixed = [regex]::Replace($row, $sizePattern, { param($m) "$size" + $m.Groups[2].Value })
					$fixed = [regex]::Replace($fixed, '\|\s*[\d,]{4,}\s*\|', { param($m) "| $size |" })

					[regex]::Replace($fixed, $hashPattern, $hash)
				}
				$after = ($perLine -join "`r`n")
			}
			else {
				$after = [regex]::Replace($before, $sizePattern, { param($m) "$size" + $m.Groups[2].Value })
				# In a markdown table the size is a bare cell next to the digest, with no unit after it.
				$after = [regex]::Replace($after, '\|\s*[\d,]{4,}\s*\|', { param($m) "| $size |" })
				$after = [regex]::Replace($after, $hashPattern, $hash)
			}
			if ($after -ne $before) { $paragraphs[$i] = $after; $changes++ }
		}

		if ($changes -gt 0) {
			$files++
			$touched += $changes
			if (-not $DryRun) {
				[System.IO.File]::WriteAllText($full, ($paragraphs -join ""), (New-Object System.Text.UTF8Encoding($bom)))
			}
			Write-Host "  $file  $changes 段"
		}
	}

	Write-Host ""
	if ($DryRun) { Write-Host "[DryRun] 会更新 $files 个文件、$touched 段" }
	else { Write-Host "已更新 $files 个文件、$touched 段;请 git diff 复核(尤其 CHANGELOG 与 release\notes\)" }
	return
}

# ---------------------------------------------------------------- version mode

$current = if ($Mc) { Resolve-Version $Line $Mc } else { Get-CurrentVersion $Line }

if ($Part) { Write-Output $current; return }

if ($Set -and $Kind) { throw "-Set 与 -Kind 只能给一个" }
if (-not $Set -and -not $Kind) { throw "给 -Kind major|minor|patch,或用 -Set 指定版本号" }

$target = if ($Set) { (Parse-Version $Set).text } else { Next-Version $current $Kind }
$old = Parse-Version $current
$new = Parse-Version $target

$order = Compare-Version $new $old
if ($order -eq 0) { throw "新版本号与当前相同($current);SemVer §3:已发行版本的内容不能改,只能发新版本" }
if ($order -lt 0 -and -not $Force) { throw "新版本号 $target 低于当前版本 $current(SemVer §11);确实要降就加 -Force" }
if ($new.build) { Write-Host "提示:版本号里带了编译信息(+$($new.build));按 §10 它不参与优先级比较。" }

if ($Mc) {
	Write-Host "版本号:$current -> $target  ($Line 线,$Mc 这一个版本,仓库根项目;其余版本不动)"
} else {
	Write-Host "版本号:$current -> $target  ($Line 线,仓库根项目)"
}
if ($DryRun) { Write-Host "[DryRun] 不写任何文件" }

# 1. the project's own version (only for a whole-line bump: with -Mc the base is what the other versions use)
if (-not $Mc) {
	$properties = Join-Path $root (Join-Path $lines[$Line].project "gradle.properties")
	$count = Update-File (Join-Path $lines[$Line].project "gradle.properties") @{ "mod_version_base=$current" = "mod_version_base=$target" }
	Write-Host ("  {0,-45} {1} 处" -f "gradle.properties", $count)
}

# 2. the release script's version map: one entry per Minecraft version that has a version of its own, plus
#    the default for the versions that have none.
if ($Mc) {
	$count = Set-McVersion $Mc $target
	Write-Host ("  {0,-45} {1} 处" -f "release/publish.ps1  (`$modVersions[""$Mc""])", $count)
} else {
	$publishPairs = @{}
	$publishPairs['$defaultModVersion = "' + $current + '"'] = '$defaultModVersion = "' + $target + '"'
	$count = Update-File "release/publish.ps1" $publishPairs
	Write-Host ("  {0,-45} {1} 处" -f "release/publish.ps1", $count)
	if ($count -eq 0) { Write-Host "    (没找到 publish.ps1 里的默认版本号映射,请手动确认)" }

	# 逐 MC 版本的例外值也要跟着走:整线升版时,凡是值**等于旧基数**的条目都必须一起改写成新版本,
	# 否则 release\publish.ps1 会继续拿旧版本号去找 jar 与 tag,那些产物会全部对不上。值不等于旧基数的
	# 是有意的逐版本例外值(见 docs\VERSIONING.md 第五节),不能替用户决定,只打印警告、留原样。
	$mcOverrides = Get-McVersionMap
	foreach ($mcKey in @($mcOverrides.Keys | Sort-Object)) {
		if ($mcOverrides[$mcKey] -eq $current) {
			$count = Set-McVersion $mcKey $target
			Write-Host ("  {0,-45} {1} 处" -f "release/publish.ps1  (`$modVersions[""$mcKey""])", $count)
		}
		else {
			Write-Host "  警告:release\publish.ps1 里 $mcKey 单独用 $($mcOverrides[$mcKey]),不等于整线旧版本 $current;本次不改它,要改就用 -Mc $mcKey 单独处理"
		}
	}
}

# 3. every "<version>+mc" reference in the documents. Matching on the bare version covers both the artifact
#    names (OptiFabric-<version>+mc<mc>.jar) and the version fields of the release checklists. With -Mc
#    the string carries the Minecraft version too, so no other jar's references (or history) can be caught. The
#    lookbehind keeps names where this version sits inside a *different*, frozen artifact (see Update-FilePattern).
$from = if ($Mc) { "$current+mc$Mc" } else { "$current+mc" }
$to = if ($Mc) { "$target+mc$Mc" } else { "$target+mc" }
$pattern = '(?<!archive-' + [regex]::Escape($lines[$Line].artifact) + '-)' + [regex]::Escape($from)
$total = 0
foreach ($file in $documentFiles) {
	$count = Update-FilePattern $file $pattern $to
	if ($count -gt 0) { Write-Host ("  {0,-45} {1} 处" -f $file, $count) }
	$total += $count
}
Write-Host "  文档合计 $total 处"

$jar = Get-BuiltJar $Line $target $(if ($Mc) { $Mc } else { $lines[$Line].mc })
Write-Host ""
Write-Host "接下来:"
if ($Mc) {
	Write-Host "  1. .\gradlew build ""-Pmc=$Mc"" ""-Pmod_version_base=$target"" --offline"
} else {
	Write-Host "  1. .\gradlew build --offline"
}
Write-Host "     -> 产物名应是 $($jar.name)"
if ($Mc) {
	Write-Host "  2. .\release\version.ps1 -Line $Line -Mc $Mc -RecordDigest"
} else {
	Write-Host "  2. .\release\version.ps1 -Line $Line -RecordDigest"
}
Write-Host "     -> 把尺寸与 SHA-256 写回文档(README、CHANGELOG、release/notes/、release/MANUAL_RELEASE.md)"
Write-Host "  3. 按 release\MANUAL_RELEASE*.md 发布;tag 名与产物名成对写,别只写版本号"
Write-Host "  4. 已发布过的版本号不得复用(§3);内容要改就发新版本"
