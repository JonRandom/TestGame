import ea.*;
import ea.internal.collision.Collider;

import java.awt.*;

/**
 * Diese Klasse managed die Karte und die Rechtecke der Gebäude,
 * die im Hintergrund die Kollisionerkennung leiten.
 *
 * Gebäudedaten vielleicht auch aus JSON??
 *
 */

public class Map extends Knoten{
    //Matrix für die Kollisiongebäude. in jedem eintrag sind 4 int für Obenx,Obeny, Untenx,UntenY
    private int NumberofB = 8;
    private int[][] Buildings = new int[NumberofB][4];
    private int[][] Doors = new int[NumberofB][4];
    private Bild MapPic;
    private int PlayerW;
    private int PlayerH;

    private BoundingRechteck[] BuildingsObjects = new BoundingRechteck[NumberofB];
    private BoundingRechteck[] BuildingsObjectsInner = new BoundingRechteck[NumberofB]; //Innere Klossionstests
    private BoundingRechteck[] DoorObjects = new BoundingRechteck[NumberofB];

    private Rechteck[] BuildingsObjectsDisplay = new Rechteck[NumberofB];
    private Rechteck[] BuildingsObjectsDisplayInner = new Rechteck[NumberofB];
    private Rechteck[] DoorObjectsDisplay = new Rechteck[NumberofB];

    //Status, ob der Spieler sich in einem Gebäude befindet für func Walkable
    private boolean visiting = false;




    public Map(float PW,float PH){
        //super();
        this.PlayerW = (int)PW;
        this.PlayerH = (int)PH;

        MapPic= new Bild(0,0,"./Assets/Map3.jpg");
        this.add(MapPic);

        BuildingsInit();
        MakeBuildingObjects();

        DisplayBuildings();



    }

    public void BuildingsInit(){
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
    public void MakeBuildingObjects(){
        System.out.println(PlayerW +"+"+PlayerH);
        for(int i =0;i<NumberofB;i++){
            DoorObjects[i] = new BoundingRechteck(Doors[i][0],Doors[i][1],Doors[i][2],Doors[i][3]);
            BuildingsObjects[i] = new BoundingRechteck(Buildings[i][0],Buildings[i][1],Buildings[i][2],Buildings[i][3]);
            BuildingsObjectsInner[i] = new BoundingRechteck(Buildings[i][0]+PlayerW,Buildings[i][1]+PlayerH,Buildings[i][2]-2*PlayerW,Buildings[i][3]-2*PlayerH);

            //this.add(BuildingsObjects[i]); //muss und kann nicht angezeigt werden!
            System.out.println("test");

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

    public void DisplayBuildings(){
        for(int i =0;i<NumberofB;i++){
            BuildingsObjectsDisplay[i]= new Rechteck(Buildings[i][0],Buildings[i][1],Buildings[i][2],Buildings[i][3]);
            this.add(BuildingsObjectsDisplay[i]);
        }
        for(int i =0;i<NumberofB;i++){
            BuildingsObjectsDisplayInner[i]= new Rechteck(Buildings[i][0]+PlayerW,Buildings[i][1]+PlayerH,Buildings[i][2]-2*PlayerW,Buildings[i][3]-2*PlayerH);
            BuildingsObjectsDisplayInner[i].farbeSetzen(Color.green);
            this.add(BuildingsObjectsDisplayInner[i]);
        }
        for(int i =0;i<NumberofB;i++){
            DoorObjectsDisplay[i]= new Rechteck(Doors[i][0],Doors[i][1],Doors[i][2],Doors[i][3]);

            this.add(BuildingsObjectsDisplayInner[i]);
        }
    }


    public boolean getWalkable(DummyPlayer p){
        if(visiting){
            boolean coll=false;
            for(int i =0;i<NumberofB;i++){
                if (p.inFlaeche(BuildingsObjectsInner[i])) {
                    coll= true;
                }
            }
            return coll; //falls es irgendwo eine Kollision mit den inneren Rechtecken gibt, soll er laufen können.
        }
        else{
            boolean coll=false;
            for(int i =0;i<NumberofB;i++){
                if (p.inFlaeche(BuildingsObjects[i])) {
                    coll= true;
                }
            }
            return !coll;
        }

    }

    public void setVisiting(boolean visiting) {
        this.visiting = visiting;
    }

    public boolean isVisiting() {
        return visiting;
    }

    //an wenn aus, aus wenn an
    public void toggleVisibility(){
        if(visiting){
            visiting = false;
        }else{
            visiting = true;
        }
    }
}
