def output
#Output function goes here
end

output_thread = Thread.new{output()}

#Input goes here
#Test

count=0;
while(char!=nil && count<3)
  char = STDIN.getc
  if(char=="$")
    count++;
  end
  ser.write(char)
end

#End of output thread
output_thread.join
