package org.treez.data.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetProcessor {

	void process(ResultSet resultSet) throws SQLException;

}
