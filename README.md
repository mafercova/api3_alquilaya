# AlquilaYa API

Este proyecto es una API para una empresa de alquiler de vehículos llamada
AlquilaYa. Se pueden registrar clientes, oficinas, vehículos, alquileres y
mantenimientos, además de consultar, editar y borrar sus datos.

El precio del alquiler se calcula automáticamente. También se revisa que el
vehículo no esté en mantenimiento ni tenga otra reserva en las mismas fechas.
No tiene login.

## Qué se usó

- Java 21 y Spring Boot 4.1.1.
- Spring Web para las peticiones.
- Spring Data JPA para trabajar con la base de datos.
- MySQL y su driver.
- Bean Validation para validar los datos.
- Maven y JUnit para compilar y hacer pruebas.
- Python 3 para el script que prueba los endpoints.

Las clases están separadas en modelos, repositorios, servicios y controladores.
También hay carpetas para los datos de las peticiones, los errores y la configuración.

## Cómo ejecutarlo

1. Tener instalado Java 21 y MySQL. MySQL debe estar encendido.
2. Abrir una terminal en la carpeta donde están `pom.xml` y `mvnw.cmd`.
3. Crear la base de datos en MySQL Workbench o en la consola de MySQL:

```sql
CREATE DATABASE IF NOT EXISTS alquilaya CHARACTER SET utf8mb4;
```

4. Poner estas variables en PowerShell, cambiando lo que está entre `< >`
por los datos de tu equipo:

```powershell
$env:JAVA_HOME = '<ruta del JDK 21>'
$env:DB_URL = 'jdbc:mysql://localhost:3306/alquilaya'
$env:DB_USERNAME = '<usuario MySQL>'
$env:DB_PASSWORD = '<contraseña MySQL>'
```

5. Ejecutar este comando:

```powershell
.\mvnw.cmd spring-boot:run
```

Otra forma de ejecutarlo es generar el JAR:

```powershell
.\mvnw.cmd -DskipTests package
& "$env:JAVA_HOME\bin\java.exe" -jar target\alquilaya-0.0.1-SNAPSHOT.jar
```

En Linux o macOS se usa `export` para las variables y `./mvnw` para Maven.
La primera vez se necesita Internet para descargar las dependencias.
Las tablas se crean o actualizan al iniciar, pero primero hay que crear la
base de datos y dar permisos al usuario de MySQL.

La API queda en `http://localhost:8080/api`. Para ver si está funcionando:

```powershell
curl.exe http://localhost:8080/api/oficinas
```

Si no hay oficinas registradas, devuelve `[]` con código `200`.
Si falla la conexión a MySQL, revisar el usuario, la contraseña y que el servicio esté encendido.

## Variables de entorno

| Variable | Necesaria | Descripción |
| --- | --- | --- |
| `JAVA_HOME` | Para Maven | Ruta del JDK 21 |
| `DB_URL` | Opcional | URL JDBC; por defecto `jdbc:mysql://localhost:3306/alquilaya` |
| `DB_USERNAME` | Sí | Usuario MySQL, sin valor fijo en el código |
| `DB_PASSWORD` | Sí | Contraseña MySQL, sin valor fijo en el código |
| `SERVER_PORT` | Opcional | Puerto HTTP; por defecto `8080` |

Las variables se ponen en la terminal o en el IDE; el proyecto no lee `.env`
automáticamente. No se deben subir las contraseñas.

Quedó código auxiliar para crear usuarios, pero está apagado y no hace falta
para usar la API. Solo se activa con `SEED_USERS_ENABLED=true` y las variables
`ADMIN_EMAIL`, `ADMIN_PASSWORD`, `OPERADOR_EMAIL` y `OPERADOR_PASSWORD`.
Esto no agrega un login.

## Endpoints

Todas estas rutas empiezan con `/api` y trabajan con JSON.

