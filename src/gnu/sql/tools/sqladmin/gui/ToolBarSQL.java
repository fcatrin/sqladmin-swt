package gnu.sql.tools.sqladmin.gui;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.Composite;
import gnu.sql.tools.sqladmin.events.*;
import java.util.*;

public class ToolBarSQL extends ToolBar{
	public static final int EV_NULL = 0;
	public static final int EV_NEW = 1;
	public static final int EV_LOAD = 2;
	public static final int EV_SAVE = 3;
	public static final int EV_PRINT = 4;
	public static final int EV_RUN = 5;
	public static final int EV_STOP = 6;
	
	public ToolBarSQL(Composite parent, IEventHandler evh, int eventSrcType){
		super(parent, evh, eventSrcType);
		Vector v = new Vector();
		v.add(new ToolBarItem("new.gif", "New query", SWT.PUSH, EV_NEW, -1));
		v.add(new ToolBarItem("open.gif", "Open query", SWT.PUSH, EV_LOAD, -1));
		v.add(new ToolBarItem("save.gif", "Save query", SWT.PUSH, EV_SAVE, EV_SAVE));
		v.add(new ToolBarItem("", "", SWT.SEPARATOR, EV_NULL, -1));
		v.add(new ToolBarItem("print.gif", "Print query", SWT.PUSH, EV_PRINT, EV_PRINT));
		v.add(new ToolBarItem("", "", SWT.SEPARATOR, EV_NULL, -1));
		v.add(new ToolBarItem("run.gif", "Run query over connection", SWT.PUSH, EV_RUN, EV_RUN));
		v.add(new ToolBarItem("stop.gif", "Stop running query", SWT.PUSH, EV_STOP, EV_STOP));
		v.add(new ToolBarItem("", "", SWT.SEPARATOR, EV_NULL, -1));
		items = v;
		create();
	}

}
