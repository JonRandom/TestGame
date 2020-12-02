import ea.Bild;
import ea.Knoten;

public class FadeScreen extends Knoten {


    //private DialogController4 dialogController;
    private Bild blackImg;
    private Bild whiteImg;

    public boolean whiteActive = false;
    public boolean blackActive = false;

    private int counter = 0;
    private final float length = 120; //in game ticks

    public FadeScreen (){
        System.out.println("FadeScreen: Neuer FadeScreen");
        //dialogController = dc;
        blackImg = new Bild(0,0, MAIN.fadeBlackPlate);
        blackImg.sichtbarSetzen(false);
        whiteImg = new Bild(0,0, MAIN.fadeBlackPlate); //noch vorläufig
        whiteImg.sichtbarSetzen(false);
        this.add(blackImg, whiteImg);
    }

    public void tick(){
        //System.out.println("FadeScreen: blackActive = " + blackActive);
        if(blackActive){
            float op = blackImg.getOpacity();
            System.out.println("Alte OP = " + op);
            if(counter <= length/2){
                float addOp = (2/length);
                float newOp = Math.min(1.0f, op + addOp);
                blackImg.setOpacity(newOp); //darker
                System.out.println("Neue OP = " + blackImg.getOpacity());
            } else if(counter > length/2){
                float addOp = (2/length);
                float newOp = Math.min(1.0f, op - addOp);
                blackImg.setOpacity(newOp); //lighter
                System.out.println("Neue OP = " + blackImg.getOpacity());
            }
            counter ++;
            if(counter >= length ){
                blackImg.setOpacity(0f);
                counter = 0;
                blackActive = false;
            }
        }

    }

    public void startBlackFade(){
        System.out.println("FadeScreen: Schwarzer Fade gestartet");
        blackImg.sichtbarSetzen(true);
        if(whiteActive){
            System.out.println("FadeScreen: FEHLER WEIß SCHON AN");
        } else{
            blackActive = true;
        }
        blackImg.setOpacity(0f);
        counter = 0;
    }

    public void startWhiteFade(){
        if(blackActive){
            System.out.println("FadeScreen: FEHLER SCHWARZ SCHON AN");
        } else{
            whiteActive = true;
        }
        blackImg.setOpacity(0f);
        counter = 0;
    }
}
