import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Knoten;
import ea.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

public class DialogController4 extends Knoten {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    private final String dialogLinesPath = "./Assets/Files/Dialoge.json";
    private final String dialogPacketsPath = "./Assets/Files/DialogPackets.json";


    //GLOBAL STUFF;
    private String globalTemporalPosition = "Tag 1 Abschnitt 1 (Start Zeit)";


    //Mode booleans
    private boolean active = false;
    private boolean waitingForInput = false;
    private boolean playingLastLine = false; // Sonderfall es wird kein echter Dialog abgespielt;

    //sub-Objects
    private final NpcController2 NPC_Controller2;
    private final GameSaver gameSaver;

    //JSON GSON
    private Map<String, DialogController4.DialogLine> dialogLines; //für die Json mit den DialogZeilen
    private Map<String, Map<String, List<DialogController4.DialogPacket>>> dialogPackets; //für die Json mit den DialogPackets

    //VISIBLE STUFF;
    private String defaultPath = "./Assets/Dialoge/";
    private Text displayTextObject;
    private Text displayResponseTextObject;
    private Bild displayDialogBackground;
    private Bild[] displayButtons;
    private final int textPosY = 850;

    //DIALOG LINE STUFF;
    private String currentDialogCode;
    private boolean lastLineSelf = false; //war die Letzte Zeile self?

    //WAHL;
    private int buttonCursor = 0;
    private boolean oneButtonMode = false; //Wenn es nur eine Wahl gibt, wird ein Knopf ausgeblendet

    //lastLine
    private Map<String, String> lastLines = new HashMap<String, String>() {
    }; //key mit name und als inhalt den Code


    public DialogController4(NpcController2 NPC_C2, GameSaver gs) {
        this.NPC_Controller2 = NPC_C2;
        this.gameSaver = gs;
        //initialisert
        readJSON_DialogLines();
        readJSON_DialogPackets();

        addDisplayObjects();
        hideWindow();
        NPC_Controller2.updateNpcPositions(globalTemporalPosition);
    }

    private void addDisplayObjects() {
        int textPosX = MAIN.x / 2;
        displayButtons = new Bild[2];
        //Bilder mit try catch
        try {
            displayDialogBackground = new Bild(defaultPath + "DialogFenster.png");
            displayDialogBackground.positionSetzen(textPosX - displayDialogBackground.getBreite() / 2, textPosX);
            displayButtons[0] = new Bild(defaultPath + "ButtonWahl0.png");
            displayButtons[0].positionSetzen(400, textPosY + 50);
            displayButtons[1] = new Bild(defaultPath + "ButtonWahl1.png");
            displayButtons[1].positionSetzen(700, textPosY + 50);
            this.add(displayDialogBackground);
            this.add(displayButtons[0], displayButtons[1]);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DialogController4: FEHLER beim Importieren der Bilder");
        }

        //Text als letztes also ganz oben
        displayResponseTextObject = new Text(textPosX, textPosY - 50, "DEFAULT RESPONSE TEXT");
        displayTextObject = new Text(textPosX, textPosY, "DEFAULT TEXT");
        this.add(displayTextObject, displayResponseTextObject);
    }

    /**
     * Main Methode die auch von SPIEL aufgerufen wird.
     *
     * @param npcID String ID des NPCs
     */
    public void startDialog(String npcID) { //Voraussetztung Kollision mit NPC und activ=false;
        waitingForInput = true;
        active = true;
        if (isDialogPacketPlayable(npcID)) {
            //start eines neuen Dialogpacketes
            DialogController4.DialogPacket element = getPlayableDialogPacket(npcID);
            currentDialogCode = element.code;
            showWindow();
            displayCurrentDialogLine();
        } else { //Es wird kein Dialogpacket für diesen NPC gefunden worden
            playLastLine(npcID);
        }
    }

