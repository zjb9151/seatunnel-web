# Stop integration-server and managed child processes (best-effort).

$ErrorActionPreference = "SilentlyContinue"
Get-CimInstance Win32_Process -Filter "Name='java.exe'" |
    Where-Object { $_.CommandLine -match 'org\.apache\.integration\.IntegrationApplication' } |
    ForEach-Object { Stop-Process -Id $_.ProcessId -Force; Write-Host "Stopped integration-server PID $($_.ProcessId)" }

Get-CimInstance Win32_Process -Filter "Name='java.exe'" |
    Where-Object { $_.CommandLine -match 'org\.apache\.seatunnel\.app\.SeatunnelApplication' } |
    ForEach-Object { Stop-Process -Id $_.ProcessId -Force; Write-Host "Stopped seatunnel-web PID $($_.ProcessId)" }

Get-CimInstance Win32_Process -Filter "Name='java.exe'" |
    Where-Object { $_.CommandLine -match 'StandaloneServer' } |
    ForEach-Object { Stop-Process -Id $_.ProcessId -Force; Write-Host "Stopped DolphinScheduler PID $($_.ProcessId)" }

Write-Host "Done."
