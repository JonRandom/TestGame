/*
Erzeugt das SPIEL Objekt und setzt Fenster Einstellungen.
 */

public class MAIN {
    public static void main(String[] args) {
        SPIEL spiel  = new SPIEL(1000,1000,true);
        spiel.mausIconSetzen("./Assets/MouseC.png",0,0);

    }
}
