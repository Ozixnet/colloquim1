@echo off
setlocal
rem Запуск из каталога collocv (рядом с gradlew.bat).
rem   run-windows.bat        — только тесты
rem   run-windows.bat jar    — clean + test + jar для WiseTask
cd /d "%~dp0"

if /i "%~1"=="jar" (
  echo [Gradle] clean test jar
  call gradlew.bat clean test jar
) else (
  echo [Gradle] test
  call gradlew.bat test
)

set "GRADLE_EXIT=%ERRORLEVEL%"
if not "%GRADLE_EXIT%"=="0" (
  echo.
  echo Ошибка сборки, код: %GRADLE_EXIT%
)
exit /b %GRADLE_EXIT%
