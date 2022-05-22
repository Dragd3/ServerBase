package server.main;

import classes.Usuari;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.bbdd.Accions;

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
        ObjectInputStream in = null;
        PrintStream dataOut = null;
        BufferedReader dataIn = null;
        Crides c = new Crides();
        Accions a = new Accions(this);
        sessio = new String();

        try {
            //Stream de sortida de dades
            dataOut = new PrintStream(client.getOutputStream());
            //Stream d'entrada de dades
            dataIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //Stream d'enviament d'objectes
            out = new ObjectOutputStream(client.getOutputStream());
            //Stream d'entrada d'objectes
            in = new ObjectInputStream(client.getInputStream());

            running = true;
            while (running) {
                System.out.println("Client connectat: " + client.getInetAddress());
                String line = dataIn.readLine();
                //Si line == ";" es tanca el socket
                while (!line.equals(";")) {
                    /*
                    "0" per a fer log out
                    "1" per a fer login
                    
                    "10" per a crear Usuari
                    "11" per a esborrar Usuari
                    "12" per a rebre Usuari
                    "14" per a canviar status d'admin
                    "15" per a modificar password, mail o data
                    
                    "20" per a crear Esdeveniment
                    "21" per a esborrar Esdeveniment
                    "22" per a rebre tots els Esdeveniments
                    "23" per a rebre Esdeveniments de forma parametritzada
                    "24" per a modificar un Esdeveniment
                    
                    "30" per a afegir un assistent
                    "31" per a eliminar un assistent
                    */
                    switch (line) {
                        case "0":
                            sessio = a.logout(sessio, dataOut, dataIn);
                            break;
                        case "1":
                            sessio = a.login(sessio, dataOut, dataIn);
                            break;
                        case "10":
                            a.createUser(dataOut, dataIn);
                            break;
                        case "11":
                            a.removeUsuari(sessio, dataOut, dataIn);
                            break;
                        case "12":
                            a.sendUsusari(sessio, dataOut, dataIn, out);
                            break;
                        case "14":
                            a.modifyAdmin(sessio, dataOut, dataIn);
                            break;
                        case "15":
                            a.modifyUsuari(sessio, dataOut, dataIn);
                            break;
                        case "20":
                            a.createEsdeveniment(sessio, dataOut, dataIn, in, out);
                            break;
                        case "21":
                            a.removeEsdeveniment(sessio, dataOut, dataIn);
                            break;
                        case "22":
                            a.showEsdeveniments(sessio, dataOut, dataIn, out);
                            break;
                        case "23":
                            a.showEsdevenimentsParam(sessio, dataOut, dataIn, out);
                            break;
                        case "24":
                            a.modifyEsdeveniments(sessio, dataOut, dataIn, in);
                            break;
                        case "30":
                            a.addAssist(sessio, dataOut, dataIn);
                            break;
                        case "31":
                            a.removeAssist(sessio, dataOut, dataIn);
                            break;
                        default:
                            dataOut.println("CODI_NOK");
                    }
                    line = dataIn.readLine();

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
     * Comprova si un Usuari esta a la llista de sessions
     * 
     * @param user String amb el nom d'Usuari a cercar
     * @return boolean indicant si ho esta o no
     */
    public boolean isUserOnline(String user) {
        for (Usuari u : sessions.values()) {
            if (u.getUser().equals(user)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna el codi d'un Usuari connectat
     * 
     * @param user String amb el nom d'Usuari a cercar
     * @return String del codi del Usuari. Null si no existeix
     */
    public String getCodi(String user) {
        for (String s : sessions.keySet()) {
            if (getUsuari(s).getUser().equals(user)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Retorna un Usuari basat en el codi de login
     *
     * @param codi sessio loguejada
     * @return Usuari que s'hi correspon
     */
    public Usuari getUsuari(String codi) {
        return sessions.get(codi);
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
