package org.treez.core.springspel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Evaluates a string as a list of double values
 */
public class VectorEvaluation {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(VectorEvaluation.class);

	//#region ATTRIBUTES

	private ExpressionParser parser;

	private StandardEvaluationContext context;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 */
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
			//TODO Auto-generated catch block
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
		Expression expression = parser.parseExpression(adaptedValueString);

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
					+ "' to a double list. Returning  a list containing a single Double.NaN value.";
			sysLog.warn(message);
			List<Double> nanList = new ArrayList<>();
			nanList.add(Double.NaN);
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
	 * Creates a display string for the given Double list
	 *
	 * @param doubleList
	 * @return
	 */
	public static String doubleListToDisplayString(List<Double> doubleList) {

		Objects.requireNonNull(doubleList, "VectorList must not be null.");

		boolean isEmpty = doubleList.isEmpty();
		if (isEmpty) {
			return "";
		}

		boolean hasSingleEntry = (doubleList.size() == 1);
		if (hasSingleEntry) {
			String vectorString = "" + doubleList.get(0);
			return vectorString;
		}

		List<String> items = new ArrayList<>();
		for (Double doubleItem : doubleList) {
			items.add("" + doubleItem);
		}

		String vectorString = "[" + String.join(",", items) + "]";
		return vectorString;

	}

	//#end region

}
