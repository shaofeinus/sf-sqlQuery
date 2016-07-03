package sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SQLQuerySelect implements SQLQuery {
	
	private static final Logger logger = Logger.getLogger(SQLQuerySelect.class.getName());
	
	private static final String FIELD_COLUMNS = "<columns>";
	private static final String FIELD_TABLE = "<table>";
	private static final String FIELD_CONDITIONS = "<conditions>";
	
	private String template;
	private String tableName;
	
	private StringBuilder columnField;
	private StringBuilder conditionPlaceHolders;
	private List<String> conditionStringVals;
//	private List<Integer> conditionIntVals;
	
	
	public SQLQuerySelect() {
		init();
	}
	
	private void init() {
		template = 
				"SELECT " + FIELD_COLUMNS + 	
				" FROM " + FIELD_TABLE +
				" " + FIELD_CONDITIONS;
		
		conditionStringVals = new ArrayList<String>();
//		conditionIntVals = new ArrayList<Integer>();
		
		columnField = new StringBuilder();
		conditionPlaceHolders = new StringBuilder();
	}
	
	public void setTable(String tableName) {
		this.tableName = tableName;
	}
	
	public void setColumns(List<String> columnNames) {
		for(int i = 0; i < columnNames.size(); i++)
			columnField.append(columnNames.get(i) + ",");
		columnField.replace(
				columnField.length() - 1, 
				columnField.length(), "");
	}

	public void setStringConditions(Map<String, String> conditions) {
		for(Map.Entry<String, String> entry: conditions.entrySet()) {
			conditionPlaceHolders.append(entry.getKey() + "=? AND ");
			conditionStringVals.add(entry.getValue());
		}
	}
	
//	public void setIntConditions(Map<String, Integer> conditions) {
//		for(Map.Entry<String, Integer> entry: conditions.entrySet()) {
//			conditionPlaceHolders.append(entry.getKey() + "=? AND ");
//			conditionIntVals.add(entry.getValue());
//		}
//	}
	
	@Override
	public String makeTemplate() {
		// Set table name
		if(tableName == null || tableName.equals("")) {
			logger.severe("No table name specified");
			return null ;
		} else
			template = template.replace(FIELD_TABLE, tableName);
		
		// Set column names
		if(columnField.length() > 0)
			template = template.replace(FIELD_COLUMNS, columnField);
		else 
			template = template.replace(FIELD_COLUMNS, "*");
		
		// Set conditions place holders
		if(!conditionStringVals.isEmpty()) {
			conditionPlaceHolders.insert(0, "WHERE ");
			conditionPlaceHolders.replace(
					conditionPlaceHolders.length() - 5, 
					conditionPlaceHolders.length(), "");
		}
		template = template.replace(FIELD_CONDITIONS, 
				conditionPlaceHolders);

		return template;
	}

	@Override
	public void prepareStatement(PreparedStatement prepStmt) throws SQLException {
		for(int i = 0; i < conditionStringVals.size(); i++)
			prepStmt.setString(i + 1, conditionStringVals.get(i));
	}
	
	public void clear() {
		init();
	}

}
