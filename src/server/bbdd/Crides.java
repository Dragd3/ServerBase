package server.bbdd;

import classes.Esdeveniment;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import classes.Usuari;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import server.utils.Utilitats;

/**
 *
 * @author EMiF
 */
public class Crides {

    Utilitats utils = new Utilitats();
    Connectivitat connectivitat = new Connectivitat();
    Connection conn = connectivitat.connect();

    /**
     * Contrasta usuari i contrasenya contra BBDD
     *
     * @param user String del nom d'usuari o de mail
     * @param pass String de contrasenya
     * @return Usuari si es correcte, null en cas contrari
     */
    public Usuari login(String user, String pass) {
        Usuari us = new Usuari();
        String consulta = "SELECT * FROM accounts a WHERE username = ? AND "
                + "password = ?";
        if (utils.isMailValid(user)) {
            consulta = "SELECT * FROM accounts a WHERE mail = ? AND "
                    + "password = ?";
        }
        try {
            PreparedStatement query = conn.prepareStatement(consulta);
            query.setString(1, user);
            query.setString(2, pass);
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                us.setUser(rs.getString(1));
                us.setPassword(rs.getString(2));
                us.setMail(rs.getString(3));
                us.setBirth(rs.getString(4));
                us.setAdmin(rs.getBoolean(5));
                us = actualizeAssistences(us);
                us = actualizeOwneds(us);
                return us;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /* ********** CREACIO ********** */
    /**
     * Crea un Usuari a la BBDD.
     *
     * @param user String d'usuari
     * @param pass String de password
     * @param mail String de mail
     * @param birth String de data naixmenet
     * @return -1 si dades incorrectes, 0 si es existent, 1 si OK
     */
    public int create(String user, String pass, String mail, String birth) {
        String cons = "SELECT * FROM accounts a WHERE username = ? OR mail = ?";
        String consulta = "INSERT INTO accounts VALUES (?,?,?,?,?);";
        if (utils.isPwdStrong(pass)) {
            if (utils.isMailValid(mail)) {
                if (utils.isBirthOk(birth) != null) {
                    if (utils.isAdult(birth)) {
                        try {
                            PreparedStatement query = conn.prepareStatement(cons);
                            query.setString(1, user);
                            query.setString(2, mail);
                            ResultSet rs = query.executeQuery();
                            if (!rs.next()) {
                                PreparedStatement qry = conn.prepareStatement(consulta);
                                qry.setString(1, user);
                                qry.setString(2, pass);
                                qry.setString(3, mail);
                                qry.setString(4, birth);
                                qry.setBoolean(5, false);
                                qry.executeUpdate();
                                return 1;
                            } else {
                                return 0;
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Crea un esdeveniment a la BBDD
     *
     * @param e Esdeveniment a fer persistent
     * @return boolean indicant si l'operacio s'ha dut a terme
     */
    public int createEvent(Esdeveniment e) {
        String cons = "INSERT INTO events VALUES (?,?,?,?,?,?,?,?) RETURNING events.id";
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setString(1, e.getTitol());
            query.setString(2, e.getContingut());
            query.setString(3, e.getCreador());
            query.setString(4, e.getData());
            query.setString(5, e.getTema());
            query.setString(6, e.getLloc());
            query.setArray(7, conn.createArrayOf("VARCHAR", new String[]{e.getCreador()}));
            query.setInt(8, e.getMax());
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    /* ********** MODIFICACIO ********** */
    /**
     * Modifica la clau de l'usuari
     *
     * @param user usuari al qual se li modificara la clau
     * @param pwd nova clau
     * @return boolean si s'ha fet la modificacio o no
     */
    public boolean modifyPwd(String user, String pwd) {
        String cons = "UPDATE accounts SET password = ? WHERE username = ?";
        if (utils.isPwdStrong(pwd)) {
            try {
                PreparedStatement query = conn.prepareStatement(cons);
                query.setString(1, pwd);
                query.setString(2, user);
                query.executeUpdate();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    /**
     * Modifica el mail de l'usuari
     *
     * @param user usuari al qual se li modifica el mail
     * @param mail nou mail
     * @return boolean si s'ha fet la modificacio o no
     */
    public boolean modifyMail(String user, String mail) {
        String cons = "UPDATE accounts SET mail = ? WHERE username = ?";
        if (utils.isMailValid(mail)) {
            try {
                PreparedStatement query = conn.prepareStatement(cons);
                query.setString(1, mail);
                query.setString(2, user);
                query.executeUpdate();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    /**
     * Modifica la data de naixement de l'usuari
     *
     * @param user usuari al qual se li modifica la data de naixement
     * @param date nova data
     * @return boolean si s'ha fet la modificacio o no
     */
    public boolean modifyDate(String user, String date) {
        String cons = "UPDATE accounts SET date = ? WHERE username = ?";
        if (utils.isBirthOk(date) != null) {
            if (utils.isAdult(date)) {
                try {
                    PreparedStatement query = conn.prepareStatement(cons);
                    query.setString(1, date);
                    query.setString(2, user);
                    query.executeUpdate();
                    return true;
                } catch (SQLException ex) {
                    Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }

    /**
     * Modifica l'status d'admin de l'usuari segons ho fos o no
     *
     * @param user usuari al qual se li fa la modificacio
     * @return 1 si es torna admin, 0 si es torna user, -1 si no es fa.
     */
    public int changeAdmin(String user) {
        String cons = "SELECT ac.admin FROM accounts ac WHERE username = ?";
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setString(1, user);
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                boolean isAdmin = rs.getBoolean(1);
                if (isAdmin) {
                    cons = "UPDATE accounts SET admin = false WHERE username = ?";
                    query = conn.prepareStatement(cons);
                    query.setString(1, user);
                    query.execute();
                    return 0;
                } else {
                    cons = "UPDATE accounts SET admin = true WHERE username = ?";
                    query = conn.prepareStatement(cons);
                    query.setString(1, user);
                    query.execute();
                    return 1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    /**
     * Modifica el titol, contingut, data, tematica o lloc d'un esdeveniment
     *
     * @param e Esdeveniment modificat
     * @return boolean indicant si l'operacio s'ha dut a terme
     */
    public boolean modifyEvent(Esdeveniment e) {
        String cons = "UPDATE events SET title = ?, content = ?, date = ?,"
                + "theme = ?, place = ? WHERE id = ?";
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setString(1, e.getTitol());
            query.setString(2, e.getContingut());
            query.setString(3, e.getData());
            query.setString(4, e.getTema());
            query.setString(5, e.getLloc());
            query.setInt(6, e.getId());
            query.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    /**
     * Afegeix un participant a un esdeveniment i si escau modifica si esta ple
     *
     * @param id Id de l'esdeveniment a modificar
     * @param user String del nom de l'usuari participant
     * @return boolean indicant si s'ha dut a terme l'operacio
     */
    public boolean addGoer(int id, String user) {
        String cons = "SELECT complete, array_length(goers, 1), max FROM events WHERE id = ?";
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setInt(1, id);
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                boolean full = rs.getBoolean(1);
                int actual = rs.getInt(2);
                int max = rs.getInt(3);
                if (!full) {
                    if (max - actual == 1) {
                        cons = "UPDATE events SET goers = array_append(goers, ?)"
                                + ", complete = true WHERE id = ?";
                    } else {
                        cons = "UPDATE events SET goers = array_append(goers, ?)"
                                + " WHERE id = ?";
                    }
                    query = conn.prepareStatement(cons);
                    query.setString(1, user);
                    query.setInt(2, id);
                    query.execute();
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Treu un participat d'un esdeveniment i modifica si escau si esta ple
     *
     * @param id Id de l'esdeveniment a modificar
     * @param user String del nom de l'usuari a esborrar
     * @return boolean que indica si l'operacio s'ha dut a terme
     */
    public boolean removeGoer(int id, String user) {
        String cons = "SELECT complete FROM events WHERE id = ?";
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setInt(1, id);
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                if (rs.getBoolean(1)) {
                    cons = "UPDATE events SET goers = (select array_remove(goers, ?) from events WHERE id = ?), complete = false WHERE id = ?";
                } else {
                    cons = "UPDATE events SET goers = (select array_remove(goers, ?) from events WHERE id = ?) WHERE id = ?";
                }
                query = conn.prepareStatement(cons);
                query.setString(1, user);
                query.setInt(2, id);
                query.setInt(3, id);
                query.execute();
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /* ********** CONSULTA ********** */
    /**
     *
     * @param user
     * @return
     */
    public Usuari showUsuari(String user) {
        String cons = "SELECT * FROM accounts WHERE username = ?";
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setString(1, user);
            ResultSet rs = query.executeQuery();
            Usuari us = null;
            if (rs.next()) {
                us = new Usuari(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(4));
                us.setAdmin(rs.getBoolean(5));
            }
            if (us != null) {
                us = actualizeOwneds(us);
                us = actualizeAssistences(us);
            }
            return us;
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Retorna una List amb tots els esdeveniments
     *
     * @return List<Esdeveniment>
     */
    public List<Esdeveniment> showEsdeveniments() {
        String cons = "SELECT * FROM events";
        List<Esdeveniment> esdeveniments = new ArrayList<Esdeveniment>();
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            ResultSet rs = query.executeQuery();
            while (rs.next()) {
                String t = rs.getString("title");
                String c = rs.getString("content");
                String o = rs.getString("owner");
                String d = rs.getString("date");
                String th = rs.getString("theme");
                String p = rs.getString("place");
                Array g = rs.getArray("goers");
                int max = rs.getInt("max");
                int id = rs.getInt("id");
                boolean complete = rs.getBoolean("complete");
                List<String> go = new ArrayList<String>();
                for (Object obj : (String[]) g.getArray()) {
                    go.add((String) obj);
                }
                Esdeveniment e = new Esdeveniment(t, c, o, d, th, p, go, max, id, complete);
                esdeveniments.add(e);
            }
            return esdeveniments;
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Retorna una List amb tots els esdeveniments que tinguin el parametre
     *
     * @param param int que indica el tipus de parametre a evaluar
     * @param value String amb el valor que te el parametre
     * @return List<Esdeveniment> amb el filtratge, null si no hi ha cap
     */
    public List<Esdeveniment> showEsdevenimentsParam(int param, String value) {
        String cons = "";
        List<Esdeveniment> esdeveniments = new ArrayList<Esdeveniment>();
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            switch (param) {
                case 1:
                    cons = "SELECT * FROM events WHERE title LIKE ?";
                    query = conn.prepareStatement(cons);
                    query.setString(1, "%" + value + "%");
                    break;
                case 2:
                    cons = "SELECT * FROM events WHERE content LIKE ?";
                    query = conn.prepareStatement(cons);
                    query.setString(1, "%" + value + "%");
                    break;
                case 3:
                    cons = "SELECT * FROM events WHERE owner = ?";
                    query = conn.prepareStatement(cons);
                    query.setString(1, value);
                    break;
                case 4:
                    cons = "SELECT * FROM events WHERE date = ?";
                    query = conn.prepareStatement(cons);
                    query.setString(1, value);
                    break;
                case 5:
                    cons = "SELECT * FROM events WHERE theme LIKE ?";
                    query = conn.prepareStatement(cons);
                    query.setString(1, "%" + value + "%");
                    break;
                case 6:
                    cons = "SELECT * FROM events WHERE place LIKE ?";
                    query = conn.prepareStatement(cons);
                    query.setString(1, "%" + value + "%");
                    break;
                case 7:
                    cons = "SELECT * FROM events WHERE complete = ?";
                    query = conn.prepareStatement(cons);
                    query.setBoolean(1, parseBoolean(value));
                    break;
                case 8:
                    cons = "SELECT * FROM events WHERE id = ?";
                    query = conn.prepareStatement(cons);
                    query.setInt(1, parseInt(value));
                    break;
            }
            ResultSet rs = query.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    String t = rs.getString("title");
                    String c = rs.getString("content");
                    String o = rs.getString("owner");
                    String d = rs.getString("date");
                    String th = rs.getString("theme");
                    String p = rs.getString("place");
                    Array g = rs.getArray("goers");
                    int max = rs.getInt("max");
                    int id = rs.getInt("id");
                    boolean complete = rs.getBoolean("complete");
                    List<String> go = new ArrayList<String>();
                    for (Object obj : (String[]) g.getArray()) {
                        go.add((String) obj);
                    }
                    Esdeveniment e = new Esdeveniment(t, c, o, d, th, p, go, max, id, complete);
                    esdeveniments.add(e);
                }
                return esdeveniments;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Passant un Usuari, retorna aquest objecte actualitzant Esdeveniments
     *
     * @param us Usuari que es vol actualitzar
     * @return Usuari actualitzat
     */
    public Usuari actualizeOwneds(Usuari us) {
        String cons = "SELECT id FROM events WHERE owner = ?";
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setString(1, us.getUser());
            ResultSet rs = query.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    System.out.println(rs.getInt(1));
                    us.getEsvedeniments().add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return us;
    }

    /**
     * Passant un Usuari, retorna aquest objecte actualitzant Assistencies
     *
     * @param us Usuari que es vol actualitzar
     * @return Usuari actualitzat
     */
    public Usuari actualizeAssistences(Usuari us) {
        String cons = "SELECT id FROM events WHERE ? = ANY (ARRAY[goers]) AND "
                + "NOT owner = ?";
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setString(1, us.getUser());
            query.setString(2, us.getUser());
            ResultSet rs = query.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    int assist = rs.getInt(1);
                    if (!us.getAssistencies().contains(assist)) {
                        us.getAssistencies().add(assist);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return us;
    }

    /* ********** ESBORRAR ********** */
    public boolean deleteUsuari(Usuari user) {
        if (!user.getEsvedeniments().isEmpty()) {
            for (int i : user.getEsvedeniments()) {
                deleteEsdeveniment(i);
            }
        }
        if (!user.getAssistencies().isEmpty()) {
            for (int i : user.getAssistencies()) {
                removeGoer(i, user.getUser());
            }
        }
        String cons = "DELETE FROM accounts WHERE username = ?";
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setString(1, user.getUser());
            query.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteEsdeveniment(int id) {
        String cons = "SELECT goers FROM events WHERE id = ?";
        List<String> goers = new ArrayList<String>();
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setInt(1, id);
            ResultSet rs = query.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    goers.add(rs.getString(1));
                }
                for (String s : goers) {
                    removeGoer(id, s);
                }
            }
            cons = "DELETE FROM events WHERE id = ?";
            query = conn.prepareStatement(cons);
            query.setInt(1, id);
            query.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
