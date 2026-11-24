<#
.SYNOPSIS
    Full verification: Gradle build (compile + JUnit tests + jars), then the client gametest.
.DESCRIPTION
    Prints one line per stage and one line total. On failure prints only the error lines
    and the log path. Raw Gradle output goes to build\verify\<stage>.log.
.PARAMETER SkipGameTest
    Skip the in-game client gametest (it needs a display and takes ~1.5 minutes).
.PARAMETER NoDaemon
    Pass --no-daemon to Gradle (CI parity).
#>
[CmdletBinding()]
param(
    [switch]$SkipGameTest,
    [switch]$NoDaemon
)

$ErrorActionPreference = 'Continue'
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

$gradlew = Join-Path $root 'gradlew.bat'
$logDir = Join-Path $root 'build\verify'
New-Item -ItemType Directory -Force $logDir | Out-Null

function Invoke-Stage {
    param(
        [Parameter(Mandatory)][string]$Name,
        [Parameter(Mandatory)][string[]]$GradleArgs
    )
    $log = Join-Path $logDir "$Name.log"
    $extra = @()
    if ($NoDaemon) { $extra += '--no-daemon' }

    $sw = [Diagnostics.Stopwatch]::StartNew()
    & $gradlew @GradleArgs @extra *> $log
    $code = $LASTEXITCODE
    $sw.Stop()
    $secs = [math]::Round($sw.Elapsed.TotalSeconds)

    if ($code -ne 0) {
        Write-Host "FAIL  $Name (${secs}s)  log: $log"
        Select-String -Path $log -Pattern 'error:|FAILED|What went wrong|Exception|AssertionError|Could not' |
            Select-Object -First 25 |
            ForEach-Object { Write-Host "  $($_.Line.Trim())" }
        exit $code
    }
    Write-Host "ok    $Name (${secs}s)"
    return $secs
}

$total = [Diagnostics.Stopwatch]::StartNew()

# 'build' depends on 'check', which runs the JUnit tests.
Invoke-Stage -Name 'build' -GradleArgs @('build') | Out-Null

if (-not $SkipGameTest) {
    # Clear stale screenshots so the count below reflects this run only.
    $shots = Join-Path $root 'build\run\clientGameTest\screenshots'
    if (Test-Path $shots) { Remove-Item -Recurse -Force $shots }
    Invoke-Stage -Name 'gametest' -GradleArgs @('runClientGameTest') | Out-Null
    $count = if (Test-Path $shots) { (Get-ChildItem $shots -Filter *.png).Count } else { 0 }
    if ($count -eq 0) {
        Write-Host "FAIL  gametest produced no screenshots in $shots"
        exit 1
    }
    Write-Host "      screenshots: $count in $shots"
}

$total.Stop()
Write-Host "VERIFY OK ($([math]::Round($total.Elapsed.TotalSeconds))s)"
exit 0
