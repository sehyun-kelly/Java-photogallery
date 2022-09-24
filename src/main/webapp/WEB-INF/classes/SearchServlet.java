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

	private static String[] CAPTION_FILENAME;
	private static String[] DATA_FILENAME;
	private static String[] MY_FILENAME;
	private static int CAPTION_LOOP = -1;
	private static int DATE_LOOP = -1;
	private static int MY_LOOP = -1;
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			CheckDateCaption(date, caption);

			if ((Objects.equals(caption, "")) & (Objects.equals(date, ""))) {
				out.println("<div>Fill in at least one. (Caption or data)</div>");
			}
			else if ((!Objects.equals(caption, "")) & (Objects.equals(date, ""))) {
				if ((CAPTION_FILENAME != null) & (CAPTION_LOOP >= 0)) {
					for (int i = 0; i <= CAPTION_LOOP; i++) {
						out.println("<img id = \"img_src\" src=./images/" + CAPTION_FILENAME[i] + " alt=" + CAPTION_FILENAME[i] + " width=400 height=350>");
					}

				} else {
					out.println("<div>No such photo was found! Please enter correct caption.</div>");
				}
			}
			else if (Objects.equals(caption, "")) {
				if ((DATA_FILENAME != null) & (DATE_LOOP >= 0)) {
					for (int j = 0; j <= DATE_LOOP; j++) {
						out.println("<img id = \"img_src\" src=./images/" + DATA_FILENAME[j] + " alt=" + DATA_FILENAME[j] + " width=400 height=350>");
					}
				} else {
					out.println("<div>No such photo was found! Please enter correct date.</div>");
				}
			}
			else {
				if (MY_LOOP >= 0) {
					for (int k = 0; k <= MY_LOOP; k++) {
						out.println("<img id = \"img_src\" src=./images/" + MY_FILENAME[k] + " alt=" + MY_FILENAME[k] + " width=400 height=350>");
					}
				} else {
					out.println("<div>Caption and Data not match in one photo.</div>");
				}
			}
		}
	}

	public void CheckDateCaption(String date, String caption) {
		Connection con;
		String[] myArray = new String[1024];
		MY_LOOP = -1;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			System.out.println("Search/Class: " + ex.getMessage());
		}
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comp3940", "comp3940", "");
			PreparedStatement s = con.prepareStatement("SELECT filename FROM Photos WHERE dateTaken = '" + date + "' && caption = '" + caption + "';");
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				MY_LOOP++;
				myArray[MY_LOOP] = rs.getString(1);
			}
			MY_FILENAME = myArray;

		} catch (Exception e) {
			System.out.println("Search/findCaptionAndDate: " + e.getMessage());
		}
	}

	private void findDate(String date) {
		Connection con;
		String[] dateArray = new String[1024];
		DATE_LOOP = -1;
//		String filename = null;
//		System.out.println(date);
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			System.out.println("Search/Class: " + ex.getMessage());
		}
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comp3940", "comp3940", "");
			PreparedStatement s = con.prepareStatement("SELECT filename FROM Photos WHERE dateTaken = '" + date + "';");
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				DATE_LOOP++;
				dateArray[DATE_LOOP] = rs.getString(1);
			}
			DATA_FILENAME = dateArray;

		} catch (Exception e) {
			System.out.println("Search/findDate: " + e.getMessage());
		}

	}

	private void findCaption(String caption) {
		Connection con;
		String[] captionArray = new	String[1024];
		CAPTION_LOOP = -1;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			System.out.println("Search/Class: " + ex.getMessage());
		}
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/comp3940", "comp3940", "");
			PreparedStatement s = con.prepareStatement("SELECT filename FROM Photos WHERE caption = '" + caption + "';");
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				CAPTION_LOOP ++;
				captionArray[CAPTION_LOOP] = rs.getString(1);
			}
			CAPTION_FILENAME = captionArray;
		} catch (Exception e) {
			System.out.println("Search/findCaption: " + e.getMessage());
		}
	}



	private boolean isLoggedIn(HttpServletRequest req) {
		HttpSession session = req.getSession(false);

		return session != null && req.isRequestedSessionIdValid();

	}	
}
