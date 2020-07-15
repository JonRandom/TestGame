import ea.Bild;
import ea.Knoten;

/**
 * Klasse für aktive Spieler
 * Struktur aus dem Beispiel demo-netzwerkgame-master
 */
public class Player extends Knoten {



    private float posX;
    private float posY;

    private int walkspeed = 3; // Laufgeschwindigkeit

    private ImageCollection IC;

    public Player(float posX,float posY){

        super();
        this.posX=posX;
        this.posY=posY;

        IC = new ImageCollection(this.posX,this.posY,"./Assets/Player/Player");
        IC.Init();

        this.add(IC);

        ;

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
float lastX = posX;
    public void WalkLeft() {
        IC.walkLeft(walkspeed);
        IC.verschieben(walkspeed,0);
    }

    public void WalkRight(){
        IC.walkRight(-walkspeed);
        IC.verschieben(-walkspeed,0);
    }




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
