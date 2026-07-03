# Build SeaTunnel UI and DolphinScheduler UI (with SeaTunnel integration).
#
# Usage (from repo root):
#   .\scripts\build-ui.ps1
#   .\scripts\build-ui.ps1 -DsHome "C:\path\to\apache-dolphinscheduler-3.2.2-bin"
#
# Outputs:
#   - SeaTunnel UI  -> ./ui/  (served by backend at http://host:8801/ui/)
#   - DS UI         -> $DsHome/ui/  (served by DS at http://host:12345/dolphinscheduler/ui/)

param(
    [string]$DsHome = $env:DOLPHINSCHEDULER_HOME,
    [switch]$SkipDs,
    [switch]$SkipSt
)

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")

function Invoke-NpmBuild {
    param(
        [string]$Dir,
        [string]$Label
    )
    Push-Location $Dir
    try {
        if (-not (Test-Path "node_modules")) {
            Write-Host "[$Label] npm install..."
            npm install --ignore-scripts
        }
        Write-Host "[$Label] npm run build:prod..."
        $env:NODE_ENV = "production"
        npm run build:prod
        if ($LASTEXITCODE -ne 0) { throw "$Label build failed" }
    } finally {
        Pop-Location
    }
}

function Invoke-PnpmBuild {
    param(
        [string]$Dir,
        [string]$Label
    )
    Push-Location $Dir
    try {
        if (-not (Test-Path "node_modules")) {
            Write-Host "[$Label] npm install (with devDependencies)..."
            npm install --include=dev --legacy-peer-deps
        }
        Write-Host "[$Label] production build..."
        $env:NODE_ENV = "production"
        npx vue-tsc --noEmit
        if ($LASTEXITCODE -ne 0) { throw "$Label typecheck failed" }
        npx vite build --mode production
        if ($LASTEXITCODE -ne 0) { throw "$Label build failed" }
    } finally {
        Pop-Location
    }
}

if (-not $SkipSt) {
    $stUi = Join-Path $repoRoot "seatunnel-ui"
    $stDist = Join-Path $stUi "dist"
    $stTarget = Join-Path $repoRoot "ui"

    Invoke-NpmBuild -Dir $stUi -Label "SeaTunnel UI"

    if (-not (Test-Path $stDist)) {
        throw "SeaTunnel UI dist not found: $stDist"
    }
    if (Test-Path $stTarget) {
        Remove-Item $stTarget -Recurse -Force
    }
    New-Item -ItemType Directory -Path $stTarget -Force | Out-Null
    Copy-Item -Path (Join-Path $stDist "*") -Destination $stTarget -Recurse -Force
    Write-Host "[SeaTunnel UI] Copied to $stTarget"
}

if (-not $SkipDs) {
    $dsUi = Join-Path $repoRoot "dolphinscheduler/dolphinscheduler-ui"
    if (-not (Test-Path $dsUi)) {
        Write-Host "[DS UI] Source not found. Run scripts/dolphinscheduler/clone-source.ps1 first."
    } else {
        Invoke-PnpmBuild -Dir $dsUi -Label "DolphinScheduler UI"
        $dsDist = Join-Path $dsUi "dist"
        if (-not (Test-Path $dsDist)) {
            throw "DS UI dist not found: $dsDist"
        }
        if ($DsHome) {
            $dsTarget = Join-Path $DsHome "ui"
            if (Test-Path $dsTarget) {
                Remove-Item $dsTarget -Recurse -Force
            }
            New-Item -ItemType Directory -Path $dsTarget -Force | Out-Null
            Copy-Item -Path (Join-Path $dsDist "*") -Destination $dsTarget -Recurse -Force
            Write-Host "[DS UI] Copied to $dsTarget"
        } else {
            Write-Host "[DS UI] Built at $dsDist"
            Write-Host "[DS UI] Set DOLPHINSCHEDULER_HOME and re-run to deploy into DS standalone ui/ folder."
        }
    }
}

Write-Host ""
Write-Host "=== UI build complete ==="
if (-not $SkipSt) {
    Write-Host "SeaTunnel UI: http://127.0.0.1:8801/ui/"
}
if (-not $SkipDs -and $DsHome) {
    Write-Host "DS UI:        http://127.0.0.1:12345/dolphinscheduler/ui/"
}
