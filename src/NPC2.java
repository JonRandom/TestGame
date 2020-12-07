import com.google.gson.*;
import com.google.gson.annotations.Expose;
import ea.Bild;
import ea.BoundingRechteck;
import ea.Knoten;
import ea.Raum;
import ea.internal.collision.Collider;

import java.lang.reflect.Type;

public class NPC2 extends Knoten {

    @Expose
    public String name;
    @Expose
    public String displayName;
    //@Expose
    private Bild img;


    private final String highlighterPath = "./Assets/NPCs/highLight.png";
    private final String specialBedHighlighterPath = "./Assets/NPCs/highLightBed.png";

    private final Bild highLightImg;
    private boolean highlightState = true;

    @Expose
    private float posX;
    @Expose
    private float posY;
    @Expose
    private float relativePosX;
    @Expose
    private float relativePosY;
    @Expose
    private int houseNumber;

    @Expose
    public String lastLine; //letzte DialogZeile

    public NPC2(int pX, int pY, int rX, int rY, int hN, String n, String mlastLine, String dN){
        this.posX=pX;
        this.posY=pY;
        this.relativePosX = rX;
        this.relativePosY = rY;
        this.name = n;
        this.houseNumber = hN;
        this.lastLine = mlastLine;
        this.displayName = dN;



        try{
            String path = "./Assets/NPCs/" + name + ".png";
            img = new Bild(pX,pY,path);

        }
        catch(Exception e){
            System.out.println("NPC2: Fehler beim importieren der Datei");
            System.out.println("NPC2: " + e);
            e.printStackTrace();
        }
        if(name.equals("zzz")){
            highLightImg = new Bild(posX, posY, specialBedHighlighterPath);
        } else{
            highLightImg = new Bild(posX+2, posY-50, highlighterPath);
        }
        this.add(highLightImg); //wird beim highLighten gemacht
        this.add(img);


    }

    public void setLastLine(String code){
        this.lastLine = code;
    }
    @Override
    public void verschieben(float dX, float dY) {
        posX=posX +dX;
        posY=posY +dY;
        super.verschieben(dX, dY);

    }

    @Override
    public void positionSetzen(float x, float y) {
        posX = x;
        posY = y;
        super.positionSetzen(x, y);
    }



    public boolean schneidetNEU(Raum r) {
        if(highlightState){
            return this.schneidet(r);
        } else{
            return img.schneidet(r);
        }

    }


    public void setRelativPos(int x, int y){
        System.out.println("DIE RELITVE POS WIRD GESETZTT AUF : " + y + "| y:" + y);
        relativePosX = x;
        relativePosY = y;
    }

    public float getRelativPosX() {
        return relativePosX;
    }

    public float getRelativPosY() {
        return relativePosY;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
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

    public void setHighlightState(boolean h){
        highlightState = h;
        highLightImg.sichtbarSetzen(highlightState);
    }

    @Override
    public String toString() {
        return "NPC2{" +
                "name='" + name + '\'' +
                ", img=" + img +
                ", highlighterPath='" + highlighterPath + '\'' +
                ", highLightImg=" + highLightImg +
                ", highlightState=" + highlightState +
                ", posX=" + posX +
                ", posY=" + posY +
                ", relativePosX=" + relativePosX +
                ", relativePosY=" + relativePosY +
                ", houseNumber=" + houseNumber +
                ", lastLine='" + lastLine + '\'' +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<NPC2> {

        public NPC2 deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException{

            JsonObject jsonObject = (JsonObject) jsonElement;
            return new NPC2(jsonObject.get("posX").getAsInt(), jsonObject.get("posY").getAsInt(), jsonObject.get("relativePosX").getAsInt(), jsonObject.get("relativePosY").getAsInt(),jsonObject.get("houseNumber").getAsInt(), jsonObject.get("name").getAsString(), jsonObject.get("lastLine").getAsString(), jsonObject.get("displayName").getAsString());
        }
    }
}
