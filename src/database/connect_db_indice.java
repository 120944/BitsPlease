package database;

import java.sql.*;

/**
 * Created by Michael on 6-4-2016.
 */

public class connect_db_indice {
    public static void main(String[] args) {
        try {
            // verbinden met database
            // Parameters zijn (db_url, user, password)
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/indice", "root", "");

            // statement maken
            Statement myStmt = myConn.createStatement();

            // SQL query uitvoeren & informatie ophalen
            ResultSet myRs = myStmt.executeQuery("select * from veiligheidsindice");

            // data verwerken
            while (myRs.next()) {
                System.out.println(myRs.getString("Gebied") + ", " + myRs.getString("Jaar") + ", " + myRs.getString("Cijfer"));
            }
        }
        catch (Exception exc) {
            // error? throwback hier
            exc.printStackTrace();
        }
    }
}
