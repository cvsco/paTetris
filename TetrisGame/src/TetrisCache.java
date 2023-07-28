
import java.io.*;

public class TetrisCache {

    public static void saveCache(){
        try( //1
            ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream("./myfiles/tetrisCache.bin")); 
            ){
            oout.writeObject(new MatchData()); //2
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

    //funzione che recupera e reimposta i dati sull'interfaccia
    //public static MatchData loadCache(){
    public static boolean loadCache(){
    	try(
            ObjectInputStream oin = new ObjectInputStream(new FileInputStream("./myfiles/tetrisCache.bin"));
            ){
            MatchData gameData = (MatchData) oin.readObject(); //3
            TetrisGUI.user.setText(gameData.getUsername()); //rimetto a posto l'username salvato in cache
            TetrisGUI.currentGame = gameData.getGameData(); //rimetto a posto l'oggetto GameManager salvato in cache 
            
            return true;
            
    	} catch(IOException | ClassNotFoundException ex){
    		ex.printStackTrace();
    	}
        
    	return false;
    }
}

/*
1) Utilizziamo il try con risorse inserendo tra le () tutte le istruzioni
che sono risorse del mio sistema, cioè la JVM chiederà al sistema operativo
di allocare risorse. Per esempio quando apro un file (che è una risorsa) 
ed essendo aperto in quel momento se non lo chiudo nessun altro può scriverci. 
Stessa cosa per un socket e qualsiasi altra cosa che è una risorsa cioè che va fuori
dalla JVM che poi mi richiede un impegno da parte del mio processo. 
Questa risorsa condivisa sarà una risorsa che devo racchiudere nelle () perché così in questo modo 
java lo chiude da solo (anche in caso di eccezione). Poi nelle {} metto tutte le istruzioni 
che non solo possono generare eccezioni ma non sono una istanzazione di risorsa.
FileOutputStream serve per effettuare un flusso di uscita verso file.

1.1) siccome FileOutputStream con la write o FileInputStream con la read consentono di scrivere e leggere
solo un solo byte e sono scomodi da gestire essendo troppo di basso livello e se continuassimo ad usare
il byte dovremmo fare cicli for e quindi andremmo ad usare un approccio implementativo, rispetto alla nostra
applicazione, che non richiede questo tipo di approccio. Quindi possiamo utilizzare delle librerie di 
più alto livello e in java ci sono dei flussi Object che sono flussi generici e ci permette di 
disinteressarci una volta per tutte di cosa andiamo ad archiviare o leggere perché ci penserà automaticamente java.
Questi flussi oggetto permettono di serializzare automaticamente un oggetto indipendentemente dalla struttura da 
cui è composto. Il bello di questi flussi oggetto è che possono essere concatenati ad un flusso file per salvare 
l'oggetto o prenderlo. Quindi i flussi in generale in java si possono concatenare uno dentro l'altro e questo 
permette di trasformare il dato con step multipli. (reg.16 min.3)

2) utilizzo un oggetto MatchData per raccogliere dall'interfaccia tutte le informazini che mi serve salvare. 

3) Reg.16 min.9 
Quando leggo un oggetto leggo dei byte e devo fare un cast per dirgli come mi deve dare l'oggetto. 
Di conseguenza quando io vado a leggere un oggetto se quello è un intero mi va bene perché 
java sa come istanziare un intero, ma nel momento in cui vado a leggere un oggetto di una classe
che ho definito io e siccome quando leggo un oggetto non so da dove proviene per cui potrei
non avere il file .class della mia classe nella cartella o comunque nella visibilità come 
applicazione java e quindi in qualche modo devo leggere dei bit che sono il contenuto di un’istanza 
che devo andare a riempire nei vari attributi e nei vari campi e se non ho il file .class non 
so di che cosa sto parlando. Per cui c’è un’eccezione che devo gestire in più che è la ClassNotFoundException 
e vuol dire che nella mia visibilità non ho trovato il file .class e quindi non riesco a creare 
un’istanza di quella classe. Qui si parla di risorse (file .class) per cui devo accedere al 
file system e non sta dentro la JVM e devo andare a caricare dinamicamente il file .class perché 
fino ad allora non l’ho mai utilizzato siccome java ha un meccanismo di caricamento dinamico per cui 
mi permette di agganciare le nuove classi man mano che mi servono. Quindi quando ho questo cast devo 
vedere se ho il mio file .class e se non ce l’ho mi lancia l’eccezione ClassNotFoundException.
*/