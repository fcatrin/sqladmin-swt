package gnu.sql.tools.sqladmin.gui;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.Composite;
import gnu.sql.tools.sqladmin.events.*;
import java.util.*;

public class ToolBarMain extends ToolBar{
	public static final int EV_NULL = 0;
	public static final int EV_DRIVERS = 1;
	public static final int EV_CONNECTIONS = 5;
	public static final int EV_CLOSE = 6;
	public static final int EV_SQL = 7;
	public static final int EV_REFRESH = 8;
	public static final int EV_CLASSPATH = 9;

	public ToolBarMain(Composite parent, IEventHandler evh, int eventSrcType){
		super(parent, evh, eventSrcType);
		Vector v = new Vector();
		v.add(new ToolBarItem("save.gif", "save", SWT.PUSH, EV_NULL, View.CAN_SAVE));
		v.add(new ToolBarItem("refresh.gif", "Refresh", SWT.PUSH, EV_REFRESH, View.CAN_REFRESH));
		v.add(new ToolBarItem("view-sql.gif", "View SQL", SWT.PUSH, EV_SQL, View.CAN_SQL));
		v.add(new ToolBarItem("", "", SWT.SEPARATOR, EV_NULL, -1));
		v.add(new ToolBarItem("close.gif", "Close View", SWT.PUSH, EV_CLOSE, View.CAN_CLOSE));
		v.add(new ToolBarItem("", "", SWT.SEPARATOR, EV_NULL, -1));
		v.add(new ToolBarItem("connections.gif", "New connection...", SWT.PUSH, EV_CONNECTIONS, -1));
		v.add(new ToolBarItem("drivers.gif", "Setup Drivers...", SWT.PUSH, EV_DRIVERS, -1));
		items = v;
		create();
	}

}
