/**
 * Die Klasse wird verwendet um die Kollision des nächsten Schrittes des Spielers zu testen.
 * Der Dummyplayer wird vorbewegt und dann, wenn keine Kollision besteht, auch der angezeigte Spieler nachgezogen.
 *
 */

import ea.Bild;
import ea.Knoten;

public class DummyPlayer extends Knoten {

    private Bild image;
    private float x;
    private float y;
    public DummyPlayer(int posX,int posY){
        x = posX;
        y = posY;
        image = new Bild(posX,posY,"./Assets/SpielerTest/Spieler-L0.png");

        image.sichtbarSetzen(false);
        this.add(image);
    }

    @Override
    public void positionSetzen(float x, float y) {
        this.x = x;
        this.y = y;
        super.positionSetzen(x, y);
    }

    @Override
    public void verschieben(float dX, float dY) {
        this.x = this.x +dX;
        this.y = this.y +dY;
        super.verschieben(dX, dY);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}
