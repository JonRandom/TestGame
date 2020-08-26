import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ea.Game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;

public class GameSaver {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    private Save saveState = new Save();


    /**
     * Setze defualt werte, dass ein JSON generiert wird ohne Lücken
     */
    public GameSaver()  {
        saveState.setName("DEFAULT");
        saveState.setPosX(0);
        saveState.setPosY(0);
        saveState.setWalkspeed(0);
        saveJSON();

    }

    private void saveJSON() {

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileOutputStream fout = new FileOutputStream("./Assets/Files/GameSave.json");
            fout.write(gson.toJson(saveState).getBytes());
            fout.close();

        }
        catch(Exception e) {
            System.out.println(ANSI_PURPLE + "Ein Fehler beim Lesen der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "Eigentlich kann nur jemand anderes schuld sein..." + ANSI_RESET);
        }

    }
    public void SavePlayer(Player Player) {
        //System.out.println("Saved Game");

        saveState.setName(Player.getName());
        saveState.setPosX((int)Player.getPosX());
        saveState.setPosY((int)Player.getPosY());
        saveState.setWalkspeed(Player.getWalkspeed());

        saveJSON();
    }

    /**
     * template klasse für die Speicherung von Daten in der JSON datei
     */
    class Save{
        public String name;
        public int posX;
        public int posY;
        public int walkspeed;


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
    }
}
