package mainpakcage;

import java.util.List;
import static java.lang.System.exit;

/** Class to connect with Speech to record the relations of each column */
public class SpeechRelation {
    /** Which columns this speech is talking about */
    public Speech speech;
    /** Corresponding relations to columns */
    public List<Integer> relations;

    /**
     * Constructor function
     * Two parameter: speech and corresponding factors
     * */
    public SpeechRelation(Speech speech, List<Integer> relations) {
        // Exit if dimensions don't match
        if(speech.columns.size() != relations.size()) {
            System.out.print("Unmatched columns and relations!");
            exit(1);
        }

        this.speech = new Speech(speech);
        this.relations = relations;
    }

    /** Print information */
    public void printInfo() {
        speech.printInfo();
        System.out.println("Corresponding relations:");
        for(Integer relation : relations) {
            System.out.printf("%d ", relation);
        }
        System.out.printf("\n");
    }
}