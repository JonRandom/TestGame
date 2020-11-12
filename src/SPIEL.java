import ea.*;

import java.lang.management.MemoryNotificationInfo;

public class SPIEL extends Game implements TastenLosgelassenReagierbar, Ticker, KlickReagierbar
{

    private int zaehler;

    private Player ActivePlayer;
    private DialogController DialogController;
    private Map3 map;
    private DebugAnzeige debugAnzeige1;
    private DebugAnzeige debugAnzeige2;
    private DebugAnzeige debugAnzeige3;
    private DebugAnzeige debugAnzeige4;

    private DummyPlayer DP;
    private NpcController NpcController;
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

        pet1 = new Pet(1100,1100);
        map = new Map3(ActivePlayer.getBreite(),ActivePlayer.getHoehe());
        NpcController = new NpcController();
        DialogController = new DialogController(NpcController);
        DialogController.setVisisbilty(false);
        debugAnzeige1 = new DebugAnzeige(0,0);
        debugAnzeige2 = new DebugAnzeige(200,0);
        debugAnzeige3 = new DebugAnzeige(500,0);
        debugAnzeige4 = new DebugAnzeige(700,0);
        gamesaver = new GameSaver(); //GameSaver, der im Moment nur Spieler-Sachen speichert
        Minigame2 = new Minigame2(ActivePlayer);
        ObjectController Autos = new ObjectController();



        //HouseLoader1 = new HouseLoader(map);


        //Minigame1 = new Minigame1(); // unused do to lack of ideas

        /* MAUS
        cursor = new Bild(0, 0, "./Assets/MouseC.png");
        hotspot = new Punkt(11,11);
        maus = new Maus(cursor, hotspot);
        mausAnmelden(maus);
        maus.klickReagierbarAnmelden(this);
        */


        wurzel.add(Autos);
        wurzel.add(DP);
        wurzel.add(map);
        wurzel.add(ActivePlayer);
        wurzel.add(NpcController);
        wurzel.add(pet1);

        //statischeWurzel.add(HouseLoader1);
        statischeWurzel.add(StartSc);
        StartSc.setActive(true);
        statischeWurzel.add(DialogController);

        statischeWurzel.add(Minigame2);


        statischeWurzel.add(debugAnzeige1);
        statischeWurzel.add(debugAnzeige2);
        statischeWurzel.add(debugAnzeige3);
        statischeWurzel.add(debugAnzeige4);



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
        debugAnzeige4.SetContent("DialogRunning:" + DialogController.isDialogRunning());

        DP.positionSetzen(playerX,playerY);


        if(!DialogController.GetDialogStatus() && !StartSc.isActive()) {
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
        if(NpcController.checkForCollision(ActivePlayer, map.isVisiting())&&!DialogController.GetDialogStatus()){

            DialogController.openDialog(ActivePlayer);
            //DialogController.SetContent("Hallo ich bin ein NPC der mit dir ein Dialog führen kann");
            DialogController.setVisisbilty(true);
        }


        gamesaver.SavePlayer(ActivePlayer);

        pet1.follow(ActivePlayer);

        Minigame2.tick();
    }




    //  https://engine-alpha.org/wiki/Tastaturtabelle
    public void tasteReagieren(int tastenkuerzel)
    {
        //Togglet beim Drücken der G Taste den Dialog
        if(tastenkuerzel == 6){
            DialogController.toggleVisibilty();
        }
        if(tastenkuerzel == 8){//I als in
            map.enterHouse(ActivePlayer,0);
        }
        if(tastenkuerzel == 14){//o als out
            //HouseLoader1.HideView();
            map.leaveHouse(ActivePlayer);
        }
        if(tastenkuerzel == 17){ //Wenn R gedrückt
            DialogController.dialogBeginnen();
        }

        if(tastenkuerzel == 12){//M für minigame
            Minigame2.startGame();
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
            else if (tastenkuerzel == 31) {
                StartSc.SelectButtons();
            }
            //Leertaste
            //Leertaste
            /*
            else if (tastenkuerzel == 30) {
                StartSc.TextStartScEntfernen();
                StartSc.setActive(false);
                switch (StartSc.getSelection()) {

                    case (0):
                        System.out.println("test0");
                        break;
                    case (1):
                        System.out.println("test1");
                        break;
                    case (2):
                        System.out.println("test2");
                        break;
                    case (3):
                        System.out.println("test3");
                        break;

                }
            } */

        }

        if(DialogController.GetDialogStatus()){
            if(tastenkuerzel == 0){
                DialogController.ShiftLeft();
            }
            else if(tastenkuerzel == 3){
                DialogController.ShiftRight();
            }
            else if(tastenkuerzel == 31) {
                DialogController.SelectWahl(ActivePlayer);
            }
            //leertaste
            else if(tastenkuerzel == 30){
                //macht dialog aus
                DialogController.toggleVisibilty();
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
