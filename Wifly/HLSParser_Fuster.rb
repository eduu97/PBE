require 'net/http' #net/http ja requereix 'uri'

uri = URI(ARGV[0])
playlist = Net::HTTP.get(uri) #descarreguem l'arxiu de la uri

if (isMaster(playlist))
  #ya tal

else
  matches = playlist.scan(/#EXTINF:\d+.?\d*/)  #màgia regex
  durations = []  #no se segur si cal declarar
  for a in matches
    puts a
    durations.push(a.split(":")[1])  #printem i guardem els valors de duracio per l'algoritme HLS
  end
end

def isMaster(playlist){
  return playlist.include? "EXT-X-MEDIA" or playlist.include? "EXT-X-STREAM-INF"  #si te les etiquetes aquestes, es master
}
