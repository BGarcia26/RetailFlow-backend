# Etapa 1: compila el jar con el JDK completo
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Primero solo los archivos de Gradle: si no cambian, Docker reutiliza las dependencias ya descargadas
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
# gradlew puede venir con saltos de línea de Windows (CRLF), que Linux no sabe ejecutar
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null

COPY src src
# Los tests ya se corren en el PC; aquí solo se arma el jar
RUN ./gradlew bootJar --no-daemon -x test

# Etapa 2: imagen final liviana, solo con lo necesario para ejecutar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# El plan gratis de Render tiene 512 MB: se limita el heap para no quedarse sin memoria
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
