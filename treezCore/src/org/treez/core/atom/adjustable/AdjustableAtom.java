package org.treez.core.atom.adjustable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.treez.core.Activator;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.atom.uisynchronizing.ResultWrapper;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.ActionSeparator;
import org.treez.core.treeview.action.TreeViewerAction;

/**
 * An implementation of the AbstractAtom<?> which is defined by an underlying model tree. See the package description
 * for more information.
 */
public class AdjustableAtom extends AbstractUiSynchronizingAtom<AdjustableAtom> {

	//#region ATTRIBUTES

	/**
	 * This AbstractAtom<?> represents the model of this AdjustableAtom
	 */
	private AbstractAtom<?> model = null;

	/**
	 * Specifies if this AdjustableAtom should show a run button in the context menu
	 */
	private Boolean runnable = false;

	/**
	 * Container that can be refreshed
	 */
	protected Composite contentContainer = null;

	//#end region

	//#region CONSTRUCTORS

	public AdjustableAtom(String name) {
		super(name);
		//sysLog.debug("Creating adjustable atom " + name);
	}

	/**
	 * Copy constructor
	 */
	public AdjustableAtom(AdjustableAtom atomToCopy) {
		super(atomToCopy);
		boolean hasNoModel = (atomToCopy.model == null);
		if (hasNoModel) {
			model = null;
		} else {
			model = atomToCopy.model.copy();
		}
		runnable = atomToCopy.runnable;

	}

	//#end region

	//#region METHODS

	@Override
	public AdjustableAtom getThis() {
		return this;
	}

	@Override
	public AdjustableAtom copy() {
		return new AdjustableAtom(this);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void copyTreezAttributes(AdjustableAtom source, AdjustableAtom target) {

		Set<Field> fields = getFields(source);

		for (Field field : fields) {

			try {
				field.setAccessible(true);
				Object fieldObject = field.get(source);
				boolean isAttribute = fieldObject instanceof Attribute;
				if (isAttribute) {
					Wrap wrapToCopy = (Wrap) fieldObject;
					AbstractAttributeAtom attributeToCopy = (AbstractAttributeAtom) wrapToCopy.getAttribute();

					Object fieldObjectToSet = target.getFieldValue(field.getName());
					Wrap wrapToSet = (Wrap) fieldObjectToSet;
					wrapToSet.setAttribute(attributeToCopy.copy());

				}

			} catch (Exception exception) {
				String message = "Could not copy attribute value for class '" + getClass().getSimpleName() + "'";
				throw new IllegalStateException(message, exception);
			}

		}

	}

	private static Set<Field> getFields(Object object) {
		Set<Field> fields = new HashSet<>();

		Class<?> clazz = object.getClass();

		Field[] accessibleFields = clazz.getFields();
		fields.addAll(Arrays.asList(accessibleFields));

		Field[] declaredFields = clazz.getDeclaredFields();
		fields.addAll(Arrays.asList(declaredFields));

		return fields;
	}

	private Object getFieldValue(String name) {
		Set<Field> fields = getFields(this);
		for (Field field : fields) {
			if (field.getName().equals(name)) {

				field.setAccessible(true);
				try {
					return field.get(this);
				} catch (Exception e) {
					String message = "Could not get field value '" + name + "' from class '"
							+ this.getClass().getSimpleName()
							+ "'. (One possible cause is a missing copy constructor.)";
					throw new IllegalStateException(message, e);
				}
			}
		}
		String message = "Could not get field value '" + name + "' from class '" + this.getClass().getSimpleName()
				+ "'. (One possible cause is a missing copy constructor.)";
		throw new IllegalStateException(message);
	}

	@Override
	public
			AbstractControlAdaption
			createControlAdaption(Composite parent, FocusChangingRefreshable treeViewRefreshable) {

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
					contentContainer,
					this,
					treeViewRefreshable);

			contentContainer.layout();
			afterCreateControlAdaptionHook();

			controlAdaptionWrapper.setValue(newControlAdaption);
		};
		runUiTaskBlocking(createControlAdaptionRunnable);
		AdjustableAtomControlAdaption controlAdaption = controlAdaptionWrapper.getValue();

