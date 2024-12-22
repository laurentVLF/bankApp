FROM gradle:jdk17 AS build
COPY . /home/myBankApp/
WORKDIR /home/myBankApp
RUN gradle clean build --no-daemon

FROM openjdk:17-jdk-slim
EXPOSE 8080
COPY --from=build /home/myBankApp/build/libs/myBankApp-1.0-SNAPSHOT.jar /app/app.jar
WORKDIR /app
CMD ["java", "-jar", "app.jar"]