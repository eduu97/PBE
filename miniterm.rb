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
  char = serOut.getc
  while (char != nil)
    print char
    char = serOut.getc
  end
}

#Input loop

char = STDIN.getc
while (char != "\cd")
  serOut.write(char)
  char = STDIN.getc
end
