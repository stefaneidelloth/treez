package org.treez.results.atom.axis;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.CheckBox;
import org.treez.core.atom.attribute.EnumComboBox;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.Scales;
import org.treez.results.atom.axis.scale.OrdinalScaleBuilder;
import org.treez.results.atom.axis.scale.QuantitativeScaleBuilder;
import org.treez.results.atom.graph.Graph;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> label = new Wrap<>();

	public final Attribute<String> mode = new Wrap<>();

	public final Attribute<String> direction = new Wrap<>();

	/**
	 * If true, the axis is mirrored to the other side of the graph: a second axis is shown.
	 */
	public Attribute<Boolean> autoMirror = new Wrap<>();

	public Attribute<Boolean> hide = new Wrap<>();

	public Attribute<Boolean> autoMin = new Wrap<>();

	public final Attribute<String> borderMin = new Wrap<>();

	public final Attribute<Double> min = new Wrap<>();

	//public final Attribute<String> minTime = new Wrap<>();

	public Attribute<Boolean> autoMax = new Wrap<>();

	public final Attribute<String> borderMax = new Wrap<>();

	public final Attribute<Double> max = new Wrap<>();

	//public final Attribute<String> maxTime = new Wrap<>();

	//public final Attribute<String> timeFormat = new Wrap<>();

	//public final Attribute<String> timeZone = new Wrap<>();

	public final Attribute<Boolean> log = new Wrap<>();

	private org.treez.javafxd3.d3.svg.Axis primaryD3Axis;

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page dataPage = root.createPage("data");
		dataPage.setTitle("   Data   ");

		createGeneralSection(dataPage);
		createDomainSection(dataPage);
	}

	private void createGeneralSection(Page dataPage) {
		Section general = dataPage.createSection("general");

		general.createTextField(label, this);

		EnumComboBox<AxisMode> modeCombo = general.createEnumComboBox(mode, this, AxisMode.QUANTITATIVE);
		modeCombo.createDisableTarget("disableDomainSection", AxisMode.ORDINAL, "data.domain");

		general.createEnumComboBox(direction, this, Direction.HORIZONTAL);

		CheckBox autoMirrorCheck = general.createCheckBox(autoMirror, this, true);
		autoMirrorCheck.setLabel("Auto mirror");

		general.createCheckBox(hide, this);
	}

	private void createDomainSection(Page dataPage) {
		Section domain = dataPage.createSection("domain");

		CheckBox autoMinCheckBox = domain.createCheckBox(autoMin, this, true) //
				.setLabel("Auto min");
		autoMinCheckBox.createEnableTarget("enableBorderMin", "data.domain.borderMin");
		autoMinCheckBox.createDisableTarget("disableMin", "data.domain.min");

		domain.createEnumComboBox(borderMin, this, BorderMode.TWO);

		domain.createDoubleVariableField(min, this, 0.0).setEnabled(false);
		//domain.createTextField(minTime, this, "0") //
		//		.setLabel("Min time") //
		//		.setEnabled(false);

		CheckBox autoMaxCheckBox = domain.createCheckBox(autoMax, this, true) //
				.setLabel("Auto max");
		autoMinCheckBox.createEnableTarget("enableBorderMax", "data.domain.borderMax");
		autoMaxCheckBox.createDisableTarget("disableMax", "data.domain.max");

		domain.createEnumComboBox(borderMax, this, BorderMode.TWO);

		domain.createDoubleVariableField(max, this, 1.0) //
				.setEnabled(false);
		/*
		domain.createTextField(maxTime, this, "1") //
				.setLabel("Max time") //
				.setEnabled(false);

		domain.createTextField(timeFormat, this, "%a %d") //
				.setLabel("Time format") //
				.setEnabled(false);

		domain.createTextField(timeZone, this, "%a %d") //
				.setLabel("Time zone") //
				.setEnabled(false);
		*/

		domain.createCheckBox(log, this);
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		Axis parentAxis = (Axis) parent;
		Graph graph = (Graph) parentAxis.getParentAtom();

		addUpdateListeners(d3, graph);

		AbstractGraphicsAtom.bindDisplayToBooleanAttribute("hideAxis", axisSelection, hide);

		Double graphWidthInPx = Length.toPx(graph.data.width.get());
		Double graphHeightInPx = Length.toPx(graph.data.height.get());

		createScale(d3, parentAxis, graphWidthInPx, graphHeightInPx);
		plotAxisWithD3(d3, axisSelection, parentAxis, graphWidthInPx, graphHeightInPx);

		return axisSelection;
	}

	private void addUpdateListeners(D3 d3, Graph graph) {
		Consumer replotGraph = () -> {
			graph.updatePlotWithD3(d3);
		};

		mode.addModificationConsumer("replotAxis", replotGraph);
		direction.addModificationConsumer("replotAxis", replotGraph);
		autoMirror.addModificationConsumer("replotAxis", replotGraph);

		autoMin.addModificationConsumer("replotAxis", replotGraph);
		borderMin.addModificationConsumer("replotAxis", replotGraph);
		min.addModificationConsumer("replotAxis", replotGraph);

		autoMax.addModificationConsumer("replotAxis", replotGraph);
		borderMax.addModificationConsumer("replotAxis", replotGraph);
		max.addModificationConsumer("replotAxis", replotGraph);

		log.addModificationConsumer("replotAxis", replotGraph);
	}

	private void createScale(D3 d3, Axis axis, Double graphWidthInPx, Double graphHeightInPx) {

		Scales scaleFactory = d3 //
				.scale();

		AxisMode axisMode = getAxisMode();
		switch (axisMode) {
		case QUANTITATIVE:
			QuantitativeScaleBuilder scaleBuilder = axis.getQuantitativeScaleBuilder();
			scaleBuilder.createScale(scaleFactory, graphWidthInPx, graphHeightInPx);
			break;
		case ORDINAL:
			OrdinalScaleBuilder ordinalScaleBuilder = axis.getOrdinalScaleBuilder();
			ordinalScaleBuilder.createScale(scaleFactory, isHorizontal(), graphWidthInPx, graphHeightInPx);
			break;
		//case TIME:
		//	throw new IllegalStateException("not yet implemented");
		default:
			throw new IllegalStateException("not yet implemented");
		}
	}

	private Selection plotAxisWithD3(
			D3 d3,
			Selection axisSelection,
			Axis axis,
			Double graphWidthInPx,
			Double graphHeightInPx) {

		axisSelection.selectAll(".primary").remove();
		plotPrimaryAxis(d3, axis, axisSelection, graphHeightInPx);

		axisSelection.selectAll(".secondary").remove();
		if (autoMirror.get()) {
			plotSecondaryAxis(d3, axis, axisSelection, graphWidthInPx);
		}

		return axisSelection;
	}

	private Selection plotPrimaryAxis(D3 d3, Axis axisAtom, Selection axisSelection, Double graphHeightInPx) {

		Selection primaryAxisSelection = axisSelection //
				.append("g") //
				.attr("id", "primary")
				.attr("class", "primary");

		AxisMode axisMode = getAxisMode();
		switch (axisMode) {
		case QUANTITATIVE:
			primaryD3Axis = createPrimaryQuantitativeD3Axis(d3, axisAtom, primaryAxisSelection, graphHeightInPx);
			break;
		case ORDINAL:
			primaryD3Axis = createPrimaryOrdinalD3Axis(d3, axisAtom, primaryAxisSelection, graphHeightInPx);
			break;
		//case TIME:
		//	throw new IllegalStateException("not yet implemented");
		default:
			throw new IllegalStateException("not yet implemented");
		}

		setAxisDirection(primaryD3Axis);
		primaryD3Axis.apply(primaryAxisSelection);
		return primaryAxisSelection;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private org.treez.javafxd3.d3.svg.Axis createPrimaryQuantitativeD3Axis(
			D3 d3,
			Axis axisAtom,
			Selection axisSelection,
			Double graphHeightInPx) {
		int numberOfTicksAimedFor = Integer.parseInt(axisAtom.majorTicks.number.get());
		String tickFormat = axisAtom.tickLabels.format.get();

		//set translation and tick padding
		double tickPadding;
		if (isHorizontal()) {
			axisSelection.attr("transform", "translate(0," + graphHeightInPx + ")");
			tickPadding = -6.0;
		} else {
			tickPadding = -12.0;
		}

		//create tick format expression
		//also see https://github.com/mbostock/d3/wiki/Formatting#d3_format
		String formatFunctionExpression = createFormatFunctionExpression(tickFormat);

		QuantitativeScaleBuilder scaleBuilder = axisAtom.getQuantitativeScaleBuilder();

		//create d3 axis
		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(scaleBuilder.getScale()) //
				.outerTickSize(0.0) //
				.tickPadding(tickPadding);

		if (log.get()) {
			//for log axis only the number of tick labels will be influenced, not the number of tick lines
			axis.ticksExpression(numberOfTicksAimedFor, formatFunctionExpression);
		} else {
			axis.ticks(numberOfTicksAimedFor);
			axis.tickFormatExpression(formatFunctionExpression);
		}
		return axis;
	}

	private String createFormatFunctionExpression(String tickFormat) {
		String formatString = tickFormat;
		if (formatString.equals("")) {
			if (log.get()) {
				formatString = "log";
			} else {
				formatString = "g";
			}
		}
		String formatFunctionExpression = "d3.format('" + formatString + "')"; //"function (d) { return d; }";

		if (formatString.equals("log")) {

			//use unicode characters to create exponent notation 10^0, 10^1, ...
			formatFunctionExpression = "function(d){" //
					+ " var superscript = '\u2070\u00B9\u00B2\u00B3\u2074" // super script numbers in
					+ "\u2075\u2076\u2077\u2078\u2079';" // unicode from 0 to 9
					+ " function formatPower (d){" //
					+ "  return (d + \"\").split(\"\").map(function(c) { return superscript[c]; }).join(\"\");" //
					+ " }" //
					+ " var power = formatPower(Math.round(Math.log(d) / Math.LN10));"
					+ " var displayString = '10' + power;" //
					+ " return displayString;" //
					+ "}";

		}
		return formatFunctionExpression;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private org.treez.javafxd3.d3.svg.Axis createPrimaryOrdinalD3Axis(
			D3 d3,
			Axis axisAtom,
			Selection axisSelection,
			Double graphHeightInPx) {

		//set translation and tick padding
		double tickPadding;
		if (isHorizontal()) {
			axisSelection.attr("transform", "translate(0," + graphHeightInPx + ")");
			tickPadding = -6.0;
		} else {
			tickPadding = -12.0;
		}

		OrdinalScaleBuilder scaleBuilder = axisAtom.getOrdinalScaleBuilder();

		//create d3 axis
		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(scaleBuilder.getScale()) //
				.outerTickSize(0.0) //
				.ticks(scaleBuilder.getSize())
				.tickPadding(tickPadding);

		return axis;
	}

	private void setAxisDirection(org.treez.javafxd3.d3.svg.Axis axis) {
		if (isHorizontal()) {
			axis.orient(org.treez.javafxd3.d3.svg.Axis.Orientation.BOTTOM);
		} else {
			axis.orient(org.treez.javafxd3.d3.svg.Axis.Orientation.LEFT);
		}
	}

	private Selection plotSecondaryAxis(D3 d3, Axis axisAtom, Selection axisSelection, Double graphWidthInPx) {

		Selection secondaryAxisSelection = axisSelection //
				.append("g") //
				.attr("id", "secondary")
				.attr("class", "secondary");

		if (!isHorizontal()) {
			secondaryAxisSelection.attr("transform", "translate(" + graphWidthInPx + ",0)");
		}

		org.treez.javafxd3.d3.svg.Axis axis;
		AxisMode axisMode = getAxisMode();
		switch (axisMode) {
		case QUANTITATIVE:
			axis = createSecondaryQuantitativeD3Axis(d3, axisAtom);
			break;
		case ORDINAL:
			axis = createSecondaryOrdinalD3Axis(d3, axisAtom);
			break;
		//case TIME:
		//	throw new IllegalStateException("not yet implemented");
		default:
			throw new IllegalStateException("not yet implemented");
		}

		setAxisDirection(axis);

		axis.apply(secondaryAxisSelection);
		return secondaryAxisSelection;
	}

	private static org.treez.javafxd3.d3.svg.Axis createSecondaryQuantitativeD3Axis(D3 d3, Axis axisAtom) {
		int numberOfTicksAimedFor = Integer.parseInt(axisAtom.majorTicks.number.get());

		QuantitativeScaleBuilder scaleBuilder = axisAtom.getQuantitativeScaleBuilder();

		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(scaleBuilder.getScale()) //
				.outerTickSize(0.0) //
				.ticks(numberOfTicksAimedFor) //for log axis only the tick labels will be influenced
				.tickFormatExpression("function (d) { return ''; }"); //hides the tick labels
		return axis;
	}

	private static org.treez.javafxd3.d3.svg.Axis createSecondaryOrdinalD3Axis(D3 d3, Axis axisAtom) {

		OrdinalScaleBuilder scaleBuilder = axisAtom.getOrdinalScaleBuilder();

		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(scaleBuilder.getScale()) //
				.outerTickSize(0.0) //
				.tickFormatExpression("function (d) { return ''; }"); //hides the tick labels
		return axis;
	}

	//#end region

	//#region ACCESSORS

	public AxisMode getAxisMode() {
		return AxisMode.from(mode.get());
	}

	public boolean isHorizontal() {
		boolean isHorizontal = direction.get().equals("" + Direction.HORIZONTAL);
		return isHorizontal;
	}

	public boolean isQuantitative() {
		AxisMode axisMode = getAxisMode();
		return axisMode.equals(AxisMode.QUANTITATIVE);
	}

	public boolean isOrdinal() {
		AxisMode axisMode = getAxisMode();
		return axisMode.equals(AxisMode.ORDINAL);
	}

	//#end region

}
