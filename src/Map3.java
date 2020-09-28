import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Knoten;
import ea.Rechteck;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Diese Klasse managed die Karte und die Rechtecke der Gebäude,
 * die im Hintergrund die Kollisionerkennung leiten.
 * <p>
 * JSON Lesen Erklärung:
 * Erst wird aus der TemplateKlasse Haus(Map.Haus) eingestellt und dann wird die Datei zu der java.utils.map names MAP gewandelt.
 * Aus der map Map kann mit dem Key(numerisch) dann ein element gegriffen werden. In diesem Element sind dann die Gebäude-daten, nach der Struktur der Map.Haus Klasse.
 *
 *
 */


public class Map3 extends Knoten {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    private int NumberofB = -1;//is set in InitArrays()

    private int PlayerW;
    private int PlayerH;

    private ImageColliderTest mapHitboxTester;
    private Bild mapHitboxDisplayImg;
    private Bild mapImg; //for when in the Interior


    private String defaultPath = "./Assets/Houses/"; //Basis-Pfad für alle interiorPics

    private HashMap<String, Map3.Haus> MAP;

    private ImageColliderTest[] houseHitboxTester; // ICT Objekte zum Testen.
    private Bild[] houseHitboxDisplayImgs; // greyscale Bilder mit Wänden und Co.
    private Bild[] houseImgs; //Anzeigebilder der Innenraüme


    public Map3(float PW, float PH) {
        this.PlayerW = (int) PW;
        this.PlayerH = (int) PH;

        readJSON();//muss erst gelesen werden, um länge für Arrays zu geben
        InitArrays();

        InitMapPic(); //muss früh gemacht werden, damit die unten sind.

        FillArrays();


    }


    /**
     * Erstellt die Arrays mit richtiger Länge
     */
    private void InitArrays() {
        NumberofB = MAP.size();

        []

        ;
    }

    /**
     * Füllt die Arrays mit allen Nötigen Daten aus dem Json und berechnet schon bestimmte Sachen.
     */
    private void FillArrays() {

        int i = 0;
        for (String key : MAP.keySet()) {
            Map3.Haus element = MAP.get(key);

            element.name;

            i++;
        }
    }




    /**
     * Erstellt die beiden "statischen" Bilder und bindet sie ein. Das muss früh geschehen, damit sie weit hinten sind.
     */
    private void InitMapPic(){
        MapPic = new Bild(0, 0, "./Assets/Map3.jpg");
        MapPic.setOpacity(1f);
        this.add(MapPic);

    }






    private void FixInteriorPos(Player AP){

    }

    public Color decodeHex(int hexC){
        //int hex = 0x123456;
        int r = (hexC & 0xFF0000) >> 16;
        int g = (hexC & 0xFF00) >> 8;
        int b = (hexC & 0xFF);

        return new Color(r, g, b);
    }




    private void readJSON() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./Assets/Files/KarteNew.json"));

            Type MapType = new TypeToken<HashMap<String, Map3.Haus>>() {
            }.getType();
            MAP = gson.fromJson(bufferedReader, MapType);
        } catch (Exception e) {
            System.out.println(ANSI_PURPLE + "Map2: Ein Fehler beim Lesen der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "Map2: Eigentlich kann nur das Grafikteam schuld sein..." + ANSI_RESET);

        }

    }

    public class Haus {
        String name; //Klartext Name
        int hexColorCode; //farbe des Innenraums z.B 0x123456

        int spawnX; //da wo der Spieler, wenn er durch die Tür geht spawnt
        int spawnY; //da wo der Spieler, wenn er durch die Tür geht spawnt

        boolean defaultLock; //ist das Haus am anfang gelockt?
    }
}

