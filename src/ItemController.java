import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import ea.Bild;
import ea.Game;
import ea.Knoten;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemController extends Knoten {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    //für die JSON
    private List<Item> items = new ArrayList<>();
    private final String itemInitFilePath = "./Assets/Files/Items.json";

    //für Kollision und Items sammeln
    private Player activPlayer;
    private GameSaver gamesaver;

    public ItemController(Player ap, GameSaver gs) {
        this.activPlayer = ap;
        this.gamesaver = gs;

        readJSON();
        addAllItems();
        System.out.println(items.toString());
    }
    public boolean checkForCollision(){
        boolean coll = false;
        for (Item item : items) {  //geht die JSON durch und
            if(activPlayer.schneidet(item) && item.visible){ //wenn der Spieler ein sichbares Item schneidet
                coll = true;
            }
        }
        return coll;
    }
    public String getCollidingItemName(){
        Item collItem = null;
        for (Item item : items) {  //geht die JSON durch und
            if(activPlayer.schneidet(item) && item.visible){ //wenn der Spieler ein sichbares Item schneidet
                collItem = item;
            }
        }
        if(collItem == null){
            System.out.println("ItemController: FEHLER: ER schneidet KEIN Item");
            return null;
        } else{
            return collItem.name;
        }
    }

    public void hideCollidingItem(){
        Item collItem = null;
        for (Item item : items) {  //geht die JSON durch und
            if(activPlayer.schneidet(item) && item.visible){ //wenn der Spieler ein sichbares Item schneidet
                collItem = item;
            }
        }
        if(collItem == null){
            System.out.println("ItemController: FEHLER: ER schneidet KEIN Item");
        } else{
            System.out.println("ItemController: Der Spieler schneidet echt ein Item und es wird jz ausgeblendet, Itemname=("+ collItem.name + ").");
            collItem.hideItem();
        }

    }
    private void addAllItems(){
        for (Item item : items) {  //geht die JSON durch und
            this.add(item);
        }
    }

    private void readJSON() {


        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ItemController.Item.class, new ItemController.Deserializer())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(itemInitFilePath));
            Type ListType = new TypeToken<List<Item>>() {
            }.getType();


            items = gson.fromJson(bufferedReader, ListType);
            System.out.println(ANSI_GREEN + "ItemController: JSON(" + itemInitFilePath + ") erfolgreich gelesen" + ANSI_RESET);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            System.out.println(ANSI_PURPLE + "ItemController: Ein Fehler beim Lesen der JSON(" + itemInitFilePath + ") Datei. Entweder Pfad flasch, oder JSON Struktur." + ANSI_RESET);

        }
    }


    private class Item extends Knoten {

        private String mainPath = "./Assets/Items/";

        private Bild img;

        @Expose
        private String name;
        @Expose
        private float posX;
        @Expose
        private float posY;
        @Expose
        private int houseN;
        @Expose
        private boolean visible;

        private Item(String name, float x, float y, int hn, boolean vb) {
            this.name = name;
            this.posX = x;
            this.posY = y;
            this.houseN = hn;
            this.visible = vb;

            try {
                String path = mainPath + name + ".png";
                img = new Bild(posX, posY, path);
                this.add(img);
                this.sichtbarSetzen(visible);
            } catch (Exception e) {
                System.out.println("Item: Fehler beim importieren der Datei");
                System.out.println("Item: " + e);
            }

        }

        private void showItem(){
            visible = true;
            this.sichtbarSetzen(true);
        }
        private void hideItem(){
            visible = false;
            this.sichtbarSetzen(false);
        }

        @Override
        public String toString() {
            return "Item{" +
                    "mainPath='" + mainPath + '\'' +
                    ", img=" + img +
                    ", name='" + name + '\'' +
                    ", posX=" + posX +
                    ", posY=" + posY +
                    ", houseN=" + houseN +
                    ", visible=" + visible +
                    '}';
        }
    }

    public class Deserializer implements JsonDeserializer<Item> {

        public Item deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException{

            JsonObject jsonObject = (JsonObject) jsonElement;
            return new Item(jsonObject.get("name").getAsString(), jsonObject.get("posX").getAsInt(),jsonObject.get("posY").getAsInt(), jsonObject.get("houseN").getAsInt(), jsonObject.get("visible").getAsBoolean());
        }
    }
}
