package org.treez.views.tree;

import java.util.Objects;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.scripting.ScriptType;
import org.treez.core.scripting.Scripting;
import org.treez.core.scripting.VariableNameRegistry;
import org.treez.core.scripting.java.JavaScripting;
import org.treez.core.scripting.javascript.JavaScriptScripting;
import org.treez.core.treeview.TreeErrorState;
import org.treez.core.treeview.TreeViewCodeConverter;
import org.treez.core.treeview.TreeViewProvider;
import org.treez.views.tree.rootAtom.Root;

/**
 * This class is responsible for converting a tree to java or java script and vice versa
 */
public class JavaTreeViewCodeConverter implements TreeViewCodeConverter {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(JavaTreeViewCodeConverter.class);

	// #region ATTRIBUTES

	/**
	 * The tree view provider this converter works for
	 */
	private TreeViewProvider treeViewProvider;

	/**
	 * Contains error information
	 */
	private String errorCode;

	// #end region

	// #region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param treeViewProvider
	 */
	public JavaTreeViewCodeConverter(TreeViewProvider treeViewProvider) {
		this.treeViewProvider = treeViewProvider;
	}

	// #end region

	// #region METHODS

	/**
	 * Checks if the currently active document is a java script and builds the tree from the script
	 */
	@Override
	public void checkActiveDocumentAndBuildTreeFromCode() {

		sysLog.info("building tree");

		//get the file name of the currently active document
		String fileName = treeViewProvider.getFileNameOfCurrentlyActiveDocument();

		// check if the file is a java script
		ScriptType scriptType = treeViewProvider.getScriptTypeFromFileName(fileName);
		switch (scriptType) {
		case JAVA_SCRIPT:
			buildTreeFromScript(new JavaScriptScripting());
			break;
		case JAVA:
			buildTreeFromScript(new JavaScripting());
			break;
		default:
			String message = "In order to build the tree a valid script document is required." + "The document '"
					+ fileName + "' has an unknown type.";
			throw new IllegalStateException(message);
		}

	}

	/**
	 * Builds java script from the tree and updates the currently active java script document
	 */
	@Override
	public void checkActiveDoumentAndBuildCodeFromTree() {

		//get file name
		String fileName = treeViewProvider.getFileNameOfCurrentlyActiveDocument();

		//get script type
		ScriptType scriptType = treeViewProvider.getScriptTypeFromFileName(fileName);

		//check script type
		boolean isUnknown = scriptType.equals(ScriptType.UNKNOWN);
		if (isUnknown) {
			String message = "In order to build script from the tree a script file has to be active."
					+ "Please activate a script file and try again.";
			treeViewProvider.showMessage(message);
		}

		//build code
		sysLog.debug("building " + scriptType + " code from tree");
		buildCodeFromTree(fileName, scriptType);
	}

	/**
	 * Builds Java code from the current tree and writes it to the current document
	 */
	private void buildCodeFromTree(String fileName, ScriptType scriptType) {

		//reset variable name registry
		VariableNameRegistry.reset();

		//get root atom
		AbstractAtom rootAtom = getRootAtom();
		if (rootAtom == null) {
			return;
		}
		sysLog.info("Identified root atom '" + rootAtom.getName() + "'");

		//create code
		CodeAdaption codeAdaption = rootAtom.createCodeAdaption(scriptType);
		String className = createClassName(fileName);
		CodeContainer rootContainer = codeAdaption.buildRootCodeContainer(className);
		Optional<CodeContainer> injectedChildContainer = Optional.ofNullable(null);
		CodeContainer codeContainer = codeAdaption.buildCodeContainer(rootContainer, injectedChildContainer);
		String code = codeContainer.buildCode();

		//write code to current document
		writeCodeToCurrentDocument(code);

	}

	/**
	 * Creates a class name from the given file name
	 *
	 * @return
	 */
	private static String createClassName(String fileName) {

		String[] subStrings = fileName.split("\\.");
		String className;
		if (subStrings.length < 1) {
			className = "#error determinding class name#";
		} else {
			className = subStrings[0];
		}
		return className;
	}

