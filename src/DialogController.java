import ea.*;

/**
 * Diese Klasse soll ein Dialogfenster bestehend aus Formen/Texten/Knöpfen handeln.
 * Ausßerdem soll es aus Textdateien die Dialoge laden können und anzeigen
 *
 * Liest möglicherweise eine XML aus mit Codes
 * Jeder Dialog hat außerdem max. 2 Optionen und die Codes für den anschließenden Dialog.
 * Lange Dialoge werden in kleinere unterteilt und haben dann nur 1 Option.
 */

public class DialogController {
    private TEXT TextObject;
    private BILD BackgroundBild;


    public DialogController(){
        BackgroundBild = new BILD(400,500,"./Assets/DialogFenster.png");
        BackgroundBild.sichtbarSetzen(false);
        TextObject = new TEXT("MANNO",200,500,10,"Weiss");
        TextObject.sichtbarSetzen(false);




    }
    public void SetContent(String content){
        TextObject.inhaltSetzen(content);
    }

    public void ShowWindow(){
        TextObject.sichtbarSetzen(true);
        BackgroundBild.sichtbarSetzen(true);

    }
    public void HideWindow(){
        TextObject.sichtbarSetzen(true);
        BackgroundBild.sichtbarSetzen(true);
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
