package org.treez.core.scripting.javascript;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.scripting.AbstractScripting;

/**
 * Class for interpreting java script
 */
public class JavaScriptScripting extends AbstractScripting {

	private static final Logger LOG = LogManager.getLogger(JavaScriptScripting.class);

	//#region ATTRIBUTES

	private ScriptEngine engine;

	//#end region

	//#region CONSTRUCTORS

	public JavaScriptScripting() {

		ScriptEngineManager manager = new ScriptEngineManager();

		//Print all available scripting languages
		/*
		 * for (ScriptEngineFactory factory : manager.getEngineFactories()) {
		 * System.out.println("Available language: " +
		 * factory.getLanguageName()); }
		 */

		this.engine = manager.getEngineByName("JavaScript");

	}

	//#end region

	//#region METHODS

	/**
	 * Interprets /executes the text of the current document
	 */
	@Override
	public void executeDocument() {
		setSourceName();
		super.executeDocument();
	}

	/**
	 * Tries to get the root adaptable object from the script engine
	 *
	 * @return
	 */
	@Override
	@SuppressWarnings("checkstyle:illegalcatch")
	public AbstractAtom<?> getRoot() {
		try {
			Object object = engine.get("root");
			if (object instanceof AbstractAtom) {
				return (AbstractAtom<?>) object;
			}
		} catch (Exception e) {
			LOG.debug("Could not get root", e);
		}
		return null;
	}

	private void setSourceName() {
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		if (part instanceof ITextEditor) {

			//get current file name
			final ITextEditor editor = (ITextEditor) part;
			String filename = editor.getEditorInput().getName();

			//set source name
			engine.put(ScriptEngine.FILENAME, filename);
		} else {
			engine.put(ScriptEngine.FILENAME, "Adaptable Code");
		}

	}

	/**
	 * Executes the given javaScript
	 *
	 * @param code
	 */
	@Override
	public void execute(String code) {

		if (engine != null) {
			try {
				engine.eval(code);
			} catch (ScriptException exception) {
				String message = "Could not evaluate script";
				LOG.error(message, exception);
				throw new IllegalStateException(message, exception);
			}
		} else {
			LOG.error("Engine is null!");
		}

	}

	//#end region
}
