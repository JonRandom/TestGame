import ea.Bild;
import ea.Knoten;

public class StartingScreen extends Knoten {

    private Bild BackgroundPic;
    private Bild ButtonLeft;
    private Bild ButtonRight;

    private boolean LeftActive = false;
    private boolean RightActive = false;

    private boolean LeftSel = false;
    private boolean RightSel = false;

    private boolean active = false;//Ob der StartingScreen gerade offen ist, oder nicht.

    public StartingScreen(){
        BackgroundPic = new Bild(0,0,"./Assets/StartingScreen/Forest.jpg");
        ButtonLeft = new Bild(200,400,"./Assets/StartingScreen/Button.png");
        ButtonRight = new Bild(400,400,"./Assets/StartingScreen/Button.png");

        this.add(BackgroundPic,ButtonLeft,ButtonRight);


    }

    /**
     * Setzt den Linken Knopf "Aktiv"
     * und rechten nicht mehr "Aktiv"
     */
    public void LeftActive(){
        LeftActive = true;
        RightActive = false;

        ButtonLeft.setOpacity(0.5f);//ausgewählt
        ButtonRight.setOpacity(1f);
    }
    public void RightActive(){
        RightActive = true;
        LeftActive = false;
        ButtonRight.setOpacity(0.5f);//ausgewählt
        ButtonLeft.setOpacity(1f);
    }
    public void select() {
        if(LeftActive){
            Leftactive();
        }

    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;

        BackgroundPic.sichtbarSetzen(active);
        ButtonLeft.sichtbarSetzen(active);
        ButtonRight.sichtbarSetzen(active);

    }
}
