import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ea.*;
import ea.internal.collision.Collider;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Diese Klasse managed die Karte und die Rechtecke der Gebäude,
 * die im Hintergrund die Kollisionerkennung leiten.
 *
 * Gebäudedaten vielleicht auch aus JSON??
 *
 */

public class Map extends Knoten{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    //Matrix für die Kollisiongebäude. in jedem eintrag sind 4 int für Obenx,Obeny, Untenx,UntenY
    private int NumberofB = 8;
    private int[][] Buildings;
    private int[][] Doors;
    private Bild MapPic;
    private int PlayerW;
    private int PlayerH;

    private HashMap<String, MapTest.Haus> MAP;




    private BoundingRechteck[] BuildingsObjects;
    private BoundingRechteck[] BuildingsObjectsInner; //Innere Klossionstests
    private BoundingRechteck[] DoorObjects;

    private Rechteck[] BuildingsObjectsDisplay;
    private Rechteck[] BuildingsObjectsDisplayInner;
    private Rechteck[] DoorObjectsDisplay;

    //Status, ob der Spieler sich in einem Gebäude befindet für func Walkable
    private boolean visiting = false;
    private boolean isInDoor = false;




    public Map(float PW,float PH) throws FileNotFoundException {
        this.PlayerW = (int)PW;
        this.PlayerH = (int)PH;

        MapPic= new Bild(0,0,"./Assets/Map3.jpg");
        this.add(MapPic);

        readJSON();//muss erst gelesen werden, um länge für Arrays zu geben
        InitArrays();


        InitBuildings2();
        DoorsInit();

        MakeBuildingObjects();

        DisplayBuildings();



    }

    private void BuildingsInit(){
        Buildings[0][0]=200;
        Buildings[0][1]=200;
        Buildings[0][2]=100;
        Buildings[0][3]=100;

        Buildings[1][0]=400;
        Buildings[1][1]=400;
        Buildings[1][2]=400;
        Buildings[1][3]=260;

        Buildings[2][0]=800;
        Buildings[2][1]=900;
        Buildings[2][2]=180;
        Buildings[2][3]=100;
    }
    private void DoorsInit(){
        Doors[1][0]=400-10;
        Doors[1][1]=500;
        Doors[1][2]=20;
        Doors[1][3]=60;
    }

    /**
     * Macht aus den angegebene Positionen der Gebaüde BoundingRechteck Objekte auch die Innernen und Türen.
     */
    private void MakeBuildingObjects(){


        for(int i =0;i<NumberofB;i++){

            DoorObjects[i] = new BoundingRechteck(Doors[i][0],Doors[i][1],Doors[i][2],Doors[i][3]);
            BuildingsObjects[i] = new BoundingRechteck(Buildings[i][0],Buildings[i][1],Buildings[i][2],Buildings[i][3]);
            BuildingsObjectsInner[i] = new BoundingRechteck(Buildings[i][0]+PlayerW,Buildings[i][1]+PlayerH,Buildings[i][2]-2*PlayerW,Buildings[i][3]-2*PlayerH);

            //this.add(BuildingsObjects[i]); //muss und kann nicht angezeigt werden!
        }
    }
    private void InitBuildings2(){
        System.out.println(MAP.size()
        );
        int i = 0;

        for( String key : MAP.keySet() ) {
            MapTest.Haus value = MAP.get(key);

            Buildings[i][0] = value.posX;
            Buildings[i][1] = value.posY;
            Buildings[i][2] = value.width;
            Buildings[i][3] = value.height;

            i++;
        }
    }


    public boolean[] ColliderTest(Player p){
        boolean[] CollisionB = new boolean[NumberofB];
        for(int i =0;i<NumberofB;i++){
            CollisionB[i]= p.inFlaeche(BuildingsObjects[i]);
        }
        return CollisionB;
    }
    public boolean ColliderTestAny(DummyPlayer p){
        boolean coll=false;
        for(int i =0;i<NumberofB;i++){
            if (p.inFlaeche(BuildingsObjects[i])) {
                coll= true;
            }
        }
        return coll;
    }

