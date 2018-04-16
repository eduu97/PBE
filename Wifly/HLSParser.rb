require 'expect'
require 'uri'
require 'serialport'

if (ARGV.length < 1) #per defecte usem la URI del exercici
    uri = URI('http://192.168.1.100/Justice-dance/hls_localhost.m3u8')
else
    uri = URI(ARGV[0])
end

ip = uri.host
url = uri.path
port = '/dev/ttyUSB0'
$sp = SerialPort.new(port, 9600, 8, 1, SerialPort::NONE)
$sp.sync = true #per evitar haver de fer flush

def command(comm, resp) #envia la comanda comm (acabada en \r) i llegeix la resposta textual acabada en resp
    $sp.write(comm + "\r")
    $sp.expect(resp)
end

def modeComandes #per entrar en mode comandes
    $sp.write('$$$') #no usem un command perque no volem ficar \r
    $sp.expect('CMD')
end

def confWifly
    command('set wlan ext_antenna 1', 'AOK') #per usar l'antena externa
    command('join PBE', 'IP') #per connectar-nos a la xarxa de PBE, esperem al tag del IP
    command('set ip proto 24', 'AOK') #per posar mode HTTP + TCP client
    command('set option format 1', 'AOK') #mostra per pantalla tant bon punt arriba el header HTML
end

def getList(ip, url) #per cridar-lo has d'estar en mode comandes, quan acaba surt del mode comandes
    command('set com remote GET$' + url, 'AOK') #fem get de la direccio que volem
    $sp.write('open ' + ip + ' 80' + "\r")
    return $sp.expect('*CLOS*').to_s #esperem a close per a llegir les dades
end

def processMaster(master)
    plBW = master.scan(/BANDWIDTH=\d+/)
    plBW.each { |bw| bw.sub('BANDWIDTH=','') }
    masterpl = master.scan(/http\S+.m3u8/)
    npl = plBW.length
    subpl = Array.new(npl)
    for i in 0..npl
        puts 'Llegint de ' + masterpl(i) + ', amb BW ' + plBW(i)
        modeComandes
        subpl(i) = getList(ip, URI(masterpl(i)).path)
        puts subpl(i).scan(/DURATION:\d+/).to_s #durada en segments de la subplaylist
    end
end

modeComandes #posem wifly mode comandes
puts 'Hem entrat a mode comandes'
confWifly #configurem les opcions del Wifly
puts 'Wifly configurat'
master = getList(ip, url) #obtenim la playlist de la direccio donada
puts 'Master playlist obtinguda'
processMaster(master) #obtenim les dades necessaries per HLS
puts 'Master playlist processada'
