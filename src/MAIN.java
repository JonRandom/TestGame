import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Erzeugt das SPIEL Objekt und setzt Fenster Einstellungen.
 * Es ist die höchste Klasse in der Hierarchie
 *
 *
 */


public class MAIN {
    public static int x = 1500;
    public static int y = 1000;

    public static int blackThreshold = 4;// für die Kolliderbildbearbeitung
    public static int whiteThreshold = 250;// für die Kolliderbildbearbeitung

    public static void main(String[] args){
        //System.out.println("Random Array = " + Arrays.toString(giveShuffeldArray(10)));
        SPIEL spiel  = new SPIEL();
        spiel.fokusSetzten();


    }

    private static int[] giveShuffeldArray(int size){
        int[] arraySolution = new int[size];
        List<Integer> solution = new ArrayList<>();
        for (int i = 0; i < size; i++){ //to generate from 0-10 inclusive.
            //For 0-9 inclusive, remove the = on the <=
            solution.add(i);
        }
        Collections.shuffle(solution);
        for (int i = 0; i < solution.size(); i++) {
            arraySolution[i] = solution.get(i);
        }
        return arraySolution;

    }

}
