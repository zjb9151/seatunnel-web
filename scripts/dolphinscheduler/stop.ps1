# Stop DolphinScheduler standalone started by start.ps1
#
# Usage:
#   $env:DOLPHINSCHEDULER_HOME = "C:\path\to\apache-dolphinscheduler-3.2.2-bin"
#   .\stop.ps1
# Or:
#   .\stop.ps1 -DsRoot "C:\path\to\apache-dolphinscheduler-3.2.2-bin"

param(
    [string]$DsRoot = $env:DOLPHINSCHEDULER_HOME
)

$ErrorActionPreference = "Stop"

if (-not $DsRoot) {
    throw "Set -DsRoot or DOLPHINSCHEDULER_HOME to the extracted apache-dolphinscheduler-*-bin directory."
}

$PidFile = Join-Path (Join-Path $DsRoot "standalone-server") "pid"
if (Test-Path $PidFile) {
    $pid = [int](Get-Content $PidFile -Raw)
    if (Get-Process -Id $pid -ErrorAction SilentlyContinue) {
        Stop-Process -Id $pid -Force
        Write-Host "Stopped DolphinScheduler PID $pid"
    }
    Remove-Item $PidFile -Force
} else {
    Write-Host "PID file not found at $PidFile; DolphinScheduler may not be running."
}
