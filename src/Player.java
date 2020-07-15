import ea.Bild;
import ea.Knoten;

/**
 * Klasse für aktive Spieler
 * Struktur aus dem Beispiel demo-netzwerkgame-master
 */
public class Player extends Knoten {

    Bild pic;
    private float posX;
    private float posY;

    private int walkspeed = 3; // Laufgeschwindigkeit


    public Player(float posX,float posY){
        super();
        this.posX=posX;
        this.posY=posY;

        pic = new Bild("./Assets/Player.png");
        pic.positionSetzen(posX,posY);
        System.out.println(pic.getHoehe());
        this.add(pic);
    }

    @Override
    public void verschieben(float dX, float dY) {
        super.verschieben(dX, dY);
        posX= posX+dX;
        posY= posY+dY;

    }
/*
    @Override

    public void positionSetzen(float x, float y) {
        super.positionSetzen(x, y);
        posX= x;
        posY= y;
    }

     */





    public float getCenterX(){
        return (posX+this.getBreite()/2);
    }
    public float getCenterY(){
        return (posY+this.getHoehe()/2);
    }
    public int getWalkspeed(){
        return walkspeed;
    }
}
