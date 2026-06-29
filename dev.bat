@echo off
echo ========================================
echo  Asistencia QR - Modo Desarrollo
echo ========================================
echo.
echo [1/2] Iniciando backend (puerto 8081, perfil dev)...
start "Backend" cmd /c "cd /d %~dp0backend ^& mvn spring-boot:run -Dspring-boot.run.profiles=dev"
echo.
echo Esperando 10 segundos para que el backend arranque...
timeout /t 10 /nobreak >nul
echo.
echo [2/2] Iniciando frontend (puerto 4200)...
start "Frontend" cmd /c "cd /d %~dp0frontend ^& npx ng serve --ssl --host 0.0.0.0 --proxy-config proxy.conf.json"
echo.
echo ========================================
echo  Frontend: https://localhost:4200
echo  Backend:  http://localhost:8081
echo  H2 Console: http://localhost:8081/h2-console
echo ========================================
echo.
echo Para detener, cierre las ventanas que se abrieron.
pause
