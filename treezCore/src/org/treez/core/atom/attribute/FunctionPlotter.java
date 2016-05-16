package org.treez.core.atom.attribute;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.treez.core.Activator;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.attribute.base.AbstractAttributeAtom;
import org.treez.core.atom.base.annotation.IsParameter;
import org.treez.core.swt.JavaFxWrapperForSwt;

/**
 * Plots a functional expression
 */
public class FunctionPlotter extends AbstractAttributeAtom<String> {

	//#region ATTRIBUTES

	@IsParameter(defaultValue = "Label")
	private String title;

	@IsParameter(defaultValue = "exp(-x^2)")
	private String defaultExpression;

	/**
	 * The parent composite for the attribute atom control can be stored here to
	 * be able to refresh it.
	 */
	protected Composite attributeAtomParent = null;

	private org.treez.javafxd3.javafx.FunctionPlotter plotter;

	//#end region

	//#region CONSTRUCTORS

	public FunctionPlotter(String name) {
		super(name);
		title = name;
	}

	public FunctionPlotter(String name, String expression) {
		super(name);
		title = name;
		set(expression);
	}

	/**
	 * Copy constructor
	 */
	protected FunctionPlotter(FunctionPlotter functionPlotterToCopy) {
		super(functionPlotterToCopy);
		title = functionPlotterToCopy.title;
		defaultExpression = functionPlotterToCopy.defaultExpression;
	}

	//#end region

	//#region METHODS

	@Override
	public FunctionPlotter copy() {
		return new FunctionPlotter(this);
	}

	@Override
	public Image provideImage() {
		Image baseImage = Activator.getImage("CheckBox.png");
		return baseImage;
	}

	@Override
	@SuppressWarnings("checkstyle:magicnumber")
	public AbstractAttributeAtom<String> createAttributeAtomControl(
			Composite parent, FocusChangingRefreshable treeViewerRefreshable) {
		this.attributeAtomParent = parent;
		this.treeViewRefreshable = treeViewerRefreshable;

		//initialize value at the first call
		if (!isInitialized()) {
			set(defaultExpression);
		}

		//Create JavaFx scene with FunctionPlotter
		double width = 420;
		double height = 400;
		final JavaFxWrapperForSwt javaFxWrapper = new JavaFxWrapperForSwt(
				parent);
		plotter = new org.treez.javafxd3.javafx.FunctionPlotter(width, height);
		javaFxWrapper.setContent(plotter);
		plotter.setXDomain(-2, 2);
		plotter.setYDomain(-0.01, 0);
		plotter.plot("exp(-x^2)");

		return this;
	}

	/**
	 * Plots the given expression, e.g. "x^2"
	 *
	 * @param expression
	 */
	public void plot(String expression) {
		if (plotter != null) {
			plotter.plot(expression);
		}
	}

	/**
	 * Plots the given custom expression, e.g.
	 * "[{fn: '3 + sin(x)', range: [2, 8], closed: true }]"
	 *
	 * @param customExpression
	 */
	public void plotCustomExpression(String customExpression) {
		if (plotter != null) {
			plotter.plotCustomExpression(customExpression);
		}

	}

	/**
	 * Shows that there is some error
	 */
	public void showError() {
		if (plotter != null) {
			plotter.showError();
		}
	}

	@Override
	public void setEnabled(boolean state) {
		super.setEnabled(state);

		if (treeViewRefreshable != null) {
			treeViewRefreshable.refresh();
		}
		this.refreshAttributeAtomControl();
	}

	@Override
	public void refreshAttributeAtomControl() {

	}

	//#end region

	//#region ACCESSORS

	public String getLabel() {
		return title;
	}

	public void setLabel(String label) {
		this.title = label;
	}

	@Override
	public String getDefaultValue() {
		return defaultExpression;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultExpression = defaultValue;
	}

	public void setXDomain(Double xMin, Double xMax) {
		if (plotter != null) {
			plotter.setXDomain(xMin, xMax);
		}
	}

	public void setYDomain(Double yMin, Double yMax) {
		if (plotter != null) {
			plotter.setYDomain(yMin, yMax);
		}
	}

	@Override
	public void setBackgroundColor(Color backgroundColor) {
		//TODO Auto-generated method stub
	}

	//#end region

}
