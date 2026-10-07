# Silent-catch lint: every catch block must either say something or say why it does not.
#
# The rule exists because of a measured comparison, not a style preference. A loader that reaches
# "no crash" by turning failures into no-ops reports success while parts of a mod are dead, and its own
# tests cannot see the difference. This project's answer is the opposite: a repair must either restore the
# vanilla behaviour or re-create the injection point, and every repair announces itself in the log.
#
# So a catch block that swallows an exception is allowed, but it must be *explicit*:
#
#   * ERROR   - the block neither speaks (System.out / System.err / a logger / throw / OptifabricError)
#               nor carries any comment. This fails the run.
#   * WARNING - the block carries a comment, but not the marker `silent by design:`. It is being reported
#               so the marker can be added deliberately; add it once you have read the block and agree
#               that silence is right there.
#
# A block that speaks needs nothing. The "speaks" vocabulary deliberately includes `builder.append(` and
# `append(`, because writing the reason into a crash report is speaking too - that is where a reader looks.

param(
    # Repository root holding src/main/java.
    [string]$Root = (Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)),

    # How many lines after the catch clause count as its body. Bodies here are short by convention.
    [int]$BodyLines = 12,

    # Treat WARNING as failure too. Off by default so the first run reports without blocking.
    [switch]$Strict
)

$ErrorActionPreference = "Stop"

$sourceRoot = Join-Path $Root "src\main\java"

if (-not (Test-Path -LiteralPath $sourceRoot)) {
    Write-Host "silent-catch lint: no src\main\java under $Root - nothing to check"
    exit 0
}

# Marker a block carries once someone has read it and decided silence is right there.
$marker = "silent by design:"

# Evidence that the block is not silent. `append(` is on purpose: a message written into a crash report
# or a builder is heard by the person who has to read it.
$speaks = 'System\.(out|err)|printStackTrace|throw\s|OptifabricError|LOGGER|logger\.|log\.|append\('

$errors = @()
$warnings = @()
$checked = 0

foreach ($file in (Get-ChildItem -LiteralPath $sourceRoot -Recurse -File -Filter '*.java')) {
    $lines = [IO.File]::ReadAllLines($file.FullName)
    $relative = $file.FullName.Substring($Root.Length).TrimStart('\')

    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -notmatch 'catch\s*\(') { continue }

        $checked++

        $end = [Math]::Min($i + $BodyLines, $lines.Count - 1)
        $body = ($lines[($i + 1)..$end]) -join "`n"

        if ($body -match $speaks) { continue }

        $entry = "{0}:{1}" -f $relative, ($i + 1)

        if ($body -match [regex]::Escape($marker)) {
            continue
        }

        # No marker. A comment of its own is a warning; nothing at all is an error.
        if ($body -match '(?m)^\s*(//|/\*|\*)') {
            $warnings += $entry
        } else {
            $errors += $entry
        }
    }
}

Write-Host ("silent-catch lint: {0} catch block(s) checked in {1}" -f $checked, $sourceRoot)

if ($warnings.Count -gt 0) {
    Write-Host ("{0} block(s) carry a comment but not the marker - add `"{1} <reason>`" once you agree:" -f $warnings.Count, $marker)
    $warnings | ForEach-Object { Write-Host ("   warning  " + $_) }
}

if ($errors.Count -gt 0) {
    Write-Host ("{0} block(s) swallow an exception without a word:" -f $errors.Count)
    $errors | ForEach-Object { Write-Host ("   error    " + $_) }
}

if ($errors.Count -gt 0 -or ($Strict -and $warnings.Count -gt 0)) {
    Write-Host "silent-catch lint: FAILED"
    exit 1
}

Write-Host "silent-catch lint: OK"
exit 0
