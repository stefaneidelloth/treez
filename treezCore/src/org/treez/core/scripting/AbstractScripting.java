package org.treez.core.scripting;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.treez.core.atom.base.AbstractAtom;

/**
 * Abstract base class for all scripting classes
 */
public abstract class AbstractScripting implements Scripting {

	//#region METHODS

	/**
	 * Interprets /executes the text of the current document
	 */
	@Override
	public void executeDocument() {
		String editorText = getDocumentText();
		execute(editorText);

	}

	/**
	 * Executes the given script; has to be implemented by deriving classes
	 *
	 * @param script
	 */
	protected abstract void execute(String script);

	/**
	 * Tries to get the root adaptable object from the script engine. Returns null if the root cannot be retrieved.
	 *
	 * @return
	 */
	@Override
	public abstract AbstractAtom getRoot();

	/**
	 * Gets the current document text
	 *
	 * @return
	 */
	protected String getDocumentText() {
		String editorText = "error";

		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor) part;
			IDocumentProvider documentProvider = editor.getDocumentProvider();
			editorText = documentProvider.getDocument(editor.getEditorInput()).get();
		}

		return editorText;
	}

	/**
	 * Gets the currently selected document text
	 *
	 * @return
	 */
	protected String getSelectedDocumentText() {
		String editorText = "";

		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor) part;
			ISelection sel = editor.getSelectionProvider().getSelection();
			if (sel instanceof TextSelection) {
				TextSelection textSel = (TextSelection) sel;
				editorText = textSel.getText();
			}
		}
		return editorText;
	}

	//#end region

}
