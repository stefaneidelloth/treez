package org.treez.model.atom.genericInput;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.adjustable.AdjustableAtomControlAdaption;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.BooleanVariableField;
import org.treez.core.atom.variablefield.DirectoryPathVariableField;
import org.treez.core.atom.variablefield.DoubleVariableField;
import org.treez.core.atom.variablefield.FilePathVariableField;
import org.treez.core.atom.variablefield.IntegerVariableField;
import org.treez.core.atom.variablefield.QuantityVariableField;
import org.treez.core.atom.variablefield.StringItemVariableField;
import org.treez.core.atom.variablefield.StringVariableField;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.AddColoredChildAtomTreeViewerAction;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.model.Activator;
import org.treez.model.atom.AbstractModel;

/**
 * Represents a generic model that typically consists of several variable fields. The model for this adjustable atom is
 * created from its children (=the variable fields) that can be dynamically added by the user. The model can not perform
 * actions by itself. The variable fields of this generic model can be used as dependency/input for other atoms, e.g.
 * the InputFileGenerator.
 */
public class GenericInputModel extends AbstractModel {

	//#region CONSTRUCTORS

	public GenericInputModel(String name) {
		super(name);
		setManualModel();
		createGenericModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the control adaption on the given contentComposite. You might want to clean old content on the
	 * contentComposite before (re-) creating the ControlAdaption with this method.
	 */
	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite contentComposite,
			FocusChangingRefreshable treeViewRefreshable) {
		return super.createControlAdaption(contentComposite, treeViewRefreshable);
	}

	@SuppressWarnings("unused")
	@Override
	public synchronized void refresh() {
		super.refresh();
		createGenericModel();
		if (isAvailable(contentContainer)) {
			AdjustableAtomControlAdaption controlAdaption = new AdjustableAtomControlAdaption(
					contentContainer,
					this,
					treeViewRefreshable);
			contentContainer.layout();

		}
	}

	/**
	 * Creates/updates the model for this atom
	 */
	private void createGenericModel() {

		// root, page and section
		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "genericInputModel";
		String helpContextId = Activator.getAbsoluteHelpContextIdStatic(relativeHelpContextId);

		Section data = dataPage.createSection("data", helpContextId);

		// build variable fields from children
		List<TreeNodeAdaption> childNodes = this.createTreeNodeAdaption().getChildren();
		for (TreeNodeAdaption childNode : childNodes) {
			Adaptable adaptable = childNode.getAdaptable();
			AbstractAtom<?> variableField = (AbstractAtom<?>) adaptable;
			data.addChildReference(variableField);
		}

		setModel(root);
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("genericModel.png");
	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		//disable children
		List<AbstractAtom<?>> children = this.getChildAtoms();
		boolean hasChildren = children != null && !children.isEmpty();
		if (hasChildren) {
			actions.add(new TreeViewerAction(
					"Disable all variable fields",
					org.treez.core.Activator.getImage("disable.png"),
					treeViewer,
					() -> disableAllVariableFields()));

			//enable children
			actions.add(new TreeViewerAction(
					"Enable all variable fields",
					org.treez.core.Activator.getImage("enable.png"),
					treeViewer,
					() -> enableAllVariableFields()));
		}

		Action addQuantityVariableField = new AddColoredChildAtomTreeViewerAction(
				QuantityVariableField.class,
				"quantityVariable",
				org.treez.core.Activator.getImage("quantityVariable.png"),
				this,
				treeViewer);
		actions.add(addQuantityVariableField);

		Action addDoubleVariableField = new AddColoredChildAtomTreeViewerAction(
				DoubleVariableField.class,
				"doubleVariable",
				org.treez.core.Activator.getImage("doubleVariable.png"),
				this,
				treeViewer);
		actions.add(addDoubleVariableField);

		Action addIntegerVariableField = new AddColoredChildAtomTreeViewerAction(
				IntegerVariableField.class,
				"integerVariable",
				org.treez.core.Activator.getImage("integerVariable.png"),
				this,
				treeViewer);
		actions.add(addIntegerVariableField);

		Action addBooleanVariableField = new AddColoredChildAtomTreeViewerAction(
				BooleanVariableField.class,
				"booleanVariable",
				org.treez.core.Activator.getImage("booleanVariable.png"),
				this,
				treeViewer);
		actions.add(addBooleanVariableField);

		Action addStringVariableField = new AddColoredChildAtomTreeViewerAction(
				StringVariableField.class,
				"stringVariable",
				org.treez.core.Activator.getImage("stringVariable.png"),
				this,
				treeViewer);
		actions.add(addStringVariableField);

		Action addStringItemVariableField = new AddColoredChildAtomTreeViewerAction(
				StringItemVariableField.class,
				"stringItemVariable",
				org.treez.core.Activator.getImage("stringItemVariable.png"),
				this,
				treeViewer);
		actions.add(addStringItemVariableField);

		Action addFilePathVariableField = new AddColoredChildAtomTreeViewerAction(
				FilePathVariableField.class,
				"filePathVariable",
				org.treez.core.Activator.getImage("filePathVariable.png"),
				this,
				treeViewer);
		actions.add(addFilePathVariableField);

		Action addDirectoryPathVariableField = new AddColoredChildAtomTreeViewerAction(
				DirectoryPathVariableField.class,
				"directoryPathVariable",
				org.treez.core.Activator.getImage("directoryPathVariable.png"),
				this,
				treeViewer);
		actions.add(addDirectoryPathVariableField);

		return actions;
	}

