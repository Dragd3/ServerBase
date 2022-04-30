package server.bbdd;

import classes.Esdeveniment;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import classes.Usuari;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
     * **********CREACIO D'USUARIS**********
     */
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
            PreparedStatement qry = conn.prepareStatement(consulta);
            qry.setString(1, user);
            qry.setString(2, pass);
            ResultSet rs = qry.executeQuery();
            if (rs.next()) {
                us.setUser(rs.getString(1));
                us.setPassword(rs.getString(2));
                us.setMail(rs.getString(3));
                us.setBirth(rs.getString(4));
                us.setAdmin(rs.getBoolean(5));
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return us;
    }

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
     * **********MODIFICACIO D'USUARIS**********
     */
    
    
    /**
     * Modifica la clau de l'usuari
     *
     * @param user usuari al qual se li modificara la clau
     * @param pwd nova clau
     * @return boolean si s'ha fet la modificacio o no
     */
    public boolean modifyPwd(String user, String pwd) {
        String cons = "UPDATE accounts SET password = '?' WHERE username = '?'";
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
        String cons = "UPDATE accounts SET mail = '?' WHERE username = '?'";
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
        String cons = "UPDATE accounts SET date = '?' WHERE username = '?'";
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
        String cons = "SELECT admin FROM accounts WHERE username = '?'";
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setString(1, user);
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                boolean isAdmin = rs.getBoolean(1);
                if (isAdmin) {
                    cons = "UPDATE accounts SET admin = false WHERE username = '?'";
                    query.setString(1, user);
                    query.execute();
                    return 0;
                } else {
                    cons = "UPDATE accounts SET admin = true WHERE username = '?'";
                    query.setString(1, user);
                    query.executeUpdate();
                    return 1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    /**
     * **********CREACIO D'ESDEVENIMENTS**********
     */
    
    /**
     * Crea un esdeveniment a la BBDD
     * 
     * @param e Esdeveniment a fer persistent
     * @return boolean indicant si l'operacio s'ha dut a terme
     */
    public boolean createEvent(Esdeveniment e) {
        String cons = "INSERT INTO events VALUES ('?','?','?','?','?','?','{\"?\"}','?')";
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setString(1, e.getTitol());
            query.setString(2, e.getContingut());
            query.setString(3, e.getCreador());
            query.setString(4, e.getData());
            query.setString(5, e.getTema());
            query.setString(6, e.getLloc());
            query.setString(7, e.getCreador());
            query.setInt(8, e.getMax());
            query.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Modifica el titol, contingut, data, tematica o lloc d'un esdeveniment
     * 
     * @param e Esdeveniment modificat
     * @return boolean indicant si l'operacio s'ha dut a terme
     */
    public boolean modifyEvent(Esdeveniment e) {
        String cons = "UPDATE events SET title = '?', content = '?', date = '?',"
                + "theme = '?', place = '?' WHERE id = '?'";
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

        String cons = "SELECT full FROM events WHERE id = '?'";
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setInt(1, id);
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                if (rs.getBoolean(1)) {
                    return false;
                } else {
                    cons = "UPDATE events SET goers = array_append(goers, '?') "
                            + "WHERE id = '?'";
                    query = conn.prepareStatement(cons);
                    query.setString(1, user);
                    query.setInt(2, id);
                    query.executeUpdate();
                    cons = "SELECT array_length(goers, 1), max FROM events WHERE"
                            + " id = '?'";
                    query = conn.prepareStatement(cons);
                    query.setInt(1, id);
                    rs = query.executeQuery();
                    if (rs.next()){
                        int actual = rs.getInt(1);
                        int max = rs.getInt(2);
                        if (actual == max){
                            cons = "UPDATE events SET full = true WHERE id = '?'";
                            query = conn.prepareStatement(cons);
                            query.executeUpdate();
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * Elimina un participat d'un esdeveniment i modifica si escau si esta ple
     * 
     * @param id Id de l'esdeveniment a modificar
     * @param user String del nom de l'usuari a esborrar
     * @return boolean que indica si l'operacio s'ha dut a terme
     */
    public boolean removeGoer(int id, String user){
        String cons = "SELECT full FROM events WHERE id = '?'";
        //UPDATE events SET goers = (select array_remove(goers, 'Edgard') from events WHERE id = 3) WHERE id = 3
        try {
            PreparedStatement query = conn.prepareStatement(cons);
            query.setInt(1, id);
            ResultSet rs = query.executeQuery();
            if (rs.next()){
                if(rs.getBoolean(1)){
                    cons = "UPDATE events SET goers = (select array_remove(goers, '?'), full = false from events WHERE id = '?') WHERE id = '?'";
                    query = conn.prepareStatement(cons);
                    query.setString(1, user);
                    query.setInt(2, id);
                    query.setInt(3, id);
                    query.executeUpdate();
                } else{
                    cons = "UPDATE events SET goers = (select array_remove(goers, '?') from events WHERE id = '?') WHERE id = '?'";
                    query = conn.prepareStatement(cons);
                    query.setString(1, user);
                    query.setInt(2, id);
                    query.setInt(3, id);
                    query.executeUpdate();
                }
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Crides.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
