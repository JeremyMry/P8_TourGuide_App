FROM openjdk:11
RUN mkdir /app
COPY ./target/app-0.0.1-SNAPSHOT.jar /app/app-0.0.1-SNAPSHOT.jar
WORKDIR /app
EXPOSE 8080
CMD "java" "-jar" "app-0.0.1-SNAPSHOT.jar"