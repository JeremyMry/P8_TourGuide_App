FROM openjdk:8
COPY ./out/production/HelloWorld/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java","P8_TourGuide_App"]