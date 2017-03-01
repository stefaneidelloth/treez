package org.treez.results.atom.axis.scale;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.treez.javafxd3.d3.arrays.Array;
import org.treez.javafxd3.d3.core.Value;
import org.treez.javafxd3.d3.scales.QuantitativeScale;
import org.treez.javafxd3.d3.scales.Scales;
import org.treez.results.atom.axis.Axis;
import org.treez.results.atom.axis.BorderMode;

public class QuantitativeScaleBuilder {

	//#region ATTRIBUTES

	private Axis parent;

	private QuantitativeScale<?> scale;

	/**
	 * For determining automatic min & max values
	 */
	private TreeSet<Double> dataForAutoScale = new TreeSet<>();

	//#end region

	//#region CONSTRUCTORS

	public QuantitativeScaleBuilder(Axis parent) {
		this.parent = parent;
	}

	//#end region

	//#region METHODS

	public void createScale(Scales scaleFactory, Double graphWidthInPx, Double graphHeightInPx) {

		if (parent.data.log.get()) {
			scale = scaleFactory //
					.log() //
					.clamp(true);

		} else {
			scale = scaleFactory //
					.linear() //
					.clamp(true);
		}

		createRange(graphWidthInPx, graphHeightInPx);
		updateManualLimits();
		updateAutoLimits();
	}

	private void createRange(Double graphWidthInPx, Double graphHeightInPx) {
		if (parent.data.isHorizontal()) {
			scale.range(0.0, graphWidthInPx);
		} else {
			scale.range(graphHeightInPx, 0.0);
		}
	}

	private void updateManualLimits() {
		if (scale == null) {
			return;
		}

		if (!parent.data.autoMin.get()) {
			Double minValue = correctMinIfLogScaleAndZero(parent.data.min.get());
			setMinScaleValue(minValue);
		}

		if (!parent.data.autoMax.get()) {
			setMaxScaleValue(parent.data.max.get());
		}
	}

	private void updateAutoLimits() {
		if (scale == null) {
			return;
		}

		if (parent.data.autoMin.get()) {
			updateAutoMinValue();
		}

		if (parent.data.autoMax.get()) {
			updateAutoMaxValue();
		}
	}

	private void setMinScaleValue(Double min) {
		Array<Value> oldDomain = scale.domain();
		Double oldMax = oldDomain.get(1, Value.class).asDouble();
		scale.domain(min, oldMax);

	}

	private void setMaxScaleValue(Double max) {
		Array<Value> oldDomain = scale.domain();
		Double oldMin = oldDomain.get(0, Value.class).asDouble();
		scale.domain(oldMin, max);

	}

	private void updateAutoMinValue() {
		Double correctedMin = getAutoMinValue();
		setMinScaleValue(correctedMin);
	}

	private Double determineAutoMinValue() {
		boolean autoDataExists = dataForAutoScale.size() > 0;
		if (autoDataExists) {
			Double minValue = dataForAutoScale.first();
			Double maxValue = dataForAutoScale.last();
			Double delta = maxValue - minValue;
			BorderMode minBorderMode = BorderMode.from(parent.data.borderMin.get());
			Double borderFactor = minBorderMode.getFactor();
			Double autoMinValue = minValue - borderFactor * delta;
			return autoMinValue;
		}
		return 0.0;
	}

	private void updateAutoMaxValue() {
		Double autoMaxValue = determineAutoMaxValue();
		setMaxScaleValue(autoMaxValue);
	}

	private Double determineAutoMaxValue() {
		boolean autoDataExists = dataForAutoScale.size() > 0;
		if (autoDataExists) {
			Double minValue = dataForAutoScale.first();
			Double maxValue = dataForAutoScale.last();
			Double delta = maxValue - minValue;
			BorderMode maxBorderMode = BorderMode.from(parent.data.borderMax.get());
			Double borderFactor = maxBorderMode.getFactor();
			Double autoMaxValue = maxValue + borderFactor * delta;
			return autoMaxValue;
		}
		return 0.0;
	}

	private Double correctMinIfLogScaleAndZero(Double value) {
		if (!parent.data.log.get()) {
			return value;
		} else {
			if (value == null || value.compareTo(0.0) == 0) {
				final double smallValueNextToZero = 1e-10;
				return smallValueNextToZero;
			} else {
				return value;
			}
		}
	}

	private static List<Double> getMinAndMax(Collection<Double> dataForAutoScale) {
		TreeSet<Double> treeSet = new TreeSet<>();
		treeSet.addAll(dataForAutoScale);
		return Arrays.asList(treeSet.first(), treeSet.last());
	}

	//#end region

	//#region ACCESSORS

	public QuantitativeScale<?> getScale() {
		return scale;
	}

	public void setDataForAutScale(Collection<Double> dataForAutoScale) {
		List<Double> minAndMax = getMinAndMax(dataForAutoScale);
		this.dataForAutoScale.clear();
		this.dataForAutoScale.addAll(minAndMax);
		updateAutoLimits();
	}

	public void includeDataForAutScale(Collection<Double> dataForAutoScale) {
		List<Double> minAndMax = getMinAndMax(dataForAutoScale);
		this.dataForAutoScale.addAll(minAndMax);
		updateAutoLimits();
	}

	public void includeForAutScale(Double valueToInclude) {
		boolean added = dataForAutoScale.add(valueToInclude);
		if (added) {
			updateAutoLimits();
		}
	}

	public void clearDataForAutoScale() {
		dataForAutoScale.clear();
		updateAutoLimits();
	}

	public void excludeForAutScale(Double valueToExclude) {
		boolean removed = this.dataForAutoScale.remove(valueToExclude);
		if (removed) {
			updateAutoLimits();
		}
	}

	public Double getAutoMinValue() {
		Double autoMinValue = determineAutoMinValue();
		Double correctedMin = correctMinIfLogScaleAndZero(autoMinValue);
		return correctedMin;
	}

	public Double getAutoMaxValue() {
		return determineAutoMaxValue();
	}

	//#end region

}
