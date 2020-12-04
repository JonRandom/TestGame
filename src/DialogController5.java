import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Farbe;
import ea.Knoten;
import ea.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

public class DialogController5 extends Knoten {
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

    //sub-Objects
    private final NpcController2 NPC_Controller2;
    private final GameSaver gameSaver;

    //JSON GSON
    private Map<String, DialogController5.DialogLine> dialogLines; //für die Json mit den DialogZeilen
    private Map<String, Map<String, List<DialogController5.DialogPacket>>> dialogPackets; //für die Json mit den DialogPackets

    //VISIBLE STUFF;
    private String defaultPath = "./Assets/Dialoge/";
    private Text displayTextObject;
    private Bild displayDialogBackgroundLeft;
    private Bild displayDialogBackgroundRight;

    //Text Stuff
    private final int textPosY = 630;
    private final int defaultTextSize = 30;
    private final int maxTextWidth = 800;

    //DIALOG LINE STUFF;
    private String currentDialogCode;
    private String lastDialogCode;

    //Dialog Weg selection
    private int selection = 0;

    //playingLastLine
    private boolean playingLastLine = false;


    //lastLine
    private Map<String, String> lastLines = new HashMap<String, String>() {
    }; //<NAME, INHALT>

    //GESCHIHCTER DIE ANGEZEIGT WERDEN
    private Map<String, Bild> npcFaces = new HashMap<String, Bild>() {
    }; //<NAME, INHALT>

    private final int faceLocationX = 100;
    private final int faceLocationY = 620;
    private final int selfFaceLocationX = MAIN.x - faceLocationX;


    public DialogController5(NpcController2 NPC_C2, GameSaver gs) {
        this.NPC_Controller2 = NPC_C2;
        this.gameSaver = gs;
        //initialisert
        readJSON_DialogLines();
        readJSON_DialogPackets();

        addDisplayObjects();
        hideWindow();

        globalTemporalPosition = gameSaver.getTemporalPosition();
        NPC_Controller2.updateNpcPositions(globalTemporalPosition);
    }

    /**
     * Fügt alle sichtbaren Elemente zum Knoten zu und regelt Bildimporte
     */
    private void addDisplayObjects() {

        try { //Bilder mit try catch
            displayDialogBackgroundLeft = new Bild(0, 0, defaultPath + "DialogFensterLeft.png");
            displayDialogBackgroundRight = new Bild(0, 0, defaultPath + "DialogFensterRight.png");
            displayDialogBackgroundLeft.sichtbarSetzen(false);
            displayDialogBackgroundRight.sichtbarSetzen(false);
            this.add(displayDialogBackgroundLeft, displayDialogBackgroundRight);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "DialogController5: FEHLER beim Importieren der Bilder" + ANSI_RESET);
        }

        //NPC Faces
        HashMap<String, NPC2> NpcMap = NPC_Controller2.getNPCs();
        Bild selfFace = new Bild(selfFaceLocationX, faceLocationY, MAIN.playerStillImgPath);
        npcFaces.put("self", selfFace);
        this.add(selfFace);
        for (String name : NpcMap.keySet()) {
            try {
                Bild tempImg = new Bild(faceLocationX, faceLocationY, MAIN.npcFacesPath + name + ".png");
                this.add(tempImg);
                tempImg.sichtbarSetzen(false);
                npcFaces.put(name, tempImg);
                System.out.println("DialogController5: Neues Gesicht hinzugefügt mit dem name: " + name);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("DialogController5: FEHLER beim Importieren der Gesicht-Bilder");
            }
        }

