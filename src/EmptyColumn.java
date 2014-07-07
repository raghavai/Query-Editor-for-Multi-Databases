import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EmptyColumn extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
                Connection connection = null;
		Statement   statement = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String	error_message	= null;
		boolean error_occured	= false;

		String message	 = null;
		String tableName = null;
		String columnName= null;
		String primaryKey= null;
		String columnSpec= null;
                
                //Creating session object and retreiving session
		HttpSession session = req.getSession(false);
                
                //Redirecting to Login page when Session Timed Out
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
		
                //Session Exists
                else {
			
                        //Retreiving Session Attributes
                        driver	= session.getAttribute("driver").toString();
			url	= session.getAttribute("url").toString();
			userid	= session.getAttribute("userid").toString();		
			pass	= session.getAttribute("pass").toString();
                        
                        //Retreiving Form Parameters
			tableName = req.getParameter("table_name");
			columnName= req.getParameter("column_name");
			primaryKey= req.getParameter("primary_key");
			columnSpec= req.getParameter("column_spec");

			String query = null;
			
			try {
				//Loading Driver
                                Class.forName(driver);
				
                                //Establishing Connection
                                connection	= DriverManager.getConnection(url,userid,pass);
				statement	= connection.createStatement();
                                
                                //Drop Column Query
				query = "alter table " + tableName + " drop column " + columnName;
				statement.executeUpdate(query);
				//Add Column Query
                                query = "alter table " + tableName + " add column " + columnName + " " + columnSpec;
				statement.executeUpdate(query);
		
				/*Search for primary key
                                 *      if exists drop primarykey
                                 *          before adding new primary key */
                                if(primaryKey.indexOf("," + columnName + ",") != -1) {
					//Dropping primary key
                                        primaryKey = primaryKey.substring(1,primaryKey.length()-1);
					query = "alter table " + tableName + " drop primary key";
					statement.executeUpdate(query);
					//Adding new primary key
                                        query = "alter table " + tableName + " add primary key(" + primaryKey + ")";
					statement.executeUpdate(query);
				}
				//Updating message after successful execution 
                                message	= columnName + " column emptied successfully.";
			}
			catch(Exception e)	{	
				//Enabling error occured if execution not successful
                                error_occured = true;
				error_message = e.toString();	
			}
                        
                        //Closing Connection
			try {
				statement.close();
				connection.close();
			}
			catch(Exception e)	{	e.printStackTrace();	}
                        
                        //Redirect to TabOperations if exection failed
			if(error_occured) res.sendRedirect(	"TabOperations?table_name=" + 
												tableName + "&message=" + error_message);
                        
                        //Redirect to DescTab on successful execution
			else res.sendRedirect("DescTab?table_name=" + tableName + "&message=" + message);
		}
	}
}