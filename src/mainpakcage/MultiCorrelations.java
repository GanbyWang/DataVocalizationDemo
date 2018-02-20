package mainpakcage;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

/** Data structure for multiple columns in one correlation */
public class MultiCorrelations {
    /**
     * Correlated column sets
     * Right now we only only at most 2 columns as a pair
     *  */
    public List<List<Integer>> correlatedCols;
    /** Corresponding relations to each set */
    public List<Integer> correlations;
    /** Score of the speech */
    public int score;

    /**
     * Constructor
     * Two parameters: correlated column sets and corresponding relations
     * */
    public MultiCorrelations(List<List<Integer>> correlatedCols, List<Integer> correlations) {
        // Check if dimensions are matched
        if(correlatedCols.size() != correlations.size()) {
            System.out.print("Unmatched columns and relations!");
            exit(1);
        }

        // Initialize score
        score = 0;

        // Do deep copies
        this.correlatedCols = new ArrayList<List<Integer>>();
        for(List<Integer> item : correlatedCols) {
            List<Integer> singlePair = new ArrayList<Integer>();
            singlePair.addAll(item);
            this.correlatedCols.add(singlePair);
        }

        this.correlations = new ArrayList<Integer>();
        this.correlations.addAll(correlations);
    }

    /** Helper function to print all information of this speech */
    public void printInfo() {
        // Print columns
        System.out.printf("Selected column pairs:\n");
        for(List<Integer> pair : correlatedCols) {
            System.out.printf("{");
            for(int i = 0; i < pair.size() - 1; i++) {
                System.out.printf("%d, ", pair.get(i));
            }
            System.out.printf("%d} ", pair.get(pair.size() - 1));
        }
        System.out.printf("\n");

        // Print relations
        System.out.printf("Corresponding relations:\n");
        for(int i = 0; i < correlations.size(); i++) {
            System.out.printf("%d ", correlations.get(i));
        }
        System.out.printf("\n");

        // Print score
        System.out.printf("Score: %d\n", score);
    }

    /**
     * Helper function to generate a string based on the speech
     * */
    public String generateSpeech(String[] colNames, int targetCol) {
        String output = "";

        List<Integer> posSingleSet = new ArrayList<Integer>();
        List<Integer> negSingleSet = new ArrayList<Integer>();

        // Enumerate every column set
        for(int i = 0; i < correlatedCols.size(); i++) {
            String relation = correlations.get(i) > 0 ? "positively" : "negatively";
            List<Integer> curSet = correlatedCols.get(i);

            if(curSet.size() > 1) {
                output += "The combination of ";
            } else {
                /*
                * If there's only one column in this set
                * Then add it to single sets to compress and skip this enumeration
                * */
                if(correlations.get(i) > 0) {
                    posSingleSet.add(curSet.get(0));
                } else {
                    negSingleSet.add(curSet.get(0));
                }
                continue;
            }

            output += colNames[curSet.get(0)];

            for(int j = 1; j < curSet.size() - 1; j++) {
                output += ", " + colNames[curSet.get(j)];
            }

            output += " and " + colNames[curSet.get(curSet.size() - 1)];
            output += " are ";

            output += relation + " correlated with " + colNames[targetCol] + ". ";
        }

        // Add compressed sentences to output as well
        output += compressedSentences(posSingleSet, negSingleSet, colNames, targetCol);

//        System.out.println(output);
        return output;
    }

    /* Helper function to generate compressed sentence */
    private String compressedSentences(List<Integer> positiveCols, List<Integer> negativeCols,
                                       String[] columnNames, int targetColumn) {
        String outputSpeech = "";

        if(positiveCols.size() > 1) {
            outputSpeech += "Columns ";
            for(int i = 0; i < positiveCols.size() - 1; i++) {
                outputSpeech += columnNames[positiveCols.get(i)];
                outputSpeech += ", ";
            }
            outputSpeech += "and ";
            outputSpeech += columnNames[positiveCols.get(positiveCols.size() - 1)];
            outputSpeech += " are positively correlated with column ";
            outputSpeech += columnNames[targetColumn];
            outputSpeech += ". ";
        } else if(positiveCols.size() == 1) {
            outputSpeech += "Column ";
            outputSpeech += columnNames[positiveCols.get(0)];
            outputSpeech += " is positively correlated with column ";
            outputSpeech += columnNames[targetColumn];
            outputSpeech += ". ";
        }

        if(negativeCols.size() > 1) {
            outputSpeech += "Columns ";
            for(int i = 0; i < negativeCols.size() - 1; i++) {
                outputSpeech += columnNames[negativeCols.get(i)];
                outputSpeech += ", ";
            }
            outputSpeech += "and ";
            outputSpeech += columnNames[negativeCols.get(negativeCols.size() - 1)];
            outputSpeech += " are negatively correlated with ";
            outputSpeech += columnNames[targetColumn];
            outputSpeech += ". ";
        } else if(negativeCols.size() == 1) {
            outputSpeech += "Column ";
            outputSpeech += columnNames[negativeCols.get(0)];
            outputSpeech += " is negatively correlated with ";
            outputSpeech += columnNames[targetColumn];
            outputSpeech += ". ";
        }

        return outputSpeech;
    }
}
