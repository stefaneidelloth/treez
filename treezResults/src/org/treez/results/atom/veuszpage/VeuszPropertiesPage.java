package org.treez.results.atom.veuszpage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.scripting.ScriptType;

/**
 * Represents a veusz properties page
 */
public abstract class VeuszPropertiesPage extends AdjustableAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(VeuszPropertiesPage.class);

	//#region ATTRIBUTES

	protected List<VeuszPageModel> veuszPageModels;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public VeuszPropertiesPage(String name) {
		super(name);
		setRunnable();
		createPropertiesModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the model that describes Veusz properties
	 */
	private void createPropertiesModel() {

		AttributeRoot root = new AttributeRoot("root");

		//fill list with page models
		veuszPageModels = new ArrayList<VeuszPageModel>();
		fillVeuszPageModels();

		//create pages
		for (VeuszPageModel pageFactory : veuszPageModels) {
			pageFactory.createPage(root, this);
		}

		//set model
		setModel(root);

	}

	/**
	 * fills veuszPageModels with the veusz page models
	 */
	protected abstract void fillVeuszPageModels();

	/**
	 * Provides veusz text to represent this atom
	 *
	 * @return
	 */
	public String getVeuszText() {

		String veuszString = createVeuszStartText();

		//add text for pages
		for (VeuszPageModel pageFactory : veuszPageModels) {
			veuszString = veuszString + pageFactory.createVeuszText(this);
		}

		//add text for children
		String endText = createVeuszEndText();
		veuszString = veuszString + endText;

		return veuszString;
	}

	/**
	 * Returns the code adaption
	 */
	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new VeuszPropertiesPageCodeAdaption(this);
	}

	/**
	 * Creates the string that represents the start of the veusz text (=part before the text for the individual page
	 * models is added)
	 *
	 * @return
	 */
	protected abstract String createVeuszStartText();

	/**
	 * Creates the string that represents the end of the veusz text (=part after the text for the individual page models
	 * is added)
	 *
	 * @return
	 */
	protected abstract String createVeuszEndText();

	//#end region

}
