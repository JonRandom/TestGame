import ea.*;

public class SPIEL extends Game implements TastenLosgelassenReagierbar, Ticker, KlickReagierbar {

    private int zaehler;
    private Player ActivePlayer;
    private DialogController4 DialogController4;
    private Map3 map;
    private ItemController itemController;
    private DebugAnzeige debugAnzeige1;
    private DebugAnzeige debugAnzeige2;
    private DebugAnzeige debugAnzeige3;
    private DebugAnzeige debugAnzeige4;
    private DebugAnzeige debugAnzeige5;
    private DebugAnzeige debugAnzeige6;
    private DebugAnzeige debugAnzeige7;
    private DebugAnzeige debugAnzeige8;
    private DebugAnzeige debugAnzeige9;
    private DebugAnzeige debugAnzeige10;

    //sound
    private SoundController soundController;

    //MAIN LOADING
    private boolean initDone = false;

    private DummyPlayer DP;
    private NpcController2 NpcController2;
    private StartingScreen StartSc;
    //private Minigame1 Minigame1;
    private Minigame2 Minigame2;
    private Pet pet1;

    public GameSaver gamesaver;

    private Bild cursor;
    private Punkt hotspot;
    private Maus maus;

    //zähler für 2. tick routine;
    public int tickCounter;


    public SPIEL() {
        super(MAIN.x, MAIN.y, "P-SEM GAME");//windowsize kann nicht mit variable gemacht werden.

        StartSc = new StartingScreen();
        statischeWurzel.add(StartSc);
        StartSc.setActive(true);
    }

    public void Konstruktor() {
        tickerAnmelden(this, 16);

        zaehler = 0;
        gamesaver = new GameSaver(); //GameSaver, der im Moment nur Spieler-Sachen speichert

        DP = new DummyPlayer(600, 400);


        ActivePlayer = new Player(gamesaver.getPosX(), gamesaver.getPosX(), gamesaver);
        soundController = new SoundController();
        //pet1 = new Pet(1100,1100);
        NpcController2 = new NpcController2(ActivePlayer, gamesaver);
        map = new Map3(NpcController2, soundController, ActivePlayer, gamesaver);
        DialogController4 = new DialogController4(NpcController2, gamesaver);
        debugAnzeige1 = new DebugAnzeige(0, 0);
        debugAnzeige2 = new DebugAnzeige(200, 0);
        debugAnzeige3 = new DebugAnzeige(350, 0);
        debugAnzeige4 = new DebugAnzeige(500, 0);
        debugAnzeige5 = new DebugAnzeige(700, 0);
        debugAnzeige6 = new DebugAnzeige(1300, 0);
        debugAnzeige7 = new DebugAnzeige(0, 30); //dialogPos
        debugAnzeige8 = new DebugAnzeige(400, 30); //ButtonCursor
        debugAnzeige9 = new DebugAnzeige(650, 30);
        debugAnzeige10 = new DebugAnzeige(1000, 30); //LastSelfBoolean
        Minigame2 = new Minigame2(ActivePlayer);
        itemController = new ItemController(ActivePlayer, gamesaver);
        //ObjectController Autos = new ObjectController();

        if (true) {
            //Beginn oben links, im Uhrzeigersinn
            Bild testauto1 = new Bild(220, 1550, "./Assets/Tests/Testauto.png");
            wurzel.add(testauto1);
            animationsManager.streckenAnimation(testauto1, 40000, new Punkt(220, 1550), new Punkt(5300, 1550), new Punkt(5300, 2500), new Punkt(220, 2500));
            //Auto 2 fährt um Häuserblock beide Reihen Wphnhäuser
            //Beginn unten rechts, im Uhrzeigersinn
            Bild testauto2 = new Bild(5300, 3550, "./Assets/Tests/Testauto.png");
            wurzel.add(testauto2);
            animationsManager.streckenAnimation(testauto2, 40000, new Punkt(5300, 3550), new Punkt(220, 3550), new Punkt(220, 1550), new Punkt(5300, 1550));
            //Auto 3 fährt um Häuserblock Schule, Baustelle, Polizei,...
            //Beginn unten links, im Uhrzeigersinn, gegen Uhrzeigersinn, im Uhrzeigersinn => fährt "liegende 8"
            Bild testauto3 = new Bild(220, 4800, "./Assets/Tests/Testauto.png");
            wurzel.add(testauto3);
            animationsManager.streckenAnimation(testauto3, 80000, new Punkt(220, 4800), new Punkt(220, 3640), new Punkt(5300, 3640), new Punkt(5300, 4900), new Punkt(7420, 4890), new Punkt(7420, 3550), new Punkt(5300, 3550), new Punkt(5300, 4800));
        }


        //wurzel.add(Autos);
        wurzel.add(DP);
        wurzel.add(map);
        wurzel.add(ActivePlayer);
        wurzel.add(NpcController2);
        wurzel.add(itemController);
        //
        //wrzel.add(pet1);

        //statischeWurzel.add(HouseLoader1);
        statischeWurzel.add(DialogController4);

        statischeWurzel.add(Minigame2);


        statischeWurzel.add(debugAnzeige1);
        statischeWurzel.add(debugAnzeige2);
        statischeWurzel.add(debugAnzeige3);
        statischeWurzel.add(debugAnzeige4);
        statischeWurzel.add(debugAnzeige5);
        statischeWurzel.add(debugAnzeige6);
        statischeWurzel.add(debugAnzeige7);
        statischeWurzel.add(debugAnzeige8);
        statischeWurzel.add(debugAnzeige9);
        statischeWurzel.add(debugAnzeige10);


        tastenReagierbarAnmelden(this);
        tastenLosgelassenReagierbarAnmelden(this);



        DialogController4.highLightReadyNpcs(); //einmal alle highlighten die können

        fokusSetzten();
        StartSc.hideLoadingScreen();
        initDone = true;
    }


