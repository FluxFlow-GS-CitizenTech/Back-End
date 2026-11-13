
# ==========================================
# ESTÁGIO 1: BUILD DA APLICAÇÃO
# ==========================================
FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml .
COPY src/ src/

RUN mvn -B -DskipTests clean package

# ==========================================
# ESTÁGIO 2: EXECUÇÃO DA APLICAÇÃO
# ==========================================
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=build /app/target/fluxflow-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
