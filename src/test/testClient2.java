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
public class clientConsola {

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
            clientConsola c = new clientConsola();

            Esdeveniment e = new Esdeveniment("Esdeveniment prova",
                    "Prova a Hospitalet",
                    "UsuariProva",
                    "2022-10-10",
                    "Proves",
                    "Hospitalet",
                    8);

//            c.crearUsuari("UsuariProva", "123456789", "1990-09-09", "usuari@prova.up", ps, bin);
//            codi = c.login("UsuariProva", "123456789", ps, bin);
//            c.buscarUsuari(codi, ps, bin, in);
//            c.canviarAdmin(codi, "UsuariProva", ps, bin);

            List<Esdeveniment> esdevs = new ArrayList<Esdeveniment>();
//            esdevs = c.mostrarEsdevParam(codi, "6", "pital", ps, bin, in);
//            System.out.println(esdevs);
//            e = esdevs.get(0);
//            e.setLloc("Montcada");
//            c.modificarEsdev(codi, e, ps, bin, out);
//            esdevs = c.mostrarEsdev(codi, ps, bin, in);
//            System.out.println(esdevs);
//            c.modificarUsuari(codi, "UsuariProva", 1, "987654321", ps, bin);
//            c.buscarUsuari(codi, ps, bin, in);
//            c.logout(codi, ps, bin);
            
            codi = c.login("admin1", "admin", ps, bin);
//            esdevs = c.mostrarEsdevParam(codi, "6", "pital", ps, bin, in);
//            System.out.println(esdevs);
//            e = esdevs.get(0);
//            e.setLloc("Montcada");
//            c.modificarEsdev(codi, e, ps, bin, out);
//            c.afegirGoer(codi, ""+e.getId(), ps, bin);
//            esdevs = c.mostrarEsdevParam(codi, "3", "UsuariProva", ps, bin, in);
//            System.out.println(esdevs);
//            c.treureGoer(codi, ""+e.getId(), ps, bin);
//            esdevs = c.mostrarEsdevParam(codi, "5", "Proves", ps, bin, in);
//            System.out.println(esdevs);
//            c.esborrarEsdev(codi, e.getId(), ps, bin);
//            esdevs = c.mostrarEsdev(codi, ps, bin, in);
//            System.out.println(esdevs);
            c.modificarUsuari(codi, "UsuariProva", 2, "mail@deprova.ok", ps, bin);
            c.canviarAdmin(codi, "UsuariProva", ps, bin);
            c.logout(codi, ps, bin);
            codi = c.login("UsuariProva", "987654321", ps, bin);
            c.esborrarUsuari(codi, "UsuariProva", ps, bin);
            
            ps.println(";");

        } catch (IOException ex) {
            Logger.getLogger(clientConsola.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(clientConsola.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String login(String user, String pass, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(1);
        ps.println(user);
        ps.println(pass);
        String codi = bin.readLine();
        System.out.println(codi);
        return codi;
    }

    public void logout(String codi, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(0);
        ps.println(codi);
        System.out.println(bin.readLine());
    }

    public void crearUsuari(String name, String pwd, String birth, String mail,
            PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(10);
        ps.println(name);
        ps.println(pwd);
        ps.println(birth);
        ps.println(mail);
        System.out.println(bin.readLine());
    }
    
    public void esborrarUsuari(String codi, String user, PrintStream ps, BufferedReader bin) throws IOException{
        ps.println(11);
        ps.println(codi);
        ps.println(user);
        System.out.println(bin.readLine());
        
    }

    public void buscarUsuari(String codi, PrintStream ps, BufferedReader bin, ObjectInputStream in) throws IOException {
        ps.println(12);
        ps.println(codi);
        if (bin.readLine().equals("READY")) {
            try {
                System.out.println(in.readObject());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(clientConsola.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void canviarAdmin(String codi, String usuari, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(14);
        ps.println(codi);
        ps.println(usuari);
        System.out.println(bin.readLine());
    }

    public void modificarUsuari(String codi, String user, int param, String value, PrintStream ps, BufferedReader bin) throws IOException{
        ps.println(15);
        ps.println(codi);
        ps.println(user);
        ps.println(param);
        ps.println(value);
        System.out.println(bin.readLine());
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
                Logger.getLogger(clientConsola.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println(reply);
        return null;
    }
    
    public void esborrarEsdev(String codi, int id, PrintStream ps, BufferedReader bin) throws IOException{
        ps.println(21);
        ps.println(codi);
        ps.println(id);
        System.out.println(bin.readLine());
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
                Logger.getLogger(clientConsola.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(clientConsola.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println(reply);
        }
        return null;
    }
    
    public void modificarEsdev(String codi, Esdeveniment e, PrintStream ps, BufferedReader bin, ObjectOutputStream out) throws IOException {
        ps.println(24);
        ps.println(codi);
        out.writeObject(e);
        out.flush();
        System.out.println(bin.readLine());
    }

    public void afegirGoer(String codi, String id, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(30);
        ps.println(codi);
        ps.println(id);
        System.out.println(bin.readLine());
    }

    public void treureGoer(String codi, String id, PrintStream ps, BufferedReader bin) throws IOException {
        ps.println(31);
        ps.println(codi);
        ps.println(id);
        System.out.println(bin.readLine());
    }
}
