package gnu.sql.tools.sqladmin.gui;

import gnu.sql.tools.sqladmin.*;
import gnu.sql.tools.sqladmin.db.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.graphics.*;
import java.sql.*;
import java.util.*;

public class ResultSetTable extends View implements IResultSetHandler {
	private Table table=null;
	private static final int MAX_FIELD_WIDTH = 7;
	
	private boolean canRefresh = false;
	private Query query = null;
	
	public ResultSetTable(Composite parent,  Query query) {
		panel = new Composite(parent, SWT.NONE);
		this.query = query;
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 2;
		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL 
			| GridData.FILL_VERTICAL));
		panel.setLayout(layout);
	}
	
	void createBottomView(Composite parent){

		table = new Table(parent,SWT.BORDER );
		table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
			| GridData.FILL_VERTICAL));
		table.setHeaderVisible(true);
        
        parent.layout(true);
        
	}
	
	public void refresh(){
		query.setResultSetHandler(this);
		query.execSQL();
	}
	
	
	public int process(Query query){
		this.query = query;
		ResultSet rs = query.getResultSet();
		if (table!=null) table.dispose();
		createBottomView(panel);
		table.setRedraw(false);
		int rows=0, i=0;
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			for (i=0;i<cols;i++){
				TableColumn col = new TableColumn(table,SWT.NULL);
				String colName = rsmd.getColumnName(i+1);
				col.setText(colName);
			}
			while (rs.next()) {
				TableItem item = new TableItem(table,0);	
				for (i=0;i<cols;i++){
					String text = rs.getString(i+1);
					item.setText(i,text!=null?text:"");
				}
				rows++;
			}			
		} catch (Exception e){
			e.printStackTrace();
		}
		table.setRedraw(true);
		return rows;
	}
	
	public int getAction(int action){
		switch (action) {
			case CAN_REFRESH: return (canRefresh?ACT_DISABLE:ACT_ENABLE);
			case CAN_CLOSE:
			case CAN_SQL:
			case CAN_SAVE: return ACT_ENABLE;
		}
		return ACT_IGNORE;
	}

	public void doAction(int action){
		switch (action) {
			case CAN_SQL : showSQL();break;
			case CAN_REFRESH : refresh();break;
		}
	 }
	 
	public void showSQL(){
		Shell shell = new Shell(SqlAdmin.display, SWT.DIALOG_TRIM);
		shell.setSize(380,200);
		shell.setText("SQL Query"); 
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = layout.marginWidth = 2;
		shell.setLayout(layout);

		Text sqlText = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		sqlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL 
			| GridData.FILL_VERTICAL));
		sqlText.setText(query.getSql());	
		shell.open();
	}
}
