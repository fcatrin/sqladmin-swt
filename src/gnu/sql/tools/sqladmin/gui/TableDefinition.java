package gnu.sql.tools.sqladmin.gui;

import gnu.sql.tools.sqladmin.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.custom.*;
import java.sql.*;
import java.util.*;

public class TableDefinition extends View {
	private Table table=null;
	private static final int MAX_FIELD_WIDTH = 7;
	
	public TableDefinition(Composite parent,  String description) {
		panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 2;
		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL 
			| GridData.FILL_VERTICAL));
		panel.setLayout(layout);
		
		createTopView(panel, description);
		createBottomView(panel);
	}
	
	void createTopView(Composite parentControl, String description) {
		Composite topPanel = new Composite(parentControl, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1; 
		topPanel.setLayout(layout);
		topPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL ));
		Label text = new Label(topPanel,SWT.BORDER);
		text.setText("Table definition for " + description);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL ));
	}
	
	void createBottomView(Composite parent){
		table = new Table(parent,SWT.BORDER );
		table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
			| GridData.FILL_VERTICAL));
		table.setHeaderVisible(true);
	}		
	
	public int process(Connection db, String tableName){
		int rows=0,i=0;
		try {
			DatabaseMetaData dbmd = db.getMetaData();
			ResultSet columns = dbmd.getColumns(null,null,tableName,null);
			Vector v = new Vector();
			Vector w = new Vector();
			String labels[]= {"Name","Type","Size","Digits","Default","NULL",null};
			String fields[]= {"COLUMN_NAME","TYPE_NAME","COLUMN_SIZE",
				"DECIMAL_DIGITS","COLUMN_DEF","IS_NULLABLE"};
			for (i=0;labels[i]!=null;i++){
				TableColumn col = new TableColumn(table,SWT.NULL);
				String colName = labels[i];
				col.setText(colName);
				
			}
			int cols = i;
			while (columns.next()) {
				TableItem item = new TableItem(table,0);	
				for (i=0;i<cols;i++){
					String text = columns.getString(fields[i]);
					text=(text!=null?text:"");
					item.setText(i,text);
				}
				rows++;
			}
			for (i=0;i<cols;i++)
				table.getColumn(i).pack();
	
		} catch (Exception e){
			e.printStackTrace();
		}
		return rows;
	}

	public int getAction(int action){
		switch (action) {
			case CAN_SQL :
			case CAN_SAVE : return ACT_DISABLE;
			case CAN_REFRESH : 
			case CAN_CLOSE: return ACT_ENABLE;
		}
		return ACT_IGNORE;
	}
	
	public void doAction(int action){
		return;
	}

}
