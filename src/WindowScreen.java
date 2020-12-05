import ea.Bild;
import ea.Knoten;

public class WindowScreen extends Knoten {

    private Bild mainImg;
    //private String path = MAIN.settingsScreenImg;
    private boolean active = false;


    public WindowScreen(String path){
        mainImg = new Bild(0,0,path);
        mainImg.sichtbarSetzen(false);
        this.add(mainImg);
    }

    public void show(){
        active = true;
        mainImg.sichtbarSetzen(true);
    }

    public void hide(){
        active = false;
        mainImg.sichtbarSetzen(false);
    }
    public void toggleWindow(){
        if(active){
            hide();
        }else{
            show();
        }
    }

    public boolean isActive() {
        return active;
    }
}
