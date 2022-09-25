import java.sql.*;

public class SetUp {
    private static Connection con = null;
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("SetUp/Message: " + ex.getMessage());
        }
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://us-cdbr-east-06.cleardb.net/heroku_a7d042695ca2198", "b62388eed31a05", "866f0c06");

            Statement s = con.createStatement();
            s.executeUpdate("DROP TABLE IF EXISTS Photos;");
            s.executeUpdate("DROP TABLE IF EXISTS Users;");
            s.executeUpdate("CREATE TABLE Users (" +
                    "id VARBINARY(16)," +
                    "userId VARCHAR(50) UNIQUE," +
                    "password VARBINARY(64)," +
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
            Connection con = DriverManager.getConnection("jdbc:mysql://us-cdbr-east-06.cleardb.net/heroku_a7d042695ca2198", "b62388eed31a05", "866f0c06");

            Statement s = con.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS Users (" +
                    "id VARBINARY(16)," +
                    "userId VARCHAR(50) UNIQUE," +
                    "password VARBINARY(64)," +
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
            con.close();
        } catch (Exception e) {
            System.out.println("SetUp/Message: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        if (con != null) return con;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("SetUp/Message: " + ex.getMessage());
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://us-cdbr-east-06.cleardb.net/heroku_a7d042695ca2198", "b62388eed31a05", "866f0c06");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SetUp/getConnection: " + e.getMessage());
        }
        return con;
    }
}
