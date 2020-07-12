import ea.edu.*;
import ea.*;


/**
 * Klasse BILD zum Darstellen eines GIF, JPG, PNG in EDU-Engine
 *
 * @author      mike ganshorn
 * @version     2014-04-01
 *
 * @changelog   Konstruktor setzt nun Bild-Mittelpunkt auf uebergebenen Mittelpunkt (nicht Ecke links oben)
 */
public class BILD
        extends BildE {


    public BILD(int x, int y, String name) {
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


//     /**
//      * Methode schneidet
//      *
//      * @param   r   Ein anderes BILD ODER RECHTECK oder KREIS oder DREIECK
//      * @return  ture, wenn sich die beiden Objekte schneiden
//      */
//     public boolean schneidet(Raum r) {
//         return super.schneidet(r);
//     }



    /**
     * Dreht die Grafik um einen Winkel
     *
     * @param   winkelAenderung     +: mathematisch positiver Drehsinn (gegen den Uhrzeigersinn)
     *                              -: mathematisch negativer Drehsinn (im Uhrzeigersinn)
     */
    public void drehenUm(int winkelAenderung)
    {
        this.drehenRelativ( (double)( -winkelAenderung ) );
    }


    /**
     * Setzt den Drehwinkel auf eine absoluten neuen Wert
     *
     * @param   neuerDrehwinkel     der neue Drehwinkel
     *                              +: mathematisch positiver Drehsinn (gegen den Uhrzeigersinn)
     *                              -: mathematisch negativer Drehsinn (im Uhrzeigersinn)
     */
    public void setzeDrehwinkel(int neuerDrehwinkel)
    {
        this.drehenAbsolut( (double)( -neuerDrehwinkel ) );
    }


    /**
     * Nennt den Winkel, um den die Grafik gedreht wurde
     *
     * @return      der Winkel, um den die Grafik gedreht wurde
     *              0: wenn nicht gedreht
     *              +: wenn mathematisch positiver Drehsinn (gegen den Uhrzeigersinn)
     *              -: wenn mathematisch negativer Drehsinn (im Uhrzeigersinn)
     */
    public int nenneWinkel()
    {
        return (int)( -this.gibDrehung() );
    }


    /**
     * liefert den Sinus des Drehwinkels der Grafik
     *
     * @return  Sinus des aktuellen Drehwinkels
     */
    public double sin_Drehwinkel()
    {
        return Math.sin( this.gibDrehung() * Math.PI / 180 );
    }


    /**
     * liefert den Cosinus des Drehwinkels der Grafik
     *
     * @return  Cosinus des aktuellen Drehwinkels
     */
    public double cos_Drehwinkel()
    {
        return Math.cos( this.gibDrehung() * Math.PI / 180 );
    }

}
