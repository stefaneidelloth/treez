package org.treez.results;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. This page is used to modify
 * preferences only. They are stored in the preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(PreferencePage.class);

	/**
	 * the tool kit
	 */
	private FormToolkit toolkit;

	/**
	 * Constructor
	 */
	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getInstance().getPreferenceStore());
		// setDescription("example");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself. List of available field editors:
	 * http://help.eclipse.org/kepler/index.jsp? topic=%2Forg.eclipse.platform.doc
	 * .isv%2Fguide%2Fpreferences_prefs_contribute.htm
	 */
	@Override
	public void createFieldEditors() {

		Composite parent = getFieldEditorParent();
		Display display = parent.getDisplay();
		toolkit = new FormToolkit(display);

		toolkit.createLabel(parent, "Preference Page for Treez");

	}

	@Override
	public void init(IWorkbench workbench) {
		// not used here
	}

}
