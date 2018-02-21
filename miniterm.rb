require 'serialport'

inputArray = ARGV
if (inputArray.length != 5)
  raise "Parametres del SerialPort incorrectes"
end
baud_rate = inputArray[1].to_i
data_bits = inputArray[2].to_i
stop_bits = inputArray[3].to_i
parity = SerialPort::NONE
if (parity != 'N')
 parity = inputArray[4].to_i
end
serOut = SerialPort.new(inputArray[0], baud_rate, data_bits, stop_bits, parity)

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
while (char != nil and char != '^D')
  char = STDIN.getc
  serOut.write(char)
end
