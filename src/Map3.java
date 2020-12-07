import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Game;
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
 */


public class Map3 extends Knoten {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    private int numberofB = -1;//is set in InitArrays()

    private int PlayerW;
    private int PlayerH;

    private boolean visiting;
    private int houseNumber = -1;

    private ImageCollider mapHitbox; // map Kollider
    private Bild mapImg; //Main anzeigeBild
    private Bild backgroundImg;


    private String defaultPath = "./Assets/Houses/"; //Basis-Pfad für alle interiorPics
    private String pathHitboxImg = "./Assets/Tests/Map3_coll.png";
    private String pathMainImg = "./Assets/Map3.png";
    private String pathBackgroundImg = "./Assets/Tests/blur.png";

    private HashMap<String, Map3.Haus> MAP; //für die Json

    private ImageCollider[] houseHitbox;
    private Bild[] houseImgs; //Anzeigebilder der Innenraüme
    private int[] RedColorCodes; // Array mit allen HäuserRedCodes;
    private int[][] intSpawnPos; //da wo der Spieler im Inneren, wenn er durch die Tür geht spawnt ->s. JSON Template-Klasse(Haus) //[i][0] = x | [i][1] = y
    private boolean[] defaultLock; //->s. JSON Template-Klasse(Haus)


    //bestimmte init Zahl, um beim Start nicht diese zu nehmen
    private int lastWhiteX = -1;
    private int lastWhiteY = -1;

    //für den start des Spiels um pos zu fixen
    private float lastFinalPosX;
    private float lastFinalPosY;


    private NpcController2 npc_C;
    private SoundController sound_c;
    private GameSaver gamesaver;
    private Player player;
    private ItemController itemC;

    private int blackThreshold = MAIN.blackThreshold; //lower threshold for "black";


    public Map3(NpcController2 NPC_C, SoundController sc, Player ap, GameSaver gs, ItemController iC) {
        this.npc_C = NPC_C;
        this.sound_c = sc;
        this.player = ap;
        this.gamesaver = gs;
        this.itemC = iC;

        this.PlayerW = (int) player.getBreite();
        this.PlayerH = (int) player.getHoehe();
        houseNumber = gs.getHouseNumber(); //init HN

        readJSON();//muss erst gelesen werden, um länge für Arrays zu geben


        InitArrays();
        FillArrays();

        loadStartPos();
    }


    /**
     * Erstellt die Arrays mit richtiger Länge
     */
    private void InitArrays() {
        numberofB = MAP.size();

        houseHitbox = new ImageCollider[numberofB];
        houseImgs = new Bild[numberofB];
        RedColorCodes = new int[numberofB];
        intSpawnPos = new int[numberofB][2];//[i][0] = x | [i][1] = y
        defaultLock = new boolean[numberofB];
    }

    /**
     * Füllt die Arrays & variablen mit allen Nötigen Daten aus dem Json und berechnet schon bestimmte Sachen.
     */
    private void FillArrays() {

        try {
            mapImg = new Bild(0, 0, pathMainImg);
            mapImg.setOpacity(0.5f);
            this.add(mapImg);
        } catch (Exception e) {
            System.out.println("Fehler in der Map Klasse: Bild bei " + pathMainImg + " kann nicht gefunden werden!");
            System.out.println(e);
        }

        try {
            backgroundImg = new Bild(0, 0, pathBackgroundImg);
            backgroundImg.setOpacity(1f);
            backgroundImg.sichtbarSetzen(false);
            this.add(backgroundImg);
        } catch (Exception e) {
            System.out.println("Fehler in der Map Klasse: Bild bei " + pathBackgroundImg + " kann nicht gefunden werden!");
            System.out.println(e);
        }


        mapHitbox = new ImageCollider(pathHitboxImg); // Main Hitbox Collider für die Karte
        this.add(mapHitbox);

        int i = 0;
        for (String key : MAP.keySet()) {
            Map3.Haus element = MAP.get(key);

            String tempPath = defaultPath + element.name + ".png";
            String tempCollPath = defaultPath + element.name + "_coll.png";


            try {
                houseImgs[i] = new Bild(0, 0, tempPath);
                houseImgs[i].sichtbarSetzen(false);
                this.add(houseImgs[i]);
            } catch (Exception e) {
                System.out.println("Fehler in der Map Klasse: Bild bei " + tempPath + " kann nicht gefunden werden!");
                System.out.println(e);
            }

            houseHitbox[i] = new ImageCollider(tempCollPath); // Main Hitbox Collider für die Karte
            houseHitbox[i].sichtbarSetzen(false);
            this.add(houseHitbox[i]);

            //hexColorCodes[i] = element.hexColorCode;
            intSpawnPos[i][0] = element.intSpawnX;
            intSpawnPos[i][1] = element.intSpawnY;
            defaultLock[i] = element.defaultLock;
            RedColorCodes[i] = element.RedColorCode;
            i++;
        }
        System.out.println("MAP3, ARRAYS SIND GEFÜLLT");
    }

