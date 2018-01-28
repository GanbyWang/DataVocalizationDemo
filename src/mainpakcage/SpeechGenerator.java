package mainpakcage;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

/** Used to generate the optimal speech */
public class SpeechGenerator {
    // Store the target column
    private int targetColumn;
    // An array of all possible speeches
    private List<Speech> speeches;
    // An array of all possible speeches with relations
    private List<SpeechRelation> relatedSpeeches;
    // The data to analyze
    DataAnalysis data;
    // The results
    private Speech optimalSpeech;
    private SpeechRelation optimalRelatedSpeech;
    private String outputSpeech;

    /**
     * Constructor function
     * Two parameters: target column and the original data
     * */
    public SpeechGenerator(int targetColumn, DataAnalysis data, int maxCol, int repeatTimes) {
        this.targetColumn = targetColumn;
        this.data = data;

        // Generate all possible speeches
//        generateAllSpeeches();
//        printAllSpeeches();
        System.out.printf("Maximum columns: %d\n", maxCol);
        generateAllWithoutFactor(maxCol);
//        printAllRelatedSpeeches();

        // Repeat enough times to find the optimal speech
//         compete(repeatTimes);
        competeWithDB(repeatTimes);

        // Get the optimal speech
//         getOptimal();
        getRelatedOptimal();
    }

    /** Generate all speeches */
    private void generateAllSpeeches() {
        speeches = new ArrayList<Speech>();

        /*
        * Since the limit of characters is 300
        * We only generate speeches with no more than 5 columns
        * */
        for(int i = 0; i < 8; i++) {
            if(i == targetColumn) {
                continue;
            }

            ArrayList<Integer> columns = new ArrayList<Integer>();
            columns.add(i);
            speeches.add(new Speech(columns));

            for(int j = i + 1; j < 8; j++) {
                if (j == targetColumn) {
                    continue;
                }

                columns = new ArrayList<Integer>();
                columns.add(i);
                columns.add(j);
                speeches.add(new Speech(columns));

                for (int k = j + 1; k < 8; k++) {
                    if(k == targetColumn) {
                        continue;
                    }

                    columns = new ArrayList<Integer>();
                    columns.add(i);
                    columns.add(j);
                    columns.add(k);
                    speeches.add(new Speech(columns));

                    for (int l = k + 1; l < 8; l++) {
                        if(l == targetColumn) {
                            continue;
                        }

                        columns = new ArrayList<Integer>();
                        columns.add(i);
                        columns.add(j);
                        columns.add(k);
                        columns.add(l);
                        speeches.add(new Speech(columns));
                    }
                }
            }
        }
    }

    /** Generate all speeches without factor matrix */
    private void generateAllWithoutFactor(int maxCol) {
        // Limit the range of maximum columns
        if(maxCol >= 8) {
            maxCol = 7;
        }

        speeches = new ArrayList<Speech>();
        generateByPermutation(0, new ArrayList<Integer>(), maxCol);

        relatedSpeeches = new ArrayList<SpeechRelation>();
        for(Speech speech : speeches) {
            permutateRelations(speech, 0, new ArrayList<Integer>());
        }
    }

    /* DFS to generate all speeches */
    private void generateByPermutation(int curCol, List<Integer> columns, int maxCol) {
        // Maintain a local copy
        List<Integer> tmpCol = new ArrayList<Integer>();
        tmpCol.addAll(columns);

        if(curCol == 7) {
            if(tmpCol.size() > 0) {
                speeches.add(new Speech(tmpCol));
            }
            // The column involved cannot be the target column
            if(tmpCol.size() < maxCol && targetColumn != curCol) {
                tmpCol.add(7);
                speeches.add(new Speech(tmpCol));
            }
            return;
        }

        generateByPermutation(curCol + 1, tmpCol, maxCol);

        if(tmpCol.size() < maxCol && targetColumn != curCol) {
            tmpCol.add(curCol);
            generateByPermutation(curCol + 1, tmpCol, maxCol);
        }
    }

