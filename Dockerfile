FROM openjdk:11
RUN mkdir /app
COPY ./out/artifacts/app_jar/app.jar /app/app.jar
WORKDIR /app
CMD "java" "-jar" "app.jar"