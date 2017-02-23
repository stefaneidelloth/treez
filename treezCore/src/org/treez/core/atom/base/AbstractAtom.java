package org.treez.core.atom.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.treez.core.Activator;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.GraphicsAdaption;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.base.annotation.IsParameters;
import org.treez.core.atom.copy.Copiable;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.NameConsumer;
import org.treez.core.attribute.Wrap;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.core.utils.Utils;

/**
 * This is the main implementation of the interface Adaptable and the parent class for all treez atoms. An AbstractAtom
 * (actually its TreeNodeAdaption) has parents and children. The ControlAdaption of this AbstractAtom<?> is build with
 * the help of the annotation "IsParameter", see the class AtomControlAdaption for more details. * The AttributeAtom and
 * its deriving classes give an example on how to use these annotations. * If you want to create more complex atoms,
 * also have a look at the AdjustableAtom. The ControlAdaption of AdjustableAtoms is created from an underlying tree
 * model. Each implementation of this AbstractAtom<?> should define a copy constructor and use it in the method copy()
 * that must be overridden. The purpose of the generic type A is to allow for method chaining calls of inheriting
 * classes (Also see
 * http://stackoverflow.com/questions/1069528/method-chaining-inheritance-don-t-play-well-together/1070556#1070556).
 */
public abstract class AbstractAtom<A extends AbstractAtom<A>> implements Adaptable, Copiable<AbstractAtom<A>> {

	private static final Logger LOG = Logger.getLogger(AbstractAtom.class);

	//#region ATTRIBUTES

	/**
	 * The name of this AbstractAtom. This name will for example be used by the TreeNodeAdaption. In order to be able to
	 * identify an AbstractAtom<?> by its tree path, this name should only be used once for all children of the parent
	 * AbstractAtom. The name might also be used in Java code for saving the tree structure. It is recommended to use
	 * lower case names.
	 */
	protected String name;

	/**
	 * A list of consumers that are informed on name changes
	 */
	private List<NameConsumer> nameConsumers;

	/**
	 * The parent of this AbstractAtom
	 */
	protected AbstractAtom<?> parentAtom;

	/**
	 * The children of this AbstractAtom
	 */
	protected List<AbstractAtom<?>> children;

	/**
	 * The context menu actions of this AbstractAtom
	 */
	protected List<IAction> contextMenuActions;

	/**
	 * Used to save the expansion state (has to be set from outside)
	 */
	protected ArrayList<String> expandedNodes = new ArrayList<>();

	/**
	 * Default key for an image that represents this AbstractAtom<?> in a tree view
	 */
	protected String IMAGE_KEY = ISharedImages.IMG_OBJ_ELEMENT;

	/**
	 * The help id that can be used do access dynamic help for the atom
	 */
	protected String helpId = "org.treez.core.UNDEFINED_HELP_ID";

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor. If a derived class uses the annotation IsParameter for its attributes, the attributes are
	 * initialized using the value provided by that annotation.
	 *
	 * @param name
	 */
	public AbstractAtom(String name) {
		//LOG.debug("Creating abstract atom " + name);
		this.name = name;
		this.children = new ArrayList<AbstractAtom<?>>();
		initAttributesWithIsParameterAnnotationValues();
	}

	/**
	 * Copy Constructor
	 */
	public AbstractAtom(AbstractAtom<A> abstractAtomToCopy) {
		this.name = abstractAtomToCopy.name;
		this.children = copyAbstractAtoms(abstractAtomToCopy.children);
		this.expandedNodes = abstractAtomToCopy.expandedNodes;
		initAttributesWithIsParameterAnnotationValues();
	}

	//#end region

	//#region METHODS

	protected abstract A getThis();

	//#region COPY

	@Override
	public abstract AbstractAtom<A> copy();

	/**
	 * Copies the given list of abstract atoms
	 *
	 * @param abstractAtomsToCopy
	 * @return
	 */
	public List<AbstractAtom<?>> copyAbstractAtoms(List<AbstractAtom<?>> abstractAtomsToCopy) {
		List<AbstractAtom<?>> abstractAtoms = new ArrayList<>(abstractAtomsToCopy.size());
		for (AbstractAtom<?> abstractAtomToCopy : abstractAtomsToCopy) {
			AbstractAtom<?> abstractAtom = abstractAtomToCopy.copy();
			abstractAtoms.add(abstractAtom);
		}
		return abstractAtoms;
	}

