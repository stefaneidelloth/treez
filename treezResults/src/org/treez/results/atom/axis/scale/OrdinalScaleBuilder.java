package org.treez.results.atom.axis.scale;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.treez.javafxd3.d3.scales.OrdinalScale;
import org.treez.javafxd3.d3.scales.Scales;

public class OrdinalScaleBuilder {

	//#region ATTRIBUTES

	private OrdinalScale scale;

	private Set<String> ordinalValues = new HashSet<>();

	//#end region

	//#region CONSTRUCTORS

	public OrdinalScaleBuilder() {}

	//#end region

	//#region METHODS

	public void createScale(Scales scales, boolean isHorizontal, Double graphWidthInPx, Double graphHeightInPx) {
		scale = scales.ordinal();
		createRange(isHorizontal, graphWidthInPx, graphHeightInPx);
		updateDomain();
	}

	private void createRange(boolean isHorizontal, Double graphWidthInPx, Double graphHeightInPx) {
		if (isHorizontal) {
			scale.rangeRoundPoints(0.0, graphWidthInPx, 1);
		} else {
			scale.rangeRoundPoints(graphHeightInPx, 0.0, 1);
		}
	}

	private void updateDomain() {
		if (scale != null) {
			String[] values = ordinalValues.toArray(new String[ordinalValues.size()]);
			scale.domain(values);
		}
	}

	//#end region

	//#region ACCESSORS

	public OrdinalScale getScale() {
		return scale;
	}

	public int getSize() {
		return scale.domain().sizes().get(0);
	}

	public Set<String> getValues() {
		return ordinalValues;
	}

	public void setValues(Set<String> ordinalValues) {
		this.ordinalValues = ordinalValues;
		updateDomain();
	}

	public void setValues(String... ordinalValues) {
		Set<String> values = new HashSet<String>(Arrays.asList(ordinalValues));
		setValues(values);
	}

	public void addValue(String ordinalValue) {
		ordinalValues.add(ordinalValue);
		updateDomain();
	}

	public void removeOrdinalValue(String ordinalValue) {
		ordinalValues.remove(ordinalValue);
		updateDomain();
	}

	//#end region

}
