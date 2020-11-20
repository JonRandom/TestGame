import com.google.gson.*;
import com.google.gson.annotations.Expose;
import ea.Bild;
import ea.Knoten;

import java.lang.reflect.Type;

public class NPC2 extends Knoten {
    @Expose
    public String name;
    //@Expose
    private Bild img;

    @Expose
    private float posX;
    @Expose
    private float posY;
    @Expose
    private int houseNumber;

    public NPC2(int pX, int pY, int hN, String n){
        this.posX=pX;
        this.posY=pY;
        this.name = n;
        this.houseNumber = hN;

        try{
            String path = "./Assets/NPCs/" + name + ".png";
            img = new Bild(pX,pY,path);
            this.add(img);
        }
        catch(Exception e){
            System.out.println("NPC2: Fehler beim importieren der Datei");
            System.out.println("NPC2: " + e);
        }

    }
    @Override
    public void verschieben(float dX, float dY) {
        posX=posX +dX;
        posY=posY +dY;
        super.verschieben(dX, dY);

    }

    public float getPosY() {
        return posY;
    }

    public float getPosX() {
        return posX;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public boolean isInHouse(){
        if(houseNumber == -1){
            return false;
        }
        else if(houseNumber >= 0){
            return true;
        }
        else{
            System.out.println("NPC2: Fetter Fail: Spieler ist in dem negativen Haus: " + houseNumber + " und nicht >=(-1)");
            return false;
        }
    }


    public static class Deserializer implements JsonDeserializer<NPC2> {

        public NPC2 deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException{

            JsonObject jsonObject = (JsonObject) jsonElement;
            return new NPC2(jsonObject.get("posX").getAsInt(), jsonObject.get("posY").getAsInt(), jsonObject.get("houseNumber").getAsInt(), jsonObject.get("name").getAsString());
        }
    }
}
