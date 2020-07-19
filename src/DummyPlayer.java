/**
 * Die Klasse wird verwendet um die Kollision des nächsten Schrittes des Spielers zu testen.
 * Der Dummyplayer wird vorbewegt und dann, wenn keine Kollision besteht, auch der angezeigte Spieler nachgezogen.
 *
 */

import ea.Bild;
import ea.Knoten;

public class DummyPlayer extends Knoten {

    private Bild image;
    public DummyPlayer(int posX,int posY){
        image = new Bild(posX,posY,"./Assets/SpielerTest/Spieler-L0.png");

        image.sichtbarSetzen(false);
        this.add(image);
    }
}
