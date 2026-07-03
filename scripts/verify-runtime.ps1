# Verify runtime install layout under runtime/ (managed by integration-server).
#
# Usage:
#   .\scripts\verify-runtime.ps1

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$runtimeRoot = Join-Path $repoRoot "runtime"

$jar = Join-Path $repoRoot "integration-server\integration-app\target\integration-app-1.0.3-SNAPSHOT.jar"
Write-Host "[integration-app] $jar"
if (-not (Test-Path $jar)) {
    Write-Host "  WARN — run .\scripts\build-all.ps1"
} else {
    Write-Host "  OK"
}

Write-Host "[runtime] $runtimeRoot (created on first start)"
Write-Host ""
Write-Host "Tip: .\scripts\download-bundled.ps1 then .\scripts\start.ps1"
