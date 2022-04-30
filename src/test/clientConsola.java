
package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import classes.Usuari;

/**
 *
 * @author EMiF
 */
public class clientConsola {

    public static void main(String[] args) {
        ObjectInputStream in = null;
        try {
            //Connexio al servidor
            String addr = "localhost";
            int port = 6666;
            Socket socket = new Socket(addr, port);

            //lectors i escritors per al servidor
            PrintStream out = new PrintStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Scanner scann = new Scanner(System.in);
            while (scann.hasNextLine()) {
                String data = scann.nextLine();
                out.println(data);
                String pr = bin.readLine();
                if (pr.equals("READY")) {
                    try {
                        System.out.println(in.readObject());
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    System.out.println(pr);
                }
                if (pr.equals("DISCONNECT")) {
                    socket.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(clientConsola.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(clientConsola.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
