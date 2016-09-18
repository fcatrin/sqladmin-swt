package gnu.sql.tools.sqladmin.gui;

import gnu.sql.tools.sqladmin.*;
import gnu.sql.tools.sqladmin.db.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

public class ConnectionTree {
	Connection con=null;
	TreeItem tree = null;
	SqlAdmin app = null;
	public static final String ID_UPDATED = "updated";
	private String[] tableTypes={"TABLE"};
	
	public ConnectionTree(SqlAdmin app, Connection con){
		this.con=con;
		this.app = app;
	}

	public void createTree(TreeItem baseTree){
		TreeItem dbItem = new TreeItem(baseTree,SWT.NULL);
		dbItem.setText(con.getName());
		dbItem.setData(SqlAdmin.ID_TYPE,SqlAdmin.ID_TYPE_CONNECTION);
		dbItem.setData(SqlAdmin.ID_CONNECTION,con);
		dbItem.setData(SqlAdmin.ID_CONNTREE,this);
		dbItem.setData(ID_UPDATED,new Boolean(false));
		
		dbItem.setImage(GUIUtil.getIcon("db-off.gif"));
		new TreeItem(dbItem, SWT.NULL);
		tree = dbItem;
	}

	public void expandTree(TreeItem item){
		String type = (String)item.getData(SqlAdmin.ID_TYPE);
		if (type.equals(SqlAdmin.ID_TYPE_CONNECTION)) createConnectedTree(item);
		if (type.equals(SqlAdmin.ID_TYPE_SCHEMA)) createSchemaTree(item);
		if (type.equals(SqlAdmin.ID_TYPE_TABLE)) createTableTree(item);
	}
	
	void createTableTreeItem(TreeItem item, String schemaName, String tableName){
		TreeItem tableItem = new TreeItem(item,SWT.NULL);
		tableItem.setImage(GUIUtil.getIcon("table.gif"));
		tableItem.setText(tableName);
		tableItem.setData(SqlAdmin.ID_TYPE, SqlAdmin.ID_TYPE_TABLE);
		tableItem.setData(SqlAdmin.ID_SCHEMA, schemaName);
		tableItem.setData(SqlAdmin.ID_CONNECTION, con);
		tableItem.setData(SqlAdmin.ID_CONNTREE,this);
		tableItem.setData(ID_UPDATED,new Boolean(false));
		new TreeItem(tableItem, SWT.NULL);
	}
	
	void createSchemaTree(TreeItem item){
		boolean withErrors = false;		
		Boolean updated = (Boolean)item.getData(ID_UPDATED);
		if (updated.booleanValue()) return;
		item.setData(ID_UPDATED,new Boolean(true));
		cleanTree(item);
		try {
			java.sql.DatabaseMetaData dbmd = con.db.getMetaData();
			String schemaName = (String)item.getText();
			java.sql.ResultSet tables = dbmd.getTables(null,schemaName,null,tableTypes);
			if (tables != null)
				while (tables.next()) createTableTreeItem(item, schemaName, tables.getString("TABLE_NAME"));
		} catch (Exception e){
			withErrors = true;
			SqlAdmin.log(e.toString());
		}
		if (withErrors) 
			SqlAdmin.log("Database metadata for " + con.getName() + " loaded with errors");
		else
			SqlAdmin.log("Database metadata for " + con.getName() + " loaded successfully");
				
	}


	void createTableTree(TreeItem item){
		Boolean updated = (Boolean)item.getData(ID_UPDATED);
		if (updated.booleanValue()) return;
		item.setData(ID_UPDATED,new Boolean(true));
		cleanTree(item);
		try {
			java.sql.DatabaseMetaData dbmd = con.db.getMetaData();
			java.sql.ResultSet columns = dbmd.getColumns(null,(String)item.getData(SqlAdmin.ID_SCHEMA),item.getText(),null);
			while (columns.next()){
				TreeItem columnItem = new TreeItem(item,SWT.NULL);
				columnItem.setImage(GUIUtil.getIcon("field.gif"));
				columnItem.setText(columns.getString("COLUMN_NAME"));
			}
		} catch (Exception e){
			SqlAdmin.log(e.toString());
		}
	}

	public void setBrokenConnection(TreeItem item) {
		cleanTree(item);
		item.setImage(GUIUtil.getIcon("db-off.gif"));
		TreeItem voidItem = new TreeItem(item,SWT.NULL);
		voidItem.setText("...broken");
	}
	
	public void createConnectedTree(TreeItem item){
		Boolean updated = (Boolean)item.getData(ID_UPDATED);
		if (updated.booleanValue()) return;
		item.setData(ID_UPDATED,new Boolean(true));
		con.disconnect();
		if (!con.connect()) {
			setBrokenConnection(item);
			return;
		}
		app.handleEvent(SqlAdmin.EV_CON_CONNECT, null);
		boolean withErrors = false;
		
		SqlAdmin.log("Reading database metadata for " + con.getName());
		Tree parent = item.getParent();
		parent.setRedraw(false);
		parent.setCursor(new Cursor(SqlAdmin.display,SWT.CURSOR_WAIT));
		try {
			item.setImage(GUIUtil.getIcon("db.gif"));
			cleanTree(item);
			java.sql.DatabaseMetaData dbmd = con.db.getMetaData();
			java.sql.ResultSet schemas = dbmd.getSchemas();
			if (schemas!=null) {
				if (!schemas.next() || schemas.getString("TABLE_SCHEM").equals(""))
					schemas = null;
				else {
					do {
						String schemaName = schemas.getString("TABLE_SCHEM");
						java.sql.ResultSet tables = dbmd.getTables(null,schemaName,null,tableTypes);
						if (tables != null && tables.next()) {
							TreeItem schemaItem = new TreeItem(item, SWT.NULL);
							schemaItem.setImage(GUIUtil.getIcon("schema.gif"));
							schemaItem.setText(schemaName);
							schemaItem.setData(SqlAdmin.ID_TYPE, SqlAdmin.ID_TYPE_SCHEMA);
							schemaItem.setData(SqlAdmin.ID_CONNECTION, con);
							schemaItem.setData(SqlAdmin.ID_CONNTREE,this);
							schemaItem.setData(ID_UPDATED,new Boolean(false));
							new TreeItem(schemaItem, SWT.NULL);
						}
					} while (schemas.next());
				}
			}
			if (schemas==null) {
				java.sql.ResultSet tables = dbmd.getTables(null,null,null,tableTypes);
				if (tables != null)
					while (tables.next())createTableTreeItem(item, null, tables.getString("TABLE_NAME"));
			}
		} catch (Exception e){
			withErrors = true;
			SqlAdmin.log(e.toString());
		}
		parent.setCursor(new Cursor(SqlAdmin.display,SWT.CURSOR_ARROW));
		parent.setRedraw(true);
		if (withErrors) 
			SqlAdmin.log("Database metadata for " + con.getName() + " loaded with errors");
		else
			SqlAdmin.log("Database metadata for " + con.getName() + " loaded successfully");
	}
	
	public void cleanTree(TreeItem item){
		TreeItem items[] = item.getItems();
		for (int i=0;i<items.length;i++)
			items[i].dispose();
	}
}
