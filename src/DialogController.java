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

    private NpcController NPC_Controller;
    //private Bild ButtonWahl0;
    //private Bild ButtonWahl1;
    private HashMap<String, DialogController.DialogText> DialogListe; //für die Json

    private boolean[] GefundeneItems = new boolean[5];

    private int nextDialogCode = 100991; //JF aktueller nächster Code; oder erster Code
    private boolean dialogRunning = false;//wird gerade ein dialog abgespiel

    private int ersterDialogCode = 100000;
    private int wahl;
    private int aktuellerDialogCode;
    private String aktuellerDialog;
    private String letzterDialog;

    private int ButtonWahl = 2;
    private Bild[] Buttons = new Bild[ButtonWahl];
    private int aktuelleAuswahl = 0; //von 0 bis ButtonWahl -1



    private boolean genugItems;

    private boolean DialogMode;


    public DialogController(NpcController NPC_C){
        this.NPC_Controller = NPC_C;
        DialogMode = false;

        BackgroundBild = new Bild(400,500,"./Assets/Dialoge/DialogFenster.png");
        //ButtonWahl1 = new Bild("./Assets/Dialoge/ButtonWahl0");
        //ButtonWahl1 = new Bild("./Assets/Dialoge/ButtonWahl1");


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
        FillWahlObjects();
        //DialogController.DialogText element = DialogListe.get("1");
        //System.out.println(element.inhalt);
    }

    public void SetContent(String content){
        TextObject.positionSetzen(BackgroundBild.mittelPunkt());
        TextObject.inhaltSetzen(content);
        TextObject.verschieben(-TextObject.getBreite()/2,-TextObject.getHoehe()/2);//setzt Text in die Mitte vom Bild

    }

    public void setVisisbilty(boolean v){

        DialogMode = v;
        TextObject.sichtbarSetzen(v);
        BackgroundBild.sichtbarSetzen(v);
        for(Bild b : Buttons){
            b.sichtbarSetzen(v);
        } //buttons (un)sichtbar
        if(!v){
            dialogRunning = false; //macht dialog auch aus
        }
        //ButtonWahl0.sichtbarSetzen(v);
        //ButtonWahl1.sichtbarSetzen(v);


    }

    public void toggleVisibilty(){
        //System.out.println("toggle");
        if(DialogMode){
            this.setVisisbilty(false);
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

    public boolean isDialogRunning() {
        return dialogRunning;
    }
    //Startet Dialog
    /**
     * Grundgedanke:
     * Wenn man sich einer Person nähert, gibt es einen Knopf für den Start des Gesprächs ODER  alternativ öffnet sich das Dialogfenster automatisch.
     * Zunächst wird der letzte Dialog angezeigt, den man mit dieser Person geführt hat.
     * Nun kann man den Dialog fortsetzen (dabei evtl. zw. 2 Mögl. auswählen), sofern man alle nötigen Items gefunden hat.
     * Bei Wahl gibt es 2 Buttons, die mit den Pfeiltasten ausgewählt und mit Enter bestätigt werden können.
     * Mit der Leertaste kann das Dialogfenster geschlossen werden ODER durch Weggehen???
     *
     */
    public void dialogBeginnen() { //wird mit R aufgerufen
        setVisisbilty(true); //JF: dafür hatte ich eigentlich die setVisisbilty() Methode angedacht, die Alles(BILD,TEXT,..) ausblendet
        aktuelleAuswahl = 0; //Wahl1 wird ausgewählt
        /*this.SetContent(letzterDialog);
        updateWahl();
        updateButtons();*/
        if (wahl == 1){
            if(genugItems == true){
                letzterDialog = aktuellerDialog;
                //aktuellerDialogCode = ?
                this.SetContent(letzterDialog);
            }else{
                System.out.println("Nicht genug Items gefunden");
            }

        }else if(wahl == 2){ //Wahl 2
        }
    }
    public void openDialog(Player AP){ // JF:Variante zu dialogBeginnen()
        System.out.println("NEUER DIALOG WIRD AUFGERUFEN mit nextCode = ");
        if(dialogRunning){
            System.out.println("Dialog läuft schon");
        }
        else {
            dialogRunning = true;
            int playerCode = NPC_Controller.getCollidingNPC(AP) + 10; // +10 um von startindex 0 auf startindex 10 zu kommen

            int code = NPC_Controller.getLastCode(playerCode);
            int wantedPlayer = getPlayerFromCode(nextDialogCode);
            //System.out.println("DialogController: watnedPlayer = "+ wantedPlayer);
            if (playerCode == wantedPlayer) {
                System.out.println("DialogController: Player entspricht wanted Player");
                DialogText DT = DialogListe.get(String.valueOf(nextDialogCode)); //kriegt die Dialogzeile von dem CODE
                SetContent(DT.inhalt);
            } else if (playerCode != -1) {
                int LastDialogCode = NPC_Controller.getLastCode(playerCode);
                DialogText DT = DialogListe.get(String.valueOf(LastDialogCode));
                SetContent(DT.inhalt);
            } else {//kein Player geschnitten
                SetContent("DialogController: FEHLER: MIT KEINEM PLAYER GESCHNITTEN");
            }
        }
    }

    public int updateWahl(){
        return aktuelleAuswahl;
    }



    private void FillWahlObjects() {
        //System.out.println("FillWahlObjects");
        for (int i = 0; i < ButtonWahl; i++) {
            //System.out.println(i);
            try {
                Buttons[i] = new Bild(350 + i * 200, 500, "./Assets/Dialoge/ButtonWahl" + i + ".png"); //jedes Bild 200 pixel weiter rechts
                this.add(Buttons[i]);
                Buttons[i].sichtbarSetzen(true);
            }
            catch(Exception e) {
                System.out.println("DialogController: Fehler beim initaliseren der Buttons" + e);
            }
        }
        this.updateWahl();
    }

    private void updateButtons() {
        for (int i = 0; i < ButtonWahl; i++) {
            //System.out.println("DialogController: i= " + i);
            Buttons[i].setOpacity(0.5f);//alle halb sichbar
        }
        System.out.println(aktuelleAuswahl);
        Buttons[aktuelleAuswahl].setOpacity(1f);
    }

    public void SelectWahl(Player AP) { //Enter = 31
        switch (aktuelleAuswahl) {
            case 0: {
                System.out.println("Wahl1");
                dialogRunning = false;
                DialogText DT = DialogListe.get(String.valueOf(nextDialogCode)); //kriegt die Dialogzeile von dem CODE
                nextDialogCode = DT.Wahl1;
                openDialog(AP);
                wahl = 1;
            }
            break;
            case 1: {
                System.out.println("Wahl2");
                wahl = 2;
            }
            break;
        }

    }

    public boolean dialogActive(){
        return DialogMode;
    }

    public void ShiftLeft() {
        aktuelleAuswahl--;
        if (aktuelleAuswahl < 0) {
            aktuelleAuswahl = 0;//stay at first
        }
        updateButtons();
    }

    public void ShiftRight() {
        aktuelleAuswahl++;
        if (aktuelleAuswahl >= ButtonWahl) {
            aktuelleAuswahl = ButtonWahl - 1;//stay at last
        }
        updateButtons();
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


    private int getPlayerFromCode(int code){
        //gibt die ersten Beiden Ziffern des Codes zurück;
        int second = (code/10000) % 10; //Zahl an 5ter Stelle
        int first = (code/100000) % 10;
        return (first*10)+second;
    }
    private void readJSON() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./Assets/Files/Dialoge.json"));

            Type MapType = new TypeToken<HashMap<String, DialogController.DialogText>>() {}.getType();
            DialogListe = gson.fromJson(bufferedReader, MapType);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(ANSI_PURPLE + "DialogController: Ein Fehler beim Lesen der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "DialogController: Eigentlich kann nur das Storyteam schuld sein..." + ANSI_RESET);

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
