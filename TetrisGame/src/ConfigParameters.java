import com.thoughtworks.xstream.*;
import java.nio.file.*;

class ConfigParameters {
    public String ipDB = "127.0.0.1";
    public int portDB = 3306;
    public String userDB = "root";
    public String passwordDB = "";
    public String ipServerLog = "127.0.0.1";
    public int portServerLog = 8080;
    public String ipClient = "127.0.0.1";
    public int rankingTopX = 10;
    public int startLevel = 1;
    public String windowColor = "#282F3E";
    public String playingFieldColor = "#10141c";
    public String textFont = "Helvetica";
    public int textSize = 15;
    public String textColor = "#f08414"; 

    public ConfigParameters(){
        ConfigParameters userParam = null;
        XStream xs = new XStream(); //1
        xs.useAttributeFor(ConfigParameters.class, "ipDB");
        xs.useAttributeFor(ConfigParameters.class, "portDB");
        try{ 
            String xmlContent = new String(Files.readAllBytes(Paths.get("./myfiles/configparameters.xml"))); //2
            //invoco la validazione del file di configurazione XML
            if(ValidateXML.validateFile(xmlContent, "./myfiles/configparameters.xsd")){
                //deserializzo il contenuto XML come oggetto Java
                userParam = (ConfigParameters) xs.fromXML(xmlContent); //3
                //imposto i valori con quelli che ho recuperato dal file di configurazione
                this.ipDB = userParam.ipDB;
                this.portDB = userParam.portDB;
                this.userDB = userParam.userDB;
                this.passwordDB = userParam.passwordDB;
                this.ipServerLog = userParam.ipServerLog;
                this.portServerLog = userParam.portServerLog;
                this.ipClient = userParam.ipClient;
                this.rankingTopX = userParam.rankingTopX;
                this.startLevel = userParam.startLevel;
                this.windowColor = userParam.windowColor;
                this.playingFieldColor = userParam.playingFieldColor;
                this.textFont = userParam.textFont;
                this.textSize = userParam.textSize;
                this.textColor = userParam.textColor;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

/*
1) prima creo un oggetto XStream che è come se fosse un flusso OutputStream o un InputStream e serve
a serializzare e deserializzare in XML e rappresenta un flusso in cui posso far viaggiare oggetti XML
simile al flusso di java per oggetti binari.

2) Devo leggere il file di configurazione XML.
La classe Files è una classe con metodi statici e tra questi metodi possiamo usare la write() 
che in un colpo solo riesce a scrivere tutto quello che ci serve su file. E possiamo anche evitare 
di metterlo nelle () del try con risorse siccome fa tutto da solo cioè in un colpo solo apre e 
chiude tutto. Utilizziamo poi la classe Paths per passare il percorso che serve proprio per 
gestire vari tipi di path e in questo caso lo utilizziamo perché lo richiede questo tipo di metodo.
Dopo aver dato il path di dove deve scrivere la risorsa, devo dirgli che cosa devo scrivere 
e siccome i file sono sempre binari nel file system utilizzo getBytes() che mi permette di 
prendere il dato e di scriverlo su file. 
Per leggere invece ci basta usare il metodo readAllBytes() e getBytes() non ci serve più. (reg.16 min.24)

3) Siccome nei casi in cui volevamo leggere o scrivere utilizzavamo ObjectInputStream e ObjectOutputStream
che ci fornivano le funzioni readObject() e writeObject() che ci permettevano di leggere e scrivere codice 
binario di Java ma questa codifica si basava su un formato binario proprietario di java e questo significa 
che occorrono N interfacce per N applicazioni non java e dualmente altrettante.
Per questo motivo usilizziamo lo standard XML che permette ai documenti di essere processati da applicazioni
di qualsiasi natura definendone la struttura mediante XML Schema permettendo la realizzazione di applicazioni
device independent.
La libreria XStream è l'equivalente di ObjectInputStream e ObjectOutputStream per l'XML e ci permette di 
serializzare automaticamente un oggetto Java in codifica XML attraverso toXML() e a quest'ultima di essere
deserializzata in un oggetto Java attraverso fromXML(). Il file XML rispetto al file di testo è semistrutturato
già e questo mi permette di fare query e manipolarlo in maniera più standard ad esempio attraveso le API DOM.
Tutto questo mi permette di: archiviare la struttura XML su file o database, posso trasmettere l'oggetto tra 
due piattaforme, posso creare un file di configurazione per la mia applicazione in modo tale che un utente
possa inizializzare degli oggetti, posso effettuare un test case I/O cioè l'applicazione prende l'input vede
l'output e vede se corrrisponde al mio XML. 


- questa è una classe molto semplice perché è fatta da tanti attributi
public perché sono tutti quanti i parametri che mi servono quindi io i 
parametri li vedo nella specifica quali sono ed è come una struttura dati 
ad esempio del C++ mentre in questo caso essendo tutto classe ci possono 
essere classi che sono fatte prevalentemente da campi.

- Poi ci sarà un costruttore in cui io leggo il file XML e lo converto in 
un oggetto e di fatto io questo lo avrei gratis perché XStream me lo fa già 
questo oggetto, se vado a vedere nei laboratori io ho un modo automatico 
in XStream per leggere XML e creare un oggetto java. Per cui di fatto la 
struttura che mi fa questo oggetto java è esattamente quella che io ho pensato
perché il mio XML sarà fatto da una sequenza di valori come li ho immaginati 
nel mio ParametriDiConfigurazione. Quindi vedere laboratorio 6 quando leggo 
XML e creo un oggetto

- siccome questa classe avrà tutti metodi pubblici basterà passarne il riferimento alle altre
classi che abbiano bisogno dei parametri e ognuno si prende quello che gli pare.
Può anche essere una classe statica che ha tutti i valori accessibili attraverso
il nome della classe potrebbe anche non servire un riferimento ad un oggetto specifico
*/
