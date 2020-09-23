/**
 * Eine Klasse die Boundingrechteck entspricht aber auch komplexere Formen handeln kann
 *
 * Zwei Konstruktoren:
 * Einer mit vier Eingaben -> einfaches Rect
 *
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
        this.yCorner = y;
        this.width = w;
        this.height = h;
        //System.out.println("ColliderShape:" + xCorner +", "+yCorner +", "+width +", "+height );

    }

    public boolean isIn(int xObject, int yObject, int widthObject, int heightObject){
        //System.out.println("isIn(" + xObject +", " + yObject +")");

        if((xObject + widthObject)< xCorner){//links von dem Rechteck
            return false;
        }
        else if(xObject > (xCorner +width)){//rechts von dem Rechteck
            return false;
        }
        else if((yObject + heightObject) < yCorner){//oberhalb von dem Rechteck
            return false;
        }
        else if(yObject > (yCorner + height)){//unterhalb von dem Rechteck
            return false;
        }
        else{
            return true;
        }

    }
    public boolean isIn(DummyPlayer dp){
        int T_xObject = (int)dp.getX();
        int T_Object = (int)dp.getY();
        int T_width = (int)dp.getBreite();
        int T_height = (int)dp.getHoehe();

        return isIn(T_xObject,T_Object,T_width,T_height);

    }

}
