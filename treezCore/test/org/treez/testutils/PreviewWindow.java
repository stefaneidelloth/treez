package org.treez.testutils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.treez.core.treeview.TreezView;

/**
 * A preview window that can be used by the tests
 */
public class PreviewWindow implements TreezView {

	//#region ATTRIBUTES

	/**
	 * A shell that is used as parent composite
	 */
	private Shell shell;

	/**
	 * A label for the name of the tested atom
	 */
	private Label nameLabel;

	/**
	 * A label for the icon preview
	 */
	private Label iconLabel;

	/**
	 * A composite for the control preview
	 */
	private Composite controlComposite;

	/**
	 * A composite for the parameter control preview
	 */
	private Composite attributeControlComposite;

	/**
	 * A composite for the graphics preview
	 */
	private Composite graphicsComposite;

	/**
	 * A text field for the code
	 */
	private Label codeArea;

	/**
	 * This runnable is executed after closing the preview window
	 */
	private Runnable postClosingHook;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 */

	public PreviewWindow() {

		//get display
		Display display = Display.getCurrent();

		//define background color
		final Color backgroundColor = new Color(null, 255, 255, 255);

		//define shell
		createShell(display);

		//create label for name
		createNameLabel(backgroundColor);

		//create label for icon preview
		createIconLabel();

		//create composite for control preview
		createControlComposite(backgroundColor);

		//create composite for AttributeAtom preview
		createAttributeControlComposite(backgroundColor);

		//create composite for CAD preview
		createGraphicsComposite(backgroundColor);

		//create text composite for code preview
		createCodeArea(backgroundColor);

	}

	@SuppressWarnings("checkstyle:magicnumber")
	private void createShell(Display display) {
		shell = new Shell(display);
		shell.setLayout(new GridLayout());
		shell.setText("Test Preview Window");
		shell.setSize(600, 850);
	}

	private void createNameLabel(final Color backgroundColor) {
		Label nameHeader = new Label(shell, SWT.NONE);
		nameHeader.setText("Name:");
		nameLabel = new Label(shell, SWT.NONE);
		nameLabel.setBackground(backgroundColor);
	}

	private void createIconLabel() {
		Label iconHeader = new Label(shell, SWT.NONE);
		iconHeader.setText("Icon:");
		iconLabel = new Label(shell, SWT.NONE);
	}

	private void createControlComposite(final Color backgroundColor) {
		Label controlHeader = new Label(shell, SWT.NONE);
		controlHeader.setText("Control:");
		controlComposite = new Composite(shell, SWT.BORDER);

		controlComposite.setBackground(backgroundColor);
		controlComposite.setLayout(new GridLayout(1, true));
		controlComposite.setLayoutData(
				new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
	}

	private void createAttributeControlComposite(final Color backgroundColor) {
		Label attributeControlHeader = new Label(shell, SWT.NONE);
		attributeControlHeader.setText("AttributeAtom Control:");
		attributeControlComposite = new Composite(shell, SWT.BORDER);
		attributeControlComposite.setLayout(new GridLayout(1, true));
		attributeControlComposite.setLayoutData(
				new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		attributeControlComposite.setBackground(backgroundColor);
	}

	private void createGraphicsComposite(final Color backgroundColor) {
		Label graphicsControlHeader = new Label(shell, SWT.NONE);
		graphicsControlHeader.setText("CAD Control:");
		graphicsComposite = new Composite(shell, SWT.BORDER);
		graphicsComposite.setBackground(backgroundColor);
	}

	private void createCodeArea(final Color backgroundColor) {
		Label codeHeader = new Label(shell, SWT.NONE);
		codeHeader.setText("Code:");
		codeArea = new Label(shell, SWT.BORDER);
		codeArea.setBackground(backgroundColor);
	}

	//#end region

	//#region METHODS

	//#end region

	//#region ACCESSORS

	/**
	 * Sets the preview image
	 *
	 * @param image
	 */
	public void setImage(Image image) {
		iconLabel.setImage(image);
	}

	/**
	 * Returns the parent composite for the control preview as and
	 * implementation of the TreezView interface
	 */
	@Override
	public Composite getContentComposite() {
		return controlComposite;
	}

	/**
	 * Returns the parent composite for the control preview
	 *
	 * @return
	 */
	public Composite getControlComposite() {
		return controlComposite;
	}

	/**
	 * Returns parent composite for the parameter control preview
	 *
	 * @return
	 */
	public Composite getAttributeControlComposite() {
		return attributeControlComposite;
	}

	/**
	 * Returns parent composite for the graphics preview
	 *
	 * @return
	 */
	public Composite getGraphicsComposite() {
		return graphicsComposite;
	}

	/**
	 * Shows the preview window until it is manually closed
	 */
	public void showUntilManuallyClosed() {
		//shell.pack();
		shell.open();

		Display display = Display.getCurrent();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		if (postClosingHook != null) {
			postClosingHook.run();
		}
	}

	/**
	 * Sets the code for the code preview
	 *
	 * @param code
	 */
	public void setCode(String code) {
		codeArea.setText(code);
	}

	/**
	 * Sets the name of the previewed atom
	 *
	 * @param name
	 */
	public void setName(String name) {
		nameLabel.setText(name);
	}

	@Override
	public IWorkbenchPartSite getSite() {
		return null;
	}

	@Override
	public IViewSite getViewSite() {
		return null;
	}

	/**
	 * @param postClosingHook
	 */
	public void setPostClosingHook(Runnable postClosingHook) {
		this.postClosingHook = postClosingHook;
	}

	//#end region

}
