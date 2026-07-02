# Start DolphinScheduler standalone on Windows (no Docker/WSL required).
# Requires: JDK 8/11/17, extracted apache-dolphinscheduler-*-bin package.

param(
    [string]$DsRoot = "C:\Users\zjbin\Desktop\apache-dolphinscheduler-3.2.2-bin",
    [string]$JavaHome = "C:\Program Files\Java\jdk-17"
)

$ErrorActionPreference = "Stop"
$DsHome = Join-Path $DsRoot "standalone-server"
$LogDir = Join-Path $DsHome "logs"
$PidFile = Join-Path $DsHome "pid"

if (-not (Test-Path $DsHome)) {
    throw "DolphinScheduler not found at $DsHome. Extract apache-dolphinscheduler-*-bin first."
}

$rootLibs = Join-Path $DsRoot "libs"
if ((Get-ChildItem $rootLibs -Filter "*.jar" -ErrorAction SilentlyContinue).Count -lt 100) {
    throw "Root libs incomplete at $rootLibs. Re-extract the tarball; server lib folders are symlinks on Linux."
}

New-Item -ItemType Directory -Path $LogDir -Force | Out-Null

$CpDir = "C:\ds322\cp"
$CpReady = Join-Path $CpDir ".ready"
if (-not (Test-Path $CpReady)) {
    Write-Host "Building classpath directory (one-time copy to $CpDir)..."
    if (Test-Path $CpDir) { Remove-Item $CpDir -Recurse -Force }
    New-Item -ItemType Directory -Path $CpDir -Force | Out-Null
    $seen = @{}
    foreach ($jar in @(
            Get-ChildItem (Join-Path $DsHome "libs\standalone-server\*.jar")
            Get-ChildItem (Join-Path $DsRoot "libs\*.jar")
        )) {
        if ($jar.Name -like "dolphinscheduler-tools-*") {
            continue
        }
        if (-not $seen.ContainsKey($jar.Name)) {
            Copy-Item $jar.FullName (Join-Path $CpDir $jar.Name)
            $seen[$jar.Name] = $true
        }
    }
    Write-Host "Copied $($seen.Count) jars."
    New-Item -ItemType File -Path $CpReady -Force | Out-Null
}

$classpath = "$CpDir\*;$((Join-Path $DsHome 'conf'))"

$env:JAVA_HOME = $JavaHome
$env:DATABASE = "h2"
$java = Join-Path $JavaHome "bin\java.exe"

$javaOpts = @(
    "-server",
    "-Duser.timezone=Asia/Shanghai",
    "-Ddolphinscheduler.home=$DsRoot",
    "-Dresource.azure.client.id=minioadmin",
    "-Dresource.azure.client.secret=minioadmin",
    "-Dresource.azure.subId=minioadmin",
    "-Dresource.azure.tenant.id=minioadmin",
    "-Ddemo.tenant-code=default",
    "-Xms1g",
    "-Xmx1g",
    "-Xmn512m"
)

Write-Host "Starting DolphinScheduler standalone on http://127.0.0.1:12345/dolphinscheduler/ui"
Write-Host "Logs: $LogDir"

$proc = Start-Process -FilePath $java `
    -ArgumentList ($javaOpts + @("-cp", $classpath, "org.apache.dolphinscheduler.StandaloneServer")) `
    -WorkingDirectory $DsHome `
    -RedirectStandardOutput (Join-Path $LogDir "standalone-server.out") `
    -RedirectStandardError (Join-Path $LogDir "standalone-server.err") `
    -PassThru `
    -WindowStyle Hidden

$proc.Id | Out-File -FilePath $PidFile -Encoding ascii -Force
Write-Host "PID: $($proc.Id)"
