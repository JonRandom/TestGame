import ea.*;
import ea.edu.*;

import java.awt.*;

public class SPIEL
{
    /**
     * Die Anzeige des Spiels.
     */
    private static AnzeigeE anzeige;

    private int zaehler;
    private ActivePlayer ActivePlayer1;

    public Map mapObject;

    public Knoten MainPlate; //Knoten mit allen Objekten auf dem Bildschirm die bewegt werden sollen.



    public SPIEL(int breite, int hoehe,  boolean maus)
    {
        //Zaehler fuer Tick, Tack, ...
        zaehler = 0;
        anzeige = new AnzeigeE(breite, hoehe);


        if(maus)
        {
            anzeige.klickReagierbarAnmelden(this, true);
        }
        anzeige.tastenReagierbarAnmelden(this);
        anzeige.tickerAnmelden(this, 500);

        Map mapObject = new Map();
        MainPlate = new Knoten();
        MainPlate.add(mapObject);

        ActivePlayer1 = new ActivePlayer(300,300,"./Assets/MouseC.png");



        DialogController d = new DialogController();
        d.HideWindow();





    }





    public void tick()
    {
        System.out.println("Tick!");
    }


    public void klickReagieren(int x, int y)
    {
        //Einfache Bildschirmausgabe. Kann spaeter in Subklasse beliebig ueberschrieben werden.
        System.out.println("Klick bei (" + x  + ", " + y + ").");
    }




    /**
     * Wird bei jedem Tastendruck automatisch aufgerufen.
     *
     * @param   tastenkuerzel   Der int-Wert, der fuer die gedrueckte Taste steht.
     *                          Details koennen in der <i>Tabelle aller
     *                          Tastaturkuerzel</i> abgelesen werden.
     */
    public void tasteReagieren(int tastenkuerzel)
    {
        //System.out.println("Taste mit Kuerzel " + tastenkuerzel +" wurde gedrueckt");
        if(tastenkuerzel==27){
            ActivePlayer1.verschiebenUm(10,0);//rechts
            MainPlate.verschieben(15,2);

        }
        if(tastenkuerzel==29){
            ActivePlayer1.verschiebenUm(-10,0);//links
        }
        if(tastenkuerzel==26){
            ActivePlayer1.verschiebenUm(0,-10);//hoch
        }
        if(tastenkuerzel==28){
            ActivePlayer1.verschiebenUm(0,10);//runter
        }
    }






    public void tickerIntervallSetzen(int ms)
    {
        anzeige.tickerAbmelden(this);
        anzeige.tickerAnmelden(this, ms);
    }


    public void tickerStoppen()
    {
        anzeige.tickerAbmelden(this);
    }


    public void tickerNeuStarten(int ms)
    {
        anzeige.tickerAbmelden(this);
        anzeige.tickerAnmelden(this, ms);
    }



    public void mausIconSetzen(String pfad, int hotspotX, int hotspotY)
    {
        ea.edu.FensterE.getFenster().mausAnmelden(new Maus(new Bild(0,0,pfad), new Punkt(hotspotX, hotspotY)), true);
    }



    public void setzeFarbePunktestand(String farbe) { anzeige.setzeFarbePunktestand(farbe); }


    /**
     * Gibt eine Zufallszahl aus.
     *
     * @param von   Die Untergrenze der Zufallszahl (INKLUSIVE)
     *
     * @param bis   Die Obergrenze der Zufallszahl (INKLUSIVE)
     *
     * @return      Eine Zufallszahl z mit:   von <= z <= bis
     */
    public int zufallszahlVonBis(int von, int bis)
    {
        return anzeige.zufallszahlVonBis(von, bis);
    }


    /**
     * Setzt eine Hintergrundgrafik fuer das Spiel.
     *
     * @param   pfad    Der Pfad der Bilddatei (jpg, bmp, png) des Bildes,
     *                  das benutzt werden soll. ZB: "hintergrund.jpg"
     */
    public void hintergrundgrafikSetzen(String pfad)
    {
        ea.edu.FensterE.getFenster().hintergrundSetzen(new Bild(0,0,pfad));
    }



    public void warte(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
