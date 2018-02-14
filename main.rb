def output
#Output function goes here
end

output_thread = Thread.new{output()}

#End of output thread
output_thread.join