		return controlAdaption;
	}

	/**
	 * Method that might perform some additional actions after creating the control adaption. Can be overridden by
	 * inheriting classes. This default implementation does nothing.
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
		case JAVA:
			codeAdaption = new AdjustableAtomCodeAdaption(this);
			break;
		default:
			String message = "The ScriptType " + scriptType + " is not known.";
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
	protected List<Object> createContextMenuActions(final TreeViewerRefreshable treeViewerRefreshable) {

		List<Object> actions = new ArrayList<>();

		//add default actions on top
		if (isRunnable()) {
			Image image = Activator.getImage("run.png");
			actions.add(
					new TreeViewerAction("Run", image, treeViewerRefreshable, () -> execute(treeViewerRefreshable)));
		}

		//add separator
		actions.add(new ActionSeparator());

		//add further actions
		actions = extendContextMenuActions(actions, treeViewerRefreshable);

		//add default actions at bottom

		//add separator
		actions.add(new ActionSeparator());

		List<Object> superActions = super.createContextMenuActions(treeViewerRefreshable);
		actions.addAll(superActions);

		return actions;
	}

	/**
	 * Adds additional actions to the context menu. Might be overridden by deriving classes.
	 *
	 * @param actions
	 * @param treeViewer
	 * @return
	 */
	protected List<Object> extendContextMenuActions(
			List<Object> actions,
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
	 * Get a model attribute from the model that is identified by the given model attribute path
	 *
	 * @param <T>
	 * @param modelAttributePath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String modelAttributePath) {

		final int rootPrefixLength = 5;
		boolean pathStartsWithRoot = modelAttributePath.substring(0, rootPrefixLength).equals("root.");

		if (pathStartsWithRoot) {
			String remainingAttributePath = modelAttributePath.substring(rootPrefixLength, modelAttributePath.length());

			AbstractAtom<?> root = getModel();
			AbstractAtom<?> atom = root.getChild(remainingAttributePath);
			AbstractAttributeAtom<?, ?> attributeAtom = (AbstractAttributeAtom<?, ?>) atom;
			Object attribute = attributeAtom.get();

			if (attribute != null) {

				//try to cast the attribute to the wanted result type and
				//return it
				try {
					T result = (T) attribute;
					return result;
				} catch (ClassCastException exception) {
					throw new IllegalArgumentException(
							"Could not cast from '" + attribute.getClass().getSimpleName()
									+ "' to the wanted generic return type.");
				}
			} else {
				throw new IllegalArgumentException(
						"The attribute path '" + remainingAttributePath + "' resulted in a null value.");
			}

		} else {
			throw new IllegalArgumentException("The model path has to start with 'root.' ");
		}

	}

	/**
	 * Set the value for the attribute with the given attribute path and string value. The path can be relative or
	 * absolute (=starting with "root").
	 *
	 * @param modelAttributePath
	 * @param value
	 */
	public void setAttribute(String modelAttributePath, String value) {
		AbstractAttributeAtom<?, String> propertyAtom = getAttributeAtom(modelAttributePath);
		propertyAtom.set(value);
	}

	/**
	 * Returns the AttributeAtom for a given modelAttributePath
	 *
	 * @param <T>
	 * @param modelAttributePath
	 * @return
	 */
	public <T extends AbstractAttributeAtom<?, ?>> T getAttributeAtom(String modelAttributePath) {

		final int rootPrefixLength = 5;
		boolean pathStartsWithRoot = modelAttributePath.substring(0, rootPrefixLength).equals("root.");

		if (pathStartsWithRoot) {
			String remainingAttributePath = modelAttributePath.substring(rootPrefixLength, modelAttributePath.length());

			//initialize model if required
			boolean modelIsInitialized = model != null;
			if (!modelIsInitialized) {
				//initialize the model if it is not yet build
				initializeModel();
			}

			if (model != null) {
				AbstractAtom<?> child = model.getChild(remainingAttributePath);
				@SuppressWarnings("unchecked")
				T attributeAtom = (T) child;
				return attributeAtom;
			} else {
				throw new IllegalArgumentException(
						"Could not get property atom '" + modelAttributePath
								+ "'. The root node does not have the wanted children.");
			}

		} else {
			throw new IllegalArgumentException("The model path has to start with 'root.' ");
		}
	}

	/**
	 * Initializes the model
	 */
	private void initializeModel() {
		Composite dummyParent = new Composite(Display.getCurrent().getActiveShell(), SWT.NULL);
		this.createControlAdaption(dummyParent, null);
	}

	//#end region

	//#region ACCESSORS

	public AbstractAtom<?> getModel() {
		return model;
	}

	public void setModel(AbstractAtom<?> model) {
		this.model = model;
	}

	public Boolean isRunnable() {
		return runnable;
	}

	/**
	 * Set runnable to true: a run action will be shown in the context menu. You might also want to add a run button at
	 * the header of the section in the control adaption.
	 */
	public void setRunnable() {
		this.runnable = true;
	}

	//#end region

}
