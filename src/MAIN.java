/*
Erzeugt das SPIEL Objekt und setzt Fenster Einstellungen.
 */

import java.io.FileNotFoundException;

public class MAIN {
    public static void main(String[] args) throws FileNotFoundException {
        SPIEL spiel  = new SPIEL(1000,1000,true);
        spiel.fokusSetzten();
        //spiel.mausIconSetzen("./Assets/MouseC.png",0,0);

        //MapGSONTester GsonT = new MapGSONTester();
        //MapTest MT = new MapTest();

    }
}
