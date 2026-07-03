# Start integration-server (auto-starts DS, Engine, seatunnel-web per config/integration.yml)
#
# Usage:
#   .\scripts\start.ps1

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$env:JAVA_HOME = if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-17" }
$env:SEATUNNEL_HOME = if ($env:SEATUNNEL_HOME) { $env:SEATUNNEL_HOME } else { "C:\Users\zjbin\Desktop\apache-seatunnel-2.3.11" }

$targetDir = Join-Path $repoRoot "integration-server\integration-app\target"
$jar = Join-Path $targetDir "integration-app-1.0.3-SNAPSHOT.jar"
$libs = Join-Path $targetDir "dependency"

if (-not (Test-Path $jar)) {
    Write-Host "integration-app jar not found. Run .\scripts\build-all.ps1 first."
    exit 1
}

$cp = $jar
if (Test-Path $libs) {
    $depJars = Get-ChildItem $libs -Filter "*.jar" | ForEach-Object { $_.FullName }
    if ($depJars.Count -gt 0) {
        $cp = ($depJars + $jar) -join ";"
    }
}

Push-Location $repoRoot
try {
    Write-Host "Starting integration-server on http://127.0.0.1:9000 ..."
    $configPath = (Join-Path $repoRoot "config\integration.yml") -replace '\\', '/'
    & "$env:JAVA_HOME\bin\java.exe" -server -Xms512m -Xmx1g `
        -cp $cp `
        "-Dspring.config.import=optional:file:$configPath" `
        org.apache.integration.IntegrationApplication
} finally {
    Pop-Location
}
