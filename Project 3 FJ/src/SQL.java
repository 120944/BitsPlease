import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by floris-jan on 14-04-16.
 */
public class SQL {
    public static ResultSet getDBResults(String dataBaseUrl, String user, String password, String query) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(dataBaseUrl, user, password);
            Statement statement =  connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
