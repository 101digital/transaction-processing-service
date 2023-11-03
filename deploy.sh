#!/bin/bash

cd charts/transaction-processing-service
helm uninstall transaction-processing-service -n adb-dev
helm install transaction-processing-service . -f values-$1.yaml -n adb-dev --create-namespace --set image.tag=1.0.0-LOSNAPSHOT