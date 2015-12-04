package org.treez.core.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite that can be identified by an id
 */
public class IdentifiableComposite extends Composite {

	//#region ATTRIBUTES

	private String id;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param parent
	 * @param id
	 */
	public IdentifiableComposite(Composite parent, String id) {
		super(parent, SWT.NONE);
		this.id = id;
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the id
	 *
	 * @return
	 */
	public String getId() {
		return id;
	}

	//#end region

}
