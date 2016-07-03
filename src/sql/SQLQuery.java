package sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SQLQuery {
	
	public String makeTemplate();
	
	public void prepareStatement(PreparedStatement prepStmt) throws SQLException;
	
}
