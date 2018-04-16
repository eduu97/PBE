require 'expect'
require 'uri'
require 'serialport'

if (ARGV.length < 1) #Per la uri es la del exercici
    uri = URI('http://192.168.1.100/Justice-dance/hls_localhost.m3u8')
else
    uri = URI(ARGV[0])
end

ip = uri.host
url = uri.path
port = '/dev/ttyUSB0'
$sp = SerialPort.new(port, 9600, 8, 1, SerialPort::NONE)
$sp.sync = true #per evitar haver de fer flush

def command(comm, resp) #s'envia la commanda comm des del port serie (acabada en \r) i llegeix la re$sposta textual del wifly acabada en el string resp
    $sp.write(comm + "\r")
    $sp.expect(resp)
end

def modeComandes #Per entrar en mode comandes
    $sp.write('$$$') #no usem un command perque no volem ficar \r
    $sp.expect('CMD')
end

def confWifly
    command('set wlan ext_antenna 1', 'AOK') #per usar l'antena externa
    command('join PBE', 'IP') #per connectarnos a la xarxa de PBE, esperem al tag del IP
    command('set ip proto 24', 'AOK') #per posar mode HTTP + TCP client
    command('set option format 1', 'AOK') #mostra per pantalla tant bon punt arriba el header HTML
end

def getList(ip, url) #Per cridar-lo has d'estar en mode comandes, quan acaba surt del mode comandes
    command('set com remote GET$' + url, 'AOK') #fem get de la direccio que volem
    $sp.write('open ' + ip + ' 80' + "\r")
    return $sp.expect('*CLOS*') #esperem a close per a llegir les dades
end

def processMaster(master)
    plBW = (master.to_s).scan(/BANDWIDTH=\d+/)
    for bw in plBW
        bw.sub('BANDWIDTH=','')
    end
    masterpl = (master.to_s).scan(/http\S+.m3u8/)
    npl = plBW.length
    subpl = Array.new(npl)
    for i in 0..npl
      puts 'Llegint de ' + masterpl(i) + ', amb BW ' + plBW(i)
      modeComandes
      list = getList(ip, URI(masterpl(i)).path)
      subpl(i) = list.to_s
      puts (subpl(i).to_s).scan(/DURATION:\d+/).to_s #durada en segments de la subplaylist
    end
end

modeComandes #Posem wifly mode comandes
puts 'Hem entrat a mode comandes'
confWifly #Configurem les opcions del Wifly
puts 'Wifly configurat'
master = getList(ip, url) #Obtenim la playlist de la direccio donada
puts 'Master playlist obtinguda'
processMaster(master) #Obtenim les dades necessaries per HLS
puts 'Master playlist processada'

#Interesting links, rubular es per regex

#http://ruby-doc.org/stdlib-2.1.1/libdoc/uri/rdoc/URI.html
#https://en.wikipedia.org/wiki/Netcat
#http://ruby-doc.org/stdlib-2.5.0/libdoc/pty/rdoc/IO.html#method-i-expect
#https://en.wikipedia.org/wiki/HTTP_Live_Streaming
#https://en.wikipedia.org/wiki/Expect
#http://atenea.upc.edu/pluginfile.php/2288614/mod_resource/content/1/draft-pantos-http-live-streaming-11.pdf
#http://atenea.upc.edu/pluginfile.php/2288615/mod_resource/content/2/microchip-wifly-command-reference.pdf
#http://rubular.com/
#http://192.168.1.100/Justice-dance/hi/prog_index_loc.m3u8
#http://192.168.1.100/Justice-dance/hls_localhost.m3u8


