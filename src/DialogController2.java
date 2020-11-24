import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.tools.javac.Main;
import ea.Bild;
import ea.Knoten;
import ea.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * Diese Klasse soll ein Dialogfenster bestehend aus Formen/Texten/Knöpfen handeln.
 * Ausßerdem soll es aus Textdateien die Dialoge laden können und anzeigen
 * <p>
 * Liest möglicherweise eine XML aus mit Codes
 * Jeder Dialog hat außerdem max. 2 Optionen und die Codes für den anschließenden Dialog.
 * Lange Dialoge werden in kleinere unterteilt und haben dann nur 1 Option.
 */

public class DialogController2 extends Knoten {
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


    private NpcController2 NPC_Controller2;

    private Map<String, DialogController2.DialogLine> dialogLines; //für die Json mit den DialogZeilen
    private Map<String, Map<String, DialogPacket>> dialogPackets; //für die Json mit den DialogPackets

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


    public DialogController2(NpcController2 NPC_C2) {
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

    private void showWindow() {
        displayButtons[0].sichtbarSetzen(true);
        displayButtons[1].sichtbarSetzen(true);
        displayTextObject.sichtbarSetzen(true);
        displayResponseTextObject.sichtbarSetzen(true);
        displayDialogBackground.sichtbarSetzen(true);
    }

    private void hideWindow() {
        displayButtons[0].sichtbarSetzen(false);
        displayButtons[1].sichtbarSetzen(false);
        displayTextObject.sichtbarSetzen(false);
        displayResponseTextObject.sichtbarSetzen(false);
        displayDialogBackground.sichtbarSetzen(false);
    }

    private void updateText(String content) {
        buttonCursor = 0;
        if (!playingLastLine) {
            DialogLine currentLine = dialogLines.get(currentDialogCode);
            displayTextObject.inhaltSetzen(currentLine.inhalt);

            int width = (int) displayTextObject.getBreite();
            int posX_new = MAIN.x / 2 - width / 2;
            displayTextObject.positionSetzen(posX_new, textPosY);
        } else {
            displayTextObject.inhaltSetzen(content);
            int width = (int) displayTextObject.getBreite();
            int posX_new = MAIN.x / 2 - width / 2;
            displayTextObject.positionSetzen(posX_new, textPosY);
        }
    }

    private void advanceDialogLine() {
        if (!playingLastLine) {
            DialogLine currentLine = dialogLines.get(currentDialogCode);
            if (currentLine.nextTime.equals("")) {
                if (buttonCursor == 0) {
                    currentDialogCode = currentLine.wahl1;
                    if(dialogLines.get(currentLine.wahl1).name.equals("self")){
                        System.out.println("NpcController2: Die nächste line wird übersprungen, weil er von Npc selbst ist");
                        DialogLine nextLine = dialogLines.get(currentLine.wahl1);
                        currentDialogCode = nextLine.wahl1; //die 1 Wahl des nächsten Dialogs wird als nächste Zeile festgelegt
                    }
                }
                if (buttonCursor == 1) {
                    currentDialogCode = currentLine.wahl2;
                    if(dialogLines.get(currentLine.wahl2).name.equals("self")){
                        System.out.println("NpcController2: Die nächste line wird übersprungen, weil er von Npc selbst ist");
                        DialogLine nextLine = dialogLines.get(currentLine.wahl1);
                        currentDialogCode = nextLine.wahl1; //die 1 Wahl des nächsten Dialogs wird als nächste Zeile festgelegt
                    }
                }
                System.out.println("DialogController2: Dialog weitergeführt. Der NPC Names " + dialogLines.get(currentDialogCode).name + " spricht jetzt!");
                updateText(null);
                DialogLine newLine = dialogLines.get(currentDialogCode);
                if (newLine.wahl2.equals("")) {
                    oneButtonMode = true; //blendet effektiv den 2.Button aus
                } else {
                    oneButtonMode = false;
                }
                updateButtons();
                updateResonse();
            } else {    //falls nextTime nicht leer
                globalTemporalPosition = currentLine.nextTime;
                System.out.println("DialogController2: Dialog hat sein Ende erreicht und die ZeitPosition ist fortgeschritten auf: " + currentLine.nextTime + " !");
                endDialog();
            }
        } else { //playingLastLine!"!
            endDialog();
        }
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


    public void openDialogPacket(String NpcID) {
        if (!active) {
            waitingForInput = true;
            active = true;
            if (!isPlayingLastLine()) {
                DialogController2.DialogPacket element = dialogPackets.get(globalTemporalPosition).get(NpcID);
                currentDialogCode = element.code;
                oneButtonMode = false;
                showWindow();
                updateResonse();
                updateButtons();
                updateText(null);

            } else {
                oneButtonMode = true;
                showWindow();
                playLastLine(NpcID);
                updateButtons();
            }


        } else {
            System.out.println("DialogController2: Dialog schon offen");
        }


    }

    private void playLastLine(String npcID) {
        String lastLineID = NPC_Controller2.getNpcLastLine(npcID);
        System.out.println("LastLineID: " + lastLineID);
        DialogLine lastLine = dialogLines.get(lastLineID);
        displayResponseTextObject.sichtbarSetzen(false);
        System.out.println("Probiert jz den Dialog mit dem Inhalt abzuspielen: " + lastLine.inhalt);
        updateText(lastLine.inhalt);
    }

    public boolean isDialogPacketPlayable(String NpcID) {
        if (dialogPackets.containsKey(globalTemporalPosition)) {
            Map<String, DialogPacket> innnerPacketMap = dialogPackets.get(globalTemporalPosition);
            if (innnerPacketMap.containsKey(NpcID)) {
                DialogPacket element = innnerPacketMap.get(NpcID);   //stellt jedes Element der Map einmal als "element" zur Verfügung
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
                playingLastLine = true; //sorgt dafür, dass playLastLine aufgerufen wird
                return true;

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
        displayButtons[0].setOpacity(0.3f);
        displayButtons[1].setOpacity(0.3f);
        if (isWaitingForInput()) {
            switch (dir) {
                case "links":
                    System.out.println("TESTE LINKS GEDRÜCKT");
                    buttonCursor--;
                    if (buttonCursor < 0) {
                        buttonCursor = 0;
                    }
                    updateResonse();
                    break;
                case "rechts":
                    buttonCursor++;
                    if (buttonCursor > 1) {
                        buttonCursor = 1;
                    }
                    updateResonse();
                    break;
                case "enter":
                    advanceDialogLine();
                    break;

                default:
                    System.out.println("DialogController2: Kein valider Input");
            }
            updateButtons();
        } else {
            System.out.println("DialogController2: WARTET NICHT AUF INPUT");
        }
    }

    public void updateResonse() {
        if (!playingLastLine) {
            displayResponseTextObject.sichtbarSetzen(true);
            DialogLine currentLine = dialogLines.get(currentDialogCode);
            DialogLine nextLine;
            if (buttonCursor == 0) {
                nextLine = dialogLines.get(currentLine.wahl1);
            } else {
                nextLine = dialogLines.get(currentLine.wahl2);
            }

            displayResponseTextObject.inhaltSetzen("Antwort: " + nextLine.inhalt);
            int width = (int) displayResponseTextObject.getBreite();
            int posX_new = MAIN.x / 2 - width / 2;
            displayResponseTextObject.positionSetzen(posX_new, textPosY - 50);
        } else {
            displayResponseTextObject.sichtbarSetzen(false);
        }

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

            Type MapType = new TypeToken<Map<String, DialogController2.DialogLine>>() {
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

            Type MapType = new TypeToken<Map<String, Map<String, DialogPacket>>>() {
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
