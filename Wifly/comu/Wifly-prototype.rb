require 'serialport'
require 'uri'

def command(comm, resp)
    serial.print(comm + '\r')
    serial.expect(resp)
end

def readPag(seg_tmp) #Esta parte no la entiendo del todo bien porque me la he mirado de lo que paso Luis
	for linea in string.split('\n')
		case '#EXT-X-STREAM-INF' in seg_tmp
			when (linea = '/lo/pro')
				dicc_seq['lo']['BANDWIDTH'] = string.split('\n')[i-1].split('BANDWIDTH=')[1].split(',')[0]
                dicc_seq['lo']['url_playlist'] = linea
            when (linea = '/me/pro')
            	icc_seq['me']['BANDWIDTH'] = string.split('\n')[i - 1].split('BANDWIDTH=')[1].split(',')[0]
                dicc_seq['me']['url_playlist'] = linea
            when (linea = '/hi/pro')
            	icc_seq['hi']['BANDWIDTH'] = string.split('\n')[i - 1].split('BANDWIDTH=')[1].split(',')[0]
                dicc_seq['hi']['url_playlist'] = linea
		end

		case '/lo/file' in seg_tmp
			when (linea = "EXT-X-TARGETDURATION")
                dicc_seq['lo']['DURATION'] = linea.split(':')[1]
            when (linea = "EXT-X-VERSION")
                dicc_seq['lo']['VERSION'] = linea.split(':')[1]
            when (linea = "EXT-X-MEDIA-SEQUENCE")
                dicc_seq['lo']['SEQUENCE'] = linea.split(':')[1]
            when (liena = "EXT-X-PLAYLIST-TYPE")
                dicc_seq['lo']['TYPE'] = linea.split(':')[1]
        end

        case '/me/file' in seg_tmp
        	when (linea = "EXT-X-TARGETDURATION")
                dicc_seq['me']['DURATION'] = linea.split(':')[1]
            when (linea = "EXT-X-VERSION")
                dicc_seq['me']['VERSION'] = linea.split(':')[1]
            when (linea = "EXT-X-MEDIA-SEQUENCE")
                dicc_seq['me']['SEQUENCE'] = linea.split(':')[1]
            when (liena = "EXT-X-PLAYLIST-TYPE")
                dicc_seq['me']['TYPE'] = linea.split(':')[1]
        end	
        
        case '/hi/file' in seg_tmp
        	when (linea = "EXT-X-TARGETDURATION")
                dicc_seq['hi']['DURATION'] = linea.split(':')[1]
            when (linea = "EXT-X-VERSION")
                dicc_seq['hi']['VERSION'] = linea.split(':')[1]
            when (linea = "EXT-X-MEDIA-SEQUENCE")
                dicc_seq['hi']['SEQUENCE'] = linea.split(':')[1]
            when (liena = "EXT-X-PLAYLIST-TYPE")
                dicc_seq['hi']['TYPE'] = linea.split(':')[1]
        end
        i = i + 1 #avançem linea        
	end	
end

port = '/dev/ttyUSB0'
serial = Serial(port, 9600, 8, 1, SerialPort::NONE)

#Entrar mode comandes
serial.print('$$$')
serial.expect('CMD')
command('reboot', 'READY') #reniciar 

ipDir = ARGV[0].host

#Conectar WIFLY al server
command('set ip host ' + ipDir , 'AOK')  # ip del server
command('set ip remote 80', 'AOK')  # port del server
command('set ip protocol 18', 'AOK')  # HTTP client TCP client i server

#Salir del modo comandes
serial.print('\n')
serial.expect('EXIT')

#Legim dades
seg_tmp = serial.read(read.split(': ').[1]) #Llegim "capcelera"
readPag(seg_tmp)

#imprimir per consola dades
print ('duracio seq lo' + dicc_seq['lo']['INF'][16] + '\n')
 
print ('duracio seq me' + dicc_seq['me']['INF'][16] + '\n')
 
print ('duracio seq hi' + dicc_seq['hi']['INF'][16] + '\n')






