package classes;

//package server.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author EMiF
 */
public class Usuari implements Serializable {

    private String user;
    private String password;
    private String mail;
    private String birth;
    private boolean admin = false;
    List<Integer> esvedeniments = new ArrayList<Integer>();
    List<Integer> assistencies = new ArrayList<Integer>();

    public Usuari() {
    }

    public Usuari(String user, String pwd, String mail, String birth) {
        this.user = user;
        this.password = pwd;
        this.mail = mail;
        this.birth = birth;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public List<Integer> getEsvedeniments() {
        return esvedeniments;
    }

    public void setEsvedeniments(List<Integer> esvedeniments) {
        this.esvedeniments = esvedeniments;
    }

    public void addEsdeveniments(int e) {
        this.esvedeniments.add(e);
    }

    public List<Integer> getAssistencies() {
        return assistencies;
    }

    public void setAssistencies(List<Integer> assistencies) {
        this.assistencies = assistencies;
    }
    
    @Override
    public String toString() {
        return "Usuari {username=" + user + ", password=" + password + ", "
                + "mail=" + mail + ", birth=" + birth + ", admin=" + admin + 
                "esdeveniments=" + getEsvedeniments().toString() +
                "assistencies=" + getAssistencies().toString() + "}";
    }

}
