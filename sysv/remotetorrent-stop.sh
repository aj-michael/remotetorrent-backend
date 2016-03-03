#!/bin/bash
pid=`pgrep -f "java -jar /usr/local/bin/remotetorrent.jar"`
if [ -z "$pid" ] ; then
  exit 0
else
  sudo kill -9 $pid
  exit 0
fi
