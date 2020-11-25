import ea.*;

/**
 * Steuert z.B Autos
 */
public class ObjectController extends Knoten{


    Auto Auto1;

    public ObjectController(){

        Auto1= new Auto("./Assets/Tests/Testauto.png");
        this.add(Auto1);
    }



    public class Auto extends Knoten {
        public Bild pic;
        private Auto(String path){
            pic = new Bild(220,1550,path);
            this.add(pic);
            pic.newtonschMachen();
            pic.geschwindigkeitSetzen(new Vektor(2,0));
            pic.passivMachen();

            //animationsManager.streckenAnimation(pic,20000, true, true, new Punkt(220, 1550), new Punkt(5300,1550),new Punkt(5300, 2500),new Punkt(220,2500));

        }
    }



}
