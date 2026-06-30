@echo off
setlocal enabledelayedexpansion

:: ============================================
:: Asistencia QR - Launcher
:: ============================================

:: ------ Usar directorio del script como base ------
set "SCRIPT_DIR=%~dp0"
if "!SCRIPT_DIR:~-1!"=="\" set "SCRIPT_DIR=!SCRIPT_DIR:~0,-1!"

:: ------ Verificar que el .exe existe ------
if not exist "!SCRIPT_DIR!\asistencia-qr.exe" goto :notfound

:: ------ Detectar JAVA_HOME si no está definido ------
if not defined JAVA_HOME (
    for /f "delims=" %%I in ('where java 2^>nul') do set "JAVA_PATH=%%I"
    if defined JAVA_PATH (
        for %%J in ("!JAVA_PATH!") do set "JAVA_HOME=%%~dpJ"
        for %%J in ("!JAVA_HOME!.") do set "JAVA_HOME=%%~dpJ"
        if "!JAVA_HOME:~-1!"=="\" set "JAVA_HOME=!JAVA_HOME:~0,-1!"
        if "!JAVA_HOME:~-4!"=="\bin" set "JAVA_HOME=!JAVA_HOME:~0,-4!"
    )
)

if not defined JAVA_HOME (
    for %%P in (
        "C:\Program Files\Eclipse Adoptium\jdk-25*"
        "C:\Program Files\Eclipse Adoptium\jdk-24*"
        "C:\Program Files\Eclipse Adoptium\jdk-23*"
        "C:\Program Files\Eclipse Adoptium\jdk-22*"
        "C:\Program Files\Eclipse Adoptium\jdk-21*"
        "C:\Program Files\Java\jdk-*"
        "C:\Program Files\Java\jre-*"
        "C:\Program Files\Java\jdk*"
    ) do (
        for /d %%D in ("%%~P") do if exist "%%D\bin\java.exe" set "JAVA_HOME=%%D"
        if defined JAVA_HOME goto :exe
    )
)
:exe

if defined JAVA_HOME (
    echo JAVA_HOME=!JAVA_HOME!
) else (
    echo ADVERTENCIA: No se pudo detectar Java 21-25.
    echo Asegurese de tener Java instalado y visible en PATH.
    echo.
)

:: ------ Lanzar el .exe ------
echo Iniciando Asistencia QR...
start "Asistencia QR" "!SCRIPT_DIR!\asistencia-qr.exe"
echo.
echo El servidor se iniciara en unos segundos.
echo Cuando este listo, abra en su navegador:
echo.
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /C:"IPv4" /C:"Direcci.n IPv4"') do (
    set "ip=%%a"
    set "ip=!ip: =!"
    if not "!ip!"=="" (
        echo   https://!ip!:8443
        echo.
        echo Los codigos QR apuntan a https://...:8443.
        echo Para detener el servidor, cierre la ventana que se abrio.
        goto :done
    )
)
echo   https://localhost:8443
echo.
echo Los codigos QR apuntan a https://...:8443.
goto :done

:notfound
echo ========================================
echo  Ejecutable no encontrado.
echo.
echo  Para construirlo ejecute build.bat desde
echo  la raiz del proyecto (asistencia-qr).
echo ========================================

:done
pause
