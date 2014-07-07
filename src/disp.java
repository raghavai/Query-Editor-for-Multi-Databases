/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Vector;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author karthik
 */
public class disp extends HttpServlet {
   
    /** 
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
    Connection connection = null;
		ResultSet  resultSet  = null;
		DatabaseMetaData dbMetaData = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm = null;
		String schema	  = null;
		int schemaValue	  = 0;

		final int rowCount = 12;

		String dbProductName    = null;
		String dbProductVersion = null;

		String  error_message = null;
		boolean error_occured = false;

		boolean lobExists = false;

		String tableName = null;

		Vector colNames = new Vector();
		Vector colTypes = new Vector();
		Vector typeName = new Vector();
		Vector colSizes = new Vector();
		Vector decimals = new Vector();

		//Creating session object and retreiving session
                HttpSession session = req.getSession(false);
		res.setContentType("text/html");
		PrintWriter writer = res.getWriter();
        
                if(session == null) {
			writer.println("<HTML>");
			writer.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
							"Sorry Your session expired. Please Login Again.'\"");
			writer.println("</BODY>");
			writer.println("</HTML>");
		}
                else {
			
                        //Retreiving Session Attributes
                        driver			= session.getAttribute("driver").toString();
			url			= session.getAttribute("url").toString();
			userid			= session.getAttribute("userid").toString();		
			pass			= session.getAttribute("pass").toString();
			dbProductName           = session.getAttribute("dbProductName").toString();
			dbProductVersion        = session.getAttribute("dbProductVersion").toString();
			schemaTerm		= session.getAttribute("schemaTerm").toString();
			schemaValue		= Integer.parseInt(session.getAttribute("schemaValue").toString());
			schema			= session.getAttribute("schema").toString();
			
                        //Retreiving Form Parameters
                        tableName		= req.getParameter("table_name");

			try {
				//Loading Driver
                                Class.forName(driver);
                                
                                //Establishing Connection
				connection	= DriverManager.getConnection(url,userid,pass);
                                //Retreiving metadata
				dbMetaData	= connection.getMetaData();
			}
			catch(Exception e) {
				//Enabling error occured if connection failed
                                error_occured = true;
				error_message = e.toString();
			}

			//Connection Successful
                        if(!error_occured) {
                
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
								"<A  target=right HREF='DescTab?table_name=" + tableName + "'>" + 
								"<img onMouseOver='putOn(this,1)' onMouseOut='putOff(this,1)' " +
								"name=pic1 src='pics/structure1.jpg' border=0 " +
								"width=80 height=26 align=absbottom></A>" +
								"<A target=top HREF='BrowseForm?table_name=" + tableName + "'>" + 
								"<img onMouseOver='putOn(this,3)' onMouseOut='putOff(this,3)' " +
								"name=pic3 src='pics/browse1.jpg' border=0 " +
								"width=80 height=26 align=absbottom></A>" +
								"<A target= right HREF='TabQuery?table_name=" + tableName + "'>" + 
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
				//writer.println("<TR><TH width=27% id=common_hed>Displayed Table" + "</TH>");
				//writer.println("<TD width=73% id=common_data>" + tableName + "</TD></TR>");
				writer.println("</TABLE><BR>");
                                
                                writer.close();
        
                             } 
            }
    }           
}