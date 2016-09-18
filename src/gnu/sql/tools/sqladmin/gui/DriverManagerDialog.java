package gnu.sql.tools.sqladmin.gui;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

import gnu.sql.tools.sqladmin.*;
import java.util.*;

public class DriverManagerDialog {
	
	private Shell shell;
	private Vector items;
	private org.eclipse.swt.widgets.List list = null;
	private boolean saved = false;
	String fileExtensions[] = {"*.jar"};
	Button btnModify = null;
	Button btnRemove = null;
	Vector retValue = new Vector();
	
	public DriverManagerDialog(Vector items){
		shell = null;
		this.items = items;
	}
	
	public boolean open(){
		shell = new Shell(SqlAdmin.display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("Driver Class Path");
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = layout.marginWidth = 2;
		shell.setLayout(layout);
		createPathList(shell);
		createDialogButtons(shell);
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!SqlAdmin.display.readAndDispatch()) SqlAdmin.display.sleep();
		}
		return saved;
	}
	
	public void createPathList(Composite parent){
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = layout.marginWidth = 2;
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		list = new org.eclipse.swt.widgets.List(panel, SWT.BORDER);
		list.setLayoutData(new GridData(GridData.FILL_BOTH));
		for (int i=0;i<items.size(); i++){
			list.add(items.elementAt(i).toString());
		}
		createActionButtons(panel);
		enableModifyRemove(list.getSelectionIndex()>=0);
		list.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				enableModifyRemove(list.getSelectionIndex()>=0);
			}
		});
	}

	public void createActionButtons(Composite parent){
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = layout.marginWidth = 2;
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		Button btn;
		btn = new Button(panel, SWT.PUSH);
		btn.setText("Add...");
		btn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				dlg.setText("Select JAR file to add");
				String fileName = dlg.open();
				if (fileName!=null) list.add(fileName);
				enableModifyRemove(list.getSelectionIndex()>=0);
			}
		});
		
		btnModify = new Button(panel, SWT.PUSH);
		btnModify.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btnModify.setText("Modify...");
		btnModify.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				dlg.setText("Select JAR file to replace");
				dlg.setFileName(list.getItem(list.getSelectionIndex()));
				String fileName = dlg.open();
				if (fileName!=null) list.setItem(list.getSelectionIndex(),fileName);
				enableModifyRemove(list.getSelectionIndex()>=0);
			}
		});

		btnRemove = new Button(panel, SWT.PUSH);
		btnRemove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btnRemove.setText("Remove");
		btnRemove.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				list.remove(list.getSelectionIndex());
				enableModifyRemove(list.getSelectionIndex()>=0);
			}
		});
	}

	public void createDialogButtons(Composite parent){
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = layout.marginWidth = 2;
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btn;
		btn = new Button(panel, SWT.PUSH);
		btn.setText("Apply");
		btn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		btn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				saved = true;
				saveItems();
				shell.close();
			}
		});
				
		btn = new Button(panel, SWT.PUSH);
		btn.setText("Cancel");
		btn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		btn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				saved = false;
				shell.close();
			}
		});
	}
	
	public Vector getItems(){
		return retValue;
	}
	
	protected void saveItems(){
		retValue = new Vector();
		String items[] = list.getItems();
		for (int i=0;i<items.length;i++){
			retValue.addElement(items[i]);
		}
	}
	
	public void enableModifyRemove(boolean enable){
		btnModify.setEnabled(enable);
		btnRemove.setEnabled(enable);
	}
	
}
