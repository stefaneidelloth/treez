package org.treez.core.atom.attribute.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeParentAtom;
import org.treez.core.atom.copy.CopyHelper;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.ActionSeparator;
import org.treez.core.treeview.action.TreeViewerAction;

/**
 * Abstract base class for all AttributeAtoms. See the package description for more information. The second generic type
 * T determines the type of the represented value, e.g. Double or String.
 */
public abstract class AbstractAttributeAtom<A extends AbstractAttributeAtom<A, T>, T>
		extends
		AbstractAttributeParentAtom<A>
		implements
		Attribute<T> {

	//#region ATTRIBUTES

	/**
	 * This size is used to determine if form elements should be displayed in a single line or in extra lines
	 */
	protected static final int CHARACTER_LENGTH_LIMIT = 50;

	protected static final Color DEFAULT_BACKGROUND_COLOR = new Color(null, 255, 255, 255);

	/**
	 * The attribute value that is managed by this AttributeAtom
	 */
	protected T attributeValue = null;

	/**
	 * If this is true, the AttributeAtom has already been initialized and the attribute value can be obtained
	 */
	private Boolean isInitialized = false;

	/**
	 * Listener that will react on modifications of the the attribute value. (The bindings of that listeners have to be
	 * considered in the implementations of the AttributeAtom, e.g. by calling triggerModificationListeners) In order to
	 * avoid duplicate lambda expressions, the listeners are managed as a map.
	 */
	private Map<String, Consumer> modifyListeners = null;

	/**
	 * If this is true, the modifyListeners are informed when the method triggerModificationListeners is called. If it
	 * is false, the modifyListener will not be informed. This can be used to avoid that the modifyListeners are
	 * informed several times.
	 */
	private boolean modifyListenersEnabled = true;

	private boolean isEnabled = true;

	private boolean isVisible = true;

	protected Color backgroundColor = DEFAULT_BACKGROUND_COLOR;

	//#end region

	//#region CONSTRUCTORS

	public AbstractAttributeAtom(String name) {
		super(name);
		modifyListeners = new HashMap<>();
	}

	/**
	 * Copy constructor
	 */
	public AbstractAttributeAtom(AbstractAttributeAtom<A, T> atomToCopy) {
		super(atomToCopy);
		//modify listeners are not copied
		modifyListeners = new HashMap<>();
		attributeValue = CopyHelper.copyAttributeValue(atomToCopy.attributeValue);
		isInitialized = atomToCopy.isInitialized;
		modifyListenersEnabled = atomToCopy.modifyListenersEnabled;
		isEnabled = atomToCopy.isEnabled;
		isVisible = atomToCopy.isVisible;
		backgroundColor = atomToCopy.backgroundColor;
	}

	//#end region

	//#region METHODS

	@Override
	public abstract AbstractAttributeAtom<A, T> copy();

	/**
	 * Creates the control for the AttributeAtom. A control for the parameters of the AttributeAtom can be created with
	 * the method ControlAdaption getControlAdaption(Composite parent) which is inherited from AbstractAtom
	 *
	 * @param parent
	 * @return
	 */
	public abstract
			AbstractAttributeAtom<A, T>
			createAttributeAtomControl(Composite parent, FocusChangingRefreshable treeViewerRefreshable);

	/**
	 * Refreshes the control of the AttributeAtom after the attribute value has been set by calling setValue()
	 */
	public abstract void refreshAttributeAtomControl();

	@Override
	public AttributeAtomCodeAdaption<T> createCodeAdaption(ScriptType scriptType) {

		AttributeAtomCodeAdaption<T> codeAdaption;
		switch (scriptType) {
		case JAVA:
			codeAdaption = new AttributeAtomCodeAdaption<T>(this);
			break;
		default:
			String message = "The ScriptType " + scriptType + " is not yet implemented.";
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
	protected List<Object> createContextMenuActions(final TreeViewerRefreshable treeViewerRefreshable) {
		ArrayList<Object> actions = new ArrayList<>();

		//reset
		actions.add(new TreeViewerAction(
				"Reset",
				Activator.getImage("reset.png"),
				treeViewerRefreshable,
				() -> resetToDefaultValue()));

		//disable
		if (isEnabled) {
			actions.add(new TreeViewerAction(
					"Disable",
					Activator.getImage("disable.png"),
					treeViewerRefreshable,
					() -> setEnabled(false)));
		}

		//enable
		if (!isEnabled) {
			actions.add(new TreeViewerAction(
					"Enable",
					Activator.getImage("enable.png"),
					treeViewerRefreshable,
					() -> setEnabled(true)));
		}

		actions.add(new ActionSeparator());

		List<Object> superActions = super.createContextMenuActions(treeViewerRefreshable);
		actions.addAll(superActions);

		return actions;
	}

	/**
	 * Resets the attribute value to its default value
	 */
	private void resetToDefaultValue() {
		set(getDefaultValue());
	}

	//#region SWT MODIFICATION LISTENERS

	public void removeModifyListener(String key) {
		modifyListeners.remove(key);
	}

	/**
	 * Informs the modification consumers about changes
	 */
	public synchronized void triggerListeners() {
		if (this.modifyListenersEnabled) {

			Set<Consumer> modificationConsumers = getModificationConsumers();
			for (Consumer consumer : modificationConsumers) {
				consumer.consume();
			}

		}
	}

	//#end region

	//#region JAVAFX CHANGE LISTENERS (for implementing JavaFx ObservableValue)

	//#end region

	/**
	 * Creates a container layout where all content is put in a single line
	 *
	 * @param contentContainer
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	protected static void createLayoutForSingleLine(Composite contentContainer, int marginWidth) {

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
	 * Creates a container layout where the controls are put into individual lines
	 *
	 * @param contentContainer
	 */
	protected static void createLayoutForIndividualLines(Composite contentContainer, int marginWidth) {

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

	protected Composite createVerticalContainer(Composite parent, FormToolkit toolkit) {
		//create grid data to use all horizontal space
		GridData fillHorizontal = new GridData();
		fillHorizontal.grabExcessHorizontalSpace = true;
		fillHorizontal.horizontalAlignment = GridData.FILL;

		//container for label and rest
		Composite container = toolkit.createComposite(parent);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(fillHorizontal);
		container.setBackground(backgroundColor);
		return container;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	protected Composite createHorizontalContainer(Composite parent, FormToolkit toolkit) {
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
		container.setBackground(backgroundColor);
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
			String message = "Could not wrap " + this.toString() + " in " + wrap.toString();
			throw new IllegalArgumentException(message, exception);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends Attribute<T>> C addModificationConsumer(String key, Consumer consumer) {

		this.modifyListeners.put(key, consumer);
		return (C) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends Attribute<T>> C addModificationConsumerAndRun(String key, Consumer consumer) {
		addModificationConsumer(key, consumer);
		consumer.consume();
		return (C) this;
	}

	//#end region

	//#region ACCESSORS

	//#region ENABLED

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public A setEnabled(boolean state) {
		isEnabled = state;
		return getThis();
	}

	//#end region

	//#region VISIBLE

	@Override
	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public A setVisible(boolean state) {
		isVisible = state;
		return getThis();
	}

	//#end region

	//#region VALUE

	/**
	 * Returns the object that represents the property value. Might be overridden by implementing classes.
	 */
	@Override
	public T get() {
		if (isInitialized()) {
			return attributeValue;
		} else {
			return getDefaultValue();
		}
	}

	@Override
	public A set(T value) {
		if (value != attributeValue) {
			attributeValue = value;
			setInitialized();
			AbstractUiSynchronizingAtom.runUiTaskNonBlocking(() -> refreshAttributeAtomControl());
			triggerListeners();
		}
		return getThis();
	}

	//#end region

	//#region DEFAULT VALUE

	public abstract T getDefaultValue();

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

	public Boolean isInitialized() {
		return isInitialized;
	}

	protected void setInitialized() {
		this.isInitialized = true;
	}

	public void resetInitialized() {
		this.isInitialized = false;
	}

	//#end region

	//#region MODIFICATION LISTENING

	public Set<Consumer> getModificationConsumers() {
		Set<Consumer> modificationConsumers = new HashSet<>();
		for (Consumer consumer : modifyListeners.values()) {
			modificationConsumers.add(consumer);
		}
		return modificationConsumers;
	}

	/**
	 * Enables the triggering of the modification listeners with the method triggerModificationListeners()
	 */
	public void enableModificationListeners() {
		this.modifyListenersEnabled = true;
	}

	/**
	 * Disables the triggering of the modification listeners with the method triggerModificationListeners()
	 */
	public void disableModificationListeners() {
		this.modifyListenersEnabled = false;
	}

	//#end region

	//#region BACKGROUND COLOR

	public abstract A setBackgroundColor(Color backgroundColor);

	//#end region

	//#end region
}
