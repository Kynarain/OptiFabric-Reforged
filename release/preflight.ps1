# Run this before creating a release for this line. Every line has a release ritual; this is the part of it that a
# machine can check, so that a release cannot be cut from a tree whose own checks are red.
# Nothing here launches Minecraft: see release\MANUAL_RELEASE.md for the in-game step.
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$failed = New-Object System.Collections.Generic.List[string]

function Step([string]$name, [scriptblock]$body) {
	Write-Host ''
	Write-Host ('== ' + $name)
	try {
		& $body
		if ($LASTEXITCODE -ne 0) { $failed.Add($name) }
	}
	catch {
		Write-Host ('   ' + $_.Exception.Message)
		$failed.Add($name)
	}
}

Step 'documentation gate' { & powershell -NoProfile -ExecutionPolicy Bypass -File (Join-Path $root 'tools\check-docs.ps1') }

Step 'silent-catch lint' { & powershell -NoProfile -ExecutionPolicy Bypass -File (Join-Path $root 'tools\check-silent-catches.ps1') }

Step 'build' {
	Push-Location $root
	try { & cmd /c "gradlew.bat build --offline --console=plain 2>&1" | Select-String -Pattern 'BUILD SUCCESSFUL|BUILD FAILED|error:' | ForEach-Object { Write-Host ('   ' + $_.Line) } }
	finally { Pop-Location }
}

Write-Host ''
if ($failed.Count -eq 0) {
	Write-Host 'preflight: OK - the documents and the build agree with the version in gradle.properties.'
	Write-Host 'Next: release\MANUAL_RELEASE.md, and the in-game step it describes.'
	exit 0
}
Write-Host ('preflight: ' + $failed.Count + ' step(s) failed: ' + ($failed -join ', '))
Write-Host 'Do not publish from this tree until they pass.'
exit 1
