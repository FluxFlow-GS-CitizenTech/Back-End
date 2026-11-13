
# ==========================================
# ESTÁGIO 1: BUILD DA APLICAÇÃO
# ==========================================
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

# Copia os arquivos de configuração do Maven
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Permissão de execução do Maven Wrapper
RUN chmod +x mvnw

# Baixa as dependências e compila a aplicação
COPY src/ src/
RUN ./mvnw -B -DskipTests clean package


# ==========================================
# ESTÁGIO 2: EXECUÇÃO DA APLICAÇÃO
# ==========================================
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copia somente o JAR gerado no estágio anterior
COPY --from=build /app/target/fluxflow-0.0.1-SNAPSHOT.jar app.jar

# Porta utilizada pela aplicação
EXPOSE 8080

# Inicializa o FluxFlow
ENTRYPOINT ["java", "-jar", "app.jar"]
