package org.treez.results.atom.graph;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.results.atom.veuszpage.VeuszPageModel;

/**
 * The background settings for a graph
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Background implements VeuszPageModel {

	//#region ATTRIBUTES

	/**
	 * Background color
	 */
	public final Attribute<String> color = new Wrap<>();

	/**
	 * Background fill style
	 */
	public final Attribute<String> fillStyle = new Wrap<>();

	/**
	 * Background transparency
	 */
	public final Attribute<String> transparency = new Wrap<>();

	/**
	 * Hide
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page backgroundPage = root.createPage("background");

		Section background = backgroundPage.createSection("background");

		background.createColorChooser(color, "color");

		background.createFillStyle(fillStyle, "style");

		background.createTextField(transparency, "transparency", "0");

		background.createCheckBox(hide, "hide");
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {
		String veuszString = "\n";

		veuszString = veuszString + "Set('Background/color', u'" + color + "')\n";
		veuszString = veuszString + "Set('Background/style', u'" + fillStyle + "')\n";
		if (hide.get()) {
			veuszString = veuszString + "Set('Background/hide', True)";
		}
		veuszString = veuszString + "Set('Background/transparency', " + transparency + ")\n";

		return veuszString;
	}

	//#end region

}
