@echo off
setlocal
cd /d "%~dp0backend"
title Lab10 Series - Servidor Django
if not exist ".venv\Scripts\python.exe" (
    py -3 -m venv .venv
    if errorlevel 1 goto error
)
".venv\Scripts\python.exe" -c "import django, rest_framework" >nul 2>&1
if errorlevel 1 (
    ".venv\Scripts\python.exe" -m pip install -r requirements.txt
    if errorlevel 1 goto error
)
".venv\Scripts\python.exe" -c "import json,urllib.request; data=json.load(urllib.request.urlopen('http://127.0.0.1:8000/',timeout=2)); raise SystemExit(0 if data.get('mensaje')=='servidor activo' else 1)" >nul 2>&1
if not errorlevel 1 (
    echo Django ya esta activo en el puerto 8000. Puedes ejecutar Lab10 Series.
    pause
    exit /b 0
)
".venv\Scripts\python.exe" manage.py migrate --noinput
if errorlevel 1 goto error
echo.
echo Servidor de Lab10 Series. Deja esta ventana abierta mientras usas la app.
echo En Android Studio ejecuta Lab10_Series. En la app puedes pulsar Reintentar.
echo Para detener el servidor pulsa Ctrl+C.
echo.
".venv\Scripts\python.exe" manage.py runserver 0.0.0.0:8000 --noreload
if errorlevel 1 goto error
exit /b 0
:error
echo.
echo No se pudo iniciar Django. Revisa el mensaje anterior.
pause
exit /b 1
