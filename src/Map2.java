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
 *
 * JSON Lesen Erklärung:
 * Erst wird aus der TemplateKlasse Haus(Map.Haus) eingestellt und dann wird die Datei zu der java.utils.map names MAP gewandelt.
 * Aus der map Map kann mit dem Kex(numerisch) dann ein element gegriffen werden. In diesem Element sind dann die Gebäude-daten, nach der Struktur der Map.Haus Klasse
 */

public class Map2 extends Knoten{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    //Matrix für die Kollisiongebäude. in jedem eintrag sind 4 int für Obenx,Obeny, Untenx,UntenY
    private int NumberofB = -1;//is set in InitArrays()
    private int DoorLength = 20;//global DoorLength als in width/height
    private int DoorDepth = 10;//global DoorLength als in width/height


    private int[][] Buildings;
    private int[][] Doors;
    private int[][] Interior;
    private Bild[] InteriorPics;

    private Bild MapPic;
    private int PlayerW;
    private int PlayerH;

    private String defaultPath = "./Assets/Houses/"; //Basis-Pfad für alle interiorPics

    private int HouseNumber;// zeigt an in welchem haus der Player ist. Entspricht der reinfolge des Jsons

    private HashMap<String, Map2.Haus> MAP;


    private ColliderShape[] OuterWallHitbox;
    private ColliderShape[] InteriorHitbox; //for not leaving the Interior

    private Rechteck[] Display_Buildings;
    private Rechteck[] Display_Interior;
    private Rechteck[] Display_Doors;

    //Status, ob der Spieler sich in einem Gebäude befindet für func Walkable
    private boolean visiting = false;
    private boolean isInDoor = false;




    public Map2(float PW, float PH) {
        this.PlayerW = (int)PW;
        this.PlayerH = (int)PH;

        MapPic= new Bild(0,0,"./Assets/Map3.jpg");
        this.add(MapPic);

        readJSON();//muss erst gelesen werden, um länge für Arrays zu geben
        InitArrays();
        FillArrays();

        CreateHitboxObjects();
        DisplayObjects();

    }



    /**
     * Erstellt die Arrays mit richtiger Länge
     */
    private void InitArrays(){
        NumberofB = MAP.size();

        Buildings = new int[NumberofB][4];
        Doors = new int[NumberofB][4];
        Interior = new int[NumberofB][4];
        InteriorPics = new Bild[NumberofB];

        OuterWallHitbox = new ColliderShape[NumberofB];
        InteriorHitbox = new ColliderShape[NumberofB];
        ;
    }

    private void FillArrays(){

        String path ="";
        int i = 0;
        for( String key : MAP.keySet() ) {
            Map2.Haus element = MAP.get(key);


            Buildings[i][0] = element.posX;
            Buildings[i][1] = element.posY;
            Buildings[i][2] = element.width;
            Buildings[i][3] = element.height;

            if(element.doorTyp == "l"){ // falls die Tür an der Linken seite ist
                Doors[i][0] = element.posX- DoorDepth/2;
                Doors[i][1] = element.posY + element.DoorOffset;
                Doors[i][2] = element.posX + DoorDepth/2;
                Doors[i][3] = element.posY + element.DoorOffset + DoorLength;
            }
            else if(element.doorTyp == "r"){
                Doors[i][0] = element.posX - DoorDepth/2 + element.width;
                Doors[i][1] = element.posY + element.DoorOffset;
                Doors[i][2] = element.posX + DoorDepth/2 + element.width;
                Doors[i][3] = element.posY + element.DoorOffset + DoorLength;
            }
            else if(element.doorTyp == "t"){
                Doors[i][0] = element.posX + element.DoorOffset;
                Doors[i][1] = element.posY - DoorDepth/2;
                Doors[i][2] = element.posX + element.DoorOffset + DoorLength;
                Doors[i][3] = element.posY + DoorDepth/2;
            }
            else if(element.doorTyp == "b"){
                Doors[i][0] = element.posX + element.DoorOffset;
                Doors[i][1] = element.posY - DoorDepth/2 + element.height;
                Doors[i][2] = element.posX + element.DoorOffset + DoorLength;
                Doors[i][3] = element.posY + DoorDepth/2 + element.height;
            }
            else{
                System.out.println(ANSI_PURPLE + "Fehler in der Map2 Klasse: Eine Tür mit invalidem doorTyp wurde gefunden" + ANSI_RESET);
            }



            path = defaultPath + element.name + ".png";

            InteriorPics[i] = new Bild(0,0,path);
            //Zentriert Bild anhand von der globalen Window Größe (MAIN.x, MAIN.y)
            float x = InteriorPics[i].getBreite();
            float y = InteriorPics[i].getHoehe();
            float centerX = (MAIN.x/2) - x/2;
            float centerY = (MAIN.y/2) - y/2;

            InteriorPics[i].positionSetzen(centerX,centerY);//zentriert das Bild
            this.add(InteriorPics[i]);

            int finalInteriorPos_X = (int)InteriorPics[i].getX();//Realposition der Oberen Ecke
            int finalInteriorPos_Y = (int)InteriorPics[i].getY();

            Interior[i][0] = element.InnerX + PlayerW + finalInteriorPos_X; //Offset + PlayerBreite + Realposition der Oberen Ecke
            Interior[i][1] = element.InnerY + PlayerH + finalInteriorPos_Y; //Offset + PlayerHoehe + Realposition der Oberen Ecke
            Interior[i][2] = element.InnerWidth - PlayerW;
            Interior[i][3] = element.InnerHeight - PlayerH;

            i++;
        }
    }