| Recurso | Listar | Consultar | Crear | Actualizar | Eliminar |
| --- | --- | --- | --- | --- | --- |
| Clientes | `GET /clientes` | `GET /clientes/{id}` | `POST /clientes` | `PUT /clientes/{id}` | `DELETE /clientes/{id}` |
| Oficinas | `GET /oficinas` | `GET /oficinas/{id}` | `POST /oficinas` | `PUT /oficinas/{id}` | `DELETE /oficinas/{id}` |
| Vehículos | `GET /vehiculos` | `GET /vehiculos/{id}` | `POST /vehiculos` | `PUT /vehiculos/{id}` | `DELETE /vehiculos/{id}` |
| Alquileres | `GET /alquileres` | `GET /alquileres/{id}` | `POST /alquileres` | `PUT /alquileres/{id}` | `DELETE /alquileres/{id}` |
| Mantenimientos | `GET /mantenimientos` | `GET /mantenimientos/{id}` | `POST /mantenimientos` | `PUT /mantenimientos/{id}` | `DELETE /mantenimientos/{id}` |

Al crear se devuelve `201` con la dirección del registro en `Location`.
Al consultar o editar se devuelve `200`, y al borrar `204` sin contenido.
Para editar con `PUT` hay que mandar todos los campos, igual que al crear.

## Ejemplos

Se pueden probar en Postman usando `Content-Type: application/json`.
Primero hay que crear una oficina y un cliente, luego un vehículo.
Los IDs de los ejemplos son de muestra; hay que usar los que devuelva la API.

### Oficina

`POST /api/oficinas`:

```json
{"nombreOficina":"Centro","direccion":"Calle 123","ciudad":"Ciudad de México","region":"CDMX"}
```

Respuesta `201`, `Location: /api/oficinas/1`:

```json
{"id":1,"nombreOficina":"Centro","direccion":"Calle 123","ciudad":"Ciudad de México","region":"CDMX"}
```

### Cliente

`POST /api/clientes`:

```json
{"nombre":"Ana","apellido":"Pérez","email":"ana@example.com","licencia":"LIC-12345","telefono":"+525512345678"}
```

Respuesta `201`, `Location: /api/clientes/1`:

```json
{"id":1,"nombre":"Ana","apellido":"Pérez","email":"ana@example.com","licencia":"LIC-12345","telefono":"+525512345678"}
```

### Vehículo

`POST /api/vehiculos`:

```json
{"marca":"Toyota","modelo":"Corolla","anio":2024,"placa":"ABC123","tarifaDiaria":850.0,"estado":"DISPONIBLE","oficinaId":1}
```

Respuesta `201`, `Location: /api/vehiculos/1`:

```json
{"id":1,"marca":"Toyota","modelo":"Corolla","anio":2024,"placa":"ABC123","tarifaDiaria":850.0,"estado":"DISPONIBLE","oficina":{"id":1,"nombreOficina":"Centro","direccion":"Calle 123","ciudad":"Ciudad de México","region":"CDMX"}}
```

### Alquiler

`POST /api/alquileres`:

```json
{"vehiculoId":1,"clienteId":1,"fechaInicio":"2027-01-01","fechaFin":"2027-01-04","estado":"PENDIENTE"}
```

Respuesta `201`, `Location: /api/alquileres/1`:

```json
{"id":1,"vehiculoId":1,"clienteId":1,"fechaInicio":"2027-01-01","fechaFin":"2027-01-04","precioTotal":2550.0,"estado":"PENDIENTE"}
```

El precio se calcula como tarifa diaria por número de días. No enviar
`precioTotal` en la solicitud. Para activar este contrato, enviar a
`PUT /api/alquileres/1` el mismo cuerpo con `"estado":"ACTIVO"`. La respuesta
es `200` y el vehículo pasa a `ALQUILADO`. Completarlo usa `"estado":"COMPLETADO"`.

### Mantenimiento

`POST /api/mantenimientos`:

