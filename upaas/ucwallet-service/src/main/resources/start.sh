#!/bin/sh
java -Dspring.profiles.active=dev -jar ucwallet-service-1.1.0.jar  > /dev/null 2>&1 &
