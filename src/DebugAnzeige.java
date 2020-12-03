import ea.Knoten;
import ea.Text;

public class DebugAnzeige extends Knoten {

    private Text t;
    public DebugAnzeige(int x, int y){
        super();
        t = new Text("TEST",x,y);

        //t.leuchtetSetzen(true);

        this.add(t);
        t.sichtbarSetzen(MAIN.showDebugFields);

    }
    public void SetContent(String content){
        t.inhaltSetzen(content);
    }


}
