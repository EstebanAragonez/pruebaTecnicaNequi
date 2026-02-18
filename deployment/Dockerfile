# Etapa de compilación
FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /app

# Copiar configuración y descargar dependencias (cache si build no cambia)
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY domain ./domain
COPY applications ./applications
COPY infrastructure ./infrastructure

# Compilar (excluir tests para build mas rápido)
RUN gradle :app-service:bootJar -x test --no-daemon

# Etapa de ejecución
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Usuario no root para mayor seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el JAR desde la etapa de build
COPY --from=builder /app/applications/app-service/build/libs/*.jar app.jar

# Crear directorio de logs y dar permisos al usuario de la app
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
