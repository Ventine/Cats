# ===============================
# Etapa 1: Construcción con Maven
# ===============================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar solo archivos de dependencias primero (para cacheo eficiente)
COPY pom.xml .
RUN mvn dependency:go-offline

# Ahora sí copiamos el código
COPY src ./src

# Compilamos y empaquetamos (sin tests para velocidad)
RUN mvn clean package -DskipTests

# ===============================
# Etapa 2: Imagen final liviana
# ===============================
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copiamos el jar resultante (forzamos un nombre fijo con finalName en pom.xml o lo dejamos dinámico)
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto usado por Spring Boot
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
