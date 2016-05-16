package org.treez.results.atom.axis;

import java.util.List;
import java.util.Objects;

import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.Scale;
import org.treez.results.Activator;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPage;

/**
 * Represents a plot axis
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Axis extends GraphicsPropertiesPage {

	//#region ATTRIBUTES

	/**
	 * The data properties of the axis
	 */
	public Data data;

	/**
	 * The properties of the axis line
	 */
	public AxisLine axisLine;

	/**
	 * The properties of the major ticks
	 */
	public MajorTicks majorTicks;

	/**
	 * The properties of the minor ticks
	 */
	public MinorTicks minorTicks;

	/**
	 * The properties of the tick labels
	 */
	public TickLabels tickLabels;

	/**
	 * The properties of the axis label
	 */
	public AxisLabel axisLabel;

	private Selection axisSelection;

	//#end region

	//#region CONSTRUCTORS

	public Axis(String name) {
		super(name);
	}

	public Axis(String name, Direction direction) {
		super(name);
		//set value for axis direction
		setAttribute("root.data.data.direction", direction.toString());
	}

	//#end region

	//#region METHODS

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("axis.png");
	}

	@Override
	protected void createPropertyPageFactories() {

		data = new Data();
		propertyPageFactories.add(data);

		axisLine = new AxisLine();
		propertyPageFactories.add(axisLine);

		majorTicks = new MajorTicks();
		propertyPageFactories.add(majorTicks);

		minorTicks = new MinorTicks();
		propertyPageFactories.add(minorTicks);

		tickLabels = new TickLabels();
		propertyPageFactories.add(tickLabels);

		axisLabel = new AxisLabel();
		propertyPageFactories.add(axisLabel);

	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		//no actions available right now
		return actions;
	}

	/**
	 * @param d3
	 * @param graphSelection
	 * @return
	 */
	@Override
	public Selection plotWithD3(
			D3 d3,
			Selection graphSelection,
			Selection graphRectSelection,
			FocusChangingRefreshable refreshable) {
		Objects.requireNonNull(d3);
		this.treeViewRefreshable = refreshable;

		//remove old axis group if it already exists
		graphSelection //
				.select("#" + name) //
				.remove(); //

		//create new axis group
		axisSelection = graphSelection //
				.append("g") //
				.attr("class", "axis") //
				.onMouseClick(this);
		bindNameToId(axisSelection);

		updatePlotWithD3(d3);

		return graphSelection;

	}

	@Override
	public void updatePlotWithD3(D3 d3) {
		plotPageModels(d3);
	}

	private void plotPageModels(D3 d3) {
		for (GraphicsPropertiesPageFactory pageModel : propertyPageFactories) {
			axisSelection = pageModel.plotWithD3(d3, axisSelection, null, this);
		}
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Returns true if the scale of this axis is quantitative. Throws an IllegalStateException if the scale has not yet
	 * been defined.
	 *
	 * @return
	 */
	public Boolean hasQuantitativeScale() {
		if (data != null) {
			boolean hasQuantitativeScale = this.data.hasQuantitativeScale();
			return hasQuantitativeScale;
		} else {
			throw new IllegalStateException("The scale has not yet been defined");
		}
	}

	/**
	 * Returns the scale of the axis. Throws an IllegalStateException if the scale has not yet been defined.
	 *
	 * @return
	 */
	public Scale<?> getScale() {
		if (data != null) {
			Scale<?> scale = this.data.getScale();
			return scale;
		} else {
			throw new IllegalStateException("The scale has not yet been defined");
		}
	}

	//#end region

}
