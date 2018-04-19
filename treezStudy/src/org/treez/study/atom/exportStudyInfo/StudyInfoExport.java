package org.treez.study.atom.exportStudyInfo;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.comboBox.enumeration.EnumComboBox;
import org.treez.core.atom.attribute.fileSystem.FilePath;
import org.treez.core.atom.attribute.text.TextField;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.data.database.mysql.MySqlDatabase;
import org.treez.data.database.sqlite.SqLiteDatabase;
import org.treez.model.atom.AbstractModel;
import org.treez.model.input.ModelInput;
import org.treez.study.Activator;
import org.treez.study.atom.ModelInputGenerator;
import org.treez.study.atom.Study;

public class StudyInfoExport extends AbstractModel {

	private static final Logger LOG = Logger.getLogger(StudyInfoExport.class);

	//#region ATTRIBUTES

	/**
	 * If this is true, a text file with information about the study will be exported to the specified export path
	 */
	public final Attribute<StudyInfoExportType> studyInfoExportType = new Wrap<>();

	/**
	 * The path where study info will be exported if the export option is enabled
	 */
	public final Attribute<String> studyInfoExportPath = new Wrap<>();

	public final Attribute<String> host = new Wrap<>();

	public final Attribute<String> port = new Wrap<>();

	public final Attribute<String> user = new Wrap<>();

	public final Attribute<String> password = new Wrap<>();

	public final Attribute<String> schema = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	public StudyInfoExport(String name) {
		super(name);
		createModel();
	}

	public StudyInfoExport(StudyInfoExport atomToCopy) {
		super(atomToCopy);
	}

	//#end region

	//#region METHODS

	private void createModel() {

		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "studyInfoExport";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);

		createStudyInfoSection(dataPage, absoluteHelpContextId);

