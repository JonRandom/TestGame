import ea.*;
import ea.edu.TextE;

import java.awt.*;

public class TEXT extends TextE {

    private int posX;
    private int posY;

    private int textSize;
    private String color;
    private java.awt.Font font;


    private boolean visible;


    public TEXT(){
        this("EMPTY CONSTRUCTOR TEXT",400,200,20,"Rot");
    }
    public TEXT(String content, int posX, int posY, int textSize, String color){
        super(content);
        super.positionSetzen(posX,posY);
        this.visible = true;
        super.sichtbarSetzen(true);
        this.color = color;
        super.farbeSetzen(this.color);
        this.posX = posX;
        this.posY = posY;







    }


}
