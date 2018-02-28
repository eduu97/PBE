# put /dev/tty in raw mode
stty -echo raw

ruby miniterm.rb /dev/pts/18 9600 8 1 N

#restore /dev/tty to cooked mode
stty echo -raw
