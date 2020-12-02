import ea.Bild;
import ea.Knoten;

public class ComputerScreen extends Knoten {

    private Bild mainOverlayImg;

    private FadeScreen fadeScreen;

    private boolean activ = false;

    public ComputerScreen(){

        //Bilder
        mainOverlayImg = new Bild(0,0, "./Assets/Computer/overlay.png");
        mainOverlayImg.sichtbarSetzen(false);
        this.add(mainOverlayImg);

        //FADESCRENN -> Ganz oben drauf
        fadeScreen = new FadeScreen();
        this.add(fadeScreen);
    }
    public void openPC(){
        this.activ = true;
        //fadeScreen.startBlackFade(); //das läuft dann durch
        showWindow();
    }

    public void closePC(){
        this.activ = false;
        hideWindow();
    }

    public void showWindow(){
        mainOverlayImg.sichtbarSetzen(true);
        //...
    }

    public void hideWindow(){
        mainOverlayImg.sichtbarSetzen(false);
        //...
    }

    public boolean isActiv() {
        return activ;
    }
}
