package gnu.sql.tools.sqladmin.gui;

import gnu.sql.tools.sqladmin.*;
import gnu.sql.tools.sqladmin.db.*;
import gnu.sql.tools.sqladmin.events.*;
import java.util.*;
import java.io.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

public class PanelSQL implements IEventHandler {
	public String EV_ID = "event";
	public int EV_SRC_PANEL = 0;
	
	public Composite panel = null;
	SqlAdmin app = null;
	Composite topPanel = null;
	Combo lstConnections = null;
	Vector connections = null;
	Text sqlText = null;
	boolean hasConnection = false;
	
	ToolBarSQL toolbar = null;
	
	public PanelSQL (SqlAdmin app, Composite parent ){
		this.app = app;
		panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 2;
		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL 
			| GridData.FILL_VERTICAL));
		panel.setLayout(layout);
		
		createTopView(panel);
		createBottomView(panel);
	}
	
	void createTopView(Composite parent){
		topPanel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		topPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		topPanel.setLayout(layout);
		toolbar = new ToolBarSQL(topPanel, this, EV_SRC_PANEL);
		lstConnections = new Combo(topPanel,SWT.READ_ONLY);
		lstConnections.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}		
	
	public void setConnections(Vector v){
		boolean emptyList = true;
		connections = v;
		lstConnections.removeAll();
		if (v.size()>0) {
			for (int i=0;i<v.size();i++){
				Connection con = (Connection)v.elementAt(i);
				if (con.connected) lstConnections.add(con.toString());
				emptyList = emptyList && !con.connected;
			}
			if (v.size()>0) lstConnections.select(0);
			hasConnection = true;
		} 
		if (emptyList) {
			lstConnections.add("Not Connected");
			lstConnections.select(0);
			hasConnection = false;
		}
		updateToolbar();
	}
	
	void createBottomView(Composite parent){
		sqlText = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		sqlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL 
			| GridData.FILL_VERTICAL));
			
		sqlText.addKeyListener ( new KeyAdapter() {
			public void keyReleased(KeyEvent e){
				if (e.keyCode==SWT.F5){
					Hashtable event = new Hashtable();
					event.put(EV_ID, new Integer(ToolBarSQL.EV_RUN));
					handleEvent(event);
				}
			}
		});
		
		sqlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e){
				updateToolbar();
			}
		});
		
		updateToolbar();
	}

	void updateToolbar(){
		boolean hasText = !sqlText.getText().equals("");
		
		toolbar.setEnabled(ToolBarSQL.EV_SAVE, hasText);
		toolbar.setEnabled(ToolBarSQL.EV_PRINT, hasText);
		
		toolbar.setEnabled(ToolBarSQL.EV_RUN, hasText && hasConnection);
		toolbar.setEnabled(ToolBarSQL.EV_STOP, hasText && hasConnection);
	}

	void clearSQLText(){
		if (GUIUtil.query("Are you sure you want to empty the SQL Entry box?")) 
			sqlText.setText("");
	}

 	String read(String fname){
		String line="";
		try{
			int c=0;
			FileReader f = new FileReader(fname);
			while ((c=f.read())!=-1) {
				char ch=(char)c;
				line = line + ch;
			}
			f.close();
			app.log(fname + " loaded successfully");
			return line;
		} catch (Exception e){
			app.log(e.toString());
			return null;
		}
	}


	boolean save(String fname, String content){
		try{
			FileWriter f = new FileWriter(fname);
			f.write(content,0,content.length());
			f.close();
			app.log(fname + " written successfully");
			return true;
		} catch (Exception e){
			app.log(e.toString());
			return false;
		}
	}


	String getFileName(String title, int style){
		FileDialog file = new FileDialog(app.shell, style);
		String ext[] = {"*.sql","*.*"};
		String des[] = {"SQL Query files","All files"};
		file.setText(title);
		file.setFilterExtensions(ext);
		file.setFilterNames(des);
		file.open();
		if (file.getFilterPath()==null) return null;
		return file.getFilterPath() + "/" + file.getFileName() ;
	}
	
	void loadSQLText(){
		String fileName = getFileName("Open Query...",SWT.OPEN);
		String content = null;;
		if (fileName!=null) {
			content = read(fileName);
			if (content!=null) sqlText.setText(content);
		}
	}					

	void saveSQLText(){
		String fileName = getFileName("Save Query...",SWT.SAVE);
		String content = sqlText.getText();
		if (fileName!=null) save(fileName, content);
	}					

	public void handleEvent(Hashtable properties){
		int event = ((Integer)properties.get(EV_ID)).intValue();
		switch (event) {
			case ToolBarSQL.EV_RUN : exec();break;
			case ToolBarSQL.EV_NEW : clearSQLText();break;
			case ToolBarSQL.EV_LOAD: loadSQLText();break;
			case ToolBarSQL.EV_SAVE: saveSQLText();break;
		}
	}
	
	private Vector getQueries(String sql){
		Vector v = new Vector();
		sql = sql + ";";
		int p = sql.indexOf(";");
		while (p>=0){
			if (p>0) v.add(sql.substring(0,p));
			sql = sql.substring(p+1).trim();
			p = sql.indexOf(";");
		}
		return v;
	}
	
	void exec(){
		Connection con = null;
		int i=0;
		
		for (i=0;i<connections.size();i++){
			con = (Connection)connections.elementAt(i);
			if (con.toString().equals(lstConnections.getText())) break;
		}
		if (con!=null && i<connections.size()){
			Vector queries = getQueries(sqlText.getText());
			for(i=0;i<queries.size();i++) app.execSQL(con,null, (String)queries.elementAt(i),0);
		} 
	}
}
