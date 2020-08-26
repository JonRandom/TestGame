import ea.*;
import com.google.gson.*;



import java.awt.*;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class SPIEL extends Game implements TastenLosgelassenReagierbar, Ticker
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

    public GameSaver gamesaver;

    public Knoten StaticPlate; //Knoten mit allen Objekten auf dem Bildschirm die bewegt werden sollen.



    public SPIEL(int breite, int hoehe,  boolean maus) throws FileNotFoundException {
        super(1200,800,"P-SEM GAME");//windowsize kann nicht mit variable gemacht werden.
        //Zaehler fuer Tick, Tack, ...
        zaehler = 0;

        StaticPlate = new Knoten();

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




        wurzel.add(DP);
        wurzel.add(map);
        wurzel.add(ActivePlayer);
        wurzel.add(NpcController);


        StaticPlate.add(StartSc);
        StartSc.setActive(true);
        StaticPlate.add(DialogController);

        statischeWurzel.add(debugAnzeige1);
        statischeWurzel.add(debugAnzeige2);
        statischeWurzel.add(StaticPlate);



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

    //unused for now
    public void klickReagieren(int x, int y)
    {
        System.out.println("Klick bei (" + x  + ", " + y + ").");
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
            else if(tastenkuerzel == 30){
                //StartSc.select();
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




}