    public void fokusSetzten() {
        System.out.println("FOKUS SETZEN");
        cam.fokusSetzen(ActivePlayer);
        BoundingRechteck CamBounds = new BoundingRechteck(0, 0, map.getBreite(), map.getHoehe());
        cam.boundsSetzen(CamBounds);
    }

    public void tick() {
        if(initDone) {
            if (itemController.checkForCollision()) {
                gamesaver.addItem(itemController.getCollidingItemName());
                itemController.hideCollidingItem();

            }

            int playerX = ActivePlayer.positionX();
            int playerY = ActivePlayer.positionY();

            debugAnzeige1.SetContent("Pos:" + playerX + "  -  " + playerY);
            debugAnzeige2.SetContent("Visiting:" + map.isVisiting());
            debugAnzeige3.SetContent("Geld:" + ActivePlayer.getMoney());
            debugAnzeige4.SetContent("Dialog2Activ:" + DialogController4.isActive());
            debugAnzeige5.SetContent("ZeitPosition: " + DialogController4.getGlobalTemporalPosition());
            debugAnzeige6.SetContent("PlayingLastLine: " + DialogController4.isPlayingLastLine());
            debugAnzeige7.SetContent("CurrentDialogCode: " + DialogController4.getCurrentDialogCode());
            debugAnzeige8.SetContent("ButtonAuswahl: " + DialogController4.getButtonCursor());
            debugAnzeige9.SetContent("OneButtonMode?: " + DialogController4.isOneButtonMode());
            debugAnzeige10.SetContent("LastSelf: " + DialogController4.isPlayingLastLine());

            DP.positionSetzen(playerX, playerY);


            if (!DialogController4.isActive() && !StartSc.isActive()) {
                int walkspeed = ActivePlayer.getWalkspeed();

                if (tasteGedrueckt(Taste.W)) {
                    DP.positionSetzen(ActivePlayer.getPosX(), ActivePlayer.getPosY() - walkspeed);

                    if (map.isWalkable2(DP, ActivePlayer)) {
                        ActivePlayer.WalkTop();
                    }

                }
                if (tasteGedrueckt(Taste.S)) {
                    DP.positionSetzen(ActivePlayer.getPosX(), ActivePlayer.getPosY() + walkspeed);

                    if (map.isWalkable2(DP, ActivePlayer)) {
                        ActivePlayer.WalkBottom();
                    }
                }

                if (tasteGedrueckt(Taste.A)) {
                    DP.positionSetzen(ActivePlayer.getPosX() - walkspeed, ActivePlayer.getPosY());
//
                    if (map.isWalkable2(DP, ActivePlayer)) {
                        ActivePlayer.WalkLeft();
                    }
                }

                if (tasteGedrueckt(Taste.D)) {
                    DP.positionSetzen(ActivePlayer.getPosX() + walkspeed, ActivePlayer.getPosY());


                    if (map.isWalkable2(DP, ActivePlayer)) {
                        ActivePlayer.WalkRight();
                    }
                }
            }
            if (NpcController2.checkForCollision(ActivePlayer) && !DialogController4.isActive()) {
                String npcID = NpcController2.getCollidingNPC(ActivePlayer);
                System.out.println("Der Spieler schneidet den NPC mit der ID: " + npcID);
                DialogController4.startDialog(npcID);

            }

            gamesaver.SavePlayer(ActivePlayer);
            //pet1.follow(ActivePlayer);
            Minigame2.tick();
            tickCounter++;
            if (tickCounter > 400) { //alle vier sekunden
                tickCounter = 0;
                slowTick();
            }

        }
        StartSc.tickLoadingAnimation();
    }

