package gnu.sql.tools.sqladmin.gui;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import java.util.*;

class Dialog extends Object {

	public Text text(Composite parent, String value, int layout){
		Text retValue = new Text(parent, SWT.BORDER);
		retValue.setLayoutData(new GridData(layout)); 
		if (value!=null) retValue.setText(value);
		return retValue;
	}
	
	public Label label(Composite parent, String value, int style){
		Label retValue = new Label(parent,style);
		if (value!=null) retValue.setText(value);
		return retValue;
	}
	
	public void createPropertyList(Composite parent, Vector labels, Vector values){
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
		for (int i=0; i<labels.size(); i++){
			Label lbl = label(parent, labels.elementAt(i).toString(), SWT.NULL);
			lbl.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
			label(parent, values.elementAt(i).toString(), SWT.NULL);
		}
	}
}
		
		
