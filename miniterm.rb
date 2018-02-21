require 'serialport'

if (ARGV.length != 5)
  raise "Parametres del SerialPort incorrectes"
end

for i in 1..3
  ARGV[i]=ARGV[i].to_i
end

case ARGV[4]
  when "E"
    ARGV[4] = SerialPort::EVEN
  when "O"
    ARGV[4] = SerialPort::ODD
  else
    ARGV[4] = SerialPort::NONE
end

serOut = SerialPort.new(ARGV[0], *ARGV[1])

#Output thread

thrOut = Thread.new {
  char = "a"
  while (char != nil)
    char = serOut.getc
    print char
  end
}

#Input loop

char = "a"
while (char != "\cd")
  char = STDIN.getc
  if (char != "\cd") 
    serOut.write(char)
  end
end
