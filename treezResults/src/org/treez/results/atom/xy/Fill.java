package org.treez.results.atom.xy;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.results.atom.veuszpage.VeuszPageModel;

/**
 * XY fill settings
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Fill implements VeuszPageModel {

	//#region ATTRIBUTES

	/**
	 *
	 */
	public final Attribute<String> belowColor = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> belowFillStyle = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> belowHideEdgeFill = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> belowTransparency = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> belowHideErrorFill = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> aboveColor = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> aboveFillStyle = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> aboveHideEdgeFill = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<String> aboveTransparency = new Wrap<>();

	/**
	 *
	 */
	public final Attribute<Boolean> aboveHideErrorFill = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page fillPage = root.createPage("fill", "   Fill     ");

		// #region fill below section

		Section fillBelow = fillPage.createSection("fillBelow", "Fill below");

		fillBelow.createColorChooser(belowColor, "color", "black");

		fillBelow.createFillStyle(belowFillStyle, "style");

		fillBelow.createCheckBox(belowHideEdgeFill, "hideEdgeFill", "Hide edge fill");

		fillBelow.createTextField(belowTransparency, "transparency", "0");

		fillBelow.createCheckBox(belowHideErrorFill, "hideErrorFill", "Hide error fill");

		// #end region

		// #region fill above section

		Section fillAbove = fillPage.createSection("fillAbove", "Fill above", false);

		fillAbove.createColorChooser(aboveColor, "color", "black");

		fillAbove.createFillStyle(aboveFillStyle, "style");

		fillAbove.createCheckBox(aboveHideEdgeFill, "hideEdgeFill", "Hide edge fill");

		fillAbove.createTextField(aboveTransparency, "transparency", "0");

		fillAbove.createCheckBox(aboveHideErrorFill, "hideErrorFill", "Hide error fill");
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "";

		return veuszString;
	}

	//#end region

}
