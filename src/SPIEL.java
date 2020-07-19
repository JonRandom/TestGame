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
    private DummyPlayer DP;


    public Knoten StaticPlate; //Knoten mit allen Objekten auf dem Bildschirm die bewegt werden sollen.



    public SPIEL(int breite, int hoehe,  boolean maus)
    {
        super(1200,800,"P-SEM GAME");//windowsize kann nicht mit variable gemacht werden.
        //Zaehler fuer Tick, Tack, ...
        zaehler = 0;

        StaticPlate = new Knoten();

        DP = new DummyPlayer(600,400);
        DialogController = new DialogController();
        DialogController.ShowWindow();
        ActivePlayer = new Player(600,400);
        map = new Map();
        debugAnzeige1 = new DebugAnzeige(0,0);

        wurzel.add(DP);
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
        int playerX = ActivePlayer.positionX();
        int playerY = ActivePlayer.positionY();

        debugAnzeige1.SetContent("Pos:" + playerX + "  -  " + playerY);


        DP.positionSetzen(playerX,playerY);

        if(!DialogController.GetDialogStatus()) {
            int walkspeed = ActivePlayer.getWalkspeed();
            System.out.println(Arrays.toString(map.ColliderTest(ActivePlayer)));
            System.out.println(map.ColliderTestAny(DP));

            if (tasteGedrueckt(Taste.W)) {
                DP.verschieben(0,-walkspeed);
                if(!map.ColliderTestAny(DP)){
                    ActivePlayer.WalkTop();
                }
            }
            if (tasteGedrueckt(Taste.S)) {
                DP.verschieben(0,walkspeed);
                if(!map.ColliderTestAny(DP)){
                    ActivePlayer.WalkBottom();
                }
            }

            if (tasteGedrueckt(Taste.A)) {
                DP.verschieben(-walkspeed,0);
                if(!map.ColliderTestAny(DP)){
                    ActivePlayer.WalkLeft();
                }
            }

            if (tasteGedrueckt(Taste.D)) {
                DP.verschieben(walkspeed,0);
                if(!map.ColliderTestAny(DP)){
                    ActivePlayer.WalkRight();
                }
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

