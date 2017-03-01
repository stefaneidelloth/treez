package org.treez.data.tableSource;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.treez.core.Activator;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.EnumComboBox;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.data.table.TableSourceType;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class TableSource extends AdjustableAtom implements org.treez.core.data.table.TableSource {

	//#region ATTRIBUTES

	public final Attribute<String> sourceType = new Wrap<>();

	public final Attribute<String> filePath = new Wrap<>();

	public final Attribute<String> columnSeparator = new Wrap<>();

	public final Attribute<String> host = new Wrap<>();

	public final Attribute<String> port = new Wrap<>();

	public final Attribute<String> user = new Wrap<>();

	public final Attribute<String> password = new Wrap<>();

	public final Attribute<String> schema = new Wrap<>();

	public final Attribute<String> tableName = new Wrap<>();

	public final Attribute<Boolean> filterForJob = new Wrap<>();

	public final Attribute<String> jobId = new Wrap<>();

	public final Attribute<Boolean> useCustomQuery = new Wrap<>();

	public final Attribute<String> customQuery = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	public TableSource(String name) {
		super(name);
		setRunnable();
		createTableSourceModel();
	}

	/**
	 * Copy constructor based on interface
	 */
	public TableSource(org.treez.core.data.table.TableSource tableSource) {
		this("TableSource");
		sourceType.set(tableSource.getSourceType().toString());
		filePath.set(tableSource.getSourceFilePath());
		columnSeparator.set(tableSource.getColumnSeparator());
		host.set(tableSource.getHost());
		port.set(tableSource.getPort());
		user.set(tableSource.getUser());
		password.set(tableSource.getPassword());
		schema.set(tableSource.getSchema());
		tableName.set(tableSource.getTableName());
		filterForJob.set(tableSource.isFilteringForJob());
		jobId.set(tableSource.getJobId());
		useCustomQuery.set(tableSource.isUsingCustomQuery());
		customQuery.set(tableSource.getCustomQuery());

	}

	//#end region

	//#region METHODS

	@Override
	public TableSource copy() {
		TableSource newTableSource = new TableSource(this);
		return newTableSource;
	}

	private void createTableSourceModel() {

		AttributeRoot root = new AttributeRoot("root");

		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "tableSource";
		String absoluteHelpContextId = Activator.getAbsoluteHelpContextIdStatic(relativeHelpContextId);

		createSourceTypeSection(dataPage, absoluteHelpContextId);
		createSourceDataSection(dataPage, absoluteHelpContextId);

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
		sourceTypeCheck.addModificationConsumer("enableComponents", () -> enableAndDisableDependentComponents());

	}

	private void createSourceDataSection(Page dataPage, String absoluteHelpContextId) {
		Section sourceDataSection = dataPage.createSection("sourceDataSection", absoluteHelpContextId);
		sourceDataSection.setLabel("Source data");

		//path to data file (enabled if source is file based)
		sourceDataSection.createFilePath(filePath, this, "Source file", "C:\\data.txt");

		//column separator
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

		//database name (e.g. for MySql sources)
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
				.createTextField(jobId, this) //
				.setLabel("JobId");

		sourceDataSection
				.createCheckBox(useCustomQuery, this, false) //
				.setLabel("Use custom query") //
				.addModificationConsumer("enableAndDistableQueryComponents", () -> enableAndDisableQueryComponents());

		sourceDataSection
				.createTextArea(customQuery, this) //
				.setLabel("Custom query");

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

	private void enableAndDisableJobComponents() {
		boolean isFilteringForJob = filterForJob.get();
		if (isFilteringForJob) {
			setEnabled(jobId, true);
		} else {
			setEnabled(jobId, false);
		}
	}

	private void enableAndDisableQueryComponents() {
		boolean isUsingCustomQuery = useCustomQuery.get();
		if (isUsingCustomQuery) {
			setEnabled(customQuery, true);
			setEnabled(tableName, false);
			setEnabled(filterForJob, false);
			setEnabled(jobId, true);
		} else {
			setEnabled(customQuery, false);
			setEnabled(tableName, true);
			setEnabled(filterForJob, true);
			enableAndDisableJobComponents();
		}
	}

	private void enableAndDisableCompontentsForCsv() {

		setEnabled(host, false);
		setEnabled(port, false);
		setEnabled(user, false);
		setEnabled(password, false);
		setEnabled(schema, false);
		setEnabled(tableName, false);
		setEnabled(filterForJob, false);
		setEnabled(jobId, false);
		setEnabled(useCustomQuery, false);
		setEnabled(customQuery, false);
	}

	private void enableAndDisableCompontentsForSqLite() {

		setEnabled(columnSeparator, false);
		setEnabled(host, false);
		setEnabled(port, false);
		setEnabled(user, false);
		setEnabled(password, true);
		setEnabled(schema, false);
		setEnabled(tableName, true);
		setEnabled(filterForJob, true);
		setEnabled(useCustomQuery, true);
		enableAndDisableQueryComponents();
	}

	private void enableAndDisableCompontentsForMySql() {

		setEnabled(filePath, false);
		setEnabled(columnSeparator, false);
		setEnabled(host, true);
		setEnabled(port, true);
		setEnabled(user, true);
		setEnabled(password, true);
		setEnabled(schema, true);
		setEnabled(tableName, true);
		setEnabled(filterForJob, true);
		setEnabled(useCustomQuery, true);
		enableAndDisableQueryComponents();
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

	@Override
	public Image provideImage() {
		return Activator.getImage("source.png");
	}

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
		EnumComboBox<TableSourceType> combo = (EnumComboBox<TableSourceType>) sourceTypeWrap.getAttribute();
		combo.set(sourceTypeEnum.toString());
	}

	@Override
	public String getSourceFilePath() {
		return filePath.get();
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
		return true;
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