    public void highLightReadyNpcs() {
        if (dialogPackets.containsKey(globalTemporalPosition)) {
            Map<String, List<DialogController4.DialogPacket>> innnerPacketMap = dialogPackets.get(globalTemporalPosition);
            Set keys = innnerPacketMap.keySet();
            if (keys.isEmpty()) {
                System.out.println("DialogController4: FEHLER BEIM HIGHLIGHTEN, KEIN SPIELER KANN GERADE SPRECHEN");
            } else {
                NPC_Controller2.highLightNpcs(keys);
            }
        } else {
            System.out.println("DialogController4: FEHLER BEIM HIGHLIGHTEN, DIE NÄCHSTE ZEIT GIBT ES GAR NICHT");
            System.out.println("DialogController4: Die Zeit POS ist:" + globalTemporalPosition);
            NPC_Controller2.highLightNpcs(null);
        }

    }


    private void displayCurrentDialogLine() {
        playingLastLine = false;
        DialogController4.DialogLine currentLine = dialogLines.get(currentDialogCode);
        if(currentLine == null){
            System.out.println("DialogController4: FEHLER: Die Letzte Line war scheinbar korrupt, sie zeigt auf eine andere leere Line: " + currentLine);
        }
        gameSaver.addLine(currentDialogCode);
        if(currentDialogCode == null){
            System.out.println("DialogController4: FEHLER: Die Letzte Line war scheinbar korrupt, sie hat scheinbar keinen Code??");
        }
        //System.out.println("DialogController4: Die current line wird displayed. Die Line hat den CODE: " + currentDialogCode);

        if (currentLine.name.equals("self") && !lastLineSelf) { //wenn man selber drann ist und nicht schon einmal drann war
            lastLineSelf = true;
            skipLine(); //effectivly skipps the next dialog line

        } else { //dialog wird nicht geskippt
            if (!currentLine.name.equals("self")) { //wenn nicht mehr self
                //System.out.println("DialogController4: SELF spricht nicht mehr" + currentDialogCode);
                lastLineSelf = false;

                if (true) { //Speicherung der aktuellen Line als LastLine
                    String name = currentLine.name;
                    lastLines.put(name, currentDialogCode);
                }
            }
            //displayResponseTextObject.sichtbarSetzen(true);
            if (currentLine.wahl2.equals("")) { //nur eine Wahl
                oneButtonMode = true;
                updateButtons();
                //System.out.println("DialogController4:");

            } else {
                oneButtonMode = false;
                updateButtons();
                //System.out.println("DialogController4:");
            }
            setConvText(currentLine.inhalt);
            setReplyText();

        }
    }

    private void skipLine() {
        DialogController4.DialogLine currentLine = dialogLines.get(currentDialogCode);
        //System.out.println("DialogController4: Eigener Dialog wird übersprungen während LastLineSelf=" + lastLineSelf);
        //System.out.println("->DialogController4: Sein Inhalt war: " + currentLine.inhalt);
        oneButtonMode = true;
        currentDialogCode = currentLine.wahl1; //skipped die nächste Zeile, weil dies
        displayCurrentDialogLine();
    }

    private void nextLine() {
        System.out.println("DialogController4: Es wurde nextLine() aufgerufen mit der line:" + currentDialogCode);
        if (!playingLastLine) { //falls gerade nicht eine Letzte Line abgespielt wird
            DialogController4.DialogLine currentLine = dialogLines.get(currentDialogCode);


            if (oneButtonMode) {
                buttonCursor = 0;
            }
            if (buttonCursor == 0) {
                //nextLine = dialogLines.get(currentLine.wahl1);
                currentDialogCode = currentLine.wahl1;
            }
            if (buttonCursor == 1) {
                //nextLine = dialogLines.get(currentLine.wahl2);
                currentDialogCode = currentLine.wahl2;
            }
            if (!currentLine.nextTime.equals("")) {
                System.out.println("NextTime des Dialogs ist nicht mehr leer, und deswegen wir beendet jz wird beendet und gehighlighted");
                endDialog();
                globalTemporalPosition = currentLine.nextTime;
                highLightReadyNpcs(); //updatet die Highlights
                NPC_Controller2.updateNpcPositions(globalTemporalPosition);
            } else {
                displayCurrentDialogLine();
            }


        } else {
            endDialog();
        }

    }

