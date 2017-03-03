package org.treez.results.atom.probe;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.attribute.modelPath.ModelPath;
import org.treez.core.atom.attribute.modelPath.ModelPathSelectionType;
import org.treez.core.atom.attribute.text.TextField;
import org.treez.core.atom.variablerange.VariableRange;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.data.column.ColumnBlueprint;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.quantity.Quantity;
import org.treez.data.output.OutputAtom;
import org.treez.data.table.nebula.Table;
import org.treez.results.Activator;

/**
 * Collects data from a picker parameter variation and puts it in a single (probe-) table. That table can easier be used
 * to produce plots than the distributed picker results.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class ProbabilityProbe extends AbstractProbe {

	private static final Logger LOG = Logger.getLogger(ProbabilityProbe.class);

	//#region ATTRIBUTES

	//time section

	public final Attribute<String> timeLabel = new Wrap<>();

	public final Attribute<String> timeRange = new Wrap<>();

	//y section

	public final Attribute<String> yLabel = new Wrap<>();

	//tuple section

	public final Attribute<String> tupleListLabel = new Wrap<>();

	public final Attribute<String> tupleList = new Wrap<>();

	//probe section

	public final Attribute<String> probeName = new Wrap<>();

	public final Attribute<String> propabilityOutput = new Wrap<>();

	public final Attribute<String> firstProbeTable = new Wrap<>();

	public final Attribute<String> probeColumnIndex = new Wrap<>();

	public final Attribute<String> probeRowIndex = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	public ProbabilityProbe(String name) {
		super(name);
		createPickerProbeModel();
	}

	//#end region

	//#region METHODS

	private void createPickerProbeModel() {
		AttributeRoot root = new AttributeRoot("root");

		org.treez.core.atom.attribute.attributeContainer.Page page = root.createPage("page");

		//time section
		Section timeSection = page.createSection("timeSection", "Time");
		timeSection.createSectionAction("action", "Run probe", () -> execute(treeViewRefreshable));

		TextField timeLabelField = timeSection.createTextField(timeLabel, this, "Year");
		timeLabelField.setLabel("Label for time axis");
		ModelPath timeRangePath = timeSection.createModelPath(timeRange, this, "", VariableRange.class, this);
		timeRangePath.setLabel("Range for time axis");
		timeRangePath.setSelectionType(ModelPathSelectionType.FLAT);
		//timeRangePath.set("root.studies.picker.time");

		//y section
		Section ySection = page.createSection("ySection", "Y");

		TextField yLabelField = ySection.createTextField(yLabel, this, "y");
		yLabelField.setLabel("Label for y-Axis");

		//tuple list section
		Section tupleListSection = page.createSection("tupleList", "Tuple list");
		tupleListSection.setExpanded(false);

		//tupleListSection.createTextField(tupleListLabel, "tupleListLabel", "Label for first family", "family1");
		//tupleListSection.createModelPath(tupleList, this, "Range for first family", "",
		//		VariableRange.class, this);

		//probe section
		Section probeSection = page.createSection("probe", "Probe");

		TextField probeNameField = probeSection.createTextField(probeName, this, "MyProbe");
		probeNameField.setLabel("Name");
		ModelPath sweepOutputModelPath = probeSection.createModelPath(propabilityOutput, this, "", OutputAtom.class,
				this);
		sweepOutputModelPath.setLabel("Probability output");

		ModelPath firstProbeTablePath = probeSection.createModelPath(firstProbeTable, this, sweepOutputModelPath,
				Table.class);
		firstProbeTablePath.setLabel("First probe table");

		TextField columnIndex = probeSection.createTextField(probeColumnIndex, this, "0");
		columnIndex.setLabel("Column index");
		TextField rowIndex = probeSection.createTextField(probeRowIndex, this, "0");
		rowIndex.setLabel("Row index");

		setModel(root);

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
		Image baseImage = Activator.getImage("probability.png");
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

		//create column blueprints
		List<ColumnBlueprint> columnBlueprints = new ArrayList<>();

		//time column----------------------------------------
		String timeLabelString = timeLabel.get();
		String timeColumnName = timeLabelString;
		Class<?> timeType = getTimeType();
		ColumnType timeColumnType = ColumnType.getType(timeType);
		String timeLegend = timeLabelString;
		columnBlueprints.add(new ColumnBlueprint(timeColumnName, timeColumnType, timeLegend));

		//y columns---------------------------------------

		//get y information
		String yLabelString = yLabel.get();
		ColumnType yColumnType = ColumnType.STRING;

		//get tuple information
		String tupleListLabelString = tupleListLabel.get();
		List<?> tupleList = getTupleList();

		//create y column names, types and legends
		int tupleIndex = 1;
		for (Object tuple : tupleList) {
			String columnName = yLabelString + "#" + tupleIndex;
			String tupleResultValueString = tuple.toString();
			String legendText = tupleListLabelString + ": " + tupleResultValueString;
			columnBlueprints.add(new ColumnBlueprint(columnName, yColumnType, legendText));

			tupleIndex++;
		}

		//create columns--------------------------------------------------------------------------
		createColumns(table, columnBlueprints);

		LOG.info("Created table columns.");

	}

	private List<?> getTupleList() {
		String firstFamilyPath = tupleList.get();
		boolean firstFamilyIsSpecified = !"".equals(firstFamilyPath);
		List<?> firstFamilyRangeValues = null;
		if (firstFamilyIsSpecified) {
			VariableRange<?> firstFamilyRangeAtom = this.getChildFromRoot(firstFamilyPath);
			firstFamilyRangeValues = firstFamilyRangeAtom.getRange();
		} else {
			String message = "At least the first family range needs to be specified.";
			throw new IllegalStateException(message);
		}
		return firstFamilyRangeValues;
	}

	private Class<?> getTimeType() {
		String timePath = timeRange.get();
		boolean timeIsSpecified = !"".equals(timePath);
		Class<?> timeType = null;
		if (timeIsSpecified) {
			VariableRange<?> timeRangeAtom = this.getChildFromRoot(timePath);
			timeType = timeRangeAtom.getType();
		}
		return timeType;
	}

	//#end region

	//#region COLLECT PROBE DATA

	@Override
	protected void collectProbeDataAndFillTable(Table table) {

		LOG.info("Filling probe table...");

		//get time information
		String timeLabelString = timeLabel.get();
		String timePath = timeRange.get();
		boolean timeIsSpecified = !"".equals(timePath);
		VariableRange<?> timeRangeAtom = null;

		List<?> timeRangeValues = null;
		if (timeIsSpecified) {
			timeRangeAtom = this.getChildFromRoot(timePath);
			timeRangeValues = timeRangeAtom.getRange();
		}

		//get y information
		String yLabelString = yLabel.get();

		//get tuple information
		String tupleyPath = tupleList.get();
		List<?> tupleListValues = getTupleValues(tupleyPath);

		//column names
		List<String> columnNames = createColumnNames(timeLabelString, yLabelString, tupleListValues);

		//get sweep output path
		String sweepOutputPath = propabilityOutput.get();

		//get probe table relative path
		String firstProbeTableRelativePath = getFirstProbeRelativePath();
		String[] pathItems = firstProbeTableRelativePath.split("\\.");
		String firstPrefix = pathItems[0];
		int firstIndex = firstPrefix.length() + 1;
		String relativeProbeTablePath = firstProbeTableRelativePath.substring(firstIndex);

		//get probe table prefix
		String prefix = getProbeTablePrefix(firstPrefix);

		fillProbeTable(table, timeRangeValues, columnNames, sweepOutputPath, relativeProbeTablePath, prefix);

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
		int probeRowId = Integer.parseInt(probeRowIndex.get());

		//get probe table column index
		int probeColumnId = Integer.parseInt(probeColumnIndex.get());

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

	private List<?> getTupleValues(String firstFamilyPath) {
		boolean firstFamilyIsSpecified = !"".equals(firstFamilyPath);
		List<?> firstFamilyRangeValues = null;
		if (firstFamilyIsSpecified) {
			VariableRange<?> firstFamilyRangeAtom = this.getChildFromRoot(firstFamilyPath);
			firstFamilyRangeValues = firstFamilyRangeAtom.getRange();
		} else {
			String message = "At least the first family range needs to be specified.";
			throw new IllegalStateException(message);
		}
		return firstFamilyRangeValues;
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