		setModel(root);
	}

	@Override
	public void execute(FocusChangingRefreshable refreshable) {

		if (studyInfoExportType.get() == StudyInfoExportType.DISABLED) {
			return;
		}

		String sweepTitle = "StudyInfoExport '" + getName() + "'";
		runNonUiTask(sweepTitle, (mainMonitor) -> {
			Study study = (Study) this.getParentAtom();
			ModelInputGenerator inputGenerator = study.getModelInputGenerator();
			exportStudyInfo(inputGenerator);
			mainMonitor.done();
		});
	}

	private String getstudyNameFromParent() {
		Study study = (Study) this.getParentAtom();
		return study.getId();
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("studyInfoExport.png");
	}

	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {
		return actions;
	}

	@Override
	public AbstractModel copy() {
		return new StudyInfoExport(this);
	}

	protected void createStudyInfoSection(Page dataPage, String absoluteHelpContextId) {
		//study info
		Section studyInfoSection = dataPage.createSection("studyInfo", absoluteHelpContextId);
		studyInfoSection.setLabel("Export study info");
		studyInfoSection.createSectionAction("action", "Export study info", () -> execute(treeViewRefreshable));

		//export study info combo box
		EnumComboBox<StudyInfoExportType> exportStudyInfoTypeCombo = studyInfoSection
				.createEnumComboBox(studyInfoExportType, this, StudyInfoExportType.DISABLED);
		exportStudyInfoTypeCombo.setLabel("Export study information");

		//export sweep info path
		FilePath filePath = studyInfoSection.createFilePath(studyInfoExportPath, this,
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

			StudyInfoExportType exportType = studyInfoExportType.get();
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

	protected void exportStudyInfo(ModelInputGenerator modelInputGenerator) {

		StudyInfoExportType exportType = studyInfoExportType.get();
		String filePath = studyInfoExportPath.get();

		switch (exportType) {
		case DISABLED:
			//Nothing to do
			break;
		case TEXT_FILE:
			if (filePath.isEmpty()) {
				String message = "Export of study info to text file is enabled but no file "
						+ "(e.g. c:/studyInfo.txt) has been specified. Export is cancled.";
				LOG.warn(message);
				return;
			}
			modelInputGenerator.exportStudyInfoToTextFile(filePath);
			LOG.info("Exported study info totextfile: " + filePath);
			return;
		case SQLITE:
			if (filePath.isEmpty()) {
				String message = "Export of study info to text file is enabled but no file "
						+ "(e.g. c:/studyInfo.txt) has been specified. Export is cancled.";
				LOG.warn(message);
				return;
			}
			exportStudyInfoToSqLiteDatabase(modelInputGenerator, filePath);
			LOG.info("Exported study info to SqLite database: " + filePath);
			return;
		case MYSQL:
			exportStudyInfoToMySqlDatabase(modelInputGenerator);
			LOG.info("Exported study info to MySqlDatabase: " + host.get() + ":" + port.get() + "/" + schema.get());
			break;
		default:
			String message = "The export type '" + exportType + "' has not yet been implemented.";
			throw new IllegalStateException(message);
		}

	}

	private void exportStudyInfoToSqLiteDatabase(ModelInputGenerator modelInputGenerator, String filePath) {
		SqLiteDatabase database = new SqLiteDatabase(filePath);
		writeStudyInfo(modelInputGenerator, database);
		writeJobInfo(modelInputGenerator, database);

	}

	private void exportStudyInfoToMySqlDatabase(ModelInputGenerator inputGenerator) {
		String url = host.get() + ":" + port.get();
		String userValue = user.get();
		String passwordValue = password.get();
		String schemaName = schema.get();

		MySqlDatabase database = new MySqlDatabase(url, userValue, passwordValue);
		writeStudyInfo(inputGenerator, database, schemaName);
		writeJobInfo(inputGenerator, database, schemaName);
	}

	private void writeStudyInfo(ModelInputGenerator inputGenerator, SqLiteDatabase database) {
		String studyInfoTableName = "study_info";
		createStudyInfoTableIfNotExists(database, studyInfoTableName);
		deleteOldEntriesForStudyIfExist(database, studyInfoTableName);
		inputGenerator.fillStudyInfo(database, studyInfoTableName, getstudyNameFromParent());
	}

	private void writeStudyInfo(ModelInputGenerator inputGenerator, MySqlDatabase database, String schema) {
		String studyInfoTableName = "study_info";
		createStudyInfoTableIfNotExists(database, schema, studyInfoTableName);
		deleteOldEntriesForStudyIfExist(database, schema, studyInfoTableName);
		inputGenerator.fillStudyInfo(database, schema, studyInfoTableName, getstudyNameFromParent());
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
		String query = "DELETE FROM '" + tableName + "' WHERE study = '" + getstudyNameFromParent() + "';";
		database.execute(query);
	}

	private void deleteOldEntriesForStudyIfExist(MySqlDatabase database, String schema, String tableName) {
		String query = "DELETE FROM `" + schema + "`.`" + tableName + "` WHERE study = '" + getstudyNameFromParent()
				+ "';";
		database.execute(query);
	}

	private void writeJobInfo(ModelInputGenerator inputGenerator, SqLiteDatabase database) {
		String jobInfoTableName = "job_info";
		createJobInfoTableIfNotExists(database, jobInfoTableName);
		deleteOldEntriesForStudyIfExist(database, jobInfoTableName);
		String studyName = getstudyNameFromParent();

		for (ModelInput modelInput : inputGenerator.createModelInputs()) {
			String jobName = modelInput.getjobName();
			List<String> variablePaths = modelInput.getAllVariableModelPaths();
			for (String variablePath : variablePaths) {
				Object value = modelInput.getVariableValue(variablePath);
				String query = "INSERT INTO '" + jobInfoTableName + "' VALUES(null, '" + studyName + "', '" + jobName
						+ "', '" + variablePath + "','" + value + "')";
				database.execute(query);
			}
		}
	}

	private void writeJobInfo(ModelInputGenerator inputGenerator, MySqlDatabase database, String schema) {
		String jobInfoTableName = "job_info";
		createJobInfoTableIfNotExists(database, schema, jobInfoTableName);
		deleteOldEntriesForStudyIfExist(database, schema, jobInfoTableName);
		String studyName = getstudyNameFromParent();
		for (ModelInput modelInput : inputGenerator.createModelInputs()) {
			String jobName = modelInput.getjobName();
			List<String> variablePaths = modelInput.getAllVariableModelPaths();
			for (String variablePath : variablePaths) {
				Object value = modelInput.getVariableValue(variablePath);
				String query = "INSERT INTO `" + schema + "`.`" + jobInfoTableName + "` VALUES(null, '" + studyName
						+ "', '" + jobName + "', '" + variablePath + "','" + value + "')";
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

}
