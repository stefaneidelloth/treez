package org.treez.core.atom.attribute.base.parent;

import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.Refreshable;

/**
 * Abstract base class for all AttributeAtom Containers and Attribute Atoms.
 */
public abstract class AbstractAttributeContainerAtom extends AbstractAttributeParentAtom {

	//#region ATTRIBUTES

	/**
	 * The absolute help id (has to be assigned by the atom control)
	 */
	private String absoluteHelpId = "org.treez.core.emptySectionHelp";

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public AbstractAttributeContainerAtom(String name) {
		super(name);
	}

	/**
	 * Copy constructor
	 *
	 * @param attributeContainerAtomToCopy
	 */
	public AbstractAttributeContainerAtom(AbstractAttributeContainerAtom attributeContainerAtomToCopy) {
		super(attributeContainerAtomToCopy);
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the control for the AttributeContainerAtom. A control for the parameters of the AttributeContainerAtom
	 * can be created with the method ControlAdaption getControlAdaption(Composite parent) which is inherited from
	 * AbstractAtom
	 *
	 * @param parent
	 */
	public abstract void createAtomControl(Composite parent, Refreshable treeViewerRefreshable);

	//#end region

	//#region ACCESSORS

	/**
	 * Sets the absolute help id
	 *
	 * @param absoluteHelpId
	 */
	public void setAbsoluteHelpId(String absoluteHelpId) {
		this.absoluteHelpId = absoluteHelpId;

	}

	/**
	 * Returns the absolute help id
	 *
	 * @return
	 */
	public String getAbsoluteHelpId() {
		return absoluteHelpId;
	}

	//#end region
}
