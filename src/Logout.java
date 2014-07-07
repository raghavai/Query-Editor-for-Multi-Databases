import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Logout extends HttpServlet
{
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		//Creating session object and retreiving session
                HttpSession session = req.getSession(false);
		
                //Invalidate session if session exists
                if(session != null)	
                    session.invalidate();

		res.setContentType("text/html");
		PrintWriter writer = res.getWriter();

		/*Display logout message
                 *      and redirect to login page */
                writer.println("<HTML>");
		writer.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
						"You are successfully logged out'\"");
		writer.println("</BODY>");
		writer.println("</HTML>");

		writer.close();
	}
}