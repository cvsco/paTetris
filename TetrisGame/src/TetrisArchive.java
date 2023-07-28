import java.util.*;
import java.sql.*;

class TetrisArchive {
    //aggiungere tutte le informazioni sul database prese dai parametri di configurazione
    private String ipDB;
    private int portDB;
    private String userDB;
    private String passwordDB;
    private int maxRecord;

    public TetrisArchive(ConfigParameters cp){
        ipDB = cp.ipDB;
        portDB = cp.portDB;
        userDB = cp.userDB;
        passwordDB = cp.passwordDB;
        maxRecord = cp.rankingTopX;
    }

    /*prendo i dati sui giocatori dal DBMS e li inserisco 
    in una lista che restituisco al chiamante*/
    public List<Player> loadPlayers(){
        List<Player> players = new ArrayList<>();
        /*1,2,3*/
        try(//creo la connessione al mio database
            //siccome vado fuori dalla JVM devo fare un try catch
            Connection co = DriverManager.getConnection("jdbc:mysql://" + ipDB + ":" + portDB + "/tetris", userDB, passwordDB);
            //devo creare un oggetto Statemnet siccome se poi faccio più volte la
            //stesa query allora lui usa lo stesso oggetto e non lo deve istanziare
            Statement st = co.createStatement();
            ) {
            //La classe ResulSet mi serve per contenere il risultato della query
            ResultSet result = st.executeQuery("SELECT * FROM players ORDER BY score DESC LIMIT " + maxRecord); //al posto di 10 mettere maxRecord
            while(result.next())
                players.add(new Player(result.getString("username"), result.getInt("score")));
        } 
        catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return players;
    }

    /*cerco il giocatore nel database se lo trovo vedo se ha fatto un nuovo record personale 
    se non lo trovo lo aggiungo al database*/
    public void checkPlayer(String user, int score){
        List<Player> players = new ArrayList<>();

        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://" + ipDB + ":" + portDB + "/tetris", userDB, passwordDB);
            PreparedStatement ps = co.prepareStatement("SELECT * FROM players WHERE username = ?");  
            ) {
            ps.setString(1, user);
            ResultSet result = ps.executeQuery();
            while(result.next())
                players.add(new Player(result.getString("username"), result.getInt("score")));
        } 
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
        if(players.size() == 0){
            //aggiungo un nuovo giocatore
            addNewPlayer(user, score); 
        } else {
            //il giocatore era già registrato, controllo
            //se ha stabilito un nuovo record personale
            if(players.get(0).getScore() < score)
                updatePlayer(user, score); //se ha un nuovo record personale aggiorno il punteggio
        }          
    }

    private void addNewPlayer(String user, int score){
        try(/*4,5,6*/
            Connection co = DriverManager.getConnection("jdbc:mysql://" + ipDB + ":" + portDB + "/tetris", userDB, passwordDB);
            PreparedStatement ps = co.prepareStatement("INSERT INTO players VALUES(?, ?)");
            ) {
            ps.setString(1, user); ps.setInt(2, score);
            //executeUpdate() perché devo eseguire un inserimento in cui viene stampato
            //un intero che indica quante righe ho inserito con la mia query
            //System.out.println("inserimento nuovo giocatore"); 
            System.out.println("rows affected: " + ps.executeUpdate()); //7
        } 
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private void updatePlayer(String user, int score){
        try(
            Connection co = DriverManager.getConnection("jdbc:mysql://" + ipDB + ":" + portDB + "/tetris", userDB, passwordDB);
            PreparedStatement ps = co.prepareStatement("UPDATE players SET score = ? WHERE username = ?");
            ) {
            ps.setInt(1, score); ps.setString(2, user);
            //executeUpdate() perché devo eseguire un aggiornamento in cui viene stampato
            //un intero che indica quante righe ho aggiornato con la mia query
            System.out.println("rows affected: " + ps.executeUpdate()); //7
        } 
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}

/*
1) dopo aver istanziato l'ObservableList devo fare una query sul database e per farla mi serve una libreria siccome dobbiamo
comunicare con un'applicazione esterna che java non conosce ed ogni costruttore di database (come oracole, miscrosoft e MySQL) 
quando fanno i database definiscono i protocolli specifici che devono essere usati cioè siccome il client di mysql le connessioni 
le fa in TCP/IP ma quello che viaggia in questo TCP/IP è una specifica che stabilisce mysql e java non la conosce e il costruttore di DBMS 
rilascia anche una libreria per i principali linguaggi. Quindi per agganciare la libreria dobbiamo scaricarla dal sito MySQL (il prof lo ha già fatto) 
e poi inserirla in ”Libraries”, cioè dobbiamo aggiungerci il file .jar creato da MySQL per collegarci al DBMS MySQL  siccome questo file.jar 
contiene le classi java per usare il  protocollo di MySQL che è proprietario (in NetBeans tasto destro su Librarie->Add JAR) 

2) Java ha una sua libreria principale che si chiama jdbc (java database connectivity) e che fornisce le classi per gestire i vari database ed ogni database 
ha la sua libreria che si chiama driver e il ragionamento è lo stesso del driver di una periferica hardware. Quindi allo stesso modo per tutti i database che 
devo usare ho bisogno del driver di quel database siccome mi connetto tramite questo driver al protocollo che mi permette di interfacciarmi. Quindi un jdbs 
driver è un adattatore che converte le richieste da java al protocollo proprietario che può essere compreso da quel DBMS,  quindi è un software scritto dal 
costruttore del DBMS perché solo lui conosce quel protocollo e lui si può interfacciare con il protocollo java che è stato definito nelle specifiche java.

3) Per connettermi posso usare una URI che utilizza un protocollo (come http per il web solo che in questo caso uso jdbc che è il protocollo di java 
e poi metto :mysql che è il protocollo specifico di mysql) e da questo formato della URL java capisce che sto passando dal protocollo generico di java 
a quello specifico. Dopo di che aggiungo il nome dell’host e la porta e poi /nomeDatabase e così intercetto il mio database. Poi devo metterci nome utente e password del database. 

4) lo Statement di solito viene compilato per essere poi eseguibile lato MySQL ma questo è inefficiente siccome ogni volta dovremmo compilare un oggetto Statement 
(cioè prendo la stringa SQL e la trasformo in qualche formato che possa essere eseguito sul DBMS). Ci sono gli statement preconfezionati PreparedStatement per risolvere questo problema 
e sono molto utili quando io ho dei parametri, sia per motivi di sicurezza perché mi impediscono di iniettare codice strano e sia per motivi legati all’efficienza cioè al fatto di dover 
rieseguire molte volte quello statement, in questo modo lo statement viene compilato una sola volta in maniera da poter eseguirlo più volte con diversi parametri. 
Lo statement preconfezionato mi viene compilato con la possibilità di non specificare il parametro numerico (per questo c’è il ?)

5) i metodi set() vado a specificare il parametro che devo assegnare (i numeri all’interno sono dei numeri ordinali che indicano l’ordine di assegnamento dei parametri seguendo 
l’ordine di lettura dello statement SQL)

6) la differenza è che qui io ho la possibilità di eseguire più volte questo statement preparato con più parametri e questo mi permette di interagire in maniere molto efficiente 
perché riuso lo statement che ho inizialmente compilato

7) executeQuery() esegue l'SQL statement nell'oggetto PreparedStatement corrente, il quale deve essere un 
SQL Data Manipulation Language (DML) statement, come per esempio INSERT, UPDATE or DELETE; oppure un SQL statement 
che non ritorna nulla, come un DDL statement.
*/