
import javax.xml.*; 
//import javax.xml.parsers.*;  
import java.io.*; 
import org.xml.sax.*; 
import javax.xml.validation.*; 
import javax.xml.transform.stream.*;  

class ValidateXML { //1
    public static boolean validateFile(String xml, String xsd){ //2
        try{
            //DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder(); //3
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); //4
        
            //Document d = db.parse(new File("./myfiles/configparameters.xml")); //5
            Schema s = sf.newSchema(new StreamSource(new File(xsd))); //6
            //s.newValidator().validate(new DOMSource(d)); //7
            s.newValidator().validate(new StreamSource(new StringReader(xml))); //7
                
        } catch(Exception e){
            if(e instanceof SAXException) //8
                System.out.println("Errore di validazione: " + e.getMessage());
            else
                System.out.println(e.getMessage());

            return false;
        }
        
        return true;
    }
}

/*
1) se il documento è modificabile da un agente esterno (es. e' un file di configurazione oppure un file 
proveniente da socket) si deve validare dinamicamente, adoperando le API Java.xml.validation:
https://docs.oracle.com/javase/8/docs/api/javax/xml/validation/package-summary.html

2) Passo come argomenti una stringa con il contenuto del file xml e il path di dove si trova il file xsd
che mi serve per validare

3) DocumentBuilder sono delle classi che permettono di istanziare dei parser che sono quei software
che verificano un file di testo. Questi poi producono degli oggetti DOM da documenti XML. Quando troviamo
Factory vuol dire che c'è un'istanza di qualcosa ed è un'istanza che non viene fatta con un costruttore new
ma viene fatta con dei metodi di classi statiche. I metodi factory sono nati perché il DOM ha creato delle 
interfacce e siccome le interfacce non hanno costruttore e quindi ha bisogno di metodi factory per poter poi 
generare degli oggetti. Attraverso newDocumentBuilder andiamo a creare un builder di documento che mi
permetterà poi di creare degli oggetti DOM. DocumentBuilderFactory istanzia dei parser che producono oggetti 
DOM da documenti XML 

4) lo stesso lo abbiamo anche sugli schema con SchemaFactory cioè la fabbrica degli schemi. Passo come costante
quello del W3C XML e questa è una costante che la libreria mette a disposizione per identificare quel linguaggio
xsd che troviamo nei file xsd. Quindi questi fino ad ora erano il builder del documento XML e il builder
del documento di schema.

4.1) SchemaFactory legge rappresentazioni esterne di schemi, per la validazione

5) sul Builder db vado a creare il mio documento dal file XML "configparameters.xml". Quindi io a questa riga
ho il DOM del mio documento xml

6) faccio la stessa cosa per lo schema, utilizzando il file xsd vado a creare la mia istanza di schema

7) facendo newValidator() sullo schema 's' valido il documento xml. Passo il contenuto del file xml come stringa
e non come un path di un file in modo che questa funzione possa essere usata sia da ConfigParameters.java che da
ServerLog.java.
Ho utilizzando la classe StreamSource (acts as an holder for a transformation Source in the form of a stream of XML markup).
Mentre la classe StringReader crea uno stream di caratteri partendo da una stringa. 
FONTE: https://stackoverflow.com/questions/46995839/how-to-validate-an-xml-string-in-java
Il prof a lezione aveva scritto:
Document d = db.parse(new File("./myfiles/configparameters.xml")); //5 -> rigo sopra a Schema s=...
s.newValidator().validate(new DOMSource(d)); -> rigo dopo Schema s=...

8) SAXException: il DOM (che è un modello a oggetti del documento) ed è pesante come memoria siccome il DOM
crea tutto l'albero e poi dopo lo possiamo esplorare come ci pare e piace. Invece il SAX è un altro modello
di parsing dei documenti XML e funziona a eventi e fa una sola passata cioè prende il file testuale XML  e
lo parsa riga per riga elemento per elemento e mentre parsa non crea un oggetto documento, ma scorre riga 
per riga alla ricerca di eventi. Per cui io in questo modo posso cercare eventi specifici e queste SAXExcemption
sono previste perché se c'è qualcosa che non va e c'è un errore di validazione stampo l'errore che c'è. Quindi 
l'approccio SAX viene usato ad eventi cioè un parsing ad eventi del documento e non crea l'albero come fa il DOM 
ma legge e butta fino a quando non trova l'evento specifico (se lo trova) che l'è stato installato su quel parser.
*/
