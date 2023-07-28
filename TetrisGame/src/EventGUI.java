
import com.thoughtworks.xstream.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.*;


class EventGUI { //1
    public String appName = "TetrisGame";
    public String ipClient;
    public String timestamp;
    public String label;

    public EventGUI(String eventLable, ConfigParameters parameters){
        //inizializzo tutte le componenti che definiscono l'evento
        ipClient = parameters.ipClient;
        label = eventLable;
        timestamp = Timestamp.from(Instant.now()).toString(); //2

        this.serializeToXML(parameters); //serializzo l'oggetto in XML
    }

    private void serializeToXML(ConfigParameters parameters){
        XStream xs = new XStream();

        String eventXML = xs.toXML(this); //serializzo da java a formato XML
        
        sendXMLEvent(eventXML, parameters); //invia l'evento di log in XML alla classe ServerLog
    }

    private void sendXMLEvent(String event, ConfigParameters parameters){
        //3
        try(DataOutputStream dout = new DataOutputStream((new Socket(parameters.ipServerLog, parameters.portServerLog)).getOutputStream()))
        { 
            dout.writeUTF(event); 
        } catch (Exception e) {e.printStackTrace();}
    }
}


/*
1) quando clicco qualche comando di gioco o dell'interfaccia dovrò prima di tutto 
costruirmi l'oggetto evento e poi lo converto in XML. Non devo manualmente 
costruirmi una stringa XML pezzo per pezzo ma devo utilizzare XStream cioè la classe
che serializza e deserializza.
Ho bisogno di un oggetto che mi viene istanziato quindi avrà un costruttore dove 
gli passo quello che io ho deciso, nel mio progetto, avere come eventi cioè quelli 
che ho messo nella specifica di analisi cioè quali eventi noi stiamo andando a considerare 
e quali sono i dati da inviare, questi dati sono gli attributi di questa classe.

1.1) Ora questo è l'oggetto evento che mi contiene questi attributi quando io man mano genero l'evento, 
poi ho dei metodi che posso metterli nello stesso oggetto o in un oggetto diverso. 
Quindi il metodo che serializza in XML lo potrei mettere o in quella stessa classe metodo "serializzaInXML"
oppure in una classe diversa che poi prende quell'oggetto e lo serializza ma questa è una scelta secondaria. 
Andando più sulla scelta primaria cioè di usare la stessa classe per fare tutte e due le cose. 
Io potrei prendere un metodo che si chiama serializzaToXML() per esempio e questo è facile farlo perché prendo 
il this o il mio oggetto e poi faccio toXML() e ce l'ho pronto come stringa, questa stringa va inviata alla classe log. 

Quindi se devo inviare un XML mi vado a vedere i metodi di labboratorio su come faccio un 
inviaInXML e quindi avrò un altro metodo di questa classe che si chiama inviaInXML() e invio
alla classe ServerLog.  

2) con Timestamp.from(Instant.now()) mi creo un oggetto Timestamp che mi fornisce la data e l'ora della
creazione dell'evento con precisione ai millisecondi, per poi chiamare toString() che applicato all'oggetto 
Timestamp me lo restituisce come stringa
FONTI: https://docs.oracle.com/javase/8/docs/api/java/sql/Timestamp.html#toString--
       https://www.javatpoint.com/java-timestamp

3) DataOutputStream e' uno stream per primitive Java e permette di scrivere primitive java data type su 
un output stream in modo portatile. Il DataOutputStrem è uno stream per inviare le stringhe cioè io posso
far viaggiare caratteri in questo stream cioè è come l'ObjectOutputStream che però mi codifica una stringa
in binario e sbaglio se loutilizzo perché è il binario della codifica di java e se dall'altra parte non ho
un'applicazione java non riuscirà a codificare quella stringa perché sarà in formato bianrio di java. Se 
invece io voglio avere un socket in cui viaggiano caratteri con formato UTF allora devo usare il DataOutputStream
in cui posso sempre prendere da socket il mio outputstream e poi posso agganciarlo ad un DataOuputStream invece che
ad un ObjectOutputStream.

FONTI:
lab 6
https://docs.oracle.com/javase/8/docs/api/java/io/DataOutputStream.html
*/