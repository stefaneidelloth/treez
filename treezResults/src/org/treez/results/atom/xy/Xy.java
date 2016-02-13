package org.treez.results.atom.xy;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.Activator;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.veuszpage.GraphicsPropertiesPage;

/**
 * Represents an xy scatter plot
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Xy extends GraphicsPropertiesPage {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Xy.class);

	//#region ATTRIBUTES

	/**
	 * The data properties of the xy plot
	 */
	public Data data;

	/**
	 * The marker properties of the xy plot
	 */
	public Symbol symbol;

	/**
	 * The line properties of the xy plot
	 */
	public Line line;

	/**
	 * The error bar properties of the xy plot
	 */
	//public ErrorBar errorBar;

	/**
	 * The area properties of the xy plot
	 */
	public Area area;

	/**
	 * The label properties of the xy plot
	 */
	//public Label label;

	private Selection xySelection;

	//#end region

	// #region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Xy(String name) {
		super(name);
		setRunnable();
	}

	// #end region

	// #region METHODS

	@Override
	protected void fillVeuszPageModels() {
		data = new Data();
		pageModels.add(data);

		symbol = new Symbol();
		pageModels.add(symbol);

		line = new Line();
		pageModels.add(line);

		//errorBar = new ErrorBar();
		//veuszPageModels.add(errorBar);

		area = new Area();
		pageModels.add(area);

		//label = new Label();
		//veuszPageModels.add(label);
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

	/**
	 * @param d3
	 * @param graphSelection
	 * @param rectSelection
	 */
	public Selection plotWithD3(D3 d3, Selection graphSelection, Selection rectSelection) {
		Objects.requireNonNull(d3);

		xySelection = graphSelection //
				.insert("g", ".axis") //
				.attr("id", "" + name) //TODO: listener for name changes to update id
				.attr("class", "xy");

		xySelection = data.plotWithD3(d3, xySelection, rectSelection, this);

		xySelection.onMouseClick(this);

		return graphSelection;
	}

	// #end region

}
