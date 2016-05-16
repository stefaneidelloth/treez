package org.treez.results.atom.legend;

import org.apache.log4j.Logger;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.functions.DatumFunction;
import org.treez.javafxd3.d3.wrapper.Element;

import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class OnDragStart implements DatumFunction<Void> {

	private static Logger LOG = Logger.getLogger(OnDragStart.class);

	private D3 d3;

	public OnDragStart(D3 d3) {

		this.d3 = d3;
	}

	@Override
	public Void apply(final Object context, final Object d, final int index) {

		LOG.info("drag start");

		JSObject jsContext = (JSObject) context;

		WebEngine webEngine = d3.getWebEngine();
		Element element = new Element(webEngine, jsContext);

		d3
				.select(element) //
				.attr("fill", "red");

		return null;
	}

}
