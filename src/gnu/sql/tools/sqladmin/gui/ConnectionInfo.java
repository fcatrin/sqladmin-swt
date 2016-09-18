package gnu.sql.tools.sqladmin.gui;

import gnu.sql.tools.sqladmin.db.*;
import gnu.sql.tools.sqladmin.*;
import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class ConnectionInfo extends Dialog {
	String dbName="";
	String dbVersion="";
	String drvName="";
	String drvVersion="";
	Connection con = null;
	
	public ConnectionInfo(Connection con){	
		dbName = con.getProperty(con.PROP_DB_NAME);
		dbVersion = con.getProperty(con.PROP_DB_VERSION);
		drvName = con.getProperty(con.PROP_DRV_NAME);
		drvVersion = con.getProperty(con.PROP_DRV_VERSION);
		this.con = con;
	}
	
	public void open(){
		Shell shell = new Shell(SqlAdmin.display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(con.getName() +" Connection Information");
		
		Vector labels = new Vector();
		labels.add("DataBase Name");
		labels.add("DataBase Version"); 
		labels.add("Driver Name");
		labels.add("Driver Version");
		
		Vector values = new Vector();
		values.add(dbName);
		values.add(dbVersion);
		values.add(drvName);
		values.add(drvVersion);
		
		createPropertyList(shell, labels, values);
		
		shell.pack();
		shell.open();
	}
	
}
