import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@MultipartConfig
public class ConsoleUploadServlet extends HttpServlet {
    private Connection con = SetUp.getConnection();
    private String currentUser;
    private Gson gson;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
        writeToDatabase(out, fileName, captionName, formDate, localPath, currentUser);

        ArrayList<String> list = getListing();

        gson = new Gson();
        String fileListString = this.gson.toJson(list);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.println(fileListString);
        out.flush();
    }

    public void writeToDatabase(PrintWriter out, String fileName, String captionName, String formDate, String localPath, String currentUser) {
        try {
            con = SetUp.getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(
                    "INSERT INTO Photos (id, userId, picture, fileName, caption, dateTaken) VALUES (?,?,?,?,?,?)");
            FileInputStream fin = new FileInputStream(localPath);

            preparedStatement.setBytes(1, UuidGenerator.asBytes(UUID.randomUUID()));
            preparedStatement.setBytes(2, getUuid(currentUser));
            preparedStatement.setBinaryStream(3, fin);
            preparedStatement.setString(4, fileName);
            preparedStatement.setString(5, captionName);
            preparedStatement.setString(6, formDate);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            out.println("Upload completed!");
        } catch (Exception e) {
            System.out.println("consoleUpload/writeToDatabase: " + e.getMessage());
        }
    }

    public byte[] getUuid(String userId) {
        try {
            con = SetUp.getConnection();
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
            con = SetUp.getConnection();
            PreparedStatement s = con.prepareStatement("SELECT fileName FROM Photos;");
            ResultSet rs = s.executeQuery();

//            StringBuilder dirList = new StringBuilder("Files you have posted:");
//            while (rs.next()) {
//                if (checkPoster(rs.getString("fileName"))){
//                    dirList.append("<li>").append(rs.getString("fileName")).append("</li>");
//                }
//            }
//
//            return dirList.toString();

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
            con = SetUp.getConnection();
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
            con = SetUp.getConnection();
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
}
