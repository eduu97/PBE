//Wifly

require 'uri'
require 'expect'
require 'serialport'

def command(comm, resp) #copy paste del que ha fet el Tomas que m'ha semblat bé
    #s'envia la commanda comm des del port serie (acabada en \r) i llegeix la resposta textual del wifly acabada en el string resp
    sp.print(comm + '\r')
    sp.expect(resp)
end

uri = URI(ARGV[0])
ip = uri.host 
url = uri.path
port = '/dev/ttyUSB0'
sp = SerialPort.new(port, 9600, 8, 1, SerialPort::NONE)

#agafar els fitxers

command('set ftp address' + ip, 'AOK')
command('set ftp dir' + url, 'AOK')
command('save', 'AOK')
command('reboot', 'READY')
command('ftp get'+ url, 'AOK')

