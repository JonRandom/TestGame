import ea.Sound;

public class SoundController {

    //Main Stuff
    private String mainPath = MAIN.musicMainPath;

    //Dateien
    private Sound sadMusic;
    private Sound happyMusic;

    //https://freesound.org/people/Tabook/sounds/400329/
    private Sound doorSound;


    public SoundController(){
        sadMusic = new Sound(mainPath + "musik1.mp3");
        //happyMusic = new Sound(mainPath + "musik1.mp3");
        //sadMusic.loop();
        doorSound = new Sound(mainPath + "door.wav");
    }

    public void playDoorSound(){
        doorSound.play();
    }

    public void playSadEnding(){
        sadMusic.loop();
    }
    public void playGoodEnding(){
        happyMusic.loop();
    }

    public void stopAllMusic(){
        sadMusic.stop();
        happyMusic.stop();
        //ergänzen
    }
}