    private void saveLastLines() {
        //System.out.println("DialogController4: Die LastLines der NPCs werden im NPC_Controller gespeichert(NPCs-NEW.json)");
        for (String key : lastLines.keySet()) {
            String npcName = key;
            String code = lastLines.get(key);
            NPC_Controller2.setNpcLastLine(npcName, code);
        }
        lastLines.clear();
    }

    private void endDialog() {
        System.out.println("DialogController4: endDialog() aufgerufen");
        hideWindow();
        active = false;
        currentDialogCode = null; //kontorvers ob das hier Sinn macht
        waitingForInput = false;
        playingLastLine = false;
        //NPC_Controller2.prepareReset(); //setzt flag hig, damit Spieler nächsten Tick(in SPIEL.java) zurückgesetzt wird.
        NPC_Controller2.resetToLastQuietPos();
        //NPC_Controller2.updateNpcPositions(globalTemporalPosition);
        saveLastLines();
    }

    private void playLastLine(String npcID) {
        System.out.println("DialogController4: playLastLine() aufgerufen");
        oneButtonMode = true;
        waitingForInput = true;
        playingLastLine = true; //für die input klasse
        String lastLineID = NPC_Controller2.getNpcLastLine(npcID);
        //System.out.println("LastLineID: " + lastLineID);
        DialogController4.DialogLine lastLine = dialogLines.get(lastLineID);


        displayResponseTextObject.sichtbarSetzen(false);
        displayTextObject.sichtbarSetzen(true);
        displayDialogBackground.sichtbarSetzen(true);
        displayButtons[0].sichtbarSetzen(true);
        displayButtons[1].sichtbarSetzen(false);
        String inhalt;
        try {
            inhalt = lastLine.inhalt;
            setConvText(inhalt);
        } catch (Exception e) {
            System.out.println("DialogController4: FEHLER: Für diesem NPC gibt es scheinbar kein lastLine Eintrag");
            displayTextObject.inhaltSetzen("FEHLER! Für diesem NPC gibt es scheinbar kein lastLine Eintrag!");
        }
    }
    /*
    public void updateNpcPos(){
        Map<String, DialogPacket> packet = dialogPackets.get(globalTemporalPosition);
        for (String key : packet.keySet()) {
            String code = packet.get(key).code;
            String name = dialogLines.get(code).name; //name des ersten aktüres


            NPC_Controller2.updateNpcPos();
        }
        NPC_Controller2.updateNpcPos();
    }

     */

    private DialogController4.DialogPacket getPlayableDialogPacket(String npcID){
        DialogPacket returnPacket = null;
        if (dialogPackets.containsKey(globalTemporalPosition)) {
            Map<String, List<DialogController4.DialogPacket>> innnerPacketMap = dialogPackets.get(globalTemporalPosition); //gibt mir alle einträge zu einer Zeit
            if (innnerPacketMap.containsKey(npcID)) {
                List<DialogController4.DialogPacket> npcOccs = innnerPacketMap.get(npcID); //gibt mir alle Occs zu einem NPC
                List<String> foundItems = gameSaver.getItems(); //holt sich aus dem Savefile die momentanen Items
                List<String> readLines = gameSaver.getLines(); //holt sich aus dem Savefile die momentanen gelesenen Lines;

                for (DialogController4.DialogPacket packet : npcOccs) { //get also alle Packete einer Occ durch
                    if (foundItems.containsAll(packet.requiredItems)) {
                        if (readLines.containsAll(packet.requiredLines)) {
                            //System.out.println("DialogController4: Es sind alle nötigen Items und Zeilen vorhanden");
                            returnPacket = packet;
                        } else {
                            System.out.println("DialogController4: Es sind alle nötigen Items vorhanden, aber nicht alle Zeilen.");
                            //return false;
                        }
                    } else {
                        System.out.println("DialogController4: Es sind nicht alle nötigen Items gefunden worden");
                        //return false;
                    }

                }
                //System.out.println("DialogController4: FEHLER: Zu diesem NPC sind zwar Einträge aber keine Occs vorhanden");
                //return false;

            } else {
                System.out.println("DialogController4: Zu diesem NPC gibt es im Moment kein Eintrag in der Story");
                //return false;

            }
        } else {
            System.out.println("DialogController4: FEHLER: Zu diesem Zeitpunkt gibt es keinen Eintrag");
            //return false;
        }
        if(returnPacket == null){
            System.out.println("DialogController4: FEHLER: Obwohl erwartet gibt es kein DialogPacket was abgespielt werden, oder ist null");
        }

        return returnPacket;
    }


