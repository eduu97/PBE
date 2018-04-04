require 'serialport'

if (ARGV.length != 5)
  raise "Parametres del SerialPort incorrectes"
end

for i in 1..3
  ARGV[i]=ARGV[i].to_i
end

ARGV[4] = case ARGV[4]
  when "E"
    SerialPort::EVEN
  when "O"
    SerialPort::ODD
  when "N"
    SerialPort::NONE
end

serOut = SerialPort.new(ARGV[0], *ARGV[1])

#Output thread
thrOut = Thread.new {
  while ((char = serOut.getc) != nil)
    print char
  end
}

#Input loop
#Try ctrl+enter to end line
while ((char = STDIN.getc) != "\cd")
  serOut.write(char)
end
