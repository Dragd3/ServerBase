package server.bbdd;

import classes.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import static java.lang.Integer.parseInt;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.main.ConnClient;

/**
 *
 * @author EMiF
 */
public class Accions {

    ConnClient serv;

    public Accions(ConnClient s) {
        this.serv = s;
    }
    Crides c = new Crides();

    public String login(String sessio, PrintStream ps, BufferedReader in) throws IOException {
        String user = in.readLine();
        String pwd = in.readLine();
        if (sessio.isEmpty()) {
            Usuari us = c.login(user, pwd);
            if (us != null) {
                String codi = serv.generarCodi(us);
                if (serv.putSession(codi, us)) {
                    ps.println(codi);
                    return codi;
                }
            } else {
                ps.println("NOK");
                return "";
            }
        }
        ps.println("ALREADY_LOGGED");
        return "";
    }

    public String logout(String sessio, PrintStream ps, BufferedReader in) throws IOException {
        String codi = in.readLine();
        if (sessio.equals(codi)) {
            if (serv.removeSession(codi)) {
                ps.println("LOGGEDOUT");
            } else {
                ps.println("LOGNOTFOUND");
            }
        } else {
            ps.println("ERROR_SESSIO");
            return sessio;
        }
        return "";
    }

    public void createUser(PrintStream ps, BufferedReader in) throws IOException {
        String name = in.readLine();
        String pwd = in.readLine();
        String birth = in.readLine();
        String mail = in.readLine();
        int reply = c.create(name, pwd, mail, birth);
        switch (reply) {
            case 0:
                ps.println("EXISTEIX");
                break;
            case 1:
                ps.println("OK");
                break;
            case -1:
                ps.println("NOK");
                break;
        }
    }

    public void createEsdeveniment(String sessio, PrintStream ps, BufferedReader in,
            ObjectInputStream bin, ObjectOutputStream out) throws IOException {
        try {
            String codi = in.readLine();
            Esdeveniment e = (Esdeveniment) bin.readObject();
            if (sessio.equals(codi)) {
                int reply = c.createEvent(e);
                if (reply == -1) {
                    ps.println("NOK");
                } else {
                    Usuari us = c.actualizeOwneds(serv.getUsuari(codi));
                    us = c.actualizeAssistences(us);
                    serv.removeSession(codi);
                    serv.putSession(codi, us);
                    ps.println("READY");
                    out.writeObject(us);
                    out.flush();
                }
            } else {
                ps.println("ERROR_SESSIO");
            }
        } catch (ClassNotFoundException ex) {
            ps.println("ESDEVENIMENT_NOK");
        }
    }

    public void sendUsusari(String sessio, PrintStream ps, BufferedReader in,
            ObjectOutputStream out) throws IOException {
        String codi = in.readLine();
        if (sessio.equals(codi)) {
            ps.println("READY");
            out.writeObject(serv.getUsuari(codi));
            out.flush();
        } else {
            ps.println("ERROR_SESSIO");
        }
    }

    public void showEsdeveniments(String sessio, PrintStream ps,
            BufferedReader in, ObjectOutputStream out) throws IOException {
        String codi = in.readLine();
        if (sessio.equals(codi)) {
            List<Esdeveniment> esdevs = c.showEsdeveniments();
            if (esdevs != null) {
                ps.println("READY");
                out.writeObject(esdevs);
            } else {
                System.out.println("NOK");
            }
        } else {
            ps.println("ERROR_SESSIO");
        }
    }

    public void showEsdevenimentsParam(String sessio, PrintStream ps,
            BufferedReader in, ObjectOutputStream out) throws IOException {
        String codi = in.readLine();
        String param = in.readLine();
        String value = in.readLine();
        if (sessio.equals(codi)) {
            List<Esdeveniment> esdevs = c.showEsdevenimentsParam(parseInt(param),
                    value);
            if (!esdevs.isEmpty()) {
                ps.println("READY");
                out.writeObject(esdevs);
                out.flush();
            } else {
                ps.println("NOK");
            }
        } else {
            ps.println("ERROR_SESSIO");
        }
    }

