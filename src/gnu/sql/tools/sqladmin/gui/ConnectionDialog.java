package gnu.sql.tools.sqladmin.gui;

import gnu.sql.tools.sqladmin.*;
import gnu.sql.tools.sqladmin.db.*;
import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;

public class ConnectionDialog {

	public Connection con = null;
	
	Text txtName = null;
	Text txtServer = null;
	Text txtPort = null;
	Text txtDatabase = null;
	Text txtUrl = null;
	Text txtUid = null;
	Text txtPwd = null;
	Combo cmbDriver = null;
	Shell shell = null;
	Button btnUseSettings = null;
	Button btnUseURL = null;
	
	public ConnectionDialog(Connection con){
		this.con = con;
		shell = null;
	}
	
	
	public void open(){
		shell = new Shell(SqlAdmin.display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(380,330);
		shell.setText(con.getName() +" Connection Properties");
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		shell.setLayout(layout);
	
		createConnectionInfo(shell);
		createActionButtons(shell);
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!SqlAdmin.display.readAndDispatch()) SqlAdmin.display.sleep();
		}

	}
	
	private Combo createDriverList(Composite parent, Vector drivers){
		Combo cmb = new Combo(parent,SWT.READ_ONLY);
		int j=-1;
		String driverName = con.getProperty(con.PROP_DRIVER);
		for(int i=0;i<drivers.size();i++){
			Driver drv = (Driver)drivers.elementAt(i);
			cmb.add(drv.getName());
			if (drv.getName().equals(driverName)) j=i;
		}
		if (j>=0) cmb.select(j);
		else if (drivers.size()>0) cmb.select(0);
		cmb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return cmb;
	}

	private void createConnectionInfo(Composite parent){
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		Group panel = new Group(parent,SWT.BORDER);
		panel.setText("Connection");
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 

		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING );
		layoutData.horizontalSpan = 2;

		Label lbl;
		
		Dialog dlg = new Dialog();

		dlg.label(panel, "Name", SWT.NULL);
		txtName = dlg.text(panel, con.getName(), GridData.FILL_HORIZONTAL);

		dlg.label(panel, "Driver", SWT.NULL);
		cmbDriver = createDriverList(panel, DriverManager.getDrivers());


		btnUseSettings = new Button(panel, SWT.RADIO);
		btnUseSettings.setText("Use these settings");
		btnUseSettings.setSelection(true);
		btnUseSettings.setLayoutData(layoutData);

		dlg.label(panel, "Server", SWT.NULL);
		txtServer = dlg.text(panel, con.getProperty(con.PROP_SERVER), GridData.FILL_HORIZONTAL);

		dlg.label(panel,"Port", SWT.NULL);
		txtPort = dlg.text(panel, con.getProperty(con.PROP_PORT), GridData.FILL_HORIZONTAL);
		
		dlg.label(panel,"Database", SWT.NULL);
		txtDatabase = dlg.text(panel, con.getProperty(con.PROP_DB), GridData.FILL_HORIZONTAL);

		layoutData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING );
		layoutData.horizontalSpan = 2;

		btnUseURL = new Button(panel, SWT.RADIO);
		btnUseURL.setText("Use specific url");
		btnUseURL.setSelection(false);
		btnUseURL.setLayoutData(layoutData);
		
		dlg.label(panel, "JDBC URL", SWT.NULL);
		txtUrl = dlg.text(panel, con.getProperty(con.PROP_SURL), GridData.FILL_HORIZONTAL);
		
		panel = new Group(parent, SWT.BORDER);
		panel.setText("Login");

		layout = new GridLayout();
		layout.numColumns = 1;
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 

		dlg.label(panel, "UserName", SWT.NULL);
		txtUid = dlg.text(panel, con.getProperty(con.PROP_UID), GridData.FILL_HORIZONTAL);

		dlg.label(panel, "Password", SWT.NULL);
		txtPwd = dlg.text(panel, con.getProperty(con.PROP_PWD), GridData.FILL_HORIZONTAL);
	}			


	private void createActionButtons(Composite parent){
		Composite panel = new Composite(parent,SWT.NULL);
		RowLayout layout = new RowLayout();
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Button btn = new Button(panel,SWT.PUSH);
		btn.setText("Save");
		btn.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {};
			public void widgetSelected(SelectionEvent e) {
				con.setName(txtName.getText());
				con.setProperty(con.PROP_SERVER,txtServer.getText());
				con.setProperty(con.PROP_PORT,txtPort.getText());
				con.setProperty(con.PROP_DB,txtDatabase.getText());
				con.setProperty(con.PROP_SURL, txtUrl.getText());
				con.setProperty(con.PROP_UID, txtUid.getText());
				con.setProperty(con.PROP_PWD, txtPwd.getText());
				con.setProperty(con.PROP_DRIVER, cmbDriver.getText());
				con.setProperty(con.PROP_USE_URL, btnUseURL.getSelection()?"Y":"N");
				con.updateDriver();
				SqlAdmin.saveConfiguration();
				shell.close();
			}
			
		});
			
		btn = new Button(panel,SWT.PUSH);
		btn.setText("Cancel");
		btn.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {};
			public void widgetSelected(SelectionEvent e) {
				shell.close();
				con = null;
			}
		});
	}	
}
