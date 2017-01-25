package org.treez.core.atom.adjustable;

import java.util.List;

import org.treez.core.adaptable.CodeContainer;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.base.parent.AttributeParentCodeAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.base.AtomCodeAdaption;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;

/**
 * CodeAdaption for atoms
 */
public class AdjustableAtomCodeAdaption extends AtomCodeAdaption {

	//#region CONSTRUCTORS

	public AdjustableAtomCodeAdaption(AbstractUiSynchronizingAtom<?> atom) {
		super(atom);
	}

	//#end region

	//#region METHODS

	/**
	 * Builds the code for setting attribute values of the atom. Might be overridden by inheriting classes.
	 *
	 * @return
	 */

	@Override
	protected CodeContainer buildCodeContainerForAttributes() {

		AdjustableAtom adjustableAtom = initializeModelIfRequired();

		AbstractAtom<?> model = adjustableAtom.getModel();
		if (model != null) {
			CodeContainer codeContainer = createCodeForAttributesFromModel(adjustableAtom);
			return codeContainer;
		} else {
			throw new IllegalStateException(
					"Could not create attribute code because the underlying model could not be initialized.");
		}

	}

	@SuppressWarnings("checkstyle:illegalcatch")
	protected AdjustableAtom initializeModelIfRequired() {
		AdjustableAtom adjustableAtom = (AdjustableAtom) atom;
		return adjustableAtom;
	}

	/**
	 * Builds the code for setting attribute values using the underlying model.
	 *
	 * @param parentAtom
	 * @return
	 */
	protected CodeContainer createCodeForAttributesFromModel(AdjustableAtom parentAtom) {

		List<TreeNodeAdaption> pageNodes = getPageNodes(parentAtom);

		CodeContainer attributeContainer = new CodeContainer(scriptType);
		for (TreeNodeAdaption pageNode : pageNodes) {

			assertPageNodeIsPage(pageNode);
			Page page = (Page) pageNode.getAdaptable();

			//extend code with attribute code for page
			AttributeParentCodeAdaption codeAdaption = page.createCodeAdaption(scriptType);
			AbstractAtom<?> intermediateAtom = null;
			attributeContainer = codeAdaption.extendAttributeCodeContainerForModelParent(parentAtom, intermediateAtom,
					attributeContainer);
		}

		return attributeContainer;
	}

	protected static List<TreeNodeAdaption> getPageNodes(AdjustableAtom parentAtom) {
		AbstractAtom<?> model = parentAtom.getModel();
		List<TreeNodeAdaption> pageNodes = model.createTreeNodeAdaption().getChildren();
		return pageNodes;
	}

	protected static void assertPageNodeIsPage(TreeNodeAdaption pageNode) {
		String type = pageNode.getAdaptable().getClass().getSimpleName();
		String pageType = Page.class.getSimpleName();
		boolean isPage = type.equals(pageType);
		if (!isPage) {
			String message = "The type of the first children of an AdjustableAtom has to be " + pageType + " and not '"
					+ type + "'.";
			throw new IllegalArgumentException(message);
		}
	}

	//#end region

}
