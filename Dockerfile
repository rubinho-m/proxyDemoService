FROM maven:3.9.5-eclipse-temurin-17 as build
WORKDIR /usr/src/app
COPY . .
RUN mvn clean package -DskipTests

WORKDIR /usr/src/app/target

EXPOSE 8080

CMD ["java", "-jar", "vkProxy-0.0.1-SNAPSHOT.jar"]