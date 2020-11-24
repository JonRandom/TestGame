import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Knoten;
import ea.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class DialogController3 extends Knoten {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    private final String dialogLinesPath = "./Assets/Files/Dialoge.json";
    private final String dialogPacketsPath = "./Assets/Files/DialogPackets.json";


    //GLOBAL STUFF;
    private String globalTemporalPosition = "01";
    ArrayList<String> foundItems = new ArrayList<String>();
    ArrayList<String> readLines = new ArrayList<String>();

    private boolean active = false;
    private boolean waitingForInput = false;
    private boolean playingLastLine = false; // Sonderfall es wird kein echter Dialog abgespielt;


    private final NpcController2 NPC_Controller2;

    private Map<String, DialogController3.DialogLine> dialogLines; //für die Json mit den DialogZeilen
    private Map<String, Map<String, DialogController3.DialogPacket>> dialogPackets; //für die Json mit den DialogPackets

    //VISIBLE STUFF;
    private String defaultPath = "./Assets/Dialoge/";
    private Text displayTextObject;
    private Text displayResponseTextObject;
    private Bild displayDialogBackground;
    private Bild[] displayButtons;
    private final int textPosY = 850;

    //DIALOG LINE STUFF;
    private String currentDialogCode;

    //WAHL;
    private int buttonCursor = 0;
    private boolean oneButtonMode = false; //Wenn es nur eine Wahl gibt, wird ein Knopf ausgeblendet


    public DialogController3(NpcController2 NPC_C2) {
        this.NPC_Controller2 = NPC_C2;
        //initialisert
        readJSON_DialogLines();
        readJSON_DialogPackets();

        addDisplayObjects();
        hideWindow();
    }

    private void addDisplayObjects() {
        int textPosX = MAIN.x / 2;
        displayButtons = new Bild[2];


        //Bilder mit try catch
        try {
            displayDialogBackground = new Bild(defaultPath + "DialogFenster.png");
            displayDialogBackground.positionSetzen(textPosX - displayDialogBackground.getBreite() / 2, textPosX);
            displayButtons[0] = new Bild(defaultPath + "ButtonWahl0.png");
            displayButtons[0].positionSetzen(400, textPosY);
            displayButtons[1] = new Bild(defaultPath + "ButtonWahl1.png");
            displayButtons[1].positionSetzen(700, textPosY);
            this.add(displayDialogBackground);
            this.add(displayButtons[0], displayButtons[1]);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DialogController2: FEHLER beim Importieren der Bilder");
        }

        //Text als letztes also ganz oben
        displayResponseTextObject = new Text(textPosX, textPosY - 50, "DEFAULT RESONSE TEXT");
        displayTextObject = new Text(textPosX, textPosY, "DEFAULT TEXT");
        this.add(displayTextObject, displayResponseTextObject);
    }

    /**
     * Main Methode die auch von SPIEL aufgerufen wird.
     * @param npcID String ID des NPCs
     */
    public void startDialog(String npcID) { //Voraussetztung Kollision mit NPC und activ=false;
        active = true;
        if (isDialogPacketPlayable(npcID)) {
            //start eines neuen Dialogpacketes
            DialogController3.DialogPacket element = dialogPackets.get(globalTemporalPosition).get(npcID);
            currentDialogCode = element.code;
            displayCurrentDialogLine();
        } else { //Es wird kein Dialogpacket für diesen NPC gefunden worden
            playLastLine(npcID);
        }
    }

    private void displayCurrentDialogLine() {
        playingLastLine = false;
        DialogController3.DialogLine currentLine = dialogLines.get(currentDialogCode);
        if(currentLine.name.equals("self")) {
            System.out.println("DialogController3: Dialog von selber");
            oneButtonMode = true;
            nextLine();
        } else {
            //displayResponseTextObject.sichtbarSetzen(true);
            if(currentLine.wahl2.equals("")) { //nur eine Wahl
                oneButtonMode = true;
                updateButtons();
                //System.out.println("DialogController3:");

            } else {
                oneButtonMode = false;
                updateButtons();
                //System.out.println("DialogController3:");
            }
            setReplyText();
            setConvText(currentLine.inhalt);

        }
    }
    private void nextLine() {
        DialogController3.DialogLine currentLine = dialogLines.get(currentDialogCode);
        DialogController3.DialogLine nextLine = null;
        if(oneButtonMode) {
            buttonCursor = 0;
        }
        if(buttonCursor == 0) {
            nextLine = dialogLines.get(currentLine.wahl1);
            currentDialogCode = currentLine.wahl1;
        }
        if(buttonCursor == 1) {
            nextLine = dialogLines.get(currentLine.wahl2);
            currentDialogCode = currentLine.wahl1;
        }
        if(!nextLine.nextTime.equals("")) {
            endDialog();
            globalTemporalPosition = nextLine.nextTime;
        }
        displayCurrentDialogLine();
    }

    private void endDialog() {
        hideWindow();
        active = false;
        currentDialogCode = null; //kontorvers ob das hier Sinn macht
        waitingForInput = false;
        playingLastLine = false;
        //NPC_Controller2.prepareReset(); //setzt flag hig, damit Spieler nächsten Tick(in SPIEL.java) zurückgesetzt wird.
        NPC_Controller2.resetToLastQuietPos();
    }

    private void playLastLine(String npcID) {
        waitingForInput = true;
        playingLastLine = true; //für die input klasse
        String lastLineID = NPC_Controller2.getNpcLastLine(npcID);
        //System.out.println("LastLineID: " + lastLineID);
        DialogController3.DialogLine lastLine = dialogLines.get(lastLineID);


        displayResponseTextObject.sichtbarSetzen(false);
        displayTextObject.sichtbarSetzen(true);
        displayDialogBackground.sichtbarSetzen(true);
        displayButtons[0].sichtbarSetzen(true);
        displayButtons[1].sichtbarSetzen(false);
        String inhalt;
        try {
            inhalt = lastLine.inhalt;
            setConvText(inhalt);
        }
        catch (Exception e) {
            System.out.println("DialogController3: FEHLER: Für diesem NPC gibt es scheinbar kein lastLine Eintrag");
        }
    }

    public boolean isDialogPacketPlayable(String npcID) {
        if (dialogPackets.containsKey(globalTemporalPosition)) {
            Map<String, DialogController3.DialogPacket> innnerPacketMap = dialogPackets.get(globalTemporalPosition);
            if (innnerPacketMap.containsKey(npcID)) {
                DialogController3.DialogPacket element = innnerPacketMap.get(npcID);   //stellt jedes Element der Map einmal als "element" zur Verfügung
                if (foundItems.containsAll(element.requiredItems)) {
                    if (readLines.containsAll(element.requiredLines)) {
                        System.out.println("DialogController2: Es sind alle nötigen Items und Zeilen vorhanden");
                        return true;
                    } else {
                        System.out.println("DialogController2: Es sind alle nötigen Items vorhanden, aber nicht alle Zeilen");
                        return false;
                    }
                } else {
                    System.out.println("DialogController2: Es sind nicht alle nötigen Items gefunden worden");
                    return false;
                }
            } else {
                System.out.println("DialogController2: Zu diesem Spieler gibt es im Moment kein Eintrag in der Story");
                return false;

            }
        } else {
            System.out.println("DialogController2: FEHLER: Zu diesem Zeitpunkt gibt es keinen Eintrag");
            return false;
        }
    }

    private void updateButtons() {
        displayButtons[0].setOpacity(0.3f);
        if (oneButtonMode) {
            buttonCursor = 0;
            displayButtons[1].setOpacity(0); //ausgeblendet
        } else {
            displayButtons[1].setOpacity(0.3f);
        }
        displayButtons[buttonCursor].setOpacity(1f);
    }

    public void input(String dir) {
        if (isWaitingForInput()) {
            switch (dir) {
                case "links":
                    buttonCursor--;
                    if (buttonCursor < 0) {
                        buttonCursor = 0;
                    }
                    setReplyText();
                    break;
                case "rechts":
                    buttonCursor++;
                    if (buttonCursor > 1) {
                        buttonCursor = 1;
                    }
                    setReplyText();
                    break;
                case "enter":
                    nextLine();
                    break;

                default:
                    System.out.println("DialogController2: Kein valider Input");
            }
            updateButtons();
        } else {
            System.out.println("DialogController2: WARTET NICHT AUF INPUT");
        }
    }

    private void hideWindow() {
        displayButtons[0].sichtbarSetzen(false);
        displayButtons[1].sichtbarSetzen(false);
        displayTextObject.sichtbarSetzen(false);
        displayResponseTextObject.sichtbarSetzen(false);
        displayDialogBackground.sichtbarSetzen(false);
    }

    private void setConvText(String text) {
        displayTextObject.inhaltSetzen(text);
        int width = (int) displayTextObject.getBreite();
        int posX_new = MAIN.x / 2 - width / 2;
        displayTextObject.positionSetzen(posX_new, textPosY);
    }
    private void setReplyText() {
        displayResponseTextObject.sichtbarSetzen(true);
        DialogController3.DialogLine currentLine = dialogLines.get(currentDialogCode);
        if(currentLine.wahl2.equals("")) {//falls die wahl2 leer ist bzw. es nur eine Option gibt
            if(!currentLine.name.equals("self")) { //wenn man auch nicht selber drann ist
                displayResponseTextObject.sichtbarSetzen(false);
            }
        }
        if(playingLastLine = true) {
            displayResponseTextObject.sichtbarSetzen(false);
        }
        DialogController3.DialogLine nextLine = null;
        if(oneButtonMode) {
            buttonCursor = 0;
        }
        if(buttonCursor == 0) {
            nextLine = dialogLines.get(currentLine.wahl1);
        }
        if(buttonCursor == 1) {
            nextLine = dialogLines.get(currentLine.wahl2);
        }
        displayResponseTextObject.inhaltSetzen(nextLine.inhalt);
        int width = (int) displayResponseTextObject.getBreite();
        int posX_new = MAIN.x / 2 - width / 2;
        displayResponseTextObject.positionSetzen(posX_new, textPosY);
    }

    public boolean isActive() {
        return active;
    }

    public String getGlobalTemporalPosition() {
        return globalTemporalPosition;
    }

    public boolean isWaitingForInput() {
        return waitingForInput;
    }

    private void readJSON_DialogLines() {
        Gson gson = new Gson();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(dialogLinesPath));

            Type MapType = new TypeToken<Map<String, DialogController3.DialogLine>>() {
            }.getType();
            dialogLines = gson.fromJson(bufferedReader, MapType);
            System.out.println();
            System.out.println(ANSI_GREEN + "DialogController2: JSON(" + dialogLinesPath + ")  erfolgreich gelesen" + ANSI_RESET);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "DialogController2: Ein Fehler beim Lesen der Json Datei(" + dialogLinesPath + " ). Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }

    }

    private void readJSON_DialogPackets() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(dialogPacketsPath));

            Type MapType = new TypeToken<Map<String, Map<String, DialogController3.DialogPacket>>>() {
            }.getType();
            dialogPackets = gson.fromJson(bufferedReader, MapType);
            System.out.println(ANSI_GREEN + "DialogController2: JSON(" + dialogPacketsPath + ")  erfolgreich gelesen" + ANSI_RESET);
            //System.out.println("ANTWORT: " + dialogPackets.get("01").get("11").NpcID);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "DialogController2: Ein Fehler beim Lesen der Json Datei(" + dialogPacketsPath + " ). Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }

    }

    public boolean isPlayingLastLine() {
        return playingLastLine;
    }



    /**
     * JF:
     * Das ist die Klasse die als Muster zum Auslesen des JSON (mit GSON) dient.
     * Alle Methoden hierdrinn sind also nur für eine Textzeile im allgemeinen verwendbar und selten brauchbar.
     * Eigentlich muss in dieser Klasse nicht geändert werden
     */
    public class DialogLine {

        String inhalt; //Text der Dialog Zeile
        String name; //NPC bei dem der Dialog abgespielt wird
        String wahl1; // Code der Ersten Wahl
        String wahl2; // Code der Zweiten Wahl

        String nextTime; //nexter Zeitabschnitt, leer wenn nicht letzter

    }

    public class DialogPacket {
        //key Time also "01!

        ArrayList<String> requiredItems;
        ArrayList<String> requiredLines;

        String code; // erster Code des Dialogs


    }
}

