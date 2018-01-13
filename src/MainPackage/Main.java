package MainPackage;

/**
 * Created by wangyicheng on 10/01/2018.
 */
public class Main {
    /**
     * Application reads in an argument as the target column
     * The CSV file and the factor matrix have been stored already
     * */
    public static void main(String[] args) {
        // Target column is distinguished as integers
        int targetCol = Integer.valueOf(args[0]);

        // Read the file in
        DataAnalysis data = new DataAnalysis();
        try {
            data.readFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Generate the output speech
        SpeechGenerator generator = new SpeechGenerator(targetCol, data);

        generator.readOutResult();
    }
}