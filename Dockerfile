# ---------- BUILD STAGE ----------
FROM --platform=linux/arm64 maven:3.9.9-eclipse-temurin-23 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests clean package


# ---------- RUNTIME STAGE ----------
FROM --platform=linux/arm64 eclipse-temurin:23-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
