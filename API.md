# API de AlquilaYa

Todas las rutas usan JSON y están bajo `/api`. Los recursos disponibles son:

| Recurso | Ruta | Operaciones |
| --- | --- | --- |
| Clientes | `/clientes` | GET lista, GET `/{id}`, POST, PUT `/{id}`, DELETE `/{id}` |
| Vehículos | `/vehiculos` | GET lista, GET `/{id}`, POST, PUT `/{id}`, DELETE `/{id}` |
| Oficinas | `/oficinas` | GET lista, GET `/{id}`, POST, PUT `/{id}`, DELETE `/{id}` |
| Alquileres | `/alquileres` | GET lista, GET `/{id}`, POST, PUT `/{id}`, DELETE `/{id}` |
| Mantenimientos | `/mantenimientos` | GET lista, GET `/{id}`, POST, PUT `/{id}`, DELETE `/{id}` |

Los `POST` responden `201 Created`, incluyen el recurso creado y su URI en el
encabezado `Location`. Los `PUT` responden `200 OK`; los `DELETE`, `204 No
Content`. Las búsquedas de recursos inexistentes responden `404 Not Found`.
Una solicitud inválida responde `400 Bad Request` con `title`, `detail` y un
mapa `errors` cuando aplica. Los conflictos de unicidad o integridad responden
`409 Conflict`.

## Ejemplos de solicitudes

`POST /api/clientes`

```json
{
  "nombre": "Ana",
  "apellido": "Pérez",
  "email": "ana@example.com",
  "licencia": "LIC-12345",
  "telefono": "+525512345678"
}
```

`POST /api/oficinas`

```json
{
  "nombreOficina": "Centro",
  "direccion": "Av. Central 123",
  "ciudad": "Ciudad de México",
  "region": "CDMX"
}
```

`POST /api/vehiculos`

```json
{
  "marca": "Toyota",
  "modelo": "Corolla",
  "anio": 2024,
  "placa": "ABC123",
  "tarifaDiaria": 850.0,
  "estado": "DISPONIBLE",
  "oficinaId": 1
}
```

`POST /api/alquileres`

```json
{
  "vehiculoId": 1,
  "clienteId": 1,
  "fechaInicio": "2026-11-01",
  "fechaFin": "2026-11-04",
  "estado": "PENDIENTE"
}
```

`precioTotal` se calcula en el servidor y no se recibe en la solicitud.
`fechaFin` debe ser posterior a `fechaInicio`.

`POST /api/mantenimientos`

```json
{
  "vehiculoId": 1,
  "fechaMantenimiento": "2026-10-10",
  "descripcion": "Servicio preventivo",
  "costo": 1200.0
}
```

Las operaciones `PUT` aceptan los mismos campos que sus respectivos `POST`;
`id` siempre se toma de la ruta. Los campos relacionados usan identificadores,
no objetos JPA anidados. Los estados válidos son `DISPONIBLE`, `ALQUILADO` y
`MANTENIMIENTO` para vehículos, y `PENDIENTE`, `ACTIVO` y `COMPLETADO` para
alquileres.

## Reglas de alquiler y mantenimiento

Los alquileres pendientes y activos bloquean el periodo reservado. La fecha
de fin es exclusiva: una reserva puede comenzar el día en que termina otra.
Crear o actualizar una reserva coincidente devuelve `409 Conflict`; los
alquileres completados no bloquean periodos. Un vehículo en mantenimiento
no admite reservas pendientes ni activaciones.

Un vehículo solo puede tener un alquiler activo a la vez, incluso si las
fechas de los contratos no coinciden. Activar un alquiler cambia el vehículo
a `ALQUILADO`; completarlo o eliminarlo lo devuelve a `DISPONIBLE`, salvo
que esté en mantenimiento. Cambiar el vehículo de un contrato activo libera
el anterior y ocupa el nuevo dentro de la misma transacción. Los bloqueos de
base de datos evitan que dos solicitudes simultáneas reserven el mismo periodo.

La descripción de mantenimiento admite hasta 1000 caracteres tanto en la
validación de entrada como en la columna MySQL. Una descripción de 1001
caracteres devuelve `400 Bad Request`.

## Pruebas HTTP con MySQL

Usar una base aislada, nunca una base con datos reales. Crear
`alquilaya_pruebas` en MySQL y configurar estas variables en PowerShell:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
$env:DB_URL = 'jdbc:mysql://localhost:3306/alquilaya_pruebas'
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = '<contraseña local>'
.\mvnw.cmd -DskipTests package
& "$env:JAVA_HOME\bin\java.exe" -jar target\alquilaya-0.0.1-SNAPSHOT.jar --server.port=18081
```

Cuando la API esté lista, ejecutar en otra terminal con Python 3:

```powershell
python tests\crud_mysql.py http://localhost:18081 reporte-crud.json
```

El script comprueba CRUD, restricciones únicas, errores, relaciones,
disponibilidad, solapamientos, concurrencia y longitud de descripción. Elimina
los registros creados por su ejecución y guarda un informe JSON. No requiere login.
