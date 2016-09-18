package gnu.sql.tools.sqladmin.setup;

import gnu.sql.tools.sqladmin.*;
import java.util.*;
import java.io.*;

public class Configuration {
	private static Vector lines = new Vector();
	private static Hashtable values = new Hashtable();
	private static String fileName = null;
		
	public static void load(String fname){
		if (fileName!=null) return;
		String s = File.separator;
		String userFileName = System.getProperty("user.home") + s + fname;
		fileName = userFileName;
		Reader r;
		try {
			r = new FileReader(fileName);
		} catch (Exception e){
			SqlAdmin.log("Can't open configuration file: " + e.toString());
			SqlAdmin.log("Reading default configuration file...");
			fileName = fname;
			r = new InputStreamReader(SqlAdmin.class.getResourceAsStream("etc/" + fileName));
		}
		
		if (r==null){
			SqlAdmin.log("no valid configuration files found");
		} else {
			if (read(r)) parse();
		}
		fileName = userFileName;
	}
	
	
	private static boolean read(Reader f){
		int c=0,i=0;
		String line="";
		try{
			BufferedReader br = new BufferedReader(f);
			while ((line = br.readLine())!=null) lines.add(line);
			SqlAdmin.log("Configuration file loaded successfully");
			return true;
		} catch (Exception e){
			SqlAdmin.log("Can't read configuration file: " + e.toString());
			return false;
		}
	}
		
	private static void parse(){
		int p=0;
		String line="";
		String object="";
		for(int i=0;i<lines.size();i++){
			line=(String)lines.elementAt(i);
			if (line.indexOf("[")>=0 && line.indexOf("]")>=0){
				line = line.replace('[',' ');
				line = line.replace(']',' ').trim();
				object=line;
			} else {
				p = line.indexOf("=");
				if (p>=0) {
					values.put(object + "." + line.substring(0,p), line.substring(p+1));
				}
			}
		}
	}
	
	public static Vector getObjects(String type){
		Vector v = new Vector();
		for (Enumeration e = values.keys() ; e.hasMoreElements() ;) {
			Object key = e.nextElement();
		 	if (key.toString().indexOf(".type")>=0) {
				String thisKey = key.toString();
				String thisValue = (String)values.get(thisKey);
				if (thisValue.equals(type)){
					int p = thisKey.indexOf(".");
					v.add(key.toString().substring(0,p));
			 	}
		 	}
		}
		return v;
	}
	
	public static String getValue(String section,String key){
		return (String) values.get(section + "." + key);
	}
	
	public static void configObject(Configurable object){
		Vector v = object.getPropertyNames();
		String section = object.getName();
		for(int i=0; i<v.size();i++){
			String propName = (String)v.elementAt(i);
			object.setProperty(propName, getValue(section, propName));
		}
	}
	
	public static void save(Vector v){
		File oldFile = new File(fileName);
		oldFile.renameTo(new File(fileName + "~"));
		try  {
			FileWriter f = new FileWriter(fileName);
			for(int i=0;i<v.size();i++){
				Configurable conf = (Configurable)v.elementAt(i);
				Vector props = conf.getPropertyNames();
				f.write("[" + conf.getName() + "]\n");
				for(int j=0;j<props.size();j++){
					String propName = props.elementAt(j).toString();
					String value = conf.getProperty(props.elementAt(j).toString());
					if (value!=null) f.write( propName  + "=" + value + "\n");
				}
				f.write("\n");
			}
			f.close();
			SqlAdmin.log("Configuration file written successfully");
		} catch (Exception e) {
			SqlAdmin.log("Can't write configuration file");
		}
	}
}
