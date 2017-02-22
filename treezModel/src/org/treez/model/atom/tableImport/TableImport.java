package org.treez.model.atom.tableImport;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.CheckBox;
import org.treez.core.atom.attribute.ComboBox;
import org.treez.core.atom.attribute.EnumComboBox;
import org.treez.core.atom.attribute.ModelPath;
import org.treez.core.atom.attribute.ModelPathSelectionType;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.IntegerVariableField;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.table.TableSource;
import org.treez.core.data.table.TableSourceType;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.data.column.Columns;
import org.treez.data.database.sqlite.SqLiteImporter;
import org.treez.data.table.nebula.Table;
import org.treez.data.tableImport.ExcelDataTableImporter;
import org.treez.data.tableImport.TableData;
import org.treez.data.tableImport.TextDataTableImporter;
import org.treez.model.Activator;
import org.treez.model.atom.AbstractModel;
import org.treez.model.atom.executable.Executable;
import org.treez.model.atom.executable.FilePathProvider;
import org.treez.model.output.ModelOutput;

/**
 * Imports data from the file system (e.g. from a text file) to a result table.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class TableImport extends AbstractModel implements TableSource {

	private static final Logger LOG = Logger.getLogger(TableImport.class);

	//#region ATTRIBUTES

	public final Attribute<String> sourceType = new Wrap<>();

	public final Attribute<Boolean> linkSource = new Wrap<>();

	public final Attribute<Integer> rowLimit = new Wrap<>();

	public final Attribute<Boolean> inheritSourceFilePath = new Wrap<>();

	public final Attribute<String> sourceFilePath = new Wrap<>();

	public final Attribute<String> columnSeparator = new Wrap<>();

	public final Attribute<String> host = new Wrap<>();

	public final Attribute<String> port = new Wrap<>();

	public final Attribute<String> user = new Wrap<>();

	public final Attribute<String> password = new Wrap<>();

	public final Attribute<String> schema = new Wrap<>();

	public final Attribute<String> table = new Wrap<>();

	public final Attribute<Boolean> filterForJob = new Wrap<>();

	public final Attribute<String> jobId = new Wrap<>();

	public final Attribute<Boolean> useCustomQuery = new Wrap<>();

	public final Attribute<String> customQuery = new Wrap<>();

	public final Attribute<String> resultTableModelPath = new Wrap<>();

	public final Attribute<Boolean> appendData = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	public TableImport(String name) {
		super(name);
		setRunnable();
		createTableImportModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the model for this atom
	 */
	private void createTableImportModel() {

		AttributeRoot root = new AttributeRoot("root");

		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "tableImport";
		String absoluteHelpContextId = Activator.getAbsoluteHelpContextIdStatic(relativeHelpContextId);

		createSourceTypeSection(dataPage, absoluteHelpContextId);
		createSourceDataSection(dataPage, absoluteHelpContextId);
		createTargetSection(dataPage, absoluteHelpContextId);
		setModel(root);
	}

	private void createTargetSection(Page dataPage, String absoluteHelpContextId) {
		Section targetSection = dataPage.createSection("target", absoluteHelpContextId);

		//target result table (must already exist for manual execution of the TableImport)
		ModelPathSelectionType selectionType = ModelPathSelectionType.FLAT;
		ModelPath resultTable = targetSection.createModelPath(resultTableModelPath, this, null, Table.class,
				selectionType, this, false);
		resultTable.setLabel("Result table");

		//append check box (if true, existing data is not deleted and new data is appended)
		CheckBox appendDataCheck = targetSection.createCheckBox(appendData, this, false);
		appendDataCheck.setLabel("Append data");
	}

	private void createSourceDataSection(Page dataPage, String absoluteHelpContextId) {
		Section sourceDataSection = dataPage.createSection("sourceDataSection", absoluteHelpContextId);
		sourceDataSection.setLabel("Source data");

		//inherit source file path : take (modified) parent output path
		CheckBox inheritSourcePath = sourceDataSection.createCheckBox(inheritSourceFilePath, this, true);
		inheritSourcePath.setLabel("Inherit source file");
		inheritSourcePath.addModifyListener("enableComponents", (event) -> enableAndDisableDependentComponents());

		//path to data file (enabled if source is file based)
		sourceDataSection.createFilePath(sourceFilePath, this, "Source file", "C:\\data.txt");

		TextField columnSeparatorField = sourceDataSection.createTextField(columnSeparator, this, ";");
		columnSeparatorField.setLabel("Column separator");
		//host
		TextField hostField = sourceDataSection.createTextField(host, this, "localhost");
		hostField.setLabel("Host name/IP address");

		//port
		sourceDataSection.createTextField(port, this, "3306");

		//user
		sourceDataSection.createTextField(user, this, "root");

		//password
		sourceDataSection.createTextField(password, this, "");

		//database name (e.g. for SqLite or MySql sources)
		TextField schemaField = sourceDataSection.createTextField(schema, this, "my_schema");
		schemaField.setLabel("Schema name");

		//table name (e.g. name of Excel sheet or SqLite table )
		TextField tableField = sourceDataSection.createTextField(table, this, "Sheet1");
		tableField.setLabel("Table name");
	}

	private void createSourceTypeSection(Page dataPage, String absoluteHelpContextId) {
		Section sourceTypeSection = dataPage.createSection("sourceTypeSection", absoluteHelpContextId);
		sourceTypeSection.setLabel("Source type");
		sourceTypeSection.createSectionAction("action", "Import data", () -> execute(treeViewRefreshable));

		//source type
		EnumComboBox<TableSourceType> sourceTypeCheck = sourceTypeSection.createEnumComboBox(sourceType, this,
				TableSourceType.CSV);
		sourceTypeCheck.setLabel("Source type");
		sourceTypeCheck.addModifyListener("enableComponents", (event) -> enableAndDisableDependentComponents());

		//if true, the target table is linked to the original source
		//pro: for huge tables only the first few rows need to be initialized and the
		//remaining data can be loaded lazily.
		//con: if the source is replaced/changed/deleted, e.g. in a sweep, the
		//link might not give meaningful data.
		CheckBox linkSourceCheck = sourceTypeSection.createCheckBox(linkSource, this, false);
		linkSourceCheck.setLabel("Link source");

		IntegerVariableField rowLimitField = sourceTypeSection.createIntegerVariableField(rowLimit, this, 1000);
		rowLimitField.setLabel("Row limit");

		sourceTypeSection.createCheckBox(filterForJob, this, false).setLabel("Filter rows with JobId");
	}

	@Override
	protected void afterCreateControlAdaptionHook() {
		enableAndDisableDependentComponents();
	}

	private void enableAndDisableDependentComponents() {
		TableSourceType tableSourceType = getSourceType();
		switch (tableSourceType) {
		case CSV:
			enableAndDisableCompontentsForCsv();
			break;
		case EXCEL:
			enableAndDisableCompontentsForExcel();
			break;
		case SQLITE:
			enableAndDisableCompontentsForSqLite();
			break;
		case MYSQL:
			enableAndDisableCompontentsForMySql();
			break;
		default:
			String message = "The TableSourceType " + tableSourceType + " is not yet implemented.";
			throw new IllegalStateException(message);
		}
	}

	private void enableAndDisableCompontentsForCsv() {
		setEnabled(inheritSourceFilePath, true);

		boolean inheritPath = inheritSourceFilePath.get();
		setEnabled(sourceFilePath, !inheritPath);
		setEnabled(columnSeparator, !inheritPath);

		setEnabled(host, false);
		setEnabled(port, false);
		setEnabled(user, false);
		setEnabled(password, false);
		setEnabled(schema, false);
		setEnabled(table, false);
	}

	private void enableAndDisableCompontentsForExcel() {
		setEnabled(inheritSourceFilePath, true);

		boolean inheritPath = inheritSourceFilePath.get();
		setEnabled(sourceFilePath, !inheritPath);

		setEnabled(columnSeparator, false);
		setEnabled(host, false);
		setEnabled(port, false);
		setEnabled(user, false);
		setEnabled(password, false);
		setEnabled(schema, false);
		setEnabled(table, true);
	}

	private void enableAndDisableCompontentsForSqLite() {
		setEnabled(inheritSourceFilePath, true);

		boolean inheritPath = inheritSourceFilePath.get();
		setEnabled(sourceFilePath, !inheritPath);

		setEnabled(columnSeparator, false);
		setEnabled(host, false);
		setEnabled(port, false);
		setEnabled(user, false);
		setEnabled(password, true);
		setEnabled(schema, false);
		setEnabled(table, true);
	}

	private void enableAndDisableCompontentsForMySql() {
		setEnabled(inheritSourceFilePath, false);
		setEnabled(sourceFilePath, false);
		setEnabled(columnSeparator, false);
		setEnabled(host, true);
		setEnabled(port, true);
		setEnabled(user, true);
		setEnabled(password, true);
		setEnabled(schema, true);
		setEnabled(table, true);
	}

	/**
	 * Sets the enabled state of the given attribute atom
	 *
	 * @param attribute
	 * @param value
	 */
	private static void setEnabled(Attribute<?> attribute, boolean value) {
		Wrap<?> attributeWrapper = (Wrap<?>) attribute;
		AbstractAttributeAtom<?, ?> atom = (AbstractAttributeAtom<?, ?>) attributeWrapper.getAttribute();
		boolean hasEqualValue = (atom.isEnabled() == value);
		if (!hasEqualValue) {
			atom.setEnabled(value);
		}
	}

	/**
	 * Runs the model with the current model state and creates its ModelOutput.
	 *
	 * @return
	 */
	@Override
	public ModelOutput runModel(FocusChangingRefreshable refreshable, IProgressMonitor monitor) {

		LOG.info("Running " + this.getClass().getSimpleName() + " '" + getName() + "'");

		// import table data
		TableData tableData = importTableData();

		// write data to result table
		String targetModelPath = resultTableModelPath.get();
		Boolean appendFlag = appendData.get();
		Table treezTable = writeDataToResultTable(tableData, targetModelPath, this, appendFlag);

		//create a copy of the result table to be able to conserve it as a model output for the current run
		String outputTableName = getName() + "Output";
		Table outputTable = treezTable.copy();
		outputTable.setName(outputTableName);

		//wrap table in a model output
		ModelOutput modelOutput = () -> {
			//overrides getRootOutput
			return outputTable;
		};

		//return model output
		return modelOutput;
	}

	/**
	 * Imports table data from the file system
	 *
	 * @param dataFilePath
	 * @param databaseName
	 * @param table
	 * @return
	 */
	private TableData importTableData() {

		TableSourceType tableSourceType = getSourceType();
		int maxRows = getRowLimit();

		String sourcePath = getSourcePath();

		String columnSeparatorString = columnSeparator.get();

		String hostString = host.get();
		String portString = port.get();
		String userString = user.get();
		String passwordString = password.get();
		String schemaString = schema.get();
		String tableNameString = table.get();

		Boolean filterRows = this.filterForJob.get();

		String jobId = null;
		if (filterRows) {
			AbstractAtom<?> parent = this.getParentAtom();
			Executable executable = (Executable) parent;
			jobId = executable.getJobId();
		}

		//determine file extension (=>data type)
		TableData tableData;
		switch (tableSourceType) {
		case CSV:
			tableData = TextDataTableImporter.importData(sourcePath, columnSeparatorString, maxRows);
			return tableData;
		case EXCEL:
			tableData = ExcelDataTableImporter.importData(sourcePath, tableNameString, filterRows, jobId, maxRows);
			return tableData;
		case SQLITE:
			tableData = SqLiteImporter.importData(sourcePath, passwordString, tableNameString, filterRows, jobId,
					maxRows, 0);
			return tableData;
		case MYSQL:
			String url = hostString + ":" + portString + "/" + schemaString;

			tableData = org.treez.data.database.mysql.MySqlImporter.importData(url, userString, passwordString,
					tableNameString, filterRows, jobId, maxRows, 0);
			return tableData;
		default:
			throw new IllegalStateException("The TableSourceType '" + tableSourceType + "' is not yet implemented.");
		}
	}

	private String getSourcePath() {
		boolean inheritPath = inheritSourceFilePath.get();
		String sourcePath;
		if (inheritPath) {
			sourcePath = getSourcePathFromParent();
		} else {
			sourcePath = sourceFilePath.get();
		}
		return sourcePath;
	}

	/**
	 * Tries to retrieve the source path from a parent source path provider
	 *
	 * @return
	 */
	private String getSourcePathFromParent() {
		AbstractAtom<?> parent = this.getParentAtom();
		boolean parentIsPathProvider = FilePathProvider.class.isAssignableFrom(parent.getClass());
		if (parentIsPathProvider) {
			FilePathProvider pathProvider = (FilePathProvider) parent;
			String path = pathProvider.provideFilePath();
			return path;
		} else {
			String message = "The parent atom '" + parent.getName()
					+ "' does not implement the interface FilePathPrivider. "
					+ "Therefore the soruce file path could not be inherited. Please deactivate the inheritance.";
			throw new IllegalStateException(message);
		}
	}

	/**
	 * Writes the given table data to the table with the given model path. If appendData = true the data is appended to
	 * the table. If appendDate = false the data is overridden. At the end the adapted table is returned.
	 *
	 * @param tableData
	 * @param tableModelPath
	 * @param appendData
	 * @return
	 */
	private Table writeDataToResultTable(
			TableData tableData,
			String tableModelPath,
			TableSource tableSourceInfo,
			boolean appendData) {

		boolean isLinked = tableSourceInfo.isLinked();

		//get result table
		Table treezTable = getChildFromRoot(tableModelPath);

		if (isLinked) {
			checkAndPrepareSourceLinkIfRequired(tableSourceInfo, treezTable);
		}

		//check if columns already exist and create them if not
		checkAndPrepareColumnsIfRequired(tableData, treezTable);

		//delete old table content if data should not be appended
		if (!appendData) {
			treezTable.deleteAllRows();
		}

		//write data rows to table
		for (List<Object> rowEntries : tableData.getRowData()) {
			treezTable.addRow(rowEntries);
		}

		return treezTable;

	}

	private static void checkAndPrepareSourceLinkIfRequired(TableSource tableSourceInfo, Table treezTable) {
		//check if table is already linked and create link if not
		boolean tableIsLinked = treezTable.isLinkedToSource();
		if (tableIsLinked) {
			//check if existing link fits to required link
			boolean linkIsOk = treezTable.sourceEquals(tableSourceInfo);
			if (!linkIsOk) {
				String message = "The result table is already linked to a different source.";
				throw new IllegalStateException(message);
			}
		} else {
			//create link

		}
	}

	private static void checkAndPrepareColumnsIfRequired(TableData tableData, Table treezTable) {
		List<String> headers = tableData.getHeaderData();
		boolean columnsExist = treezTable.hasColumns();
		if (columnsExist) {
			//check if existing columns fit to required columns1
			boolean columnNamesAreOk = treezTable.checkHeaders(headers);
			if (!columnNamesAreOk) {
				String message = "The result table already has columns but the column names are wrong.";
				throw new IllegalStateException(message);
			}
		} else {
			//create columns
			Columns columns;
			try {
				columns = treezTable.getColumns();
			} catch (IllegalStateException exception) {
				columns = treezTable.createColumns("columns");
			}

			for (String header : headers) {
				columns.createColumn(header, ColumnType.STRING);
			}
		}
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("tableImport.png");
	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {
		// no actions available right now
		return actions;
	}

	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new AdjustableAtomCodeAdaption(this);
	}

	//#end region

	//#region ACCESSORS

	@Override
	public TableSourceType getSourceType() {
		Wrap<String> sourceTypeWrap = (Wrap<String>) sourceType;
		EnumComboBox<TableSourceType> combo = (EnumComboBox<TableSourceType>) sourceTypeWrap.getAttribute();
		TableSourceType type = combo.getValueAsEnum();
		return type;
	}

	public void setSourceType(TableSourceType sourceTypeEnum) {
		Wrap<String> sourceTypeWrap = (Wrap<String>) sourceType;
		ComboBox combo = (ComboBox) sourceTypeWrap.getAttribute();
		combo.setValue(sourceTypeEnum);
	}

	public int getRowLimit() {
		return rowLimit.get();
	}

	public void setRowLimit(int maxRows) {
		rowLimit.set(maxRows);
	}

	@Override
	public String getSourceFilePath() {
		return sourceFilePath.get();
	}

	@Override
	public String getHost() {
		return host.get();
	}

	@Override
	public String getPort() {
		return port.get();
	}

	@Override
	public String getUser() {
		return user.get();
	}

	@Override
	public String getPassword() {
		return password.get();
	}

	@Override
	public String getSchema() {
		return schema.get();
	}

	@Override
	public String getTableName() {
		return table.get();
	}

	@Override
	public boolean isLinked() {
		return this.linkSource.get();
	}

	@Override
	public Boolean isUsingCustomQuery() {
		return useCustomQuery.get();
	}

	@Override
	public String getCustomQuery() {
		return customQuery.get();
	}

	@Override
	public Boolean isFilteringForJob() {
		return filterForJob.get();
	}

	@Override
	public String getJobId() {
		return jobId.get();
	}

	@Override
	public void setJobId(String jobId) {
		this.jobId.set(jobId);
	}

	//#end region

}
