FROM openjdk:19-jdk-alpine

WORKDIR /VeridifoneProject

COPY target/VerProject-1.0-SNAPSHOT.jar VeridifoneProject-1.0-SNAPSHOT.jar

COPY sample-websites.csv /VerProject/sample-websites.csv
COPY merged-data3.csv /VerProject/merged-data3.csv

CMD ["java", "-jar", "VerProject-1.0-SNAPSHOT.jar"]

