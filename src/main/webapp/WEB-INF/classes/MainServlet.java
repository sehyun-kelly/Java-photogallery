import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;

public class MainServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(302);
            response.sendRedirect("login");
        }
        String title = "Logged in as: " + session.getAttribute("USER_ID");
        response.setContentType("text/html");
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
        String html = docType + "<html>\n" + "<head><title>" + title + "</title></head>\n"
                + "<body bgcolor=\"#f0f0f0\">\n" + "<h1 align=\"center\">" + title + "</h1>\n" +
                "<div style=\"text-align: center;\">\n" +
                "<form action=\"upload\" method=\"GET\">\n" +
                "<input type=\"submit\" value=\"UPLOAD\" />\n" +
                "</form>\n" +
                "</div>\n" +
                "<div style=\"text-align: center;\">\n" +
                "<form action=\"gallery\" method=\"GET\">\n" +
                "<input type=\"submit\" value=\"GALLERY\" />\n" +
                "</form>\n" +
                "</div>\n" +
                "<div style=\"text-align: center;\">\n" +
                "<form action=\"search\" method=\"GET\">\n" +
                "<input type=\"submit\" value=\"SEARCH\" />\n" +
                "</form>\n" +
                "</div>\n" +
                "<div style=\"text-align: center;\">\n" +
                "<form action=\"chat\" method=\"GET\">\n" +
                "<input type=\"submit\" value=\"CHAT\" />\n" +
                "</form>\n" +
                "</div>\n" +
                "<div style=\"text-align: center;\">\n" +
                "<form action=\"logout\" method=\"GET\">\n" +
                "<input type=\"submit\" value=\"LOGOUT\" />\n" +
                "</form>\n" +
                "</div>\n" +
				"</body></html>";

        PrintWriter out = response.getWriter();
        out.println(html);
    }
}
