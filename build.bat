@echo off
setlocal enabledelayedexpansion

echo ========================================
echo  Asistencia QR - Build Script
echo ========================================
echo.

echo [1/3] Building Angular frontend...
cd frontend
call npx ng build --configuration production --output-path ..\backend\src\main\resources\static
if %ERRORLEVEL% neq 0 (
    echo ERROR: Angular build failed
    exit /b 1
)
echo Frontend built successfully
echo.

:: Angular 20+ outputs to static/browser/ -- flatten to static/
if exist ..\backend\src\main\resources\static\browser (
    echo Flattening Angular output...
    xcopy /E /Y ..\backend\src\main\resources\static\browser\* ..\backend\src\main\resources\static\ >nul
    rmdir /S /Q ..\backend\src\main\resources\static\browser >nul
)

echo [2/3] Static resources ready at backend\src\main\resources\static\
cd ..\backend
echo.

echo [3/3] Building Spring Boot backend and packaging .exe...

:: Ensure JAVA_HOME is set for Launch4j to find the JDK
if not defined JAVA_HOME (
    for /f "delims=" %%I in ('where java 2^>nul') do set "JAVA_PATH=%%I"
    if defined JAVA_PATH (
        for %%J in ("!JAVA_PATH!") do set "JAVA_HOME=%%~dpJ.."
    ) else if exist "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot" (
        set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
    ) else if exist "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.9-hotspot" (
        set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.9.9-hotspot"
    )
)
if not defined JAVA_HOME (
    echo WARNING: JAVA_HOME not set. Launch4j may not find the JDK.
    echo Set JAVA_HOME manually if the .exe fails to find Java.
)
echo JAVA_HOME=!JAVA_HOME!

call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven build failed
    exit /b 1
)
echo.

echo Copying start.cmd to target directory...
copy /Y start.cmd target\start.cmd >nul

echo ========================================
echo  Build complete!
echo  Output: backend\target\asistencia-qr.exe
echo  Launcher: backend\target\start.cmd
echo ========================================
pause
