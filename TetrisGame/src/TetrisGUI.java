
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.input.*;
import javafx.stage.*;
import javafx.animation.*;
import javafx.util.*;
import java.util.*;



public class TetrisGUI extends Application{
    public static TextField user;
    private Label userCap;
    private Label score, lines, level, scoreCap, linesCap, levelCap;
    private Button playButton, resetButton;
    private GridPane gridPlayingField;
    private VBox leftPane;
    private VBox rightPane;
    public static final int HEIGHT = 22; //righe del campo di gioco
    public static final int WIDTH = 10; //colonne del campo di gioco
    private final int PIXEL = 22; //dimensioni di ogni cella
    private HBox root;
    private List<Point> fillPoints; //lista di punti occupati sul campo di gioco da tetramini già posizionati e da quello corrente
    public static GameManager currentGame; //si occupa di gestire tutto quello che riguarda il comportamento della partita corrente
    private int shapeSpeed;
    private SequentialTransition shapeTransition;
    private boolean gameOver; //per sapere se il giocatore ha perso
    private boolean running; //se la partita è in corso o è in pausa (serve per mettere in pausa o no il gioco)
    private boolean reset; //serve per resettare la partita in corso
    private Ranking ranking;
    private ConfigParameters parameters = new ConfigParameters();
    private EventGUI event;

    
    public void start(Stage stage){
        //creo evento per l'avvio dell'applicazine
        event = new EventGUI("START", parameters);

        //crea left pane
        leftPane = makeLeftPane();

        //crea la griglia del campo di gioco
        gridPlayingField = makePlayingField();
        
        //crea right pane
        rightPane = makeRightPane();

        //creo evento per il bottone "Play" e per "Reset"
        playButton.setOnAction((ActionEvent av) -> {startNewGame();});
        resetButton.setOnAction((ActionEvent av) -> {resetGame();});

        //layout complessivo della scena
        root = new HBox();
        root.getChildren().addAll(leftPane, gridPlayingField, rightPane);

        //imposto i parametri di stile recuperati dal file di configurazione
        setParametersSettings();

        //recupero dati salvati in cache
        if(TetrisCache.loadCache() && currentGame != null){ //recupero i dati e controllo se ho trovato dei dati salvati in cache
            shapeSpeed = currentGame.getFallTime(); //reimposto la velocità a cui viaggiava il tetramino prima della chiusura dell'applicazione
            paint(); //disegno il campo di gioco che era stato salvato in cache
        } else
            //caso in cui loadCache() ha lanciato un'eccezione
            paintPlayingField(); //carico campo da gioco di default

        Scene scene = new Scene(root); 
        scene.getStylesheets().add("file:myfiles/layoutstyles.css");
        scene.setOnKeyPressed((KeyEvent ke) -> {keyBehavior(ke);}); //metto a scene l'evento di riconoscimento dei tasti che preme il giocatore così
                                                                   //do il focus al layout e in questo modo riesco ad intercettare tutti i tasti che vengono digitati
        stage.setOnCloseRequest((WindowEvent we) -> {TetrisCache.saveCache(); event = new EventGUI("CLOSE", parameters);});
        stage.setTitle("Tetris Game");
        stage.setScene(scene);
        stage.show();
    }




//--------------------------FUNZIONI PER EVENTI
    private void startNewGame(){
        //creo l'evento per l'inizio della partita
        event = new EventGUI("PLAY", parameters);

        if(currentGame == null) //creo un nuovo gestore della partita se non esiste già
            currentGame = new GameManager();

        //imposto valori default all'inizio della partita
        running = true;
        reset = false;

        /*bisogna ridare focus all'oggetto HBox "root" 
        altrimenti resta il focus sul bottone "Play"
        e non prende tutti i tasti che si digitano per giocare*/
        root.requestFocus();
        
        /*creaimo la transizione che fa spostare il tetramino 
        verso il basso ogni tot di millisecondi*/
        makeTransition();
        paint(); //aggiorno la grafica del campo di gioco
    }

    private void resetGame(){
        reset = true;
        paint();
    }

    private void paintPlayingField(){
        //resetto il campo di gioco altrimenti durante una partita 
        //si ha un eccesso di  oggetti nella griglia
        gridPlayingField.getChildren().clear(); 
    
        //disegno il tetramino corrente nel campo di gioco insieme ai punti già occupati
        //e rappresento con colorazione di default quelli vuoti
        for(int i=0; i<HEIGHT; i++){
            for(int j=0; j<WIDTH; j++){
                //scambio i e j perché la j->x e i->y
                //e ogni punto è registrato come (x, y)
                Point currentPoint = new Point(j, i); //punto del campo di gioco da analizzare
                Rectangle rect = new Rectangle(PIXEL, PIXEL); 
                //fillPoints è null nel momento in cui non abbiamo ancora creato
                //un oggetto currentGame
                if(fillPoints != null && fillPoints.contains(currentPoint)){ //devo controllare se il punto (j;i) appartiene ad un tetramino (o quello corrente o quelli già depositati)
                    int index = fillPoints.indexOf(currentPoint); //nell'array di punti occupati seleziono l'indice dell'elemento in cui si trova il punto che mi serve 
                    rect.setFill(fillPoints.get(index).getColor()); //recupero il colore del punto occupato e lo imposto come colore della cella
                } 
                else
                    rect.setFill(Color.web(parameters.playingFieldColor)); //setto colore cella di default
                
                gridPlayingField.add(rect, j, i); //add() in questo caso vuole prima l'indice di colonna e poi quello di riga
            }
        }
    }