    public boolean isDialogPacketPlayable(String npcID) {
        if (dialogPackets.containsKey(globalTemporalPosition)) {
            Map<String, List<DialogController4.DialogPacket>> innnerPacketMap = dialogPackets.get(globalTemporalPosition); //gibt mir alle einträge zu einer Zeit
            if (innnerPacketMap.containsKey(npcID)) {
                List<DialogController4.DialogPacket> npcOccs = innnerPacketMap.get(npcID); //gibt mir alle Occs zu einem NPC
                List<String> foundItems = gameSaver.getItems(); //holt sich aus dem Savefile die momentanen Items
                List<String> readLines = gameSaver.getLines(); //holt sich aus dem Savefile die momentanen gelesenen Lines;

                for (DialogController4.DialogPacket packet : npcOccs) { //get also alle Packete einer Occ durch
                    if (foundItems.containsAll(packet.requiredItems)) {
                        if (readLines.containsAll(packet.requiredLines)) {
                            System.out.println("DialogController4: Es sind alle nötigen Items und Zeilen vorhanden");
                            return true;
                        } else {
                            System.out.println("DialogController4: Es sind alle nötigen Items vorhanden, aber nicht alle Zeilen.");
                            return false;
                        }
                    } else {
                        System.out.println("DialogController4: Es sind nicht alle nötigen Items gefunden worden");
                        return false;
                    }

                }
                System.out.println("DialogController4: FEHLER: Zu diesem Spieler sind zwar Einträge aber keine Occs vorhanden");
                return false;

            } else {
                System.out.println("DialogController4: Zu diesem Spieler gibt es im Moment kein Eintrag in der Story");
                return false;

            }
        } else {
            System.out.println("DialogController4: FEHLER: Zu diesem Zeitpunkt gibt es keinen Eintrag");
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
                    if (!playingLastLine) {
                        setReplyText();
                    }
                    break;
                case "rechts":
                    buttonCursor++;
                    if (buttonCursor > 1) {
                        buttonCursor = 1;
                    }
                    if (!playingLastLine) {
                        setReplyText();
                    }
                    break;
                case "enter":
                    nextLine();
                    break;

                default:
                    System.out.println("DialogController4: Kein valider Input");
            }
            updateButtons();
        } else {
            System.out.println("DialogController4: WARTET NICHT AUF INPUT");
        }
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

    private void setConvText(String text) {
        displayTextObject.inhaltSetzen(text);
        int width = (int) displayTextObject.getBreite();
        int posX_new = MAIN.x / 2 - width / 2;
        displayTextObject.positionSetzen(posX_new, textPosY);
    }

