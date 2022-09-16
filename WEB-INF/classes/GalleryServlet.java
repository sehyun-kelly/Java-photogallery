import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class GalleryServlet extends HttpServlet {
  private int mCount;

  public void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
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
          out.println("<form action='/photogallery/image' method='GET'>");
          out.println("<div>");

          String caption = "NONE RETRIEVED";
          String date = "NO DATE";
          String fileName = "";
          String alt_text = "";

          byte b[];
          Blob blob = null;
          int i = 0;

          while(rs.next()){
              caption = rs.getString("caption");
              date = String.valueOf(rs.getDate("dateTaken"));
              fileName = rs.getString("fileName");
              blob = rs.getBlob("picture");

              File f = new File(System.getProperty("catalina.base") + "/webapps/photogallery/images/"+ fileName);
              FileOutputStream fs = new FileOutputStream(f);
              b = blob.getBytes(1, (int)blob.length());
              fs.write(b);

              alt_text = "img num " + i;

              out.println("<img id = \"img_src\" src=./images/" + fileName + " alt=" + alt_text + " width=200 height=150>");
              out.println("<br>");
              out.println("<span>" + caption +"</span>");
              out.println("<br>");
              out.println("<span>"+ date + "<span>");
              out.println("<div>");
              i++;
          }
          out.println("<br>");
          out.println("<div class='button'>");
          out.println("<button class='button' id='prev'>Prev</button>");
          out.println("<button class='button' id='next'>Next</button>");
          out.println("</div></div><br>");
          out.println("</form>");
          out.println("<div>");
          out.println("<form action='main' method='GET'>");
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
}

