package org.treez.core.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

/**
 * Wraps JavaFx content on a canvas that can be used with SWT
 */
public class JavaFxWrapperForSwt extends FXCanvas {

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param parent
	 */
	public JavaFxWrapperForSwt(Composite parent) {
		super(parent, SWT.NONE);
	}

	//#end region

	//#region METHODS

	/**
	 * Sets the JavaFx content of the wrapper
	 *
	 * @param plotter
	 */
	public void setContent(Node plotter) {
		AnchorPane root = new AnchorPane();
		root.setPrefHeight(200);
		root.setPrefWidth(200);
		root.getChildren().add(plotter);
		Scene scene = new Scene(root);

		this.setScene(scene);
	}

	//#end region

}