    public void slowTick() {
        gamesaver.saveJSON();
    }



    //  https://engine-alpha.org/wiki/Tastaturtabelle
    public void tasteReagieren(int tastenkuerzel) {
        if (!StartSc.isActive()) {
            if (tastenkuerzel == 8) {//I als in
                //map.enterHouse(ActivePlayer,0);
            }
            if (tastenkuerzel == 14) {//o als out
                //HouseLoader1.HideView();
                map.leaveHouse(ActivePlayer);
            }
            if (tastenkuerzel == 17) { //Wenn R gedrückt

            }

            if (tastenkuerzel == 12) {//M für minigame
                Minigame2.startGame();
            }


            if (tastenkuerzel == 21) {//Wenn V gedrückt wird toggle visiting
                map.toggleVisting();
            }
            if (tastenkuerzel == 19) {//Wenn T gedrückt wird teleport 20 Blöcke nach vorne
                ActivePlayer.positionSetzen(ActivePlayer.positionX() + 10, ActivePlayer.positionY());

            }



            if (DialogController4.isWaitingForInput()) {
                if (tastenkuerzel == 0) {
                    //System.out.println("Taste ist gedrückt und isWaitingForInputs = true");
                    DialogController4.input("links");
                } else if (tastenkuerzel == 3) {
                    DialogController4.input("rechts");
                } else if (tastenkuerzel == 31) {
                    System.out.println("SPIEL: ENTER GEDRÜCKt");
                    DialogController4.input("enter");
                }
            }
        }

        if (StartSc.isActive()) {
            if (tastenkuerzel == 0) {
                StartSc.ShiftLeft();
            } else if (tastenkuerzel == 3) {
                StartSc.ShiftRight();
            } else if (tastenkuerzel == 31) { //enter
                int sel = StartSc.getSelection();
                StartSc.startLoadingScreen();
                if (sel == 1) {

                    NewGameLoader gl = new NewGameLoader();
                    System.out.println("fertig mit Laden");
                }
                Konstruktor();
            }
        }
    }


    public void warte(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tasteLosgelassen(int i) {
        //System.out.println(i);
    }


    @Override
    public void klickReagieren(Punkt punkt) {
        System.out.println("Klick bei (" + punkt + ").");
        int dx = (int) ActivePlayer.getPosX() - (int) punkt.x();
        int dy = (int) ActivePlayer.getPosY() - (int) punkt.y();
        System.out.println(dx + dy);
        BallTest ball1 = new BallTest((int) ActivePlayer.getPosX(), (int) ActivePlayer.getPosY(), dx, dy);
        wurzel.add(ball1);
    }
}
