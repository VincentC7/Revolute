FROM maven:3-openjdk-11 AS BUILDER
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B -DskipTests clean package

FROM openjdk:11
COPY --from=builder /app/target/Banque-service-1.0.jar /app/target/Banque-service-1.0.jar
EXPOSE 8085
CMD ["java", "-jar", "/app/target/Banque-service-1.0.jar"]