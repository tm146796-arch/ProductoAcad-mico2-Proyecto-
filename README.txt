# cordero_pa2

Aplicacion Android para registrar y administrar solicitudes de soporte tecnico.
El proyecto fue desarrollado en Java y utiliza SQLite para guardar la informacion
de forma local en el dispositivo.

## Descripcion

La aplicacion permite llevar un control sencillo de solicitudes de soporte. Cada
registro incluye el nombre del solicitante, el problema reportado, la prioridad y
el estado de atencion.

## Funcionalidades

- Registrar nuevas solicitudes de soporte.
- Listar las solicitudes guardadas.
- Actualizar una solicitud existente mediante su ID.
- Eliminar solicitudes con confirmacion previa.
- Validar campos obligatorios antes de guardar o actualizar.
- Guardar los datos localmente con SQLite.

## Tecnologias utilizadas

- Java
- Android SDK
- SQLite
- Gradle

## Datos principales

- Package: cordero.pa2.org
- Base de datos: soporte.db
- Tabla: solicitudes
- Min SDK: 23
- Target SDK: 35

## Estructura del proyecto

```text
app/src/main/java/cordero/pa2/org/
  DatabaseHelper.java
  MainActivity.java

app/src/main/res/layout/
  activity_main.xml

app/src/main/res/values/
  styles.xml
```

## Como abrir el proyecto

1. Abrir Android Studio.
2. Seleccionar la opcion Open.
3. Elegir la carpeta cordero_pa2.
4. Esperar a que Gradle sincronice el proyecto.
5. Ejecutar la aplicacion en un emulador o dispositivo Android.

## Uso basico

1. Completar los campos de solicitante, problema, prioridad y estado.
2. Presionar Guardar para crear una solicitud.
3. Presionar Listar para ver los registros guardados.
4. Ingresar el ID de una solicitud para actualizarla o eliminarla.

## Notas

Si Android Studio solicita actualizar Gradle o instalar componentes del SDK,
aceptar la instalacion para poder compilar el proyecto correctamente.
