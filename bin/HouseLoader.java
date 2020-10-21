import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Knoten;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Diese Klasse kümmert sich darum, dass der Innenraum der Häuser angezeigt werden kann.
 * Sie läd die innen-Bilder und zeigt sie je nach wahl an.
 *

 */
public class HouseLoader extends Knoten {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public Map MapObject;

    private int NumberofB = 8;
    private Bild BackgroundPic;
    private Bild[] Houses;
    private String defaultPath = "./Assets/Houses/";

    private HashMap<String, Map.Haus> MAP;



    public HouseLoader(Map m){
        MapObject=m;
        BackgroundPic = new Bild(0,0,"./Assets/BLANK.png");
        this.add(BackgroundPic);
        BackgroundPic.sichtbarSetzen(false);


        readJSON();
        initArrays();//muss erst gelesen werden, um länge für Arrays zu geben
        initHouses();

        HideHouses();


    }

    /**
     * Empfängt den Player in dem Haus
     */
    public void welcomeGuest(){
        int HouseNumber = MapObject.getHouseNumber();

        if(HouseNumber==-1){
            System.out.println(ANSI_RED + "ERROR in der HouseLoader Klasse: Es wird die Hausnummer von einem Haus angefragt, obwohl der Spieler gar nicht visiting ist!" + ANSI_RESET);
        }
        else {
            HideHouses();
            BackgroundPic.sichtbarSetzen(true);
            Houses[HouseNumber].sichtbarSetzen(true);
        }

    }

    public void HideView(){
        BackgroundPic.sichtbarSetzen(false);
        HideHouses();

    }
    private void HideHouses(){
        for(int i=0;i<NumberofB;i++){

            Houses[i].sichtbarSetzen(false);
        }
    }

    private void initHouses(){
        NumberofB = MAP.size();
        String path ="";

        int i = 0;
        for( String key : MAP.keySet() ) {
            Map.Haus element = MAP.get(key);

            path = defaultPath + element.name + ".png";

            Houses[i] = new Bild(200,200,path);


            //Zentriert Bild anhand von der globalen Window Größe (MAIN.x, MAIN.y)
            float x = Houses[i].getBreite();
            float y = Houses[i].getHoehe();
            float centerX = (MAIN.x/2) - x/2;
            float centerY = (MAIN.y/2) - y/2;

            Houses[i].positionSetzen(centerX,centerY);//zentriert das Bild
            this.add(Houses[i]);

            i++;
        }
    }


    private void initArrays(){
        Houses = new Bild[NumberofB];
    }

    private void readJSON() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./Assets/Files/Karte.json"));

            Type MapType = new TypeToken<HashMap<String, Map.Haus>>(){}.getType();
            MAP = gson.fromJson(bufferedReader, MapType);
        }
        catch(Exception e) {
            System.out.println(ANSI_PURPLE + "ERROR in der HouseLoader Klasse: Ein Fehler beim Lesen der Json Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "ERROR in der HouseLoader Klasse: Eigentlich kann nur das Grafikteam schuld sein..." + ANSI_RESET);

        }

    }
}
