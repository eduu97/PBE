require "SerialPort"

inputArray = ARGV
if (inputArray.length != 5)
  raise "Parametres del SerialPort incorrectes"
end
serOut = SerialPort.new(inputArray)

#Output thread

thrOut = Thread.new {
  char = "a"
  while (char != EOF)
    char = serOut.getc
    print char
  end
}

#Input loop
count = 0
char = "a"
while (char != EOF && count < 3)
  char = STDIN.getc
  if (char == "$")
    count += 1
  else
    count = 0
  end
  serOut.write(char)
end

#End of output thread
thrOut.join
