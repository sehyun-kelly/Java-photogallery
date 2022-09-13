import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
public class LoginServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html>\n" + "<head><title>" + "Login" + "</title></head>\n" + "<body>\n"
				+ "<h1 align=\"center\">" + "Login" + "</h1>\n" + "<form action=\"login\" method=\"POST\">\n"
				+ "Username: <input type=\"text\" name=\"user_id\">\n" + "<br />\n"
				+ "Password: <input type=\"password\" name=\"password\" />\n" + "<br />\n"
				+ "<input type=\"submit\" value=\"Sign in\" />\n" + "</form>\n"
				+ "</form>\n" + "</body>\n</html\n");
				
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		String title = "Logged in as: ";
		String username = request.getParameter("user_id");
		String password = request.getParameter("password");
		HttpSession session = request.getSession(true);
		session.setAttribute("USER_ID", username);
		response.setStatus(302);
		response.sendRedirect("main");		
	}
}
