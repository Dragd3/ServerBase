package server.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author EMiF
 */
public class Servidor {

    //port de connexio amb el client, es pot canviar
    static int port = 6666;
    
    public static void main(String[] args) throws IOException {

        Socket socket = null;
        ServerSocket sk = null;
        ConnClient server;
        int count = 0;

        //Connexio basica per sockets
        try {
            sk = new ServerSocket(port);
            while (true) {
                //Bucle per acceptar clients, generar thread i passar-li
                socket = sk.accept();
                server = new ConnClient(socket);
                server.start();
                server.setName(""+count);
                count++;
            }
        } catch (Exception e) {

        } finally {
            try {
                socket.close();
                sk.close();
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
