#!/bin/bash

cd charts/transaction-processing-service
helm uninstall transaction-processing-service -n $2
helm install transaction-processing-service . -f values-$1.yaml -n $2 --create-namespace --set image.tag=1.0.0-LOSNAPSHOT