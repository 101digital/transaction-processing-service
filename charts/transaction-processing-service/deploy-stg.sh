#!/bin/bash

cd charts/account-statement-service
helm uninstall account-statement-service -n $2
helm install account-statement-service . -f values-$1.yaml -n $2 --set image.tag=1.0.0-LOSNAPSHOT --create-namespace --set image.tag=1.0.0-LOSNAPSHOT
