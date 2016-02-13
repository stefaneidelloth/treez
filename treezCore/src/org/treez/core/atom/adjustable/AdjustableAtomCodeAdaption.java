package org.treez.core.atom.adjustable;

import java.util.List;

import org.apache.log4j.Logger;
import org.treez.core.adaptable.CodeContainer;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.base.parent.AttributeParentCodeAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.base.AtomCodeAdaption;

/**
 * CodeAdaption for atoms
 */
public class AdjustableAtomCodeAdaption extends AtomCodeAdaption {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(AdjustableAtomCodeAdaption.class);

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param atom
	 */
	public AdjustableAtomCodeAdaption(AbstractAtom atom) {
		super(atom);
	}

	//#end region

	//#region METHODS

	/**
	 * Builds the code for setting attribute values of the atom. Might be overridden by inheriting classes.
	 *
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	@Override
	protected CodeContainer buildCodeContainerForAttributes() {

		//Initialize the model if required
		AdjustableAtom adjustableAtom = (AdjustableAtom) atom;
		AbstractAtom model = adjustableAtom.getModel();
		boolean modelIsInitialized = model != null;
		if (!modelIsInitialized) {
			try {
				adjustableAtom.createAjustableAtomModel();
			} catch (Exception exception) {
				String message = "Could not create attribute code because the underlying model could not be initialized.";
				throw new IllegalStateException(message, exception);
			}
		}

		model = adjustableAtom.getModel();
		if (model != null) {
			CodeContainer codeContainer = createCodeForAttributesFromModel(adjustableAtom);
			return codeContainer;
		} else {
			throw new IllegalStateException(
					"Could not create attribute code because the underlying model could not be initialized.");
		}

	}

	/**
	 * Builds the code for setting attribute values using the underlying model.
	 *
	 * @param parentAtom
	 * @return
	 */
	private CodeContainer createCodeForAttributesFromModel(AdjustableAtom parentAtom) {

		CodeContainer attributeContainer = new CodeContainer(scriptType);

		AbstractAtom model = parentAtom.getModel();
		List<TreeNodeAdaption> pageNodes = model.createTreeNodeAdaption().getChildren();

		for (TreeNodeAdaption pageNode : pageNodes) {

			//test the type
			String type = pageNode.getAdaptable().getClass().getSimpleName();
			String pageType = Page.class.getSimpleName();
			boolean isPage = type.equals(pageType);
			if (!isPage) {
				String message = "The type of the first children of an AdjustableAtom has to be " + pageType
						+ " and not '" + type + "'.";
				throw new IllegalArgumentException(message);
			}

			//get page from pageNode
			Page page = (Page) pageNode.getAdaptable();

			//extend code with attribute code for page

			AttributeParentCodeAdaption codeAdaption = page.createCodeAdaption(scriptType);

			attributeContainer = codeAdaption
					.extendAttributeCodeContainerForModelParent(parentAtom, attributeContainer);
		}

		return attributeContainer;
	}

	//#end region

}