    /* DFS to generate all speeches with relations */
    private void permutateRelations(Speech speech, int curLevel, List<Integer> relations) {
        // Maintain local copies
        List<Integer> tmpRelations1 = new ArrayList<Integer>();
        tmpRelations1.addAll(relations);
        List<Integer> tmpRelations2 = new ArrayList<Integer>();
        tmpRelations2.addAll(relations);

        if(curLevel == speech.columns.size() - 1) {
            tmpRelations1.add(1);
            relatedSpeeches.add(new SpeechRelation(speech, tmpRelations1));
            tmpRelations2.add(-1);
            relatedSpeeches.add(new SpeechRelation(speech, tmpRelations2));
            return;
        }

        tmpRelations1.add(1);
        permutateRelations(speech, curLevel + 1, tmpRelations1);
        tmpRelations2.add(-1);
        permutateRelations(speech, curLevel + 1,tmpRelations2);
    }

    /* Print the information of all speeches */
    private void printAllSpeeches() {
        System.out.printf("There're %d speeches in total.\n", speeches.size());

        for(Speech singleSpeech : speeches) {
            singleSpeech.printInfo();
        }
    }

    /* Print the information of all speeches with factors */
    private void printAllRelatedSpeeches() {
        System.out.printf("There're %d related speeches in total.\n", relatedSpeeches.size());

        for(SpeechRelation relation : relatedSpeeches) {
            relation.printInfo();
        }
    }

    /**
     * Used to distinguish speeches
     * One parameter: the repeat time
     * */
    private void compete(int repeatTimes) {

        System.out.printf("Target Column: %d\n", targetColumn);
        System.out.printf("Repeated Times: %d\n", repeatTimes);

        for(int i = 0; i < repeatTimes; i++) {
            // Generate two random tuples
            int index1 = new Random().nextInt(data.originData.size());
            int index2 = new Random().nextInt(data.originData.size());
            while(index2 == index1) {
                index2 = new Random().nextInt(data.originData.size());
            }

            DataAnalysis.tuple tuple1 = data.originData.get(index1);
            DataAnalysis.tuple tuple2 = data.originData.get(index2);

            // Repeat for each single speech
            for(Speech singleSpeech : speeches) {
                // Initialize scores of tuples
                int score1 = 0;
                int score2 = 0;

                // Calculate scores
                for(Integer col : singleSpeech.columns) {
                    if(data.factorMatrix[targetColumn][col] > 0) {
                        if(tuple1.columns[col] > tuple2.columns[col]) {
                            score1++;
                        } else if(tuple1.columns[col] < tuple2.columns[col]) {
                            score2++;
                        }
                    } else {
                        if(tuple1.columns[col] > tuple2.columns[col]) {
                            score1--;
                        } else if(tuple1.columns[col] < tuple2.columns[col]) {
                            score2--;
                        }
                    }
                }

                // If the prediction is correct, this speech gets one point
                if((score1 > score2 && tuple1.columns[targetColumn] > tuple2.columns[targetColumn])
                        || (score1 < score2 && tuple1.columns[targetColumn] < tuple2.columns[targetColumn])) {
                    singleSpeech.score++;
                }
            }
        }
    }

    /**
     * Used to distinguish speeches with factors
     * One parameter: the repeat time
     * */
    private void competeWithDB(int repeatTimes) {
        System.out.printf("Target Column: %d\n", targetColumn);
        System.out.printf("Repeated Times: %d\n", repeatTimes);

        // Create a new database accessor
        DbAccessor accessor = new DbAccessor();
        int tupleNum = accessor.getTupleNum();

        for(int i = 0; i < repeatTimes; i++) {
            // Generate two random tuples
            int index1 = new Random().nextInt(tupleNum);
            int index2 = new Random().nextInt(tupleNum);
            while(index2 == index1) {
                index2 = new Random().nextInt(tupleNum);
            }

            List<Double> tuple1 = accessor.getTupleById(index1 + 1);
            List<Double> tuple2 = accessor.getTupleById(index2 + 1);

            // Repeat for every speech
            for(SpeechRelation relatedSpeech : relatedSpeeches) {
                int score1 = 0;
                int score2 = 0;

                // Calculate scores
                for(int j = 0; j < relatedSpeech.relations.size(); j++) {
                    int col = relatedSpeech.speech.columns.get(j);

                    // If the factor is positive
                    if(relatedSpeech.relations.get(j) > 0) {
                        if(tuple1.get(col) > tuple2.get(col)) {
                            score1++;
                        } else if(tuple1.get(col) < tuple2.get(col)) {
                            score2++;
                        }
                    // If the factor is negative
                    } else {
                        if(tuple1.get(col) > tuple2.get(col)) {
                            score1--;
                        } else if(tuple1.get(col) < tuple2.get(col)) {
                            score2--;
                        }
                    }
                }

                // If the prediction is correct, this speech gets one point
                if((score1 > score2 && tuple1.get(targetColumn) > tuple2.get(targetColumn))
                        || (score1 < score2 && tuple1.get(targetColumn) < tuple2.get(targetColumn))) {
                    relatedSpeech.speech.score++;
                }
            }
        }
    }