	/**
	 * Enables all variable field children
	 */
	private void enableAllVariableFields() {
		List<AbstractAtom<?>> children = this.getChildAtoms();
		for (AbstractAtom<?> child : children) {
			boolean isVariableField = VariableField.class.isAssignableFrom(child.getClass());
			if (isVariableField) {
				VariableField<?, ?> variableField = (VariableField<?, ?>) child;
				variableField.setEnabled(true);
			}

		}
	}

	/**
	 * Disable all variable field children
	 */
	private void disableAllVariableFields() {
		List<AbstractAtom<?>> children = this.getChildAtoms();
		for (AbstractAtom<?> child : children) {
			boolean isVariableField = VariableField.class.isAssignableFrom(child.getClass());
			if (isVariableField) {
				VariableField<?, ?> variableField = (VariableField<?, ?>) child;
				variableField.setEnabled(false);
			}

		}
	}

	/**
	 * Returns the code adaption
	 */
	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {
		return new GenericInputModelCodeAdaption(this);
	}

	/**
	 * Overrides the method addChild to update the generic model after adding the child
	 */
	@Override
	public void addChild(AbstractAtom<?> child) {
		super.addChild(child);
		createGenericModel();
	}

	//#region CREATE CHILD ATOMS

	public DoubleVariableField createDoubleVariableField(String name) {
		DoubleVariableField child = new DoubleVariableField(name);
		addChild(child);
		return child;
	}

	public IntegerVariableField createIntegerVariableField(String name) {
		IntegerVariableField child = new IntegerVariableField(name);
		addChild(child);
		return child;
	}

	public QuantityVariableField createQuantityVariableField(String name) {
		QuantityVariableField child = new QuantityVariableField(name);
		addChild(child);
		return child;
	}

	public StringVariableField createStringVariableField(String name) {
		StringVariableField child = new StringVariableField(name);
		addChild(child);
		return child;
	}

	public FilePathVariableField createFilePathVariableField(String name) {
		FilePathVariableField child = new FilePathVariableField(name);
		addChild(child);
		return child;
	}

	public DirectoryPathVariableField createDirectoryPathVariableField(String name) {
		DirectoryPathVariableField child = new DirectoryPathVariableField(name);
		addChild(child);
		return child;
	}

	public BooleanVariableField createBooleanVariableField(String name) {
		BooleanVariableField child = new BooleanVariableField(name);
		addChild(child);
		return child;
	}

	public StringItemVariableField createStringItemVariableField(String name) {
		StringItemVariableField child = new StringItemVariableField(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

	//#region ACCESSORS

	public String getVariable(String variableName) {
		return getAttribute("root.data.data." + variableName);
	}

	public void setVariable(String variableName, String valueString) {
		setAttribute("root.data.data." + variableName, valueString);
	}

	public List<VariableField<?, ?>> getVariableFields() {
		List<VariableField<?, ?>> variableFields = new ArrayList<>();
		List<TreeNodeAdaption> childNodes = this.createTreeNodeAdaption().getChildren();
		for (TreeNodeAdaption childNode : childNodes) {
			@SuppressWarnings("unchecked")
			VariableField<?, ?> variableField = (VariableField<?, ?>) childNode.getAdaptable();
			variableFields.add(variableField);
		}
		return variableFields;
	}

	public List<VariableField<?, ?>> getEnabledVariableFields() {
		List<VariableField<?, ?>> variableFields = new ArrayList<>();
		List<TreeNodeAdaption> childNodes = this.createTreeNodeAdaption().getChildren();
		for (TreeNodeAdaption childNode : childNodes) {
			Adaptable child = childNode.getAdaptable();
			@SuppressWarnings("unchecked")
			VariableField<?, ?> variableField = (VariableField<?, ?>) child;
			if (variableField.isEnabled()) {
				variableFields.add(variableField);
			}

		}
		return variableFields;
	}

	//#end region

}
