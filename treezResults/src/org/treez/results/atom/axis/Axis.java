package org.treez.results.atom.axis;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.Activator;
import org.treez.results.atom.veuszpage.GraphicsPageModel;
import org.treez.results.atom.veuszpage.GraphicsPropertiesPage;

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

}