    /** Get the optimal speech */
    private void getOptimal() {
        // Sort the speech
        Collections.sort(speeches, new Comparator<Speech>() {
            @Override
            public int compare(Speech o1, Speech o2) {
                if(o1.score < o2.score) {
                    return -1;
                } else if(o1.score == o2.score) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        // Get the speech with the highest score
        optimalSpeech = speeches.get(speeches.size() - 1);

        optimalSpeech.printInfo();

        // Change the result into a string
        outputSpeech = "";

        for(int col : optimalSpeech.columns) {
            outputSpeech += "Column ";
            outputSpeech += data.columnNames[col];
            outputSpeech += " is ";

            if(data.factorMatrix[targetColumn][col] < 0) {
                outputSpeech += "negatively ";
            } else {
                outputSpeech += "positively ";
            }

            outputSpeech += "correlated with column ";
            outputSpeech += data.columnNames[targetColumn];
            outputSpeech += ". ";
        }

        System.out.printf("Optimal Speech: %s\n", outputSpeech);
    }

    /** Get the optimal speech with factors */
    public void getRelatedOptimal() {
        // Sort the speech
        Collections.sort(relatedSpeeches, new Comparator<SpeechRelation>() {
            @Override
            public int compare(SpeechRelation o1, SpeechRelation o2) {
                if(o1.speech.score < o2.speech.score) {
                    return -1;
                } else if(o1.speech.score == o2.speech.score) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        // Get the speech with the highest score
        optimalRelatedSpeech = relatedSpeeches.get(relatedSpeeches.size() - 1);

        optimalRelatedSpeech.printInfo();

        // Change the result into a string
        outputSpeech = "";

        for(int i = 0; i < optimalRelatedSpeech.relations.size(); i++) {
            int col = optimalRelatedSpeech.speech.columns.get(i);

            outputSpeech += "Column ";
            outputSpeech += data.columnNames[col];
            outputSpeech += " is ";

            if(optimalRelatedSpeech.relations.get(i) < 0) {
                outputSpeech += "negatively ";
            } else {
                outputSpeech += "positively ";
            }

            outputSpeech += "correlated with column ";
            outputSpeech += data.columnNames[targetColumn];
            outputSpeech += ". ";
        }

        System.out.printf("Optimal Speech: %s\n", outputSpeech);
    }

    /**
     * Read out the optimal speech
     * Copied from the web
     * */
    public void readOutResult() {
        try {
            SynthesizerModeDesc desc = new SynthesizerModeDesc("FreeTTS en_US general synthesizer", "general",
                    Locale.US, null, null);
            Synthesizer synthesizer = Central.createSynthesizer(desc);
            if (synthesizer == null) {
                System.exit(1);
            }
            synthesizer.allocate();
            synthesizer.resume();
            desc = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
            Voice voices[] = desc.getVoices();
            if(voices != null && voices.length > 0) {
                synthesizer.getSynthesizerProperties().setVoice(voices[0]);
                // Read out the speech as an argument
                synthesizer.speakPlainText(outputSpeech, null);
                synthesizer.waitEngineState(0x10000L);
            }
            synthesizer.deallocate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}