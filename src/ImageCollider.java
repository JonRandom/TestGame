import ea.Bild;
import ea.Knoten;
import org.w3c.dom.ls.LSOutput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

class ImageCollider extends Knoten {

    private String ImgPath = "./Assets/Tests/coll2.png";
    private int PlayerW = 48;
    private int PlayerH = 90;


    private int offsetX;
    private int offsetY;

    private Bild displayImg;

    private BufferedImage colliderImg = null;
    private File f = null;

    public ImageCollider(String path){
        ImgPath = path;

        try{
            f = new File(ImgPath);
            colliderImg = ImageIO.read(f);
        }catch(IOException e){
            System.out.println("Fehler in der ImageCollider Klasse: Bild bei " + ImgPath + " kann nicht gefunden werden!");
            System.out.println(e);
        } //import Image for filebuffer;
        InitImage();
        /*

        //get red
        int r = (p>>16) & 0xff;
        System.out.println(r);
        //get green
        int g = (p>>8) & 0xff;
        System.out.println(g);

        //get blue
        int b = p & 0xff;
        System.out.println(b);

         */

    }
    private void InitImage(){
        try{
            displayImg = new Bild(0,0,ImgPath);

        }catch(Exception e){
            System.out.println("Fehler in der ImageCollider Klasse: Bild bei " + ImgPath + " kann nicht gefunden werden!");
            System.out.println(e);
        } //import Image for display;
        displayImg.setOpacity(0.5f);
        this.add(displayImg);
    }
    private void setDisplayImageVisibility(boolean v){
        displayImg.sichtbarSetzen(v);
    }

    /**
     * //korriert die Position von relativ zu absolut
     * @param x xOffset /player pos
     * @param y yOffset /player pos
     */
    public void setOffset(int x, int y){
        //System.out.println("Setoffset: " + x +"|" + y);
        offsetX = x;
        offsetY = y;
        //System.out.println("Setoffset gesetzt: " + offsetY +"|" + offsetX);
        displayImg.positionSetzen(x,y);
    }

    public boolean TestColl(int ObjectX, int ObjectY){

        //System.out.println("Sucht von LO: " + ObjectX + ", " + ObjectY);
        //System.out.println("OFFSET: " + offsetX + ", " + offsetY);
        ObjectX = ObjectX - offsetX;
        ObjectY = ObjectY - offsetY;
        //System.out.println("Sucht von LO Corr: " + ObjectX + ", " + ObjectY);

        boolean coll = false;
        for(int i=0;i<PlayerH;i++){ //links nach unten
            int p = 0;
            try{
                p = colliderImg.getRGB(ObjectX ,i + ObjectY );
            } catch (Exception e){
                System.out.println(e);
                System.out.println("Fehler in der ImageCollider Klasse: Spieler ist nicht in der Fläche eines Kolliders");
            }
           int r = (p>>16) & 0xff;
           if(r<100){
               coll = true;
           }
        }
        //System.out.println("links: " + coll);
        for(int i=0;i<PlayerH;i++){ //rechts nach unten
            int p = 0;
            try{
                p = colliderImg.getRGB(ObjectX+ PlayerW,i +ObjectY );
            } catch (Exception e){
                System.out.println(e);
                System.out.println("Fehler in der ImageCollider Klasse: Spieler ist nicht in der Fläche eines Kolliders");
            }

            int r = (p>>16) & 0xff;
            if(r<100){
                coll = true;

            }


        }
        //System.out.println("rechts: " + coll);
        for(int i=0;i<PlayerW;i++){ //oben nach rechts
            int p = 0;
            try{
                p = colliderImg.getRGB(ObjectX + i,ObjectY );
            } catch (Exception e){
                System.out.println(e);
                System.out.println("Fehler in der ImageCollider Klasse: Spieler ist nicht in der Fläche eines Kolliders");
            }
            int r = (p>>16) & 0xff;
            if(r<100){
                coll = true;

            }

        }
        //System.out.println("oben: " + coll);
        for(int i=0;i<PlayerW;i++){ //unten nach rechts
            int p = 0;
            try{
                p = colliderImg.getRGB(ObjectX + i,PlayerH + ObjectY );
            } catch (Exception e){
                System.out.println(e);
                System.out.println("Fehler in der ImageCollider Klasse: Spieler ist nicht in der Fläche eines Kolliders");
            }
            int r = (p>>16) & 0xff;
            if(r<100){
                coll = true;

            }

        }
        //System.out.println("unten: " + coll);

        return coll;
    }

