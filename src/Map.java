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
        Rechteck r3 = new Rechteck(Buildings[0][0],Buildings[0][1],Buildings[0][2],Buildings[0][3]);
        this.add(r3);



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
    }
    public boolean[] ColliderTest(Player p){
        boolean[] CollisionB = new boolean[NumberofB];
        for(int i =0;i<NumberofB;i++){
            CollisionB[i]= p.inFlaeche(BuildingsObjects[i]);
        }
        return CollisionB;
    }




    public void drawRects(){}

}
