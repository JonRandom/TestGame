import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Knoten;

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

    private int numberofB = -1;//is set in InitArrays()

    private int PlayerW;
    private int PlayerH;

    private boolean visiting;

    private ImageCollider mapHitbox; // map Kollider
    private Bild mapImg; //Main anzeigeBild


    private String defaultPath = "./Assets/Houses/"; //Basis-Pfad für alle interiorPics
    private String pathHitboxImg = "./Assets/Tests/Map3_coll.png";
    private String pathMainImg = "./Assets/Map3.jpg";

    private HashMap<String, Map3.Haus> MAP; //für die Json

    private ImageCollider[] houseHitbox;
    private Bild[] houseImgs; //Anzeigebilder der Innenraüme
    private int[] hexColorCodes; // Array mit allen HäuserHexCodes;
    private int[][] intSpawnPos; //da wo der Spieler im Inneren, wenn er durch die Tür geht spawnt ->s. JSON Template-Klasse(Haus) //[i][0] = x | [i][1] = y
    private boolean[] defaultLock; //->s. JSON Template-Klasse(Haus)


    public Map3(float PW, float PH) {
        this.PlayerW = (int) PW;
        this.PlayerH = (int) PH;

        readJSON();//muss erst gelesen werden, um länge für Arrays zu geben

        InitArrays();
        FillArrays();


    }


    /**
     * Erstellt die Arrays mit richtiger Länge
     */
    private void InitArrays() {
        numberofB = MAP.size();

        houseHitbox = new ImageCollider[numberofB];
        houseImgs = new Bild[numberofB];
        hexColorCodes = new int[numberofB];
        intSpawnPos = new int[numberofB][2];//[i][0] = x | [i][1] = y
        defaultLock = new boolean[numberofB];
    }

    /**
     * Füllt die Arrays & variablen mit allen Nötigen Daten aus dem Json und berechnet schon bestimmte Sachen.
     */
    private void FillArrays() {

        try{
            mapImg = new Bild(0,0,pathMainImg);
            mapImg.setOpacity(0.5f);
            this.add(mapImg);
        }catch(Exception e){
            System.out.println("Fehler in der Map Klasse: Bild bei " + pathMainImg + " kann nicht gefunden werden!");
            System.out.println(e);
        } //import MainDisplayImage wird auch zum Knoten geadded.

        mapHitbox = new ImageCollider(pathHitboxImg); // Main Hitbox Collider für die Karte
        this.add(mapHitbox);

        int i = 0;
        for (String key : MAP.keySet()) {
            Map3.Haus element = MAP.get(key);

            String tempPath = defaultPath + element.name +".png";
            String tempCollPath = defaultPath + element.name +"_coll.png";


            try{
                houseImgs[i] = new Bild(0,0,tempPath);
                houseImgs[i].setOpacity(1f);
                this.add(houseImgs[i]);
            }catch(Exception e){
                System.out.println("Fehler in der Map Klasse: Bild bei " + tempPath + " kann nicht gefunden werden!");
                System.out.println(e);
            }

            houseHitbox[i] = new ImageCollider(tempCollPath); // Main Hitbox Collider für die Karte
            this.add(houseHitbox[i]);

            //hexColorCodes[i] = element.hexColorCode;
            intSpawnPos[i][0] = element.intSpawnX;
            intSpawnPos[i][1] = element.intSpawnY;
            defaultLock[i] = element.defaultLock;
            i++;
        }
        System.out.println("MAP3, ARRAYS SIND GEFÜLLT");
    }


    /**
     * Aus der AP Position wird die Mitte des Bildschirms berechnet und dort wird das bild positioniert.
     */
    public void FixInteriorPos(Player AP, int HouseN){


        float imgWidth = houseImgs[HouseN].getBreite();
        float imgHeight = houseImgs[HouseN].getHoehe();
        float finalPosX = AP.getPosX() - imgWidth/2;
        float finalPosY = AP.getPosY() - imgHeight/2;
        //float centerX = AP_x - MAIN.x/2;
        //float centerY = AP_y - MAIN.y/2;

        houseHitbox[HouseN].setOffset((int)finalPosX,(int)finalPosY);
        houseImgs[HouseN].positionSetzen(finalPosX,finalPosY);
        //System.out.println("OFFSET GESTZT IN KARTE: " + (int)AP_x + "," + (int)AP_y);
        AP.positionSetzen(intSpawnPos[HouseN][0] + finalPosX,intSpawnPos[HouseN][1] + finalPosY);//setzt den Spieler an die Pos wo er spawnen soll

    }

    public Color decodeHex(int hexC){
        //int hex = 0x123456;
        int r = (hexC & 0xFF0000) >> 16;
        int g = (hexC & 0xFF00) >> 8;
        int b = (hexC & 0xFF);

        return new Color(r, g, b);
    }

    public boolean isWalkable(DummyPlayer dp, Player ap){
        if(!visiting){
            return mapHitbox.AllowWalk(dp);
        }
        else{
            return houseHitbox[0].AllowWalk(dp);
        }
    }

    public boolean isVisiting() {
        return visiting;
    }
    public void toggleVisting() {
        if (visiting) {
            visiting = false;
        } else {
            visiting = true;
        }
    }

    private void readJSON() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./Assets/Files/KarteNew.json"));

            Type MapType = new TypeToken<HashMap<String, Map3.Haus>>() {
            }.getType();
            MAP = gson.fromJson(bufferedReader, MapType);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(ANSI_PURPLE + "Map2: Ein Fehler beim Lesen der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "Map2: Eigentlich kann nur das Grafikteam schuld sein..." + ANSI_RESET);

        }

    }

    public class Haus {
        String name; //Klartext Name
        String hexColorCode; //farbe des Innenraums z.B //0x123456 nur temp String!!!!

        int intSpawnX; //da wo der Spieler im Inneren, wenn er durch die Tür geht spawnt, relativ zum Bild des Inneren
        int intSpawnY; //da wo der Spieler im Inneren, wenn er durch die Tür geht spawnt, relativ zum Bild des Inneren

        boolean defaultLock; //ist das Haus am anfang gelockt?
    }
}

