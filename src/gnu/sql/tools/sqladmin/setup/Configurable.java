package gnu.sql.tools.sqladmin.setup;

import java.util.*;

public class Configurable {
	Hashtable properties = new Hashtable();
	protected String confType = null;
	protected String name = null;
	
	public Configurable(String name){
		setName(name);
	}

	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public Vector getPropertyNames(){
		Vector v = new Vector();
		v.add("type");
		return v;
	}
	
	public Hashtable getProperties(){
		return properties;
	}
	
	public void setProperties(Hashtable properties){
		this.properties = properties;
	}
	
	public String getProperty(String name){
		if (name.equals("type")) return confType;
		return (String)properties.get(name);
	}

	public void setProperty(String name, Object value){
		if (value!=null) properties.put(name, value);
	}
	
	public void setDefaultProperty(String name, Object value){
		if (value==null) return;
		String prop = getProperty(name);
		if (prop==null || prop.trim().equals("")) properties.put(name, value);
	}
	
}
