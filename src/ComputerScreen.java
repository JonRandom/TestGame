import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Knoten;
import ea.Raum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComputerScreen extends Knoten {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    private Bild mainOverlayImg;
    private FadeScreen fadeScreen;
    private boolean activ = false;


    public ComputerScreen(){

        //Bilder
        mainOverlayImg = new Bild(0,0, "./Assets/Computer/overlay.png");
        mainOverlayImg.sichtbarSetzen(false);
        this.add(mainOverlayImg);

        //FADESCRENN -> Ganz oben drauf
        fadeScreen = new FadeScreen();
        this.add(fadeScreen);
    }
    public void openPC(){
        this.activ = true;
        //fadeScreen.startBlackFade(); //das läuft dann durch
        showWindow();
    }

    public void closePC(){
        this.activ = false;
        hideWindow();
    }

    public void showWindow(){
        mainOverlayImg.sichtbarSetzen(true);
        //...
    }

    public void hideWindow(){
        mainOverlayImg.sichtbarSetzen(false);
        //...
    }

    public boolean isActiv() {
        return activ;
    }

}
