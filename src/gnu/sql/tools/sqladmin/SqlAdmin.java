package gnu.sql.tools.sqladmin;

import gnu.sql.tools.sqladmin.setup.*;
import gnu.sql.tools.sqladmin.gui.*;
import gnu.sql.tools.sqladmin.db.*;
import gnu.sql.tools.sqladmin.events.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.custom.*;
import java.util.*;
import java.text.*;

public class SqlAdmin implements IResultSetHandler, IEventHandler {
	
	public static final int EV_EXIT = 3;
	public static final int EV_ABOUT = 4;

	public static final int EV_CON_PROPERTIES = 1;
	public static final int EV_CON_CONNECT = 2;
	public static final int EV_CON_INFORMATION = 3;
	public static final int EV_CON_REFRESH = 4;
	public static final int EV_CON_REMOVE = 5;
	
	public static final int EV_TBL_FIRST_ROWS = 1;
	public static final int EV_TBL_ALL_ROWS = 2;
	public static final int EV_TBL_PROPERTIES = 3;
	public static final String EV_TBL_TABLE = "tableName";
	public static final String EV_TBL_CONNECTION = "connection";
	
	public static final int DEFAULT_MAX_ROWS = 10;

	public static final String EV_ID_SRC = "source";
	public static final String EV_ID = "event";
	public static final int EV_SRC_NONE = 0;
	public static final int EV_SRC_TABLE = 1;
	public static final int EV_SRC_CONNECTION = 2;
	public static final int EV_SRC_APPLICATION = 3;
	
	public static final String ID_TYPE = "type";
	public static final String ID_TYPE_CONNECTION = "con";
	public static final String ID_TYPE_TABLE = "table";
	public static final String ID_TYPE_SCHEMA = "schema";
	
	public static final String ID_CONNECTION = "con";
	public static final String ID_CONNTREE = "connectionTree";
	public static final String ID_TREEITEM = "treeItem";
	public static final String ID_TABLE = "table";
	public static final String ID_SCHEMA = "schema";
	public static final String ID_VIEW = "view";
	public static final String ID_APP = "sqladmin";
	
	public static String name = "SQL Admin/SWT v.0.2.2";
	public static String shortName = "SQL Admin";
	public static String iconPath="", basePath="";
	public static Shell shell=null;
	public static Display display=null;
	static Vector connections = new Vector();
	public ViewPanel viewPanel = null;
	TabFolder bottomTabFolder = null;
	int nResults = 0;	
	static Text messages = null;
	static Label statusBar = null;
	PanelSQL panelSQL = null;
	
	public TreeItem connectionsTreeRoot = null;

	private  java.sql.Statement stmt = null;
	private boolean isResultSet = false;
	private String resultSetTitle = null;
	private int resultSetMaxRows = 0;
	private Connection resultSetConnection = null;
	private String resultSetSQL = null;
	public boolean hasResult = false;
	
	ToolBarMain toolBarMain = null;

	static String pendingLog="";
	static DriverManager driverManager;
	