    /**
     * Macht aus den angegebene Positionen der Gebaüde ColliderShape Objekte und Türen.
     */
    private void CreateHitboxObjects(){
        for(int i =0;i<NumberofB;i++){
            OuterWallHitbox[i] = new ColliderShape(Buildings[i][0],Buildings[i][1],Buildings[i][2],Buildings[i][3]);
            InteriorHitbox[i] = new ColliderShape(Interior[i][0],Interior[i][1],Interior[i][2],Interior[i][3]);
        }
    }
    /**
     * Macht aus den angegebene Positionen der Gebaüde Rechteck Objekt und Türen.
     * Diese werden zum "this Knoten" hinzugefügt und somit angezeigt.
     */
    private void DisplayObjects(){
        for(int i =0;i<NumberofB;i++) {
            Display_Buildings[i] = new Rechteck(Buildings[i][0],Buildings[i][1],Buildings[i][2],Buildings[i][3]);
            Display_Buildings[i].farbeSetzen(Color.gray);
            Display_Buildings[i].setOpacity(0.5f);//macht sie halbtransparent
            this.add(Display_Buildings[i]);

            Display_Interior[i] = new Rechteck(Interior[i][0],Interior[i][1],Interior[i][2],Interior[i][3]);
            Display_Interior[i].farbeSetzen(Color.black);
            Display_Interior[i].setOpacity(0.5f);//macht sie halbtransparent
            this.add(Display_Interior[i]);

            Display_Doors[i] = new Rechteck(Doors[i][0],Doors[i][1],Doors[i][2],Doors[i][3]);
            Display_Doors[i].farbeSetzen(Color.blue);
            Display_Doors[i].setOpacity(0.5f);//macht sie halbtransparent
            this.add(Display_Doors[i]);

        }
    }

    /**
     * Sagt ob ein Dummyplayer der vorgeschickt laufen darf.
     *
     * In Türen z.B darf man immer laufen.
     */
    public boolean getWalkable(DummyPlayer dp){
        return true;
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
    public void toggleVisting(){
        if(visiting){
            visiting = false;
        }else{
            visiting = true;
        }
    }

    private void readJSON() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./Assets/Files/Karte.json"));

            Type MapType = new TypeToken<HashMap<String, Map2.Haus>>(){}.getType();
            MAP = gson.fromJson(bufferedReader, MapType);
        }
        catch(Exception e) {
            System.out.println(ANSI_PURPLE + "Ein Fehler beim Lesen der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "Eigentlich kann nur das Grafikteam schuld sein..." + ANSI_RESET);

        }

    }

    public class Haus{
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

