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
    private Bild MapPic;

    private BoundingRechteck[] BuildingsObjects = new BoundingRechteck[NumberofB];
    private Rechteck[] BuildingsObjectsDisplay = new Rechteck[NumberofB];




    public Map(){
        super();
        MapPic= new Bild(0,0,"./Assets/Map3.jpg");
        this.add(MapPic);

        BuildingsInit();

        for(int i =0;i<NumberofB;i++){
            BuildingsObjects[i] = new BoundingRechteck(Buildings[i][0],Buildings[i][1],Buildings[i][2],Buildings[i][3]);
            //this.add(BuildingsObjects[i]);
            System.out.println("test");

        }

        DisplayBuildings();



    }

    public void BuildingsInit(){
        Buildings[0][0]=200;
        Buildings[0][1]=200;
        Buildings[0][2]=100;
        Buildings[0][3]=100;

        Buildings[1][0]=400;
        Buildings[1][1]=400;
        Buildings[1][2]=150;
        Buildings[1][3]=160;

        Buildings[2][0]=800;
        Buildings[2][1]=900;
        Buildings[2][2]=180;
        Buildings[2][3]=100;
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


    }




    public void drawRects(){}

}
