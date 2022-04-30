package test;

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
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author EMiF
 * 
 * INDEX DE PROVES
 * 1 LOGIN OK (RESULTAT CODI COMENÇA AMB 0)
 * 2 LOGIN NOK (RESULTAT ALREADY_LOGIN)
 * 3 LOGOUT OK (RESULTAT LOGGEDOUT)
 * 4 LOGOUT NOK (RESULTAT NOLOGGED)
 * 5 LOGIN NOK (RESULTAT NOK)
 * 6 LOGIN OK (RESULTAT CODI COMENÇA AMB 1)
 * 7 CREACIO NOK (RESULTAT EXISTEIX)
 * 8 CREACIO NOK (RESULTAT NOK)
 * 9 CREACIO NOK (RESULTAT NOK)
 * 10 CREACIO NOK (RESULTAT NOK)
 * 11 CREACIO OK (RESULTAT OK)
 * 12 USUARI NOK (RESULTAT NOK)
 * 13 USUARI OK (RESULTAT preuser 123123 usuario@gmail.com 2000-10-10)
 * 
 */
public class client {

    //Client per a provar el servidor en local
    public static void main(String[] args) {

        try {
            //Connexio al servidor
            String addr = "localhost";
            int port = 6666;
            Socket socket = new Socket(addr, port);

            //variables d'us al test
            int tests = 10;
            int i = 1;
            String codi = "";

            //lectors i escritors per al servidor
            PrintStream out = new PrintStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader bin2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Test 1: LOGIN OK
            //Ha de tornar un codi començat per 0, es a dir d'administrador
            Scanner reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            FileWriter writer = new FileWriter(new File("src\\test\\respostes.txt"));
            String resposta = testCrida(writer, reader, out, bin);
            System.out.print("Test " + i + ": ");
            mostraCodi(resposta, 0);
            codi = resposta;
            reader.close();

            //Test 2: LOGIN NOK
            //Ha d'informar que l'usuari ja esta loguejat
            i++;
            reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            resposta = testCrida(writer, reader, out, bin);
            System.out.print("Test " + i + ": ");
            mostraResposta("ALREADY_LOGGED", resposta);
            reader.close();

            //Test 3: LOGOUT OK
            //Ha de retornar el codi corresponent a desloguejarse
            i++;
            FileWriter wr = new FileWriter(new File("src\\test\\proves\\prova" + i + ".txt"), false);
            wr.write("0");
            wr.write("\n");
            wr.write(codi);
            reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            wr.close();
            resposta = testCrida(writer, reader, out, bin);
            System.out.print("Test " + i + ": ");
            mostraResposta("LOGGEDOUT", resposta);
            reader.close();

            //Test 4: LOGOUT NOK
            //Ha de retornar el codi corresponent a que no hi ha cap usuari loguejat
            i++;
            reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            resposta = testCrida(writer, reader, out, bin);
            System.out.print("Test " + i + ": ");
            mostraResposta("NOLOGGED", resposta);
            reader.close();

            //Test 5: LOGIN NOK
            //Ha d'informar que les dades introduïdes no son correctes
            i++;
            reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            resposta = testCrida(writer, reader, out, bin);
            System.out.print("Test " + i + ": ");
            mostraResposta("NOK", resposta);
            reader.close();
            
            //Test 6: LOGIN OK
            //Ha de tornar un codi començat per 1, es a dir d'usuari
            i++;
            reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            resposta = testCrida(writer, reader, out, bin);
            codi = resposta;
            System.out.print("Test " + i + ": ");
            mostraCodi(resposta, 1);
            reader.close();

            //Test 7: Crear usuari NOK
            //Ha d'informar que l'usuari ja existeix
            i++;
            reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            resposta = testCrida(writer, reader, out, bin);
            System.out.print("Test " + i + ": ");
            mostraResposta("EXISTEIX", resposta);
            reader.close();

            //Test 8: Crear usuari NOK
            //Ha d'informar que les dades no son correctes (data sense format)
            i++;
            reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            resposta = testCrida(writer, reader, out, bin);
            System.out.print("Test " + i + ": ");
            mostraResposta("NOK", resposta);
            reader.close();
            
            //Test 9: Crear usuari NOK
            //Ha d'informar que les dades no son correctes (email sense format)
            i++;
            reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            resposta = testCrida(writer, reader, out, bin);
            System.out.print("Test " + i + ": ");
            mostraResposta("NOK", resposta);
            reader.close();
            
            //Test 10: Crear usuari NOK
            //Ha d'informar que les dades no son correctes (menor d'edat)
            i++;
            reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            resposta = testCrida(writer, reader, out, bin);
            System.out.print("Test " + i + ": ");
            mostraResposta("NOK", resposta);
            reader.close();
            
            //Test 11: Crear usuari OK
            //Retorna confirmacio de que l'usuari ha estat creat a la BBDD
            i++;
            reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            resposta = testCrida(writer, reader, out, bin);
            System.out.print("Test " + i + ": ");
            mostraResposta("OK", resposta);
            reader.close();


            //Test 12: Usuari NOK
            //Informa que el codi no correspon amb el de sessio
            i++;
            wr = new FileWriter(new File("src\\test\\proves\\prova" + i + ".txt"), false);
            wr.write("3");
            wr.write("\n");
            wr.write(codi + "123");
            wr.close();
            reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            resposta = "null";
            System.out.print("Test " + i + ": ");
            if (testUsuari(writer, reader, out, bin, in) == null) {
                mostraResposta("NOK", "NOK");
            } else {
                mostraResposta("NOK", resposta);
            }
            reader.close();
            
            //Test 13: Usuari OK
            //Retorna un objecta del tipus Usuari corresponent al usuari loguejat
            //que es mostra per pantalla amb el format Usuari.toString()
            i++;
            wr = new FileWriter(new File("src\\test\\proves\\prova" + i + ".txt"), false);
            wr.write("3");
            wr.write("\n");
            wr.write(codi);
            wr.write("\n");
            wr.write("READY");
            wr.close();
            reader = new Scanner(new File("src\\test\\proves\\prova" + i + ".txt"));
            resposta = testUsuari(writer, reader, out, bin, in).toString();
            String usuari = "Usuari {username=preuser, password=123123, "
                    + "mail=usuario@gmail.com, birth=2000-10-10, admin=false}";
            System.out.print("Test " + i + ": ");
            mostraResposta(usuari, resposta);

            reader.close();
            writer.close();
            
            //Tanca el client
            out.println(";");

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String testCrida(FileWriter wr, Scanner rd, PrintStream out, BufferedReader bin) {
        String response = "-1";
        try {
            while (rd.hasNextLine()) {
                String data = rd.nextLine();
                out.println(data);
                response = bin.readLine();
            }
            wr.write(response);
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    public static Usuari testUsuari(FileWriter wr, Scanner rd, PrintStream out, BufferedReader bin, ObjectInputStream in) {
        while (rd.hasNextLine()) {
            try {
                String data = rd.nextLine();
                out.println(data);
                if (data.equals("READY")) {
                    Usuari u = (Usuari) in.readObject();
                    wr.write(u.toString());
                    return u;
                }
                String s = bin.readLine();

            } catch (IOException ex) {
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static void mostraResposta(String exemple, String r) {
        if (r.equals(exemple)) {
            System.out.println("completat correctament. Resultat:");
            System.out.println(r);
        } else {
            System.out.println("Error en el test");
        }
    }

    public static void mostraCodi(String r, int primer) {
        if (r.startsWith("" + primer)) {
            System.out.println("completat correctament. Resultat:");
            System.out.println(r);
        } else {
            System.out.println("Error en el test");
        }
    }
}
