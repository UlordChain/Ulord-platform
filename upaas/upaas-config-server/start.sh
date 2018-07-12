#!/bin/sh
java -Dspring.profiles.active=dev -jar build/libs/upaas-config-server-1.0.0.jar > /dev/null 2>&1 &
