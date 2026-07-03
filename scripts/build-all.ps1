# Build integration-server and integration-web only.
#
# Usage:
#   .\scripts\build-all.ps1

param(
    [switch]$SkipIntegration,
    [switch]$SkipWeb
)

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$env:JAVA_HOME = if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-17" }

Push-Location $repoRoot
try {
    if (-not $SkipIntegration) {
        Write-Host "[Build] integration-server..."
        .\mvnw.cmd -pl integration-server/integration-app -am package "-DskipTests"
    }
    if (-not $SkipWeb) {
        Write-Host "[Build] integration-web..."
        Push-Location (Join-Path $repoRoot "integration-web")
        if (-not (Test-Path node_modules)) { npm install --include=dev }
        npm run build:prod
        Pop-Location
    }
    Write-Host ""
    Write-Host "=== Build complete ==="
    Write-Host "Bundled runtimes: .\scripts\download-bundled.ps1"
    Write-Host "Start:            .\scripts\start.ps1"
    Write-Host "URL:              http://127.0.0.1:9000/runtime"
} finally {
    Pop-Location
}
