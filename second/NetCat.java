import java.io.*;
import java.net.*;
import java.util.*;

public class NetCat {
    
    static void link(final Socket s) {
        
        //output thread
        new Thread() {
            public void run() {
                //Output
                /*try {
                  Scanner scanout = new Scanner(s.getInputStream());
                  while(true) System.out.println(s.getInputStream().read());
                } catch (IOException ex) {
                  System.err.println("Error in output");
                }*/    
                try {
                  Scanner scanout = new Scanner(s.getInputStream());
                  PrintWriter writeout = new PrintWriter(System.out);
                  while (scanout.hasNext()) {
                    writeout.print(scanout.next().charAt(0));
                  }
                } catch (IOException ex) {
                  System.err.println("Error in output");
                }             
            }
        }.start();
        
        //Input. tancar el socket en escriptura quan
        // es tanqui el teclat
        /*try {
            int b;
            while ((b = System.in.read()) != -1) {
                s.getOutputStream().write(b);
            }
            s.close();
        } catch (IOException ex){
            System.err.println("Error in input");
        } */
        try {            
            PrintWriter writer = new PrintWriter(s.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                writer.print(scanner.next().charAt(0));
            }
            s.close();
        } catch (IOException ex) {
            System.err.println("Error in input");
        }
    }
    
    public static void main(String args[]) throws IOException {
        if (args[0].equals("-1")) { //server
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
