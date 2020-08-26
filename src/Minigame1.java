import ea.Bild;
import ea.Knoten;

public class Minigame1 extends Knoten {

    private boolean activ = false;
    private int score =0;


    private Bild BackgroundPic;
    private int FallingCount = 10;
    private Bild[] FallingObjects = new Bild[FallingCount];


    public Minigame1(){
        BackgroundPic = new Bild(0,0,"./Assets/StartingScreen/Forest.jpg");
        this.add(BackgroundPic);

        InitFallingbjects();
    }

    public void InitFallingbjects(){
        for(int i= 0;i<FallingCount;i++){
            FallingObjects[i] = new Bild(0,0,"./Assets/MouseC.png");
            FallingObjects[i].sichtbarSetzen(false);
            this.add(FallingObjects[i]);
        }
    }

    public void startGame(){
        if(!activ){
            activ = true;
        }
    }

    public boolean isActiv() {
        return activ;
    }
}
