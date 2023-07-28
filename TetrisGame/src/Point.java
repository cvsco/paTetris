import javafx.scene.paint.*;
import java.io.*;


public class Point implements Serializable {
    private int x;
    private int y;
    private CustomColors color; //colore di ogni punto

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Point(int x, int y, int t){
        this(x, y); //richiama il primo costruttore

        switch(t){ //1
            case 1: color = new CustomColors(Color.rgb(0, 255, 255)); break; //ciano
            case 2: color = new CustomColors(Color.rgb(24, 72, 197)); break; //blu
            case 3: color = new CustomColors(Color.rgb(240, 132, 20)); break; //arancione
            case 4: color = new CustomColors(Color.rgb(233, 220, 18)); break; //giallo
            case 5: color = new CustomColors(Color.rgb(36, 228, 40)); break; //verde
            case 6: color = new CustomColors(Color.rgb(231, 88, 233)); break; //viola
            case 7: color = new CustomColors(Color.rgb(255, 0, 0)); break; //rosso
        }
    }

    public Point(int x, int y, CustomColors c){
        this(x, y);
        color = c;
    }

//--------------------------FUNZIONI: get...()
    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public CustomColors getCustomColors(){
        return color;
    }

    public Color getColor(){ //2
        return color.getCustomColor();
    }

//--------------------------FUNZIONI: update...()
    //modifico coordianata x del punto passando un numero positivo o negativo
    public void updateX(int x){
        this.x += x;
    }

    //modifico coordianata y del punto passando un numero positivo o negativo
    public void updateY(int y){
        this.y += y;
    }

//--------------------------FUNZIONI DI UTILITA'
    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj){
        //mi assicuro che obj non sia un riferimento nullo e che
        //la classe dell'oggetto puntato da obj sia la stessa dell'oggetto implicito
        if(obj == null || getClass() != obj.getClass())
            return false;
        
        Point p = (Point) obj;
        return x == p.x && y == p.y; //x ed y sono dell'oggetto implicito
    }
}


/*
1) Impostiamo il colore del punto. Al costruttore di un oggetto CustomColors posso passare un
qualsiasi oggetto Color. Per farlo posso usare ad esempio rgb() o hsb() o web() ecc...

2) serve per restituire un oggetto Color impostato secondo i dati di un oggetto CustomColor
*/