	SqlAdmin(Display display){
		this.display=display;
		shell = new Shell(display);
		shell.setText(name);
		
		try {
			basePath = new java.io.File(Class.forName("gnu.sql.tools.sqladmin.SqlAdmin").getProtectionDomain().
				getCodeSource().getLocation().getFile()).getCanonicalPath() 
				+ "/gnu/sql/tools/sqladmin/";
				
			iconPath =  basePath + "images/";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		shell.setImage(GUIUtil.getIcon("app.gif"));
		
		Configuration.load(".sqladminrc");
		driverManager = new DriverManager();
		DriverManager.loadFromConfig();
		getConnections();
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		shell.setLayout(gridLayout);
		
		shell.setMenuBar(createMenu(shell));
		toolBarMain = new gnu.sql.tools.sqladmin.gui.ToolBarMain(shell, this, EV_SRC_APPLICATION);
	
		SashForm sashForm = new SashForm(shell, SWT.NONE);
		sashForm.setOrientation(SWT.HORIZONTAL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		gridData.horizontalSpan = 3;
		sashForm.setLayoutData(gridData);
		createTreeView(sashForm);
		
		SashForm mainPanel = new SashForm(sashForm, SWT.NONE);
		mainPanel.setOrientation(SWT.VERTICAL);
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		gridData.verticalSpan = 3;
		mainPanel.setLayoutData(gridData);
		
		sashForm.setWeights(new int[] { 2, 5 });

		viewPanel = new ViewPanel(this, mainPanel);

		bottomTabFolder = createTextViewFolder(mainPanel);	
		
		mainPanel.setWeights(new int[] { 5, 3 });
		
		statusBar = new Label(shell, SWT.BORDER);
		statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		updateActions();
		log("Ready");	
		SqlAdmin.saveConfiguration();
	}
	
	public TabFolder createTextViewFolder(Composite parent){
		TabFolder textView = new TabFolder(parent, SWT.NONE);
		textView.setLayoutData(new GridData(GridData.FILL_HORIZONTAL 
			| GridData.FILL_VERTICAL));
		
		TabItem tabItem = new TabItem(textView, SWT.CLOSE);
		
		tabItem.setText("SQL");
		panelSQL = new PanelSQL(this, textView);
		panelSQL.setConnections(connections);
		tabItem.setControl(panelSQL.panel);
		
		tabItem = new TabItem(textView, SWT.CLOSE);
		tabItem.setText("Messages");
		tabItem.setControl(createMessageView(textView));
		return textView;
	}
	
	private Control createMessageView(Composite parent){
		messages = new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		messages.setLayoutData(new GridData(GridData.FILL_HORIZONTAL 
			| GridData.FILL_VERTICAL));
		return messages;
	}
		
	public static void main (String [] args) {
		Display display = new Display();
		SqlAdmin app = new SqlAdmin(display);
		Shell shell = app.shell;	
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}

	void createTreeView(Composite parent){
		final Tree tree = new Tree(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
		tree.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

		tree.addTreeListener(new TreeAdapter() {
			public void treeExpanded(TreeEvent event) {
				final TreeItem item = (TreeItem) event.item;
				ConnectionTree conTree = (ConnectionTree)item.getData(ID_CONNTREE);
				if (conTree!=null) conTree.expandTree(item);				
			}
			public void treeCollapsed(TreeEvent event) {};
		});
			
		tree.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e){
				if (e.button!=3) return;
				TreeItem item = tree.getItem (new Point(e.x,e.y));
				if (item==null) return;
				TreeItem[] items =  {item};
				tree.setSelection(items);
				tree.update();
				String type = (String)item.getData(ID_TYPE);
				if (type==null) return;
				if (type.equals(ID_TYPE_CONNECTION)){
					Connection con = (Connection)item.getData(ID_CONNECTION);
					if (con!=null) {
						Menu popup = createConnectionMenu(tree, item);
						popup.setVisible(true);
					}
				} else if (type.equals(ID_TYPE_TABLE)){
					Connection con = (Connection)item.getData(ID_CONNECTION);
					if (con!=null) {
						Menu popup = createTableMenu(tree, con, item.getText());
						popup.setVisible(true);
					}
				}
			}
		});


		connectionsTreeRoot  = new TreeItem(tree, SWT.NULL);
		connectionsTreeRoot .setImage(GUIUtil.getIcon("connection.gif"));
		connectionsTreeRoot .setText("Connections");

		for (int i=0;i<connections.size();i++) {
			Connection con = (Connection)connections.elementAt(i);
			ConnectionTree dbTree = new ConnectionTree(this, con);
			dbTree.createTree(connectionsTreeRoot );
		}
	}

	private void getConnections(){
		Vector v=Configuration.getObjects("connection");
		String section="";
		for (int i=0;i<v.size();i++) {
			section = (String)v.elementAt(i);
			Connection con = new Connection(section);
			Configuration.configObject(con);
			con.updateDriver();
			connections.add(con);
		}
	}		

	public void execSQL (Connection con, String title, String SQL, int maxRows){
		Query query = new Query(con, title, maxRows);
		query.setSql(SQL);
		query.setResultSetHandler(this);
		query.execSQL();
	}

	public int process(Query query) {
		ResultSetTable rst = new ResultSetTable(viewPanel.getContainer(), query);
		int rows = rst.process(query);
		log(rows + " row(s) returned");
		viewPanel.addView(query.getTitle(), rst);
		return rows;
	}

