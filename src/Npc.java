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

    private int houseNumber;

    public Npc(int PosX, int PosY, String path, int hN){
        this.posX=PosX;
        this.posY=PosY;

        this.houseNumber = hN;

        try{
            img = new Bild(PosX,PosY,path);
            this.add(img);
        }
        catch(Exception e){
            System.out.println("Npc: Fehler beim importieren der Datei");
            System.out.println("Npc: " + e);
        }

    }


    @Override
    public void verschieben(float dX, float dY) {
        posX=posX +dX;
        posY=posY +dY;
        super.verschieben(dX, dY);

    }

    public float getPosY() {
        return posY;
    }

    public float getPosX() {
        return posX;
    }

    public int getHouseNumber() {
        return houseNumber;
    }
}
