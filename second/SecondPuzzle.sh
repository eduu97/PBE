#!/bin/sh
[-p /tmp/wifly.pipe] || mkfifo /tmp/wifly.pipe

while true; do 
java NetCat -1 8080 </tmp/wifly.pipe |
tee -a /tmp/wiflyRequest.log |
java NetCat "$1" "$2" |
tee -a /tmp/wiflyResponse.log >/tmp/wifly.pipe
done