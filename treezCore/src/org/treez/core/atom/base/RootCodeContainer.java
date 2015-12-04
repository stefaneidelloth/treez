package org.treez.core.atom.base;

import java.util.List;

import org.treez.core.adaptable.CodeContainer;
import org.treez.core.scripting.ScriptType;

/**
 * The root code container contains the complete code for creating a tree.
 * 
 */
public class RootCodeContainer extends CodeContainer {

	//#region ATTRIBUTES

	/**
	 * The AbstractAtom this code container corresponds to
	 */
	private AbstractAtom atom;

	/**
	 * The class name
	 */
	private String className;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 * 
	 * @param atom
	 * @param className
	 */
	public RootCodeContainer(AbstractAtom atom, String className) {
		super(ScriptType.JAVA);
		this.atom = atom;
		this.className = className;
		buildHeader();
		buildImports();
		buildOpening();

		buildClosing();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the header
	 */
	private void buildHeader() {
		extendHeader("package org.treez.example;");
		extendHeaderWithEmptyLine();
	}

	/**
	 * Creates the initial import lines
	 */
	private void buildImports() {
		extendImports("import org.treez.core.scripting.ModelProvider;");
	}

	/**
	 * Creates the opening code
	 */
	private void buildOpening() {
		extendOpeningWithEmptyLine();
		extendOpening("public class " + className + " extends ModelProvider {");
		extendOpeningWithEmptyLine();
		extendOpening("\tpublic Root createModel() {");
		extendOpeningWithEmptyLine();
	}

	/**
	 * Creates the closing code
	 */
	private void buildClosing() {
		buildExpansionCode();
		buildReturnRootCode();
	}

	/**
	 * Builds code that is used to save the expansion state
	 * 
	 */
	private void buildExpansionCode() {
		List<String> expandedNodes = atom.getExpandedNodes();

		if (expandedNodes.size() > 0) {
			String code = atom.getName() + ".setExpandedNodes('";
			for (int index = 0; index < expandedNodes.size() - 1; index++) {
				code = code + expandedNodes.get(index) + ",";
			}
			code = code + expandedNodes.get(expandedNodes.size() - 1) + "');";
			extendClosing(code);
		}
	}

	/**
	 * Build code that returns the root atom
	 */
	private void buildReturnRootCode() {
		extendClosingWithEmptyLine();
		extendClosing("\t\treturn root;");
		extendClosing("\t}");
		extendClosing("}");
	}

	//#end region

}
