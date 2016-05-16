package org.treez.results.atom.legend;

import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.functions.DatumFunction;
import org.treez.javafxd3.d3.wrapper.Element;

import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class OnDragEnd implements DatumFunction<Void> {

	private D3 d3;

	public OnDragEnd(D3 d3) {

		this.d3 = d3;
	}

	@Override
	public Void apply(final Object context, final Object d, final int index) {

		JSObject jsContext = (JSObject) context;

		WebEngine webEngine = d3.getWebEngine();
		Element element = new Element(webEngine, jsContext);

		// remove fill attributes
		d3
				.select(element) //
				.attr("fill", "");

		return null;
	}

}
