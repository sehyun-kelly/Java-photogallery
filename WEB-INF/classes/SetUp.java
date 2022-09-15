import java.sql.*;

public class SetUp {
    public static void main(String[] args) {
        Connection con;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("Message: " + ex.getMessage());
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306", "comp3940", "");

            Statement s = con.createStatement();
            s.executeUpdate("create schema if not exists comp3940;");
            s.executeUpdate("use comp3940;");
            s.executeUpdate("drop table if exists Photos;");
            s.executeUpdate("drop table if exists Users;");
            s.executeUpdate("create table Users (" +
                    "id VARBINARY(16)," +
                    "userId varchar(50) unique," +
                    "password varchar(50)," +
                    "PRIMARY KEY (id));");
            s.executeUpdate("create table Photos(" +
                    "id VARBINARY(16)," +
                    "userId VARBINARY(16)," +
                    "picture MEDIUMBLOB," +
                    "fileName varchar(50)," +
                    "caption char(100)," +
                    "dateTaken date," +
                    "PRIMARY KEY (id)," +
                    "FOREIGN KEY (userid) REFERENCES Users(id));");
            s.close();
        } catch (Exception e) {
            System.out.println("Message: " + e.getMessage());
        }
    }
}
