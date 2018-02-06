package mainpakcage;

/** Main class */
public class Main {

    /**
     * Application reads three arguments:
     *     targetCol: the target column
     *     maxCol: the maximum number of correlated columns in a speech
     *     sampleFraction: the fraction of sampling
     *     extraSQL: extra limit of querying
     * The CSV file and the factor matrix have been stored already
     * */
    public static void main(String[] args) {
        // Target column and maximum column number are distinguished as integers
        int targetCol = Integer.valueOf(args[0]);
        int maxCol = Integer.valueOf(args[1]);
        int sampleFraction = Integer.valueOf(args[2]);
        String extraSQL = args[3];
        System.out.printf("Extra query constraint is: \"%s\"\n", extraSQL);

        // Read the file in
        DataAnalysis data = new DataAnalysis();
//        try {
//            data.readFile();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Generate the output speech
        SpeechGenerator generator = new SpeechGenerator(targetCol, data,
                maxCol, sampleFraction, extraSQL);

        DistributionGenerator disGenerator = new DistributionGenerator(data, targetCol, sampleFraction);

        // Read out the result
        Speaker speaker = new Speaker();
        speaker.speakString(generator.readOutResult() + disGenerator.readOutResult());
    }
}