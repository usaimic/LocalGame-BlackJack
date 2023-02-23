package ConsegnaCompleta;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class client {
    public static void main(String[] args) {
        try {
            BufferedReader keyboardreader = new BufferedReader(new InputStreamReader(System.in));
            
            // byte a[]= {(byte)192, (byte)168,(byte)67,(byte)113};
            // Socket datasocket = new Socket(InetAddress.getByAddress(a), 8000);
            // System.out.println(datasocket);
            //Inserimento del nome utente
            
            System.out.println("Inserisci il nome utente ");
            String nomeUtente = keyboardreader.readLine();
            
            System.out.println("Inserisci il saldo (Solo cifre)");
            int saldo = Integer.parseInt(keyboardreader.readLine());
            int entrata = 0;
            do{
                System.out.println("Inserisci la quantità di denario con la quale vuoi entrare in partita (Solo cifre e minore del tuo saldo)");
                entrata = Integer.parseInt(keyboardreader.readLine());
            }while(entrata>saldo);
            System.out.println("Sei entrato con EUR "+entrata);
            saldo -= entrata;

            try (
                //Socket datasocket = new Socket("192.168.67.81", 8000);
                Socket datasocket = new Socket("localhost", 8000)) {
                DataOutputStream socketwriter = new DataOutputStream(datasocket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(datasocket.getInputStream()));

                int i;
                int punteggio = 0;
                int numero = 0;
                String carta = "";
                String msg = nomeUtente;

                //Comunicazione del nome utente
                socketwriter.writeBytes(nomeUtente + "\n");

                //Attesa degli altri giocatori
                System.out.println("Attendi gli altri giocatori ");
                String conferma = reader.readLine();
                
                // Tutti gli altri giocatori sono collegati
                System.out.println(conferma);

                // Mazzo personale
                
                ArrayList<String> mazzo = new ArrayList<String>();
                ArrayList<Integer> numeri = new ArrayList<Integer>();
                ArrayList<Integer> numeriMazziere = new ArrayList<Integer>();    

                sezione();

                int y=0;
                // Prima e seconda carta
                for(i=0;i<2;i++){
                    carta = reader.readLine();
                    System.out.println(carta);
                    mazzo.add(carta);
                    numero = getNumero(carta);   
                    if(numero==1)
                        numero=11;
                    punteggio += numero;
                    numeri.add(numero);
                    if(punteggio>21){
                        y = 0;
                        while(y<numeri.size()){
                            if(numeri.get(y) == 11 ){
                                punteggio -= 10;
                                numeri.set(y,1);
                                y = numeri.size();
                            }
                            y++;
                        }
                    }
                }

                sezione();

                if((numeri.get(0) == 10 && numeri.get(1) == 1) || (numeri.get(0) == 1 && numeri.get(1) == 10)){
                    System.out.println("Hai fatto BlackJack ");
                    punteggio = 21;
                    socketwriter.writeBytes(punteggio+"\n");
                }
                else{
                    socketwriter.writeBytes(punteggio+"\n");
                    System.out.println("Il tuo punteggio : " + punteggio);
                }

                String mazziere = "";
                
                sezione();

                mazziere = reader.readLine();
                System.out.println("Carta del mazziere : " + mazziere);
                int cartaMazziere = getNumero(mazziere);
                if(cartaMazziere == 1)
                    cartaMazziere=11;
                numeriMazziere.add(cartaMazziere);
                if(punteggio<21){
                    conferma = reader.readLine();
                    System.out.println(conferma);
                }

                sezione();
                
                //Turno del giocatore
                if(punteggio == 21){
                    socketwriter.writeBytes("BLACKJACK \n");
                }
                while (punteggio<21 && !msg.equalsIgnoreCase("STARE") ) {
                    System.out.println("\nIL TUO MAZZO\n");
                    for(i=0;i<mazzo.size();i++)
                        System.out.println(" - "+mazzo.get(i));
                    System.out.println("Il tuo punteggio : " + punteggio);
                    do {
                        do {
                            System.out.println("\n[Carta] - [Stare] - [Exit]");
                            msg = keyboardreader.readLine();
                        } while (!msg.equalsIgnoreCase("CARTA") && !msg.equalsIgnoreCase("STARE")&& !msg.equalsIgnoreCase("EXIT"));
                        if(msg.equalsIgnoreCase("CARTA")){
                            socketwriter.writeBytes(msg + "\n");
                            carta = reader.readLine();
                            System.out.println(carta);
                            mazzo.add(carta);
                            numero = getNumero(carta);   
                            if(numero==1)
                                numero=11;
                            punteggio += numero;
                            numeri.add(numero);
                            if(punteggio>21){
                                y = 0;
                                while(y<numeri.size()){
                                    if(numeri.get(y) == 11 ){
                                        punteggio -= 10;
                                        numeri.set(y,1);
                                        y = numeri.size();
                                    }
                                    y++;
                                }
                            }
                            System.out.println("\nIL TUO MAZZO\n");
                            for(i=0;i<mazzo.size();i++)
                                System.out.println(" - "+mazzo.get(i));
                            System.out.println("Il tuo punteggio : " + punteggio);
                            socketwriter.writeBytes(punteggio+"\n");
                        }
                    } while ((!msg.equalsIgnoreCase("EXIT"))&& (punteggio<21) && (!msg.equalsIgnoreCase("STARE")));
                    if(punteggio > 21){
                        System.out.println("Hai sballato ");
                        socketwriter.writeBytes("SBALLATO\n");
                    }
                    if(punteggio == 21){
                        System.out.println("Hai fatto 21 ");
                        socketwriter.writeBytes("21\n");
                    }
                    if(msg.equalsIgnoreCase("EXIT")){
                        System.out.println("Hai abbandonato la partita ");
                        socketwriter.writeBytes("EXIT\n");
                    } 
                    if(msg.equalsIgnoreCase("STARE")){
                        System.out.println("Hai deciso di stare ");
                        socketwriter.writeBytes("STARE\n");
                    } 
                    
                }
                
                sezione();
                //Seconda carta mazziere
                msg = reader.readLine();
                System.out.println(msg);
                cartaMazziere = getNumero(msg.substring(29));
                if(cartaMazziere == 1)
                    cartaMazziere=11;
                    
                numeriMazziere.add(cartaMazziere);
                
                //Punteggio mazziere
                msg = reader.readLine();
                System.out.println(msg); 

                int punteggioMazziere = Integer.parseInt(msg);
                System.out.println("Punteggio attuale mazziere : "+ punteggioMazziere);
                while(punteggioMazziere < 17){
                    msg = reader.readLine();
                    System.out.println("Carta mazziere : "+msg);
                    cartaMazziere = getNumero(msg);
                    if(cartaMazziere==1)
                        cartaMazziere=11;
                    punteggioMazziere += cartaMazziere;
                    numeriMazziere.add(cartaMazziere);
                    if(punteggioMazziere>21){
                        y = 0;
                        while(y<numeriMazziere.size()){
                            if(numeriMazziere.get(y) == 11 ){
                                punteggioMazziere -= 10;
                                numeriMazziere.set(y,1);
                                y = numeriMazziere.size();
                            }
                            y++;
                        }
                    }
                    System.out.println("Punteggio mazziere : "+punteggioMazziere);
                }

                sezione();

                System.out.println("Il mazziere si ferma a "+punteggioMazziere);
                
                sezione();

                System.out.println("\nIL TUO MAZZO\n");
                for(i=0;i<mazzo.size();i++)
                    System.out.println(" - "+mazzo.get(i));
                System.out.println("Il tuo punteggio : " + punteggio);
                    

                sezione();
                boolean avviso = false;
                if(punteggioMazziere >21 && punteggio<21 && !avviso){
                    System.out.println("Hai vinto ");
                    System.out.println("Hai vinto EUR "+entrata*2);
                    saldo += entrata*2;
                    avviso = true;
                }
                if(punteggioMazziere <=21 && punteggioMazziere > punteggio && !avviso){
                    System.out.println("Hai perso ");
                    System.out.println("Hai perso EUR "+entrata);
                    avviso = true;
                }
                if(punteggioMazziere < punteggio && punteggio<21 && !avviso){
                    System.out.println("Hai vinto ");
                    System.out.println("Hai vinto EUR "+entrata*2);
                    saldo += entrata*2;
                    avviso = true;
                }
                if(punteggio == 21 && mazzo.size()==2 && !avviso){
                    System.out.println("Hai fatto BlackJack ");
                    System.out.println("Hai vinto EUR "+entrata*2.5);
                    saldo += entrata*2.5;
                    avviso = true;
                }
                if(punteggio == 21 && mazzo.size()>2 && punteggioMazziere!= 21 && !avviso){
                    System.out.println("Hai fatto 21 ");
                    System.out.println("Hai vinto EUR "+entrata*2);
                    saldo += entrata*2;
                    avviso = true;
                }
                if(punteggioMazziere <=21 && punteggioMazziere == punteggio && !avviso){
                    System.out.println("Hai pareggiato ");
                    System.out.println("Hai vinto EUR "+entrata);
                    saldo += entrata;
                    avviso = true;
                }
                if(punteggio > 21 && !avviso){
                    System.out.println("Hai perso ");
                    System.out.println("Hai perso EUR "+entrata);
                    avviso = true;
                }
                //}
                sezione();
                Thread.sleep(5000);
                System.out.println("Gioco terminato");     
                datasocket.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    //Metodo per avere il valore di ogni carta dalla stringa 
    public static int getNumero(String t){
        if(t.substring(0, 2).equals("10"))
            return 10;
        return Integer.parseInt(t.substring(0,1));
    }

    //Metodo per separare tra una comunicazione e l'altra e non intasare il main
    public static void sezione(){
        System.out.println("-------------------------------------------------------------------------");
    }
}
