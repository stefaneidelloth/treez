package org.treez.results.atom.page;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.treez.core.adaptable.Adaptable;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.graphics.GraphicsAtom;
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
import org.treez.results.veusz.VeuszToImage;

/**
 * Represents a veusz page
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class Page extends GraphicsAtom {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(Page.class);

	//#region ATTRIBUTES

	private static PlotOption plotOption = PlotOption.D3;

	private JavaFxD3Browser browser;

	private Selection pageSelection;

	private static String VEUSZ_PATH = "C:/Program Files (x86)/Veusz/veusz.exe";

	private static String TEMP_PATH = "D:/";

	/**
	 * Path to the Veusz executable. Veusz is used to generate the plot and the finished plot is then imported as image.
	 */
	public final Attribute<String> filePath = new Wrap<>();

	/**
	 * Directory for temporary files
	 */
	public final Attribute<String> directoryPath = new Wrap<>();

	/**
	 * Width of the page
	 */
	public final Attribute<String> pageWidth = new Wrap<>();

	/**
	 * Height of the page
	 */
	public final Attribute<String> pageHeight = new Wrap<>();

	/**
	 * If this is true the page is hidden.
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

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
		createPageModel();
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the page model
	 */
	private void createPageModel() {

		//root
		AttributeRoot root = new AttributeRoot("root");

		//page
		org.treez.core.atom.attribute.Page page = root.createPage("page");

		//section
		Section veuszSection = page.createSection("section");
		veuszSection.setTitle("Veusz page");

		Runnable runAction = () -> execute(treeViewRefreshable);
		veuszSection.createSectionAction("action", "Build page", runAction);

		//file path to veusz executable
		veuszSection.createFilePath(filePath, "filePath", "Path to Veusz executable", VEUSZ_PATH);

		//directory for temporary files
		veuszSection.createDirectoryPath(directoryPath, "directoryPath", "Directory for temporary files", TEMP_PATH);

		//page settings
		veuszSection.createTextField(pageWidth, "pageWidth", "Page width", "10 cm");
		veuszSection.createTextField(pageHeight, "pageHeight", "Page Height", "10 cm");
		veuszSection.createCheckBox(hide, "hide");

		setModel(root);

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
	@SuppressWarnings("checkstyle:illegalcatch")
	public void execute(Refreshable refreshable) {

		switch (plotOption) {
		case D3:
			plotWithD3();
			break;
		case Veusz:
			plotWithVeusz();
			break;
		default:
			throw new IllegalStateException("Plot option " + plotOption + " is not yet implemented.");
		}

	}

	private void plotWithD3() {
		Runnable executeRunnable = () -> {
			D3 d3 = browser.getD3();
			plotWithD3(d3);
		};
		browser = createD3BrowserInCadView(executeRunnable);
	}

	private void plotWithD3(D3 d3) {
		Objects.requireNonNull(d3);
		plotPageWithD3AndCreatePageSelection(d3);
		for (Adaptable child : children) {
			Boolean isGraph = child.getClass().equals(Graph.class);
			if (isGraph) {
				Graph graph = (Graph) child;
				graph.plotWidthD3(d3, pageSelection);
			}
		}
	}

	private void plotPageWithD3AndCreatePageSelection(D3 d3) {

		Selection svgSelection = d3 //
				.select("#svg");

		bindStringAttribute(svgSelection, "width", pageWidth);
		bindStringAttribute(svgSelection, "height", pageHeight);

		pageSelection = svgSelection //
				.append("g") //
				.attr("id", "" + name);

		bindDisplayToBooleanAttribute(pageSelection, hide);

		Selection rect = pageSelection //
				.append("rect") //
				.attr("fill", "lightblue");

		bindStringAttribute(rect, "width", pageWidth);
		bindStringAttribute(rect, "height", pageHeight);

		rect.onMouseClick(this);
	}

	/**
	 * Shows the given image in the CadAdaption view of treez
	 *
	 * @param image
	 */
	private static JavaFxD3Browser createD3BrowserInCadView(Runnable postLoadingHook) {
		//get CadAdaption view
		TreezView cadView = (TreezView) getView("org.treez.views.graphics");

		//get content composite from CadView
		if (cadView != null) {
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

	private void plotWithVeusz() {
		//create veusz text
		sysLog.debug("Building Veusz text...");
		String veuszString = getVeuszText();
		sysLog.debug(veuszString);

		//convert veusz text to image &
		sysLog.debug("Converting Veusz text to image...");
		String veuszPath = filePath.get();
		String tempPath = directoryPath.get();
		Image image = null;
		try {
			image = VeuszToImage.convert(veuszString, veuszPath, tempPath);
			sysLog.debug("Image created.");
		} catch (Exception exception) {
			sysLog.error("Could not convert veusz text", exception);
		}

		//show image in CadAdaption view
		sysLog.debug("Showing Veusz image in CadView...");
		if (image != null) {
			try {
				showImageInCadView(image);
			} catch (Exception exception) {
				sysLog.error("Could not convert veusz text", exception);
			}

			sysLog.debug("Veusz page finished.");
		}
	}

	/**
	 * Provides veusz text to represent this atom
	 *
	 * @return
	 */
	private String getVeuszText() {

		//construct veusz text
		String veuszString = "";
		veuszString = veuszString + "Add('page', name=u'" + name + "', autoadd=False)\n";
		veuszString = veuszString + "To(u'" + name + "')\n";
		if (hide.get()) {
			veuszString = veuszString + "Set('hide', True)\n";
		}
		veuszString = veuszString + "Set('width', u'" + pageWidth + "')\n";
		veuszString = veuszString + "Set('height', u'" + pageHeight + "')\n\n";

		//add veusz text of children
		for (Adaptable child : children) {
			Boolean isGraph = child.getClass().getSimpleName().equals("Graph");
			if (isGraph) {
				Graph graph = (Graph) child;
				String graphText = graph.getVeuszText();
				Objects.requireNonNull(graphText);
				veuszString = veuszString + graphText;
				veuszString = veuszString + "To('..')\n";
			}
		}

		return veuszString;
	}

	/**
	 * Shows the given image in the CadAdaption view of treez
	 *
	 * @param image
	 */
	private static void showImageInCadView(Image image) {
		//get CadAdaption view
		TreezView cadView = (TreezView) getView("org.treez.views.graphics");

		//get content composite from CadView
		if (cadView != null) {
			Composite contentComposite = cadView.getContentComposite();

			//delete old contents
			for (Control child : contentComposite.getChildren()) {
				child.dispose();
			}

			//show image
			Label label = new Label(contentComposite, SWT.None);
			label.setImage(image);

			//update content composite
			contentComposite.layout();
		} else {
			throw new IllegalStateException("Could not get treez graphics view");
		}

	}

	/**
	 * Gets a view with a given id
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
