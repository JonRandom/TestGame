/**
 * Erzeugt das SPIEL Objekt und setzt Fenster Einstellungen.
 * Es ist die höchste Klasse in der Hierarchie
 *
 *
 */


public class MAIN {
    public static int x = 1920;
    public static int y = 1080;
    public static void main(String[] args){
        SPIEL spiel  = new SPIEL(x,y);
        spiel.fokusSetzten();

        //ColliderShape CS = new ColliderShape(200,300,100,200);
        //System.out.println(CS.isIn(200,100));
        //System.out.println(CS.isIn(200,301));
        //System.out.println(CS.isIn(250,501));

        ImageColliderTest ICT = new ImageColliderTest("./Assets/Tests/coll2.png");
        System.out.println("CollTest" + ICT.TestColl(0,0));
        System.out.println("CollTest" +ICT.TestColl(50,0));
        System.out.println("CollTest" +ICT.TestColl(85,0));
        System.out.println("CollTest" +ICT.TestColl(200,0));
        System.out.println("CollTest" +ICT.TestColl(13,140));
        System.out.println("CollTest" +ICT.TestColl(200,140));
    }

}
