package server.bbdd;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author EMiF
 */
public class Connectivitat {
    
    /**
     * Crea connexio amb la BBDD
     * @return Connection
     */
    public Connection connect() {
        Connection c = null;
        //Connexio a DDBB, es poden canviar els parametres
        String host = "localhost", port = "5432", bbdd = "QuedAppBBDD", 
                sessio = "ioc", pwd = "ioc";
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://" +host+ ":" +port+ "/" +bbdd , sessio, pwd);
        } catch (SQLException e) {
            System.out.println("Error en la connexio");
        } catch (ClassNotFoundException e) {
            System.out.println("Dirver no trobat");
        }
        return c;
    }
    
    /**
     * Passant un Connection, la tanca
     * @param c 
     */
    public void disconnect(Connection c){
        try {
            if (c != null && !c.isClosed()) try {
                c.close();
            } catch (SQLException e){
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(Connectivitat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
