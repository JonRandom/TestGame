/**
 * Erzeugt das SPIEL Objekt und setzt Fenster Einstellungen.
 * Es ist die höchste Klasse in der Hierarchie
 *
 *
 */


public class MAIN {
    public static int x = 1000;
    public static int y = 1000;

    public static int blackThreshold = 4;// für die Kolliderbildbearbeitung
    public static int whiteThreshold = 250;// für die Kolliderbildbearbeitung

    public static void main(String[] args){
        SPIEL spiel  = new SPIEL(x,y);
        spiel.fokusSetzten();

        //ColliderShape CS = new ColliderShape(200,300,100,200);
        //System.out.println(CS.isIn(200,100));
        //System.out.println(CS.isIn(200,301));
        //System.out.println(CS.isIn(250,501));

        /* ImageCollider Tests
        ImageCollider ICT = new ImageCollider("./Assets/Tests/coll2.png");
        System.out.println("CollTest" + ICT.TestColl(0,0));
        System.out.println("CollTest" +ICT.TestColl(50,0));
        System.out.println("CollTest" +ICT.TestColl(85,0));
        System.out.println("CollTest" +ICT.TestColl(200,0));
        System.out.println("CollTest" +ICT.TestColl(13,140));
        System.out.println("CollTest" +ICT.TestColl(200,140));

         */
    }

}
