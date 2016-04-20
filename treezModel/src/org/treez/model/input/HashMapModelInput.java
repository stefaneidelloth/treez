package org.treez.model.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.treez.core.atom.copy.Copiable;

/**
 * A model input whose entries are backed up by a HashMap
 */
public class HashMapModelInput implements ModelInput {

	//#region ATTRIBUTES

	/**
	 * The id that will be used for the next created HashMapModelInput; will be increased when creating a new
	 * HashMapModelInput
	 */
	private static long nextId = 1;

	/**
	 * The id of this model input
	 */
	private long id;

	/**
	 * Maps from model path of variable to corresponding Quantity to set
	 */
	private Map<String, java.lang.Object> modelInputMap = null;

	/**
	 * The model path of the parent study
	 */
	private String parentStuyModelPath = null;

	//#end region

	//#region CONSTRUCTORS

	public HashMapModelInput(String parentStuyModelPath) {
		this.id = getNextId();
		modelInputMap = new HashMap<>();
		this.parentStuyModelPath = parentStuyModelPath;
	}

	/**
	 * Copy constructor. Copies the data of the given HashMapModelInput to a new HashMapModelInput. You might want to
	 * use the function increaseId to increase the id of the new model input to the next available value.
	 *
	 * @param hashMapModelInputToCopy
	 */
	private HashMapModelInput(HashMapModelInput hashMapModelInputToCopy) {
		modelInputMap = copyInputMap(hashMapModelInputToCopy.modelInputMap);
		this.id = hashMapModelInputToCopy.id;
		this.parentStuyModelPath = hashMapModelInputToCopy.parentStuyModelPath;
	}

	//#end region

	//#region METHODS

	/**
	 * Returns the next id and increases the id counter.
	 *
	 * @return
	 */
	private static long getNextId() {
		long currentId = nextId;
		nextId++;
		return currentId;
	}

	public static void resetIdCounter() {
		nextId = 1;
	}

	@Override
	public void increaseId() {
		this.id = getNextId();
	}

	@Override
	public HashMapModelInput copy() {
		HashMapModelInput modelInput = new HashMapModelInput(this);
		return modelInput;
	}

	/**
	 * Deeply copies the given model input map
	 *
	 * @param modelInputMapToCopy
	 * @return
	 */
	private static Map<String, Object> copyInputMap(Map<String, Object> modelInputMapToCopy) {
		Map<String, Object> modelInput = new HashMap<>();
		for (String modelPath : modelInputMapToCopy.keySet()) {
			Object rangeObject = modelInputMapToCopy.get(modelPath);

			Object copiedObject = null;
			boolean isCopiable = Copiable.class.isAssignableFrom(rangeObject.getClass());
			if (isCopiable) {
				Copiable<?> copiableRangeObject = (Copiable<?>) rangeObject;
				copiedObject = copiableRangeObject.copy();
			} else {
				copiedObject = rangeObject;
			}
			modelInput.put(modelPath, copiedObject);
		}
		return modelInput;
	}

	@Override
	public void add(String variableModelPath, java.lang.Object quantity) {
		modelInputMap.put(variableModelPath, quantity);
	}

	@Override
	public boolean containsVariableModelPath(String variableModelPath) {
		boolean pathExists = modelInputMap.keySet().contains(variableModelPath);
		return pathExists;
	}

	//#end region

	//#region ACCESSORS

	@Override
	public String getId() {
		String idString = "" + id;
		return idString;
	}

	@Override
	public String getParentStudyModelPath() {
		return parentStuyModelPath;
	}

	@Override
	public Object getVariableValue(String variableModelPath) {
		Object quantity = modelInputMap.get(variableModelPath);
		return quantity;
	}

	@Override
	public List<String> getAllVariableModelPaths() {
		List<String> allPaths = new ArrayList<>(modelInputMap.keySet());
		return allPaths;
	}

	//#end region

}
