import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class GalleryServlet extends HttpServlet {
    private int count = 0;
  private int numRows;

  private ArrayList<Blob> idList;
  private ArrayList<Blob> pictureList;
  private ArrayList<String> fileList;
  private ArrayList<String> captionList;
  private ArrayList<String> dateList;

    protected void doGet(HttpServletRequest request,
      HttpServletResponse response) {
      getDataFromDB();

      try {
          Class.forName("com.mysql.cj.jdbc.Driver");
      } catch (Exception ex) {
          System.out.println("Gallery/Class: " + ex.getMessage());
      }
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

          PrintWriter out = response.getWriter();
          String button = request.getParameter("button");

          if(Objects.equals(button,"Next")){
              if((numRows - 1) > count) count++;
              else if(numRows - 1 == count) count = 0;
          }else if(Objects.equals(button,"Prev")){
              if(count > 0) count--;
              else if(count == 0) count = numRows - 1;
          }

          File f = new File(System.getProperty("catalina.base") + "/webapps/photogallery/images/"+ fileList.get(count));
          FileOutputStream fs = new FileOutputStream(f);
          byte b[] = pictureList.get(count).getBytes(1, (int)pictureList.get(count).length());
          fs.write(b);

          out.println("<html>");
          out.println("<head>");
          out.println("<meta charset='UTF-8'>");
          out.println("<style>");
          out.println("#username {");
          out.println("text-align: right;");
          out.println("color: red;");
          out.println("}");
          out.println("</style>");
          out.println("</head>");
          out.println("<body>");
          out.println("<div>");
          out.println("<div id=\"username\">" + loginMsg + "</div>");

          for(int i = 0; i < numRows; i++){
              if(i == count){
                  out.println("<div id='gallery_" + i + "' class='currentView'>");
                  request.setAttribute("fileName", fileList.get(i));
              }
              else out.println("<div id='gallery_" + i + "' hidden>");
              out.println("<img id = \"img-" + i + "\"   src=./images/" + fileList.get(i) + " alt=\"image\" width=400 height=300>");
              out.println("<br>");
              out.println("<span id = \"caption-" + i + "\"  =>" + captionList.get(i) +"</span>");
              out.println("<br>");
              out.println("<span id = \"date-" + i + "\" >"+ dateList.get(i) + "</span>");

              out.println("<span id = \"id-" + i + "\" class='hiddenGetter' getId='"+ idList.get(i) + "' hidden></span>");
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


          out.println("<form action='gallery' method='post' id='deleteForm'>");
          out.println("<input type='submit' form='deleteForm' id='delete' name='delete' value='Delete'></input>");
          out.println("<input form='deleteForm' id='param' name='file' value='' hidden></input>");
          out.println("</form>");

          out.println("<div>");
          out.println("<form action='main' method='get'>");
          out.println("<button class='button' id='main'>Main</button>");
          out.println("</div><br>");
          out.println("</form>");
          writeScript(out);
          out.println("</body>");
          out.println("</html>");

          out.close();
      }catch(Exception e){
          e.printStackTrace();
      }
   }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Gallery/Class: " + ex.getMessage());
        }
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comp3940", "comp3940", "");
            PreparedStatement preparedStatement = con.prepareStatement(
                    "DELETE FROM Photos where fileName = ?");
            String fileName = request.getParameter("file");

//            Blob blob = con.createBlob();
//            blob.setBytes(1, fileName.getBytes());
//            preparedStatement.setBlob(1, blob);
//            preparedStatement.executeUpdate();

//            PrintWriter out = response.getWriter();
//            out.println(fileName);

            preparedStatement.setString(1, fileName);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            doGet(request, response);
        } catch (Exception e) {
            System.out.println("Gallery/SQL: " + e.getMessage());
        }
    }

   private boolean isLoggedIn(HttpServletRequest req) {
		HttpSession session = req.getSession(false);

		if (session == null || !req.isRequestedSessionIdValid()) {
			return false;
		}else{
			return true;
		}
	}

    public void getDataFromDB(){
        idList = new ArrayList<>();
      pictureList = new ArrayList<>();
      fileList = new ArrayList<>();
      captionList = new ArrayList<>();
      dateList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("Gallery/Class: " + ex.getMessage());
        }
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comp3940", "comp3940", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from Photos");

            Blob blobId;
            Blob blobImage;
            numRows = 0;

            while(rs.next()){
                blobId = rs.getBlob("id");
                blobImage = rs.getBlob("picture");
                idList.add(blobId);
                pictureList.add(blobImage);
                fileList.add(rs.getString("fileName"));
                captionList.add(rs.getString("caption"));
                dateList.add(String.valueOf(rs.getDate("dateTaken")));
                numRows++;
            }

            stmt.close();
            con.close();
        }catch(Exception e){
            System.out.println("gallery/getIdFromDB: " + e.getMessage());
        }

    }

    public void writeScript(PrintWriter out){
        out.println("<script>");
        out.println("let myInterval;");
        out.println("function submitNext(){");
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
        out.println("let id = document.querySelector('.currentView').querySelector('img').getAttribute('src').split('/')[2];");
//        out.println("let id = document.querySelector('.currentView').querySelector('.hiddenGetter').getAttribute('getId');");
        out.println("document.getElementById('param').setAttribute('value',id);");
        out.println("</script>");
    }
}

