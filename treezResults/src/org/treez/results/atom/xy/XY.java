package org.treez.results.atom.xy;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.results.Activator;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.veuszpage.VeuszPropertiesPage;

/**
 * Represents a veusz graph
 */
public class XY extends VeuszPropertiesPage {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(XY.class);

	// #region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public XY(String name) {
		super(name);
		setRunnable();
	}

	// #end region

	// #region METHODS

	@Override
	protected void fillVeuszPageModels() {
		veuszPageModels.add(new Data());
		veuszPageModels.add(new Marker());
		veuszPageModels.add(new Line());
		veuszPageModels.add(new ErrorBar());
		veuszPageModels.add(new Fill());
		veuszPageModels.add(new Label());
	}

	@Override
	protected String createVeuszStartText() {
		String veuszString = "";
		veuszString = veuszString + "Add('xy', name='" + name + "', autoadd=False)\n";
		veuszString = veuszString + "To('" + name + "')\n";
		return veuszString;
	}

	@Override
	protected String createVeuszEndText() {
		return "";
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("xy.png");
	}

	/**
	 * Creates the context menu actions
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		// no actions available right now

		return actions;
	}

	@Override
	public void execute(Refreshable refreshable) {
		Graph graph = (Graph) createTreeNodeAdaption().getParent().getAdaptable();
		graph.execute(refreshable);
	}

	// #end region

}
