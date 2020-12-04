import ea.*;

public class SPIEL extends Game implements TastenLosgelassenReagierbar, Ticker, KlickReagierbar {

    private int zaehler;
    private Player ActivePlayer;
    private DialogController5 DialogController;
    private Map3 map;

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

    //Items
    private ItemController itemController;
    private ItemAnimation itemAnimator;

    //Fade Screen
    private FadeScreen fadeScreen;

    //COMPUTER / Fynstergram
    private ComputerScreen computer;

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

    //zähler für 2.tick routine;
    public int tickCounter;


    public SPIEL() {
        super(MAIN.x, MAIN.y, "P-SEM GAME");//windowsize kann nicht mit variable gemacht werden.

        soundController = new SoundController();
        soundController.startTitleMusic();
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
        //pet1 = new Pet(1100,1100);
        NpcController2 = new NpcController2(ActivePlayer, gamesaver);


        DialogController = new DialogController5(NpcController2, gamesaver);
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
        itemAnimator = new ItemAnimation();
        itemController = new ItemController(ActivePlayer, gamesaver, DialogController, itemAnimator);
        computer = new ComputerScreen();

        fadeScreen = new FadeScreen();

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
        map = new Map3(NpcController2, soundController, ActivePlayer, gamesaver, itemController);
        wurzel.add(map);
        wurzel.add(ActivePlayer);
        wurzel.add(NpcController2);
        wurzel.add(itemController);


        //statischeWurzel.add(HouseLoader1);
        statischeWurzel.add(DialogController);

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

        statischeWurzel.add(computer);
        statischeWurzel.add(itemAnimator);

        statischeWurzel.add(fadeScreen);


        tastenReagierbarAnmelden(this);
        tastenLosgelassenReagierbarAnmelden(this);


        DialogController.highLightReadyNpcs(); //einmal alle highlighten die können

        fokusSetzten();
        StartSc.hideLoadingScreen();
        initDone = true;

        soundController.stopAllMusic();

    }


    public void fokusSetzten() {
        //System.out.println("FOKUS SETZEN");
        cam.fokusSetzen(ActivePlayer);
        BoundingRechteck CamBounds = new BoundingRechteck(0, 0, map.getBreite(), map.getHoehe());
        cam.boundsSetzen(CamBounds);
    }

    public void tick() {
        if (initDone) {
            if (itemController.checkForCollision()) {
                gamesaver.addItem(itemController.getCollidingItemName());
                itemController.hideCollidingItem();
            }

            int playerX = ActivePlayer.positionX();
            int playerY = ActivePlayer.positionY();

            debugAnzeige1.SetContent("Pos:" + playerX + "  -  " + playerY);
            debugAnzeige2.SetContent("Visiting:" + map.isVisiting());
            debugAnzeige3.SetContent("Geld:" + ActivePlayer.getMoney());
            //debugAnzeige4.SetContent("Dialog2Activ:" + DialogController.isActive());
            debugAnzeige5.SetContent("ZeitPosition: " + DialogController.getGlobalTemporalPosition());
            //debugAnzeige6.SetContent("PlayingLastLine: " + DialogController.isPlayingLastLine());
            debugAnzeige7.SetContent("CurrentDialogCode: " + DialogController.getCurrentDialogCode());
            //debugAnzeige8.SetContent("ButtonAuswahl: " + DialogController.getButtonCursor());
            //debugAnzeige9.SetContent("OneButtonMode?: " + DialogController.isOneButtonMode());
            //debugAnzeige10.SetContent("LastSelf: " + DialogController.isPlayingLastLine());

            DP.positionSetzen(playerX, playerY);


            if (!DialogController.isActive() && !StartSc.isActive() && !itemAnimator.isActiv()) {
                int walkspeed = ActivePlayer.getWalkspeed();

                if (tasteGedrueckt(Taste.W)) {
                    DP.positionSetzen(ActivePlayer.getPosX(), ActivePlayer.getPosY() - walkspeed);
                    if (map.isWalkable2(DP, ActivePlayer)) {
                        ActivePlayer.WalkTop();
                    }

                }
                else if (tasteGedrueckt(Taste.S)) {
                    DP.positionSetzen(ActivePlayer.getPosX(), ActivePlayer.getPosY() + walkspeed);
                    if (map.isWalkable2(DP, ActivePlayer)) {
                        ActivePlayer.WalkBottom();
                    }
                }

                if (tasteGedrueckt(Taste.A)) {
                    DP.positionSetzen(ActivePlayer.getPosX() - walkspeed, ActivePlayer.getPosY());
                    if (map.isWalkable2(DP, ActivePlayer)) {
                        ActivePlayer.WalkLeft();
                    }
                }

                else if (tasteGedrueckt(Taste.D)) {
                    DP.positionSetzen(ActivePlayer.getPosX() + walkspeed, ActivePlayer.getPosY());
                    if (map.isWalkable2(DP, ActivePlayer)) {
                        ActivePlayer.WalkRight();
                    }
                }
                if(!tasteGedrueckt(Taste.W) && !tasteGedrueckt(Taste.S) && !tasteGedrueckt(Taste.A) && !tasteGedrueckt(Taste.D)){ //Es wird keine der Tasten gedrückt
                    ActivePlayer.standStill();
                }
            }
            if (NpcController2.checkForCollision(ActivePlayer) && !DialogController.isActive()) {
                String npcID = NpcController2.getCollidingNPC(ActivePlayer);
                System.out.println("Der Spieler schneidet den NPC mit der ID: " + npcID);
                DialogController.startDialog(npcID);

            }

            gamesaver.SavePlayer(ActivePlayer);
            //pet1.follow(ActivePlayer);

            Minigame2.tick();
            itemAnimator.tick();
            fadeScreen.tick();
            soundController.tickMusic();

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

                if (computer.isActiv()) {
                    computer.closePC();
                } else {
                    computer.openPC();
                }
            }
            if (tastenkuerzel == 14) {//o als out
                //HouseLoader1.HideView();
                map.leaveHouse();
            }
            if (tastenkuerzel == 17) { //Wenn R gedrückt
                soundController.toggleMute();
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


            if (DialogController.isWaitingForInput()) {
                if (tastenkuerzel == 0) {
                    //System.out.println("Taste ist gedrückt und isWaitingForInputs = true");
                    DialogController.input("links");
                } else if (tastenkuerzel == 3) {
                    DialogController.input("rechts");
                } else if (tastenkuerzel == 31) {
                    System.out.println("SPIEL: ENTER GEDRÜCKt");
                    DialogController.input("enter");
                }
            }
            if (itemAnimator.isActiv()) {
                if (tastenkuerzel == 31) {
                    System.out.println("SPIEL: ENTER GEDRÜCKt");
                    itemAnimator.hideEverything();
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
                switch (sel) {
                    case (0):
                        System.out.println("PLAY: Spiel wird gestartet");
                        StartSc.startLoadingScreen();
                        Konstruktor();
                        break;

                    case (1):
                        System.out.println("NEW GAME: Spiel überschriebt Dateien");
                        StartSc.startLoadingScreen();
                        NewGameLoader gl = new NewGameLoader();
                        //System.out.println("LADEN FERTIG!!");
                        Konstruktor();
                        break;

                    case (2):
                        System.out.println("EXIT KNOPF GEDRÜCKT: Spiel wird geschlossen");
                        schliessen();
                        break;

                    case (3):
                        System.out.println("ABOUT GEDRÜCKT: ------");
                        break;

                    case (4):
                        System.out.println("SETTINGS GEDRÜCKT: ------");
                        break;
                }


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
