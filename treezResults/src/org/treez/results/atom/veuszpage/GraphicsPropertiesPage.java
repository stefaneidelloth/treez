package org.treez.results.atom.veuszpage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.scripting.ScriptType;

/**
 * Represents a veusz properties page
 */
public abstract class GraphicsPropertiesPage extends GraphicsAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(GraphicsPropertiesPage.class);

	//#region ATTRIBUTES

	protected List<GraphicsPageModel> pageModels;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public GraphicsPropertiesPage(String name) {
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
		pageModels = new ArrayList<GraphicsPageModel>();
		fillVeuszPageModels();

		//create pages
		for (GraphicsPageModel pageFactory : pageModels) {
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
		for (GraphicsPageModel pageFactory : pageModels) {
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
		return new GraphicsPropertiesPageCodeAdaption(this);
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
