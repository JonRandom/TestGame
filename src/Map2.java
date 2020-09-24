import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.BoundingRechteck;
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
 * Aus der map Map kann mit dem Kex(numerisch) dann ein element gegriffen werden. In diesem Element sind dann die Gebäude-daten, nach der Struktur der Map.Haus Klasse
 *
 *
 */


public class Map2 extends Knoten {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    //Matrix für die Kollisiongebäude. in jedem eintrag sind 4 int für Obenx,Obeny, Untenx,UntenY
    private int NumberofB = -1;//is set in InitArrays()
    private int DoorLength = 40;//global DoorLength als in width/height
    private int DoorDepth = 14;//global DoorLength als in width/height
    private int DoorHysteresis = 20;//Hysterese, also Abstand den der Spieler von der Tür hat wenn er aus einem Haus kommt, oder gerade in eins hineingeht.

    private int[][] Buildings;
    private int[][] Doors;
    private int[][] InteriorDoors;
    private int[][] Interior;
    private Bild[] InteriorPics;
    private String[] DoorTyp;

    private Bild MapPic;
    private Bild BackgroundPic; //for when in the Interior
    private int PlayerW;
    private int PlayerH;

    private String defaultPath = "./Assets/Houses/"; //Basis-Pfad für alle interiorPics

    private int HouseNumber = 0;// zeigt an in welchem haus der Player ist. Entspricht der reinfolge des Jsons

    private HashMap<String, Map2.Haus> MAP;


    private ColliderShape[] OuterWallHitbox;
    private ColliderShape[] InteriorHitbox; //for not leaving the Interior
    private ColliderShape[] DoorHitbox;
    private ColliderShape[] InteriorDoorHitbox;

    private Rechteck[] Display_Buildings;
    private Rechteck[] Display_Interior;
    private Rechteck[] Display_Doors;
    private Rechteck[] Display_InteriorDoors;

    //Status, ob der Spieler sich in einem Gebäude befindet für func Walkable
    private boolean visiting = false;
    private boolean isInDoor = false;


    public Map2(float PW, float PH) {
        this.PlayerW = (int) PW;
        this.PlayerH = (int) PH;

        readJSON();//muss erst gelesen werden, um länge für Arrays zu geben
        InitArrays();

        InitMapPic(); //muss früh gemacht werden, damit die unten sind.

        FillArrays();
        InitBlankPic();


        CreateHitboxObjects();

        DisplayObjects();

    }


    /**
     * Erstellt die Arrays mit richtiger Länge
     */
    private void InitArrays() {
        NumberofB = MAP.size();

        Buildings = new int[NumberofB][4];
        Doors = new int[NumberofB][4];
        InteriorDoors = new int[NumberofB][4];
        Interior = new int[NumberofB][4];
        InteriorPics = new Bild[NumberofB];
        DoorTyp = new String[NumberofB];

        OuterWallHitbox = new ColliderShape[NumberofB];
        InteriorHitbox = new ColliderShape[NumberofB];
        DoorHitbox = new ColliderShape[NumberofB];
        InteriorDoorHitbox = new ColliderShape[NumberofB];

        Display_Doors = new Rechteck[NumberofB];
        Display_InteriorDoors = new Rechteck[NumberofB];
        Display_Buildings = new Rechteck[NumberofB];
        Display_Interior = new Rechteck[NumberofB];

        ;
    }

