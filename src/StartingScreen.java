/**
 * Bereitet einen Startbildschim vor und zeigt diesen auch an.
 * Hier kann ausgewählt werden zwischen:
 * - Neues Spiel starten
 * - SaveGame Laden
 * - LEER
 * - Über Spiel
 * <p>
 * ButtonBenennung:
 * -ButtonX.png
 * -X von 0 bis ButtonCount(z.B 4)
 */

import ea.Bild;
import ea.Knoten;
import ea.Text;

public class StartingScreen extends Knoten {

    private Bild BackgroundPic;
    private Bild loadingPic;


    private int ButtonCount = 5;
    private Bild[] Buttons = new Bild[ButtonCount];

    private int selection = 0; //von 0 bis ButtonCount -1

    private boolean active = false;//Ob der StartingScreen gerade offen ist, oder nicht.

    private Text tb0; //Text fuer Button 0 StartingScreen
    private Text tb1; //Text fuer Button 1 StartingScreen

    /*
    private Text tb2; //Text fuer Button 2 StartingScreen
    private Text tb3; //Text fuer Button 3 StartingScreen
     */

    public StartingScreen() {
        BackgroundPic = new Bild(0, 0, "./Assets/StartingScreen/StartbilschirmOHNEButtonsundTitel.png");
        loadingPic = new Bild(0, 0, MAIN.playerStillImgPath);
        float xPos = MAIN.x / 2 - (loadingPic.getBreite() / 2);
        float yPos = MAIN.y / 2 - (loadingPic.getHoehe() / 2);
        loadingPic.positionSetzen(xPos, yPos);
        this.add(BackgroundPic);
        this.add(loadingPic);
        loadingPic.sichtbarSetzen(false);
        FillButtonObjects();
    }

    private void FillButtonObjects() {
        //System.out.println("FillButtonObjects");
        for (int i = 0; i < ButtonCount; i++) {
            //System.out.println(i);
            Buttons[i] = new Bild(0, 0, "./Assets/StartingScreen/ButtonFinal" + i + ".png"); //jedes Bild 200 pixel weiter rechts
            this.add(Buttons[i]);
        }
        UpdateButtons();
    }

    /**
     * Updatet die Auswahl der Knöpfe(auch graphisch) nach der -selection- Variable
     */
    private void UpdateButtons() {
        this.TextStartScEntfernen();
        for (int i = 0; i < ButtonCount; i++) {
            Buttons[i].setOpacity(0.5f);//alle halb sichbar
        }
        System.out.println(selection);
        Buttons[selection].setOpacity(1f);
    }


    /**
     * Setzt den Linken Knopf "Aktiv"
     * und rechten nicht mehr "Aktiv"
     */
    public void ShiftLeft() {
        selection--;
        if (selection < 0) {
            selection = 0;//stay at first
        }

        UpdateButtons();
    }

    public void ShiftRight() {
        selection++;
        if (selection >= ButtonCount) {
            selection = ButtonCount - 1;//stay at last
        }
        UpdateButtons();
    }

    public boolean isActive() {
        return active;
    }

    public int getSelection() {
        return selection;

    }

    public void setActive(boolean active) {
        this.active = active;

        BackgroundPic.sichtbarSetzen(active);
        loadingPic.sichtbarSetzen(false);

        for (int i = 0; i < ButtonCount; i++) {
            Buttons[i].sichtbarSetzen(active);
        }
    }

    public void tickLoadingAnimation() {
        if (this.active) {
            //System.out.println("StartingScreen: tickLoadingAnimation();");
            loadingPic.drehenRelativ(6);
        }
    }


    //unbenutzt
    public void SelectButtons() { //Enter = 31
        switch (selection) {
            case 0: {
                TextStartScEntfernen();
                System.out.println("Button 1: Exit");
                this.setActive(false);
                //tb0 = new Text("Text fuer Button 0",150,350);
                //this.add(tb0);
            }
            break;
            case 1: {
                TextStartScEntfernen();
                System.out.println("Button 2: Play");
                //tb1 = new Text("Text fuer Button 1",350,350);
                //this.add(tb1);
            }
            break;
        }
    }

    public void startLoadingScreen() {
        BackgroundPic.sichtbarSetzen(false);
        for (int i = 0; i < ButtonCount; i++) {
            Buttons[i].sichtbarSetzen(false);
        }
        loadingPic.sichtbarSetzen(true);
    }

    public void hideLoadingScreen() {
        this.setActive(false);

    }


    public void TextStartScEntfernen() {
        //JF: Du kannst auch einfach tb0.sichtbarSetzen(false); machen
        //JF: Oder füg die einfach zur Mehtode setActive hinzu. Da wird alles ausgeblendet.
        this.entfernen(tb0);
        this.entfernen(tb1);
        //this.entfernen(tb2);
        //this.entfernen(tb3);
    }
}
