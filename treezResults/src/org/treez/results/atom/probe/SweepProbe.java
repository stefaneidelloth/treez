package org.treez.results.atom.probe;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.ModelPath;
import org.treez.core.atom.attribute.ModelPathSelectionType;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.atom.variablerange.VariableRange;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.quantity.Quantity;
import org.treez.data.column.Columns;
import org.treez.data.output.OutputAtom;
import org.treez.data.table.Table;
import org.treez.results.Activator;

/**
 * Collects data from a sweep parameter variation and puts it in a single (probe-) table. That table can easier be used
 * to produce plots than the distributed sweep results.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class SweepProbe extends AbstractProbe {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(SweepProbe.class);

	//#region ATTRIBUTES

	//x section

	/**
	 * x label
	 */
	public final Attribute<String> xLabel = new Wrap<>();

	/**
	 * x range
	 */
	public final Attribute<String> xRange = new Wrap<>();

	//y section

	/**
	 * y label
	 */
	public final Attribute<String> yLabel = new Wrap<>();

	//first family section

	/**
	 * first family label
	 */
	public final Attribute<String> firstFamilyLabel = new Wrap<>();

	/**
	 * first family range
	 */
	public final Attribute<String> firstFamilyRange = new Wrap<>();

	//second family section

	/**
	 * second family label
	 */
	public final Attribute<String> secondFamilyLabel = new Wrap<>();

	/**
	 * second family range
	 */
	public final Attribute<String> secondFamilyRange = new Wrap<>();

	//probe section

	/**
	 * probe name
	 */
	public final Attribute<String> probeName = new Wrap<>();

	/**
	 * sweep output model path
	 */
	public final Attribute<String> sweepOutput = new Wrap<>();

	/**
	 * first probe table model path
	 */
	public final Attribute<String> firstProbeTable = new Wrap<>();

	/**
	 * probe column index
	 */
	public final Attribute<String> probeColumnIndex = new Wrap<>();

	/**
	 * probe row index
	 */
	public final Attribute<String> probeRowIndex = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public SweepProbe(String name) {
		super(name);
		createSweepProbeModel();
	}

	//#end region

	//#region METHODS

	private void createSweepProbeModel() {
		AttributeRoot root = new AttributeRoot("root");

		org.treez.core.atom.attribute.Page page = root.createPage("page");

		//x section
		Section xSection = page.createSection("xSection", "X");
		xSection.createSectionAction("action", "Run probe", () -> execute(treeViewRefreshable));

		TextField xLabelField = xSection.createTextField(xLabel, "xLabel", "x");
		xLabelField.setLabel("Label for x-Axis");
		ModelPath xRangePath = xSection.createModelPath(xRange, "xRange", "", VariableRange.class, this);
		xRangePath.setLabel("Range for x-Axis");
		xRangePath.setSelectionType(ModelPathSelectionType.FLAT);
		xRangePath.set("root.studies.sweep.threshold");

		//y section
		Section ySection = page.createSection("ySection", "Y");

		TextField yLabelField = ySection.createTextField(yLabel, "yLabel", "y");
		yLabelField.setLabel("Label for y-Axis");

		//first family section
		Section firstFamilySection = page.createSection("firstFamily", "First family");
		firstFamilySection.setExpanded(false);

		TextField firstFamilyField = firstFamilySection.createTextField(firstFamilyLabel, "firstFamilyLabel",
				"family1");
		firstFamilyField.setLabel("Label for first family");
		ModelPath firstFamilyRangePath = firstFamilySection.createModelPath(firstFamilyRange, "firstFamilyRange", "",
				VariableRange.class, this);
		firstFamilyRangePath.setLabel("Range for first family");

		//second family section
		Section secondFamilySection = page.createSection("secondFamily", "Second family");
		secondFamilySection.setExpanded(false);

		TextField secondFamilyField = secondFamilySection.createTextField(secondFamilyLabel, "secondFamilyLabel",
				"family2");
		secondFamilyField.setLabel("Label for second family");
		ModelPath secondFamilyRangePath = secondFamilySection.createModelPath(secondFamilyRange, "secondFamilyRange",
				"", VariableRange.class, this);
		secondFamilyRangePath.setLabel("Range for second family");

		//probe section
		Section probeSection = page.createSection("probe", "Probe");

		TextField probeNameField = probeSection.createTextField(probeName, "propeName", "MyProbe");
		probeNameField.setLabel("Name");
		ModelPath sweepOutputModelPath = probeSection.createModelPath(sweepOutput, "sweepOutput", "", OutputAtom.class,
				this);
		sweepOutputModelPath.setLabel("Sweep output");

		ModelPath firstProbeTablePath = probeSection.createModelPath(firstProbeTable, "firstProbeTable",
				sweepOutputModelPath, Table.class);
		firstProbeTablePath.setLabel("First probe table");

		TextField columnIndex = probeSection.createTextField(probeColumnIndex, "probeColumnIndex", "0");
		columnIndex.setLabel("Column index");

		TextField rowIndex = probeSection.createTextField(probeRowIndex, "probeRowIndex", "0");
		rowIndex.setLabel("Row index");

		setModel(root);

	}

	@Override
	protected void afterCreateControlAdaptionHook() {
		Attribute<String> attribute = getWrappedAttribute(firstProbeTable);
		ModelPath firstProbeTableModelPath = (ModelPath) attribute;
		firstProbeTableModelPath.updateRelativeRootAtom();
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideBaseImage() {
		Image baseImage = Activator.getImage("sweep.png");
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

		sysLog.info("Creating table columns...");

		//determine column names, types and legends------------------------------------------
		List<String> columnNames = new ArrayList<>();
		List<ColumnType> columnTypes = new ArrayList<>();
		List<String> columnLegends = new ArrayList<>();

		//x column----------------------------------------
		String xLabelString = xLabel.get();
		String xColumnName = xLabelString;
		columnNames.add(xColumnName);

		Class<?> xType = getXType();
		ColumnType xColumnType = ColumnType.getDefaultTypeForClass(xType);
		columnTypes.add(xColumnType);

		String xLegend = xLabelString;
		columnLegends.add(xLegend);

		//y columns---------------------------------------

		//get y information
		String yLabelString = yLabel.get();
		ColumnType yColumnType = ColumnType.TEXT;

		//get first family information
		String firstFamilyLabelString = firstFamilyLabel.get();
		List<?> firstFamilyRangeValues = getFirstFamilyRangeValues();

		//get second family information
		String secondFamilyLabelString = firstFamilyLabel.get();
		String secondFamilyPath = secondFamilyRange.get();
		boolean secondFamilyIsSpecified = !"".equals(secondFamilyPath);
		List<?> secondFamilyRangeValues = null;
		if (secondFamilyIsSpecified) {
			VariableRange<?> secondFamilyRangeAtom = (VariableRange<?>) this.getChildFromRoot(secondFamilyPath);
			secondFamilyRangeValues = secondFamilyRangeAtom.getRange();
		}

		//create y column names, types and legends
		int firstFamilyIndex = 1;
		for (Object firstFamilyRangeValue : firstFamilyRangeValues) {
			String columnName = yLabelString + "#" + firstFamilyIndex;
			String firstFamilyRangeValueString = firstFamilyRangeValue.toString();
			String legendText = firstFamilyLabelString + ": " + firstFamilyRangeValueString;
			if (secondFamilyIsSpecified) {
				int secondFamilyIndex = 1;
				for (Object secondFamilyRangeValue : secondFamilyRangeValues) {
					String extendedColumnName = columnName + "#" + secondFamilyIndex;
					columnNames.add(extendedColumnName);
					columnTypes.add(yColumnType);
					String secondFamilyRangeValueString = secondFamilyRangeValue.toString();
					String extendedLegendText = legendText + ", " + secondFamilyLabelString + ": "
							+ secondFamilyRangeValueString;
					columnLegends.add(extendedLegendText);
					secondFamilyIndex++;
				}

			} else {
				columnNames.add(columnName);
				columnTypes.add(yColumnType);
				columnLegends.add(legendText);
			}
			firstFamilyIndex++;
		}

		//create columns--------------------------------------------------------------------------
		createColumns(table, columnNames, columnTypes, columnLegends);

		sysLog.info("Created table columns.");

	}

	private static void createColumns(
			Table table,
			List<String> columnNames,
			List<ColumnType> columnTypes,
			List<String> columnLegends) {

		Columns columns = table.createColumns("columns");

		for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {
			String columnHeader = columnNames.get(columnIndex);
			ColumnType columnType = columnTypes.get(columnIndex);
			String legendText = columnLegends.get(columnIndex);
			columns.createColumn(columnHeader, columnType, legendText);
		}
	}

	private List<?> getFirstFamilyRangeValues() {
		String firstFamilyPath = firstFamilyRange.get();
		boolean firstFamilyIsSpecified = !"".equals(firstFamilyPath);
		List<?> firstFamilyRangeValues = null;
		if (firstFamilyIsSpecified) {
			VariableRange<?> firstFamilyRangeAtom = (VariableRange<?>) this.getChildFromRoot(firstFamilyPath);
			firstFamilyRangeValues = firstFamilyRangeAtom.getRange();
		} else {
			String message = "At least the first family range needs to be specified.";
			throw new IllegalStateException(message);
		}
		return firstFamilyRangeValues;
	}

	private Class<?> getXType() {
		String xPath = xRange.get();
		boolean xIsSpecified = !"".equals(xPath);
		Class<?> xType = null;
		if (xIsSpecified) {
			VariableRange<?> xRangeAtom = (VariableRange<?>) this.getChildFromRoot(xPath);
			xType = xRangeAtom.getType();
		}
		return xType;
	}

	//#end region

	//#region COLLECT PROBE DATA

	@Override
	protected void collectProbeDataAndFillTable(Table table) {

		sysLog.info("Filling probe table...");

		//get x information
		String xLabelString = xLabel.get();
		String xPath = xRange.get();
		boolean xIsSpecified = !"".equals(xPath);
		VariableRange<?> xRangeAtom = null;

		List<?> xRangeValues = null;
		if (xIsSpecified) {
			xRangeAtom = (VariableRange<?>) this.getChildFromRoot(xPath);
			xRangeValues = xRangeAtom.getRange();
		}

		//get y information
		String yLabelString = yLabel.get();

		//get first family information
		String firstFamilyPath = firstFamilyRange.get();
		List<?> firstFamilyRangeValues = getFirstFamilyRangeValues(firstFamilyPath);

		//get second family information
		String secondFamilyPath = secondFamilyRange.get();
		boolean secondFamilyIsSpecified = !"".equals(secondFamilyPath);
		List<?> secondFamilyRangeValues = null;
		if (secondFamilyIsSpecified) {
			VariableRange<?> secondFamilyRangeAtom = (VariableRange<?>) this.getChildFromRoot(secondFamilyPath);
			secondFamilyRangeValues = secondFamilyRangeAtom.getRange();
		}

		//column names
		List<String> columnNames = createColumnNames(xLabelString, yLabelString, firstFamilyRangeValues,
				secondFamilyIsSpecified, secondFamilyRangeValues);

		//get sweep output path
		String sweepOutputPath = sweepOutput.get();

		//get probe table relative path
		String firstProbeTableRelativePath = getFirstProbeRelativePath();
		String[] pathItems = firstProbeTableRelativePath.split("\\.");
		String firstPrefix = pathItems[0];
		int firstIndex = firstPrefix.length() + 1;
		String relativeProbeTablePath = firstProbeTableRelativePath.substring(firstIndex);

		//get probe table prefix
		String prefix = getProbeTablePrefix(firstPrefix);

		fillProbeTable(table, xRangeValues, columnNames, sweepOutputPath, relativeProbeTablePath, prefix);

		sysLog.info("Filled probe table.");

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

	private static List<String> createColumnNames(
			String xLabelString,
			String yLabelString,
			List<?> firstFamilyRangeValues,
			boolean secondFamilyIsSpecified,
			List<?> secondFamilyRangeValues) {
		List<String> columnNames = new ArrayList<>();

		//create first column info (=x column)
		String xColumnName = xLabelString;
		columnNames.add(xColumnName);

		//create remaining column info (=y columns)
		for (int firstFamilyIndex = 1; firstFamilyIndex <= firstFamilyRangeValues.size(); firstFamilyIndex++) {
			String columnName = yLabelString + "#" + firstFamilyIndex;
			if (secondFamilyIsSpecified) {
				for (int secondFamilyIndex = 1; secondFamilyIndex <= secondFamilyRangeValues
						.size(); secondFamilyIndex++) {
					String extendedColumnName = columnName + "#" + secondFamilyIndex;
					columnNames.add(extendedColumnName);
				}
			} else {
				columnNames.add(columnName);
			}
		}
		return columnNames;
	}

	private List<?> getFirstFamilyRangeValues(String firstFamilyPath) {
		boolean firstFamilyIsSpecified = !"".equals(firstFamilyPath);
		List<?> firstFamilyRangeValues = null;
		if (firstFamilyIsSpecified) {
			VariableRange<?> firstFamilyRangeAtom = (VariableRange<?>) this.getChildFromRoot(firstFamilyPath);
			firstFamilyRangeValues = firstFamilyRangeAtom.getRange();
		} else {
			String message = "At least the first family range needs to be specified.";
			throw new IllegalStateException(message);
		}
		return firstFamilyRangeValues;
	}

	private Object getProbeValue(String probeTablePath, int rowIndex, int columnIndex) {

		//get probe table
		Table probeTable = (Table) this.getChildFromRoot(probeTablePath);

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
