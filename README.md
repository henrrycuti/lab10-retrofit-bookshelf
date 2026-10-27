# Laboratorio 10
Henrry Manuel Cuti Sencia — Programación en Móviles — TECSUP.

`app`: parte guiada Lab10. Los bloques de Kotlin se copiaron sin cambios del enunciado. `ScreenInicio` y `MainActivity` completan los archivos que no proporciona la guía.
`bookshelf`: proyecto adicional Google Books con Retrofit, Gson, Coil, repositorio y pruebas falsas.
`backend`: servidor Django de series para uso local; ejecutar migraciones y `python manage.py runserver 0.0.0.0:8000`.
En Android Emulator, `10.0.2.2` permite acceder al servidor de la computadora.
El ejercicio de desarrollo se integra en henrrycuti/umbra-unity, rama codex/lab10-api.

Verificación del 27 de octubre de 2026: ambas apps compilan, las 4 pruebas Bookshelf pasan y Series permite GET, POST, PUT y DELETE en el emulador Pixel 7. Google Books devuelve HTTP 429 por cuota; se verificó el error y el botón Reintentar. No se afirma una descarga de portadas exitosa.
