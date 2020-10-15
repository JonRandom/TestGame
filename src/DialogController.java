import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ea.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Diese Klasse soll ein Dialogfenster bestehend aus Formen/Texten/Knöpfen handeln.
 * Ausßerdem soll es aus Textdateien die Dialoge laden können und anzeigen
 *
 * Liest möglicherweise eine XML aus mit Codes
 * Jeder Dialog hat außerdem max. 2 Optionen und die Codes für den anschließenden Dialog.
 * Lange Dialoge werden in kleinere unterteilt und haben dann nur 1 Option.
 */

public class DialogController extends Knoten{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    private Text TextObject;
    private Bild BackgroundBild;
    private HashMap<String, DialogController.DialogText> DialogListe; //für die Json

    private boolean[] GefundeneItems = new boolean[5];

    int ersterDialogCode = 100000;
    int letzterDialogCode; //ZAhlen Code des zuletzt mit dieser Person geführten Code (= dialogCode -1 !?)
    boolean genugItems;


    private boolean DialogMode;


    public DialogController(){

        DialogMode = false;

        BackgroundBild = new Bild(400,500,"./Assets/DialogFenster.png");

        float tempPosX =MAIN.x/2-(BackgroundBild.getBreite()/2);
        float tempPosY =MAIN.y/2-(BackgroundBild.getHoehe()/2);

        BackgroundBild.positionSetzen(tempPosX,tempPosY);
        BackgroundBild.sichtbarSetzen(false);

        TextObject = new Text(200,200,20, "START TEXT HELLO");
        System.out.println(BackgroundBild.mittelPunkt());

        TextObject.positionSetzen(BackgroundBild.mittelPunkt());
        TextObject.verschieben(-TextObject.getBreite()/2,-TextObject.getHoehe()/2);
        TextObject.sichtbarSetzen(false);
        TextObject.farbeSetzen("Rot");

        this.add(BackgroundBild,TextObject);

        readJSON();
        DialogController.DialogText element = DialogListe.get("1");
        System.out.println(element.inhalt);
    }

    public void SetContent(String content){
        TextObject.positionSetzen(BackgroundBild.mittelPunkt());
        TextObject.verschieben(-TextObject.getBreite()/2,-TextObject.getHoehe()/2);
        TextObject.inhaltSetzen(content);
    }

    public void setVisisbilty(boolean v){
        DialogMode = v;
        TextObject.sichtbarSetzen(v);
        BackgroundBild.sichtbarSetzen(v);
    }

    public void toggleVisibilty(){
        //System.out.println("toggle");
        if(DialogMode){
            setVisisbilty(false);
            DialogMode = (false);

        }
        else{
            //System.out.println("anzeigen");
            setVisisbilty(true);
            DialogMode = (true);
        }
    }

    public boolean GetDialogStatus(){
        return DialogMode;
    }

    //Startet Dialog
    /**
     * Grundgedanke:
     * Wenn man sich einer Person nähert, gibt es einen Knopf für den Start des Gesprächs ODER  alternativ öffnet sich das Dialogfenster automatisch.
     * Zunächst wird der letzte Dialog angezeigt, den man mit dieser Person geführt hat.
     * Nun kann man den Dialog fortsetzen (dabei evtl. zw. 2 Mögl. auswählen), sofern man alle nötigen Items gefunden hat.
     * Bei Wahl gibt es 2 Buttons, die mit den Pfeiltasten ausgewaählt und mit Enter bestätigt werden können.
     * Mit der Leertaste kann das Dialogfenster geschlossen werden ODER durch Weggehen???
     *
     */
    public void dialogBeginnen() {
        setVisisbilty(true); //JF: dafür hatte ich eigentlich die setVisisbilty() Methode angedacht, die Alles(BILD,TEXT,..) ausblendet

    }


    //Überprüft, ob alle notwendigen Items gesammelt sind
    public void genugItems(){
        DialogController.DialogText element = DialogListe.get("1"); //JF: muss pro Zeile geupdatet werden
        boolean brauchtFlags[] = element.brauchtFlags; //JF: holt dir für deine Zeile jz die BrauchItems Liste/Array

        genugItems = false;
        if (brauchtFlags[0]==true){
            if(GefundeneItems[0]==false){
                System.out.println("Nicht genug Items gefunden! Versuch´s später nochmal!");
            }else {
                if (brauchtFlags[1] == true) {
                    if (GefundeneItems[1] == false) {
                        System.out.println("Nicht genug Items gefunden! Versuch´s später nochmal!");
                    } else {
                        if (brauchtFlags[2] == true) {
                            if (GefundeneItems[2] == false) {
                                System.out.println("Nicht genug Items gefunden! Versuch´s später nochmal!");
                            } else {
                                if (brauchtFlags[3] == true) {
                                    if (GefundeneItems[3] == false) {
                                        System.out.println("Nicht genug Items gefunden! Versuch´s später nochmal!");
                                    } else {
                                        if (brauchtFlags[4] == true) {
                                            if (GefundeneItems[4] == false) {
                                                System.out.println("Nicht genug Items gefunden! Versuch´s später nochmal!");
                                            } else {
                                                genugItems = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } //Ende public void genugItems()


    private void readJSON() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./Assets/Files/Dialoge.json"));

            Type MapType = new TypeToken<HashMap<String, DialogController.DialogText>>() {}.getType();
            DialogListe = gson.fromJson(bufferedReader, MapType);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(ANSI_PURPLE + "Map3: Ein Fehler beim Lesen der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "Map3: Eigentlich kann nur das Storyteam schuld sein..." + ANSI_RESET);

        }

    }


    /**
     * JF:
     * Das ist die Klasse die als Muster zum Auslesen des JSON (mit GSON) dient.
     * Alle Methoden hierdrinn sind also nur für eine Textzeile im allgemeinen verwendbar und selten brauchbar.
     * Eigentlich muss in dieser Klasse nicht geändert werden
     */
    public class DialogText {

        int dialogCode; //Zahlen Code
        // GENAU 5 Elemtente!
        boolean[] brauchtFlags; //Flags die gefunden werden müssen z.B [false,false,false,false]

        String inhalt; //Text der Dialog Zeile

        int Wahl1; // Code der Ersten Wahl
        int Wahl2; // Code der Zweiten Wahl

    }
}
