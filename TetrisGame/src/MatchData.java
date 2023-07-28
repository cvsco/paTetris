
import java.io.*;

public class MatchData implements Serializable {
    private String username;
    private GameManager gameData;

    public MatchData(){ //1
        gameData = TetrisGUI.currentGame;
        username = TetrisGUI.user.getText();
    }    

    public String getUsername(){
        return username;
    }

    public GameManager getGameData(){
        return gameData;
    }
}

/*
1) sfrutto il fatto che ho reso static sia currentGame che il FiledText user
nella classe TetrisGUI per raccogliere i dati che mi serve salvare su cache 
*/
