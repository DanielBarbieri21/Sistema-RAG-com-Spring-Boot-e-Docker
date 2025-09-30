# Use OpenJDK 17 como base
FROM openjdk:17-jdk-slim

# Instalar curl para health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Definir diretório de trabalho
WORKDIR /app

# Copiar JAR compilado (deve ser compilado localmente primeiro)
COPY target/rag-system-1.0.0.jar app.jar

# Expor porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# Comando para executar
CMD ["java", "-jar", "app.jar"]
