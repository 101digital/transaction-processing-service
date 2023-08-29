#!/bin/bash

cd charts/transaction-processing-service-chart
helm uninstall transaction-processing-service -n adb
helm install transaction-processing-service . -f values-$1.yaml -n adb --create-namespace