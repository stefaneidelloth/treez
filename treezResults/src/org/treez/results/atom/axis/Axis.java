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
import org.treez.results.atom.veuszpage.GraphicsPageModel;
import org.treez.results.atom.veuszpage.GraphicsPropertiesPage;

/**
 * Represents a veusz axis
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Axis extends GraphicsPropertiesPage {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Axis.class);

	//#region ATTRIBUTES

	private Selection axisSelection;

	/**
	 * The data properties of the axis
	 */
	public Data data;

	/**
	 * The general properties of the axis
	 */
	public General general;

	/**
	 * The line properties of the axis
	 */
	public AxisLine axisLine;

	/**
	 * The label properties of the axis
	 */
	public AxisLabel axisLabel;

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
	protected void fillVeuszPageModels() {
		data = new Data();
		veuszPageModels.add(data);

		general = new General();
		veuszPageModels.add(general);

		axisLine = new AxisLine();
		veuszPageModels.add(axisLine);

		axisLabel = new AxisLabel();
		veuszPageModels.add(axisLabel);
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
	 */
	public void initializeScalesWithD3(D3 d3, Selection rectSelection) {
		Objects.requireNonNull(d3);
		data.initializeScaleWithD3(d3, rectSelection);
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

		for (GraphicsPageModel pageModel : veuszPageModels) {
			axisSelection = pageModel.plotWithD3(d3, axisSelection, rectSelection, this);
		}

		//handle mouse click
		axisSelection.onMouseClick(this);

		return graphSelection;

	}

	@Override
	protected String createVeuszStartText() {
		String veuszString = "";
		veuszString = veuszString + "Add('axis', name='" + name + "', autoadd=False)\n";
		veuszString = veuszString + "To('" + name + "')\n";
		return veuszString;
	}

	@Override
	protected String createVeuszEndText() {
		return "";
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
