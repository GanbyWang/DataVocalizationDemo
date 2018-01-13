package MainPackage;

import java.util.*;

import javax.speech.Central;
import javax.speech.EngineList;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

/**
 * Created by wangyicheng on 11/01/2018.
 * This class is used to generate the optimal speech
 */
public class SpeechGenerator {
    // Store the target column
    private int targetColumn;
    // An array of all possible speeches
    private List<Speech> speeches;
    // The data to analyze
    DataAnalysis data;
    // The result
    private Speech optimalSpeech;
    private String outputSpeech;

    /**
     * Constructor function
     * Two parameters: target column and the original data
     * */
    public SpeechGenerator(int targetColumn, DataAnalysis data) {
        this.targetColumn = targetColumn;
        this.data = data;

        // Generate all possible speeches
        generateAllSpeeches();

        // Repeat enough times to find the optimal speech
        compete(10000);

        // Get the optimal speech
        getOptimal();
    }

    /**
     * Function to generate all speeches
     * */
    private void generateAllSpeeches() {
        speeches = new ArrayList<Speech>();

        /*
        * Since the limit of characters is 300
        * We only generate speeches with no more than 5 columns
        * */
        for(int i = 0; i < 8; i++) {
            if(i == targetColumn)
                continue;

            ArrayList<Integer> columns = new ArrayList<Integer>();
            columns.add(i);
            speeches.add(new Speech(columns));

            for(int j = i + 1; j < 8; j++) {
                if (j == targetColumn)
                    continue;

                columns = new ArrayList<Integer>();
                columns.add(i);
                columns.add(j);
                speeches.add(new Speech(columns));

                for (int k = j + 1; k < 8; k++) {
                    if(k == targetColumn)
                        continue;

                    columns = new ArrayList<Integer>();
                    columns.add(i);
                    columns.add(j);
                    columns.add(k);
                    speeches.add(new Speech(columns));

                    for (int l = k + 1; l < 8; l++) {
                        if(l == targetColumn)
                            continue;

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

    /**
     * Function to distinguish speeches
     * Have one parameter to define the repeat time
     * */
    private void compete(int repeatTimes) {

        System.out.printf("Target Column: %d\n", targetColumn);
        System.out.printf("Repeated Times: %d\n", repeatTimes);

        for(int i = 0; i < repeatTimes; i++) {
            // Generate two random tuples
            int index1 = new Random().nextInt(data.originData.size());
            int index2 = new Random().nextInt(data.originData.size());
            while(index2 == index1)
                index2 = new Random().nextInt(data.originData.size());

            DataAnalysis.tuple tuple1 = data.originData.get(index1);
            DataAnalysis.tuple tuple2 = data.originData.get(index2);

            // Repeat for each single speech
            for(Speech singleSpeech:speeches) {
                // Initialize scores of tuples
                int score1 = 0;
                int score2 = 0;

                // Calculate scores
                for(Integer col:singleSpeech.columns) {
                    if(data.factorMatrix[targetColumn][col] > 0) {
                        if(tuple1.columns[col] > tuple2.columns[col])
                            score1++;
                        else if(tuple1.columns[col] < tuple2.columns[col])
                            score2++;
                    } else {
                        if(tuple1.columns[col] > tuple2.columns[col])
                            score1--;
                        else if(tuple1.columns[col] < tuple2.columns[col])
                            score2--;
                    }
                }

                // If the prediction is correct, this speech gets one point
                if((score1 > score2 && tuple1.columns[targetColumn] > tuple2.columns[targetColumn]) ||
                        (score1 < score2 && tuple1.columns[targetColumn] < tuple2.columns[targetColumn]))
                    singleSpeech.score++;
            }
        }
    }

    /**
     * Function to get the optimal speech
     * */
    private void getOptimal() {
        // Sort the speech
        Collections.sort(speeches, new Comparator<Speech>() {
            @Override
            public int compare(Speech o1, Speech o2) {
                if(o1.score < o2.score)
                    return -1;
                else if(o1.score == o2.score)
                    return 0;
                else
                    return 0;
            }
        });

        // Get the speech with the highest score
        optimalSpeech = speeches.get(speeches.size() - 1);

        optimalSpeech.printInfo();

        // Change the result into a string
        outputSpeech = "";

        for(int col:optimalSpeech.columns) {
            outputSpeech += "Column ";
            outputSpeech += data.columnNames[col];
            outputSpeech += " is ";

            if(data.factorMatrix[targetColumn][col] < 0)
                outputSpeech += "negatively ";
            else
                outputSpeech += "positively ";

            outputSpeech += "correlated with column ";
            outputSpeech += data.columnNames[targetColumn];
            outputSpeech += ". ";
        }

        System.out.printf("Optimal Speech: %s\n", outputSpeech);
    }

    /**
     * Function to read out the optimal speech
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
            if(voices != null && voices.length > 0){
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