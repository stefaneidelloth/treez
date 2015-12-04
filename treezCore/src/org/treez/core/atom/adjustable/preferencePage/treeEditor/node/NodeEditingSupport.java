package org.treez.core.atom.adjustable.preferencePage.treeEditor.node;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.treez.core.adaptable.Adaptable;

/**
 * Allows to edit the name of a node in a tree viewer
 */
public class NodeEditingSupport extends EditingSupport {

	//#region ATTRIBUTES

	private TreeViewer treeViewer;

	private TextCellEditor textCellEditor;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param treeViewer
	 */
	public NodeEditingSupport(TreeViewer treeViewer) {
		super(treeViewer);
		this.treeViewer = treeViewer;
		this.textCellEditor = new TextCellEditor(treeViewer.getTree());
	}

	//#end region

	//#region METHODS

	@Override
	protected boolean canEdit(Object element) {
		Adaptable adaptable = (Adaptable) element;
		String name = adaptable.createTreeNodeAdaption().getName();
		boolean canEdit = !name.equals("root");
		return canEdit;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return textCellEditor;
	}

	@Override
	protected Object getValue(Object element) {
		Adaptable adaptable = (Adaptable) element;
		String name = adaptable.createTreeNodeAdaption().getName();
		return name;
	}

	@Override
	protected void setValue(Object element, Object value) {
		Adaptable adaptable = (Adaptable) element;
		String name = String.valueOf(value);
		adaptable.createTreeNodeAdaption().setName(name);

		treeViewer.update(element, null);
	}

	//#end region

}
