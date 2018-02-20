package mainpakcage;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

/**
 * Class to generate speech with combinations of columns
 * Subclass of SpeechGenerator
 */
public class MixedSpeechGenerator extends SpeechGenerator {
    protected List<MultiCorrelations> speeches;     // Speech list
    protected MultiCorrelations optimalSpeech;      // Optimal speech

    /**
     * Constructor function
     * Accept the same arguments of super class constructor
     * */
    public MixedSpeechGenerator(int targetColumn, DataAnalysis data,
                                int maxCol, int sampleFraction, String extraSQL) {

        // Initialize fields
        this.targetColumn = targetColumn;
        this.data = data;
        this.sampleFraction = sampleFraction;
        this.extraSQL = extraSQL;
        this.maxCol = maxCol;

        // Generate all speeches
        generateAllSpeeches();
        compete();
        getOptimal();
    }

    /*
    * Function to generate all speeches
    * Uses the BitSetIterator class to simplify execution
    * */
    private void generateAllSpeeches() {
        speeches = new ArrayList<MultiCorrelations>();

        /*
        * 28 possibilities:
        * 7 for single columns
        * 21 for column pairs
        * Overlapping target column is dealt with in another place
        * Spec: all columns after the target column is reduced by 1,
        *       to eliminate the gap
        * */
        BitSet set = new BitSet(28);
        set.set(0, 28, true);

        // Enumerate all widths
        for(int k = 1; k <= maxCol; k++) {
            BitSetIterator bitSetIterator = new BitSetIterator(set, k);

            while(bitSetIterator.hasNext()) {
                BitSet answer = bitSetIterator.next();
                List<List<Integer>> tmpColPair = new ArrayList<List<Integer>>();

                for(int i = 0; i < 28; i++) {
                    if(answer.get(i) == true) {
                        // Get the actual column set
                        tmpColPair.add(NumToPair(i));
                    }
                }

                // Generate all possible relation combinations
                addAllCorrelatedSpeeches(tmpColPair);
            }
        }
    }

    /*
    * Helper function to generate all possible speech
    * Given a column set, generate all possible relation sets
    *   and create corresponding speeches
    * */
    private void addAllCorrelatedSpeeches(List<List<Integer>> colPairs) {
        int k = colPairs.size();

        // The combination width is decided by the column set size
        BitSet set = new BitSet(k);
        set.set(0, k, true);

        for (int i = 0; i <= k; i++) {
            BitSetIterator bitSetIterator = new BitSetIterator(set, i);

            while (bitSetIterator.hasNext()) {
                BitSet answer = bitSetIterator.next();
                List<Integer> correlations = new ArrayList<Integer>();

                // Generate a relation set based on the bit set
                for (int j = 0; j < k; j++) {
                    if (answer.get(j) == true) {
                        correlations.add(1);
                    } else {
                        correlations.add(-1);
                    }
                }

                // Add a new speech to the speech list
                speeches.add(new MultiCorrelations(colPairs, correlations));
            }
        }
    }

    /*
    * Helper function to convert an integer to a column number or a column pair
    * Called by generateAllSpeeches
    * */
    private List<Integer> NumToPair(int x) {
        List<Integer> result = new ArrayList<Integer>();

        // Check illegal arguments
        if(x < 0 || x > 27) {
            System.out.println("Invalid convert!");
            System.exit(1);
        }

        // Data-based transformation (7 single columns and 21 pairs)
        if(x <= 6) {
            result.add(x);
        } else if(x <= 12) {
            result.add(0);
            result.add(x - 6);
        } else if (x > 12 && x <= 17) {
            result.add(1);
            result.add(x - 11);
        } else if (x > 17 && x <= 21) {
            result.add(2);
            result.add(x - 15);
        } else if (x > 21 && x <= 24) {
            result.add(3);
            result.add(x - 18);
        } else if (x > 24 && x <= 26) {
            result.add(4);
            result.add(x - 20);
        } else {
            result.add(5);
            result.add(6);
        }

        // Deal with overlapping with target column
        for(int i = 0; i < result.size(); i++) {
            if(result.get(i) >= targetColumn) {
                result.set(i, result.get(i) + 1);
            }
        }

        return result;
    }

