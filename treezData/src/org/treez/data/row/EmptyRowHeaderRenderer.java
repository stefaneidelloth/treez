package org.treez.data.row;

import org.eclipse.nebula.widgets.grid.internal.DefaultRowHeaderRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

/**
 * Renders the table row headers
 */
@SuppressWarnings("restriction")
public class EmptyRowHeaderRenderer extends DefaultRowHeaderRenderer {

	//#region CONSTRUCTORS

	public EmptyRowHeaderRenderer() {
		super();
	}

	//#end region

	//#region METHODS

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public void paint(GC gc, Object value) {

		Color backgroundColor = getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		Color foregroundColor = new Color(getDisplay(), 255, 255, 255);

		//get coordinates
		int x = getBounds().x;
		int y = getBounds().y;
		int width = getBounds().width;
		int height = getBounds().height;

		//set background value and draw rectangle
		Color oldBackground = gc.getBackground();
		Color oldForeground = gc.getForeground();

		gc.setBackground(backgroundColor);
		gc.setForeground(foregroundColor);
		gc.fillRectangle(x, y, width, height);

		gc.setForeground(oldForeground);
		gc.drawRectangle(x, y, width, height);

		gc.setBackground(oldBackground);
		gc.setForeground(oldForeground);
	}

	//#end region

}
