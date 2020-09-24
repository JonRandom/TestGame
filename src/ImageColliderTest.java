import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class ImageColliderTest{

    private String ImgPath = "./Assets/Tests/coll2.png";
    private int PlayerW = 48;
    private int PlayerH = 90;

    private BufferedImage img = null;
    private File f = null;

    public ImageColliderTest(){

        try{
            f = new File(ImgPath);
            img = ImageIO.read(f);
        }catch(IOException e){
            System.out.println(e);
        }

        int p = img.getRGB(2,2);

        int r = (p>>16) & 0xff;
        System.out.println(r);

        p = img.getRGB(85,2);

        r = (p>>16) & 0xff;
        System.out.println(r);
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
    public boolean TestColl(int ObjectX, int ObjectY){
        boolean coll = false;
        for(int i=0;i<PlayerH;i++){ //links nach unten
           int p = img.getRGB(ObjectX,i + ObjectY);
           int r = (p>>16) & 0xff;
           if(r>100){
               coll = true;
           }
        }
        //System.out.println("links: " + coll);
        for(int i=0;i<PlayerH;i++){ //rechts nach unten

            int p = img.getRGB(ObjectX+ PlayerW,i +ObjectY);
            int r = (p>>16) & 0xff;
            if(r>100){
                coll = true;
            }

        }
        //System.out.println("rechts: " + coll);
        for(int i=0;i<PlayerW;i++){ //oben nach rechts
            int p = img.getRGB(ObjectX + i,ObjectY);
            int r = (p>>16) & 0xff;
            //System.out.println(r);
            if(r>100){
                coll = true;
            }

        }
        //System.out.println("oben: " + coll);
        for(int i=0;i<PlayerW;i++){ //unten nach rechts
            int p = img.getRGB(ObjectX + i,PlayerH + ObjectY);
            int r = (p>>16) & 0xff;
            if(r>100){
                coll = true;
            }

        }
        //System.out.println("unten: " + coll);

        return coll;
    }
}
