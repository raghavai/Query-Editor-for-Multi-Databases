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

public class RenameTab extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		Statement   statement = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String product = null;
		
		String  error_message = null;
		boolean error_occured = false;

		String oldName = null;
		String newName = null;
		String message = null;
		String query   = null;

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
			product	= session.getAttribute("dbProductName").toString().toUpperCase();
                        
                        //Retreiving form parameters
			oldName	= req.getParameter("old_name");
			newName	= req.getParameter("new_name");

			//Assiging rename table query to query variable based on the database selected
                        if(product.indexOf("ORACLE") != -1)
                            query = "rename " + oldName + " to " + newName;
			else if(product.equals("ACCESS")){
				
				query="SELECT * INTO "+newName+" FROM "+oldName;
				
				
			}else{
                            query = "rename table " + oldName + " to " + newName;
			
			}
                            try {
				//Loading driver
                                Class.forName(driver);
				//Establishing Connection
                                connection	= DriverManager.getConnection(url,userid,pass);
				statement	= connection.createStatement();
				//Executing query
                                statement.executeUpdate(query);	
                                
                                if(product.equals("ACCESS")){
                    				Statement st2=connection.createStatement();
                    				String query2="DROP TABLE "+oldName;
                    				st2.executeUpdate(query2);
                    				
                    			}
				//Updating successful message
                                message = oldName + " table is successfully renamed to " + newName;
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
                        
                        //Redirect and display all tables after table is successfully renamed
			if(!error_occured) {
				res.setContentType("text/html");
				PrintWriter writer = res.getWriter();
				
				writer.println("<HTML>");
				writer.println("<HEAD>");
				writer.println("<META NAME='Author' CONTENT='Vamsi'>");
				writer.println("<SCRIPT LANGUAGE='javascript'>");
				writer.println("function loadPages() { ");
				writer.println("	window.parent.left.location.href='ListDB'; ");
				writer.println(	"	window.parent.right.location.href=" +
									"'DescDB?message=" + message + "'; ");
				writer.println("} ");
				writer.println("</SCRIPT>");
				writer.println("</HEAD>");
				writer.println("<BODY onLoad='loadPages()' BGCOLOR=#ffffff>");
				writer.println("</BODY></HTML>");
				writer.close();
			}
			//Redirect to DBOperations if renaming failed
                        else res.sendRedirect("DBOperations?message=" + error_message);
		}
	}
}
