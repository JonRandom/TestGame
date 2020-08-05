import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;


public class MapTest {

    BufferedReader bufferedReader;

    String dollarJson = "{ '1': {'name':'Schule', 'posX': 200,'posY': 200,'width': 60,'height': 90}, '2': {'name':'CLUB', 'posX': 600,'posY': 200,'width': 200,'height': 90}}";

    public MapTest() throws FileNotFoundException {
        Gson gson = new Gson();
        readJSON();

        Type amountCurrencyType = new TypeToken<HashMap<String, Haus>>(){}.getType();

        HashMap<String, Haus> MAP = gson.fromJson(bufferedReader, amountCurrencyType);
        System.out.println(MAP);

        for( String key : MAP.keySet() ) {

            Haus value = MAP.get(key);
            System.out.println(value.posX);
        }

    }
    public void readJSON() throws FileNotFoundException {
        bufferedReader = new BufferedReader(
                new FileReader("./Assets/Karte/Karte.json"));
    }



    public class Haus{
        String name;
        int posX;
        int posY;

        int width;
        int height;
    }
}
