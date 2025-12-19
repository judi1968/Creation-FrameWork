@echo off
setlocal enabledelayedexpansion

REM ====== CHEMIN DU PROJET SANS ESPACES ======
for %%i in ("%CD%") do set "PROJECT_DIR=%%~fsi"

set "APP_NAME=jframework"
set "SRC_DIR=%PROJECT_DIR%\src"
set "BUILD_DIR=%PROJECT_DIR%\build"
set "LIB_DIR=%PROJECT_DIR%\lib"

REM Le projet qui doit recevoir le .jar
set "PROJECT_DEPLOY=D:\IT University\S5\Framework Mr Naina\Test Framework"
for %%i in ("%PROJECT_DEPLOY%") do set "PROJECT_DEPLOY_FIX=%%~fsi"

set "PLACE_JAR=%PROJECT_DEPLOY_FIX%\lib"
set "PATH_JAR_PROJECT=%PLACE_JAR%\%APP_NAME%.jar"

echo.
echo ============================================
echo   Projet : %PROJECT_DIR%
echo   Deploy : %PROJECT_DEPLOY_FIX%
echo ============================================
echo.

REM ====== RESET BUILD ======
echo Nettoyage du dossier build...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"

REM ====== GENERATION FICHIER LISTE ======
echo Recherche des fichiers Java...
dir /s /b "%SRC_DIR%\*.java" > "%PROJECT_DIR%\sources.txt"

REM ====== COMPILATION ======
echo.
echo ============================================
echo   Compilation en cours...
echo ============================================
echo.

javac -cp "%LIB_DIR%\*;%BUILD_DIR%" ^
      -d "%BUILD_DIR%" ^
      -parameters @"%PROJECT_DIR%\sources.txt"

if errorlevel 1 (
    echo ERREUR LORS DE LA COMPILATION !
    exit /b 1
)

del "%PROJECT_DIR%\sources.txt"

REM ====== CREATION JAR ======
echo.
echo ============================================
echo   Creation du JAR...
echo ============================================
echo.

cd "%BUILD_DIR%"
jar -cvf "%APP_NAME%.jar" *
cd "%PROJECT_DIR%"

REM ====== COPIE VERS LE PROJET ======
echo.
echo ============================================
echo   Copie vers le projet parent...
echo ============================================
echo.

if exist "%PATH_JAR_PROJECT%" del "%PATH_JAR_PROJECT%"
copy "%BUILD_DIR%\%APP_NAME%.jar" "%PLACE_JAR%"

echo.
echo ============================================
echo    Framework genere et copie avec succes !
echo ============================================

endlocal
