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
        this(200, 130);
    }

    /**
     * RECHTECK Konstruktor
     *
     * @param   breite  Breite des Rechtecks
     * @param   hoehe   Hoehe des Rechtecks
     */
    public RECHTECK(int breite, int hoehe) {
        this.sichtbar = true;
        super.sichtbarSetzen(true);
        this.farbe = "Rot";
        super.farbeSetzen(this.farbe);
        this.breite = breite;
        this.hoehe = hoehe;
        super.masseSetzen(this.breite, this.hoehe);
        this.M_x = 250;
        this.M_y = 250;
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

    /**
     * Setzt, ob dieses Rechteck sichtbar sein soll.
     * @param   sichtbarNeu Ist dieser Wert <code>true</code>, ist nach dem Aufruf dieser Methode dieses Rechteck 
     * sichtbar. Ist dieser Wert <code>false</code>, so ist nach dem Aufruf dieser Methode dieses Rechteck unsichtbar.
     */
    public void setzeSichtbar(boolean sichtbarNeu) {
        this.sichtbar = sichtbarNeu;
        super.sichtbarSetzen(sichtbarNeu);
    }

    /**
     * Verschiebt dieses Rechteck um eine Verschiebung - angegeben durch ein "Delta X" und "Delta Y".
     * @param   deltaX  Der X Anteil dieser Verschiebung. Positive Werte verschieben nach rechts, negative nach links.
     * @param   deltaY  Der Y Anteil dieser Verschiebung. Positive Werte verschieben nach unten, negative nach oben.
     */
    public void verschiebenUm(int deltaX, int deltaY) {
        this.M_x = this.M_x + deltaX;
        this.M_y = this.M_y + deltaY;
        super.verschieben(deltaX, deltaY);
    }

    /**
     * Diese Methode gibt die x-Koordinate des Mittelpunkts dieses Rechtecks zurueck
     * @return  Die aktuelle x-Koordinate des Mittelpunktes dieses Rechtecks
     */
    public int nenneM_x()
    {
        return this.M_x;
    }

    /**
     * Diese Methode gibt die y-Koordinate des Mittelpunkts dieses Rechtecks zurueck
     * @return  Die aktuelle y-Koordinate des Mittelpunktes dieses Rechtecks
     */
    public int nenneM_y()
    {
        return this.M_y;
    }

    /**
     * Diese Methode gibt die Breite dieses Rechtecks zurueck
     * @return  Die aktuelle Breite dieses Rechtecks
     */
    public int nenneBreite()
    {
        return this.breite;
    }

    /**
     * Diese Methode gibt die Hoehe dieses Rechtecks zurueck
     * @return  Die aktuelle Hoehe dieses Rechtecks
     */
    public int nenneHoehe()
    {
        return this.hoehe;
    }

    /**
     * Diese Methode gibt die Farbe dieses Rechtecks zurueck
     * @return  Die aktuelle Farbe dieses Rechtecks
     */
    public String nenneFarbe()
    {
        return this.farbe;
    }

    /**
     * Diese Methode gibt die Sichtbarkeit dieses Rechtecks zurueck
     * @return  Die aktuelle Sichtbarkeit dieses Rechtecks
     */
    public boolean nenneSichtbar()
    {
        return this.sichtbar;
    }


    /**
     * Diese Methode prueft, wie weit der Mittelpunkt dieses Rechtecks vom Mittelpunkt 
     * eines anderen Grfik-Objekts in x-Richtung entfernt ist.
     * @param   grafikObjekt    Das andere Grafik-Objekt
     * @return  Abstand (in Pixeln) dieses Rechtecks vom anderen Grafik-Objekt in x-Richtung (>0, wenn dieses Rechteck rechts des anderen Grafik-Objekts liegt)
     */
    public int berechneAbstandX(Raum grafikObjekt)
    {
        return this.M_x - grafikObjekt.mittelPunkt().x();
    }

    /**
     * Diese Methode prueft, wie weit der Mittelpunkt dieses Rechtecks vom Mittelpunkt 
     * eines anderen Grfik-Objekts in y-Richtung entfernt ist.
     * @param   grafikObjekt    Das andere Grafik-Objekt
     * @return  Abstand (in Pixeln) dieses Rechtecks vom anderen Grafik-Objekt in y-Richtung (>0, wenn dieses Rechteck unterhalb des anderen Grafik-Objekts liegt)
     */
    public int berechneAbstandY(Raum grafikObjekt)
    {
        return this.M_y - grafikObjekt.mittelPunkt().y();
    }

}