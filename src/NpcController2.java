import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ea.Knoten;
import ea.Punkt;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.util.*;

public class NpcController2 extends Knoten {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    //Pfade
    private String npcTemplatePath = "./Assets/Files/NPCs-Template.json";
    private String npcFilePath = "./Assets/Files/NPCs_NEW.json";
    private String npcPositionPath = "./Assets/Files/NPCs_Positions.json";


    //JSON GSON
    private HashMap<String, NPC2> NPCs; //für die Json
    private Map<String, Map<String, DialogController3.NpcPosition>> npcPositions; //für die Json mit den NPC Daten

    //for Position Reset
    private float lastQuietX = 0; //letzte Pos an der kein Dialog abgespielt wurde
    private float lastQuietY = 0;


    private Player AP;

    public NpcController2(Player mAP) {
        AP = mAP;
        resetJSON();
        readJSON();
        readNpcPositionJSON();
        //System.out.println("TESTETSTET POSX VON NAME: " + npcPositions.get("Tag 1 Abschnitt 1 (Start Zeit)").get("name").getPosX());
        addAllNPCs();

        leaveHouse(); //muss am anfang außen sein
        /*
        NPCs.get("01").verschieben(100,0);
        saveJSON();
         */
    }

    public boolean checkForCollision(Player AP) {
        boolean coll = false;
        //geht alle Npcs durch und schaut nach collision, aber nicht nach einer expliziten
        for (String key : NPCs.keySet()) {  //geht die JSON durch und
            NPC2 element = NPCs.get(key);   //stellt jedes Element der Map einmal als "element" zur Verfügung
            if (AP.schneidet(element) && element.sichtbar()) { //wenn der Spieler mit einem SICHTBAREN Npc kollidiert, dann weiter:
                //System.out.println("Spieler geschnitten und er ist sichtbar?? : " + element.sichtbar());
                coll = true;
            }
        }
        if (!coll) {
            lastQuietX = AP.getPosX();
            lastQuietY = AP.getPosY();
            //System.out.println("NpcController2; Der SPieler Kollidiert nicht, also wird die Pos gespeichert x = "  + lastQuietX);
        }
        return coll;
    }


    public String getCollidingNPC(Player AP) { //gibt den NPC KEY zurück mit dem geschnitten wurde

        String returnKey = null;
        for (String key : NPCs.keySet()) {  //geht die JSON durch und
            NPC2 element = NPCs.get(key);   //stellt jedes Element der Map einmal als "element" zur Verfügung
            if (AP.schneidet(element) && element.sichtbar()) { //wenn der Spieler mit einem SICHTBAREN Npc kollidiert, dann weiter:
                returnKey = key; // = current key
            }
        }
        if (returnKey == null) {
            System.out.println("NpcController2: Komisches Verhalten: Es wird nach expliziter Kollision gefragt, aber es gibt keine. Das kann für massive weitere Fehler sorgen");
            return null;
        } else {
            return returnKey; //gibt letzte Kollsion zurück
        }
    }

    public String getNpcLastLine(String npcID) {
        //System.out.println("NPC_Controller: Es wird nach dem DialogCode für den Spieler mit der ID-> gefragt: " + npcID);
        //System.out.println("NPC_Controller: Das NPC2 Objekt dazu sieht so aus: " + NPCs.get(npcID).toString());
        return NPCs.get(npcID).lastLine;
    }

    public void resetToLastQuietPos() {
        AP.positionSetzen(lastQuietX, lastQuietY);
    }


    public Punkt getLastQuietPos() {
        return new Punkt(lastQuietX, lastQuietY);
    }

    /**
     * Verschiebt die NPCs dieses Hauses an die entsprechende Position
     *
     * @param houseN  NouseNummer
     * @param offsetX Relle Postion der oberen ecke des Bildes
     * @param offsetY Relle Postion der oberen ecke des Bildes
     */
    public void enterHouse(int houseN, int offsetX, int offsetY) {
        hideAllNPCs();  //alle ausblenden
        //System.out.println("Der NPC Controller betritt auch ein Haus mit der Nummer: " + houseN);

        for (String key : NPCs.keySet()) {  //geht die JSON durch und
            NPC2 element = NPCs.get(key);   //stellt jedes Element der Map einmal als "element" zur Verfügung
            if (element.getHouseNumber() == houseN) {
                System.out.println("Der NPC names " + element.name + "ist im Haus sichtbar");
                element.sichtbarSetzen(true);
                element.positionSetzen(offsetX + (int) element.getPosX(), offsetY + (int) element.getPosY());

            }
        }
    }

    public void setNpcLastLine(String name, String lineCode) {
        System.out.println("NpcController2: für den NPC:" + name + "wird der Dialog mit dem Code: " + lineCode + " gespeichert");
        NPC2 npc = NPCs.get(name);
        npc.setLastLine(lineCode);
        saveJSON();
    }

    public void highLightNpcs(Set keySet) {
        for (String key : NPCs.keySet()) {  //geht die JSON durch und macht alle aus(false)
            NPC2 element = NPCs.get(key);   //stellt jedes Element der Map einmal als "element" zur Verfügung
            element.setHighlightState(false);
        }
        if (!(keySet == null)) {
            for (Object npcName : keySet) {
                System.out.println("NpcController2: Der Spieler mit dem Namen:" + npcName + " wird jetzt gehighlightet!");
                NPCs.get(npcName).setHighlightState(true);
            }
        } else {
            System.out.println("NpcController2: FEHLER: EIN DER HIGHLIGHT KEYSET GRUPPE IST LEER. DAS LIEGT WAHRSCHEINLICH DARAN, DASS ES KEINE WEITEREN DIALOGPACKETE FÜTR DIE NEUE ZEIT GIBT!");
        }

    }

