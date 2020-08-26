/**
 * Bereitet einen Startbildschim vor und zeigt diesen auch an.
 * Hier kann ausgewählt werden zwischen:
 *  - Neues Spiel starten
 *  - SaveGame Laden
 *  - LEER
 *  - Über Spiel
 *
 * ButtonBenennung:
 *  -ButtonX.png
 *  -X von 0 bis ButtonCount(z.B 4)
 */

import ea.Bild;
import ea.Knoten;

public class StartingScreen extends Knoten {

    private Bild BackgroundPic;


    private int ButtonCount = 4;
    private Bild[] Buttons = new Bild[ButtonCount];

    private int selection = 0; //von 0 bis ButtonCount -1

    private boolean active = false;//Ob der StartingScreen gerade offen ist, oder nicht.

    public StartingScreen(){
        BackgroundPic = new Bild(0,0,"./Assets/StartingScreen/Forest.jpg");
        this.add(BackgroundPic);

        FillButtonObjects();


    }

    private void FillButtonObjects(){
        System.out.println("FillButtonObjects");
        for(int i=0;i<ButtonCount;i++){
            System.out.println(i);
            Buttons[i] = new Bild(200 + i*200,400,"./Assets/StartingScreen/Button" + i + ".png"); //jedes Bild 200 pixel weiter rechts
            this.add(Buttons[i]);
        }
        UpdateButtons();
    }

    /**
     * Updatet die Auswahl der Knöpfe(auch graphisch) nach der -selection- Variable
     */
    private void UpdateButtons(){
        for(int i=0;i<ButtonCount;i++){
            Buttons[i].setOpacity(1f);//alle voll sichbar
        }
        System.out.println(selection);
        Buttons[selection].setOpacity(0.5f);
    }



    /**
     * Setzt den Linken Knopf "Aktiv"
     * und rechten nicht mehr "Aktiv"
     */
    public void ShiftLeft(){
        selection--;
        if(selection<0){
            selection = 0;//stay at first
        }
        UpdateButtons();
    }
    public void ShiftRight(){
        selection++;
        if(selection>=ButtonCount){
            selection = ButtonCount-1;//stay at last
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

        for(int i=0;i<ButtonCount;i++){
            Buttons[i].sichtbarSetzen(active);
        }
    }

}
