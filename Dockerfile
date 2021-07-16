FROM openjdk:11
WORKDIR /target
ADD app-0.0.1-SNAPSHOT.jar app-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD java -jar app-0.0.1-SNAPSHOT.jar