    /**
     * Aus der AP Position wird die Mitte des Bildschirms berechnet und dort wird das bild positioniert.
     */

    public void loadStartPos() {
        System.out.println("Map3: Aus den gespeicherten GameSave Daten wird die startPos des Spielers gelesen.");
        int houseN = gamesaver.getHouseNumber();
        leaveHouse();
        player.positionSetzen(gamesaver.getPosX(), gamesaver.getPosY());
        if (houseN != -1) {
            System.out.println("Aus loadStartPos mit Hausnummer -> betreten:" + houseN);
            enterHouse(houseN);
            // redundant wird schon in FoxPos aufgerufen npc_C.enterHouse(houseN, (int)lastFinalPosX, (int)lastFinalPosY);
        }

    }

    public void FixInteriorPos(Player AP, int HouseN) {
        float imgWidth = houseImgs[HouseN].getBreite();
        float imgHeight = houseImgs[HouseN].getHoehe();
        float finalPosX = AP.getPosX() - imgWidth / 2;
        float finalPosY = AP.getPosY() - imgHeight / 2;
        lastFinalPosX = finalPosX;
        lastFinalPosY = finalPosY;

        houseHitbox[HouseN].setOffset((int) finalPosX, (int) finalPosY);
        houseImgs[HouseN].positionSetzen(finalPosX, finalPosY);
        npc_C.enterHouse(HouseN, (int) finalPosX, (int) finalPosY);
        itemC.enterHouse(HouseN, (int) finalPosX, (int) finalPosY);
        //System.out.println("OFFSET GESTZT IN KARTE: " + (int)AP_x + "," + (int)AP_y);
        AP.positionSetzen(intSpawnPos[HouseN][0] + finalPosX, intSpawnPos[HouseN][1] + finalPosY);//setzt den Spieler an die Pos wo er spawnen soll

    }

    public String getOffsetPosString() {
        //System.out.println("getOffsetPosString()");
        String returnText;
        if (houseNumber == -1) {
            returnText = "ist in keinem Haus!";
        } else {
            float relativeX = player.getPosX() - lastFinalPosX;
            float relativeY = player.getPosY() - lastFinalPosY;
            returnText = "x: " + relativeX + " | y: " + relativeY;
        }
        return returnText;
    }

    public void enterHouse(int HouseN) {
        this.sichtbarSetzen(false);
        backgroundImg.sichtbarSetzen(true);

        if (lastWhiteX == -1) {
            //soll die InitDaten aus der Gamesave Json nicht überschreiben
        } else {
            gamesaver.setLastOutsidePos(lastWhiteX, lastWhiteY);
        }
        gamesaver.setHouseNumber(HouseN);
        System.out.println("Map3: Spieler betritt Haus Nummer: " + HouseN);
        sound_c.playDoorSound();
        hideAllHouses();
        //blur background Image Pos
        int x = player.positionX();
        int y = player.positionY();
        backgroundImg.mittelpunktSetzen(x, y);

        //rest
        houseImgs[HouseN].sichtbarSetzen(true);
        houseHitbox[HouseN].sichtbarSetzen(true);
        FixInteriorPos(player, HouseN);
        houseNumber = HouseN; //set global HouseNumber for collision
        visiting = true;
        //backgroundImg.positionSetzen(player.getPosX() - MAIN.x/2, player.getPosY()-MAIN.y/2);

        this.sichtbarSetzen(true); //nach allem wieder einblenden
    }

    public void leaveHouse() {
        gamesaver.setHouseNumber(-1);
        lastWhiteX = gamesaver.getLastOutsidePosX();
        lastWhiteY = gamesaver.getLastOutsidePosY();
        sound_c.playDoorSound();
        visiting = false;
        houseNumber = -1;
        hideAllHouses();
        backgroundImg.sichtbarSetzen(false);
        player.positionSetzen(lastWhiteX, lastWhiteY);

        npc_C.leaveHouse();
        itemC.leaveHouse();

    }

