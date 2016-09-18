package gnu.sql.tools.sqladmin.gui;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class ToolBarItem {
	public String icon = "";
	public String tip = "";
	public int style = SWT.NONE;
	public int event = 0;
	public ToolItem item = null;
	public int action = 0;
	
	public ToolBarItem(String icon, String tip, int style, int event, int action){
		this.icon = icon;
		this.tip = tip;
		this.style = style;
		this.event = event;
		this.action = action;
	}
	
	public void setEnabled(int action, int state){
		if (this.action!=action || state==View.ACT_IGNORE) return;
		item.setEnabled(state==View.ACT_ENABLE);
	}

	public void setEnabled(int action, boolean state){
		if (this.action!=action) return;
		item.setEnabled(state);
	}
	
}
