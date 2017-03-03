package org.treez.results.atom.graphicsPage;

import java.util.ArrayList;
import java.util.List;

import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.scripting.ScriptType;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;

/**
 * Represents a single properties page that is shown as a tab in the properties of a graphics atom
 */
public abstract class GraphicsPropertiesPage extends AbstractGraphicsAtom {

	//#region ATTRIBUTES

	/**
	 * A list of property page factories that represent all properties of the graphics atom
	 */
	protected List<GraphicsPropertiesPageFactory> propertyPageFactories;

	//#end region

	//#region CONSTRUCTORS

	public GraphicsPropertiesPage(String name) {
		super(name);
		setRunnable();
		createPropertiesModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the treez model with the property page factories
	 */
	private void createPropertiesModel() {

		AttributeRoot root = new AttributeRoot("root");

		//fill list with page models
		propertyPageFactories = new ArrayList<GraphicsPropertiesPageFactory>();
		createPropertyPageFactories();

		//create pages
		for (GraphicsPropertiesPageFactory pageFactory : propertyPageFactories) {
			pageFactory.createPage(root, this);
		}

		//set model
		setModel(root);

	}

	/**
	 * fills the list of property page factories
	 */
	protected abstract void createPropertyPageFactories();

	/**
	 * Performs the plot with javafx-d3 / applies the settings of the page factories to the given parent selection. The
	 * given Refreshable can be used to set the focus to this atom
	 *
	 * @return
	 */
	public abstract
			Selection
			plotWithD3(D3 d3, Selection parentSelection, Selection contentSelection, FocusChangingRefreshable refreshable);

	/**
	 * Updates the plot. If there are changes that can not be applied with simple bindings to the d3 selections, a
	 * re-plot is required and that is done with this method.
	 *
	 * @param d3
	 */
	public abstract void updatePlotWithD3(D3 d3);

	/**
	 * Returns the code adaption
	 */
	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new GraphicsPropertiesPageCodeAdaption(this);
	}

	//#end region

	//#region ACCESSORS

	public List<GraphicsPropertiesPageFactory> getPropertyPageFactories() {
		return propertyPageFactories;
	}

	//#end region

}
