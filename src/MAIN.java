/**
 * Erzeugt das SPIEL Objekt und setzt Fenster Einstellungen.
 * Es ist die höchste Klasse in der Hierarchie
 *
 *
 */


public class MAIN {
    public static int x = 1600;
    public static int y = 800;
    public static void main(String[] args){
        SPIEL spiel  = new SPIEL(x,y);
        spiel.fokusSetzten();

        //ColliderShape CS = new ColliderShape(200,300,100,200);
        //System.out.println(CS.isIn(200,100));
        //System.out.println(CS.isIn(200,301));
        //System.out.println(CS.isIn(250,501));



    }

}
