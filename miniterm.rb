require 'serialport'

inputArray = ARGV
if (inputArray.length != 5)
  raise "Parametres del SerialPort incorrectes"
end
for i in 1..3
  inputArray[i]=inputArray[i].to_i
end
parity = inputArray[4]
case parity
  when "E"
    parity = SerialPort::EVEN
  when "O"
    parity = SerialPort::ODD
  else
    parity = SerialPort::NONE
end
serOut = SerialPort.new(inputArray[0], inputArray[1], inputArray[2], inputArray[3], parity)

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
