import java.io.*;
import java.net.*;

public class NetCat {
    
    static void link(final Socket s) {
        
        // Output thread
        new Thread() {
            public void run() {
                int b;
                try{
                    InputStream in = s.getInputStream();
                    while((b = in.read()) != -1)
                        System.out.write(b);
                    System.out.println("Server closed Connection");
                    System.out.close();
                } catch (IOException e){}
            }
        }.start();
        
        // Input function 
        int b;    
        try {
            OutputStream out = s.getOutputStream();
            while((b = System.in.read()) != -1){
                out.write(b);
            }
            s.shutdownOutput();
        } catch(IOException e){}
    }
    
    public static void main(String args[]) throws IOException {
        if (args[0].equals("-l")) { //server
            ServerSocket ss = new ServerSocket(Integer.parseInt(args[1]));            
            Socket s = ss.accept();
            link(s);        
            ss.close();            
        } else { //client
            Socket s = new Socket(args[0], Integer.parseInt(args[1]));
            link(s);
        }
    }
    
}
