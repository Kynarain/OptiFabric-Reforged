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
#    Three unambiguous accidents are checked:
#      a) the bytes are not valid UTF-8 at all, or hold U+FFFD;
#      b) a whole line was UTF-8, read as GBK and saved again. Reversing it works: its GBK bytes are exactly the UTF-8
#         it was corrupted from, so they decode back to the original, and genuine Chinese encoded as GBK is not valid
#         UTF-8. Done line by line, so one mangled line among good ones is still found.
#      c) a one-character residue, whose GBK bytes are not valid UTF-8 at all, so (b) cannot see it; the characters
#         this accident produced are listed instead.
#    NOT checked, deliberately: the lossy form, where a few characters were lost and the rest survived. A '?' left
#    behind by a lost byte is the same character as a '?' the author typed, and this repository has lines with two of
#    them (version wildcards, "??" in release notes). Two narrower rules were tried and both flagged ordinary
#    documents, so this is a known blind spot rather than an oversight.
$encodingProblems = 0
$strict = New-Object Text.UTF8Encoding($false, $true)
$gbk = [Text.Encoding]::GetEncoding(936)
$residue = @([char]0x947A)
$mojibakeWords = @('中文', '说明', '更新', '版本', '链接', '文档', '项目', '支持', '兼容', '问题', '修复', '测试', '加载', '启动', '世界', '模组', '发布', '构建', '标签', '目录')
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
	$mangledLine = -1
	$lineNo = 0
	foreach ($line in ($text -split "`r?`n")) {
		$lineNo++
		if ($line -notmatch '[\u4e00-\u9fff]') { continue }
		try {
			$recovered = $strict.GetString($gbk.GetBytes($line))
			if ($recovered -ne $line -and $recovered -match '[\u4e00-\u9fff]') { $mangledLine = $lineNo; break }
		}
		catch {
			# GBK bytes that are not valid UTF-8: this is what genuine Chinese looks like.
		}
	}
	if ($mangledLine -gt 0) {
		$problems.Add('UTF-8 read as GBK then saved again (line ' + $mangledLine + '): ' + (Rel $f.FullName))
		$encodingProblems++
	}
	foreach ($ch in $residue) {
		if ($text.Contains([string]$ch)) {
			$problems.Add('a known mojibake residue (' + $ch + ') in ' + (Rel $f.FullName))
			$encodingProblems++
			break
		}
	}
	# A word that shows up in its GBK reinterpretation. This is how a script without a BOM wrote '涓枃鐗?' instead of
	# '中文版' into a generated page: the string was read as the local code page and every character came out wrong.
	# Words are used instead of single characters because a single mangled character is indistinguishable from a rare
	# but legitimate one, while a whole mangled word is not.
	foreach ($word in $mojibakeWords) {
		$form = $gbk.GetString([Text.Encoding]::UTF8.GetBytes($word))
		if ($form -ne $word -and $text.Contains($form)) {
			$problems.Add('GBK mojibake of "' + $word + '" (wrote "' + $form + '") in ' + (Rel $f.FullName))
			$encodingProblems++
			break
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
