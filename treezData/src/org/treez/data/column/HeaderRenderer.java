package org.treez.data.column;

import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.internal.DefaultColumnHeaderRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * Renders the table headers
 */
public class HeaderRenderer extends DefaultColumnHeaderRenderer {

	/**
	 * Constructor
	 */
	public HeaderRenderer() {
		//
	}

	@Override
	public boolean notify(int event, Point point, Object value) {
		return true;
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public void paint(GC gc, Object value) {

		Color backgroundColor = getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		Color foregroundColor = new Color(getDisplay(), 255, 255, 255);
		Color selectionColor = new Color(getDisplay(), 153, 204, 255);

		//get column
		GridColumn column = (GridColumn) value;

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
			gc.setBackground(selectionColor);
			gc.setForeground(foregroundColor);
			gc.fillGradientRectangle(x, y, width, height, true);

			gc.setForeground(selectionColor);
			gc.drawRectangle(x, y, width - 1, height - 1);

		} else {
			gc.setBackground(backgroundColor);
			gc.setForeground(foregroundColor);
			//gc.fillRectangle(x, y, width, height);
			gc.fillGradientRectangle(x, y, width, height, true);

			//gc.setForeground(oldForeground);
			//gc.drawRectangle(x,y,width,height);
		}

		gc.setBackground(oldBackground);
		gc.setForeground(oldForeground);

		//draw text
		String text = column.getText();
		int textOffset = 3;
		gc.drawString(text, x + textOffset, y + textOffset, true);

	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public Point computeSize(GC gc, int wHint, int hHint, Object value) {

		//define text offset
		final int textOffset = 3;

		//get text size
		GridColumn column = (GridColumn) value;
		String text = column.getText();
		Point p = gc.stringExtent(text);
		final int width = p.x + 3 * textOffset;
		int height = p.y;
		return new Point(width, height);
	}

}