    /**
     * Füllt die Arrays mit allen Nötigen Daten aus dem Json und berechnet schon bestimmte Sachen.
     */
    private void FillArrays() {

        String path = "";
        int i = 0;
        for (String key : MAP.keySet()) {
            Map2.Haus element = MAP.get(key);


            Buildings[i][0] = element.posX;
            Buildings[i][1] = element.posY;
            Buildings[i][2] = element.width;
            Buildings[i][3] = element.height;
            //System.out.println("BuildingDaten: " + Buildings[i][0] +"," +Buildings[i][1] +"," +Buildings[i][2]+"," +Buildings[i][3]);
            //System.out.println("FillAray Durchgang:" +i+"doorTyp: " + element.doorTyp);
            System.out.println(element.doorTyp + " :element.doorTyp == l" + (element.doorTyp == "l"));
            if (element.doorTyp.equals("l")) { // falls die Tür an der Linken seite ist
                Doors[i][0] = element.posX - DoorDepth / 2;
                Doors[i][1] = element.posY + element.DoorOffset;
                Doors[i][2] = DoorDepth;
                Doors[i][3] = DoorLength;
                System.out.println("Door l" + Doors[i][0] + ", " + Doors[i][1] + ", " + Doors[i][2] + ", " + Doors[i][3]);
            } else if (element.doorTyp.equals("r")) {
                Doors[i][0] = element.posX - DoorDepth / 2 + element.width;
                Doors[i][1] = element.posY + element.DoorOffset;
                Doors[i][2] = DoorDepth;
                Doors[i][3] = DoorLength;
            } else if (element.doorTyp.equals("t")) {
                Doors[i][0] = element.posX + element.DoorOffset;
                Doors[i][1] = element.posY - DoorDepth / 2;
                Doors[i][2] = DoorLength;
                Doors[i][3] = DoorDepth;
            } else if (element.doorTyp.equals("b")) {
                Doors[i][0] = element.posX + element.DoorOffset;
                Doors[i][1] = element.posY - DoorDepth / 2 + element.height;
                Doors[i][2] = DoorLength;
                Doors[i][3] = DoorDepth;
            } else {
                System.out.println(ANSI_PURPLE + "Fehler in der Map2 Klasse: Eine Tür mit invalidem doorTyp wurde gefunden" + ANSI_RESET);
            }


            path = defaultPath + element.name + ".png";
            InteriorPics[i] = new Bild(0, 0, path);
            //Zentriert Bild anhand von der globalen Window Größe (MAIN.x, MAIN.y)

            InteriorPics[i].positionSetzen(0, 0);//zentriert das Bild
            InteriorPics[i].setOpacity(0.5f);
            InteriorPics[i].sichtbarSetzen(false);
            this.add(InteriorPics[i]);


            Interior[i][0] = element.InnerX;
            Interior[i][1] = element.InnerY;
            Interior[i][2] = element.InnerWidth - PlayerW;
            Interior[i][3] = element.InnerHeight - PlayerH;

            DoorTyp[i] = element.doorTyp;

            //eigentlich nur für die Tür Tiefe
            if (element.doorTyp.equals("l")) { // falls die Tür an der Linken seite ist
                InteriorDoors[i][0] = Interior[i][0] - DoorDepth / 2;
                InteriorDoors[i][1] = Interior[i][1] + element.InteriorDoorOffset ;
                InteriorDoors[i][2] = DoorDepth;
                InteriorDoors[i][3] = DoorLength;

            } else if (element.doorTyp.equals("r")) {
                InteriorDoors[i][0] = Interior[i][0] - DoorDepth / 2 + element.width;
                InteriorDoors[i][1] = Interior[i][1] + element.InteriorDoorOffset ;
                InteriorDoors[i][2] = DoorDepth;
                InteriorDoors[i][3] = DoorLength;
            } else if (element.doorTyp.equals("t")) {
                InteriorDoors[i][0] = Interior[i][0] + element.InteriorDoorOffset;
                InteriorDoors[i][1] = Interior[i][1] - DoorDepth / 2;
                InteriorDoors[i][2] = DoorLength;
                InteriorDoors[i][3] = DoorDepth;
            } else if (element.doorTyp.equals("b")) {
                InteriorDoors[i][0] = Interior[i][0] + element.InteriorDoorOffset;
                InteriorDoors[i][1] = Interior[i][1] - DoorDepth / 2 + element.height;
                InteriorDoors[i][2] = DoorLength;
                InteriorDoors[i][3] = DoorDepth;
            } else {
                System.out.println(ANSI_PURPLE + "Fehler in der Map2 Klasse: Eine Tür mit invalidem doorTyp wurde gefunden" + ANSI_RESET);
            }

            i++;
        }
    }


