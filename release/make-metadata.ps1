# release/make-metadata.ps1
# Regenerates release\tmp\modrinth-1.20.6.json and release\tmp\curseforge-1.20.6.json from the top section of
# CHANGELOG.md, in the same shape the 1.1.0-1.1.2 drafts were written by hand (4-space indent, `<`/`>` escaped
# as \u003c / \u003e, Chinese left literal, UTF-8 without BOM, CRLF).
#
# This line has no release/version.ps1 (see docs/PUBLISHING.md), so this is the only generator it has:
#
#   powershell -NoProfile -ExecutionPolicy Bypass -File release\make-metadata.ps1
#   powershell -NoProfile -ExecutionPolicy Bypass -File release\make-metadata.ps1 -Version 1.1.4
param(
	[string]$Version = "",
	[string]$McVersion = "1.20.6"
)

$ErrorActionPreference = "Stop"
$root = Split-Path $PSScriptRoot -Parent
$changelogPath = Join-Path $root "CHANGELOG.md"
$tmp = Join-Path $PSScriptRoot "tmp"

if ($Version -eq "") {
	# The current version is the first "## <version>+mc<...>" heading of the changelog.
	$match = [regex]::Match([System.IO.File]::ReadAllText($changelogPath), '(?m)^## ([0-9]+(?:\.[0-9]+)*)')
	if (-not $match.Success) { throw "no '## <version>' heading in $changelogPath" }
	$Version = $match.Groups[1].Value
}

$full = "$Version+mc$McVersion"
$text = [System.IO.File]::ReadAllText($changelogPath, (New-Object System.Text.UTF8Encoding($false)))
$start = $text.IndexOf("## $full")
if ($start -lt 0) { throw "CHANGELOG.md has no section '## $full'" }
$next = $text.IndexOf("`r`n## ", $start + 5)
if ($next -lt 0) { $next = $text.Length }

$section = $text.Substring($start, $next - $start).Trim()
[string]$cr = [char]13
[string]$lf = [char]10
[string]$tab = [char]9
$escaped = $section.Replace('\', '\\').Replace('"', '\"').Replace($cr, '\r').Replace($lf, '\n').
	Replace($tab, '\t').Replace('<', '\u003c').Replace('>', '\u003e').Replace('&', '\u0026')
if ($escaped.Contains($cr) -or $escaped.Contains($lf)) { throw "escaping left a raw line break behind" }

$newline = $cr + $lf
# Every element below is one parenthesised -f expression on purpose: inside @( ), a line like
# 'a' + $x + 'b' is parsed as several statements (and therefore several elements), which silently inserts
# line breaks into the JSON. See the CRLF-vs-'+' note in this file's history.
$modrinth = @(
	'{',
	('    "name":  "OptiFabric-{0}",' -f $full),
	('    "version_number":  "{0}",' -f $full),
	('    "changelog":  "{0}",' -f $escaped),
	'    "dependencies":  [',
	'',
	'                     ],',
	'    "game_versions":  [',
	('                          "{0}"' -f $McVersion),
	'                      ],',
	'    "version_type":  "release",',
	'    "loaders":  [',
	'                    "fabric"',
	'                ],',
	'    "client_side":  "required",',
	'    "server_side":  "unsupported",',
	'    "featured":  false,',
	'    "project_id":  null,',
	'    "file_parts":  [',
	'                       "file"',
	'                   ],',
	'    "primary_file":  "file"',
	'}') -join $newline

$curseforge = @(
	'{',
	('    "changelog":  "{0}",' -f $escaped),
	'    "changelogType":  "markdown",',
	('    "displayName":  "OptiFabric-{0}",' -f $full),
	'    "releaseType":  "release",',
	'    "gameVersions":  [',
	('                         "{0}",' -f $McVersion),
	'                         "Fabric"',
	'                     ]',
	'}') -join $newline

New-Item -ItemType Directory -Force $tmp | Out-Null
$encoding = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText((Join-Path $tmp "modrinth-$McVersion.json"), $modrinth, $encoding)
[System.IO.File]::WriteAllText((Join-Path $tmp "curseforge-$McVersion.json"), $curseforge, $encoding)

foreach ($name in @("modrinth-$McVersion.json", "curseforge-$McVersion.json")) {
	$path = Join-Path $tmp $name
	$bytes = [System.IO.File]::ReadAllBytes($path)
	$bom = "no"
	if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) { $bom = "yes" }
	$body = [System.IO.File]::ReadAllText($path)
	$parsed = $body | ConvertFrom-Json
	Write-Output ("{0,-26} {1,6} B  BOM={2}  CRLF={3}  parses=OK  version={4}" -f $name, $bytes.Length, $bom,
		([regex]::Matches($body, [regex]::Escape($cr + $lf))).Count, $parsed.version_number + $parsed.displayName)
}
Write-Output "wrote $tmp\$full drafts"
