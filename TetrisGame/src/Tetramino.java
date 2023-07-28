
import java.util.*;
import java.io.*;


class Tetramino implements Serializable{
      //caselle da cui i pezzi effettuano lo spawn (contate da sisnistra):
      //L:6 I:4 T:5 S:5 Z:4 O:5 J:4
     private int type; // I:1, J:2, L:3, O:4, S:5, T:6, Z:7
     private int rotation; //0, 1, 2, 3
     private List<Point> points;

     public Tetramino(int t){
        type = t;
        rotation = 0;
        points = new ArrayList<Point>(); //punti da cui è composto il tetramino

        createTetramino(); //creiamo i punti da cui è composto a seconda del tipo
     }

     public Tetramino(Tetramino tr){
         type = tr.type;
         rotation = tr.rotation;
         //non posso fare points = new ArrayList<Point>(tr.points) e ne points.addAll(tr.points)
         //altrimenti copyRotated.points punterebbe allo stesso array a cui punta tr.points 
         //e quindi se modifico uno modifico anche l'altro. 
         //Invece a me serve solo un nuovo array con nuovi oggetti ma uguali valori
         points = new ArrayList<Point>();
         
         for(Point p: tr.points) //copio i valori dei punti di tr.points nel nuovo oggetto tetramino
            points.add(new Point(p.getX(), p.getY(), type));
     }

     /*ho utilizzato il seguente sito per stabilire in quali punti
     spawna ogni tipo di tetramino: https://harddrop.com/wiki/SRS*/
     private void createTetramino(){
         points.add(new Point(4, 1, type)); //tutti

         if(type != 5) //tutti tranne S
            points.add(new Point(5, 1, type));
      
         if(type == 1) //solo per I
            points.add(new Point(6, 1, type));
      
         if(type != 4 && type != 7) //tutti tranne O e Z
            points.add(new Point(3, 1, type));

         if(type == 2 || type == 7) //J o Z
            points.add(new Point(3, 0, type));

         if(type == 3 || type == 4 || type == 5) //L, O, S
            points.add(new Point(5, 0, type));

         if(type != 1 && type != 2 && type != 3) //O, S, T, Z
            points.add(new Point(4, 0, type));
     }

//--------------------------FUNZIONI: get...()
     public List<Point> getPoints(){ //serve per recuperare i punti da cui è composto il tetramino
         return points;
     }

     public int getType(){ //recupera il tipo del tetramino
         return type;
     }

     public List<Point> getRotatedPoints(){
         //faccio una copia del tetramino corrente
         Tetramino copyRotated = new Tetramino(this);
         
         //ruoto la copia del tetramino appena creata
         copyRotated.rotate();

         return copyRotated.points;
     }

//--------------------------FUNZIONI: move...()
    public void moveLeft(){
         for(Point p: points)
            p.updateX(-1);
     }

    public void moveRight(){
         for(Point p: points)
            p.updateX(1);
     }

    public void moveDown(){
         for(Point p: points)
            p.updateY(1);
     }

//--------------------------FUNZIONE PER ROTAZIONE
     public void rotate(){ 
         if(type != 4){ //O non ruota
            int lowX = 50;
            int lowY = 50;

            //prendo la X più piccola e la Y più piccola tra tutti i punti che compongono il tetramino
            //in maniera tale da recuperare il punto del tetramino più in alto a sinistra
            for(Point p: points){ 
               if(p.getX() < lowX)
                  lowX = p.getX();
               if(p.getY() < lowY)
                  lowY = p.getY();
            }

            //1) cerco le coordinate esatte del centro del tetramino (dipendono dal tipo del tetramino e dalla posizione in cui si trova)
            //faccio le seguenti somme perché ho verificato che in questo modo trovo i valori esatti del centro 
            //per ogni tettramino a seconda della posizione in cui si trovano
            if(type == 1){ //se il tetramino è I
               if(rotation == 0 || rotation == 2)
                  lowX++;
               else 
                  lowY++;
            }

            if(type >= 2){ //se il tetramino è J, L, S, T o Z
               if(rotation == 0 || rotation == 3){ 
                  lowX++; 
                  lowY++;
               } else {
                  if(rotation == 1)
                     lowY++;
                  else 
                     lowX++;
               }
            }

            for(Point p: points){
               int tmpX = p.getX();
               int tmpY = p.getY();
               //2) traslo i punti del tetramino nel centro degli assi
               tmpX -= lowX;
               tmpY -= lowY;
               //3) compenso il fatto che la visione delle coordinate in java è rivolta verso il basso
               tmpY *= -1;
               //4) applico formule per una rotazione oraria (matrici di rotazione) cos(pi/2)=0 sen(pi/2)=1
               int newX = tmpY; //Orario: x*cos(pi/2)+y*sen(pi/2)
               int newY = -tmpX; //Orario: -x*sen(pi/2)+y*cos(pi/2)
               //5) ripeto il passo 3
               newY *= -1;
               //6) ripeto passo 2 al contrario
               newX += lowX;
               newY += lowY;

               //devo compensare il fatto che faccio ruotare il tetramino I di un punto verso sinistra invece che in corrispondenza della griglia
               //quindi nei casi in cui è orizzontale, dopo la rotazione, deve essere spostato di uno a destra
               if(type == 1 && (rotation == 0 || rotation == 2))
                  newX++;

               p.setPosition(newX, newY);
            }
         }

         rotation = (rotation+1) % 4;
     }

     
     public String toString(){  
         String str = "";
         System.out.println("type: " + type);
         for(Point i: points)
            str += "X: " + i.getX() + " Y: " + i.getY() + "\n";
         return str;
     } 
}

/*
FONTI: https://stackoverflow.com/questions/233850/tetris-piece-rotation-algorithm
       https://harddrop.com/wiki/SRS
Here are the steps I used to solve the rotation problem in Java.

   1) For each shape, determine where its origin will be. 
   I used the points on the diagram from this page to assign my origin points. 
   Keep in mind that, depending on your implementation, 
   you may have to modify the origin every time the piece is moved by the user.

   2) Rotation assumes the origin is located at point (0,0), 
   so you will have to translate each block before it can be rotated. For example, 
   suppose your origin is currently at point (4, 5). This means that before the shape can be rotated, 
   each block must be translated -4 in the x-coordinate and -5 in the y-coordinate to be relative to (0,0).

   3) In Java, a typical coordinate plane starts with point (0,0) 
   in the upper left most corner and then increases to the right and down. 
   To compensate for this in my implementation, I multiplied each point by -1 before rotation.

   4) Here are the formulae I used to figure out the new x and y coordinate after a counter-clockwise rotation. 
   For more information on this, I would check out the Wikipedia page on Rotation Matrix. 
   x' and y' are the new coordinates:
   x' = x * cos(PI/2) - y * sin(PI/2) and y' = x * sin(PI/2) + y * cos(PI/2)

   5) For the last step, I just went through steps 2 and 3 in reverse order. 
   So I multiplied my results by -1 again and then translated the blocks back to their original coordinates.
*/