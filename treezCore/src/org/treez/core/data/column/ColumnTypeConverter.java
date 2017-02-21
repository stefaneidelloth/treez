package org.treez.core.data.column;

public interface ColumnTypeConverter {

	ColumnType getType(String databaseColumnType);

}
