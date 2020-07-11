import ea.edu.*;
import ea.*;


public class ActivePlayer
        extends BildE {


    /**
     * BILD Konstruktor
     *
     * @param   x       x-Koordinate im Fenster (Pixel)
     * @param   y       y-Koordinate im Fenster (Pixel)
     * @param   name    Name der Grafik-Datei (im Projekt-Ordner)
     */
    public ActivePlayer(int x, int y, String name) {
        super(x, y, name);
        this.setzeMittelpunkt(x, y);
    }


    public void verschiebenUm(double deltaX, double deltaY) {
        super.bewegen( (int)( Math.round(deltaX) ), (int)( Math.round(deltaY) ) );
    }


    /**
     * Methode beinhaltetPunkt
     *
     * @param   x   x-Koordinate des Punkts (Pixel)
     * @param   y   x-Koordinate des Punkts (Pixel)
     * @return      true, wenn Punkt innerhalb der Grafik
     */
    public boolean beinhaltetPunkt(int x, int y) {
        return super.beinhaltet( new Punkt(x, y) );
    }


    /**
     * Methode setzeMittelpunkt
     *
     * @param   x   x-Koordinate des Mittelpunkts (Pixel)
     * @param   y   y-Koordinate des Mittelpunkts (Pixel)
     */
    public void setzeMittelpunkt(int x, int y) {
        super.mittelpunktSetzen(x, y);
    }


    /**
     * Methode nenneMx
     *
     * @return  x-Koordinate des Mittelpunkts (Pixel)
     */
    public int nenneMx() {
        return super.zentrum().x();
    }


    /**
     * Methode nenneMY
     *
     * @return  y-Koordinate des Mittelpunkts (Pixel)
     */
    public int nenneMy() {
        return super.zentrum().y();
    }


    /**
     * Methode setzeSichtbar
     *
     * @param   sichtbarNeu     true, wenn die Grafik sichtbar sein soll
     */
    public void setzeSichtbar(boolean sichtbarNeu) {
        super.sichtbarSetzen(sichtbarNeu);
    }


    /**
     * Methode nenneSichtbar
     *
     * @return  true, wenn die Grafik gerade sichbar ist
     */
    public boolean nenneSichtbar() {
        return super.sichtbar();
    }



    public void drehenUm(int winkelAenderung)
    {
        this.drehenRelativ( (double)( -winkelAenderung ) );
    }

    public void setzeDrehwinkel(int neuerDrehwinkel)
    {
        this.drehenAbsolut( (double)( -neuerDrehwinkel ) );
    }

    public int nenneWinkel()
    {
        return (int)( -this.gibDrehung() );
    }


}
