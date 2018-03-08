import java.io.*;
import java.net.*;

public class Input {
    public static void start(final Socket s) {
        try {   
          int b;
          while ((b = System.in.read()) != -1) s.getOutputStream().write(b);
          s.shutdownInput();
        } catch (IOException ex){
            System.err.println("Error in input");
            ex.printStackTrace();
        }
    }
}