    /**
     * Erstellt die arrays mit richtiger länge
     */
    private void InitArrays(){

        NumberofB = MAP.size();

        Buildings = new int[NumberofB][4];
        Doors = new int[NumberofB][4];

        BuildingsObjects = new BoundingRechteck[NumberofB];
        BuildingsObjectsInner = new BoundingRechteck[NumberofB]; //Innere Klossionstests
        DoorObjects = new BoundingRechteck[NumberofB];

        BuildingsObjectsDisplay = new Rechteck[NumberofB];
        BuildingsObjectsDisplayInner = new Rechteck[NumberofB];
        DoorObjectsDisplay = new Rechteck[NumberofB];
    }

    /**
     * Macht aus den angegebene Positionen der Gebaüde Rechteck Objekte auch die Innernen und Türen.
     * Diese werden zum "this Knoten" hinzugefügt und somit angezeigt.
     */
    private void DisplayBuildings(){
        for(int i =0;i<NumberofB;i++) {

            BuildingsObjectsDisplay[i] = new Rechteck(Buildings[i][0], Buildings[i][1], Buildings[i][2], Buildings[i][3]);
            this.add(BuildingsObjectsDisplay[i]);

            BuildingsObjectsDisplayInner[i] = new Rechteck(Buildings[i][0] + PlayerW, Buildings[i][1] + PlayerH, Buildings[i][2] - 2 * PlayerW, Buildings[i][3] - 2 * PlayerH);
            BuildingsObjectsDisplayInner[i].farbeSetzen(Color.green);
            this.add(BuildingsObjectsDisplayInner[i]);

            DoorObjectsDisplay[i] = new Rechteck(Doors[i][0], Doors[i][1], Doors[i][2], Doors[i][3]);
            DoorObjectsDisplay[i].farbeSetzen(Color.blue);
            this.add(DoorObjectsDisplay[i]);
        }
    }

    /**
     * Sagt ob ein Dummyplayer der vorgeschickt laufen darf.
     *
     * In Türen z.B darf man immer laufen.
     */
    public boolean getWalkable(DummyPlayer p){
        updateInDoor(p);
        updateVisiting(p);
        if(!isInDoor) {
            if (visiting) {
                boolean coll = false;
                for (int i = 0; i < NumberofB; i++) {
                    if (p.inFlaeche(BuildingsObjectsInner[i])) {
                        coll = true;
                    }
                }
                return coll; //falls es irgendwo eine Kollision mit den inneren Rechtecken gibt, soll er laufen können.
            } else {
                boolean coll = false;
                for (int i = 0; i < NumberofB; i++) {
                    if (p.inFlaeche(BuildingsObjects[i])) {
                        coll = true;
                    }
                }
                return !coll;
            }
        } else {
            return true;
        }



    }

    /**
     * Setzt isInDoor auf den aktuellen Zustand.
     * @param dp Dummyplayer der rumgereicht wird.
     *           Der Arme...
     */
    private void updateInDoor(DummyPlayer dp){
        boolean coll = false;
        for(int i =0;i<NumberofB;i++) {
            if(dp.inFlaeche(DoorObjects[i])){
                coll = true;
            }
        }
        isInDoor = coll;
    }


    /**
        falls der Spieler sich in einer Tür befindet, wird, wenn er sich mit InnerBuilding schneidet, visiting auf true gesetzt.
        sonst(in Tür schneidet aber kein InnerBuilding, false.
     */
    private void updateVisiting(DummyPlayer dp){
        updateInDoor(dp);
        if(isInDoor){
            boolean coll = false;
            for (int i = 0; i < NumberofB; i++) {
                if (dp.inFlaeche(BuildingsObjectsInner[i])) {
                    coll = true;
                }
            }
            visiting = coll;

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
     *
     * muss public sein
     */
    public void toggleVisting(){
        if(visiting){
            visiting = false;
        }else{
            visiting = true;
        }
    }

    private void readJSON() throws FileNotFoundException {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./Assets/Files/Karte.json"));

            Type MapType = new TypeToken<HashMap<String, MapTest.Haus>>(){}.getType();
            MAP = gson.fromJson(bufferedReader, MapType);
        }
        catch(Exception e) {
            System.out.println(ANSI_PURPLE + "Ein Fehler beim Lesen der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "Eigentlich kann nur das Grafikteam schuld sein..." + ANSI_RESET);

        }

    }
}