        displayTextObject = new Text(MAIN.x / 2, textPosY, "DEFAULT TEXT");
        displayTextObject.positionSetzen(MAIN.x / 2 - displayTextObject.getBreite(), textPosY); //zentriert Default Text
        displayTextObject.farbeSetzen(new Farbe(0, 0, 0));
        displayTextObject.groesseSetzen(defaultTextSize);
        this.add(displayTextObject);
    }


    /**
     * Main Methode die auch von SPIEL aufgerufen wird.
     *
     * @param npcID String ID des NPCs
     */
    public void startDialog(String npcID) { //Voraussetztung Kollision mit NPC und activ=false;

        playingLastLine = false;
        waitingForInput = true;
        active = true;
        if (isDialogPacketPlayable(npcID)) {
            //start eines neuen Dialogpacketes
            DialogController5.DialogPacket element = getPlayableDialogPacket(npcID);
            currentDialogCode = element.code;
            lastDialogCode = currentDialogCode;

            showWindow();
            displayDialogLine(currentDialogCode);
        } else { //Es wird kein Dialogpacket für diesen NPC gefunden worden
            System.out.println("DialogController4: WÜRDE JZ LASTLINE SPIELEN MACHT ES ABER AUS TESTGRÜNDEN NOCH NICHT");
            playLastLine(npcID);
        }
    }

    public void highLightReadyNpcs() {
        NPC_Controller2.disguiseAllNPCs();
        for (String name : NPC_Controller2.getNPCs().keySet()) {
            if (isDialogPacketPlayable(name)) {
                NPC_Controller2.highLightNpcsByName(name);
            }
        }
    }


    public void setNpcFace(String npcName) {
        hideAllFaces();
        if (npcName.equals("self")) {
            //System.out.println("SELF SPRICHT");
        }
        //System.out.println("Der NPC mit dem Namen = " + npcName + " wird neben dem Fenster als FACE abgebildet");
        npcFaces.get(npcName).sichtbarSetzen(true);
    }

    public void hideAllFaces() {
        for (String name : npcFaces.keySet()) {
            npcFaces.get(name).sichtbarSetzen(false);
        }
    }

    private void saveLastLines() {
        //System.out.println("DialogController5: Die LastLines der NPCs werden im NPC_Controller gespeichert(NPCs-NEW.json)");
        for (String npcName : lastLines.keySet()) {
            if(npcName.equals("self")){
                //überspringe diese Line
            }else{
                String code = lastLines.get(npcName);
                NPC_Controller2.setNpcLastLine(npcName, code);
            }

        }
        lastLines.clear();
    }


    private void endDialog() {
        active = false;
        waitingForInput = false;

        NPC_Controller2.resetToLastQuietPos();
        System.out.println("END DIALOG");
        currentDialogCode = null;
        hideWindow();
        saveLastLines();

    }

    private void playLastLine(String npcID) {
        playingLastLine = true;
        System.out.println("DialogController5: playLastLine() aufgerufen");
        DialogLine lastLine = dialogLines.get(NPC_Controller2.getNpcLastLine(npcID));
        if (lastLine == null) {
            updateTextContent("FEHLER DER NPC HAT KEINE LASTLINE");
        } else {
            displayDialogLine(NPC_Controller2.getNpcLastLine(npcID));
        }
    }

    public void nextLine() {
        if (!playingLastLine) {
            System.out.println("DialogController5: Es wird die Zeile mit den Code: " + currentDialogCode + " abgespeichert!");
            gameSaver.addLine(currentDialogCode); //speicher alle Abgespielen Lines in der JSON
            lastDialogCode = currentDialogCode;
            int wahl = selection + 1;//index 0 fix zu index 1
            DialogLine currentLine = dialogLines.get(currentDialogCode);
            if (currentLine.hasNextTime()) {
                globalTemporalPosition = currentLine.nextTime;
                System.out.println("Der Dialog wird jz beendet mit und es wird zur Zeit: " + currentDialogCode + " gewechselt!");
                endDialog();
            } else {
                if(currentLine.hasNoChoice()){
                    System.out.println("Bei dem Dialog wird mit der 1.Wahl weitergemacht weile er keine Wahl hat");
                    currentDialogCode = currentLine.wahl1;
                }
                else if (wahl == 1) {
                    //hat nic bei wahl2 dirnstehen
                    System.out.println("Bei dem Dialog wird mit der 1.Wahl weitergemacht bei slection: " + selection);
                    currentDialogCode = currentLine.wahl1;
                } else if(wahl == 2){
                    System.out.println("Bei dem Dialog wird mit der 2.Wahl weitergemacht bei slection: " + selection);
                    //wenn es eine 2.Wahl gibt und wahl != 1
                    currentDialogCode = currentLine.wahl2;
                }
                displayDialogLine(currentDialogCode);
            }
        } else { //isPlayingLastLine
            endDialog();
        }

    }

    private Stream<DialogPacket> getPlayableDialogs(String npcID) {
        return dialogPackets
                .getOrDefault(globalTemporalPosition, Collections.emptyMap())
                .getOrDefault(npcID, Collections.emptyList())
                .stream()
                .filter(
                        packet ->
                                gameSaver.getItems().containsAll(packet.requiredItems) &&
                                        gameSaver.getLines().containsAll(packet.requiredLines) &&
                                        inverseContains(gameSaver.getLines(), packet.forbiddenLines)

                );
    }

    private DialogController5.DialogPacket getPlayableDialogPacket(String npcID) {
        return getPlayableDialogs(npcID)
                .findFirst()
                .orElse(null);
    }

    public boolean isDialogPacketPlayable(String npcID) {
        return getPlayableDialogs(npcID)
                .findAny()
                .isPresent();
    }

    public void showWindow() {
        displayTextObject.sichtbarSetzen(true);
        displayDialogBackgroundLeft.sichtbarSetzen(true);
        displayDialogBackgroundRight.sichtbarSetzen(true);
    }

    public void hideWindow() {
        hideAllFaces();
        displayTextObject.sichtbarSetzen(false);
        displayDialogBackgroundLeft.sichtbarSetzen(false);
        displayDialogBackgroundRight.sichtbarSetzen(false);
    }


    public void displayDialogLine(String lineCode) {
        DialogLine dL = dialogLines.get(lineCode);
        setNpcFace(dL.name);
        setDialogWindowDir(dL.isSelf());
        updateTextContent(dL.inhalt);
        System.out.println("DialogController5: Zeigt jetzt die Zeile: " + lineCode + " an!");
        lastLines.put(dL.name, lineCode); //self wird auch mitgespeicher und später rausgenommen
    }

    public void updateTextContent(String inhalt) {
        displayTextObject.sichtbarSetzen(true);
        displayTextObject.inhaltSetzen(inhalt);
        displayTextObject.groesseSetzen(defaultTextSize);
        while (displayTextObject.getBreite() > maxTextWidth) {
            displayTextObject.groesseSetzen(displayTextObject.groesse() - 1);
        }
        float textPosX = MAIN.x / 2 - displayTextObject.getBreite() / 2;
        displayTextObject.positionSetzen(textPosX, textPosY);
    }


    public void setDialogWindowDir(boolean isSelf) {
        displayDialogBackgroundLeft.sichtbarSetzen(false);
        displayDialogBackgroundRight.sichtbarSetzen(false);
        if (!isSelf) {
            //links wenn nicht selber
            displayDialogBackgroundLeft.sichtbarSetzen(true);
            //System.out.println("DialogController: DISPLAY: es wird das nach Links angezeigt");
        } else {
            //rechts wenn selber
            displayDialogBackgroundRight.sichtbarSetzen(true);
            //System.out.println("DialogController: DISPLAY: es wird das nach Rechts angezeigt");
        }


    }

    public void input(String dir) {
        if (isWaitingForInput()) {
            switch (dir) {
                case "links":
                    selection--;
                    if (selection < 0) {
                        selection = 0;
                    }
                    if(!dialogLines.get(lastDialogCode).hasNoChoice()){
                        //bei dem Dialog gibt es nicht keine Wahl, es gibt also eine!
                        String thisCode = dialogLines.get(lastDialogCode).wahl1;
                        displayDialogLine(thisCode);
                    }

                    break;

                case "rechts":
                    selection++;
                    if (selection > 1) {
                        selection = 1;
                    }
                    if(!dialogLines.get(lastDialogCode).hasNoChoice()){
                        //bei dem Dialog gibt es nicht keine Wahl, es gibt also eine!
                        String thisCode = dialogLines.get(lastDialogCode).wahl2;
                        displayDialogLine(thisCode);
                    }

                    break;

                case "enter":
                    nextLine();
                    break;

                default:
                    System.out.println(ANSI_PURPLE + "DialogController4: FEHLER Kein valider Input" + ANSI_RESET);

            }
        } else {
            System.out.println(ANSI_PURPLE + "DialogController4: FEHLER: wartet nicht auf Input" + ANSI_RESET);
        }
    }


    public String getGlobalTemporalPosition() {
        return globalTemporalPosition;
    }

    public boolean isWaitingForInput() {
        return waitingForInput;
    }

    public String getCurrentDialogCode() {
        return currentDialogCode;
    }

    public static <T> boolean inverseContains(List<T> a, List<T> b) {
        if (b == null) {
            //nur ForbiddenItems leer
            return true;
        } else if (a == null) {
            return false;
        } else {
            return a.stream().noneMatch(b::contains);
        }

    }

    public boolean isActive() {
        return active;
    }

    public int getSelection() {
        return selection;
    }

    //JSON SACHEN UND DEREN KLASSEN
    private void readJSON_DialogLines() {
        Gson gson = new Gson();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(dialogLinesPath));

            Type MapType = new TypeToken<Map<String, DialogController5.DialogLine>>() {
            }.getType();
            dialogLines = gson.fromJson(bufferedReader, MapType);
            System.out.println();
            System.out.println(ANSI_GREEN + "DialogController5: JSON(" + dialogLinesPath + ")  erfolgreich gelesen" + ANSI_RESET);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "DialogController5: Ein Fehler beim Lesen der Json Datei(" + dialogLinesPath + " ). Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }

    }

    private void readJSON_DialogPackets() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(dialogPacketsPath));

            Type MapType = new TypeToken<Map<String, Map<String, List<DialogController5.DialogPacket>>>>() {
            }.getType();
            dialogPackets = gson.fromJson(bufferedReader, MapType);
            System.out.println(ANSI_GREEN + "DialogController5: JSON(" + dialogPacketsPath + ")  erfolgreich gelesen" + ANSI_RESET);
            //System.out.println("ANTWORT: " + dialogPackets.get("01").get("11").NpcID);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "DialogController5: FEHLEE beim Lesen der Json Datei(" + dialogPacketsPath + " ). Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }
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

        public boolean isSelf() {
            return (name.equals("self"));
        }

        public boolean hasNextTime() {
            //System.out.println("HAS NEXT TIME AUFGERUFEN: ANTWORT: " + !nextTime.equals(""));
            return (!nextTime.equals(""));
        }

        public boolean hasNoChoice() {
            return (wahl2.equals(""));
        }

    }

    public class DialogPacket {
        //key Time also "01!

        ArrayList<String> requiredItems;
        ArrayList<String> requiredLines;
        ArrayList<String> forbiddenLines;

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
         *
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

