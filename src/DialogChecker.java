import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Knoten;
import ea.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogChecker {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    private final String dialogLinesPath = "./Assets/Files/Dialoge.json";
    private final String dialogPacketsPath = "./Assets/Files/DialogPackets.json";
    private final String itemInitFilePath = MAIN.itemInitFilePath;

    private final String npcFilePath = MAIN.npcTemplatePath;

    //JSON GSON
    private Map<String, DialogChecker.DialogLine> dialogLines; //für die Json mit den DialogZeilen
    private Map<String, Map<String, List<DialogChecker.DialogPacket>>> dialogPackets; //für die Json mit den DialogPackets
    private HashMap<String, NPC2> NPCs; //für die Json
    private List<DialogChecker.Item> items = new ArrayList<>();


    public DialogChecker() {
        System.out.println("DIALOGCHECKER GESTARTET");

        readJSON_DialogLines();
        readJSON_DialogPackets();
        readJSON_NPCs();
        readJSON_Item();
        System.out.println("ITEMS:" + items);

        checkEveryLine();
        checkEveryPacket();

    }

    public void checkEveryLine() {
        System.out.println(ANSI_GREEN + "DialogChecker: checkEveryLine() aufgerufen" + ANSI_RESET);
        for (String timeStamp : dialogLines.keySet()) {
            DialogLine dialogLine = dialogLines.get(timeStamp);
            if (NPCs.containsKey(dialogLine.name)) {
                //name passt
                if (!dialogLine.wahl1.equals("") && dialogLine.nextTime.equals("")) {
                    //erste Wahl nicht leer
                    if (dialogLines.containsKey(dialogLine.wahl1)) {
                        //erste Wahl ist ein echter Code
                        if (dialogLine.wahl2.equals("") || dialogLines.containsKey(dialogLine.wahl2)) {
                            //zweite Wahl ist leer oder hat einen vailden Code
                            if (dialogLine.wahl2.equals("") || dialogLines.containsKey(dialogLine.wahl2)) {
                                //erste Wahl ist ein echter Code
                                if (!dialogLine.inhalt.equals("")) {
                                    //return true;
                                    //Diese Zeile ist legit
                                } else {
                                    System.out.println(ANSI_PURPLE + "DialogChecker: FEHLER: In der DialogLine (" + timeStamp + ") ist der Inhalt leer");
                                }


                            } else {
                                System.out.println(ANSI_PURPLE + "DialogChecker: FEHLER: In der DialogLine (" + timeStamp + ") ist die wahl2 kein Code der sonst der nochmal irgendwo vorkommt. Falsche Verlinkung");
                            }


                        } else {
                            System.out.println(ANSI_PURPLE + "DialogChecker: FEHLER: In der DialogLine (" + timeStamp + ") ist die wahl2 leer, oder kein Code der sonst der nochmal irgendwo vorkommt. Falsche Verlinkung oder leeer!");
                        }

                    } else {
                        System.out.println(ANSI_PURPLE + "DialogChecker: FEHLER: In der DialogLine (" + timeStamp + ") ist die wahl1 kein Code der sonst der nochmal irgendwo vorkommt. Falsche Verlinkung!");
                    }

                } else if (!dialogLine.nextTime.equals("")) {
                    //line ist nicht leer
                    if (dialogPackets.containsKey(dialogLine.nextTime)){
                        //return true;
                        // alles passt
                    } else{
                        System.out.println(ANSI_PURPLE + "DialogChecker: FEHLER: In der DialogLine (" + timeStamp + ") zwar eine NextTime, aber diese ist nicht ein valider Code");
                        //System.out.println("NextTime = " + dialogLine.nextTime);
                        //System.out.println("getDialogPakcet = " + dialogPackets.get(dialogLine.nextTime));
                    }


                } else {
                    System.out.println(ANSI_PURPLE + "DialogChecker: FEHLER: In der DialogLine (" + timeStamp + ") ist die wahl1 leer und NextTime ist auch leer");
                }

            } else {
                System.out.println(ANSI_PURPLE + "DialogChecker: FEHLER: In der DialogLine (" + timeStamp + ") ist ein Namen, den es in NPCs-NEW nicht gibt!");
            }
        }
    }

    public void checkEveryPacket(){
        System.out.println(ANSI_GREEN + "DialogChecker: checkEveryPacket() aufgerufen" + ANSI_RESET);
        //private Map<String, Map<String, List<DialogChecker.DialogPacket>>> dialogPackets; //für die Json mit den DialogPackets
        //Map<String, List<DialogChecker.DialogPacket>> packetOccs; // = new HashMap<String, List<DialogChecker.DialogPacket>>();
        List<String> itemStringList = new ArrayList<String>();
        for(DialogChecker.Item s: items){
            itemStringList.add(s.name);
        }
        for(String timeCode : dialogPackets.keySet()){
            Map<String, List<DialogChecker.DialogPacket>> packetOccs = dialogPackets.get(timeCode);
            for(String npcName : packetOccs.keySet()){
                List<DialogChecker.DialogPacket> packets = packetOccs.get(npcName);
                if(NPCs.containsKey(npcName)){
                    //Name ist richtig
                    for(DialogChecker.DialogPacket packet : packets){
                        if(dialogLines.containsKey(packet.code)){
                            //Der Code ist vorhanden
//                            if(packet.requiredItems.stream().allMatch( -> items.contains(Item)))
                            if(itemStringList.containsAll(packet.requiredItems)){
                                //Alle Items sind für in der JSON gefunden
                                if(packet.forbiddenLines == null){
                                    //forbiddenLine false
                                }
                                else{
                                    for(String s: packet.forbiddenLines){
                                        //geht die forbiddenLines durch
                                        if(dialogLines.containsKey(s)){
                                            //Der Code ist vorhanden
                                            //return true;
                                        }
                                        else{
                                            System.out.println(ANSI_PURPLE + "DialogChecker: FEHLER: In dem DialogPacket (" + timeCode + ") ist der NPC names: " + npcName  + " vorhanden, aber eine forbiddenLine ist nicht in " + npcFilePath + " nicht vorhanden.");
                                        }

                                    }
                                }

                            }
                            else{
                                System.out.println(ANSI_PURPLE + "DialogChecker: FEHLER: In dem DialogPacket (" + timeCode + ") ist der NPC names: " + npcName  + " vorhanden, aber ein requiredItem in einem Packet ist nicht in " + itemInitFilePath + " nicht vorhanden.");
                            }
                        }
                        else{
                            System.out.println(ANSI_PURPLE + "DialogChecker: FEHLER: In dem DialogPacket (" + timeCode + ") ist der NPC names: " + npcName  + " vorhanden, aber ein Code in einem Packet ist nicht in " + npcFilePath + " nicht vorhanden.");
                        }
                    }

                    //jetzt muss nach Occs gefiltert werden
                    //in den Occs muss nach Item und Line validität getestet werden
                }
                else{
                    System.out.println(ANSI_PURPLE + "DialogChecker: FEHLER: In dem DialogPacket (" + timeCode + ") ist der NPC names: " + npcName  + " vorhanden, dieser ist jedoch nicht in NPCs-NEW dabei");
                }

            }

        }
    }


    private void readJSON_Item() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DialogChecker.Item.class, new DialogChecker.Deserializer())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(itemInitFilePath));
            Type ListType = new TypeToken<List<DialogChecker.Item>>() {
            }.getType();


            items = gson.fromJson(bufferedReader, ListType);
            System.out.println(ANSI_GREEN + "ItemController: JSON(" + itemInitFilePath + ") erfolgreich gelesen" + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "ItemController: Ein Fehler beim Lesen der JSON(" + itemInitFilePath + ") Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }
    }

    private void readJSON_DialogLines() {
        Gson gson = new Gson();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(dialogLinesPath));

            Type MapType = new TypeToken<Map<String, DialogChecker.DialogLine>>() {
            }.getType();
            dialogLines = gson.fromJson(bufferedReader, MapType);
            System.out.println();
            System.out.println(ANSI_GREEN + "DialogChecker: JSON(" + dialogLinesPath + ")  erfolgreich gelesen" + ANSI_RESET);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "DialogChecker: Ein Fehler beim Lesen der Json Datei(" + dialogLinesPath + " ). Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }

    }

    private void readJSON_DialogPackets() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(dialogPacketsPath));

            Type MapType = new TypeToken<Map<String, Map<String, List<DialogChecker.DialogPacket>>>>() {
            }.getType();
            dialogPackets = gson.fromJson(bufferedReader, MapType);
            System.out.println(ANSI_GREEN + "DialogChecker: JSON(" + dialogPacketsPath + ")  erfolgreich gelesen" + ANSI_RESET);
            //System.out.println("ANTWORT: " + dialogPackets.get("01").get("11").NpcID);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "DialogChecker: FEHLEE beim Lesen der Json Datei(" + dialogPacketsPath + " ). Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }
        System.out.println("DialogPacket:" + dialogPackets);
    }

    private void readJSON_NPCs() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(NPC2.class, new NPC2.Deserializer())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(npcFilePath));
            Type MapType = new TypeToken<HashMap<String, NPC2>>() {
            }.getType();


            NPCs = gson.fromJson(bufferedReader, MapType);
            System.out.println(ANSI_GREEN + "DialogChecker: JSON(" + npcFilePath + ") erfolgreich gelesen" + ANSI_RESET);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "DialogChecker: Ein Fehler beim Lesen der JSON(" + npcFilePath + ") Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }
        NPCs.put("self",null); //self als person
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

    public static class Item extends Knoten {

        private String mainPath = MAIN.itemsFilePath;

        private Bild img;

        @Expose
        private String name;
        @Expose
        private float posX;
        @Expose
        private float posY;
        @Expose
        private float relativePosX;
        @Expose
        private float relativePosY;
        @Expose
        private int houseN;
        @Expose
        private boolean visible;

        private Item(String n, float x, float y, float rX, float rY, int hn, boolean vb) {
            this.name = n;
            this.posX = x;
            this.posY = y;

            this.relativePosX = rX;
            this.relativePosY = rY;

            this.houseN = hn;
            this.visible = vb;

            try {
                String path = mainPath + name + ".png";
                img = new Bild(posX, posY, path);
                this.add(img);
                this.sichtbarSetzen(visible);
            } catch (Exception e) {
                System.out.println("Item: Fehler beim importieren der Datei");
                System.out.println("Item: " + e);
            }

        }

        private void showItem() {
            visible = true;
            this.sichtbarSetzen(true);
        }

        private void hideItem() {
            visible = false;
            this.sichtbarSetzen(false);
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "mainPath='" + mainPath + '\'' +
                    ", img=" + img +
                    ", name='" + name + '\'' +
                    ", posX=" + posX +
                    ", posY=" + posY +
                    ", relativePosX=" + relativePosX +
                    ", relativePosY=" + relativePosY +
                    ", houseN=" + houseN +
                    ", visible=" + visible +
                    '}';
        }
    }

    public static class Deserializer implements JsonDeserializer<DialogChecker.Item> {

        public DialogChecker.Item deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            JsonObject jsonObject = (JsonObject) jsonElement;
            return new DialogChecker.Item(jsonObject.get("name").getAsString(), jsonObject.get("posX").getAsInt(), jsonObject.get("posY").getAsInt(), jsonObject.get("relativePosX").getAsInt(), jsonObject.get("relativePosY").getAsInt(), jsonObject.get("houseN").getAsInt(), jsonObject.get("visible").getAsBoolean());
        }
    }
}
