package org.treez.results.atom.axis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.CheckBox;
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
import org.treez.javafxd3.d3.scales.OrdinalScale;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.javafxd3.d3.scales.Scale;
import org.treez.javafxd3.d3.scales.Scales;
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

	private Attribute<Boolean> auto = new Wrap<>();

	public final Attribute<String> min = new Wrap<>();

	public final Attribute<String> max = new Wrap<>();

	public final Attribute<Boolean> log = new Wrap<>();

	//public final Attribute<String> datascale = new Wrap<>();

	//public final Attribute<String> lowerPosition = new Wrap<>();

	//public final Attribute<String> upperPosition = new Wrap<>();

	//public final Attribute<String> otherPosition = new Wrap<>();

	//public final Attribute<String> match = new Wrap<>();

	//private Attribute<Boolean> reflect = new Wrap<>();

	//private Attribute<Boolean> outerTicks = new Wrap<>();

	private QuantitativeScale<?> quantitiativeScale = null;

	private OrdinalScale ordinalScale = null;

	private Set<String> ordinalValues;

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page dataPage = root.createPage("data");
		dataPage.setTitle("   Data   ");

		Section general = dataPage.createSection("general");

		general.createTextField(label, this);

		general.createEnumComboBox(mode, this, AxisMode.QUANTITATIVE);

		general.createComboBox(direction, this, "horizontal, vertical", "horizontal");

		CheckBox autoMirrorCheck = general.createCheckBox(autoMirror, this, true);
		autoMirrorCheck.setLabel("Auto mirror");

		general.createCheckBox(hide, this);

		Section domain = dataPage.createSection("domain");

		domain.createCheckBox(auto, this, true);

		domain.createTextField(min, this, "0");

		domain.createTextField(max, this, "1");

		domain.createCheckBox(log, this);

		//data.createTextField(datascale, "datascale", "Scale", "1");

		//data.createTextField(lowerPosition, "lowerPosition", "Min position", "0");

		//data.createTextField(upperPosition, "upperPosition", "Max position", "1");

		//data.createTextField(otherPosition, "otherPosition", "Axis position", "0");

		//data.createTextField(match, "match");

		//general.createComboBox(autoRange, "autoRange", "next-tick,+2%,+5%,+10%,+15%", "next-tick");

		//general.createCheckBox(reflect, this);

		//general.createCheckBox(outerTicks, this).setLabel("Outer ticks");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		Axis parentAxis = (Axis) parent;
		Graph graph = (Graph) parentAxis.getParentAtom();
		Consumer replotGraph = () -> {
			graph.updatePlotWithD3(d3);
		};

		mode.addModificationConsumer("replotAxis", replotGraph);
		autoMirror.addModificationConsumer("replotAxis", replotGraph);
		direction.addModificationConsumer("replotAxis", replotGraph);
		min.addModificationConsumer("replotAxis", replotGraph);
		max.addModificationConsumer("replotAxis", replotGraph);
		log.addModificationConsumer("replotAxis", replotGraph);

		AbstractGraphicsAtom.bindDisplayToBooleanAttribute("hideAxis", axisSelection, hide);

		Attribute<String> height = graph.data.height;
		Attribute<String> width = graph.data.width;

		initializeScale(d3, width, height);
		plotAxisWithD3(d3, axisSelection, parentAxis, width, height);

		return axisSelection;
	}

	private void initializeScale(D3 d3, Attribute<String> width, Attribute<String> height) {

		Double graphWidthInPx = Length.toPx(width.get());
		Double graphHeightInPx = Length.toPx(height.get());

		Scales scales = d3 //
				.scale();

		AxisMode axisMode = getAxisMode();
		switch (axisMode) {
		case QUANTITATIVE:
			createQuantitativeScale(scales, graphWidthInPx, graphHeightInPx);
			break;
		case ORDINAL:
			createOrdinalScale(scales, graphWidthInPx, graphHeightInPx);
			break;
		case TIME:
			throw new IllegalStateException("not yet implemented");
		default:
			throw new IllegalStateException("not yet implemented");
		}
	}

	/**
	 * @return
	 */
	public boolean isHorizontal() {
		boolean isHorizontal = direction.get().equals("" + Direction.HORIZONTAL);
		return isHorizontal;
	}

	private void createQuantitativeScale(Scales scales, Double graphWidthInPx, Double graphHeightInPx) {

		boolean isLog = log.get();

		if (isLog) {
			quantitiativeScale = scales //
					.log() //
					.clamp(true);
		} else {
			quantitiativeScale = scales //
					.linear() //
					.clamp(true);
		}

		String minString = min.get();
		boolean minIsAuto = minString.equals("Auto");
		Double minValue = determineMinValue(isLog, minString, minIsAuto);

		String maxString = max.get();
		boolean maxIsAuto = maxString.equals("Auto");
		Double maxValue = determineMaxValue(maxString, maxIsAuto);

		createQuantitativeScaleRange(graphWidthInPx, graphHeightInPx);

		boolean autoIsUsed = minIsAuto || maxIsAuto;
		if (!autoIsUsed) {
			quantitiativeScale.domain(minValue, maxValue);
		} else {
			throw new IllegalStateException("Auto scale is not yet implemented"); //TODO
		}
	}

	private static Double determineMinValue(boolean isLog, String minString, boolean minIsAuto) {
		Double minValue = determineMaxValue(minString, minIsAuto);

		if (isLog) {
			if (minValue.compareTo(0.0) == 0) {
				final double smallValueNextToZero = 1e-10;
				minValue = smallValueNextToZero;
			}
		}
		return minValue;
	}

	private static Double determineMaxValue(String maxString, boolean maxIsAuto) {
		Double maxValue = null;
		if (!maxIsAuto) {
			try {
				maxValue = Double.parseDouble(maxString);
			} catch (NumberFormatException exception) {
				maxValue = 0.0;
			}
		}
		return maxValue;
	}

	private void createQuantitativeScaleRange(Double graphWidthInPx, Double graphHeightInPx) {
		if (isHorizontal()) {
			quantitiativeScale.range(0.0, graphWidthInPx);
		} else {
			quantitiativeScale.range(graphHeightInPx, 0.0);
		}
	}

	private void createOrdinalScale(Scales scales, Double graphWidthInPx, Double graphHeightInPx) {
		this.ordinalScale = scales.ordinal();
		createOrdinalScaleRange(graphWidthInPx, graphHeightInPx);
		updateOrdinalScaleIfAvailable();

	}

	private void createOrdinalScaleRange(Double graphWidthInPx, Double graphHeightInPx) {
		if (isHorizontal()) {
			ordinalScale.rangeRoundPoints(0.0, graphWidthInPx, 1);
		} else {
			ordinalScale.rangeRoundPoints(graphHeightInPx, 0.0, 1);
		}
	}

	private Selection plotAxisWithD3(
			D3 d3,
			Selection axisSelection,
			Axis axis,
			Attribute<String> width,
			Attribute<String> height) {

		String graphWidthString = width.get();
		Double graphWidthInPx = Length.toPx(graphWidthString);

		String graphHeightString = height.get();
		Double graphHeightInPx = Length.toPx(graphHeightString);

		boolean isHorizontal = isHorizontal();
		boolean isMirrored = autoMirror.get();

		//primary axis
		axisSelection.selectAll(".primary").remove();

		Selection primary = axisSelection //
				.append("g") //
				.attr("id", "primary")
				.attr("class", "primary");
		plotPrimaryAxis(d3, axis, primary, graphHeightInPx, isHorizontal);

		//secondary axis
		axisSelection.selectAll(".secondary").remove();
		if (isMirrored) {

			Selection secondary = axisSelection //
					.append("g") //
					.attr("id", "secondary")
					.attr("class", "secondary");
			plotSecondaryAxis(d3, axis, secondary, graphWidthInPx, isHorizontal);
		}

		return axisSelection;
	}

	private Selection plotPrimaryAxis(
			D3 d3,
			Axis axisAtom,
			Selection axisSelection,
			Double graphHeightInPx,
			boolean isHorizontal) {

		org.treez.javafxd3.d3.svg.Axis axis;
		AxisMode axisMode = getAxisMode();
		switch (axisMode) {
		case QUANTITATIVE:
			axis = createPrimaryQuantitativeD3Axis(d3, axisAtom, axisSelection, graphHeightInPx, isHorizontal);
			break;
		case ORDINAL:
			axis = createPrimaryOrdinalD3Axis(d3, axisAtom, axisSelection, graphHeightInPx, isHorizontal);
			break;
		case TIME:
			throw new IllegalStateException("not yet implemented");
		default:
			throw new IllegalStateException("not yet implemented");
		}

		setAxisDirection(axis, isHorizontal);
		axis.apply(axisSelection);
		return axisSelection;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private org.treez.javafxd3.d3.svg.Axis createPrimaryQuantitativeD3Axis(
			D3 d3,
			Axis axisAtom,
			Selection axisSelection,
			Double graphHeightInPx,
			boolean isHorizontal) {
		int numberOfTicksAimedFor = Integer.parseInt(axisAtom.majorTicks.number.get());
		String tickFormat = axisAtom.tickLabels.format.get();

		//set translation and tick padding
		double tickPadding;
		if (isHorizontal) {
			axisSelection.attr("transform", "translate(0," + graphHeightInPx + ")");
			tickPadding = -6.0;
		} else {
			tickPadding = -12.0;
		}

		//create tick format expression
		//also see https://github.com/mbostock/d3/wiki/Formatting#d3_format
		String formatFunctionExpression = createFormatFunctionExpression(tickFormat);

		//create d3 axis
		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(quantitiativeScale) //
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

	@SuppressWarnings("checkstyle:magicnumber")
	private org.treez.javafxd3.d3.svg.Axis createPrimaryOrdinalD3Axis(
			D3 d3,
			Axis axisAtom,
			Selection axisSelection,
			Double graphHeightInPx,
			boolean isHorizontal) {

		//set translation and tick padding
		double tickPadding;
		if (isHorizontal) {
			axisSelection.attr("transform", "translate(0," + graphHeightInPx + ")");
			tickPadding = -6.0;
		} else {
			tickPadding = -12.0;
		}

		//create d3 axis
		int size = ordinalScale.domain().sizes().get(0);

		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(ordinalScale) //
				.outerTickSize(0.0) //
				.ticks(size)
				.tickPadding(tickPadding);

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

	private static void setAxisDirection(org.treez.javafxd3.d3.svg.Axis axis, boolean isHorizontal) {
		if (isHorizontal) {
			axis.orient(org.treez.javafxd3.d3.svg.Axis.Orientation.BOTTOM);
		} else {
			axis.orient(org.treez.javafxd3.d3.svg.Axis.Orientation.LEFT);
		}
	}

	private Selection plotSecondaryAxis(
			D3 d3,
			Axis axisAtom,
			Selection axisSelection,
			Double graphWidthInPx,
			boolean isHorizontal) {

		if (!isHorizontal) {
			axisSelection.attr("transform", "translate(" + graphWidthInPx + ",0)");
		}

		org.treez.javafxd3.d3.svg.Axis axis;
		AxisMode axisMode = getAxisMode();
		switch (axisMode) {
		case QUANTITATIVE:
			axis = createSecondaryQuantitativeD3Axis(d3, axisAtom);
			break;
		case ORDINAL:
			axis = createSecondaryOrdinalD3Axis(d3);
			break;
		case TIME:
			throw new IllegalStateException("not yet implemented");
		default:
			throw new IllegalStateException("not yet implemented");
		}

		setAxisDirection(axis, isHorizontal);

		axis.apply(axisSelection);
		return axisSelection;
	}

	private org.treez.javafxd3.d3.svg.Axis createSecondaryQuantitativeD3Axis(D3 d3, Axis axisAtom) {
		int numberOfTicksAimedFor = Integer.parseInt(axisAtom.majorTicks.number.get());

		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(quantitiativeScale) //
				.outerTickSize(0.0) //
				.ticks(numberOfTicksAimedFor) //for log axis only the tick labels will be influenced
				.tickFormatExpression("function (d) { return ''; }"); //hides the tick labels
		return axis;
	}

	private org.treez.javafxd3.d3.svg.Axis createSecondaryOrdinalD3Axis(D3 d3) {

		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(ordinalScale) //
				.outerTickSize(0.0) //
				.tickFormatExpression("function (d) { return ''; }"); //hides the tick labels
		return axis;
	}

	private void updateOrdinalScaleIfAvailable() {
		if (ordinalScale != null && ordinalValues != null) {
			String[] values = ordinalValues.toArray(new String[ordinalValues.size()]);
			ordinalScale.domain(values);
		}
	}

	//#end region

	//#region ACCESSORS

	public AxisMode getAxisMode() {
		return AxisMode.from(mode.get());
	}

	public boolean isQuantitative() {
		AxisMode axisMode = getAxisMode();
		return axisMode.equals(AxisMode.QUANTITATIVE);
	}

	public boolean isOrdinal() {
		AxisMode axisMode = getAxisMode();
		return axisMode.equals(AxisMode.ORDINAL);
	}

	public void setOrdinalValues(Set<String> ordinalValues) {
		this.ordinalValues = ordinalValues;
		updateOrdinalScaleIfAvailable();
	}

	public void setOrdinalValues(String... ordinalValues) {
		Set<String> values = new HashSet<String>(Arrays.asList(ordinalValues));
		setOrdinalValues(values);
	}

	public void addOrdinalValue(String ordinalValue) {
		if (ordinalValues == null) {
			ordinalValues = new HashSet<>();
		}
		ordinalValues.add(ordinalValue);
		updateOrdinalScaleIfAvailable();
	}

	public Set<String> getOrdinalValues() {
		return ordinalValues;
	}

	public Scale<?> getScale() {

		AxisMode axisMode = getAxisMode();
		switch (axisMode) {
		case QUANTITATIVE:
			return quantitiativeScale;
		case ORDINAL:
			return ordinalScale;
		case TIME:
			throw new IllegalStateException("not yet implemented");
		default:
			throw new IllegalStateException("not yet implemented");
		}
	}

	//#end region

}
