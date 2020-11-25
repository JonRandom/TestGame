import ea.Bild;
import ea.Knoten;
import ea.Punkt;

/**
 * Klasse für aktive Spieler
 * Struktur aus dem Beispiel demo-netzwerkgame-master
 */
public class Player extends Knoten {

    private GameSaver gamesaver;

    private String name;
    private float posX;
    private float posY;
    private int money;

    private int walkspeed = 3; // Laufgeschwindigkeit


    private ImageCollection2 IC2;

    public Player(float posX,float posY, GameSaver gs){
        this.gamesaver = gs;
        this.posX=posX;
        this.posY=posY;
        this.money = 0;

        IC2= new ImageCollection2(this.posX,this.posY,"./Assets/SpielerTest/BasicMale");
        IC2.Init();
        this.add(IC2);

    }

    @Override
    public void verschieben(float dX, float dY) {
        super.verschieben(dX, dY);
        posX= posX+dX;
        posY= posY+dY;

    }

    @Override
    public void positionSetzen(float x, float y) {
        posX = x;
        posY = y;
        super.positionSetzen(x, y);
    }
    @Override
    public void positionSetzen(Punkt p) {
        super.positionSetzen(p);
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
        IC2.walkLeft(walkspeed);
        this.posX = posX -walkspeed;
        //IC.verschieben(-walkspeed,0);
    }

    public void WalkRight(){
        IC2.walkRight(walkspeed);
        this.posX = posX +walkspeed;
        //IC.verschieben(walkspeed,0);
    }
    public void WalkBottom(){
        IC2.walkBottom(walkspeed);
        this.posY = posY +walkspeed;
        //IC.verschieben(0, walkspeed);
    }
    public void WalkTop(){
        IC2.walkTop(walkspeed);
        this.posY = posY -walkspeed;
        //IC.verschieben(0, -walkspeed);
    }

    public int getMoney(){
        return money;
    }
    public void addMoney(int x){
        money += x;
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
    public String getName() {
        return name;
    }

    public float getPosX() {
        return posX;
    }
    public float getPosY() {
        return posY;
    }
}

