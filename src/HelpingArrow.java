import ea.Bild;
import ea.Knoten;
import ea.Punkt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpingArrow extends Knoten {


    private DialogController5 dialogController;
    private Player player;

    private Map<Integer, Punkt> housePos = new HashMap<Integer, Punkt>() {};

    //Arrow Logic
    private Bild[] arrowImgs = new Bild[8];
    private Boolean[] arrowVisibility = new Boolean[8];

    //img stuff
    private final String mainImgPath = "./Assets/HelpingArrow/";


    private int directionalOffsetToDiagonal = 50;


    public HelpingArrow(DialogController5 dC, Player p) {
        this.dialogController = dC;
        this.player = p;

        /* POSITONEN DER HÄUSER
        Kiosk(1): 4970 | 1320
        Zuhause(3): 845 | 2100
        Polizei(2): 724 | 4500
        Schule(0): 6430 | 4200
        Verein(4): 7940 | 240
        */
        housePos.put(1, new Punkt(4970, 1320)); //kiosk
        housePos.put(3, new Punkt(845, 2100)); //Zuhause
        housePos.put(2, new Punkt(724, 4500)); //Polizei
        housePos.put(0, new Punkt(6430, 4200)); //Schule
        housePos.put(4, new Punkt(7940, 240)); //Verein

        //updateArrows();

    }
    public void fillImgArray(){
        // von 0 oben mitte im Uhrzeiger rum
    }
    public void updateArrows(){
        List<NPC2> npcPos = dialogController.getHighlightedNpcObjects();
        float playerPosX = player.getPosX();
        float playerPosY = player.getPosY();

        //macht jedes Pfeil element aus;
        Arrays.fill(arrowVisibility, false);

        for(NPC2 npc : npcPos){
            float comparePosX;
            float comparePosY;
            if(npc.isInHouse()){
                //falls in Haus NPC Pos durch Haus pos ersetzen
                Punkt houseLocation = housePos.get(npc.getHouseNumber());
                comparePosX = houseLocation.x;
                comparePosY = houseLocation.y;

            } else{
                comparePosX = npc.getPosX();
                comparePosY = npc.getPosY();
            }

            if(comparePosX > playerPosX){
                //Haus ist rechts vom Spieler
                if(comparePosY > playerPosY){
                    //Haus ist unter Spieler
                    if((comparePosY - playerPosY) < directionalOffsetToDiagonal){
                        //der y-Abstand ist aber innerhalb von dem Offset
                        //soll Pfeil 2 anzeigen
                        arrowVisibility[2] = true;
                    }
                    else if((comparePosX - playerPosX) < directionalOffsetToDiagonal){
                        //der x-Abstand ist innerhalb vom Offset
                        //soll Pfeil 4 anzeigen
                        arrowVisibility[4] = true;
                    } else{
                        //passt gar nicht in die Offsets rein
                        //zeugt den Dialgonalen Pfeil 3 an.
                        arrowVisibility[3] = true;
                    }
                }
                else if(comparePosY < playerPosY){
                    //Haus ist rehcts über  Spieler
                    if((playerPosY - comparePosY) < directionalOffsetToDiagonal){
                        //der y-Abstand ist aber innerhalb von dem Offset
                        //soll Pfeil 2 anzeigen
                        arrowVisibility[2] = true;
                    }
                    else if((comparePosX - playerPosX) < directionalOffsetToDiagonal){
                        //der x-Abstand ist innerhalb vom Offset
                        //soll Pfeil 0 anzeigen
                        arrowVisibility[0] = true;
                    } else{
                        //passt gar nicht in die Offsets rein
                        //zeugt den Dialgonalen Pfeil 3 an.
                        arrowVisibility[1] = true;
                    }
                }

            } else{
                //comparePosX < playerPosX
                //Haus ist  links vom Spieler
                if(comparePosY < playerPosY){
                    //Haus ist links über  Spieler
                    if((playerPosY - comparePosY) < directionalOffsetToDiagonal){
                        //der y-Abstand ist aber innerhalb von dem Offset
                        //soll Pfeil 6 anzeigen
                        arrowVisibility[6] = true;
                    }
                    else if((playerPosX -  comparePosX) < directionalOffsetToDiagonal){
                        //der x-Abstand ist innerhalb vom Offset
                        //soll Pfeil 0 anzeigen
                        arrowVisibility[0] = true;
                    } else{
                        //passt gar nicht in die Offsets rein
                        //zeugt den Dialgonalen Pfeil 7 an.
                        arrowVisibility[7] = true;
                    }
                }
                else if(comparePosY > playerPosY){
                    //Haus ist links unter  Spieler
                    if((comparePosY - playerPosY) < directionalOffsetToDiagonal){
                        //der y-Abstand ist aber innerhalb von dem Offset
                        //soll Pfeil 6 anzeigen
                        arrowVisibility[6] = true;
                    }
                    else if((playerPosX -  comparePosX) < directionalOffsetToDiagonal){
                        //der x-Abstand ist innerhalb vom Offset
                        //soll Pfeil 4 anzeigen
                        arrowVisibility[4] = true;
                    } else{
                        //passt gar nicht in die Offsets rein
                        //zeugt den Dialgonalen Pfeil 5 an.
                        arrowVisibility[5] = true;
                    }
                }
            }
        }
        snycArrowVisibility();
    }
    public void snycArrowVisibility(){
        for (int i = 0; i < arrowVisibility.length; i++) {

            arrowImgs[i].sichtbarSetzen(arrowVisibility[i]);
            System.out.print( "Pfeil: " + i +  " wird mit dem Zustand: " + arrowVisibility[i] + " versehen");
        }

    }
}
