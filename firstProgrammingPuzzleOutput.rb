require "SerialPort"

inputArray = ARGV
if(inputArray.length != 5){
  raise "Parametres del SerialPort incorrectes"
}
ser = SerialPort.new(inputArray(0), inputArray)

thrOut = Thread.new {
  char = "a"
  while(char != EOF){
    puts ser.read
  }
}

thrOut.join
