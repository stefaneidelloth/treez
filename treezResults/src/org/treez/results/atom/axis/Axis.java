package org.treez.results.atom.axis;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.LinearScale;
import org.treez.results.Activator;
import org.treez.results.atom.veuszpage.GraphicsPageModel;
import org.treez.results.atom.veuszpage.GraphicsPropertiesPage;
import org.treez.results.length.Length;

/**
 * Represents a veusz axis
 */
public class Axis extends GraphicsPropertiesPage {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Axis.class);

	//#region ATTRIBUTES

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
	protected void fillVeuszPageModels() {
		veuszPageModels.add(new Data());
		veuszPageModels.add(new General());
		veuszPageModels.add(new AxisLine());
		veuszPageModels.add(new AxisLabel());
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
	public Selection plotWidthD3(D3 d3, Selection graphSelection, Selection rectSelection) {
		Objects.requireNonNull(d3);

		String graphWidthString = rectSelection.attr("width");
		Double graphWidthInPx = Length.toPx(graphWidthString);

		LinearScale scale = d3 //
				.scale() //
				.linear() //
				.domain(0.0, 1.0)
				.range(0.0, graphWidthInPx);

		org.treez.javafxd3.d3.svg.Axis axis = d3 //
				.svg() //
				.axis() //
				.scale(scale)
				.tickPadding(8.0)
				.innerTickSize(-10.0);

		axisSelection = graphSelection //
				.append("g")
				.attr("id", "" + name)
				.attr("class", "x axis");

		axis.apply(axisSelection);

		axisSelection //
				.selectAll("path, line") //
				.style("fill", "none") //
				.style("stroke", "#000")
				.style("stroke-width", "3px") //
				.style("shape-rendering", "geometricPrecision");

		for (GraphicsPageModel pageModel : veuszPageModels) {
			axisSelection = pageModel.plotWithD3(axisSelection, null, this);
		}

		//handle mouse click

		//rectSelection.onMouseClick(this);

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

}
