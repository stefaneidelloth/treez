package org.treez.core.atom.adjustable;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.adjustable.preferencePage.Parameters;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.atom.uisynchronizing.ResultWrapper;
import org.treez.core.scripting.ScriptType;
import org.treez.core.scripting.java.JavaScripting;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.ActionSeparator;
import org.treez.core.treeview.action.TreeViewerAction;

/**
 * An implementation of the AbstractAtom which is defined by an underlying model
 * tree. See the package description for more information.
 */
public class AdjustableAtom extends AbstractUiSynchronizingAtom {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(AdjustableAtom.class);

	//#region ATTRIBUTES

	/**
	 * This AbstractAtom represents the model of this AdjustableAtom
	 */
	private AbstractAtom model = null;

	/**
	 * Specifies if this AdjustableAtom should show a run button in the context
	 * menu
	 */
	private Boolean runnable = false;

	/**
	 * Container that can be refreshed
	 */
	protected Composite contentContainer = null;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public AdjustableAtom(String name) {
		super(name);
		//sysLog.debug("Creating adjustable atom " + name);
	}

	/**
	 * Copy constructor
	 *
	 * @param adjustableAtomToCopy
	 */
	public AdjustableAtom(AdjustableAtom adjustableAtomToCopy) {
		super(adjustableAtomToCopy);
		boolean hasNoModel = (adjustableAtomToCopy.model == null);
		if (hasNoModel) {
			model = null;
		} else {
			model = adjustableAtomToCopy.model.copy();
		}
		runnable = adjustableAtomToCopy.runnable;
	}

	//#end region

	//#region METHODS

	//#region COPY

	/**
	 * Overrides the copy method of AbstractAtom using the copy constructor of
	 * this atom
	 */
	@Override
	public AbstractAtom copy() {
		return new AdjustableAtom(this);
	}

	//#end region
	@Override
	public AbstractControlAdaption createControlAdaption(Composite parent,
			Refreshable treeViewRefreshable) {

		//store refreshable tree view
		this.treeViewRefreshable = treeViewRefreshable;

		//create control adaption in UI thread
		@SuppressWarnings("checkstyle:linelength")
		final ResultWrapper<AdjustableAtomControlAdaption> controlAdaptionWrapper = new ResultWrapper<AdjustableAtomControlAdaption>(
				null);
		Runnable createControlAdaptionRunnable = () -> {

			//remove old content and reset parent layout
			resetContentAndLayoutOfParentComposite(parent);

			//create the control adaption and return it
			contentContainer = new Composite(parent, SWT.NONE);
			contentContainer.setLayout(new FillLayout());

			AdjustableAtomControlAdaption newControlAdaption = new AdjustableAtomControlAdaption(
					contentContainer, this, treeViewRefreshable);

			contentContainer.layout();
			afterCreateControlAdaptionHook();

			controlAdaptionWrapper.setValue(newControlAdaption);
		};
		runUiJobBlocking(createControlAdaptionRunnable);
		AdjustableAtomControlAdaption controlAdaption = controlAdaptionWrapper
				.getValue();

		return controlAdaption;
	}

	/**
	 * Method that might perform some additional actions after creating the
	 * control adaption. Can be overridden by inheriting classes. This default
	 * implementation does nothing.
	 */
	protected void afterCreateControlAdaptionHook() {
		//nothing to do here
	}

	/**
	 * Returns the code adaption
	 */
	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {

		CodeAdaption codeAdaption;
		switch (scriptType) {
			case JAVA :
				codeAdaption = new AdjustableAtomCodeAdaption(this);
				break;
			default :
				String message = "The ScriptType " + scriptType
						+ " is not known.";
				throw new IllegalStateException(message);
		}
		return codeAdaption;
	}

