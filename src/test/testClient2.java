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
public class testClient2 {

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
            testClient2 c = new testClient2();


            codi = c.login("admin1", "admin", ps, bin);
            
            List<Esdeveniment> esdevs = new ArrayList<Esdeveniment>();
            esdevs = c.mostrarEsdevParam(codi, "6", "Prova", ps, bin, in);
            Esdeveniment e = esdevs.get(0);
            
            if (c.afegirGoer(codi, "" + e.getId(), ps, bin)) {
                System.out.println("Prova AFEGIR_GOER: OK");
            } else {
                System.out.println("Prova AFEGIR_GOER: ERROR");
            }

            if (c.treureGoer(codi, "" + e.getId(), ps, bin)) {
                System.out.println("Prova TREURE_GOER: OK");
            } else {
                System.out.println("Prova TREURE_GOER: ERROR");
            }
            
            if(c.modificarUsuari(codi, "Test1", 2, "mail@deprova.ok", ps, bin)){
                System.out.println("Prova MODIFICAR_USUARI_ADMIN: OK");
            }else{
                System.out.println("Prova MODIFICAR_USUARI_ADMIN: ERROR");
            }
            if(c.canviarAdmin(codi, "Test1", ps, bin)){
                System.out.println("Prova MODIFICAR_PERMISOS: OK");
            } else{
                System.out.println("Prova MODIFICAR_PERMISOS: ERROR");
            }
            
            c.logout(codi, ps, bin);

            codi = c.login("Test1", "987654321", ps, bin);
            
            if(c.esborrarUsuari(codi, "Test1", ps, bin)){
                System.out.println("Prova ESBORRAR_USUARI: OK");
            } else{
                System.out.println("Prova ESBORRAR USUARI: ERROR");
            }
//            
            ps.println(";");

        } catch (IOException ex) {
            Logger.getLogger(testClient2.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(testClient2.class.getName()).log(Level.SEVERE, null, ex);
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
        bin.readLine();
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
                Logger.getLogger(testClient2.class.getName()).log(Level.SEVERE, null, ex);
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
        out.writeObject(e);
        out.flush();
        String reply = bin.readLine();
        if (reply.equals("READY")) {
            try {
                Usuari u = (Usuari) in.readObject();
                return u;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(testClient2.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(testClient2.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(testClient2.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println(reply);
        }
        return null;
    }

    public boolean modificarEsdev(String codi, Esdeveniment e, PrintStream ps, BufferedReader bin, ObjectOutputStream out) throws IOException {
        ps.println(24);
        ps.println(codi);
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
