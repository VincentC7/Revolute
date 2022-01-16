FROM maven:3-openjdk-11 AS BUILDER
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B clean
RUN mvn -e -B -DskipTests package

FROM openjdk:11
COPY --from=builder /app/target/Conversion-service-1.0.jar /app/target/Conversion-service-1.0.jar
EXPOSE 8280
CMD ["java", "-jar", "/app/target/Conversion-service-1.0.jar"]