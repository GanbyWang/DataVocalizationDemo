package mainpakcage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** Used to access database */
public class DbAccessor {

    /** Connect to the table */
    public Connection getConnected() {
        // Relation information
        String driver = "org.postgresql.Driver";
        String url = "jdbc:postgresql://localhost:5432/projectdb";
        String username = "alex";
        String password = "PakChoi3421";
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


    /**
     * Extract a single tuple specified by a SQL command
     * Return a double array
     * */
    public List<Double> extractSingleTuple(String SQL) {
        List<Double> tuple = new ArrayList<Double>();

        // Connect
        Connection connection = getConnected();

        PreparedStatement preparedStmt;
        try {
            preparedStmt = (PreparedStatement)connection.prepareStatement(SQL);
            ResultSet resultSet = preparedStmt.executeQuery();

            resultSet.next();

            for(int i = 1; i <= 8; i++) {
                tuple.add(resultSet.getDouble(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Close the connection after getting the result
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tuple;
    }

    /**
     * Extract a single tuple specified by its id
     * Return a double array
     * */
    public List<Double> getTupleById(int id) {
        String SQL = "SELECT * FROM hr_comma_sep WHERE id=" + id + ";";

        return extractSingleTuple(SQL);
    }

    /**
     * Get the number of tuples of the table
     * Use a SQL command to realize
     * */
    public int getTupleNum() {
        int tupleNum = -1;
        Connection connection = getConnected();

        // The SQL to query
        String SQL = "SELECT COUNT(*) FROM hr_comma_sep;";

        PreparedStatement preparedStmt;
        try {
            preparedStmt = (PreparedStatement)connection.prepareStatement(SQL);
            ResultSet resultSet = preparedStmt.executeQuery();

            resultSet.next();
            tupleNum = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tupleNum;
    }
}