	/**
	 * Writes the given code to the current document
	 *
	 * @param code
	 */
	private static void writeCodeToCurrentDocument(String code) {

		sysLog.info("Writing code to current document.");

		//get currently active editor
		IEditorPart activeEditor = PlatformUI
				.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getActiveEditor();
		boolean isTextEditor = activeEditor instanceof AbstractTextEditor;

		if (!isTextEditor) {
			return;
		}

		ITextEditor editor = (ITextEditor) activeEditor;
		IDocumentProvider documentProvider = editor.getDocumentProvider();
		IDocument document = documentProvider.getDocument(editor.getEditorInput());
		try {
			document.replace(0, 0, code + "\n");
		} catch (BadLocationException e) {
			String message = "Could not write code to document";
			sysLog.error(message, e);
			return;
		}

		sysLog.info("finished");

	}

	/**
	 * Gets the root atom from the treeViewProvider. Returns null if the root atom can not be retrieved.
	 *
	 * @return
	 */
	private AbstractAtom getRootAtom() {
		Objects.requireNonNull(treeViewProvider, "Tree view provider must not be null");
		TreeViewer treeViewer = treeViewProvider.getTreeViewer();
		Objects.requireNonNull(treeViewer, "Tree viewer must not be null");
		TreeItem[] roots = treeViewer.getTree().getItems();
		boolean rootExists = roots.length == 1;
		if (!rootExists) {
			String message = "Number of root itmes is not 1 but " + roots.length
					+ ". Could not get root atom from tree viewer.";
			sysLog.error(message);
			return null;
		}
		TreeItem rootItem = roots[0];
		Objects.requireNonNull(rootItem, "Root item must not be null");
		Object rootObject = rootItem.getData();
		Objects.requireNonNull(rootObject, "Root object must not be null");

		boolean isAbstractAtom = AbstractAtom.class.isAssignableFrom(rootObject.getClass());
		if (!isAbstractAtom) {
			String message = "The root object is not an AbstractAtom but " + rootObject.getClass().getSimpleName()
					+ ". Could not get valid root from tree viewer.";
			sysLog.error(message);
			return null;
		}
		AbstractAtom rootAtom = (AbstractAtom) rootObject;
		return rootAtom;
	}

	/**
	 * Builds the tree by executing the java document and setting the so created "root" object as input of the tree
	 * viewer
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public void buildTreeFromScript(Scripting scripting) {

		// initialize exception and error code
		errorCode = "";
		Exception executionException = null;

		// try to execute the currently active document
		try {
			scripting.executeDocument();
		} catch (Exception exception) {
			executionException = exception;
			errorCode = exception.getMessage();
			sysLog.error("Could not execute currently active document (error code '" + errorCode + "')", exception);
		}

		// update the tree view
		boolean exceptionExists = (executionException != null);
		if (exceptionExists) {
			treeViewProvider.visualizeErrorState(TreeErrorState.ERROR, executionException);
		} else {

			// create the invisible root element
			AbstractAtom invisibleRoot = new Root("invisibleRoot");

			// retrieve the (visible) root element from the scripting support
			AbstractAtom root = scripting.getRoot();

			// attach the (visible) root to the invisible root of the tree view
			if (root != null) {
				invisibleRoot.addChild(root);
				treeViewProvider.updateTreeContent(invisibleRoot);

				// update the error state to remove the eventually existing
				// error visualization
				treeViewProvider.visualizeErrorState(TreeErrorState.OK, null);
			} else {
				// the root element could not be created from the script:
				// show an error by setting the background color of the tree
				// view to orange
				String message = "The root item 'root' could not be found.";
				sysLog.debug(message);
				treeViewProvider.visualizeErrorState(TreeErrorState.ERROR, new IllegalStateException(message));
			}
		}

	}

	// #end region

}
