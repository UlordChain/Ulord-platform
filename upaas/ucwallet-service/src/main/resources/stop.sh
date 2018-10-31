#!/bin/sh
PROG=ucwallet-service-1.1

pid=`ps aux | grep $PROG | grep -v "grep" | awk '{print $2}'`

echo "PID is " $pid
kill $pid

