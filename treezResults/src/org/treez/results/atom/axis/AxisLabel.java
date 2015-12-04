package org.treez.results.atom.axis;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.results.atom.veuszpage.VeuszPageModel;

/**
 * Represents the label for an axis
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class AxisLabel implements VeuszPageModel {

	//#region ATTRIBUTES

	/**
	 * Label font
	 */
	public final Attribute<String> font = new Wrap<>();

	/**
	 * Label size
	 */
	public final Attribute<String> size = new Wrap<>();

	/**
	 * Label color
	 */
	public final Attribute<String> color = new Wrap<>();

	/**
	 * Label text italic style
	 */
	public final Attribute<Boolean> italic = new Wrap<>();

	/**
	 * Label text bold style
	 */
	public final Attribute<Boolean> bold = new Wrap<>();

	/**
	 * Label text underline
	 */
	public final Attribute<Boolean> underline = new Wrap<>();

	/**
	 * Label at edge
	 */
	public final Attribute<Boolean> atEdge = new Wrap<>();

	/**
	 * Label rotation
	 */
	public final Attribute<String> rotate = new Wrap<>();

	/**
	 * Label offset
	 */
	public final Attribute<String> labelOffset = new Wrap<>();

	/**
	 * Label position
	 */
	public final Attribute<String> position = new Wrap<>();

	/**
	 * Hide
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page axisLabelPage = root.createPage("axisLabel", "   Axis label   ");

		Section axisLabel = axisLabelPage.createSection("axisLabel", "Axis label");

		axisLabel.createFont(font, "font");

		axisLabel.createSize(size, "size", "14pt");

		axisLabel.createColorChooser(color, "color", "black");

		axisLabel.createCheckBox(italic, "italic");

		axisLabel.createCheckBox(bold, "bold");

		axisLabel.createCheckBox(underline, "underline");

		axisLabel.createCheckBox(hide, "hide");

		axisLabel.createCheckBox(atEdge, "atEdge", "At edge");

		axisLabel.createComboBox(rotate, "rotate", "-180,-135,-90,-45,0,45,90,135,180", "0");

		axisLabel.createSize(labelOffset, "labelOffset", "Label offset", "0pt");

		axisLabel.createComboBox(position, "position", "at-minimum,centre,at-maximum", "centre");

	}

	@Override
	public String createVeuszText(AbstractAtom parent) {
		return "";
	}

	//#end region

}
