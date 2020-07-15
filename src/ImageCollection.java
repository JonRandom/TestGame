/**
 * Organisiert die Animation von Laufbewegungen für >Player< und >Npc<
 * Sammelt Bilder für alle Gehrichtungen und entscheidet welches Bild anzuzeigen.
 *
 * Benennung:
 *
 * "MainPfad" + "-" + "L/R/T/B" + "NUMBER"
 * Bsp.: "./Assets/Player/Spieler-R0"
 *
 * INDEIZES VON 0 AN
 *
 */

import ea.*;

 class ImageCollection extends Knoten{

     private int ImageCount = 2;    //Anzahl der Bilder
     private float posX;
     private float posY;
     private String MainDir;


     private int stepL=0;
     private float lastL;

     private int stepR=0;
     private float lastR;

     private long LastMillis;


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
     public ImageCollection(float x, float y, String MainDir){
         this.posX = x;
         this.posY = y;
         this.MainDir = MainDir;


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
     public void walkLeft(int distance) {
         HideAll();
         ImgL[stepL].sichtbarSetzen(true);
         long curr = System.currentTimeMillis();
         if ((curr - LastMillis) >= 200) {

             stepL++;
             if (stepL >= ImageCount) {
                 stepL = 0;
             }
             LastMillis = curr;

         }
     }
     public void walkRight(int distance) {
         HideAll();
         ImgL[stepR].sichtbarSetzen(true);
         long curr = System.currentTimeMillis();
         if ((curr - LastMillis) >= 200) {

             stepR++;
             if (stepR >= ImageCount) {
                 stepR = 0;
             }
             LastMillis = curr;

         }
     }


     /*
     public void walkLeft(int distance){

         HideAll();
         System.out.println(stepL);
         ImgL[stepL].sichtbarSetzen(true);
         System.out.println(lastL +"--" +posX);

         if((java.lang.Math.abs(posX-lastL))>=20){
             System.out.println("WalkStep");

             stepL++;
             if(stepL>=ImageCount){
                 stepL=0;
             }
             lastL = posX;
         }
         this.verschieben(distance,0);
         posX = posX+distance;
     }

      */


     @Override
     public void positionSetzen(float x, float y) {
         positionSetzen(x, y);
         posX= x;
         posY= y;
     }

}

