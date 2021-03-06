package org.treez.core.atom.graphics;

import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;

/**
 * Serves as a factory for a properties page and "plots" the corresponding d3 representation
 */
public interface GraphicsPropertiesPageFactory {

	/**
	 * Creates the properties page as child of the given root
	 *
	 * @param root
	 * @param parent
	 */
	void createPage(AttributeRoot root, AbstractAtom<?> parent);

	/**
	 * Performs the plot with javafx-d3 / applies the setting of the page model to the given selections.
	 *
	 * @param selection
	 * @return
	 */
	Selection plotWithD3(D3 d3, Selection parentSelection, Selection contentSelection, AbstractGraphicsAtom parent);

}
