package org.treez.results.atom.graphicspage;

import java.util.List;

import org.treez.core.adaptable.CodeContainer;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.base.parent.AttributeParentCodeAdaption;
import org.treez.core.atom.base.AbstractAtom;

/**
 * Code adaption for GraphicsPropertiesPage
 */
public class GraphicsPropertiesPageCodeAdaption extends AdjustableAtomCodeAdaption {

	//#region CONSTRUCTORS

	public GraphicsPropertiesPageCodeAdaption(GraphicsPropertiesPage atom) {
		super(atom);
	}

	//#end region

	//#region METHODS

	/**
	 * Builds the code for setting attribute values of the atom. Might be overridden by inheriting classes.
	 */
	@Override
	protected CodeContainer buildCodeContainerForAttributes() {

		AdjustableAtom adjustableAtom = initializeModelIfRequired();

		AbstractAtom<?> model = adjustableAtom.getModel();
		if (model != null) {
			CodeContainer codeContainer = createCodeForAttributesFromModel(adjustableAtom);
			//codeContainer = addCodeForPropertyPages(codeContainer);
			return codeContainer;
		} else {
			throw new IllegalStateException(
					"Could not create attribute code because the underlying model could not be initialized.");
		}
	}

	/**
	 * Builds the code for setting attribute values using the underlying model.
	 */
	@Override
	protected CodeContainer createCodeForAttributesFromModel(AdjustableAtom parentAtom) {

		List<TreeNodeAdaption> pageNodes = getPageNodes(parentAtom);

		CodeContainer attributeContainer = new CodeContainer(scriptType);
		for (TreeNodeAdaption pageNode : pageNodes) {

			assertPageNodeIsPage(pageNode);
			Page page = (Page) pageNode.getAdaptable();

			//extend code with attribute code for page
			AttributeParentCodeAdaption codeAdaption = page.createCodeAdaption(scriptType);
			AbstractAtom<?> intermediateAtom = page;
			attributeContainer = codeAdaption.extendAttributeCodeContainerForModelParent(parentAtom, intermediateAtom,
					attributeContainer);
		}

		return attributeContainer;
	}

	//#end region

}
