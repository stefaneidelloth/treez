package org.treez.core.atom.attribute.base.parent;

import java.util.List;

import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.attribute.base.AttributeAtomCodeAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.base.AtomCodeAdaption;

/**
 * The CodeAdaption for AttributeParentAtom
 */
public class AttributeParentCodeAdaption extends AtomCodeAdaption {

	//#region CONSTRUCTORS

	public AttributeParentCodeAdaption(AbstractAttributeParentAtom<?> atom) {
		super(atom);
	}

	//#end region

	//#region METHODS

	/**
	 * Adds the attribute code for the AttributeParentAtom that corresponds to this code adaption to the given parent
	 * code container. The purpose is to set the attribute values of the parentAtom that uses an underlying model.
	 *
	 * @param intermediateAtom
	 * @param parentContainer
	 * @return
	 */
	public CodeContainer extendAttributeCodeContainerForModelParent(
			AbstractAtom<?> parentAtom,
			AbstractAtom<?> intermediateAtom,
			CodeContainer parentContainer) {

		CodeContainer extendedContainer = parentContainer;

		List<AbstractAtom<?>> children = atom.getChildAtoms();

		for (AbstractAtom<?> child : children) {

			boolean isAttributeAtom = child instanceof AbstractAttributeAtom;

			if (isAttributeAtom) {
				AbstractAttributeAtom<?, ?> attributeAtom = (AbstractAttributeAtom<?, ?>) child;
				AttributeAtomCodeAdaption<?> codeAdaption = attributeAtom.createCodeAdaption(scriptType);

				extendedContainer = codeAdaption.extendAttributeCodeContainerForModelParent(parentAtom,
						intermediateAtom, parentContainer);

			} else {

				boolean isAttributeParentAtom = child instanceof AbstractAttributeParentAtom;

				if (isAttributeParentAtom) {
					AbstractAttributeParentAtom<?> attributeParentAtom = (AbstractAttributeParentAtom<?>) child;
					AttributeParentCodeAdaption codeAdaption = attributeParentAtom.createCodeAdaption(scriptType);
					extendedContainer = codeAdaption.extendAttributeCodeContainerForModelParent(parentAtom,
							intermediateAtom, parentContainer);
				} else {
					String message = "The child atom " + child.getName()
							+ " has to inherit from AttributeAtom or AttributeParentAtom but it is "
							+ child.getClass().getSimpleName();
					throw new IllegalStateException(message);
				}
			}
		}

		return extendedContainer;

	}
	//#end region
}
