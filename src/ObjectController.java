import ea.Bild;
import ea.Knoten;
import ea.Vektor;

/**
 * Steuert z.B Autos
 */
public class ObjectController extends Knoten{

    Auto Auto1;

    public ObjectController(){

        Auto1= new Auto("./Assets/Tests/car.png");
        this.add(Auto1);
    }



    private class Auto extends Knoten {
        private Bild pic;
        private Auto(String path){
            pic = new Bild(12,12,path);
            this.add(pic);
            pic.newtonschMachen();
            pic.geschwindigkeitSetzen(new Vektor(1,1));
            pic.passivMachen();


        }
    }
}
