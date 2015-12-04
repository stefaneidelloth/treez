package org.treez.core.atom.attribute.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeParentAtom;
import org.treez.core.atom.attribute.event.AttributeAtomEvent;
import org.treez.core.atom.copy.CopyHelper;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.ActionSeparator;
import org.treez.core.treeview.action.TreeViewerAction;

/**
 * Abstract base class for all AttributeAtoms. See the package description for
 * more information.
 *
 * @param <T>
 */
public abstract class AbstractAttributeAtom<T>
		extends
			AbstractAttributeParentAtom
		implements Attribute<T> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger
			.getLogger(AbstractUiSynchronizingAtom.class);

	//#region ATTRIBUTES

	/**
	 * This size is used to determine if form elements should be displayed in a
	 * single line or in extra lines
	 */
	protected static final int CHARACTER_LENGTH_LIMIT = 50;

	/**
	 * Default background color
	 */
	protected static final Color DEFAULT_BACKGROUND_COLOR = new Color(null, 255,
			255, 255);

	/**
	 * The attribute value that is managed by this AttributeAtom
	 */
	protected T attributeValue = null;

	/**
	 * If this is true, the AttributeAtom has already been initialized and the
	 * attribute value can be obtained
	 */
	private Boolean isInitialized = false;

	/**
	 * List of listener that will react on modifications of the the attribute
	 * value. (The binding of that listener has to be considered in the
	 * implementations of the AttributeAtom, e.g. by calling
	 * triggerModificationListeners)
	 */
	private List<ModifyListener> modifyListeners = null;

	/**
	 * If this is true, the modifyListeners are informed when the method
	 * triggerModificationListeners is called. If it is false, the
	 * modifyListener will not be informed. This can be used to avoid that the
	 * modifyListeners are informed several times.
	 */
	private boolean modifyListenersEnabled = true;

	/**
	 * The enabled state.
	 */
	private boolean isEnabled = true;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public AbstractAttributeAtom(String name) {
		super(name);
		modifyListeners = new ArrayList<ModifyListener>();
	}

	/**
	 * Copy constructor
	 *
	 * @param attributeAtomToCopy
	 */
	public AbstractAttributeAtom(AbstractAttributeAtom<T> attributeAtomToCopy) {
		super(attributeAtomToCopy);
		//modify listeners are not copied
		modifyListeners = new ArrayList<ModifyListener>();
		attributeValue = CopyHelper
				.copyAttributeValue(attributeAtomToCopy.attributeValue);
		isInitialized = new Boolean(attributeAtomToCopy.isInitialized);
		modifyListenersEnabled = attributeAtomToCopy.modifyListenersEnabled;

	}

	//#end region

	//#region METHODS

	/**
	 * Creates the control for the AttributeAtom. A control for the parameters
	 * of the AttributeAtom can be created with the method ControlAdaption
	 * getControlAdaption(Composite parent) which is inherited from AbstractAtom
	 *
	 * @param parent
	 * @return
	 */
	public abstract AbstractAttributeAtom<T> createAttributeAtomControl(
			Composite parent, Refreshable treeViewerRefreshable);

	/**
	 * Refreshes the control of the AttributeAtom after the attribute value has
	 * been set by calling setValue()
	 */
	public abstract void refreshAttributeAtomControl();

	@Override
	public AttributeAtomCodeAdaption<T> createCodeAdaption(
			ScriptType scriptType) {

		AttributeAtomCodeAdaption<T> codeAdaption;
		switch (scriptType) {
			case JAVA :
				codeAdaption = new AttributeAtomCodeAdaption<T>(this);
				break;
			default :
				String message = "The ScriptType " + scriptType
						+ " is not yet implemented.";
				throw new IllegalStateException(message);
		}

		return codeAdaption;
	}

	/**
	 * Creates the context menu actions
	 *
	 * @return
	 */
	@Override
	protected List<Object> createContextMenuActions(
			final TreeViewerRefreshable treeViewerRefreshable) {
		ArrayList<Object> actions = new ArrayList<>();

		//reset
		actions.add(
				new TreeViewerAction("Reset", Activator.getImage("reset.png"),
						treeViewerRefreshable, () -> resetToDefaultValue()));

		//disable
		if (isEnabled) {
			actions.add(new TreeViewerAction("Disable",
					Activator.getImage("disable.png"), treeViewerRefreshable,
					() -> setEnabled(false)));
		}

		//enable
		if (!isEnabled) {
			actions.add(new TreeViewerAction("Enable",
					Activator.getImage("enable.png"), treeViewerRefreshable,
					() -> setEnabled(true)));
		}

		actions.add(new ActionSeparator());

		List<Object> superActions = super.createContextMenuActions(
				treeViewerRefreshable);
		actions.addAll(superActions);

		return actions;
	}

	/**
	 * Resets the attribute value to its default value
	 */
	private void resetToDefaultValue() {
		set(getDefaultValue());
	}

	/**
	 * Adds a modify listener to be able to listen to changes of the attribute
	 * value
	 *
	 * @param listener
	 */
	public void addModifyListener(ModifyListener listener) {
		modifyListeners.add(listener);
	}

	/**
	 * Informs the modification listeners about changes
	 */
	public void triggerModificationListeners() {
		if (this.modifyListenersEnabled) {
			ModifyEvent modifyEvent = new AttributeAtomEvent(this)
					.createModifyEvent();
			for (ModifyListener listener : getModifyListeners()) {
				listener.modifyText(modifyEvent);
			}
		}
	}

	/**
	 * Creates a container layout where all content is put in a single line
	 *
	 * @param contentContainer
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	protected static void createLayoutForSingleLine(Composite contentContainer,
			int marginWidth) {

		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.horizontalSpacing = 5;
		gridLayout.marginHeight = 4;
		gridLayout.marginWidth = marginWidth;
		contentContainer.setLayout(gridLayout);
		contentContainer.setLayoutData(fillHorizontal);
	}

	/**
	 * Creates a container layout where the controls are put into individual
	 * lines
	 *
	 * @param contentContainer
	 */
	protected static void createLayoutForIndividualLines(
			Composite contentContainer, int marginWidth) {

		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 2;
		gridLayout.marginWidth = marginWidth;
		contentContainer.setLayout(gridLayout);
		contentContainer.setLayoutData(fillHorizontal);

	}

	protected static Composite createVerticalContainer(Composite parent,
			FormToolkit toolkit) {
		//create grid data to use all horizontal space
		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

		//container for label and rest
		Composite container = toolkit.createComposite(parent);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(fillHorizontal);
		return container;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	protected static Composite createHorizontalContainer(Composite parent,
			FormToolkit toolkit) {
		//create grid data to use all horizontal space
		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

		//container for label and rest
		Composite container = toolkit.createComposite(parent);
		final int maxNumberOfColumns = 10;
		GridLayout gridLayout = new GridLayout(maxNumberOfColumns, false);
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		container.setLayout(gridLayout);
		container.setLayoutData(fillHorizontal);
		return container;
	}

	@Override
	public String toString() {
		if (attributeValue != null) {
			return attributeValue.toString();
		} else {
			return null;
		}
	}

	/**
	 * Wraps this attribute in the AttributeWrapper that is given as Attribute
	 *
	 * @param wrap
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public void wrap(Attribute<T> wrap) {
		Wrap<T> wrapper;
		try {
			wrapper = (Wrap<T>) wrap;
			wrapper.setAttribute(this);
		} catch (Exception exception) {
			String message = "Could not wrap " + this.toString() + " in "
					+ wrap.toString();
			throw new IllegalArgumentException(message, exception);
		}

	}

	//#end region

	//#region ACCESSORS

	//#region ENABLED

	/**
	 * Returns true if this attribute atom is enabled
	 *
	 * @return
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @param state
	 */
	@Override
	public void setEnabled(boolean state) {
		isEnabled = state;
	}

	//#end region

	//#region VALUE

	/**
	 * Returns the object that represents the property value. Might be
	 * overridden by implementing classes.
	 *
	 * @return
	 */
	@Override
	public T get() {
		if (isInitialized()) {
			return attributeValue;
		} else {
			return getDefaultValue();
		}

	}

	/**
	 * Sets the value
	 *
	 * @param value
	 */
	@Override
	public void set(T value) {
		if (value != attributeValue) {
			attributeValue = value;
			setInitialized();
			refreshAttributeAtomControl();
			triggerModificationListeners();
		}
	}

	//#end region

	//#region DEFAULT VALUE

	/**
	 * @return
	 */
	public abstract T getDefaultValue();

	/**
	 * Returns true if the value equals the default value
	 *
	 * @return
	 */
	public boolean hasDefaultValue() {
		T value = get();
		T defaultValue = getDefaultValue();
		if (value == null) {
			boolean hasDefaultValue = (defaultValue == null);
			return hasDefaultValue;
		} else {
			boolean hasDefaultValue = get().equals(getDefaultValue());
			return hasDefaultValue;
		}
	}

	//#end region

	//#region INITIALIZED

	/**
	 * Returns true if this AttributeAtom has already been initialized
	 *
	 * @return
	 */
	public Boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * Sets the initialization state to true
	 */
	protected void setInitialized() {
		this.isInitialized = true;
	}

	/**
	 * Sets the initialization state to false
	 */
	public void resetInitialized() {
		this.isInitialized = false;
	}

	//#end region

	//#region MODIFICATION LISTENING

	/**
	 * Returns the modify listeners
	 *
	 * @return
	 */
	public List<ModifyListener> getModifyListeners() {
		return modifyListeners;
	}

	/**
	 * Enables the triggering of the modification listeners with the method
	 * triggerModificationListeners()
	 */
	public void enableModificationListeners() {
		this.modifyListenersEnabled = true;
	}

	/**
	 * Disables the triggering of the modification listeners with the method
	 * triggerModificationListeners()
	 */
	public void disableModificationListeners() {
		this.modifyListenersEnabled = false;
	}

	//#end region

	//#region BACKGROUND COLOR

	/**
	 * Sets the background color
	 *
	 * @param backgroundColor
	 */
	public abstract void setBackgroundColor(Color backgroundColor);

	//#end region

	//#end region
}
