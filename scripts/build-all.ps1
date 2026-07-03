# Build SeaTunnel upstream, integration-server, and integration-web.
#
# Usage (from repo root):
#   .\scripts\build-all.ps1

param(
    [switch]$SkipSt,
    [switch]$SkipIntegration,
    [switch]$SkipWeb
)

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$env:JAVA_HOME = if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-17" }

Push-Location $repoRoot
try {
    if (-not $SkipSt) {
        Write-Host "[Build] seatunnel-web modules..."
        .\mvnw.cmd -pl integration-seatunnel/seatunnel-web-dist -am package "-DskipTests"
    }
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
        Write-Host "[Build] seatunnel-ui -> ui/"
        & (Join-Path $repoRoot "scripts\build-ui.ps1") -SkipDs
    }
    Write-Host ""
    Write-Host "=== Build complete ==="
    Write-Host "Start: .\scripts\start.ps1"
    Write-Host "URL:   http://127.0.0.1:9000"
} finally {
    Pop-Location
}
