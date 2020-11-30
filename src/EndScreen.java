import ea.Knoten;
import ea.Bild;

/**
 * Auch Abspann genannt
 */

public class EndScreen extends Knoten {

    private SoundController soundC;

    public EndScreen(SoundController sc){
        this.soundC = sc;
    }

    public void playSadEnding(){
        soundC.playSadEnding();
    }

    public void playGoodEnding(){
        soundC.playGoodEnding();
    }

}
