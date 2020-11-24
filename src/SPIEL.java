import ea.*;

import java.lang.management.MemoryNotificationInfo;

public class SPIEL extends Game implements TastenLosgelassenReagierbar, Ticker, KlickReagierbar
{

    private int zaehler;
    private Player ActivePlayer;
    private DialogController3 DialogController3;
    private Map3 map;
    private DebugAnzeige debugAnzeige1;
    private DebugAnzeige debugAnzeige2;
    private DebugAnzeige debugAnzeige3;
    private DebugAnzeige debugAnzeige4;
    private DebugAnzeige debugAnzeige5;
    private DebugAnzeige debugAnzeige6;

    private DummyPlayer DP;
    private NpcController2 NpcController2;
    private StartingScreen StartSc;
    private Minigame1 Minigame1;
    private Minigame2 Minigame2;
    private Pet pet1;
    //private HouseLoader HouseLoader1;



    public GameSaver gamesaver;

    private Bild cursor;
    private Punkt hotspot;
    private Maus maus;




    public SPIEL() {
        super(MAIN.x,MAIN.y,"P-SEM GAME");//windowsize kann nicht mit variable gemacht werden.
        //Zaehler fuer Tick, Tack, ...
        zaehler = 0;


        StartSc = new StartingScreen();

        DP = new DummyPlayer(600,400);


        ActivePlayer = new Player(1100,1100);

        //pet1 = new Pet(1100,1100);
        NpcController2 = new NpcController2(ActivePlayer);
        map = new Map3(ActivePlayer.getBreite(),ActivePlayer.getHoehe(),NpcController2);
        DialogController3 = new DialogController3(NpcController2);
        debugAnzeige1 = new DebugAnzeige(0,0);
        debugAnzeige2 = new DebugAnzeige(200,0);
        debugAnzeige3 = new DebugAnzeige(500,0);
        debugAnzeige4 = new DebugAnzeige(700,0);
        debugAnzeige5 = new DebugAnzeige(1000,0);
        debugAnzeige6 = new DebugAnzeige(1180,0);
        gamesaver = new GameSaver(); //GameSaver, der im Moment nur Spieler-Sachen speichert
        Minigame2 = new Minigame2(ActivePlayer);
        //ObjectController Autos = new ObjectController();



        //HouseLoader1 = new HouseLoader(map);


        //Minigame1 = new Minigame1(); // unused do to lack of ideas

        /* MAUS
        cursor = new Bild(0, 0, "./Assets/MouseC.png");
        hotspot = new Punkt(11,11);
        maus = new Maus(cursor, hotspot);
        mausAnmelden(maus);
        maus.klickReagierbarAnmelden(this);
        */



        //wurzel.add(Autos);
        wurzel.add(DP);
        wurzel.add(map);
        wurzel.add(ActivePlayer);
        wurzel.add(NpcController2);
        //
        //wrzel.add(pet1);

        //statischeWurzel.add(HouseLoader1);
        statischeWurzel.add(StartSc);
        StartSc.setActive(true);
        statischeWurzel.add(DialogController3);

        statischeWurzel.add(Minigame2);


        statischeWurzel.add(debugAnzeige1);
        statischeWurzel.add(debugAnzeige2);
        statischeWurzel.add(debugAnzeige3);
        statischeWurzel.add(debugAnzeige4);
        statischeWurzel.add(debugAnzeige5);
        statischeWurzel.add(debugAnzeige6);



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
        debugAnzeige3.SetContent("Geld:" + ActivePlayer.getMoney());
        debugAnzeige4.SetContent("Dialog2Activ:" + DialogController3.isActive());
        debugAnzeige5.SetContent("ZeitPosition: " + DialogController3.getGlobalTemporalPosition());
        debugAnzeige6.SetContent("PlayingLastLine: " + DialogController3.isPlayingLastLine());

        DP.positionSetzen(playerX,playerY);


        if(!DialogController3.isActive() && !StartSc.isActive()) {
            int walkspeed = ActivePlayer.getWalkspeed();

            if (tasteGedrueckt(Taste.W)) {
                DP.positionSetzen(ActivePlayer.getPosX(),ActivePlayer.getPosY() -walkspeed);

                if(map.isWalkable2(DP,ActivePlayer)){
                    ActivePlayer.WalkTop();
                }

            }
            if (tasteGedrueckt(Taste.S)) {
                DP.positionSetzen(ActivePlayer.getPosX(),ActivePlayer.getPosY() + walkspeed);

                if(map.isWalkable2(DP,ActivePlayer)){
                    ActivePlayer.WalkBottom();
                }
            }

            if (tasteGedrueckt(Taste.A)) {
                DP.positionSetzen(ActivePlayer.getPosX() - walkspeed,ActivePlayer.getPosY());
//
                if(map.isWalkable2(DP,ActivePlayer)){
                    ActivePlayer.WalkLeft();
                }
            }

            if (tasteGedrueckt(Taste.D)) {
                DP.positionSetzen(ActivePlayer.getPosX() + walkspeed,ActivePlayer.getPosY());


                if(map.isWalkable2(DP,ActivePlayer)){
                    ActivePlayer.WalkRight();
                }
            }
            }
        if(NpcController2.checkForCollision(ActivePlayer) && !DialogController3.isActive()){
            String npcID = NpcController2.getCollidingNPC(ActivePlayer);
            System.out.println("Der Spieler schneidet den NPC mit der ID: " + npcID );
            DialogController3.startDialog(npcID);

        }


        gamesaver.SavePlayer(ActivePlayer);
        //pet1.follow(ActivePlayer);
        Minigame2.tick();
    }




    //  https://engine-alpha.org/wiki/Tastaturtabelle
    public void tasteReagieren(int tastenkuerzel)
    {
        if(tastenkuerzel == 8){//I als in
            //map.enterHouse(ActivePlayer,0);
        }
        if(tastenkuerzel == 14){//o als out
            //HouseLoader1.HideView();
            map.leaveHouse(ActivePlayer);
        }
        if(tastenkuerzel == 17){ //Wenn R gedrückt
            //DialogController3.openDialogPacket("11");
        }

        if(tastenkuerzel == 12){//M für minigame
            Minigame2.startGame();
        }


        if(tastenkuerzel == 21) {//Wenn V gedrückt wird toggle visiting
            map.toggleVisting();
        }
        if(tastenkuerzel == 19) {//Wenn T gedrückt wird teleport 20 Blöcke nach vorne
            ActivePlayer.positionSetzen(ActivePlayer.positionX()+10,ActivePlayer.positionY());

        }
        if(StartSc.isActive()){
            if(tastenkuerzel == 0){
                StartSc.ShiftLeft();
            }
            else if(tastenkuerzel == 3){
                StartSc.ShiftRight();
            }
            else if (tastenkuerzel == 31) {
                StartSc.SelectButtons();
            }

        }

        if(DialogController3.isWaitingForInput()){
            if(tastenkuerzel == 0){
                //System.out.println("Taste ist gedrückt und isWaitingForInputs = true");
                DialogController3.input("links");
            }
            else if(tastenkuerzel == 3){
                DialogController3.input("rechts");
            }
            else if(tastenkuerzel == 31){
                System.out.println("SPIEL: ENTER GEDRÜCK<t");
                DialogController3.input("enter");
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
