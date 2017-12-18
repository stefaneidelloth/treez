package org.treez.data.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.treez.data.database.ResultSetProcessor;

public class MySqlDatabase {

	//#region ATTRIBUTES

	private String url;

	private String user;

	private String password;

	//#end region

	//#region CONSTRUCTORS

	public MySqlDatabase(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
		checkConnection();

	}

	//#end region

	//#region METHODS

	private void checkConnection() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException driverException) {
			String message = "Could not establish MySql database connection due to missing driver.";
			throw new IllegalStateException(message, driverException);
		}

		try (
				Connection connection = DriverManager.getConnection("jdbc:mysql://" + url, user,
						password);) {} catch (SQLException exception) {
			String message = "Could not establish MySql database connection to " + url;
			throw new IllegalStateException(message, exception);
		}

	}

	/**
	 * Executes a query that does not return a result
	 */
	public void execute(String query) {
		try (
				Connection connection = DriverManager.getConnection("jdbc:mysql://" + url, user, password);
				Statement statement = connection.createStatement();) {
			statement.executeUpdate(query);
		} catch (SQLException exception) {
			String message = "Could not execute query " + query;
			throw new IllegalStateException(message, exception);
		}
	}

	/**
	 * Executes a query and processes its ResultSet
	 */
	public void executeAndProcess(String query, ResultSetProcessor processor) {
		try (
				Connection connection = DriverManager.getConnection("jdbc:mysql://" + url, user, password);
				Statement statement = connection.createStatement();) {
			ResultSet resultSet = statement.executeQuery(query);
			processor.process(resultSet);
		} catch (SQLException exception) {
			String message = "Could not execute and process query " + query;
			throw new IllegalStateException(message, exception);
		}
	}

	//#end region

}
