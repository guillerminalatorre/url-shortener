# -------- BUILD STAGE --------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar pom y dependencias primero (cachea más rápido)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY ../../Documents/shortener-backup/src ./src
RUN mvn clean package -DskipTests

# -------- RUNTIME STAGE --------
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiamos el JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto
EXPOSE 8080

# Ejecutamos la app
ENTRYPOINT ["java","-jar","app.jar"]
