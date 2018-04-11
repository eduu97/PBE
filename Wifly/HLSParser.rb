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
$sp = SerialPort.new(port, 9600, 8, 1, SerialPort::NONE)
$sp.sync = true #per evitar haver de fer flush

def command(comm, resp)
    #s'envia la commanda comm des del port serie (acabada en \r) i llegeix la re$sposta textual del wifly acabada en el string re$sp
    $sp.write(comm + "\r")
    $sp.expect(resp)
end

def modeComandes
  $sp.write('$$$') #per entrar en mode comandes
  $sp.expect('CMD')
end

def confWifly
  command('set wlan ext_antenna 1', 'AOK') #per usar l'antena externa
  command('join PBE', 'IP') #per connectarnos a la xarxa de PBE, esperem al tag del IP
  command('set ip proto 24', 'AOK') #per posar mode HTTP + TCP client
  command('set option format 1', 'AOK') #mostra per pantalla tant bon punt arriba el header HTML
end

def getList(ip, url) #Per cridar-lo has d'estar en mode comandes
  command('set com remote GET$' + url, 'AOK') #fem get de la direccio que volem
  $sp.write('open ' + ip + ' 80' + "\r")
  return $sp.expect('*CLOS*') #esperem a close per a llegir les dades
end

#Pas 1.1 posem wifly mode comandes, no usem un command perque no volem ficar \r
modeComandes
confWifly
master = getList(ip, url)
#separar master en petites
#fer for per cada petita llista i fer getlist 






#Interesting links

#http://ruby-doc.org/stdlib-2.1.1/libdoc/uri/rdoc/URI.html
#https://en.wikipedia.org/wiki/Netcat
#http://ruby-doc.org/stdlib-2.5.0/libdoc/pty/rdoc/IO.html#method-i-expect
#https://en.wikipedia.org/wiki/HTTP_Live_Streaming
#https://en.wikipedia.org/wiki/Expect
#http://atenea.upc.edu/pluginfile.php/2288614/mod_resource/content/1/draft-pantos-http-live-streaming-11.pdf
#http://atenea.upc.edu/pluginfile.php/2288615/mod_resource/content/2/microchip-wifly-command-reference.pdf

#http://192.168.1.100/Justice-dance/hi/prog_index_loc.m3u8
#http://192.168.1.100/Justice-dance/hls_localhost.m3u8
