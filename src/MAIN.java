import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Erzeugt das SPIEL Objekt und setzt Fenster Einstellungen.
 * Es ist die höchste Klasse in der Hierarchie
 *
 *
 */


public class MAIN {
    public static int x = 1500;
    public static int y = 1000;

    public static int blackThreshold = 4;// für die Kolliderbildbearbeitung
    public static int whiteThreshold = 250;// für die Kolliderbildbearbeitung

    public static void main(String[] args){
        SPIEL spiel  = new SPIEL();
        spiel.fokusSetzten();

    }


}