    public int GetStatus(int ObjectX, int ObjectY){

        //System.out.println("Sucht von LO: " + ObjectX + ", " + ObjectY);
        //System.out.println("OFFSET: " + offsetX + ", " + offsetY);
        ObjectX = ObjectX - offsetX;
        ObjectY = ObjectY - offsetY;
        //System.out.println("Sucht von LO Corr: " + ObjectX + ", " + ObjectY);


        int redColor = 0;
        boolean isBlack;
        for(int i=0;i<PlayerH;i++){ //links nach unten
            int p = 0;
            try{
                p = colliderImg.getRGB(ObjectX ,i + ObjectY );
            } catch (Exception e){
                System.out.println(e);
                System.out.println("Fehler in der ImageCollider Klasse: Spieler ist nicht in der Fläche eines Kolliders");
            }
            int r = (p>>16) & 0xff;

            if(r == 0){
                isBlack = true;
            }else{
                redColor = r;
            }



        }
        //System.out.println("links: " + coll);
        for(int i=0;i<PlayerH;i++){ //rechts nach unten
            int p = 0;
            try{
                p = colliderImg.getRGB(ObjectX+ PlayerW,i +ObjectY );
            } catch (Exception e){
                System.out.println(e);
                System.out.println("Fehler in der ImageCollider Klasse: Spieler ist nicht in der Fläche eines Kolliders");
            }

            int r = (p>>16) & 0xff;
            if(r == 0){
                isBlack = true;
            }else{
                redColor = r;
            }


        }
        //System.out.println("rechts: " + coll);
        for(int i=0;i<PlayerW;i++){ //oben nach rechts
            int p = 0;
            try{
                p = colliderImg.getRGB(ObjectX + i,ObjectY );
            } catch (Exception e){
                System.out.println(e);
                System.out.println("Fehler in der ImageCollider Klasse: Spieler ist nicht in der Fläche eines Kolliders");
            }
            int r = (p>>16) & 0xff;
            if(r == 0){
                isBlack = true;
            }else{
                redColor = r;
            }

        }
        //System.out.println("oben: " + coll);
        for(int i=0;i<PlayerW;i++){ //unten nach rechts
            int p = 0;
            try{
                p = colliderImg.getRGB(ObjectX + i,PlayerH + ObjectY );
            } catch (Exception e){
                System.out.println(e);
                System.out.println("Fehler in der ImageCollider Klasse: Spieler ist nicht in der Fläche eines Kolliders");
            }
            int r = (p>>16) & 0xff;
            if(r == 0){
                isBlack = true;
            }else{
                redColor = r;
            }

        }
        //System.out.println("unten: " + coll);

        return  redColor;
    }

    public boolean TestCollPlayer(DummyPlayer dp){
        int x = (int)dp.getX();
        int y = (int)dp.getY();
        System.out.println("Relelle pos ist: " + x +"," + y);

        return TestColl(x,y);
    }
    public boolean AllowWalk(DummyPlayer dp){
        return !TestCollPlayer(dp);
    }
    public int getWalkColor(DummyPlayer dp){
        int x = (int)dp.getX();
        int y = (int)dp.getY();
        System.out.println("Relelle pos ist: " + x +"," + y);
        return GetStatus(x,y);
    }
}
