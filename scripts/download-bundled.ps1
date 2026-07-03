# Download official release archives into integration-server/bundled/
#
# Usage:
#   .\scripts\download-bundled.ps1

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$bundled = Join-Path $repoRoot "integration-server\bundled"
$versionsFile = Join-Path $repoRoot "config\vendors.versions.yml"

if (-not (Test-Path $bundled)) {
    New-Item -ItemType Directory -Path $bundled -Force | Out-Null
}

function Get-YamlBlockValue($content, $block, $key) {
    $pattern = "(?ms)^$block\s*:\s*\r?\n(.*?)(?=^\S|\z)"
    $m = [regex]::Match($content, $pattern)
    if (-not $m.Success) { return $null }
    $lines = $m.Groups[1].Value -split "`r?`n"
    foreach ($line in $lines) {
        if ($line -match "^\s+$key\s*:\s*(.+)\s*$") {
            return $Matches[1].Trim().Trim('"')
        }
    }
    return $null
}

$content = Get-Content $versionsFile -Raw
$items = @(
    @{ Block = "dolphinscheduler"; Label = "DolphinScheduler" },
    @{ Block = "seatunnel-engine"; Label = "SeaTunnel Engine" }
)

foreach ($item in $items) {
    $url = Get-YamlBlockValue $content $item.Block "url"
    $archive = Get-YamlBlockValue $content $item.Block "archive"
    if (-not $url -or -not $archive) { continue }
    $target = Join-Path $bundled $archive
    if (Test-Path $target) {
        Write-Host "[$($item.Label)] exists: $target"
        continue
    }
    Write-Host "[$($item.Label)] downloading $url ..."
    Invoke-WebRequest -Uri $url -OutFile $target -UseBasicParsing
    Write-Host "[$($item.Label)] saved to $target"
}

Write-Host ""
Write-Host "SeaTunnel-Web: copy dist to runtime via UI or install-local API."
Write-Host "Done."
