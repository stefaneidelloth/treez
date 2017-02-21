package org.treez.data.cell;

import java.io.File;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;

/**
 * Label provider for table entries that is able to validate the table entry. Text validation might also depend on the
 * cell editor.
 */
public class TreezTableNebulaLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

	//#region ATTRIBUTES

	private static final Color COLOR_NO_ERROR = new Color(Display.getCurrent(), 255, 255, 255);

	private static final Color COLOR_ERROR = new Color(Display.getCurrent(), 250, 200, 128);

	protected static final Color TEXT_COLOR_FOR_NULL_VALUES = new Color(Display.getCurrent(), 200, 200, 200);

	protected static final Color TEXT_COLOR = new Color(Display.getCurrent(), 0, 0, 0);

	private String header;

	private ColumnType columnType;

	private boolean pathValidationIsEnabled = false;

	/**
	 * The last edited table item. (Needed to validated all table items if validation is enabled/disabled).
	 */
	private GridItem lastRenderedTableItem;

	//#end region

	//#region CONSTRUCTORS

	public TreezTableNebulaLabelProvider(String header, ColumnType columnType) {
		super();
		this.header = header;
		this.columnType = columnType;
	}

	//#end region

	//#region METHODS

	@Override
	public void update(ViewerCell cell) {

		//get element
		Row row = (Row) cell.getElement();

		//set label
		String label = row.getEntryAsString(header);
		cell.setText(label);

		//set foreground color for null values
		boolean isNullString = row.isNullString(label);
		if (isNullString) {
			cell.setForeground(TEXT_COLOR_FOR_NULL_VALUES);
		} else {
			cell.setForeground(TEXT_COLOR);
		}

		//validate content for text columns (may also set background color)
		if (columnType == ColumnType.STRING) {
			GridItem tableItem = (GridItem) cell.getItem();
			validateTableItem(tableItem);
		}

	}

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		//get element
		Row row = (Row) element;

		//set label
		String cellText = row.getEntryAsString(header);
		return cellText;
	}

	/**
	 * Validates the label to represents a valid file path. If it is not a valid file path, a red background color is
	 * shown to "highlight an error state".
	 *
	 * @param label
	 * @param cell
	 */
	private void validateTableItem(GridItem tableItem) {
		Color backgroundColor = COLOR_NO_ERROR;

		Row row = (Row) tableItem.getData();

		if (pathValidationIsEnabled) {
			String label = tableItem.getText();
			File file = new File(label);
			if (!file.exists()) {
				backgroundColor = COLOR_ERROR;
				row.enableValidationError();
			} else {
				row.disableValidationError();
			}
		}
		tableItem.setBackground(backgroundColor);
		lastRenderedTableItem = tableItem;

	}

	@SuppressWarnings("checkstyle:magicnumber")
	@Override
	protected void paint(Event event, Object element) {

		if (pathValidationIsEnabled) {
			Row row = (Row) element;
			boolean hasValidationErrors = row.hasValidationErrors();
			if (hasValidationErrors) {
				//party override selection background with
				//error background

				int x = event.x + 1;
				int y = event.y + 2;

				Control control = (Control) event.widget;

				int height = event.height - 4;
				int width = control.getSize().x - 15;
				GC gc = event.gc;

				Color oldBackground = gc.getBackground();

				gc.setBackground(COLOR_ERROR);

				gc.fillRectangle(x, y, width, height);

				gc.setBackground(oldBackground);
			}
		}

		super.paint(event, element);
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Enables path validation
	 */
	public void enablePathValidation() {
		pathValidationIsEnabled = true;
		validateAllTableItems();
	}

	/**
	 * Disables path validation
	 */
	public void disablePathValidation() {
		pathValidationIsEnabled = false;
		validateAllTableItems();

	}

	/**
	 * Resets the state of all cells with errors
	 */
	private void validateAllTableItems() {
		if (lastRenderedTableItem != null) {
			Grid table = lastRenderedTableItem.getParent();
			GridItem[] allTableItems = table.getItems();

			if (allTableItems != null) {
				for (GridItem tableItem : allTableItems) {

					validateTableItem(tableItem);
				}
			}
		}

	}
	//#end region

}
