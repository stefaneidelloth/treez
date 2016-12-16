package org.treez.model.atom.tableImport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.treez.core.data.column.ColumnType;

/**
 * Imports table data from a text file
 */
public final class TextDataTableImporter {

	private static final Logger LOG = Logger.getLogger(TextDataTableImporter.class);

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private TextDataTableImporter() {}

	//#end region

	//#region METHODS

	/**
	 * @param filePath
	 * @return
	 */
	public static TableData importData(String filePath, String columnSeparator, int rowLimit) {

		//read tab separated entries
		List<List<String>> data = readTextData(filePath, columnSeparator, rowLimit);

		//check data size (number of lines > 1, number of columns equal)
		checkDataSizes(data);

		//get header data
		List<String> currentHeaderData = data.get(0);

		//get row data
		List<List<String>> currentRowData = data.subList(1, data.size());

		TableData tableData = new TableData() {

			@Override
			public List<String> getHeaderData() {
				return currentHeaderData;
			}

			@Override
			public ColumnType getColumnType(String header) {
				return ColumnType.TEXT;
			}

			@Override
			public List<List<String>> getRowData() {
				return currentRowData;
			}
		};

		return tableData;
	}

	/**
	 * Checks if the data has at least two rows (header and data) and if the number of columns is equal for each row
	 *
	 * @param data
	 */
	private static void checkDataSizes(List<List<String>> data) {
		int numberOfLines = data.size();

		//check number of lines
		if (numberOfLines < 2) {
			throw new IllegalStateException("The text file must contain at least two lines");
		}

		//get number of columns from first line
		int numberOfColumns = data.get(0).size();

		//check number of columns of all lines
		int lineCounter = 1;
		for (List<String> entries : data) {
			int currentNumberOfColumns = entries.size();
			boolean hasSameNumberOfColumns = (currentNumberOfColumns == numberOfColumns);
			if (!hasSameNumberOfColumns) {
				String message = "The number of columns in line " + lineCounter + " has to be " + numberOfColumns
						+ " but is " + currentNumberOfColumns + ".";
				throw new IllegalStateException(message);
			}
			lineCounter++;
		}

	}

	/**
	 * Reads data from a text file
	 *
	 * @param filePath
	 * @return
	 */
	private static List<List<String>> readTextData(String filePath, String columnSeparator, int rowLimit) {

		List<List<String>> lines = new ArrayList<>();
		int rowCount = 0;
		try (
				BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null && rowCount < rowLimit) {
				String[] lineEntries = line.split(columnSeparator);
				lines.add(Arrays.asList(lineEntries));
				rowCount++;
			}
		} catch (IOException exception) {
			String message = "Could not read text file '" + filePath + "'";
			LOG.error(message, exception);
			throw new IllegalStateException(message, exception);
		}

		return lines;
	}

	//#end region

}
