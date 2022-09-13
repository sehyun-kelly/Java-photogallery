import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
public class GalleryServlet extends HttpServlet {
  private int mCount;
 
  public void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
      
	if (!isLoggedIn(request)) { 
		response.setStatus(302);
		response.sendRedirect("login");
	}
      
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      File dir = new File(System.getProperty("catalina.base") + "/webapps/photogallery/images");
      String[] chld = dir.list();
     // instead of hardcoding to the first image in the list as done below
     //save search criteria as session variable and use it to filter the files in the above array
      //check if prev or next button pressed and then rotate through the array accordingly
     

	String img_src = chld[0]; 
        String alt_text = "SOME IMAGE";	 
      PrintWriter out = response.getWriter();
      out.println("<html>");
          out.println("<meta charset='UTF-8'>");
          out.println("<body>");
          out.println("<div>");
          out.println("<form action='/photogallery/gallery' method='GET'>");
          out.println("<div>");
          
          out.println("<img id = \"img_src\" src=./images/" + img_src + " alt=" + alt_text + " width=200 height=150>");
          out.println("<div>");
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