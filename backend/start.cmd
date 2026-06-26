@echo off
for /f "tokens=5" %%a in ('netstat -ano ^| find ":8081" ^| find "LISTENING"') do (
  taskkill /F /PID %%a >nul 2>&1
  echo Matando proceso PID %%a
)
echo Iniciando backend...
mvn spring-boot:run
