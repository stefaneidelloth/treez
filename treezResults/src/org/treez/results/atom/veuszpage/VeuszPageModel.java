package org.treez.results.atom.veuszpage;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.base.AbstractAtom;

/**
 * Creates a veusz properties page, its corresponding veusz text and its corresponding code container
 */
public interface VeuszPageModel {

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

}
