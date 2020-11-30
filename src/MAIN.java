import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Erzeugt das SPIEL Objekt und setzt Fenster Einstellungen.
 * Es ist die höchste Klasse in der Hierarchie
 *
 *
 */


public class MAIN {
    public static int x = 1500;
    public static int y = 1000;

    public static int blackThreshold = 4;// für die Kolliderbildbearbeitung
    public static int whiteThreshold = 250;// für die Kolliderbildbearbeitung

    public static final String gameSaveFilePath = "./Assets/Files/GameSave.json";
    public static final String gameSaveTemplateFilePath = "./Assets/Files/GameSave-Template.json";


    public static final String npcTemplatePath = "./Assets/Files/NPCs-Template.json";
    public static final String npcFilePath = "./Assets/Files/NPCs_NEW.json";
    public static final String npcPositionPath = "./Assets/Files/NPCs_Positions.json";

    public static void main(String[] args){

        SPIEL spiel  = new SPIEL();







    }


}
