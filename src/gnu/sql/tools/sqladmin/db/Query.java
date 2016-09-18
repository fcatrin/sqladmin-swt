package gnu.sql.tools.sqladmin.db;

import gnu.sql.tools.sqladmin.*;

public class Query {
	private Connection con;
	private boolean busy;
	private java.sql.Statement stmt;
	private int maxRows=0;
	private boolean isResultSet = false;
	private boolean hasResult = false;
	private String sql="";
	private String title="";
	private int nResults = 0;
	private java.sql.ResultSet rs = null;
	private IResultSetHandler resultSetHandler = null;
	private static int nResult = 0;
	
	public Query(Connection con, String name, int maxRows) {
		this.con = con;
		this.maxRows = maxRows;
		this.title = name;
	}
	
	public boolean isBusy(){
		return busy;
	}
	
	public boolean execSQL (){
		if (busy) {
			SqlAdmin.log("A statement is already running...");
			return false;
		}

		try {
			busy = true;
			con.connect();
			stmt = con.db.createStatement();
			stmt.setMaxRows(maxRows);
			isResultSet = stmt.execute(sql);
			hasResult = (isResultSet || stmt.getUpdateCount()>=0);
			if (hasResult) processResult();
			if (!hasResult) busy = false;
			return true;
		} catch (Exception e){
			SqlAdmin.log(e.toString());
			return false;
		}		
	}
		
	public void processResult() {
		boolean hasMultipleResults = false;
		
		try {
			while (isResultSet || stmt.getUpdateCount()>=0){
				if (isResultSet){
					rs = stmt.getResultSet();
					if (title==null) title = "Query " + (++nResult);
					if (resultSetHandler!=null)  resultSetHandler.process(this);
					
					isResultSet = stmt.getMoreResults();
					hasMultipleResults |= (isResultSet ||  (stmt.getUpdateCount()>=0));
				} else {
					SqlAdmin.log(stmt.getUpdateCount() + " row(s) affected");
					isResultSet = stmt.getMoreResults();
				}
			}
			stmt.close();
		} catch (Exception e){
			SqlAdmin.log(e.toString());
		}
		stmt = null;
		hasResult = false;
		busy = false;
	}
	
	public void setResultSetHandler(IResultSetHandler resultSetHandler){
		this.resultSetHandler = resultSetHandler;
	}
	
	public java.sql.ResultSet getResultSet() {
		return rs;
	}
	
	public String getSql(){
		return sql;
	}
	
	public void setSql(String sql){
		this.sql = sql;
	}
	
	public String getTitle(){
		return title;
	}
}
