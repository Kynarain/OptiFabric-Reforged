# Documentation gate: relative links, version numbers, encoding.
# Run from the repository root: powershell -NoProfile -ExecutionPolicy Bypass -File tools\check-docs.ps1
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$problems = New-Object System.Collections.Generic.List[string]

function Rel([string]$path) { return ($path -replace [regex]::Escape($root + '\'), '') }

function Files([string]$pattern) {
	return @(Get-ChildItem -LiteralPath $root -Recurse -File -Filter $pattern -ErrorAction SilentlyContinue |
		Where-Object { $_.FullName -notmatch '\\build\\|\\.git\\|\\.gradle\\' })
}

# 1. relative links must resolve
$broken = 0
foreach ($f in (Files '*.md')) {
	$text = [IO.File]::ReadAllText($f.FullName, [Text.Encoding]::UTF8)
	foreach ($m in [regex]::Matches($text, '\]\(\s*(?<target>[^)\s]+)\s*\)')) {
		$target = $m.Groups['target'].Value
		if ($target -match '^(https?:|mailto:|#)') { continue }
		if ($target -match '^\.\./\.\./tree/') { continue }   # GitHub-only links out of the file tree
		$target = ($target -split '#')[0]
		if ([string]::IsNullOrWhiteSpace($target)) { continue }
		$resolved = [IO.Path]::GetFullPath((Join-Path $f.DirectoryName $target))
		if (-not (Test-Path -LiteralPath $resolved)) {
			$problems.Add('broken link: ' + (Rel $f.FullName) + ' -> ' + $target)
			$broken++
		}
	}
}
Write-Host ("  relative links      : " + $broken + " broken")

# 2. the readme's "current version" must match the build's version
$versionProblems = 0
$pairs = @(
	@{ Props = 'gradle.properties'; Key = 'mod_version_base'; Readmes = @('README.md', 'README_CN.md') },
	@{ Props = 'gradle.properties'; Key = 'mod_version'; Readmes = @('README.md') }
)
foreach ($pair in $pairs) {
	$propsPath = Join-Path $root $pair.Props
	if (-not (Test-Path -LiteralPath $propsPath)) { continue }
	$props = [IO.File]::ReadAllText($propsPath, [Text.Encoding]::UTF8)
	$m = [regex]::Match($props, '(?m)^\s*' + $pair.Key + '\s*=\s*([0-9][0-9A-Za-z.\-]*)')
	if (-not $m.Success) { continue }
	$version = $m.Groups[1].Value
	foreach ($readme in $pair.Readmes) {
		$p = Join-Path $root $readme
		if (-not (Test-Path -LiteralPath $p)) { continue }
		# the label, then any of the quoting styles a version may be written in
		foreach ($line in [IO.File]::ReadAllLines($p, [Text.Encoding]::UTF8)) {
			if ($line -notmatch '(?i)(current version|当前版本|本分支版本|mod_version)') { continue }
			foreach ($c in [regex]::Matches($line, '`([0-9][0-9A-Za-z.\-]*)`|\*\*([0-9][0-9A-Za-z.\-]*)\*\*')) {
				$claimed = $c.Groups[1].Value
				if ([string]::IsNullOrWhiteSpace($claimed)) { $claimed = $c.Groups[2].Value }
				if ($claimed -match '^\d+\.\d+') {
					if ($claimed -ne $version) {
						$problems.Add('stale version: ' + $readme + ' says ' + $claimed + ' but ' + $pair.Key + ' is ' + $version)
						$versionProblems++
					}
					break
				}
			}
		}
	}
}
Write-Host ("  version numbers     : " + $versionProblems + " stale")

# 3. encoding.
#    * the bytes must be valid UTF-8, with no replacement character;
#    * a PowerShell script with non-ASCII must carry a BOM (5.1 reads it as ANSI otherwise);
#    * the file must not be UTF-8 that was once read as GBK and saved again. That accident leaves characters which are
#      themselves legal CJK, so a validity check cannot see it. The test is the inverse of the accident: real Chinese
#      encoded as GBK is not valid UTF-8, while a mangled file encoded as GBK reproduces exactly the UTF-8 bytes it was
#      corrupted from - so if the GBK bytes of a CJK-bearing text decode as UTF-8, the text is the mangled kind.
$encodingProblems = 0
$strict = New-Object Text.UTF8Encoding($false, $true)
$gbk = [Text.Encoding]::GetEncoding(936)
foreach ($f in (@(Files '*.md') + @(Files '*.java') + @(Files '*.ps1'))) {
	$bytes = [IO.File]::ReadAllBytes($f.FullName)
	$text = ''
	try { $text = $strict.GetString($bytes) } catch {
		$problems.Add('not UTF-8: ' + (Rel $f.FullName))
		$encodingProblems++
		continue
	}
	if ($text.Contains([string][char]0xFFFD)) {
		$problems.Add('U+FFFD (mojibake): ' + (Rel $f.FullName))
		$encodingProblems++
	}
	if ($text -match '[\u4e00-\u9fff]') {
		$mangled = $false
		try {
			$recovered = $strict.GetString($gbk.GetBytes($text))
			if ($recovered -ne $text -and $recovered -match '[\u4e00-\u9fff]') { $mangled = $true }
		}
		catch {
			# GBK bytes that are not valid UTF-8: this is what genuine Chinese looks like, so it is fine.
		}
		if ($mangled) {
			$problems.Add('UTF-8 read as GBK then saved again (the GBK bytes decode as UTF-8): ' + (Rel $f.FullName))
			$encodingProblems++
		}
	}
	if ($f.Extension -eq '.ps1') {
		$hasBom = ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF)
		$pureAscii = (@($bytes | Where-Object { $_ -gt 127 }).Count -eq 0)
		if (-not $hasBom -and -not $pureAscii) {
			$problems.Add('PowerShell script without BOM holds non-ASCII (5.1 would read it as ANSI): ' + (Rel $f.FullName))
			$encodingProblems++
		}
	}
}
Write-Host ("  encoding            : " + $encodingProblems + " problems")

if ($problems.Count -eq 0) {
	Write-Host "  documentation gate  : OK"
	exit 0
}
Write-Host ""
Write-Host ("  documentation gate  : " + $problems.Count + " problem(s)")
foreach ($p in ($problems | Select-Object -First 60)) { Write-Host ('    - ' + $p) }
if ($problems.Count -gt 60) { Write-Host ('    ... and ' + ($problems.Count - 60) + ' more') }
exit 1