    /**
     * Macht aus den angegebene Positionen der Gebaüde ColliderShape Objekte und Türen.
     */
    private void CreateHitboxObjects() {
        for (int i = 0; i < NumberofB; i++) {
            OuterWallHitbox[i] = new ColliderShape(Buildings[i][0], Buildings[i][1], Buildings[i][2], Buildings[i][3]);
            InteriorHitbox[i] = new ColliderShape(Interior[i][0], Interior[i][1], Interior[i][2], Interior[i][3]);
            DoorHitbox[i] = new ColliderShape(Doors[i][0], Doors[i][1], Doors[i][2], Doors[i][3]);
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
    private void InitBlankPic(){
        BackgroundPic = new Bild(0,0,defaultPath + "BLANK.png");
        BackgroundPic.setOpacity(1f);
        BackgroundPic.sichtbarSetzen(false);
        this.add(BackgroundPic);
    }
    /**
     * Macht aus den angegebene Positionen der Gebaüde Rechteck Objekt und Türen.
     * Diese werden zum "this Knoten" hinzugefügt und somit angezeigt.
     */
    private void DisplayObjects() {
        for (int i = 0; i < NumberofB; i++) {
            Display_Buildings[i] = new Rechteck(Buildings[i][0], Buildings[i][1], Buildings[i][2], Buildings[i][3]);
            Display_Buildings[i].farbeSetzen(Color.gray);
            Display_Buildings[i].setOpacity(0.5f);//macht sie halbtransparent
            this.add(Display_Buildings[i]);

            Display_Doors[i] = new Rechteck(Doors[i][0], Doors[i][1], Doors[i][2], Doors[i][3]);
            Display_Doors[i].farbeSetzen(Color.blue);
            Display_Doors[i].setOpacity(0.5f);//macht sie halbtransparent
            Display_Doors[i].sichtbarSetzen(true);
            this.add(Display_Doors[i]);


            Display_Interior[i] = new Rechteck(Interior[i][0], Interior[i][1], Interior[i][2], Interior[i][3]);
            Display_Interior[i].farbeSetzen(Color.green);
            Display_Interior[i].setOpacity(0.5f);//macht sie halbtransparent
            Display_Interior[i].sichtbarSetzen(false);
            this.add(Display_Interior[i]);

            Display_InteriorDoors[i] = new Rechteck(InteriorDoors[i][0], InteriorDoors[i][1], InteriorDoors[i][2], InteriorDoors[i][3]);
            Display_InteriorDoors[i].farbeSetzen(Color.MAGENTA);
            Display_InteriorDoors[i].setOpacity(0.5f);//macht sie halbtransparent
            Display_InteriorDoors[i].sichtbarSetzen(false);
            this.add(Display_InteriorDoors[i]);



        }
    }

    /**
     * Ist der Spieler in einer Tür?
     * @param dp Dummyplayer für die POS
     * @return
     */
    public boolean isInDoor(DummyPlayer dp) {
        boolean hit = false;
        for (int i = 0; i < NumberofB; i++) {
            if (DoorHitbox[i].isIn(dp)) {
                hit = true;
            }
            ;
        }
        return hit;
    }


    /**
     * Sagt ob ein Dummyplayer der vorgeschickt laufen darf.
     * <p>
     * In Türen z.B darf man immer laufen.
     */
    public boolean getWalkable(DummyPlayer dp,Player AP) {
        //gUpdateInteriorPos(AP);
        if (!visiting) {
            boolean hit = false;
            for (int i = 0; i < NumberofB; i++) {
                if (OuterWallHitbox[i].isIn(dp)) {
                    hit = true;
                    if (isInDoor(dp)) {
                        System.out.println("Player beginnt gerade in Haus: " + getHouseNumber(dp) + "zu gehen");
                        enterHouse(HouseNumber, dp, AP);
                    }
                }
            }
            return !hit;//falls Kollision, false zurückgeben, sonst true

        } else {
            boolean hit = false;
            //int HN = getHouseNumber(dp);
            if (InteriorHitbox[HouseNumber].isIn(dp)) {
                hit = true;
            }
            return hit;//falls Kollision true zurückgeben, sonst false -> er soll nicht rauslaufen dürfen

        }
    }

    public int getHouseNumber(DummyPlayer dp) {
        //System.out.println("getHouseNumber() aufgerufen");
        HouseNumber = -1;
        for (int i = 0; i < NumberofB; i++) {
            boolean hit = false;
            if (OuterWallHitbox[i].isIn(dp)) {
                //System.out.println("getHouseNumber() erfolgreif mit " + i);
                HouseNumber = i;
            }
        }
        if(HouseNumber == -1){
            System.out.println("Map2: getHouseNumber() Spieler ist aber in keinem Haus");
        }
        return HouseNumber;

    }

    private void enterHouse(int HouseN,DummyPlayer dp,Player AP){
        //setVisiting(true);
        //AP.positionSetzen(1000,1000);
        FixInteriorPos(AP);
        HouseNumber = HouseN;
        BackgroundPic.sichtbarSetzen(true);
        Display_Interior[HouseN].sichtbarSetzen(true);
        InteriorPics[HouseN].sichtbarSetzen(true);
        Display_InteriorDoors[HouseN].sichtbarSetzen(true);

        System.out.println("Tür bei x: " + Display_InteriorDoors[HouseN].getX() + " y: " +  Display_InteriorDoors[HouseN].getY()  +
                " width: " +  Display_InteriorDoors[HouseN].getBreite() + " height: " +  Display_InteriorDoors[HouseN].getHoehe());
        /*
        if(DoorTyp[HouseNumber].equals("l")){
            AP.positionSetzen(1000,1000);
        }

         */




    }


    private void leaveHouse(){
        setVisiting(false);

    }

    private void FixInteriorPos(Player AP){
        System.out.println("Breite" + AP.getBreite());
        System.out.println("Höhe" + AP.getHoehe());
        float AP_x = AP.getPosX();
        float AP_y = AP.getPosY();

        int i = 0;
        for (String key : MAP.keySet()) {
            Map2.Haus element = MAP.get(key);


            //System.out.println("BuildingDaten: " + Buildings[i][0] +"," +Buildings[i][1] +"," +Buildings[i][2]+"," +Buildings[i][3]);
            //System.out.println("FillAray Durchgang:" +i+"doorTyp: " + element.doorTyp);

            float x = InteriorPics[i].getBreite();
            float y = InteriorPics[i].getHoehe();

            float centerX = AP_x - x / 2;
            float centerY = AP_y - y / 2;


            Interior[i][0] = (int)centerX; //Offset + PlayerBreite + Realposition der Oberen Ecke
            Interior[i][1] = (int)centerY; //Offset + PlayerHoehe + Realposition der Oberen Ecke
            Interior[i][2] = element.InnerWidth;
            Interior[i][3] = element.InnerHeight;

            InteriorHitbox[i] = new ColliderShape(Interior[i][0] + PlayerW +element.InnerX, Interior[i][1] +PlayerH+element.InnerY, Interior[i][2] -PlayerW*3, Interior[i][3] - PlayerH*3);

            InteriorPics[i].positionSetzen(Interior[i][0] , Interior[i][1]);//zentriert das Bild
            Display_Interior[i].positionSetzen(Interior[i][0] +element.InnerX, Interior[i][1]+element.InnerY);


            //System.out.println(element.doorTyp + " :element.doorTyp == l" + (element.doorTyp == "l"));
            if (element.doorTyp.equals("l")) { // falls die Tür an der Linken seite ist
                InteriorDoors[i][0] = Interior[i][0] - DoorDepth / 2;
                InteriorDoors[i][1] = Interior[i][1] + element.InteriorDoorOffset ;
                InteriorDoors[i][2] = DoorDepth;
                System.out.println("Doordepth" + DoorDepth);
                InteriorDoors[i][3] = DoorLength;

            } else if (element.doorTyp.equals("r")) {
                InteriorDoors[i][0] = Interior[i][0] - DoorDepth / 2 + element.width;
                InteriorDoors[i][1] = Interior[i][1] + element.InteriorDoorOffset ;
                InteriorDoors[i][2] = DoorDepth;
                InteriorDoors[i][3] = DoorLength;
            } else if (element.doorTyp.equals("t")) {
                InteriorDoors[i][0] = Interior[i][0] + element.InteriorDoorOffset;
                InteriorDoors[i][1] = Interior[i][1] - DoorDepth / 2;
                InteriorDoors[i][2] = DoorLength;
                InteriorDoors[i][3] = DoorDepth;
            } else if (element.doorTyp.equals("b")) {
                InteriorDoors[i][0] = Interior[i][0] + element.InteriorDoorOffset;
                InteriorDoors[i][1] = Interior[i][1] - DoorDepth / 2 + element.height;
                InteriorDoors[i][2] = DoorLength;
                InteriorDoors[i][3] = DoorDepth;
            } else {
                System.out.println(ANSI_PURPLE + "Fehler in der Map2 Klasse: Eine Tür mit invalidem doorTyp wurde gefunden" + ANSI_RESET);
            }
            //System.out.println("Door " +element.doorTyp + ": "+ InteriorDoors[i][0] + ", " + InteriorDoors[i][1] + ", " + InteriorDoors[i][2] + ", " + InteriorDoors[i][3]);
            Display_InteriorDoors[i].positionSetzen(InteriorDoors[i][0], InteriorDoors[i][1]);
            System.out.println("x: " + Display_InteriorDoors[i].getX() + "y: " + Display_InteriorDoors[i].getY());
            Display_InteriorDoors[i].sichtbarSetzen(true);

            i++;
        }

    }

    private void setVisiting(boolean visiting) {
        this.visiting = visiting;
    }

    public boolean isVisiting() {
        return visiting;
    }


    /**
     * an wenn aus, aus wenn an
     * muss public sein
     */
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
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./Assets/Files/Karte.json"));

            Type MapType = new TypeToken<HashMap<String, Map2.Haus>>() {
            }.getType();
            MAP = gson.fromJson(bufferedReader, MapType);
        } catch (Exception e) {
            System.out.println(ANSI_PURPLE + "Map2: Ein Fehler beim Lesen der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "Map2: Eigentlich kann nur das Grafikteam schuld sein..." + ANSI_RESET);

        }

    }

    public class Haus {
        String name;
        int posX;
        int posY;

        int width;
        int height;

        int DoorOffset;
        int InteriorDoorOffset;

        int InnerX; //relative to InnerPic
        int InnerY; //relative to InnerPic
        int InnerWidth;
        int InnerHeight;


        String doorTyp; // r,l,t,b


    }
}

