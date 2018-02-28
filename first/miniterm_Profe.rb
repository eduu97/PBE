require 'serialport'

if ARGV.size != 5
	STDERR.print <<EOF
	Usage: ruby #{$0} num_port bps nbits stopb parity
EOF
	exit(1)
end

parity = case ARGV[4]
  when "E"
    SerialPort::EVEN
  when "O"
    SerialPort::ODD
  when "N"
    SerialPort::NONE
end

sp = SerialPort.new(ARGV[0], ARGV[1].to_i, ARGV[2].to_i, ARGV[3].to_i, parity)

STDOUT.sync = true
Thread.new{
	while (true) do
		STDOUT.putc(sp.getc)
	end
}

while ((char = STDIN.getc) != ?\C-D) do
	sp.putc(char)
end

sp.close
