package org.treez.results.atom.page;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Section;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.core.swt.JavaFxWrapperForSwt;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.core.treeview.TreezView;
import org.treez.core.treeview.action.AddChildAtomTreeViewerAction;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.javafx.JavaFxD3Browser;
import org.treez.results.Activator;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPage;

/**
 * Represents a plotting page that might include several graphs
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Page extends GraphicsPropertiesPage {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Page.class);

	//#region ATTRIBUTES

	/**
	 * Width
	 */
	public final Attribute<String> pageWidth = new Wrap<>();

	/**
	 * Height
	 */
	public final Attribute<String> pageHeight = new Wrap<>();

	/**
	 * Color
	 */
	public final Attribute<String> pageColor = new Wrap<>();

	/**
	 * If this is true the page is hidden.
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	private JavaFxD3Browser browser;

	private Selection pageSelection;

	private Selection rectSelection;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public Page(String name) {
		super(name);
		setRunnable();
		createModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the treez model for this atom
	 */
	private void createModel() {

		//root
		AttributeRoot root = new AttributeRoot("root");

		//page
		org.treez.core.atom.attribute.Page page = root.createPage("page");

		//section
		Section section = page.createSection("section");
		section.setTitle("Page");

		Runnable runAction = () -> execute(treeViewRefreshable);
		section.createSectionAction("action", "Build page", runAction);

		//page settings
		section
				.createTextField(pageWidth, "pageWidth", "15 cm") //
				.setLabel("Page width");

		section
				.createTextField(pageHeight, "pageHeight", "15 cm") //
				.setLabel("Page Height");

		section.createColorChooser(pageColor, "color", "white");

		section.createCheckBox(hide, "hide");

		setModel(root);

	}

	@Override
	protected void createPropertyPageFactories() {
		//not used here since there are only a few properties
		//the few properties are created directly
	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		return Activator.getImage("page.png");
	}

	/**
	 * Creates the context menu actions for this atom
	 */
	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {

		Action addGraph = new AddChildAtomTreeViewerAction(
				Graph.class,
				"graph",
				Activator.getImage("graph.png"),
				this,
				treeViewer);
		actions.add(addGraph);

		return actions;
	}

	/**
	 * Creates a graphical representation of this page and shows it in the CadAdaption view
	 */
	@Override
	public void execute(Refreshable refreshable) {

		Runnable plotPageRunnable = () -> {
			D3 d3 = browser.getD3();
			Objects.requireNonNull(d3);

			Selection svgSelection = d3 //
					.select("#svg");
			bindStringAttribute(svgSelection, "width", pageWidth);
			bindStringAttribute(svgSelection, "height", pageHeight);

			plotWithD3(d3, svgSelection, null, refreshable);
		};
		browser = createD3BrowserInCadView(plotPageRunnable);
	}

	@Override
	public Selection plotWithD3(D3 d3, Selection svgSelection, Selection contentSelection, Refreshable refreshable) {
		this.treeViewRefreshable = refreshable;

		//remove old page group if it already exists
		svgSelection //
				.select("#" + name)
				.remove();

		//create new page group
		pageSelection = svgSelection //
				.append("g"); //
		bindNameToId(pageSelection);

		bindDisplayToBooleanAttribute("hidePage", pageSelection, hide);

		//create rect
		rectSelection = pageSelection //
				.append("rect") //
				.onMouseClick(this);

		bindStringAttribute(rectSelection, "fill", pageColor);
		bindStringAttribute(rectSelection, "width", pageWidth);
		bindStringAttribute(rectSelection, "height", pageHeight);

		updatePlotWithD3(d3);

		return pageSelection;
	}

	@Override
	public void updatePlotWithD3(D3 d3) {
		plotChildGraphs(d3);
	}

	private void plotChildGraphs(D3 d3) {
		for (Adaptable child : children) {
			Boolean isGraph = child.getClass().equals(Graph.class);
			if (isGraph) {
				Graph graph = (Graph) child;
				graph.plotWithD3(d3, pageSelection, rectSelection, this.treeViewRefreshable);
			}
		}
	}

	private static JavaFxD3Browser createD3BrowserInCadView(Runnable postLoadingHook) {
		//get CadAdaption view
		TreezView cadView = (TreezView) getView("org.treez.views.graphics");

		if (cadView != null) {
			//get content composite from CadView
			Composite contentComposite = cadView.getContentComposite();

			//delete old contents
			for (Control child : contentComposite.getChildren()) {
				child.dispose();
			}

			//create new D3 browser
			JavaFxWrapperForSwt wrapper = new JavaFxWrapperForSwt(contentComposite);
			JavaFxD3Browser browser = new JavaFxD3Browser(postLoadingHook, true);
			wrapper.setContent(browser);

			//update content composite
			contentComposite.layout();

			return browser;

		} else {
			throw new IllegalStateException("Could not get treez graphics view");
		}

	}

	/**
	 * Retrieves an Eclipse view with given id
	 *
	 * @param id
	 * @return
	 */
	private static IViewPart getView(String id) {
		IViewReference[] viewReferences = PlatformUI
				.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getViewReferences();
		for (int i = 0; i < viewReferences.length; i++) {
			String currentId = viewReferences[i].getId();
			if (currentId.equals(id)) {
				return viewReferences[i].getView(false);
			}
		}
		return null;
	}

	//#region CREATE CHILD ATOMS

	/**
	 * Creates a Graph child
	 *
	 * @param name
	 * @return
	 */
	public Graph createGraph(String name) {
		Graph child = new Graph(name);
		addChild(child);
		return child;
	}

	//#end region

	//#end region

}
