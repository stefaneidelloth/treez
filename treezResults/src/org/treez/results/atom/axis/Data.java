package org.treez.results.atom.axis;

import java.util.Objects;

import org.treez.core.atom.attribute.AttributeRoot;
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
import org.treez.results.atom.veuszpage.GraphicsPageModel;

/**
 * Represents Veusz data
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Data implements GraphicsPageModel {

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

	private QuantitativeScale<?> scale;

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page dataPage = root.createPage("data");
		dataPage.setTitle("   Data   ");

		Section data = dataPage.createSection("data");

		data.createTextField(label, "label");

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

	}

	/**
	 * @param d3
	 * @param rectSelection
	 */
	public void initializeScaleWithD3(D3 d3, Selection rectSelection) {

		String graphWidthString = rectSelection.attr("width");
		Double graphWidthInPx = Length.toPx(graphWidthString);

		String graphHeightString = rectSelection.attr("height");
		Double graphHeightInPx = Length.toPx(graphHeightString);

		boolean isHorizontal = direction.get().equals("" + Direction.HORIZONTAL);

		boolean isLog = log.get();

		String minString = min.get();
		boolean minIsAuto = minString.equals("Auto");

		String maxString = max.get();
		boolean maxIsAuto = maxString.equals("Auto");

		Scales scales = d3 //
				.scale();

		//lin/log
		if (isLog) {
			scale = scales.log();
		} else {
			scale = scales.linear();
		}

		//range & translate
		if (isHorizontal) {
			scale.range(0.0, graphWidthInPx);
		} else {
			scale.range(graphHeightInPx, 0.0);
		}

		//domain
		boolean autoIsUsed = minIsAuto || maxIsAuto;
		if (!autoIsUsed) {
			double minValue = Double.parseDouble(minString);
			double maxValue = Double.parseDouble(maxString);
			scale.domain(minValue, maxValue);
		} else {
			throw new IllegalStateException("Auto scale is not yet implemented");
		}

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, GraphicsAtom parent) {
		Objects.requireNonNull(scale, "Scale has to be initialized bevore calling this method.");

		String graphHeightString = rectSelection.attr("height");
		Double graphHeightInPx = Length.toPx(graphHeightString);

		boolean isHorizontal = direction.get().equals("" + Direction.HORIZONTAL);

		//range & translate
		Selection newAxisSelection;
		if (isHorizontal) {
			newAxisSelection = axisSelection //
					.attr("transform", "translate(0," + graphHeightInPx + ")");
		} else {
			newAxisSelection = axisSelection;
		}

		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(scale)
				.tickPadding(8.0);

		//		.tickSize(-10.0, 2);

		//direction
		org.treez.javafxd3.d3.svg.Axis orientedAxis;
		if (isHorizontal) {
			orientedAxis = axis.orient(org.treez.javafxd3.d3.svg.Axis.Orientation.BOTTOM);
		} else {
			orientedAxis = axis.orient(org.treez.javafxd3.d3.svg.Axis.Orientation.LEFT);
		}

		orientedAxis.apply(newAxisSelection);

		newAxisSelection //
				.selectAll("path, line") //
				.style("fill", "none") //
				.style("stroke", "#000")
				.style("stroke-width", "3px") //
				.style("shape-rendering", "geometricPrecision");

		return newAxisSelection;
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "\n";
		veuszString = veuszString + "Set('label', u'" + label + "')\n";

		if (min.get().equals("Auto")) {
			veuszString = veuszString + "Set('min', u'" + min + "')\n";
		} else {
			veuszString = veuszString + "Set('min', " + min + ")\n";
		}

		if (max.get().equals("Auto")) {
			veuszString = veuszString + "Set('max', u'" + max + "')\n";
		} else {
			veuszString = veuszString + "Set('max', " + max + ")\n";
		}

		if (log.get()) {
			veuszString = veuszString + "Set('log', True)\n";
		}
		//veuszString = veuszString + "Set('mode', u'" + mode + "')\n";
		//veuszString = veuszString + "Set('datascale', " + datascale + ")\n";
		veuszString = veuszString + "Set('direction', u'" + direction + "')\n";
		//veuszString = veuszString + "Set('lowerPosition', " + lowerPosition + ")\n";
		//veuszString = veuszString + "Set('upperPosition', " + upperPosition + ")\n";
		//veuszString = veuszString + "Set('otherPosition', " + otherPosition + ")\n";
		//veuszString = veuszString + "Set('match', u'" + match + "')\n";

		return veuszString;
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
