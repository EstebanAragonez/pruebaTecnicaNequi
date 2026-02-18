# Franquicias API

API reactiva para gestión de franquicias, sucursales y productos. Desarrollada con **Spring Boot** y **Clean Architecture**.

---

## Arquitectura

El proyecto aplica **Clean Architecture** y **Hexagonal Architecture (Ports & Adapters)** mediante una estructura multi-módulo en Gradle.

### Estructura del proyecto

```
pruebaTecnicaNequi/
├── applications/
│   └── app-service/              # Módulo bootable - MainApplication, UseCaseConfig, schema.sql
├── domain/
│   ├── model/                    # Entidades de dominio y puertos (interfaces)
│   └── usecase/                  # Casos de uso - orquestan la lógica de negocio
├── infrastructure/
│   ├── driven-adapters/
│   │   └── r2dbc-postgres/       # Adaptadores que implementan los puertos (PostgreSQL R2DBC)
│   ├── entry-points/
│   │   └── rest-webflux/         # Adaptador de entrada REST (FranquiciasController)
│   └── helpers/                  # Utilidades compartidas
├── deployment/                   # Dockerfile, docker-compose
├── build.gradle
└── settings.gradle
```

### Capas y dependencias

| Capa | Módulos | Responsabilidad | Depende de |
|------|---------|-----------------|------------|
| **Domain (model)** | model | Entidades `Franchise`, `Branch`, `Product` y puertos (`FranchiseRepository`, `BranchRepository`, `ProductRepository`). Sin dependencias externas. | — |
| **Domain (usecase)** | usecase | 9 casos de uso que orquestan la lógica. Inyectan solo puertos (interfaces). | model |
| **Infrastructure (driven)** | r2dbc-postgres | Implementan los puertos. Traducen dominio ↔ entidades R2DBC. | model |
| **Infrastructure (entry)** | rest-webflux | Controlador REST, DTOs, mapeo HTTP ↔ casos de uso. | usecase |
| **Application** | app-service | `UseCaseConfig` ensambla beans; inyecta adaptadores en casos de uso. | usecase, r2dbc-postgres, rest-webflux |

### Flujo de una petición

```
HTTP Request → FranquiciasController → UseCase → Puerto (interfaz) → Adaptador R2DBC → PostgreSQL
                      ↓                    ↓
                   DTOs              Modelos de dominio
                   (validación)      (Franchise, Branch, Product)
```

El dominio **no conoce** Spring, REST ni bases de datos. Solo define contratos (puertos) y entidades puras.

---

## Patrones de diseño

| Patrón | Dónde se aplica | Descripción |
|--------|------------------|-------------|
| **Ports & Adapters (Hexagonal)** | model, usecase, infrastructure | El dominio expone **puertos** (interfaces como `FranchiseRepository`). Los **adaptadores** (`FranchiseRepositoryAdapter`, `FranquiciasController`) los implementan o los consumen. |
| **Dependency Inversion** | usecase ← model | Los casos de uso dependen de `FranchiseRepository` (interfaz), no de `FranchiseRepositoryAdapter`. La implementación concreta se inyecta en tiempo de ejecución. |
| **Use Case / Application Service** | domain/usecase | Cada operación de negocio es un caso de uso (`AddFranchiseUseCase`, `GetMaxStockProductsByFranchiseUseCase`, etc.). Encapsulan reglas y orquestan repositorios. |
| **Repository** | model/port, r2dbc-postgres | Los puertos abstraen persistencia: `save()`, `findById()`, `findByFranchiseId()`, etc. Los adaptadores mapean entre `Franchise` (dominio) y `FranchiseEntity` (BD). |
| **DTO (Data Transfer Object)** | rest-webflux/dto | Objetos como `FranchiseRequest`, `BranchResponse` separan el contrato HTTP del modelo de dominio. Evitan exponer entidades internas. |
| **Record (inmutabilidad)** | model, dto | Entidades y DTOs como Java records: inmutables, con validación en el compact constructor (`Objects.requireNonNull`, `stock >= 0`). |
| **Factory de beans** | UseCaseConfig | Configuración explícita de casos de uso con inyección por constructor. No se usa `@Component` en el dominio. |
| **Controller Advice** | GlobalExceptionHandler | Manejo centralizado de excepciones. Traduce excepciones de dominio/infraestructura a RFC 7807 (Problem Details). |

### Consideraciones adicionales

- **Modelo reactivo**: Toda la cadena usa `Mono`/`Flux`. Los puertos devuelven tipos reactivos; el controlador expone `Mono<FranchiseResponse>`.
- **Validación en capas**: DTOs con Jakarta Validation (`@NotBlank`, `@Size`, `@Positive`); dominios con validaciones en el constructor (ej. `Product`: stock no negativo).

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
