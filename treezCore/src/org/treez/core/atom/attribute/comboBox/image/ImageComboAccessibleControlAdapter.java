package org.treez.core.atom.attribute.comboBox.image;

import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * AccessibleControlAdapter for the ImageCombo
 */
public class ImageComboAccessibleControlAdapter
		extends
			AccessibleControlAdapter {

	//#region ATTRIBUTES

	private ImageCombo parent;

	//#end region

	//#region CONSTRUCTORS

	ImageComboAccessibleControlAdapter(ImageCombo parent) {
		super();
		this.parent = parent;
	}

	//#end region

	//#region METHODS

	@Override
	public void getChildAtPoint(AccessibleControlEvent e) {
		Point testPoint = parent.toControl(e.x, e.y);
		if (parent.getBounds().contains(testPoint)) {
			e.childID = ACC.CHILDID_SELF;
		}
	}

	@Override
	public void getLocation(AccessibleControlEvent e) {
		Rectangle location = parent.getBounds();
		Point pt = parent.toDisplay(location.x, location.y);
		e.x = pt.x;
		e.y = pt.y;
		e.width = location.width;
		e.height = location.height;
	}

	@Override
	public void getChildCount(AccessibleControlEvent e) {
		e.detail = 0;
	}

	@Override
	public void getRole(AccessibleControlEvent e) {
		e.detail = ACC.ROLE_COMBOBOX;
	}

	@Override
	public void getState(AccessibleControlEvent e) {
		e.detail = ACC.STATE_NORMAL;
	}

	@Override
	public void getValue(AccessibleControlEvent e) {
		e.result = parent.getText();
	}

	//#end region

}