    /*
    * Function to compete among all speeches
    * Mostly the same as the ones in super class
    * */
    private void compete() {
        // Print basic information
        System.out.printf("Maximum Columns: %d\n", maxCol);
        System.out.printf("Target Column: %d\n", targetColumn);

        List<List<Double>> sample = data.sample;
        int sampleNum = sample.size();
        int repeatTimes = sampleNum;

        System.out.printf("Sampling Fraction: %d\n", sampleFraction);
        System.out.printf("Repeated Times: %d\n", repeatTimes);

        for(int i = 0; i < repeatTimes; i++) {
            // Generate two random tuples
            int index1 = new Random().nextInt(sampleNum);
            int index2 = new Random().nextInt(sampleNum);
            while(index2 == index1) {
                index2 = new Random().nextInt(sampleNum);
            }

            List<Double> tuple1 = sample.get(index1);
            List<Double> tuple2 = sample.get(index2);

            // Repeat for every speech
            for(MultiCorrelations correlatedSpeech : speeches) {
                int score1 = 0;
                int score2 = 0;

                // Calculate scores
                for(int j = 0; j < correlatedSpeech.correlatedCols.size(); j++) {
                    List<Integer> pair = correlatedSpeech.correlatedCols.get(j);

                    // If the factor is positive
                    if(correlatedSpeech.correlations.get(j) > 0) {
                        // Only every column is higher can we say it offers a predication
                        boolean oneWin = true;
                        for(int k = 0; k < pair.size(); k++) {
                            if(tuple1.get(pair.get(k)) <= tuple2.get(pair.get(k))) {
                                oneWin = false;
                                break;
                            }
                        }

                        if(oneWin == true) {
                            score1++;
                            continue;
                        }

                        boolean twoWin = true;
                        for(int k = 0; k < pair.size(); k++) {
                            if(tuple1.get(pair.get(k)) >= tuple2.get(pair.get(k))) {
                                twoWin = false;
                                break;
                            }
                        }

                        if(twoWin == true) {
                            score2++;
                        }

                        // If the factor is negative
                    } else {
                        boolean oneWin = true;
                        for(int k = 0; k < pair.size(); k++) {
                            if(tuple1.get(pair.get(k)) <= tuple2.get(pair.get(k))) {
                                oneWin = false;
                                break;
                            }
                        }

                        if(oneWin == true) {
                            score1--;
                            continue;
                        }

                        boolean twoWin = true;
                        for(int k = 0; k < pair.size(); k++) {
                            if(tuple1.get(pair.get(k)) >= tuple2.get(pair.get(k))) {
                                twoWin = false;
                                break;
                            }
                        }

                        if(twoWin == true) {
                            score2--;
                        }
                    }
                }

                // If the prediction is correct, this speech gets one point
                if((score1 > score2 && tuple1.get(targetColumn) > tuple2.get(targetColumn))
                        || (score1 < score2 && tuple1.get(targetColumn) < tuple2.get(targetColumn))) {
                    correlatedSpeech.score++;
                }
            }
        }
    }

    /*
    * Get the optimal speech
    * */
    private void getOptimal() {
        // Get the general and distribution of the target column
        targetColInfo = data.colInfo();

        // Find the speech with maximum score
        int maxScore = 0;
        for(MultiCorrelations correlatedSpeech : speeches) {
            if(correlatedSpeech.score > maxScore) {
                maxScore = correlatedSpeech.score;
                optimalSpeech = correlatedSpeech;
            }
        }

        // Print the information of the optimal speech
        optimalSpeech.printInfo();

        // Generate the output speech
        outputSpeech = optimalSpeech.generateSpeech(data.columnNames, targetColumn);

        System.out.printf("Optimal Speech: %s\n", outputSpeech);
        System.out.printf("General Information: %s", targetColInfo);

        outputSpeech = outputSpeech + targetColInfo;
    }
}
