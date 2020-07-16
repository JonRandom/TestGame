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

import ea.*;

 class ImageCollection extends Knoten{

     private int ImageCount = 2;    //Anzahl der Bilder
     private float posX;
     private float posY;
     private String MainDir;


     private int stepL=0;
     private int stepR=0;
     private int stepT=0;
     private int stepB=0;


     private long LastMillis;

     private int walkspeed;
     private int walkConst;//animation pro distanz




     //Listen mit >Bild< Objekten
     private Bild[] ImgL = new Bild[ImageCount];  //Links
     private Bild[] ImgR = new Bild[ImageCount];  //Recht
     private Bild[] ImgT = new Bild[ImageCount];  //Top
     private Bild[] ImgB = new Bild[ImageCount];  //Bottom

     private Bild[] ImgAll = new Bild[ImageCount*4];//vielleicht Count*4 + 1



     /**
      *
      * @param x Position x
      * @param y Position y
      * @param MainDir Übergeordnetes Verzeichnis der Bilder
      */
     public ImageCollection(float x, float y, String MainDir, int walkspeed){
         this.posX = x;
         this.posY = y;
         this.MainDir = MainDir;
         this.walkspeed = walkspeed;

         walkConst=600/walkspeed;


     }
     public void HideAll(){
         for(int i=0;i<ImageCount;i++){
             ImgL[i].sichtbarSetzen(false);
         }
         for(int i=0;i<ImageCount;i++){
             ImgR[i].sichtbarSetzen(false);
         }
         for(int i=0;i<ImageCount;i++){
             ImgT[i].sichtbarSetzen(false);
         }
         for(int i=0;i<ImageCount;i++){
             ImgB[i].sichtbarSetzen(false);
         }
     }

     public void Init(){//  Run all Init Methods
         initL();
         initR();
         initT();
         initB();

         combineLists();
         System.out.println("IN INIT");

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
     public void walkLeft() {
         HideAll();
         ImgL[stepL].sichtbarSetzen(true);
         long curr = System.currentTimeMillis();
         if ((curr - LastMillis) >= walkConst) {

             stepL++;
             if (stepL >= ImageCount) {
                 stepL = 0;
             }
             LastMillis = curr;

         }
     }
     public void walkRight() {
         HideAll();
         ImgL[stepR].sichtbarSetzen(true);
         long curr = System.currentTimeMillis();
         if ((curr - LastMillis) >= walkConst) {

             stepR++;
             if (stepR >= ImageCount) {
                 stepR = 0;
             }
             LastMillis = curr;

         }
     }
     public void walkTop() {
         HideAll();
         ImgL[stepT].sichtbarSetzen(true);
         long curr = System.currentTimeMillis();
         if ((curr - LastMillis) >= walkConst) {

             stepT++;
             if (stepT >= ImageCount) {
                 stepT = 0;
             }
             LastMillis = curr;

         }
     }
     public void walkBottom() {
         HideAll();
         ImgL[stepB].sichtbarSetzen(true);
         long curr = System.currentTimeMillis();
         if ((curr - LastMillis) >= walkConst) {

             stepB++;
             if (stepB >= ImageCount) {
                 stepB = 0;
             }
             LastMillis = curr;

         }
     }

     /**
      *
      * @param dx Eig. nur ob der Spieler einen schritt in x Richtung mit Vorzeichen macht
      * @param dy Eig. nur ob der Spieler einen schritt in y Richtung mit Vorzeichen macht
      * Abstand wird vom Schritttempo angegeben
      */
     public void step(int dx, int dy){
         int abX=0;
         int abY=0;
         int distance = (int)Math.sqrt(dx*dx + dy*dy);//Diagonaler Abstand als INT

         /**
          * Macht aus dem wlakspeed eine Abstand, wenn der Player diagonal läuft
          */
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

