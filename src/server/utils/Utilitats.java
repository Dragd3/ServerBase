package server.utils;

import java.time.LocalDate;
import static java.time.LocalDate.parse;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author EMiF
 */
public class Utilitats {
         
    /**
     * Passant un String formatat (yyyy-MM-dd), mira si es major de 18 anys
     * @param birth String formatejat per isBirthOk()
     * @return true si la data es anterior a 18 anys des d'avui. Si no, false
     */
    public boolean isAdult(String birth) {
        LocalDate today = LocalDate.now();
        LocalDate birthDate = parse(birth);
        long period = ChronoUnit.DAYS.between(birthDate, today);
        if (period >= (365*18)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Passa un String i mira si te el format de data yyyy-MM-dd
     * @param birth String de data
     * @return String del parametre si es correcte, null en cas contrari
     */
    public String isBirthOk(String birth) {
        String pattern = "^\\d{4}([\\-/.])(0?[1-9]|1[1-2])\\1(3[01]|[12][0-9]|0?[1-9])$";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(birth);
        if (m.find()) {
            return birth;
        } else {
            return null;
        }
    }

    /**
     * Mira si el parametre te la longitud minima de 8 caracters
     * @param pwd String a comprovar
     * @return true si la te, false en cas contrari
     */
    public boolean isPwdStrong(String pwd) {
        if (pwd.length() < 8) {
            return false;
        } else {
            return true;
        }
    }
    

    /**
     * Passant un String es mira si te format de correu electronic
     * @param mail ha de ser combinacio-_.Alfanumerica@combinacio-_.alfanumerica@lletres.lletres
     * @return true si passa el REGEX, false en cas contrari
     */
    public boolean isMailValid(String mail){
        String pattern = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(mail);
        if(m.find()){
            return true;
        } else {
            return false;
        }
    }
    
}
