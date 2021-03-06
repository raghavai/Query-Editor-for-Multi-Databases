import java.lang.String;
import java.lang.Exception;

import java.io.PrintWriter;
import java.io.IOException;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;

import java.util.Vector;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListDB extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) 
				throws ServletException, IOException {

		Connection connection = null;
		DatabaseMetaData dbMetaData = null;
		ResultSet resultSet = null;
		
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;
		
		String dbProductName= null;
		String schemaTerm	= null;
		String schema		= null;
		int	   schemaValue	= 0;

		String types[] = { "TABLE" };

		Vector tables  = new Vector();

		boolean error_occured = false;
		String  error_message = null;

		//Creating session object and retreiving session
                HttpSession session = req.getSession(false);
		PrintWriter writer  = res.getWriter();
                
                 //Session Timed Out
		if(session == null) {
			writer.println("<HTML>");
			writer.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
							"Sorry Your session expired. Please Login Again.'\"");
			writer.println("</BODY>");
			writer.println("</HTML>");
		}
                
                 //Session Exists
		else {
                        //Retreiving Session Attributes
			driver = session.getAttribute("driver").toString();
			url    = session.getAttribute("url").toString();
			userid = session.getAttribute("userid").toString();		
			pass   = session.getAttribute("pass").toString();

			dbProductName = session.getAttribute("dbProductName").toString();
			schemaTerm	  = session.getAttribute("schemaTerm").toString();
			schemaValue	  = Integer.parseInt(session.getAttribute("schemaValue").toString());
			schema		  = session.getAttribute("schema").toString();
			try {
				//Loading Driver
                                Class.forName(driver);
				//Establishing Connection
                                connection = DriverManager.getConnection(url,userid,pass);
                                //Retreiving metadata
				dbMetaData = connection.getMetaData();		
			}
			catch(Exception e) {
				//Enabling error occured if connection failed
                                error_occured = true;
				error_message = e.toString();
			}
                        
                        //Connection Successful
			if(!error_occured) {
				try {
					//Updating ResultSet with Table Information
                                        if(schemaValue == 1) 
                                                resultSet = dbMetaData.getTables(null,schema,null,types);
					else	
                                                resultSet = dbMetaData.getTables(schema,null,null,types);
                                        //Iterating Result Set and obtaining Tables
					while(resultSet.next()) 
                                                tables.add(resultSet.getString(3).trim());
				}
				catch(Exception e) {
					//Enabling error occured
                                        error_occured = true;
					error_message = e.toString();
				}
				
                                /* Closing COnnection
                                 *      and resultSet */
                                try {
					connection.close();
					resultSet.close();
				}
				catch(Exception e)	{	e.printStackTrace();	}
			}
                        
                         //Displaying error message 
			if(error_occured) {
				writer.println("<HTML><HEAD><H3>" + error_message + "</H3></HEAD></HTML>");
			}
                        
                        //Displaying Database information
			else {	
                            	writer.println("<HTML>");
				writer.println("<HEAD>");
				writer.println("<LINK REL='stylesheet' TYPE='text/css' HREF='styles.css'>");
				writer.println("</HEAD>");
				writer.println("<BODY BGCOLOR=#c5d6df link=black alink=black vlink=black>");
				
                                //Displaying database product name
                                writer.println("<P id=list_p>Connected to<BR> " + dbProductName + "</P>");
				writer.println("<P id=list_link><A HREF='Logout'>Disconnect</A></P>");
				writer.println("<TABLE WIDTH=94% ALIGN=center CELLSPACING=1 CELLPADDING=1 BORDER=0>");
				writer.println("<TR><TH id=list_th>");
				writer.println("<A HREF='DescDB' TARGET=right STYLE='color:white'>"); 
                                
                                //Displaying schema name
				if(schema.length()>15)	writer.println(schema.substring(0,13) + "..</A></TH></TR>");
				else					writer.println(schema + "</A></TH></TR>"); 
                                
                                //Displaying empty if there are no tables
				if(tables.size()<1) writer.println("<TR><TD id=list_td>EMPTY</TD></TR>");
				
                                //Displaying list of tables
                                else {
					for(int j=0;j<tables.size();j++) {
						String tab = tables.elementAt(j).toString();
						//Display table and when clked, opens the structure of table(desctab) 
                                                writer.println(	"<TR><TD id=list_td><A TARGET=right " +
										" HREF='DescTab?table_name=" + tab + "'>");	
                                                
                                                //Displaying 19 tables in a page
						if(tab.length()>21)	writer.print(tab.substring(0,19) + "..");
						else				writer.print(tab); 

						writer.println("</A></TD></TR>");
					}
				}
				writer.println("</TABLE></BODY></HTML>");
			}
		}
		writer.close();
	}
}