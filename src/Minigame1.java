import ea.Bild;
import ea.Knoten;

public class Minigame1 extends Knoten {

    private boolean activ = false;

    private int fallingObjectCount = 10;
    private int score =0;
    private int counter =0;


    private Bild backgroundImgObject;
    private Bild[] FallingObjects = new Bild[fallingObjectCount];
    private String backgroundImgPath = "./Assets/StartingScreen/Forest.jpg";

    private int offsetX;//letzendlich PlayerposX
    private int offsetY;//letzendlich PlayerposY





    public void tick(){
        if(activ){
            int x = offsetX + (int)(Math.random()* MAIN.x);
            //FallingObjects[counter].

            counter++;
        }

    }

    public Minigame1(){
        try{
            backgroundImgObject = new Bild(0,0,backgroundImgPath);
        }
        catch  (Exception e){
            System.out.println("Fehler in der Mnigame Klasse: Bild bei " + backgroundImgPath + " kann nicht gefunden werden!");
            System.out.println(e);
        }
        this.add(backgroundImgObject);

        InitFallingbjects();
    }

    public void InitFallingbjects(){
        for(int i= 0;i<fallingObjectCount;i++){

            FallingObjects[i] = new Bild(0,0,"./Assets/MouseC.png");
            FallingObjects[i].sichtbarSetzen(false);
            FallingObjects[i].newtonschMachen();
            this.add(FallingObjects[i]);
        }
    }
    public void centerBackground() {
        int imgPosX = offsetX + MAIN.x - ((int)backgroundImgObject.getBreite()/2);
        int imgPosY = offsetY + MAIN.y - ((int)backgroundImgObject.getHoehe()/2);
        backgroundImgObject.positionSetzen(imgPosX,imgPosY);
    }

    public void startGame(int x, int y){
        setOffset(x,y);
        centerBackground();
        if(!activ){
            activ = true;
        }
    }
    public void setOffset(int x, int y){
        offsetX = x;
        offsetY = y;
    }


    public boolean isActiv() {
        return activ;
    }
}
