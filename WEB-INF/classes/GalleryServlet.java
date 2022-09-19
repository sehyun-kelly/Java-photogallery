import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class GalleryServlet extends HttpServlet {
    private int count = 0;
  private int numRows;
  private ArrayList<Blob> pictureList;
  private ArrayList<String> fileList;
  private ArrayList<String> captionList;
  private ArrayList<String> dateList;

  public void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
      getDataFromDB();

      try {
          Class.forName("com.mysql.cj.jdbc.Driver");
      } catch (Exception ex) {
          System.out.println("Gallery/Class: " + ex.getMessage());
      }
      try{
          Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comp3940", "comp3940", "");
          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery("select * from Photos");

          response.setContentType("text/html");
          response.setCharacterEncoding("UTF-8");
          String username = "";

          if (!isLoggedIn(request)) {
              response.setStatus(302);
              response.sendRedirect("login");
          }else{
              username = request.getParameter("user_id");
          }

          String loginMsg = "Logged in as: " + username;

          PrintWriter out = response.getWriter();
          String button = request.getParameter("button");

          //when next is clicked
          if(Objects.equals(button,"Next")){
              if((numRows - 1) > count) count++;
              else if(numRows - 1 == count) count = 0;
          }
          //when prev is clicked
          else if(Objects.equals(button,"Prev")){
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
          out.println("<div>");
          out.println("<img id = \"img-" + count + "\"   src=./images/" + fileList.get(count) + " alt=\"image\" width=200 height=150>");
          out.println("<br>");
          out.println("<span id = \"caption-" + count + "\"  =>" + captionList.get(count) +"</span>");
          out.println("<br>");
          out.println("<span id = \"date-" + count + "\" >"+ dateList.get(count) + "<span>");
          out.println("<div>");
          out.println("<br>");
          out.println("<div class='button'>");
          out.println("<form action='gallery' method='get' id='buttonForm'>");
          out.println("<input type='submit' form='buttonForm' id='prev' name='button' value='Prev'></input>");
          out.println("<input type='submit' form='buttonForm' id='next' name='button' value='Next'></input>");
          out.println("</form>");
//          out.println("<form action='gallery' method='post' id='autoForm'>");
//          out.println("<input type='submit' form='autoForm' class='button' id='auto' name='auto' value='Auto'></input>");
//          out.println("<input type='submit' form='autoForm' class='button' id='stop' name='stop' value='Stop'></input>");
//          out.println("</form>");
          out.println("</div></div><br>");

          out.println("<div>");
          out.println("<form action='main' method='get'>");
          out.println("<button class='button' id='main'>Main</button>");
          out.println("</div><br>");
          out.println("</form>");
          out.println("</body></html>");

          out.close();
          stmt.close();
          con.close();
      }catch(Exception e){
          e.printStackTrace();
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

            Blob blob = null;
            numRows = 0;

            while(rs.next()){
                blob = rs.getBlob("picture");
                pictureList.add(blob);
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

}

