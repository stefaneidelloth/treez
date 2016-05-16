package org.treez.results.atom.graphicspage;

import java.util.List;

import org.treez.core.adaptable.CodeContainer;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.adjustable.AdjustableAtomCodeAdaption;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.base.parent.AttributeParentCodeAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;

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
	 *
	 * @return
	 */
	@Override
	protected CodeContainer buildCodeContainerForAttributes() {

		AdjustableAtom adjustableAtom = initializeModelIfRequired();

		AbstractAtom model = adjustableAtom.getModel();
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
	 *
	 * @param parentAtom
	 * @return
	 */
	@Override
	protected CodeContainer createCodeForAttributesFromModel(AdjustableAtom parentAtom) {

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

			attributeContainer = codeAdaption.extendAttributeCodeContainerForModelParent(page, attributeContainer);
		}

		return attributeContainer;
	}

	private CodeContainer addCodeForPropertyPages(CodeContainer codeContainer) {

		GraphicsPropertiesPage propertiesPage = (GraphicsPropertiesPage) atom;
		String name = propertiesPage.getName();
		List<GraphicsPropertiesPageFactory> pageFactories = propertiesPage.getPropertyPageFactories();
		for (GraphicsPropertiesPageFactory pageFactory : pageFactories) {
			addCodeForPropertyPage(name, pageFactory, codeContainer);
		}

		return codeContainer;
	}

	private CodeContainer addCodeForPropertyPage(
			String atomName,
			GraphicsPropertiesPageFactory pageFactory,
			CodeContainer codeContainer) {

		String pageName = pageFactory.getClass().getSimpleName().toLowerCase();
		String prefix = "\t" + atomName + "." + pageName + ".";

		AdjustableAtom adjustableAtom = (AdjustableAtom) atom;
		AbstractAtom model = adjustableAtom.getModel();

		String command = prefix + "name.set('foo');";
		codeContainer.extendBulk(command);

		return codeContainer;
	}

	//#end region

}
