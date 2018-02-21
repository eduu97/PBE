def output
#Output function goes here
end

output_thread = Thread.new{output()}

#Input goes here

count=0;
while(char!=nil && count<3)
  char = STDIN.getch
  if(char=="$")
    count++;
  end
  ser.write(char)
end

#End of output thread
output_thread.join
