import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.*;

public class Ranking extends TableView<Player> {
    private ObservableList<Player> rankObservable;
    private TetrisArchive archive;

    public Ranking(){
        //1
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //1.1, 2
        setMaxHeight(300); //2
        TableColumn usernameCol = new TableColumn("USERNAME");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username")); //1.2
        TableColumn scoreCol = new TableColumn("SCORE");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score")); //1.2
        usernameCol.getStyleClass().add("column");
        scoreCol.getStyleClass().add("column");
        getColumns().addAll(usernameCol, scoreCol); //1.3, 2

        rankObservable = FXCollections.observableArrayList(); //creo l'ObservableList
        archive = new TetrisArchive(new ConfigParameters()); //3
        //carico nella lista osservabile i giocatori presenti nel database
        rankObservable.addAll(archive.loadPlayers());
        
        //collego TableView con la corrispondente ObservableList preparata
        setItems(rankObservable); //2
    }

    public void update(String user, int score){
        archive.checkPlayer(user, score); //4
        
        //aggiorno la classifica con gli utlimi dati aggiornati
        rankObservable.clear();
        rankObservable.addAll(archive.loadPlayers());
    }
}

/*

1) all'inizio inserisco tutto quello che serve per stilizzare graficamente la TableView 
e indico da quali colonne deve essere formata attaverso gli oggetti della classe TableColumn
e i metodi setCellValueFactory().

1.1) imposta il valore della proprietà columnResizePolicy. Le due policies più comuni
sono disponibili come funzioni statiche nella classe TableView: 
UNCONSTRAINED_RESIZE_POLICY and CONSTRAINED_RESIZE_POLICY

CONSTRAINED_RESIZE_POLICY:
E' una policy semplice che assicura che la somma della larghezza di tutte le colonne visibili 
nella tabella corrente sia uguale all'intera larghezza della tabella stessa.
Quando un utente ridimensiona la larghezza di una colonna di una tabella che ha questa 
policy, la tabella automaticamente aggiusta la larghezza delle colonne di destra.
Quando l'user incrementa la larghezza di una colonna, la tabella decrementa la larghezza
della colonna più a destra fino a quando non raggiunge la sua larghezza minima. Poi 
diminuisce la larghezza della seconda colonna più a destra fino a quando non raggiunge
la sua larghezza minima e così via. Quando tutte le colonne di destra hanno raggiunto
la dimenzione minima, l'utente non può più incrementare la dimensione della colonna ridimensionata.

1.2) quando creiamo un PropertyValueFactory<>("") tra le virgolette dobbiamo scrivere il nome della
proprietà come l'abbiamo scritta nella classe Bean (Player.java) che poi per coerenza è lo stesso
nome che abbiamo dato alla proprietà nel DBMS 

1.3) sto collegando alla TableView implicita le colonne appena create

2) non ci serve il this per chiamare funzioni come getColumns(), setItems(), setColumnResizePolicy()
o setMaxHeight() perché è implicito che stando nel costruttore queste funzioni facciano riferimento 
all'oggetto implicito che stiamo costruendo

3) creo l'archivio di giocatori seguendo le indicazioni fornite dai parametri di configurazione

4) controllo se il giocatore è già registrato oppure no e aggiorno il punteggio se era già
registrato, altrimenti inserisco il nuovo giocatore nel database
*/
