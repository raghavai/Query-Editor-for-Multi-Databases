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

public class AlterColumn extends HttpServlet  
{
        public void doPost(HttpServletRequest req, HttpServletResponse res)
        			throws ServletException, IOException 
        {
                Connection connection = null;
                Statement   statement = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String	error_message	= null;
		boolean error_occured	= false;

		String message		= null;
		String tableName	= null;
		String columnName	= null;
		String columnType	= null;
		String columnSize	= null;

		String primary	= null;
		String unique	= null;
		String notnull	= null;

		String	primaryKey	= null;
		boolean keyChanged	= false;
		boolean keyExisted	= false;
                
                //Creating and retreiving session
		HttpSession session = req.getSession(false);
                
                //Redirecting to Login when Session Timed Out
		if(session == null) 
                {
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
		else 
                {   
                        //Retreiving session attributes
			driver	= session.getAttribute("driver").toString();
			url	= session.getAttribute("url").toString();
			userid	= session.getAttribute("userid").toString();		
			pass	= session.getAttribute("pass").toString();
                        
                        //Retreiving form parameters
			tableName	= req.getParameter("table_name").trim();
			columnName	= req.getParameter("column_name").trim();
			columnType	= req.getParameter("column_type").trim();
			columnSize	= req.getParameter("column_size").trim();
			primaryKey	= req.getParameter("primary_key").trim();
			primary		= req.getParameter("primary");
			unique		= req.getParameter("unique");
			notnull		= req.getParameter("notnull");

                        //Query for altering table column
			String query =	"alter table " + tableName + " modify " + 
							columnName + " " + columnType;
			//Modifying Column Size
                        if(!columnSize.equals("0")) 
                            query += "(" + columnSize + ")";
			
                        //PrimaryKey Exists
			if(primaryKey.length() > 1)
                            keyExisted = true;
                            
                        //Column has both unique and not null constraints
			if(unique != null && notnull != null)	
                            primary = "1";

			//Adding primarykey
                        if(primary != null)
                        {
				if(primaryKey.indexOf("," + columnName + ",") == -1) 
                                {
					primaryKey += columnName + ",";
					keyChanged = true;
				}
			}
			else if(unique != null)	
                            query += " unique";
			else if(notnull != null)	
                            query += " not null";
			//Removing Primary Key	
			if(primary == null && primaryKey.indexOf("," + columnName + ",") != -1) {
				primaryKey = primaryKey.replaceFirst("," + columnName + "," , ",");
				keyChanged = true;
			}
			
                        //Executing alter query
			try
                        {
				//Loading Driver
                                Class.forName(driver);
                                //Establishing connection
				connection	= DriverManager.getConnection(url,userid,pass);
				statement	= connection.createStatement();
				
                                //Dropping primary key
                                if(keyExisted && keyChanged)
                                {
					statement.executeUpdate("alter table " + tableName + " drop primary key");
				}
				
                                statement.executeUpdate(query);
				
				//Adding primary key
                                if(keyChanged && primaryKey.length() > 1) 
                                {
					primaryKey = primaryKey.substring(1,primaryKey.length()-1);
					query = "alter table " + tableName + " add primary key(" + primaryKey + ")";
					statement.executeUpdate(query);
				}
                                //Updating success message
				message	= columnName + " column is successfully altered.";
			}
			catch(Exception e)	
                        {	
				error_occured = true;
				error_message = e.toString();	
			}
                        
                        //Closing statement and connection
			try
                        {
				statement.close();
				connection.close();
			}
			catch(Exception e)	
                        {
                            e.printStackTrace();	
                        }
                        //Redirecting to TabOperations if any error occurs
			if(error_occured) 
                            res.sendRedirect(	"TabOperations?table_name=" + 
												tableName + "&message=" + error_message);
			//Redirecting to DescTab page after successful execution
                        else
                            res.sendRedirect("DescTab?table_name=" + tableName + "&message=" + message);
		}
	}
}