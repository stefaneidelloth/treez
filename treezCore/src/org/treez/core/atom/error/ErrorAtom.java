package org.treez.core.atom.error;

import java.util.ArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * An atom that is used to show errors
 */
public class ErrorAtom extends AbstractAtom<ErrorAtom> {

	//#region ATTRIBUTES

	private String message;

	private Exception exception;

	private String fullMessage;

	//#end region

	//#region CONSTRUCTORS

	public ErrorAtom(String name, String message, Exception exception) {
		super(name);
		this.message = message;

		this.exception = exception;

		fullMessage = message + "\n" + ExceptionUtils.getStackTrace(exception);
	}

	/**
	 * Copy constructor
	 */
	private ErrorAtom(ErrorAtom rootToCopy) {
		super(rootToCopy);
		this.message = rootToCopy.message;
		this.exception = rootToCopy.exception;
		this.fullMessage = rootToCopy.fullMessage;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	protected ErrorAtom getThis() {
		return this;
	}

	@Override
	public ErrorAtom copy() {
		return new ErrorAtom(this);
	}

	//#end region

	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite parent,
			FocusChangingRefreshable treeViewRefreshable) {

		return new EmptyControlAdaption(parent, this, fullMessage);
	}

	@Override
	protected ArrayList<Object> createContextMenuActions(final TreeViewerRefreshable treeViewer) {

		ArrayList<Object> actions = new ArrayList<>();
		return actions;
	}

	@Override
	public Image provideImage() {
		Image image = Activator.getImage("error.png");
		return image;
	}

	//#end region

}
