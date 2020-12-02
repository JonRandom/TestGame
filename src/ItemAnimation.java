import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Knoten;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Anzeige für die Items
 */

public class ItemAnimation extends Knoten {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    //für die JSON
    private List<ItemController.Item> items = new ArrayList<>();
    private final String itemInitFilePath = MAIN.itemInitFilePath;

    //Für die Bilder
    private final String itemsFilePath = MAIN.itemsFilePath;
    private Map<String, Bild> bigItemImgs;
    private Bild headerImg;

    //display length and counter
    private final int displayduration = 600;// in game ticks
    private int tickCounter = 0;

    //MAIN ACTIVE STATE
    private boolean activ = false;

    public ItemAnimation() {
        bigItemImgs = new HashMap<String, Bild>();
        readJSON();

        try {
            headerImg = new Bild(0, 0, itemsFilePath + "header.png");
            headerImg.sichtbarSetzen(false);
            this.add(headerImg);
            for (ItemController.Item item : items) {
                Bild newImg = new Bild(0, 0, itemsFilePath + item.getName() + "Big.png");
                bigItemImgs.put(item.getName(), newImg); //Füllt Map mit <NAME, BigItemBild>

                newImg.sichtbarSetzen(false);
                this.add(newImg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ItemAnimation: Fehler bei den Item Bilder");
        }

    }

    public void openAnimation(String itemName) {
        activ = true;
        tickCounter = 0;
        headerImg.sichtbarSetzen(true);
        bigItemImgs.get(itemName).sichtbarSetzen(true);
    }

    public void tick() {
        if (activ) {
            tickCounter++;
            if (tickCounter >= displayduration) {
                tickCounter = 0;
                hideEverything();
            }
        }

    }

    public void hideEverything() {
        activ = false;
        headerImg.sichtbarSetzen(false);
        for (String key : bigItemImgs.keySet()) {
            bigItemImgs.get(key).sichtbarSetzen(false);
        }
    }

    public boolean isActiv() {
        return activ;
    }

    private void readJSON() {


        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ItemController.Item.class, new ItemController.Deserializer())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(itemInitFilePath));
            Type ListType = new TypeToken<List<ItemController.Item>>() {
            }.getType();


            items = gson.fromJson(bufferedReader, ListType);
            System.out.println(ANSI_GREEN + "ItemController: JSON(" + itemInitFilePath + ") erfolgreich gelesen" + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "ItemController: Ein Fehler beim Lesen der JSON(" + itemInitFilePath + ") Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }
    }


}
