package gnu.sql.tools.sqladmin.db;

import gnu.sql.tools.sqladmin.*;
import gnu.sql.tools.sqladmin.setup.*;
import gnu.sql.tools.sqladmin.gui.*;
import java.util.*;
import java.net.URL;
import java.net.URLClassLoader;

public class DriverManager extends Configurable{
	public static final String PROP_URLS = "urls";

	static protected Hashtable drivers = new Hashtable();
	protected static URLClassLoader classLoader = null;
	protected static Vector urls;
	
	public DriverManager(){
		super("DriverManager");
		confType = "driverManager";
		Configuration.configObject(this);
		createClassLoader();
	}
	
	public static void loadFromConfig(){
		Vector v=Configuration.getObjects("driver");
		String section="";
		for (int i=0;i<v.size();i++) {
			section = (String)v.elementAt(i);
			Driver drv = new Driver(section);
			Configuration.configObject(drv);
			addDriver(drv);
		}
	}
	
	protected void createClassLoader(){
		if (classLoader==null) {
			try {
				URL aurl[] = new URL[urls.size()];
				for(int i=0; i<urls.size(); i++){
					aurl[i] = new URL("file://" + urls.elementAt(i).toString());
				}
				classLoader = new URLClassLoader(aurl);
			} catch (Exception e) {
				SqlAdmin.log(e.toString());
			}
		}
	}
	
	public void addURL(String url){
		if (urls.indexOf(url)<0) urls.addElement(url);
	}
	
	public String getProperty(String name){
		if (!name.equals(PROP_URLS)) return super.getProperty(name);
		String urlList = "";
		for(int i=0;i<urls.size();i++){
			if (i>0) urlList = urlList + ":";
			urlList = urlList + urls.elementAt(i).toString();
		}
		return urlList;
	}
	
	public void setProperty(String name, Object value){
		if (!name.equals(PROP_URLS)) {
			super.setProperty(name, value);
			return;
		}
		urls = new Vector();
		if (value==null) return;
		StringTokenizer st = new StringTokenizer(value.toString(), ":");
		while (st.hasMoreTokens()){
			urls.addElement(st.nextToken());
		}
	}	
	
	public Vector getPropertyNames(){
		Vector v = super.getPropertyNames();
		v.add(PROP_URLS);
		return v;
	}
	
	public static void removeDriver(Driver drv){
		drivers.remove(drv.getName());
		SqlAdmin.log("Removed driver " + drv.getName() + " from configuration");
	}
	
	public static boolean addDriver(Driver drv){
		drivers.put(drv.getName(), drv);
		return loadDriver(drv);
	}

	public static boolean loadDrivers() {
		boolean retValue=true;
		Enumeration e = drivers.elements();
		while (e.hasMoreElements()) {
			Driver drv = (Driver)e.nextElement();
			retValue &= loadDriver(drv);
		}
		return retValue;
	}
	
	public static java.sql.Driver getJDBCDriver(String driverName) throws Exception {
		Class drvClass = classLoader.loadClass(driverName);
		return (java.sql.Driver)drvClass.newInstance();
	}
	
	protected static boolean loadDriver(Driver drv){
		if (drv.loaded) return true;
		boolean retValue = drv.load();
		SqlAdmin.log(drv.status);
		return retValue;
	}
	
	public static Driver getDriver(String name){
		return (Driver)drivers.get(name);
	}		
	
	public static Vector getDrivers(){
		Vector retValue = new Vector();
		Enumeration e = drivers.elements();
		while (e.hasMoreElements()) {
			Driver drv = (Driver)e.nextElement();
			retValue.addElement(drv);
		}
		return retValue;
	}
	
	public static void setup() {
		DriverDialog dlg = new DriverDialog();
		dlg.open();
	}

	public static void setupClassPath() {
		DriverManagerDialog dlg = new DriverManagerDialog(urls);
		if (dlg.open()) {
			urls = dlg.getItems();
			GUIUtil.message("Changes will take efect the next time SQLAdmin starts");
		}
		SqlAdmin.saveConfiguration();
	}
}
