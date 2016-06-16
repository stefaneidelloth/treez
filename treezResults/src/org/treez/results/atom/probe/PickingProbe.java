package org.treez.results.atom.probe;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.ComboBox;
import org.treez.core.atom.attribute.ModelPath;
import org.treez.core.atom.attribute.ModelPathSelectionType;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.IntegerVariableField;
import org.treez.core.atom.variablelist.NumberRangeProvider;
import org.treez.core.atom.variablerange.VariableRange;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.data.column.ColumnBlueprint;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.quantity.Quantity;
import org.treez.data.column.Column;
import org.treez.data.column.Columns;
import org.treez.data.output.OutputAtom;
import org.treez.data.table.Table;
import org.treez.results.Activator;

/**
 * Collects data from a picking parameter variation and puts it in a single (probe-) table. That table can easier be
 * used to produce plots than the distributed picking results.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class PickingProbe extends AbstractProbe {

	private static final Logger LOG = Logger.getLogger(PickingProbe.class);

	//#region ATTRIBUTES

	//domain section

	private static final String DOMAIN_TYPE_SAMPLES = "Samples";

	private static final String DOMAIN_TYPE_TIME_SERIES_PICKING = "Time series from picking";

	private static final String DOMAIN_TYPE_TIME_SERIES_COLUMN = "Time series from column";

	public final Attribute<String> domainType = new Wrap<>();

	public final Attribute<String> domainLabel = new Wrap<>();

	/**
	 * The model path to a column that is used to retrieve domain values
	 */
	public final Attribute<String> domainColumnPath = new Wrap<>();

	/**
	 * The model path to Picking that is used to retrieve time values
	 */
	public final Attribute<String> pickingPath = new Wrap<>();

	//probe section

	public final Attribute<String> probeLabel = new Wrap<>();

	public final Attribute<String> pickingOutput = new Wrap<>();

	public final Attribute<String> firstProbeTable = new Wrap<>();

	public final Attribute<Integer> probeColumnIndex = new Wrap<>();

	public final Attribute<Integer> probeRowIndex = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	public PickingProbe(String name) {
		super(name);
		createPickingProbeModel();
	}

	//#end region

	//#region METHODS

	@SuppressWarnings({ "checkstyle:javancss", "checkstyle:executablestatementcount" })
	private void createPickingProbeModel() {
		AttributeRoot root = new AttributeRoot("root");

		org.treez.core.atom.attribute.Page page = root.createPage("page");

		//domain section
		Section domainSection = page.createSection("domainSection", "DomainSectionHelpId");
		domainSection.setLabel("Domain");
		domainSection.createSectionAction("action", "Run probe", () -> execute(treeViewRefreshable));

		ComboBox domainTypeCombo = domainSection.createComboBox(domainType, this);
		domainTypeCombo.setLabel("Domain type");
		domainTypeCombo.setItems(
				DOMAIN_TYPE_SAMPLES + "," + DOMAIN_TYPE_TIME_SERIES_PICKING + "," + DOMAIN_TYPE_TIME_SERIES_COLUMN);
		domainTypeCombo.set("Samples");

		TextField domainLabelField = domainSection.createTextField(domainLabel, this);
		domainLabelField.setLabel("Domain label");
		domainLabelField.setEnabled(false);

		ModelPath pickingModelPath = domainSection.createModelPath(pickingPath, this, "", NumberRangeProvider.class,
				this);
		pickingModelPath.setLabel("Picking for domain range");
		pickingModelPath.setSelectionType(ModelPathSelectionType.FLAT);
		pickingModelPath.setEnabled(false);

		ModelPath timeColumnModelPath = domainSection.createModelPath(domainColumnPath, this, "", Column.class, this);
		timeColumnModelPath.setLabel("Column for domain range");
		timeColumnModelPath.setSelectionType(ModelPathSelectionType.FLAT);
		timeColumnModelPath.setEnabled(false);
		//timeRangePath.set("root.studies.picking.time");

		domainTypeCombo.addModificationConsumerAndRun("Update label field", () -> {

			domainLabelField.set("Sample");
			domainLabelField.setEnabled(false);
			pickingModelPath.setEnabled(false);
			timeColumnModelPath.setEnabled(false);

			boolean isPickingTimeSeries = isPickingTimeSeries();
			boolean isColumnTimeSeries = isColumnTimeSeries();
			if (isPickingTimeSeries || isColumnTimeSeries) {
				domainLabelField.set("Time");
				domainLabelField.setEnabled(true);
				if (isPickingTimeSeries) {
					pickingModelPath.setEnabled(true);
				}
				if (isColumnTimeSeries) {
					timeColumnModelPath.setEnabled(true);
				}
			}
		});

		//probe section
		Section probeSection = page.createSection("probe", "Probe");

		TextField probeNameField = probeSection.createTextField(probeLabel, this);
		probeNameField.setLabel("Probe label");
		ModelPath pickingOutputModelPath = probeSection.createModelPath(pickingOutput, this, "", OutputAtom.class,
				this);
		pickingOutputModelPath.setLabel("Picking output");
		ModelPath firstProbeTablePath = probeSection.createModelPath(firstProbeTable, this, pickingOutputModelPath,
				Table.class);
		firstProbeTablePath.setLabel("First probe table");

		final Color white = new Color(null, 255, 255, 255);
		IntegerVariableField columnIndex = probeSection.createIntegerVariableField(probeColumnIndex, this, 0);
		columnIndex.setLabel("Column index");
		columnIndex.setBackgroundColor(white);
		columnIndex.setMinValue(0);

		IntegerVariableField rowIndex = probeSection.createIntegerVariableField(probeRowIndex, this, 0);
		rowIndex.setLabel("Row index");
		rowIndex.setBackgroundColor(white);
		rowIndex.setMinValue(0);
		setModel(root);
	}

	private boolean isColumnTimeSeries() {
		boolean isColumnTimeSeries = domainType.get().equals(DOMAIN_TYPE_TIME_SERIES_COLUMN);
		return isColumnTimeSeries;
	}

	private boolean isPickingTimeSeries() {
		boolean isPickingTimeSeries = domainType.get().equals(DOMAIN_TYPE_TIME_SERIES_PICKING);
		return isPickingTimeSeries;
	}

	private boolean isTimeSeries() {
		boolean isPickingTimeSeries = isPickingTimeSeries();
		boolean isColumnTimeSeries = isColumnTimeSeries();
		boolean isTimeSeries = isPickingTimeSeries || isColumnTimeSeries;
		return isTimeSeries;
	}

	@Override
	protected void afterCreateControlAdaptionHook() {
		updateRelativePathRoots();
	}

	@Override
	protected void updateRelativePathRoots() {
		Attribute<String> attribute = getWrappedAttribute(firstProbeTable);
		ModelPath firstProbeTableModelPath = (ModelPath) attribute;
		firstProbeTableModelPath.updateRelativeRootAtom();
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideBaseImage() {
		Image baseImage = Activator.getImage("picking.png");
		return baseImage;
	}

	//#region CREATE TABLE COLUMNS

	/**
	 * Creates the required columns for the given new table
	 *
	 * @param table
	 */
	@Override
	@SuppressWarnings({ "checkstyle:executablestatementcount", "checkstyle:javancss" })
	protected void createTableColumns(Table table) {

		LOG.info("Creating table columns...");

		//determine column names, types and legends
		List<ColumnBlueprint> columnBlueprints = new ArrayList<>();

		if (isTimeSeries()) {
			columnBlueprints = createColumnBlueprintsWithTimeSeries();
		} else {
			//domain column
			columnBlueprints.add(new ColumnBlueprint("Sample", ColumnType.TEXT, ""));

			//probe column
			ColumnType probeColumnType = this.getPickingProbeColumnType();
			columnBlueprints.add(new ColumnBlueprint(probeLabel.get(), probeColumnType, probeLabel.get()));
		}

		createColumns(table, columnBlueprints);

		LOG.info("Created table columns.");

	}

	private List<ColumnBlueprint> createColumnBlueprintsWithTimeSeries() {

		List<ColumnBlueprint> columnBlueprints = new ArrayList<>();

		//domain column
		ColumnType domainColumnType = getDomainColumnType();
		columnBlueprints.add(new ColumnBlueprint(domainLabel.get(), domainColumnType, ""));

		//sample columns
		if (isPickingTimeSeries()) {
			columnBlueprints = addSampleColumnBlueprintsForPickingTimeSeries(columnBlueprints);
		} else if (isColumnTimeSeries()) {
			columnBlueprints = addSampleColumnBlueprintsForColumnTimeSeries(columnBlueprints);
		} else {
			throw new IllegalStateException("Unknown domain series type");
		}

		return columnBlueprints;
	}

	private List<ColumnBlueprint> addSampleColumnBlueprintsForPickingTimeSeries(
			List<ColumnBlueprint> columnBlueprints) {
		ColumnType probeColumnType = getPickingProbeColumnType();
		List<AbstractAtom<?>> samples = getSamplesFromPicking();
		for (AbstractAtom<?> sample : samples) {
			String sampleName = sample.getName();
			columnBlueprints.add(new ColumnBlueprint(sampleName, probeColumnType, sampleName));
		}
		return columnBlueprints;
	}

	private List<ColumnBlueprint> addSampleColumnBlueprintsForColumnTimeSeries(List<ColumnBlueprint> columnBlueprints) {
		String domainColumnModelPath = domainColumnPath.get();
		boolean timeIsSpecified = !"".equals(domainColumnModelPath);
		if (timeIsSpecified) {
			Column column = this.getChildFromRoot(domainColumnModelPath);
			ColumnType columnType = column.getColumnType();

			int numberOfDomainValues = column.getValues().size();
			if (numberOfDomainValues > 0) {
				int numberOfPickingOutputs = getNumberOfPickingOutputs();
				int numberOfProbeColumns = numberOfPickingOutputs / numberOfDomainValues;
				for (int columnIndex = 1; columnIndex <= numberOfProbeColumns; columnIndex++) {
					String sampleColumnName = "Costum_sample_" + columnIndex;
					columnBlueprints.add(new ColumnBlueprint(sampleColumnName, columnType, sampleColumnName));
				}
			}
		}
		return columnBlueprints;
	}

	private int getNumberOfPickingOutputs() {
		String pickingOutputModelPath = pickingOutput.get();
		boolean pickingOutputIsSpecified = !"".equals(pickingOutputModelPath);
		if (pickingOutputIsSpecified) {
			AbstractAtom<?> pickingOutputAtom = this.getChildFromRoot(pickingOutputModelPath);
			int numberOfOutputs = pickingOutputAtom.getChildAtoms().size();
			return numberOfOutputs;
		}
		return 0;
	}

	private List<AbstractAtom<?>> getSamplesFromPicking() {
		String pickingModelPath = pickingPath.get();
		boolean pickingIsSpecified = !"".equals(pickingModelPath);
		if (pickingIsSpecified) {
			AbstractAtom<?> picking = this.getChildFromRoot(pickingModelPath);
			return picking.getChildAtoms();
		}
		return null;
	}

	private ColumnType getPickingProbeColumnType() {
		String firstProbeTableModelPath = firstProbeTable.get();
		boolean probeTableSpecified = !"".equals(firstProbeTableModelPath);
		if (probeTableSpecified) {
			Table table = this.getChildFromRoot(firstProbeTableModelPath);
			Columns columns = table.getColumns();
			int columnIndex = this.probeColumnIndex.get();
			Column probeColumn = columns.getColumnByIndex(columnIndex);
			ColumnType columnType = probeColumn.getColumnType();
			return columnType;
		} else {
			String message = "Could not determine the probe column type. Please make sure that a probe table is specified.";
			throw new IllegalStateException(message);
		}
	}

	private ColumnType getDomainColumnType() {
		if (isColumnTimeSeries()) {
			return getDomainColumnTypeFromColumn();
		} else if (isPickingTimeSeries()) {
			return getDomainColumnTypeFromPicking();
		}
		throw new IllegalStateException("Unknown time series type");
	}

	private ColumnType getDomainColumnTypeFromColumn() {
		String domainColumnModelPath = domainColumnPath.get();
		boolean timeIsSpecified = !"".equals(domainColumnModelPath);
		if (timeIsSpecified) {
			Column column = this.getChildFromRoot(domainColumnModelPath);
			ColumnType columnType = column.getColumnType();
			return columnType;
		} else {
			return null;
		}
	}

	private ColumnType getDomainColumnTypeFromPicking() {
		String pickingModelPath = pickingPath.get();
		boolean pickingIsSpecified = !"".equals(pickingModelPath);
		Class<?> domainColumnType = null;
		if (pickingIsSpecified) {
			NumberRangeProvider picking = this.getChildFromRoot(pickingModelPath);
			domainColumnType = picking.getRangeType();
			if (domainColumnType == null) {
				String message = "The picking '" + pickingModelPath + "' that is used for the picking probe '"
						+ getName() + "'does not provide a time series.";
				throw new IllegalArgumentException(message);
			}
		}
		ColumnType columnType = ColumnType.getDefaultTypeForClass(domainColumnType);
		return columnType;
	}

	//#end region

	//#region COLLECT PROBE DATA

	@Override
	protected void collectProbeDataAndFillTable(Table table) {

		LOG.info("Filling probe table...");

		boolean isTimeSeries = isTimeSeries();

		if (isTimeSeries) {

			//get domain information

			List<Number> domainValues = getDomainTimeSeriesRange();

			String timeLabelString = domainLabel.get();
			String timePath = domainColumnPath.get();
			boolean timeIsSpecified = !"".equals(timePath);
			VariableRange<?> timeRangeAtom = null;

			List<?> timeRangeValues = null;
			if (timeIsSpecified) {
				timeRangeAtom = this.getChildFromRoot(timePath);
				timeRangeValues = timeRangeAtom.getRange();
			}

			/*
			//get y information
			String yLabelString = yLabel.get();

			//get tuple information
			String tupleyPath = tupleList.get();
			List<?> tupleListValues = getTupleValues(tupleyPath);

			//column names
			List<String> columnNames = createColumnNames(timeLabelString, yLabelString, tupleListValues);

			*/

			//get sweep output path
			String sweepOutputPath = pickingOutput.get();

			//get probe table relative path
			String firstProbeTableRelativePath = getFirstProbeRelativePath();
			String[] pathItems = firstProbeTableRelativePath.split("\\.");
			String firstPrefix = pathItems[0];
			int firstIndex = firstPrefix.length() + 1;
			String relativeProbeTablePath = firstProbeTableRelativePath.substring(firstIndex);

			//get probe table prefix
			String prefix = getProbeTablePrefix(firstPrefix);

			//fillProbeTable(table, timeRangeValues, columnNames, sweepOutputPath, relativeProbeTablePath, prefix);

		} else {

		}

		LOG.info("Filled probe table.");

	}

	private void fillProbeTable(
			Table table,
			List<?> xRangeValues,
			List<String> columnNames,
			String sweepOutputPath,
			String relativeProbeTablePath,
			String prefix) {
		//get probe table row index
		int probeRowId = probeRowIndex.get();

		//get probe table column index
		int probeColumnId = probeColumnIndex.get();

		int sweepIndex = 1;
		for (int rowIndex = 0; rowIndex < xRangeValues.size(); rowIndex++) {

			//create new row
			Row row = new Row(table);

			//fill x column entry
			Object xValue = xRangeValues.get(rowIndex);
			boolean isQuantity = xValue instanceof Quantity;
			if (isQuantity) {
				//only take numeric value (="remove" unit)
				Quantity quantity = (Quantity) xValue;
				xValue = quantity.getValue();
			}
			row.setEntry(columnNames.get(0), xValue);

			//fill y column entries
			for (int columnIndex = 1; columnIndex < columnNames.size(); columnIndex++) {
				String yColumnName = columnNames.get(columnIndex);
				String tablePath = sweepOutputPath + "." + prefix + sweepIndex + "." + relativeProbeTablePath;
				Object yValue = getProbeValue(tablePath, probeRowId, probeColumnId);
				row.setEntry(yColumnName, yValue);

				//increase sweep index
				sweepIndex++;
			}

			//add row
			table.addRow(row);

		}
	}

	private List<Number> getDomainTimeSeriesRange() {
		if (isColumnTimeSeries()) {
			String domainColumnModelPath = domainColumnPath.get();
			boolean timeIsSpecified = !"".equals(domainColumnModelPath);
			if (timeIsSpecified) {
				Column column = this.getChildFromRoot(domainColumnModelPath);
				List<?> domainRange = column.getValues();
				List<Number> domainNumberRange = new ArrayList<>();
				for (Object domainValue : domainRange) {
					Number domainNumberValue = (Number) domainValue;
					domainNumberRange.add(domainNumberValue);
				}
			}
			return null;
		} else if (isPickingTimeSeries()) {
			String pickingModelPath = pickingPath.get();
			boolean pickingIsSpecified = !"".equals(pickingModelPath);
			if (pickingIsSpecified) {
				NumberRangeProvider picking = (NumberRangeProvider) this.getChildFromRoot(pickingModelPath);
				List<Number> domainNumberRange = picking.getRange();
				return domainNumberRange;
			}
			return null;
		}

		throw new IllegalStateException("Unknown time series type");
	}

	private static String getProbeTablePrefix(String firstPrefix) {
		String idSeparator = "Id";
		String[] prefixItems = firstPrefix.split(idSeparator);
		String prefix = prefixItems[0] + idSeparator;
		return prefix;
	}

	private String getFirstProbeRelativePath() {
		Attribute<String> attribute = getWrappedAttribute(firstProbeTable);
		ModelPath probeTablePath = (ModelPath) attribute;
		String firstRelativeProbeTablePath = probeTablePath.getRelativeValue();
		return firstRelativeProbeTablePath;
	}

	private static
			List<String>
			createColumnNames(String xLabelString, String yLabelString, List<?> firstFamilyRangeValues) {
		List<String> columnNames = new ArrayList<>();

		//create first column info (=x column)
		String xColumnName = xLabelString;
		columnNames.add(xColumnName);

		//create remaining column info (=y columns)
		for (int firstFamilyIndex = 1; firstFamilyIndex <= firstFamilyRangeValues.size(); firstFamilyIndex++) {
			String columnName = yLabelString + "#" + firstFamilyIndex;

			columnNames.add(columnName);

		}
		return columnNames;
	}

	private Object getProbeValue(String probeTablePath, int rowIndex, int columnIndex) {

		//get probe table
		Table probeTable = this.getChildFromRoot(probeTablePath);

		//get probe value
		String columnHeader = probeTable.getHeaders().get(columnIndex);
		Row row = probeTable.getRows().get(rowIndex);
		Object value = row.getEntry(columnHeader);

		//return probe value
		return value;
	}

	//#end region

	//#end region

}
