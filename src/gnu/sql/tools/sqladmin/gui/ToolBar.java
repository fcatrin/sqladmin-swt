package gnu.sql.tools.sqladmin.gui;

import gnu.sql.tools.sqladmin.*;
import gnu.sql.tools.sqladmin.events.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import java.util.*;

public abstract class ToolBar {
	
	int eventSrcType = SqlAdmin.EV_SRC_NONE;
	IEventHandler evh = null;
	Composite parent = null;

	Vector items = null;

	public ToolBar(Composite parent, IEventHandler evh, int eventSrcType){
		this.evh = evh;
		this.eventSrcType = eventSrcType;
		this.parent = parent;
	}
	
	public void create(){
		org.eclipse.swt.widgets.ToolBar toolBar = 
			new org.eclipse.swt.widgets.ToolBar(parent, SWT.FLAT);
			
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		toolBar.setLayoutData(gridData);

		Hashtable event = new Hashtable();
		event.put(SqlAdmin.EV_ID_SRC, new Integer(eventSrcType));
		
		for (int i = 0; i< items.size(); i++) {
			ToolBarItem item = (ToolBarItem)items.elementAt(i);
			ToolItem barItem = new ToolItem(toolBar, item.style);
			item.item = barItem;
			if (item.style ==SWT.PUSH){
				barItem.setImage(GUIUtil.getIcon(item.icon));
				barItem.setToolTipText(item.tip);
				event.put(SqlAdmin.EV_ID, new Integer(item.event));
				barItem.addSelectionListener(new SelectionHandler(evh, event));
			}
		}
	}
	
	public void update(View view){
		for (int i = 0; i< items.size(); i++) {
			ToolBarItem item = (ToolBarItem)items.elementAt(i);
			if (item.action>=0) {
				if (view!=null)
					item.setEnabled(item.action, view.getAction(item.action));
				else
					item.setEnabled(item.action, View.ACT_DISABLE);
			}
		}		
	}
	
	public void setEnabled(int action, boolean state){
		for (int i = 0; i< items.size(); i++) {
			ToolBarItem item = (ToolBarItem)items.elementAt(i);
			item.setEnabled(action, state);
		}
	}
}
