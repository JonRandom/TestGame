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
    private Bild post1;
    private Bild post2;

    private boolean activ = false;


    public ComputerScreen() {

        //Bilder
        mainOverlayImg = new Bild(0, 0, "./Assets/Computer/overlay.png");
        mainOverlayImg.sichtbarSetzen(false);
        this.add(mainOverlayImg);
        post1 = new Bild(0, 0, "./Assets/Computer/post1.png");
        post2 = new Bild(0, 0, "./Assets/Computer/post2.png");
        this.add(post1, post2);
        post1.sichtbarSetzen(false);
        post2.sichtbarSetzen(false);

    }

    public void openPC() {
        this.activ = true;
        showWindow();
    }
    public void viewPost1(){
        System.out.println("ComputerScreen: viewPost1() aufgerufen" );
        hideWindow();
        this.activ = true;
        showWindow();
        post1.sichtbarSetzen(true);
    }
    public void viewPost2(){
        System.out.println("ComputerScreen: viewPost1() aufgerufen" );
        hideWindow();
        this.activ = true;
        showWindow();
        post2.sichtbarSetzen(true);
    }

    public void closePC() {
        this.activ = false;
        hideWindow();
    }

    public void showWindow() {
        mainOverlayImg.sichtbarSetzen(true);

    }

    public void hideWindow() {
        mainOverlayImg.sichtbarSetzen(false);
        post1.sichtbarSetzen(false);
        post2.sichtbarSetzen(false);

    }

    public boolean isActiv() {
        return activ;
    }

}
