package test;

import classes.Esdeveniment;
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
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Binding;

/**
 *
 * @author EMiF
 */
public class testClient {

    public static void main(String[] args) {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            //Connexio al servidor
            String addr = "localhost";
            int port = 6666;
            Socket socket = new Socket(addr, port);

            //lectors i escritors per al servidor
            PrintStream ps = new PrintStream(socket.getOutputStream());
            BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            String codi;
            testClient c = new testClient();
            /*
            Esdeveniment e = new Esdeveniment("Esdeveniment prova",
                    "Prova a Barcelona",
                    "Test1",
                    "2022-10-10",
                    "Proves",
                    "Hospitalet",
                    8);

            if (c.crearUsuari("Test1", "123456789", "1990-03-03", "test@prova.up", ps, bin)) {
                System.out.println("Prova CREAR_USUARI: OK");
            } else {
                System.out.println("Prova CREAR_USUARI: ERROR");
            }

            codi = c.login("Test1", "123456789", ps, bin);

            if (c.buscarUsuari(codi, ps, bin, in)) {
                System.out.println("Prova BUSCAR_USUARI: OK");
            } else {
                System.out.println("Prova BUSCAR_USUARI: ERROR");
            }

            //No ha de poder canviarAdmin, doncs no te permisos
            if (!c.canviarAdmin(codi, "UsuariProva", ps, bin)) {
                System.out.println("Prova CANVIAR_ADMIN: OK");
            } else {
                System.out.println("Prova CANVIAR_ADMIN: ERROR");
            }

            Usuari u = c.crearEsdev(codi, e, ps, bin, out, in);
            if (u != null){
                System.out.println("Prova CREAR_ESDEVENIMENT: OK");
            } else{
                System.out.println("Prova CREAR_ESDEVENIMENT: ERROR");
            }
            
            List<Esdeveniment> esdevs = new ArrayList<Esdeveniment>();
            esdevs = c.mostrarEsdev(codi, ps, bin, in);

            if (esdevs != null) {
                System.out.println("Prova LLISTAR_ESDEVENIMENTS: OK");
            } else {
                System.out.println("Prova LLISTAR_ESDEVENIMENTS: ERROR");
            }

            esdevs = c.mostrarEsdevParam(codi, "6", "pita", ps, bin, in);
            if (esdevs != null) {
                System.out.println("Prova LLISTAR_ESDEVENIMENTS_PARAM: OK");
            } else {
                System.out.println("Prova LLISTAR_ESDEVENIMENTS_PARAM: ERROR");
            }
             */

            codi = c.login("Test1", "123456789", ps, bin);
            List<Esdeveniment> esdevs = new ArrayList<Esdeveniment>();
            esdevs = c.mostrarEsdev(codi, ps, bin, in);
            
            Esdeveniment e = esdevs.get(0);
            e.setLloc("CiutatdeProva");
            if (c.modificarEsdev(codi, e, ps, bin, out)) {
                System.out.println("Prova MODIFICAR_ESDEVENIMENTS: OK");
            } else {
                System.out.println("Prova MODIFICAR_ESDEVENIMENTS: ERROR");
            }
/*
            if (c.modificarUsuari(codi, "Test1", 1, "987654321", ps, bin)) {
                System.out.println("Prova MODIFICAR_USUARI: OK");
            } else {
                System.out.println("Prova MODIFICAR_USUARI: ERROR");
            }
*/
            c.logout(codi, ps, bin);

            ps.println(";");

        } catch (IOException ex) {
            Logger.getLogger(testClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(testClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String login(String user, String pass, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(1);
        ps.println(user);
        ps.println(pass);
        String codi = bin.readLine();
        return codi;
    }

    public void logout(String codi, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(0);
        ps.println(codi);
    }

    public boolean crearUsuari(String name, String pwd, String birth, String mail,
            PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(10);
        ps.println(name);
        ps.println(pwd);
        ps.println(birth);
        ps.println(mail);
        String reply = bin.readLine();
        if (reply.equals("OK")) {
            return true;
        }
        return false;
    }

    public boolean esborrarUsuari(String codi, String user, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(11);
        ps.println(codi);
        ps.println(user);
        String reply = bin.readLine();
        if (reply.equals("OK")) {
            return true;
        }
        return false;
    }

    public boolean buscarUsuari(String codi, PrintStream ps, BufferedReader bin, ObjectInputStream in) throws IOException {
        ps.println(12);
        ps.println(codi);
        if (bin.readLine().equals("READY")) {
            try {
                in.readObject();
                //System.out.println(in.readObject());
                return true;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(testClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public boolean canviarAdmin(String codi, String usuari, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(14);
        ps.println(codi);
        ps.println(usuari);
        String reply = bin.readLine();
        if (reply.equals("USER") || reply.equals("ADMIN")) {
            return true;
        }
        return false;
    }

    public boolean modificarUsuari(String codi, String user, int param, String value, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(15);
        ps.println(codi);
        ps.println(user);
        ps.println(param);
        ps.println(value);
        String reply = bin.readLine();
        if (reply.equals("OK")) {
            return true;
        }
        return false;
    }

    public Usuari crearEsdev(String codi, Esdeveniment e, PrintStream ps, BufferedReader bin, ObjectOutputStream out, ObjectInputStream in) throws IOException {
        ps.println(20);
        ps.println(codi);
        ps.println("READY");
        ps.flush();
        out.writeObject(e);
        out.flush();
        String reply = bin.readLine();
        if (reply.equals("READY")) {
            try {
                Usuari u = (Usuari) in.readObject();
                return u;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(testClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println(reply);
        return null;
    }

    public boolean esborrarEsdev(String codi, int id, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(21);
        ps.println(codi);
        ps.println(id);
        String reply = bin.readLine();
        if (reply.equals("OK")) {
            return true;
        }
        return false;
    }

    public List<Esdeveniment> mostrarEsdev(String codi, PrintStream ps, BufferedReader bin, ObjectInputStream in) throws IOException {
        ps.println(22);
        ps.println(codi);
        String reply = bin.readLine();
        if (reply.equals("READY")) {
            try {
                List<Esdeveniment> esdev = (ArrayList) in.readObject();
                return esdev;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(testClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println(reply);
        }
        return null;
    }

    public List<Esdeveniment> mostrarEsdevParam(String codi, String p, String v, PrintStream ps, BufferedReader bin, ObjectInputStream in) throws IOException {
        ps.println(23);
        ps.println(codi);
        ps.println(p);
        ps.println(v);
        String reply = bin.readLine();
        if (reply.equals("READY")) {
            try {
                List<Esdeveniment> esdev = (ArrayList) in.readObject();
                return esdev;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(testClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println(reply);
        }
        return null;
    }

    public boolean modificarEsdev(String codi, Esdeveniment e, PrintStream ps, BufferedReader bin, ObjectOutputStream out) throws IOException {
        ps.println(24);
        ps.println(codi);
        ps.println("READY");
        ps.flush();
        out.writeObject(e);
        out.flush();
        String reply = bin.readLine();
        if (reply.equals("OK")) {
            return true;
        }
        return false;
    }

    public boolean afegirGoer(String codi, String id, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(30);
        ps.println(codi);
        ps.println(id);
        String reply = bin.readLine();
        if (reply.equals("OK")) {
            return true;
        }
        return false;
    }

    public boolean treureGoer(String codi, String id, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(31);
        ps.println(codi);
        ps.println(id);
        String reply = bin.readLine();
        if (reply.equals("OK")) {
            return true;
        }
        return false;
    }
}
