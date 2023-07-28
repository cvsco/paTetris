import java.util.*;
import java.util.function.*;
import java.io.*;

public class GameManager implements Serializable{
    private List<Point> stack; //lista di punti appartenenti a tetramini già posizionati 
    private Tetramino currentTetramino;
    private boolean gameOver; //per sapere quando la partita termina
    private boolean stopHardDrop; //per sapere quando stoppare il ciclo per un comando di hard drop
    private int score;
    private int lines;
    private int level;
    private int fallTime; //tempo (millisecondi) che il tetramino impiega a spostarsi verso il basso

    public GameManager(){
        ConfigParameters parameters = new ConfigParameters();
        stack = new ArrayList<>();
        gameOver = false;
        stopHardDrop = false;
        score = 0;
        lines = 0;
        level = parameters.startLevel;
        fallTime = 1000;

        createNewTetramino();
    }

//--------------------------FUNZIONI UTILITA' PER TETRAMINO
    public void createNewTetramino(){
        //Scelta del tetramino in modo casuale e sua creazione
        /*Altro metodo:
        Random rand = new Random();
        int type = rand.nextInt(7) + 1;*/
        int type = (int) (Math.random()*7 + 1); //+1 perché altrimenti produce valori tra 0 e 6
        
        //memorizzo le coordinate dei punti del tetramino appena pozionato sul campo di gioco
        if(currentTetramino != null)
            stack.addAll(currentTetramino.getPoints()); 
        
        //ne creo uno nuovo
        currentTetramino = new Tetramino(type);

        //faccio un controllo siccome se il nuovo tetramino spawna in un punto
        //già occupato da un altro tetramino non potra spawnare correttamente e 
        //quindi significa che la partita è persa
        for(Point p: currentTetramino.getPoints())
            if(stack.contains(p))
                gameOver = true;
    }

    public void moveTetraminoDown(){ //sposto di una riga in basso tutti i punti che compongono il tetramino corrente
        //se non si trova vicino al bordo o non ci sono altri blocchi sotto allora si muove in basso
        if(!nearToBottomBorder() && !hasBlocksDown()) 
            currentTetramino.moveDown();        
        else 
            if(nearToTopBorder()) //siccome non si può muovere controllo se è arrivato al bordo in alto -> termina la partita
                gameOver = true;
            else { //se arrivo qui allora ho posizionato il vecchio tetramino
                stopHardDrop = true; //serve per terminare il cilco durante un'azione hard drop
                createNewTetramino(); 
                removeLines();
            }
    }

    public void moveTetraminoLeft(){ 
        if(!nearToLeftBorder() && !hasBlocksLeft())
            currentTetramino.moveLeft();
    }

    public void moveTetraminoRight(){
        if(!nearToRightBorder() && !hasBlocksRight())
            currentTetramino.moveRight();
    }

    public void rotateTetramino(){
        if(canRotate()) //controllo se è possibile ruotare il tetramino
            currentTetramino.rotate();
    }

    private boolean canRotate(){
        //faccio una copia del tetramino corrente, lo ruoto
        //e mi salvo i punti da cui è composto 
        List<Point> rotatedPoints = currentTetramino.getRotatedPoints();

        //controllo che i punti da cui è composto non siano fuori dal campo di gioco
        //o appartengano a tetramini già posizionati
        /*il foreach si potrebbe scrivere anche nel seguente modo
        rotatedPoints.stream().forEach((p) -> {
            stack.add(new Point(p.getX(), p.getY(), p.getCustomColors()));
        });*/
        for(Point p: rotatedPoints){
            if (stack.contains(p) || p.getX() >= TetrisGUI.WIDTH || p.getX() < 0 || p.getY() >= TetrisGUI.HEIGHT || p.getY() < 0){
                //System.out.println("il tetramino non può ruotare");
                return false;
            }
        }
        return true;
    }

//--------------------------FUNZIONI: nearTo...Border()
    private boolean nearToBottomBorder(){
        //controllo se il tetramino ha toccato il bordo inferiore del campo di gioco
        for(Point p: currentTetramino.getPoints())
            if(p.getY() == TetrisGUI.HEIGHT-1)
                return true; //il tetramino ha toccato il fondo del campo di gioco 
            
        return false;
    }

    private boolean nearToTopBorder(){
        for(Point p: currentTetramino.getPoints())
            if(p.getY() == 0)
                return true; //il tetramino ha toccato il bordo superiore del campo di gioco 
        return false;
    }

    private boolean nearToLeftBorder(){
        for(Point p: currentTetramino.getPoints())
            if(p.getX() == 0)
                return true;
        return false;
    }

    private boolean nearToRightBorder(){
        for(Point p: currentTetramino.getPoints())
            if(p.getX() == TetrisGUI.WIDTH-1)
                return true;
        return false;
    }

//--------------------------FUNZIONI: hasPoint...()
    private boolean hasBlocksDown(){
        for(Point p: currentTetramino.getPoints())
            if(stack.contains(new Point(p.getX(), p.getY()+1)))
                return true;
        return false;
    }

