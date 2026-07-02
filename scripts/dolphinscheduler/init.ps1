# Initialize DolphinScheduler for SeaTunnel Web integration.
# Creates project "seatunnel", API token, and updates application.yml.

param(
    [string]$ApiBase = "http://127.0.0.1:12345/dolphinscheduler",
    [string]$Username = "admin",
    [string]$Password = "dolphinscheduler123",
    [string]$ProjectName = "seatunnel",
    [string]$ApplicationYml = "C:\Users\zjbin\Desktop\seatunnel-web\seatunnel-server\seatunnel-app\src\main\resources\application.yml",
    [int]$WaitSeconds = 180
)

$ErrorActionPreference = "Stop"

function Wait-ForDs {
    param([int]$TimeoutSec)
    $deadline = (Get-Date).AddSeconds($TimeoutSec)
    while ((Get-Date) -lt $deadline) {
        try {
            $r = Invoke-WebRequest -Uri "$ApiBase/actuator/health" -UseBasicParsing -TimeoutSec 3 -ErrorAction Stop
            if ($r.StatusCode -eq 200) { return $true }
        } catch {
            try {
                $body = @{ userName = $Username; userPassword = $Password }
                $r = Invoke-RestMethod -Uri "$ApiBase/login" -Method POST -Body $body -TimeoutSec 3
                if ($r.code -eq 0) { return $true }
            } catch { }
        }
        Start-Sleep -Seconds 3
    }
    return $false
}

function Invoke-DsApi {
    param(
        [string]$Method,
        [string]$Path,
        [hashtable]$Form,
        [string]$SessionId
    )
    $headers = @{ sessionId = $SessionId }
    if ($Form) {
        return Invoke-RestMethod -Uri "$ApiBase$Path" -Method $Method -Headers $headers -Body $Form -TimeoutSec 30
    }
    return Invoke-RestMethod -Uri "$ApiBase$Path" -Method $Method -Headers $headers -TimeoutSec 30
}

Write-Host "Waiting for DolphinScheduler API..."
if (-not (Wait-ForDs -TimeoutSec $WaitSeconds)) {
    throw "DolphinScheduler did not become ready within ${WaitSeconds}s"
}

Write-Host "Logging in as $Username..."
$login = Invoke-RestMethod -Uri "$ApiBase/login" -Method POST -Body @{
    userName     = $Username
    userPassword = $Password
}
if ($login.code -ne 0) {
    throw "Login failed: $($login.msg)"
}
$sessionToken = $login.data.sessionId
if (-not $sessionToken) { $sessionToken = $login.data.token }
Write-Host "Login OK."

Write-Host "Ensuring project '$ProjectName' exists..."
$list = Invoke-DsApi -Method GET -Path "/projects/list?pageSize=100&pageNo=1" -SessionId $sessionToken
$project = $list.data | Where-Object { $_.name -eq $ProjectName } | Select-Object -First 1
if (-not $project) {
    $created = Invoke-DsApi -Method POST -Path "/projects" -SessionId $sessionToken -Form @{
        projectName = $ProjectName
        description = "SeaTunnel Web integration project"
    }
    if ($created.code -ne 0) {
        throw "Create project failed: $($created.msg)"
    }
    $projectCode = [string]$created.data.code
    Write-Host "Created project code: $projectCode"
} else {
    $projectCode = [string]$project.code
    Write-Host "Project already exists, code: $projectCode"
}

Write-Host "Creating API access token..."
$expireTime = (Get-Date).AddYears(10).ToString("yyyy-MM-dd HH:mm:ss")
$tokenResp = Invoke-DsApi -Method POST -Path "/access-tokens" -SessionId $sessionToken -Form @{
    userId     = "1"
    expireTime = $expireTime
}
if ($tokenResp.code -ne 0) {
    throw "Create access token failed: $($tokenResp.msg)"
}
$apiToken = $tokenResp.data.token
Write-Host "API token created."

$serviceToken = -join ((48..57) + (97..102) | Get-Random -Count 32 | ForEach-Object { [char]$_ })

Write-Host "Updating application.yml..."
if (-not (Test-Path $ApplicationYml)) {
    throw "application.yml not found: $ApplicationYml"
}
$content = Get-Content $ApplicationYml -Raw -Encoding UTF8
$content = $content -replace '(?m)^(\s*project-code:\s*).*$', "`${1}$projectCode"
$content = $content -replace '(?m)^(\s*token:\s*).*$', "`${1}`"$apiToken`""
$content = $content -replace '(?m)^(\s*service-token:\s*).*$', "`${1}`"$serviceToken`""
Set-Content -Path $ApplicationYml -Value $content -Encoding UTF8 -NoNewline

Write-Host ""
Write-Host "=== Initialization complete ==="
Write-Host "DS UI:      http://127.0.0.1:12345/dolphinscheduler/ui"
Write-Host "Login:      $Username / $Password"
Write-Host "Project:    $ProjectName (code=$projectCode)"
Write-Host "API token:  $apiToken"
Write-Host "Service token updated in application.yml (restart SeaTunnel Web backend to apply)."
