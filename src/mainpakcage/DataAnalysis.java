package mainpakcage;

import com.opencsv.CSVReader;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileReader;

/** Used to store all the information about the CSV data*/
public class DataAnalysis {

    /** Used to store every tuple of the data */
    public class tuple {
        // Only store numerical columns
        public double columns[] = new double[8];

        /**
         * Helper function
         * Used to print all fields
         * */
        public void printInfo() {
            System.out.printf("Satisfaction level: %f\n"
                            + "Last evaluation: %f\n"
                            + "Number of project: %f\n"
                            + "Average monthly hours: %f\n"
                            + "Time spend in company: %f\n"
                            + "Work accident: %f\n"
                            + "Left: %f\n"
                            + "Promotion in last 5 years: %f\n",
                    columns[0], columns[1], columns[2], columns[3],
                    columns[4], columns[5], columns[6], columns[7]);
        }
    }

    /**
     * Factor matrix of the data
     * The matrix is get from the Kaggle
     * The link is https://www.kaggle.io/svf/441884/647b8c07ae7a081c547af6d9324351c1/__results__.html#
     * The matrix is 8*8
     * Every column stands for:
     *   "satisfaction_level", "last_evaluation", "number_project", "average_monthly_hours",
     *   "time_spend_company", "Work_accident", "left", "promotion_last_5years"
     * in order.
     * */
    public double[][] factorMatrix = {
            {1.00000000, 0.105021214, -0.142969586, -0.020048113, -0.100866073, 0.058697241, -0.38837498, 0.025605186},
            {0.10502121, 1.000000000, 0.349332589, 0.339741800, 0.131590722, -0.007104289, 0.00656712, -0.008683768},
            {-0.14296959, 0.349332589, 1.000000000, 0.417210634, 0.196785891, -0.004740548, 0.02378719, -0.006063958},
            {-0.02004811, 0.339741800, 0.417210634, 1.000000000, 0.127754910, -0.010142888, 0.07128718, -0.003544414},
            {-0.10086607, 0.131590722, 0.196785891, 0.127754910, 1.000000000, 0.002120418, 0.14482217, 0.067432925},
            {0.05869724, -0.007104289, -0.004740548, -0.010142888, 0.002120418, 1.000000000, -0.15462163, 0.039245435},
            {-0.38837498, 0.006567120, 0.023787185, 0.071287179, 0.144822175, -0.154621634, 1.00000000, -0.061788107},
            {0.02560519, -0.008683768, -0.006063958, -0.003544414, 0.067432925, 0.039245435, -0.06178811, 1.000000000}
    };

    /**
    * The column names of the data
    * Change the names a little to make them more natural to read out
    * */
    public String[] columnNames = {
            "satisfaction level", "last evaluation", "the number of projects", "average of monthly hours",
            "time spent in company", "work accidents", "left", "promotion in last 5 years"
    };

    /** The table column names of the data in the database */
    public String[] tableNames = {
            "satisfaction_level", "last_evaluation", "number_project", "average_montly_hours", "time_spend_company",
            "work_accident", "if_left", "promotion_last_5years", "sales", "salary", "id"
    };

    /** Used to store the file */
    public List<tuple> originData = new ArrayList<tuple>();

    /** This function reads in the CSV file and store all data into the array */
    public void readFile() throws Exception {

        // Read in the file
        // The file is put in the res documentary
        File file = new File("src/res/HR_comma_sep.csv");
        FileReader fReader = new FileReader(file);
        CSVReader csvReader = new CSVReader(fReader);

        // Store the column names first
        String[] colNames = csvReader.readNext();

        // allData stores all information
        List<String[]> allData = csvReader.readAll();
        for(String[] singleData : allData) {
            tuple tmp = new tuple();

            for(int i = 0; i < 8; i++) {
                tmp.columns[i] = Double.valueOf(singleData[i]);
            }

            // Add to the array
            originData.add(tmp);
        }
        csvReader.close();
    }
}