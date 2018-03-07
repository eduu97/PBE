import java.io.*;
import java.net.*;
import java.util.*;

public class Input {
    public static void start(final Socket s) {
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
}

/*
try {
  int b;
  while ((b = System.in.read()) != -1) {
    s.getOutputStream().write(b);
  }
  s.close();
  } catch (IOException ex){
  System.err.println("Error in input");
} */
