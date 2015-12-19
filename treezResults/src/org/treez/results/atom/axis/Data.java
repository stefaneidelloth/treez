package org.treez.results.atom.axis;

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
import org.treez.javafxd3.d3.scales.LinearScale;
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
	public final Attribute<String> mode = new Wrap<>();

	/**
	 * Data scale
	 */
	public final Attribute<String> datascale = new Wrap<>();

	/**
	 * Direction
	 */
	public final Attribute<String> direction = new Wrap<>();

	/**
	 * Lower position
	 */
	public final Attribute<String> lowerPosition = new Wrap<>();

	/**
	 * Upper position
	 */
	public final Attribute<String> upperPosition = new Wrap<>();

	/**
	 * Other position
	 */
	public final Attribute<String> otherPosition = new Wrap<>();

	/**
	 * Match
	 */
	public final Attribute<String> match = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page dataPage = root.createPage("data");
		dataPage.setTitle("   Data   ");

		Section data = dataPage.createSection("data");

		data.createTextField(label, "label");

		data.createTextField(min, "min", "Auto");

		data.createTextField(max, "max", "Auto");

		data.createCheckBox(log, "log");

		data.createComboBox(mode, "mode", "numeric, datetime, labels", "numeric");

		data.createTextField(datascale, "datascale", "Scale", "1");

		data.createComboBox(direction, "direction", "horizontal, vertical", "horizontal");

		data.createTextField(lowerPosition, "lowerPosition", "Min position", "0");

		data.createTextField(upperPosition, "upperPosition", "Max position", "1");

		data.createTextField(otherPosition, "otherPosition", "Axis position", "0");

		data.createTextField(match, "match");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, GraphicsAtom parent) {

		String graphWidthString = rectSelection.attr("width");
		Double graphWidthInPx = Length.toPx(graphWidthString);

		String graphHeightString = rectSelection.attr("height");
		Double graphHeightInPx = Length.toPx(graphHeightString);

		LinearScale scale = d3 //
				.scale() //
				.linear() //
				.domain(0.0, 1.0)
				.range(0.0, graphWidthInPx);

		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(scale)
				.tickPadding(8.0)
				.tickSize(-10.0, 2);

		Selection newAxisSelection = axisSelection //
				.attr("transform", "translate(0," + graphHeightInPx + ")");

		axis.apply(newAxisSelection);

		newAxisSelection //
				.selectAll("path, line") //
				.style("fill", "none") //
				.style("stroke", "#000")
				.style("stroke-width", "3px") //
				.style("shape-rendering", "geometricPrecision");

		return axisSelection;
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
		veuszString = veuszString + "Set('mode', u'" + mode + "')\n";
		veuszString = veuszString + "Set('datascale', " + datascale + ")\n";
		veuszString = veuszString + "Set('direction', u'" + direction + "')\n";
		veuszString = veuszString + "Set('lowerPosition', " + lowerPosition + ")\n";
		veuszString = veuszString + "Set('upperPosition', " + upperPosition + ")\n";
		veuszString = veuszString + "Set('otherPosition', " + otherPosition + ")\n";
		veuszString = veuszString + "Set('match', u'" + match + "')\n";

		return veuszString;
	}

	//#end region

}
