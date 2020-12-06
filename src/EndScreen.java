import ea.Knoten;
import ea.Bild;

/**
 * Auch Abspann genannt
 */

public class EndScreen extends Knoten {

    private SoundController soundC;
    private boolean active = false;
    private Bild endingImg;
    private Bild backgroundImg;

    public EndScreen(SoundController sc){
        this.soundC = sc;
        endingImg = new Bild(0,0,MAIN.endScreenImgPath);
        backgroundImg = new Bild(0,0,MAIN.endScreenBackgroundImgPath);
        this.add(backgroundImg, endingImg);
        endingImg.sichtbarSetzen(false);
        backgroundImg.sichtbarSetzen(false);
    }

    public void playEnding(boolean isSad){
        soundC.stopAllMusic();
        active = true;
        if(isSad){
            playSadEnding();
        } else {
            playGoodEnding();
        }
        endingImg.sichtbarSetzen(true);
        backgroundImg.sichtbarSetzen(true);
    }

    public void tick(){
        if(active){
            endingImg.verschieben(0,-1);
        }
    }

    public void playSadEnding(){
        soundC.playSadMusic();
    }

    public void playGoodEnding(){
        soundC.playHappyMusic();
    }

    public boolean isActive() {
        return active;
    }
}
