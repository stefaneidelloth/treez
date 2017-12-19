package org.treez.study.atom;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.comboBox.enumeration.EnumComboBox;
import org.treez.core.atom.attribute.fileSystem.FilePath;
import org.treez.core.atom.attribute.text.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.utils.Utils;
import org.treez.data.database.mysql.MySqlDatabase;
import org.treez.data.database.sqlite.SqLiteDatabase;
import org.treez.model.atom.AbstractModel;
import org.treez.model.input.ModelInput;
import org.treez.model.interfaces.Model;
import org.treez.results.atom.data.Data;
import org.treez.results.atom.results.Results;
import org.treez.study.atom.range.AbstractVariableRange;
import org.treez.study.atom.sweep.ExportStudyInfoType;

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
	 * Identifies the study (length should be <= 64 characters)
	 */
	public final Attribute<String> studyId = new Wrap<>();

	/**
	 * Describes the purpose of the study
	 */
	public final Attribute<String> studyDescription = new Wrap<>();

	/**
	 * The path to the model that will be executed by the sweep
	 */
	public final Attribute<String> modelToRunModelPath = new Wrap<>();

	/**
	 * The path to the model that provides the variables/parameters that can be varied
	 */
	public final Attribute<String> sourceModelPath = new Wrap<>();

	public final Attribute<Boolean> isConcurrentVariation = new Wrap<>();

	/**
	 * If this is true, a text file with information about the study will be exported to the specified export path
	 */
	public final Attribute<ExportStudyInfoType> exportStudyInfoType = new Wrap<>();

	/**
	 * The path where study info will be exported if the export option is enabled
	 */
	public final Attribute<String> exportStudyInfoPath = new Wrap<>();

	public final Attribute<String> host = new Wrap<>();

	public final Attribute<String> port = new Wrap<>();

	public final Attribute<String> user = new Wrap<>();

	public final Attribute<String> password = new Wrap<>();

	public final Attribute<String> schema = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	public AbstractParameterVariation(String name) {
		super(name);
		setRunnable();
	}

	//#end region

	//#region METHODS

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

	protected void logAndShowSweepEndMessage() {
		//get final time
		double currentTime = Double.parseDouble("" + System.currentTimeMillis());
		String finalDateString = millisToDateString(currentTime);

		//log message
		String message = "-- " + finalDateString + " -------- Finished! --------------------------------";
		LOG.info(message);
		Utils.showMessage("Finished!");
	}

	protected void logAndShowSweepCancelMessage() {
		//get final time
		double currentTime = Double.parseDouble("" + System.currentTimeMillis());
		String finalDateString = millisToDateString(currentTime);

		//log message
		String message = "-- " + finalDateString + " -------- Canceled! --------------------------------";
		LOG.info(message);
		Utils.showMessage("Canceled!");
	}

	/**
	 * Estimates the end time and returns it as date string
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
	 */
	public String millisToDateString(Double timeInMilliseconds) {
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

	protected void createStudyInfoSection(Page dataPage, String absoluteHelpContextId) {
		//study info
		Section studyInfoSection = dataPage.createSection("studyInfo", absoluteHelpContextId);
		studyInfoSection.setLabel("Export study info");

		//export study info combo box
		EnumComboBox<ExportStudyInfoType> exportStudyInfoTypeCombo = studyInfoSection
				.createEnumComboBox(exportStudyInfoType, this, ExportStudyInfoType.DISABLED);
		exportStudyInfoTypeCombo.setLabel("Export study information");

		//export sweep info path
		FilePath filePath = studyInfoSection.createFilePath(exportStudyInfoPath, this,
				"Target file path for study information", "");
		filePath.setValidatePath(false);

		//host
		TextField hostField = studyInfoSection.createTextField(host, this, "localhost");
		hostField.setLabel("Host name/IP address");

		//port
		TextField portField = studyInfoSection.createTextField(port, this, "3306");

		//user
		TextField userField = studyInfoSection.createTextField(user, this, "root");

		//password
		TextField passwordField = studyInfoSection.createTextField(password, this, "");

		//schema
		TextField schemaField = studyInfoSection.createTextField(schema, this, "my_schema");
		schemaField.setLabel("Schema name");

		//enable & disable fields
		exportStudyInfoTypeCombo.addModificationConsumer("updateEnabledState", () -> {

			ExportStudyInfoType exportType = exportStudyInfoType.get();
			switch (exportType) {
			case DISABLED:
				filePath.setEnabled(false);
				hostField.setEnabled(false);
				portField.setEnabled(false);
				userField.setEnabled(false);
				passwordField.setEnabled(false);
				schemaField.setEnabled(false);
				break;
			case TEXT_FILE:
				filePath.setEnabled(true);
				hostField.setEnabled(false);
				portField.setEnabled(false);
				userField.setEnabled(false);
				passwordField.setEnabled(false);
				schemaField.setEnabled(false);
				break;
			case SQLITE:
				filePath.setEnabled(true);
				hostField.setEnabled(false);
				portField.setEnabled(false);
				userField.setEnabled(false);
				passwordField.setEnabled(false);
				schemaField.setEnabled(false);
				break;
			case MYSQL:
				filePath.setEnabled(false);
				hostField.setEnabled(true);
				portField.setEnabled(true);
				userField.setEnabled(true);
				passwordField.setEnabled(true);
				schemaField.setEnabled(true);
				break;
			default:
				throw new IllegalStateException("The export type '" + exportType + "' has not yet been implemented.");
			}

		});
	}

	protected void exportStudyInfo(
			List<AbstractVariableRange<?>> variableRanges,
			List<ModelInput> modelInputs,
			int numberOfSimulations) {

		ExportStudyInfoType exportType = exportStudyInfoType.get();
		String filePath = exportStudyInfoPath.get();

		switch (exportType) {
		case DISABLED:
			//Nothing to do
			break;
		case TEXT_FILE:
			if (filePath.isEmpty()) {
				LOG.warn(
						"Export of study info to text file is enabled but no file (e.g. c:/studyInfo.txt) has been specified. Export is cancled.");
				return;
			}
			exportStudyInfoToTextFile(variableRanges, numberOfSimulations, filePath);
			return;
		case SQLITE:
			if (filePath.isEmpty()) {
				LOG.warn(
						"Export of study info to text file is enabled but no file (e.g. c:/studyInfo.txt) has been specified. Export is cancled.");
				return;
			}
			exportStudyInfoToSqLiteDatabase(variableRanges, modelInputs, filePath);
			return;
		case MYSQL:
			exportStudyInfoToMySqlDatabase(variableRanges, modelInputs);
			break;
		default:
			throw new IllegalStateException("The export type '" + exportType + "' has not yet been implemented.");
		}

	}

	protected static void exportStudyInfoToTextFile(
			List<AbstractVariableRange<?>> variableRanges,
			int numberOfSimulations,
			String filePath) {
		String studyInfo = "---------- StudyInfo ----------\r\n\r\n" + //
				"Total number of simulations:\r\n" + numberOfSimulations + "\r\n\r\n" + //
				"Variable model paths and values:\r\n\r\n";

		for (AbstractVariableRange<?> range : variableRanges) {
			String variablePath = range.getSourceVariableModelPath();
			studyInfo += variablePath + "\r\n";
			List<?> rangeValues = range.getRange();
			for (Object value : rangeValues) {
				studyInfo += value.toString() + "\r\n";
			}
			studyInfo += "\r\n";
		}

		File file = new File(filePath);

		try {
			FileUtils.writeStringToFile(file, studyInfo);
		} catch (IOException exception) {
			String message = "The specified exportStudyInfoPath '" + filePath
					+ "' is not valid. Export of study info is skipped.";
			LOG.error(message);
		}
	}

	private void exportStudyInfoToSqLiteDatabase(
			List<AbstractVariableRange<?>> variableRanges,
			List<ModelInput> modelInputs,
			String filePath) {

		SqLiteDatabase database = new SqLiteDatabase(filePath);
		writeStudyInfo(variableRanges, database);
		writeJobInfo(modelInputs, database);

	}

	private void exportStudyInfoToMySqlDatabase(
			List<AbstractVariableRange<?>> variableRanges,
			List<ModelInput> modelInputs) {

		String url = host.get() + ":" + port.get();
		String userValue = user.get();
		String passwordValue = password.get();
		String schemaValue = schema.get();

		MySqlDatabase database = new MySqlDatabase(url, userValue, passwordValue);
		writeStudyInfo(variableRanges, database, schemaValue);
		writeJobInfo(modelInputs, database, schemaValue);

	}

	private void writeStudyInfo(List<AbstractVariableRange<?>> variableRanges, SqLiteDatabase database) {
		String studyInfoTableName = "study_info";
		createStudyInfoTableIfNotExists(database, studyInfoTableName);
		deleteOldEntriesForStudyIfExist(database, studyInfoTableName);

		for (AbstractVariableRange<?> range : variableRanges) {
			String variablePath = range.getSourceVariableModelPath();
			List<?> rangeValues = range.getRange();
			for (Object value : rangeValues) {
				String query = "INSERT INTO '" + studyInfoTableName + "' VALUES(null, '" + studyId + "', '"
						+ variablePath + "','" + value + "')";
				database.execute(query);
			}
		}
	}

	private void writeStudyInfo(List<AbstractVariableRange<?>> variableRanges, MySqlDatabase database, String schema) {
		String studyInfoTableName = "study_info";
		createStudyInfoTableIfNotExists(database, schema, studyInfoTableName);
		deleteOldEntriesForStudyIfExist(database, schema, studyInfoTableName);

		for (AbstractVariableRange<?> range : variableRanges) {
			String variablePath = range.getSourceVariableModelPath();
			List<?> rangeValues = range.getRange();
			for (Object value : rangeValues) {
				String query = "INSERT INTO `" + schema + "`.`" + studyInfoTableName + "` VALUES(null, '" + studyId
						+ "', '" + variablePath + "','" + value + "')";
				database.execute(query);
			}
		}
	}

	private static void createStudyInfoTableIfNotExists(SqLiteDatabase database, String tableName) {
		String query = "CREATE TABLE IF NOT EXISTS '" + tableName
				+ "' (id INTEGER PRIMARY KEY NOT NULL, study TEXT, variable TEXT, value TEXT);";
		database.execute(query);
	}

	private static void createStudyInfoTableIfNotExists(MySqlDatabase database, String schema, String tableName) {
		String query = "CREATE TABLE IF NOT EXISTS `" + schema + "`.`" + tableName
				+ "` (id int NOT NULL AUTO_INCREMENT, study TEXT, variable TEXT, value TEXT, PRIMARY KEY(id));";
		database.execute(query);
	}

	private void deleteOldEntriesForStudyIfExist(SqLiteDatabase database, String tableName) {
		String query = "DELETE FROM '" + tableName + "' WHERE study = '" + studyId + "';";
		database.execute(query);
	}

	private void deleteOldEntriesForStudyIfExist(MySqlDatabase database, String schema, String tableName) {
		String query = "DELETE FROM `" + schema + "`.`" + tableName + "` WHERE study = '" + studyId + "';";
		database.execute(query);
	}

	private void writeJobInfo(List<ModelInput> modelInputs, SqLiteDatabase database) {
		String jobInfoTableName = "job_info";
		createJobInfoTableIfNotExists(database, jobInfoTableName);
		deleteOldEntriesForStudyIfExist(database, jobInfoTableName);
		for (ModelInput modelInput : modelInputs) {
			String jobId = modelInput.getJobId();
			List<String> variablePaths = modelInput.getAllVariableModelPaths();
			for (String variablePath : variablePaths) {
				Object value = modelInput.getVariableValue(variablePath);
				String query = "INSERT INTO '" + jobInfoTableName + "' VALUES(null, '" + studyId + "', '" + jobId
						+ "', '" + variablePath + "','" + value + "')";
				database.execute(query);
			}
		}
	}

	private void writeJobInfo(List<ModelInput> modelInputs, MySqlDatabase database, String schema) {
		String jobInfoTableName = "job_info";
		createJobInfoTableIfNotExists(database, schema, jobInfoTableName);
		deleteOldEntriesForStudyIfExist(database, schema, jobInfoTableName);
		for (ModelInput modelInput : modelInputs) {
			String jobId = modelInput.getJobId();
			List<String> variablePaths = modelInput.getAllVariableModelPaths();
			for (String variablePath : variablePaths) {
				Object value = modelInput.getVariableValue(variablePath);
				String query = "INSERT INTO `" + schema + "`.`" + jobInfoTableName + "` VALUES(null, '" + studyId
						+ "', '" + jobId + "', '" + variablePath + "','" + value + "')";
				database.execute(query);
			}
		}
	}

	private static void createJobInfoTableIfNotExists(SqLiteDatabase database, String tableName) {
		String query = "CREATE TABLE IF NOT EXISTS '" + tableName
				+ "' (id INTEGER PRIMARY KEY NOT NULL, study TEXT, job TEXT, variable TEXT, value TEXT);";
		database.execute(query);
	}

	private static void createJobInfoTableIfNotExists(MySqlDatabase database, String schema, String tableName) {
		String query = "CREATE TABLE IF NOT EXISTS `" + schema + "`.`" + tableName
				+ "` (id int NOT NULL AUTO_INCREMENT, study TEXT, job TEXT, variable TEXT, value TEXT, PRIMARY KEY(id));";
		database.execute(query);
	}

	//#end region

	//#region ACCESSORS

	@Override
	public String getId() {
		return studyId.get();
	}

	@Override
	public String getDescription() {
		return studyDescription.get();
	}

	@Override
	public String getSourceModelPath() {
		return sourceModelPath.get();
	}

	@Override
	public String getModelToRunModelPath() {
		return modelToRunModelPath.get();
	}

	//#end region

}
