package org.treez.standalone;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.treez.core.Activator;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.standallone.StandAloneWorkbench;
import org.treez.testutils.TestUtils;
import org.treez.views.graphics.GraphicsViewPart;
import org.treez.views.properties.PropertyViewPart;
import org.treez.views.tree.TreeViewPart;
import org.treez.views.tree.rootAtom.Root;

@SuppressWarnings("checkstyle:uncommentedmain")
public final class TreezStarter {

	//#region ATTRIBUTES

	private static Composite treeContainer;

	private static Composite propertyContainer;

	private static Composite graphicsContainer;

	//#end region

	//#region CONSTRUCTORS

	private TreezStarter() {

	}

	//#end region

	//#region METHODS

	public static void main(String[] args) {
		initializeLogging();
		//XySeriesExample example = new XySeriesExample();
		//BarExample example = new BarExample();
		//ContourExample example = new ContourExample();
		TornadoExampleHorizontal example = new TornadoExampleHorizontal();
		AbstractAtom<?> root = example.createModel();
		startTreez(root);

	}

	public static void startTreez(AbstractAtom<?> root) {

		final Shell shell = createShellAndViewContainers();

		//create and register views
		TreeViewPart treeView = new TreeViewPart();
		StandAloneWorkbench.registerView(TreeViewPart.ID, treeView);

		PropertyViewPart propertyView = new PropertyViewPart();
		StandAloneWorkbench.registerView(PropertyViewPart.ID, propertyView);

		GraphicsViewPart graphicsView = new GraphicsViewPart();
		StandAloneWorkbench.registerView(GraphicsViewPart.ID, graphicsView);

		//build views
		treeView.createPartControl(treeContainer);
		propertyView.createPartControl(propertyContainer);
		graphicsView.createPartControl(graphicsContainer);

		//set tree content
		setTreeContent(root, treeView);

		org.treez.core.color.ColorBrewer.drawColorRectangles(graphicsContainer);

		showShell(shell);
	}

	@SuppressWarnings("checkstyle:illegalcatch")
	private static void initializeLogging() {
		URL binUrl = TestUtils.class.getClassLoader().getResource(".");
		try {
			URI binUri = binUrl.toURI();
			URI log4jUri = binUri.resolve("../META-INF/log4j.properties");
			try {
				URL log4jUrl = log4jUri.toURL();
				PropertyConfigurator.configure(log4jUrl);
			} catch (Exception exception) {
				throw new IllegalStateException("Could not initialize logging", exception);
			}
		} catch (URISyntaxException exception) {
			throw new IllegalStateException("Could not initialize logging", exception);
		}
	}

	private static Shell createShellAndViewContainers() {
		final Display display = Display.getCurrent();
		final Shell shell = new Shell(display);
		shell.setText("Treez");
		shell.setImage(Activator.getImage("tree.png"));
		shell.setMaximized(true);

		Composite treeParentContainer = createParentComposite(shell);
		treeContainer = new Composite(treeParentContainer, SWT.NONE);

		Composite restContainer = new Composite(shell, SWT.NONE);

		final double mainRatio = 0.25;
		@SuppressWarnings("unused")
		SeparatorPanel mainSeparationPanel = new SeparatorPanel(shell, treeParentContainer, restContainer, mainRatio);

		Composite propertyParentContainer = createParentComposite(restContainer);
		propertyContainer = new Composite(propertyParentContainer, SWT.NONE);

		Composite graphicsParentContainer = createParentComposite(restContainer);
		graphicsContainer = new Composite(graphicsParentContainer, SWT.NONE);

		final double subRatio = 0.5;
		@SuppressWarnings("unused")
		SeparatorPanel restSeperatorPanel = new SeparatorPanel(
				restContainer,
				propertyParentContainer,
				graphicsParentContainer,
				subRatio);

		return shell;
	}

	private static Composite createParentComposite(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());
		return container;
	}

	private static void setTreeContent(AbstractAtom<?> root, TreeViewPart treeView) {
		AbstractAtom<?> invisibleRoot = new Root("invisibleRoot");
		invisibleRoot.addChild(root);
		treeView.setTreeContent(invisibleRoot);
		treeView.expandAll();
	}

	private static void showShell(final Shell shell) {
		final Display display = Display.getCurrent();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	//#end region

}
