/**
 * Organisiert die Animation von Laufbewegungen für >Player< und >Npc<
 * Sammelt Bilder für alle Gehrichtungen und entscheidet welches Bild anzuzeigen.
 *
 * Benennung:
 *
 * "FigurName" + "-" + "L/R/T/B" + "NUMBER"
 * Bsp.:  "Spieler-R0.png"
 * Bsp.2: "Torben-T0.png"
 *
 * INDIZES VON 0 AN
 *
 */

/**
 * Vielleicht anstatt SystemZeit und 
 */

import ea.Bild;
import ea.Knoten;

class ImageCollection2 extends Knoten{

    private int ImageCount = 2;    //Anzahl der Bilder
    private int AllImageCount = ImageCount*4;//vielleicht Count*4 + 1

    private float posX;
    private float posY;
    private String MainDir;


    private int stepL=0;
    private int stepR=0;
    private int stepT=0;
    private int stepB=0;

    private int distanceL = 0;
    private int distanceR = 0;
    private int distanceT = 0;
    private int distanceB = 0;


    private int stepDistance = 20; // Distanz die einen Schritt ausmacht



    //Listen mit >Bild< Objekten
    private Bild[] ImgL = new Bild[ImageCount];  //Links
    private Bild[] ImgR = new Bild[ImageCount];  //Recht
    private Bild[] ImgT = new Bild[ImageCount];  //Top
    private Bild[] ImgB = new Bild[ImageCount];  //Bottom

    private Bild[] ImgAll = new Bild[AllImageCount*4];//vielleicht Count*4 + 1



    /**
     *
     * @param x Position x
     * @param y Position y
     * @param MainDir Übergeordnetes Verzeichnis der Bilder
     */
    public ImageCollection2(float x, float y, String MainDir){
        this.posX = x;
        this.posY = y;
        this.MainDir = MainDir;



    }
    public void HideAll(){
        for(int i=0;i<AllImageCount;i++){
            ImgAll[i].sichtbarSetzen(false);
        }
    }

    //  Run all Init Methods
    public void Init(){
        initL();
        initR();
        initT();
        initB();

        combineLists();

    }
    public void initL(){
        for(int i=0;i<ImageCount;i++){
            System.out.println(i);

            String Dir = MainDir + "-L" +(i)+".png";

            System.out.println(Dir);

            ImgL[i] = new Bild(posX,posY,Dir);
            this.add(ImgL[i]);
        }

    }
    public void initR(){
        for(int i=0;i<ImageCount;i++){
            System.out.println(i);

            String Dir = MainDir + "-L" +(i)+".png";
            System.out.println(Dir);

            ImgR[i] = new Bild(posX,posY,Dir);
            this.add(ImgR[i]);
        }
    }
    public void initT(){
        for(int i=0;i<ImageCount;i++){
            System.out.println(i);

            String Dir = MainDir + "-L" +(i)+".png";
            System.out.println(Dir);

            ImgT[i] = new Bild(posX,posY,Dir);
            this.add(ImgT[i]);
        }
    }
    public void initB(){
        for(int i=0;i<ImageCount;i++){
            System.out.println(i);

            String Dir = MainDir + "-L" +(i)+".png";
            System.out.println(Dir);

            ImgB[i] = new Bild(posX,posY,Dir);
            this.add(ImgB[i]);
        }
    }

    public void combineLists(){
        System.arraycopy(ImgL, 0, ImgAll, 0, ImageCount);
        System.arraycopy(ImgR, 0, ImgAll, ImageCount, ImageCount);
        System.arraycopy(ImgT, 0, ImgAll, ImageCount*2, ImageCount);
        System.arraycopy(ImgB, 0, ImgAll, ImageCount*3, ImageCount);
    }

    public void walkLeft(int dis) {
        distanceL = distanceL+ dis ;
        HideAll();
        ImgL[stepL].sichtbarSetzen(true);
        if (distanceL >= stepDistance) {

            stepL++;
            if (stepL >= ImageCount) {
                stepL = 0;
            }
            distanceL=0;

        }
        this.verschieben(-dis,0);
    }
    public void walkRight(int dis) {
        distanceR = distanceR+ dis ;
        HideAll();
        ImgR[stepR].sichtbarSetzen(true);
        if (distanceR >= stepDistance) {

            stepR++;
            if (stepR >= ImageCount) {
                stepR = 0;
            }
            distanceR=0;

        }
        this.verschieben(dis,0);
    }
    public void walkTop(int dis) {
        distanceT = distanceT+ dis ;
        HideAll();
        ImgT[stepT].sichtbarSetzen(true);
        if (distanceT >= stepDistance) {

            stepT++;
            if (stepT >= ImageCount) {
                stepT = 0;
            }
            distanceT=0;

        }
        this.verschieben(0,-dis);
    }
    public void walkBottom(int dis) {
        distanceB = distanceB+ dis ;
        HideAll();
        ImgB[stepB].sichtbarSetzen(true);
        if (distanceB >= stepDistance) {

            stepB++;
            if (stepB >= ImageCount) {
                stepB = 0;
            }
            distanceB=0;

        }
        this.verschieben(0,dis);
    }

    /**
     *
     * @param dx Eig. nur ob der Spieler einen schritt in x Richtung mit Vorzeichen macht
     * @param dy Eig. nur ob der Spieler einen schritt in y Richtung mit Vorzeichen macht
     * Abstand wird vom Schritttempo angegeben
     */
    /*
    public void step(int dx, int dy){
        int abX=0;
        int abY=0;
        int distance = (int)Math.sqrt(dx*dx + dy*dy);//Diagonaler Abstand als INT


        abX = dx*walkspeed/(int)Math.sqrt(2);
        abY = dy*walkspeed/(int)Math.sqrt(2);
        System.out.println(abY);
        if (dx==1 && dy==1){
            walkRight();
            walkBottom();
            verschieben(abX,abY);
        }
        if (dx==-1 && dy==1){
            walkLeft();
            walkBottom();
            verschieben(abX,abY);
        }
        if (dx==1 && dy==-1){
            walkRight();
            walkTop();
            verschieben(abX,abY);
        }
        if (dx==-1 && dy==-1){
            walkLeft();
            walkRight();
            verschieben(abX,abY);
        }

        if (dx==-1){
            walkRight();
            verschieben(-walkspeed,0);
        }
        if (dx==1){
            walkLeft();
            verschieben(walkspeed,0);
        }
        if (dy==-1){
            walkTop();
            verschieben(0,-walkspeed);
        }
        if (dy==1){
            walkBottom();
            verschieben(0,walkspeed);
        }


    }
    */

    /**
     *  Wichtig für später Player Teleporation oder Resets
     * @param x s. Raum Doku
     * @param y s. Raum Doku
     */

    @Override
    public void positionSetzen(float x, float y) {
        positionSetzen(x, y);
        posX= x;
        posY= y;
    }

}

