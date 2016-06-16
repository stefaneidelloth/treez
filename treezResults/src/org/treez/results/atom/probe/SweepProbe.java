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
import org.treez.core.data.column.ColumnBlueprint;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;
import org.treez.core.quantity.Quantity;
import org.treez.data.output.OutputAtom;
import org.treez.data.table.Table;
import org.treez.results.Activator;

/**
 * Collects data from a sweep parameter variation and puts it in a single (probe-) table. That table can easier be used
 * to produce plots than the distributed sweep results.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class SweepProbe extends AbstractProbe {

	private static final Logger LOG = Logger.getLogger(SweepProbe.class);

	//#region ATTRIBUTES

	/**
	 * Used as separator in column names, e.g. probeColumn_1_3
	 */
	private static final String NAME_SEPARATOR = "_";

	//x section

	/**
	 * domain label (name of first column)
	 */
	public final Attribute<String> domainLabel = new Wrap<>();

	/**
	 * domain range (values of first column)
	 */
	public final Attribute<String> domainRange = new Wrap<>();

	//range section

	/**
	 * probe label If there is only one result column, this equals the name of the result column. If there are several
	 * result columns, this will be used as name prefix. The family range indices will also be added to the result
	 * column names.
	 */
	public final Attribute<String> probeLabel = new Wrap<>();

	//first family section

	/**
	 * first family legend (will be saved as column description)
	 */
	public final Attribute<String> firstFamilyLegend = new Wrap<>();

	/**
	 * first family range
	 */
	public final Attribute<String> firstFamilyRange = new Wrap<>();

	//second family section

	/**
	 * second family legend (will be saved as column description)
	 */
	public final Attribute<String> secondFamilyLegend = new Wrap<>();

	/**
	 * second family range
	 */
	public final Attribute<String> secondFamilyRange = new Wrap<>();

	//probe section

	//public final Attribute<String> probeName = new Wrap<>();

	public final Attribute<String> sweepOutput = new Wrap<>();

	public final Attribute<String> firstProbeTable = new Wrap<>();

	public final Attribute<String> probeColumnIndex = new Wrap<>();

	public final Attribute<String> probeRowIndex = new Wrap<>();

	//#end region

	//#region CONSTRUCTORS

	public SweepProbe(String name) {
		super(name);
		createSweepProbeModel();
	}

	//#end region

	//#region METHODS

	@SuppressWarnings({ "checkstyle:javancss", "checkstyle:executablestatementcount" })
	private void createSweepProbeModel() {
		AttributeRoot root = new AttributeRoot("root");

		org.treez.core.atom.attribute.Page page = root.createPage("page");

		//domain section
		Section domainSection = page.createSection("domainSection", "DomainSectionHelpId");
		domainSection.setLabel("Domain");
		domainSection.createSectionAction("action", "Run probe", () -> execute(treeViewRefreshable));

		TextField domainLabelField = domainSection.createTextField(domainLabel, this, "x");
		domainLabelField.setLabel("Domain label");
		ModelPath xRangePath = domainSection.createModelPath(domainRange, this, "", VariableRange.class, this);
		xRangePath.setLabel("Domain range");
		xRangePath.setSelectionType(ModelPathSelectionType.FLAT);
		xRangePath.set("root.studies.sweep.threshold");

		//first family section
		Section firstFamilySection = page.createSection("firstFamily", "First family");
		firstFamilySection.setExpanded(false);

		TextField firstFamilyField = firstFamilySection.createTextField(firstFamilyLegend, this, "family1");
		firstFamilyField.setLabel("Legend for first family");
		ModelPath firstFamilyRangePath = firstFamilySection.createModelPath(firstFamilyRange, this, "",
				VariableRange.class, this);
		firstFamilyRangePath.setLabel("Range for first family");

		//second family section
		Section secondFamilySection = page.createSection("secondFamily", "Second family");
		secondFamilySection.setExpanded(false);

		TextField secondFamilyField = secondFamilySection.createTextField(secondFamilyLegend, this, "family2");
		secondFamilyField.setLabel("Legend for second family");
		ModelPath secondFamilyRangePath = secondFamilySection.createModelPath(secondFamilyRange, this, "",
				VariableRange.class, this);
		secondFamilyRangePath.setLabel("Range for second family");

		//probe section
		Section probeSection = page.createSection("probe", "Probe");

		TextField probeLabelField = probeSection.createTextField(probeLabel, this, "y");
		probeLabelField.setLabel("Probe label");

		ModelPath sweepOutputModelPath = probeSection.createModelPath(sweepOutput, this, "", OutputAtom.class, this);
		sweepOutputModelPath.setLabel("Sweep output");

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
		Image baseImage = Activator.getImage("sweep.png");
		return baseImage;
	}

	//#region CREATE TABLE COLUMNS

	/**
	 * Creates the required columns for the given new table. This also includes meta data about the column legends.
	 *
	 * @param table
	 */
	@Override
	@SuppressWarnings({ "checkstyle:executablestatementcount", "checkstyle:javancss" })
	protected void createTableColumns(Table table) {

		LOG.info("Creating table columns...");

		//create column blueprints
		List<ColumnBlueprint> columnBlueprints = new ArrayList<>();

		//domain column----------------------------------------
		String domainLabelString = domainLabel.get();
		String domainColumnName = domainLabelString;
		Class<?> domainType = getDomainType();
		ColumnType domainColumnType = ColumnType.getDefaultTypeForClass(domainType);
		String domainLegend = domainLabelString;
		columnBlueprints.add(new ColumnBlueprint(domainColumnName, domainColumnType, domainLegend));

		//probe columns---------------------------------------

		//get probe information
		String probeLabelString = probeLabel.get();
		ColumnType probeColumnType = ColumnType.TEXT;

		//get first family information
		String firstFamilyLabelString = firstFamilyLegend.get();
		List<?> firstFamilyRangeValues = getFirstFamilyRangeValues();

		//get second family information
		String secondFamilyLabelString = firstFamilyLegend.get();
		List<?> secondFamilyRangeValues = getSecondFamilyRangeValues();

		//create y column names, types and legends
		boolean firstFamilyIsSpecified = firstFamilyRangeValues != null;
		if (firstFamilyIsSpecified) {
			boolean secondFamilyIsSpecified = secondFamilyRangeValues != null;
			int firstFamilyIndex = 1;
			for (Object firstFamilyRangeValue : firstFamilyRangeValues) {
				String columnName = probeLabelString + NAME_SEPARATOR + firstFamilyIndex;
				String firstFamilyRangeValueString = firstFamilyRangeValue.toString();
				String legendText = firstFamilyLabelString + ": " + firstFamilyRangeValueString;
				if (secondFamilyIsSpecified) {
					int secondFamilyIndex = 1;
					for (Object secondFamilyRangeValue : secondFamilyRangeValues) {
						String extendedColumnName = columnName + NAME_SEPARATOR + secondFamilyIndex;
						String secondFamilyRangeValueString = secondFamilyRangeValue.toString();
						String extendedLegendText = legendText + ", " + secondFamilyLabelString + ": "
								+ secondFamilyRangeValueString;
						columnBlueprints
								.add(new ColumnBlueprint(extendedColumnName, probeColumnType, extendedLegendText));
						secondFamilyIndex++;
					}

				} else {
					columnBlueprints.add(new ColumnBlueprint(columnName, probeColumnType, legendText));
				}
				firstFamilyIndex++;
			}
		} else {
			String columnName = probeLabelString;
			columnBlueprints.add(new ColumnBlueprint(columnName, probeColumnType, ""));
		}

		//create columns
		createColumns(table, columnBlueprints);

		LOG.info("Created table columns.");

	}

	private List<?> getFirstFamilyRangeValues() {
		String firstFamilyPath = firstFamilyRange.get();
		boolean firstFamilyIsSpecified = firstFamilyPath != null && !"".equals(firstFamilyPath);
		List<?> firstFamilyRangeValues = null;
		if (firstFamilyIsSpecified) {
			VariableRange<?> firstFamilyRangeAtom = this.getChildFromRoot(firstFamilyPath);
			firstFamilyRangeValues = firstFamilyRangeAtom.getRange();
		}
		return firstFamilyRangeValues;
	}

	private List<?> getSecondFamilyRangeValues() {
		String secondFamilyPath = secondFamilyRange.get();
		boolean secondFamilyIsSpecified = secondFamilyPath != null && !"".equals(secondFamilyPath);
		List<?> secondFamilyRangeValues = null;
		if (secondFamilyIsSpecified) {
			VariableRange<?> secondFamilyRangeAtom = this.getChildFromRoot(secondFamilyPath);
			secondFamilyRangeValues = secondFamilyRangeAtom.getRange();
		}
		return secondFamilyRangeValues;
	}

	private Class<?> getDomainType() {
		String xPath = domainRange.get();
		boolean xIsSpecified = !"".equals(xPath);
		Class<?> xType = null;
		if (xIsSpecified) {
			VariableRange<?> xRangeAtom = this.getChildFromRoot(xPath);
			xType = xRangeAtom.getType();
		}
		return xType;
	}

	//#end region

	//#region COLLECT PROBE DATA

	@Override
	protected void collectProbeDataAndFillTable(Table table) {

		LOG.info("Filling probe table...");

		//get x information
		String xLabelString = domainLabel.get();
		String xPath = domainRange.get();
		boolean xIsSpecified = !"".equals(xPath);
		VariableRange<?> xRangeAtom = null;

		List<?> xRangeValues = null;
		if (xIsSpecified) {
			xRangeAtom = this.getChildFromRoot(xPath);
			xRangeValues = xRangeAtom.getRange();
		}

		//get y information
		String yLabelString = probeLabel.get();

		//get first family information
		List<?> firstFamilyRangeValues = getFirstFamilyRangeValues();

		//get second family information
		List<?> secondFamilyRangeValues = getSecondFamilyRangeValues();

		//column names
		List<String> columnNames = createColumnNames(xLabelString, yLabelString, firstFamilyRangeValues,
				secondFamilyRangeValues);

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

	private static List<String> createColumnNames(
			String xLabelString,
			String yLabelString,
			List<?> firstFamilyRangeValues,
			List<?> secondFamilyRangeValues) {
		List<String> columnNames = new ArrayList<>();

		//create first column info (=x column)
		String xColumnName = xLabelString;
		columnNames.add(xColumnName);

		//create remaining column info (=y columns)
		boolean firstFamilyIsSpecified = firstFamilyRangeValues != null;
		boolean secondFamilyIsSpecified = secondFamilyRangeValues != null;
		if (firstFamilyIsSpecified) {
			for (int firstFamilyIndex = 1; firstFamilyIndex <= firstFamilyRangeValues.size(); firstFamilyIndex++) {
				String columnName = yLabelString + NAME_SEPARATOR + firstFamilyIndex;
				if (secondFamilyIsSpecified) {
					for (int secondFamilyIndex = 1; secondFamilyIndex <= secondFamilyRangeValues
							.size(); secondFamilyIndex++) {
						String extendedColumnName = columnName + NAME_SEPARATOR + secondFamilyIndex;
						columnNames.add(extendedColumnName);
					}
				} else {
					columnNames.add(columnName);
				}
			}
		} else {
			String yColumnName = yLabelString;
			columnNames.add(yColumnName);
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
