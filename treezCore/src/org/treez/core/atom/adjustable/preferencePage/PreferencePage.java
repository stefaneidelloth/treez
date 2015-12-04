package org.treez.core.atom.adjustable.preferencePage;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.treez.core.Activator;
import org.treez.core.atom.adjustable.preferencePage.treeEditor.TreeEditor;

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

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 */
	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getPreferenceStoreStatic());
	}

	//#end region

	//#region METHODS

	/**
	 * Creates field editors for the stored preferences. Field editors are abstractions of the common GUI blocks needed
	 * to manipulate various types of preferences. Each field editor knows how to save and restore itself. List of
	 * common field editors: http://help.eclipse.org/kepler/index.jsp?topic =%2Forg.eclipse.platform.doc
	 * .isv%2Fguide%2Fpreferences_prefs_contribute.htm Here a custom field editor is used that allows to edit a model
	 * tree.
	 */
	@Override
	public void createFieldEditors() {

		Composite parent = getFieldEditorParent();
		TreeEditor treeEditor = new TreeEditor(Parameters.TREE_EDITOR_STRING, "TableEditorLabel", parent);
		addField(treeEditor);
	}

	@Override
	public void init(IWorkbench workbench) {
		//not used here
	}

	//#end region

}
