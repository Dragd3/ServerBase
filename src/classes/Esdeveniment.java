package classes;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author prova1
 */
public class Esdeveniment implements Serializable{
    
    private String titol;
    private String contingut;
    private String creador;
    private String data;
    private String tema;
    private String lloc;
    private List<String> participants;
    private int max;
    private boolean ple = false;
    private int id;
    
    public Esdeveniment(){}
    
    public Esdeveniment(String t, String c, String creador, String d, 
            String tema, String lloc, int maxParticipants){
        this.titol = t;
        this.contingut = c;
        this.creador = creador;
        this.data = d;
        this.tema = tema;
        this.lloc = lloc;
        participants.add(creador);
        this.max = maxParticipants;
    }
    
    

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getContingut() {
        return contingut;
    }

    public void setContingut(String contingut) {
        this.contingut = contingut;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getLloc() {
        return lloc;
    }

    public void setLloc(String lloc) {
        this.lloc = lloc;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public boolean isPle() {
        return ple;
    }

    public void setPle(boolean ple) {
        this.ple = ple;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    

    @Override
    public String toString() {
        return "Esdeveniment {titol=" + titol + ", creador=" + creador + 
                ", data=" + data + ", lloc=" + lloc + ", max=" + max + 
                ", ple=" + ple + '}';
    }
    
    
    
    
}
