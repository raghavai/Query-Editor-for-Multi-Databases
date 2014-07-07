/* Used For 
 *      validating data types */

import java.sql.*;
import java.lang.*;
import java.util.*;
import java.math.*;

public final class Validate {

	
        //validating Bit type
        public static boolean isBit(String value)	{
		if(value.equals("0") || value.equals("1")) return true;
		else return false;
	}
        //validating Short type
	public static boolean isShort(String value) {
		try	{	new Short(value);	return true;	}
		catch(NumberFormatException e)	{	return false;	}
	}
        //validating Integer type
	public static boolean isInteger(String value)	{
		try {	new Integer(value);	return true;	}
		catch(NumberFormatException e)	{	return false;	}
	}
        //validating Float type
	public static boolean isFloat(String value)	{
		try {	new Float(value);	return true;	}
		catch(NumberFormatException e)	{	return false;	}
	}
        //validating Double type
	public static boolean isDouble(String value)	{
		try {	new Double(value);	return true;	}
		catch(NumberFormatException e)	{	return false;	}
	}
        //validating Long type
	public static boolean isLong(String value)	{
		try	{	new Long(value);	return true;	}
		catch(NumberFormatException e)	{	return false;	}
	}
        //validating BigDecimal type
	public static boolean isBigDecimal(String value)	{
		try	{	new BigDecimal(value);	return true;	}
		catch(NumberFormatException e)	{	return false;	}		
	}
        //validating Date type
	public static boolean isDate(String value)	{
		try {	java.util.Date.parse(value);	return true;	}
		catch(IllegalArgumentException e)	{	return false;	}
	}
}