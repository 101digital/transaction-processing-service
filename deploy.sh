#!/bin/bash

cd charts/transaction-processing-service
helm uninstall transaction-processing-service -n adb
helm install transaction-processing-service . -f values-$1.yaml --set image.tag=1.0.0-LOSNAPSHOT -n adb --create-namespace