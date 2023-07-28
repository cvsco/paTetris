
import java.io.*;
import javafx.scene.paint.*;

/*1,2*/
class CustomColors implements Serializable {
    private double red;
    private double green;
    private double blue;
    private double alpha;

    public CustomColors(Color color){
    	red = color.getRed();
    	green = color.getGreen();
    	blue = color.getBlue();
    	alpha = color.getOpacity();
    }

    public Color getCustomColor(){ //3
    	return new Color(red, green, blue, alpha);
    }
}


/*
1) L'idea di creare questa classe è nata principalmente dal fatto che dovendo scrivere 
su file un'istanza della classe GameManager quest'ultima doveva essere serializzabile. Siccome in 
GameManager troviamo istanze di Tetramino e Point allora dovevano esserlo anche loro. Poi 
Point tra gli attributi aveva un riferimento a Color e visto che questa era una classe di 
sistema non potevo metterla Serialazable, allora ho trovato questa soluzione che torna anche 
molto comoda nel caso in cui l'utente voglia creare colorazioni personalizzate per i tetramini
tramite file di configurazione. 

2) Fonte dal quale ho recuperato la logica: 
Saving color as a state in JavaFX Application
https://stackoverflow.com/questions/36748358/saving-color-as-state-in-a-javafx-application 

3) ci serve la componente di rosso, verde, blue e alpha (che indica l'opacità)
siccome il costruttore della classe Color prende in ingresso questi valori e devono
essere double
*/
