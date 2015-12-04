package org.treez.results.atom.axis;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.results.Activator;
import org.treez.results.atom.veuszpage.VeuszPropertiesPage;

/**
 * Represents a veusz axis
 */
public class Axis extends VeuszPropertiesPage {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Axis.class);

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
