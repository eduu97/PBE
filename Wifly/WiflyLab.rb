require 'expect'
require 'uri'
require 'serialport'

#Faig que la uri per defecte sigui la del exercici
if (ARGV.length < 1)
    uri = URI('http://192.168.1.100/Justice-dance/hls_localhost.m3u8')
else
    uri = URI(ARGV[0])
end
ip = uri.host
url = uri.path
port = '/dev/ttyUSB0'
sp = SerialPort.new(port, 9600, 8, 1, SerialPort::NONE)

#Pas 1.1 posem wifly mode comandes, no usem un command perque no volem ficar \r
sp.print('$$$') #per entrar en mode comandes
sp.expect('CMD')
command('set wlan ext_antenna 1', 'AOK') #per usar l'antena externa
command('join PBE', 'Associated!') #per connectarnos a la xarxa de PBE esperar Associated!
command('set ip proto 24', 'AOK') #per posar mode HTTP + TCP client
command('set com remote GET$/Justice-dance/hls_localhost.m3u8', 'AOK') #fem get de la direccio que volem
command('set option format 1', 'AOK') #mostra per pantalla tant bon punt arriba el header HTML
command('open 192.168.1.100 80', '*OPEN*') #obrim la connexio, potser esperar '*CLOS*', aixi llegir totes les dades

def command(comm, resp)
    #s'envia la commanda comm des del port serie (acabada en \r) i llegeix la resposta textual del wifly acabada en el string resp
    sp.print(comm + '\r')
    puts sp.expect(resp)
end

#Interesting links

#http://ruby-doc.org/stdlib-2.1.1/libdoc/uri/rdoc/URI.html
#https://en.wikipedia.org/wiki/Netcat
#http://ruby-doc.org/stdlib-2.5.0/libdoc/pty/rdoc/IO.html#method-i-expect
#https://en.wikipedia.org/wiki/HTTP_Live_Streaming
#https://en.wikipedia.org/wiki/Expect
#https://www.sparkfun.com/products/retired/10050
#http://atenea.upc.edu/pluginfile.php/2288614/mod_resource/content/1/draft-pantos-http-live-streaming-11.pdf
#http://atenea.upc.edu/pluginfile.php/2288615/mod_resource/content/2/microchip-wifly-command-reference.pdf

