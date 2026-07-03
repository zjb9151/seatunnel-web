# Clone vendor sources and verify runtime paths.
#
# Usage:
#   .\scripts\setup-vendors.ps1

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")

& (Join-Path $PSScriptRoot "dolphinscheduler\clone-source.ps1")

Write-Host ""
Write-Host "Vendor layout:"
Write-Host "  integration-dolphinscheduler/  - DS official source (git submodule)"
Write-Host "  integration-seatunnel/         - SeaTunnel-Web official modules"
Write-Host ""
Write-Host "Configure runtime paths in config/integration.yml:"
Write-Host "  processes.dolphinscheduler.home  -> apache-dolphinscheduler-*-bin"
Write-Host "  processes.seatunnel-engine.home  -> apache-seatunnel-*"
Write-Host "  processes.seatunnel-web.dist-home -> seatunnel-web-dist output"
