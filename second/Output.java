       
import java.io.*;
import java.net.*;
import java.util.*;

public class Output {
    public static void start(final Socket s) {
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
}

/*try {
  Scanner scanout = new Scanner(s.getInputStream());
  while(true) System.out.println(s.getInputStream().read());
} catch (IOException ex) {
  System.err.println("Error in output");
}*/    
