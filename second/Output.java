import java.io.*;
import java.net.*;


public class Output {
  public static void start(final Socket s) {
    try {
      int b;    
      while ((b = s.getInputStream().read()) != -1) System.out.print((char) b);
    } catch (IOException ex) {
      System.err.println("Error in output");
      ex.printStackTrace();
    }
  }       
}
