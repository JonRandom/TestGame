import ea.*;
import ea.edu.*;

public class SPIEL
{
    /**
     * Die Anzeige des Spiels.
     */
    private static AnzeigeE anzeige;

    /**
     * Dieser Zaehler ermoeglicht den Tik-Tak-Wechsel.
     */
    private int zaehler;




    /**
     * Erstellt ein Spiel. Startet die Anzeige.
     *
     * @param   punkteLinks     ist dieser Wert <code>true</code>, so sieht man
     *                          links eine Punkteanzeige. Ist er <code>false</code>
     *                          sieht man keine.
     *
     * @param   punkteRechts    ist dieser Wert <code>true</code>, so sieht man
     *                          rechts eine Punkteanzeige. Ist er <code>false</code>
     *                          sieht man keine.
     *
     * @param   maus            ist dieser Wert <code>true</code>, wird eine Maus im
     *                          Spiel angezeigt und verwendet. Ist er
     *                          <code>false</code>, gibt es keine Maus.
     */
    public SPIEL(int breite, int hoehe, boolean punkteLinks, boolean punkteRechts, boolean maus)
    {
        //Zaehler fuer Tick, Tack, ...
        zaehler = 0;
        anzeige = new AnzeigeE(breite, hoehe);

        //Punkteanzeige
        anzeige.punkteLinksSichtbarSetzen(punkteLinks);
        anzeige.punkteRechtsSichtbarSetzen(punkteRechts);

        //Maus ggf. aktivieren
        if(maus)
        {
            anzeige.klickReagierbarAnmelden(this, true);
        }

        //Tastatur
        anzeige.tastenReagierbarAnmelden(this);

        //Ticker
        //Alle 500 Millisekunden (=Jede halbe Sekunde) ein Tick
        anzeige.tickerAnmelden(this, 500);
    }



    /*
     * Erstellt ein einfaches Spiel ohne Anzeige und Maus.<br />
     * Das Spiel hat somit Ticker und Tastatureingaben.
     */






    /**
     * Wird regelmaessig aufgerufen. So kommt Bewegung ins Spiel!
     */
    public void tick()
    {
        //Einfache Bildschirmausgabe. Kann spaeter in Subklasse beliebig ueberschreiben werden.
        zaehler++;
        zaehler = zaehler % 2;
        if (zaehler == 1)
        {
            System.out.println("Tick!");
        }
        else
        {
            System.out.println("Tack!");
        }
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
        System.out.println("Taste mit Kuerzel " + tastenkuerzel +
                " wurde gedrueckt");
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


    /**
     * Sorgt dafuer, dass sowohl der rechte als auch der linke Punktestand sichtbar ist.
     */
    public void allePunkteSichtbar()
    {
        anzeige.punkteLinksSichtbarSetzen(true);
        anzeige.punkteRechtsSichtbarSetzen(true);
    }


    /**
     * Sorgt dafuer, dass nur der linke Punktestand sichtbar ist.
     */
    public void nurRechtePunkteSichtbar()
    {
        anzeige.punkteLinksSichtbarSetzen(false);
        anzeige.punkteRechtsSichtbarSetzen(true);
    }


    /**
     * Sorgt dafuer, dass nur der rechte Punktestand sichtbar ist.
     */
    public void nurLinkePunkteSichtbar()
    {
        anzeige.punkteLinksSichtbarSetzen(true);
        anzeige.punkteRechtsSichtbarSetzen(false);
    }


    /**
     * Sorgt dafuer, dass weder der rechte noch der linke Punktestand sichtbar ist.
     */
    public void allePunkteUnsichtbar()
    {
        anzeige.punkteLinksSichtbarSetzen(false);
        anzeige.punkteRechtsSichtbarSetzen(false);
    }


    /**
     * Setzt den linken Punktestand. Aenderungen sind nur sichtbar,
     * wenn auch der linke Punktestand sichtbar ist.
     *
     * @param   pl      Der neue linke Punktestand.
     */
    public void punkteLinksSetzen(int pl)
    {
        anzeige.punkteLinksSetzen(pl);
    }


    /**
     * Setzt den rechten Punktestand. Aenderungen sind nur sichtbar,
     * wenn auch der rechte Punktestand sichtbar ist.
     *
     * @param   pr      Der neue rechte Punktestand.
     */
    public void punkteRechtsSetzen(int pr)
    {
        anzeige.punkteRechtsSetzen(pr);
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
