package org.treez.core.data.cell;

import java.io.File;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.treez.core.data.column.ColumnType;
import org.treez.core.data.row.Row;

/**
 * Label provider for table entries that is able to validate the table entry. Text validation might also depend on the
 * cell editor.
 */
public class TreezTableJFaceLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

	//#region ATTRIBUTES

	private static final Color COLOR_NO_ERROR = new Color(Display.getCurrent(), 255, 255, 255);

	private static final Color COLOR_ERROR = new Color(Display.getCurrent(), 250, 200, 128);

	private String valueHeader;

	private ColumnType valueColumnType;

	private String infoHeader;

	private boolean pathValidationIsEnabled = false;

	/**
	 * The last edited table item. (Needed to validated all table items if validation is enabled/disabled).
	 */
	private TableItem lastRenderedTableItem;

	//#end region

	//#region CONSTRUCTORS

	public TreezTableJFaceLabelProvider(String valueHeader, ColumnType valueColumnType, String infoHeader) {
		super();
		this.valueHeader = valueHeader;
		this.valueColumnType = valueColumnType;
		this.infoHeader = infoHeader;
	}

	//#end region

	//#region METHODS

	@Override
	public void update(ViewerCell cell) {

		//get element
		Row row = (Row) cell.getElement();

		int columnIndex = cell.getColumnIndex();
		if (columnIndex == 0) {
			//set label
			String label = row.getEntryAsString(valueHeader);
			cell.setText(label);
			if (valueColumnType == ColumnType.STRING) {
				TableItem tableItem = (TableItem) cell.getItem();
				validateTableItem(tableItem);
			}
		} else {
			String label = row.getEntryAsString(infoHeader);
			if (label.equals(row.getNullString())) {
				cell.setText("");
			} else {
				cell.setText(label);
			}

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
		String cellText = row.getEntryAsString(valueHeader);
		return cellText;
	}

	/**
	 * Validates the label to represents a valid file path. If it is not a valid file path, a red background color is
	 * shown to "highlight an error state".
	 *
	 * @param label
	 * @param cell
	 */
	private void validateTableItem(TableItem tableItem) {
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
				//partly override selection background with
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
			Table table = lastRenderedTableItem.getParent();
			TableItem[] allTableItems = table.getItems();

			if (allTableItems != null) {
				for (TableItem tableItem : allTableItems) {

					validateTableItem(tableItem);
				}
			}
		}

	}
	//#end region

}
