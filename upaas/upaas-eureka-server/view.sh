#!/bin/sh

PROG="upaas-eureka-server"

ps aux | grep $PROG | grep -v "grep"
