import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class SearchServlet extends HttpServlet {

	private static String CAPTION_FILENAME;
	private static String DATA_FILENAME;
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
System.out.println("yes, doGet Called?");
		if (!isLoggedIn(request)) { 
			response.setStatus(302);
			response.sendRedirect("login");
		}
		
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession(false);
        boolean isLoggedIn = isLoggedIn(request);
		if (!isLoggedIn) {
            response.setStatus(302);
            response.sendRedirect("login");

        } else {
			String userName = "Logged in as: " + session.getAttribute("USER_ID");
			String html = "<!DOCTYPE html>" +
					"<html>" +
					"<body>" +
					"<div style=\"text-align: right;\">" + userName + "</div>" +
					"<h2> Search Filter </h2> " +
			"<form action='search' method = 'post' id = 'searchForm'>" +
			"<label for='caption'>Caption: </label>" +
			"<input type='text' id = 'caption' name = 'caption'>" +
			"<label for='date'>Date: </label>" +
			"<input type='date' placeholder='yyyy-mm-dd' id = 'date' name = 'date'>" +
			"<button type='submit' form='searchForm' value='Submit'>Search</button>" +
			"</form>" +
			"</body>" +
					"</html>";
			PrintWriter out = response.getWriter();
			out.println(html);
		}

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
System.out.println("?????doPost Called???????????????");
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		if (!(isLoggedIn(request))) {
			response.sendRedirect("login");
		} else {

			HttpSession session = request.getSession(false);

			PrintWriter out = response.getWriter();
			String caption = request.getParameter("caption");
			String date = request.getParameter("date");
			System.out.println("caption: " + caption);
			System.out.println("date: " + date);
			findCaption(caption);
			findDate(date);

			if ((Objects.equals(caption, "")) & (Objects.equals(date, ""))) {
				out.println("<div>Fill in at least one. (Caption or data)</div>");
			}
			else if ((!Objects.equals(caption, "")) & (Objects.equals(date, ""))) {
				if (CAPTION_FILENAME != null) {
					out.println("<img id = \"img_src\" src=./images/" + CAPTION_FILENAME + " alt=" + CAPTION_FILENAME + " width=400 height=350>");
				} else {
					out.println("<div>No such photo was found! Please enter correct caption.</div>");
				}
			}
			else if (Objects.equals(caption, "")) {
				if (DATA_FILENAME != null) {
					out.println("<img id = \"img_src\" src=./images/" + DATA_FILENAME + " alt=" + DATA_FILENAME + " width=400 height=350>");
				} else {
					out.println("<div>No such photo was found! Please enter correct date.</div>");
				}
			}
			else {
				if (Objects.equals(DATA_FILENAME, CAPTION_FILENAME)) {
					out.println("<img id = \"img_src\" src=./images/" + CAPTION_FILENAME + " alt=" + CAPTION_FILENAME + " width=400 height=350>");
				} else {
					out.println("<div>Caption and Data not match in one photo.</div>");
				}

			}
		}
	}

	private void findDate(String date) {
		Connection con;
		String filename = null;
		System.out.println(date);
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			System.out.println("Search/Class: " + ex.getMessage());
		}
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comp3940", "comp3940", "");
			PreparedStatement s = con.prepareStatement("SELECT filename FROM Photos WHERE dateTaken = '" + date + "';");
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				filename = rs.getString(1);
			}
			DATA_FILENAME = filename;

		} catch (Exception e) {
			System.out.println("Search/findCaption: " + e.getMessage());
		}

	}

	private void findCaption(String caption) {
		Connection con;
		String filename = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			System.out.println("Search/Class: " + ex.getMessage());
		}
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comp3940", "comp3940", "");
			PreparedStatement s = con.prepareStatement("SELECT filename FROM Photos WHERE caption = '" + caption + "';");
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				filename = rs.getString(1);
			}
			CAPTION_FILENAME = filename;

		} catch (Exception e) {
			System.out.println("Search/findCaption: " + e.getMessage());
		}

	}



	private boolean isLoggedIn(HttpServletRequest req) {
		HttpSession session = req.getSession(false);

		return session != null && req.isRequestedSessionIdValid();

	}	
}
