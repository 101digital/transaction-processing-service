#!/bin/bash

baseDir=$(pwd)

echo "building app"
cd "${baseDir}"
mvn clean package -DskipTests
time docker build -t containers.github.acsdigital.dev/adb/transaction-processing-service:1.0.0-LOSNAPSHOT .
docker push containers.github.acsdigital.dev/adb/transaction-processing-service:1.0.0-LOSNAPSHOT
