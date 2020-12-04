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

    //Dialog Weg selection
    private int selection = 0;


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
        waitingForInput = true;
        active = true;
        if (isDialogPacketPlayable(npcID)) {
            //start eines neuen Dialogpacketes
            DialogController5.DialogPacket element = getPlayableDialogPacket(npcID);
            currentDialogCode = element.code;
            showWindow();
            updateTextContent(dialogLines.get(currentDialogCode));
        } else { //Es wird kein Dialogpacket für diesen NPC gefunden worden
            System.out.println("DialogController4: WÜRDE JZ LASTLINE SPIELEN MACHT ES ABER AUS TESTGRÜNDEN NOCH NICHT");
            //playLastLine(npcID);
        }
    }

    public void highLightReadyNpcs() {
        NPC_Controller2.disguiseAllNPCs();
        if (dialogPackets.containsKey(globalTemporalPosition)) {
            Map<String, List<DialogController5.DialogPacket>> innnerPacketMap = dialogPackets.get(globalTemporalPosition);
            Set keys = innnerPacketMap.keySet();
            for (String npcName : innnerPacketMap.keySet()) { //get die NPC durch
                List<DialogController5.DialogPacket> npcOccs = innnerPacketMap.get(npcName); //gibt mir alle Occs zu einem NPC
                List<String> foundItems = gameSaver.getItems(); //holt sich aus dem Savefile die momentanen Items
                List<String> readLines = gameSaver.getLines(); //holt sich aus dem Savefile die momentanen gelesenen Lines;

                for (DialogController5.DialogPacket packet : npcOccs) { //get also alle Packete einer Occ durch
                    if (foundItems.containsAll(packet.requiredItems)) {
                        if (readLines.containsAll(packet.requiredLines)) {
                            //System.out.println("DialogController4: Es sind alle nötigen Items und Zeilen vorhanden");
                            if (packet.forbiddenLines == null) {
                                System.out.println("DialogController4: Es sind alle nötigen Items und Zeilen vorhanden und keine Lines verboten für den NPC: " + npcName);
                                //System.out.println(;
                                NPC_Controller2.highLightNpcsByName(npcName);
                            } else if (inverseContains(packet.forbiddenLines, readLines)) {
                                //es gibt forbiddenLines, meine sind aber nicht dabei
                                System.out.println("DialogController4: Das Dialogpacket hat zwar verbotene Lines, unsere sind aber nicht dabei für den NPC: " + npcName);
                                NPC_Controller2.highLightNpcsByName(npcName);
                            } else {
                                System.out.println("DialogController4: Das Dialogpacket ist verboten!");
                                //nix machen

                            }
                        } else {
                            System.out.println("DialogController4: highLightReadyNpcs: Es sind alle nötigen Items vorhanden, aber nicht alle Zeilen für den NPC: " + npcName);
                            //return false;
                        }
                    } else {
                        System.out.println("DialogController4: highLightReadyNpcs(): Es sind nicht alle nötigen Items gefunden worden");
                        //return false;

                    }

                }

            }
        } else {
            System.out.println("DialogController4: FEHLER BEIM HIGHLIGHTEN, DIE NÄCHSTE ZEIT GIBT ES GAR NICHT");
            System.out.println("DialogController4: Die Zeit POS ist:" + globalTemporalPosition);
            NPC_Controller2.highLightNpcs(null);
        }

    }


    public void setNpcFace(String npcName) {
        hideAllFaces();
        if(npcName.equals("self")){
            System.out.println("SELF SPRICHT");
        }
        System.out.println("Der NPC mit dem Namen = " + npcName + " wird neben dem Fenster als FACE abgebildet");
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
            String code = lastLines.get(npcName);
            NPC_Controller2.setNpcLastLine(npcName, code);
        }
        lastLines.clear();
    }


    private void endDialog() {
        NPC_Controller2.resetToLastQuietPos();
        System.out.println("END DIALOG");
        currentDialogCode = null;
        hideWindow();
        active = false;
    }

//    private void playLastLine(String npcID) {
//        System.out.println("DialogController5: playLastLine() aufgerufen");
//        oneButtonMode = true;
//        waitingForInput = true;
//        playingLastLine = true; //für die input klasse
//        String lastLineID = NPC_Controller2.getNpcLastLine(npcID);
//        //System.out.println("LastLineID: " + lastLineID);
//        DialogController5.DialogLine lastLine = dialogLines.get(lastLineID);
//
//
//        displayTextObject.sichtbarSetzen(true);
//        displayDialogBackground.sichtbarSetzen(true);
//
//        String inhalt;
//        try {
//            inhalt = lastLine.inhalt;
//            setConvText(inhalt);
//            setNpcFace(lastLine.name);
//        } catch (Exception e) {
//            System.out.println(ANSI_PURPLE + "DialogController5: FEHLER: Für diesem NPC gibt es scheinbar kein lastLine Eintrag" + ANSI_RESET);
//            displayTextObject.inhaltSetzen("FEHLER! Für diesem NPC gibt es scheinbar kein lastLine Eintrag!");
//        }
//    }

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


    public void nextLine() {
        int wahl = selection + 1;//index 0 fix zu index 1
        DialogLine currentLine = dialogLines.get(currentDialogCode);
        if (currentLine.hasNextTime()) {
            endDialog();
        }
        else {
            if (currentLine.hasNoChoice() || wahl == 1) {
                //hat nic bei wahl2 dirnstehen
                currentDialogCode = currentLine.wahl1;
            } else {
                //wenn es eine 2.Wahl gibt und wahl != 1
                currentDialogCode = currentLine.wahl2;
            }
            updateTextContent(dialogLines.get(currentDialogCode));
        }
    }


    public void updateTextContent(DialogLine dL) {
        setNpcFace(dL.name);
        setDialogWindowDir(dL.isSelf());
        displayTextObject.sichtbarSetzen(true);
        displayTextObject.inhaltSetzen(dL.inhalt);
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
            System.out.println("DialogController: DISPLAY: es wird das nach Links angezeigt");
        } else {
            //rechts wenn selber
            displayDialogBackgroundRight.sichtbarSetzen(true);
            System.out.println("DialogController: DISPLAY: es wird das nach Rechts angezeigt");
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
                    break;

                case "rechts":
                    selection++;
                    if (selection > 1) {
                        selection = 1;
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
            System.out.println("HAS NEXT TIME AUFGERUFEN: ANTWORT: " + !nextTime.equals(""));
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

