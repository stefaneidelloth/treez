package org.treez.results.atom.axis;

import java.util.function.Consumer;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.CheckBox;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.javafxd3.d3.scales.Scale;
import org.treez.javafxd3.d3.scales.Scales;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageModel;

/**
 * Represents the main settings of an axis
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPropertiesPageModel {

	//#region ATTRIBUTES

	/**
	 * Label
	 */
	public final Attribute<String> label = new Wrap<>();

	/**
	 * Min
	 */
	public final Attribute<String> min = new Wrap<>();

	/**
	 * Max
	 */
	public final Attribute<String> max = new Wrap<>();

	/**
	 * Log
	 */
	public final Attribute<Boolean> log = new Wrap<>();

	/**
	 * Axis mode (numeric, datetime or labels)
	 */
	//public final Attribute<String> mode = new Wrap<>();

	/**
	 * Data scale
	 */
	//public final Attribute<String> datascale = new Wrap<>();

	/**
	 * Direction
	 */
	public final Attribute<String> direction = new Wrap<>();

	/**
	 * Lower position
	 */
	//public final Attribute<String> lowerPosition = new Wrap<>();

	/**
	 * Upper position
	 */
	//public final Attribute<String> upperPosition = new Wrap<>();

	/**
	 * Other position
	 */
	//public final Attribute<String> otherPosition = new Wrap<>();

	/**
	 * Match
	 */
	//public final Attribute<String> match = new Wrap<>();

	/**
	 * Hides the axis
	 */
	public Attribute<Boolean> hide = new Wrap<>();

	/**
	 *
	 */
	//private Attribute<String> autoRange = new Wrap<>();

	/**
	 * If true, the axis is mirrored to the other side of the graph: a second axis is shown.
	 */
	public Attribute<Boolean> autoMirror = new Wrap<>();

	/**
	 *
	 */
	//private Attribute<Boolean> reflect = new Wrap<>();

	/**
	 *
	 */
	//private Attribute<Boolean> outerTicks = new Wrap<>();

	private QuantitativeScale<?> scale;

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page dataPage = root.createPage("data");
		dataPage.setTitle("   Data   ");

		Section data = dataPage.createSection("data");

		data.createTextField(label, "Label");

		data.createTextField(min, "min", "0");

		data.createTextField(max, "max", "1");

		data.createCheckBox(log, "log");

		//data.createComboBox(mode, "mode", "numeric, datetime, labels", "numeric");

		//data.createTextField(datascale, "datascale", "Scale", "1");

		data.createComboBox(direction, "direction", "horizontal, vertical", "horizontal");

		//data.createTextField(lowerPosition, "lowerPosition", "Min position", "0");

		//data.createTextField(upperPosition, "upperPosition", "Max position", "1");

		//data.createTextField(otherPosition, "otherPosition", "Axis position", "0");

		//data.createTextField(match, "match");

		data.createCheckBox(hide, "hide");

		//general.createComboBox(autoRange, "autoRange", "next-tick,+2%,+5%,+10%,+15%", "next-tick");

		CheckBox autoMirrorCheck = data.createCheckBox(autoMirror, "autoMirror", true);
		autoMirrorCheck.setLabel("Auto mirror");

		//general.createCheckBox(reflect, "reflect");

		//general.createCheckBox(outerTicks, "outerTicks", "Outer ticks");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, GraphicsAtom parent) {

		Axis parentAxis = (Axis) parent;

		//hint: the auto mirror option will be considered by the other plot pages
		autoMirror.addModificationConsumer("replotAxis", (data) -> parentAxis.plotPageModels(d3, rectSelection));

		GraphicsAtom.bindDisplayToBooleanAttribute("hideAxis", axisSelection, hide);

		Graph graph = (Graph) parentAxis.getParentAtom();
		Attribute<String> height = graph.main.height;
		Attribute<String> width = graph.main.width;

		Consumer<String> replotAxisConsumer = (data) -> {
			initializeScale(d3, width, height);
			plotAxisWithD3(d3, axisSelection, parentAxis, width, height);
		};

		width.addModificationConsumer("replotAxis", replotAxisConsumer);
		height.addModificationConsumer("replotAxis", replotAxisConsumer);
		direction.addModificationConsumer("replotAxis", replotAxisConsumer);
		log.addModificationConsumer("replotAxis", (data) -> replotAxisConsumer.accept(null));
		min.addModificationConsumer("replotAxis", replotAxisConsumer);
		max.addModificationConsumer("replotAxis", replotAxisConsumer);

		replotAxisConsumer.accept(null);

		return axisSelection;
	}

	private void initializeScale(D3 d3, Attribute<String> width, Attribute<String> height) {
		String graphWidthString = width.get();
		Double graphWidthInPx = Length.toPx(graphWidthString);

		String graphHeightString = height.get();
		Double graphHeightInPx = Length.toPx(graphHeightString);

		boolean isHorizontal = isHorizontal();

		boolean isLog = log.get();

		String minString = min.get();
		boolean minIsAuto = minString.equals("Auto");
		Double minValue = Double.parseDouble(minString);

		String maxString = max.get();
		boolean maxIsAuto = maxString.equals("Auto");
		Double maxValue = null;
		if (!maxIsAuto) {
			maxValue = Double.parseDouble(maxString);
		}

		if (isLog) {
			if (minValue.compareTo(0.0) == 0) {
				final double smallValueNextToZero = 1e-10;
				minValue = smallValueNextToZero;
			}
		}

		Scales scales = d3 //
				.scale();

		createLinOrLogScale(isLog, scales);
		createScaleRange(graphWidthInPx, graphHeightInPx, isHorizontal);

		//domain
		boolean autoIsUsed = minIsAuto || maxIsAuto;
		if (!autoIsUsed) {
			scale.domain(minValue, maxValue);
		} else {
			throw new IllegalStateException("Auto scale is not yet implemented");
		}
	}

	/**
	 * @return
	 */
	public boolean isHorizontal() {
		boolean isHorizontal = direction.get().equals("" + Direction.HORIZONTAL);
		return isHorizontal;
	}

	private void createLinOrLogScale(boolean isLog, Scales scales) {
		if (isLog) {
			scale = scales //
					.log() //
					.clamp(true);
		} else {
			scale = scales //
					.linear() //
					.clamp(true);
		}
	}

	private void createScaleRange(Double graphWidthInPx, Double graphHeightInPx, boolean isHorizontal) {
		if (isHorizontal) {
			scale.range(0.0, graphWidthInPx);
		} else {
			scale.range(graphHeightInPx, 0.0);
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

		int numberOfTicksAimedFor = Integer.parseInt(axis.majorTicks.number.get());

		boolean isHorizontal = isHorizontal();
		boolean isMirrored = autoMirror.get();

		//primary axis
		axisSelection.selectAll(".primary").remove();

		Selection primary = axisSelection //
				.append("g") //
				.attr("id", "primary")
				.attr("class", "primary");
		plotPrimaryAxis(d3, primary, graphHeightInPx, isHorizontal, numberOfTicksAimedFor);

		//secondary axis
		axisSelection.selectAll(".secondary").remove();
		if (isMirrored) {

			Selection secondary = axisSelection //
					.append("g") //
					.attr("id", "secondary")
					.attr("class", "secondary");
			plotSecondaryAxis(d3, secondary, graphWidthInPx, isHorizontal, numberOfTicksAimedFor);
		}

		return axisSelection;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private Selection plotPrimaryAxis(
			D3 d3,
			Selection axisSelection,
			Double graphHeightInPx,
			boolean isHorizontal,
			int numberOfTicksAimedFor) {

		//set translation and tick padding
		double tickPadding;
		if (isHorizontal) {
			axisSelection.attr("transform", "translate(0," + graphHeightInPx + ")");
			tickPadding = -6.0;
		} else {
			tickPadding = -12.0;

		}

		//create d3 axis
		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(scale)
				.outerTickSize(0.0)
				.tickPadding(tickPadding)
				.ticks(numberOfTicksAimedFor); //for log axis only the tick labels will be influenced

		setAxisDirection(axis, isHorizontal);

		axis.apply(axisSelection);

		return axisSelection;
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
			Selection axisSelection,
			Double graphWidthInPx,
			boolean isHorizontal,
			int numberOfTicksAimedFor) {

		if (!isHorizontal) {
			axisSelection.attr("transform", "translate(" + graphWidthInPx + ",0)");
		}

		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(scale) //
				.outerTickSize(0.0) //
				.ticks(numberOfTicksAimedFor) //for log axis only the tick labels will be influenced
				.tickFormatExpression("function (d) { return ''; }"); //hides the tick labels

		setAxisDirection(axis, isHorizontal);

		axis.apply(axisSelection);
		return axisSelection;
	}

	//#end region

	//#region ACCESSORS

	/**
	 * @return
	 */
	public boolean hasQuantitativeScale() {
		if (scale != null) {
			return true;
		} else {
			throw new IllegalStateException("The scale has not yet been defined.");
		}
	}

	/**
	 * @return
	 */
	public Scale<?> getScale() {
		if (scale != null) {
			return scale;
		} else {
			throw new IllegalStateException("The scale has not yet been defined.");
		}
	}

	//#end region

}
