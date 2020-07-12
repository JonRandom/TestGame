/* @author     mike ganshorn + michael andonie
 *
 *  @Version    2013-10-11
 *
 *  Bei Aenderung der Breite/Hoehe bleibt der Mittelpunkt erhalten
 *  keine Abhaengigkeit mehr zwischen den alpha-Formen
 */

import ea.edu.RechteckE;
import ea.Raum;

/**
 * Diese Klasse stellt ein einfaches Rechteck dar.
 */
public class RECHTECK extends RechteckE {

    /**
     * Die Farbe dieses Rechtecks
     */
    private String farbe;

    /**
     * Gibt an, ob dieses Rechteck sichtbar ist.
     */
    private boolean sichtbar;

    /**
     * Breite des Rechtecks
     */
    private int breite;

    /**
     * Hoehe des Rechtecks
     */
    private int hoehe;

    /**
     * x-Koordinate ds Mittelpunkts
     */
    private int M_x;

    /**
     * y-Koordinate des Mittelpunkts
     */
    private int M_y;


    /**
     * Konstruktor der Klasse <code>RECHTECK</code>. Erstellt ein neues Rechteck.
     */
    public RECHTECK() {
        this(200, 130,250,250);
    }

    /**
     * RECHTECK Konstruktor
     *
     * @param   breite  Breite des Rechtecks
     * @param   hoehe   Hoehe des Rechtecks
     */
    public RECHTECK(int breite, int hoehe,int M_x, int M_y) {
        this.sichtbar = true;
        super.sichtbarSetzen(true);
        this.farbe = "Rot";
        super.farbeSetzen(this.farbe);
        this.breite = breite;
        this.hoehe = hoehe;
        super.masseSetzen(this.breite, this.hoehe);
        this.M_x = M_x;
        this.M_y = M_y;
        super.mittelpunktSetzen(this.M_x, this.M_y);
    }


    /**
     * Setzt die Hoehe und Breite dieses Rechtecks neu.
     * @param   breite  Die neue Breite dieses Rechtecks
     * @param   hoehe   Die neue Hoehe dieses Rechtecks
     */
    public void setzeGroesse(int breite, int hoehe) {
        int x = this.nenneM_x();
        int y = this.nenneM_y();
        this.breite = breite;
        this.hoehe = hoehe;
        super.masseSetzen(breite, hoehe);
        super.mittelpunktSetzen(x, y);
    }

    /**
     * Setzt die Farbe dieses Rechtecks neu.
     * @param   farbeNeu    Diese Farbe erhaelt das Rechteck (z.B. "Rot")
     */
    public void setzeFarbe(String farbeNeu) {
        this.farbe = farbeNeu;
        this.farbeSetzen(farbe);
    }

    /**
     * Setzt den Mittelpunkt dieses Rechtecks neu.
     * @param   m_x Die X-Koordinate des neuen Mittelpunktes
     * @param   m_y Die Y-Koordinate des neuen Mittelpunktes
     */
    public void setzeMittelpunkt(int m_x, int m_y) {
        this.M_x = m_x;
        this.M_y = m_y;
        super.mittelpunktSetzen(m_x, m_y);
    }


    public void setzeSichtbar(boolean sichtbarNeu) {
        this.sichtbar = sichtbarNeu;
        super.sichtbarSetzen(sichtbarNeu);
    }


    public void verschiebenUm(int deltaX, int deltaY) {
        this.M_x = this.M_x + deltaX;
        this.M_y = this.M_y + deltaY;
        super.verschieben(deltaX, deltaY);
    }


    public int nenneM_x()
    {
        return this.M_x;
    }


    public int nenneM_y()
    {
        return this.M_y;
    }


    public int nenneBreite()
    {
        return this.breite;
    }


    public int nenneHoehe()
    {
        return this.hoehe;
    }


    public String nenneFarbe()
    {
        return this.farbe;
    }


    public boolean nenneSichtbar()
    {
        return this.sichtbar;
    }



    public int berechneAbstandX(Raum grafikObjekt)
    {
        return this.M_x - grafikObjekt.mittelPunkt().x();
    }


    public int berechneAbstandY(Raum grafikObjekt)
    {
        return this.M_y - grafikObjekt.mittelPunkt().y();
    }

}