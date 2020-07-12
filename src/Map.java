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

public class Map extends Raum{
    //Matrix für die Kollisiongebäude. in jedem eintrag sind 4 int für Obenx,Obeny, Untenx,UntenY
    private int[][] Buildings = new int[8][4];


    private BILD MapPic;


    public void verschieben(int dx, int dy){
        MapPic.verschieben(dx,dy);

    }


    public Map(){
        MapPic= new BILD(0,0,"./Assets/Map.png");
    }

    @Override
    public void zeichnen(Graphics2D graphics2D, BoundingRechteck boundingRechteck) {

    }

    @Override
    public BoundingRechteck dimension() {
        return null;
    }

    @Override
    public Collider erzeugeCollider() {
        return null;
    }

    public void InitBuilidingData(){
        Buildings[0][0]=5;
        Buildings[0][1]=5;

        Buildings[0][100]=5;
        Buildings[0][100]=5;
    }
    public void drawRects(){}

}
