require 'expect'
require 'uri'
require 'serialport'

def command(comm, resp)
    #s'envia la commanda comm des del port serie (acabada en \r) i llegeix la resposta textual del wifly acabada en el string resp
    sp.print(comm + '\r')
    sp.expect(resp)
end

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

#Suposare que funciona fins aqui

#Pas 1: Posar el Wifly en mode comandes, despres HTTP i connectar-lo a internet
#Pas 2: Get de la master playlist de path
#Pas 3: Implementar l'algorisme de  HLS i imprimir els fitxers per consola

#Pas 1.1 posem wifly mode comandes, no usem un command perque no volem ficar \r
sp.print('$$$')
sp.expect('CMD')

#Pas 1.2 connectar wifly a la xarxa
#TO-DO

#Pas 1.3 posar wifly a mode http
command('set ip proto 18', 'AOK')
command('set ip host' + ip, 'AOK')
command('set ip remote 80', 'AOK')
command('set com remote 0') #No estic segur d'aquest, ho deia al manual
command('save', 'AOK') #guarda la config
command('open ' + ip + ' 80', '*OPEN*') #per obrir la connexio
command('reboot', 'READY') #reinica el wifly amb la configuracio adient

#Pas 2 Get master playlist
master = sp.read

#Pas 3 HLS i imprimir fitxers per consola
#No se com entrar a les diferents carpetes i diferents arxius...

#Interesting links

#http://ruby-doc.org/stdlib-2.1.1/libdoc/uri/rdoc/URI.html
#https://en.wikipedia.org/wiki/Netcat
#http://ruby-doc.org/stdlib-2.5.0/libdoc/pty/rdoc/IO.html#method-i-expect
#https://en.wikipedia.org/wiki/HTTP_Live_Streaming
#https://en.wikipedia.org/wiki/Expect
#https://www.sparkfun.com/products/retired/10050
#http://atenea.upc.edu/pluginfile.php/2288614/mod_resource/content/1/draft-pantos-http-live-streaming-11.pdf
#http://atenea.upc.edu/pluginfile.php/2288615/mod_resource/content/2/microchip-wifly-command-reference.pdf


