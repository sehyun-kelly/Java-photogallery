import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public class GalleryServlet extends HttpServlet {
    private final Connection con = SetUp.getConnection();
    private int currentIndex = 0;
    private int numRows;
    private ArrayList<byte[]> idList;
    private ArrayList<byte[]> userIdList;
    private ArrayList<Blob> pictureList;
    private ArrayList<String> fileList;
    private ArrayList<String> captionList;
    private ArrayList<String> dateList;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        getDataFromDB(response.getWriter());

        try{
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            HttpSession session = request.getSession(false);
            String username = "";

            if (!isLoggedIn(request)) {
                response.setStatus(302);
                response.sendRedirect("login");
            }else{
                username = session.getAttribute("USER_ID").toString();
            }

            String loginMsg = "Logged in as: " + username;

            String button = request.getParameter("button");

            if(Objects.equals(button,"Next")){
                if((numRows - 1) > currentIndex) currentIndex++;
                else if(numRows - 1 == currentIndex) currentIndex = 0;
            }else if(Objects.equals(button,"Prev")){
                if(currentIndex > 0) currentIndex--;
                else if(currentIndex == 0) currentIndex = numRows - 1;
            }else currentIndex = 0;

            byte username_uuid[] = getUuid(username);
            byte userIdList_uuid[] = userIdList.get(currentIndex);
            byte idList_id[] = idList.get(currentIndex);
            String pictureId = new String(idList_id);

            PrintWriter out = response.getWriter();

            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<style>");
            out.println("#username {");
            out.println("text-align: right;");
            out.println("}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div>");
            out.println("<div id=\"username\">" + loginMsg + "</div>");

            for(int i = 0; i < numRows; i++){
                if(i == currentIndex){
                    out.println("<div id='gallery_" + i + "' class='currentView'>");
                    request.setAttribute("fileName", fileList.get(i));
                }
                else out.println("<div id='gallery_" + i + "' hidden>");
                byte[] imagebytes = pictureList.get(i).getBytes(1, (int)pictureList.get(i).length());
                out.println("<img id = \"img-" + i + "\"   src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(imagebytes) + "\" alt=\"image\" width=400 height=300>");
                out.println("<br>");
                out.println("<span id = \"caption-" + i + "\"  =>" + captionList.get(i) +"</span>");
                out.println("<br>");
                out.println("<span id = \"date-" + i + "\" >"+ dateList.get(i) + "</span>");
                out.println("</div>");
            }

            out.println("<br>");
            out.println("<div class='button'>");
            out.println("<form action='gallery' method='get' id='buttonForm'>");
            out.println("<input type='submit' form='buttonForm' id='prev' name='button' value='Prev'></input>");
            out.println("<input type='submit' form='buttonForm' id='next' name='button' value='Next'></input>");
            out.println("</form>");
            out.println("<button type='button' id='auto'>Auto</button>");
            out.println("<button type='button' id='stop'>Stop</button>");
            out.println("</div></div><br>");


            //Delete button appears only on the picture this user uploads
            if(Arrays.equals(username_uuid, userIdList_uuid)) {
                out.println("<form action='gallery' method='post' id='deleteForm'>");
                out.println("<input type='submit' form='deleteForm' id='delete' name='delete' value='Delete'></input>");
                out.println("<input form='deleteForm' id='param' name='pictureId' value='" + pictureId + "' hidden></input>");
                out.println("<input form='deleteForm' name='fileName' value='" + fileList.get(currentIndex) + "' hidden></input>");

                out.println("</form>");
            }

            out.println("<div>");
            out.println("<form action='main' method='get'>");
            out.println("<button class='button' id='main'>Main</button>");
            out.println("</div>");
            out.println("</form>");
            writeScript(out);
            out.println("</body>");
            out.println("</html>");

            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Gallery/Class: " + ex.getMessage());
        }
        try {
            //delete with id - to be updated!!!
//            byte id[] = request.getParameter("pictureId").getBytes();
//            deleteFile(id);

            //delete with fileName
            PreparedStatement preparedStatement = con.prepareStatement(
                    "DELETE FROM Photos where fileName = ?");
            String fileName = request.getParameter("fileName");

            preparedStatement.setString(1, fileName);
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (Exception e) {
            System.out.println("Gallery/SQL: " + e.getMessage());
        }

        doGet(request, response);
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);

        if (session == null || !req.isRequestedSessionIdValid()) {
            return false;
        }else{
            return true;
        }
    }

    private void getDataFromDB(PrintWriter out){
        idList = new ArrayList<>();
        userIdList = new ArrayList<>();
        pictureList = new ArrayList<>();
        fileList = new ArrayList<>();
        captionList = new ArrayList<>();
        dateList = new ArrayList<>();

        Blob blobImage;

        try{
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from Photos");
            numRows = 0;

            while(rs.next()){
                blobImage = rs.getBlob("picture");
                idList.add(rs.getBytes("id"));
                userIdList.add(rs.getBytes("userId"));
                pictureList.add(blobImage);
                fileList.add(rs.getString("fileName"));
                captionList.add(rs.getString("caption"));
                dateList.add(String.valueOf(rs.getDate("dateTaken")));
                numRows++;
            }
            stmt.close();
        }catch(Exception e){
            System.out.println("gallery/getIdFromDB: " + e.getMessage());
        }

    }

    private void writeScript(PrintWriter out){
        out.println("<script>");
        out.println("let myInterval;");
        out.println("function submitNext(){");
        out.println("document.getElementById('deleteForm').style.display = 'none';");
        out.println("let currentView = document.querySelector('.currentView');");
        out.println("let currentIndex = currentView.getAttribute('id').split('_')[1];");
        out.println("let nextIndex;");
        out.println("if(currentIndex == " + (numRows - 1) +") nextIndex = 0;");
        out.println("else nextIndex = ++currentIndex;");
        out.println("currentView.setAttribute('hidden','');");
        out.println("currentView.removeAttribute('class');");
        out.println("document.getElementById('gallery_' + nextIndex).removeAttribute('hidden');");
        out.println("document.getElementById('gallery_' + nextIndex).setAttribute('class', 'currentView');");
        out.println("}");
        out.println("function stopInterval(){");
        out.println("clearInterval(myInterval);");
        out.println("myInterval = null;}");
        out.println("function startInterval(){");
        out.println("if(!myInterval) myInterval = setInterval(submitNext, 2000);}");
        out.println("document.getElementById('auto').addEventListener('click', startInterval);");
        out.println("document.getElementById('stop').addEventListener('click', stopInterval);");
        out.println("</script>");
    }

    private byte[] getUuid(String userId) {
        try {
            PreparedStatement s = con.prepareStatement("SELECT * FROM Users WHERE userId = ?;");
            s.setString(1, userId);

            ResultSet rs = s.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return rs.getBytes("id");
        } catch (Exception e) {
            System.out.println("Gallery/getUuid: " + e.getMessage());
        }
        return null;
    }

    private void deleteFile(byte[] fileID) throws SQLException {
        PreparedStatement preparedStatement = con.prepareStatement(
                    "DELETE FROM Photos where id = ?");
//
//            PrintWriter out = response.getWriter();
//
//            out.println("delete file name:");
//            out.println("<br>");
//            for(int i = 0; i < id.length; i++){
//                out.println(id[i]);
//            }
//            out.println("<br>");
//
//            out.println(new String(id));
//
            preparedStatement.setBytes(1, fileID);
            preparedStatement.executeUpdate();
            preparedStatement.close();

    }
}

