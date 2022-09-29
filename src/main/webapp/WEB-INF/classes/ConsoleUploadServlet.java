import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@MultipartConfig
public class ConsoleUploadServlet extends HttpServlet {
    private static Connection con;
    private String currentUser;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        con = getConnection();
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = isLoggedIn(request);
        if (!isLoggedIn) {
            currentUser = "guest";
        } else {
            currentUser = session.getAttribute("USER_ID").toString();
        }
        String loginMsg = "Logged in as: " + currentUser;
        PrintWriter writer = response.getWriter();
        writer.append("<!DOCTYPE html>\r\n")
                .append("<html>\r\n")
                .append("    <head>\r\n")
                .append("        <title>File Upload Form</title>\r\n")
                .append("    </head>\r\n")
                .append("    <body>\r\n");
        writer.append("<div style=\"text-align: right;\">\n")
                .append(loginMsg)
                .append("\n</div>");
        writer.append("<h1>Upload file</h1>\r\n");
        writer.append("<form method=\"POST\" action=\"consoleUpload\" ")
                .append("enctype=\"multipart/form-data\">\r\n");
        writer.append("<input type=\"file\" name=\"fileName\"/><br/><br/>\r\n");
        writer.append("Caption: <input type=\"text\" name=\"caption\"<br/><br/>\r\n");
        writer.append("<br />\n");
        writer.append("Date: <input type=\"date\" name=\"date\"<br/><br/>\r\n");
        writer.append("<br />\n");
        writer.append("<input type=\"submit\" value=\"Submit\"/>\r\n");
        writer.append("</form>\r\n");
        writer.append("<br />\n");
        writer.append("<div>");
        writer.append("<form action='main' method='get'>");
        writer.append("<button class='button' id='main'>Main</button>");
        writer.append("</div>");
        writer.append("</form>");
        writer.append("</body>\r\n").append("</html>\r\n");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        con = getConnection();
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = isLoggedIn(request);
        if (!isLoggedIn) {
            currentUser = "guest";
        } else {
            currentUser = session.getAttribute("USER_ID").toString();
        }

        Path path = Paths.get(System.getProperty("user.home") + "/images/");

        Files.createDirectories(path);

        Part filePart = request.getPart("fileName");
        String captionName = request.getParameter("caption");
        String formDate = request.getParameter("date");
        String fileName = filePart.getSubmittedFileName() + "_" + captionName + "_" + formDate;

        if (formDate.equals("")) formDate = String.valueOf(LocalDate.now());
        if (captionName.equals("")) captionName = "No caption";
        String localPath = System.getProperty("user.home") + "/images/" + fileName;
        filePart.write(localPath);

        PrintWriter out = response.getWriter();
        writeToDatabase(fileName, captionName, formDate, localPath, currentUser);

        ArrayList<String> list = getListing();

        Gson gson = new Gson();
        String fileListString = gson.toJson(list);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.println(fileListString);
        out.flush();
    }

    public void writeToDatabase(String fileName, String captionName, String formDate, String localPath, String currentUser) {
        try {
            con = getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(
                    "INSERT INTO Photos (id, userId, picture, fileName, caption, dateTaken) VALUES (?,?,?,?,?,?)");
            FileInputStream fin = new FileInputStream(localPath);

            preparedStatement.setBytes(1, asBytes(UUID.randomUUID()));
            preparedStatement.setBytes(2, getUuid(currentUser));
            preparedStatement.setBinaryStream(3, fin);
            preparedStatement.setString(4, fileName);
            preparedStatement.setString(5, captionName);
            preparedStatement.setString(6, formDate);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            System.out.println("consoleUpload/writeToDatabase: " + e.getMessage());
        }
    }

    public static byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public byte[] getUuid(String userId) {
        try {
            con = getConnection();
            PreparedStatement s = con.prepareStatement("SELECT * FROM Users WHERE userId = ?;");
            s.setString(1, userId);

            ResultSet rs = s.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return rs.getBytes("id");
        } catch (Exception e) {
            System.out.println("consoleUpload/getUuid: " + e.getMessage());
        }
        return null;
    }

    private ArrayList<String> getListing() {
        ArrayList<String> fileList = new ArrayList<>();

        try {
            con = getConnection();
            PreparedStatement s = con.prepareStatement("SELECT fileName FROM Photos;");
            ResultSet rs = s.executeQuery();
            while(rs.next()){
                if (checkPoster(rs.getString("fileName"))){
                    fileList.add(rs.getString("fileName"));
                }
            }

        } catch (Exception e) {
            System.out.println("consoleUpload/getListing: " + e.getMessage());
        }

        return fileList;
    }

    private boolean checkPoster(String fileName) {
        try {
            con = getConnection();
            PreparedStatement s = con.prepareStatement("SELECT userId FROM Photos WHERE fileName = ?;");
            s.setString(1, fileName);

            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                if (checkUsername(rs.getBytes("userId"))){
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("consoleUpload/checkPoster: " + e.getMessage());
        }
        return false;
    }

    private boolean checkUsername(byte[] uuid) {
        try {
            con = getConnection();
            PreparedStatement s = con.prepareStatement("SELECT userId FROM Users WHERE id = ?;");
            s.setBytes(1, uuid);

            ResultSet rs = s.executeQuery();
            if (!rs.next()) {
                return false;
            }
            String username = rs.getString("userId");
            return username.equals(currentUser);
        } catch (Exception e) {
            System.out.println("consoleUpload/checkUsername: " + e.getMessage());
        }
        return false;
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);

        return session != null && req.isRequestedSessionIdValid();
    }

    public static Connection getConnection() {
        try {
            if (con != null && con.isValid(0)) return con;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SetUp/getCurrentConnection: " + e.getMessage());
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://us-cdbr-east-06.cleardb.net/heroku_a7d042695ca2198", "b62388eed31a05", "866f0c06");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SetUp/getNewConnection: " + e.getMessage());
        }
        return con;
    }
}
