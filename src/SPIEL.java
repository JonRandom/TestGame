import ea.*;
import ea.edu.*;

import java.awt.*;
import java.util.Arrays;

public class SPIEL extends Game implements TastenLosgelassenReagierbar, Ticker
{

    private int zaehler;

    private Player ActivePlayer;
    private DialogController DialogController;
    private Map map;
    private DebugAnzeige debugAnzeige1;



    public Knoten StaticPlate; //Knoten mit allen Objekten auf dem Bildschirm die bewegt werden sollen.



    public SPIEL(int breite, int hoehe,  boolean maus)
    {
        super(1200,800,"P-SEM GAME");//windowsize kann nicht mit variable gemacht werden.
        //Zaehler fuer Tick, Tack, ...
        zaehler = 0;

        StaticPlate = new Knoten();

        DialogController = new DialogController();
        DialogController.ShowWindow();
        ActivePlayer = new Player(600,400);
        map = new Map();
        debugAnzeige1 = new DebugAnzeige(0,0);


        wurzel.add(map);
        wurzel.add(ActivePlayer);

        StaticPlate.add(DialogController);

        statischeWurzel.add(debugAnzeige1);
        statischeWurzel.add(StaticPlate);



        tastenReagierbarAnmelden(this);
        tastenLosgelassenReagierbarAnmelden(this);

        tickerAnmelden(this, 20);





    }



    public void fokusSetzten(){
        cam.fokusSetzen(ActivePlayer);
        BoundingRechteck CamBounds = new BoundingRechteck(0,0,map.getBreite(),map.getHoehe());
        cam.boundsSetzen(CamBounds);
    }

    public void tick() {
        debugAnzeige1.SetContent("Pos:" + ActivePlayer.positionX() + "  -  " + ActivePlayer.positionY());
        if(!DialogController.GetDialogStatus()) {
            int walkspeed = ActivePlayer.getWalkspeed();
            //System.out.println(Arrays.toString(map.ColliderTest(ActivePlayer)));

            if (tasteGedrueckt(Taste.W)) {
                ActivePlayer.WalkTop();
            }
            if (tasteGedrueckt(Taste.S)) {
                ActivePlayer.WalkBottom();
            }

            if (tasteGedrueckt(Taste.A)) {
                ActivePlayer.WalkLeft();
            }

            if (tasteGedrueckt(Taste.D)) {
                ActivePlayer.WalkRight();
            }
            }
        }

    //unused for now
    public void klickReagieren(int x, int y)
    {
        System.out.println("Klick bei (" + x  + ", " + y + ").");
    }

    //  https://engine-alpha.org/wiki/Tastaturtabelle
    public void tasteReagieren(int tastenkuerzel)
    {
        //Togglet beim Drücken der G Taste den Dialog
        if(tastenkuerzel == 6 && DialogController.GetDialogStatus()){
            DialogController.HideWindow();
        }
        else if(tastenkuerzel == 6 && !DialogController.GetDialogStatus()){
            DialogController.ShowWindow();
        }



    }


    public void warte(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void tasteLosgelassen(int i) {
        //System.out.println(i);
    }




}

