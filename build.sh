#!/bin/bash

# Maven ile uygulamayı derle
./mvnw clean package -DskipTests

# JAR dosyasını çalıştır
java -jar target/*.jar
