# Franquicias API

API reactiva para gestión de franquicias, sucursales y productos. Desarrollada con **Spring Boot** y **Clean Architecture**.

---

## Arquitectura

El proyecto sigue Clean Architecture con una estructura multi-módulo en Gradle:

```
pruebaTecnicaNequi/
├── applications/
│   └── app-service/              # Módulo bootable - MainApplication, config, resources
├── domain/
│   ├── model/                    # Modelos de dominio y puertos (interfaces)
│   └── usecase/                  # Casos de uso (lógica de negocio)
├── infrastructure/
│   ├── driven-adapters/
│   │   └── r2dbc-postgres/       # Adaptador de persistencia PostgreSQL R2DBC
│   ├── entry-points/
│   │   └── rest-webflux/         # Punto de entrada REST (WebFlux)
│   └── helpers/                  # Utilidades compartidas
├── deployment/                   # Dockerfile, docker-compose
├── build.gradle
└── settings.gradle
```

### Capas

| Capa | Módulos | Responsabilidad |
|------|---------|-----------------|
| **Domain** | model, usecase | Entidades, puertos y reglas de negocio. Independiente del marco. |
| **Infrastructure** | driven-adapters, entry-points | Implementaciones: R2DBC, controladores REST. |
| **Application** | app-service | Inyección de dependencias y arranque de la aplicación. |

---

## Consideraciones de diseño

- **Modelo reactivo**: Uso de Project Reactor (`Mono`/`Flux`) en toda la capa de aplicación y acceso a datos.
- **Dependencias invertidas**: Los use cases dependen de puertos (interfaces); los adaptadores implementan esos puertos.
- **Modelos inmutables**: Entidades de dominio como records con validaciones en el constructor.
- **Validación en capas**: Validación en DTOs (Jakarta Validation) y en la capa de dominio.
- **Respuestas estándar**: Errores bajo RFC 7807 (Problem Details) con `GlobalExceptionHandler`.

---

## Tecnologías

| Tecnología | Uso |
|------------|-----|
| Java 21 | Lenguaje |
| Spring Boot 3.2 | Framework |
| Spring WebFlux | API REST reactiva |
| Spring Data R2DBC | Persistencia reactiva |
| PostgreSQL | Base de datos |
| Project Reactor | Programación reactiva (Mono/Flux) |
| SpringDoc OpenAPI | Documentación Swagger |
| Jakarta Validation | Validación de entrada |
| JUnit 5 + Mockito | Tests unitarios |
| JaCoCo | Cobertura de código |

---

## Despliegue en entorno local

### Requisitos

- Java 21
- Gradle 8.5+ (incluido wrapper)
- Docker (opcional, para PostgreSQL)

### Opción A: Con Docker Compose (PostgreSQL + API)

```bash
cd deployment
docker-compose up -d --build
```

- **PostgreSQL**: puerto 5432
- **API**: puerto 8080

### Opción B: Solo PostgreSQL + app local

1. Levantar PostgreSQL:

```bash
cd deployment
docker-compose up -d postgres
```

2. Ejecutar la aplicación:

```bash
.\gradlew.bat :app-service:bootRun
# o
./gradlew :app-service:bootRun
```

### Opción C: Sin Docker

1. Crear la base de datos PostgreSQL:

```sql
CREATE DATABASE franquicias_db;
```

2. Ejecutar el schema: `applications/app-service/src/main/resources/schema.sql`

3. Configurar variables de entorno (o usar defaults en `application.yml`):

| Variable | Default |
|----------|---------|
| POSTGRES_HOST | localhost |
| POSTGRES_PORT | 5432 |
| POSTGRES_DB | franquicias_db |
| POSTGRES_USER | postgres |
| POSTGRES_PASSWORD | postgres |
| SERVER_PORT | 8080 |

4. Ejecutar:

```bash
.\gradlew.bat :app-service:bootRun
```

### Compilar y empaquetar

```bash
.\gradlew.bat build
.\gradlew.bat :app-service:bootJar
java -jar applications/app-service/build/libs/app-service-1.0.0-SNAPSHOT.jar
```

---

## API REST

Base path: `/api/v1`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /franquicias | Crear franquicia |
| PATCH | /franquicias/{franchiseId}/nombre | Actualizar nombre de franquicia |
| POST | /sucursales | Crear sucursal |
| PATCH | /sucursales/{branchId}/nombre | Actualizar nombre de sucursal |
| POST | /productos | Crear producto |
| PATCH | /productos/{productId}/nombre | Actualizar nombre de producto |
| PATCH | /productos/{productId}/stock | Actualizar stock de producto |
| DELETE | /sucursales/{branchId}/productos/{productId} | Eliminar producto |
| GET | /franquicias/{franchiseId}/productos-mayor-stock | Productos con mayor stock por sucursal de una franquicia |

### Ejemplo de peticiones

**Crear franquicia**
```json
POST /api/v1/franquicias
{"nombre": "Mi Franquicia"}
```

**Crear sucursal**
```json
POST /api/v1/sucursales
{"franchiseId": 1, "nombre": "Sucursal Centro"}
```

**Crear producto**
```json
POST /api/v1/productos
{"branchId": 1, "nombre": "Producto A", "stock": 10}
```

---

## Documentación OpenAPI (Swagger)

La API está documentada con **SpringDoc OpenAPI**.

Una vez la aplicación está en marcha, accede a:

- **Swagger UI**: http://localhost:8080/swagger-ui.html

Los endpoints incluyen anotaciones `@Operation` y `@ApiResponses` para documentar parámetros, cuerpos y respuestas.

---

## Manejo de errores

La API usa RFC 7807 (Problem Details) para los errores. El `GlobalExceptionHandler` centraliza el manejo de excepciones:

| Excepción | Código HTTP | Título |
|-----------|-------------|--------|
| ResponseStatusException (NOT_FOUND) | 404 | No encontrado |
| ResponseStatusException (otros) | según excepción | Error en la solicitud |
| IllegalArgumentException | 400 | Solicitud inválida |
| WebExchangeBindException | 400 | Error de validación |
| ConstraintViolationException | 400 | Error de validación |
| HandlerMethodValidationException | 400 | Error de validación |
| Exception (genérica) | 500 | Error interno |

### Ejemplo de respuesta de error

```json
{
  "type": "about:blank",
  "title": "Error de validación",
  "status": 400,
  "detail": "nombre: El nombre es requerido"
}
```

---

## Pruebas

### Ejecutar tests

```bash
.\gradlew.bat test
```

### Cobertura con JaCoCo

```bash
.\gradlew.bat test jacocoRootReport
```

Reporte agregado: `build/reports/jacoco/jacocoRootReport/html/index.html`

### Estructura de tests

| Módulo | Tests |
|--------|-------|
| model | FranchiseTest, BranchTest, ProductTest |
| usecase | Tests de los 9 casos de uso |
| r2dbc-postgres | FranchiseRepositoryAdapterTest, BranchRepositoryAdapterTest, ProductRepositoryAdapterTest |
| rest-webflux | FranquiciasControllerTest, GlobalExceptionHandlerTest, DtoValidationTest |
| app-service | UseCaseConfigTest |
