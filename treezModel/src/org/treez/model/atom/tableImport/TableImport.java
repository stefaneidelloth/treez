package org.treez.model.atom.tableImport;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.attribute.checkBox.CheckBox;
import org.treez.core.atom.attribute.comboBox.enumeration.EnumComboBox;
import org.treez.core.atom.attribute.modelPath.ModelPath;
import org.treez.core.atom.attribute.modelPath.ModelPathSelectionType;
import org.treez.core.atom.attribute.text.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.console.TreezMonitor;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.table.TableSource;
import org.treez.core.data.table.TableSourceType;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.data.column.Columns;
import org.treez.data.database.sqlite.SqLiteImporter;
import org.treez.data.table.nebula.Table;
import org.treez.data.tableImport.TableData;
import org.treez.data.tableImport.TextDataTableImporter;
import org.treez.model.Activator;
import org.treez.model.atom.AbstractModel;
import org.treez.model.atom.executable.FilePathProvider;
import org.treez.model.output.ModelOutput;

/**
 * Imports data from the file system (e.g. from a text file) to a result table.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class TableImport extends AbstractModel implements TableSource {

	private static final Logger LOG = Logger.getLogger(TableImport.class);

	//#region ATTRIBUTES

	public final Attribute<TableSourceType> sourceType = new Wrap<>();

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

	public final Attribute<String> tableName = new Wrap<>();

	public final Attribute<Boolean> filterForJob = new Wrap<>();

	private String customJobIdField;

	public final Attribute<String> customJobId = new Wrap<>();

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

	public TableImport(TableImport atomToCopy) {
		super(atomToCopy);
		copyTreezAttributes(atomToCopy, this);
	}

	//#end region

	//#region METHODS

	@Override
	public TableImport copy() {
		return new TableImport(this);
	}

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

	private void createSourceTypeSection(Page dataPage, String absoluteHelpContextId) {
		Section sourceTypeSection = dataPage.createSection("sourceTypeSection", absoluteHelpContextId);
		sourceTypeSection.setLabel("Source type");
		sourceTypeSection.createSectionAction("action", "Import data", () -> execute(treeViewRefreshable));

		//source type
		EnumComboBox<TableSourceType> sourceTypeCheck = sourceTypeSection.createEnumComboBox(sourceType, this,
				TableSourceType.CSV);
		sourceTypeCheck.setLabel("Source type");
		sourceTypeCheck.addModificationConsumer("enableAndDisableDependentComponents",
				() -> enableAndDisableDependentComponents());

		//if true, the target table is linked to the original source
		//pro: for huge tables only the first few rows need to be initialized and the
		//remaining data can be loaded lazily.
		//con: if the source is replaced/changed/deleted, e.g. in a sweep, the
		//link might not give meaningful data.
		sourceTypeSection
				.createCheckBox(linkSource, this, false) //
				.setLabel("Link source") //
				.addModificationConsumer("enableAndDisableLinkComponents", () -> {
					enableAndDisableLinkComponents();
				});

		sourceTypeSection
				.createIntegerVariableField(rowLimit, this, 1000) //
				.setLabel("Row limit");

	}

	private void enableAndDisableLinkComponents() {
		boolean linkToSource = linkSource.get();
		if (linkToSource) {
			setEnabled(rowLimit, false);
			setEnabled(appendData, false);
		} else {
			setEnabled(rowLimit, true);
			setEnabled(appendData, true);
		}

	}

	private void createSourceDataSection(Page dataPage, String absoluteHelpContextId) {
		Section sourceDataSection = dataPage.createSection("sourceDataSection", absoluteHelpContextId);
		sourceDataSection.setLabel("Source data");

		//inherit source file path : take (modified) parent output path
		CheckBox inheritSourcePath = sourceDataSection.createCheckBox(inheritSourceFilePath, this, true);
		inheritSourcePath.setLabel("Inherit source file");
		inheritSourcePath.addModificationConsumer("enableComponents", () -> enableAndDisableDependentComponents());

		//path to data file (enabled if source is file based)
		String sourcePath = getSourcePath();
		sourceDataSection.createFilePath(sourceFilePath, this, "Source file", sourcePath);

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
		TextField tableField = sourceDataSection.createTextField(tableName, this, "Sheet1");
		tableField.setLabel("Table name");

		sourceDataSection
				.createCheckBox(filterForJob, this, false) //
				.setLabel("Filter rows with JobId") //
				.addModificationConsumer("enableAndDistableJobComponents", () -> enableAndDisableJobComponents());

		sourceDataSection
				.createTextField(customJobId, this) //
				.setLabel("JobId") //
				.addModificationConsumer("updateJobIdOfAbstractModelWithUserInput", () -> {
					if (getJobId() != customJobId.get()) {
						this.setJobId(customJobId.get());
					}
				});

		if (customJobIdField != null) {
			if (customJobId.get() == null) {
				customJobId.set(customJobIdField);
			}
		}

		sourceDataSection
				.createCheckBox(useCustomQuery, this, false) //
				.setLabel("Use custom query") //
				.addModificationConsumer("enableAndDistableQueryComponents", () -> enableAndDisableQueryComponents());

		sourceDataSection
				.createTextArea(customQuery, this) //
				.setLabel("Custom query");

	}

	private void enableAndDisableJobComponents() {
		boolean isFilteringForJob = filterForJob.get();
		if (isFilteringForJob) {
			setEnabled(customJobId, true);
		} else {
			setEnabled(customJobId, false);
		}
	}

	private void enableAndDisableQueryComponents() {
		boolean isUsingCustomQuery = useCustomQuery.get();
		if (isUsingCustomQuery) {
			setEnabled(customQuery, true);
			setEnabled(tableName, false);
			setEnabled(filterForJob, false);
			setEnabled(customJobId, true);
		} else {
			setEnabled(customQuery, false);
			setEnabled(tableName, true);
			setEnabled(filterForJob, true);
			enableAndDisableJobComponents();
		}
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

	@Override
	protected void afterCreateControlAdaptionHook() {
		enableAndDisableDependentComponents();
		updatedInheritedSourcePath();
	}

	private void updatedInheritedSourcePath() {
		boolean inheritPath = inheritSourceFilePath.get();
		if (inheritPath) {
			sourceFilePath.set(getSourcePath());
		}

	}

	private void enableAndDisableDependentComponents() {
		TableSourceType tableSourceType = getSourceType();
		switch (tableSourceType) {
		case CSV:
			enableAndDisableCompontentsForCsv();
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

		setEnabled(linkSource, false); //TODO: check if csv can be read paginated. If so, it might make sense to enable this
		setEnabled(filterForJob, false);

		setEnabled(inheritSourceFilePath, true);

		boolean inheritPath = inheritSourceFilePath.get();
		setEnabled(sourceFilePath, !inheritPath);
		setEnabled(columnSeparator, !inheritPath);

		setEnabled(host, false);
		setEnabled(port, false);
		setEnabled(user, false);
		setEnabled(password, false);
		setEnabled(schema, false);
		setEnabled(tableName, false);

		setEnabled(useCustomQuery, false);
	}

	private void enableAndDisableCompontentsForSqLite() {

		setEnabled(linkSource, true);
		setEnabled(filterForJob, true);

		setEnabled(inheritSourceFilePath, true);

		boolean inheritPath = inheritSourceFilePath.get();
		setEnabled(sourceFilePath, !inheritPath);

		setEnabled(columnSeparator, false);
		setEnabled(host, false);
		setEnabled(port, false);
		setEnabled(user, false);
		setEnabled(password, true);
		setEnabled(schema, false);
		setEnabled(tableName, true);

		setEnabled(useCustomQuery, true);
		enableAndDisableQueryComponents();
	}

	private void enableAndDisableCompontentsForMySql() {

		setEnabled(linkSource, true);
		setEnabled(filterForJob, true);

		setEnabled(inheritSourceFilePath, false);
		setEnabled(sourceFilePath, false);
		setEnabled(columnSeparator, false);
		setEnabled(host, true);
		setEnabled(port, true);
		setEnabled(user, true);
		setEnabled(password, true);
		setEnabled(schema, true);
		setEnabled(tableName, true);

		setEnabled(useCustomQuery, true);
		enableAndDisableQueryComponents();
	}

	/**
	 * Sets the enabled state of the given attribute atom
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
	 */
	@Override
	public ModelOutput runModel(FocusChangingRefreshable refreshable, TreezMonitor monitor) {

		LOG.info("Running " + this.getClass().getSimpleName() + " '" + getName() + "'");

		String targetModelPath = resultTableModelPath.get();

		if (targetModelPath.isEmpty()) {
			throw new IllegalStateException("The table import must define a target table.");
		}

		try {
			getChildFromRoot(targetModelPath);
		} catch (Exception exception) {
			String message = "Could not find target table " + targetModelPath;
			throw new IllegalStateException(message);
		}

		Table treezTable;

		boolean linkToSource = linkSource.get();
		if (linkToSource) {
			treezTable = linkTargetTableToSource(targetModelPath, this);
		} else {
			TableData tableData = importTableData();
			Boolean appendFlag = appendData.get();
			treezTable = writeDataToTargetTable(tableData, targetModelPath, appendFlag);
		}

		//create a copy of the target table to be able to conserve it as a model output for the current run
		String outputTableName = getName() + "Output";
		Table outputTable = treezTable.copy();
		outputTable.setName(outputTableName);

		//wrap table in a model output
		ModelOutput modelOutput = () -> {
			//overrides getRootOutput
			return outputTable;
		};

		LOG.info(this.getClass().getSimpleName() + " '" + getName() + "' finished.");

		//return model output
		return modelOutput;
	}

	private TableData importTableData() {

		TableSourceType tableSourceType = getSourceType();
		int maxRows = getRowLimit();

		String sourcePath = getSourcePath();

		String columnSeparatorString = columnSeparator.get();

		String passwordString = password.get();
		String tableNameString = tableName.get();

		Boolean filterRows = filterForJob.get();
		String jobIdString = getJobId();

		//determine file extension (=>data type)
		TableData tableData;
		switch (tableSourceType) {
		case CSV:
			tableData = TextDataTableImporter.importData(sourcePath, columnSeparatorString, maxRows);
			return tableData;
		case SQLITE:
			tableData = SqLiteImporter.importData(sourcePath, passwordString, tableNameString, filterRows, jobIdString,
					maxRows, 0);
			return tableData;
		case MYSQL:
			String hostString = host.get();
			String portString = port.get();
			String userString = user.get();
			String schemaString = schema.get();
			String url = hostString + ":" + portString + "/" + schemaString;

			tableData = org.treez.data.database.mysql.MySqlImporter.importData(url, userString, passwordString,
					tableNameString, filterRows, jobIdString, maxRows, 0);
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
	 */
	private String getSourcePathFromParent() {
		AbstractAtom<?> parent = this.getParentAtom();
		if (parent == null) {
			return "";
		}

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

	private Table linkTargetTableToSource(String targetModelPath, TableSource tableSource) {
		Table table = getChildFromRoot(targetModelPath);
		table.removeChildrenByInterface(TableSource.class);
		org.treez.data.tableSource.TableSource newTableSource = new org.treez.data.tableSource.TableSource(tableSource);
		table.addChild(newTableSource);
		table.refresh();
		return table;
	}

	/**
	 * Writes the given table data to the table with the given model path. If appendData = true the data is appended to
	 * the table. If appendDate = false the data is overridden. At the end the adapted table is returned.
	 */
	private Table writeDataToTargetTable(TableData tableData, String tableModelPath, boolean appendData) {

		//get result table
		Table treezTable = getChildFromRoot(tableModelPath);

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
		return sourceType.get();
	}

	public void setSourceType(TableSourceType sourceTypeEnum) {
		sourceType.set(sourceTypeEnum);
	}

	public int getRowLimit() {
		return rowLimit.get();
	}

	public void setRowLimit(int maxRows) {

		Wrap<Integer> wrap = (Wrap<Integer>) rowLimit;
		Attribute<Integer> attribute = wrap.getAttribute();

		if (attribute != null) {
			attribute.set(maxRows);
		}

	}

	@Override
	public String getSourceFilePath() {
		return sourceFilePath.get();
	}

	@Override
	public String getColumnSeparator() {
		return columnSeparator.get();
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
		return tableName.get();
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
	public void setJobId(String jobId) {
		super.setJobId(jobId);
		customJobIdField = jobId;

		Wrap<String> wrap = (Wrap<String>) customJobId;
		Attribute<String> attribute = wrap.getAttribute();

		if (attribute != null) {
			attribute.set(jobId);
		}

	}

	//#end region

}
