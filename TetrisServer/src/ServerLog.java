import java.net.*;
import java.io.*;

public class ServerLog {
    public static void main(String[] args) { 
        //1
        try(ServerSocket servs = new ServerSocket(8080)) //2
        { 
            while(true){
                try(Socket sock = servs.accept(); //3
                    DataInputStream din = new DataInputStream(sock.getInputStream());) //4
                {
                    String event = din.readUTF(); //5

                    //invoco la validazione dell'evento di log tramite XML Schema
                    if(ValidateXML.validateFile(event, "./myfiles/logEvent.xsd")){
                    //aggiungo l'evento XML al file di log in modo incementale 
                        try(FileWriter fw = new FileWriter("logFile.txt", true); //6
                            BufferedWriter bw = new BufferedWriter(fw);
                            PrintWriter out = new PrintWriter(bw)) //6.1
                        {
                            out.println(event);
                            out.println("");
                        } //7
                    }
                }
            }
        } catch(IOException e) {e.printStackTrace();} //8 
    }
}

/*
1) Devo leggere da un socket. Un socket è la metafora di una presa che io collego la 
mia spina e quindi mi connetto e la stessa cosa avviene fra client e server e il 
socket è quindi una sorta di canale di comunicazione ed è bidirezionale.

1.1) Per agganciarsi il server deve essere in ascolto e il client si connette. 
Per stare in ascolto bisogna stabilire un numero (porta) che è la metafora di 
ingresso all’interno dell’applicativo. Quindi l’applicativo chiede al sistema 
operativo una porta per ascoltare, si stabilisce un canale di comunicazione perché 
il client punta all’indirizzo IP su quella porta, si collegano i due applicativi e 
si possono parlare in maniera applicativa. Quindi c’è un passaggio tecnico in cui 
devono agganciare questi socket (è come se ci fosse un tecnico che va a mettere la spina) 
dopo di che in quel cavo si possono parlare applicativamente parlando con i loro dati.

1.2) Dobbiamo usare il try catch perché anche in rete abbiamo le eccezioni siccome 
ci può essere una disconnessione o altri problemi quindi ogni volta che usciamo 
fuori dalla JVM usiamo il try catch per gestire le eccezioni. Utilizziamo il try 
con risorse così non abbiamo problemi di chiudere socket etc... 

2) Creiamo un TCP/IP server attraverso la classe ServerSocket, che è in grado di 
ricevere richieste di connessione e quando c’è una richiesta restituisce un socket 
per l’applicativo che lo chiede. Per metterlo in ascolto utilizzo la porta 8080 
che è la porta che è sempre libera.

3) Creiamo un socket all’interno del server. Poi con accept() che è un’istruzione 
bloccante, cioè si ferma in attesa e nel momento in cui un client si connette alla porta 
si procede. Allo stesso modo fatto per i file  utilizziamo ObjectInputStream per leggere
i bit dal socket siccome ha proprio un costruttore che prende uno stream di input e utilizzo
proprio il socket per ottenere un input stream.

4) un DataInputStream legge tipi di dato primitivi Java da un input stream. Con DataInputStream vado a leggere
la codifica testuale che invio attraverso DataOutputSteam in EventGUI.java 

FONTI: 
lab 6
https://docs.oracle.com/javase/8/docs/api/java/io/DataInputStream.html

5) vado a leggere il formato XML direttamente in testo tanto poi abbiamo visto nel lab6 come commutare questo 
testo ad un formato java attraverso XStream con fromXML(), per questo motivo il server non necessità delle 
librerie XStream siccome non va a manipolare l'XML ma lo va solo a scrivere su file di testo e in questo 
modo si dimostra la piena compatibilità cioè io riesco a leggere da questa parte il formato XML inviato.

6) Qui utilizzo un metodo che ho trovato su stackoverflow per scrivere in append diverse
volte sullo stesso file in maniera efficiente. La classe FileWriter è una convenience class
per scrivere file di caratteri, il secondo parametro che abbiamo dato al costruttore di FileWriter
serve per dire che se il file esiste già effettua l'append altrimenti se il file non esiste lo 
crea e ci scrive ed effettua sempre l'append man mano che continuiamo a scriverci. 
Poi nei casi in cui bisogna effettuare numerose scritture incrementali sullo stesso file è 
consigliato l'utilizzo della classe BufferedWriter che è una classe che "writes text to a character-output
stream, buffering characters so as to provide for the efficient writing of single characters, arrays, and strings".
La classe PrintWriter permette di stampare una rappresentazione formattata di un oggetto su output stream di testo
ed implementa tutti i metodi print presenti nella classe PrintStream. Utilizziamo questa classe perché
ci da accesso all'utilizzo del metodo println() come di solito lo si utilizza per System.out
Quindi in particolare l'utilizzo di FileWriter con BufferedWriter è consigliato quando c'è la 
necessità di chiudere e aprire molte volte lo stesso file per effettuare scritture incrementali.

FONTI: 
https://stackoverflow.com/questions/1625234/how-to-append-text-to-an-existing-file-in-java
https://docs.oracle.com/javase/8/docs/api/java/io/FileWriter.html
https://docs.oracle.com/javase/8/docs/api/java/io/BufferedWriter.html
https://docs.oracle.com/javase/8/docs/api/java/io/PrintWriter.html 

6.1) potrei anche attuare un altro metodo in cui evito di usare PrintWriter e al posto di println()
usere direttamente bw.write(event); mentre al posto di println("") userei bw.newLine(); che sono delle
funzioni direttamente implementate in BufferedWriter

7) posso evitare di mettere i catch nei livelli interni tanto tutte le istruzioni che ci sono 
possono lanciare tutte IOException e quindi metto un unico catch al try più esterno di tutti

8) Il metodo printStackTrace() in caso di eccezione mi stampa le varie chiamate 
di metodo per capire dov’è che ci sono stati problemi. Con printStackTrace() mi fornisce
la sequenza di chiamate a catena che ci sono state e quindi riusciamo subito a capire
in quale riga è stata generata l'eccezione e le stampa una dopo l'altra.
*/