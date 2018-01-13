package MainPackage;

import java.util.List;

/**
 * Created by wangyicheng on 11/01/2018.
 * This class is used to store basic information of a speech
 */
public class Speech {
    // Which columns this speech is talking about
    public List<Integer> columns;
    // Score is used to compare which speech is better
    public int score;

    /**
     * Constructor function
     * Have one parameter to illustrate which columns are in this speech
     * */
    public Speech(List<Integer> columns) {
        this.columns = columns;
        score = 0;
    }

    /**
     * Helper function to print information
     * */
    public void printInfo() {
        System.out.printf("Selected Columns: ");
        for(Integer col:columns)
            System.out.printf("%d ", col);
        System.out.printf("\nScore: %d\n", score);
    }
}