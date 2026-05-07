# syntax=docker/dockerfile:1.6

# Stage 1: Build with Maven + JDK 21
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build
COPY maven-settings.xml /root/.m2/settings.xml
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B || true

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests -B

# Stage 2: Runtime with JRE 21 alpine
FROM eclipse-temurin:21-jre-alpine

LABEL org.opencontainers.image.title="ikindle-backend" \
      org.opencontainers.image.description="iKindle 电子书阅读平台后端服务" \
      org.opencontainers.image.version="1.0.0"

RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

WORKDIR /app
RUN mkdir -p /data/ikindle/uploads
COPY --from=builder /build/target/ikindle-backend-*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -fsS http://localhost:8080/api/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
