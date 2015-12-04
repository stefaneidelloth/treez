package org.treez.core.springspel;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.treez.testutils.TestUtils;

/**
 * Demonstrates the evaluation of strings that represent ranges / vectors with JEP
 */
public class VectorEvaluationTest {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(VectorEvaluationTest.class);

	/**
	 * tests the vector evaluation
	 */
	@Test
	public void testVectorEvaluation() {

		//initialize logging
		TestUtils.initializeLogging();

		//prepare VectorEvaluation
		VectorEvaluation vectorEvaluation = new VectorEvaluation();

		//define expression
		String vectorExpression1 = "range(0.0,10.0,2.0)";

		//evaluate expression
		List<Double> vectorList1 = vectorEvaluation.parseStringToDoubleList(vectorExpression1);

		//show result
		String displayString = VectorEvaluation.doubleListToDisplayString(vectorList1);
		sysLog.info("Result1: " + displayString);

		//define expression
		String vectorExpression2 = "range(0.0,3.0,1.0)";

		//evaluate expression
		List<Double> vectorList2 = vectorEvaluation.parseStringToDoubleList(vectorExpression2);

		//show result
		String displayString2 = VectorEvaluation.doubleListToDisplayString(vectorList2);
		sysLog.info("Result2: " + displayString2);

		//define expression
		String vectorExpression3 = "{0.0, 10.0, 15.0, 20.0}";

		//evaluate expression
		List<Double> vectorList3 = vectorEvaluation.parseStringToDoubleList(vectorExpression3);

		//show result
		String displayString3 = VectorEvaluation.doubleListToDisplayString(vectorList3);
		sysLog.info("Result3: " + displayString3);

	}

}
