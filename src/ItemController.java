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
    private final String itemInitFilePath = MAIN.itemInitFilePath;

    //für Kollision und Items sammeln
    private Player activPlayer;
    private GameSaver gamesaver;
    private DialogController5 diaController;
    private ItemAnimation itemAnimator;

    public ItemController(Player ap, GameSaver gs, DialogController5 diaC, ItemAnimation iA) {
        this.activPlayer = ap;
        this.gamesaver = gs;
        this.diaController = diaC;
        this.itemAnimator = iA;

        readJSON();
        addAllItems();
        System.out.println(items.toString());
    }

    public void enterHouse(int hN, int offsetX, int offsetY) {
        hideAllItems();
        for (Item item : items) {  //geht die JSON durch und
            //System.out.println("TestetItem: " + item);
            if (item.visible && (item.houseN == hN)) { //wenn der Spieler ein sichbares Item schneidet und es im gleichen Haus ist

                item.positionSetzen(item.relativePosX + offsetX, item.relativePosY + offsetY);
                item.sichtbarSetzen(true);
            }
        }
    }

    public void leaveHouse() {
        hideAllItems();
        for (Item item : items) {  //geht die JSON durch und
            if (item.visible && (item.houseN == -1)) { //wenn der Spieler ein sichbares Item schneidet und es im gleichen Haus ist
                item.positionSetzen(item.relativePosX, item.relativePosY);
                item.sichtbarSetzen(true);
            }
        }
    }

    public boolean checkForCollision() {
        boolean coll = false;
        for (Item item : items) {  //geht die JSON durch und
            if (activPlayer.schneidet(item) && item.visible && item.sichtbar()) { //wenn der Spieler ein sichbares Item schneidet
                coll = true;
            }
        }
        return coll;
    }

    public String getCollidingItemName() {
        diaController.highLightReadyNpcs();
        Item collItem = null;
        for (Item item : items) {  //geht die JSON durch und
            if (activPlayer.schneidet(item) && item.visible && item.sichtbar()) { //wenn der Spieler ein sichbares Item schneidet
                collItem = item;
            }
        }
        if (collItem == null) {
            System.out.println("ItemController: FEHLER: ER schneidet KEIN Item");
            return null;
        } else {
            return collItem.name;
        }
    }

    public void hideCollidingItem() {
        Item collItem = null;
        for (Item item : items) {  //geht die JSON durch und
            if (activPlayer.schneidet(item) && item.visible) { //wenn der Spieler ein sichbares Item schneidet
                collItem = item;
            }
        }
        if (collItem == null) {
            System.out.println("ItemController: FEHLER: Er schneidet KEIN Item");
        } else {
            System.out.println("ItemController: Der Spieler schneidet echt ein Item und es wird jz ausgeblendet, Itemname=(" + collItem.name + ").");
            collItem.hideItem();
            itemAnimator.openAnimation(collItem.name); //öffnet die Große animation zu dem Item
        }


    }

    private void hideAllItems() {
        for (Item item : items) {  //geht die JSON durch und

            item.sichtbarSetzen(false);
        }
    }

    private void addAllItems() {
        for (Item item : items) {  //geht die JSON durch und
            this.add(item);
            item.sichtbarSetzen(false);
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


    public static class Item extends Knoten {

        private String mainPath = MAIN.itemsFilePath;

        private Bild img;

        @Expose
        private String name;
        @Expose
        private float posX;
        @Expose
        private float posY;
        @Expose
        private float relativePosX;
        @Expose
        private float relativePosY;
        @Expose
        private int houseN;
        @Expose
        private boolean visible;

        private Item(String n, float x, float y, float rX, float rY, int hn, boolean vb) {
            this.name = n;
            this.posX = x;
            this.posY = y;

            this.relativePosX = rX;
            this.relativePosY = rY;

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

        private void showItem() {
            visible = true;
            this.sichtbarSetzen(true);
        }

        private void hideItem() {
            visible = false;
            this.sichtbarSetzen(false);
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "mainPath='" + mainPath + '\'' +
                    ", img=" + img +
                    ", name='" + name + '\'' +
                    ", posX=" + posX +
                    ", posY=" + posY +
                    ", relativePosX=" + relativePosX +
                    ", relativePosY=" + relativePosY +
                    ", houseN=" + houseN +
                    ", visible=" + visible +
                    '}';
        }
    }

    public static class Deserializer implements JsonDeserializer<Item> {

        public Item deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            JsonObject jsonObject = (JsonObject) jsonElement;
            return new Item(jsonObject.get("name").getAsString(), jsonObject.get("posX").getAsInt(), jsonObject.get("posY").getAsInt(), jsonObject.get("relativePosX").getAsInt(), jsonObject.get("relativePosY").getAsInt(), jsonObject.get("houseN").getAsInt(), jsonObject.get("visible").getAsBoolean());
        }
    }
}
