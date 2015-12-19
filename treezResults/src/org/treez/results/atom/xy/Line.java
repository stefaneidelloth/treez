package org.treez.results.atom.xy;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.atom.veuszpage.GraphicsPageModel;

/**
 * XY line settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Line implements GraphicsPageModel {

	//#region ATTRIBUTES

	/**
	 *
	 */
	public final Attribute<String> steps = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> bezierJoin = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> color = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> width = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> style = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> transparency = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page linePage = root.createPage("line", "   Line    ");

		Section line = linePage.createSection("line");

		line.createComboBox(steps, "steps", "off, left, centre, left-shift-points, right-shift-points, vcentre", "off");

		line.createCheckBox(bezierJoin, "bezierJoin", "Bezier join");

		line.createColorChooser(color, "color", "black");

		line.createSize(width, "width", "0.5pt");

		line.createLineStyle(style, "style");

		line.createTextField(transparency, "transparency", "0");

		line.createCheckBox(hide, "hide");
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection, GraphicsAtom parent) {

		//parent.bindStringAttribute(selection, "x", leftMargin);

		return graphSelection;
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "";

		return veuszString;
	}

	//#end region

}