```json
{"vehiculoId":1,"fechaMantenimiento":"2026-10-13","descripcion":"Revisión preventiva","costo":1200.0}
```

Respuesta `201`, `Location: /api/mantenimientos/1`:

```json
{"id":1,"vehiculoId":1,"fechaMantenimiento":"2026-10-13","descripcion":"Revisión preventiva","costo":1200.0}
```

### Consulta y eliminación

`GET /api/clientes/1` devuelve el objeto de cliente; `GET /api/clientes`
devuelve una lista de esos objetos. `DELETE /api/mantenimientos/1` devuelve
`204` sin cuerpo. Consultarlo después devuelve `404`.

### Errores

Consultar un cliente inexistente devuelve `404` con un cuerpo de esta forma
(el campo `instance` puede incluir la ruta):

```json
{"type":"about:blank","title":"Not Found","status":404,"detail":"Cliente no encontrado","instance":"/api/clientes/999999"}
```

Un campo inválido devuelve `400`; los errores de validación del cuerpo incluyen
un mapa `errors` por campo. JSON mal formado, estados desconocidos y fechas
inválidas también se rechazan. Duplicar un correo, licencia o placa, reservar
un periodo ocupado o eliminar un recurso con referencias devuelve `409`:

```json
{"type":"about:blank","title":"Conflict","status":409,"detail":"El vehículo tiene un alquiler en ese periodo","instance":"/api/alquileres"}
```

## Algunas validaciones

- Campos obligatorios, IDs positivos, email y teléfono válidos.
- Placa, email y licencia únicos. Las relaciones deben apuntar a registros existentes.
- Año entre 1886 y 2100, tarifa positiva y costo de mantenimiento no negativo.
- Descripción de mantenimiento de 1 a 1000 caracteres, con el mismo límite en MySQL.
- Fin del alquiler posterior al inicio. La fecha de fin es exclusiva y permite reservas contiguas.
- Los alquileres pendientes y activos impiden reservas solapadas; los completados no bloquean periodos.
- Vehículos en mantenimiento no admiten nuevos alquileres pendientes o activos.
- Solo un alquiler activo por vehículo. Activar, completar, eliminar o cambiar el vehículo del contrato sincroniza su estado, respetando mantenimiento.
- Si llegan dos reservas a la vez para el mismo periodo, solo se acepta una.

Estados de vehículo: `DISPONIBLE`, `ALQUILADO`, `MANTENIMIENTO`.
Estados de alquiler: `PENDIENTE`, `ACTIVO`, `COMPLETADO`.
Registrar un historial de mantenimiento no cambia automáticamente el estado
del vehículo; ese estado se modifica mediante `PUT /api/vehiculos/{id}`.

## Pruebas

Para probar sin tocar otros datos, crear la base `alquilaya_pruebas` y cambiar
`DB_URL` a `jdbc:mysql://localhost:3306/alquilaya_pruebas`.
Las pruebas de Java se ejecutan con:

```powershell
.\mvnw.cmd test
```

Para las pruebas HTTP, arrancar la API en esa base y ejecutar:

```powershell
python tests\crud_mysql.py http://localhost:8080 reporte-crud.json
```

El script crea datos para probar y los borra al terminar. Revisa los cinco CRUD,
datos inválidos, duplicados y alquileres. Usarlo solo en la base de pruebas.
Se probó con MySQL 8.0.46: pasaron 175 comprobaciones HTTP y 3 pruebas Java.

## Qué incluir en la entrega

Dejar `src/`, `tests/`, `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/wrapper/`,
`.gitignore`, `.gitattributes`, este README y `API.md`.

No incluir `target/`, archivos del IDE, `.env`, configuración local, logs,
reportes de pruebas, cachés Python ni copias de la base de datos.
Estos archivos están en `.gitignore`, pero si se hace un ZIP hay que quitarlos
de la copia que se va a entregar. Tampoco incluir contraseñas reales o tokens.

Hay más ejemplos y detalles en [API.md](API.md).
