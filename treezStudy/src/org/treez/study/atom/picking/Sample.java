package org.treez.study.atom.picking;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.adaptable.AbstractControlAdaption;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.CheckBox;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.variablefield.VariableField;
import org.treez.core.atom.variablelist.AbstractVariableListField;
import org.treez.core.atom.variablelist.DoubleVariableListField;
import org.treez.core.atom.variablelist.IntegerVariableListField;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.action.TreeViewerAction;
import org.treez.study.Activator;

/**
 * Represents a picking sample for a picking parameter variation.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Sample extends AdjustableAtom {

	//#region ATTRIBUTES

	/**
	 * The enabled state check box
	 */
	public Attribute<Boolean> enabled = new Wrap<>(new CheckBox("enabled", true));

	private Section pickingSection;

	/**
	 * Contains all data of the sample, maps from VariableField name to VariableField atom. The values are stored in the
	 * VariableField atoms.
	 */
	private Map<String, VariableField<?, ?>> variableData;

	/**
	 * Same as variableData, but for temporary data storage during update. The temporary map allows to circumvent issues
	 * with the order of the VariableField atoms: its easier to create a new ordered map than inserting new entries
	 * between existing old entries.
	 */
	private Map<String, VariableField<?, ?>> tempVariableData;

	/**
	 * Contains all time series data of the sample, maps from VariableField name to VariableListField atom. The values
	 * are stored in the VariableListField atoms.
	 */
	private Map<String, AbstractVariableListField<?, ?>> variableSeriesData;

	/**
	 * Same as variableListData, but for temporary data storage during update. The temporary map allows to circumvent
	 * issues with the order of the VariableListField atoms: its easier to create a new ordered map than inserting new
	 * entries between existing old entries.
	 */
	private Map<String, AbstractVariableListField<?, ?>> tempVariableSeriesData;

	//#region CONSTRUCTORS

	public Sample(String name) {
		super(name);
	}

	//#end region

	//#region METHODS

	@Override
	public AbstractControlAdaption createControlAdaption(
			Composite parent,
			FocusChangingRefreshable treeViewRefreshable) {
		updateSampleModel();
		AbstractControlAdaption controlAdaption = super.createControlAdaption(parent, treeViewRefreshable);
		return controlAdaption;
	}

	//#region CREATE & UPDATE MODEL

	/**
	 * Checks with VariableFields are defined by the parent Picking and updates the model of this picking Sample
	 * accordingly.
	 */
	private void updateSampleModel() {

		// root, page and section
		AttributeRoot root = new AttributeRoot("root");
		Page dataPage = root.createPage("data", "   Data   ");

		String relativeHelpContextId = "pickingSample";
		String absoluteHelpContextId = Activator.getInstance().getAbsoluteHelpContextId(relativeHelpContextId);

		pickingSection = dataPage.createSection("sample", absoluteHelpContextId);

		//create / update variable fields or variable list fields
		createOrUpdateVariableFields(pickingSection);

		createEnabledCheckBox();

		setModel(root);

	}

	private void createOrUpdateVariableFields(Section pickingSection) {

		Picking pickingParent = getPickingParent();
		boolean isTimeDependent = pickingParent.isTimeDependent.get();

		List<VariableField<?, ?>> variableFields = getVariableFieldsFromPickingParent();

		if (variableData == null) {
			variableData = new LinkedHashMap<>();
		}
		if (variableSeriesData == null) {
			variableSeriesData = new LinkedHashMap<>();
		}

		//transfer data from variableData to tempVariableData if data still
		//exists and create or update new variable fields
		tempVariableData = new LinkedHashMap<>();
		tempVariableSeriesData = new LinkedHashMap<>();
		for (VariableField<?, ?> variableField : variableFields) {
			if (variableField != null) {
				if (isTimeDependent) {
					String timeVariableName = getTimeVariableName(pickingParent);
					createOrUpdateVariableListFieldWithTempMap(variableField, timeVariableName);
				} else {
					createOrUpdateVariableFieldWithTempMap(variableField);
				}
			}
		}

		//update map and model from temporary (adapted) variable data
		variableData = new LinkedHashMap<>();
		variableSeriesData = new LinkedHashMap<>();

		if (isTimeDependent) {

			createTimeSeriesLabelField(pickingSection, pickingParent);

			for (String variableName : tempVariableSeriesData.keySet()) {
				AbstractVariableListField<?, ?> variableListField = tempVariableSeriesData.get(variableName);
				variableSeriesData.put(variableName, variableListField);
				pickingSection.addChild(variableListField);
			}

		} else {

			for (String variableName : tempVariableData.keySet()) {
				VariableField<?, ?> variableField = tempVariableData.get(variableName);
				variableData.put(variableName, variableField);
				AbstractAtom<?> variableFieldAtom = (AbstractAtom<?>) variableField;
				pickingSection.addChild(variableFieldAtom);
			}
		}

	}

	private static void createTimeSeriesLabelField(Section pickingSection, Picking pickingParent) {
		String timeVariableName = getTimeVariableName(pickingParent);
		List<Number> timeRange = pickingParent.getTimeRange();
		if (!timeRange.isEmpty()) {
			Number firstEntry = timeRange.get(0);
			boolean isInteger = Integer.class == firstEntry.getClass();
			if (isInteger) {
				IntegerVariableListField timeListField = new IntegerVariableListField(timeVariableName);
				timeListField.setEnabled(false);
				List<Integer> integerTimeRange = new ArrayList<>();
				for (Number number : timeRange) {
					Integer value = (Integer) number;
					integerTimeRange.add(value);
				}
				timeListField.set(integerTimeRange);
				pickingSection.addChild(timeListField);
				return;
			}

			boolean isDouble = Double.class == firstEntry.getClass();
			if (isDouble) {
				DoubleVariableListField timeListField = new DoubleVariableListField(timeVariableName);
				timeListField.setEnabled(false);
				List<Double> doubleTimeRange = new ArrayList<>();
				for (Number number : timeRange) {
					Double value = (Double) number;
					doubleTimeRange.add(value);
				}
				timeListField.set(doubleTimeRange);
				pickingSection.addChild(timeListField);
				return;
			}

			String message = "The type '" + firstEntry.getClass().getSimpleName() + "' is not yet implemented.";
			throw new IllegalStateException(message);

		} else {
			DoubleVariableListField timeListField = new DoubleVariableListField(timeVariableName);
			timeListField.setEnabled(false);
			pickingSection.addChild(timeListField);
		}

	}

	private static String getTimeVariableName(Picking pickingParent) {
		String timeVariablePath = pickingParent.timeVariableModelPath.get();
		String[] pathItems = timeVariablePath.split("\\.");
		String timeVariableName = pathItems[pathItems.length - 1];
		return timeVariableName;
	}

	private void createOrUpdateVariableFieldWithTempMap(VariableField<?, ?> variableFieldAtom) {
		String variableFieldName = variableFieldAtom.getName();
		boolean alreadyExists = variableData.containsKey(variableFieldName);
		if (!alreadyExists) {
			createVariableField(variableFieldAtom);
		} else {
			updateVariableField(variableFieldAtom);
		}
	}

	private void createVariableField(VariableField<?, ?> variableField) {
		AbstractAtom<?> variableFieldAtom = (AbstractAtom<?>) variableField;
		AbstractAtom<?> newVariableFieldAtom = variableFieldAtom.copy();
		VariableField<?, ?> newVariableField = (VariableField<?, ?>) newVariableFieldAtom;
		String variableFieldName = newVariableFieldAtom.getName();
		tempVariableData.put(variableFieldName, newVariableField);
	}

	private void updateVariableField(VariableField<?, ?> variableField) {
		String name = variableField.getName();
		VariableField<?, ?> oldVariableField = variableData.get(name);
		String newLabel = variableField.getLabel();
		oldVariableField.setLabel(newLabel);

		AbstractAtom<?> variableFieldAtom = (AbstractAtom<?>) oldVariableField;
		AbstractAtom<?> newVariableFieldAtom = variableFieldAtom.copy();
		VariableField<?, ?> newVariableField = (VariableField<?, ?>) newVariableFieldAtom;
		tempVariableData.put(name, newVariableField);
	}

	private void createOrUpdateVariableListFieldWithTempMap(
			VariableField<?, ?> variableFieldAtom,
			String timeVariableName) {
		String variableFieldName = variableFieldAtom.getName();
		boolean alreadyExists = variableSeriesData.containsKey(variableFieldName);
		if (!alreadyExists) {
			createVariableListField(variableFieldAtom, timeVariableName);
		} else {
			updateVariableListField(variableFieldAtom, timeVariableName);
		}
	}

	private void createVariableListField(VariableField<?, ?> variableField, String timeVariableName) {
		AbstractVariableListField<?, ?> newVariableListField = variableField.createVariableListField();
		String variableFieldName = variableField.getName();
		String label = variableFieldName + "(" + timeVariableName + ")";
		newVariableListField.setLabel(label);
		tempVariableSeriesData.put(variableFieldName, newVariableListField);
	}

	private void updateVariableListField(VariableField<?, ?> variableField, String timeVariableName) {
		String name = variableField.getName();
		AbstractVariableListField<?, ?> oldVariableListField = variableSeriesData.get(name);

		AbstractVariableListField<?, ?> newVariableListField = oldVariableListField.copy();
		String newLabel = variableField.getLabel();
		String label = newLabel + "(" + timeVariableName + ")";
		newVariableListField.setLabel(label);
		tempVariableSeriesData.put(name, newVariableListField);
	}

	private List<VariableField<?, ?>> getVariableFieldsFromPickingParent() {
		Picking picking = getPickingParent();
		List<VariableField<?, ?>> variableFields = picking.getPickingVariables();
		return variableFields;
	}

	private Picking getPickingParent() {
		AbstractAtom<?> parent = this.getParentAtom();
		checkIfParentIsPicking(parent);
		Picking picking = (Picking) parent;
		return picking;
	}

	private static void checkIfParentIsPicking(AbstractAtom<?> parent) {
		boolean parentIsPicking = parent instanceof Picking;
		if (!parentIsPicking) {
			String message = "Sample atoms must be used with a Picking parent.";
			throw new IllegalStateException(message);
		}
	}

	protected void createEnabledCheckBox() {
		CheckBox enabledCheck = pickingSection.createCheckBox(enabled, this, enabled.get());
		enabledCheck.addModificationConsumer("updateEnabledState", () -> {
			boolean enabledState = enabled.get();
			setEnabled(enabledState);
		});
	}

	//#end region

	/**
	 * Extends the context menu actions for this atom
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
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("pickingSample.png");
		Image image = decorateImageWidthEnabledState(baseImage);
		return image;
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

	//#region VARIABLE VALUES

	/**
	 * Sets the (sample-) value for the variable with the given name to the given value. Only specify the name of the
	 * variable, not its full path. The path to the source model has to be specified before using this method.
	 *
	 * @param variableName
	 * @param valueString
	 */
	public void setVariable(String variableName, String valueString) {
		if (variableData == null) {
			updateSampleModel();
		}

		VariableField<?, ?> variableField = variableData.get(variableName);
		if (variableField != null) {
			variableField.setValueString(valueString);
		} else {
			String message = "A variable with name '" + variableName + "' could not be found.";
			throw new IllegalStateException(message);
		}
	}

	/**
	 * Returns the variable data of the sample
	 *
	 * @return
	 */
	public Map<String, VariableField<?, ?>> getVariableData() {
		return variableData;
	}

	/**
	 * Returns the variable time series data of the sample
	 *
	 * @return
	 */
	public Map<String, AbstractVariableListField<?, ?>> getVariableSeriesData() {
		return variableSeriesData;
	}

	//#end region

	//#region ENABLED STATE

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

}
