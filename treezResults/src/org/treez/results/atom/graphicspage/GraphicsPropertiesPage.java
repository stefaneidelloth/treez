package org.treez.results.atom.graphicspage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.scripting.ScriptType;

/**
 * Represents a single properties page that is shown as a tab in the properties of a graphics atom
 */
public abstract class GraphicsPropertiesPage extends GraphicsAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(GraphicsPropertiesPage.class);

	//#region ATTRIBUTES

	/**
	 * A list of page models that represent all properties of the graphics atom
	 */
	protected List<GraphicsPropertiesPageModel> pageModels;

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
	 * Creates the main model from the page models
	 */
	private void createPropertiesModel() {

		AttributeRoot root = new AttributeRoot("root");

		//fill list with page models
		pageModels = new ArrayList<GraphicsPropertiesPageModel>();
		fillPageModelList();

		//create pages
		for (GraphicsPropertiesPageModel pageFactory : pageModels) {
			pageFactory.createPage(root, this);
		}

		//set model
		setModel(root);

	}

	/**
	 * fills the list of page models
	 */
	protected abstract void fillPageModelList();

	/**
	 * Returns the code adaption
	 */
	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new GraphicsPropertiesPageCodeAdaption(this);
	}

	//#end region

}
