package server.main;

import classes.Usuari;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.bbdd.Connectivitat;

import server.bbdd.Crides;

/**
 *
 * @author EMiF
 */
public class ConnClient extends Thread {

    //Llista de sessions obertes al servidor
    static HashMap<String, Usuari> sessions = new HashMap<String, Usuari>();

    private Socket client;

    private boolean running = false;
    private String sessio;

    public ConnClient(Socket socket) {
        this.client = socket;
    }

    /**
     * Anima del servidor, gestiona les peticions del client
     */
    @Override
    public void run() {
        ObjectOutputStream out = null;
        BufferedReader in = null;
        PrintStream dataOut = null;
        Crides c = new Crides();
        sessio = new String();

        try {
            //Stream d'entrada de dades
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //Stream de sortida de dades
            dataOut = new PrintStream(client.getOutputStream());
            //Stream d'enviament d'objectes
            out = new ObjectOutputStream(client.getOutputStream());
            String codi;
            running = true;
            while (running) {
                System.out.println("Client connectat: " + client.getInetAddress());
                String line = in.readLine();
                //Si line == ";" es tanca el socket
                while (!line.equals(";")) {
                    switch (line) {
                        //Envia "0" per a fer log out
                        case "0":
                            //Si el client ha fet login, s'esborra memoria, es
                            //deslogueja i envia "LOGOUT"
                            if (!sessio.isEmpty()) {
                                dataOut.println("CODI");
                                codi = in.readLine();
                                if (removeSession(codi)) {
                                    sessio = new String();
                                    dataOut.println("LOGGEDOUT");
                                } else {
                                    dataOut.println("LOGNOTFOUND");
                                }
                            } else {
                                //Envia "NOLOGGED" si el client no ha fet login
                                dataOut.println("NOLOGGED");
                            }
                            break;
                        //Enviar "1" per a fer login i despres credencials
                        case "1":
                            if (sessio.isEmpty()) {
                                dataOut.println("user");
                                String user = in.readLine();
                                dataOut.println("password");
                                String pass = in.readLine();
                                Usuari us;
                                //Si existeix Usuari, es genera codi de sessio,
                                //s'emmagatzema i s'envia al client.
                                //"NOK" si no existeix. "LOGGED" si ja te sessio
                                if (c.login(user, pass) != null) {
                                    us = c.login(user, pass);
                                    sessio = generarCodi(us);
                                    if (putSession(sessio, us)) {
                                        dataOut.println(sessio);
                                    } else {
                                        sessio = new String();
                                        dataOut.println("ALREADY_LOGGED");
                                    }
                                } else {
                                    dataOut.println("NOK");
                                }
                            } else {
                                dataOut.println("ALREADY_LOGGED");
                            }
                            break;
                        //Enviar "2" per a crear usuari i despres dades
                        case "2":
                            dataOut.println("user");
                            String name = in.readLine();
                            dataOut.println("password");
                            String pwd = in.readLine();
                            dataOut.println("data de naixement (yyyy-MM-dd)");
                            String birth = in.readLine();
                            dataOut.println("mail");
                            String mail = in.readLine();
                            switch (c.create(name, pwd, mail, birth)) {
                                case 0:
                                    //retorna "EXISTEIX" si existeix usuari o mail
                                    dataOut.println("EXISTEIX");
                                    break;
                                case 1:
                                    dataOut.println("OK");
                                    break;
                                case -1:
                                    dataOut.println("NOK");
                                    break;
                            }
                            break;
                        case "3":
                        dataOut.println("CODI");
                        codi = in.readLine();
                        if (sessio.equals(codi)) {
                            dataOut.println("OK");
                            if (in.readLine().equals("READY")) {
                            out.writeObject(sessions.get(codi));
                            out.flush();
                            }
                        }else{
                            dataOut.println("NOK");
                        }
                        default: 
                            dataOut.println("CODI_NOK");
                    }
                    line = in.readLine();

                }
                //Si line == ";" desconecta socket. Envia "DISCONNECT" al client
                if (line.equals(";")) {
                    dataOut.println("DISCONNECT");
                    shutDown();
                }
            }
        } catch (IOException e) {
            removeSession(sessio);
            System.out.println("Client " + client.getInetAddress() + " s'ha tancat inesperadament");
        }
    }

    /**
     * Desconecta el client del servidor
     */
    public void shutDown() {
        try {
            if (client != null && !client.isClosed()) {
                System.out.println("Client desconnectat: " + client.getInetAddress());
                removeSession(sessio);
                client.close();
            }
        } catch (IOException e) {
            Logger.getLogger(ConnClient.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            running = false;
        }
    }

    /**
     * Posa la sessio al HashMap
     *
     * @param codi codi de la sessio
     * @param user usuari vinculat a la sessio
     * @return boolean si s'ha inclos la sessio
     */
    public synchronized boolean putSession(String codi, Usuari user) {
        if (sessions.containsKey(codi)) {
            return false;
        } else {
            for (Usuari u : sessions.values()) {
                if (u.toString().equals(user.toString())) {
                    return false;
                }
            }
            sessions.put(codi, user);
            return true;
        }
    }

    /**
     * Treu una sessio de la llista
     *
     * @param codi sessio que es vol esborrar
     * @return boolean si s'ha el·liminat la sessio del HashMap
     */
    public synchronized boolean removeSession(String codi) {
        if (sessions.containsKey(codi)) {
            sessions.remove(codi);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Genera un codi d'autenticacio començat per 0 si es admin o 1 si es user
     *
     * @param user usuari vinculat a la sessio
     * @return String del codi o -1 si error
     */
    public synchronized String generarCodi(Usuari user) {
        String codi = "-1";
        boolean existeix = true;
        while (existeix) {
            if (user.isAdmin()) {
                codi = "0" + (int) Math.floor(Math.random() * 100000);
            } else {
                codi = "1" + (int) Math.floor(Math.random() * 100000);
            }
            existeix = sessions.containsKey(codi);
        }
        return codi;
    }

}