	//#end region

	//#region implementation of Adaptable interface using specialized classes

	@Override
	public TreeNodeAdaption createTreeNodeAdaption() {
		TreeNodeAdaption treeNodeAdaption = new AtomTreeNodeAdaption(this);
		return treeNodeAdaption;
	}

	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {

		CodeAdaption codeAdaption;
		switch (scriptType) {
		case JAVA:
			codeAdaption = new AtomCodeAdaption(this);
			break;
		default:
			String message = "The ScriptType " + scriptType + " is not yet implemented.";
			throw new IllegalStateException(message);
		}

		return codeAdaption;

	}

	@Override
	public GraphicsAdaption createGraphicsAdaption(Composite parent) {
		GraphicsAdaption graphicsAdaption = new AtomGraphicsAdaption(parent, this);
		return graphicsAdaption;
	}

	//#end region

	//#region initialization of attributes with the annotation IsParameter

	/**
	 * If a derived class uses the IsParameter annotation for some of its attributes, the values of those attributes are
	 * initialized with this method, using the default values that are provided by the IsParameter annotation. If the
	 * class itself does not use IsParameter annotations, but its direct super class does, use the annotations of the
	 * direct super class to initialize the attributes.
	 */
	protected void initAttributesWithIsParameterAnnotationValues() {

		Class<?> atomClass = this.getClass();

		boolean foundAnIsParameterAnnotation = initAttributesWithAnnotationsForClass(atomClass);

		if (!foundAnIsParameterAnnotation) {
			//No IsParameter annotation has been found. Maybe it is a class
			//that derives
			//from an attribute atom and has no parameter annotations itself.
			//In that case, try to use the annotations of the super class to
			//initialize the
			//attributes.
			Class<?> superClass = atomClass.getSuperclass();
			foundAnIsParameterAnnotation = initAttributesWithAnnotationsForClass(superClass);

			if (!foundAnIsParameterAnnotation) {

				Class<?> superSuperClass = atomClass.getSuperclass();
				foundAnIsParameterAnnotation = initAttributesWithAnnotationsForClass(superSuperClass);

				if (!foundAnIsParameterAnnotation) {

					Class<?> superSuperSuperClass = superSuperClass.getSuperclass();
					foundAnIsParameterAnnotation = initAttributesWithAnnotationsForClass(superSuperSuperClass);
				}
			}
		}

	}

	/**
	 * Initializes the values of the attributes that are annotated with the IsParameter annotation using the given
	 * class. If an IsParameter annotation has been found, the method returns true.
	 *
	 * @param atomClass
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private boolean initAttributesWithAnnotationsForClass(Class<?> atomClass) {
		boolean foundAnIsParameterAnnotation = false;
		Field[] attributes = atomClass.getDeclaredFields();
		for (Field attribute : attributes) {
			//LOG.debug("Existing attribute: " + attribute.getName());
			attribute.setAccessible(true);
			boolean isParameterAnnotated = IsParameters.isAnnotated(attribute);
			if (isParameterAnnotated) {
				foundAnIsParameterAnnotation = true;
				//LOG.debug("The field " + field.getName() + " is
				//annotated.");
				String valueString = IsParameters.getDefaultValueString(attribute);
				try {
					Object attributeParent = this;
					IsParameters.setAttributeValue(attribute, attributeParent, valueString);
				} catch (Exception exception) {
					String message = "Could not set attribute value for " + attribute.getName();
					LOG.error(message, exception);
					throw new IllegalStateException(message, exception);
				}
			}
		}
		return foundAnIsParameterAnnotation;
	}

	//#end region

	//#region actions

	/**
	 * Provide an execution method for this AbstractAtom. This method might be overridden be deriving classes and does
	 * nothing by default. (This method can be triggered for example with a button in the tree view.)
	 */
	@SuppressWarnings("unused")
	public void execute(FocusChangingRefreshable treeViewerRefreshable) {
		//empty default implementation
	}

