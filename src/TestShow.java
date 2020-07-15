import ea.Bild;
import ea.Knoten;

public class TestShow extends Knoten {
    Bild b1;


    public TestShow(){
        super();
        b1= new Bild(500,500,"./Assets/MouseC.png");
        this.add(b1);
        System.out.println(getMasse());
    }
    public Bild ReturnObject(){
        return b1;
    }
}
