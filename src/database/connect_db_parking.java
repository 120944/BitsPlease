package database;

import java.sql.*;

/**
 * Created by Michael on 7-4-2016.
 */

public class connect_db_parking {
    public static void main(String[] args) {
        try {
            // verbinden met database
            // Parameters zijn (db_url, user, password)
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/parkeergarages", "root", "");

            // statement maken
            Statement myStmt = myConn.createStatement();

            // SQL query uitvoeren & informatie ophalen
            ResultSet myRs = myStmt.executeQuery("select * from positie");

            // data verwerken
            System.out.println("Format: Naam, Latitude, Longitude");
            while (myRs.next()) {
                System.out.println(myRs.getString("Naam") + ", " + myRs.getString("Latitude") + ", " + myRs.getString("Longitude"));
            }
        }
        catch (Exception exc) {
            // error? throwback hier
            exc.printStackTrace();
        }
    }
}