	public void showTableInfo (Connection con, String tableName){
		con.connect();
		shell.setCursor(new Cursor(display, SWT.CURSOR_WAIT));
		statusBar.setText("Reading metadata...");
		shell.update();

		TableDefinition rst = new TableDefinition(viewPanel.getContainer(),  tableName);
		int rows = rst.process(con.db, tableName);

		viewPanel.addView(tableName, rst);
		
		shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
		statusBar.setText("Ready");
	}

	public static void log(String txt){
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		String now = df.format(new java.util.Date());
		if (messages!=null) {		
			if (!pendingLog.equals("")) {
				messages.append(pendingLog);
				pendingLog="";
			}
			String eol = messages.getLineDelimiter();
			messages.append(now + "> " + txt + eol);
			statusBar.setText(txt);
		} else {
			pendingLog = pendingLog + now + "> " + txt + "\n";
		}
	}
	
	private Menu createMenu(Shell shell){
		Menu menu = new Menu(shell,SWT.BAR);
	
		MenuItem item = GUIUtil.cascadeMenuItem(menu,"&Connection");	
	
		Menu connMenu= new Menu(shell,SWT.DROP_DOWN);
		item.setMenu(connMenu);

		Hashtable event = new Hashtable();
		event.put(EV_ID_SRC, new Integer(EV_SRC_APPLICATION));
		
		GUIUtil.cascadeMenuItem(connMenu, this, ToolBarMain.EV_CONNECTIONS, "New Connection...", event);
		GUIUtil.cascadeMenuItem(connMenu, this, ToolBarMain.EV_DRIVERS, "Drivers...", event);
		GUIUtil.cascadeMenuItem(connMenu, this, ToolBarMain.EV_CLASSPATH, "Classpath...", event);

		item = new MenuItem(connMenu, SWT.SEPARATOR);

		GUIUtil.cascadeMenuItem(connMenu, this, EV_EXIT, "Exit", event);

		item = GUIUtil.cascadeMenuItem(menu, "&Help");	

		Menu helpMenu= new Menu(shell,SWT.DROP_DOWN);
		item.setMenu(helpMenu);
	
		GUIUtil.cascadeMenuItem(helpMenu, this, EV_ABOUT, "About...", event);
		
		return menu;	
	}

	private Menu createConnectionMenu(Tree parent, TreeItem treeItem){
		Menu menu = new Menu(parent);
		Connection con = (Connection)treeItem.getData(ID_CONNECTION);
	
		Hashtable event = new Hashtable();

		event.put(EV_ID_SRC, new Integer(EV_SRC_CONNECTION));
		event.put(ID_TREEITEM, treeItem);
		event.put(ID_CONNECTION, con);
		event.put(ID_CONNTREE, treeItem.getData(ID_CONNTREE));
	
		MenuItem item = null;
		
		item = GUIUtil.cascadeMenuItem(menu, this, EV_CON_CONNECT, 
			con.connected?"Disconnect":"Connect", event);

		item = GUIUtil.cascadeMenuItem(menu, this, EV_CON_REFRESH,"Refresh", event);
		if (!con.connected) item.setEnabled(false);
		
		item = GUIUtil.cascadeMenuItem(menu, this, EV_CON_REMOVE,"Remove", event);
		
		item = new MenuItem(menu,SWT.SEPARATOR);

		item = GUIUtil.cascadeMenuItem(menu, this, EV_CON_INFORMATION,"Information", event);
		if (!con.connected) item.setEnabled(false);

		item = GUIUtil.cascadeMenuItem(menu, this, EV_CON_PROPERTIES,"Properties", event);
		
		return menu;	
	}

	private Menu createTableMenu(Control parent, Connection con, String tableName){
		Menu menu = new Menu(parent);
	
		Hashtable event = new Hashtable();
		event.put(EV_ID_SRC, new Integer(EV_SRC_TABLE));
		event.put(EV_TBL_CONNECTION, con);
		event.put(EV_TBL_TABLE, tableName);
		
		GUIUtil.cascadeMenuItem(menu, this, EV_TBL_FIRST_ROWS,
			"Return first " + DEFAULT_MAX_ROWS + " rows", event);
		
		GUIUtil.cascadeMenuItem(menu, this, EV_TBL_ALL_ROWS,
			"Return all rows", event);
		
		new MenuItem(menu,SWT.SEPARATOR);

		GUIUtil.cascadeMenuItem(menu, this, EV_TBL_PROPERTIES,
			"Properties", event);
		
		return menu;	
	}


