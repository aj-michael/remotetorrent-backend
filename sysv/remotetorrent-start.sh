#!/bin/bash
nohup java -jar /usr/local/bin/remotetorrent.jar $2 $1 &> /var/log/remotetorrent.log&
