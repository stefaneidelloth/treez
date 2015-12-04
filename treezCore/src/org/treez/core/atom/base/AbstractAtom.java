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
import org.treez.core.adaptable.GraphicsAdaption;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.adaptable.TreeNodeAdaption;
import org.treez.core.atom.base.annotation.IsParameters;
import org.treez.core.atom.copy.Copiable;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.core.utils.Utils;

/**
 * This is the main implementation of the interface Adaptable and the parent
 * class for all treez atoms. An AbstractAtom (actually its TreeNodeAdaption)
 * has parents and children. The ControlAdaption of this AbstractAtom is build
 * with the help of the annotation "IsParameter", see the class
 * AtomControlAdaption for more details. * The AttributeAtom and its deriving
 * classes give an example on how to use these annotations. * If you want to
 * create more complex atoms, also have a look at the AdjustableAtom. The
 * ControlAdaption of AdjustableAtoms is created from an underlying tree model.
 * Each implementation of this AbstractAtom should define a copy constructor and
 * use it in the method copy() that must be overridden.
 */
public abstract class AbstractAtom
		implements
			Adaptable,
			Copiable<AbstractAtom> {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(AbstractAtom.class);

	//#region ATTRIBUTES

	/**
	 * The name of this AbstractAtom. This name will for example be used by the
	 * TreeNodeAdaption. (In order to be able to identify an AbstractAtom by its
	 * tree path, this name should only be used once for all children of the
	 * parent AbstractAtom.)
	 */
	protected String name;

	/**
	 * The parent of this AbstractAtom
	 */
	protected AbstractAtom parentAtom;

	/**
	 * The children of this AbstractAtom
	 */
	protected List<AbstractAtom> children;

	/**
	 * The context menu actions of this AbstractAtom
	 */
	protected List<IAction> contextMenuActions;

	/**
	 * Used to save the expansion state (has to be set from outside)
	 */
	protected ArrayList<String> expandedNodes = new ArrayList<>();

	/**
	 * Default key for an image that represents this AbstractAtom in a tree view
	 */
	protected String IMAGE_KEY = ISharedImages.IMG_OBJ_ELEMENT;

	/**
	 * The help id that can be used do access dynamic help for the atom
	 */
	protected String helpId = "org.treez.core.UNDEFINED_HELP_ID";

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor. If a derived class uses the annotation IsParameter for its
	 * attributes, the attributes are initialized using the value provided by
	 * that annotation.
	 *
	 * @param name
	 */
	public AbstractAtom(String name) {
		//sysLog.debug("Creating abstract atom " + name);
		this.name = name;
		this.children = new ArrayList<AbstractAtom>();
		initAttributesWithIsParameterAnnotationValues();
	}

	/**
	 * Copy Constructor
	 *
	 * @param abstractAtomToCopy
	 */
	public AbstractAtom(AbstractAtom abstractAtomToCopy) {
		this.name = abstractAtomToCopy.name;
		this.children = copyAbstractAtoms(abstractAtomToCopy.children);
		this.expandedNodes = abstractAtomToCopy.expandedNodes;
		initAttributesWithIsParameterAnnotationValues();
	}

	//#end region

	//#region METHODS

	//#region COPY

	/**
	 * Copies the abstract atom
	 *
	 * @return
	 */
	@Override
	public abstract AbstractAtom copy();

	/**
	 * Copies the given list of abstract atoms
	 *
	 * @param abstractAtomsToCopy
	 * @return
	 */
	public static List<AbstractAtom> copyAbstractAtoms(
			List<AbstractAtom> abstractAtomsToCopy) {
		List<AbstractAtom> abstractAtoms = new ArrayList<>(
				abstractAtomsToCopy.size());
		for (AbstractAtom abstractAtomToCopy : abstractAtomsToCopy) {
			AbstractAtom abstractAtom = abstractAtomToCopy.copy();
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
			case JAVA :
				codeAdaption = new AtomCodeAdaption(this);
				break;
			default :
				String message = "The ScriptType " + scriptType
						+ " is not yet implemented.";
				throw new IllegalStateException(message);
		}

		return codeAdaption;

	}

	@Override
	public GraphicsAdaption createGraphicsAdaption(Composite parent) {
		GraphicsAdaption graphicsAdaption = new AtomGraphicsAdaption(parent,
				this);
		return graphicsAdaption;
	}

	//#end region

	//#region initialization of attributes with the annotation IsParameter

	/**
	 * If a derived class uses the IsParameter annotation for some of its
	 * attributes, the values of those attributes are initialized with this
	 * method, using the default values that are provided by the IsParameter
	 * annotation. If the class itself does not use IsParameter annotations, but
	 * its direct super class does, use the annotations of the direct super
	 * class to initialize the attributes.
	 */
	protected void initAttributesWithIsParameterAnnotationValues() {

		Class<?> atomClass = this.getClass();

		boolean foundAnIsParameterAnnotation = initAttributesWithAnnotationsForClass(
				atomClass);

		if (!foundAnIsParameterAnnotation) {
			//No IsParameter annotation has been found. Maybe it is a class
			//that derives
			//from an attribute atom and has no parameter annotations itself.
			//In that case, try to use the annotations of the super class to
			//initialize the
			//attributes.
			Class<?> superClass = atomClass.getSuperclass();
			foundAnIsParameterAnnotation = initAttributesWithAnnotationsForClass(
					superClass);

			if (!foundAnIsParameterAnnotation) {

				Class<?> superSuperClass = atomClass.getSuperclass();
				foundAnIsParameterAnnotation = initAttributesWithAnnotationsForClass(
						superSuperClass);
			}
		}

	}

	/**
	 * Initializes the values of the attributes that are annotated with the
	 * IsParameter annotation using the given class. If an IsParameter
	 * annotation has been found, the method returns true.
	 *
	 * @param atomClass
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private boolean initAttributesWithAnnotationsForClass(Class<?> atomClass) {
		boolean foundAnIsParameterAnnotation = false;
		Field[] attributes = atomClass.getDeclaredFields();
		for (Field attribute : attributes) {
			//sysLog.debug("Existing attribute: " + attribute.getName());
			attribute.setAccessible(true);
			boolean isParameterAnnotated = IsParameters.isAnnotated(attribute);
			if (isParameterAnnotated) {
				foundAnIsParameterAnnotation = true;
				//sysLog.debug("The field " + field.getName() + " is
				//annotated.");
				String valueString = IsParameters
						.getDefaultValueString(attribute);
				try {
					Object attributeParent = this;
					IsParameters.setAttributeValue(attribute, attributeParent,
							valueString);
				} catch (Exception exception) {
					String message = "Could not set attribute value for "
							+ attribute.getName();
					sysLog.error(message, exception);
					throw new IllegalStateException(message, exception);
				}
			}
		}
		return foundAnIsParameterAnnotation;
	}

	//#end region

	//#region actions

	/**
	 * Provide an execution method for this AbstractAtom. This method might be
	 * overridden be deriving classes and does nothing by default. (This method
	 * can be triggered for example with a button in the tree view.)
	 */
	@SuppressWarnings("unused")
	public void execute(Refreshable treeViewerRefreshable) {
		//empty default implementation
	}

	/**
	 * Executes all children that have the given class
	 *
	 * @param wantedClass
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	protected void executeChildren(Class<?> wantedClass,
			Refreshable treeViewerRefreshable) throws IllegalArgumentException {
		for (AbstractAtom child : children) {
			Class<?> currentClass = child.getClass();
			boolean hasWantedClass = currentClass.equals(wantedClass);
			if (hasWantedClass) {
				try {
					child.execute(treeViewerRefreshable);
				} catch (Exception exception) {
					String message = "Could not execute child '"
							+ child.getName() + "' of '" + getName() + "'.";
					sysLog.error(message, exception);
				}
			}
		}
	}

	/**
	 * Default implementation for the creation of the context menu actions. This
	 * method is used by the corresponding AtomTreeNodeAdaption to fill its
	 * context menu. This default implementation only includes a dummy example
	 * action. It should be overridden by inheriting classes.
	 *
	 * @param treeViewerRefreshable
	 * @return
	 */
	protected List<Object> createContextMenuActions(
			TreeViewerRefreshable treeViewerRefreshable) {

		ArrayList<Object> actions = new ArrayList<>();

		//rename
		actions.add(
				new TreeViewerAction("Rename", Activator.getImage("rename.png"),
						treeViewerRefreshable, () -> rename()));

		//move up
		boolean canBeMovedUp = canBeMovedUp();
		if (canBeMovedUp) {
			actions.add(new TreeViewerAction("Move up",
					Activator.getImage("up.png"), treeViewerRefreshable,
					() -> moveUp()));
		}

		//move down
		boolean canBeMovedDown = canBeMovedDown();
		if (canBeMovedDown) {
			actions.add(new TreeViewerAction("Move down",
					Activator.getImage("down.png"), treeViewerRefreshable,
					() -> moveDown()));
		}

		//delete
		actions.add(new TreeViewerAction("Delete",
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
		AbstractAtom parent = this.getParentAtom();
		if (parent != null) {
			List<AbstractAtom> currentChildren = parent.getChildAtoms();
			boolean childrenExist = currentChildren != null
					&& currentChildren.size() > 1;
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
	private void moveUp() {
		boolean canBeMovedUp = canBeMovedUp();
		if (canBeMovedUp) {
			AbstractAtom parent = this.getParentAtom();
			List<AbstractAtom> currentChildren = parent.getChildAtoms();
			int currentIndex = currentChildren.indexOf(this);
			Collections.swap(currentChildren, currentIndex, currentIndex - 1);
			tryToRefreshAtom(parent);
		}
	}

	/**
	 * Returns true if this atom can be moved down in the children of its parent
	 *
	 * @return
	 */
	private boolean canBeMovedDown() {
		AbstractAtom parent = this.getParentAtom();
		if (parent != null) {
			List<AbstractAtom> currentChildren = parent.getChildAtoms();
			boolean childrenExist = currentChildren != null
					&& currentChildren.size() > 1;
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
	private void moveDown() {
		boolean canBeMovedDown = canBeMovedDown();
		if (canBeMovedDown) {
			AbstractAtom parent = this.getParentAtom();
			List<AbstractAtom> currentChildren = parent.getChildAtoms();
			int currentIndex = currentChildren.indexOf(this);
			Collections.swap(currentChildren, currentIndex + 1, currentIndex);
			tryToRefreshAtom(parent);
		}
	}

	/**
	 * Refreshes the given AbstractAtom if it implements the interface
	 * Refreshable
	 *
	 * @param parent
	 */
	private static void tryToRefreshAtom(AbstractAtom parent) {
		boolean parentIsRefreshable = Refreshable.class
				.isAssignableFrom(parent.getClass());
		if (parentIsRefreshable) {
			Refreshable refreshableParent = (Refreshable) parent;
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
		String newName = Utils.getInput("Please enter the new name:",
				getName());
		setName(newName);
	}

	//#end region

	//#region image

	/**
	 * Returns the image for this AbstractAtom. It is used by the corresponding
	 * AtomTreeNodeAdaption. This method might be overridden by deriving
	 * classes. This default implementation returns the default image that is
	 * defined by the IMAGE_KEY.
	 *
	 * @return
	 */
	public Image provideImage() {
		return Activator.getImage(IMAGE_KEY);
	}

	//#end region

	//#region child operations

	/**
	 * Add the given AbstractAtom as a child and removes it from the old parent
	 * if an old parent exists.
	 *
	 * @param child
	 */
	public void addChild(AbstractAtom child) {
		//sysLog.debug("add child to " + getName());
		AbstractAtom oldParent = child.getParentAtom();
		child.setParentAtom(this);
		children.add(child);
		if (oldParent != null) {
			//remove child from old parent
			oldParent.createTreeNodeAdaption()
					.removeChild(child.createTreeNodeAdaption());
		}
	}

	/**
	 * Adds the given AbstractAtom as a child but does not set the parent of the
	 * child. The given AbstractAtom will be listed as a child of this
	 * AbstractAtom. If the given AbstractAtom is asked for its parent, the old
	 * parent will be returned. This way, an AbstractAtom can be used in several
	 * trees as a child while the "one and only real parent" is kept.
	 *
	 * @param child
	 */
	public void addChildReference(AbstractAtom child) {
		children.add(child);
	}

	/**
	 * Get child atom with given child name/sub model path. Throws an
	 * IllegalArgumentException if the child could not be found.
	 *
	 * @param childPath
	 * @return
	 * @throws IllegalArgumentException
	 */
	public AbstractAtom getChild(String childPath)
			throws IllegalArgumentException {

		boolean isPath = childPath.contains(".");

		if (isPath) {
			//iterate through path to get wanted child
			String[] childNames = childPath.split("\\.");
			String firstName = childNames[0];
			AbstractAtom child = getChildByName(firstName);
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
	 * Get child atom with given child tree path. Throws an
	 * IllegalArgumentException if the child tree path can not be found.
	 *
	 * @param childPathStartingWithRoot
	 * @return
	 */
	public AbstractAtom getChildFromRoot(String childPathStartingWithRoot)
			throws IllegalArgumentException {

		final int rootLength = 5; //"root."
		boolean isTooShort = childPathStartingWithRoot.length() < rootLength
				+ 1;
		if (isTooShort) {
			throw new IllegalArgumentException(
					"The path has to start with 'root.' but is '"
							+ childPathStartingWithRoot + "'.");
		}

		boolean startsWithRoot = childPathStartingWithRoot
				.substring(0, rootLength).equals("root.");

		if (startsWithRoot) {
			int length = childPathStartingWithRoot.length();
			String childPath = childPathStartingWithRoot.substring(rootLength,
					length);
			AbstractAtom root = getRoot();
			return root.getChild(childPath);
		} else {
			throw new IllegalArgumentException(
					"The path has to start with 'root.' but is '"
							+ childPathStartingWithRoot + "'.");
		}

	}

	/**
	 * Creates a child atom of the given class with the given name prefix.
	 *
	 * @param atomClass
	 * @param namePrefix
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public void createChildAtom(Class<? extends AbstractAtom> atomClass,
			String namePrefix) {
		Objects.requireNonNull(atomClass, "Atom class must not be null");
		Objects.requireNonNull(namePrefix, "Name prefix must not be null");

		String newName = AtomTreeNodeAdaption.createChildNameStartingWith(this,
				namePrefix);
		AbstractAtom newChild;
		try {
			Constructor<? extends AbstractAtom> atomConstructor = atomClass
					.getConstructor(new Class[]{String.class});
			newChild = atomConstructor.newInstance(new Object[]{newName});
		} catch (Exception exception) {
			String message = "Could not create child atom for class "
					+ atomClass.getSimpleName();
			sysLog.error(message, exception);
			throw new IllegalArgumentException(message, exception);
		}

		addChild(newChild);
	}

	/**
	 * Returns true if the root of this atom has a child at the given child
	 * path.
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
	 * Gets the first child atom with the given name. Throws an
	 * IllegalArgumentException if the child could not be found.
	 *
	 * @param childName
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected AbstractAtom getChildByName(String childName)
			throws IllegalArgumentException {
		for (AbstractAtom currentChild : children) {
			boolean isWantedChild = currentChild.getName().equals(childName);
			if (isWantedChild) {
				return currentChild;
			}
		}
		throw new IllegalArgumentException(
				"Could not find child '" + childName + "' in '" + name + "'.");
	}

	/**
	 * Gets the first child atom with the given class. Throws an
	 * IllegalArugmentException if the child could not be found.
	 *
	 * @param clazz
	 * @return
	 */
	public <T> T getChildByClass(Class<T> clazz) {

		for (AbstractAtom currentChild : children) {
			boolean isWantedChild = currentChild.getClass().equals(clazz);
			if (isWantedChild) {
				@SuppressWarnings("unchecked")
				T castedChild = (T) currentChild;
				return castedChild;
			}
		}
		throw new IllegalArgumentException("Could not find a child with class'"
				+ clazz.getSimpleName() + "' in '" + name + "'.");
	}

	/**
	 * Checks if any of the children, sub children and so on is of the class
	 * with the given name
	 *
	 * @param targetClassName
	 * @return
	 */
	public boolean containsChildOfType(String targetClassName) {

		//check if any of the children has the wanted type
		for (AbstractAtom currentChild : children) {
			boolean hasWantedType = Utils.checkIfHasWantedType(currentChild,
					targetClassName);
			if (hasWantedType) {
				return true;
			}
		}

		//go on and check if any of the children of the children has the wanted
		//type
		for (AbstractAtom currentChild : children) {
			boolean hasWantedType = Utils.checkIfHasWantedType(currentChild,
					targetClassName);
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
	 * Returns the root atom of the tree this atom is included in. Returns null
	 * if the parent node of this atom is null.
	 *
	 * @return
	 */
	public AbstractAtom getRoot() {

		//get parent node
		TreeNodeAdaption parentNode = this.createTreeNodeAdaption().getParent();

		if (parentNode == null) {
			throw new IllegalStateException("The AbstractAtom '"
					+ this.getName() + "' has no parent. Could not get root.");
		} else {
			//get parent atom
			AbstractAtom parent = (AbstractAtom) parentNode.getAdaptable();

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
	protected static <T> Attribute<T> getWrappedAttribute(
			Attribute<T> wrappingAttribute) {
		Wrap<T> wrap = (Wrap<T>) wrappingAttribute;
		Attribute<T> attribute = wrap.getAttribute();
		return attribute;
	}

	//#end region

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the name of this atom (The name can also be accessed using the
	 * AtomTreeNodeAdaption.)
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this atom (The name can also be accessed using the
	 * AtomTreeNodeAdaption)
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the parent AbstractAtom. Returns null if this AbstractAtom has no
	 * parent AbstractAtom.
	 *
	 * @return
	 */
	public AbstractAtom getParentAtom() {
		return parentAtom;
	}

	/**
	 * Sets the parent atom
	 *
	 * @param parent
	 */
	public void setParentAtom(AbstractAtom parent) {
		this.parentAtom = parent;
	}

	/**
	 * Returns the child AbstractAtoms
	 *
	 * @return
	 */
	public List<AbstractAtom> getChildAtoms() {
		return children;
	}

	/**
	 * Sets the help id
	 *
	 * @param helpId
	 */
	public void setHelpId(String helpId) {
		this.helpId = helpId;
	}

	/**
	 * Returns the help id
	 *
	 * @return
	 */
	public String getHelpId() {
		return helpId;
	}

	//#end region

}
