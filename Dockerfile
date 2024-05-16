# Build Process
FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /tmp/
COPY pom.xml .
COPY src ./src/
RUN mvn package -Pprod

# Runtime Image
FROM eclipse-temurin:21-jre-alpine
EXPOSE 8080
WORKDIR /opt/word-parser
ENV API_KEYS=
ENV ENABLE_SWAGGER_UI=
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=builder /tmp/target/*.jar word-parser.jar
ENTRYPOINT ["java","-jar","word-parser.jar"] 