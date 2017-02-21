package org.treez.data.database.mysql;

import org.treez.core.data.column.ColumnType;
import org.treez.core.data.column.ColumnTypeConverter;

public class MySqlColumnTypeConverter implements ColumnTypeConverter {

	//#region METHODS

	@Override
	public ColumnType getType(String databaseColumnType) {

		switch (databaseColumnType) {
		case "int":
			return ColumnType.INTEGER;
		case "integer":
			return ColumnType.INTEGER;
		case "tinyint":
			return ColumnType.INTEGER;
		case "bool":
			return ColumnType.INTEGER;
		case "boolean":
			return ColumnType.INTEGER;
		case "float":
			return ColumnType.DOUBLE;
		case "double":
			return ColumnType.DOUBLE;
		case "char":
			return ColumnType.STRING;
		case "varchar":
			return ColumnType.STRING;
		default:
			throw new IllegalStateException(
					"The mysql column type '" + databaseColumnType + "' is not yet implemented.");
		}
	}

	//#end region

}
