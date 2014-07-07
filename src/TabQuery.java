import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TabQuery extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		String schemaTerm	= null;
		String schema		= null;

		String dbProductName    = null;
		String dbProductVersion = null;

		String tableName = null;

		//Creating session object and retreiving session
                HttpSession session = req.getSession(false);
		res.setContentType("text/html");
		PrintWriter writer  = res.getWriter();
                //Redirecting to login page if session timed out
		if(session == null) {
			writer.println("<HTML>");
			writer.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
							"Sorry Your session expired. Please Login Again.'\"");
			writer.println("</BODY>");
			writer.println("</HTML>");
		}
                
                //Session Exists
		else {
			//Retreiving session attributes
                        dbProductName   	= session.getAttribute("dbProductName").toString();
			dbProductVersion        = session.getAttribute("dbProductVersion").toString();
			schemaTerm		= session.getAttribute("schemaTerm").toString();
			schema			= session.getAttribute("schema").toString();
                        
                        //Retreiving form parameters
			tableName	= req.getParameter("table_name");
			
                        //Displaying page prerequisites
			writer.println("<HTML>");
			writer.println("<HEAD>");
			writer.println("<META NAME='Author' CONTENT='Vamsi'>");
			writer.println("<LINK REL='stylesheet' TYPE='text/css' HREF='styles.css'>");
			writer.println( "<SCRIPT LANGUAGE='javascript' TYPE='text/javascript' " +
							" SRC='script.js'></SCRIPT>");
			writer.println("</HEAD>");
			writer.println(	"<BODY onLoad='loadImages()' BGCOLOR=#ffffff " +
							" link=black alink=black vlink=black>");
			writer.println("<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0>");
			writer.println(	"<TR><TD>&nbsp&nbsp&nbsp&nbsp" +
							"<A HREF='DescTab?table_name=" + tableName + "'>" + 
							"<img onMouseOver='putOn(this,1)' onMouseOut='putOff(this,1)' " +
							"name=pic1 src='pics/structure1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='BrowseForm?table_name=" + tableName + "'>" + 
							"<img onMouseOver='putOn(this,3)' onMouseOut='putOff(this,3)' " +
							"name=pic3 src='pics/browse1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<img name=pic4 src='pics/sql2.jpg' border=0 " +
							"width=80 height=26 align=absbottom>" +
							"<A HREF='InsertForm?table_name=" + tableName + "'>" + 
							"<img onMouseOver='putOn(this,6)' onMouseOut='putOff(this,6)' " +
							"name=pic6 src='pics/insert1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>"+
							"<A HREF='ExportTab?table_name=" + tableName + "'>" +
							"<img onMouseOver='putOn(this,7)' onMouseOut='putOff(this,7)' " +
							"name=pic7 src='pics/export1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='TabOperations?table_name=" + tableName + "'>" +
							"<img onMouseOver='putOn(this,8)' onMouseOut='putOff(this,8)' " +
							"name=pic8 src='pics/operations1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='SearchTab?table_name=" + tableName + "'>" +
							"<img onMouseOver='putOn(this,9)' onMouseOut='putOff(this,9)' "+
							"name=pic9 src='pics/search1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A></TD></TR>" +
							"<TR><TD vAlign=top><IMG SRC='pics/bar.jpg' " +
							" ALIGN=absTop BORDER=0 WIDTH=590 HEIGHT=13>" +
							"</TD></TR></TABLE><BR>");
			writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%>"); 
			writer.println("<TR><TH width=27% id=common_hed>Database Product Name</TH>"); 
			writer.println("<TD width=73% id=common_data>" + dbProductName + "</TD></TR>");
			writer.println("<TR><TH width=27% id=common_hed>Database Product Version</TH>"); 
			writer.println("<TD width=73% id=common_data>" + dbProductVersion + "</TD></TR>");
			writer.println("<TR><TH width=27% id=common_hed>Displayed " + schemaTerm + "</TH>");
			writer.println("<TD width=73% id=common_data>" + schema + "</TD></TR>");
			writer.println("</TABLE><BR>");

			//Table Query Form
                        writer.println("<FORM NAME=query_form METHOD=post ACTION='TabQuery'>");
			writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
			writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=3 BORDER=0 WIDTH=100%" + 
							" STYLE='border-style:double;border-width:1px;border-color:black'>");
			//Text area for query pane
                        writer.println("<TR><TH COLSPAN=2 id=common_th>QUERY PANE</TH></TR>");
			writer.println("<TR><TD COLSPAN=2 STYLE='text-align:center;background:#c5d6df'>");
			writer.println("<TEXTAREA NAME=query COLS=70 ROWS=10 STYLE='background:azure'>");
			writer.println("</TEXTAREA></TD></TR>");
			writer.println("<TR><TD WIDTH=50% STYLE='background:#f5f5f5' ALIGN=right>");
			//Reset option
                        writer.println(	"<IMG NAME=pic11 SRC='pics/reset1.jpg' " +
							" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,11)' " +
							" onMouseUp='putOff(this,11)' onMouseOut='putOff(this,11)' " +
							" onClick='document.query_form.reset()' STYLE='cursor:hand'></TD>");
			//Run option
                        writer.println( "<TD ALIGN=left STYLE='background:#f5f5f5'>" +
							"<IMG NAME=pic21 SRC='pics/run1.jpg' STYLE='cursor:hand'" +
							" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,21)' " +
							" onMouseUp='putOff(this,21)' onMouseOut='putOff(this,21)' " +
							" onClick='submitQueryForm(document.query_form)' ></TD>");
			writer.println("</TR></TABLE></FORM>");

			writer.println("</BODY></HTML>");
		}
		writer.close();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection	connection	= null;
		Statement	statement	= null;
		ResultSet	resultSet	= null;
		ResultSetMetaData metaData = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm	= null;
		String schema		= null;

		String dbProductName    = null;
		String dbProductVersion = null;

		String  error_message = null;
		boolean error_occured = false;

		String query	= null;
		String message	= null;

		String tableName = null;

		boolean isDDLQuery = false;

		boolean resultExists = false;
		int		updateCount  = -1;

		//Creating sesion object and retreiving session
                HttpSession session = req.getSession(false);

		res.setContentType("text/html");
		PrintWriter writer  = res.getWriter();
                
                //Redirecting to login page if session timed out
		if(session == null) {
			writer.println("<HTML>");
			writer.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
							"Sorry Your session expired. Please Login Again.'\"");
			writer.println("</BODY>");
			writer.println("</HTML>");
		}
                
                //Session exists
		else {
                        //Retreiving sesion attributes
			driver			= session.getAttribute("driver").toString();
			url			= session.getAttribute("url").toString();
			userid			= session.getAttribute("userid").toString();		
			pass			= session.getAttribute("pass").toString();
			dbProductName           = session.getAttribute("dbProductName").toString();
			dbProductVersion        = session.getAttribute("dbProductVersion").toString();
			schemaTerm		= session.getAttribute("schemaTerm").toString();
			schema			= session.getAttribute("schema").toString();
                        
                        //Retreiving Form Parameters
			tableName = req.getParameter("table_name");
			query = req.getParameter("query").trim();
			
			try {
				//Loading Driver
                                Class.forName(driver);
                                //Establishing Connection
                                connection	= DriverManager.getConnection(url,userid,pass);
				statement	= connection.createStatement();
			}
			catch(Exception e) {
				//Enabling error_occured if connection failed
                                error_occured = true;
				error_message = e.toString();
			}
                        
                        //Connection successful
			if(!error_occured) {
				try {	
					//Executing query
                                        resultExists = statement.execute(query);	
					//Updating message if executer successfully
                                        message = "Query Executed Successfully.";
                                        
					String queryPrefix = query.substring(0,query.indexOf(" ")).toUpperCase();
					//Determining whether query is DDL query 
                                        if(	queryPrefix.equals("CREATE") || queryPrefix.equals("RENAME") || 
						queryPrefix.equals("DROP"))	isDDLQuery = true;
				}
				catch(Exception e)	{
					//Enabling error_occured if query executin failed
                                        error_occured = true;
					error_message = e.toString();
				}
			}
			
			writer.println("<HTML>");
			writer.println("<HEAD>");
			writer.println("<META NAME='Author' CONTENT='Vamsi'>");
			writer.println("<LINK REL='stylesheet' TYPE='text/css' HREF='styles.css'>");
			writer.println( "<SCRIPT LANGUAGE='javascript' TYPE='text/javascript' " +
							" SRC='script.js'></SCRIPT>");
			writer.println("</HEAD>");
			
                        //Redirecting and listing all database tables if query is DDL
                        if(isDDLQuery) {
					writer.println(	"<BODY BGCOLOR=#ffffff link=black alink=black vlink=black " +
									"onLoad=\"window.parent.left.location.href='ListDB';" +
									"loadImages()\">");
			}
			//Displaying Page requisites
                        else	writer.println(	"<BODY onLoad='loadImages()' BGCOLOR=#ffffff " +
									" link=black alink=black vlink=black>");
			writer.println("<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0>");
			writer.println(	"<TR><TD>&nbsp&nbsp&nbsp&nbsp" +
							"<A HREF='DescTab?table_name=" + tableName + "'>" + 
							"<img onMouseOver='putOn(this,1)' onMouseOut='putOff(this,1)' " +
							"name=pic1 src='pics/structure1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='BrowseForm?table_name=" + tableName + "'>" + 
							"<img onMouseOver='putOn(this,3)' onMouseOut='putOff(this,3)' " +
							"name=pic3 src='pics/browse1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<img name=pic4 src='pics/sql2.jpg' border=0 " +
							"width=80 height=26 align=absbottom>" +
							"<A HREF='InsertForm?table_name=" + tableName + "'>" + 
							"<img onMouseOver='putOn(this,6)' onMouseOut='putOff(this,6)' " +
							"name=pic6 src='pics/insert1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>"+
							"<A HREF='ExportTab?table_name=" + tableName + "'>" +
							"<img onMouseOver='putOn(this,7)' onMouseOut='putOff(this,7)' " +
							"name=pic7 src='pics/export1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='TabOperations?table_name=" + tableName + "'>" +
							"<img onMouseOver='putOn(this,8)' onMouseOut='putOff(this,8)' " +
							"name=pic8 src='pics/operations1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='SearchTab?table_name=" + tableName + "'>" +
							"<img onMouseOver='putOn(this,9)' onMouseOut='putOff(this,9)' "+
							"name=pic9 src='pics/search1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A></TD></TR>" +
							"<TR><TD vAlign=top><IMG SRC='pics/bar.jpg' " +
							" ALIGN=absTop BORDER=0 WIDTH=590 HEIGHT=13>" +
							"</TD></TR></TABLE><BR>");
			writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%>"); 
			writer.println("<TR><TH width=27% id=common_hed>Database Product Name</TH>"); 
			writer.println("<TD width=73% id=common_data>" + dbProductName + "</TD></TR>");
			writer.println("<TR><TH width=27% id=common_hed>Database Product Version</TH>"); 
			writer.println("<TD width=73% id=common_data>" + dbProductVersion + "</TD></TR>");
			writer.println("<TR><TH width=27% id=common_hed>Displayed " + schemaTerm + "</TH>");
			writer.println("<TD width=73% id=common_data>" + schema + "</TD></TR>");
			writer.println("</TABLE><BR>");
                        
                        //Displaying error message if query execution failed
			if(error_occured) {
				writer.println("<HR WIDTH=100%>");
				writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
				writer.println("<TR>");
				writer.println(	"<TH id=insert_err_msg>" + error_message + "</TH>");
				writer.println("</TR>");
				writer.println("</TABLE>");
				writer.println("<HR WIDTH=100%>");
			}
                        
                        //Query executed successfully
			else {
                                //Displaying success message
                                writer.println("<HR WIDTH=100%>");
				writer.println("<TABLE ALIGN=center CELLPADDING=4 BORDER=0 WIDTH=100%");
				writer.println("<TR>");
				writer.println(	"<TH id=insert_norm_msg>" + message + "</TH>");
				writer.println("</TR>");
				writer.println("</TABLE>");
				writer.println("<HR WIDTH=100%>");										

				//Retreive resultset and metadata if results exist
                                if(resultExists) {
					try {	
						resultSet = statement.getResultSet();	
						metaData  = resultSet.getMetaData();
					}
					catch(Exception e)	{	error_occured = true;	}
					
                                        //Display error if resulset retreival failed
					if(error_occured) {
						writer.println("<HR WIDTH=100%>");
						writer.println("<TABLE ALIGN=center CELLPADDING=4 BORDER=0 WIDTH=100%");
						writer.println("<TR>");
						writer.println(	"<TH id=insert_err_msg>$RESULT_READ_ERROR$</TH>");
						writer.println("</TR>");
						writer.println("</TABLE>");
						writer.println("<HR WIDTH=100%>");
					}
                                        else {
						int columnCount = 0;
						try {	columnCount = metaData.getColumnCount();	}
						catch(Exception e)	{	columnCount = 0;	}
						
						writer.println("<BR><DIV id=common_div align=center>");
						writer.println("<TABLE WIDTH=100% BORDER=0 CELLSPACING=1 CELLPADDING=4>");
						if(columnCount > 0)	writer.println(	"<TR><TH COLSPAN=" + columnCount + 
															" id=common_th>QUERY RESULTS</TH></TR>");
						else	writer.println("<TR><TH id=common_th>RESULTSET IS EMPTY</TH></TR>"); 
						
						writer.println("<TR>");
						//Displaying Column Names
                                                for(int i=0;i<columnCount;i++)	{
							String colname = null;
							try {	colname = metaData.getColumnName(i+1);	}
							catch(Exception e)	{	colname = "ERROR";	}
							writer.println("<TH id=props_subhed>" + colname + "</TH>");
						}
						writer.println("</TR>");
						
						//Displaying Column Data
                                                try {
                                                            //Iterating Resultset to obtain column data
                                                            while(resultSet.next()) {
								writer.println("<TR>");
								for(int i=0;i<columnCount;i++)	{
									String data = null;
									int    type = -1;

									type = metaData.getColumnType(i+1);	

									switch(type) {
										case Types.LONGVARBINARY:
										case Types.LONGVARCHAR	:
										case Types.VARBINARY	:
										case Types.BINARY		:
										case Types.BLOB			:
										case Types.CLOB			:	data = "$LARGE_OBJECT$"; break;
										default			:	data = resultSet.getString(i+1);
									}

									writer.println("<TD id=common_td>" + data + "</TD>");
								}
								writer.println("</TR>");
							}
						}
						catch(Exception e) {}
						writer.println("</TABLE></DIV>");
					}
				}
                                //Displaying number of rows updated if query is insert or update
				else {
					writer.println("<HR WIDTH=100%>");
					writer.println("<TABLE ALIGN=center CELLPADDING=4 BORDER=0 WIDTH=100%");
					try {
						updateCount = statement.getUpdateCount();
						writer.println(	"<TR><TH id=insert_norm_msg>Update Count ::: " + 
										updateCount + "</TH></TR>");
					}
					catch(Exception e) { 
						writer.println(	"<TR><TH id=insert_err_msg>$DATABASE_ACCESS_ERROR$</TH></TR>");
					}
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%>");										
				}				
			}
                        
                        //Displaying TabQuery Form
			writer.println("<FORM NAME=query_form METHOD=post ACTION='TabQuery'>");
			writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
			writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=3 BORDER=0 WIDTH=100%" + 
							" STYLE='border-style:double;border-width:1px;border-color:black'>");
			//Query Pane
                        writer.println("<TR><TH COLSPAN=2 id=common_th>QUERY PANE</TH></TR>");
			writer.println("<TR><TD COLSPAN=2 STYLE='text-align:center;background:#c5d6df'>");
			writer.println("<TEXTAREA NAME=query COLS=70 ROWS=10 STYLE='background:azure'>");
			writer.println(query + "</TEXTAREA></TD></TR>");
			writer.println("<TR><TD WIDTH=50% STYLE='background:#f5f5f5' ALIGN=right>");
			writer.println(	"<IMG NAME=pic11 SRC='pics/reset1.jpg' " +
							" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,11)' " +
							" onMouseUp='putOff(this,11)' onMouseOut='putOff(this,11)' " +
							" onClick='document.query_form.reset()' STYLE='cursor:hand'></TD>");
			writer.println( "<TD ALIGN=left STYLE='background:#f5f5f5'>" +
							"<IMG NAME=pic21 SRC='pics/run1.jpg' STYLE='cursor:hand'" +
							" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,21)' " +
							" onMouseUp='putOff(this,21)' onMouseOut='putOff(this,21)' " +
							" onClick='submitQueryForm(document.query_form)' ></TD>");
			writer.println("</TR></TABLE></FORM>");

			writer.println("</BODY></HTML>");
		}
		writer.close();
	}
}