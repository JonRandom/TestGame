import ea.*;

/**
 * Diese Klasse soll ein Dialogfenster bestehend aus Formen/Texten/Knöpfen handeln.
 * Ausßerdem soll es aus Textdateien die Dialoge laden können und anzeigen
 *
 * Liest möglicherweise eine XML aus mit Codes
 * Jeder Dialog hat außerdem max. 2 Optionen und die Codes für den anschließenden Dialog.
 * Lange Dialoge werden in kleinere unterteilt und haben dann nur 1 Option.
 */

public class DialogController extends Knoten{
    private Text TextObject;
    private Bild BackgroundBild;

    private int WindowSizeX= 1000;
    private int WindowSizeY= 600;

    private boolean DialogMode;


    public DialogController(){

        DialogMode = false;

        BackgroundBild = new Bild(400,500,"./Assets/DialogFenster.png");

        float tempPosX =WindowSizeX/2-(BackgroundBild.getBreite()/2);
        float tempPosY =WindowSizeY-(BackgroundBild.getHoehe());

        BackgroundBild.positionSetzen(tempPosX,tempPosY);
        BackgroundBild.sichtbarSetzen(false);

        TextObject = new Text(200,200,20, "START TEXT HELLO");
        System.out.println(BackgroundBild.mittelPunkt());

        TextObject.positionSetzen(BackgroundBild.mittelPunkt());
        TextObject.verschieben(-TextObject.getBreite()/2,-TextObject.getHoehe()/2);
        TextObject.sichtbarSetzen(false);
        TextObject.farbeSetzen("Rot");


        this.add(BackgroundBild,TextObject);
    }

    public void SetContent(String content){
        TextObject.inhaltSetzen(content);
    }

    public void ShowWindow(){
        DialogMode = true;
        TextObject.sichtbarSetzen(true);
        BackgroundBild.sichtbarSetzen(true);
    }


    public void HideWindow(){
        DialogMode = false;
        TextObject.sichtbarSetzen(true);
        BackgroundBild.sichtbarSetzen(true);
    }

    /**
     *
     * @return  boolean - Gibt zurück, ob der Dialog-Modus aktiv ist
     */

    public boolean GetDialogStatus(){
        return DialogMode;
    }


    /**
     *
     * @param option Gibt die im Diaglogfenster ausgewählte Option weiter.
     *               Diese bestimmtden weiteren Dialog.
     *               Die Auswahlt wird über die Tastatur gemacht.
     *
     */
    public void advanceDialog(int option){

    }
}