    private boolean hasBlocksLeft(){
        for(Point p: currentTetramino.getPoints())
            if(stack.contains(new Point(p.getX()-1, p.getY())))
                return true;
        return false;
    }

    private boolean hasBlocksRight(){
        for(Point p: currentTetramino.getPoints())
            if(stack.contains(new Point(p.getX()+1, p.getY())))
                return true;
        return false;
    }

//--------------------------FUNZIONI get...()
    public Tetramino getCurrentTetramino(){
        return currentTetramino;
    }

    public boolean getGameOver(){
        return gameOver;
    } 

    public List<Point> getFillPoints() {
        List<Point> points = new ArrayList<Point>();
    
        points.addAll(stack); //prendo i punti del campo di gioco già occupati precedentemente (sarà null la prima volta) 
        points.addAll(currentTetramino.getPoints()); //aggiungo quelli appartenenti al tetramino corrente

        Set<Point> set = new HashSet<Point>();
        set.addAll(points); //elimino eventuali elementi duplicati

        //ricostituisco l'array di punti senza duplicati e lo restituisco
        points.clear();
        points.addAll(set);

        return points;
    }

    public int getFallTime(){
        return fallTime;
    }

    public boolean getStopHardDrop(){
        return stopHardDrop;
    }

    public int getScore(){
        return score;
    }

    public int getLevel(){
        return level;
    }

    public int getLines(){
        return lines;
    }

//--------------------------FUNZIONI UTILITA'
    private void removeLines(){
        //recupero tutti i punti occupati sul campo di gioco
        List<Point> allPoints = getFillPoints();

        //fullLines contiene gli indici delle righe da eliminare
        ArrayList<Integer> fullLines = new ArrayList<Integer>();
        
        for(int i = 0; i < TetrisGUI.HEIGHT; i++){
            //si devono controllare tutte le righe perché ci possono 
            //essere dei salti di riga tra una piena e l'altra

            if(fullLines.size() == 4) //al massimo ci sono 4 righe da eliminare contemporaneamente
                break;
            
            boolean full = true; //true se la riga corrente è piena

            for(int j = 0; j < TetrisGUI.WIDTH; j++){
                if(!allPoints.contains(new Point(j, i))){
                    full = false; //il punto (j,i) nel campo di gioco è ancora vuoto
                    break;
                }
            }
            //se full è ancora true allora la riga è piena e viene memorizzata in fullLines
            if(full)
                fullLines.add(i);
        }        
            
        /*Essendo l'array fullLines ordinato in modo crescente faccio un ciclo in cui elimino la riga completa
        che sta più in alto fra quelle complete (che è il primo elemento di fullLines) ed abbasso di uno 
        tutte le righe incomplete che stanno più in alto di lei. Poi elimino il primo elemento di fullLines 
        e ricomincio d'accapo fino a quando fullLines non è vuoto*/
        while(fullLines.size() > 0){
            lines += 1;
            score += fullLines.size() * 20; 
            //La combo è che si aggiungono 20 punti in più per ogni riga che viene eliminata cioè:
            //2 righe = 20*2+20 3 righe = 20*3+20*2+20 e così via
            level = (lines+10)/10; //ogni 10 linee completate aumenta il livello (il livello parte da 1)
            //algoritmo per aumentare la velocità a seconda del livello: https://harddrop.com/wiki/Tetris_Worlds
            fallTime = (int) (1000*Math.pow(0.8-(level-1)*0.007, level-1)); //*1000 perché mi serve espresso in millisecondi

            //Siccome fullLines è oridinato in modo crescente il primo elemento dell'array
            //è la riga più in alto nella griglia fra quelle complete
            int rowToDelete = fullLines.get(0);

            //elimino da stack i punti delle righe piene cioè 
            //tutti gli elementi che soddisfano il predicato p.getY()==rowToDelete
            Predicate<Point> pointsPredicate = p -> p.getY() == rowToDelete;
            stack.removeIf(pointsPredicate);
            /*Predicate<Point> pointsPredicate = (Point p) -> {
                return p.getY() == rowToDelete; //corpo della funzione test della functional interface predicate
            };*/

            for(int j = 0; j < stack.size(); j++)
                if(stack.get(j).getY() < rowToDelete)
                    stack.get(j).updateY(1); //abbasso di una riga i punti delle righe superiori rispetto a quella che ho eliminato

            /*Elimino il primo elemento di fullLines perché ormai quella riga l'abbiamo eliminata
            e in questo modo l'array slitta verso sinistra e la sua dimensione diminuisce di 1
            e passiamo ad eliminare la prossima riga*/
            fullLines.remove(0);
        }
    }

    public void resetStopHardDrop(){
        //resetto l'indice stopHardDrop rimettendolo a false
        //dopo la conclusione del ciclo per un'azione hard drop
        if(stopHardDrop)
            stopHardDrop = false;
    }    

    public String toString(){ 
        String str = "";
      
        for(Point i: stack)
            str += "X: " + i.getX() + " Y: " + i.getY() + "\n";
        return str;
    }  
}
