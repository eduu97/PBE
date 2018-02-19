require "SerialPort"

inputArray = ARGV
if(inputArray.length != 5){
  raise "Parametres del SerialPort incorrectes"
}
ser = SerialPort.new(inputArray)

thrOut = Thread.new {
  char = "a"
  while(char != EOF){
    char = ser.read
    print char
  }
}

thrOut.join
