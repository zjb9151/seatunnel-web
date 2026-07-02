# Stop DolphinScheduler standalone started by start.ps1

$PidFile = "C:\Users\zjbin\Desktop\apache-dolphinscheduler-3.2.2-bin\standalone-server\pid"
if (Test-Path $PidFile) {
    $pid = [int](Get-Content $PidFile -Raw)
    if (Get-Process -Id $pid -ErrorAction SilentlyContinue) {
        Stop-Process -Id $pid -Force
        Write-Host "Stopped DolphinScheduler PID $pid"
    }
    Remove-Item $PidFile -Force
} else {
    Write-Host "PID file not found; DolphinScheduler may not be running."
}
