import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EmptyTab extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		Statement   statement = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm = null;
		String schema	  = null;

		String dbProductName    = null;
		String dbProductVersion = null;

		String  error_message = null;
		boolean error_occured = false;

		String tableName = null;
		String message	 = null;

		int deletedCount = 0;

		//Creating session object and Retreiving session 
                HttpSession session = req.getSession(false);
                
                //session timed out
		if(session == null) {
			res.setContentType("text/html");
			PrintWriter writer = res.getWriter();
                        
			writer.println("<HTML>");
			writer.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
							"Sorry Your session expired. Please Login Again.'\"");
			writer.println("</BODY>");
			writer.println("</HTML>");

			writer.close();
		}
                
                //Session exists
		else {
                        
                        //Retreiving session attributes
			driver			= session.getAttribute("driver").toString();
			url			= session.getAttribute("url").toString();
			userid			= session.getAttribute("userid").toString();		
			pass			= session.getAttribute("pass").toString();
			dbProductName   	= session.getAttribute("dbProductName").toString();
			dbProductVersion        = session.getAttribute("dbProductVersion").toString();
			schemaTerm		= session.getAttribute("schemaTerm").toString();
			schema			= session.getAttribute("schema").toString();
			tableName		= req.getParameter("table_name");
                        
                        //Delete table query
			String query = "delete from " + tableName;
                        
			try {
                                //Loading Driver
				Class.forName(driver);
				//Establishing Connection
                                connection	= DriverManager.getConnection(url,userid,pass);
				statement	= connection.createStatement();
				
                                //Executing query and retreiving number of rows deleted
                                deletedCount    = statement.executeUpdate(query);
				//Updating message with number of rows delted
                                message		= deletedCount + " record(s) deleted from '" + tableName + "' table.";
			}
			catch(Exception e)	{
				error_occured = true;
				error_message = e.toString();
			}
                        
                        //Closing Connection
			try {
				statement.close();
				connection.close();
			}
			catch(Exception e)	{	e.printStackTrace();	}

			//Redirecting to DescDB with updated message on successful deletion
                        if(!error_occured)	
                            res.sendRedirect("DescDB?message=" + message);
			
                        //Redirecting to DescDb with error mesesage if deletion failed
                        else		
                            res.sendRedirect("DescDB?message=" + error_message);
		}
	}
}