/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tomas.ortega
 */

import java.io.*;
import java.net.*;

public class NetCat {
    
    static void link(final Socket s) {
        
        //output thread
        new Thread() {
            @Override
            public void run() {
                //Output
            }
        }.start();
        
        //Input. tancar el socket en escriptura quan
        // es tanqui el teclat
        try {
            int b;
            while ((b = System.in.read()) != -1) {
                s.getOutputStream().write(b);
            }
            s.close();
        } catch (IOException ex){
            System.err.println("Error in input");
        } 
        /*alternativament
        try {            
            PrintWriter writer = new PrintWriter(s.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                writer.print(scanner.next().charAt(0));
            }
            s.close();
        } catch (IOException ex) {
            System.err.println("Error in input");
        }*/
    }
    
    public static void main(String args[]) throws IOException {
        if (args[0].equals("-1")) { //server
            ServerSocket ss = new ServerSocket(Integer.parseInt(args[1]));
            
            Socket s = ss.accept();
            link(s);
        } else { //client
            Socket s = new Socket(args[0], Integer.parseInt(args[1]));
            link(s);
        }
    }
    
}
