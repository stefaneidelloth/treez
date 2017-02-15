package org.treez.results.atom.tornado;

import java.util.ArrayList;
import java.util.List;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.EnumComboBox;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.data.table.nebula.Table;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.Scale;
import org.treez.results.atom.axis.Axis;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	//#region GENERAL

	public final Attribute<String> dataMode = new Wrap<>();

	public final Attribute<String> tablePath = new Wrap<>();

	public final Attribute<String> leftLegendText = new Wrap<>();

	public final Attribute<String> rightLegendText = new Wrap<>();

	public final Attribute<Double> barFillRatio = new Wrap<>();

	//#end region

	//#region INPUT

	public final Attribute<String> inputLabel = new Wrap<>();

	public final Attribute<String> inputBase = new Wrap<>();

	public final Attribute<String> inputLeft = new Wrap<>();

	public final Attribute<String> inputRight = new Wrap<>();

	public final Attribute<String> inputUnit = new Wrap<>();

	public final Attribute<String> inputAxis = new Wrap<>();

	//#end region

	//#region OUTPUT

	public final Attribute<String> outputBase = new Wrap<>();

	public final Attribute<String> outputLeft = new Wrap<>();

	public final Attribute<String> outputRight = new Wrap<>();

	public final Attribute<String> outputUnit = new Wrap<>();

	public final Attribute<String> outputAxis = new Wrap<>();

	public final Attribute<String> sortingMode = new Wrap<>();

	//#end region

	private Tornado tornado;

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {
		Page dataPage = root.createPage("data", "   Data   ");
		createGeneralSection(parent, dataPage);
		createInputSection(parent, dataPage);
		createOutputSection(parent, dataPage);

	}

	private void createGeneralSection(AbstractAtom<?> parent, Page dataPage) {
		Section generalSection = dataPage.createSection("general");

		EnumComboBox<DataMode> dataModeBox = generalSection.createEnumComboBox(dataMode, this, DataMode.TABLE);
		dataModeBox.createEnableTarget("enableTablePath", DataMode.TABLE, "data.general.tablePath");
		dataModeBox.createDisableTarget("disableInputColumns", DataMode.TABLE, "data.input");
		dataModeBox.createDisableTarget("disableOutputColumns", DataMode.TABLE, "data.output");

		generalSection.createModelPath(tablePath, this, "", Table.class).setLabel("Table");

		Class<?> targetClass = org.treez.results.atom.axis.Axis.class;
		generalSection //
				.createModelPath(inputAxis, this, "", targetClass, parent) //
				.setLabel("Input axis");

		generalSection //
				.createModelPath(outputAxis, this, "", targetClass, parent) //
				.setLabel("Output axis");

		generalSection.createTextField(outputUnit, this).setLabel("Unit");

		generalSection.createEnumComboBox(sortingMode, this, SortingMode.LARGEST_DIFFERENCE) //
				.setLabel("Sorting mode");

		generalSection.createTextField(leftLegendText, this).setLabel("Left legend text");
		generalSection.createTextField(rightLegendText, this).setLabel("Right legend text");

		final double defaultBarFillRatio = 0.75;
		generalSection.createDoubleVariableField(barFillRatio, this, defaultBarFillRatio);
	}

	private void createInputSection(AbstractAtom<?> parent, Page dataPage) {
		Section inputSection = dataPage.createSection("input");
		inputSection.setLabel("Input columns");

		Class<?> targetClass = org.treez.data.column.Column.class;
		String defaultValue = "root.data.table.columns.inputLabel";
		inputSection.createModelPath(inputLabel, this, defaultValue, targetClass, parent) //
				.setLabel("Label");

		defaultValue = "root.data.table.columns.inputBase";
		inputSection.createModelPath(inputBase, this, defaultValue, targetClass, parent) //
				.setLabel("Base");

		defaultValue = "root.data.table.columns.inputLeft";
		inputSection.createModelPath(inputLeft, this, defaultValue, targetClass, parent) //
				.setLabel("Left");

		defaultValue = "root.data.table.columns.inputRight";
		inputSection.createModelPath(inputRight, this, defaultValue, targetClass, parent) //
				.setLabel("Right");

		defaultValue = "root.data.table.columns.inputUnit";
		inputSection.createModelPath(inputUnit, this, defaultValue, targetClass, parent) //
				.setLabel("Unit");

	}

	private void createOutputSection(AbstractAtom<?> parent, Page dataPage) {
		Class<?> targetClass;
		String defaultValue;
		Section outputSection = dataPage.createSection("output");
		outputSection.setLabel("Output columns");

		targetClass = org.treez.data.column.Column.class;
		defaultValue = "root.data.table.columns.outputBase";
		outputSection.createModelPath(outputBase, this, defaultValue, targetClass, parent) //
				.setLabel("Base");

		defaultValue = "root.data.table.columns.outputLeft";
		outputSection.createModelPath(outputLeft, this, defaultValue, targetClass, parent) //
				.setLabel("Left");

		defaultValue = "root.data.table.columns.outputRight";
		outputSection.createModelPath(outputRight, this, defaultValue, targetClass, parent) //
				.setLabel("Right");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection xySelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		tornado = (Tornado) parent;
		Consumer dataChangedConsumer = () -> {
			tornado.updatePlotWithD3(d3);
		};
		inputLabel.addModificationConsumer("replot", dataChangedConsumer);
		inputBase.addModificationConsumer("replot", dataChangedConsumer);
		inputLeft.addModificationConsumer("replot", dataChangedConsumer);
		inputRight.addModificationConsumer("replot", dataChangedConsumer);
		inputUnit.addModificationConsumer("replot", dataChangedConsumer);
		inputAxis.addModificationConsumer("replot", dataChangedConsumer);

		outputBase.addModificationConsumer("replot", dataChangedConsumer);
		outputLeft.addModificationConsumer("replot", dataChangedConsumer);
		outputRight.addModificationConsumer("replot", dataChangedConsumer);
		outputUnit.addModificationConsumer("replot", dataChangedConsumer);
		outputAxis.addModificationConsumer("replot", dataChangedConsumer);

		// legendText
		leftLegendText.addModificationConsumer("replot", dataChangedConsumer);
		rightLegendText.addModificationConsumer("replot", dataChangedConsumer);
		sortingMode.addModificationConsumer("replot", dataChangedConsumer);
		barFillRatio.addModificationConsumer("replot", dataChangedConsumer);

		return xySelection;
	}

	public String getLeftBarDataString() {
		List<Object> inputLabelData = getInputLabelData();
		List<Object> inputLeftData = getInputLeftData();
		List<Object> outputBaseData = getOutputBaseData();
		List<Object> outputLeftData = getOutputLeftData();
		int dataSize = outputBaseData.size();

		boolean inputAxisIsOrdinal = getInputAxis().isOrdinal();

		List<String> rowList = new java.util.ArrayList<>();
		for (int rowIndex = 0; rowIndex < dataSize; rowIndex++) {

			String inputLeftString = inputLeftData.get(rowIndex).toString();
			Double inputLeft = Double.parseDouble(inputLeftString);

			String outputBaseString = outputBaseData.get(rowIndex).toString();
			Double outputBase = Double.parseDouble(outputBaseString);

			String outputLeftString = outputLeftData.get(rowIndex).toString();
			Double outputLeft = Double.parseDouble(outputLeftString);

			Double difference = outputBase - outputLeft;

			Double position = outputLeft;
			Double size = difference;
			if (difference < 0) {
				position = outputBase;
				size = -difference;
			}

			String inputValue;
			if (inputAxisIsOrdinal) {
				inputValue = "'" + inputLabelData.get(rowIndex).toString() + "'";
			} else {
				inputValue = "" + (rowIndex + 1);
			}

			String rowString = "{key:" + inputValue + //
					", input:" + inputLeft + //
					", value:" + position + //
					", size:" + size + //

					"}";
			rowList.add(rowString);
		}
		String dataString = "[" + String.join(",", rowList) + "]";
		return dataString;
	}

	public String getRightBarDataString() {
		List<Object> inputLabelData = getInputLabelData();
		List<Object> inputRightData = getInputLeftData();
		List<Object> outputBaseData = getOutputBaseData();
		List<Object> outputRightData = getOutputRightData();
		int dataSize = outputBaseData.size();

		boolean inputAxisIsOrdinal = getInputAxis().isOrdinal();

		List<String> rowList = new java.util.ArrayList<>();
		for (int rowIndex = 0; rowIndex < dataSize; rowIndex++) {

			String inputRightString = inputRightData.get(rowIndex).toString();
			Double inputRight = Double.parseDouble(inputRightString);

			String outputBaseString = outputBaseData.get(rowIndex).toString();
			Double outputBase = Double.parseDouble(outputBaseString);

			String outputRightString = outputRightData.get(rowIndex).toString();
			Double outputRight = Double.parseDouble(outputRightString);

			Double difference = outputRight - outputBase;
			Double position = outputBase;
			Double size = difference;
			if (difference < 0) {
				position = outputRight;
				size = -difference;
			}

			String key;
			if (inputAxisIsOrdinal) {
				key = "'" + inputLabelData.get(rowIndex).toString() + "'";
			} else {
				key = "" + (rowIndex + 1);
			}

			String rowString = "{key:" + key + //
					", input:" + inputRight + //
					",value:" + position + //
					",size:" + size + //
					"}";
			rowList.add(rowString);
		}
		String dataString = "[" + String.join(",", rowList) + "]";
		return dataString;
	}

	public List<Double> getAllBarData() {

		List<Object> rangeBaseData = getOutputBaseData();
		List<Object> rangeLeftData = getOutputLeftData();
		List<Object> rangeRightData = getOutputRightData();
		int dataSize = rangeBaseData.size();
		List<Double> allData = new java.util.ArrayList<>();
		for (int rowIndex = 0; rowIndex < dataSize; rowIndex++) {

			Double base = parseDouble(rangeBaseData.get(rowIndex));
			if (base != null) {
				allData.add(base);
			}

			Double left = parseDouble(rangeLeftData.get(rowIndex));
			if (left != null) {
				allData.add(left);
			}

			Double right = parseDouble(rangeRightData.get(rowIndex));
			if (right != null) {
				allData.add(right);
			}

		}

		return allData;
	}

	private static Double parseDouble(Object object) {
		try {
			return Double.parseDouble(object.toString());
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	public List<Object> getInputLabelData() {
		String dataPath = inputLabel.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getInputBaseData() {
		String dataPath = inputBase.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getInputLeftData() {
		String dataPath = inputLeft.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getInputRightData() {
		String dataPath = inputRight.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getOutputBaseData() {
		String dataPath = outputBase.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getOutputLeftData() {
		String dataPath = outputLeft.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getOutputRightData() {
		String dataPath = outputRight.get();
		return getValuesWithColumnPath(dataPath);
	}

	private List<Object> getValuesWithColumnPath(String dataPath) {
		if (dataPath.isEmpty()) {
			return new ArrayList<>();
		}
		org.treez.data.column.Column dataColumn = tornado.getChildFromRoot(dataPath);
		List<Object> dataValues = dataColumn.getValues();
		return dataValues;
	}

	//#end region

	//#region ACCESSORS

	public Scale<?> getInputScale() {
		Axis inputAxis = getInputAxis();
		if (inputAxis == null) {
			return null;
		}
		Scale<?> scale = inputAxis.getScale();
		return scale;
	}

	public Scale<?> getOutputScale() {
		Axis outputAxis = getOutputAxis();
		if (outputAxis == null) {
			return null;
		}
		Scale<?> scale = outputAxis.getScale();
		return scale;
	}

	public int getDataSize() {
		List<Object> domainBaseData = getInputBaseData();
		return domainBaseData.size();
	}

	public Axis getInputAxis() {
		String xAxisPath = inputAxis.get();
		if (xAxisPath == null || xAxisPath.isEmpty()) {
			return null;
		}
		Axis xAxisAtom = tornado.getChildFromRoot(xAxisPath);
		return xAxisAtom;
	}

	public Axis getOutputAxis() {
		String yAxisPath = outputAxis.get();
		if (yAxisPath == null || yAxisPath.isEmpty()) {
			return null;
		}
		Axis yAxisAtom = tornado.getChildFromRoot(yAxisPath);
		return yAxisAtom;
	}

	//#end region

}
