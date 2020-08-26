import ea.Bild;
import ea.Knoten;
import ea.Vektor;

public class BallTest extends Knoten {

    private Bild ball = new Bild(200,200,"./Assets/MouseC.png");

    public BallTest(int x, int y, int dx, int dy){

        this.add(ball);
        this.aktivMachen();
        this.newtonschMachen();
        this.positionSetzen(x,y);
        this.geschwindigkeitHinzunehmen(new Vektor(dx,dy));
    }

}
