import ea.Bild;
import ea.Knoten;

/**
 * Klasse für aktive Spieler
 * Struktur aus dem Beispiel demo-netzwerkgame-master
 */
public class Player extends Knoten {

    Bild pic1;
    Bild pic2;
    private int PicCount = 2;

    private Bild[] pics = new Bild[PicCount];

    private int step;
    private float posX;
    private float posY;

    private int walkspeed = 3; // Laufgeschwindigkeit


    public Player(float posX,float posY){

        super();
        this.posX=posX;
        this.posY=posY;

        pics[0] = new Bild("./Assets/Player.png");
        pics[1] = new Bild("./Assets/Player2.png");
        pics[0].positionSetzen(posX,posY);
        pics[1].positionSetzen(posX,posY);
        step = 0;

        pics[1].sichtbarSetzen(false);


        this.add(pics[0]);
        this.add(pics[1]);
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
    public void WalkLeft(){

        hideAllImages();
        pics[step].sichtbarSetzen(true);
        System.out.println(lastX +"--" +posX);


        if((java.lang.Math.abs(posX-lastX))>=20){
            System.out.println("WalkStep");
            step++;
            if(step>=PicCount){
                step=0;
            }
            lastX = posX;
        }
        verschieben(walkspeed,0);




    }
    public void hideAllImages(){
        for(int i=0;i<PicCount;i++){
            pics[i].sichtbarSetzen(false);
        }
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
