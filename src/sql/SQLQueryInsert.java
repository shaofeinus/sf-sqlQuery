package sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SQLQueryInsert implements SQLQuery {

	private static final Logger logger = Logger.getLogger(SQLQueryInsert.class
			.getName());

	private static final String FIELD_COLUMNS = "<columns>";
	private static final String FIELD_TABLE = "<table>";
	private static final String FIELD_VALUES = "<values>";

	private String template;
	private String tableName;

	private StringBuilder columnField;
	private StringBuilder columnPlaceHolders;
	private List<String> columnStringVals;

	public SQLQueryInsert() {
		init();
	}

	private void init() {
		template = "INSERT INTO " + FIELD_TABLE + " " + FIELD_COLUMNS
				+ " VALUES " + FIELD_VALUES;

		columnField = new StringBuilder();
		columnStringVals = new ArrayList<String>();
		columnPlaceHolders = new StringBuilder();
	}

	public void setTable(String tableName) {
		this.tableName = tableName;
	}

	public void setColumn(Map<String, String> colValues) {
		for (Map.Entry<String, String> entry : colValues.entrySet()) {
			columnField.append(entry.getKey() + ",");
			columnPlaceHolders.append("?,");
			columnStringVals.add(entry.getValue());
		}
		columnField.replace(
				columnField.length() - 1, 
				columnField.length(), "");
		columnPlaceHolders.replace(
				columnPlaceHolders.length() - 1,
				columnPlaceHolders.length(), "");
	}

	@Override
	public String makeTemplate() {
		// Set table name
		if (tableName == null || tableName.equals("")) {
			logger.severe("No table name specified");
			return null;
		} else
			template = template.replace(FIELD_TABLE, tableName);

		// Set column names
		if (columnField.length() == 0) {
			logger.severe("No values inserted");
			return null;
		} else
			template = template.replace(FIELD_COLUMNS, "(" + columnField + ")");

		// Set values place holders
		if (columnStringVals.isEmpty()) {
			logger.severe("No values inserted");
			return null;
		} else
			template = template.replace(FIELD_VALUES, "(" + columnPlaceHolders + ")");

		return template;
	}

	@Override
	public void prepareStatement(PreparedStatement prepStmt) throws SQLException {
		for(int i = 0; i < columnStringVals.size(); i++)
			prepStmt.setString(i + 1, columnStringVals.get(i));
	}

}
