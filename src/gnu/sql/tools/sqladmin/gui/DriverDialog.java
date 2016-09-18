package gnu.sql.tools.sqladmin.gui;


import gnu.sql.tools.sqladmin.*;
import gnu.sql.tools.sqladmin.db.*;
import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;

public class DriverDialog {
	Text txtName = null;
	Text txtClass = null;
	Text txtURL = null;
	Text txtServer = null;
	Text txtPort = null;
	Text txtDatabase = null;
	Shell shell = null;
	protected Driver selectedDriver=null;
	protected Combo lstDrivers=null;
		
	public DriverDialog(){
		shell = null;
	}
	
	public void open(){
		shell = new Shell(SqlAdmin.display, SWT.DIALOG_TRIM);
		shell.setSize(280,200);
		shell.setText("JDBC Driver Setup");
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = layout.marginWidth = 2;
		shell.setLayout(layout);
	
		createDriverInfo(shell);
		createActionButtons(shell);
		shell.pack();
		shell.open();
	}
	
	public void setWidgetText(Text txt, String content){
		if (content!=null) txt.setText(content);
		else txt.setText("");
	}
	
	public void setActiveDriver(String name){
		Driver drv = DriverManager.getDriver(name);
		if (drv==null){
			setWidgetText(txtName,"");		
			setWidgetText(txtClass,"");		
			setWidgetText(txtURL,"");		
			setWidgetText(txtServer,"");		
			setWidgetText(txtPort,"");		
			setWidgetText(txtDatabase,"");
			selectedDriver=null;
		} else {
			setWidgetText(txtName,drv.getName());
			setWidgetText(txtClass,drv.getProperty(Driver.PROP_CLASS));
			setWidgetText(txtURL,drv.getProperty(Driver.PROP_URL));
			setWidgetText(txtServer,drv.getProperty(Driver.PROP_SERVER));
			setWidgetText(txtPort,drv.getProperty(Driver.PROP_PORT));
			setWidgetText(txtDatabase,drv.getProperty(Driver.PROP_DB));
			selectedDriver=drv;
		}
	}
	
	private void updateDriverList(final Combo cmb){
		int j=0;
		cmb.removeAll();
		cmb.add("New Driver...");
		Vector drivers = DriverManager.getDrivers();
		for(int i=0;i<drivers.size();i++){
			Driver drv = (Driver)drivers.elementAt(i);
			cmb.add(drv.getName());
			if (drv.equals(selectedDriver)) j=i+1;
		}
		cmb.select(j);
		cmb.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cmb.addSelectionListener( new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {};
			public void widgetSelected(SelectionEvent e) {
				setActiveDriver(cmb.getText());
			}
		});
	}
	
	private Combo createDriverList(Composite parent){
			
		Label lbl = new Label(parent,SWT.NONE);
		lbl.setText("Drivers");
		
		Combo cmb = new Combo(parent,SWT.SIMPLE | SWT.READ_ONLY);
		updateDriverList(cmb);
		return cmb;
	}
	
	private void createDriverInfo(Composite parent){
		Composite panel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = layout.marginWidth = 2;
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 
		
		lstDrivers = createDriverList(panel);
		
		Dialog dlg = new Dialog();
		
		dlg.label(panel, "Name", SWT.NULL);
		txtName = dlg.text(panel,"", GridData.FILL_HORIZONTAL);

		dlg.label(panel,"Driver", SWT.NULL);
		txtClass = dlg.text(panel, "", GridData.FILL_HORIZONTAL);

		dlg.label(panel,"URL", SWT.NULL);
		txtURL = dlg.text(panel, "", GridData.FILL_HORIZONTAL);
		
		dlg.label(panel,"Default server", SWT.NULL);
		txtServer = dlg.text(panel,"", GridData.FILL_HORIZONTAL);

		dlg.label(panel,"Default port", SWT.NULL);
		txtPort = dlg.text(panel,"", GridData.FILL_HORIZONTAL);

		dlg.label(panel,"Default Database", SWT.NULL);
		txtDatabase = dlg.text(panel, "", GridData.FILL_HORIZONTAL);
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
				Driver drv = selectedDriver;
				if (drv==null) drv = new Driver(txtName.getText());
				else {
					drv.loaded=false;
					drv.setName(txtName.getText());
				}
				drv.setProperty(Driver.PROP_CLASS,txtClass.getText());
				drv.setProperty(Driver.PROP_SERVER,txtServer.getText());
				drv.setProperty(Driver.PROP_PORT,txtPort.getText());
				drv.setProperty(Driver.PROP_DB,txtDatabase.getText());
				drv.setProperty(Driver.PROP_URL, txtURL.getText());
				drv.updateConnections();
				if (selectedDriver==null) DriverManager.addDriver(drv);
				DriverManager.loadDrivers();
				SqlAdmin.saveConfiguration();
				selectedDriver = drv;
				updateDriverList(lstDrivers);
			}
		});

		btn = new Button(panel,SWT.PUSH);
		btn.setText("Delete");
		btn.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {};
			public void widgetSelected(SelectionEvent e) {
				if (selectedDriver!=null) {
					if (selectedDriver.isEmpty()){
						DriverManager.removeDriver(selectedDriver);
						SqlAdmin.saveConfiguration();
						selectedDriver=null;
						updateDriverList(lstDrivers);
						lstDrivers.select(0);
						setActiveDriver(lstDrivers.getText());
					} else {
						GUIUtil.alert("This driver still contains active connections, remove them first");
					}			
				}
			}
		});	
			
		btn = new Button(panel,SWT.PUSH);
		btn.setText("Close");
		btn.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {};
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
	}	
}
