import ea.Knoten;
import ea.Bild;
import ea.Punkt;
import ea.Sound;

import java.util.*;
import java.util.stream.IntStream;

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

    private int bottomEdge = 900;
    private int row0_firstPos = 0;
    private int row1_firstPos = 0;
    private int row2_firstPos = 0;

    private Punkt row0_TL_POS = new Punkt(200,100);
    private Punkt row1_TL_POS = new Punkt(500,100);//100 Abstand zur Vorherigen
    private Punkt row2_TL_POS = new Punkt(800,100);

    private String mainPath = "./Assets/Minigames/SlotMachine/";

    private Bild frameImg;

    private Bild[] leverImgs = new Bild[10];
    private Bild[] row0_tileImgs = new Bild[tileCount];
    private Bild[] row1_tileImgs = new Bild[tileCount];
    private Bild[] row2_tileImgs = new Bild[tileCount];

    private int[] row0_tileImgsPointer = new int[tileCount];
    private int[] row1_tileImgsPointer = new int[tileCount];
    private int[] row2_tileImgsPointer = new int[tileCount];

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
            frameImg = new Bild(offsetX,offsetY,path);
            this.add(frameImg);
            frameImg.sichtbarSetzen(false);
        }
        catch(Exception e){
            System.out.println("Minigame2: "  + e);
        }
    }

    private void shufflePointers(){
        /*
        for(int i = 0; i < tileCount; i++){
            int r = (int)(Math.random() * ((tileCount-1) + 1));
            //System.out.println("Random r: " + r);
            while(contains(row0_tileImgsPointer,r) && i!=0){
                //System.out.println("Gibt es schon , nochmal neu, Stelle i = " + i);
                //System.out.println("Minigame: Bis jz: " + Arrays.toString(row0_tileImgsPointer));
                r = (int)(Math.random() * tileCount);
            }
            row0_tileImgsPointer[i] = r;
        }
        System.out.println("Minigame: row0_tileImgsPointer final: " + Arrays.toString(row0_tileImgsPointer));
         */
        row0_tileImgsPointer = giveShuffeldArray(tileCount);
        System.out.println("Minigame: Random Liste0:" + Arrays.toString(row0_tileImgsPointer));

        row1_tileImgsPointer = giveShuffeldArray(tileCount);
        System.out.println("Minigame: Random Liste1:" + Arrays.toString(row1_tileImgsPointer));

        row2_tileImgsPointer = giveShuffeldArray(tileCount);
        System.out.println("Minigame: Random Liste2:" + Arrays.toString(row2_tileImgsPointer));


        //System.out.println("Minigame: Random Liste:" + Arrays.toString(row0_tileImgsPointer));
        //BSP:. row0_tileImgsPointer = [2, 7, 5, 1, 6, 4, 3, 0]
    }

    /*
    private void shuffleArray(Bild[] array){
        int index,
                temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

     */

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
        decelerationCounter = decelerationCounterOriginal;

        shufflePointers();
        setRows();
    }
    public void tick(){
        if(active && rolling){
            switch(rowPos){
                case(0):
                    //System.out.println("Erstes Element = " + row0_firstPos + " und damit Bild Nummer " + row0_tileImgsPointer[row0_firstPos]);
                    for(int i = 0; i< tileCount; i++){
                        //row0_tileImgs[i].sichtbarSetzen(true);
                        //System.out.println("Minigame2: Row0 wird nach unten verschoben");
                        row0_tileImgs[row0_tileImgsPointer[i]].verschieben(0,speed); // nach oben verschieben
                    }
                    if(row0_tileImgs[row0_tileImgsPointer[row0_firstPos]].getY() >= bottomEdge + offsetY){
                        row0_tileImgs[row0_tileImgsPointer[row0_firstPos]].verschieben(0,-(tileCount)*tileSize);//obenstellen
                        row0_firstPos++;//das naechste bild als ersten angeben
                        if(row0_firstPos>= tileCount){
                            row0_firstPos = 0;
                        }
                        //System.out.println("Neues erstes element = " + row0_firstPos + " und damit Bild Nummer " + row0_tileImgsPointer[row0_firstPos]);
                    }
                    break;


                case(1):
                    for(int i = 0; i< tileCount; i++){
                        //row0_tileImgs[i].sichtbarSetzen(true);
                        //float delta = (bottomEdge - row1_tileImgs[row1_firstPos].getY());
                        //row1_tileImgs[i].verschieben(0,(float)(delta*0.01)); // nach oben verschieben
                        row1_tileImgs[row1_tileImgsPointer[i]].verschieben(0,speed); // nach oben verschieben
                    }
                    if(row1_tileImgs[row1_tileImgsPointer[row1_firstPos]].getY() >= bottomEdge + offsetY){

                        row1_tileImgs[row1_tileImgsPointer[row1_firstPos]].verschieben(0,-(tileCount)*tileSize);//obenstellen
                        row1_firstPos++;//das naechste bild als ersten angeben
                        if(row1_firstPos>= tileCount){
                            row1_firstPos = 0;
                        }
                    }
                    break;


                case(2):
                    for(int i = 0; i< tileCount; i++){
                        //row0_tileImgs[i].sichtbarSetzen(true);
                        row2_tileImgs[row2_tileImgsPointer[i]].verschieben(0,speed); // nach oben verschieben
                    }
                    if(row2_tileImgs[row2_tileImgsPointer[row2_firstPos]].getY() >= bottomEdge + offsetY){
                        row2_tileImgs[row2_tileImgsPointer[row2_firstPos]].verschieben(0,-(tileCount)*tileSize);//obenstellen
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
                    //Game zu ende
                    rolling = false;
                    System.out.println("An unteresten Stelle ist der Jackpot wert = " + Arrays.toString(analyseJackpot()));
                }
                else{
                    System.out.println("Minigame2: " + rowPos + " ist jz unten angekommen");
                    speed = originalSpeed;
                    rowPos++;
                }
            }


        }
    }

    /**
     * Untersucht alle angezeigten(je nach der var checks) Zeilen auch Jackpots mit der Methode checkJackpots(pos);
     * Gibt nur das Ergebnis dieser Methode als Liste zurück und kümmert sich über row0_firstPos Spezialfälle.
     * @return Int[var checks] mit allen ergebnissen des checkJackpots Mehtodeaufrufs.
     */
    private int[] analyseJackpot(){
        int check = 4;//vier Reihen werden angezeigt
        int[] result = new int[check];

        /*
        for(int i = 0;i<check;i++){
            int pos = row0_firstPos - i;
            if(pos<0){
                pos = tileCount + pos;
            }
            System.out.println("Minigame2: Testet Reihe mit i(" + i+ "), welche eig. Reihe " + pos + " ist.");
            result[i] = checkJackpot(pos);
        }
        */
        for(int i = 0;i<check;i++){
            int pos = row0_firstPos + i;
            if(pos>tileCount-1){
                pos = pos-tileCount;
            }
            System.out.println("Minigame2: Testet Reihe mit i(" + i+ "), welche eig. Reihe " + pos + " ist.");
            result[i] = checkJackpot(pos);
        }

        return result;
    }
    /**
     * Gibt mir für die zeile POS den Jackpot indikator zurück
     * @param pos Zeile die verglichen werden soll;
     * @return gibt mit entweder 0,1,2 oder 3 zurück je nach Gewinn/Jackpot
     */
    private int checkJackpot(int pos){

        int i = pos;
        int jackpot = 0;
        if(row0_tileImgsPointer[i] == row1_tileImgsPointer[i] && row0_tileImgsPointer[i] == row2_tileImgsPointer[i] && row1_tileImgsPointer[i] == row2_tileImgsPointer[i]){
            jackpot = 3;
        }
        else if(row0_tileImgsPointer[i] == row1_tileImgsPointer[i]){ //links und mitte gleich
            jackpot = 1;
        }
        else if(row1_tileImgsPointer[i] == row2_tileImgsPointer[i]){ //mitte und rechts gleich
            jackpot = 2;
        }

       return jackpot;
    }

    private void setRows(){
        for(int i = 0; i< tileCount; i++){
            row0_tileImgs[row0_tileImgsPointer[i]].sichtbarSetzen(true);
            row0_tileImgs[row0_tileImgsPointer[i]].positionSetzen(row0_TL_POS);
            row0_tileImgs[row0_tileImgsPointer[i]].verschieben(offsetX,offsetY);
            row0_tileImgs[row0_tileImgsPointer[i]].verschieben(0,-(i+1)*tileSize); // nach oben verschieben
        }
        for(int i = 0; i< tileCount; i++){
            row1_tileImgs[row1_tileImgsPointer[i]].sichtbarSetzen(true);
            row1_tileImgs[row1_tileImgsPointer[i]].positionSetzen(row1_TL_POS);
            row1_tileImgs[row1_tileImgsPointer[i]].verschieben(offsetX,offsetY);
            row1_tileImgs[row1_tileImgsPointer[i]].verschieben(0,-(i+1)*tileSize); // nach oben verschieben
        }
        for(int i = 0; i< tileCount; i++){
            row2_tileImgs[row2_tileImgsPointer[i]].sichtbarSetzen(true);
            row2_tileImgs[row2_tileImgsPointer[i]].positionSetzen(row2_TL_POS);
            row2_tileImgs[row2_tileImgsPointer[i]].verschieben(offsetX,offsetY);
            row2_tileImgs[row2_tileImgsPointer[i]].verschieben(0,-(i+1)*tileSize); // nach oben verschieben
        }

    }
    private int[] giveShuffeldArray(int size){
        int[] arraySolution = new int[size];
        List<Integer> solution = new ArrayList<>();
        for (int i = 0; i < size; i++){ //to generate from 0-10 inclusive.
            //For 0-9 inclusive, remove the = on the <=
            solution.add(i);
        }
        Collections.shuffle(solution);
        for (int i = 0; i < solution.size(); i++) {
            arraySolution[i] = solution.get(i);
        }
        return arraySolution;

    }

    public boolean isActive() {
        return active;
    }

}
