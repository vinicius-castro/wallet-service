# --------------------------------------------------------------------------
# STAGE 1: BUILD
# --------------------------------------------------------------------------
FROM gradle:8.9-jdk21-jammy AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew /app/
COPY gradle /app/gradle

RUN ./gradlew dependencies

COPY src /app/src

RUN ./gradlew bootJar

# --------------------------------------------------------------------------
# STAGE 2: RUNTIME
# --------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy

VOLUME /tmp

ARG JAR_FILE=build/libs/*.jar

COPY --from=build /app/${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]