# Usar JDK 21
FROM eclipse-temurin:21-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR desde target/
COPY target/urlshortener-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto de la app
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]

#docker-compose up --build