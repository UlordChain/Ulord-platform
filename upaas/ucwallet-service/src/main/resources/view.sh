#!/bin/sh

PROG="ucwallet-service-1.1"

ps aux | grep $PROG | grep -v "grep"