    public void modifyAdmin(String sessio, PrintStream ps, BufferedReader in) throws IOException {
        String codi = in.readLine();
        String user = in.readLine();
        if (sessio.equals(codi)) {
            if (serv.getUsuari(sessio).isAdmin()) {
                int reply = c.changeAdmin(user);
                switch (reply) {
                    case 0:
                        ps.println("USER");
                        break;
                    case 1:
                        ps.println("ADMIN");
                        break;
                    case -1:
                        ps.println("NOK");
                        break;
                }
            } else {
                ps.println("NOADMIN");
            }
        } else {
            ps.println("ERROR_SESSIO");
        }
    }

    public void modifyUsuari(String sessio, PrintStream ps, BufferedReader in) throws IOException {
        String codi = in.readLine();
        String user = in.readLine();
        int param = parseInt(in.readLine());
        String value = in.readLine();
        boolean change = false;
        if (sessio.equals(codi) && (serv.getUsuari(sessio).isAdmin()
                || serv.getUsuari(codi).getUser().equals(user))) {
            switch (param) {
                case 1:
                    change = c.modifyPwd(c.showUsuari(user).getUser(), value);
                    break;
                case 2:
                    change = c.modifyMail(c.showUsuari(user).getUser(), value);
                    break;
                case 3:
                    change = c.modifyDate(c.showUsuari(user).getUser(), value);
                    break;
            }
            if (change) {
                if (serv.isUserOnline(user)) {
                    String online = serv.getCodi(user);
                    serv.removeSession(online);
                    serv.putSession(online, c.showUsuari(user));
                }
                ps.println("OK");
            } else {
                ps.println("NOK");
            }
        } else {
            ps.println("ERROR_SESSIO");
        }
    }

    public void modifyEsdeveniments(String sessio, PrintStream ps, BufferedReader in,
            ObjectInputStream bin) throws IOException {
        try {
            String codi = in.readLine();
            Esdeveniment e = (Esdeveniment) bin.readObject();
            if (sessio.equals(codi) && (serv.getUsuari(codi).isAdmin()
                    || serv.getUsuari(codi).getEsvedeniments().contains(e.getId()))) {
                if (c.modifyEvent(e)) {
                    ps.println("OK");
                } else {
                    ps.println("NOK");
                }
            } else {
                ps.println("ERROR_SESSIO");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Accions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addAssist(String sessio, PrintStream ps, BufferedReader in) throws IOException {
        String codi = in.readLine();
        String id = in.readLine();
        if (sessio.equals(codi)) {
            if (c.addGoer(parseInt(id), serv.getUsuari(codi).getUser())) {
                ps.println("OK");
            } else {
                ps.println("NOK");
            }
        } else {
            ps.println("ERROR_SESSIO");
        }
    }

    public void removeAssist(String sessio, PrintStream ps, BufferedReader in) throws IOException {
        String codi = in.readLine();
        String id = in.readLine();
        if (sessio.equals(codi)) {
            if (c.removeGoer(parseInt(id), serv.getUsuari(codi).getUser())) {
                ps.println("OK");
            } else {
                ps.println("NOK");
            }
        } else {
            ps.println("ERROR_SESSIO");
        }
    }

    public void removeUsuari(String sessio, PrintStream ps, BufferedReader in) throws IOException {
        String codi = in.readLine();
        String user = in.readLine();
        if (sessio.equals(codi) && (serv.getUsuari(sessio).isAdmin()
                || serv.getUsuari(codi).getUser().equals(user))) {
            Usuari us = serv.getUsuari(serv.getCodi(user));
            if (c.deleteUsuari(us)) {
                ps.println("OK");
                if (serv.isUserOnline(user)) {
                    serv.removeSession(serv.getCodi(user));
                }
            } else {
                ps.println("NOK");
            }
        } else {
            ps.println("ERROR_SESSIO");
        }
    }

    public void removeEsdeveniment(String sessio, PrintStream ps, BufferedReader in) throws IOException {
        String codi = in.readLine();
        int id = parseInt(in.readLine());
        if (sessio.equals(codi) || serv.getUsuari(sessio).isAdmin()) {
            if (c.deleteEsdeveniment(id)) {
                ps.println("OK");
            } else {
                ps.println("NOK");
            }
        } else {
            ps.println("ERROR_SESSIO");
        }
    }
}
