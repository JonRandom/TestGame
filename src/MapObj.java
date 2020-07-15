import ea.Knoten;
import ea.Rechteck;

public class MapObj extends Knoten {

    private Rechteck r1;
    private Rechteck r2;


    public MapObj(){
        super();
        r1 = new Rechteck(200,200,100,100);
        r2 = new Rechteck(200,400,100,100);


        this.add(r1,r2);
    }
}
