package mainpakcage;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

/** Data structure for multiple columns in one correlation */
public class MultiCorrelations {
    /** Correlated column sets */
    public List<List<Integer>> correlatedCols;
    /** Corresponding relations to each set */
    public List<Integer> correlations;

    /**
     * Constructor
     * Two parameters: correlated column sets and corresponding relations
     * */
    public MultiCorrelations(List<List<Integer>> correlatedCols, List<Integer> correlations) {
        if(correlatedCols.size() != correlations.size()) {
            System.out.print("Unmatched columns and relations!");
            exit(1);
        }

        this.correlatedCols = new ArrayList<List<Integer>>();
        this.correlatedCols.addAll(correlatedCols);

        this.correlations = new ArrayList<Integer>();
        this.correlations.addAll(correlations);
    }

    /**
     * Helper function to generate a string based on the speech
     * */
    public String generateSpeech(String[] colNames, int targetCol) {
        String output = "";

        for(int i = 0; i < correlatedCols.size(); i++) {
            String relation = correlations.get(i) > 0 ? "positively" : "negatively";
            List<Integer> curSet = correlatedCols.get(i);

            output += colNames[curSet.get(0)];

            for(int j = 1; j < curSet.size() - 1; j++) {
                output += ", " + colNames[curSet.get(j)];
            }

            if(curSet.size() >= 1) {
                output += " and " + curSet.get(curSet.size() - 1);
                output += " are ";
            } else {
                output += " is ";
            }

            output += relation + " correlated with " + colNames[targetCol] + ". ";
        }

        System.out.println(output);
        return output;
    }
}
