package org.treez.data.column;

import org.eclipse.nebula.widgets.grid.internal.DefaultColumnHeaderRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

/**
 * Renderer for the extra empty column
 */
public class EmptyHeaderRenderer extends DefaultColumnHeaderRenderer {

	//#region CONSTRUCTORS

	public EmptyHeaderRenderer() {
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
		gc.fillGradientRectangle(x, y, width, height, true);

		gc.setBackground(oldBackground);
		gc.setForeground(oldForeground);
	}

	//#end region

}
