import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;

public class InsertLobs extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		ResultSet  resultSet  = null;
		DatabaseMetaData dbMetaData = null;
		PreparedStatement statement = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm = null;
		String schema	  = null;
		int schemaValue	  = 0;

		String dbProductName    = null;
		String dbProductVersion = null;

		String  error_message = null;
		boolean error_occured = false;

		String tableName  = null;

		int emptyCount	  = 0;		
		int columnCount   = 0;
		int insertedCount = 0;

		Vector colNames  = new Vector();
		Vector colTypes  = new Vector();
		Vector typeName  = new Vector();
		Vector colSizes  = new Vector();
		Vector decimals  = new Vector();
		Vector Nullable	 = new Vector();
		Vector rowStatus = new Vector();
		Vector rowData	 = new Vector();
		Vector cnames    = new Vector();

		boolean rowOk	= true;

		DiskFileUpload upload = null;			
                List items            = null;

		//Creating session object and retreiving session
                HttpSession session	  = req.getSession(false);

		res.setContentType("text/html");
		PrintWriter writer = res.getWriter();
                 //Redirecting to Login page if session Timed Out
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
			driver			= session.getAttribute("driver").toString();
			url			= session.getAttribute("url").toString();
			userid			= session.getAttribute("userid").toString();		
			pass			= session.getAttribute("pass").toString();
			dbProductName   	= session.getAttribute("dbProductName").toString();
			dbProductVersion        = session.getAttribute("dbProductVersion").toString();
			schemaTerm		= session.getAttribute("schemaTerm").toString();
			schemaValue		= Integer.parseInt(session.getAttribute("schemaValue").toString());
			schema			= session.getAttribute("schema").toString();
                        
			//Creating an object of DiskFileUpload and parsing a request for upload
                        try {
				upload = new DiskFileUpload();			
                                items  = upload.parseRequest(req);
			}
			catch(FileUploadException fue)	{
				//Enable error_occured if parsing request failed
                                error_occured = true;
				error_message = fue.toString();
			}
                        
                        //Request successfully parsed
			if(!error_occured)	{
                        	int index     = 0;
				//Creating an iterator to access list items
                                Iterator iter = items.iterator();
                                //Iterating list
                                    while (iter.hasNext()) {
                                        //Retreiving file item from list of items
                                        FileItem item = (FileItem) iter.next();
					//Retreiving file name
                                        String fname = item.getFieldName();					
					
                                        //Form Field Items
					if(item.isFormField())	{
						//Retreiving value of a form field item
                                                String value = item.getString();
						//Form field as a table
                                                if(fname.equals("table_name"))	{
							tableName = value;
							try {
								//Loading driver
                                                                Class.forName(driver);
								//Establishing Connection
                                                                connection	= DriverManager.getConnection(url,userid,pass);
								//Retreiving metadata information
                                                                dbMetaData	= connection.getMetaData();

								//Updating result set with column information
                                                                if(schemaValue == 0) 
									 resultSet = dbMetaData.getColumns(schema,null,tableName,null);
								else resultSet = dbMetaData.getColumns(null,schema,tableName,null);
                                                                
                                                                //Iterating result set to retreive column information 
								while(resultSet.next()) {
									colNames.add(resultSet.getString(4));
									colTypes.add(new Integer(resultSet.getString(5)));
									typeName.add(resultSet.getString(6));
									colSizes.add(new Long(resultSet.getLong(7)));
									decimals.add(new Integer(resultSet.getInt(9)));
									Nullable.add(new Integer(resultSet.getInt(11)));
								}
							}
							catch(Exception e)	{
								error_occured = true;
								error_message = e.toString();
							}
                                                        
                                                        //Closing resultset
							try {
								if(resultSet != null) resultSet.close();
								connection.close();
							}
							catch(Exception e)	{	e.printStackTrace();	}
						}
                                                //Form Field as a column
						else {
							index		 = Integer.parseInt(fname);
							int type	 = Integer.parseInt(colTypes.elementAt(index).toString());
							int nullable = Integer.parseInt(Nullable.elementAt(index).toString());
							boolean isOk = true;

                                                        //Validating column if it is nullable
							if(value.equalsIgnoreCase("null") && nullable != 0) isOk=true;
							else {
								 //Validating column type if it is not nullable
                                                                switch(type) {
									case Types.BIT		:	isOk = Validate.isBit(value);		break;
									case Types.SMALLINT :
									case Types.TINYINT	:	isOk = Validate.isShort(value);		break;
									case Types.INTEGER	:	isOk = Validate.isInteger(value);	break;
									case Types.BIGINT	:	isOk = Validate.isLong(value);		break;
									case Types.NUMERIC	:
									case Types.DECIMAL	:	isOk = Validate.isBigDecimal(value);break;
									case Types.REAL		:	isOk = Validate.isFloat(value);		break;
									case Types.DOUBLE	:
									case Types.FLOAT	:	isOk = Validate.isDouble(value);	break;
									case Types.DATE		:
									case Types.TIME		:
									case Types.TIMESTAMP:	isOk = Validate.isDate(value);		break;
									case Types.CHAR		:
									case Types.VARCHAR	:	isOk = true;						break;
								}
							}

							if(value.equals("") && nullable!= 0)	isOk = true;
							//Validating row if column data is valid
                                                        if(!isOk)  {
								rowOk = false;
								rowStatus.add("false");
							}
							else rowStatus.add("true");

							Vector data = new Vector();
							data.add(value);
							rowData.add(data);
							cnames.add(colNames.elementAt(index));
						}
					}
                                        
                                        //Non form field items
					else {
						index		= Integer.parseInt(fname);
						int type	= Integer.parseInt(colTypes.elementAt(index).toString());
						int nullable= Integer.parseInt(Nullable.elementAt(index).toString());
						boolean isOk= true;
						
						switch(type) {
							case Types.LONGVARBINARY:
							case Types.LONGVARCHAR	:
							case Types.VARBINARY	:
							case Types.BINARY		:
							case Types.BLOB			:
							case Types.CLOB			:	isOk = true;	break;
							default					:	isOk = false;
						}

						if(nullable != 0) isOk = true;
						else			  isOk = false;

						//Updating LobData
                                                try {
							Vector lobData = new Vector();
							long size = item.getSize();
							if(size > 0) {
								lobData.add(item.getName());
								lobData.add(new Long(size));
								lobData.add(item.getInputStream());
								rowData.add(lobData);	
								isOk = true;
							}
							else throw new IOException();
						}
                                                
                                                catch(IOException e) {	
							Vector data = new Vector();
							data.add(item.getName());
							rowData.add(data);
							isOk = false;	
						}
                                                
						if(!isOk)  {
							rowOk = false;
							rowStatus.add("false");
						}
						else rowStatus.add("true");

						cnames.add(colNames.elementAt(index));
					}
				}
				
                                //Inserting Lob Data into table
				if(rowOk) {
					String colPart	= " (";
					String dataPart	= " values (";
					columnCount		= cnames.size();
					Vector lobIndex = new Vector();
                                        
                                        //Iterating Columns of row
					for(int i=0;i<columnCount;i++)	{
						String colname = cnames.elementAt(i).toString();
						String coldata = ((Vector) rowData.elementAt(i)).elementAt(0).toString();
						int type = Integer.parseInt(colTypes.elementAt(i).toString());

						if(!coldata.equals("")) {
							colPart	 += colname + ",";
                                                        
                                                        //Retreiving type of date to be inserted
							switch(type) {
								case Types.BIT			:	
								case Types.SMALLINT		:
								case Types.TINYINT		:	
								case Types.INTEGER		:	
								case Types.BIGINT		:	
								case Types.NUMERIC		:
								case Types.DECIMAL		:	
								case Types.REAL			:	
								case Types.DOUBLE		:
								case Types.FLOAT		:	dataPart += coldata + ",";	break;
								case Types.DATE			:
								case Types.TIME			:
								case Types.TIMESTAMP	:	
								case Types.CHAR			:
								case Types.VARCHAR		:	dataPart += "'" + coldata + "',"; break;
								case Types.BLOB			:
								case Types.CLOB			:
								case Types.BINARY		:
								case Types.VARBINARY	:
								case Types.LONGVARCHAR	:
								case Types.LONGVARBINARY:	dataPart += "?,";	
															lobIndex.add(new Integer(i));
															break;
							}
						}
						else emptyCount++;
					}
                                        
                                        //Inserting Data into table
					if(emptyCount < columnCount) {
						colPart  = colPart.substring(0,colPart.length()-1) + ")";
						dataPart = dataPart.substring(0,dataPart.length()-1) + ")";
						//Creating insert query
                                                String insertQuery = "insert into " + tableName + colPart + dataPart;
						
						try {
							//Establishing a connection
                                                        connection	= DriverManager.getConnection(url,userid,pass);
							statement	= connection.prepareStatement(insertQuery);
							for(int i=0;i<lobIndex.size();i++)	{
								int lobindex   = Integer.parseInt(lobIndex.elementAt(i).toString());
								Vector lobData = (Vector) rowData.elementAt(lobindex);
								int size	   = Integer.parseInt(lobData.elementAt(1).toString());
								InputStream is = (InputStream) lobData.elementAt(2);
								statement.setBinaryStream(i+1,is,size);
							}
                                                        //Retreiving number of rows inserted
							insertedCount = statement.executeUpdate();
						}
						catch(SQLException se)	{	insertedCount = -1;	}
					}
				}
			}
                        
                        //Displaying error message 
			if(error_occured) {
				writer.println("<HTML><HEAD><H3>" + error_message + "</H3></HEAD></HTML>");
			}
                        
                        //Inserting successful
			else {
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
								"<A HREF='TabQuery?table_name=" + tableName + "'>" + 
								"<img onMouseOver='putOn(this,4)' onMouseOut='putOff(this,4)' " +
								"name=pic4 src='pics/sql1.jpg' border=0 " +
								"width=80 height=26 align=absbottom></A>" +
								"<img name=pic6 src='pics/insert2.jpg' border=0 " +
								"width=80 height=26 align=absbottom>"+
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
				writer.println("<TR><TH width=27% id=common_hed>Displayed Table" + "</TH>");
				writer.println("<TD width=73% id=common_data>" + tableName + "</TD></TR>");
				writer.println("</TABLE><BR>");				
                                
                                //Displaying success message for single inserted row
				if(insertedCount == 1) {
					writer.println("<HR width=100%>");
					writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
					writer.println("<TR>");
					writer.println("<TH id=insert_norm_msg>Row Inserted Successfully</TH>");
					writer.println("</TR>");
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%>");
				}
                                //Displaying error message if insertion caused errors
				else if(insertedCount == -1) {
					writer.println("<HR WIDTH=100%>");
					writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
					writer.println("<TR>");
					writer.println("<TH id=insert_err_msg>ERROR OCCURED DURING QUERY EXECUTION</TH>");
					writer.println("</TR>");
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%>");
				}
                                //Displaying error message if invalid data is found
				else if(insertedCount == 0) {
					writer.println("<HR WIDTH=100%>");
					writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
					writer.println("<TR>");
					writer.println("<TH id=insert_err_msg>INVALID DATA FOUND IN FORM FIELDS</TH>");
					writer.println("</TR>");
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%>");
				}
                                
                                //Displaying InsertLob Form page
				writer.println(	"<FORM NAME=insert_form ACTION='InsertLobs' " +
								" METHOD=post ENCTYPE='multipart/form-data'>");
				writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
				
				writer.println(	"<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=1 BORDER=0 " +
								"style='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR>");
				writer.println("<TH id=insert_th WIDTH=25%>COLUMN NAME</TH>");
				writer.println("<TH id=insert_th WIDTH=25%>DATA TYPE</TH>");
				writer.println("<TH id=insert_th WIDTH=50%>VALUE</TH>");
				writer.println("</TR>");

				for(int i=0;i<colNames.size();i++)	{
					String colName	= colNames.elementAt(i).toString();
					String typename = typeName.elementAt(i).toString();
					long maxlength  = Long.parseLong(colSizes.elementAt(i).toString());
					int	 colType	= Integer.parseInt(colTypes.elementAt(i).toString());
					int  decimal	= Integer.parseInt(decimals.elementAt(i).toString());					
	
					writer.println("<TR>");
					writer.println(	"<TD id=insert_td STYLE='font-weight:bold'>" + 
									colName + "</TD>");
					writer.print(	"<TD id=insert_td >" + typename + "[" + maxlength);
					
					if(decimal > 0)	writer.println("," + decimal + "]</TD>");
					else			writer.println("]</TD>");
			
					String inputType = "text";
					switch(colType) {
						case Types.LONGVARBINARY:
						case Types.LONGVARCHAR	:
						case Types.VARBINARY	:
						case Types.BINARY		:
						case Types.BLOB			:
						case Types.CLOB			:	inputType = "file";	break;
						default					:	inputType = "text";	break;
					}
					
					String style = null;
					String value = null;
					if(rowOk) {
						style = "insert_norm_inp";
						value = "";
					}
					else {
						String status = rowStatus.elementAt(i).toString();
						if(status.equals("true"))	style = "insert_norm_inp";
						else						style = "insert_error_inp";
						
						value = ((Vector)rowData.elementAt(i)).elementAt(0).toString();
					}

					writer.println(	"<TD id=insert_td><INPUT TYPE=" + inputType + 
									" id=" + style + " VALUE='" + value + "' STYLE='WIDTH=100%' " +
									"NAME=" + i + " MAXLENGTH=" + maxlength +	"></TD>");
					writer.println("</TR>");
				}
				writer.println("</TABLE><BR>");
				writer.println("</FORM>");

				writer.println("<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0>");
				writer.println("<TR>");
				writer.println(	"<TD WIDTH=20% ALIGN=left><IMG SRC='pics/go1.jpg' " +
								"NAME=go1 WIDTH=30 HEIGHT=30 BORDER=0 STYLE='cursor:hand' " +
								"onMouseOut='putOff(this,13)' onMouseUp='putOff(this,13)' " +
								"onMouseDown='putOn(this,13)' onClick='insert_form.submit()'>" +
								"</TD>");
				writer.println("<TD WIDTH=60%></TD>");
				writer.println(	"<TD WIDTH=20% ALIGN=right><IMG SRC='pics/go1.jpg' " +
								"NAME=go2 WIDTH=30 HEIGHT=30 BORDER=0 STYLE='cursor:hand' " +
								"onMouseOut='putOff(this,13)' onMouseUp='putOff(this,13)' " +
								"onMouseDown='putOn(this,13)' onClick='insert_form.submit()'>" +
								"</TD>");
				writer.println("</TR>");
				writer.println("</TABLE>");

				writer.println("</BODY></HTML>");
			}
		}
		writer.close();
	}
}