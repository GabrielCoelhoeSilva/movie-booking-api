# ─── Etapa 1: Build ───────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o pom.xml primeiro (aproveita cache do Docker)
COPY pom.xml .

# Baixa as dependências antes de copiar o código
RUN mvn dependency:go-offline -B

# Copia o código fonte
COPY src ./src

# Compila e gera o .jar sem rodar os testes
RUN mvn clean package -DskipTests

# ─── Etapa 2: Runtime ─────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia só o .jar gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Porta que a aplicação usa
EXPOSE 8082

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]