package org.treez.core.atom.attribute.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.parent.AbstractAttributeParentAtom;
import org.treez.core.atom.attribute.event.AttributeAtomEvent;
import org.treez.core.atom.copy.CopyHelper;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.ActionSeparator;
import org.treez.core.treeview.action.TreeViewerAction;

import com.sun.javafx.binding.ExpressionHelper;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;

/**
 * Abstract base class for all AttributeAtoms. See the package description for more information.
 *
 * @param <T>
 */
public abstract class AbstractAttributeAtom<T> extends AbstractAttributeParentAtom implements Attribute<T> {

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
	private Map<String, ModifyListener> modifyListeners = null;

	/**
	 * If this is true, the modifyListeners are informed when the method triggerModificationListeners is called. If it
	 * is false, the modifyListener will not be informed. This can be used to avoid that the modifyListeners are
	 * informed several times.
	 */
	private boolean modifyListenersEnabled = true;

	/**
	 * The enabled state.
	 */
	private boolean isEnabled = true;

	private ExpressionHelper<T> javaFxListenerHelper;

	//#end region

	//#region CONSTRUCTORS

	public AbstractAttributeAtom(String name) {
		super(name);
		modifyListeners = new HashMap<>();
	}

	/**
	 * Copy constructor
	 */
	public AbstractAttributeAtom(AbstractAttributeAtom<T> attributeAtomToCopy) {
		super(attributeAtomToCopy);
		//modify listeners are not copied
		modifyListeners = new HashMap<>();
		attributeValue = CopyHelper.copyAttributeValue(attributeAtomToCopy.attributeValue);
		isInitialized = new Boolean(attributeAtomToCopy.isInitialized);
		modifyListenersEnabled = attributeAtomToCopy.modifyListenersEnabled;

	}

	//#end region

	//#region METHODS

	/**
	 * Creates the control for the AttributeAtom. A control for the parameters of the AttributeAtom can be created with
	 * the method ControlAdaption getControlAdaption(Composite parent) which is inherited from AbstractAtom
	 *
	 * @param parent
	 * @return
	 */
	public abstract AbstractAttributeAtom<T> createAttributeAtomControl(
			Composite parent,
			FocusChangingRefreshable treeViewerRefreshable);

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

	/**
	 * Adds a modify listener to be able to listen to changes of the attribute value
	 *
	 * @param listener
	 */
	public void addModifyListener(String key, ModifyListener listener) {
		modifyListeners.put(key, listener);
	}

	public void removeModifyListener(String key) {
		modifyListeners.remove(key);
	}

	/**
	 * Informs the modification listeners about changes
	 */
	public synchronized void triggerListeners() {
		if (this.modifyListenersEnabled) {
			//trigger SWT modify listeners
			ModifyEvent modifyEvent = new AttributeAtomEvent(this).createModifyEvent();
			Set<ModifyListener> listeners = getModifyListeners();
			for (ModifyListener listener : listeners) {
				listener.modifyText(modifyEvent);
			}

			//trigger JavaFx change listeners
			fireJavaFxValueChangedEvent();
		}
	}

	//#end region

	//#region JAVAFX CHANGE LISTENERS (for implementing JavaFx ObservableValue)

	@Override
	public void addListener(ChangeListener<? super T> listener) {
		javaFxListenerHelper = ExpressionHelper.addListener(javaFxListenerHelper, this, listener);
	}

	@Override
	public void removeListener(ChangeListener<? super T> listener) {
		javaFxListenerHelper = ExpressionHelper.removeListener(javaFxListenerHelper, listener);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		javaFxListenerHelper = ExpressionHelper.addListener(javaFxListenerHelper, this, listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		javaFxListenerHelper = ExpressionHelper.removeListener(javaFxListenerHelper, listener);
	}

	protected void fireJavaFxValueChangedEvent() {
		ExpressionHelper.fireValueChangedEvent(javaFxListenerHelper);
	}

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

	protected static Composite createVerticalContainer(Composite parent, FormToolkit toolkit) {
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
	protected static Composite createHorizontalContainer(Composite parent, FormToolkit toolkit) {
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
			String message = "Could not wrap " + this.toString() + " in " + wrap.toString();
			throw new IllegalArgumentException(message, exception);
		}

	}

	@Override
	public void addModificationConsumer(String key, Consumer consumer) {
		addModifyListener(key, (event) -> consumer.consume());
	}

	@Override
	public void addModificationConsumerAndRun(String key, Consumer consumer) {
		addModificationConsumer(key, consumer);
		consumer.consume();
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

	@Override
	public void setEnabled(boolean state) {
		isEnabled = state;
	}

	//#end region

	//#region VALUE

	/**
	 * Returns the object that represents the property value. Might be overridden by implementing classes.
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
	 * For implementing javafx ObservableValue
	 */
	@Override
	public T getValue() {
		return get();
	}

	@Override
	public void set(T value) {
		if (value != attributeValue) {
			attributeValue = value;
			setInitialized();
			refreshAttributeAtomControl();
			triggerListeners();
		}
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

	public Set<ModifyListener> getModifyListeners() {
		Set<ModifyListener> listeners = new HashSet<>();
		for (ModifyListener listener : modifyListeners.values()) {
			listeners.add(listener);
		}
		return listeners;
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

	public abstract void setBackgroundColor(Color backgroundColor);

	//#end region

	//#end region
}