    public void hideAllHouses() {
        for (int i = 0; i < numberofB; i++) {
            houseImgs[i].sichtbarSetzen(false);
            houseHitbox[i].sichtbarSetzen(false);
        }
    }

    public Color decodeHex(int hexC) {
        //int hex = 0x123456;
        int r = (hexC & 0xFF0000) >> 16;
        int g = (hexC & 0xFF00) >> 8;
        int b = (hexC & 0xFF);

        return new Color(r, g, b);
    }

    //wird verwendet
    public boolean isWalkable2(DummyPlayer dp, Player ap) {
        if (!visiting) {
            ImageCollider.ColliderReturnType result = mapHitbox.scanSurrounding(dp);

            //FRAGLICH: Er darf nun auch laufen, wenn der nächste schritt im Schwarzen liegt aber auch in einer Farbe.
            if (!result.isInColor()) {
                // er halt also KEINE eine Farbe besucht
                if (result.isBlack()) {
                    //er nicht darf laufen, weil er mal im Schwarzen war;
                    //System.out.println("Darf nicht laufen");
                    return false;
                }
                if (result.isWhite()) {
                    //er darf laufen, weil er nur im Weißen war;
                    lastWhiteX = ap.positionX(); //AP ODER DP NO LÖSEN
                    lastWhiteY = ap.positionY(); //AP ODER DP NO LÖSEN
                    //System.out.println(lastWhiteX +", "  + lastWhiteY);
                    return true;
                } else {
                    System.out.println("Map3: Komisches Ergebniss der Farbanalyse - PRÜFEN");
                    return false;
                }
            } else {
                // er halt also eine Farbe besucht
                int match = -1;
                //System.out.println(numberofB);
                for (int i = 0; i < numberofB; i++) {
                    //System.out.println("Sucht nach Häusern die matchen bei i = " + i);
                    //System.out.println("Eingetragene Farbe bei i ist " + RedColorCodes[i]);
                    //System.out.println("aktuelle Farbe = " + result.getColor());
                    if (result.getColor() == RedColorCodes[i]) {
                        match = i;
                    }
                }
                if (match == -1) {
                    //Farbe hat mit keiner der Tabelle gematched
                    System.out.println("Map3: Der Spieler ist in einer Farbe die nicht in der Tabelle eingetragen wurde!");
                    System.out.println("Map3: Gruß an das Grafikteam!");
                    return false;
                } else {
                    enterHouse(match);
                    return true;
                }

            }

        } else {
            //somit visiting = 1;
            //er besucht also gerade ein Haus

            ImageCollider.ColliderReturnType result2 = houseHitbox[houseNumber].scanSurrounding(dp);
            if (result2.isOutOfBounds()) {
                leaveHouse();
                return false;
            } else if (result2.isBlack()) {
                return false;
            } else if (result2.isWhite()) {
                return true;
            } else {
                System.out.println("Map3: Komisches Ergebniss der Farbanalyse beim Besuchen (visiting=true)- PRÜFEN");
                return false;
            }

        }
    }

    public int getHouseNumber() {
        //System.out.println("GET HOUSE NUMBER AUFGERUFEN");
        return houseNumber;
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
            System.out.println(ANSI_PURPLE + "Map3: Ein Fehler beim Lesen der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "Map3: Eigentlich kann nur das Grafikteam schuld sein..." + ANSI_RESET);

        }

    }

    /**
     * JF:
     * Das ist die Klasse die als Muster zum Auslesen des JSON (mit GSON) dient.
     * Alle Methoden hierdrinn sind also nur für eine Haus im allgemeinen verwendbar und selten brauchbar.
     * Eigentlich muss in dieser Klasse nicht geändert werden
     */
    public class Haus {
        String name; //Klartext Name
        int RedColorCode; //farbe des Innenraums 0-255 als 8bit farbe

        int intSpawnX; //da wo der Spieler im Inneren, wenn er durch die Tür geht spawnt, relativ zum Bild des Inneren
        int intSpawnY; //da wo der Spieler im Inneren, wenn er durch die Tür geht spawnt, relativ zum Bild des Inneren

        boolean defaultLock; //ist das Haus am anfang gelockt?
    }
}