    private void setReplyText() {
        System.out.println("DialogController4: setReplyText() aufgerufen");
        displayResponseTextObject.sichtbarSetzen(false);
        DialogController4.DialogLine currentLine = dialogLines.get(currentDialogCode);
        //System.out.println("DialogController4: currentLine:" + currentLine.toString());
        if (currentLine.nextTime.equals("")) { //nur wenn der nächste dialog auch echt was beinhaltet
            if (isNextLineSelf(currentDialogCode)) {
                displayResponseTextObject.sichtbarSetzen(true);
            }
            if (lastLineSelf) {
                displayResponseTextObject.sichtbarSetzen(false);
            }
            if (playingLastLine) {
                displayResponseTextObject.sichtbarSetzen(false);
            }
            DialogController4.DialogLine nextLine = null;
            if (oneButtonMode) {
                buttonCursor = 0;
            }
            if (buttonCursor == 0) {
                nextLine = dialogLines.get(currentLine.wahl1);
                //System.out.println(nextLine.toString());
            }
            if (buttonCursor == 1) {
                nextLine = dialogLines.get(currentLine.wahl2);
            }

            displayResponseTextObject.inhaltSetzen(nextLine.inhalt);
            int width = (int) displayResponseTextObject.getBreite();
            int posX_new = MAIN.x / 2 - width / 2;
            displayResponseTextObject.positionSetzen(posX_new, textPosY - 50);
        } else { //das ist der Fall, dass der nächste Dialog nicht existieren sollte
            //nix, oder?
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

            Type MapType = new TypeToken<Map<String, DialogController4.DialogLine>>() {
            }.getType();
            dialogLines = gson.fromJson(bufferedReader, MapType);
            System.out.println();
            System.out.println(ANSI_GREEN + "DialogController4: JSON(" + dialogLinesPath + ")  erfolgreich gelesen" + ANSI_RESET);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "DialogController4: Ein Fehler beim Lesen der Json Datei(" + dialogLinesPath + " ). Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }

    }

    private void readJSON_DialogPackets() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(dialogPacketsPath));

            Type MapType = new TypeToken<Map<String, Map<String, List<DialogController4.DialogPacket>>>>() {
            }.getType();
            dialogPackets = gson.fromJson(bufferedReader, MapType);
            System.out.println(ANSI_GREEN + "DialogController4: JSON(" + dialogPacketsPath + ")  erfolgreich gelesen" + ANSI_RESET);
            //System.out.println("ANTWORT: " + dialogPackets.get("01").get("11").NpcID);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "DialogController4: Ein Fehler beim Lesen der Json Datei(" + dialogPacketsPath + " ). Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }
    }


    public boolean isPlayingLastLine() {
        return playingLastLine;
    }

    private boolean isNextLineSelf(String code) {
        //System.out.println("Schaut, ob die Line mit dem Code=" + code + " eine nächste hat die Self ist");
        String w = dialogLines.get(code).wahl1;
        //System.out.println("Der code der nächsten Line ist=" + w);
        DialogLine nextLine = dialogLines.get(w);
        if (nextLine == null) {
            System.out.println("DialogController4: FEHLER: Es die nächste Line ist null. Das bedeutet meistens ein Fehler in der Json");
        }
        String nextName = nextLine.name;
        return nextName.equals("self");
    }

    public String getCurrentDialogCode() {
        return currentDialogCode;
    }

    public int getButtonCursor() {
        return buttonCursor;
    }

    public boolean isOneButtonMode() {
        return oneButtonMode;
    }

    public boolean isLastLineSelf() {
        return lastLineSelf;
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

        @Override
        public String toString() {
            return "DialogLine{" +
                    "inhalt='" + inhalt + '\'' +
                    ", name='" + name + '\'' +
                    ", wahl1='" + wahl1 + '\'' +
                    ", wahl2='" + wahl2 + '\'' +
                    ", nextTime='" + nextTime + '\'' +
                    '}';
        }
    }

    public class DialogPacket {
        //key Time also "01!

        ArrayList<String> requiredItems;
        ArrayList<String> requiredLines;

        //NpcPosition npcPos;
        String code; // erster Code des Dialogs


    }


    public class NpcPosition {
        private String name;
        private float posX;
        private float posY;
        private int houseN;


        public NpcPosition(String name, int x, int y, int hn) {
            this.name = name;
            this.posX = x;
            this.posY = y;
            this.houseN = hn;
        }

        public float getPosX() {
            return posX;
        }

        public float getPosY() {
            return posY;
        }

        public int getHouseN() {
            return houseN;
        }

        public String getName() {
            return name;
        }

        /**
         * Don't use for now!
         * @return
         */
        public boolean isInHouse() {
            if (houseN > -1) {
                return true;

            } else {
                //hier landet man auch mit falschen Eingaben!!
                return false;
            }
        }
    }


}

