@echo off
set APP_NAME=jframework
set SRC_DIR=src
set BUILD_DIR=build
set LIB_DIR=lib
set PROJECT_DEPLOY="D:\IT University\S5\Framework Mr Naina\Test Framework"
set "PLACE_JAR=%PROJECT_DEPLOY%\lib"
set "PATH_JAR_PROJECT=%PLACE_JAR%\%APP_NAME%.jar"

REM Suppression et recreation du dossier temporaire
if exist %BUILD_DIR% rmdir /s /q %BUILD_DIR%


REM Compilation des fichiers Java avec le JAR des Servlets et autres dependances
dir /s /b %SRC_DIR%\*.java > sources.txt

for /f "usebackq delims=" %%f in ("sources.txt") do (
    javac -cp "%BUILD_DIR%;%LIB_DIR%\*"; -d "%BUILD_DIR%" "%%f"
)
@REM del sources.txt

REM Copier les fichiers web

REM Creation du fichier .war dans le dossier build
cd %BUILD_DIR%
jar -cvf %APP_NAME%.jar *
cd ..

REM Deploiement vers Tomcat
if exist %PATH_JAR_PROJECT% del %PATH_JAR_PROJECT%
copy %BUILD_DIR%\%APP_NAME%.jar %PROJECT_DEPLOY%\lib

echo Creation framework termine avec succes.