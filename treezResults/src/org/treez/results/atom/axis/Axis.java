package org.treez.results.atom.axis;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.Scale;
import org.treez.results.Activator;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPage;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageModel;

/**
 * Represents a plot axis
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Axis extends GraphicsPropertiesPage {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Axis.class);

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

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Axis(String name) {
		super(name);
	}

	/**
	 * Constructor with direction
	 *
	 * @param name
	 * @param direction
	 */
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
	protected void fillPageModelList() {

		data = new Data();
		pageModels.add(data);

		axisLine = new AxisLine();
		pageModels.add(axisLine);

		majorTicks = new MajorTicks();
		pageModels.add(majorTicks);

		minorTicks = new MinorTicks();
		pageModels.add(minorTicks);

		tickLabels = new TickLabels();
		pageModels.add(tickLabels);

		axisLabel = new AxisLabel();
		pageModels.add(axisLabel);

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
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection) {
		Objects.requireNonNull(d3);

		axisSelection = graphSelection //
				.append("g")
				.attr("id", "" + name)
				.attr("class", "axis");

		plotPageModels(d3, rectSelection);

		//handle mouse click
		axisSelection.onMouseClick(this);

		return graphSelection;

	}

	/**
	 * @param d3
	 * @param rectSelection
	 */
	public void plotPageModels(D3 d3, Selection rectSelection) {
		for (GraphicsPropertiesPageModel pageModel : pageModels) {
			axisSelection = pageModel.plotWithD3(d3, axisSelection, rectSelection, this);
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
