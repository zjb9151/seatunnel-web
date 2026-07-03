# Start integration-server (auto-installs runtime, starts DS / Engine / ST-Web)
#
# Usage:
#   .\scripts\start.ps1

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$env:JAVA_HOME = if ($env:JAVA_HOME) { $env:JAVA_HOME } else { "C:\Program Files\Java\jdk-17" }

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

$configDir = Join-Path $repoRoot "config"
$configPath = (Join-Path $configDir "integration.yml") -replace '\\', '/'

Push-Location $repoRoot
try {
    Write-Host "Starting integration-server on http://127.0.0.1:9000 ..."
    & "$env:JAVA_HOME\bin\java.exe" -server -Xms512m -Xmx1g `
        -cp $cp `
        "-Dintegration.config.dir=$configDir" `
        "-Dspring.config.import=optional:file:$configPath" `
        org.apache.integration.IntegrationApplication
} finally {
    Pop-Location
}
