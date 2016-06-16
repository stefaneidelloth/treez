package org.treez.study.atom;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.utils.Utils;
import org.treez.model.atom.AbstractModel;
import org.treez.model.interfaces.Model;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.results.Results;

/**
 * Parent class for parameter variations
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public abstract class AbstractParameterVariation extends AdjustableAtom implements Study {

	private static final Logger LOG = Logger.getLogger(AbstractParameterVariation.class);

	//#region ATTRIBUTES

	/**
	 * Date format for progress information that is logged to the console
	 */
	protected final String DATE_FORMAT_STRING = "yyy-MM-dd HH:mm:ss";

	/**
	 * The path to the model that will be executed by the sweep
	 */
	public final Attribute<String> modelToRunModelPath = new Wrap<>();

	/**
	 * The path to the model that provides the variables/parameters that can be varied
	 */
	public final Attribute<String> sourceModelPath = new Wrap<>();

	/**
	 * If this is true, a text file with information about the study will be exported to the specified export path
	 */
	public final Attribute<Boolean> exportStudyInfo = new Wrap<>();

	/**
	 * The path where study info will be exported if the export option is enabled
	 */
	public final Attribute<String> exportStudyInfoPath = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	public AbstractParameterVariation(String name) {
		super(name);
		setRunnable();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the results atom if it does not yet exist
	 */
	protected void createResultsAtomIfNotExists() {
		String resultAtomPath = "root.results";
		boolean resultAtomExists = this.rootHasChild(resultAtomPath);
		if (!resultAtomExists) {
			Results results = new Results("results");
			AbstractAtom<?> root = this.getRoot();
			root.addChild(results);
			LOG.info("Created " + resultAtomPath + " for sweep output.");
		}
	}

	/**
	 * Creates the data atom if it does not yet exist
	 */
	protected void createDataAtomIfNotExists() {
		String resultAtomPath = "root.results";
		String dataAtomName = "data";
		String dataAtomPath = createOutputDataAtomPath();
		boolean dataAtomExists = this.rootHasChild(dataAtomPath);
		if (!dataAtomExists) {
			Data data = new Data(dataAtomName);
			AbstractAtom<?> results = this.getChildFromRoot(resultAtomPath);
			results.addChild(data);
			LOG.info("Created " + dataAtomPath + " for sweep output.");
		}
	}

	/**
	 * Creates the path for the data atom that will be the parent of the study output atom
	 *
	 * @return
	 */
	protected static String createOutputDataAtomPath() {
		String dataAtomPath = "root.results.data";
		return dataAtomPath;
	}

	/**
	 * Creates the name for the sweep output atom
	 *
	 * @return
	 */
	protected String createStudyOutputAtomName() {
		String sweepOutpuAtomName = getName() + "Output";
		return sweepOutpuAtomName;
	}

	/**
	 * Creates the path for the sweep output atom
	 *
	 * @return
	 */
	protected String getStudyOutputAtomPath() {
		String sweepOutputAtomPath = createOutputDataAtomPath() + "." + createStudyOutputAtomName();
		return sweepOutputAtomPath;
	}

	/**
	 * Retrieves the model that should be executed by this sweep using the modelToRunModelPath
	 *
	 * @return
	 */
	protected Model getModelToRun() {
		String modelToRunModelPathString = modelToRunModelPath.get();
		try {
			Model model = this.getChildFromRoot(modelToRunModelPathString);
			return model;
		} catch (IllegalArgumentException exception) {
			String message = "The model path '" + modelToRunModelPathString + "' does not point to a valid model.";
			throw new IllegalStateException(message, exception);
		}
	}

	/**
	 * Retrieves the model that provides the variables using the sourceModelPath
	 *
	 * @return
	 */
	protected AbstractModel getSourceModelAtom() {
		String sourcePath = sourceModelPath.get();
		if (sourcePath == null) {
			return null;
		}
		try {
			AbstractModel modelAtom = this.getChildFromRoot(sourcePath);
			return modelAtom;
		} catch (IllegalArgumentException exception) {
			String message = "The model path '" + sourcePath + "' does not point to a valid model.";
			throw new IllegalStateException(message, exception);
		}
	}

	protected void logModelStartMessage(int counter, double startTime, int numberOfSimulations) {

		//get current time
		Double currentTime = Double.parseDouble("" + System.currentTimeMillis());
		String currentDateString = millisToDateString(currentTime);

		//estimate end time
		String endTimeString = estimateEndTime(startTime, currentTime, counter, numberOfSimulations);

		//log start message
		String message = "-- " + currentDateString + " --- Simulation " + counter + " of " + numberOfSimulations
				+ " -------- " + endTimeString + " --";
		LOG.info(message);

	}

	/**
	 * Logs the end message
	 */
	protected void logAndShowSweepEndMessage() {
		//get final time
		double currentTime = Double.parseDouble("" + System.currentTimeMillis());
		String finalDateString = millisToDateString(currentTime);

		//log message
		String message = "-- " + finalDateString + " -------- Completed! --------------------------------";
		LOG.info(message);
		Utils.showMessage("Completed!");
	}

	/**
	 * Estimates the end time and returns it as date string
	 *
	 * @param startTime
	 * @param currentTime
	 * @param counter
	 * @param numberOfSimulations
	 * @return
	 */
	private String estimateEndTime(double startTime, double currentTime, int counter, int numberOfSimulations) {
		Double timeDifference = Double.parseDouble("" + (currentTime - startTime));
		int numberOfFinishedSimulations = (counter - 1);
		Double estimatedTimePerSimulation = Double.NaN;
		if (numberOfFinishedSimulations != 0) {
			estimatedTimePerSimulation = timeDifference / numberOfFinishedSimulations;
		}
		int numberOfRemainingSimulations = numberOfSimulations - numberOfFinishedSimulations;
		Double estimatedRemainingTime = estimatedTimePerSimulation * numberOfRemainingSimulations;
		Double estimatedEndTime = currentTime + estimatedRemainingTime;
		String endTimeString = millisToDateString(estimatedEndTime);
		return endTimeString;
	}

	/**
	 * Converts the given time (as provided by System.currentTimeMillis()) to a date string
	 *
	 * @param timeInMilliseconds
	 * @return
	 */
	private String millisToDateString(Double timeInMilliseconds) {
		if (timeInMilliseconds.isNaN()) {
			return "  not yet estimated";
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
			Long longTime = timeInMilliseconds.longValue();
			Date date = new Date(longTime);
			String dateString = dateFormat.format(date);
			return dateString;
		}
	}

	//#end region

	//#region ACCESSORS

	//#region SOURCE

	@Override
	public String getSourceModelPath() {
		return sourceModelPath.get();
	}

	//#end region

	//#region MODEL TO RUN

	@Override
	public String getModelToRunModelPath() {
		return modelToRunModelPath.get();
	}

	//#end region

	//#end region

}
