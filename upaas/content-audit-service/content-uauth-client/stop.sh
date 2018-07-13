#!/bin/sh
PROG=content-uauth-client

pid=`ps aux | grep $PROG | grep -v "grep" | awk '{print $2}'`

echo "PID is " $pid
kill $pid

