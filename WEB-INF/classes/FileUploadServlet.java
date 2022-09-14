import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;
import java.sql.*;
import java.util.*;

@MultipartConfig
public class FileUploadServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = isLoggedIn(request);
        if (!isLoggedIn) {
            response.setStatus(302);
            response.sendRedirect("login");

        } else {
            PrintWriter writer = response.getWriter();
            writer.append("<!DOCTYPE html>\r\n")
                    .append("<html>\r\n")
                    .append("    <head>\r\n")
                    .append("        <title>File Upload Form</title>\r\n")
                    .append("    </head>\r\n")
                    .append("    <body>\r\n");
            writer.append("<h1>Upload file</h1>\r\n");
            writer.append("<form method=\"POST\" action=\"upload\" ")
                    .append("enctype=\"multipart/form-data\">\r\n");
            writer.append("<input type=\"file\" name=\"fileName\"/><br/><br/>\r\n");
            writer.append("Caption: <input type=\"text\" name=\"caption\"<br/><br/>\r\n");
            writer.append("<br />\n");
            writer.append("Date: <input type=\"date\" name=\"date\"<br/><br/>\r\n");
            writer.append("<br />\n");
            writer.append("<input type=\"submit\" value=\"Submit\"/>\r\n");
            writer.append("</form>\r\n");
            writer.append("</body>\r\n").append("</html>\r\n");
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Part filePart = request.getPart("fileName");
        String captionName = request.getParameter("caption");
        String formDate = request.getParameter("date");
        String fileName = filePart.getSubmittedFileName();

        if (fileName.equals("")) {
            response.setStatus(302);
            response.sendRedirect("upload");
            return;
        }

        if (formDate.equals("")) formDate = "2020-10-10";
        if (captionName.equals("")) captionName = "No caption";
        String localPath = System.getProperty("catalina.base") + "/webapps/photogallery/images/" + fileName;
        filePart.write(localPath);

        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("Message: " + ex.getMessage());
            return;
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comp3940", "comp3940", "");
            PreparedStatement preparedStatement = con.prepareStatement(
                    "INSERT INTO Photos (id, userId, picture, fileName, caption, dateTaken) VALUES (?,?,?,?,?,?)");
            preparedStatement.setBytes(1, UuidGenerator.asBytes(UUID.randomUUID()));
            preparedStatement.setBytes(2, UuidGenerator.asBytes(UUID.randomUUID()));
            preparedStatement.setString(4, fileName);
            preparedStatement.setString(5, captionName);
            preparedStatement.setString(6, formDate);
            try {
                FileInputStream fin = new FileInputStream(localPath);
                preparedStatement.setBinaryStream(3, fin);
            } catch (Exception ex) {
                System.out.println("Message: " + ex.getMessage());
            }
            int row = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            System.out.println("Message: " + e.getMessage());
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String topPart = "<!DOCTYPE html><html><body><ul>";
        String bottomPart = "</ul></body></html>";
        out.println(topPart + getListing(System.getProperty("catalina.base") + "/webapps/photogallery/images") + bottomPart);
    }

    private String getListing(String path) {
        String dirList = null;
        File dir = new File(path);
        String[] chld = dir.list();
        for (int i = 0; i < chld.length; i++) {
            if ((new File(path + chld[i])).isDirectory())
                dirList += "<li><button type=\"button\">" + chld[i] + "</button></li>";
            else
                dirList += "<li>" + chld[i] + "</li>";
        }
        return dirList;
    }


    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);

        if (session == null || !req.isRequestedSessionIdValid()) {
            return false;
        } else {
            return true;
        }

    }

}


