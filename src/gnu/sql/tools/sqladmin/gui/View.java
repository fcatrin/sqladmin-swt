package gnu.sql.tools.sqladmin.gui;

import org.eclipse.swt.widgets.*;

public abstract class View {

	public static final int CAN_CLOSE = 0;
	public static final int CAN_REFRESH = 1;
	public static final int CAN_SAVE = 2;
	public static final int CAN_SQL = 3;
	
	public static final int ACT_IGNORE = 0;
	public static final int ACT_ENABLE = 1;
	public static final int ACT_DISABLE = 2;
	
	public Composite panel = null;
	
	abstract public int getAction(int action);
	abstract public void doAction(int action);
	
}