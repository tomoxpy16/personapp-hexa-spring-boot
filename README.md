# PersonApp — Servicio web con Arquitectura Hexagonal

Laboratorio 2 de Arquitectura de Software, Pontificia Universidad Javeriana.

Servicio web que expone el CRUD del modelo **persona / profesion / estudios / telefono** usando
**Arquitectura Hexagonal** (puertos y adaptadores) con los patrones **Repository** y **Service**.
Tiene dos adaptadores de entrada (**API REST** documentada con Swagger y **CLI** de consola) y
dos de salida (**MariaDB** y **MongoDB**). En cada operación se elige el motor de base de datos.
Además, la API sirve una **interfaz web** sencilla que consume esos mismos endpoints.

Basado en la plantilla [andres-karoll/personapp-hexa-spring-boot](https://github.com/andres-karoll/personapp-hexa-spring-boot).

## Stack

| Componente | Versión |
|---|---|
| JDK | 21 (LTS) |
| Spring Boot | 4.1.2 (Spring Framework 7, Hibernate 7) |
| Swagger / OpenAPI 3 (springdoc-openapi) | 3.2.0 |
| Lombok | 1.18.48 |
| MariaDB | 11.4 |
| MongoDB | 8.0 |
| Maven | 3.9 o superior |

## Arquitectura

```
   Adaptadores de entrada                                   Adaptadores de salida
 ┌──────────────────────┐                                ┌──────────────────────────┐
 │ rest-input-adapter   │                                │ maria-output-adapter     │
 │ Controller → Adapter │──┐   ┌───────────────────┐  ┌─▶│ Adapter → Mapper → JPA   │
 └──────────────────────┘  ├──▶│ application       │──┤  └──────────────────────────┘
 ┌──────────────────────┐  │   │ InputPort/UseCase │  │  ┌──────────────────────────┐
 │ cli-input-adapter    │──┘   │ OutputPort        │  └─▶│ mongo-output-adapter     │
 │ Menu → Adapter       │      │ domain            │     │ Adapter → Mapper → Mongo │
 └──────────────────────┘      └───────────────────┘     └──────────────────────────┘
```

| Módulo | Responsabilidad |
|---|---|
| `common` | Anotaciones `@Adapter`, `@Port`, `@UseCase`, `@Mapper`; excepciones; `DatabaseOption` |
| `domain` | Entidades de negocio puras: `Person`, `Profession`, `Phone`, `Study` |
| `application` | Puertos de entrada y salida, y casos de uso (reglas de negocio) |
| `maria-output-adapter` | Persistencia en MariaDB con JPA (entidades, mappers, repositorios) |
| `mongo-output-adapter` | Persistencia en MongoDB (documentos, mappers, repositorios) |
| `rest-input-adapter` | API REST + Swagger (controllers, adapters que hacen de *Service*, DTOs) e interfaz web (`static/index.html`) |
| `cli-input-adapter` | Interfaz de consola con un menú por entidad |

Equivalencia con el patrón Controller–Service–Repository:

| Patrón | En el proyecto |
|---|---|
| Controller | `XControllerV1` (REST) o `XMenu` (CLI) |
| Service | `XInputAdapterRest` / `XInputAdapterCli` + `XUseCase` |
| Repository | `XOutputAdapterMaria` / `XOutputAdapterMongo` + repositorios de Spring Data |

### Reglas de negocio

Las reglas viven en la capa de aplicación (`application/rules/BusinessRules` y los casos de uso), así que se
cumplen igual por REST y por CLI y con MariaDB o MongoDB. Los límites salen del modelo de datos (DDL).

| Entidad | Regla | Respuesta REST |
|---|---|---|
| Persona | Cédula mayor que 0; nombre y apellido obligatorios, de máximo 45 caracteres | 400 |
| Persona | Género `M` o `F`; edad opcional, entre 0 y 150 | 400 |
| Teléfono | Número de exactamente 10 dígitos; operador obligatorio, de máximo 45 caracteres | 400 |
| Teléfono | El dueño debe existir | 404 |
| Profesión | Id entre 1 y 999999 (`INT(6)`); nombre obligatorio, de máximo 90 caracteres | 400 |
| Estudio | Fecha de grado no futura; universidad de máximo 50 caracteres | 400 |
| Estudio | La persona y la profesión deben existir | 404 |
| Todas | No se crean registros duplicados | 409 |
| Persona / Profesión | Al eliminarlas se eliminan en cascada sus teléfonos y estudios | — |

### Cambios respecto a la plantilla

El código de la plantilla se conserva. Solo se modificó lo siguiente; todo lo demás son archivos o métodos nuevos:

- **Versiones:** JDK 11 + Spring Boot 2.7 (sin soporte) → JDK 21 + Spring Boot 4.1.2. Esto obligó a:
  - cambiar los imports `javax.persistence` y `javax.transaction` por `jakarta.persistence` y el `@Transactional` de Spring;
  - usar los artefactos `spring-boot-starter-webmvc` y `springdoc-openapi-starter-webmvc-ui`;
  - renombrar las propiedades `spring.data.mongodb.*` a `spring.mongodb.*`;
  - quitar las dependencias JAXB, que solo servían de parche para Java 11.
- **Error de los mappers (adapter → dominio):**
  - `EstudiosMapper*.fromAdapterToDomain` retornaba `null`.
  - Los mappers se llamaban en círculo (Persona → Estudio → Persona …), lo que causaba `StackOverflowError` en
    cuanto una persona tenía teléfonos o estudios. Se agregó `fromAdapterToDomainShallow` (sin listas) y las
    relaciones hijas la usan.
  - `EstudiosMapperMaria` convertía la fecha con `Date.toInstant()`, que lanza `UnsupportedOperationException`
    con el `java.sql.Date` que entrega Hibernate.
  - `EstudiosMapperMaria.fromDomainToAdapter` no asignaba la persona ni la profesión al estudio.
  - `PersonaMapperRest.fromAdapterToDomain` retornaba un `Person` vacío, por lo que el POST nunca guardaba datos.
- **Scripts de MongoDB:** `use X` no se ejecuta cuando el script se corre como archivo con `mongosh`; se cambió por
  `db = db.getSiblingDB("X")`. Además, el DML apuntaba a `prueba_db` en vez de `persona_db`.
- **Persona:** `create` ahora rechaza duplicados (antes sobrescribía), y en MongoDB el borrado de una persona
  también borra sus teléfonos y estudios, igual que lo hacen las llaves foráneas en MariaDB.

## Configuración

La configuración de cada aplicación está en `src/main/resources/application.properties` del módulo
`rest-input-adapter` y del módulo `cli-input-adapter`.

| Parámetro | Propiedad | Valor |
|---|---|---|
| Puerto de la API REST | `server.port` | `3000` |
| URL de MariaDB | `spring.datasource.url` | `jdbc:mariadb://localhost:3307/persona_db` |
| Usuario / contraseña de MariaDB | `spring.datasource.username` / `password` | `persona_db` / `persona_db` |
| Host / puerto de MongoDB | `spring.mongodb.host` / `port` | `localhost` / `27017` |
| Base de datos MongoDB | `spring.mongodb.database` | `persona_db` |
| Usuario / contraseña de MongoDB | `spring.mongodb.username` / `password` | `persona_db` / `persona_db` (autenticado en `admin`) |
| Ruta de OpenAPI | `springdoc.api-docs.path` | `/api-docs` |
| Logs | `logging.file.name` | `logs/persona.log` |

`spring.jpa.hibernate.ddl-auto=none`: Hibernate no crea las tablas, las crean los scripts DDL.

Cualquier propiedad se puede sobrescribir al ejecutar, sin tocar el archivo. Por ejemplo:
`java -jar app.jar --spring.datasource.url=jdbc:mariadb://otro-host:3307/persona_db`.

## Configurar el ambiente

### 1. Requisitos

- **JDK 21** (`java -version`).
- **Maven 3.9+** (`mvn -v`).
- **Git**.
- **Docker Desktop**, para la opción A. Si se prefiere, se pueden instalar MariaDB y MongoDB a mano (opción B).
- IDE con soporte de **Lombok**. IntelliJ lo trae incluido; en Eclipse/STS hay que ejecutar `lombok.jar`.

### 2. Obtener el código

```bash
git clone https://github.com/tomoxpy16/personapp-hexa-spring-boot.git
cd personapp-hexa-spring-boot
```

### 3. Bases de datos

#### Opción A — Docker (recomendada)

```bash
docker compose up -d --wait
```

Levanta MariaDB en el puerto **3307** y MongoDB en el **27017**. La primera vez ejecuta solos los scripts DDL y DML
de `scripts/`, que crean el usuario `persona_db`, las tablas o colecciones y los datos de ejemplo.
Los usuarios administradores de los contenedores son `root` / `root`.

- Ver el estado: `docker compose ps`.
- Detener (conserva los datos): `docker compose down`.
- Reiniciar los datos desde cero: `docker compose down -v`, y luego `docker compose up -d --wait`.

#### Opción B — Instalación manual

1. Instalar **MariaDB** escuchando en el puerto **3307** y ejecutar, como root:
   ```bash
   mariadb -uroot -p -P3307 < scripts/persona_ddl_maria.sql
   mariadb -uroot -p -P3307 < scripts/persona_dml_maria.sql
   ```
2. Instalar **MongoDB** en el puerto **27017** con un usuario administrador y ejecutar:
   ```bash
   mongosh "mongodb://<admin>:<clave>@localhost:27017/admin" scripts/persona_ddl_mongo.js
   mongosh "mongodb://<admin>:<clave>@localhost:27017/admin" scripts/persona_dml_mongo.js
   ```

Si las bases de datos están en otro host o puerto, se sobrescriben las propiedades como se indica en
[Configuración](#configuración).

## Compilación

Desde la raíz del proyecto:

```bash
mvn clean package
```

Compila los 7 módulos y genera dos ejecutables:

- `rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar`
- `cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar`

Para solo verificar que compila: `mvn -q compile`.

## Despliegue

Son dos aplicaciones Spring Boot independientes, una por cada adaptador de entrada. Comparten el dominio, la
aplicación y los adaptadores de salida. Las bases de datos deben estar arriba antes de iniciarlas.

### API REST

```bash
java -jar rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar
```

- Swagger UI: http://localhost:3000/swagger-ui.html
- OpenAPI (JSON): http://localhost:3000/api-docs

### Interfaz web

La interfaz web viene dentro de la API: se inicia con el mismo comando y se abre en el navegador.

```bash
java -jar rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar
```

- Interfaz web: http://localhost:3000/

Es una página estática (`rest-input-adapter/src/main/resources/static/index.html`) que Spring Boot sirve
junto con la API. Tiene una pestaña por entidad y un selector de MariaDB / MongoDB, y permite crear,
editar, borrar y ver las relaciones (teléfonos y estudios de una persona, estudios de una profesión).
No agrega código Java: es un cliente de los endpoints REST, así que el núcleo no cambia.
Al borrar una persona o una profesión también se borran sus teléfonos y estudios.

### CLI

```bash
java -jar cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar
```

Menú principal → módulo (Personas / Profesiones / Teléfonos / Estudios) → motor (MariaDB / MongoDB) → operación CRUD.

El CLI no abre ningún puerto, así que puede correr en otra terminal al mismo tiempo que la API y la interfaz
web. Los cambios hechos en uno se ven en el otro, porque usan las mismas bases de datos.

Si al iniciar la API aparece `Port 3000 was already in use`, ya hay una copia de la API corriendo: hay que
cerrarla (Ctrl+C en su terminal) antes de volver a iniciarla, o usar otro puerto con `--server.port=3001`.

### Verificar el despliegue

Con la API corriendo, desde Git Bash o Linux/macOS:

```bash
bash scripts/test_api.sh
```

Ejecuta el CRUD completo de las 4 entidades en ambas bases de datos, incluidas las validaciones y el borrado
en cascada. El resultado esperado es `60 OK, 0 FAIL`. El script borra lo que crea, así que la base de datos
queda como estaba.

```bash
bash scripts/test_reglas.sh
```

Envía 40 datos inválidos (20 por motor: edad negativa o de 500, cédula negativa, teléfonos de 5 o 14 dígitos
o con letras, fechas futuras, textos demasiado largos, etc.). El resultado esperado es `Rechazados correctamente: 40 | Aceptados (falla de validacion): 0`.

## Endpoints

`{database}` puede ser `MARIA` o `MONGO`. En POST y PUT la base de datos va en el campo `database` del body.

| Recurso | Listar | Buscar uno | Contar | Crear | Editar | Eliminar | Extra |
|---|---|---|---|---|---|---|---|
| Persona | `GET /api/v1/persona/{database}` | `GET .../{database}/{cc}` | `GET .../{database}/count` | `POST /api/v1/persona` | `PUT /api/v1/persona/{cc}` | `DELETE .../{database}/{cc}` | `GET .../{database}/{cc}/telefonos`, `GET .../{database}/{cc}/estudios` |
| Profesión | `GET /api/v1/profesion/{database}` | `GET .../{database}/{id}` | `GET .../{database}/count` | `POST /api/v1/profesion` | `PUT /api/v1/profesion/{id}` | `DELETE .../{database}/{id}` | `GET .../{database}/{id}/estudios` |
| Teléfono | `GET /api/v1/telefono/{database}` | `GET .../{database}/{num}` | `GET .../{database}/count` | `POST /api/v1/telefono` | `PUT /api/v1/telefono/{num}` | `DELETE .../{database}/{num}` | |
| Estudio | `GET /api/v1/estudio/{database}` | `GET .../{database}/{idProf}/{cc}` | `GET .../{database}/count` | `POST /api/v1/estudio` | `PUT /api/v1/estudio/{idProf}/{cc}` | `DELETE .../{database}/{idProf}/{cc}` | |

Ejemplos de body:

```json
// POST /api/v1/persona
{ "dni": "111", "firstName": "Ana", "lastName": "Diaz", "age": "20", "sex": "F", "database": "MARIA" }
// POST /api/v1/profesion
{ "id": "5", "name": "Contador", "description": "Finanzas", "database": "MONGO" }
// POST /api/v1/telefono
{ "number": "3001112233", "company": "Claro", "ownerId": "111", "database": "MARIA" }
// POST /api/v1/estudio
{ "professionId": "5", "personId": "111", "graduationDate": "2023-05-10", "universityName": "Javeriana", "database": "MONGO" }
```

Códigos de respuesta: `200` correcto, `400` dato o base de datos inválidos, `404` no existe, `409` ya existe.
Los dos endpoints originales de la plantilla (`GET /api/v1/persona/{database}` y `POST /api/v1/persona`) conservan
su comportamiento: con una base de datos inválida responden una lista vacía o un cuerpo vacío, en vez de `400`.

## Scripts DDL y DML

| Archivo | Contenido |
|---|---|
| `scripts/persona_ddl_maria.sql` | Usuario `persona_db`, esquema `persona_db` y tablas con PK y FK |
| `scripts/persona_dml_maria.sql` | Datos de ejemplo de las 4 tablas |
| `scripts/persona_ddl_mongo.js` | Usuario `persona_db` y colecciones |
| `scripts/persona_dml_mongo.js` | Datos de ejemplo de las 4 colecciones |
| `scripts/test_api.sh` | Prueba del CRUD completo contra la API |
| `scripts/test_reglas.sh` | Prueba de las reglas de negocio con datos inválidos |
| `docker-compose.yml` | MariaDB + MongoDB, con los scripts DDL y DML como inicialización |
