#!/bin/sh
java -Dspring.profiles.active=test -jar build/libs/content-uauth-service-1.0-SNAPSHOT.jar > /dev/null 2>&1 &
