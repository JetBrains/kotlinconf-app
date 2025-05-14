@echo off

@rem
@rem Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
@rem

@rem Possible environment variables:
@rem   AMPER_DOWNLOAD_ROOT        Maven repository to download Amper dist from
@rem                              default: https://packages.jetbrains.team/maven/p/amper/amper
@rem   AMPER_JRE_DOWNLOAD_ROOT    Url prefix to download Amper JRE from.
@rem                              default: https:/
@rem   AMPER_BOOTSTRAP_CACHE_DIR  Cache directory to store extracted JRE and Amper distribution
@rem   AMPER_JAVA_HOME            JRE to run Amper itself (optional, does not affect compilation)
@rem   AMPER_JAVA_OPTIONS         JVM options to pass to the JVM running Amper (does not affect the user's application)

setlocal

@rem The version of the Amper distribution to provision and use
set amper_version=0.7.0-dev-2808
@rem Establish chain of trust from here by specifying exact checksum of Amper distribution to be run
set amper_sha256=0afdcda1ac9de4d25a9d6eec7cd0f8f9d7358da2c597a4a647592c5e49578920

if not defined AMPER_DOWNLOAD_ROOT set AMPER_DOWNLOAD_ROOT=https://packages.jetbrains.team/maven/p/amper/amper
if not defined AMPER_JRE_DOWNLOAD_ROOT set AMPER_JRE_DOWNLOAD_ROOT=https:/
if not defined AMPER_BOOTSTRAP_CACHE_DIR set AMPER_BOOTSTRAP_CACHE_DIR=%LOCALAPPDATA%\Amper
@rem remove trailing \ if present
if [%AMPER_BOOTSTRAP_CACHE_DIR:~-1%] EQU [\] set AMPER_BOOTSTRAP_CACHE_DIR=%AMPER_BOOTSTRAP_CACHE_DIR:~0,-1%

goto :after_function_declarations

REM ********** Download and extract any zip or .tar.gz archive **********

:download_and_extract
setlocal

set moniker=%~1
set url=%~2
set target_dir=%~3
set sha=%~4
set sha_size=%~5

set flag_file=%target_dir%\.flag
if exist "%flag_file%" (
    set /p current_flag=<"%flag_file%"
    if "%current_flag%" == "%sha%" exit /b
)

@rem This multiline string is actually passed as a single line to powershell, meaning #-comments are not possible.
@rem So here are a few comments about the code below:
@rem  - we need to support both .zip and .tar.gz archives (for the Amper distribution and the JBR)
@rem  - tar should be present in all Windows machines since 2018 (and usable from both cmd and powershell)
@rem  - tar requires the destination dir to exist
@rem  - We use (New-Object Net.WebClient).DownloadFile instead of Invoke-WebRequest for performance. See the issue
@rem    https://github.com/PowerShell/PowerShell/issues/16914, which is still not fixed in Windows PowerShell 5.1
@rem  - DownloadFile requires the directories in the destination file's path to exist
set download_and_extract_ps1= ^
Set-StrictMode -Version 3.0; ^
$ErrorActionPreference = 'Stop'; ^
 ^
$createdNew = $false; ^
$lock = New-Object System.Threading.Mutex($true, ('Global\amper-bootstrap.' + '%target_dir%'.GetHashCode().ToString()), [ref]$createdNew); ^
if (-not $createdNew) { ^
    Write-Host 'Another Amper instance is bootstrapping. Waiting for our turn...'; ^
    [void]$lock.WaitOne(); ^
} ^
 ^
try { ^
    if ((Get-Content '%flag_file%' -ErrorAction Ignore) -ne '%sha%') { ^
        $temp_file = '%AMPER_BOOTSTRAP_CACHE_DIR%\' + [System.IO.Path]::GetRandomFileName(); ^
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; ^
        Write-Host 'Downloading %moniker%... (only happens on the first run of this version)'; ^
        [void](New-Item '%AMPER_BOOTSTRAP_CACHE_DIR%' -ItemType Directory -Force); ^
        if (Get-Command curl.exe -errorAction SilentlyContinue) { ^
            curl.exe -L --silent --show-error --fail --output $temp_file '%url%'; ^
        } else { ^
            (New-Object Net.WebClient).DownloadFile('%url%', $temp_file); ^
        } ^
 ^
        $actualSha = (Get-FileHash -Algorithm SHA%sha_size% -Path $temp_file).Hash.ToString(); ^
        if ($actualSha -ne '%sha%') { ^
            $writeErr = if ($Host.Name -eq 'ConsoleHost') { [Console]::Error.WriteLine } else { $host.ui.WriteErrorLine } ^
            $writeErr.Invoke(\"ERROR: Checksum mismatch for $temp_file (downloaded from %url%): expected checksum %sha% but got $actualSha\"); ^
            exit 1; ^
        } ^
 ^
        if (Test-Path '%target_dir%') { ^
            Remove-Item '%target_dir%' -Recurse; ^
        } ^
        if ($temp_file -like '*.zip') { ^
            Add-Type -A 'System.IO.Compression.FileSystem'; ^
            [IO.Compression.ZipFile]::ExtractToDirectory($temp_file, '%target_dir%'); ^
        } else { ^
            [void](New-Item '%target_dir%' -ItemType Directory -Force); ^
            tar -xzf $temp_file -C '%target_dir%'; ^
        } ^
        Remove-Item $temp_file; ^
 ^
        Set-Content '%flag_file%' -Value '%sha%'; ^
        Write-Host 'Download complete.'; ^
        Write-Host ''; ^
    } ^
} ^
finally { ^
    $lock.ReleaseMutex(); ^
}

set powershell=%SystemRoot%\system32\WindowsPowerShell\v1.0\powershell.exe
"%powershell%" -NonInteractive -NoProfile -NoLogo -Command %download_and_extract_ps1%
if errorlevel 1 exit /b 1
exit /b 0

:fail
echo ERROR: Amper bootstrap failed, see errors above
exit /b 1

:after_function_declarations

REM ********** Provision Amper distribution **********

set amper_url=%AMPER_DOWNLOAD_ROOT%/org/jetbrains/amper/cli/%amper_version%/cli-%amper_version%-dist.tgz
set amper_target_dir=%AMPER_BOOTSTRAP_CACHE_DIR%\amper-cli-%amper_version%
call :download_and_extract "Amper distribution v%amper_version%" "%amper_url%" "%amper_target_dir%" "%amper_sha256%" "256"
if errorlevel 1 goto fail

REM ********** Provision JRE for Amper **********

if defined AMPER_JAVA_HOME goto jre_provisioned

@rem Auto-updated from syncVersions.main.kts, do not modify directly here
set jbr_version=21.0.6
set jbr_build=b895.97
if "%PROCESSOR_ARCHITECTURE%"=="ARM64" (
    set jbr_arch=aarch64
    set jbr_sha512=188bb92c35bc31b8ec9596701b498797c6578fb8513f1a854a2c8501ff3d2883a1fc74d24c45322526cdaaeb86940fffaf9729f39ba8dd52dd0f2b6f63da35fe
) else if "%PROCESSOR_ARCHITECTURE%"=="AMD64" (
    set jbr_arch=x64
    set jbr_sha512=7e71a463327a92e6974b3d1013efde00f9d852660d5a18eae5765534b6d3cf0de471f72fd30d3caae910253b8b0df7202e2a76f0435e84ad80d13fb298a84c48
) else (
    echo Unknown Windows architecture %PROCESSOR_ARCHITECTURE% >&2
    goto fail
)

REM !! DO NOT REMOVE !!
REM                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                exit /b %ERRORLEVEL%
REM
REM The above comment is strategically placed to compensate for a bug in the update command in Amper 0.5.0.
REM During the update, the wrapper script is overwritten in-place while running. The problem is that cmd.exe doesn't
REM buffer the original script as a whole, and instead reloads it after every command, and tries to resume at the same
REM byte offset as before.
REM In the 0.5.0 script, the java command running Amper is followed by the command 'exit /b %ERRORLEVEL%', which is
REM exactly at the byte offset 6826. So, when the java command finishes, cmd.exe wants to run this exit command, but
REM it first reloads the file and gets the new content (this one) before trying to run whatever is at offset 6826.
REM We must place an exit command right at that offset to allow 0.5.0 to complete properly.
REM Since there are version/checksum placeholders at the top of this template wrapper file, we need to dynamically
REM adjust the position of the exit command, hence the padding placeholder.

@rem URL for JBR (vanilla) - see https://github.com/JetBrains/JetBrainsRuntime/releases
set jbr_url=%AMPER_JRE_DOWNLOAD_ROOT%/cache-redirector.jetbrains.com/intellij-jbr/jbr-%jbr_version%-windows-%jbr_arch%-%jbr_build%.tar.gz
set jbr_target_dir=%AMPER_BOOTSTRAP_CACHE_DIR%\jbr-%jbr_version%-windows-%jbr_arch%-%jbr_build%
call :download_and_extract "JetBrains Runtime v%jbr_version%%jbr_build%" "%jbr_url%" "%jbr_target_dir%" "%jbr_sha512%" "512"
if errorlevel 1 goto fail

set AMPER_JAVA_HOME=
for /d %%d in ("%jbr_target_dir%\*") do if exist "%%d\bin\java.exe" set AMPER_JAVA_HOME=%%d
if not exist "%AMPER_JAVA_HOME%\bin\java.exe" (
  echo Unable to find java.exe under %jbr_target_dir%
  goto fail
)
:jre_provisioned

REM ********** Launch Amper **********

set jvm_args=-ea -XX:+EnableDynamicAgentLoading %AMPER_JAVA_OPTIONS%
"%AMPER_JAVA_HOME%\bin\java.exe" "-Damper.wrapper.dist.sha256=%amper_sha256%" "-Damper.wrapper.path=%~f0" %jvm_args% -cp "%amper_target_dir%\lib\*" org.jetbrains.amper.cli.MainKt %*
exit /B %ERRORLEVEL%
