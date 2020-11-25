import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Knoten;
import ea.Punkt;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class NpcController2 extends Knoten {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    private HashMap<String, NPC2> NPCs; //für die Json

    //for Position Reset
    private float lastQuietX = 0; //letzte Pos an der kein Dialog abgespielt wurde
    private float lastQuietY = 0;


    private Player AP;
    public NpcController2(Player mAP) {
        AP = mAP;
        resetJSON();
        readJSON();
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

    public void resetToLastQuietPos(){
        AP.positionSetzen(lastQuietX,lastQuietY);
    }


    public Punkt getLastQuietPos(){
        return new Punkt(lastQuietX,lastQuietY);
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
    public void setNpcLastLine(String name, String lineCode){
        System.out.println("NpcController2: für den NPC:"  + name  + "wird der Dialog mit dem Code: " + lineCode + " gespeichert");
        NPC2 npc = NPCs.get(name);
        npc.setLastLine(lineCode);
        saveJSON();
    }
    public void highLightNpcs(Set keySet){
        for (String key : NPCs.keySet()) {  //geht die JSON durch und macht alle aus(false)
            NPC2 element = NPCs.get(key);   //stellt jedes Element der Map einmal als "element" zur Verfügung
            element.setHighlightState(false);
        }
        if(!(keySet == null)){
            for (Object npcName : keySet) {
                System.out.println("NpcController2: Der Spieler mit dem Namen:"  + npcName  +" wird jetzt gehighlightet!");
                NPCs.get(npcName).setHighlightState(true);
            }
        } else{
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
            Writer writer = new FileWriter("./Assets/Files/NPCs_NEW.json");
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

    /**
     * Liest die JSON(NPCs-Temple.json) Datei und schreibt ihren Inhalt in den Json(NPCs_NEW.json).
     */
    private void resetJSON() {
        try {
            File originalFile = new File("./Assets/Files/NPCs-Template.json");
            File destinationFile = new File("./Assets/Files/NPCs_NEW.json");
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
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./Assets/Files/NPCs_NEW.json"));
            Type MapType = new TypeToken<HashMap<String, NPC2>>() {
            }.getType();

            /*
            HashMap<String, NPC2> TestMAP = new HashMap<String, NPC2>();
            TestMAP.put("01", new NPC2(100,100,1,"paul"));
            TestMAP.put("01", new NPC2(200,100,1,"erika"));
            String json = gson.toJson(TestMAP);
            System.out.println("JSON INHALT: " + json);
            HashMap<String, NPC2> TestMAP2 = gson.fromJson(json, MapType);
            this.add(TestMAP2.get("01"));

             */
            NPCs = gson.fromJson(bufferedReader, MapType);
            System.out.println(ANSI_GREEN + "NpcController2: JSON(./Assets/Files/NPCs_NEW.json) erfolgreich gelesen" + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "NpcController2: Ein Fehler beim Lesen der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }

    }

}
