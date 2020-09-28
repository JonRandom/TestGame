import ea.Bild;
import ea.Knoten;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class ImageColliderTest extends Knoten {

    private String ImgPath = "./Assets/Tests/coll2.png";
    private int PlayerW = 48;
    private int PlayerH = 90;

    private Bild Img;

    private BufferedImage img = null;
    private File f = null;

    public ImageColliderTest(String path){
        ImgPath = path;

        try{
            f = new File(ImgPath);
            img = ImageIO.read(f);
        }catch(IOException e){
            System.out.println(e);
        }
        InitImage(ImgPath);
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
    private void InitImage(String pathImg){
        Img = new Bild(0,0,pathImg);
        Img.setOpacity(0.3f);
        this.add(Img);
    }

    public boolean TestColl(int ObjectX, int ObjectY){
        boolean coll = false;
        for(int i=0;i<PlayerH;i++){ //links nach unten
           int p = img.getRGB(ObjectX,i + ObjectY);
           int r = (p>>16) & 0xff;
           if(r<100){
               coll = true;
           }
        }
        //System.out.println("links: " + coll);
        for(int i=0;i<PlayerH;i++){ //rechts nach unten

            int p = img.getRGB(ObjectX+ PlayerW,i +ObjectY);
            int r = (p>>16) & 0xff;
            if(r<100){
                coll = true;

            }


        }
        //System.out.println("rechts: " + coll);
        for(int i=0;i<PlayerW;i++){ //oben nach rechts
            int p = img.getRGB(ObjectX + i,ObjectY);
            int r = (p>>16) & 0xff;
            //System.out.println(r);
            if(r<100){
                coll = true;

            }

        }
        //System.out.println("oben: " + coll);
        for(int i=0;i<PlayerW;i++){ //unten nach rechts
            int p = img.getRGB(ObjectX + i,PlayerH + ObjectY);
            int r = (p>>16) & 0xff;
            if(r<100){
                coll = true;

            }

        }
        //System.out.println("unten: " + coll);

        return coll;
    }

    public boolean TestCollPlayer(DummyPlayer dp){
        int x = (int)dp.getX();
        int y = (int)dp.getY();

        return TestColl(x,y);
    }
    public boolean AllowWalk(DummyPlayer dp){
        return !TestCollPlayer(dp);
    }
}
