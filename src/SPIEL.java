import ea.*;
import com.google.gson.*;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class SPIEL extends Game implements TastenLosgelassenReagierbar, Ticker, KlickReagierbar
{

    private int zaehler;

    private Player ActivePlayer;
    private DialogController DialogController;
    private Map map;
    private DebugAnzeige debugAnzeige1;
    private DebugAnzeige debugAnzeige2;
    private DummyPlayer DP;
    private NpcController NpcController;
    private StartingScreen StartSc;
    private Minigame1 Minigame1;



    public GameSaver gamesaver;

    private Bild cursor;
    private Punkt hotspot;
    private Maus maus;




    public SPIEL(int breite, int hoehe) throws FileNotFoundException {
        super(1200,800,"P-SEM GAME");//windowsize kann nicht mit variable gemacht werden.
        //Zaehler fuer Tick, Tack, ...
        zaehler = 0;


        StartSc = new StartingScreen();

        DP = new DummyPlayer(600,400);
        DialogController = new DialogController();
        DialogController.setVisisbilty(false);
        ActivePlayer = new Player(600,400);
        map = new Map(ActivePlayer.getBreite(),ActivePlayer.getHoehe());
        NpcController = new NpcController();
        debugAnzeige1 = new DebugAnzeige(0,0);
        debugAnzeige2 = new DebugAnzeige(200,0);
        gamesaver = new GameSaver(); //GameSaver, der im Moment nur Spieler-Sachen speichert


        //Minigame1 = new Minigame1(); // unused do to lack uf ideas

        cursor = new Bild(0, 0, "./Assets/MouseC.png");

        hotspot = new Punkt(11,11);

        maus = new Maus(cursor, hotspot);
        mausAnmelden(maus);
        maus.klickReagierbarAnmelden(this);


        wurzel.add(DP);
        wurzel.add(map);
        wurzel.add(ActivePlayer);
        wurzel.add(NpcController);


        statischeWurzel.add(StartSc);
        StartSc.setActive(true);
        statischeWurzel.add(DialogController);
        //statischeWurzel.add(Minigame1);


        statischeWurzel.add(debugAnzeige1);
        statischeWurzel.add(debugAnzeige2);



        tastenReagierbarAnmelden(this);
        tastenLosgelassenReagierbarAnmelden(this);

        tickerAnmelden(this, 10);

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
        debugAnzeige2.SetContent("Visiting:" +map.isVisiting());

        DP.positionSetzen(playerX,playerY);


        if(!DialogController.GetDialogStatus() && !StartSc.isActive()) {
            int walkspeed = ActivePlayer.getWalkspeed();

            if (tasteGedrueckt(Taste.W)) {
                DP.verschieben(0,-walkspeed);
                if(map.getWalkable(DP)){
                    ActivePlayer.WalkTop();
                }
            }
            if (tasteGedrueckt(Taste.S)) {
                DP.verschieben(0,walkspeed);
                if(map.getWalkable(DP)){
                    ActivePlayer.WalkBottom();
                }
            }

            if (tasteGedrueckt(Taste.A)) {
                DP.verschieben(-walkspeed,0);
                if(map.getWalkable(DP)){
                    ActivePlayer.WalkLeft();
                }
            }

            if (tasteGedrueckt(Taste.D)) {
                DP.verschieben(walkspeed,0);
                if(map.getWalkable(DP)){
                    ActivePlayer.WalkRight();
                }
            }
            }
        if(NpcController.ColliderTest(ActivePlayer)&&!DialogController.GetDialogStatus()){

            DialogController.SetContent("Hallo ich bin ein NPC der mit dir ein Dialog führen kann");
            DialogController.setVisisbilty(true);
        }


        gamesaver.SavePlayer(ActivePlayer);

        }



    //  https://engine-alpha.org/wiki/Tastaturtabelle
    public void tasteReagieren(int tastenkuerzel)
    {
        //Togglet beim Drücken der G Taste den Dialog
        if(tastenkuerzel == 6){
            DialogController.toggleVisibilty();
        }

        if(tastenkuerzel == 21) {//Wenn V gedrückt wird toggle visiting
            map.toggleVisting();
        }
        if(tastenkuerzel == 19) {//Wenn T gedrückt wird teleport 10 Blöcke nach vorne
            ActivePlayer.positionSetzen(ActivePlayer.positionX()+10,ActivePlayer.positionY());

        }
        if(tastenkuerzel == 27) {//Wenn PFEIL-rechts gedrückt wird konstate Kraft an
            ActivePlayer.konstanteKraftSetzen(new Vektor(2,0));
        }
        if(tastenkuerzel == 29) {//Wenn PFEIL-links gedrückt wird konstate Kraft aus
            ActivePlayer.konstanteKraftSetzen(new Vektor(0,0));
        }
        if(StartSc.isActive()){
            if(tastenkuerzel == 0){
                StartSc.ShiftLeft();
            }
            else if(tastenkuerzel == 3){
                StartSc.ShiftRight();
            }
            //Leertaste
            //Leertaste
            else if(tastenkuerzel == 30){
                StartSc.setActive(false);
                switch(StartSc.getSelection()){

                    case(0):
                        System.out.println("test0");
                        break;
                    case(1):
                        System.out.println("test1");
                        break;
                    case(2):
                        System.out.println("test2");
                        break;
                    case(3):
                        System.out.println("test3");
                        break;

                }
            }

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


    @Override
    public void klickReagieren(Punkt punkt) {
        System.out.println("Klick bei ("+ punkt+ ").");
        int dx = (int)ActivePlayer.getPosX()-(int)punkt.x();
        int dy = (int)ActivePlayer.getPosY()-(int)punkt.y();
        System.out.println(dx+dy);
        BallTest ball1 = new BallTest((int)ActivePlayer.getPosX(),(int)ActivePlayer.getPosY(),dx,dy);
        wurzel.add(ball1);
    }
}

