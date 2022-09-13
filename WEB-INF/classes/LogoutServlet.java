import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set response content type

		HttpSession session = request.getSession(false);
		if (session != null && request.isRequestedSessionIdValid()) {
			session.invalidate();
		}

		response.sendRedirect("login");

	}

// Method to handle POST method request.
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set response content type
		HttpSession session = request.getSession(false);
		if (session != null && request.isRequestedSessionIdValid()) {
			session.invalidate();
		}
		response.sendRedirect("login");

	}
}
