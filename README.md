# AlquilaYa API

API REST para gestionar una empresa de alquiler de vehículos. Permite crear,
consultar, actualizar y eliminar clientes, oficinas, vehículos, alquileres e
historiales de mantenimiento. Calcula automáticamente el precio del alquiler
y controla reservas coincidentes y disponibilidad. Los endpoints son públicos;
el entregable no implementa login.

## Tecnologías

- Java 21 y Spring Boot 4.1.1.
- Spring Web MVC y JSON.
- Spring Data JPA / Hibernate y MySQL Connector/J.
- MySQL (verificado con 8.0.46).
- Jakarta Bean Validation y manejo de errores con `ProblemDetail`.
- Maven Wrapper y JUnit 5. Python 3 para las pruebas HTTP opcionales.

El código se organiza en `models`, `repositories`, `services`, `controllers`,
`dto`, `exceptions` y `config`. Los controladores reciben DTOs validados y
delegan la lógica a servicios transaccionales; los repositorios acceden a MySQL.

## Instalación y ejecución

1. Instalar JDK 21 y MySQL y arrancar el servicio de MySQL.
2. Abrir una terminal en la carpeta que contiene `pom.xml` y `mvnw.cmd`.
3. Crear la base de datos desde MySQL Workbench o el cliente MySQL:

```sql
CREATE DATABASE IF NOT EXISTS alquilaya CHARACTER SET utf8mb4;
```

4. Configurar las variables en la misma terminal PowerShell. Sustituir los
marcadores por los valores locales; no guardarlos en archivos para entregar:

```powershell
$env:JAVA_HOME = '<ruta del JDK 21>'
$env:DB_URL = 'jdbc:mysql://localhost:3306/alquilaya'
$env:DB_USERNAME = '<usuario MySQL>'
$env:DB_PASSWORD = '<contraseña MySQL>'
```

5. Ejecutar la API:

```powershell
.\mvnw.cmd spring-boot:run
```

También se puede generar y ejecutar el JAR:

```powershell
.\mvnw.cmd -DskipTests package
& "$env:JAVA_HOME\bin\java.exe" -jar target\alquilaya-0.0.1-SNAPSHOT.jar
```

En Linux/macOS, usar `export` para las variables y `./mvnw` para Maven.
El primer uso del wrapper requiere Internet para descargar dependencias.
Hibernate crea o actualiza las tablas (`ddl-auto=update`); la base debe existir
y el usuario debe tener permisos para modificar su esquema.

La URL base es `http://localhost:8080/api`. Una comprobación inicial es:

```powershell
curl.exe http://localhost:8080/api/oficinas
```

Una base vacía devuelve `200 OK` con `[]`. Un error de acceso a MySQL requiere
revisar el servicio, la URL, las credenciales y los permisos de ese usuario.

## Variables de entorno

| Variable | Necesaria | Descripción |
| --- | --- | --- |
| `JAVA_HOME` | Para Maven | Ruta del JDK 21 |
| `DB_URL` | Opcional | URL JDBC; por defecto `jdbc:mysql://localhost:3306/alquilaya` |
| `DB_USERNAME` | Sí | Usuario MySQL, sin valor fijo en el código |
| `DB_PASSWORD` | Sí | Contraseña MySQL, sin valor fijo en el código |
| `SERVER_PORT` | Opcional | Puerto HTTP; por defecto `8080` |

No se carga `.env` automáticamente. Configurar las variables en la terminal o
en la configuración privada del IDE. La creación automática de usuarios está
desactivada por defecto y no se necesita para la API. El código auxiliar solo
la activa si `SEED_USERS_ENABLED=true`; en ese caso requiere `ADMIN_EMAIL`,
`ADMIN_PASSWORD`, `OPERADOR_EMAIL` y `OPERADOR_PASSWORD` locales. No proporciona
un endpoint de login.

## Endpoints

Todas las rutas siguientes incluyen el prefijo `/api` y usan JSON.

