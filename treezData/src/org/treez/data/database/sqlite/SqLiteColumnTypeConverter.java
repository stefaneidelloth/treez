package org.treez.data.database.sqlite;

import org.treez.core.data.column.ColumnType;
import org.treez.core.data.column.ColumnTypeConverter;

public class SqLiteColumnTypeConverter implements ColumnTypeConverter {

	//#region METHODS

	@Override
	public ColumnType getType(String databaseColumnType) {

		switch (databaseColumnType) {
		case "INTEGER":
			return ColumnType.INTEGER;
		case "REAL":
			return ColumnType.DOUBLE;
		case "TEXT":
			return ColumnType.STRING;
		case "BLOB":
			return ColumnType.STRING;
		default:
			throw new IllegalStateException("Unknown sqlite column type '" + databaseColumnType + "'");
		}
	}

	//#end region

}
