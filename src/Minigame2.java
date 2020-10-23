import ea.Knoten;
import ea.Bild;
import ea.Punkt;

public class Minigame2 extends Knoten {

    private Player AP;

    private int offsetX = 0;
    private int offsetY = 0;

    private int tileCount = 8; // anzahl der tiles
    private int leverCount = 8; // anzahl der Lever animation bilder

    private int tileSize = 200; // SIE MUESSEN RECHTECKIG SEIN
    private int rowPos = 0;// 3 Reihen 0-2

    private int originalSpeed = 10;
    private int speed;

    private int bottomEdge = 800;
    private int row0_firstPos = 0;
    private int row1_firstPos = 0;
    private int row2_firstPos = 0;

    private Punkt row0_TL_POS = new Punkt(200,200);
    private Punkt row1_TL_POS = new Punkt(500,200);//100 Abstand zur Vorherigen
    private Punkt row2_TL_POS = new Punkt(800,200);

    private String mainPath = "./Assets/Minigames/SlotMachine/";

    private Bild frameImg;

    private Bild[] leverImgs = new Bild[10];
    private Bild[] row0_tileImgs = new Bild[tileCount];
    private Bild[] row1_tileImgs = new Bild[tileCount];
    private Bild[] row2_tileImgs = new Bild[tileCount];

    private boolean rolling = false;

    private int decelerationCounterOriginal = 80;
    private int decelerationCounter;

    private boolean active = false;


    public Minigame2(Player AP){
        this.AP = AP;
        InitImgs();
    }


    private void InitImgs(){

        //für den Hebel animation
        //für alle Bilder der Reihe/tiles
        //benennung z.B tile0
        for(int i = 0; i< tileCount; i++){
            String path = mainPath  + "tile" + i + ".png";
            try {
                row0_tileImgs[i] = new Bild(path);
                row1_tileImgs[i] = new Bild(path);
                row2_tileImgs[i] = new Bild(path);
                this.add(row0_tileImgs[i],row1_tileImgs[i], row2_tileImgs[i]);
                row0_tileImgs[i].sichtbarSetzen(false);
                row1_tileImgs[i].sichtbarSetzen(false);
                row2_tileImgs[i].sichtbarSetzen(false);
            }
            catch(Exception e){
                System.out.println("Minigame2: "  + e);
            }
        }

        //für den Hebel animation
        //benennung z.B lever0
        for(int i = 0; i< leverCount; i++){
            String path = mainPath  + "lever" + i + ".png";
            try {
                leverImgs[i] = new Bild(path);
                this.add(leverImgs[i]);
                leverImgs[i].sichtbarSetzen(false);
            }
            catch(Exception e){
                System.out.println("Minigame2: "  + e);
            }
        }
        //für das FrameBild GANZ OBEN
        try {
            String path = mainPath  + "frame.png";
            frameImg = new Bild(path);
            this.add(frameImg);
            frameImg.sichtbarSetzen(false);
        }
        catch(Exception e){
            System.out.println("Minigame2: "  + e);
        }
    }


    public void startGame(){
        //offsetX = (int)AP.getPosX();
        //offsetY = (int)AP.getPosY();
        active = true;
        frameImg.sichtbarSetzen(true);
        rowPos = 0;
        row0_firstPos = 0;
        row1_firstPos = 0;
        row2_firstPos = 0;
        rolling = true;
        speed = originalSpeed;
        setRows();
        decelerationCounter = decelerationCounterOriginal;
    }
    public void tick(){
        if(active && rolling){
            switch(rowPos){
                case(0):
                    for(int i = 0; i< tileCount; i++){
                        //row0_tileImgs[i].sichtbarSetzen(true);
                        System.out.println("Minigame2: Row0 wird nach unten verschoben");
                        row0_tileImgs[i].verschieben(0,speed); // nach oben verschieben
                    }
                    if(row0_tileImgs[row0_firstPos].getY() >= bottomEdge + offsetY){
                        row0_tileImgs[row0_firstPos].verschieben(0,-(tileCount)*tileSize);//obenstellen
                        row0_firstPos++;//das naechste bild als ersten angeben
                        if(row0_firstPos>= tileCount){
                            row0_firstPos = 0;
                        }
                    }
                    break;
                case(1):
                    for(int i = 0; i< tileCount; i++){
                        //row0_tileImgs[i].sichtbarSetzen(true);
                        row1_tileImgs[i].verschieben(0,speed); // nach oben verschieben
                    }
                    if(row1_tileImgs[row1_firstPos].getY() >= bottomEdge + offsetY){
                        row1_tileImgs[row1_firstPos].verschieben(0,-(tileCount)*tileSize);//obenstellen
                        row1_firstPos++;//das naechste bild als ersten angeben
                        if(row1_firstPos>= tileCount){
                            row1_firstPos = 0;
                        }
                    }
                    break;
                case(2):
                    for(int i = 0; i< tileCount; i++){
                        //row0_tileImgs[i].sichtbarSetzen(true);
                        row2_tileImgs[i].verschieben(0,speed); // nach oben verschieben
                    }
                    if(row2_tileImgs[row2_firstPos].getY() >= bottomEdge + offsetY){
                        row2_tileImgs[row2_firstPos].verschieben(0,-(tileCount)*tileSize);//obenstellen
                        row2_firstPos++;//das naechste bild als ersten angeben
                        if(row2_firstPos>= tileCount){
                            row2_firstPos = 0;
                        }
                    }

                    break;

            }
            //speed--;//wird langsammer
            decelerationCounter--;
            if(decelerationCounter <= 0){
                decelerationCounter = decelerationCounterOriginal;
                speed--;
            }
            if(speed <= 0){
                if(rowPos >= 2){ //falls noch nicht reihe 2 fdann eine reihe weiter mit speed = 10;
                    System.out.println("Minigame2: " + rowPos + " ist jz unten angekommen");
                    rolling = false;
                }
                else{
                    System.out.println("Minigame2: " + rowPos + " ist jz unten angekommen");
                    speed = originalSpeed;
                    rowPos++;
                }
            }


        }
    }

    private void setRows(){
        for(int i = 0; i< tileCount; i++){
            row0_tileImgs[i].sichtbarSetzen(true);
            row0_tileImgs[i].positionSetzen(row0_TL_POS);
            row0_tileImgs[i].verschieben(offsetX,offsetY);
            row0_tileImgs[i].verschieben(0,-(i+1)*tileSize); // nach oben verschieben
        }
        for(int i = 0; i< tileCount; i++){
            row1_tileImgs[i].sichtbarSetzen(true);
            row1_tileImgs[i].positionSetzen(row1_TL_POS);
            row1_tileImgs[i].verschieben(offsetX,offsetY);
            row1_tileImgs[i].verschieben(0,-(i+1)*tileSize); // nach oben verschieben
        }
        for(int i = 0; i< tileCount; i++){
            row2_tileImgs[i].sichtbarSetzen(true);
            row2_tileImgs[i].positionSetzen(row2_TL_POS);
            row2_tileImgs[i].verschieben(offsetX,offsetY);
            row2_tileImgs[i].verschieben(0,-(i+1)*tileSize); // nach oben verschieben
        }

    }

    public void lowerSpeed() {
        speed--;
    }
    public boolean isActive() {
        return active;
    }
}
