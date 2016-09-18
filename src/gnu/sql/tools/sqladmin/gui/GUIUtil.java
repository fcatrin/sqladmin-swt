package gnu.sql.tools.sqladmin.gui;

import gnu.sql.tools.sqladmin.*;
import gnu.sql.tools.sqladmin.events.*;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;

import java.util.*;

public class GUIUtil {
	public static Image getIcon(String name){
		return new Image(SqlAdmin.display, SqlAdmin.class.getResourceAsStream("images/" + name));
	}
	
	public static boolean query(String text){
		MessageBox msg = new MessageBox(SqlAdmin.shell,SWT.APPLICATION_MODAL | SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		msg.setText(SqlAdmin.shortName);
		msg.setMessage(text);
		return (msg.open()==SWT.OK);
	}
	public static void alert(String text){
		MessageBox msg = new MessageBox(SqlAdmin.shell,SWT.APPLICATION_MODAL | SWT.ICON_WARNING | SWT.OK);
		msg.setText(SqlAdmin.name);
		msg.setMessage(text);
		msg.open();
	}
	public static void message(String text){
		MessageBox msg = new MessageBox(SqlAdmin.shell,SWT.APPLICATION_MODAL | SWT.ICON_INFORMATION | SWT.OK);
		msg.setText(SqlAdmin.name);
		msg.setMessage(text);
		msg.open();
	}
	
	public static MenuItem cascadeMenuItem(Menu parent, IEventHandler evh, int evId, String text, Hashtable event){
		MenuItem item = cascadeMenuItem(parent, text);
		event.put(SqlAdmin.EV_ID,new Integer(evId));
		item.addSelectionListener(new SelectionHandler(evh, event));
		return item;
	}

	public static MenuItem cascadeMenuItem(Menu parent, String text){
		MenuItem item = new MenuItem(parent,SWT.CASCADE);
		item.setText(text);	
		return item;
	}
}
