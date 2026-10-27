# Lab10 Series

Proyecto independiente de la parte guiada. Abrir esta carpeta en Android Studio y ejecutar la configuración app. Solo incluye la aplicación com.tecsup.lab10.

Antes de usar la app, abre Iniciar_servidor.cmd con doble clic y deja esa ventana abierta. El archivo prepara un entorno Python local, instala los requisitos si hace falta, aplica las migraciones y arranca Django. La primera preparación requiere Internet. El emulador accede por http://10.0.2.2:8000/.

Para crear una serie escribe un nombre, una fecha AAAA-MM-DD, un rating entero mayor o igual a cero y una categoría: horror, comedy, action o drama. El formulario conserva los datos si hay un error y solo vuelve a la lista después de que Django confirme el guardado. Si Django está apagado, la app muestra un mensaje con Reintentar y permanece abierta.

El código original del enunciado está conservado en referencia/ScreenSeries_enunciado.kt.txt y en el informe. La versión ejecutable agrega validación, comprobación de respuestas y manejo de errores para evitar los cierres detectados con un rating no numérico y con el servidor apagado.

Usar Java 17. No requiere abrir Bookshelf.
