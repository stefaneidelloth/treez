package org.treez.javafxd3.javafx;


import org.eclipse.swt.widgets.Composite;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

/**
 * Wraps JavaFx content on a canvas that can be used with SWT
 */
public class JavaFxWrapperForSwt extends JFXPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//#region CONSTRUCTORS

	public JavaFxWrapperForSwt(Composite parent) {
		super();
		Platform.setImplicitExit(false);
	}

	//#end region

	//#region METHODS

	public void setContent(Node plotter) {
		StackPane root = new StackPane();
		root.getChildren().add(plotter);
		Scene scene = new Scene(root);
		this.setScene(scene);
	}

	//#end region

}
