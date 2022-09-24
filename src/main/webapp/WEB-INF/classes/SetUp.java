import java.sql.*;

public class SetUp {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("SetUp/Message: " + ex.getMessage());
        }
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306", "comp3940", "");

            Statement s = con.createStatement();
            s.executeUpdate("CREATE SCHEMA IF NOT EXISTS comp3940;");
            s.executeUpdate("USE comp3940;");
            s.executeUpdate("DROP TABLE IF EXISTS Photos;");
            s.executeUpdate("DROP TABLE IF EXISTS Users;");
            s.executeUpdate("CREATE TABLE Users (" +
                    "id VARBINARY(16)," +
                    "userId VARCHAR(50) UNIQUE," +
                    "password VARCHAR(255)," +
                    "PRIMARY KEY (id));");
            s.executeUpdate("CREATE TABLE Photos(" +
                    "id VARBINARY(16)," +
                    "userId VARBINARY(16)," +
                    "picture MEDIUMBLOB," +
                    "fileName VARCHAR(50)," +
                    "caption CHAR(100)," +
                    "dateTaken DATE," +
                    "PRIMARY KEY (id)," +
                    "FOREIGN KEY (userid) REFERENCES Users(id));");
            s.close();
        } catch (Exception e) {
            System.out.println("SetUp/Message: " + e.getMessage());
        }
    }

    public static void setUpTable() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("SetUp/Message: " + ex.getMessage());
        }
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306", "comp3940", "");

            Statement s = con.createStatement();
            s.executeUpdate("CREATE SCHEMA IF NOT EXISTS comp3940;");
            s.executeUpdate("USE comp3940;");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS Users (" +
                    "id VARBINARY(16)," +
                    "userId VARCHAR(50) UNIQUE," +
                    "password VARCHAR(255)," +
                    "PRIMARY KEY (id));");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS Photos(" +
                    "id VARBINARY(16)," +
                    "userId VARBINARY(16)," +
                    "picture MEDIUMBLOB," +
                    "fileName VARCHAR(50)," +
                    "caption CHAR(100)," +
                    "dateTaken DATE," +
                    "PRIMARY KEY (id)," +
                    "FOREIGN KEY (userid) REFERENCES Users(id));");
            s.close();
        } catch (Exception e) {
            System.out.println("SetUp/Message: " + e.getMessage());
        }
    }
}
