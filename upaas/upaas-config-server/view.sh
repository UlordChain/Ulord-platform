#!/bin/sh

PROG="upaas-config-server"

ps aux | grep $PROG | grep -v "grep"
