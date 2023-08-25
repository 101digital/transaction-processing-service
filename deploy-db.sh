#!/bin/bash

cd charts/transaction-processing-service-db-chart
helm uninstall transaction-processing-service-db -n adb
helm install transaction-processing-service-db . -f values-$1.yaml -n adb --create-namespace
