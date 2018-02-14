def output
#Output function goes here
end

output_thread = Thread.new{output()}

#Input goes here


#End of output thread
output_thread.join
