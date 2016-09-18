package gnu.sql.tools.sqladmin.events;

import java.util.*;
import org.eclipse.swt.events.*;

public class SelectionHandler implements SelectionListener {
	public static String NAME = "name";
	private Hashtable properties = null;
	private IEventHandler evh = null;
	
	public SelectionHandler(IEventHandler evh, Hashtable properties){
		this.properties = (Hashtable)properties.clone();
		this.evh = evh;
	}
	
	public Object getProperty(String name){
		return properties.get(name);
	}
	
	public void setProperty(String name, Object value){
		properties.put(name, value);
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {};
	public void widgetSelected(SelectionEvent e) {
		evh.handleEvent(properties);
	}
	
}
