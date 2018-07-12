#!/bin/sh
PROG=upaas-eureka-server

pid=`ps aux | grep $PROG | grep -v "grep" | awk '{print $2}'`

echo "PID is " $pid
kill $pid

