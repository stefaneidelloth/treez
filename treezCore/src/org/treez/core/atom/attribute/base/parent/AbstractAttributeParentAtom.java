package org.treez.core.atom.attribute.base.parent;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.treez.core.Activator;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * Abstract base class for all AttributeAtom Containers and Attribute Atoms.
 */
public abstract class AbstractAttributeParentAtom<A extends AbstractAttributeParentAtom<A>>
		extends
		AbstractUiSynchronizingAtom<A> {

	//#region CONSTRUCTORS

	public AbstractAttributeParentAtom(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 */
	public AbstractAttributeParentAtom(AbstractAttributeParentAtom<A> attributeParentAtomToCopy) {
		super(attributeParentAtomToCopy);
	}

	//#end region

	//#region METHODS

	@Override
	public abstract AbstractAttributeParentAtom<A> copy();

	@Override
	public Image provideImage() {
		return Activator.getImage(IMAGE_KEY);
	}

	@Override
	public AttributeParentCodeAdaption createCodeAdaption(ScriptType scriptType) {

		AttributeParentCodeAdaption codeAdaption;
		switch (scriptType) {
		case JAVA:
			codeAdaption = new AttributeParentCodeAdaption(this);
			break;
		default:
			String message = "The ScriptType " + scriptType + " is not yet implemented.";
			throw new IllegalStateException(message);
		}

		return codeAdaption;
	}

	public abstract boolean isEnabled();

	/**
	 * Enables or disables the property atom. This can be used for example by the class ComboBoxEnableTarget if the
	 * inheriting class provides a meaningful implementation
	 *
	 * @param enable
	 */
	public abstract A setEnabled(boolean enable);

	public abstract boolean isVisible();

	public abstract A setVisible(boolean enable);

	/**
	 * Tries to find the the child AttributeAtom for the given model path and returns it. If the given model path is
	 * wrong an IllegalArgumentException is thrown.
	 *
	 * @param <K>
	 * @param modelPath
	 * @return
	 * @throws IllegalArgumentException
	 */
	public <K extends AbstractAttributeAtom<?, ?>> K getAttributeAtom(String modelPath)
			throws IllegalArgumentException {

		AbstractAtom<?> root = getRoot();

		if (root != null) {
			try {
				AbstractAtom<?> child = root.getChild(modelPath);
				@SuppressWarnings("unchecked")
				K propertyAtom = (K) child;
				return propertyAtom;
			} catch (IllegalArgumentException | ClassCastException exception) {
				throw new IllegalArgumentException("Could not get attribute atom '" + modelPath + "'.", exception);
			}
		} else {
			throw new IllegalArgumentException("Could not get attribute atom '" + modelPath + "' due to missing root.");
		}
	}

	/**
	 * Creates the context menu actions
	 *
	 * @return
	 */
	@Override
	protected List<Object> createContextMenuActions(final TreeViewerRefreshable treeViewerRefreshable) {
		ArrayList<Object> actions = new ArrayList<>();

		List<Object> superActions = super.createContextMenuActions(treeViewerRefreshable);
		actions.addAll(superActions);

		return actions;
	}

	//#end region

}
