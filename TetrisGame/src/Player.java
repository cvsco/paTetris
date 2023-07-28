import javafx.beans.property.*;

public class Player{
    //1
    private final SimpleStringProperty username;
    private final SimpleIntegerProperty score;

    //2
    public Player(String u, int s){
        username = new SimpleStringProperty(u);
        score = new SimpleIntegerProperty(s);
    }

    //3
    public String getUsername(){
        return username.get();
    }

    public int getScore(){
        return score.get();
    }
}

/*
1) ho un attributo per ogni colonna della tabella e sono final perché queste 
quando si istanziano trasportano il dato che contengono e poi si distruggono.
Gli attributi devono essere Simple...Property proprio per far sapere a java
che questa è una classe Bean

2) Il costruttore posso farlo private tanto lo uso solo qui dentro e lo userà
java e io una volta

3) poi devo avere dei metodi di accesso che si chiamano metodi get e set che sono
dei metodi per accedere al bean perché in questo modo java sa automaticamente che 
invocando questi metodi va a leggere quella proprietà specifica
*/