import java.io.*;
import java.net.*;

public class Input {
    public static void start(final Socket s) throws IOException{
        int b;
        while ((b = System.in.read()) != -1) {
            try {
                s.getOutputStream().write(b);
            } catch (IOException ex) {
                // In case the pipe is broken, the sent bytes are lost
            }
        }
    }
}
