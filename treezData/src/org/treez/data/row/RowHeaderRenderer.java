package org.treez.data.row;

import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.grid.internal.DefaultRowHeaderRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

/**
 * Renders the table row headers
 */
@SuppressWarnings("restriction")
public class RowHeaderRenderer extends DefaultRowHeaderRenderer {

	//#region CONSTRUCTORS

	public RowHeaderRenderer() {
		super();
	}

	//#end region

	//#region METHODS

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public void paint(GC gc, Object value) {

		Color backgroundColor = getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		Color selectionColor = new Color(getDisplay(), 153, 204, 255);
		Color selectionColorLight = new Color(getDisplay(), 160, 212, 255);
		Color foregroundColor = new Color(getDisplay(), 0, 0, 0);

		//get text
		GridItem item = (GridItem) value;
		String text = "" + (item.getParent().indexOf(item) + 1);

		//get selection state
		boolean selected = this.isSelected();

		//get coordinates
		int x = getBounds().x;
		int y = getBounds().y;
		int width = getBounds().width;
		int height = getBounds().height;

		//set background value and draw rectangle
		Color oldBackground = gc.getBackground();
		Color oldForeground = gc.getForeground();

		if (selected) {
			gc.setBackground(selectionColorLight);
			gc.fillRectangle(x, y, width, height);

			gc.setForeground(selectionColor);
			gc.drawRectangle(x, y, width - 1, height - 1);

		} else {
			gc.setBackground(backgroundColor);
			gc.fillRectangle(x, y, width, height);
		}

		//draw text
		gc.setForeground(foregroundColor);
		int textOffset = 3;
		gc.drawString(text, x + textOffset, y + textOffset, true);

		gc.setBackground(oldBackground);
		gc.setForeground(oldForeground);
	}

	//#end region

}
