package org.treez.results.atom.veuszpage;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;

/**
 * Creates a veusz properties page, its corresponding veusz text and its corresponding code container
 */
public interface GraphicsPageModel {

	/**
	 * Creates the veusz page as child of the given root
	 *
	 * @param root
	 * @param parent
	 */
	void createPage(AttributeRoot root, AbstractAtom parent);

	/**
	 * Creates the veusz text for the page
	 *
	 * @return
	 */
	String createVeuszText(AbstractAtom parent);

	/**
	 * Applies the setting of the page model to the given selection
	 *
	 * @param selection
	 * @return
	 */
	Selection plotWithD3(D3 d3, Selection parentSelection, Selection contentSelection, GraphicsAtom parent);

}
