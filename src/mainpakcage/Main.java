package mainpakcage;

import java.sql.Connection;
import java.sql.DriverManager;

/** Main class */
public class Main {

    /**
     * Application reads three arguments:
     *     targetCol: the target column
     *     maxCol: the maximum number of correlated columns in a speech
     *     repeatTimes: the time of competing
     * The CSV file and the factor matrix have been stored already
     * */
    public static void main(String[] args) {
        // Target column and maximum column number are distinguished as integers
        int targetCol = Integer.valueOf(args[0]);
        int maxCol = Integer.valueOf(args[1]);
        int repeatTimes = Integer.valueOf(args[2]);

        // Read the file in
        DataAnalysis data = new DataAnalysis();
//        try {
//            data.readFile();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Generate the output speech
        SpeechGenerator generator = new SpeechGenerator(targetCol, data, maxCol, repeatTimes);

        // Read out the result
        generator.readOutResult();
    }
}