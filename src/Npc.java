import ea.Bild;
import ea.Knoten;

/**
 * NPCs sind passive Figuren, die mit den der Spieler interagieren kann.
 * Sie laufen z.B in ihrem Haus rum
 *
 *
 */
public class Npc extends Knoten {

    private String name;
    private Bild img;

    private float posX;
    private float posY;

    public Npc(int PosX, int PosY){
        this.posX=PosX;
        this.posY=PosY;

        img = new Bild(PosX,PosY,"./Assets/Player.png");
        this.add(img);

    }


    @Override
    public void verschieben(float dX, float dY) {
        posX=posX +dX;
        posY=posY +dY;
        super.verschieben(dX, dY);

    }
}