    private void paint(){
        //controllo se il giocatore ha perso
        checkGameOver();
        
        //aggiorno le statistiche della partita
        score.setText(Integer.toString(currentGame.getScore()));
        level.setText(Integer.toString(currentGame.getLevel()));
        lines.setText(Integer.toString(currentGame.getLines()));

        paintPlayingField(); //aggiorno campo di gioco

        //in caso di sconfitta dopo aver ridisegnato il campo 
        //di gioco di default reimposto i dati di default
        if(gameOver){
            gameOver = false; //reimposto gameOver nel caso in cui si voglia fare un'altra partita
            //setto dati di default da salvare in cache se si chiude l'applicazione al termine di una partita
            currentGame = null;
            user.setText(""); 
        }
    }

    private void checkGameOver(){
        if(currentGame.getGameOver() || reset){
            event = new EventGUI("END", parameters); //creo evento partita persa
            gameOver = true;
            //stoppo la transizione che fa scendere il tetramino corrente
            if(running) //se il gioco è già fermo non c'è bisogno di stopparla
                shapeTransition.stop();
            running = false;
            //svuoto l'array che memorizzava i punti occupati sul campo di gioco
            fillPoints.clear();
            //aggiorno la classifica con i dati aggiornati
            updateRanking();
        } else {
            //recupero i punti occupati dal tetramino corrente 
            //e da quelli già posizionati in precedenza
            fillPoints = currentGame.getFillPoints();
                
            //aggiorno la velocità con cui scende il tetramino
            if(shapeSpeed != currentGame.getFallTime()){
                shapeTransition.stop();
                makeTransition();
            }
        }
    }

    private void makeTransition(){
        shapeSpeed = currentGame.getFallTime();
        //creo una transazione che si attiva dopo un certo numero di millisecondi
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(shapeSpeed));
        pauseTransition.setOnFinished((ActionEvent ae) ->{
            currentGame.moveTetraminoDown(); //spostiamo in basso il tetramino corrente allo scadere della PauseTransition
            paint(); //aggiorno grafica del campo di gioco con la nuova posizione del tetramino
        });

        //inserisco tutto in una SequentialTransition siccome non devo effettuare transizioni parallele
        shapeTransition = new SequentialTransition(pauseTransition);
        shapeTransition.setCycleCount(Timeline.INDEFINITE); //la transizione si ripete sempre
        shapeTransition.play();
    }

    private void updateRanking(){
        //recupero i dati aggiornati dalla partita appena conclusa
        String username = user.getText();
        int playerScore = Integer.parseInt(score.getText());
        
        //aggiorno la classifica
        ranking.update(username, playerScore);
    }

    private void keyBehavior(KeyEvent ke){
        //siccome KeyCode è un enumeratore con lo switch si può evitare
        //di usare il nome completamente qualificato cioè KeyCode.LEFT
        //ed usare direttamente il valore, ricordiamo anche che i tipi Enum
        //hanno di default toString() ridefinito
        switch(ke.getCode()){ //getCode() fornisce l'enumeratore KeyCode.VALUE dove VALUE indica il tasto che è stato digitato
            case LEFT: 
                event = new EventGUI("LEFT", parameters); //creo evento pressione tasto sinistro
                if(running)
                    currentGame.moveTetraminoLeft();
                paint();
                break;
            case RIGHT:
                event = new EventGUI("RIGHT", parameters); //creo evento pressione tasto destro
                if(running) 
                    currentGame.moveTetraminoRight(); 
                paint();
                break;
            case UP: //il tetramino ruota in senso orario
                event = new EventGUI("UP", parameters); //creo evento pressione tasto sopra
                if(running)
                    currentGame.rotateTetramino();
                paint();
                break;
            case DOWN: 
                event = new EventGUI("DOWN", parameters); //creo evento pressione tasto giù
                if(running)
                    currentGame.moveTetraminoDown();
                paint();
                break;
            case SPACE:
                event = new EventGUI("SPACEBAR", parameters); //creo evento pressione tasto spazio
                if(running){
                    while(!currentGame.getGameOver() && !currentGame.getStopHardDrop())
                        currentGame.moveTetraminoDown();
                    currentGame.resetStopHardDrop();
                }
                paint();
                break;
            case ESCAPE:
                event = new EventGUI("ESC", parameters); //creo evento pressione tasto esc
                if(running){
                    running = false;
                    shapeTransition.pause();
                } else {
                    running = true;
                    shapeTransition.play();
                }
                paint();
                break;
        }
    }

    

