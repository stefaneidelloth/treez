package org.treez.core.data.cell;

import java.io.File;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/**
 * A cell editor for cells that contain file paths. It can handle null values. The path is validated to be a valid path.
 * The validation might also depend on the label provider.
 */
public class TreezPathCellEditor extends TreezStringCellEditor {

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 */
	public TreezPathCellEditor(Composite parent) {
		super(parent);
	}

	//#end region

	//#region METHODS

	/**
	 * Validates the value to represents a valid file path. If it is not a valid file path, a red background color is
	 * shown to "highlight an error state".
	 *
	 * @param label
	 * @param cell
	 */
	@Override
	protected void doValidateValue() {
		String label = text.getText();
		Color backgroundColor = TEXT_BACKGROUND_COLOR;
		File file = new File(label);
		if (!file.exists()) {
			backgroundColor = TEXT_BACKGROUND_COLOR_ERROR;
		}
		text.setBackground(backgroundColor);
	}

	//#end region

}
