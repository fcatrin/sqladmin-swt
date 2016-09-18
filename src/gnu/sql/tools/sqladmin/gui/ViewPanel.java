package gnu.sql.tools.sqladmin.gui;

import gnu.sql.tools.sqladmin.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;


public class ViewPanel{
	
	TabFolder container = null;
	public View activeView = null;
	SqlAdmin app = null;
	
	public ViewPanel(SqlAdmin app, Composite parent){
		this.app = app;
		container = new TabFolder(parent, SWT.H_SCROLL);
		container.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				TabItem item = (TabItem)e.item;
				activeView = (View)item.getData(SqlAdmin.ID_VIEW);
				SqlAdmin app = (SqlAdmin)item.getData(SqlAdmin.ID_APP);
				if (app!=null) app.updateActions();
			}
		});			
	}
	
	public Composite getContainer(){
		return container;
	}
	
	public void updateActive(){
		int selected = container.getSelectionIndex();
		activeView = null;
		if (selected>=0) {
			TabItem item = container.getItem(selected);
			if (item!=null) activeView = (View)item.getData(SqlAdmin.ID_VIEW);
		}
		app.updateActions();
	}		
	
	public void removeActive(){
		TabItem item = container.getItem(container.getSelectionIndex());
		item.dispose();
		updateActive();
	}
	
	public Widget addView(String title, View view){
		TabItem tabItem = new TabItem(container, SWT.CLOSE);
		tabItem.setText(title);
		tabItem.setControl(view.panel);
		tabItem.setData(SqlAdmin.ID_VIEW, view);
		tabItem.setData(SqlAdmin.ID_APP, app);
		container.setSelection(container.indexOf(tabItem));
		updateActive();
		return tabItem;
	}
	
	public void doAction(int action){
		if (activeView!=null) activeView.doAction(action);
	}
}


