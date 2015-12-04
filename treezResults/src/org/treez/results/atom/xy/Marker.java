package org.treez.results.atom.xy;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.results.atom.veuszpage.VeuszPageModel;

/**
 * XY marker settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Marker implements VeuszPageModel {

	//#region ATTRIBUTES

	/**
	 *
	 */
	public final Attribute<String> markerStyle = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> size = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> thinMarkers = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> errorStyle = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> borderColor = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> borderWidth = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> markerLineStyle = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> transparency = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> hideBorder = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> fillColor = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> fillStyle = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> fillTransparency = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> hideFill = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> colorMap = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> invertMap = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page markerPage = root.createPage("marker", "   Marker   ");

		// #region marker section

		Section marker = markerPage.createSection("marker");

		marker.createSymbolStyle(markerStyle, "marker");

		marker.createSize(size, "size", "0.5pt");

		marker.createTextField(thinMarkers, "thinMarkers", "Thin markers", "1");

		marker.createCheckBox(hide, "hide");

		marker.createErrorBarStyle(errorStyle, "errorStyle", "Error style");

		// #end region

		// #region marker border section

		Section markerBorder = markerPage.createSection("markerBorder", "Marker border", false);

		markerBorder.createColorChooser(borderColor, "color", "black");

		markerBorder.createSize(borderWidth, "width", "0.5pt");

		markerBorder.createLineStyle(markerLineStyle, "style");

		markerBorder.createTextField(transparency, "transparency", "0");

		markerBorder.createCheckBox(hideBorder, "hide");

		// #end region

		// #region marker fill section

		Section markerFill = markerPage.createSection("markerFill", "Marker fill", false);

		markerFill.createColorChooser(fillColor, "color", "black");

		markerFill.createFillStyle(fillStyle, "style");

		markerFill.createTextField(fillTransparency, "transparency", "0");

		markerFill.createCheckBox(hideFill, "hide");

		markerFill.createColorMap(colorMap, "colorMap", "Color map");

		markerFill.createCheckBox(invertMap, "invertMap", "Invert map");

		// #end region
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "";

		return veuszString;
	}

	//#end region

}
