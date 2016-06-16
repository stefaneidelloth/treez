package org.treez.core.atom.attribute.event;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Event;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;

/**
 * Event that can be used to trigger ModifyListeners *
 */
public class AttributeAtomEvent extends Event {

	//#region CONSTRUCTORS

	public AttributeAtomEvent() {
		super();
		this.data = null;
		this.widget = new AttributeAtomEventWidget();
	}

	public AttributeAtomEvent(AbstractAttributeAtom<?, ?> attributeAtom) {
		super();
		this.data = attributeAtom.get();
		this.widget = new AttributeAtomEventWidget();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates a ModifyEvent based on this AttributeAtomEvent
	 */
	public ModifyEvent createModifyEvent() {
		ModifyEvent modifyEvent = new ModifyEvent(this);
		return modifyEvent;
	}

	//#end region

}