	/**
	 * Executes all children that have the given class
	 *
	 * @param wantedClass
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	protected void executeChildren(Class<?> wantedClass, FocusChangingRefreshable treeViewerRefreshable)
			throws IllegalArgumentException {

		AbstractAtom<?>[] childArray = children.toArray(new AbstractAtom[children.size()]);

		for (AbstractAtom<?> child : childArray) {
			Class<?> currentClass = child.getClass();
			boolean hasWantedClass = wantedClass.isAssignableFrom(currentClass);
			if (hasWantedClass) {
				try {
					child.execute(treeViewerRefreshable);
				} catch (Exception exception) {
					String message = "Could not execute child '" + child.getName() + "' of '" + getName() + "'.";
					LOG.error(message, exception);
				}
			}
		}
	}

	/**
	 * Default implementation for the creation of the context menu actions. This method is used by the corresponding
	 * AtomTreeNodeAdaption to fill its context menu. This default implementation only includes a dummy example action.
	 * It should be overridden by inheriting classes.
	 *
	 * @param treeViewerRefreshable
	 * @return
	 */
	protected List<Object> createContextMenuActions(TreeViewerRefreshable treeViewerRefreshable) {

		ArrayList<Object> actions = new ArrayList<>();

		//rename
		actions.add(new TreeViewerAction(
				"Rename",
				Activator.getImage("rename.png"),
				treeViewerRefreshable,
				() -> rename()));

		//move up
		boolean canBeMovedUp = canBeMovedUp();
		if (canBeMovedUp) {
			actions.add(new TreeViewerAction(
					"Move up",
					Activator.getImage("up.png"),
					treeViewerRefreshable,
					() -> moveUp()));
		}

		//move down
		boolean canBeMovedDown = canBeMovedDown();
		if (canBeMovedDown) {
			actions.add(new TreeViewerAction(
					"Move down",
					Activator.getImage("down.png"),
					treeViewerRefreshable,
					() -> moveDown()));
		}

		//delete
		actions.add(new TreeViewerAction(
				"Delete",
				Activator.getImage(ISharedImages.IMG_TOOL_DELETE),
				treeViewerRefreshable,
				() -> createTreeNodeAdaption().delete()));

		return actions;
	}

