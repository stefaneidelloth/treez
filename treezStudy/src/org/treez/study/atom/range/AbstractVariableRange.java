package org.treez.study.atom.range;

import java.util.List;
import java.util.Objects;

import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.CodeAdaption;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.CheckBox;
import org.treez.core.atom.attribute.ModelPath;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.base.AbstractStringAttributeAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablerange.VariableRange;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.AttributeWrapper;
import org.treez.core.attribute.Wrap;
import org.treez.core.scripting.ScriptType;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.core.utils.Utils;
import org.treez.study.Activator;
import org.treez.study.atom.Study;

/**
 * Parent class for variable ranges
 */
/**
 * @param <T>
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public abstract class AbstractVariableRange<T> extends AdjustableAtom implements VariableRange<T> {

	//#region ATTRIBUTES

	protected Section data;

	public Attribute<Boolean> enabled = new Wrap<>(new CheckBox("enabled", true));

	/**
	 * The absolute model path to the source variable (the atom control might only display the relative path in respect
	 * to the source model)
	 */
	public Attribute<String> sourceVariableModelPath = new Wrap<>();

	/**
	 * The full model path to the source model (is updated when setting the parent Study atom)
	 */
	protected String sourceModelModelPath = null;

	//#end region

	//#region CONSTRUCTORS

	public AbstractVariableRange(String name) {
		super(name);
		createVariableRangeModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the underlying model
	 */
	protected abstract void createVariableRangeModel();

	/**
	 * Adds additional actions to the context menu. Might be overridden by deriving classes.
	 *
	 * @param actions
	 * @param treeViewer
	 * @return
	 */
	@Override
	protected List<Object> extendContextMenuActions(
			List<Object> actions,
			@SuppressWarnings("unused") TreeViewerRefreshable treeViewer) {

		//disable
		if (enabled.get()) {
			actions.add(new TreeViewerAction(
					"Disable",
					org.treez.core.Activator.getImage("disable.png"),
					treeViewer,
					() -> setEnabled(false)));
		}

		//enable
		if (!enabled.get()) {
			actions.add(new TreeViewerAction(
					"Enable",
					org.treez.core.Activator.getImage("enable.png"),
					treeViewer,
					() -> setEnabled(true)));
		}

		return actions;
	}

	/**
	 * Changes the model path selection for the source variable to use the source model as relative root
	 */
	protected void assignRealtiveRootToSourceVariablePath() {
		Objects.requireNonNull(sourceModelModelPath, "Source model path must not be null when calling this function.");
		data.setLabel("Data for " + sourceModelModelPath);
		AbstractAtom<?> relativeRootAtom = this.getChildFromRoot(sourceModelModelPath);
		AttributeWrapper<String> pathWrapper = (AttributeWrapper<String>) sourceVariableModelPath;
		ModelPath modelPath = (ModelPath) pathWrapper.getAttribute();
		modelPath.setModelRelativeRoot(relativeRootAtom);
	}

	@Override
	public void setParentAtom(AbstractAtom<?> parent) {
		super.setParentAtom(parent);
		checkParentAndUpdateSourceModel(parent);
	}

	/**
	 * Returns the code adaption
	 */
	@Override
	public CodeAdaption createCodeAdaption(ScriptType scriptType) {

		CodeAdaption codeAdaption;
		switch (scriptType) {
		case JAVA:
			codeAdaption = new VariableRangeCodeAdaption(this);
			break;
		default:
			String message = "The ScriptType " + scriptType + " is not known.";
			throw new IllegalStateException(message);
		}
		return codeAdaption;
	}

	/**
	 * Checks if the parent is a study, gets the source model from it and updates the source model for this
	 * VariableRange
	 *
	 * @param parent
	 */
	public void checkParentAndUpdateSourceModel(AbstractAtom<?> parent) {

		if (parent == null) {
			// throw exception if the parent is null
			String message = "The parent of " + this.getName() + " must not be null";
			throw new IllegalStateException(message);
		}

		String wantedTypeName = org.treez.study.atom.Study.class.getName();
		boolean hasWantedType = Utils.checkIfHasWantedType(parent, wantedTypeName);
		if (hasWantedType) {
			// update source model
			Study study = (Study) parent;
			updateSourceModel(study);
		} else {
			// throw exception if parent is no Study
			String message = "The parent '" + parent.getName() + "' is not a valid parent for the "
					+ this.getClass().getSimpleName() + " '" + this.getName()
					+ "' since it does not implement the interface " + wantedTypeName;
			throw new IllegalStateException(message);
		}

	}

	/**
	 * Updates the source model
	 *
	 * @param parent
	 */
	private void updateSourceModel(Study parentStudy) {
		sourceModelModelPath = parentStudy.getSourceModelPath();
		boolean assignRelativeRoot = sourceModelModelPath != null && !sourceModelModelPath.isEmpty();
		if (assignRelativeRoot) {
			assignRealtiveRootToSourceVariablePath();
		}
	}

	protected void createEnabledCheckBox() {
		// enabled state
		CheckBox enabledCheck = data.createCheckBox(enabled, this, enabled.get());
		enabledCheck.addModificationConsumer("updateEnabledState", () -> {
			boolean enabledState = enabled.get();
			setEnabled(enabledState);
		});
	}

	/**
	 * Creates an image that is decorated with the enabled state and based on the given base image
	 *
	 * @param baseImage
	 * @return
	 */
	protected Image decorateImageWidthEnabledState(Image baseImage) {
		Image image;
		if (enabled.get()) {
			String overlayImageName = "enabledDecoration.png";
			image = Activator.getOverlayImageStatic(baseImage, overlayImageName);
		} else {
			String overlayImageName = "disabledDecoration.png";
			image = Activator.getOverlayImageStatic(baseImage, overlayImageName);
		}
		return image;
	}

	//#end region

	//#region ACCESSORS

	//#region RANGE VALUES

	/**
	 * Returns the range values as a list
	 *
	 * @return
	 */
	@Override
	public abstract List<T> getRange();

	/**
	 * Sets the range with a list of individual values
	 */
	@SuppressWarnings("unchecked")
	protected abstract void setRange(T... rangeValues);

	/**
	 * Sets the range with a comma separated string
	 */
	protected abstract void setRangeValueString(String rangeString);

	//#end region

	//#region SOURCE VARIABLE

	public void setSourceVariableModelPath(String sourceVariableModelPath) {
		this.sourceVariableModelPath.set(sourceVariableModelPath);
	}

	public void setRelativeSourceVariableModelPath(String relativePath) {
		if (sourceModelModelPath == null) {
			String message = "The source model path must not be null when calling this method. "
					+ "Please ensure that this VariableRange has a valid parent atom that "
					+ "provides the source model path before calling this method.";
			throw new IllegalStateException(message);
		}
		String absoluteSourceModelModelPath = sourceModelModelPath + "." + relativePath;
		sourceVariableModelPath.set(absoluteSourceModelModelPath);

		//trigger modification listeners
		Wrap<String> wrap = (Wrap<String>) sourceVariableModelPath;
		Attribute<String> attribute = wrap.getAttribute();
		AbstractStringAttributeAtom<?> attributeAtom = (AbstractStringAttributeAtom<?>) attribute;
		attributeAtom.triggerListeners();
	}

	public String getRelativeSourceVariableModelPath() {
		if (sourceModelModelPath == null) {
			String message = "The source model path must not be null when calling this method. "
					+ "Please ensure that this VariableRange has a valid parent atom that "
					+ "provides the source model path before calling this method.";
			throw new IllegalStateException(message);
		}

		int prefixLength = sourceModelModelPath.length();

		String relativePath = sourceVariableModelPath.get().substring(prefixLength + 1);

		return relativePath;
	}

	/**
	 * Returns the model path for the variable that is controlled with this range
	 */
	public String getSourceVariableModelPath() {
		return sourceVariableModelPath.get();
	}

	//#end region

	//#region ENABLED STATE

	/**
	 * Sets the enabled state
	 *
	 * @param enabledState
	 */
	public void setEnabled(boolean enabledState) {
		if (enabled.get() != enabledState) {
			enabled.set(enabledState);
		}
		//update tree view to show new overlay icon
		if (treeViewRefreshable != null) {
			treeViewRefreshable.refresh();
		}
	}

	//#end region

	//#end region

	//#end region

}