	public void exit(){
		if (GUIUtil.query("Are you sure you want exit SQLAdmin?")) shell.dispose();
	}
	

	public void newConnection(){
		Connection con = new Connection("New Connection");
		if (con.setup())  {
			connections.add(con);
			ConnectionTree dbTree = new ConnectionTree(this, con);
			dbTree.createTree(connectionsTreeRoot);
			saveConfiguration();
		}
	}

	public void handleEvent(int event, Hashtable properties){
		switch (event){
			case ToolBarMain.EV_CLOSE : viewPanel.removeActive();break;
			case ToolBarMain.EV_SQL : viewPanel.doAction(View.CAN_SQL); break;
			case ToolBarMain.EV_REFRESH : viewPanel.doAction(View.CAN_REFRESH);break;
			case ToolBarMain.EV_DRIVERS : DriverManager.setup();break;
			case ToolBarMain.EV_CLASSPATH : DriverManager.setupClassPath();break;
			case ToolBarMain.EV_CONNECTIONS : newConnection();break;
			case EV_EXIT : exit();break;
			case EV_CON_CONNECT : panelSQL.setConnections(connections);break;
			case EV_ABOUT : {
				AboutDialog about = new AboutDialog();
				about.open();
				break;
			}
		}
	}

	public void removeConnection(TreeItem treeItem){
		Connection con = (Connection)treeItem.getData(ID_CONNECTION);
		String name = con.getName();
		if (GUIUtil.query("Are you sure you want to remove connection " + name + "?")) {
			treeItem.dispose();
			con.disconnect();
			connections.remove(con);
			saveConfiguration();
			handleEvent(EV_CON_CONNECT, null);
			log("Connection " + name + " removed");
		}
	}
	
	public void handleEventConnection(int event, Hashtable properties){
		TreeItem treeItem = (TreeItem) properties.get(ID_TREEITEM);
		Connection con = (Connection)properties.get(ID_CONNECTION);
		ConnectionTree conTree = (ConnectionTree)properties.get(ID_CONNTREE);
		
		switch (event){
			case EV_CON_PROPERTIES : {
				if (con.setup())  treeItem.setText(con.getName());
				break;
			}
			case EV_CON_INFORMATION : {
				ConnectionInfo ci = new ConnectionInfo(con);
				ci.open();
				break;
			}
			case EV_CON_REMOVE : removeConnection(treeItem);break;
			case EV_CON_REFRESH : 
			case EV_CON_CONNECT : {
				treeItem.setData(ConnectionTree.ID_UPDATED,new Boolean(false));
				conTree.createConnectedTree(treeItem);
				panelSQL.setConnections(connections);
				break;
			}
		}
	}

	public void handleEventTable(int event, Hashtable properties){
		Connection con = (Connection)properties.get(EV_TBL_CONNECTION);
		String tableName = (String)properties.get(EV_TBL_TABLE);
		switch (event){
			case EV_TBL_FIRST_ROWS: 
				execSQL(con, tableName,"SELECT * FROM " + tableName,DEFAULT_MAX_ROWS);break;
			case EV_TBL_ALL_ROWS: 
				execSQL(con, tableName, "SELECT * FROM " + tableName,0);break;
			case EV_TBL_PROPERTIES: 
				showTableInfo(con, tableName);break;
		}
	}
	
	public void handleEvent(Hashtable properties){
		int event = ((Integer)properties.get(EV_ID)).intValue();
		int source = ((Integer)properties.get(EV_ID_SRC)).intValue();
		switch (source){
			case EV_SRC_TABLE : 
				handleEventTable(event, properties); break;
			case EV_SRC_CONNECTION : 
				handleEventConnection(event, properties); break;
			case EV_SRC_APPLICATION : 
				handleEvent(event, properties); break;
		}
	}
	
	public static void saveConfiguration(){
		Vector v = new Vector();
		v.addAll(connections);
		v.addAll(DriverManager.getDrivers());
		v.addElement(driverManager);
		Configuration.save(v);
	}	
	
	public void updateActions(){
		toolBarMain.update(viewPanel.activeView);
	}

}