	/**
	 * Creates an "empty model" that just consists of a root atom.
	 */
	protected void createEmptyModel() {
		AttributeRoot emptyModel = new AttributeRoot("root");
		setModel(emptyModel);
	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected List<Object> createContextMenuActions(
			final TreeViewerRefreshable treeViewerRefreshable) {

		List<Object> actions = new ArrayList<>();

		//add default actions on top
		if (isRunnable()) {
			Image image = Activator.getImage("run.png");
			actions.add(
					new TreeViewerAction("Run", image, treeViewerRefreshable,
							() -> execute(treeViewerRefreshable)));
		}

		//add separator
		actions.add(new ActionSeparator());

		//add further actions
		actions = extendContextMenuActions(actions, treeViewerRefreshable);

		//add default actions at bottom

		//add separator
		actions.add(new ActionSeparator());

		List<Object> superActions = super.createContextMenuActions(
				treeViewerRefreshable);
		actions.addAll(superActions);

		return actions;
	}

	/**
	 * Adds additional actions to the context menu. Might be overridden by
	 * deriving classes.
	 *
	 * @param actions
	 * @param treeViewer
	 * @return
	 */
	protected List<Object> extendContextMenuActions(List<Object> actions,
			@SuppressWarnings("unused") TreeViewerRefreshable treeViewerRefreshable) {
		return actions;
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image image = Activator.getImage("adjustable.png");
		return image;
	}

	/**
	 * Defines the model from which the Composites of the Control for the
	 * AdjustableAtom are created. This method might be overridden by inheriting
	 * classes.
	 */
	public void createAjustableAtomModel() {

		//get property string
		String modelDefinition = createModelScript();

		//build model tree structure
		JavaScripting scripting = new JavaScripting();
		scripting.execute(modelDefinition);
		model = scripting.getRoot();
		if (model == null) {
			sysLog.error("Could not get root from following model definition:\n"
					+ modelDefinition);
		}

	}

	/**
	 * Defines a java script ... that defines the model. This method might be
	 * overridden by inheriting classes. This default implementation gets the
	 * java script definition from the eclipse preference store.
	 *
	 * @return
	 */
	public String createModelScript() {

		AbstractUIPlugin activator = Activator.getInstance();
		boolean runningInEclipse = activator != null;

		if (runningInEclipse) {
			IPreferenceStore store = Activator.getPreferenceStoreStatic();
			String preferences = store.getString(Parameters.TREE_EDITOR_STRING);
			return preferences;
		} else {
			return "//Error: you need to set a model with setModel(AbstractAtom model) to use the AdjustableAtom";
		}
	}

	/**
	 * Get a model attribute from the model that is identified by the given
	 * model attribute path
	 *
	 * @param <T>
	 * @param modelAttributePath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String modelAttributePath) {

		final int rootPrefixLength = 5;
		boolean pathStartsWithRoot = modelAttributePath
				.substring(0, rootPrefixLength).equals("root.");

		if (pathStartsWithRoot) {
			String remainingAttributePath = modelAttributePath
					.substring(rootPrefixLength, modelAttributePath.length());

			AbstractAtom root = getModel();
			AbstractAtom atom = root.getChild(remainingAttributePath);
			AbstractAttributeAtom<?> attributeAtom = (AbstractAttributeAtom<?>) atom;
			Object attribute = attributeAtom.get();

			if (attribute != null) {

				//try to cast the attribute to the wanted result type and
				//return it
				try {
					T result = (T) attribute;
					return result;
				} catch (ClassCastException exception) {
					throw new IllegalArgumentException("Could not cast from '"
							+ attribute.getClass().getSimpleName()
							+ "' to the wanted generic return type.");
				}
			} else {
				throw new IllegalArgumentException(
						"The attribute path '" + remainingAttributePath
								+ "' resulted in a null value.");
			}

		} else {
			throw new IllegalArgumentException(
					"The model path has to start with 'root.' ");
		}

	}

	/**
	 * Set the value for the attribute with the given attribute path and string
	 * value. The path can be relative or absolute (=starting with "root").
	 *
	 * @param modelAttributePath
	 * @param value
	 */
	public void setAttribute(String modelAttributePath, String value) {
		AbstractAttributeAtom<String> propertyAtom = getAttributeAtom(
				modelAttributePath);
		propertyAtom.set(value);
	}

	/**
	 * Returns the AttributeAtom for a given modelAttributePath
	 *
	 * @param <T>
	 * @param modelAttributePath
	 * @return
	 */
	public <T extends AbstractAttributeAtom<?>> T getAttributeAtom(
			String modelAttributePath) {

		final int rootPrefixLength = 5;
		boolean pathStartsWithRoot = modelAttributePath
				.substring(0, rootPrefixLength).equals("root.");

		if (pathStartsWithRoot) {
			String remainingAttributePath = modelAttributePath
					.substring(rootPrefixLength, modelAttributePath.length());

			//initialize model if required
			boolean modelIsInitialized = model != null;
			if (!modelIsInitialized) {
				//initialize the model if it is not yet build
				initializeModel();
			}

			if (model != null) {
				AbstractAtom child = model.getChild(remainingAttributePath);
				@SuppressWarnings("unchecked")
				T attributeAtom = (T) child;
				return attributeAtom;
			} else {
				throw new IllegalArgumentException(
						"Could not get property atom '" + modelAttributePath
								+ "'. The root node does not have the wanted children.");
			}

		} else {
			throw new IllegalArgumentException(
					"The model path has to start with 'root.' ");
		}
	}

	/**
	 * Initializes the model
	 */
	private void initializeModel() {
		Composite dummyParent = new Composite(
				Display.getCurrent().getActiveShell(), SWT.NULL);
		this.createControlAdaption(dummyParent, null);
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Get model
	 *
	 * @return the model
	 */
	public AbstractAtom getModel() {
		return model;
	}

	/**
	 * Set model
	 *
	 * @param model
	 *            the model to set
	 */
	public void setModel(AbstractAtom model) {
		this.model = model;
	}

	/**
	 * Get runnable
	 *
	 * @return the runnable
	 */
	public Boolean isRunnable() {
		return runnable;
	}

	/**
	 * Set runnable to true: a run action will be shown in the context menu. You
	 * might also want to add a run button at the header of the section in the
	 * control adaption.
	 */
	public void setRunnable() {
		this.runnable = true;
	}

	//#end region

}
