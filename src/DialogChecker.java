import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
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

    private final String npcFilePath = MAIN.npcTemplatePath;

    //JSON GSON
    private Map<String, DialogChecker.DialogLine> dialogLines; //für die Json mit den DialogZeilen
    private Map<String, Map<String, List<DialogChecker.DialogPacket>>> dialogPackets; //für die Json mit den DialogPackets
    private HashMap<String, NPC2> NPCs; //für die Json


    public DialogChecker() {
        System.out.println("DIALOGCHECKER GESTARTET");

        readJSON_DialogLines();
        readJSON_DialogPackets();
        readJSON_NPCs();
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
                        System.out.println("NextTime = " + dialogLine.nextTime);
                        System.out.println("getDialogPakcet = " + dialogPackets.get(dialogLine.nextTime));
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
        for(String timeCode : dialogPackets.keySet()){
            Map<String, List<DialogChecker.DialogPacket>> packetOccs = dialogPackets.get(timeCode);
            for(String npcName : packetOccs.keySet()){
                if(NPCs.containsKey(npcName)){
                    //Name ist richtig
                    //jetzt muss nach Occs gefiltert werden
                    //in den Occs muss nach Item und Line validität getestet werden
                }
                else{
                    System.out.println(ANSI_PURPLE + "DialogChecker: FEHLER: In dem DialogPacket (" + timeCode + ") ist der NPC names: " + npcName  + "vorhanden, dieser ist jedoch nicht in NPCs-NEW dabei");
                }

            }

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
}
