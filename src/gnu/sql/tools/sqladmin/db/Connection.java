package gnu.sql.tools.sqladmin.db;


import gnu.sql.tools.sqladmin.*;
import gnu.sql.tools.sqladmin.setup.*;
import gnu.sql.tools.sqladmin.gui.*;
import java.sql.*;
import java.util.*;

public class Connection extends Configurable {
	public static final String PROP_SERVER = "server";
	public static final String PROP_PORT = "port";
	public static final String PROP_DB = "database";
	public static final String PROP_UID = "uid";
	public static final String PROP_PWD = "pwd";
	public static final String PROP_SURL = "specificURL";
	public static final String PROP_DRIVER = "driver";
	public static final String PROP_USE_URL = "useURL";
	public static final String PROP_DB_NAME = "dbName";
	public static final String PROP_DB_VERSION = "dbVersion";
	public static final String PROP_DRV_NAME = "drvName";
	public static final String PROP_DRV_VERSION = "drvVersion";

	protected String url = "";
	public java.sql.Connection db = null;
	public boolean connected = false;
	public boolean tried = false;
	protected Driver drv=null;

	public Connection(String name){
		super(name);
		confType = "connection";
		this.name=name;
	}
	
	public void updateDriver(){
		if (drv!=null) drv.removeConnection(this);
		drv = DriverManager.getDriver(getProperty(PROP_DRIVER));
		drv.addConnection(this);
		setDefaultProperty(PROP_SERVER,drv.getProperty(Driver.PROP_SERVER));
		setDefaultProperty(PROP_PORT ,drv.getProperty(Driver.PROP_PORT));
		setDefaultProperty(PROP_DB, drv.getProperty(Driver.PROP_DB));
		url = expandURL();
	}
	
	public Vector getPropertyNames(){
		Vector v = super.getPropertyNames();
		v.add(PROP_SERVER);
		v.add(PROP_PORT);
		v.add(PROP_DB);
		v.add(PROP_SURL);
		v.add(PROP_USE_URL);
		v.add(PROP_UID);
		v.add(PROP_PWD);
		v.add(PROP_DRIVER);
		return v;
	}
	
	public String toString(){
		return name;
	}
	
	String urlReplace(StringBuffer s, String token, String param){
		String aux = s.toString();
		int p = aux.indexOf(token);
		if (p>=0) s.replace(p, p+2, param);
		return s.toString();
	}
		
	public String expandURL(){
		String aux = drv.getProperty(Driver.PROP_URL);
		aux = urlReplace(new StringBuffer(aux),"%s",getProperty(PROP_SERVER));
		aux = urlReplace(new StringBuffer(aux),"%p",getProperty(PROP_PORT));
		aux = urlReplace(new StringBuffer(aux),"%d",getProperty(PROP_DB));
		return aux;
	}
		
	public boolean connect(){
		if (connected) return true;
		if (tried) return false;
		SqlAdmin.log("Connecting to " + name + "...");
		tried=true;
		try { 
			Properties props = new Properties();
			props.put("user", getProperty(PROP_UID));
			props.put("password", getProperty(PROP_PWD));
			
			java.sql.Driver jdbcDriver = drv.getJDBCDriver();
			db = jdbcDriver.connect(url,props);
			connected = true;
			SqlAdmin.log("Connected to " + name);
			getConnectionInfo();
			return true;
		} catch (Exception e){
			SqlAdmin.log("Can't connect to " + name + ": " + e.toString());
			return false;
		}
	}
	
	public void disconnect(){
		tried = false;
		if (!connected) return;
		try {
			if (db!=null) db.close();
			connected = false;
		} catch (Exception e){
			e.printStackTrace();
		}			
	}
	
	public boolean setup(){
		ConnectionDialog dlg = new ConnectionDialog(this);
		dlg.open();
		return (dlg.con!=null);
	}
	
	private void getConnectionInfo(){
		try{
			DatabaseMetaData dbmd = db.getMetaData();
			setProperty(PROP_DB_NAME, dbmd.getDatabaseProductName());
			setProperty(PROP_DB_VERSION, dbmd.getDatabaseProductVersion());
			setProperty(PROP_DRV_NAME, dbmd.getDriverName());
			setProperty(PROP_DRV_VERSION, dbmd.getDriverVersion());
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
