import java.io.*;
import java.net.*;

public class NetCat {
    
    static void link(final Socket s) {
        
        // Output thread
        new Thread() {
            public void run() {
                Output.start(s);                            
            }
        }.start();
        
        // Input function     
        try {
            Input.start(s);
            s.shutdownInput();
        } catch(IOException ex){
            System.err.println("Error in input");
        }
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
