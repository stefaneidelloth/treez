package org.treez.core.atom.copy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.treez.core.data.row.Row;
import org.treez.core.data.table.TreezTable;

/**
 * Provides method that help to copy atoms
 */
public final class CopyHelper {

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private CopyHelper() {
	}

	//#end region

	//#region METHODS

	//#region ATTRIBUTE VALUE

	/**
	 * Copies the given attribute value (a simple value, a Copiable, a list of
	 * simple values or a list of Copiable)
	 *
	 * @param <T>
	 * @param valueToCopy
	 * @return
	 */
	public static <T> T copyAttributeValue(T valueToCopy) {
		boolean isList = valueToCopy instanceof List;
		if (isList) {
			T value = copyListAttributeValue(valueToCopy);
			return value;
		} else {
			T value = copySingleAttributeValue(valueToCopy);
			return value;
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T copyListAttributeValue(T valueToCopy) {
		boolean isList = valueToCopy instanceof List;
		if (isList) {
			//loop through the list and copy each item of the list
			List<Object> listToCopy = (List<Object>) valueToCopy;
			List<Object> list = new ArrayList<>();
			for (Object itemToCopy : listToCopy) {
				Object item = copyAttributeValue(itemToCopy);
				list.add(item);
			}
			T typedList;
			try {
				typedList = (T) list;
			} catch (ClassCastException exception) {
				String message = "Could not cast list of type '"
						+ list.getClass().getSimpleName()
						+ "' to expected type of list attribute value.";
				throw new IllegalStateException(message, exception);
			}
			return typedList;
		} else {
			//other iterables are not yet supported
			String message = "The attribute value must be a single value or of type List to be able to copy it. "
					+ "The type '" + valueToCopy.getClass().getSimpleName()
					+ "' is not yet implemented.";
			throw new IllegalStateException(message);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T copySingleAttributeValue(T valueToCopy) {

		//check for null value
		if (valueToCopy == null) {
			return null;
		}

		//check for primitive value
		boolean isPrimitiveOrString = isPrimitiveOrString(
				valueToCopy.getClass());
		if (isPrimitiveOrString) {
			return valueToCopy;
		}

		//check for Boolean
		boolean isBoolean = valueToCopy instanceof Boolean;
		if (isBoolean) {
			Boolean oldValue = (Boolean) valueToCopy;
			Boolean newValue = new Boolean(oldValue);
			return (T) newValue;
		}

		//check for Copiable
		boolean isCopiable = valueToCopy instanceof Copiable<?>;
		if (isCopiable) {
			Copiable<T> copiableToCopy;
			try {
				copiableToCopy = (Copiable<T>) valueToCopy;
			} catch (ClassCastException exception) {
				String message = "Could not cast attribute value to required type";
				throw new IllegalStateException(message, exception);
			}

			T value = copiableToCopy.copy();
			return value;
		}

		//this place should not be reached
		String message = "Could not copy value of type '"
				+ valueToCopy.getClass().getName()
				+ "'. It must implement Copiable.";
		throw new IllegalStateException(message);
	}

	/**
	 * Returns true if the given type is primitive or a string. This means the
	 * type is immutable.
	 *
	 * @param type
	 * @return
	 */
	private static boolean isPrimitiveOrString(Class<?> type) {
		return type.isPrimitive() || String.class.equals(type);
	}

	//#end region

	//#region ROWS

	/**
	 * Copies the given list of rows for the given target table
	 *
	 * @param definitionRowsToCopy
	 * @param targetTable
	 * @return
	 */
	public static List<Row> copyRowsForTargetTable(
			List<Row> definitionRowsToCopy, TreezTable targetTable) {
		Objects.requireNonNull(targetTable, "New table must not be null.");

		List<Row> rows = new ArrayList<>();
		for (Row rowToCopy : definitionRowsToCopy) {
			Row row = rowToCopy.copyForNewTable(targetTable);
			rows.add(row);
		}

		return rows;
	}

	//#end region

	//#region STRING MAP

	/**
	 * Copies the given nested string map
	 *
	 * @param mapToCopy
	 * @return
	 */
	public static Map<String, List<String>> copyNestedStringMap(
			Map<String, List<String>> mapToCopy) {

		if (mapToCopy == null) {
			return null;
		}
		Map<String, List<String>> map = new HashMap<>(mapToCopy.size());
		for (String key : mapToCopy.keySet()) {
			List<String> entryToCopy = mapToCopy.get(key);
			List<String> entry = new ArrayList<>(entryToCopy); //shallow copy is enough here since String is Immutable
			map.put(key, entry);
		}
		return map;
	}

	//#end region

	//#end region

}
