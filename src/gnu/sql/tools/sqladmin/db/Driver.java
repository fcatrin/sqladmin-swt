package gnu.sql.tools.sqladmin.db;

import gnu.sql.tools.sqladmin.*;
import gnu.sql.tools.sqladmin.setup.*;
import java.util.*;


public class Driver extends Configurable {
	
	public static final String PROP_CLASS = "class";
	public static final String PROP_URL = "url";
	public static final String PROP_SERVER = "server";
	public static final String PROP_PORT= "port";
	public static final String PROP_DB = "database";

	public String status="";
	public boolean loaded = false;
	
	protected Vector connections = new Vector(); 
	protected java.sql.Driver drv = null;
	
	public Driver(String name){
		super(name);
		confType="driver";
	}
		
	public Vector getPropertyNames(){
		Vector v = super.getPropertyNames();
		v.add(PROP_SERVER);
		v.add(PROP_PORT);
		v.add(PROP_DB);
		v.add(PROP_URL);
		v.add(PROP_CLASS);
		return v;
	}
	
	boolean load(){
		try {
			drv = DriverManager.getJDBCDriver(getProperty(PROP_CLASS));
			status="JDBC Driver " + name + " loaded successfully";
			loaded = true;
		} catch (Exception e) {
			status="Can't load JDBC Driver " + name + ". " + e.toString();
			loaded = false;
		}
		return loaded;
	}
	
	public void addConnection(Connection con){
		connections.add(con);
	}

	public boolean isEmpty(){
		return connections.isEmpty();
	}
	
	public void removeConnection(Connection con){
		connections.remove(con);	
	}
	
	public void updateConnections(){
		for (int i=0;i<connections.size();i++){
			Connection con = (Connection)connections.elementAt(i);
			con.setProperty(Connection.PROP_DRIVER, getName());
			con.updateDriver();
		}
	}
	public boolean isValid(){
		load();
		return loaded;
	}
	
	public java.sql.Driver getJDBCDriver(){
		return drv;
	}
}
