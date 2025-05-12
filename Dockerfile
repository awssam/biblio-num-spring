FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY --from=build /workspace/app/target/bibliotheque-0.0.1-SNAPSHOT.jar app.jar
COPY bibliodb.sqlite bibliodb.sqlite
ENTRYPOINT ["java","-Dspring.profiles.active=production","-jar","/app.jar"]
EXPOSE 8080