	/**
	 * Returns true if this atom can be moved up in the children of its parent
	 *
	 * @return
	 */
	private boolean canBeMovedUp() {
		AbstractAtom<?> parent = this.getParentAtom();
		if (parent != null) {
			List<AbstractAtom<?>> currentChildren = parent.getChildAtoms();
			boolean childrenExist = currentChildren != null && currentChildren.size() > 1;
			if (childrenExist) {
				int currentIndex = currentChildren.indexOf(this);
				if (currentIndex > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Moves the atom up in the children of the parent atom
	 */
	public AbstractAtom<A> moveUp() {
		boolean canBeMovedUp = canBeMovedUp();
		if (canBeMovedUp) {
			AbstractAtom<?> parent = this.getParentAtom();
			List<AbstractAtom<?>> currentChildren = parent.getChildAtoms();
			int currentIndex = currentChildren.indexOf(this);
			Collections.swap(currentChildren, currentIndex, currentIndex - 1);
			tryToRefreshAtom(parent);
		}
		return this;
	}

	/**
	 * Moves the atom in the children of the parent atom to a specific index (Position)
	 */
	public AbstractAtom<A> moveAtom(int position) {
		boolean canBeMovedUp = canBeMovedUp();
		if (canBeMovedUp) {
			AbstractAtom<?> parent = this.getParentAtom();
			List<AbstractAtom<?>> currentChildren = parent.getChildAtoms();
			int currentIndex = currentChildren.indexOf(this);
			Collections.rotate(currentChildren.subList(position, currentIndex + 1), 1);
			tryToRefreshAtom(parent);
		}
		return this;
	}

	/**
	 * Returns true if this atom can be moved down in the children of its parent
	 *
	 * @return
	 */
	private boolean canBeMovedDown() {
		AbstractAtom<?> parent = this.getParentAtom();
		if (parent != null) {
			List<AbstractAtom<?>> currentChildren = parent.getChildAtoms();
			boolean childrenExist = currentChildren != null && currentChildren.size() > 1;
			if (childrenExist) {
				int currentIndex = currentChildren.indexOf(this);
				if (currentIndex < currentChildren.size() - 1) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Moves the atom down in the children of the parent atom
	 */
	public AbstractAtom<A> moveDown() {
		boolean canBeMovedDown = canBeMovedDown();
		if (canBeMovedDown) {
			AbstractAtom<?> parent = this.getParentAtom();
			List<AbstractAtom<?>> currentChildren = parent.getChildAtoms();
			int currentIndex = currentChildren.indexOf(this);
			Collections.swap(currentChildren, currentIndex + 1, currentIndex);
			tryToRefreshAtom(parent);
		}
		return this;
	}

	/**
	 * Refreshes the given AbstractAtom<?> if it implements the interface Refreshable
	 *
	 * @param parent
	 */
	private static void tryToRefreshAtom(AbstractAtom<?> parent) {
		boolean parentIsRefreshable = FocusChangingRefreshable.class.isAssignableFrom(parent.getClass());
		if (parentIsRefreshable) {
			FocusChangingRefreshable refreshableParent = (FocusChangingRefreshable) parent;
			refreshableParent.refresh();
		}
	}

	//#end region

	//#region name

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Shows a dialog for renaming this AbstractAtom
	 */
	public void rename() {
		String newName = Utils.getInput("Please enter the new name:", getName());
		setName(newName);
	}

	protected void addNameModificationListener(NameConsumer nameConsumer) {
		if (nameConsumers == null) {
			nameConsumers = new ArrayList<>();
		}
		nameConsumers.add(nameConsumer);
	}

	protected void removeNameModificationListener(Consumer nameConsumer) {
		nameConsumers.remove(nameConsumer);
	}

	private void triggerNameListeners(String newName) {
		if (nameConsumers != null) {
			for (NameConsumer listener : nameConsumers) {
				listener.consume(newName);
			}
		}
	}

	//#end region

	//#region image

	/**
	 * Returns the image for this AbstractAtom. It is used by the corresponding AtomTreeNodeAdaption. This method might
	 * be overridden by deriving classes. This default implementation returns the default image that is defined by the
	 * IMAGE_KEY.
	 *
	 * @return
	 */
	public Image provideImage() {
		return Activator.getImage(IMAGE_KEY);
	}

	//#end region

	//#region REFLECTION

	protected static String getFieldName(Object fieldObject, Object parent) {
		Objects.requireNonNull(fieldObject);
		boolean parentIsString = parent.getClass().equals(String.class);
		if (parentIsString) {
			throw new IllegalStateException(
					"Parent must not be a string. You might want to use 'this' instead of a property name.");
		}

		Field[] allPublicFieldsWithFieldsOfSuperClasses = parent.getClass().getFields();

		String fieldName = getFieldName(fieldObject, parent, allPublicFieldsWithFieldsOfSuperClasses);
		if (fieldName == null) {
			Field[] allFieldsOfCurrentClass = parent.getClass().getDeclaredFields();
			fieldName = getFieldName(fieldObject, parent, allFieldsOfCurrentClass);
		}

		if (fieldName != null) {
			return fieldName;
		}

		String message = "Could not determine field name of parent '" + parent.toString() + "'.";

		throw new IllegalStateException(message);
	}

	@SuppressWarnings("checkstyle:illegalcatch")
	private static String getFieldName(Object fieldObject, Object parent, Field[] allFields) {

		for (Field field : allFields) {

			boolean isAccessible = field.isAccessible();
			field.setAccessible(true);
			Object currentFieldObject;
			try {
				currentFieldObject = field.get(parent);
				field.setAccessible(isAccessible);
			} catch (Exception e) {
				throw new IllegalStateException("Could not determine field name.");
			}
			boolean isWantedField = fieldObject.equals(currentFieldObject);
			if (isWantedField) {
				String fieldName = field.getName();
				return fieldName;
			}
		}

		return null;
	}

	//#end region

	//#region child operations

	/**
	 * Add the given AbstractAtom<?> as a child and removes it from the old parent if an old parent exists.
	 *
	 * @param child
	 */
	public void addChild(AbstractAtom<?> child) {
		//LOG.debug("add child to " + getName());
		AbstractAtom<?> oldParent = child.getParentAtom();
		child.setParentAtom(this);

		//LOG.debug("parent set");

		children.add(child);
		if (oldParent != null) {
			//remove child from old parent
			oldParent.createTreeNodeAdaption().removeChild(child.createTreeNodeAdaption());
		}
	}

	/**
	 * Adds the given AbstractAtom<?> as a child but does not set the parent of the child. The given AbstractAtom <?>
	 * will be listed as a child of this AbstractAtom. If the given AbstractAtom<?> is asked for its parent, the old
	 * parent will be returned. This way, an AbstractAtom<?> can be used in several trees as a child while the "one and
	 * only real parent" is kept.
	 *
	 * @param child
	 */
	public void addChildReference(AbstractAtom<?> child) {
		children.add(child);
	}

	/**
	 * Get child atom with given child name/sub model path. Throws an IllegalArgumentException if the child could not be
	 * found.
	 *
	 * @param childPath
	 * @return
	 * @throws IllegalArgumentException
	 */
	public AbstractAtom<?> getChild(String childPath) throws IllegalArgumentException {

		boolean isPath = childPath.contains(".");

		if (isPath) {
			//iterate through path to get wanted child
			String[] childNames = childPath.split("\\.");
			String firstName = childNames[0];
			AbstractAtom<?> child = getChildByName(firstName);
			//go to the wanted child in a loop; each iteration
			//overrides the previous parent atom in the loop
			for (int index = 1; index < childNames.length; index++) {
				child = child.getChildByName(childNames[index]);
			}
			return child;
		} else {
			//get child by name
			String childName = childPath;
			return getChildByName(childName);
		}
	}

	/**
	 * Get child atom with given child tree path. Throws an IllegalArgumentException if the child tree path can not be
	 * found.
	 *
	 * @param childPathStartingWithRoot
	 * @return
	 */
	public <T> T getChildFromRoot(String childPathStartingWithRoot) throws IllegalArgumentException {

		final int rootLength = 5; //"root."
		boolean isTooShort = childPathStartingWithRoot.length() < rootLength + 1;
		if (isTooShort) {
			throw new IllegalArgumentException(
					"The path has to start with 'root.' but is '" + childPathStartingWithRoot + "'.");
		}

		boolean startsWithRoot = childPathStartingWithRoot.substring(0, rootLength).equals("root.");

		if (startsWithRoot) {
			int length = childPathStartingWithRoot.length();
			String childPath = childPathStartingWithRoot.substring(rootLength, length);
			AbstractAtom<?> root = getRoot();
			AbstractAtom<?> child = root.getChild(childPath);
			if (child == null) {
				return null;
			}

			try {
				@SuppressWarnings("unchecked")
				T castedChild = (T) child;
				return castedChild;
			} catch (ClassCastException exception) {
				throw new IllegalArgumentException("Could not cast child to wanted type", exception);
			}
		} else {
			throw new IllegalArgumentException(
					"The path has to start with 'root.' but is '" + childPathStartingWithRoot + "'.");
		}

	}

	/**
	 * Creates a child atom of the given class with the given name prefix.
	 *
	 * @param atomClass
	 * @param namePrefix
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public AbstractAtom<?> createChildAtom(Class<? extends AbstractAtom<?>> atomClass, String namePrefix) {
		Objects.requireNonNull(atomClass, "Atom class must not be null");
		Objects.requireNonNull(namePrefix, "Name prefix must not be null");

		String newName = AtomTreeNodeAdaption.createChildNameStartingWith(this, namePrefix);
		AbstractAtom<?> newChild;
		try {
			Constructor<? extends AbstractAtom<?>> atomConstructor = atomClass
					.getConstructor(new Class[] { String.class });
			newChild = atomConstructor.newInstance(new Object[] { newName });
		} catch (Exception exception) {
			String message = "Could not create child atom for class " + atomClass.getSimpleName();
			LOG.error(message, exception);
			throw new IllegalArgumentException(message, exception);
		}

		addChild(newChild);
		return newChild;
	}

	/**
	 * Returns true if the root of this atom has a child at the given child path.
	 *
	 * @param childPathStartingWithRoot
	 * @return
	 */
	public boolean rootHasChild(String childPathStartingWithRoot) {
		try {
			getChildFromRoot(childPathStartingWithRoot);
			return true;
		} catch (IllegalArgumentException exception) {
			return false;
		}
	}

	/**
	 * Gets the first child atom with the given name. Throws an IllegalArgumentException if the child could not be
	 * found.
	 *
	 * @param childName
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected AbstractAtom<?> getChildByName(String childName) throws IllegalArgumentException {
		for (AbstractAtom<?> currentChild : children) {
			boolean isWantedChild = currentChild.getName().equals(childName);
			if (isWantedChild) {
				return currentChild;
			}
		}
		throw new IllegalArgumentException("Could not find child '" + childName + "' in '" + name + "'.");
	}

	/**
	 * Gets the first child atom with the given class. Throws an IllegalArugmentException if the child could not be
	 * found.
	 *
	 * @param clazz
	 * @return
	 */
	public <T> T getChildByClass(Class<T> clazz) {

		for (AbstractAtom<?> currentChild : children) {
			boolean isWantedChild = currentChild.getClass().equals(clazz);
			if (isWantedChild) {
				@SuppressWarnings("unchecked")
				T castedChild = (T) currentChild;
				return castedChild;
			}
		}
		throw new IllegalArgumentException(
				"Could not find a child with class'" + clazz.getSimpleName() + "' in '" + name + "'.");
	}

	/**
	 * Gets a list of all child atoms with the given class. Returns an empty list if no child with the given class could
	 * be found.
	 */
	public <T> List<T> getChildrenByClass(Class<T> clazz) {
		List<T> wantedChildren = new ArrayList<>();
		for (AbstractAtom<?> currentChild : children) {
			boolean isWantedChild = currentChild.getClass().equals(clazz);
			if (isWantedChild) {
				@SuppressWarnings("unchecked")
				T castedChild = (T) currentChild;
				wantedChildren.add(castedChild);
			}
		}
		return wantedChildren;
	}

	/**
	 * Gets a list of all child atoms that implement the given Interface. Returns an empty list if no child implements
	 * the interface.
	 */
	public <T> List<T> getChildrenByInterface(Class<T> clazz) {
		List<T> wantedChildren = new ArrayList<>();
		for (AbstractAtom<?> currentChild : children) {
			boolean isWantedChild = clazz.isInstance(currentChild);
			if (isWantedChild) {
				@SuppressWarnings("unchecked")
				T castedChild = (T) currentChild;
				wantedChildren.add(castedChild);
			}
		}
		return wantedChildren;
	}

	public void removeChildrenByInterface(Class<?> clazz) {
		List<AbstractAtom<?>> childrenToRemove = new ArrayList<>();
		for (AbstractAtom<?> child : children) {
			boolean isWantedChild = clazz.isInstance(child);
			if (isWantedChild) {
				childrenToRemove.add(child);
			}
		}
		children.removeAll(childrenToRemove);
	}

	/**
	 * Checks if any of the children, sub children and so on is of the class with the given name
	 *
	 * @param targetClassName
	 * @return
	 */
	public boolean containsChildOfType(String targetClassName) {

		//check if any of the children has the wanted type
		for (AbstractAtom<?> currentChild : children) {
			boolean hasWantedType = Utils.checkIfHasWantedType(currentChild, targetClassName);
			if (hasWantedType) {
				return true;
			}
		}

		//go on and check if any of the children of the children has the wanted
		//type
		for (AbstractAtom<?> currentChild : children) {
			boolean hasWantedType = Utils.checkIfHasWantedType(currentChild, targetClassName);
			if (hasWantedType) {
				return true;
			}
		}

		//could not find any child that has the wanted type
		return false;
	}

	/**
	 * Returns true if the given control is available
	 *
	 * @param composite
	 * @return
	 */
	protected static boolean isAvailable(Control composite) {
		return composite != null && !composite.isDisposed();
	}

	/**
	 * Removes all children of this atom
	 */
	public void removeAllChildren() {
		children.clear();
	}

	/**
	 * Removes the child with the given name if it exists. The names of the children should be unique. Only the first
	 * child with the given name is removed.
	 */
	public void removeChildIfExists(String childName) {

		AbstractAtom<?> childToRemove = null;
		for (AbstractAtom<?> child : children) {
			String currentChildName = child.getName();
			boolean isWantedChild = currentChildName.equals(childName);
			if (isWantedChild) {
				childToRemove = child;
				break;
			}
		}
		if (childToRemove != null) {
			children.remove(childToRemove);
		}

	}

	//#end region

	//#region expansion state

	/**
	 * Gets the expanded nodes that were saved in this atom.
	 *
	 * @return
	 */
	public ArrayList<String> getExpandedNodes() {
		return expandedNodes;
	}

	/**
	 * Saves the expanded nodes
	 *
	 * @param expandedNodesString
	 */
	public void setExpandedNodes(String expandedNodesString) {
		ArrayList<String> givenExpandedNodes = new ArrayList<>();
		String[] nodes = expandedNodesString.split(",");
		for (String node : nodes) {
			givenExpandedNodes.add(node);
		}
		this.expandedNodes = givenExpandedNodes;
	}

	//#end region

	//#region parent operations

	/**
	 * Returns the root atom of the tree this atom is included in. Returns null if the parent node of this atom is null.
	 *
	 * @return
	 */
	public AbstractAtom<?> getRoot() {

		//get parent node
		TreeNodeAdaption parentNode = this.createTreeNodeAdaption().getParent();

		if (parentNode == null) {
			throw new IllegalStateException(
					"The AbstractAtom<?> '" + this.getName() + "' has no parent. Could not get root.");
		} else {
			//get parent atom
			AbstractAtom<?> parent = (AbstractAtom<?>) parentNode.getAdaptable();

			//check if parent is root
			boolean parentIsRoot = parent.getName().equals("root");
			if (parentIsRoot) {
				return parent;
			} else {
				//continue with search
				return parent.getRoot();
			}
		}

	}

	//#end region

	//#region ATTRIBUTES

	/**
	 * Helper method that extracts a wrapped Attribute from a Wrap
	 *
	 * @param <T>
	 * @param wrappingAttribute
	 * @return
	 */
	protected static <T> Attribute<T> getWrappedAttribute(Attribute<T> wrappingAttribute) {
		Wrap<T> wrap = (Wrap<T>) wrappingAttribute;
		Attribute<T> attribute = wrap.getAttribute();
		return attribute;
	}

	/**
	 * Adds a modification listener to the attribute that is wrapped by the given wrapping attribute.
	 *
	 * @param <T>
	 * @param wrappingAttribute
	 * @param consumer
	 */
	protected <T>
			AbstractAtom<A>
			addModificationConsumer(String key, Attribute<T> wrappingAttribute, Consumer consumer) {
		Attribute<T> wrappedAttribute = getWrappedAttribute(wrappingAttribute);
		wrappedAttribute.addModificationConsumer(key, consumer);
		return this;
	}

	protected static <T>
			void
			addModificationConsumerStatic(String key, Attribute<T> wrappingAttribute, Consumer consumer) {
		Attribute<T> wrappedAttribute = getWrappedAttribute(wrappingAttribute);
		wrappedAttribute.addModificationConsumer(key, consumer);
	}

	/**
	 * Adds a modification listener to the attribute that is wrapped by the given wrapping attribute and executes it
	 * once
	 *
	 * @param <T>
	 * @param wrappingAttribute
	 * @param consumer
	 */
	protected static <T>
			void
			addModificationConsumerAndRun(String key, Attribute<T> wrappingAttribute, Consumer consumer) {
		Attribute<T> wrappedAttribute = getWrappedAttribute(wrappingAttribute);
		wrappedAttribute.addModificationConsumerAndRun(key, consumer);
	}

	//#end region

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the name of this atom (The name can also be accessed using the AtomTreeNodeAdaption.)
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this atom (The name can also be accessed using the AtomTreeNodeAdaption)
	 *
	 * @param name
	 */
	public A setName(String name) {
		boolean isDifferentName = (name != null && !name.equals(this.name)) || (name == null && this.name != null);
		if (isDifferentName) {
			this.name = name;
			triggerNameListeners(name);
		}
		return getThis();
	}

	/**
	 * Returns the parent AbstractAtom. Returns null if this AbstractAtom<?> has no parent AbstractAtom.
	 *
	 * @return
	 */
	public AbstractAtom<?> getParentAtom() {
		return parentAtom;
	}

	public void setParentAtom(AbstractAtom<?> parent) {
		this.parentAtom = parent;
	}

	public List<AbstractAtom<?>> getChildAtoms() {
		return children;
	}

	public String getHelpId() {
		return helpId;
	}

	public A setHelpId(String helpId) {
		this.helpId = helpId;
		return getThis();
	}

	//#end region

}
