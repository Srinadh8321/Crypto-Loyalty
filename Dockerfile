# ---------- BUILD STAGE ----------
FROM maven:3.9.9-eclipse-temurin-23 AS builder

WORKDIR /app

# Copy pom first for caching
COPY pom.xml .

# Download dependencies (safe)
RUN mvn -B -DskipTests dependency:resolve

# Copy source and build
COPY src ./src
RUN mvn -B -DskipTests clean package

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:23-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
