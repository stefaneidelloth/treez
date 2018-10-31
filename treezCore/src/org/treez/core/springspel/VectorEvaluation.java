package org.treez.core.springspel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Evaluates a string as a list of double values
 */
public class VectorEvaluation {

	private static final Logger LOG = LogManager.getLogger(VectorEvaluation.class);

	//#region ATTRIBUTES

	private ExpressionParser parser;

	private StandardEvaluationContext context;

	//#end region

	//#region CONSTRUCTORS

	public VectorEvaluation() {

		//create parser
		parser = new SpelExpressionParser();

		//create context
		context = new StandardEvaluationContext(this);

		//add range method to context
		registerRangeMethod();

		//add NaN to context
		context.setVariable("NaN", Double.NaN);

	}

	//#end region

	//#region METHODS

	private void registerRangeMethod() {
		Method rangeMethod = null;
		try {
			rangeMethod = this.getClass().getDeclaredMethod("range",
					new Class[] { Number.class, Number.class, Number.class });
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		context.registerFunction("range", rangeMethod);
	}

	/**
	 * The range method: creates a range from min, max, step
	 *
	 * @param minNumber
	 * @param maxNumber
	 * @param stepNumber
	 * @return
	 */
	public static List<Double> range(Number minNumber, Number maxNumber, Number stepNumber) {

		//check whether max >= min
		Double min = minNumber.doubleValue();
		Double max = maxNumber.doubleValue();
		Double step = stepNumber.doubleValue();
		if (max < min) {
			throw new IllegalArgumentException("Second parameter max has to be >= first parameter min.");
		}

		//check whether step > 0
		if (step <= 0) {
			throw new IllegalArgumentException("Third parameter step has to be > 0.");
		}

		//create vector
		List<Double> result = new ArrayList<>();
		Double currentValue = min;
		while (currentValue <= max) {
			result.add(currentValue);
			currentValue += step;
		}
		return result;
	}

	/**
	 * Parses a string to a list of double values.
	 *
	 * @param valueString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Double> parseStringToDoubleList(String valueString) {

		//replace NaN with #NaN
		String adaptedValueString = valueString.replace("NaN", "#NaN");

		//parse
		Expression expression = null;
		try {
			expression = parser.parseExpression(adaptedValueString);
		} catch (NullPointerException exception) {

		}

		if (expression == null) {
			String message = "Could not parse the value string '" + valueString
					+ "' to a Double list. Returning  a list containing a single Double.NaN value.";
			LOG.warn(message);
			List<Double> nanList = new ArrayList<>();
			nanList.add(Double.NaN);
			return nanList;
		}

		//evaluate
		Object result = expression.getValue(context, List.class);

		//post process result
		if (result != null) {
			boolean isList = result instanceof List<?>;
			if (isList) {
				List<Double> resultList = (List<Double>) result;
				List<Double> doubleResultList = new ArrayList<>();
				boolean isEmptyList = resultList.isEmpty();
				if (!isEmptyList) {
					//convert elements to doubles (might be in fact integers)
					for (Object value : resultList) {
						Number numberValue = (Number) value;
						Double doubleValue = numberValue.doubleValue();
						doubleResultList.add(doubleValue);
					}
					return doubleResultList;
				}
				return resultList;
			} else {
				boolean isSingleValue = result instanceof Double;
				if (isSingleValue) {
					return createSingleDoubleListFromResult(result);
				} else {
					String message = "Could not parse valueString to double since the resutl type '"
							+ result.getClass().getSimpleName() + "' is not yet implemented.";
					throw new IllegalArgumentException(message);
				}

			}
		} else {
			String message = "Could not parse the value string '" + valueString
					+ "' to a Double list. Returning  a list containing a single Double.NaN value.";
			LOG.warn(message);
			List<Double> nanList = new ArrayList<>();
			nanList.add(Double.NaN);
			return nanList;
		}

	}

	/**
	 * Parses a string to a list of integer values.
	 *
	 * @param valueString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> parseStringToIntegerList(String valueString) {

		//replace NaN with #NaN
		String adaptedValueString = valueString.replace("NaN", "#NaN");

		//parse
		Expression expression = null;
		try {
			expression = parser.parseExpression(adaptedValueString);
		} catch (NullPointerException exception) {

		}

		if (expression == null) {
			String message = "Could not parse the value string '" + valueString
					+ "' to an Integer list. Returning  a list containing a single null value.";
			LOG.warn(message);
			List<Integer> nanList = new ArrayList<>();
			nanList.add(null);
			return nanList;
		}

		//evaluate
		Object result = expression.getValue(context, List.class);

		//post process result
		if (result != null) {
			boolean isList = result instanceof List<?>;
			if (isList) {
				List<Integer> resultList = (List<Integer>) result;
				List<Integer> integerResultList = new ArrayList<>();
				boolean isEmptyList = resultList.isEmpty();
				if (!isEmptyList) {
					//convert elements to Integers (might be in fact doubles)
					for (Object value : resultList) {
						Number numberValue = (Number) value;
						Integer integerValue = numberValue.intValue();
						integerResultList.add(integerValue);
					}
					return integerResultList;
				}
				return resultList;
			} else {
				boolean isSingleValue = result instanceof Integer;
				if (isSingleValue) {
					return createSingleIntegerListFromResult(result);
				} else {
					String message = "Could not parse valueString to Integer since the resutl type '"
							+ result.getClass().getSimpleName() + "' is not yet implemented.";
					throw new IllegalArgumentException(message);
				}

			}
		} else {
			String message = "Could not parse the value string '" + valueString
					+ "' to an Integer list. Returning  a list containing a single null value.";
			LOG.warn(message);
			List<Integer> nanList = new ArrayList<>();
			nanList.add(null);
			return nanList;
		}

	}

	/**
	 * Creates a Double list containing a single Double value which is passed as an object.
	 *
	 * @param result
	 * @return
	 */
	private static List<Double> createSingleDoubleListFromResult(Object result) {
		List<Double> singleValueList = new ArrayList<>();
		Number numberValue = (Number) result;
		singleValueList.add(numberValue.doubleValue());
		return singleValueList;
	}

	/**
	 * Creates an Integer list containing a single Integer value which is passed as an object.
	 *
	 * @param result
	 * @return
	 */
	private static List<Integer> createSingleIntegerListFromResult(Object result) {
		List<Integer> singleValueList = new ArrayList<>();
		Number numberValue = (Number) result;
		singleValueList.add(numberValue.intValue());
		return singleValueList;
	}

	/**
	 * Creates a display string for the given Double list
	 *
	 * @param doubleList
	 * @return
	 */
	public static String doubleListToDisplayString(List<Double> doubleList) {

		Objects.requireNonNull(doubleList, "VectorList must not be null.");

		boolean isEmpty = doubleList.isEmpty();
		if (isEmpty) {
			return "{}";
		}

		boolean hasSingleEntry = (doubleList.size() == 1);
		if (hasSingleEntry) {
			String vectorString = "{" + doubleList.get(0) + "}";
			return vectorString;
		}

		List<String> items = new ArrayList<>();
		for (Double doubleItem : doubleList) {
			items.add("" + doubleItem);
		}

		String vectorString = "{" + String.join(",", items) + "}";
		return vectorString;

	}

	/**
	 * Creates a display string for the given Integer list
	 *
	 * @param valueList
	 * @return
	 */
	public static String integerListToDisplayString(List<Integer> valueList) {
		Objects.requireNonNull(valueList, "VectorList must not be null.");

		boolean isEmpty = valueList.isEmpty();
		if (isEmpty) {
			return "{}";
		}

		boolean hasSingleEntry = (valueList.size() == 1);
		if (hasSingleEntry) {
			String vectorString = "{" + valueList.get(0) + "}";
			return vectorString;
		}

		List<String> items = new ArrayList<>();
		for (Integer doubleItem : valueList) {
			items.add("" + doubleItem);
		}

		String vectorString = "{" + String.join(",", items) + "}";
		return vectorString;
	}

	//#end region

}
