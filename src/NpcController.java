import ea.Knoten;

public class NpcController extends Knoten {

    private Npc npc1;

    public NpcController(){
        npc1 = new Npc(600,600);

        this.add(npc1);

    }

    public Npc GetCollider(Player Spieler){

        if(Spieler.schneidet(npc1)) {
            return npc1;
        }else{
            return null;
        }

    }
    public boolean ColliderTest(Player Spieler){

        if(Spieler.schneidet(npc1)) {
            return true;
        }else{
            return false;
        }

    }
}
