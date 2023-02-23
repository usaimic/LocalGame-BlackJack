package ConsegnaCompleta;


import java.net.*;
import java.io.*;
import java.util.*;

public class server {
    public static void main(String[] args) {
        try {
            try (ServerSocket server = new ServerSocket(8000)) {
                
                // ServerSocket server = new ServerSocket();
                // InetAddress ipaddr = InetAddress.getByName("192.168.67.81");
                // InetSocketAddress sockaddr = new InetSocketAddress(ipaddr, 8000);
                // server.bind(sockaddr);

                System.out.println("DATI DEL SERVER : \n"+server+"\n");
                System.out.println("SERVER : waiting for a connection ..." /*+server*/);

                ArrayList<String> nomiUtente = new ArrayList<String>();
                ArrayList<String> risultati = new ArrayList<String>();
                ArrayList<Socket> datasocket = new ArrayList<Socket>();
                ArrayList<String> mazziere = new ArrayList<String>();
                ArrayList<Integer> numeri = new ArrayList<Integer>();

                ArrayList<DataOutputStream> writer = new ArrayList<DataOutputStream>();
                ArrayList<BufferedReader> reader = new ArrayList<BufferedReader>();

                String mazzo[] = getMazzo();
                List<String> mazzoShuffle = new ArrayList<String>(Arrays.asList(mazzo));

                int i,indexCarta;

                //IMPLEMENTAZIONE PER CHIUDERE LOBBY PRIMA
                // int numeroGiocatori = 5; //Numero consigliato di giocatori MAX 5-6 (Si puo decidere di chiudere la lobby prima che la lobby si riempia)

                int numeroGiocatori = 2;

                // Connessione con i giocatori
                for (i = 0; i < numeroGiocatori ; i++) {
                    Socket temp = server.accept();
                    datasocket.add(temp);
                    reader.add(new BufferedReader(new InputStreamReader(temp.getInputStream())));
                    writer.add(new DataOutputStream(temp.getOutputStream()));
                    nomiUtente.add(reader.get(i).readLine());
                    System.out.println("Connection established : " /*+ datasocket */ + " Nome Utente --> " + nomiUtente.get(i) + "\n\n");
                    sezione();
                    System.out.println("Giocatori collegati : "+datasocket.size()+" / "+numeroGiocatori);
                    
                    //IMPLEMENTAZIONE PER CHIUDERE LA LOBBY QUANDO SI VUOLE 
                    /*
                    System.out.println("Invia 0 se vuoi chiudere la lobby , premi INVIO se vuoi continuare ");
                    if(readerTastiera.readLine().equalsIgnoreCase("0")){
                        System.out.println("Sei sicuro di voler chiudere la stanza di gioco ? [SI / NO] /nSe deciderai di non chiudere la lobby sarà necessario il collegamento di un altro client ");
                        if(readerTastiera.readLine().equalsIgnoreCase("SI")){
                            numeroGiocatori = datasocket.size();
                        }   
                    }
                    */
                }

                // Comunica a tutti i giocatori che sono collegati tutti gli altri
                for (i = 0; i < datasocket.size(); i++) 
                    writer.get(i).writeBytes("Tutti i giocatori sono collegati \n");

                int y = 0;
                indexCarta = 79;

                // Invio prime 2 carte ai giocatori
                int punteggio = 0;
                boolean seconda = false;
                for (i = 0; i < datasocket.size() * 2; i++) {
                    if (!seconda) {
                        System.out.println("CARTA DI " + nomiUtente.get(y) + "\n\n");
                    }
                    writer.get(y).writeBytes(mazzoShuffle.get(indexCarta) + "\n");
                    System.out.println(
                            "Inviata la carta : " + mazzoShuffle.get(indexCarta) + " al giocatore " + nomiUtente.get(y));
                    mazzoShuffle.remove(indexCarta);
                    indexCarta--;
                    if (seconda) {
                        punteggio = Integer.parseInt(reader.get(y).readLine());
                        if(punteggio==21)
                            risultati.add("BLACKJACK");
                        else    
                            risultati.add("" + punteggio);
                        System.out
                                .println("\nPunteggio attuale giocatore " + nomiUtente.get(y) + " : " + punteggio + "\n\n");
                        sezione();
                    }
                    if (i % 2 == 0) {
                        seconda = true;
                    } else {
                        seconda = false;
                        y++;
                    }

                }

                // Prime 2 carte anche al mazziere (Solo la prima è visibile)
                String temp = "";
                int carta, punteggioMazziere = 0;
                for (i = 0; i < 2; i++) {
                    temp = mazzoShuffle.get(indexCarta);
                    mazziere.add(temp);
                    mazzoShuffle.remove(indexCarta);
                    indexCarta--;
                    carta = getNumero(temp);
                    if(carta==1)
                        carta=11;
                    punteggioMazziere += carta;
                    numeri.add(carta);
                    if(punteggioMazziere>21){
                        y = 0;
                        while(y<numeri.size()){
                            if(numeri.get(y) == 11 ){
                                punteggioMazziere -= 10;
                                numeri.set(y,1);
                                y = numeri.size();
                            }
                            y++;
                        }
                    }
                    if (i == 0)
                        System.out.println("Carta del mazziere : " + mazziere.get(i));
                }
                sezione();

                String scelta = "";
                boolean passo = false;

                for (i = 0; i < datasocket.size(); i++) {
                    writer.get(i).writeBytes(mazziere.get(0) + "\n");
                }

                for (i = 0; i < datasocket.size(); i++) {
                    if (risultati.get(i).equalsIgnoreCase("BLACKJACK"))
                        passo = true;
                    else {
                        passo = false;
                        writer.get(i).writeBytes("E' il tuo turno \n");
                        System.out.println("E' il turno di " + nomiUtente.get(i));
                        scelta = "";
                    }
                    punteggio = 0;
                    while (!passo) {
                        scelta = reader.get(i).readLine();
                        if (scelta.equalsIgnoreCase("CARTA")) {
                            System.out.println(nomiUtente.get(i) + " ha chiesto carta ");
                            writer.get(i).writeBytes(mazzoShuffle.get(indexCarta) + "\n");
                            System.out.println(
                                    "Inviata la carta : " + mazzoShuffle.get(indexCarta) + " a " + nomiUtente.get(i));
                            mazzoShuffle.remove(indexCarta);
                            indexCarta--;
                            punteggio = Integer.parseInt(reader.get(i).readLine());
                            System.out.println("Punteggio giocatore " + nomiUtente.get(i) + " : " + punteggio);
                        }
                        if (scelta.equalsIgnoreCase("STARE")) {
                            System.out.println(nomiUtente.get(i) + " ha deciso di stare fermo ");
                            if (punteggio != 0)
                                risultati.set(i, "" + punteggio);
                            passo = true;
                        }
                        if (scelta.equalsIgnoreCase("EXIT")) {
                            System.out.println(nomiUtente.get(i) + " è uscito dalla partita ");
                            risultati.set(i, "Exit");
                            passo = true;
                        }
                        if (scelta.equalsIgnoreCase("SBALLATO")) {
                            System.out.println(nomiUtente.get(i) + " ha sballato ");
                            risultati.set(i, "Sballato");
                            passo = true;
                        }
                        if (scelta.equalsIgnoreCase("21")) {
                            System.out.println(nomiUtente.get(i) + " ha fatto 21 ");
                            risultati.set(i, "21");
                            passo = true;
                        }
                        if (scelta.equalsIgnoreCase("BLACKJACK")) {
                            System.out.println(nomiUtente.get(i) + " ha fatto BlackJack ");
                            risultati.set(i, "BlackJack");
                            passo = true;
                        }
                    }
                    sezione();
                }

                // Viene mostrata la seconda carta del mazziere
                System.out.println("Seconda carta del mazziere : " + mazziere.get(1));

                // Viene comunicata la seconda carta a tutti i giocatori
                for (i = 0; i < datasocket.size(); i++) {
                    writer.get(i).writeBytes("Seconda carta del mazziere : " + mazziere.get(1) + " \n");
                    writer.get(i).writeBytes(punteggioMazziere + "\n");
                }
                System.out.println("Punteggio del mazziere : " + punteggioMazziere);

                // Turno del mazziere (da 17 in poi sceglie sempre "stare")
                while (punteggioMazziere < 17) {
                    temp = mazzoShuffle.get(indexCarta);
                    mazziere.add(temp);
                    mazzoShuffle.remove(indexCarta);
                    indexCarta--;
                    carta = getNumero(temp);
                    if(carta==1)
                        carta=11;
                    punteggioMazziere += carta;
                    numeri.add(carta);
                    if(punteggioMazziere>21){
                        y = 0;
                        while(y<numeri.size()){
                            if(numeri.get(y) == 11 ){
                                punteggioMazziere -= 10;
                                numeri.set(y,1);
                                y = numeri.size();
                            }
                            y++;
                        }
                    }
                    System.out.println("Carta del mazziere : " + temp);
                    System.out.println("Punteggio mazziere : " + punteggioMazziere + " \n");
                    for (i = 0; i < datasocket.size(); i++) 
                        writer.get(i).writeBytes(temp + "\n");
                }
                System.out.println("Il mazziere si ferma a " + punteggioMazziere);
                for (i = 0; i < datasocket.size(); i++) {
                    writer.get(i).writeBytes("Punteggio mazziere : " + punteggioMazziere + "\n");
                }
                
                System.out.println("\nIL TUO MAZZO\n");
                for(i=0;i<mazziere.size();i++){
                    System.out.println(" - "+mazziere.get(i));
                }

                boolean avviso;
                for (i = 0; i < datasocket.size(); i++) {
                    sezione();
                    avviso = false;
                    if (risultati.get(i).equalsIgnoreCase("EXIT") && !avviso) {
                        System.out.println(nomiUtente.get(i) + " ha abbandonato ");
                        avviso = true;
                    }
                    if (risultati.get(i).equalsIgnoreCase("SBALLATO") && !avviso) {
                        System.out.println(nomiUtente.get(i) + " ha sballato ");
                        avviso = true;
                    }
                    if (risultati.get(i).equalsIgnoreCase("21") && !avviso) {
                        System.out.println(nomiUtente.get(i) + " ha fatto 21  ");
                        avviso = true;
                    }
                    if (risultati.get(i).equalsIgnoreCase("BLACKJACK") && !avviso) {
                        System.out.println(nomiUtente.get(i) + " ha fatto BlackJack ");
                        avviso = true;
                    }
                    if (!avviso) {
                        if (Integer.parseInt(risultati.get(i)) < punteggioMazziere && punteggioMazziere <= 21 && !avviso) {
                            System.out.println(nomiUtente.get(i) + " ha perso facendo " + risultati.get(i));
                            avviso = true;
                        }
                        if (Integer.parseInt(risultati.get(i)) < 21 && punteggioMazziere > 21 && !avviso) {
                            System.out.println(nomiUtente.get(i) + " ha vinto facendo " + risultati.get(i));
                            avviso = true;
                        }
                        if (Integer.parseInt(risultati.get(i)) == punteggioMazziere && punteggioMazziere < 21 && !avviso) {
                            System.out.println(nomiUtente.get(i) + " ha pareggiato facendo " + risultati.get(i));
                            avviso = true;
                        }
                        if (Integer.parseInt(risultati.get(i)) > punteggioMazziere && Integer.parseInt(risultati.get(i)) < 21 && !avviso) {
                            System.out.println(nomiUtente.get(i) + " ha vinto facendo " + risultati.get(i));
                            avviso = true;
                        }
                    }
                }

                sezione();
                // Fine gioco
                Thread.sleep(5000);
                System.out.println("Gioco terminato");
                server.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // Metodo per generare , mischiare e restituire il mazzo (2 mazzi francesi da 40
    // carte)
    public static String[] getMazzo() {
        String mazzo[] = {
                "1 Cuori", "2 Cuori", "3 Cuori", "4 Cuori", "5 Cuori", "6 Cuori", "7 Cuori", "8 Cuori", "9 Cuori",
                "10 Cuori",
                "1 Quadri", "2 Quadri", "3 Quadri", "4 Quadri", "5 Quadri", "6 Quadri", "7 Quadri", "8 Quadri",
                "9 Quadri", "10 Quadri",
                "1 Fiori", "2 Fiori", "3 Fiori", "4 Fiori", "5 Fiori", "6 Fiori", "7 Fiori", "8 Fiori", "9 Fiori",
                "10 Fiori",
                "1 Picche", "2 Picche", "3 Picche", "4 Picche", "5 Picche", "6 Picche", "7 Picche", "8 Picche",
                "9 Picche", "10 Picche",
                "1 Cuori", "2 Cuori", "3 Cuori", "4 Cuori", "5 Cuori", "6 Cuori", "7 Cuori", "8 Cuori", "9 Cuori",
                "10 Cuori",
                "1 Quadri", "2 Quadri", "3 Quadri", "4 Quadri", "5 Quadri", "6 Quadri", "7 Quadri", "8 Quadri",
                "9 Quadri", "10 Quadri",
                "1 Fiori", "2 Fiori", "3 Fiori", "4 Fiori", "5 Fiori", "6 Fiori", "7 Fiori", "8 Fiori", "9 Fiori",
                "10 Fiori",
                "1 Picche", "2 Picche", "3 Picche", "4 Picche", "5 Picche", "6 Picche", "7 Picche", "8 Picche",
                "9 Picche", "10 Picche"
        };
        List<String> tempList = Arrays.asList(mazzo);
        Collections.shuffle(tempList);
        tempList.toArray(mazzo);
        return mazzo;
    }

    // Metodo per avere il valore di ogni carta dalla stringa
    public static int getNumero(String t) {
        if (t.substring(0, 2).equals("10"))
            return 10;
        return Integer.parseInt(t.substring(0, 1));
    }

    // Metodo per separare tra una comunicazione e l'altra e non intasare il main
    public static void sezione() {
        System.out.println("-------------------------------------------------------------------------");
    }
}
