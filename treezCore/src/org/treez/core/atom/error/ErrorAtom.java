package org.treez.core.atom.error;

import java.util.ArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.EmptyControlAdaption;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.treeview.TreeViewerRefreshable;

/**
 * An atom that is used to show errors
 */
public class ErrorAtom extends AbstractAtom {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(ErrorAtom.class);

	//#region ATTRIBUTES

	private String message;

	private Exception exception;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 * @param message
	 * @param exception
	 */
	public ErrorAtom(String name, String message, Exception exception) {
		super(name);
		this.message = message;
		this.exception = exception;
	}

	/**
	 * Copy constructor
	 *
	 * @param rootToCopy
	 */
	private ErrorAtom(ErrorAtom rootToCopy) {
		super(rootToCopy);
		this.message = rootToCopy.message;
		this.exception = rootToCopy.exception;
	}

	//#end region

	//#region METHODS

	//#region COPY

	@Override
	public ErrorAtom copy() {
		return new ErrorAtom(this);
	}

	//#end region

	/**
	 * Returns the control adaption for this atom
	 */
	@Override
	public AbstractControlAdaption createControlAdaption(Composite parent, Refreshable treeViewRefreshable) {
		String displayString = message + "\n" + ExceptionUtils.getStackTrace(exception);
		return new EmptyControlAdaption(parent, this, displayString);
	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected ArrayList<Object> createContextMenuActions(final TreeViewerRefreshable treeViewer) {

		ArrayList<Object> actions = new ArrayList<>();
		return actions;
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image image = Activator.getImage("error.png");
		return image;
	}

	//#end region

}
