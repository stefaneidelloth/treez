package org.treez.data.row;

import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * Label provider for table entries
 */
public class VariableDefinitionLabelProvider extends StyledCellLabelProvider {

	//#region ATTRIBUTES

	private String header;

	//#end region

	//#region CONSTRUCTORS

	public VariableDefinitionLabelProvider(String header) {
		super();
		this.header = header;
	}

	//#end region

	//#region METHODS

	@Override
	public void update(ViewerCell cell) {

		//get row
		VariableDefinitionRow row = (VariableDefinitionRow) cell.getElement();

		//set label
		String value = row.getEntryAsString(header);
		cell.setText(value);

		//set backgrounds
		final Color errorColor = new Color(Display.getCurrent(), 250, 200, 128);
		final Color notEditableColor = new Color(Display.getCurrent(), 251, 251, 251);
		final Color backgroundColor = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		final Color normalColor = new Color(Display.getCurrent(), 255, 255, 255);

		boolean isEditable = row.getVariableDefinition().getTable().isEditable(header);
		boolean errorExists = errorExists(row);

		if (isEditable) {
			if (errorExists) {
				FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
				Image errorIcon = registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
				cell.setImage(errorIcon);
				cell.setBackground(errorColor);
			} else {
				cell.setImage(null);
				cell.setBackground(normalColor);
			}
		} else {
			cell.setBackground(notEditableColor);
		}

		if (header.equals("#")) {
			cell.setBackground(backgroundColor);
		}
	}

	@Override
	public String getToolTipText(Object object) {

		//get row
		VariableDefinitionRow row = (VariableDefinitionRow) object;

		//return tool tip text
		return row.getToolTip(header);
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public Point getToolTipShift(Object object) {
		return new Point(5, 5);
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public int getToolTipDisplayDelayTime(Object object) {

		boolean showTooltip = errorExists(object);
		if (showTooltip) {
			return 100; //msec
		} else {
			return 999999999; // msec
		}
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public int getToolTipTimeDisplayed(Object object) {

		//get row
		VariableDefinitionRow row = (VariableDefinitionRow) object;

		//get tool tip
		String toolTip = row.getToolTip(header);

		//get definition header
		String definitionHeader = row.getVariableDefinition().getTable().getHeaders().get(1);

		//set display time for tool tip
		boolean showTooltip = header.equals(definitionHeader) && !toolTip.equals("");

		if (showTooltip) {
			return 5000; //msec
		} else {
			return 0; // msec
		}
	}

	/**
	 * Checks if the evaluation of the variable definition caused an error
	 *
	 * @param element
	 * @return
	 */
	private boolean errorExists(Object element) {
		//get row
		VariableDefinitionRow row = (VariableDefinitionRow) element;

		//get tool tip
		String toolTip = row.getToolTip(header);

		return !toolTip.equals("") && (header.equals("Name") || header.equals("Definition"));
	}

	//#end region
}
