package org.treez.javafxd3.javafx;

import org.eclipse.swt.widgets.Composite;
import org.treez.javafxd3.d3.D3;

import javafx.application.Platform;

public class PlainJavaFxD3BrowserWrapper extends JavaFxWrapperForSwt {
	
	private PlainJavaFxD3Browser browser;

	public PlainJavaFxD3BrowserWrapper(Composite parent, Runnable postLoadingHook, boolean enableDebugMode) {
		super(parent);
	    browser = new PlainJavaFxD3Browser(postLoadingHook, enableDebugMode);
	    setContent(browser);
	}

	public D3 getD3() {		
		return browser.getD3();
	}

	public void runLater(Runnable runnable) {
		Platform.runLater(runnable);		
	}
	

}
