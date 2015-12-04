package org.treez.results.atom.axis;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.results.atom.veuszpage.VeuszPageModel;

/**
 * Represents the general settings of an axis
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class General implements VeuszPageModel {

	//#region ATTRIBUTES

	/**
	 *
	 */
	private Attribute<Boolean> hide = new Wrap<>();

	/**
	 *
	 */
	private Attribute<String> autoRange = new Wrap<>();

	/**
	 *
	 */
	private Attribute<Boolean> autoMirror = new Wrap<>();

	/**
	 *
	 */
	private Attribute<Boolean> reflect = new Wrap<>();

	/**
	 *
	 */
	private Attribute<Boolean> outerTicks = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page generalPage = root.createPage("general", "   General   ");

		Section general = generalPage.createSection("general");

		general.createCheckBox(hide, "hide");

		general.createComboBox(autoRange, "autoRange", "next-tick,+2%,+5%,+10%,+15%", "next-tick");

		general.createCheckBox(autoMirror, "autoMirror", "Auto mirror", true);

		general.createCheckBox(reflect, "reflect");

		general.createCheckBox(outerTicks, "outerTicks", "Outer ticks");
	}

	@Override
	public String createVeuszText(AbstractAtom parent) {
		String veuszString = "\n";

		return veuszString;
	}

	//#end region

}
