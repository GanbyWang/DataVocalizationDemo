package mainpakcage;

import com.opencsv.CSVReader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileReader;

/** Used to store all the information about the CSV data*/
public class DataAnalysis {

    // The fraction of sampling
    private double sampleFraction;
    // Accessor to the database
    private DbAccessor dbAccessor;
    // The constraint of sampling
    private String extraSQL;
    // The target column
    private int tarCol;
    // General information of the sample
    public double sampleMean;
    public double sampleVar;
    public double sampleMax;
    public double sampleMin;
    // Extracted list of the target column
    public List<Double> singleColList;

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

    /** The sample */
    public List<List<Double>> sample = new ArrayList<List<Double>>();

    /**
     * Constructor function
     * Accepts 3 arguments: sampling fraction, sampling constraint, target column
     * */
    public DataAnalysis(int sampleFraction, String extraSQL, int tarCol) {
        this.sampleFraction = sampleFraction;
        this.extraSQL = extraSQL;
        this.tarCol = tarCol;

        // Get the sample using SQL query
        dbAccessor = new DbAccessor();
        sample = dbAccessor.getSample(this.sampleFraction, this.extraSQL);
        dbAccessor.disconnected();

        // Calculate the general information of the sample
        getBasicInfo();
    }

    /* Function to calculate all general information of the target column */
    private void getBasicInfo() {
        singleColList = new ArrayList<Double>();
        singleColList.addAll(getSingleCol(sample, tarCol));

        double mean = getMean(singleColList);
        double var = getVariance(singleColList, mean);

//        System.out.println(mean);
//        System.out.println(var);

        sampleMean = round(mean);
        sampleVar = round(var);
        sampleMax = getMax(singleColList);
        sampleMin = getMin(singleColList);
    }

    /*
     * Calculate the mean of the given array
     * One parameter: the array
     * */
    private double getMean(List<Double> list) {
        double sum = 0;
        for(Double each : list) {
            sum += each;
        }
        return sum / (double) list.size();
    }

    /*
     * Calculate the variance of the given array
     * Two parameter: the array and the mean of the array
     * */
    private double getVariance(List<Double> list, double mean) {
        double sum = 0;
        for(Double each : list) {
            sum += Math.pow(each - mean, 2);
        }
        return sum / (double) list.size();
    }

    /*
    * Helper function to round a double number
    * The result only have 2 effective bits
    * */
    private double round(double num) {

        double cp1 = num;
        double cp2 = num;

        int posPower = 0;
        int negPower = 0;

        if(num > 1) {
            while (cp1 > 10) {
                posPower++;
                cp1 /= 10;
            }

            BigDecimal bd = new BigDecimal(cp1);
            BigDecimal tmpBD = new BigDecimal(bd.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue()
                                                * Math.pow(10, posPower));
            return tmpBD.setScale(0, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        } else {
            while(cp2 < 1) {
                negPower++;
                cp2 *= 10;
            }

            BigDecimal bd = new BigDecimal(cp2);
            return bd.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue() / Math.pow(10, negPower);
        }
    }

    /* Get the maximum of the given array */
    private Double getMax(List<Double> list) {
        double max = 0;
        for(Double element : list) {
            if(element > max) {
                max = element;
            }
        }

        return max;
    }

    /* Get the minimum of the given array */
    private Double getMin(List<Double> list) {
        double min = Double.MAX_VALUE;
        for(Double element : list) {
            if(element < min) {
                min = element;
            }
        }

        return min;
    }

    /* Extract every target column field from each tuple */
    private List<Double> getSingleCol(List<List<Double>> lists, int colNum) {
        List<Double> result = new ArrayList<Double>();

        for(List<Double> singleTuple : lists) {
            result.add(singleTuple.get(colNum));
        }

        return result;
    }

    /**
     * Helper function to print all general information of the target column
     * */
    public String colInfo() {
        String columnInfo = "";

        columnInfo += "The average of column " + columnNames[tarCol] + " is " + sampleMean + ", ";
        columnInfo += "the variance is " + sampleVar + ", ";

        return columnInfo;
    }

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