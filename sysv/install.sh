#!/bin/bash

if [[ $UID != 0 ]]; then
  echo "Please run this script with sudo."
  exit 1
fi

if [ -z "$RT_PUSHER_APP_KEY" ] ; then
  echo "pusher.com app key:"
  read RT_PUSHER_APP_KEY
fi
echo "RT_PUSHER_APP_KEY=$RT_PUSHER_APP_KEY" > /etc/default/remotetorrent

echo "torrent download destination:"
read RT_DESTINATION
echo "RT_DESTINATION=$RT_DESTINATION" >> /etc/default/remotetorrent


./gradlew build
mkdir -p ~/torrents
rm -f /usr/local/bin/remotetorrent.jar
mv build/libs/remotetorrent.jar /usr/local/bin
cp remotetorrent-start.sh /usr/local/bin
cp remotetorrent-stop.sh /usr/local/bin
cp remotetorrent /etc/init.d
update-rc.d remotetorrent defaults
