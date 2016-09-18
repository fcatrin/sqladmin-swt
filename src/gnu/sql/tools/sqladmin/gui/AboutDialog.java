package gnu.sql.tools.sqladmin.gui;

import gnu.sql.tools.sqladmin.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

public class AboutDialog extends Dialog{

	private String getText(){
		StringBuffer text = new StringBuffer("");
		text.append(SqlAdmin.name + "\n");
		text.append("A simple multi database client tool\n\n");
		text.append("http://sqladmin.sf.net\n");
		text.append("Released under LGPL license");
		return text.toString();
	}
	
	private Widget getPanel(Composite parent){
		Composite panel = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayout(layout);

		Label image = new Label(panel, SWT.NONE);
		image.setImage(GUIUtil.getIcon("app.gif"));
		
		label(panel, getText(), SWT.NONE);
		return panel;
	}
	
	public void open(){
		final Shell shell = new Shell(SqlAdmin.display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("About SQLAdmin");
		
		shell.setLayout(new GridLayout());

		getPanel(shell);
		
		Button btn = new Button(shell, SWT.NULL);
		btn.setText("Close");
		btn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		btn.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {};
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		
		shell.pack();
		shell.open();
	}
}
