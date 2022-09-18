import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;

public class GalleryServlet extends HttpServlet {
  private int numRows;
  private ArrayList<Blob> pictureList = new ArrayList<>();
  private ArrayList<String> fileList = new ArrayList<>();
  private ArrayList<String> captionList = new ArrayList<>();
  private ArrayList<String> dateList = new ArrayList<>();

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

//          displayPhoto(idList.get(0), out);

          File f = new File(System.getProperty("catalina.base") + "/webapps/photogallery/images/"+ fileList.get(0));
          FileOutputStream fs = new FileOutputStream(f);
          byte b[] = pictureList.get(0).getBytes(1, (int)pictureList.get(0).length());
          fs.write(b);

          out.println("<img id = \"img-0\"   src=./images/" + fileList.get(0) + " alt=\"image\" width=200 height=150>");
          out.println("<br>");
          out.println("<span id = \"caption-0\"  =>" + captionList.get(0) +"</span>");
          out.println("<br>");
          out.println("<span id = \"date-0\" >"+ dateList.get(0) + "<span>");
          out.println("<div>");
          out.println("<br>");
          out.println("<div class='button'>");
          out.println("<button class='button' id='prev'>Prev</button>");
          out.println("<button class='button' id='next'>Next</button>");
          out.println("</div></div><br>");

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

    public void getDataFromDB(){
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

    public void displayPhoto(Blob id, PrintWriter out){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("Gallery/Class: " + ex.getMessage());
        }
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comp3940", "comp3940", "");
            PreparedStatement stmt = con.prepareStatement("select * from Photos where id = ?");
            stmt.setBlob(1, id);

            ResultSet rs = stmt.executeQuery();

            String fileName = "";
            String caption = "";
            String date = "";
            Blob blob;

            while(rs.next()){
                fileName = rs.getString("fileName");
                caption = rs.getString("caption");
                date = String.valueOf(rs.getDate("dateTaken"));
                blob = rs.getBlob("picture");

                File f = new File(System.getProperty("catalina.base") + "/webapps/photogallery/images/"+ fileName);
                FileOutputStream fs = new FileOutputStream(f);
                byte b[] = blob.getBytes(1, (int)blob.length());
                fs.write(b);

                out.println("<img id = \"img_src\" src=./images/" + fileName + " alt=\"image\" width=200 height=150>");
                out.println("<br>");
                out.println("<span>" + caption +"</span>");
                out.println("<br>");
                out.println("<span>"+ date + "<span>");
                out.println("<div>");
            }

            stmt.close();
            con.close();
        }catch(Exception e){
            System.out.println("gallery/displayPhoto: " + e.getMessage());
        }

    }
}

