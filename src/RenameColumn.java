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

public class RenameColumn extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
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
		String oldName	 = null;
		String newName	 = null;
		String tableName = null;
		String primaryKey= null;
		String columnSpec= null;
		String dbProductName=null;
		//Creating session object and retreiving session
                HttpSession session = req.getSession(false);
                
                //Redirecting to Login page if session Timed Out
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
                        
                        //Retreiving session attributes
			driver	= session.getAttribute("driver").toString();
			url	= session.getAttribute("url").toString();
			userid	= session.getAttribute("userid").toString();		
			pass	= session.getAttribute("pass").toString();
                        
                        //Retreiving form parameters
			oldName		= req.getParameter("old_name");
			newName		= req.getParameter("new_name");
			tableName	= req.getParameter("table_name");
			primaryKey	= req.getParameter("primary_key");
			columnSpec	= req.getParameter("column_spec");
			dbProductName	        = session.getAttribute("dbProductName").toString();

			try {
				//Loading driver
                                Class.forName(driver);
				//Establishing connection
                                connection	= DriverManager.getConnection(url,userid,pass);
				statement	= connection.createStatement();
                                
                                //Assiging alter table query to query variable
				String query="";
				
				 query =	"alter table " + tableName + " change " + 
								oldName +" "+newName+" "+columnSpec;
                                //Executing alter query
    			
                                statement.executeUpdate(query);
                                //Updating succesful message
				message		= oldName + " column is successfully renamed to " + newName + ".";
			}
			catch(Exception e)	{	
                                //Enabling error_occured if any error occurs during renaming or connection fails
				error_occured = true;
				error_message = e.toString();	
			}
                        
                        //Closing connection
			try {
				statement.close();
				connection.close();
			}
			catch(Exception e)	{	e.printStackTrace();	}
                        
                        //Redirecting to table operations along with error message if renaming failed 
			if(error_occured) res.sendRedirect(	"TabOperations?table_name=" + 
												tableName + "&message=" + error_message);
			//Redirecting to Desctab along with successful message after renaming is successful
                        else res.sendRedirect("DescTab?table_name=" + tableName + "&message=" + message);
		}
	}
}