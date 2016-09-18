package gnu.sql.tools.sqladmin.db;

import java.sql.*;

public interface IResultSetHandler {
  	public int process(Query query);
}