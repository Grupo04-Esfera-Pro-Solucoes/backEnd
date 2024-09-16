# Usar uma imagem base do Maven para compilar o projeto
FROM maven:3.8.6-openjdk-17 AS build

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o código fonte para dentro do container
COPY pom.xml .
COPY src ./src

# Executar o comando para baixar dependências e gerar o .jar
RUN mvn clean package -DskipTests

# Fase final - criar uma imagem mais leve com o JAR
FROM openjdk:17-jdk-alpine

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o .jar gerado da fase de build
COPY --from=build /app/target/*.jar app.jar

# Comando para executar o JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
