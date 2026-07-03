# Clone Apache DolphinScheduler source into ./integration-dolphinscheduler (3.2.2-release).
#
# Usage (from repo root):
#   .\scripts\dolphinscheduler\clone-source.ps1

param(
    [string]$Branch = "3.2.2-release",
    [string]$TargetDir = ""
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "../..")
if (-not $TargetDir) {
    $TargetDir = Join-Path $repoRoot "integration-dolphinscheduler"
}

if (Test-Path (Join-Path $TargetDir ".git")) {
    Write-Host "DolphinScheduler source already exists at $TargetDir"
    exit 0
}

if (Test-Path $TargetDir) {
    Remove-Item $TargetDir -Recurse -Force
}

Write-Host "Cloning apache/dolphinscheduler ($Branch) into $TargetDir ..."
git clone --depth 1 --branch $Branch https://github.com/apache/dolphinscheduler.git $TargetDir
Write-Host "Done."