//--------------------------FUNZIONI GUI
    private VBox makeLeftPane(){
        VBox vbLeft = new VBox();
        vbLeft.setPadding(new Insets(40, 40, 0, 40));

        //creo la scritta Username e il campo di testo corrispondente
        VBox vbUser = new VBox();
        vbUser.getStyleClass().add("user");

        userCap = new Label("Username");
        user = new TextField();
        user.setMaxWidth(150);

        vbUser.getChildren().addAll(userCap, user);
        
        //creo i bottoni play e reset
        HBox hbButton = new HBox(); //i bottoni devono stare uno affianco all'altro
        hbButton.setPadding(new Insets(10, 10, 10, 10));
        hbButton.setSpacing(10);
        hbButton.setStyle("-fx-alignment: center");
        playButton = new Button("Play");
        resetButton = new Button("Reset");
        playButton.setId("button");
        resetButton.setId("button");
        playButton.getStyleClass().add("playButton");
        resetButton.getStyleClass().add("resetButton");

        hbButton.getChildren().add(playButton);
        hbButton.getChildren().add(resetButton);

        //creo la classifica
        VBox vbTable = new VBox();
        vbTable.setPadding(new Insets(50, 10, 10, 10));

        ranking = new Ranking();

        vbTable.getChildren().add(ranking);

        //aggiungo ogni componente al pannello di sinistra
        vbLeft.getChildren().addAll(vbUser, hbButton, vbTable);

        return vbLeft;
    }

    private GridPane makePlayingField(){
        GridPane gp = new GridPane();

        //imposto esplicitamente la grandezza di ogni riga
        for(int i=0; i<HEIGHT; i++)
            gp.getRowConstraints().add(new RowConstraints(PIXEL));

        //imposto esplicitamente la grandezza di ogni colonna
        for(int i=0; i<WIDTH; i++)
            gp.getColumnConstraints().add(new ColumnConstraints(PIXEL));
        
        gp.setPadding(new Insets(45, 10, 20, 10));
        gp.setHgap(0.5); //più piccolo è il valore più i quadrati escono attaccati l'uno all'altro
        gp.setVgap(0.5);
        gp.setStyle("-fx-alignment: center;");

        return gp;
    }

    private VBox makeRightPane(){
        VBox vbRight = new VBox();
        vbRight.setPadding(new Insets(40, 40, 40, 40));
        vbRight.setSpacing(15);
        vbRight.setStyle("-fx-text-fill: red");

        VBox vbScore = new VBox();
        vbScore.getStyleClass().add("right_pannel");

        //se ho delle informazioni salvate in cache devo caricare quelle
        //e non il valore di default
        scoreCap = new Label("Score");
        score = new Label("0");
        vbScore.getChildren().addAll(scoreCap, score);

        VBox vbLevel = new VBox();
        vbLevel.getStyleClass().add("right_pannel");

        levelCap = new Label("Level");
        level = new Label("1");
        vbLevel.getChildren().addAll(levelCap, level);

        VBox vbLines = new VBox();
        vbLines.getStyleClass().add("right_pannel");

        linesCap = new Label("Lines");
        lines = new Label("0");
        vbLines.getChildren().addAll(linesCap, lines);
        
        vbRight.getChildren().addAll(vbScore, vbLevel, vbLines);
        return vbRight;
    }

    private void setParametersSettings(){
        root.setStyle("-fx-font-family: " + parameters.textFont + "; -fx-background-color: " + parameters.windowColor); //aggiunto colore background
        
        userCap.setStyle("-fx-font-weight: bold; -fx-font-size: " + parameters.textSize + "; -fx-text-fill: " + parameters.textColor);
        
        scoreCap.setStyle("-fx-font-weight: bold; -fx-font-size: " + parameters.textSize + "; -fx-text-fill: " + parameters.textColor);
        score.setStyle("-fx-font-size: " + parameters.textSize + "; -fx-text-fill: white");
        
        levelCap.setStyle("-fx-font-weight: bold; -fx-font-size: " + parameters.textSize + "; -fx-text-fill: " + parameters.textColor);
        level.setStyle("-fx-font-size: " + parameters.textSize + "; -fx-text-fill: white");
        
        linesCap.setStyle("-fx-font-weight: bold; -fx-font-size: " + parameters.textSize + "; -fx-text-fill: " + parameters.textColor);
        lines.setStyle("-fx-font-size: " + parameters.textSize + "; -fx-text-fill: white");
        
        user.setStyle("-fx-control-inner-background: " + parameters.playingFieldColor);
    }
}

