package org.treez.core.atom.adjustable.preferencePage;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.treez.core.Activator;

/**
 * Class used to initialize default preference values.
 */
public class DefaultValues extends AbstractPreferenceInitializer {

	//#region METHODS

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getPreferenceStoreStatic();
		String defaultString = "package org.treez.core.atom.adjustable.preferencePage;\n"
				+ "import org.treez.core.atom.attribute.Root;\n" + "import org.treez.core.scripting.ModelProvider;\n"
				+ "public class DefaultPreferences extends ModelProvider {\n" + "    public Root createModel() {\n"
				+ "        Root root = new Root(\"root\");\n" + "    	return root;\n" + "    }\n" + "};\n";
		store.setDefault(Parameters.TREE_EDITOR_STRING, defaultString);
	}

	//#end region
}
