import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Knoten;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class NpcController extends Knoten {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    private HashMap<String, NpcController.NPC> NPCs; //für die Json
    private Npc npc1;
    private Npc[] NPC_List;

    public NpcController(){

        resetJSON();
        readJSON();

        System.out.println(NPCs.get("10"));
        //NPCs.get("10").setName("Test");
        saveGson();
        FillNPC_list();
    }

    /**
     * Speicht bei einem Spieler den Code, welcher zu letzt abgespielt wurde
     * @param player der Zahlencode des Spielers, bei dem man den Code speichern will; z.B. 10 oder 23
     * @param code der gesamte Code; z.B. 102345
     */
    public void setLastCode(int player, int code){
        String playerStringID = String.valueOf(player);
        NPCs.get(playerStringID).setLastCode(code);
        saveGson();
    }
    public int getLastCode(int player){
        readJSON();
        String playerStringID = String.valueOf(player);
        NpcController.NPC element = NPCs.get(playerStringID);
        return element.lastCode;
    }

    public boolean checkForCollision(Player AP, boolean isInHouse){
        boolean coll = false;
        //geht alle Npcs durch un schaut nach collision, aber nicht nach einer expliziten
        for (Npc n : NPC_List) {
            if(AP.schneidet(n)) {
                if(!isInHouse && (n.getHouseNumber() == -1)){ //sucht nicht im Haus und NPC nicht im Haus
                    coll= true;
                }
                if(isInHouse && n.getHouseNumber() != -1){
                    coll= true;
                }
            }
        }
        return coll;
    }
    public int getCollidingNPC(Player AP){
        boolean coll = false;
        int playerID = -1;
        for (int i=0;i<NPC_List.length;i++) {
            if(AP.schneidet(NPC_List[i])) {
                playerID = i;
            }
        }
        if(playerID == -1){
            System.out.println("NpcController: Komisch kein Player collided obwohl so vorausgesetzt");
        }
        return playerID;
    }

    private void FillNPC_list(){
        readJSON();
        NPC_List = new Npc[NPCs.size()];
        int i = 0;
        for (String key : NPCs.keySet()) {
            NpcController.NPC element = NPCs.get(key);
            String path = "./Assets/NPCs/"  + element.name + ".png";
            System.out.println("Neuer NPC names" + path);
            NPC_List[i] = new Npc(element.posX,element.posY,path, element.inHouse);
            if(element.inHouse == -1) { //in keinem Haus
                this.add(NPC_List[i]);
            }

            i++;

        }
    }
    private void saveGson(){
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileOutputStream fout = new FileOutputStream("./Assets/Files/NPCs.json");
            fout.write(gson.toJson(NPCs).getBytes());
            fout.close();

        }
        catch(Exception e) {
            System.out.println(ANSI_PURPLE + "NpcController: Ein Fehler beim Schreiben der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
        }
    }
    private void readJSON() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./Assets/Files/NPCs.json"));
            Type MapType = new TypeToken<HashMap<String, NpcController.NPC>>() {
            }.getType();
            NPCs = gson.fromJson(bufferedReader, MapType);
            System.out.println(ANSI_GREEN + "NpcController: JSON erfolgreich gelesen" + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(ANSI_PURPLE + "Map3: Ein Fehler beim Lesen der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "Map3: Eigentlich kann nur das Grafikteam schuld sein..." + ANSI_RESET);

        }

    }
    /**
     * Liest die JSON(NPCs-Temple.json) Datei und schreibt ihren Inhalt in den Json(NPCs.json).
     */
    private void resetJSON() {
        try {
            File originalFile = new File("./Assets/Files/NPCs-Template.json");
            File destinationFile = new File("./Assets/Files/NPCs.json");
            FileChannel src = new FileInputStream(originalFile).getChannel();
            FileChannel dest = new FileOutputStream(destinationFile).getChannel();
            dest.transferFrom(src, 0, src.size());

            System.out.println(ANSI_GREEN + "NpcController: JSON erfolgreich überschrieben" + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(ANSI_PURPLE + "NpcController: JSON erfolgreich überschrieben" + ANSI_RESET);

        }
    }

    class NPC{
        String name; //Klartext Name


        int lastCode; //letzter dialog der ausgesprochen werden soll;

        int posX;
        int posY;
        int inHouse;

        public void setName(String name) {
            this.name = name;
        }
        public void setLastCode(int code) {
            this.lastCode = code;
        }

    }
}
