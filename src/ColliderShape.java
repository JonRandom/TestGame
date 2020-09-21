/**
 * Eine Klasse die Boundingrechteck entspricht aber auch komplexere Formen handeln kann
 *
 * Zwei Konstruktoren:
 * Einer mit vier Eingaben -> einfaches Rect
 * Einer mit
 *
 *
 * Koordinaten von OBEN LINKS
 */


public class ColliderShape {

    private int xCorner;
    private int yCorner;

    private int width;
    private int height;


    public ColliderShape(int x,int y,int w,int h){
        this.xCorner = x;
        this.yCorner = x;
        this.width = w;
        this.height = h;
        System.out.println(xCorner +", "+yCorner +", "+width +", "+height );

    }

    public boolean isIn(int xObject, int yObject){
        System.out.println("isIn(" + xObject +", " + yObject +")");

        if(xObject < xCorner){//links von dem Rechteck
            return false;
        }
        else if(xObject > xCorner + width){//rechts von dem Rechteck
            return false;
        }
        else if(yObject < yCorner){//oberhalb von dem Rechteck
            return false;
        }
        else if(yObject > yCorner + height){//unterhalb von dem Rechteck
            return false;
        }
        else{
            return true;
        }

    }
    public boolean isIn(DummyPlayer dp){
        int xObject = (int)dp.getX();
        int yObject = (int)dp.getY();
        //System.out.println("isIn(" + xObject +", " + yObject +")");

        if(xObject < xCorner){//links von dem Rechteck
            return false;
        }
        else if(xObject > xCorner + width){//rechts von dem Rechteck
            return false;
        }
        else if(yObject < yCorner){//oberhalb von dem Rechteck
            return false;
        }
        else if(yObject > yCorner + height){//unterhalb von dem Rechteck
            return false;
        }
        else{
            return true;
        }

    }

}
