import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.Objects;
import java.util.UUID;


public class LoginServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		SetUp.setUpTable();
		PrintWriter out = response.getWriter();
		out.println("<html>\n" + "<head><title>" + "Login" + "</title></head>\n" + "<body>\n"
				+ "<h1 align=\"center\">" + "Login" + "</h1>\n" + "<form action=\"login\" method=\"POST\">\n"
				+ "Username: <input type=\"text\" name=\"user_id\">\n" + "<br />\n"
				+ "Password: <input type=\"password\" name=\"password\" />\n" + "<br />\n"
				+ "<input type=\"submit\" name=\"button\" value=\"Sign in\" />\n"
				+ "<input type=\"submit\" name=\"button\" value=\"Register\" />\n"+ "</form>\n"
				+ "</form>\n" + "</body>\n</html\n");
				
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String button = request.getParameter("button");
		response.setContentType("text/html");
		String title = "Logged in as: ";
		String username = request.getParameter("user_id");
		String password = request.getParameter("password");
		HttpSession session = request.getSession(true);
		session.setAttribute("USER_ID", username);
		Boolean logged = false;

		final String sql = "INSERT INTO users VALUES (?, ?, ?);";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (Objects.equals(button, "Sign in")) {
			System.out.println("sign in pass");
			try {
				Connection conn = DriverManager.getConnection("jdbc:mysql://us-cdbr-east-06.cleardb.net/heroku_a7d042695ca2198", "b62388eed31a05", "866f0c06");

				Statement stmt = conn.createStatement();

				ResultSet rs = stmt.executeQuery("SELECT * FROM users");
				while (rs.next()) {
					String user = rs.getString("userID");
					String pass = rs.getString("password");
					if (user.equals(username) && pass.equals(password)) {
//						response.setStatus(302);
						logged = true;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Account doesn't exist");
			}
			if (logged) {
				response.sendRedirect("main");
			} else {
				response.sendRedirect("login");
			}
		} else if (button.equals("Register")){
			System.out.println("register pass");
			try {
				Connection conn = DriverManager.getConnection("jdbc:mysql://us-cdbr-east-06.cleardb.net/heroku_a7d042695ca2198", "b62388eed31a05", "866f0c06");
				PreparedStatement stmt = conn.prepareStatement(sql);

				stmt.setBytes(1, UuidGenerator.asBytes(UUID.randomUUID()));
				stmt.setString(2, username);
				stmt.setString(3, password);
				stmt.executeUpdate();

				System.out.println("Account created");
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Account exists");
			}
			response.sendRedirect("login");
		}
	}
}
