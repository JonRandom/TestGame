import ea.Sound;

public class SoundController {

    //Main Stuff
    private String mainPath = MAIN.musicMainPath;

    //Dateien
    private Sound sadMusic;
    private Sound happyMusic;
    private Sound title1;
    private Sound title2;
    private Sound lysander;

    //https://freesound.org/people/Tabook/sounds/400329/
    private Sound doorSound;

    private final int pauseLength = 12000;
    private int tickCounter = pauseLength / 4;

    private boolean mute = false;


    public SoundController() {
        sadMusic = new Sound(mainPath + "sad.mp3");
        happyMusic = new Sound(mainPath + "happy.mp3");
        lysander = new Sound(mainPath + "lysander.mp3");
        title1 = new Sound(mainPath + "title1.mp3");
        title2 = new Sound(mainPath + "title2.mp3");

        doorSound = new Sound(mainPath + "door.wav");
    }


    public void tickMusic() {
        tickCounter++;
        if (tickCounter >= pauseLength) {
            tickCounter = 0;
            System.out.println("SoundController: Neue Music wird angefangen!");
            stopAllMusic();
            playRandomSound();
        }
    }

    public void toggleMute() {
        if (mute) {
            mute = false;
            System.out.println("SoundController: Game wurde entmutet");
            playRandomSound();

        } else {
            System.out.println("SoundController: Game wurde gemutet");
            mute = true;
            stopAllMusic();
        }
    }

    public void startTitleMusic() {
        double r = Math.random();
        if (r >= 0.5) {
            title1.play();
        } else {
            title2.play();
        }
    }

    public void playDoorSound() {
        if (!mute) {
            doorSound.play();
        }
    }

    public void playRandomSound() {
        if (!mute) {
            double r = Math.random();
            if (r >= 0.5) {
                lysander.play();
            } else {
                happyMusic.play();
            }
        }
    }

    public void playSadMusic() {
        if (!mute) {
            sadMusic.play();
        }
    }

    public void playHappyMusic() {
        if (!mute) {
            happyMusic.play();
        }

    }


    public void stopAllMusic() {
        sadMusic.stop();
        happyMusic.stop();
        lysander.stop();
        title1.stop();
        title2.stop();
        //ergänzen
    }
}
