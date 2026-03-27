# Build Process
FROM maven:3.9.14-eclipse-temurin-21 as builder
WORKDIR /tmp/
COPY pom.xml .
COPY src ./src/
RUN mvn package -Pprod

# Runtime Image
FROM eclipse-temurin:21-jre-ubi10-minimal
EXPOSE 8080
WORKDIR /opt/word-parser
RUN groupadd -r spring && useradd -r -g spring -s /bin/false spring
USER spring:spring
COPY --from=builder /tmp/target/*.jar word-parser.jar
ENTRYPOINT ["java","-jar","word-parser.jar"] 