| Recurso | Listar | Consultar | Crear | Actualizar | Eliminar |
| --- | --- | --- | --- | --- | --- |
| Clientes | `GET /clientes` | `GET /clientes/{id}` | `POST /clientes` | `PUT /clientes/{id}` | `DELETE /clientes/{id}` |
| Oficinas | `GET /oficinas` | `GET /oficinas/{id}` | `POST /oficinas` | `PUT /oficinas/{id}` | `DELETE /oficinas/{id}` |
| Vehículos | `GET /vehiculos` | `GET /vehiculos/{id}` | `POST /vehiculos` | `PUT /vehiculos/{id}` | `DELETE /vehiculos/{id}` |
| Alquileres | `GET /alquileres` | `GET /alquileres/{id}` | `POST /alquileres` | `PUT /alquileres/{id}` | `DELETE /alquileres/{id}` |
| Mantenimientos | `GET /mantenimientos` | `GET /mantenimientos/{id}` | `POST /mantenimientos` | `PUT /mantenimientos/{id}` | `DELETE /mantenimientos/{id}` |

`POST` devuelve `201 Created` y `Location`; `GET` y `PUT`, `200 OK`;
`DELETE`, `204 No Content` sin cuerpo. `PUT` requiere todos los campos de la
solicitud, igual que `POST`; el identificador se toma de la ruta.

## Ejemplos de peticiones y respuestas

Enviar `Content-Type: application/json` en `POST` y `PUT`. Los identificadores
siguientes son ilustrativos: usar los devueltos por la API. Crear primero una
oficina y un cliente; después un vehículo y sus alquileres/mantenimientos.

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

## Validaciones y reglas de negocio

- Campos obligatorios, IDs positivos, email y teléfono válidos.
- Placa, email y licencia únicos. Las relaciones deben apuntar a registros existentes.
- Año entre 1886 y 2100, tarifa positiva y costo de mantenimiento no negativo.
- Descripción de mantenimiento de 1 a 1000 caracteres, con el mismo límite en MySQL.
- Fin del alquiler posterior al inicio. La fecha de fin es exclusiva y permite reservas contiguas.
- Los alquileres pendientes y activos impiden reservas solapadas; los completados no bloquean periodos.
- Vehículos en mantenimiento no admiten nuevos alquileres pendientes o activos.
- Solo un alquiler activo por vehículo. Activar, completar, eliminar o cambiar el vehículo del contrato sincroniza su estado, respetando mantenimiento.
- Bloqueos transaccionales protegen las reservas simultáneas.

Estados de vehículo: `DISPONIBLE`, `ALQUILADO`, `MANTENIMIENTO`.
Estados de alquiler: `PENDIENTE`, `ACTIVO`, `COMPLETADO`.
Registrar un historial de mantenimiento no cambia automáticamente el estado
del vehículo; ese estado se modifica mediante `PUT /api/vehiculos/{id}`.

## Pruebas

Para las pruebas, crear una base separada `alquilaya_pruebas` y cambiar `DB_URL`
a `jdbc:mysql://localhost:3306/alquilaya_pruebas`. Con las credenciales locales
configuradas, ejecutar las pruebas Java:

```powershell
.\mvnw.cmd test
```

Para las pruebas HTTP, arrancar la API en esa base y ejecutar:

```powershell
python tests\crud_mysql.py http://localhost:8080 reporte-crud.json
```

El script crea sus propios registros, prueba los cinco CRUD, errores,
duplicados, concurrencia y estados, y elimina los registros creados al terminar.
No ejecutarlo contra datos reales. La verificación realizada con MySQL 8.0.46
obtuvo 175 comprobaciones HTTP y 3 pruebas Java aprobadas.

## Archivos del entregable

Incluir `src/`, `tests/`, `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/wrapper/`,
`.gitignore`, `.gitattributes`, este README y `API.md`.

Excluir `target/`, archivos del IDE, `.env`, configuración local, logs,
reportes generados, cachés Python y volcados de base de datos. `.gitignore`
contiene esas exclusiones; al crear un ZIP hay que respetarlas manualmente.
No incluir contraseñas reales, tokens ni archivos con datos de usuarios.
El proyecto no contiene credenciales fijas en `application.properties`.

Para más detalles de las solicitudes y las reglas, consultar [API.md](API.md).
