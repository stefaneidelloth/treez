package org.treez.results.atom.graph;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.results.atom.veuszpage.VeuszPageModel;

/**
 * The main settings for a graph
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Main implements VeuszPageModel {

	//#region ATTRIBUTES

	//#region MARGINS

	/**
	 * Left margin
	 */
	public final Attribute<String> leftMargin = new Wrap<>();

	/**
	 * Right margin
	 */
	public final Attribute<String> rightMargin = new Wrap<>();

	/**
	 * Top margin
	 */
	public final Attribute<String> topMargin = new Wrap<>();

	/**
	 * Bottom margin
	 */
	public final Attribute<String> bottomMargin = new Wrap<>();

	//#end region

	/**
	 * Aspect ratio
	 */
	public final Attribute<String> aspectRatio = new Wrap<>();

	/**
	 * Hide
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page mainPage = root.createPage("main", "   Main   ");

		Section main = mainPage.createSection("main");

		main.createTextField(leftMargin, "leftMargin", "Left margin", "1 cm");

		main.createTextField(rightMargin, "rightMargin", "Right margin", "1 cm");

		main.createTextField(topMargin, "topMargin", "Top margin", "1 cm");

		main.createTextField(bottomMargin, "bottomMargin", "Bottom margin", "1 cm");

		main.createTextField(aspectRatio, "aspectRatio", "Aspect ratio", "Auto");

		main.createCheckBox(hide, "hide");
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {

		String veuszString = "";

		if (hide.get()) {
			veuszString = veuszString + "Set('hide', True)";
		}
		veuszString = veuszString + "Set('leftMargin', u'" + leftMargin + "')\n";
		veuszString = veuszString + "Set('rightMargin', u'" + rightMargin + "')\n";
		veuszString = veuszString + "Set('topMargin', u'" + topMargin + "')\n";
		veuszString = veuszString + "Set('bottomMargin', u'" + bottomMargin + "')\n";
		veuszString = veuszString + "Set('aspect', u'" + aspectRatio + "')\n";

		return veuszString;
	}

	//#end region

}