    public void leaveHouse() {
        hideAllNPCs();

        //System.out.println("NpcController2: Haus wird verlassen");
        for (String key : NPCs.keySet()) {  //geht die JSON durch und
            NPC2 element = NPCs.get(key);   //stellt jedes Element der Map einmal als "element" zur Verfügung
            if (!element.isInHouse()) {
                //System.out.println("Npc_Controller2: Player ID -> sichtbar: " + element.name);
                element.sichtbarSetzen(true);
            }
        }
    }


    private void hideAllNPCs() {
        for (String key : NPCs.keySet()) {  //geht die JSON durch und
            NPC2 element = NPCs.get(key);   //stellt jedes Element der Map einmal als "element" zur Verfügung
            element.sichtbarSetzen(false);
        }
    }

    private void addAllNPCs() {
        for (String key : NPCs.keySet()) {  //geht die JSON durch und
            NPC2 element = NPCs.get(key);   //stellt jedes Element der Map einmal in "element" zur Verfügung
            this.add(element);
            //System.out.println("Elemente : " + element.name);
        }
    }

    /**
     * Speichert die aktuellen Daten in der Java Map "NPCs" in der JSON
     */
    private void saveJSON() {
        try {
            Writer writer = new FileWriter(npcFilePath);
            Gson gson = new GsonBuilder() //man könnte noch den gleichen gson von dem readJson nehmen, aber geht auch so
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            String json = gson.toJson(NPCs); //kriegt json inhalt zum schreiben gleich
            writer.write(json);
            writer.close(); //wichtig

            System.out.println(ANSI_GREEN + "NpcController2: JSON(./Assets/Files/NPCs_NEW.json) erfolgreich überschrieben" + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(ANSI_PURPLE + "NpcController2: Ein Fehler beim SCHREIBEN der Json Datei:" + ANSI_RESET);
            System.out.println(e);
        }

    }

    public void updateNpcPositions(String timePos) {
        Map<String, DialogController3.NpcPosition> positionsAtTime = npcPositions.get(timePos);
        if (!(positionsAtTime == null)) {
            for (String key : positionsAtTime.keySet()) {  //geht die JSON durch und macht alle aus(false)
                try {
                    DialogController3.NpcPosition posObject = positionsAtTime.get(key);
                    String name = key;
                    System.out.println("NpcController2: Die Position des NPCs mir dem Name (" + name + ") wird geupdatet");
                    NPC2 npc = NPCs.get(name);
                    if (!(npc == null)) {
                        //setzte Position und Hausnummer des NPCs mit dem entsprechenden Namen.
                        npc.positionSetzen(posObject.getPosX(), posObject.getPosY());
                        npc.setHouseNumber(posObject.getHouseN());
                    }else{
                        System.out.println("NpcController: FEHLER: NPC mit dem name (" + name + ") existiert nicht in der ursprünglichen JSON mit den NPCs");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("NpcController: FEHLER beim update der Positionen der NPCs");
                }

            }
        } else {
            System.out.println("NpcController: Für diese Zeit gibt es kein NPC Pos Update");
        }


    }

    /**
     * Liest die JSON(NPCs-Temple.json) Datei und schreibt ihren Inhalt in den Json(NPCs_NEW.json).
     */
    private void resetJSON() {
        try {
            File originalFile = new File(npcTemplatePath);
            File destinationFile = new File(npcFilePath);
            FileChannel src = new FileInputStream(originalFile).getChannel();
            FileChannel dest = new FileOutputStream(destinationFile).getChannel();
            dest.transferFrom(src, 0, src.size());

            System.out.println(ANSI_GREEN + "NpcController2: JSON erfolgreich aus Template überschrieben" + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(ANSI_PURPLE + "NpcController: Fehler beim Überschreiben der JSON aus Template:" + ANSI_RESET);
            e.printStackTrace();

        }
    }

    private void readJSON() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(NPC2.class, new NPC2.Deserializer())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(npcFilePath));
            Type MapType = new TypeToken<HashMap<String, NPC2>>() {
            }.getType();


            NPCs = gson.fromJson(bufferedReader, MapType);
            System.out.println(ANSI_GREEN + "NpcController2: JSON(" + npcFilePath + ") erfolgreich gelesen" + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "NpcController2: Ein Fehler beim Lesen der JSON(" + npcFilePath + ") Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }

    }


    private void readNpcPositionJSON() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(npcPositionPath));

            Type MapType = new TypeToken<Map<String, Map<String, DialogController3.NpcPosition>>>() {
            }.getType();
            npcPositions = gson.fromJson(bufferedReader, MapType);
            System.out.println(ANSI_GREEN + "NpcController2: JSON(" + npcPositionPath + ")  erfolgreich gelesen" + ANSI_RESET);
            //System.out.println("ANTWORT: " + dialogPackets.get("01").get("11").NpcID);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "NpcController2: Ein Fehler beim Lesen der Json Datei(" + npcPositionPath + " ). Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }

    }

}
