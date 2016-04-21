import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQL {
    //Gets the SQL-data

    /**
     *
     * @param dataBaseUrl
     * @param user
     * @param password
     * @param query
     * @return
     */
    public static ResultSet getDBResults(String dataBaseUrl, String user, String password, String query) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(dataBaseUrl, user, password);
            Statement statement =  connection.createStatement();

            /* Following part was redundant, storing the resultSet locally before immediately returning out of the function
             * ResultSet resultSet = statement.executeQuery(query);
             * return resultSet;
             */
            return statement.executeQuery(query);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet getChartData(ChartInfo chartInfo) {
        return SQL.getDBResults(chartInfo.getDbURL(),chartInfo.getDbUsername(),chartInfo.getDbPassword(),chartInfo.getDbQuery());
    }
}
