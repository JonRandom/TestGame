/**
 * Erzeugt das SPIEL Objekt und setzt Fenster Einstellungen.
 * Es ist die höchste Klasse in der Hierarchie
 *
 *
 */


public class MAIN {
    public static void main(String[] args){
        SPIEL spiel  = new SPIEL(1000,1000);
        spiel.fokusSetzten();

        ColliderShape CS = new ColliderShape(200,300,100,200);
        System.out.println(CS.isIn(200,100));
        System.out.println(CS.isIn(200,301));
        System.out.println(CS.isIn(250,501));



    }
}
