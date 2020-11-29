import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ea.Game;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class GameSaver {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    private Save saveState = new Save();
    private final String gameSaveFilePath = MAIN.gameSaveFilePath;


    /**
     * Setze defualt werte, dass ein JSON generiert wird ohne Lücken
     */
    public GameSaver()  {
        readJSON();
        System.out.println("GameSaver: Erster Save TEST gelesen: " + saveState);


    }
    public void startNewGame(){
        try {
            readJSON();
            saveState.setName("DEFAULT");
            saveState.setPosX(1100);
            saveState.setPosY(1100);
            saveState.setWalkspeed(0);
            saveState.setHouseNumber(0);
            saveState.setLastOutsidePos(1000, 1000);
            saveState.setTemporalPosition("Tag 1 Abschnitt 1 (Start Zeit)");
            saveJSON();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("FETTER FEHLER BEIM ÜBERSCHREIBEN " + saveState);
        }
    }

    public void SavePlayer(Player Player) {
        //System.out.println("Saved Game");

        saveState.setName(Player.getName());
        saveState.setPosX((int)Player.getPosX());
        saveState.setPosY((int)Player.getPosY());
        saveState.setWalkspeed(Player.getWalkspeed());

        //saveJSON();

    }

    public List<String> getItems(){
        return saveState.items;
    }
    public void addItem(String item){
        saveState.items.add(item);
        saveJSON();
    }
    public void addLine(String code){
        saveState.lines.add(code);
        saveJSON();
    }

    public List<String> getLines() {
        return saveState.lines;
    }

    public void setTemporalPosition(String newTemporalPosition) {
        saveState.temporalPosition = newTemporalPosition;
        saveJSON();
    }
    public void setHouseNumber(int HouseNumber){
        saveState.setHouseNumber(HouseNumber);
    }
    public int getHouseNumber(){
        return saveState.houseNumber;
    }

    public int getPosX(){
        return saveState.posX;
    }
    public int getPosY(){
        return saveState.posY;
    }

    public void setLastOutsidePos(int x, int y){
        saveState.lastOutsidePosX = x;
        saveState.lastOutsidePosY = y;
        //saveJSON();
    }
    public int getLastOutsidePosX(){
        return saveState.lastOutsidePosX;
    }
    public int getLastOutsidePosY(){
        return saveState.lastOutsidePosY;
    }

    public String getTemporalPosition() {
        return saveState.temporalPosition;
    }


    private void readJSON() {
        Gson gson = new GsonBuilder().create();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(gameSaveFilePath));
            Type JsonType = new TypeToken<Save>() {
            }.getType();

            saveState = gson.fromJson(bufferedReader, JsonType);
            System.out.println(ANSI_GREEN + "GameSaver: JSON(" +  gameSaveFilePath + ") erfolgreich gelesen" + ANSI_RESET);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "GameSaver: Ein Fehler beim Lesen der Json Datei(" +  gameSaveFilePath + "). Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }
    }
    public void saveJSON() {

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileOutputStream fout = new FileOutputStream(gameSaveFilePath);
            fout.write(gson.toJson(saveState).getBytes());
            fout.close();
            System.out.println(ANSI_GREEN + "GameSaver: JSON(" +  gameSaveFilePath + ") erfolgreich gespeichert" + ANSI_RESET);
        }
        catch(Exception e) {
            System.out.println(ANSI_PURPLE + "Ein Fehler beim Schreiben der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
        }
        readJSON(); //update saveState

    }



    /**
     * template klasse für die Speicherung von Daten in der JSON datei
     */
    class Save{
        public String name;
        public int posX;
        public int posY;
        public int walkspeed;
        public int houseNumber;

        public int lastOutsidePosX;
        public int lastOutsidePosY;

        public String temporalPosition;
        public List<String> items;
        public List<String> lines;

        public Save(){
            items = new ArrayList<>();
            lines = new ArrayList<>();
            }

        public void setName(String name) {
            this.name = name;
        }

        public void setPosX(int posX) {
            this.posX = posX;
        }

        public void setPosY(int posY) {
            this.posY = posY;
        }

        public void setWalkspeed(int walkspeed) {
            this.walkspeed = walkspeed;
        }

        public void setHouseNumber(int houseNumber) {
            this.houseNumber = houseNumber;
        }

        public void setTemporalPosition(String temporalPosition) {
            this.temporalPosition = temporalPosition;
        }

        public void setLastOutsidePos(int x, int y){
            lastOutsidePosX = x;
            lastOutsidePosY = y;
        }
        @Override
        public String toString() {
            return "Save{" +
                    "name='" + name + '\'' +
                    ", posX=" + posX +
                    ", posY=" + posY +
                    ", walkspeed=" + walkspeed +
                    ", houseNumber=" + houseNumber +
                    ", lastOutSidePosX=" + lastOutsidePosX +
                    ", lastOutSidePosY=" + lastOutsidePosY +
                    ", temporalPosition='" + temporalPosition + '\'' +
                    ", items=" + items +
                    ", lines=" + lines +
                    '}';
        }
    }
}
